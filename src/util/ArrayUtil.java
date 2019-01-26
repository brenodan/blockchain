package util;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ArrayUtil {

	public static ArrayList<BufferedImage> removeItemFromArray(ArrayList<BufferedImage> album, BufferedImage value){
		
		ArrayList<BufferedImage> returnAlbum = new ArrayList<BufferedImage>();
		
		for(BufferedImage image : album){
			if(image == value){
				returnAlbum.add(image);
			}
		}
		
		return returnAlbum;
		
	}
	
	public static ArrayList<BufferedImage> addToArray(ArrayList<BufferedImage> images1, ArrayList<BufferedImage> images2) {
		
		images1.addAll(images2);
		return images1;
		
	}
	
	public static ArrayList<BufferedImage> populateArray(String stringLocation) {
		
		return new ArrayList<BufferedImage>();
	}
	
}
