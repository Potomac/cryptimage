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


/**
 * @author Mannix54
 *
 */
public class SimpleDiscret11MaskedBorder extends SimpleDiscret11 {
	
	int[] tabPixels;
	int[] rgbPixel = new int[3];

	public SimpleDiscret11MaskedBorder(int key16bits, int audienceLevel, int mode, int height, int width, double perc1,
			double perc2) {
		super(key16bits, audienceLevel, mode, height, width, perc1, perc2);		
	}

	public SimpleDiscret11MaskedBorder(int key16bits, int audienceLevel, int mode, int height, int width) {
		super(key16bits, audienceLevel, mode, height, width);		
	}

	protected void drawLine(int y){
		//draw black line at start of delay
		
		int lineSizePixels = JobConfig.getDelay2() * 3;
		int[] tabPixels = new int[lineSizePixels];					
		
		raster.setPixels(0, y, JobConfig.getDelay2(), 1, tabPixels);			
	}
	
	
}
