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
 * 16 oct. 2015 Author Mannix54
 */



package com.ib.cryptimage.core;

import java.awt.image.BufferedImage;

/**
 * @author Mannix54
 *
 */
public class Discret11EncNoBlack extends Discret11Enc {
	
	private int[] tabPixels;
	private int[] rgbPixel = new int[3];
	
	

	public Discret11EncNoBlack(int key16bits, String audienceList, int cycle) {
		super(key16bits, audienceList, cycle);		
	}
	
	public Discret11EncNoBlack(int key16bits, String audienceList, int cycle, double perc1, double perc2) {
		super(key16bits, audienceList, cycle, perc1, perc2);		
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

		//checkMotif(image);		
		
			//System.out.println("transform : " + totalFrameCount);

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

			if (this.seqFrame == 0) {				
				image = setWhite310Line(image);	
				if (this.saveIndexUse11bitsKey != -1 ) {					
					int saveKey = this.indexUse11bitsKey;
					this.indexUse11bitsKey = this.saveIndexUse11bitsKey;
					image = modifyOddFrame2(image, 1);
					cptArray = 0;					
					this.indexUse11bitsKey = saveKey;
				}
				// we compute only the even part of
												// the image				
				image = modifyEvenFrame(image, z);
				this.seqFrame++;
				setAudience622Line(image);
				this.currentframePos++;
			} else { // we compute both the odd and even parts of the image (
						// impaire, paire )				
				
				image = modifyOddFrame(image, z);
				this.seqFrame++;

				if (this.seqFrame == 6) {					
					indexCycle +=1;	
					if(indexCycle == 8) {
						this.enable = true;
					}
					image = setWhite310Line(image);	
					this.seqFrame = 0;					
				}
				else{
					image = setBlack310Line(image);	
				}
				
				if(indexCycle ==  8  ){					
					changeAudience();
				}		
				

				image = modifyEvenFrame(image, z);
				this.seqFrame++;
				setAudience622Line(image);

				this.currentframePos++;

			}
	
			return image;		
	}
	
	
	/**
	 * Transform the lines of the even part of an image ( trame paire )
	 * @param image
	 * @param z the z value for the delay array
	 * @return
	 */
	private BufferedImage modifyEvenFrame(BufferedImage image, int z) {
		if (enable) {				
			raster = image.getRaster();
			
			//int temp2 = 0;

			for (int y = 1; y < 576; y++) {
				if (cptArray == 286) {
					this.cptArray = 0;
				}

				if (y != 573 && y != 575) { // we don't increment if next line
											// is 622 ( 574 in
					// digital image ) or if next line is 623 ( 576 in digital
					// image )
					raster.setPixels(delayArray[indexUse11bitsKey][this.seqFrame][cptArray], y, this.sWidth - delayArray[indexUse11bitsKey][this.seqFrame][cptArray], 1, raster
							.getPixels(0, y, this.sWidth - delayArray[indexUse11bitsKey][this.seqFrame][cptArray], 1,
									new int[(this.sWidth - delayArray[indexUse11bitsKey][this.seqFrame][cptArray]) * 3]));
	
					//draw color line at start of delay
					try {
						rgbPixel = raster.getPixel(delayArray[indexUse11bitsKey][this.seqFrame][cptArray]*3, y, 
								new int[3]);
					} catch (Exception e) {
						rgbPixel = raster.getPixel(delayArray[indexUse11bitsKey][this.seqFrame][cptArray], y, 
								new int[3]);
					}
										
					tabPixels = new int[delayArray[indexUse11bitsKey][this.seqFrame][cptArray]*3];
					for (int i = 0; i < tabPixels.length; i=i+3) {
						tabPixels[i] = rgbPixel[0];
						tabPixels[i+1] = rgbPixel[1];
						tabPixels[i+2] = rgbPixel[2];
					}
					
					raster.setPixels(0, y, delayArray[indexUse11bitsKey][this.seqFrame][cptArray], 1, 
							tabPixels);
					
					cptArray++;
				}
				y++; // add one to y in order to have only even lines frame
			}
		}
		return image;
	}
	
