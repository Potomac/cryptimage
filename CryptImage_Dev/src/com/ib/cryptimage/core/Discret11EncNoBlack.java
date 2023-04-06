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
public class Discret11EncNoBlack extends Discret11Enc {
	
	private int[] tabPixels;
	private int[] rgbPixel = new int[3];
	
	

	public Discret11EncNoBlack(int key16bits, String audienceList, int cycle) {
		super(key16bits, audienceList, cycle);		
	}
	
	public Discret11EncNoBlack(int key16bits, String audienceList, int cycle, double perc1, double perc2) {
		super(key16bits, audienceList, cycle, perc1, perc2);		
	}
	
	protected void drawLine(int delay, int y){
		//draw color line at start of delay
		try {
			rgbPixel = raster.getPixel(delay*3, y, 
					new int[3]);
		} catch (Exception e) {
			rgbPixel = raster.getPixel(delay, y, 
					new int[3]);
		}
		
		tabPixels = new int[25 *3];
		for (int i = 0; i < tabPixels.length; i=i+3) {
			tabPixels[i] = 0;
			tabPixels[i+1] = 0;
			tabPixels[i+2] = 0;
		}
		
		raster.setPixels(0, y, 25, 1, 
				tabPixels);			
	}

}
