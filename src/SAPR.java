import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class SAPR extends Application{


    public static void main(String[] args) {
        Application.launch(args);
    }

    public static ArrayList<Bar> barArray = new ArrayList<>();

    public static ArrayList<Force> forceArray  = new ArrayList<>();

    public static ArrayList<Load> loadArray  = new ArrayList<>();



    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader mainLoader = new FXMLLoader();
        URL mainXmlUrl = getClass().getResource("/mainWindow.fxml");
        mainLoader.setLocation(mainXmlUrl);
        Parent mainWindow = mainLoader.load();
        stage.setScene(new Scene(mainWindow));

        stage.show();




    }

}


