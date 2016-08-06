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
 * 26 juil. 2016 Author Mannix54
 */



package com.ib.cryptimage.core;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * @author Mannix54
 *
 */
public class SecamEngine {
	
	BufferedImage img;	
	YuvCalc yuvCalc;
	WritableRaster raster;
	int[] linePixels = new int[768*3];
	int[] linePixels2 = new int[768*3];
	int[] pixelTab = new int[3];
	boolean u = false;
	boolean v = false;

	/**
	 * 
	 */
	public SecamEngine() {
		yuvCalc = new YuvCalc();
	}
	
	public SecamEngine(BufferedImage img) {		
		setImg(img);		
		
		yuvCalc = new YuvCalc();			
	}

	public void setImg(BufferedImage img_){		
		this.img = img_;
		raster = img.getRaster();				

	}
	
	public BufferedImage encode() {
		tagLines();
		return img; // newImage;
	}
	
	public BufferedImage decode() {
		//convertToYUV();
		secamAverageEvenFrame();
		secamAverageOddFrame();
		//convertToRGB();

		removeTags();

		return img;
	}
	
	private void tagLines(){
		// odd field
		int pol = 1;
		for (int i = 0; i < 576; i++) {
			if (pol == 1) { // u
				raster.setPixel(0, i, new int[] { 0, 0, 0 });
			} else {// v
				raster.setPixel(0, i, new int[] { 255, 255, 255 });
			}
			i++;
			pol = pol * -1;
		}

		// even field
		pol = 1;
		for (int i = 1; i < 576; i++) {
			if (pol == 1) { // u
				raster.setPixel(0, i, new int[] { 0, 0, 0 });
			} else {// v
				raster.setPixel(0, i, new int[] { 255, 255, 255 });
			}
			i++;
			pol = pol * -1;
		}
	}
	
	
	
	private String getTypeChroma(int[] pixel){		
		String res = "";
		
		if(pixel[0] == 0 ){
			u = true;
			res = "u";
		}
		
		if(pixel[0] == 255 ){
			v = true;
			res = res + "v";
		}
		
		return res;
	}	
	
	
	
	
//	private void convertToYUV(){
//		for (int y = 0; y < 576; y++) {
//			for (int x = 0; x < 768 ; x++) {				
//				raster.setPixel(x, y,
//						yuvCalc.convertRGBtoYUV(raster.getPixel(x, y,pixelTab)));
//			}
//		}		
//	}
//	
//	private void convertToRGB(){		
//		for (int y = 0; y < 576; y++) {
//			for (int x = 0; x < 768; x++) {
//				raster.setPixel(x, y, yuvCalc.convertYUVtoRGB(raster.getPixel(x, y,
//						pixelTab)));
//			}			
//		}		
//	}	
	
	private void removeTags(){
		for (int y = 0; y < 576; y++) {		
				raster.setPixel(0, y, raster.getPixel(1, y, pixelTab));			
		}
	}
	
	private void secamAverageOddFrame(){		
		int[] pixelL1 = new int[3];
		int[] pixelL2 = new int[3];
		String seq = "";
		
		for (int y = 0; y < 576; y++) { // 2
			pixelL1 = raster.getPixel(0, y, pixelL1);
			pixelL2 = raster.getPixel(0, y + 2, pixelL2);
			seq = getTypeChroma(pixelL1) + getTypeChroma(pixelL2);
			//System.out.println("seq " + seq  + " ligne  " + y + " et ligne " + ( y +2 ) );
			
			raster.setPixels(0, y, 768, 1, averageLine(raster.getPixels(
					0, y, 768, 1, linePixels),
					raster.getPixels(0, y + 2, 768, 1, linePixels2),y, seq));
			y += 3;
		}

	}
	
