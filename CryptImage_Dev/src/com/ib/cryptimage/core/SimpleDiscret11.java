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
 * 6 oct. 2014 Author Mannix54
 */



package com.ib.cryptimage.core;


import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;


/**
 * @author Mannix54
 *
 */
public class SimpleDiscret11 {
	/**
	 * store the 11 bits key word
	 */
	private int key11bits;
	/**
	 * store the 16 bits key word
	 */
	private int key16bits;
	
	/**
	 * store the 7 11 bit words according to the audience level
	 */
	private int[] key11BitsTab = new int[7];
	
	/**
	 * true if encoding, false otherwise
	 */
	private boolean isEnc;
	/**
	 * true if decoding, false otherwise
	 */
	private boolean isDec;
	
	/**
	 * store the current operation mode ( 0 : encoding, 1: decoding )
	 */
	private int currentMode;
	/**
	 * encode mode
	 */
	public final static int MODE_ENC = 0;
	/**
	 * decode mode
	 */
	public final static int MODE_DEC = 1;
	/**
	 * array for storing the values of LFSR
	 */
	private int[] poly;
	/**
	 * store the number of frames that has been transformed by Discret11
	 */
	private int currentframePos = 0;
	/**
	 * store the current position from the 3 frames ( full image ) sequence
	 */
	private int seqFullFrame = 0;
	/**
	 * store the value for the truth table
	 * first dimension is z, second is b0, third is b10
	 * delay[z][b0][b10]
	 */
	private int[][][] truthTable = new int[2][2][2];
	/**
	 * store the delay in pixels value
	 * in 2 dimension array who represents
	 * the 6 TV frames
	 */
	private int[][] delayArray;	
	/**
	 * array for storing the delay in pixels
	 * 3 types of delay
	 */
	private int[] decaPixels = new int[3];

	/**
	 * the height of the image
	 */
	private int height;
	/**
	 * the width of the image
	 */
	private int width;
	
	
	/**
	 * create a new SimpleDiscret11 object
	 * @param key16bit the 16 bits key word to initialize the Discret11 object
	 * @param audienceLevel the level of the audience
	 * @param mode the operational mode ( 0 for encoding, 1 for decoding )
	 * @param height the height of the image
	 * @param width the width of the image
	 */
	public SimpleDiscret11(int key16bits, int audienceLevel, int mode, int height, int width){		
		this.key16bits = key16bits;
		this.initKey11BitsTab(key16bits);			
		this.key11bits = this.key11BitsTab[audienceLevel - 1];
		
		this.height = height;
		this.width = width;
		poly = new int[this.height * 3];
		delayArray = new int [3][this.height];
		initMode(mode);
		initPolyLFSR(key11bits);
		// choose the right truth table and feed the array delay
		initTruthTable();
		initDecaPixels(0.0167,0.0334);
		initDelayArray();
	}
	
	/**
	 * create a new SimpleDiscret11 object with redefined percentages for the delay 1 and 2
	 * @param key16bit the 16 bits key word to initialize the Discret11 object
	 * @param audienceLevel the level of the audience
	 * @param mode the operational mode ( 0 for encoding, 1 for decoding )
	 * @param height the height of the image
	 * @param width the width of the image
	 * @param perc1 the percentage level for delay 1
	 * @param perc2 the percentage level for delay 2
	 */
	public SimpleDiscret11(int key16bits, int audienceLevel, int mode, int height, int width,
			double perc1, double perc2){		
		this.key16bits = key16bits;
		this.initKey11BitsTab(key16bits);			
		this.key11bits = this.key11BitsTab[audienceLevel - 1];
		
		this.height = height;
		this.width = width;
		poly = new int[this.height * 3];
		delayArray = new int [3][this.height];
		initMode(mode);
		initPolyLFSR(key11bits);
		// choose the right truth table and feed the array delay
		initTruthTable();
		initDecaPixels(perc1,perc2);
		initDelayArray();
	}
	
	/**
	 * initialize the key11BitsTab
	 * @param key16bits the 16 bits keyword
	 */
	private void initKey11BitsTab(int key16bits){
		String word = String.format
				("%16s", Integer.toBinaryString(key16bits)).replace(" ", "0");
		//word = new StringBuilder(word).reverse().toString();
		
		//audience 1
		String audience1 = word.substring(0, 11);
		this.key11BitsTab[0] = Integer.parseInt(audience1,2);
		
		//audience 2
		String audience2 = word.substring(3, 14);
		this.key11BitsTab[1] = Integer.parseInt(audience2,2);
		
		//audience 3
		String audience3 = word.substring(6, 16) + word.charAt(0);
		this.key11BitsTab[2] = Integer.parseInt(audience3,2);
		
		//audience 4
		String audience4 =  word.substring(9, 16) + word.substring(0, 4);
		this.key11BitsTab[3] = Integer.parseInt(audience4,2);
		
		//audience 5
		String audience5 = word.substring(12, 16) +  word.substring(0, 7);
		this.key11BitsTab[4] = Integer.parseInt(audience5,2);
		
		//audience 6
		String audience6 =  word.charAt(15) + word.substring(0, 10) ;
		this.key11BitsTab[5] = Integer.parseInt(audience6,2);
		
		//audience 7		
		this.key11BitsTab[6] = 1337;
		
	}
	
	
	/**
	 * initialize the current mode ( 0 for encoding, 1 for decoding )
	 * @param mode 0 for encoding, 1 for decoding
	 */
	private void initMode(int mode){
		this.currentMode = mode;
		if(mode == MODE_ENC){
			this.isEnc = true;
			this.isDec = false;
		}
		else{
			this.isEnc = false;
			this.isDec = true;
		}
	}
	
