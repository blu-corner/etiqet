package com.neueda.etiqet.orderbook.etiqetorderbook;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class FIXServerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(FIXServerApplication.class.getResource("fixserver.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("FIX Server");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
