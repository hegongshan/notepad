package com.hegongshan.notepad.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hegongshan.notepad.ui.FontPicker;
import com.hegongshan.notepad.util.FileUtils;
import com.hegongshan.notepad.util.StringUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

public class MainController {
	private int row = 1;
	private Label caretRowLabel;
	private Label caretColumnLabel;
	private int caretRow = 1;
	private int caretColumn = 1;

	@FXML
	private VBox leftSideBar;
	@FXML
	private TextArea textArea;
	@FXML
	private TabPane tabPane;

	public void newFile() {
		Tab tab = getTab();
		tab.setText("* 新建文件");
		tabPane.getTabs().add(tab);
		SelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
		selectionModel.select(tab);
	}

	public void openFile(ActionEvent event) {
		Tab tab = new Tab();
		tab.setClosable(true);
		VBox newLeftSideBar = new VBox();
		newLeftSideBar.setPrefWidth(55);
		newLeftSideBar.setMinWidth(55);
		TextArea newTextArea = new TextArea();
		/*
		 * newTextArea.widthProperty().addListener((value,oldValue,newValue)->{
		 * newValue =
		 * tabPane.getScene().widthProperty().doubleValue()-newLeftSideBar.
		 * widthProperty().doubleValue(); });
		 */
		HBox container = new HBox();
		container.getChildren().addAll(newLeftSideBar, newTextArea);
		FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showOpenDialog(textArea.getScene().getWindow());
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String str;
			while ((str = br.readLine()) != null) {
				newTextArea.appendText(str + "\n");
			}
			br.close();
			int newRows = getRows(newTextArea.getText());
			if (newRows > row) {
				for (int i = 1; i <= newRows - row; i++) {
					HBox hBox2 = new HBox();
					hBox2.setAlignment(Pos.CENTER);
					hBox2.getChildren().add(new Label(String.valueOf(row + i)));
					newLeftSideBar.getChildren().add(hBox2);
				}
			} else if (newRows < row) {
				for (int i = 1; i <= row - newRows; i++) {
					newLeftSideBar.getChildren().remove(row - i);
				}
			}
			row = newRows;
		} catch (Exception e) {
			e.printStackTrace();
		}
		tab.setContent(container);
		tab.setText(file.getName());
		tab.setTooltip(new Tooltip(file.getPath()));
		tabPane.getTabs().add(tab);
		SelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
		selectionModel.select(tab);
	}

	public void saveFile() {
		String text = textArea.getText();
		if (StringUtils.isNotEmpty(text)) {
			FileChooser fileChooser = new FileChooser();
			File file = fileChooser.showSaveDialog(textArea.getScene().getWindow());
			try {
				FileUtils.writeFile(file, text);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void exit(ActionEvent event) {
		Window window = textArea.getScene().getWindow();
		((Stage) window).close();
	}

	public void keyRelease() {
		int newRows = getRows(textArea.getText());
		if (newRows > row) {
			for (int i = 1; i <= newRows - row; i++) {
				HBox hBox2 = new HBox();
				hBox2.setAlignment(Pos.CENTER);
				hBox2.getChildren().add(new Label(String.valueOf(row + i)));
				leftSideBar.getChildren().add(hBox2);
			}
		} else if (newRows < row) {
			for (int i = 1; i <= row - newRows; i++) {
				leftSideBar.getChildren().remove(row - i);
			}
		}
		row = newRows;

		int caretPos = textArea.getCaretPosition();
		int line = textArea.getText().substring(0, caretPos).lastIndexOf("\n");
		caretPos = (caretPos == 0 ? 1 : caretPos);
		line = (line == -1 ? 0 : line);
		caretColumn = caretPos - line;
		caretRow = line + 1;
		caretRowLabel.setText(String.valueOf(caretRow));
		caretColumnLabel.setText(String.valueOf(caretColumn));
	}

	public void aboutMe(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("../view/About.fxml"));
		Scene scene = new Scene(root, 250, 250);
		Stage about = new Stage();
		about.initOwner(textArea.getScene().getWindow());
		about.setScene(scene);
		about.show();
	}

	public void autoLine(ActionEvent event) {
		textArea.setWrapText(true);
	}

	public Tab getTab() {
		Tab tab = new Tab();
		tab.setClosable(true);

		VBox leftSideBar = new VBox();
		leftSideBar.setPrefWidth(55);
		leftSideBar.setMinWidth(55);
		leftSideBar.setAlignment(Pos.TOP_CENTER);
		leftSideBar.getChildren().add(new Label("1"));
		TextArea textArea = new TextArea();
		textArea.setOnKeyReleased(event -> {
			int newRows = getRows(textArea.getText());
			if (newRows > row) {
				for (int i = 1; i <= newRows - row; i++) {
					HBox hBox2 = new HBox();
					hBox2.setAlignment(Pos.CENTER);
					hBox2.getChildren().add(new Label(String.valueOf(row + i)));
					leftSideBar.getChildren().add(hBox2);
				}
			} else if (newRows < row) {
				for (int i = 1; i <= row - newRows; i++) {
					leftSideBar.getChildren().remove(row - i);
				}
			}
			row = newRows;
		});
		HBox container = new HBox(leftSideBar, textArea);

		tab.setContent(container);
		return tab;
	}

	public void getFont() {
		FontPicker picker = new FontPicker();
		picker.initOwner(tabPane.getScene().getWindow());
		picker.showAndWait();
		System.out.println(picker.getFont().getFamily());
	}

	private int getRows(String text) {
		Pattern pattern = Pattern.compile("\n");
		Matcher matcher = pattern.matcher(text);
		int row = 0;
		while (matcher.find()) {
			row++;
		}
		return row + 1;
	}
}
