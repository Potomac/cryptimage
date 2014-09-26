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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.Raster;


//import java.util.Random;

public class CryptImage {
	
	private ImageRef img;	
	private int deca[] = new int[0];
	private String sDecaList = "";
	private int posFrame;
	private int discret11Word;
	private int[][] delayArrayCrypt;
	private boolean strictMode;	
	private int[] arrayShift;
	private int audienceLevel;
	private boolean isDecoding;
	
	/**
	 * constructor
	 * @param img the buffered image to crypt, an ImageRef object
	 * @param posFrame the frame position in sequence ( 1, 2 or 3 )
	 * @param strict use the "discret11 strict mode", image will be resized to 768x576 pixels
	 */
	public CryptImage(BufferedImage buffImg, int posFrame,
			boolean strict, int audienceLevel, boolean isDecoding) {
		this.isDecoding = isDecoding;
		if (strict == true){
			this.strictMode = strict;				
			this.img = new ImageRef(buffImg);			
			this.arrayShift = new int[576];
			this.audienceLevel = audienceLevel;
		}
		else {
			this.strictMode = strict;
			this.img = new ImageRef(buffImg);
			this.arrayShift = new int[buffImg.getHeight()];
		}		
		this.posFrame = posFrame;
		this.discret11Word = 0;		
	}
	
	/**
	 * appel du cryptage discret11 sans paramètres définis de décalages
	 * @param discret11Word the 11 bits discret11 word to use for crypting
	 * @return un BufferedImage crypté en discret11
	 */
	public BufferedImage getCryptDiscret11(int discret11Word){
		this.discret11Word = discret11Word;
		
		if (this.deca.length == 0 && this.delayArrayCrypt == null) {
			this.setDeca();
	
			this.delayArrayCrypt = new DelayArray(this.img.getHeight(),
					this.discret11Word, strictMode, this.isDecoding).getDelayArray();
		
			for (int i = 0; i < arrayShift.length; i++) {
				sDecaList += String.format("%02d", deca[this.delayArrayCrypt[this.posFrame - 1][i]]) + " ";
			}			
		}
		
		for (int i = 0; i < img.getHeight(); i++) {
			arrayShift[i] = deca[this.delayArrayCrypt[this.posFrame - 1][i]];
		}
		
		return getCryptDiscret11Param(arrayShift);
	}
	
	/**
	 * Crypt an image with a discret11 algorithm
	 * @return a discret 11 bufferedImage
	 * @param tabDeca the decay tab in pixels
	 */
	/**
	 * @return
	 */
	private BufferedImage getCryptDiscret11Param(int[] tabDeca){
		
		if(this.strictMode){
			setAudience622Line(this.audienceLevel,this.posFrame, this.img.getImg());
		}
		
		if(this.strictMode && ( this.posFrame == 1)){
			setWhite310Line(this.img.getImg());			
		}
		
		if(this.strictMode && ( this.posFrame == 2)){
			setBlack310Line(this.img.getImg());			
		}
		
		if(this.strictMode && ( this.posFrame == 3)){
			setBlack310Line(this.img.getImg());			
		}		
		
		return this.getDelayLines(tabDeca);		
	}
	
	private BufferedImage getDelayLines(int[] tabDeca) {
		// image cible résultant de la modification
		BufferedImage bi = new BufferedImage(img.getWidth(), img.getHeight(),
				BufferedImage.TYPE_3BYTE_BGR);// img.getType_image());
											// BufferedImage.TYPE_INT_BGR
				
		this.img.setImg(convertToType(this.img.getImg(), BufferedImage.TYPE_3BYTE_BGR));

		Raster raster1 = img.getImg().getRaster();
		WritableRaster raster2 = bi.getRaster();
		
		
//
//		// long temps1 = System.currentTimeMillis();
		int shift = 0;
		for (int y = 0; y < img.getHeight(); y++) {
			shift = 0;
			if (this.isDecoding && tabDeca[y] == this.deca[1]) {
				shift = (this.deca[2] - this.deca[1]) - this.deca[1];
			}
			raster2.setPixels(tabDeca[y] + shift, y, img.getWidth()
					- tabDeca[y] - shift, 1, raster1.getPixels(0, y,
							img.getWidth() - tabDeca[y] - shift, 1,
					new int[(img.getWidth() - tabDeca[y]- shift)*3 ]));
		}		
	
//		// long temps2 = System.currentTimeMillis();
		return bi;
	}
	
	
	/**
	 * set the 3 shift pixels value for deca[]
	 */
	private void setDeca(){
		deca = new int[3];
		deca[0] = 0;
		deca[1] = (int)(Math.round(0.0167 * img.getWidth())); // previous value : 0.018
		deca[2] = (int)(Math.round(0.0347 * img.getWidth())); // previous value : 0.036
		
		if (deca[1] == 0){
			deca[1] = 1;
		}
		if (deca[2] == 0 ){
			deca[2] = 2;
		}
		
	}
	
	
	/**
	 * Decrypt a discret11 image by using a discret11 word
	 */
	public BufferedImage getDecryptDiscret11WithCode(int discret11Word){
		
		this.discret11Word = discret11Word;

		if (this.deca.length == 0 && this.delayArrayCrypt == null) {
			this.setDeca();
			this.discret11Word = discret11Word;
			sDecaList = "";

			this.delayArrayCrypt = new DelayArray(this.img.getHeight(),
					this.discret11Word, this.strictMode, this.isDecoding).getDelayArray();

			for (int i = 0; i < arrayShift.length; i++) {
				sDecaList += String.format("%02d", deca[this.delayArrayCrypt[this.posFrame - 1][i]]) + " ";
			}
		}

		// int[] tabShift = new int[img.getHeight()];

		for (int i = 0; i < img.getHeight(); i++) {
			arrayShift[i] = deca[this.delayArrayCrypt[this.posFrame - 1][i]];
		}			
	
		return getDecryptDiscret11(arrayShift);
	}
	
