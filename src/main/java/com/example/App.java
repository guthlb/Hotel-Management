package com.example;

import com.hotel.util.DatabaseConnection;
import com.hotel.util.ThemeManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        try {
            DatabaseConnection.initializeDatabase();

            Parent root = FXMLLoader.load(getClass().getResource("/fxml/dashboard.fxml"));

            Scene scene = new Scene(root);
            ThemeManager.applyTheme(scene);

            stage.setTitle("Hotel Management System");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
