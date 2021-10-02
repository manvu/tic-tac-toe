package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


import javafx.scene.*;

/**
 * Present the presntation layer
 *
 * @author Van Phuc Pham,000761144
 */
public class Main extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        mainWindow();
    }

    //Present the main window
    public void mainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("sample.fxml"));
            AnchorPane pane = loader.load();

            Scene scene = new Scene(pane);
            Controller controller = loader.getController();
            controller.setMain(this);
            primaryStage.setTitle("Unbeatable Tic Tac Toe");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