	/**
	 * initialize the array of LFSR values
	 * @param key11bits the 11 bits key word
	 */
	private void initPolyLFSR(int key11bits){
		String word = String.format
				("%11s", Integer.toBinaryString(key11bits)).replace(" ", "0");
		word = new StringBuilder(word).reverse().toString();
		
		int key = Integer.parseInt(word,2);		
		
		poly[0] = key;
		
		for (int i = 1; i < poly.length; i++) {
			key = getXorPoly(key);
			poly[i] = key;
		}		
	}
	
	/**
	 * initialize the delay table, 
	 * we choose the right truth table to be compatible with
	 * the current operational mode of the Discret11 object
	 * truthTable[z][b0][b10]
	 */
	private void initTruthTable(){
		switch (this.currentMode) {
		case 0: //encoding mode			
			truthTable[0][0][0] = 0;
			truthTable[0][0][1] = 1;
			truthTable[0][1][0] = 2;
			truthTable[0][1][1] = 2;
			truthTable[1][0][0] = 2;
			truthTable[1][0][1] = 0;
			truthTable[1][1][0] = 0;
			truthTable[1][1][1] = 1;			
			break;
		case 1: //decoding mode
			truthTable[0][0][0] = 2;
			truthTable[0][0][1] = 1;
			truthTable[0][1][0] = 0;
			truthTable[0][1][1] = 0;
			truthTable[1][0][0] = 0;
			truthTable[1][0][1] = 2;
			truthTable[1][1][0] = 2;
			truthTable[1][1][1] = 1;
			break;
		default:
			break;
		}	
	}
	
	/**
	 * set the 3 shift pixels value for the decaPixels array
	 * @param perc1 the percentage value of retard 1
	 * @param perc2 the percentage value of retard 2
	 */
	private void initDecaPixels(double perc1, double perc2){
		
		decaPixels[0] = 0;
		decaPixels[1] = (int)(Math.round(perc1 * 768)); // previous value : 0.018 0.0167 0.0167
		decaPixels[2] = (int)(Math.round(perc2 * 768)); // previous value : 0.036 0.0347 0.0334
		
		if (decaPixels[1] == 0){
			decaPixels[1] = 1;
		}
		if (decaPixels[2] == 0 ){
			decaPixels[2] = 2;
		}		
	}
	/**
	 * initialize the delay array for
	 * the 6 TV frames
	 */
	private void initDelayArray(){

		int[][] z = new int[3][2];
		z[0][0] = 0;
		z[0][1] = 0;
		z[1][0] = 0;
		z[1][1] = 1;
		z[2][0] = 1;
		z[2][1] = 1;	

		for (int fullFrame = 0; fullFrame < 3; fullFrame++) {
			
			// odd frame ( frame impaire )
			for (int i = 0; i < this.height; i++) {	
				delayArray[fullFrame][i] = decaPixels[
				 getDelay(poly[(fullFrame*this.height) + i],z[fullFrame][0])];				
				i++;
			}			
			
			// even frame ( frame paire )
			for (int i = 1; i < this.height; i++) {						
				delayArray[fullFrame][i] = decaPixels[
				  getDelay(poly[(fullFrame*this.height) + i],z[fullFrame][1])];				
				i++;
			}
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
		
		BufferedImage bi = new BufferedImage(this.width,this.height,
				BufferedImage.TYPE_3BYTE_BGR);// img.getType_image());
											// BufferedImage.TYPE_INT_BGR			
		
		Raster raster1 = bi.getRaster();
		WritableRaster raster2 = image.getRaster();		
		
		
		int temp = 0;
		int shift = 0;
		
		for (int y = 0; y < this.height; y++) {
			temp = delayArray[this.seqFullFrame][y];
			if (this.isDec == true && (temp == decaPixels[1])) {
				shift = (this.decaPixels[2] - this.decaPixels[1])
						- this.decaPixels[1];
			}
			raster2.setPixels(temp + shift, y, this.width
					- temp - shift, 1, raster2.getPixels(0,
					y, this.width - temp - shift, 1,
					new int[(this.width - temp - shift) * 3]));
			//draw black line at start of delay
			raster2.setPixels(0, y, temp + shift, 1, raster1.getPixels(0,
					y, temp + shift, 1,
					new int[(temp + shift) * 3]));			
		}
		
		this.seqFullFrame++;
	
		if (this.seqFullFrame == 3) {
			this.seqFullFrame = 0;			
		}

		this.currentframePos++;
		
		return image;
	}	
	
	/**
	 * Convert a source image to a desired BufferedImage type
	 * @param sourceImage the image source
	 * @param targetType the target type
	 * @return a converted BufferedImage
	 */
	private  BufferedImage convertToType(BufferedImage sourceImage,
			int targetType) {
		BufferedImage image;

		// if the source image is already the target type, return the source
		// image
		if (sourceImage.getType() == targetType) {
			image = sourceImage;
		}
		// otherwise create a new image of the target type and draw the new
		// image
		else {
			image = new BufferedImage(sourceImage.getWidth(),
					sourceImage.getHeight(), targetType);
			image.getGraphics().drawImage(sourceImage, 0, 0, null);
		}
		return image;
	}	

	public int getKey11bits() {
		return key11bits;
	}

	public boolean isEnc() {
		return isEnc;
	}

	public boolean isDec() {
		return isDec;
	}

	public int getCurrentMode() {
		return currentMode;
	}

	public int getCurrentframePos() {
		return currentframePos;
	}

	public int getSeqFrame() {
		return seqFullFrame;
	}

	public int[] getDecaPixels() {
		return decaPixels;
	}

	public int getKey16bits() {
		return key16bits;
	}
	
}
