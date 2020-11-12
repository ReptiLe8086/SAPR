import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.Precision;

import java.io.*;
import java.util.ArrayList;

public class ProcessorController {

    private Integer nodeCount;
    //public Integer barCount;
    private boolean isLeftTermination = true;
    private boolean isRightTermination = true;
    public static double[] forces;
    public static double[] loads;
    public static double[] Nx_ks;
    public static double[] Nx_bs;
    public static double[] Ux_as;
    public static double[] Ux_bs;
    public static double[] Ux_cs;
    public static double[] Sigma_ks;
    public static double[] Sigma_bs;

    private final ArrayList<Double> elasticityArray = new ArrayList<>();
    private final ArrayList<Double> areaArray = new ArrayList<>();
    private final ArrayList<Double> lengthArray = new ArrayList<>();

    @FXML
    private TextField fileNameTextField;

    @FXML
    private Button openButton;

    public double Nx_b(double E, double A, double L, double Up0, double UpL, double q){
        return (E*A/L) * (UpL - Up0) + q*L/2;
    }

    public double Ux_b(double E, double A, double L, double Up0, double UpL, double q){
        return (UpL - Up0 + (q*L*L) / (2*E*A)) / L;
    }

    public double Ux_a(double E, double A, double q) {
        return -q/(2*E*A);
    }

    public void calculating() {
        int barCount = nodeCount - 1;
        forces = new double[nodeCount];
        loads = new double[barCount];
        Nx_ks = new double[barCount];
        Nx_bs = new double[barCount];
        Ux_as = new double[barCount];
        Ux_bs = new double[barCount];
        Ux_cs = new double[barCount];
        Sigma_ks = new double[barCount];
        Sigma_bs = new double[barCount];
        double[][] reactionMatrixData = new double[nodeCount][nodeCount];


        for (int idx = 0; idx < nodeCount; idx++) {
            forces[idx] = .0;
            for (Force force : SAPR.forceArray) {
                if (force.number == idx + 1) {
                    forces[idx] = force.value;
                }
            }
        }

        for (int idx = 0; idx < barCount; idx++) {
            loads[idx] = .0;
            for (Load load : SAPR.loadArray) {
                if (load.number == idx + 1) {
                    loads[idx] = load.value;
                }
            }
        }


        for(int i = 0; i < nodeCount; i++){
            for(int j = 0; j < nodeCount; j++){
                if(i == j && i > 0 && j > 0 && i < barCount && j < barCount) {
                    reactionMatrixData[i][j] = (elasticityArray.get(i - 1) * areaArray.get(i - 1)) / lengthArray.get(i - 1) +
                            (elasticityArray.get(j) * areaArray.get(j)) / lengthArray.get(j);
                }
                else if(i == j + 1) {
                    reactionMatrixData[i][j] = - (elasticityArray.get(j) * areaArray.get(j)) / lengthArray.get(j);
                }
                else if(j == i + 1) {
                    reactionMatrixData[i][j] = - (elasticityArray.get(i) * areaArray.get(i)) / lengthArray.get(i);
                }
                else reactionMatrixData[i][j] = .0;
            }
        }

        double[][] reactionVectorData = new double[nodeCount][1];

        for(int idx = 1; idx < barCount; idx++){
            reactionVectorData[idx][0] = forces[idx] + loads[idx] * lengthArray.get(idx) / 2 + loads[idx - 1] * lengthArray.get(idx - 1) / 2;
        }


        if(isLeftTermination) {
            reactionMatrixData[0][0] = 1.0;
            reactionMatrixData[0][1] = .0;
            reactionMatrixData[1][0] = .0;
            reactionVectorData[0][0] = .0;
        }
        else { reactionMatrixData[0][0] = (elasticityArray.get(0) * areaArray.get(0)) / lengthArray.get(0);
                reactionVectorData[0][0] = forces[0] + loads[0] * lengthArray.get(0) / 2; }

        if(isRightTermination) {
            reactionMatrixData[barCount][barCount] = 1.0;
            reactionMatrixData[barCount - 1][barCount] = .0;
            reactionMatrixData[barCount][barCount - 1] = .0;
            reactionVectorData[barCount][0] = .0;
        }
        else { reactionMatrixData[barCount][barCount] = (elasticityArray.get(barCount - 1) *
                areaArray.get(barCount - 1)) / lengthArray.get(barCount - 1);
                reactionVectorData[barCount][0] = forces[barCount] + loads[barCount - 1] * lengthArray.get(barCount - 1) / 2;}

        RealMatrix reactionMatrix = MatrixUtils.createRealMatrix(reactionMatrixData);
        RealMatrix reactionVector = MatrixUtils.createRealMatrix(reactionVectorData);
        RealMatrix inverseReactionMatrix = new LUDecomposition(reactionMatrix).getSolver().getInverse();
        RealMatrix deltaVector = inverseReactionMatrix.multiply(reactionVector);

        double[] uZeros = new double[barCount];
        double[] uLengths = new double[barCount];
        for(int idx = 0; idx < barCount; idx++){
            uZeros[idx] = deltaVector.getEntry(idx, 0);
        }
        System.arraycopy(uZeros, 1, uLengths, 0, barCount - 1);
        uLengths[barCount - 1] = deltaVector.getEntry(barCount, 0);
//        double[] nxs = new double[2 * barCount];
//        double[] uxs = new double[nodeCount];
//        double x;
//        int count = 0;
//        for(int idx = 0; idx < barCount; idx++){
//            x = 0d;
//            nxs[count] = -loads[idx]*x + Nx_b(elasticityArray.get(idx), areaArray.get(idx), lengthArray.get(idx),
//                    uZeros[idx], uLengths[idx], loads[idx]);
//            x = lengthArray.get(idx);
//            count++;
//            nxs[count] = -loads[idx]*x + Nx_b(elasticityArray.get(idx), areaArray.get(idx), lengthArray.get(idx),
//                    uZeros[idx], uLengths[idx], loads[idx]);
//            count++;
//        }
//
//        System.arraycopy(uZeros, 0, uxs, 0, barCount);
//        uxs[barCount] = uLengths[barCount - 1];


        //polynome coeffs
        for(int idx = 0; idx < barCount; idx++){
            Nx_ks[idx] = -loads[idx];
            Nx_bs[idx] = Nx_b(elasticityArray.get(idx), areaArray.get(idx), lengthArray.get(idx),
                    uZeros[idx], uLengths[idx], loads[idx]);

            Ux_as[idx] = Ux_a(elasticityArray.get(idx), areaArray.get(idx), loads[idx]);
            Ux_bs[idx] = Ux_b(elasticityArray.get(idx), areaArray.get(idx), lengthArray.get(idx), uZeros[idx],
                    uLengths[idx], loads[idx]);
            Ux_cs[idx] = uZeros[idx];

            Sigma_ks[idx] = Nx_ks[idx] / areaArray.get(idx);
            Sigma_bs[idx] = Nx_bs[idx] / areaArray.get(idx);

        }

        for(int idx = 0; idx < barCount; idx++){
            System.out.println("N" + (idx+1) + "x: " + Precision.round(-loads[idx], 4) + "x + "
                    + Precision.round(Nx_b(elasticityArray.get(idx), areaArray.get(idx), lengthArray.get(idx),
                    uZeros[idx], uLengths[idx], loads[idx]), 4));
            System.out.println("U" + (idx+1) + "x:"
                    + Ux_a(elasticityArray.get(idx), areaArray.get(idx), loads[idx])
                    + "x^2 + " + Ux_b(elasticityArray.get(idx), areaArray.get(idx), lengthArray.get(idx), uZeros[idx],
                    uLengths[idx], loads[idx]) + "x + " + uZeros[idx]);
            System.out.println("Sigma" +  (idx+1) + "x:" + Precision.round((-loads[idx]/areaArray.get(idx)), 4)
                    + "x + " +Precision.round(Nx_b(elasticityArray.get(idx), areaArray.get(idx), lengthArray.get(idx),
                    uZeros[idx], uLengths[idx], loads[idx]) / areaArray.get(idx), 4) + "\n");
        }
//
//        for(int idx = 0; idx < 2*barCount; idx++){
//            System.out.println("N" + (idx+1) + ":" + Precision.round(nxs[idx], 4));
//        }
//
//        for(int idx = 0; idx < nodeCount; idx++){
//            System.out.println("U" + (idx+1) + ":" + uxs[idx]);
//        }
//        System.out.println("###################");
    }

