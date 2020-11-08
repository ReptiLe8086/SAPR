import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class MainWindowController {




    public void PreProcessorClicked(javafx.event.ActionEvent event) throws IOException{

        Stage stage = new Stage();
        FXMLLoader preProcessorLoader = new FXMLLoader();
        Parent root = preProcessorLoader.load(getClass().getResource("/preprocessor.fxml").openStream());
        ((PreProcessorController) preProcessorLoader.getController()).setValidator();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public void processorClicked(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/processor.fxml"));


        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public void postProcessorClicked(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader postProcessorLoader = new FXMLLoader();
        Parent root = postProcessorLoader.load(getClass().getResource("/postprocessor.fxml").openStream());
        ((PostProcessorController) postProcessorLoader.getController()).setStartSettings();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