	/**
	 * decrypt an image who has been previously crypted in discret11
	 * @param sDeca the file who stores the shift numbers used for the encryption
	 * @return a decrypted image
	 */
	public BufferedImage getDecryptDiscret11(int[] tDeca){	
		
		return getDelayLines(tDeca);		
	}
	
	public BufferedImage getScaledImage(BufferedImage src, int w, int h){
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
	
	private void setAudience622Line(int audience, int posFrame,
			BufferedImage buff) {
		switch (audience) {
		case 1:
			if (posFrame == 1) {
				setWhite622Line(buff);
			}
			if (posFrame == 2) {
				setBlack622Line(buff);
			}
			if (posFrame == 3) {
				setBlack622Line(buff);
			}
			break;
		case 2:
			if (posFrame == 1) {
				setBlack622Line(buff);
			}
			if (posFrame == 2) {
				setWhite622Line(buff);
			}
			if (posFrame == 3) {
				setBlack622Line(buff);
			}
			break;
		case 3:
			if (posFrame == 1) {
				setWhite622Line(buff);
			}
			if (posFrame == 2) {
				setWhite622Line(buff);
			}
			if (posFrame == 3) {
				setBlack622Line(buff);
			}
			break;
		case 4:
			if (posFrame == 1) {
				setBlack622Line(buff);
			}
			if (posFrame == 2) {
				setBlack622Line(buff);
			}
			if (posFrame == 3) {
				setWhite622Line(buff);
			}
			break;
		case 5:
			if (posFrame == 1) {
				setWhite622Line(buff);
			}
			if (posFrame == 2) {
				setBlack622Line(buff);
			}
			if (posFrame == 3) {
				setWhite622Line(buff);
			}
			break;
		case 6:
			if (posFrame == 1) {
				setBlack622Line(buff);
			}
			if (posFrame == 2) {
				setWhite622Line(buff);
			}
			if (posFrame == 3) {
				setWhite622Line(buff);
			}
			break;
		case 7:
			if (posFrame == 1) {
				setWhite622Line(buff);
			}
			if (posFrame == 2) {
				setWhite622Line(buff);
			}
			if (posFrame == 3) {
				setWhite622Line(buff);
			}
			break;
		default:
			break;
		}
	}

	private BufferedImage setWhite310Line(BufferedImage buff){
		for (int i = 0; i < buff.getWidth(); i++) {
			buff.setRGB(i, 574, new Color(255,255, 255).getRGB());
		}
		return buff;		
	}
	
	private BufferedImage setBlack310Line(BufferedImage buff){
		for (int i = 0; i < buff.getWidth(); i++) {
			buff.setRGB(i, 574, new Color(0, 0, 0).getRGB());
		}
		return buff;		
	}
	
	private BufferedImage setBlack622Line(BufferedImage buff){
		for (int i = 0; i < buff.getWidth(); i++) {
			buff.setRGB(i, 573, new Color(0, 0, 0).getRGB());
		}
		return buff;		
	}
	
	private BufferedImage setWhite622Line(BufferedImage buff){
		for (int i = 0; i < buff.getWidth(); i++) {
			buff.setRGB(i, 573, new Color(255,255, 255).getRGB());
		}
		return buff;		
	}
	
	

	/**
	 * get the deca list used to crypt the image
	 * @return a list of value used to shift each line of an image
	 */
	public String getsDecaList() {
		return sDecaList;
	}
	
	/**
	 * get the delay list for a specific frame ( 1,2 or 3 frames )
	 * @return a list of delay used to shift each line of a specific frame
	 */
	public String getDelayArrayAtFrame(int frame) {
		String sDelayFrame ="";
		for (int i = 0; i < this.img.getHeight(); i++) {
			sDelayFrame +=String.format("%02d",delayArrayCrypt[frame-1][i]) + " ";
		}		
		return sDelayFrame;
	}
	
	/**
	 * get the 3 different shift values
	 * @return the 3 different shift values
	 */
	public String getShiftValues(){
		String values = "";
		for (int i=0;i<deca.length;i++){
			values += String.valueOf(deca[i]) + " ";
		}
		return values;
	}
	
	/**
	 * get the ImageRef
	 * @return the ImageRef
	 */
	public ImageRef getImgRef() {
		return this.img;
	}
	
	public int[][] getDelayTabCrypt() {
		return delayArrayCrypt;
	}

	public int getDiscret11Word() {
		return discret11Word;
	}

	public void setDiscret11Word(int discret11Word) {
		this.discret11Word = discret11Word;
	}

	public int getPosFrame() {
		return posFrame;
	}

	public void setPosFrame(int posFrame) {
		this.posFrame = posFrame;
	}	


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
	
	
}
