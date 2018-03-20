package helper;

import java.sql.Blob;

public class Data {
	private String textEN;
	private String textFR;
	//premier est image initial
	private Blob[] images;
	
	//Constructeurs
	public Data() {}
	public Data(String textEN, Blob imageInit) {
		this.textEN = textEN;
		images[0] = imageInit;
	}
	
	//add value to data
	public void addTraduction(String txt) {
		textFR += txt;
	}
	
	public void addImage(Blob image) {
		images[images.length] = image;
	}
	
	//set value to data
	public void setTextEN(String textEN) {
		this.textEN = textEN;
	}

	public void setInitialImage(Blob image) {
		images[0] = image;
	}
	
	public void setTraduction(String textFR) {
		this.textFR = textFR;
	}
	
	public void setImages(Blob[] images) {
		this.images = images;
	}
	
	//get images
	public Blob[] getImages() {
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