	private void secamAverageEvenFrame(){		
		int[] pixelL1 = new int[3];
		int[] pixelL2 = new int[3];
		String seq = "";
		
		for (int y = 1; y < 574; y++) { // 2
			pixelL1 = raster.getPixel(0, y, pixelL1);
			pixelL2 = raster.getPixel(0, y + 2, pixelL2);
			seq = getTypeChroma(pixelL1) + getTypeChroma(pixelL2);
			//System.out.println("seq " + seq  + " ligne  " + y + " et ligne " + ( y +2 ) );
			
			raster.setPixels(0, y, 768, 1, averageLine(raster.getPixels(
					0, y, 768, 1, linePixels),
					raster.getPixels(0, y + 2, 768, 1, linePixels2),y, seq));
			y += 3;
		}
	}
	
	public BufferedImage averageSpecial(){		
		secamAverageEvenFrameVideoCrypt();
		secamAverageOddFrameVideoCrypt();
		return img;
	}
	
	private void secamAverageOddFrameVideoCrypt(){		
		int[] pixelL1 = new int[3];
		int[] pixelL2 = new int[3];		
		
		for (int y = 0; y < 576; y++) { // 2
			pixelL1 = raster.getPixel(0, y, pixelL1);
			pixelL2 = raster.getPixel(0, y + 2, pixelL2);						
			
			raster.setPixels(0, y, 768, 1, averageLine(raster.getPixels(
					0, y, 768, 1, linePixels),
					raster.getPixels(0, y + 2, 768, 1, linePixels2),y));
			y += 3;
		}

	}
	
	private void secamAverageEvenFrameVideoCrypt(){		
		int[] pixelL1 = new int[3];
		int[] pixelL2 = new int[3];	
		
		for (int y = 1; y < 574; y++) { // 2
			pixelL1 = raster.getPixel(0, y, pixelL1);
			pixelL2 = raster.getPixel(0, y + 2, pixelL2);
			
			
			raster.setPixels(0, y, 768, 1, averageLine(raster.getPixels(
					0, y, 768, 1, linePixels),
					raster.getPixels(0, y + 2, 768, 1, linePixels2),y));
			y += 3;
		}
	}
	
	private int[] averageLine(int[] line1, int[] line2, int y) {
		int[] tempPix = new int[3];
		
		
		for (int i = 1; i < 768; i++) {
			tempPix[0] = line1[3 * i ];
			tempPix[1] = line1[3 * i + 1];
			tempPix[2] = line1[3 * i + 2];
			
			tempPix = yuvCalc.convertRGBtoYUV(tempPix);
			
			line1[3 * i ] = tempPix[0];
			line1[3 * i + 1] = tempPix[1];
			line1[3 * i + 2] = tempPix[2];
						
			tempPix[0] = line2[3 * i ];
			tempPix[1] = line2[3 * i + 1];
			tempPix[2] = line2[3 * i + 2];
			
			tempPix = yuvCalc.convertRGBtoYUV(tempPix);
			
			line2[3 * i ] = tempPix[0];
			line2[3 * i + 1] = tempPix[1];
			line2[3 * i + 2] = tempPix[2];
			
			
			line1[3 * i + 1] = line2[3 * i + 1];
			line2[3 * i + 2] = line1[3 * i + 2];
			
			tempPix[0] = line1[3 * i ];
			tempPix[1] = line1[3 * i + 1];
			tempPix[2] = line1[3 * i + 2];
			
			tempPix = yuvCalc.convertYUVtoRGB(tempPix);
			
			line1[3 * i ] = tempPix[0];
			line1[3 * i + 1] = tempPix[1];
			line1[3 * i + 2] = tempPix[2];
			
			tempPix[0] = line2[3 * i ];
			tempPix[1] = line2[3 * i + 1];
			tempPix[2] = line2[3 * i + 2];
			
			tempPix = yuvCalc.convertYUVtoRGB(tempPix);
			
			line2[3 * i ] = tempPix[0];
			line2[3 * i + 1] = tempPix[1];
			line2[3 * i + 2] = tempPix[2];
			
		}
		raster.setPixels(0, y + 2, 768, 1, line2);
		return line1;
	}
	
