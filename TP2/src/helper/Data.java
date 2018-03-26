package helper;

import java.awt.Image;
import java.io.File;
import java.sql.Blob;

public class Data {
	private String textEN;
	private String textFR;
	//premier est image initial
	private File[] images;
	
	//Constructeurs
	public Data() {}
	public Data(String textEN, File imageInit) {
		this.textEN = textEN;
		images[0] = imageInit;
	}
	
	//add value to data
	public void addTraduction(String txt) {
		textFR += txt;
	}
	
	public void addImage(File image) {
		images[images.length] = image;
	}
	
	//set value to data
	public void setTextEN(String textEN) {
		this.textEN = textEN;
	}

	public void setInitialImage(File image) {
		images[0] = image;
	}
	
	public void setTraduction(String textFR) {
		this.textFR = textFR;
	}
	
	public void setImages(File[] images) {
		this.images = images;
	}
	
	//get images
	public File[] getImages() {
		return images;
	}
	
	//get french text
	public String getTextFR() {
		return textFR;
	}
	
	//get english text
	public String getTextEN() {
		return textEN;
	}
}
