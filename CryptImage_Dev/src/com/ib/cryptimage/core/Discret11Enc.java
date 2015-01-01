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
 * along with CryptImage_Dev.  If not, see <http://www.gnu.org/licenses/>
 * 
 * 31 déc. 2014 Author Mannix54
 * http://ibsoftware.free.fr/cryptimage.php
 */



package com.ib.cryptimage.core;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;


/**
 * @author Mannix54
 *
 */
public class Discret11Enc {
	/**
	 * store the 16 bits key word
	 */
	private int key16bits;
	/**
	 * store the 11 bits key word
	 */
	private int key11bits;	
	/**
	 * store the current index for the 11 bits key
	 */
	private int index11bitsKey =-1;
	
	private int saveIndexUse11bitsKey;
	
	private boolean start = false;


	/**
	 * enable or disable the encoder
	 */
	private boolean enable = false;
	/**
	 * store the current audience level
	 */
	private int audienceLevel;
	
	/**
	 * store the 7 11 bit words according to the audience level
	 */
	private int[] key11BitsTab = new int[7];	

	/**
	 * array for storing the values of LFSR for each audience level
	 */
	private int[][] poly = new int[7][1716];
	/**
	 * store the number of frames that has been transformed by Discret11
	 */
	private int currentframePos = 0;
	/**
	 * store the current position from the 6 frames ( half image ) sequence
	 */
	private int seqFrame = 0;
	
	private int totalFrameCount = 0;
	
	private int[] audienceList = null;
	
	private int cycle;
	
	private int cptCycle;
	
	private int maxCycle = 0;
	
	private int indexCycle;
	
	private int indexUse11bitsKey = -1;
	
	
	
	/**
	 * store the value for the truth table
	 * first dimension is z, second is b0, third is b10
	 * delay[z][b0][b10]
	 */
	private int[][][] truthTable = new int[2][2][2];
	/**
	 * store the delay in pixels value
	 * in 2 dimension array who represents
	 * the 6 TV frames, we have 7 possibilities because 7 audience levels
	 */
	private int[][][] delayArray = new int[7][6][286];
	/**
	 * array for storing the delay in pixels
	 * 3 types of delay
	 */
	private int[] decaPixels = new int[3];
	/**
	 * the count iterator for poly array
	 */
	private int cptPoly = 0;
	/**
	 * the count iterator for delay array
	 */
	private int cptArray = 0;	
	/**
	 * the default width of the image which is 768
	 */
	private final int sWidth = 768;

	
	
	/**
	 * create a new Discret11Dec object	
	 * @param key16bit the 16 bits key word to initialize
	 *  the Discret11Enc object	
	 */
	public Discret11Enc(int key16bits, String audienceList, int cycle){
		initAudienceList(audienceList);
		this.cycle = cycle;
		this.key16bits = key16bits;
		this.initKey11BitsTab(key16bits);	
			
		initPolyLFSR();
		// choose the right truth table and feed the array delay
		initTruthTable();
		initDecaPixels(0.0167,0.0334);
		initDelayArray();
		this.key11bits = key11BitsTab[0];
	}	

	
	/**
	 * create a new Discret11Dec object with redefined percentages for the delay 1 and 2
	 * @param key16bit the 16 bits key word to initialize
	 *  the Discret11Enc object	
	 * @param perc1 the percentage level for delay 1
	 * @param perc2 the percentage level for delay 2
	 */
	public Discret11Enc(int key16bits, String audienceList, int cycle, double perc1, double perc2){
		initAudienceList(audienceList);
		this.cycle = cycle;
		this.key16bits = key16bits;
		this.initKey11BitsTab(key16bits);	
		
		initPolyLFSR();
		// choose the right truth table and feed the array delay
		initTruthTable();
		initDecaPixels(perc1,perc2);
		initDelayArray();
		this.key11bits = key11BitsTab[0];
	}	
	
