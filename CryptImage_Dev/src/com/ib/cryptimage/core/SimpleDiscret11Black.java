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


/**
 * @author Mannix54
 *
 */
public class SimpleDiscret11Black extends SimpleDiscret11 {
	int[] tabPixels;
	int[] rgbPixel = new int[3];	

	public SimpleDiscret11Black(int key16bits, int audienceLevel, int mode, int height, int width) {
		super(key16bits, audienceLevel, mode, height, width);		
	}
	
	
	public SimpleDiscret11Black(int key16bits, int audienceLevel, int mode, int height, int width, double perc1,
			double perc2) {
		super(key16bits, audienceLevel, mode, height, width, perc1, perc2);		
	}

	protected void drawLine(int y){
		//draw black line at start of delay
		raster.setPixels(0, y, JobConfig.getDelay2(), 1, 
				new int[JobConfig.getDelay2() * 3]);		
	}
	
	protected void drawLineDec(int y){
		//draw color line at end of delay
		try {
			rgbPixel = raster.getPixel(this.width - 1 - delayArray[this.seqFullFrame][y], y, 
					new int[3]);
		} catch (Exception e) {
			rgbPixel = raster.getPixel(this.width - 1 - delayArray[this.seqFullFrame][y], y, 
					new int[3]);
		}
					
		tabPixels = new int[delayArray[this.seqFullFrame][y]*3];
		for (int i = 0; i < tabPixels.length; i=i+3) {
			tabPixels[i] = rgbPixel[0];
			tabPixels[i+1] = rgbPixel[1];
			tabPixels[i+2] = rgbPixel[2];
		}
		
		raster.setPixels(this.width - delayArray[this.seqFullFrame][y], y,delayArray[this.seqFullFrame][y], 1, 
				tabPixels);			
	}

}


