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
}
