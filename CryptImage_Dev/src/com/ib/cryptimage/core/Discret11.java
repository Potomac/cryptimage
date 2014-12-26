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
 * 28 sept. 2014 Author Mannix54
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
public class Discret11 {
	
	/**
	 * store the 11 bits key word
	 */
	private int key11bits;
	/**
	 * true if encoding, false otherwise
	 */
	private boolean isEnc;
	/**
	 * true if decoding, false otherwise
	 */
	private boolean isDec;
	/**
	 * store the current audience level
	 */
	private int audienceLevel;
	
	/**
	 * store the 7 11 bit words according to the audience level
	 */
	private int[] key11BitsTab = new int[7];
	
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
	private int[] poly = new int[1716];
	/**
	 * store the number of frames that has been transformed by Discret11
	 */
	private int currentframePos = 0;
	/**
	 * store the current position from the 6 frames ( half image ) sequence
	 */
	private int seqFrame = 0;
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
	private int[][] delayArray = new int[6][286];
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
	 * a string for storing a debug message about the modified lines in a trame
	 */
	private String sDebugLines = "";
	
//	/**
//	 * the current image processed by the Direct11 object
//	 */
//	private BufferedImage imgRef;
	/**
	 * the default width of the image which is 768
	 */
	private final int sWidth = 768;
	
	
	/**
	 * create a new Discret11 object
	 * @param key11bit the 16 bits key word to initialize the Discret11 object
	 * @param mode the operational mode ( 0 for encoding, 1 for decoding )
	 * @param audienceLevel the audience Level ( 1 to 7 )
	 */
	public Discret11(int key16bits, int mode, int audienceLevel){		
		this.initKey11BitsTab(key16bits);			
		this.key11bits = this.key11BitsTab[audienceLevel - 1];
		
		this.audienceLevel = audienceLevel;
		initMode(mode);
		initPolyLFSR(key11bits);
		// choose the right truth table and feed the array delay
		initTruthTable();
		initDecaPixels(0.0167,0.0334);
		initDelayArray();
	}	

	
	/**
	 * create a new Discret11 object with redefined percentages for the delay 1 and 2
	 * @param key16bit the 16 bits key word to initialize the Discret11 object
	 * @param mode the operational mode ( 0 for encoding, 1 for decoding )
	 * @param audienceLevel the audience Level ( 1 to 7 )
	 * @param perc1 the percentage level for delay 1
	 * @param perc2 the percentage level for delay 2
	 */
	public Discret11(int key16bits, int mode, int audienceLevel, 
			double perc1, double perc2){		
		this.initKey11BitsTab(key16bits);			
		this.key11bits = this.key11BitsTab[audienceLevel - 1];
		
		this.audienceLevel = audienceLevel;
		initMode(mode);
		initPolyLFSR(key11bits);
		// choose the right truth table and feed the array delay
		initTruthTable();
		initDecaPixels(perc1,perc2);
		initDelayArray();
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
	 * initialize the delay array for
	 * the 6 TV frames
	 */
	private void initDelayArray(){
		int z = 0;
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 286; j++) {
				if(i == 0 || i == 1 || i == 2){
					z = 0;					
				}
				else{
					z = 1;
				}
				delayArray[i][j] = decaPixels[getDelay(poly[(i*286) +j], z)];
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
	public BufferedImage transform(BufferedImage image){
				
		//we check the type image and the size
		image = this.convertToType(image, BufferedImage.TYPE_3BYTE_BGR);
		if(image.getWidth() != this.sWidth || image.getHeight() != 576){
			image = this.getScaledImage(image, this.sWidth, 576);
		}
//		this.imgRef = image;
		
		int z = 0;
		//this.seqFrame++;	

		
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
		
		if (this.currentframePos == 0){ // we compute only the even part of the image
			image = modifyEvenFrame(image, z);			
			this.seqFrame++;						
			this.currentframePos++;			
		}
		else{ // we compute both the odd and even parts of the image ( impaire, paire )	
			image = modifyOddFrame(image, z);
			this.seqFrame++;			
			setAudience622Line(image);
			if(this.seqFrame == 6 ){
				setWhite310Line(image);				
				this.seqFrame = 0;
				this.cptPoly = 0;
			}			
			else {
				image = setBlack310Line(image);
			}
				

			image = modifyEvenFrame(image, z);			
			this.seqFrame++;
			setAudience622Line(image);
	
			this.currentframePos++;
		
		}

		//System.out.println("retourne image seq " + this.seqFrame + " nb images : " + this.currentframePos );
		return image;
	}
	
	/**
	 * Transform the lines of the even part of an image ( trame paire )
	 * @param image
	 * @param z the z value for the delay array
	 * @return
	 */
	private BufferedImage modifyEvenFrame(BufferedImage image, int z){	
		BufferedImage bi = new BufferedImage(this.sWidth,576,
				BufferedImage.TYPE_3BYTE_BGR);// img.getType_image());
											// BufferedImage.TYPE_INT_BGR			
		
		Raster raster1 = bi.getRaster();
		WritableRaster raster2 = image.getRaster();		
		
		int temp2 = 0;
		
		for (int y = 1; y < 576; y++) {
			if(cptArray == 286){
				this.cptArray = 0;
			}
			
			temp2 = delayArray[this.seqFrame][cptArray];
			

			if (y != 573 && y != 575) { // we don't increment if next line is 622 ( 574 in
				// digital image ) or if next line is 623 ( 576 in digital image )
				raster2.setPixels(temp2 , y, this.sWidth
						- temp2 , 1, raster2.getPixels(0,
						y, this.sWidth - temp2 , 1,
						new int[(this.sWidth - temp2) * 3]));
				//draw black line at start of delay
				raster2.setPixels(0, y, temp2 , 1, raster1.getPixels(0,
						y, temp2 , 1,
						new int[temp2  * 3]));							
				cptPoly++; // we increment the count of poly array
				cptArray++;
			}
			y++; // add one to y in order to have only even lines frame
		}		
		return image;		
	}
	
	/**
	 * Transform the lines of the odd part of an image ( trame impaire )
	 * @param image
	 * @param z the z value for the delay array
	 * @return
	 */
	private BufferedImage modifyOddFrame(BufferedImage image, int z){	
		BufferedImage bi = new BufferedImage(this.sWidth,576,
				BufferedImage.TYPE_3BYTE_BGR);// img.getType_image());
											// BufferedImage.TYPE_INT_BGR		
		
		Raster raster1 = bi.getRaster();
		WritableRaster raster2 = image.getRaster();	

		int temp2 = 0;	

	
		for (int y = 2; y < 576; y++) {
			if(cptArray == 286){
				this.cptArray = 0;
			}
			
			if(cptPoly == 1716){
				cptPoly = 0;				
			}			
			temp2 = delayArray[this.seqFrame][cptArray];

			if (y != 574) { // we don't increment if it's line 310 ( 575 in
				// digital image )
				raster2.setPixels(temp2 , y, this.sWidth
						- temp2 , 1, raster2.getPixels(0,
						y, this.sWidth - temp2, 1,
						new int[(this.sWidth - temp2 ) * 3]));
				//draw black line at start of delay
				raster2.setPixels(0, y, temp2 , 1, raster1.getPixels(0,
						y, temp2 , 1,
						new int[temp2  * 3]));				
				cptPoly++; // we increment the count of poly array
				cptArray++;
			}
			y++; // add one to y in order to have only odd lines frame
		}
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
//	    else if(src.getWidth()==768 && src.getHeight() == 576 && w == 720){
//	    	shiftw = (double)src.getWidth()/720d;	    	
//	    	//shiftw = 768d/(double)src.getWidth();
//	    	//finalw = (int)(finalh * shiftw);
//	    }
//	    else if(src.getWidth()==720 && src.getHeight()!=576 && w == 720){
//	    	//shiftw = (double)src.getWidth()/768d;	    	
//	    	shiftw = 768d/(double)src.getWidth();
//	    	//finalw = (int)(finalh * shiftw);
//	    }
//	    else if(w ==720){
//	    	//shiftw = (double)src.getWidth()/768d;	    	
//	    	shiftw = 768d/720d;
//	    	//finalw = (int)(finalh * shiftw);
//	    }
	    
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
	 * return a message about debug infos of all modified lines for frames
	 * who have been computed by the Discret11 object
	 * @return a string message about debug infos on modified lines
	 */
	public String getsDebugLines() {
		return sDebugLines;
	}	
	
	public int getAudienceLevel() {
		return audienceLevel;
	}

	public void setAudienceLevel(int audienceLevel) {
		this.audienceLevel = audienceLevel;
	}

	public int getKey11bits() {
		return this.key11bits;
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
		return seqFrame;
	}

	public int[] getDecaPixels() {
		return decaPixels;
	}		

}



