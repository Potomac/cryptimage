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
public class Discret11DecBlack extends Discret11Dec {
	
	int[] tabPixels;
	int[] rgbPixel = new int[3];	
	
	public Discret11DecBlack(int key16bits) {
		super(key16bits);		
	}

	public Discret11DecBlack(int key16bits, double perc1, double perc2) {
		super(key16bits, perc1, perc2);		
	}

	protected void _drawLine(int delay, int y){
		//draw black line at start of delay
		raster.setPixels(0, y, JobConfig.getDelay2() , 1, 
				new int[JobConfig.getDelay2()  * 3]);		
	}
	
	protected void drawLine(int delay, int y){
		//draw black line at start of delay
//		raster.setPixels(this.sWidth - JobConfig.getDelay2(), y, JobConfig.getDelay2() , 1, 
//				new int[JobConfig.getDelay2()  * 3]);
		//draw color line at end of delay
		try {
			rgbPixel = raster.getPixel(this.sWidth - 1 - delay, y, 
					new int[3]);
		} catch (Exception e) {
			rgbPixel = raster.getPixel(this.sWidth - 1 - delay, y, 
					new int[3]);
		}
					
		tabPixels = new int[delay * 3];
		for (int i = 0; i < tabPixels.length; i=i+3) {
			tabPixels[i] = rgbPixel[0];
			tabPixels[i+1] = rgbPixel[1];
			tabPixels[i+2] = rgbPixel[2];
		}
		
		raster.setPixels(this.sWidth - delay, y,delay, 1, 
				tabPixels);	
	}


}