	/**
	 * Transform the lines of the odd part of an image ( trame impaire )
	 * @param image
	 * @param z the z value for the delay array
	 * @return
	 */
	private BufferedImage modifyOddFrame(BufferedImage image, int z) {
		if (enable) {			
			raster = image.getRaster();
			
			for (int y = 2; y < 576; y++) {
				if (cptArray == 286) {
					this.cptArray = 0;
				}
				

				if (y != 574) { // we don't increment if it's line 310 ( 575 in
					// digital image )
					raster.setPixels(delayArray[indexUse11bitsKey][this.seqFrame][cptArray], y, this.sWidth - delayArray[indexUse11bitsKey][this.seqFrame][cptArray], 1, raster
							.getPixels(0, y, this.sWidth - delayArray[indexUse11bitsKey][this.seqFrame][cptArray], 1,
									new int[(this.sWidth - delayArray[indexUse11bitsKey][this.seqFrame][cptArray]) * 3]));
					
			
					//draw color line at start of delay
					try {
						rgbPixel = raster.getPixel(delayArray[indexUse11bitsKey][this.seqFrame][cptArray]*3, y, 
								new int[3]);
					} catch (Exception e) {
						rgbPixel = raster.getPixel(delayArray[indexUse11bitsKey][this.seqFrame][cptArray], y, 
								new int[3]);
					}
					
					tabPixels = new int[delayArray[indexUse11bitsKey][this.seqFrame][cptArray]*3];
					for (int i = 0; i < tabPixels.length; i=i+3) {
						tabPixels[i] = rgbPixel[0];
						tabPixels[i+1] = rgbPixel[1];
						tabPixels[i+2] = rgbPixel[2];
					}
					
					raster.setPixels(0, y, delayArray[indexUse11bitsKey][this.seqFrame][cptArray], 1, 
							tabPixels);	
					
					cptArray++;
				}
				y++; // add one to y in order to have only odd lines frame
			}
		}
		return image;
	}
	
	/**
	 * Transform the lines of the odd part of an image ( trame impaire )
	 * @param image
	 * @param z the z value for the delay array
	 * @return
	 */
	private BufferedImage modifyOddFrame2(BufferedImage image, int z) {
		if (enable) {			
			raster = image.getRaster();			
			
			for (int y = 2; y < 576; y++) {
				if (cptArray == 286) {
					this.cptArray = 0;
				}

				if (y != 574) { // we don't increment if it's line 310 ( 575 in
					// digital image )
					raster.setPixels(delayArray[indexUse11bitsKey][5][cptArray], y, this.sWidth - delayArray[indexUse11bitsKey][5][cptArray], 1, raster
							.getPixels(0, y, this.sWidth - delayArray[indexUse11bitsKey][5][cptArray], 1,
									new int[(this.sWidth - delayArray[indexUse11bitsKey][5][cptArray]) * 3]));

					//draw color line at start of delay
					try {
						rgbPixel = raster.getPixel(delayArray[indexUse11bitsKey][5][cptArray]*3, y, 
								new int[3]);
					} catch (Exception e) {
						rgbPixel = raster.getPixel(delayArray[indexUse11bitsKey][5][cptArray], y, 
								new int[3]);
					}
					
					tabPixels = new int[delayArray[indexUse11bitsKey][5][cptArray]*3];
					for (int i = 0; i < tabPixels.length; i=i+3) {
						tabPixels[i] = rgbPixel[0];
						tabPixels[i+1] = rgbPixel[1];
						tabPixels[i+2] = rgbPixel[2];
					}
					
					raster.setPixels(0, y, delayArray[indexUse11bitsKey][5][cptArray], 1, 
							tabPixels);						
					cptArray++;
				}
				y++; // add one to y in order to have only odd lines frame
			}
		}
		return image;
	}

}
