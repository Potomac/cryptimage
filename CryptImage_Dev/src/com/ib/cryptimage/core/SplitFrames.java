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
 * 6 juil. 2018 Author Mannix54
 */



package com.ib.cryptimage.core;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * @author Mannix54
 *
 */
public class SplitFrames {

	BufferedImage img;
	String burst1;
	String burst2;
	int digRate;
	
	/**
	 * 
	 */
	public SplitFrames(int digRate) {	
		this.digRate = digRate;
	}

	private void initBurst(int currentFrame){	
		if(digRate == 17750000) {
			switch (currentFrame) {
			case 1:			
				burst1 = "/ressources/burst_top_phase_4.bmp";
				burst2 = "/ressources/burst_bot_phase_4.bmp";
				break;
			case 2:		
				burst1 = "/ressources/burst_top_phase_1.bmp";
				burst2 = "/ressources/burst_bot_phase_1.bmp";
				break;
			case 3:	
				burst1 = "/ressources/burst_top_phase_2.bmp";
				burst2 = "/ressources/burst_bot_phase_2.bmp";
				break;
			case 4:	
				burst1 = "/ressources/burst_top_phase_3.bmp";
				burst2 = "/ressources/burst_bot_phase_3.bmp";
				break;
			default:			
				burst1 = "/ressources/burst_top_phase_4.bmp";
				burst2 = "/ressources/burst_bot_phase_4.bmp";
				break;
			}
		}
		else if (digRate == 14750000) {
			switch (currentFrame) {
			case 1:				
				burst1 = "/ressources/burst_14.75_phase_1_top.bmp";
				burst2 = "/ressources/burst_14.75_phase_1_bot.bmp";
				break;
			case 2:				
				burst1 = "/ressources/burst_14.75_phase_2_top.bmp";
				burst2 = "/ressources/burst_14.75_phase_2_bot.bmp";
				break;
			case 3:			
				burst1 = "/ressources/burst_14.75_phase_3_top.bmp";
				burst2 = "/ressources/burst_14.75_phase_3_bot.bmp";
				break;
			case 4:			
				burst1 = "/ressources/burst_14.75_phase_4_top.bmp";
				burst2 = "/ressources/burst_14.75_phase_4_bot.bmp";
				break;
			default:
				System.out.println("error pal frame number");
				burst1 = "/ressources/burst_14.75_phase_1_top.bmp";
				burst2 = "/ressources/burst_14.75_phase_1_bot.bmp";
				break;
			}
		}

	}
	
	
	public BufferedImage splitFrames(boolean grey, BufferedImage img) {
		initBurst(JobConfig.getCurrentPalFrame());
		
		this.img = img;
		BufferedImage buff = new  BufferedImage(944, 626, BufferedImage.TYPE_3BYTE_BGR);
		
		img = convertToType(img, BufferedImage.TYPE_3BYTE_BGR);
		
		WritableRaster raster = buff.getRaster();
		
		BufferedImage burst_top = null;
		try {
			burst_top = ImageIO.read(getClass().getResource(burst1));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BufferedImage burst_bot = null;
		try {
			burst_bot = ImageIO.read(getClass().getResource(burst2));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		Raster rasterBurst1 = burst_top.getRaster();
		Raster rasterBurst2 = burst_bot.getRaster();
		
		Raster rasterfullFrame = img.getRaster();	
		
		raster.setPixels(76, 21, 36, 288,
				rasterBurst1.getPixels(0, 0, 36, 288, new int[36 * 288 * 3]));
		
		raster.setPixels(76, 332, 36, 288,
				rasterBurst2.getPixels(0, 0, 36, 288, new int[36 * 288 * 3]));
		
		//buff = convertToType(buff, BufferedImage.TYPE_BYTE_GRAY);
		
		int j = 0;
		
		for (int i = 0; i < 576;  i++) {			
			//System.out.println(i);
			raster.setPixels(156, j + 21, 768, 1,
					rasterfullFrame.getPixels(0, i, 768, 1, new int[768 *3]));						
			i++;
			j++;			
		}
		
		j=0;
		for (int i = 1; i < 576;  i++) {			
			//System.out.println(i);
			raster.setPixels(156, j + 332, 768, 1,
					rasterfullFrame.getPixels(0, i , 768, 1, new int[768 *3]));						
			i++;
			j++;			
		}
		
		if(grey) {
			return convertToType(buff, BufferedImage.TYPE_BYTE_GRAY);
		}
		else {
			return buff;
		}
		
	}
	
	public BufferedImage unsplitFrames(BufferedImage buff) {		
		if(buff.getWidth() !=944 || buff.getHeight() != 626 ) {			
			return buff;
		}
				
		buff = convertToType(buff, BufferedImage.TYPE_3BYTE_BGR);
		BufferedImage fullFrame = new BufferedImage(768, 576, BufferedImage.TYPE_3BYTE_BGR);
		WritableRaster rasterFullFrame = fullFrame.getRaster();
		Raster raster = buff.getRaster();
		
		int j = 0;
		
		for (int i = 0; i < 576;  i++) {			
			//System.out.println(i);
			rasterFullFrame.setPixels(0, i, 768, 1,
					raster.getPixels(156, j + 21, 768, 1, new int[768 * 3]));						
			i++;
			j++;
			
		}
		
		j=0;
		for (int i = 1; i < 576;  i++) {			
			//System.out.println(i);
			rasterFullFrame.setPixels(0, i, 768, 1,
					raster.getPixels(156, j + 332, 768, 1, new int[768 * 3]));						
			i++;
			j++;
			
		}
		
		return fullFrame;
		
	}
	
	/**
	 * Convert a source image to a desired BufferedImage type
	 * @param sourceImage the image source
	 * @param targetType the target type
	 * @return a converted BufferedImage
	 */
	protected  BufferedImage convertToType(BufferedImage sourceImage,
			int targetType) {
		BufferedImage image;

		// if the source image is already the target type, return the source
		// image
		if (sourceImage.getType() == targetType) {
			image = sourceImage;
		}
		// otherwise create a new image of the target type and draw the new
		// image
		else {
			image = new BufferedImage(sourceImage.getWidth(),
					sourceImage.getHeight(), targetType);
			image.getGraphics().drawImage(sourceImage, 0, 0, null);
		}
		return image;
	}
	
	
	
}
