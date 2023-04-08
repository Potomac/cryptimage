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
public class Discret11EncMaskedBorder extends Discret11Enc {	

	public Discret11EncMaskedBorder(int key16bits, String audienceList, int cycle) {
		super(key16bits, audienceList, cycle);		
	}
	
	public Discret11EncMaskedBorder(int key16bits, String audienceList, int cycle, double perc1, double perc2) {
		super(key16bits, audienceList, cycle, perc1, perc2);		
	}
	
	protected void drawLine(int delay, int y){
		//draw black line at start of delay
		int lineSizePixels = JobConfig.getDelay2() * 3;;
		int[] tabPixels = new int[lineSizePixels];		

		raster.setPixels(0, y, JobConfig.getDelay2(), 1, tabPixels);			
	}

}
