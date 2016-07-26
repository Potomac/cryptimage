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
 * 18 juil. 2016 Author Mannix54
 */



package com.ib.cryptimage.core;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;


/**
 * @author Mannix54
 *
 */
public class PalEngine {
	BufferedImage img;	
	YuvCalc yuvCalc;
	WritableRaster raster;
	int[] linePixels = new int[768*3];
	int[] linePixels2 = new int[768*3];
	int[] pixelTab = new int[3];	
	
	public PalEngine(){
		yuvCalc = new YuvCalc();	
	}
	
	/**
	 * 
	 */
	public PalEngine(BufferedImage img) {		
		setImg(img);				
		yuvCalc = new YuvCalc();			
	}
		
	public void setImg(BufferedImage img_){	
		
//		//ajout
//		BufferedImage raw = new BufferedImage(img_.getWidth(),
//				img_.getHeight(), BufferedImage.TYPE_INT_BGR);
//		raw.getGraphics().drawImage(img_, 0, 0, null);
//		img_ = raw;//fin ajout
		
		
		this.img = img_;
		raster = img.getRaster();
	}	
	
		
	public BufferedImage decode(){	    
		if(JobConfig.isWantDec()){
			tagLines();
		}
		if(JobConfig.isWantDec()){
			palInversePhase(-45); //-45
		}
		else {
			palInversePhase(45); //45			
		}		
	
		if (JobConfig.isAveragingPal() == true) {
			convertToYUV();
		    palAverageEvenFrame();
			palAverageOddFrame();
			//palAverage();
			convertToRGB();
		}
				
		removeTags();
		
//		//ajout		
//		BufferedImage raw = new BufferedImage(img.getWidth(),
//					img.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
//				raw.getGraphics().drawImage(img, 0, 0, null);
//				img = raw;//fin ajout
		
		return img;
	}
	
	public BufferedImage encode(){		
		if(JobConfig.isWantDec()){
			rephase45();
		}
		if(!JobConfig.isWantDec()){			
			tagLines();
		}
		
		return img;
	}
		
	
	private void rephase45(){		
		// odd field
		int pol = 1;
		
		for (int y = 0; y < 576; y++) {
			for (int x = 0; x < 768; x++) {
				if (pol == 1) { // 45 degrees
					raster.setPixel(x, y, yuvCalc.getRotateRGB(raster.getPixel(x, y, pixelTab), -45));//-45
				} else {// -45 degrees
					raster.setPixel(x, y, yuvCalc.getRotateRGB(raster.getPixel(x, y, pixelTab), 45));//45
				}
			}
			pol = pol * -1;
			y++;
		}

		pol=1;
		// odd field
		for (int y = 1; y < 576; y++) {
			for (int x = 0; x < 768; x++) {
				if (pol == 1) { // 45 degrees
					raster.setPixel(x, y, yuvCalc.getRotateRGB(raster.getPixel(x, y, pixelTab), -45));//-45
				} else {// -45 degrees
					raster.setPixel(x, y, yuvCalc.getRotateRGB(raster.getPixel(x, y, pixelTab), 45));//45
				}
			}
			pol = pol * -1;
			y++;
		}
	}
	
	private void removeTags(){
		for (int y = 0; y < 576; y++) {		
				raster.setPixel(0, y, raster.getPixel(1, y, pixelTab));			
		}
	}
	
	private void tagLines(){
		// odd field
		int pol = 1;
		for (int i = 0; i < 576; i++) {
			if (pol == 1) { // 45 degrees
				raster.setPixel(0, i, new int[] { 0, 0, 0 });
			} else {// 315 degrees
				raster.setPixel(0, i, new int[] { 64, 1, 0  });
			}
			i++;
			pol = pol * -1;
		}

		// even field
		pol = 1;
		for (int i = 1; i < 576; i++) {
			if (pol == 1) { // 135 degrees
				raster.setPixel(0, i, new int[] { 128, 0, 0 });
			} else {// 225 degrees
				raster.setPixel(0, i, new int[] { 255, 1, 0 });
			}
			i++;
			pol = pol * -1;
		}
	}