	private int[] averageLine(int[] line1, int[] line2, int y, String seq){
		
		if(JobConfig.getGui().getComboTableSysterEnc().getSelectedIndex()==1){
			int[] tempPix = new int[3];
			if (seq.equals("uv")) {
//				 for (int i = 0; i < 768; i++) {			 
//				  line1[3*i +2] = line2[3*i +2];
//				  line2[3*i +1] = line1[3*i +1];			 
//				 }
//				 raster.setPixels(0, y+2, 768,1,line2);
				return line1;
			} else {
				int valU = 0;
				int valV = 0;
				if(seq.equals("uu")){
					valU = 84;
					valV = 255;
				}
				if (seq.equals("vv")) {
					valU = 255;
					valV = 107;
				}
				if (seq.equals("vu")) {
					valU = 206;
					valV = 253;
				}
				
				for (int i = 0; i < 768; i++) {
					tempPix[0] = line1[3 * i ];
					tempPix[1] = line1[3 * i + 1];
					tempPix[2] = line1[3 * i + 2];
					
					//yuvCalc.setRGB(tempPix[0], tempPix[1], tempPix[2]);
					tempPix = yuvCalc.convertRGBtoYUV(tempPix);
					tempPix[1] = valU;
					tempPix[2] = valV;
					
					tempPix = yuvCalc.convertYUVtoRGB(tempPix);
				
					//tempPix = yuvCalc.getRotateRGB(tempPix, 225);
					line1[3 * i ] = tempPix[0];
					line1[3 * i + 1] = tempPix[1];
					line1[3 * i + 2] = tempPix[2];
					
					tempPix[0] = line2[3 * i ];
					tempPix[1] = line2[3 * i + 1];
					tempPix[2] = line2[3 * i + 2];
					
					tempPix = yuvCalc.convertRGBtoYUV(tempPix);
					tempPix[1] = valU;
					tempPix[2] = valV;
					tempPix = yuvCalc.convertYUVtoRGB(tempPix);
					
					//tempPix = yuvCalc.getRotateRGB(tempPix, 225);
					line2[3 * i ] = tempPix[0];
					line2[3 * i + 1] = tempPix[1];
					line2[3 * i + 2] = tempPix[2];
				}
				raster.setPixels(0, y + 2, 768, 1, line2);
				return line1;
			}
		}
		
		int[] tempPix = new int[3];
		if (seq.equals("uv")) {
//			 for (int i = 0; i < 768; i++) {			 
//			  line1[3*i +2] = line2[3*i +2];
//			  line2[3*i +1] = line1[3*i +1];			 
//			 }
//			 raster.setPixels(0, y+2, 768,1,line2);
			return line1;
		} else {			
			for (int i = 0; i < 768; i++) {
				tempPix[0] = line1[3 * i ];
				tempPix[1] = line1[3 * i + 1];
				tempPix[2] = line1[3 * i + 2];
			
				tempPix = setToBlackAndWhite(tempPix);
				line1[3 * i ] = tempPix[0];
				line1[3 * i + 1] = tempPix[1];
				line1[3 * i + 2] = tempPix[2];
				
				tempPix[0] = line2[3 * i ];
				tempPix[1] = line2[3 * i + 1];
				tempPix[2] = line2[3 * i + 2];
			
				tempPix = setToBlackAndWhite(tempPix);
				line2[3 * i ] = tempPix[0];
				line2[3 * i + 1] = tempPix[1];
				line2[3 * i + 2] = tempPix[2];
			}
			raster.setPixels(0, y + 2, 768, 1, line2);
			return line1;
		}
	}
	
	private int[] setToBlackAndWhite(int[] line){
		int[] pixelBW = new int[3];
		pixelBW = yuvCalc.convertRGBtoYUV(line);
		pixelBW[1] = 128;
		pixelBW[2] = 128;
		
		return yuvCalc.convertYUVtoRGB(pixelBW);
	}
	
	
}
