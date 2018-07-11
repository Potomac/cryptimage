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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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
	String burst;
	
	int digRate;
	
	/**
	 * 
	 */
	public SplitFrames(int digRate) {	
		this.digRate = digRate;
	}

	private void initBurst(int currentFrame){	
		switch (currentFrame) {
		case 1:			
			burst = "/ressources/burst_17.73_phase_1.bmp";				
			break;
		case 2:		
			burst = "/ressources/burst_17.73_phase_2.bmp";	
			break;
		case 3:			
			burst = "/ressources/burst_17.73_phase_3.bmp";	
			break;
		case 4:		
			burst = "/ressources/burst_17.73_phase_4.bmp";	
			break;
		default:
			System.out.println("error pal frame number");
			break;
		}

	}
	
	
	public BufferedImage splitFrames(boolean grey, BufferedImage img) {
		initBurst(JobConfig.getCurrentPalFrame());
			
		img = getScaledImage(img, 922, 576);
		
		this.img = img;
		BufferedImage buff = new  BufferedImage(1135, 625, BufferedImage.TYPE_3BYTE_BGR);
		
		img = convertToType(img, BufferedImage.TYPE_3BYTE_BGR);
		
		WritableRaster raster = buff.getRaster();
		
		BufferedImage burst_top = null;
		try {
			burst_top = ImageIO.read(getClass().getResource(burst));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
					
		Raster rasterBurst1 = burst_top.getRaster();
			
		Raster rasterfullFrame = img.getRaster();	
		
		raster.setPixels(99, 22, 40, 601,
				rasterBurst1.getPixels(0, 0, 40, 601, new int[40 * 601 * 3]));
		
				
		//buff = convertToType(buff, BufferedImage.TYPE_BYTE_GRAY);
		
		int j = 0;
		
		for (int i = 0; i < 576;  i++) {			
			//System.out.println(i);
			raster.setPixels(184, j + 22, 922, 1,
					rasterfullFrame.getPixels(0, i, 922, 1, new int[922 *3]));							
			i++;
			j++;			
		}
		
		j=0;
		for (int i = 1; i < 576;  i++) {			
			//System.out.println(i);
			raster.setPixels(184, j + 335, 922, 1,
					rasterfullFrame.getPixels(0, i , 922, 1, new int[922 *3]));							
			i++;
			j++;			
		}
		
	
		//scale buffInput regarding the sampling rate
		if(digRate == 13500000) {
			buff = getScaledImage(buff, 864, 625);
		}
		if(digRate == 14000000) {
			buff = getScaledImage(buff, 896, 625);
		}
		if(digRate == 14750000) {
			buff = getScaledImage(buff, 944, 625);
		}
		
		BufferedImage finalOutput = new BufferedImage(1136, 626, BufferedImage.TYPE_3BYTE_BGR);
		
		WritableRaster finalWRaster = finalOutput.getRaster();
		Raster finalRaster = buff.getRaster();
		
		finalWRaster.setPixels(0, 0, buff.getWidth(), buff.getHeight(),
				finalRaster.getPixels(0, 0,	buff.getWidth(), buff.getHeight(), 
						new int[buff.getHeight() * buff.getWidth() * 3]));
		
		grey = false;
		
		if(grey) {
			return convertToType(finalOutput, BufferedImage.TYPE_BYTE_GRAY);
		}
		else {
			return finalOutput;
		}
		
	}
	
	public BufferedImage unsplitFrames(BufferedImage buff) {		
		if(buff.getWidth() !=1136 || buff.getHeight() != 626 ) {			
			return buff;
		}
		
			
		//remove additionnal black pixels
		BufferedImage unBlack = new BufferedImage(1135, 625, BufferedImage.TYPE_3BYTE_BGR);
		
		WritableRaster wRaster = unBlack.getRaster();
		Raster rRaster = buff.getRaster();
		
		wRaster.setPixels(0, 0, buff.getWidth() - 1, buff.getHeight() - 1,
				rRaster.getPixels(0, 0,	buff.getWidth() -1 , buff.getHeight() - 1, 
						new int[(buff.getHeight() - 1) * (buff.getWidth() - 1) * 3]));
		
		
		//scale buffInput regarding the sampling rate
		BufferedImage inter = null;
		WritableRaster wRasterInter = null;
		
		if(digRate == 13500000) {
			inter = new BufferedImage(864, 625, BufferedImage.TYPE_3BYTE_BGR);
			wRasterInter = inter.getRaster();
			wRasterInter.setPixels(0, 0, 864, 625, wRaster.getPixels(0, 0, 864, 625, new int[864 * 625 * 3]));
		}
		if(digRate == 14000000) {
			inter = new BufferedImage(896, 625, BufferedImage.TYPE_3BYTE_BGR);
			wRasterInter = inter.getRaster();
			wRasterInter.setPixels(0, 0, 896, 625, wRaster.getPixels(0, 0, 896, 625, new int[896 * 625 * 3]));
		}
		if(digRate == 14750000) {
			inter = new BufferedImage(944, 625, BufferedImage.TYPE_3BYTE_BGR);
			wRasterInter = inter.getRaster();
			wRasterInter.setPixels(0, 0, 944, 625, wRaster.getPixels(0, 0, 944, 625, new int[944 * 625 * 3]));
		}
		if(digRate == 17734375) {
			inter = new BufferedImage(1135, 625, BufferedImage.TYPE_3BYTE_BGR);
			wRasterInter = inter.getRaster();
			wRasterInter.setPixels(0, 0, 1135, 625, wRaster.getPixels(0, 0, 1135, 625, new int[1135 * 625 * 3]));
		}
		
		
		//unBlack = convertToType(unBlack, BufferedImage.TYPE_3BYTE_BGR);

		Raster raster = inter.getRaster();
		
		
		int dWidth = 0;
		int start = 0;
		int yStart1 = 0;
		int yStart2 = 0;
		
		
		if(digRate == 13500000) {			
			dWidth = 702;
			start = 140;
			yStart1 = 22;
			yStart2 = 335;
		}
		if(digRate == 14000000) {	
			dWidth = 728;
			start = 145;
			yStart1 = 22;
			yStart2 = 335;
		}
		if(digRate == 14750000) {	
			dWidth = 767;
			start = 153;
			yStart1 = 22;
			yStart2 = 335;
		}
		if(digRate == 17734375) {
			dWidth = 922;
			start = 184;
			yStart1 = 22;
			yStart2 = 335;
		}
		
		BufferedImage fullFrame = new BufferedImage(dWidth, 576, BufferedImage.TYPE_3BYTE_BGR);
		WritableRaster rasterFullFrame = fullFrame.getRaster();
		
		
		int j = 0;
		
		for (int i = 0; i < 576;  i++) {			
			//System.out.println(i);
			rasterFullFrame.setPixels(0, i, dWidth, 1,
					raster.getPixels(start, j + yStart1, dWidth, 1, new int[dWidth * 3]));						
			i++;
			j++;
			
		}
		
		j=0;
		for (int i = 1; i < 576;  i++) {			
			//System.out.println(i);
			rasterFullFrame.setPixels(0, i, dWidth, 1,
					raster.getPixels(start, j + yStart2, dWidth, 1, new int[dWidth * 3]));						
			i++;
			j++;
			
		}
		
		return getScaledImage(fullFrame, 768,576);
		
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
	
	/**
	 * Scale an image to a new size
	 * @param src the image source
	 * @param w the new width
	 * @param h the new height
	 * @return the resized image
	 */
	private BufferedImage getScaledImage(BufferedImage src, int w, int h){
	    int finalw = w;
	    int finalh = h;
	    double factor = 1.00d;
	    double shiftw = 1d;	 
	    
//	    if(src.getWidth()==720 && src.getHeight()==576 ){
//	    	shiftw = (double)src.getWidth()/(double)w; // case of if width = 720 and height = 576
//	    }
//	    
//	    if(src.getWidth() > src.getHeight()){
//	        factor = ((double)src.getHeight()/(double)src.getWidth());
//	        finalh = (int)(finalw * factor * shiftw);                
//	    }else{
//	        factor = ((double)src.getWidth()/(double)src.getHeight());
//	        finalw = (int)(finalh * factor  );
//	    }   

	    BufferedImage resizedImg = new BufferedImage(finalw, finalh, BufferedImage.TRANSLUCENT);
	    Graphics2D g2 = resizedImg.createGraphics();
	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(src, 0, 0, finalw, finalh, null);
	    g2.dispose();
	    
	    //step 2 create a bufferedimage with exact size
	    
	    BufferedImage target = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
	    
	    Graphics g3 = target.getGraphics();	    
	    g3.drawImage(resizedImg, 0, (target.getHeight() - resizedImg.getHeight())/2, null);
		    
	    return target;
	}	
	
}
