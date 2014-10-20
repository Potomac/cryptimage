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
 * 6 oct. 2014 Author Mannix54
 */



package com.ib.cryptimage.core;


import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
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
	/**
	 * the current image processed by the Direct11 object
	 */
	private BufferedImage imgRef;	
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
	 * @param key11bit the 11 bits key word to initialize the Discret11 object ( 1-2047 )
	 * @param mode the operational mode ( 0 for encoding, 1 for decoding )
	 * @param height the height of the image
	 * @param width the width of the image
	 */
	public SimpleDiscret11(int key11bits, int mode, int height, int width){		
		this.key11bits = key11bits;
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
	 * @param key11bit the 11 bits key word to initialize the Discret11 object ( 1-2047 )
	 * @param mode the operational mode ( 0 for encoding, 1 for decoding )
	 * @param height the height of the image
	 * @param width the width of the image
	 * @param perc1 the percentage level for delay 1
	 * @param perc2 the percentage level for delay 2
	 */
	public SimpleDiscret11(int key11bits, int mode, int height, int width,
			double perc1, double perc2){		
		this.key11bits = key11bits;		
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
		this.imgRef = image;
		
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
			this.cptPoly = 0;
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
	    double factor = 1.0d;
	    if(src.getWidth() > src.getHeight()){
	        factor = ((double)src.getHeight()/(double)src.getWidth());
	        finalh = (int)(finalw * factor);                
	    }else{
	        factor = ((double)src.getWidth()/(double)src.getHeight());
	        finalw = (int)(finalh * factor);
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
	
//	private void debugFrame(){
//		String bloque = "P yes";
//		
//		if(Debug.stop){
//			bloque = "P no ";
//		}
//		
//		String message = "trame " +  (Debug.seqFrame +1) + "; ligne " 
//				+ String.format("%03d",(Debug.digitalLine + 1)) + "; analog "
//				+ String.format("%03d",convertLine(Debug.digitalLine + 1)) 
//				+ "; (P" + String.format("%04d",Debug.lfsr)
//				+ "); " + bloque
//				+ " ; (retard: " + Debug.delayPixels  
//				+"); LFSR " + String.format("%04d",Debug.poly) 
//				+ " 0x" + Integer.toHexString(Debug.poly)
//				+ "; z: " + Debug.z
//				+"); LFSR2 " + String.format("%04d",
//						poly[Debug.seqFrame *286 + Debug.cptArray]) 
//				+ " 0x" + Integer.toHexString(
//						poly[Debug.seqFrame *286 + Debug.cptArray]) 
//				+ "; cptArray " + Debug.cptArray;
//		this.sDebugLines += message +" \r\n";
//				//System.out.println(message);
//	}
	
	private int inverseWord(int val){
		String word = String.format
				("%11s", Integer.toBinaryString(val)).replace(" ", "0");
		word = new StringBuilder(word).reverse().toString();
		return Integer.parseInt(word,2);
	}

	/**
	 * return a message about debug infos of all modified lines for frames
	 * who have been computed by the Discret11 object
	 * @return a string message about debug infos on modified lines
	 */
	public String getsDebugLines() {
		return sDebugLines;
	}
	
	/**
	 * convert a 576i line to his analog value
	 * @param frame_line the line number of a 576i image
	 * @return the analog line equivalent
	 */
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
	
	/**
	 * clone a BufferedImage
	 * @param bi the BufferedImage to clone
	 * @return a cloned BufferedImage
	 */
	private BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
//	final static class Debug{
//		private static int cptArray = 0;
//		private static int lfsr = 0;
//		private static int poly = 0;
//		private static int delayPixels = 0;
//		private static int digitalLine = 0;	
//		private static int z = 0;
//		private static boolean stop = false;
//		private static int seqFrame = 0;
//		private static int nbDebug = 0;
//	
//	}	
	
}