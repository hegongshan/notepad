package com.hegongshan.notepad;
	
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hegongshan.notepad.util.FileUtils;
import com.hegongshan.notepad.util.StringUtils;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class Main extends Application {
	
	private Label caretRowLabel;
	private Label caretColumnLabel;
	private int caretRow = 1;
	private int caretColumn = 1;
	
	private String lastEncoding;
	
	private int row = 1;
	
	private VBox sideBar = createLeftSideBar();
	
	private HBox bottomBar = createBottomBar();
	
	private TextArea textArea = createTextArea();
	
	@Override
	public void start(Stage primaryStage) {
		BorderPane root = new BorderPane();
		MenuBar menuBar = createMenuBar(primaryStage);
		root.setTop(menuBar);
		root.setCenter(textArea);
		root.setLeft(sideBar);
		root.setBottom(bottomBar);
		Scene scene = new Scene(root,400,400);
		scene.getStylesheets().add("com/hegongshan/notepad/resource/application.css");
		primaryStage.getIcons().add(new Image("com/hegongshan/notepad/resource/notepad.png"));
		primaryStage.setScene(scene);
		primaryStage.setTitle("记事本");
		primaryStage.show();
	}
	
	public MenuBar createMenuBar(Window window) {
		MenuBar menuBar = new MenuBar();
		menuBar.getStyleClass().add("menuBar");
		ObservableList<Menu> menus = menuBar.getMenus();
		
		Menu fileMenu = new Menu("文件(F)");
		ObservableList<MenuItem> fileMenuItems = fileMenu.getItems();
		MenuItem openMenuItem = new MenuItem("打开(O)");
		openMenuItem.setOnAction(event->{
			textArea.clear();
			FileChooser fileChooser = new FileChooser();
			File file = fileChooser.showOpenDialog(window);
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String str;
				while((str = br.readLine()) != null) {
					textArea.appendText(str+"\n");
				}
				br.close();
				int newRows = getRows(textArea.getText());
				if(newRows>row) {
					for(int i = 1 ; i <= newRows-row;i++) {
						HBox hBox2 = new HBox();
						hBox2.setAlignment(Pos.CENTER);
						hBox2.getChildren().add(new Label(String.valueOf(row+i)));
						sideBar.getChildren().add(hBox2);
					}
				} else if(newRows < row) {
					for(int i = 1 ; i <=row-newRows;i++) {
						sideBar.getChildren().remove(row-i);
					}
				}
				row = newRows;
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		openMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O,KeyCombination.CONTROL_DOWN));
		
		MenuItem saveMenuItem = new MenuItem("另存为(S)");
		saveMenuItem.setOnAction(event->{
			String text = textArea.getText();
			if(StringUtils.isNotEmpty(text)) {
				FileChooser fileChooser = new FileChooser();
				File file = fileChooser.showSaveDialog(window);
				try {
					FileUtils.writeFile(file, text);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		saveMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S,KeyCombination.CONTROL_DOWN));
		
		MenuItem exitMenuItem = new MenuItem("退出(X)");
		exitMenuItem.setOnAction(event->{
			((Stage)window).close();
		});
		exitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.W,KeyCombination.ALT_DOWN));
		fileMenuItems.addAll(openMenuItem,saveMenuItem,new SeparatorMenuItem(),exitMenuItem);
		
		Menu formatMenu = new Menu("格式(O)");
		RadioMenuItem autoLineMenuItem = new RadioMenuItem("自动换行(W)");
		autoLineMenuItem.setOnAction(event->{
			textArea.setWrapText(true);
		});
		formatMenu.getItems().addAll(autoLineMenuItem);
		
		Menu encodingMenu = new Menu("编码(E)");
		MenuItem utf8EncodeMenuItem = new MenuItem("以UTF-8格式编码");
		utf8EncodeMenuItem.setOnAction(event->encodeEventHandle("utf-8"));
		
		MenuItem bg2312EncodeMenuItem = new MenuItem("以GB2312格式编码");
		bg2312EncodeMenuItem.setOnAction(event->encodeEventHandle("GB2312"));
		
		MenuItem gbkEncodeMenuItem = new MenuItem("以GBK格式编码");
		gbkEncodeMenuItem.setOnAction(event->encodeEventHandle("gbk"));
		
		MenuItem asciiEncodeMenuItem = new MenuItem("以US-ASCII格式编码");
		asciiEncodeMenuItem.setOnAction(event->encodeEventHandle("US-ASCII"));
		
		MenuItem isoEncodeMenuItem = new MenuItem("以ISO-8859-1格式编码");
		isoEncodeMenuItem.setOnAction(event->encodeEventHandle("ISO-8859-1"));
		encodingMenu.getItems().addAll(utf8EncodeMenuItem,
				bg2312EncodeMenuItem,gbkEncodeMenuItem,asciiEncodeMenuItem,
				isoEncodeMenuItem);
		
		
		Menu helpMenu = new Menu("帮助(H)");
		MenuItem aboutMenuItem = new MenuItem("关于(?)");
		aboutMenuItem.setOnAction(event->{
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.initOwner(window);
			alert.setHeaderText("版权声明");
			alert.initModality(Modality.WINDOW_MODAL);
			alert.setContentText("版权所有：https://www.hegongshan.com\n版本号：v1.0");
			alert.setTitle("关于");
			alert.setGraphic(null);
			alert.showAndWait();
		});
		helpMenu.getItems().addAll(aboutMenuItem);
		menus.addAll(fileMenu,formatMenu,encodingMenu,helpMenu);
		return menuBar;
	}
	
	public TextArea createTextArea() {
		TextArea textArea = new TextArea();
		textArea.autosize();
		textArea.getStyleClass().add("font");
		HBox hBox = new HBox(new Label(String.valueOf(row)));
		hBox.setAlignment(Pos.CENTER);
		ObservableList<Node> sideBarList = sideBar.getChildren();
		sideBarList.add(hBox);
		textArea.setOnKeyReleased(event->{
			int newRows = getRows(textArea.getText());
			if(newRows>row) {
				for(int i = 1 ; i <= newRows-row;i++) {
					HBox hBox2 = new HBox();
					hBox2.setAlignment(Pos.CENTER);
					hBox2.getChildren().add(new Label(String.valueOf(row+i)));
					sideBarList.add(hBox2);
				}
			} else if(newRows < row) {
				for(int i = 1 ; i <=row-newRows;i++) {
					sideBarList.remove(row-i);
				}
			}
			row = newRows;
			
			int caretPos = textArea.getCaretPosition();
			int line = textArea.getText().substring(0,caretPos).lastIndexOf("\n");
			caretPos = (caretPos == 0 ? 1 : caretPos);
			line = (line == -1 ? 0 : line);
			caretColumn = caretPos - line;
			caretRow = line + 1;
			caretRowLabel.setText(String.valueOf(caretRow));
			caretColumnLabel.setText(String.valueOf(caretColumn));
		});
		return textArea;
	}
	
	public VBox createLeftSideBar() {
		sideBar = new VBox();
		sideBar.setAlignment(Pos.TOP_CENTER);
		sideBar.setPadding(new Insets(5));
		sideBar.setPrefWidth(55);
		sideBar.getStyleClass().addAll("sideBar","font");
		return sideBar;
	}
	
	public HBox createBottomBar() {
		HBox hBox = new HBox();
		hBox.setAlignment(Pos.BASELINE_RIGHT);
		hBox.getStyleClass().add("sideBar");
		caretRowLabel = new Label(String.valueOf(caretRow));
		caretColumnLabel = new Label(String.valueOf(caretColumn));
		VBox vBox1 = new VBox(new Label("光标所在行："));
		VBox vBox2 = new VBox(caretRowLabel);
		VBox vBox3 = new VBox(new Label("列："));
		VBox vBox4 = new VBox(caretColumnLabel);
		hBox.getChildren().addAll(vBox1,vBox2,vBox3,vBox4);
		return hBox;
	}
	
	public int getRows(String text) {
		Pattern pattern = Pattern.compile("\n");
		Matcher matcher = pattern.matcher(text);
		int row = 0;
		while(matcher.find()) {
			row++;
		}
		return row + 1;
	}
	
	public void encodeEventHandle(String charset) {
		String text = textArea.getText();
		if(StringUtils.isNotEmpty(text)) {
			try{
				byte[] bytes;
				if(StringUtils.isNotEmpty(lastEncoding)) {
					bytes = text.getBytes(lastEncoding);
				} else {
					bytes = text.getBytes();
				}
				text = new String(bytes,charset);
				lastEncoding = charset;
				textArea.setText(text);
			} catch(UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
