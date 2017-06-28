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
 * 16 juin 2017 Author Mannix54
 */



package com.ib.cryptimage.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;


/**
 * @author Mannix54
 *
 */
public class GenDelayArray {
	
	/**
	 * store the 2048 11 bit words
	 */
	private int[] key11BitsTab = new int[2047];	

	/**
	 * array for storing the values of LFSR for each 2048 11 bit words
	 */
	private int[][] poly = new int[2047][1716];
	
	/**
	 * store the value for the truth table
	 * first dimension is z, second is b0, third is b10
	 * delay[z][b0][b10]
	 */
	private int[][][] truthTable = new int[2][2][2];
	
	/**
	 * store the delay 
	 * in 2 dimension array who represents
	 * the 6 TV frames for the 2047 11 bit words
	 */
	private int[][][] delayArray = new int[2047][6][286];

	/**
	 * array for storing the delay in pixels
	 * 3 types of delay
	 */
	private int[] decaPixels = new int[3];

	/**
	 * 
	 */
	public GenDelayArray() {
		// TODO Auto-generated constructor stub
		initKey11BitsTab();
		initTruthTable();
		initPolyLFSR();
		initDecaPixels();
		initDelayArray();
		serializeDelayArray();		
	}

	private void initKey11BitsTab(){
		for (int i = 0; i < key11BitsTab.length; i++) {
			key11BitsTab[i] = i + 1;
		}		
	}	
	
	/**
	 * initialize the array of LFSR values
	 * @param a tab for the 2047 11 bits key words
	 */
	private void initPolyLFSR() {
		String word = "";
		int key;
		
		for (int j = 0; j < key11BitsTab.length; j++) {
			word = String.format("%11s",
					Integer.toBinaryString(key11BitsTab[j])).replace(" ", "0");
			word = new StringBuilder(word).reverse().toString();

			key = Integer.parseInt(word, 2);

			poly[j][0] = key;

			for (int i = 1; i < poly[j].length; i++) {
				key = getXorPoly(key);
				poly[j][i] = key;
			}
		}
	}
	
	/**
	 * initialize the delay table, 
	 * we choose the right truth table to be compatible with
	 * the current operational mode of the Discret11 object
	 * truthTable[z][b0][b10]
	 */
	private void initTruthTable() {
		truthTable[0][0][0] = 0;
		truthTable[0][0][1] = 1;
		truthTable[0][1][0] = 2;
		truthTable[0][1][1] = 2;
		truthTable[1][0][0] = 2;
		truthTable[1][0][1] = 0;
		truthTable[1][1][0] = 0;
		truthTable[1][1][1] = 1;		
	}	
	
	/**
	 * set the 3 shift pixels value for the decaPixels array
	 * @param perc1 the percentage value of retard 1
	 * @param perc2 the percentage value of retard 2
	 */
	private void initDecaPixels(){	
			decaPixels[0] = 0;
			decaPixels[1] = 1; 
			decaPixels[2] = 2; 
	}
	
	/**
	 * initialize the delay array for the 6 TV frames
	 */
	private void initDelayArray() {
		int z;
		for (int k = 0; k < key11BitsTab.length; k++) {
			z = 0;
			for (int i = 0; i < 6; i++) {
				for (int j = 0; j < 286; j++) {
					if (i == 0 || i == 1 || i == 2) {
						z = 0;
					} else {
						z = 1;
					}
					delayArray[k][i][j] = decaPixels[getDelay(poly[k][(i * 286)
							+ j], z)];
				}
			}
		}

	}
	
   private void serializeDelayArray(){
		//serialize delarray
		File fichier =  new File("delarray.ser") ;
		try {
			ObjectOutputStream oos =  new ObjectOutputStream(new FileOutputStream(fichier)) ;
			oos.writeObject(delayArray) ;
			oos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   }

	/**
	 * return the theorical delay according the LFSR value
	 * and z value
	 * @param valPoly the LFSR value
	 * @param z the z value
	 * @return the delay to apply on a line
	 */
	private int getDelay(int valPoly, int z){
		int b0 = this.getLSB(valPoly);
		int b10 = this.getMSB(valPoly);
		return truthTable[z][b0][b10];
	}
	
	/**
	 * return the result of the poly P(x) = x11 + x9 + 1
	 * @param val the value for the poly
	 * @return the result of the poly
	 */
	private int getXorPoly(int val){
		String key = String.format
				("%11s", Integer.toBinaryString(val)).replace(" ", "0");
		
		int msb = getMSB(val);
		
		int b09 = Integer.parseInt(Character.toString(key.charAt(2)));
		
		int res = msb ^ b09;
		
		key = key.substring(1, 11) + String.valueOf(res);		
		
		return Integer.parseInt(key,2);		
	}
	
	/**
	 * return the LSB value from a binary word
	 * @param word the binary word
	 * @return the LSB value
	 */
	private int getLSB(int word){
		String key = String.format
				("%11s", Integer.toBinaryString(word)).replace(" ", "0");
		
		return Integer.parseInt(Character.toString(key.charAt(key.length()-1)));		
	}
	
	/**
	 * return the MSB value from a binary word
	 * @param word the binary word
	 * @return the MSB value
	 */
	private int getMSB(int word){
		String key = String.format
				("%11s", Integer.toBinaryString(word)).replace(" ", "0");
		
		return Integer.parseInt(Character.toString(key.charAt(0))); 		
	}

	public int[][][] getDelayArray() {
		return delayArray;
	}
	
	
}
