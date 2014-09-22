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
	
	private void feedTheDelayArray(){
		int j = 0;
		
		// first full frame			
		// even frame ( frame paire ) z = 0
		for (int i = 1; i < this.heightFrame; i++) {			
			delay[0][i] = getDelayValue(0, getLSB(randArray[j]), getMSB(randArray[j]));
			i++;
			j++;
		}
		// odd frame ( frame impaire ) z = 0
		for (int i = 0; i < this.heightFrame; i++) {			
			delay[0][i] = getDelayValue(0, getLSB(randArray[j]), getMSB(randArray[j]));
			//delay[0][i] = 0;
			i++;
			j++;
		}
		
		//second full frame		
		// even frame ( frame paire ) z = 0
		for (int i = 1; i < this.heightFrame; i++) {			
			delay[1][i] = getDelayValue(0, getLSB(randArray[j]), getMSB(randArray[j]));			
			i++;
			j++;
		}
		// odd frame ( frame impaire ) z = 1
		for (int i = 0; i < this.heightFrame; i++) {			
			delay[1][i] = getDelayValue(1, getLSB(randArray[j]), getMSB(randArray[j]));
			//delay[1][i] = 0;
			i++;
			j++;
		}		

		
		
		//third full frame		
		// even frame ( frame paire ) z = 1
		for (int i = 1; i < this.heightFrame; i++) {			
			delay[2][i] = getDelayValue(1, getLSB(randArray[j]), getMSB(randArray[j]));
			i++;
			j++;
		}		
		// odd frame ( frame impaire ) z = 1
		for (int i = 0; i < this.heightFrame; i++) {			
			delay[2][i] = getDelayValue(1, getLSB(randArray[j]), getMSB(randArray[j]));
			//delay[2][i] = 0;
			i++;
			j++;
		}
	}
	
	private void feedTheDelayArrayStrictMode(){
		int j = 0;
		
		//first full frame
		delay[0][0] = 0; // line 23 (1) of odd frame is not crypted with strictMode
		delay[0][1] = 0; // line 336 (2) of even frame is not crypted with strictMode
		// even frame ( frame paire ) z = 0
		for (int i = 3; i < this.heightFrame; i++) {			
			delay[0][i] = getDelayValue(0, getLSB(randArray[j]), getMSB(randArray[j]));			
			if(i!=573){// we don't increment if it's line 623 ( 576 in digital image )
				j++;
			}
			i++;
		}
		delay[0][575] = 0; // line 623 (576) of even frame is not crypted with strictMode
		delay[0][573] = 0; // line 622 (574) of even frame is not crypted with strictMode
		// first full frame				
		// odd frame ( frame impaire ) z = 0
		
		for (int i = 2; i < this.heightFrame; i++) {			
			delay[0][i] = getDelayValue(0, getLSB(randArray[j]), getMSB(randArray[j]));			
			//delay[0][i] = 0;			
			if(i!=572) { // we don't increment if it's line 310 ( 575 in digital image )
				j++;}
			i++;
		}
		delay[0][574] = 0; // line 310 (575) of odd frame is not crypted with strictMode		

		////////////////////////////////////////////////////
		//second full frame
		delay[1][0] = 0; // line 23 (1) of odd frame is not crypted with strictMode
		delay[1][1] = 0; // line 336 (2) of even frame is not crypted with strictMode		
		//second full frame
		// even frame ( frame paire ) z = 0
		for (int i = 3; i < this.heightFrame; i++) {			
			delay[1][i] = getDelayValue(0, getLSB(randArray[j]), getMSB(randArray[j]));				
			if(i!=573){// we don't increment if it's line 623 ( 576 in digital image )
				j++;
			}
			i++;
		}
		delay[1][575] = 0; // line 623 (576) of even frame is not crypted with strictMode
		delay[1][573] = 0; // line 622 (574) of even frame is not crypted with strictMode
		
		// odd frame ( frame impaire ) z = 1		
		for (int i = 2; i < this.heightFrame; i++) {			
			delay[1][i] = getDelayValue(1, getLSB(randArray[j]), getMSB(randArray[j]));
			//delay[1][i] = 0;			
			if(i!=572) { // we don't increment if it's line 310 ( 575 in digital image )
				j++;}
			i++;
		}
		delay[1][574] = 0; // line 310 (575) of odd frame is not crypted with strictMode
		
		////////////////////////////////////////////////////////////////////////
		//third full frame
		delay[2][0] = 0; // line 23 (1) of odd frame is not crypted with strictMode
		delay[2][1] = 0; // line 336 (2) of even frame is not crypted with strictMode		
		//third full frame
		// even frame ( frame paire ) z = 1
		for (int i = 3; i < this.heightFrame; i++) {			
			delay[2][i] = getDelayValue(1, getLSB(randArray[j]), getMSB(randArray[j]));			
			if(i!=573){// we don't increment if it's line 623 ( 576 in digital image )
				j++;
			}
			i++;
		}
		delay[2][575] = 0; // line 623 (576) of even frame is not crypted with strictMode
		delay[2][573] = 0; // line 622 (574) of even frame is not crypted with strictMode
		// odd frame ( frame impaire ) z = 1		
		for (int i = 2; i < this.heightFrame; i++) {			
			delay[2][i] = getDelayValue(1, getLSB(randArray[j]), getMSB(randArray[j]));
			//delay[2][i] = 0;			
			if(i!=572) { // we don't increment if it's line 310 ( 575 in digital image )
				j++;}
			i++;
		}
		delay[2][574] = 0; // line 310 (575) of odd frame is not crypted with strictMode
	
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
		
		if ( z==0 && b0 == 0 && b10 ==0 ){
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
		return res;		
	}

}
