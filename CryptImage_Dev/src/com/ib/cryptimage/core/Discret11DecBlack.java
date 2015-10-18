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
 * 18 oct. 2015 Author Mannix54
 */



package com.ib.cryptimage.core;

import java.awt.image.BufferedImage;

/**
 * @author Mannix54
 *
 */
public class Discret11DecBlack extends Discret11Dec {
	
	public Discret11DecBlack(int key16bits) {
		super(key16bits);		
	}

	public Discret11DecBlack(int key16bits, double perc1, double perc2) {
		super(key16bits, perc1, perc2);		
	}
	
	/**
	 * transform an image that have been added
	 * this image can be crypted or decrypted, depending of the current mode
	 * of the Discret11 object
	 * @param image the image to be transformed
	 * @return the transformed image
	 */
	public BufferedImage transform(BufferedImage image) {
		//totalFrameCount++;		
		
		// we check the type image and the size
		image = this.convertToType(image, BufferedImage.TYPE_3BYTE_BGR);
		if (image.getWidth() != this.sWidth || image.getHeight() != 576) {
			image = this.getScaledImage(image, this.sWidth, 576);
		}
			

		if (this.enable) {			

			int z = 0;

			switch (this.seqFrame) {
			case 0:
				z = 0;
				break;
			case 1:
				z = 0;
				break;
			case 2:
				z = 0;
				break;
			case 3:
				z = 1;
				break;
			case 4:
				z = 1;
				break;
			case 5:
				z = 1;
				break;

			default:
				break;
			}

			if (this.currentframePos == 0) {
				if (this.start == false) {					
					int saveKey = this.index11bitsKey;
					this.index11bitsKey = this.saveIndex11bitsKey;
					image = modifyOddFrame2(image, 1);
					cptArray = 0;					
					this.index11bitsKey = saveKey;
				}
				// we compute only the even part of
												// the image
				image = modifyEvenFrame(image, z);

				this.seqFrame++;
				this.currentframePos++;
			} else { // we compute both the odd and even parts of the image (
						// impaire, paire )
				image = modifyOddFrame(image, z);
				this.seqFrame++;

				if (this.seqFrame == 6) {
					this.seqFrame = 0;					
				}

				image = modifyEvenFrame(image, z);
				this.seqFrame++;

				this.currentframePos++;

			}
			
			this.checkMotif(image);
			this.start = false;
			return image;
		} else {				
			this.checkMotif(image);
			return image;
		}
	}
	
	/**
	 * Transform the lines of the even part of an image ( trame paire )
	 * @param image
	 * @param z the z value for the delay array
	 * @return
	 */
	private BufferedImage modifyEvenFrame(BufferedImage image, int z){		
		
		raster = image.getRaster();		
				
		for (int y = 1; y < 576; y++) {
			if(cptArray == 286){
				this.cptArray = 0;
			}
		
			if (y != 573 && y != 575) { // we don't increment if next line is 622 ( 574 in
				// digital image ) or if next line is 623 ( 576 in digital image )
				raster.setPixels(delayArray[index11bitsKey][this.seqFrame][cptArray] , y, this.sWidth
						- delayArray[index11bitsKey][this.seqFrame][cptArray] , 1, raster.getPixels(0,
						y, this.sWidth - delayArray[index11bitsKey][this.seqFrame][cptArray] , 1,
						new int[(this.sWidth - delayArray[index11bitsKey][this.seqFrame][cptArray]) * 3]));
				//draw black line at start of delay
				raster.setPixels(0, y, JobConfig.getDelay2() , 1, 
						new int[JobConfig.getDelay2()  * 3]);	
				cptArray++;
			}
			y++; // add one to y in order to have only even lines frame
		}		
		return image;		
	}
	
	/**
	 * Transform the lines of the odd part of an image ( trame impaire )
	 * @param image
	 * @param z the z value for the delay array
	 * @return
	 */
	private BufferedImage modifyOddFrame(BufferedImage image, int z){	
		
		raster = image.getRaster();	
	
		for (int y = 2; y < 576; y++) {
			if(cptArray == 286){
				this.cptArray = 0;
			}
	
			if (y != 574) { // we don't increment if it's line 310 ( 575 in
				// digital image )
				raster.setPixels(delayArray[index11bitsKey][this.seqFrame][cptArray] , y, this.sWidth
						- delayArray[index11bitsKey][this.seqFrame][cptArray] , 1, raster.getPixels(0,
						y, this.sWidth - delayArray[index11bitsKey][this.seqFrame][cptArray], 1,
						new int[(this.sWidth - delayArray[index11bitsKey][this.seqFrame][cptArray] ) * 3]));
				//draw black line at start of delay
				raster.setPixels(0, y, JobConfig.getDelay2() , 1, 
						new int[JobConfig.getDelay2()  * 3]);	
				cptArray++;
			}
			y++; // add one to y in order to have only odd lines frame
		}
		return image;			
	}
	
	/**
	 * Transform the lines of the odd part of an image ( trame impaire )
	 * @param image
	 * @param z the z value for the delay array
	 * @return
	 */
	private BufferedImage modifyOddFrame2(BufferedImage image, int z){
		
		raster = image.getRaster();	

	
		for (int y = 2; y < 576; y++) {
			if(cptArray == 286){
				this.cptArray = 0;
			}			
					
			if (y != 574) { // we don't increment if it's line 310 ( 575 in
				// digital image )
				raster.setPixels(delayArray[index11bitsKey][5][cptArray] , y, this.sWidth
						- delayArray[index11bitsKey][5][cptArray] , 1, raster.getPixels(0,
						y, this.sWidth - delayArray[index11bitsKey][5][cptArray], 1,
						new int[(this.sWidth - delayArray[index11bitsKey][5][cptArray] ) * 3]));
				//draw black line at start of delay
				raster.setPixels(0, y, JobConfig.getDelay2() , 1, 
						new int[JobConfig.getDelay2()  * 3]);	
				cptArray++;
			}
			y++; // add one to y in order to have only odd lines frame
		}
		return image;			
	}


}
