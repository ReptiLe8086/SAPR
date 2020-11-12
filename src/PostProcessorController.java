import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.commons.math3.util.Precision;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PostProcessorController {

    private Double selectedStep;
    private int selectedBar = -1;
    private int precision;
    private double lengthSum = .0;
    private final ObservableList<Component> components = FXCollections.observableArrayList();


    public void setStartSettings(){
        samplingTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d\\d*\\.?\\d*")) {
                samplingTextField.setText(oldValue);
            }
        });

        pointTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d\\d*\\.?\\d*")) {
                pointTextField.setText(oldValue);
            }
        });

        for(int idx = 0; idx < SAPR.barArray.size(); idx++){
            barComboBox.getItems().add(idx + 1);
            lengthSum += SAPR.barArray.get(idx).length;

        }

        for(int idx = 0; idx < 18; idx++){
            precisionComboBox.getItems().add(idx);
        }
    }

    private boolean isCorrectValue(){
        if(selectedStep == .0){
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Внимание!");
            a.setHeaderText("Неправильный ввод");
            a.setContentText("Шаг дискретизации не может быть равен 0!");
            a.showAndWait();
            return false;
        }
        return true;
    }


    private LineChart<Number, Number> createChart(String type) {
            NumberAxis x = new NumberAxis();
            NumberAxis y = new NumberAxis();
            double roundedX;
            LineChart<Number, Number> componentChart = new LineChart<>(x, y);
            XYChart.Series series = new XYChart.Series();
            ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
            if (type.equals("Nx")) {
                for (double i = 0; i < SAPR.barArray.get(selectedBar - 1).length; i += selectedStep) {
                    roundedX = Precision.round(i, precision);
                    data.add(new XYChart.Data(i, Precision.round(ProcessorController.Nx_ks[selectedBar - 1] * roundedX
                            + ProcessorController.Nx_bs[selectedBar - 1], precision)));
                }
            } else if (type.equals("Ux")) {
                for (double i = 0; i < SAPR.barArray.get(selectedBar - 1).length; i += selectedStep) {
                    roundedX = Precision.round(i, precision);
                    data.add(new XYChart.Data(i, Precision.round(ProcessorController.Ux_as[selectedBar - 1]
                            * roundedX * roundedX
                            + ProcessorController.Ux_bs[selectedBar - 1] * roundedX
                            + ProcessorController.Ux_cs[selectedBar - 1], precision)));
                }
            } else {
                for (double i = 0; i < SAPR.barArray.get(selectedBar - 1).length; i += selectedStep) {
                    roundedX = Precision.round(i, precision);
                    data.add(new XYChart.Data(i, Precision.round(ProcessorController.Sigma_ks[selectedBar - 1] * roundedX
                            + ProcessorController.Sigma_bs[selectedBar - 1], precision)));
                }
            }
            series.setData(data);
            componentChart.getData().add(series);
            return componentChart;

    }

    private AreaChart<Number, Number> createAreaChart(String type){
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        double roundedX;
        double leftBorder = 0;
        double rightBorder = 0;
        AreaChart<Number, Number> areaChart = new AreaChart<>(xAxis, yAxis);
        for(int i = 0; i < SAPR.barArray.size(); i++){
            XYChart.Series series = new XYChart.Series();
            ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
            if(type.equals("Nx")){
                for(double x = 0; x < SAPR.barArray.get(i).length; x += selectedStep) {
                    roundedX = Precision.round(x, precision);
                    data.add(new XYChart.Data(roundedX + leftBorder, Precision.round(ProcessorController.Nx_ks[i] * roundedX
                            + ProcessorController.Nx_bs[i], precision)));
                }
                leftBorder += SAPR.barArray.get(i).length;
                series.setData(data);
                areaChart.getData().add(series);
            }
            else if(type.equals("Ux")){
                for(double x = 0; x < SAPR.barArray.get(i).length; x += selectedStep) {
                    roundedX = Precision.round(x, precision);
                    data.add(new XYChart.Data(roundedX + leftBorder, Precision.round(ProcessorController.Ux_as[i]
                            * roundedX * roundedX
                            + ProcessorController.Ux_bs[i] * roundedX
                            + ProcessorController.Ux_cs[i], precision)));
                }
                leftBorder += SAPR.barArray.get(i).length;
                series.setData(data);
                areaChart.getData().add(series);
            }
            else {
                for(double x = 0; x < SAPR.barArray.get(i).length; x += selectedStep) {
                    roundedX = Precision.round(x, precision);
                    data.add(new XYChart.Data(roundedX + leftBorder, Precision.round(ProcessorController.Sigma_ks[i] * roundedX
                            + ProcessorController.Sigma_bs[i], precision)));
                }
                leftBorder += SAPR.barArray.get(i).length;
                series.setData(data);
                areaChart.getData().add(series);
            }
        }
        return areaChart;
    }

    @FXML
    private TableView<Component> resultTable;

    @FXML
    private TableColumn<Component, Double> xTableColumn;

    @FXML
    private TableColumn<Component, Double> nTableColumn;

    @FXML
    private TableColumn<Component, Double> uTableColumn;

    @FXML
    private TableColumn<Component, Double> sigmaTableColumn;

    @FXML
    private ComboBox<Integer> barComboBox;

    @FXML
    private TextField samplingTextField;

    @FXML
    private ComboBox<Integer> precisionComboBox;

    @FXML
    private TextField pointTextField;

    @FXML
    private Text pointComponentsText;


    public void barComboBClicked(ActionEvent event) { selectedBar = barComboBox.getValue(); }


    public void getValuesBClicked(ActionEvent event) {
        if (selectedBar >= 1){
            resultTable.getItems().clear();
            components.clear();
            double roundedX;
        selectedStep = Double.parseDouble(samplingTextField.getText());
        if (isCorrectValue()) {
            for (double i = 0; i < SAPR.barArray.get(selectedBar - 1).length; i += selectedStep) {

                roundedX = Precision.round(i, precision);
                Component component = new Component(roundedX,
                        Precision.round(ProcessorController.Nx_ks[selectedBar - 1] * roundedX
                                + ProcessorController.Nx_bs[selectedBar - 1], precision),
                        Precision.round(ProcessorController.Ux_as[selectedBar - 1] * roundedX * roundedX
                                + ProcessorController.Ux_bs[selectedBar - 1] * roundedX
                                + ProcessorController.Ux_cs[selectedBar - 1], precision),
                        Precision.round(ProcessorController.Sigma_ks[selectedBar - 1] * roundedX
                                + ProcessorController.Sigma_bs[selectedBar - 1], precision)
                );
                components.add(component);
                resultTable.getItems().add(component);
                xTableColumn.setCellValueFactory(new PropertyValueFactory<>("x"));
                nTableColumn.setCellValueFactory(new PropertyValueFactory<>("N"));
                uTableColumn.setCellValueFactory(new PropertyValueFactory<>("U"));
                sigmaTableColumn.setCellValueFactory(new PropertyValueFactory<>("Sigma"));
                sigmaTableColumn.setCellFactory(column -> new TableCell<>() {
                    @Override
                    protected void updateItem(Double item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(item.toString());
                            if (Math.abs(item) > SAPR.barArray.get(selectedBar - 1).sigma) {
                                setStyle("-fx-background-color: red");
                            } else {
                                setStyle("");
                            }
                            setTextFill(Color.BLACK);
                        }

                    }
                });
            }


        }
    }
        else {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Внимание!");
            a.setHeaderText("Нужно выбрать стержень");
            a.setContentText("Пожалуйста, выберите стержень!");
            a.showAndWait();}
    }

    public void precisionChosen(ActionEvent event) {
        precision = precisionComboBox.getValue();
    }

    public void findValuesBClicked(ActionEvent event) {
        Double mark = .0;
        Double point = Double.parseDouble(pointTextField.getText());
        //System.out.println(point);
        if(point <= lengthSum){
        double x;
        for (int idx = 0; idx < SAPR.barArray.size(); idx++) {
            x = point - mark;
            mark += SAPR.barArray.get(idx).length;
            if (point < mark) {
                //x = mark - point;
                System.out.println(x);
                pointComponentsText.setText("N:" + Precision.round((ProcessorController.Nx_ks[idx] * x
                        + ProcessorController.Nx_bs[idx]), precision) + " U:"
                        + Precision.round((ProcessorController.Ux_as[idx] * x * x +
                        ProcessorController.Ux_bs[idx] * x + ProcessorController.Ux_cs[idx]), precision) + " Sigma:"
                        + Precision.round((ProcessorController.Sigma_ks[idx] * x + ProcessorController.Sigma_bs[idx]),
                        precision));
                break;
            }

        }
    }
        else {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Внимание!");
            a.setHeaderText("Неправильный ввод");
            a.setContentText("Точка выходит за пределы конструкции!");
            a.showAndWait();
        }
    }

    public void graphicsButtonClicked() {

        if(selectedBar >= 1) {
            selectedStep = Double.parseDouble(samplingTextField.getText());
            Stage stage = new Stage();
            stage.setTitle("Charts");
            Group group = new Group();

            ComboBox<String> componentComboBox = new ComboBox<>();
            componentComboBox.getItems().add("Nx");
            componentComboBox.getItems().add("Ux");
            componentComboBox.getItems().add("Sigmax");
            group.getChildren().add(componentComboBox);
            stage.setScene(new Scene(group, 1024, 600));
            componentComboBox.setOnAction(event -> {

                if (group.getChildren().size() > 1) {
                    group.getChildren().remove(1);
                }
                LineChart<Number, Number> chart = createChart(componentComboBox.getValue());
                chart.setLayoutX(0);
                chart.setLayoutY(100);
                chart.setMinHeight(500);
                chart.setMinWidth(960);
                chart.setLegendVisible(false);

                group.getChildren().add(chart);

            });

            stage.setResizable(true);
            stage.show();
        }
        else
        {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Внимание!");
            a.setHeaderText("Нужно выбрать стержень");
            a.setContentText("Пожалуйста, выберите стержень!");
            a.showAndWait();
        }
    }

    public void epureButtonClicked() {
        selectedStep = Double.parseDouble(samplingTextField.getText());
        Stage stage = new Stage();
        stage.setTitle("Diagrams");
        Group group = new Group();
        ComboBox<String> componentComboBox = new ComboBox<>();
        componentComboBox.getItems().add("Nx");
        componentComboBox.getItems().add("Ux");
        componentComboBox.getItems().add("Sigmax");
        group.getChildren().add(componentComboBox);
        stage.setScene(new Scene(group, 1024, 600));
        componentComboBox.setOnAction(event -> {
            if (group.getChildren().size() > 1) {
                group.getChildren().remove(1);
            }
            AreaChart<Number, Number> areaChart = createAreaChart(componentComboBox.getValue());
            areaChart.setLayoutX(0);
            areaChart.setLayoutY(100);
            areaChart.setMinHeight(500);
            areaChart.setMinWidth(960);
            areaChart.setLegendVisible(false);
            group.getChildren().add(areaChart);
        });
        stage.setResizable(true);
        stage.show();
    }

    public void createFileBClicked() {
        Stage stage = new Stage();
        stage.setTitle("Project File");
        Group group = new Group();
        Text inviteText = new Text("Введите имя файла:");
        inviteText.setX(150);
        inviteText.setY(50);
        TextField fileNameTextField = new TextField();
        fileNameTextField.setLayoutX(125);
        fileNameTextField.setLayoutY(60);
        Button createButton = new Button();
        createButton.setLayoutX(110);
        createButton.setLayoutY(90);
        createButton.setText("Сформировать файл проекта");
        createButton.setOnAction(event -> {
            String fileName = fileNameTextField.getText() + ".rst";
            File projectFile = new File(fileName);

             try {
                 FileWriter fileWriter = new FileWriter(projectFile);
                 fileWriter.write(selectedBar + "bar with sampling step:" + selectedStep +" \nX  Nx  Ux  SigmaX\n");
                 for (Component component : components) {
                     fileWriter.write(component.x + " "
                             + component.N + " " + component.U + " " + component.Sigma + "\n");
                 }
                 fileWriter.flush();
                 fileWriter.close();
             } catch (IOException e) {
                 e.printStackTrace();
             }
             stage.close();
        });
        group.getChildren().add(inviteText);
        group.getChildren().add(fileNameTextField);
        group.getChildren().add(createButton);
        stage.setScene(new Scene(group, 380, 150));
        stage.setResizable(false);
        stage.show();
    }

}
