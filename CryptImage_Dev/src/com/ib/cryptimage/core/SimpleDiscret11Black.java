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
public class SimpleDiscret11Black extends SimpleDiscret11 {

	public SimpleDiscret11Black(int key16bits, int audienceLevel, int mode, int height, int width) {
		super(key16bits, audienceLevel, mode, height, width);		
	}
	
	
	public SimpleDiscret11Black(int key16bits, int audienceLevel, int mode, int height, int width, double perc1,
			double perc2) {
		super(key16bits, audienceLevel, mode, height, width, perc1, perc2);		
	}

	/**
	 * transform an image that have been added
	 * this image can be crypted or decrypted, depending of the current mode
	 * of the SimpleDiscret11 object
	 * @param image the image to be transformed
	 * @return the transformed image
	 */
	public BufferedImage transform(BufferedImage image){
				
		//we check the type image and the size
		image = this.convertToType(image, BufferedImage.TYPE_3BYTE_BGR);		
		
		raster = image.getRaster();					
		
		for (int y = 0; y < this.height; y++) {
			raster.setPixels(delayArray[this.seqFullFrame][y], y, this.width
					- delayArray[this.seqFullFrame][y], 1, raster.getPixels(0,
					y, this.width - delayArray[this.seqFullFrame][y], 1,
					new int[(this.width - delayArray[this.seqFullFrame][y]) * 3]));
			
			//draw black line at start of delay
			raster.setPixels(0, y, JobConfig.getDelay2(), 1, 
					new int[JobConfig.getDelay2() * 3]);			
		}
		
		this.seqFullFrame++;
	
		if (this.seqFullFrame == 3) {
			this.seqFullFrame = 0;			
		}

		this.currentframePos++;
		
		return image;
	}	
	


}


