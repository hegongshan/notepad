package com.hegongshan.notepad.event;

import java.io.UnsupportedEncodingException;

import com.hegongshan.notepad.util.StringUtils;

import javafx.scene.control.TextArea;

public class TextAreaEncodeEventHandler{
	
	public void action(TextArea textArea) {
		String text = textArea.getText();
		if(StringUtils.isNotEmpty(text)) {
			try{
				text = new String(text.getBytes("utf-8"));
				textArea.setText(text);
			} catch(UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}
	
}
