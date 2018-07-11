/**
 * This file is part of	CryptImage_Dev.
 *
 * CryptImage_Dev is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * CryptImage_Dev is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with CryptImage_Dev.  If not, see <http://www.gnu.org/licenses/>
 * 
 * 6 juil. 2018 Author Mannix54
 */



package com.ib.cryptimage.core;

/**
 * @author Mannix54
 *
 */
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class FilterPixels {

	int filterWidth;
	int filterHeight;
	double[][] filter;
	double factor;
	double bias;
	WritableRaster outputRaster;
	Raster inputRaster;
	
	public FilterPixels(int filterWidth, int filterHeight, double[][] values, double factor, double bias) {
		this.filterWidth = filterWidth;
		this.filterHeight = filterHeight;
		
		filter = values;
		
		this.factor = factor;
		this.bias = bias;
	}

	
	public BufferedImage transform(BufferedImage img, boolean applyRed, boolean applyGreen, boolean applyBlue) {
		int width = img.getWidth();
		int height = img.getHeight();
		
		 int imageX, imageY;
		 int[] pixelTabs = new int[3];
		 int[] oriPixelsTabs = new int[3];
		 int res;
		 
		 
		inputRaster = img.getRaster();
		
		BufferedImage output = new BufferedImage(width, height, img.getType());
		outputRaster = output.getRaster();
		
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				double red = 0.0, green = 0.0, blue = 0.0;
				oriPixelsTabs = inputRaster.getPixel(x, y, new int[3]);
				//System.out.println(oriPixelsTabs[0] + " " + oriPixelsTabs[1] + " " + oriPixelsTabs[2]);
				
				 //multiply every value of the filter with corresponding image pixel
				 for(int filterY = 0; filterY < filterHeight; filterY++) {
					 for(int filterX = 0; filterX < filterWidth; filterX++) {
						 imageX = (x - filterWidth / 2 + filterX + width) % width;
						 imageY = (y - filterHeight / 2 + filterY + height) % height;
						 
						// System.out.println(imageX + " " + imageY);
						 
						 pixelTabs = inputRaster.getPixel(imageX, imageY, new int[3]);
						 
						 if(applyRed) {
							 //red
							 red += pixelTabs[0] * filter[filterY][filterX];
						 }
						 if(applyGreen) {
							 //green
							 green += pixelTabs[1] * filter[filterY][filterX];
						 }
						 if(applyBlue) {
							 //blue
							 blue += pixelTabs[2] * filter[filterY][filterX];
						 }						 
					 }					 
				 }
				 //truncate values smaller than zero and larger than 255
				 if(applyRed) {
					 res = (int) (factor * red + bias);
					 pixelTabs[0] = Math.min( Math.max(res, 0), 255);					
				 }
				 else {
					 pixelTabs[0] = oriPixelsTabs[0];
				 }
				 if(applyGreen) {
					 res = (int) (factor * green + bias);
					 pixelTabs[1] = Math.min( Math.max(res, 0), 255);
				 }
				 else {
					 pixelTabs[1] = oriPixelsTabs[1];
				 }
				 if(applyBlue) {
					 res = (int) (factor * blue + bias);
					 pixelTabs[2] = Math.min( Math.max(res, 0), 255);
				 }
				 else {
					 pixelTabs[2] = oriPixelsTabs[2];
				 }

				 outputRaster.setPixel(x, y, pixelTabs);				 
				
			}
			//System.exit(0);
		}
				
		return output;
		
	}
	
}