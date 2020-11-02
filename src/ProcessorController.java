import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.Precision;

import java.io.*;
import java.util.ArrayList;

public class ProcessorController {

    public Integer termination_count;
    public Integer nodeCount;
    public boolean isLeftTermination = true;
    public boolean isRightTermination = true;

    private final ArrayList<Double> elasticityArray = new ArrayList<>();
    private final ArrayList<Double> areaArray = new ArrayList<>();
    private final ArrayList<Double> lengthArray = new ArrayList<>();

    @FXML
    private TextField fileNameTextField;

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
        double[] forces = new double[nodeCount];
        double[] loads = new double[nodeCount - 1];
        double[][] reactionMatrixData = new double[nodeCount][nodeCount];
        int barCount = nodeCount - 1;

        for (int idx = 0; idx < nodeCount; idx++) {
            forces[idx] = .0;
            for (Force force : SAPR.forceArray) {
                if (force.number == idx + 1) {
                    forces[idx] = force.value;
                }
            }
        }

        for (int idx = 0; idx < nodeCount - 1; idx++) {
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
        }
        else { reactionMatrixData[barCount][barCount] = (elasticityArray.get(barCount - 1) *
                areaArray.get(barCount - 1)) / lengthArray.get(barCount - 1); }

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
        double[] Nxs = new double[2*barCount];
        double[] Uxs = new double[nodeCount];
        double x;
        int count = 0;
        for(int idx = 0; idx < barCount; idx++){
            x = 0d;
            Nxs[count] = -loads[idx]*x + Nx_b(elasticityArray.get(idx), areaArray.get(idx), lengthArray.get(idx),
                    uZeros[idx], uLengths[idx], loads[idx]);
            x = lengthArray.get(idx);
            count++;
            Nxs[count] = -loads[idx]*x + Nx_b(elasticityArray.get(idx), areaArray.get(idx), lengthArray.get(idx),
                    uZeros[idx], uLengths[idx], loads[idx]);
            count++;
        }

        System.arraycopy(uZeros, 0, Uxs, 0, barCount);
        Uxs[barCount] = uLengths[barCount - 1];


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

        for(int idx = 0; idx < 2*barCount; idx++){
            System.out.println("N" + (idx+1) + ":" + Precision.round(Nxs[idx], 4));
        }

        for(int idx = 0; idx < nodeCount; idx++){
            System.out.println("U" + (idx+1) + ":" + Uxs[idx]);
        }
        System.out.println("###################");
    }

    public void openButtonClicked(ActionEvent event) {
        String filename = fileNameTextField.getText() + ".kpr";
        File projectFile = new File(filename);

        try {
            termination_count = 0;
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
                                if (str[1].equals("1")) {
                                    isLeftTermination = true;
                                    termination_count += 1;
                                } else isLeftTermination = false;
                                if (str[2].equals("1")) {
                                    isRightTermination = true;
                                    termination_count += 1;
                                } else isRightTermination = false;
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
            a.setTitle("Warning");
            a.setHeaderText("Wrong file");
            a.setContentText("This file can't be opened!");
            a.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
        calculating();

    }
}
