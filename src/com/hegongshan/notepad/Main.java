package com.hegongshan.notepad;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("view/Main.fxml"));
		Scene scene = new Scene(root,400,400);
		scene.getStylesheets().add("com/hegongshan/notepad/resource/application.css");
		primaryStage.getIcons().add(new Image("com/hegongshan/notepad/resource/notepad.png"));
		primaryStage.setScene(scene);
		primaryStage.setTitle("记事本");
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