    public void openButtonClicked(ActionEvent event) {
        String filename = fileNameTextField.getText() + ".kpr";
        File projectFile = new File(filename);

        try {
            SAPR.barArray.clear();
            SAPR.forceArray.clear();
            SAPR.loadArray.clear();
            elasticityArray.clear();
            areaArray.clear();
            lengthArray.clear();
            BufferedReader fileReader = new BufferedReader(new FileReader(projectFile));
            String buffer;
            while ((buffer = fileReader.readLine()) != null) {

                String[] str = buffer.split(" ");

                for (String line : str) {
                    if (!line.isEmpty()) {
                        switch (line) {
                            case "=" -> {
                                Bar bar = new Bar(Integer.parseInt(str[1]),
                                        Double.parseDouble(str[2]),
                                        Double.parseDouble(str[3]),
                                        Double.parseDouble(str[4]),
                                        Double.parseDouble(str[5]));
                                SAPR.barArray.add(bar);
                                elasticityArray.add(bar.elasticity);
                                areaArray.add(bar.area);
                                lengthArray.add(bar.length);
                            }
                            case "?" -> {
                                isLeftTermination = str[1].equals("1");
                                isRightTermination = str[2].equals("1");
                            }
                            case "#" -> {
                                Force force = new Force(Integer.parseInt(str[1]),
                                        Double.parseDouble(str[2]));
                                SAPR.forceArray.add(force);

                            }
                            case "~" -> {
                                Load load = new Load(Integer.parseInt(str[1]),
                                        Double.parseDouble(str[2]));
                                SAPR.loadArray.add(load);
                            }
                        }
                    }
                }
            }
            nodeCount = elasticityArray.size() + 1;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Внимание");
            a.setHeaderText("Проблемы с файлом");
            a.setContentText("Этот файл не может быть открыт!");
            a.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
        calculating();
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Успех");
        a.setHeaderText("Рассчет проведен");
        a.setContentText("Значения посчитаны успешно!");
        a.showAndWait();
        Stage stage = (Stage) openButton.getScene().getWindow();
        stage.close();
    }
}
