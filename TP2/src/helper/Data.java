package helper;

import java.awt.Image;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.sql.Blob;

public class Data {
	private String textEN;
	private String textFR;
	//premier est image initial
	private List<File> images;
	
	//Constructeurs
	public Data() {
		images = new ArrayList<File>();
	}
	public Data(String textEN, File imageInit) {
		images = new ArrayList<File>();
		this.textEN = textEN;
		images.add(0, imageInit);
	}
	
	//add value to data
	public void addTraduction(String txt) {
		textFR += txt;
	}
	
	public void addImage(File image) {
		images.add(image);
	}
	
	//set value to data
	public void setTextEN(String textEN) {
		this.textEN = textEN;
	}

	public void setInitialImage(File image) {
		if(images.size() > 0) {
			images.set(0, image);
		}else {
			images.add(image);
		}
	}
	
	public void setTraduction(String textFR) {
		this.textFR = textFR;
	}
	
	public void setImages(File[] images) {
		this.images = Arrays.asList(images);
	}
	
	//get images
	public List<File> getImages() {
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
