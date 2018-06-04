package com.hegongshan.notepad.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * 字体选择器
 * @author hegongshan http://www.hegongshan.com
 * @since v1.0
 */
public class FontPicker extends Dialog<String> {
	
	private Map<String,List<String>> fontMap;
	private List<String> families;
	private DialogPane dialogPane;
	private StringProperty fontName;
	private DoubleProperty fontSize;
	private Font font;
	private static final double DEFAULT_SPACING = 5d; 
	
	public FontPicker() {
		fontName = new SimpleStringProperty();
		fontSize = new SimpleDoubleProperty();
		fontMap = new HashMap<>();
		families = Font.getFamilies();
		List<String> weightList;
		String family;
		for(int i = 0;i < families.size() ; i++) {
			family = families.get(i);
			List<String> fontFamilyNames = Font.getFontNames(family);
			weightList = new ArrayList<>(fontFamilyNames);
			weightList.remove(family);
			for(int j = 0 ; j < weightList.size() ; j++) {
				String f = weightList.get(j);
				if(f.contains("-")) {
					f = f.split("-")[1].trim();
				} else {
					f = f.substring(family.length()).trim();
				}
				if(f.equalsIgnoreCase("Bold")) {
					weightList.set(j, "粗体");
				} else if(f.equalsIgnoreCase("Bold Italic") || f.equalsIgnoreCase("BoldItalic")) {
					weightList.set(j, "粗斜体");
				} else if(f.equalsIgnoreCase("Italic")) {
					weightList.set(j, "斜体");
				} else if(f.equalsIgnoreCase("Regular")) {
					weightList.set(j, "常规");
				} else {
					weightList.set(j, f);
				}
			}
			fontMap.put(family, weightList);
		}
		dialogPane = getDialogPane();
		dialogPane.setContent(createContent());
		dialogPane.getButtonTypes().addAll(ButtonType.OK,ButtonType.CANCEL);
		setTitle("字体");
	}
	
	public Font getFont() {
		font = Font.font(fontName.getValue(),fontSize.getValue());
		return font;
	}
	
	private Node createContent() {
		VBox container = new VBox(DEFAULT_SPACING);
		
		HBox preview = new HBox(DEFAULT_SPACING);
		Label previewLabel = new Label("示例");
		Label previewTextLabel = new Label("AaBbCc记事本");
		previewTextLabel.setStyle("-fx-border-size:1px;-fx-border-color:gray;-fx-padding:5px;");
		VBox preview2 = new VBox(previewLabel,previewTextLabel);
		preview.getChildren().add(preview2);
		
		HBox main = new HBox(DEFAULT_SPACING);
		
		Label fontWeightLabel = new Label("字形");
		TextField fontWeightText = new TextField(fontMap.get(families.get(0)).get(0)); 
		ListView<String> fontWeightListView = new ListView<>(FXCollections.observableList(fontMap.get(families.get(0))));
		fontWeightListView.setPrefHeight(200);
		fontWeightListView.setPrefWidth(200);
		VBox fontWeightBox = new VBox(DEFAULT_SPACING);
		fontWeightBox.getChildren().addAll(fontWeightLabel,fontWeightText,fontWeightListView);

		Label fontLabel = new Label("字体");
		TextField fontText = new TextField(families.get(0));
		fontName.bind(fontText.textProperty());
		ListView<String> fontListView = new ListView<>(FXCollections.observableList(families));
		
		fontListView.setPrefHeight(200);
		fontListView.setPrefWidth(200);
		fontListView.setOnMouseClicked(event->{
			fontWeightText.setText(null);
			String font = fontListView.getSelectionModel().getSelectedItem();
			fontText.setText(font);
			fontWeightListView.setItems(FXCollections.observableList(fontMap.get(font)));
			previewTextLabel.setFont(Font.font(fontName.getValue(),fontSize.getValue()));
		});
		
		fontText.setOnKeyReleased(event->{
			List<String> newFamilies = new ArrayList<>(families.size());
			for(int i=0;i < families.size();i++) {
				String family = families.get(i);
				if(family.toLowerCase().startsWith(fontText.getText().toLowerCase())) {
					newFamilies.add(family);
				}
			}
			fontListView.setItems(FXCollections.observableList(newFamilies));
		});
		
		VBox fontBox = new VBox(DEFAULT_SPACING);
		fontBox.getChildren().addAll(fontLabel,fontText,fontListView);
		
		/*	String size = sizeText.getText();
			if(StringUtils.isNotEmpty(size)) {
				try{
					newValue = Double.parseDouble(size);
				} catch(Exception e) {
					newValue = 8;
				}
			} else {
				newValue = 0;
			}
		});*/
		
		Label sizeLabel = new Label("大小");
		TextField sizeText = new TextField(String.valueOf(getFontSizeArray()[0])); 
		fontSize.bind(new SimpleDoubleProperty(Double.parseDouble(sizeText.textProperty().get())));
		ListView<Integer> sizeListView = new ListView<>(FXCollections.observableArrayList(8,10,12,14,16));
		sizeListView.setPrefHeight(200);
		sizeListView.setPrefWidth(200);
		sizeListView.getSelectionModel().select(0);
		sizeListView.setOnMouseClicked(event->{
			SelectionModel<Integer> selectionModel = sizeListView.getSelectionModel();
			sizeText.setText(String.valueOf(selectionModel.getSelectedItem()));
			previewTextLabel.setFont(Font.font(fontName.getValue(),fontSize.getValue()));
		});
		VBox sizeBox = new VBox(DEFAULT_SPACING);
		sizeBox.getChildren().addAll(sizeLabel,sizeText,sizeListView);

		main.getChildren().addAll(fontBox,fontWeightBox,sizeBox);
		
		container.getChildren().addAll(main,preview);
		return container;
	}
	
	private double[] getFontSizeArray() {
		double[] fontSizeArray = {8,10,12,14,16};
		return fontSizeArray;
	}
	
}
