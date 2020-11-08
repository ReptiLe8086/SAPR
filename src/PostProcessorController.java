import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import org.apache.commons.math3.util.Precision;

import java.awt.*;

public class PostProcessorController {

    private Double selectedStep;
    private int selectedBar;
    private int precision;
    private ObservableList<Component> components = FXCollections.observableArrayList();

    public void setStartSettings(){
        samplingTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d\\d*\\.?\\d*")) {
                samplingTextField.setText(oldValue);
            }
        });

        for(int idx = 0; idx < SAPR.barArray.size(); idx++){
            barComboBox.getItems().add(idx + 1);
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


    public void barComboBClicked(ActionEvent event) { selectedBar = barComboBox.getValue(); }


    public void getValuesBClicked(ActionEvent event) {
        resultTable.getItems().clear();
        components.clear();
        selectedStep = Double.parseDouble(samplingTextField.getText());
        if (isCorrectValue()) {
            for (double i = 0; i < SAPR.barArray.get(selectedBar - 1).length; i += selectedStep) {

                double roundedX = Precision.round(i, precision);
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


    public void precisionChosen(ActionEvent event) {
        precision = precisionComboBox.getValue();
    }
}
