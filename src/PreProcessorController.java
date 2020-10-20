import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;



public class PreProcessorController {
    public Integer bar_count = 1;
    public Integer selected_bar = -1;
    public Integer selected_node = -1;
    public Integer termination_count = 0;

    public ObservableList<Bar> barr = FXCollections.observableArrayList();

    public ObservableList<Force> forceAr = FXCollections.observableArrayList();

    public ObservableList<Load> loadAr = FXCollections.observableArrayList();

    public void setValidator() {
        ElasticityTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d\\d*\\.?\\d*[e,E]?-?\\d*")) {
                ElasticityTextField.setText(oldValue);
            }
        });

        AreaTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d\\d*\\.?\\d*[e,E]?-?\\d*")) {
                AreaTextField.setText(oldValue);
            }
        });

        LengthTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d\\d*\\.?\\d*[e,E]?-?\\d*")) {
                LengthTextField.setText(oldValue);
            }
        });

        sigmaTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d\\d*\\.?\\d*[e,E]?-?\\d*")) {
                sigmaTextField.setText(oldValue);
            }
        });

        loadTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("-?\\d\\d*\\.?\\d*[e,E]?-?\\d*")) {
                loadTextField.setText(oldValue);
            }
        });

        forceTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("-?\\d\\d*\\.?\\d*[e,E]?-?\\d*")) {
                forceTextField.setText(oldValue);
            }
        });
    }

    public boolean isCorrectValue() {

        if (Double.parseDouble(AreaTextField.getText()) == 0 ||
                Double.parseDouble(ElasticityTextField.getText()) == 0 ||
                Double.parseDouble(LengthTextField.getText()) == 0 ||
                Double.parseDouble(sigmaTextField.getText()) == 0) {
            Alert a = new Alert(AlertType.WARNING);
            a.setTitle("Warning!");
            a.setHeaderText("Wrong input");
            a.setContentText("Values can't be 0!");
            a.showAndWait();
            return true;
        }
        return false;
    }

    @FXML
    private TextField ElasticityTextField;

    @FXML
    private TextField AreaTextField;

    @FXML
    private TextField LengthTextField;

    @FXML
    private TextField sigmaTextField;

    @FXML
    private TableView<Bar> barTable;

    @FXML
    private TableColumn<Bar, Integer> numberColumn;

    @FXML
    private TableColumn<Bar, Double> elasticityColumn;

    @FXML
    private TableColumn<Bar, Double> areaColumn;

    @FXML
    private TableColumn<Bar, Double> lengthColumn;

    @FXML
    private TableColumn<Bar, Double> sigmaColumn;

    @FXML
    private ComboBox<Integer> loadComboBox;

    @FXML
    private ComboBox<Integer> nodeComboBox;

    @FXML
    private TextField loadTextField;

    @FXML
    private TextField forceTextField;

    @FXML
    private TableView<Force> forceTable;

    @FXML
    private TableColumn<Force, Integer> nodeColumn;

    @FXML
    private TableColumn<Force, Double> forceColumn;

    @FXML
    private TableView<Load> qTable;

    @FXML
    private TableColumn<Load, Integer> barLoadColumn;

    @FXML
    private TableColumn<Load, Double> loadColumn;

    @FXML
    private CheckBox leftTermination;

    @FXML
    private CheckBox rightTermination;

    @FXML
    private void addBarClicked() {
        if (!isCorrectValue()) {
            Bar bar = new Bar(bar_count, Double.parseDouble(AreaTextField.getText()),
                    Double.parseDouble(ElasticityTextField.getText()), Double.parseDouble(LengthTextField.getText()),
                    Double.parseDouble(sigmaTextField.getText()));


            SAPR.barArray.add(bar);
            barTable.getItems().add(bar);
            numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
            elasticityColumn.setCellValueFactory(new PropertyValueFactory<>("elasticity"));
            areaColumn.setCellValueFactory(new PropertyValueFactory<>("area"));
            lengthColumn.setCellValueFactory(new PropertyValueFactory<>("length"));
            sigmaColumn.setCellValueFactory(new PropertyValueFactory<>("sigma"));

            barr.add(bar);
            bar_count++;

            nodeComboBox.getItems().clear();
            loadComboBox.getItems().clear();
            for (int idx = 1; idx <= bar_count; idx++) {
                nodeComboBox.getItems().add(idx);
            }
            for (int idx = 1; idx < bar_count; idx++) {
                loadComboBox.getItems().add(idx);
            }

        }
    }

    @FXML
    public void nodeCBoxClicked() {
        selected_node = nodeComboBox.getValue();
    }

    @FXML
    public void deleteBClicked() {
        SAPR.barArray.remove(SAPR.barArray.size() - 1);
        barTable.getItems().remove(SAPR.barArray.size());
        for (int idx = 0; idx < SAPR.forceArray.size(); idx++) {
            if (SAPR.forceArray.get(idx).number.equals(bar_count)) {
                SAPR.forceArray.remove(idx);
                forceAr.remove(idx);
                forceTable.getItems().remove(idx);
                forceTable.refresh();
                break;
            }
        }
        for (int idx = 0; idx < SAPR.loadArray.size(); idx++) {

            if (SAPR.loadArray.get(idx).number == bar_count - 1) {
                SAPR.loadArray.remove(idx);
                loadAr.remove(idx);
                qTable.getItems().remove(idx);
                qTable.refresh();
                break;
            }
        }
        bar_count--;
        forceTable.refresh();
        qTable.refresh();
        nodeComboBox.getItems().clear();
        loadComboBox.getItems().clear();
        for (int idx = 1; idx <= bar_count; idx++) {
            nodeComboBox.getItems().add(idx);
        }
        for (int idx = 1; idx < bar_count; idx++) {
            loadComboBox.getItems().add(idx);
        }
        //        forceTable.getItems().remove(PreProcessor.nodeArray.size() - 1);
//        qTable.getItems().remove(PreProcessor.loadArray.size() - 1);
    }

    @FXML
    public void addForceClicked() {
        if (selected_node >= 1) {
            Force force = new Force(selected_node, Double.parseDouble(forceTextField.getText()));
            for (int idx = 0; idx < SAPR.forceArray.size(); idx++) {
                if (selected_node.equals(SAPR.forceArray.get(idx).number)) {
                    SAPR.forceArray.remove(idx);
                    forceAr.remove(idx);
                    forceTable.getItems().remove(idx);
                    forceTable.refresh();
                    break;
                }
            }
            SAPR.forceArray.add(force);
            forceTable.getItems().add(force);
            nodeColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
            forceColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
            forceAr.add(force);
        } else {
            Alert a = new Alert(AlertType.WARNING);
            a.setTitle("Warning!");
            a.setHeaderText("Choose node");
            a.setContentText("Please, choose node");
            a.showAndWait();
        }

    }

    @FXML
    public void loadCBoxClicked() {
        selected_bar = loadComboBox.getValue();
    }

    @FXML
    public void addLoadClicked() {
        if (selected_bar >= 1) {
            Load load = new Load(selected_bar, Double.parseDouble(loadTextField.getText()));
            for (int idx = 0; idx < SAPR.loadArray.size(); idx++) {
                if (selected_bar.equals(SAPR.loadArray.get(idx).number)) {
                    SAPR.loadArray.remove(idx);
                    loadAr.remove(idx);
                    qTable.getItems().remove(idx);
                    qTable.refresh();
                    break;
                }
            }
            SAPR.loadArray.add(load);
            qTable.getItems().add(load);
            barLoadColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
            loadColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
            loadAr.add(load);
        } else {
            Alert a = new Alert(AlertType.WARNING);
            a.setTitle("Warning!");
            a.setHeaderText("Choose bar");
            a.setContentText("Please, choose bar");
            a.showAndWait();
        }
    }

    @FXML
    public void leftTerminationClicked() {
        if (leftTermination.isSelected()) {
            termination_count++;
        } else {
            termination_count--;
        }
    }

    @FXML
    public void rightTerminationClicked() {
        if (rightTermination.isSelected()) {
            termination_count++;
        } else {
            termination_count--;
        }
    }

    public boolean isValidTermination() {
        if (termination_count <= 0) {
            Alert a = new Alert(AlertType.WARNING);
            a.setTitle("Warning!");
            a.setHeaderText("Wrong termination count!");
            a.setContentText("Please, choose at least 1 terminations!");
            a.showAndWait();
            return false;
        } else {
            return true;
        }
    }

    public void paintButtonClicked(){
        if (isValidTermination()) {

            Canvas canvas = new Canvas(1024, 680);
            var gc = canvas.getGraphicsContext2D();
            gc.setFill(Color.WHITE);
            double wholeLength = 900;
            double wholeArea = 450;
            double axisCoord = 300;
            double totalLength = 0;
            double totalArea = 0;


            ArrayList<Double> paintLengthArray = new ArrayList<>();
            ArrayList<Double> paintAreaArray = new ArrayList<>();
            ArrayList<Rectangle> barRectanglesArray = new ArrayList<>();
            ArrayList<Double> lengthArray = new ArrayList<>();
            ArrayList<Double> areaArray = new ArrayList<>();

            for (int idx = 0; idx < SAPR.barArray.size(); idx++) {
                totalLength += SAPR.barArray.get(idx).length;
                totalArea += SAPR.barArray.get(idx).area;
            }


            //scaling
            double newTotalLength = 0;
            double newTotalArea = 0;

            for(int idx = 0; idx < SAPR.barArray.size(); idx++){
                if(SAPR.barArray.get(idx).length <= 0.1 * totalLength){
                    lengthArray.add(0.1 * totalLength);
                }
                else{
                    lengthArray.add(SAPR.barArray.get(idx).length);
                }
                newTotalLength += lengthArray.get(idx);
            }

            for(int idx = 0; idx < SAPR.barArray.size(); idx++){
                if(SAPR.barArray.get(idx).area <= 0.1 * totalArea){
                    areaArray.add(0.1 * totalArea);
                }
                else{
                    areaArray.add(SAPR.barArray.get(idx).area);
                }
                newTotalArea += areaArray.get(idx);
            }


            for (int idx = 0; idx < SAPR.barArray.size(); idx++) {
                paintLengthArray.add((lengthArray.get(idx) / newTotalLength) * wholeLength);
                paintAreaArray.add((areaArray.get(idx) / newTotalArea) * wholeArea);

            }

            final double leftX = 62;
            final double rightX = 962;

            double x = leftX;
            double y;

            Group group = new Group(canvas);


            //bars
            for (int idx = 0; idx < SAPR.barArray.size(); idx++) {
                Rectangle bar_rectangle = new Rectangle();
                bar_rectangle.setX(x);
                y = axisCoord - 0.5 * paintAreaArray.get(idx);
                bar_rectangle.setY(y);
                bar_rectangle.setHeight(paintAreaArray.get(idx));
                bar_rectangle.setWidth(paintLengthArray.get(idx));
                bar_rectangle.setFill(Color.TRANSPARENT);
                bar_rectangle.setStroke(Color.BLACK);
                x += paintLengthArray.get(idx);
                barRectanglesArray.add(bar_rectangle);
                group.getChildren().add(bar_rectangle);
            }


            //Left Termination
            Line line1 = new Line();
            line1.setStartX(leftX);
            line1.setEndX(leftX);
            line1.setStartY((axisCoord - 0.5 * paintAreaArray.get(0)) - 25);
            line1.setEndY((axisCoord + 0.5 * paintAreaArray.get(0)) + 25);
            line1.setStrokeWidth(2);
            line1.setStroke(Color.BLACK);
            double offset1 = (line1.getEndY() - line1.getStartY()) / 10;
            if (leftTermination.isSelected()) {
                group.getChildren().add(line1);
                for (int idx = 0; idx <= 10; idx++) {
                    Line streak = new Line();
                    streak.setStartY(line1.getStartY() + idx * offset1);
                    streak.setStartX(line1.getStartX());
                    streak.setEndX(line1.getEndX() - offset1);
                    streak.setEndY(streak.getStartY() + offset1);
                    streak.setStrokeWidth(2);
                    streak.setStroke(Color.BLACK);
                    group.getChildren().add(streak);
                }
            }


            //Right Termination
            Line line2 = new Line();
            line2.setStartX(rightX);
            line2.setEndX(rightX);
            line2.setStartY((axisCoord - 0.5 * paintAreaArray.get(paintAreaArray.size() - 1)) - 25);
            line2.setEndY((axisCoord + 0.5 * paintAreaArray.get(paintAreaArray.size() - 1)) + 25);
            line2.setStrokeWidth(2);
            line2.setStroke(Color.BLACK);
            double offset2 = (line2.getEndY() - line2.getStartY()) / 10;
            if (rightTermination.isSelected()) {
                group.getChildren().add(line2);
                for (int idx = 0; idx <= 10; idx++) {
                    Line streak = new Line();
                    streak.setStartY(line2.getStartY() + idx * offset2);
                    streak.setStartX(line2.getStartX());
                    streak.setEndX(line2.getEndX() + offset2);
                    streak.setEndY(streak.getStartY() - offset2);
                    streak.setStrokeWidth(2);
                    streak.setStroke(Color.BLACK);
                    group.getChildren().add(streak);
                }
            }


            //forces
            double forceLineLength = 30;
            for (int idx = 0; idx < SAPR.forceArray.size(); idx++) {
                for (int j = 0; j < SAPR.barArray.size(); j++) {
                    if (SAPR.forceArray.get(idx).number.equals(SAPR.barArray.get(j).number)) {
                        Line forceLine = new Line();
                        forceLine.setStartX(barRectanglesArray.get(j).getX());
                        forceLine.setStartY(axisCoord);
                        forceLine.setEndY(axisCoord);
                        forceLine.setStrokeWidth(3);
                        forceLine.setStroke(Color.BLUE);
                        if (SAPR.forceArray.get(idx).value > 0) {
                            forceLine.setEndX(forceLine.getStartX() + forceLineLength);

                            Line stroke1 = new Line();
                            stroke1.setStartX(forceLine.getEndX());
                            stroke1.setStartY(forceLine.getEndY());
                            stroke1.setEndX(forceLine.getEndX() - forceLineLength / 2);
                            stroke1.setEndY(forceLine.getEndY() - forceLineLength / 2);
                            stroke1.setStrokeWidth(2);
                            stroke1.setStroke(Color.BLUE);
                            group.getChildren().add(stroke1);

                            Line stroke2 = new Line();
                            stroke2.setStartX(forceLine.getEndX());
                            stroke2.setStartY(forceLine.getEndY());
                            stroke2.setEndX(forceLine.getEndX() - forceLineLength / 2);
                            stroke2.setEndY(forceLine.getEndY() + forceLineLength / 2);
                            stroke2.setStrokeWidth(2);
                            stroke2.setStroke(Color.BLUE);
                            group.getChildren().add(stroke2);

                            group.getChildren().add(forceLine);
                        } else if(SAPR.forceArray.get(idx).value < 0){
                            forceLine.setEndX(forceLine.getStartX() - forceLineLength);
                            Line stroke1 = new Line();
                            stroke1.setStartX(forceLine.getEndX());
                            stroke1.setStartY(forceLine.getEndY());
                            stroke1.setEndX(forceLine.getEndX() + forceLineLength / 2);
                            stroke1.setEndY(forceLine.getEndY() - forceLineLength / 2);
                            stroke1.setStrokeWidth(2);
                            stroke1.setStroke(Color.BLUE);
                            group.getChildren().add(stroke1);

                            Line stroke2 = new Line();
                            stroke2.setStartX(forceLine.getEndX());
                            stroke2.setStartY(forceLine.getEndY());
                            stroke2.setEndX(forceLine.getEndX() + forceLineLength / 2);
                            stroke2.setEndY(forceLine.getEndY() + forceLineLength / 2);
                            stroke2.setStrokeWidth(2);
                            stroke2.setStroke(Color.BLUE);
                            group.getChildren().add(stroke2);

                            group.getChildren().add(forceLine);
                        }
                        //group.getChildren().add(forceLine);
                        break;
                    } else if (SAPR.forceArray.get(idx).number == SAPR.barArray.get(j).number + 1) {
                        Line forceLine = new Line();
                        forceLine.setStartX(barRectanglesArray.get(j).getX() + barRectanglesArray.get(j).getWidth());
                        forceLine.setStartY(axisCoord);
                        forceLine.setEndY(axisCoord);
                        forceLine.setStrokeWidth(3);
                        forceLine.setStroke(Color.BLUE);
                        if (SAPR.forceArray.get(idx).value > 0) {
                            forceLine.setEndX(forceLine.getStartX() + forceLineLength);

                            Line stroke1 = new Line();
                            stroke1.setStartX(forceLine.getEndX());
                            stroke1.setStartY(forceLine.getEndY());
                            stroke1.setEndX(forceLine.getEndX() - forceLineLength / 2);
                            stroke1.setEndY(forceLine.getEndY() - forceLineLength / 2);
                            stroke1.setStrokeWidth(2);
                            stroke1.setStroke(Color.BLUE);
                            group.getChildren().add(stroke1);

                            Line stroke2 = new Line();
                            stroke2.setStartX(forceLine.getEndX());
                            stroke2.setStartY(forceLine.getEndY());
                            stroke2.setEndX(forceLine.getEndX() - forceLineLength / 2);
                            stroke2.setEndY(forceLine.getEndY() + forceLineLength / 2);
                            stroke2.setStrokeWidth(2);
                            stroke2.setStroke(Color.BLUE);
                            group.getChildren().add(stroke2);

                            group.getChildren().add(forceLine);

                        } else if(SAPR.forceArray.get(idx).value < 0){
                            forceLine.setEndX(forceLine.getStartX() - forceLineLength);
                            Line stroke1 = new Line();
                            stroke1.setStartX(forceLine.getEndX());
                            stroke1.setStartY(forceLine.getEndY());
                            stroke1.setEndX(forceLine.getEndX() + forceLineLength / 2);
                            stroke1.setEndY(forceLine.getEndY() - forceLineLength / 2);
                            stroke1.setStrokeWidth(2);
                            stroke1.setStroke(Color.BLUE);
                            group.getChildren().add(stroke1);

                            Line stroke2 = new Line();
                            stroke2.setStartX(forceLine.getEndX());
                            stroke2.setStartY(forceLine.getEndY());
                            stroke2.setEndX(forceLine.getEndX() + forceLineLength / 2);
                            stroke2.setEndY(forceLine.getEndY() + forceLineLength / 2);
                            stroke2.setStrokeWidth(2);
                            stroke2.setStroke(Color.BLUE);
                            group.getChildren().add(stroke2);
                            group.getChildren().add(forceLine);
                        }
                        //group.getChildren().add(forceLine);
                        break;
                    }
                }
            }


            //loads
            double offset;
            for (int j = 0; j < SAPR.barArray.size(); j++) {
                for (int k = 0; k < SAPR.loadArray.size(); k++) {
                    if (SAPR.loadArray.get(k).number.equals(SAPR.barArray.get(j).number)) {
                        Line axisLine = new Line();
                        axisLine.setStartX(barRectanglesArray.get(j).getX());
                        axisLine.setStartY(axisCoord);
                        axisLine.setEndY(axisCoord);
                        axisLine.setEndX(barRectanglesArray.get(j).getX() + barRectanglesArray.get(j).getWidth());
                        axisLine.setStrokeWidth(1);
                        axisLine.setStroke(Color.GREEN);
                        offset = (barRectanglesArray.get(j).getWidth() / 10);
                        if(SAPR.loadArray.get(k).value > 0) {
                            for (int idx = 1; idx < 11; idx++) {
                                Line stroke1 = new Line();
                                stroke1.setStartX(barRectanglesArray.get(j).getX() + idx * offset);
                                stroke1.setStartY(axisCoord);
                                stroke1.setEndX(stroke1.getStartX() - 8);
                                stroke1.setEndY(stroke1.getStartY() - 8);
                                stroke1.setStrokeWidth(1);
                                stroke1.setStroke(Color.GREEN);

                                Line stroke2 = new Line();
                                stroke2.setStartX(barRectanglesArray.get(j).getX() + idx * offset);
                                stroke2.setStartY(axisCoord);
                                stroke2.setEndX(stroke1.getStartX() - 8);
                                stroke2.setEndY(stroke1.getStartY() + 8);
                                stroke2.setStrokeWidth(1);
                                stroke2.setStroke(Color.GREEN);


                                group.getChildren().add(stroke1);
                                group.getChildren().add(stroke2);

                            }
                            group.getChildren().add(axisLine);
                        }
                        else if(SAPR.loadArray.get(k).value < 0) {
                            for (int idx = 0; idx < 10; idx++) {
                                Line stroke1 = new Line();
                                stroke1.setStartX(barRectanglesArray.get(j).getX() + idx * offset);
                                stroke1.setStartY(axisCoord);
                                stroke1.setEndX(stroke1.getStartX() + 8);
                                stroke1.setEndY(stroke1.getStartY() - 8);
                                stroke1.setStrokeWidth(1);
                                stroke1.setStroke(Color.GREEN);

                                Line stroke2 = new Line();
                                stroke2.setStartX(barRectanglesArray.get(j).getX() + idx * offset);
                                stroke2.setStartY(axisCoord);
                                stroke2.setEndX(stroke1.getStartX() + 8);
                                stroke2.setEndY(stroke1.getStartY() + 8);
                                stroke2.setStrokeWidth(1);
                                stroke2.setStroke(Color.GREEN);


                                group.getChildren().add(stroke1);
                                group.getChildren().add(stroke2);

                            }
                            group.getChildren().add(axisLine);
                        }
                        //group.getChildren().add(axisLine);

                    }
                }
            }


            Stage stage = new Stage();
            stage.setTitle("ConstructionPaint");
            stage.setScene(new Scene(group, 1024, 680));
            stage.setResizable(false);

            stage.show();

        }
    }

    public void projectFBClicked() {
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
            String fileName = fileNameTextField.getText() + ".kpr";
            File projectFile = new File(fileName);


            try {
                FileWriter fileWriter = new FileWriter(projectFile);
                fileWriter.write("Bar parameters:\n");
                fileWriter.write("  №   A   E   L   Sigma\n");
                for(int idx = 0; idx < SAPR.barArray.size(); idx++)
                {
                    fileWriter.write("= " + SAPR.barArray.get(idx).number + " " +
                                         SAPR.barArray.get(idx).area + " " +
                                         SAPR.barArray.get(idx).elasticity + " " +
                                         SAPR.barArray.get(idx).length + " " +
                                         SAPR.barArray.get(idx).sigma +"\n");
                }
                fileWriter.write("Terminations:\n  Left   Right\n");
                if(leftTermination.isSelected()){  fileWriter.write("? 1 ");  }
                else { fileWriter.write("? 0 ");  }
                if(rightTermination.isSelected()) { fileWriter.write("1\n"); }
                else { fileWriter.write("0\n"); }
                fileWriter.write("Concentrated loads:\n");
                fileWriter.write("  Node F\n");
                for(int idx = 0; idx < SAPR.forceArray.size(); idx++){
                    fileWriter.write("# " + SAPR.forceArray.get(idx).number + " " +
                                         SAPR.forceArray.get(idx).value +"\n");
                }
                fileWriter.write("Distributed loads:\n");
                fileWriter.write("  Bar q\n");
                for(int idx = 0; idx < SAPR.loadArray.size(); idx++){
                    fileWriter.write("~ " + SAPR.loadArray.get(idx).number + " " +
                                         SAPR.loadArray.get(idx).value +"\n");
                }
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        stage.close();
        });

        group.getChildren().add(inviteText);
        group.getChildren().add(createButton);
        group.getChildren().add(fileNameTextField);
        stage.setScene(new Scene(group, 380, 150));
        stage.setResizable(false);
        stage.show();
    }

    public void openFileButtonClicked() {
        Stage stage = new Stage();
        stage.setTitle("Open project file");
        Group group = new Group();
        Text inviteText = new Text("Enter a filename");
        inviteText.setX(150);
        inviteText.setY(50);
        TextField fileNameTextField = new TextField();
        fileNameTextField.setLayoutX(125);
        fileNameTextField.setLayoutY(60);
        Button openButton = new Button();
        openButton.setLayoutX(128);
        openButton.setLayoutY(90);
        openButton.setText("Открыть файл проекта");
        openButton.setOnAction(event -> {
            String filename = fileNameTextField.getText() + ".kpr";
            File projectFile = new File(filename);

            try {
                termination_count = 0;
                bar_count = 1;
                SAPR.barArray.clear();
                SAPR.forceArray.clear();
                SAPR.loadArray.clear();
                barTable.getItems().clear();
                forceTable.getItems().clear();
                qTable.getItems().clear();
                barr.clear();
                forceAr.clear();
                loadAr.clear();
                BufferedReader fileReader = new BufferedReader(new FileReader(projectFile));
                String buffer;
                while ((buffer = fileReader.readLine()) != null){

                    String[] str = buffer.split(" ");

                    for(String line : str){
                        if(!line.isEmpty()){
                            switch (line) {
                                case "=" -> {
                                    Bar bar = new Bar(Integer.parseInt(str[1]),
                                            Double.parseDouble(str[2]),
                                            Double.parseDouble(str[3]),
                                            Double.parseDouble(str[4]),
                                            Double.parseDouble(str[5]));
                                    SAPR.barArray.add(bar);
                                    barTable.getItems().add(bar);
                                    numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
                                    elasticityColumn.setCellValueFactory(new PropertyValueFactory<>("elasticity"));
                                    areaColumn.setCellValueFactory(new PropertyValueFactory<>("area"));
                                    lengthColumn.setCellValueFactory(new PropertyValueFactory<>("length"));
                                    sigmaColumn.setCellValueFactory(new PropertyValueFactory<>("sigma"));
                                    barr.add(bar);
                                    bar_count++;
                                }
                                case "?" -> {
                                   if(str[1].equals("1")){
                                       leftTermination.setSelected(true);
                                       termination_count += 1;
                                   }
                                   else leftTermination.setSelected(false);
                                   if(str[2].equals("1")){
                                       rightTermination.setSelected(true);
                                       termination_count += 1;
                                   }
                                   else rightTermination.setSelected(false);
                                }
                                case "#" -> {
                                    Force force = new Force(Integer.parseInt(str[1]),
                                            Double.parseDouble(str[2]));
                                    SAPR.forceArray.add(force);
                                    forceTable.getItems().add(force);
                                    nodeColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
                                    forceColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
                                    forceAr.add(force);
                                    for (int idx = 1; idx <= bar_count; idx++) {
                                        nodeComboBox.getItems().add(idx);
                                    }
                                }
                                case "~" -> {
                                    Load load = new Load(Integer.parseInt(str[1]),
                                            Double.parseDouble(str[2]));
                                    SAPR.loadArray.add(load);
                                    qTable.getItems().add(load);
                                    barLoadColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
                                    loadColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
                                    loadAr.add(load);
                                    for (int idx = 1; idx < bar_count; idx++) {
                                        loadComboBox.getItems().add(idx);
                                    }
                                }
                            }
                        }
                    }
                    //System.out.println("tut");
                }
                //System.out.println("last");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Alert a = new Alert(AlertType.WARNING);
                a.setTitle("Warning");
                a.setHeaderText("Wrong file");
                a.setContentText("This file can't be opened!");
                a.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }

        stage.close();
        });
        group.getChildren().add(inviteText);
        group.getChildren().add(openButton);
        group.getChildren().add(fileNameTextField);
        stage.setScene(new Scene(group, 380, 150));
        stage.setResizable(false);
        stage.show();


    }

}