	private void initAudienceList(String audience){
		this.audienceList = new int[audience.length()];
		for (int i = 0; i < audienceList.length; i++) {
			audienceList[i] = Integer.valueOf((audience.substring(i,i+1 )));
		}
		this.audienceLevel = audienceList[cptCycle];
		this.saveIndexUse11bitsKey = -1;				
		this.index11bitsKey = audienceList[cptCycle] -1;
		this.indexUse11bitsKey = this.index11bitsKey;
		this.key11bits = this.key11BitsTab[this.indexUse11bitsKey];
		cptArray = 0;
		cptPoly = 0;
		this.enable = false;
	}
	
	private void changeAudience(){
		maxCycle++;
		if(maxCycle == this.cycle ){
			maxCycle = 0;
			cptCycle++;
		}
		indexCycle = 0;
		
		System.out.println("taille " + audienceList.length);
		if(cptCycle == audienceList.length){
			System.out.println("raz cptcycle " + cptCycle);
			cptCycle = 0;
		}
		this.audienceLevel = audienceList[cptCycle];
		this.saveIndexUse11bitsKey = indexUse11bitsKey;
		this.indexUse11bitsKey = this.index11bitsKey;		
		this.index11bitsKey = audienceList[cptCycle] -1;
		System.out.println("cptArray " + cptArray + " ");
		//cptArray = 0;
		//cptPoly = 0;
		//this.seqFrame = 0;
		this.enable = true;	
		this.key11bits = this.key11BitsTab[this.indexUse11bitsKey];
		System.out.println("--------seqframe " + this.seqFrame + " " + this.totalFrameCount + " " +
		this.saveIndexUse11bitsKey +
		" " + this.indexUse11bitsKey +
		" " + this.index11bitsKey);
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
	 * initialize the array of LFSR values
	 * @param a tab for the 7 11 bits key words
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
	private void initDecaPixels(double perc1, double perc2){
						
		decaPixels[0] = 0;
		decaPixels[1] = (int)(Math.round(perc1 * this.sWidth )); // previous value : 0.018 0.0167 0.0167
		decaPixels[2] = (int)(Math.round(perc2 * this.sWidth )); // previous value : 0.036 0.0347 0.0334
		
		if (decaPixels[1] == 0){
			decaPixels[1] = 1;
		}
		if (decaPixels[2] == 0 ){
			decaPixels[2] = 2;
		}		
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
	 * of the Discret11 object
	 * @param image the image to be transformed
	 * @return the transformed image
	 */
	public BufferedImage transform(BufferedImage image) {
		totalFrameCount++;
		if (enable){
			System.out.println("++enable " + this.totalFrameCount);
		}
		else
			System.out.println("--disable " + totalFrameCount);
		
		// we check the type image and the size
		image = this.convertToType(image, BufferedImage.TYPE_3BYTE_BGR);
		if (image.getWidth() != this.sWidth || image.getHeight() != 576) {
			image = this.getScaledImage(image, this.sWidth, 576);
		}

		//checkMotif(image);		
		
			//System.out.println("transform : " + totalFrameCount);

			int z = 0;

			switch (this.seqFrame) {
			case 0:
				z = 0;
				break;
			case 1:
				z = 0;
				break;
			case 2:
				z = 0;
				break;
			case 3:
				z = 1;
				break;
			case 4:
				z = 1;
				break;
			case 5:
				z = 1;
				break;

			default:
				break;
			}

			if (this.seqFrame == 0) {
				System.out.println("seqframe 0 ---");
				image = setWhite310Line(image);	
				if (this.saveIndexUse11bitsKey != -1 ) {					
					System.out.println("avant zéro " + this.saveIndexUse11bitsKey + " " +
				 + this.index11bitsKey + " " +this.totalFrameCount);
					int saveKey = this.indexUse11bitsKey;
					this.indexUse11bitsKey = this.saveIndexUse11bitsKey;
					image = modifyOddFrame2(image, 1);
					cptArray = 0;
					cptPoly = 0;
					this.indexUse11bitsKey = saveKey;
				}
				// we compute only the even part of
												// the image
				image = modifyEvenFrame(image, z);
				this.seqFrame++;
				setAudience622Line(image);
				this.currentframePos++;
			} else { // we compute both the odd and even parts of the image (
						// impaire, paire )				
				
				image = modifyOddFrame(image, z);
				this.seqFrame++;

				if (this.seqFrame == 6) {					
					indexCycle +=1;	
					if(indexCycle == 8) {
						this.enable = true;
					}
					image = setWhite310Line(image);	
					this.seqFrame = 0;
					this.cptPoly = 0;
				}
				else{
					image = setBlack310Line(image);	
				}
				
				if(indexCycle ==  8  ){
					System.out.println("un cycle terminé " 
							+ this.seqFrame + " " + indexCycle );
					changeAudience();
				}		
				

				image = modifyEvenFrame(image, z);
				this.seqFrame++;
				setAudience622Line(image);

				this.currentframePos++;

			}

			// System.out.println("retourne image seq " + this.seqFrame +
			// " nb images : " + this.currentframePos );
			//this.checkMotif(image);
	
			return image;		
	}
	
	/**
	 * Transform the lines of the even part of an image ( trame paire )
	 * @param image
	 * @param z the z value for the delay array
	 * @return
	 */
	private BufferedImage modifyEvenFrame(BufferedImage image, int z) {
		if (enable) {
			BufferedImage bi = new BufferedImage(this.sWidth, 576,
					BufferedImage.TYPE_3BYTE_BGR);// img.getType_image());
													// BufferedImage.TYPE_INT_BGR

			Raster raster1 = bi.getRaster();
			WritableRaster raster2 = image.getRaster();

			int temp2 = 0;

			for (int y = 1; y < 576; y++) {
				if (cptArray == 286) {
					this.cptArray = 0;
				}

				temp2 = delayArray[indexUse11bitsKey][this.seqFrame][cptArray];

				if (y != 573 && y != 575) { // we don't increment if next line
											// is 622 ( 574 in
					// digital image ) or if next line is 623 ( 576 in digital
					// image )
					raster2.setPixels(temp2, y, this.sWidth - temp2, 1, raster2
							.getPixels(0, y, this.sWidth - temp2, 1,
									new int[(this.sWidth - temp2) * 3]));
					// draw black line at start of delay
					raster2.setPixels(0, y, temp2, 1, raster1.getPixels(0, y,
							temp2, 1, new int[temp2 * 3]));
					cptPoly++; // we increment the count of poly array
					cptArray++;
				}
				y++; // add one to y in order to have only even lines frame
			}
		}
		return image;
	}
	
	/**
	 * Transform the lines of the odd part of an image ( trame impaire )
	 * @param image
	 * @param z the z value for the delay array
	 * @return
	 */
	private BufferedImage modifyOddFrame(BufferedImage image, int z) {
		if (enable) {
			BufferedImage bi = new BufferedImage(this.sWidth, 576,
					BufferedImage.TYPE_3BYTE_BGR);// img.getType_image());
													// BufferedImage.TYPE_INT_BGR

			Raster raster1 = bi.getRaster();
			WritableRaster raster2 = image.getRaster();

			int temp2 = 0;

			for (int y = 2; y < 576; y++) {
				if (cptArray == 286) {
					this.cptArray = 0;
				}

				if (cptPoly == 1716) {
					cptPoly = 0;
				}
				temp2 = delayArray[indexUse11bitsKey][this.seqFrame][cptArray];

				if (y != 574) { // we don't increment if it's line 310 ( 575 in
					// digital image )
					raster2.setPixels(temp2, y, this.sWidth - temp2, 1, raster2
							.getPixels(0, y, this.sWidth - temp2, 1,
									new int[(this.sWidth - temp2) * 3]));
					// draw black line at start of delay
					raster2.setPixels(0, y, temp2, 1, raster1.getPixels(0, y,
							temp2, 1, new int[temp2 * 3]));
					cptPoly++; // we increment the count of poly array
					cptArray++;
				}
				y++; // add one to y in order to have only odd lines frame
			}
		}
		return image;
	}
	
	/**
	 * Transform the lines of the odd part of an image ( trame impaire )
	 * @param image
	 * @param z the z value for the delay array
	 * @return
	 */
	private BufferedImage modifyOddFrame2(BufferedImage image, int z) {
		if (enable) {
			BufferedImage bi = new BufferedImage(this.sWidth, 576,
					BufferedImage.TYPE_3BYTE_BGR);// img.getType_image());
													// BufferedImage.TYPE_INT_BGR

			System.out.println("odd frame 2");
			Raster raster1 = bi.getRaster();
			WritableRaster raster2 = image.getRaster();

			int temp2 = 0;

			for (int y = 2; y < 576; y++) {
				if (cptArray == 286) {
					this.cptArray = 0;
				}

				if (cptPoly == 1716) {
					cptPoly = 0;
				}
				temp2 = delayArray[indexUse11bitsKey][5][cptArray];

				if (y != 574) { // we don't increment if it's line 310 ( 575 in
					// digital image )
					raster2.setPixels(temp2, y, this.sWidth - temp2, 1, raster2
							.getPixels(0, y, this.sWidth - temp2, 1,
									new int[(this.sWidth - temp2) * 3]));
					// draw black line at start of delay
					raster2.setPixels(0, y, temp2, 1, raster1.getPixels(0, y,
							temp2, 1, new int[temp2 * 3]));
					cptPoly++; // we increment the count of poly array
					cptArray++;
				}
				y++; // add one to y in order to have only odd lines frame
			}
		}
		return image;
	}
	
	/**
	 * we set the color of the 622 line according to the audience level	
	 * @param buff the BufferedImage
	 */
	private void setAudience622Line(BufferedImage buff) {
		switch (this.audienceLevel) {
		case 1:
			if (this.seqFrame == 1) {
				setBlack622Line(buff);
			}
			if (this.seqFrame == 3) {
				setBlack622Line(buff);
			}
			if (this.seqFrame == 5) {
				setWhite622Line(buff);
			}
			break;
		case 2:
			if (this.seqFrame == 1) {
				setBlack622Line(buff);
			}
			if (this.seqFrame == 3) {
				setWhite622Line(buff);
			}
			if (this.seqFrame == 5) {
				setBlack622Line(buff);
			}
			break;
		case 3:
			if (this.seqFrame == 1) {
				setBlack622Line(buff);
			}
			if (this.seqFrame == 3) {
				setWhite622Line(buff);
			}
			if (this.seqFrame == 5) {
				setWhite622Line(buff);
			}
			break;
		case 4:
			if (this.seqFrame == 1) {
				setWhite622Line(buff);
			}
			if (this.seqFrame == 3) {
				setBlack622Line(buff);
			}
			if (this.seqFrame == 5) {
				setBlack622Line(buff);
			}
			break;
		case 5:
			if (this.seqFrame == 1) {
				setWhite622Line(buff);
			}
			if (this.seqFrame == 3) {
				setBlack622Line(buff);
			}
			if (this.seqFrame == 5) {
				setWhite622Line(buff);
			}
			break;
		case 6:
			if (this.seqFrame == 1) {
				setWhite622Line(buff);
			}
			if (this.seqFrame == 3) {
				setWhite622Line(buff);
			}
			if (this.seqFrame == 5) {
				setBlack622Line(buff);
			}
			break;
		case 7:
			if (this.seqFrame == 1) {
				setWhite622Line(buff);
			}
			if (this.seqFrame == 3) {
				setWhite622Line(buff);
			}
			if (this.seqFrame == 5) {
				setWhite622Line(buff);
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * set to white the 310 line
	 * @param buff the BufferedImage
	 * @return a modified BufferedImage with 310 line set to white
	 */
	private BufferedImage setWhite310Line(BufferedImage buff){
		for (int i = 0; i < buff.getWidth(); i++) {
			buff.setRGB(i, 574, new Color(255,255, 255).getRGB());
		}
		return buff;		
	}
	
	/**
	 * set to black the 310 line
	 * @param buff the BufferedImage
	 * @return a modified BufferedImage with 310 line set to black
	 */
	private BufferedImage setBlack310Line(BufferedImage buff){
		for (int i = 0; i < buff.getWidth(); i++) {
			buff.setRGB(i, 574, new Color(0, 0, 0).getRGB());
		}
		return buff;		
	}
	
	/**
	 * set to black the 622 line
	 * @param buff the BufferedImage
	 * @return a modified BufferedImage with 622 line set to black
	 */
	private BufferedImage setBlack622Line(BufferedImage buff){
		for (int i = 0; i < buff.getWidth(); i++) {
			buff.setRGB(i, 573, new Color(0, 0, 0).getRGB());
		}
		return buff;		
	}
	
	/**
	 * set to white the 622 line
	 * @param buff the BufferedImage
	 * @return a modified BufferedImage with 622 line set to white
	 */
	private BufferedImage setWhite622Line(BufferedImage buff){
		for (int i = 0; i < buff.getWidth(); i++) {
			buff.setRGB(i, 573, new Color(255,255, 255).getRGB());
		}
		return buff;		
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
	
	/**
	 * Scale an image to a new size
	 * @param src the image source
	 * @param w the new width
	 * @param h the new height
	 * @return the resized image
	 */
	private BufferedImage getScaledImage(BufferedImage src, int w, int h){
	    int finalw = w;
	    int finalh = h;
	    double factor = 1.00d;
	    double shiftw = 1d;	 
	    
	    if(src.getWidth()==720 && src.getHeight()==576 ){
	    	shiftw = (double)src.getWidth()/(double)w; // case of if width = 720 and height = 576
	    }
	    
	    if(src.getWidth() > src.getHeight()){
	        factor = ((double)src.getHeight()/(double)src.getWidth());
	        finalh = (int)(finalw * factor * shiftw);                
	    }else{
	        factor = ((double)src.getWidth()/(double)src.getHeight());
	        finalw = (int)(finalh * factor  );
	    }   

	    BufferedImage resizedImg = new BufferedImage(finalw, finalh, BufferedImage.TRANSLUCENT);
	    Graphics2D g2 = resizedImg.createGraphics();
	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(src, 0, 0, finalw, finalh, null);
	    g2.dispose();
	    
	    //step 2 create a bufferedimage with exact size
	    
	    BufferedImage target = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
	    
	    Graphics g3 = target.getGraphics();	    
	    g3.drawImage(resizedImg, 0, (target.getHeight() - resizedImg.getHeight())/2, null);
		    
	    return target;
	}	
	
	public boolean isEnable(){
		return this.enable;
	}
	
	public int getAudienceLevel() {
		return audienceLevel;
	}

	public void setAudienceLevel(int audienceLevel) {
		this.audienceLevel = audienceLevel;
	}

	public int getKey11bits() {
		return this.key11BitsTab[index11bitsKey];
	}

	public int getCurrentframePos() {
		return currentframePos;
	}

	public int getSeqFrame() {
		return seqFrame;
	}

	public int[] getDecaPixels() {
		return decaPixels;
	}


	public int getKey16bits() {
		return key16bits;
	}


	public void setKey16bits(int key16bits) {
		this.key16bits = key16bits;
	}


	public int[] getAudienceList() {
		return audienceList;
	}


	public void setAudienceList(int[] audienceList) {
		this.audienceList = audienceList;
	}


	public int getCycle() {
		return cycle;
	}


	public void setCycle(int cycle) {
		this.cycle = cycle;
	}		

}