	private void convertToYUV(){
		for (int y = 0; y < 576; y++) {
			for (int x = 0; x < 768 ; x++) {				
				raster.setPixel(x, y,
						yuvCalc.convertRGBtoYUV(raster.getPixel(x, y,pixelTab)));
			}
		}		
	}
	
	private int getAngle(int[] pixel, int line){		
		if(pixel[0] == 0 ){//45			
			if(JobConfig.isWantDec() ){ //if(op ==0 ){
			return -315;//-315;
			}
			else {				
				return 315; //315
			}
		}
		
		if(pixel[0] == 64){//315			
			if(JobConfig.isWantDec() ){ //if(op ==0 ){
			return -45;//-45;
			}
			else {				
				return 45;//45
			}
		}
		
		if(pixel[0] == 128){//135			
			if(JobConfig.isWantDec() ){ //if(op != 0 ){
			return -225;//-225;
			}
			else {			
				return 225;//225;
			}
		}
		
		if(pixel[0] == 255){//225						
			if(JobConfig.isWantDec() ){ //if(op != 0 ){
			return -135 ;//-135;
			}
			else {			
				return 135;//135
			}
		}
		
		return 0;
	}	
	
	

	
	private void palInversePhase(int pol){		
		int[] pixel = new int[3];
		int cpt = 0;	
		int angle = 0;
		int phase = pol;
		
		for (int y = 0; y < 576; y++) {
			// check angle
			pixel = raster.getPixel(0, y, pixelTab);			
			angle = getAngle(pixel, y);			
			if(cpt == 2){
				cpt = 0;
				phase = phase * -1;	
			}
			cpt++;			
						
			for (int x = 0; x < 768; x++) {				
					pixel = raster.getPixel(x, y, pixelTab);				
				
				//Additional step : -45 degrees for odd lines, 45 degrees for even lines
				if(JobConfig.isWantDec()){
					pixel = yuvCalc.getRotateRGB(pixel, angle );
				}
				else{					
					pixel = yuvCalc.getRotateRGB(pixel, angle + phase);
				}
				raster.setPixel(x, y, pixel);				
			}
		}		
	}
	
//	private void phase45(){		
//		// odd field
//		int pol = 1;
//		
//		for (int y = 0; y < 576; y++) {
//			for (int x = 0; x < 768; x++) {
//				if (pol == 1) { // 45 degrees
//					raster.setPixel(x, y, yuvCalc.getRotateRGB(raster.getPixel(x, y, pixelTab), -45));//-45
//				} else {// -45 degrees
//					raster.setPixel(x, y, yuvCalc.getRotateRGB(raster.getPixel(x, y, pixelTab), 45));//45
//				}
//			}
//			pol = pol * -1;
//			y++;
//		}
//
//		pol=1;
//		// odd field
//		for (int y = 1; y < 576; y++) {
//			for (int x = 0; x < 768; x++) {
//				if (pol == 1) { // 45 degrees
//					raster.setPixel(x, y, yuvCalc.getRotateRGB(raster.getPixel(x, y, pixelTab), -45));//-45
//				} else {// -45 degrees
//					raster.setPixel(x, y, yuvCalc.getRotateRGB(raster.getPixel(x, y, pixelTab), 45));//45
//				}
//			}
//			pol = pol * -1;
//			y++;
//		}
//	}
	
	
	private void palAverageOddFrame(){		
		for (int y = 0; y < 576; y++) { // 2			
			raster.setPixels(0, y, 768, 1, averageLine(raster.getPixels(
					0, y, 768, 1, linePixels),
					raster.getPixels(0, y + 2, 768, 1, linePixels2),y));
			y += 3;
		}

	}
	
