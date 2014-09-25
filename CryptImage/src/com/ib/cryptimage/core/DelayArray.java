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
 * 18 sept. 2014 Author Mannix54
 */



package com.ib.cryptimage.core;


public class DelayArray {
	
	private int delay[][];
	private int heightFrame;
	private Lfsr gen;	
	private int[] randArray;
	private boolean strictMode;
	
	public DelayArray(int heightFrame, int keyWord, boolean strictMode){
		this.strictMode = strictMode;
		this.heightFrame = heightFrame;
		//this.keyWord = keyWord;
		delay = new int[3][this.heightFrame];		
		this.randArray = new int[this.heightFrame * 3];			
		
		String word = String.format
				("%11s", Integer.toBinaryString(keyWord)).replace(" ", "0");
		gen = new Lfsr(word,9);
	}
	
	public int[][] getDelayArray(){
		initDelay();
		if(this.strictMode){
			feedTheDelayArrayStrictMode();
		}
		else{			
			feedTheDelayArray();
		}
		return delay;
	}
	
	private void initDelay(){
		gen.resetLFSR();
		
		for (int i = 0; i < randArray.length; i++) {
			gen.generate(i);
			randArray[i] = Integer.parseInt(gen.toString(), 2);
			gen.resetLFSR();
		}		
	}
	
	private void feedTheDelayArray() {
		int temp = 0;
		int j = 0;

		int[][] z = new int[3][2];
		z[0][0] = 0;
		z[0][1] = 0;
		z[1][0] = 0;
		z[1][1] = 1;
		z[2][0] = 1;
		z[2][1] = 1;

		for (int fullFrame = 0; fullFrame < 3; fullFrame++) {
			// even frame ( frame paire )
			for (int i = 1; i < this.heightFrame; i++) {
				temp = j;
				delay[fullFrame][i] = getDelayValue(z[fullFrame][0],
						getLSB(randArray[j]), getMSB(randArray[j]));
				j++;
				debugDelay(i, temp, delay[fullFrame][i]);
				i++;
			}

			// odd frame ( frame impaire )
			for (int i = 0; i < this.heightFrame; i++) {
				temp = j;
				delay[fullFrame][i] = getDelayValue(z[fullFrame][1],
						getLSB(randArray[j]), getMSB(randArray[j]));
				j++;
				debugDelay(i, temp, delay[fullFrame][i]);
				i++;
			}
		}
	}
	
	private void feedTheDelayArrayStrictMode(){
		int temp = 0;
		int j = 0;
		
		int[][] z = new int[3][2];
		z[0][0] = 0;
		z[0][1] = 0;
		z[1][0] = 0;
		z[1][1] = 1;
		z[2][0] = 1;
		z[2][1] = 1;
		
		for (int fullFrame = 0; fullFrame < 3; fullFrame++) {
			delay[fullFrame][0] = 0; // line 23 (1) of odd frame is not crypted with
								// strictMode			
			// even frame ( frame paire )
			for (int i = 1; i < this.heightFrame; i++) {
				temp = j;
				delay[fullFrame][i] = getDelayValue(z[fullFrame][0], getLSB(randArray[j]),
						getMSB(randArray[j]));
				if (i != 571 && i!= 573) {// we don't increment if next line is 622 ( 574 in
								// digital image ) or if next line is 623 ( 576 in digital image )
					j++;
				}
				debugDelay(i, temp, delay[fullFrame][i]);
//				System.out.println("ligne" + (i + 1) + " convert"
//						+ convertLine(i + 1) + " P" + temp + " " + "valeur("
//						+ delay[fullFrame][i]);
				i++;
			}
			delay[fullFrame][575] = 0; // line 623 (576) of even frame is not crypted
								// with strictMode
			delay[fullFrame][573] = 0; // line 622 (574) of even frame is not crypted
								// with strictMode	
			
			// odd frame ( frame impaire )
			for (int i = 2; i < this.heightFrame; i++) {
				temp = j;
				delay[fullFrame][i] = getDelayValue(z[fullFrame][1], getLSB(randArray[j]),
						getMSB(randArray[j]));				
				if (i != 572) { // we don't increment if it's line 310 ( 575 in
								// digital image )
					j++;
				}
				debugDelay(i, temp, delay[fullFrame][i]);
				i++;
			}
			delay[fullFrame][574] = 0; // line 310 (575) of odd frame is not crypted
								// with strictMode
		}
	}
	
	private int getLSB(int word){
		String key = String.format
				("%11s", Integer.toBinaryString(word)).replace(" ", "0");
		
		return Integer.parseInt(Character.toString(key.charAt(key.length()-1))); 
		
	}
	
	private int getMSB(int word){
		String key = String.format
				("%11s", Integer.toBinaryString(word)).replace(" ", "0");
		
		return Integer.parseInt(Character.toString(key.charAt(0))); 
		
	}
	
	private int getDelayValue(int z, int b0, int b10){		
		int res = 0;
		
/*		if ( z==0 && b0 == 0 && b10 ==0 ){
			res = 0;
		}
		if ( z==0 && b0 == 0 && b10 ==1 ){
			res = 1;
		}
		if ( z==0 && b0 == 1 && b10 ==0 ){
			res = 2;
		}
		if ( z==0 && b0 == 1 && b10 ==1 ){
			res = 2;
		}
		if ( z==1 && b0 == 0 && b10 ==0 ){
			res = 2;
		}
		if ( z==1 && b0 == 0 && b10 ==1 ){
			res = 0;
		}
		if ( z==1 && b0 == 1 && b10 ==0 ){
			res = 0;
		}
		if ( z == 1 && b0 == 1 && b10 == 1 ){
			res = 1;
		}
		return res;*/
		if (z == 0 && b0 == 0 && b10 == 0) {
			res = 2;
		}
		if (z == 0 && b0 == 0 && b10 == 1) {
			res = 1;
		}
		if (z == 0 && b0 == 1 && b10 == 0) {
			res = 0;
		}
		if (z == 0 && b0 == 1 && b10 == 1) {
			res = 0;
		}
		if (z == 1 && b0 == 0 && b10 == 0) {
			res = 0;
		}
		if (z == 1 && b0 == 0 && b10 == 1) {
			res = 2;
		}
		if (z == 1 && b0 == 1 && b10 == 0) {
			res = 2;
		}
		if (z == 1 && b0 == 1 && b10 == 1) {
			res = 1;
		}
		return res;
	}
	
	private int convertLine2(int frame_line)
	   {
	      if (frame_line < 23)
	      {
	         return 0;
	      }
	      if (frame_line <= 310)
	      {
	         return (2 * (frame_line - 23)) + 1;
	      }
	      if (frame_line < 336)
	      {
	         return 0;
	      }
	      if (frame_line <= 623)
	      {
	         return (2 * (frame_line - 336)) + 2;
	      }
	      else
	      {
	         return 0;
	      }
	   }
	
	private int convertLine(int frame_line) {
		float res = (float) ((float) (frame_line) / 2);
		float diff = res - (int)res;

		if (diff != 0) { //frame impaire
			int val = (int)res + 23;
			return val;
		}
		else {
			return (int)(res + 335);
		}

	}
	
	private void debugDelay(int digitalLine, int cptJ, int delay ){
		String message = "ligne " + (digitalLine + 1) + ", analog "
				+ convertLine(digitalLine + 1) + ", P" + cptJ + " " + "(, retard "+
				+ delay  +"), LFSR " + randArray[cptJ] ;
		System.out.println(message);
	}

}
