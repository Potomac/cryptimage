/**
 * This file is part of	CryptImage.
 *
 * CryptImage is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * CryptImage is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with CryptImage.  If not, see <http://www.gnu.org/licenses/>
 * 
 * 8 juin 2018 Author Mannix54
 */



package com.ib.cryptimage.core;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * @author Mannix54
 *
 */
public class Shift {
	
	WritableRaster raster;
	WritableRaster raster2;
	BufferedImage newImage;	
	
	

	/**
	 * 
	 */
	public Shift() {
		// TODO Auto-generated constructor stub
	}
	
	
	BufferedImage transform(BufferedImage image, int shiftX, int shiftY) {		
		raster = image.getRaster();
		newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		raster2 = newImage.getRaster();		
		
		
		int val_i_start = 0;
		int val_i_end = image.getHeight();
		int val_height = 0; 
		int val_x_start = 0;
		int val_x_ori_start = 0;
		int val_x_width = image.getWidth();
		
		if(shiftY > 0) {
			val_i_start = shiftY;
			val_i_end = image.getHeight();
			val_height = shiftY * -1;			
		}
		
		if(shiftY < 0) {
			val_i_start = 0;
			val_i_end = image.getHeight() + shiftY;
			val_height = Math.abs(shiftY);		
		}
		
		if(shiftX > 0) {
			val_x_start = shiftX;
			val_x_width = image.getWidth() - shiftX;
			val_x_ori_start = 0;
		}
		
		if(shiftX < 0) {
			val_x_start = 0;
			val_x_width = image.getWidth() + shiftX;
			val_x_ori_start = Math.abs(shiftX);
		}
		
		
//		for (int i = val_i_start; i < val_i_end; i++) {			
//			raster2.setPixels(val_x_start, i, val_x_width, 1, 
//					raster.getPixels(val_x_ori_start, i + val_height , val_x_width, 1,
//							new int[val_x_width * 3]));			
//		}
		
		raster2.setPixels(val_x_start, val_i_start, val_x_width, val_i_end  - val_i_start, 
				raster.getPixels(val_x_ori_start, val_i_start + val_height , val_x_width,  val_i_end  - val_i_start,
						new int[val_x_width * ( val_i_end  - val_i_start) * 3]));	
		
		
//		
//		if(shiftY > 0 && shiftX == 0) {
//			for (int i = shiftY; i < 576; i++) {			
//				raster2.setPixels(0, i, image.getWidth(), 1, 
//						raster.getPixels(0, i - shiftY, image.getWidth(), 1,
//								new int[image.getWidth() * 3]));			
//			}
//		}
//		else if(shiftY < 0 && shiftX == 0) {
//			for (int i = 0; i < 576 + shiftY; i++) {			
//				raster2.setPixels(0, i, image.getWidth(), 1, 
//						raster.getPixels(0, i + Math.abs(shiftY), image.getWidth(), 1,
//								new int[image.getWidth() * 3]));			
//			}
//		}
//		else if(shiftY > 0 && shiftX > 0) {
//			for (int i = shiftY; i < 576; i++) {			
//				raster2.setPixels(0 + shiftX, i, image.getWidth() - shiftX, 1, 
//						raster.getPixels(0, i - shiftY, image.getWidth() - shiftX, 1,
//								new int[(image.getWidth() - shiftX) * 3]));			
//			}
//		}
//		else if(shiftY < 0 && shiftX > 0) {
//			for (int i = 0; i < 576 + shiftY; i++) {			
//				raster2.setPixels(0 + shiftX, i, image.getWidth() - shiftX, 1, 
//						raster.getPixels(0, i + Math.abs(shiftY), image.getWidth() - shiftX, 1,
//								new int[(image.getWidth()- shiftX) * 3]));			
//			}
//		}
		
		
		return newImage;
		
		
	}

}
