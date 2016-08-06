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
 * 24 juil. 2016 Author Mannix54
 */



package com.ib.cryptimage.core;

/**
 * @author Mannix54
 *
 */
public class CosSinus {
	
	private float[] tabCos = new float[361];
	private float[] tabSin = new float[361];
	
	/**
	 * 
	 */
	public CosSinus() {
		for (int angle = 0; angle <= 360; angle++) {
			tabCos[angle] = (float) Math.cos( angle * Math.PI/180d);
			tabSin[angle] = (float) Math.sin(angle * Math.PI/180d);
			
//			tabCos[angle] = (float) Math.cos( angle );
//			tabSin[angle] = (float) Math.sin(angle );
	    }
		
		
		
	}

	public float getCos(int angle) {
		if(angle < 0){
			angle = angle * -1;
		}
		return tabCos[angle];
	}

	public float getSin(int angle) {
		float val;
		if(angle<0){
			val = tabSin[angle*-1] * -1;
		}
		else {
			val = tabSin[angle];
		}
		return val;
	}

}