	private void palAverageEvenFrame(){		
		for (int y = 1; y < 574; y++) { //3
			raster.setPixels(0, y, 768, 1,						 
					averageLine(raster.getPixels(0, y, 768,1,linePixels),
						raster.getPixels(0, y + 2, 768, 1, linePixels2),y));	
			y+=3;//3
		}		
	}
	
//	private void palAverage(){
//		byte[] tabLines =  new byte[768*576*3];
//		
//		//byte[] pixels = ((DataBufferByte) raster.getDataBuffer()).getData();
//		
////		
////		
////		int val = 768*3;
////		byte[] value = new int[3];
////		
////	    for (int y = 0; y < 576; y++) {
////			for (int size = 0; size < 768; size++) {
////				value[2] = pixels[3*size + val*y] ;
////				value[1] = pixels[3*size +1 + val*y]; 
////				value[0] = pixels[3*size + 2 + val*y];
////				value = yuvCalc.convertRGBtoYUV(value);
////				tabLines[3*size + val*y] = value[0];
////				tabLines[3*size +1 + val*y] = value[1];
////				tabLines[3*size + 2 + val*y] = value[2];
////				//System.out.println(value[1]);
////			}
////		}		
////		
//		
////		//odd field
////		for (int y = 0; y < 576; y++) {
////			for (int size = 0; size < 768; size++) {
////				tabLines[3*size +1 + val*y] = ( tabLines[3*size +1 +val*y] + tabLines[3*size +1 +val* (y +2)] )/2;
////				tabLines[3*size +2 + val*y] = ( tabLines[3*size +2 +val*y] + tabLines[3*size +2 +val*(y +2)] ) /2;
////				tabLines[3*size +1 + val*(y +2)] = tabLines[3*size +1 +val*y];
////				tabLines[3*size +2 + val*(y +2)] = tabLines[3*size +2 +val*y];
////			}
////			y+=3;//3
////		}
////		
////		//even field
////				for (int y = 1; y < 576; y++) {
////					for (int size = 0; size < 768; size++) {
////						tabLines[3*size +1 + val*y] = ( tabLines[3*size +1 +val*y] + tabLines[3*size +1 +val* (y +2)] )/2;
////						tabLines[3*size +2 + val*y] = ( tabLines[3*size +2 +val*y] + tabLines[3*size +2 +val*(y +2)] ) /2;
////						tabLines[3*size +1 + val*(y +2)] = tabLines[3*size +1 +val*y];
////						tabLines[3*size +2 + val*(y +2)] = tabLines[3*size +2 +val*y];
////					}
////					y+=3;//3
////				}
//				
////		for (int y = 0; y < 576; y++) {
////			for (int size = 0; size < 768; size++) {
////				value[0] = tabLines[3 * size + val * y] ;
////				value[1] = tabLines[3 * size + 1 + val * y] ;
////				value[2] = tabLines[3 * size + 2 + val * y] ;
////				value = yuvCalc.convertYUVtoRGB(value);
////				pixels[3 * size + val * y] = value[2];
////				pixels[3 * size + 1 + val * y] =   (value[1]);
////				pixels[3 * size + 2 + val * y] = ( value[2]);
////			}
////		}	
//		
//		//raster.setPixels(0, 0, 768, 576, tabLines);
//		
//	}
	
	
	private int[] averageLine(int[] line1, int[] line2, int y){
		for (int i = 0; i < 768; i++) {
			line1[3*i +1] = ( line1[3*i +1] + line2[3*i +1] ) /2;
			line1[3*i +2] = ( line1[3*i +2] + line2[3*i +2] ) /2;
			line2[3*i +1] = line1[3*i +1];
			line2[3*i +2] = line1[3*i +2];
		}
		raster.setPixels(0, y+2, 768,1,line2);
		return line1;
	}
	

	
	private void convertToRGB(){		
		for (int y = 0; y < 576; y++) {
			for (int x = 0; x < 768; x++) {
				raster.setPixel(x, y, yuvCalc.convertYUVtoRGB(raster.getPixel(x, y,
						pixelTab)));
			}			
		}		
	}	
	
	

}
