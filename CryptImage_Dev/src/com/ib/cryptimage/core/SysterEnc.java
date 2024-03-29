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
 * 23 nov. 2015 Author Mannix54
 */



package com.ib.cryptimage.core;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;


/**
 * @author Mannix54
 *
 */
public class SysterEnc extends Syster {	
	
	private Vector<Integer> vOffset = new Vector<Integer>();
	private Vector<Integer> vIncrement = new Vector<Integer>();
	WritableRaster raster;
	WritableRaster raster2;
	BufferedImage newImage;
	BufferedImage frame;
	private boolean bPreviewMode;

	public SysterEnc(int typeTable,  String nameDataFile,
			String nameManualEncodingFile, boolean bPreviewMode) {
		super(typeTable,true);
		this.bPreviewMode = bPreviewMode;
		try {
			if(!bPreviewMode){				
				fileOut = new FileWriter(nameDataFile + ".dec");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(JobConfig.isWantSysterEncRandom() == false ){
			try {
				fileInBuff = new BufferedReader(new FileReader(nameManualEncodingFile));
				this.fileInReader = new FileReader(nameManualEncodingFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	public void crypt(){
		int i,j;
		
		for (i = 0; i < 287; i++)
			order[i] = -1; // Initialisation

		for (i = 0; i < 287; i++)
		{
			j = permut[i];
			while (order[j] != -1)
				j = order[j]+32;
			order[j] = i;
		}
	}
	
	/**
	 * crypt an image that have been added 
	 * 
	 * @param image
	 *            the image to be transformed	
	 */
	public BufferedImage transform(BufferedImage image) {
		numFrames++;		
		JobConfig.incrementPalFrame();
		JobConfig.incrementPalFrameDec();
		
		image = this.convertToType(image, BufferedImage.TYPE_3BYTE_BGR);
		if (image.getWidth() != this.sWidth || image.getHeight() != 576) {
			image = this.getScaledImage(image, this.sWidth, 576);
		}
		
		JobConfig.setInputImage(image);
		
		//check shift X and Y
		if(shiftX != 0 || shiftY !=0) {
			image = shift.transform(image, shiftX, shiftY);
		}

		if(JobConfig.frameCount <= JobConfig.getSysterSelectedFrameEnd()) {
			if(JobConfig.getColorMode() == 1){			
				palEngine.setImg(image);
				image = palEngine.encode();
			}
			
			if(JobConfig.getColorMode() == 2){			
				secamEngine.setImg(image);
				image = secamEngine.encode();			
			}
			
			//encode image to pal composite
			if (JobConfig.getColorMode() == 3 || JobConfig.getColorMode() == 4 ) {
				palEncoder.setImage(image);
				image = palEncoder.encode(false);
			}
			
			if (JobConfig.isWantSysterEncRandom() == false) {
				this.readFileData();			
				this.offset = this.offSetOdd;
				this.increment = this.incrementOdd;
			} else {
				this.genOffsetIncrement();
			}
			
			this.initPermut();
			this.crypt();
			
			this.cryptOddFrame(image);
			
			if (JobConfig.isWantSysterEncRandom() == false ) {			
					this.offset = this.offSetEven;
					this.increment = this.incrementEven;			
			} else {
				if(JobConfig.isOffsetIncrementChange()){
					this.genOffsetIncrement();
				}
				
			}
			
			this.initPermut();
			this.crypt();
			
			this.cryptEvenFrame(image);		
			
			return this.getCompletFrame();			
		}
		else {
			this.skipFrame();
			return image;
		}
		

	}
	
	public BufferedImage transformPhoto(BufferedImage image, int offset1, 
			int increment1, int offset2, int increment2) {
		numFrames++;		
		JobConfig.incrementPalFrame();
		image = this.convertToType(image, BufferedImage.TYPE_3BYTE_BGR);
		if (image.getWidth() != this.sWidth || image.getHeight() != 576) {
			image = this.getScaledImage(image, this.sWidth, 576);
		}
		
		JobConfig.setInputImage(image);
		
		//check shift X and Y
		if(shiftX != 0 || shiftY !=0) {
			image = shift.transform(image, shiftX, shiftY);
		}
			
		this.offset = offset1;
		this.increment = increment1;		
		
		this.initPermut();
		this.crypt();
		
		if(JobConfig.getColorMode() == 1){			
			palEngine.setImg(image);
			image = palEngine.encode();			
		}
		
		if(JobConfig.getColorMode() == 2){			
			secamEngine.setImg(image);
			image = secamEngine.encode();			
		}
		
		//encode image to pal composite
		if (JobConfig.getColorMode() == 3 || JobConfig.getColorMode() == 4 ) {
			palEncoder.setImage(image);
			image = palEncoder.encode(false);
		}
			
		this.cryptOddFrame(image);
		
		this.offset = offset2;
		this.increment = increment2;
		
		this.initPermut();
		this.crypt();
		
		this.cryptEvenFrame(image);
				
		return this.getCompletFrame();
	}
	
	/**
	 * crypt an image that have been added 
	 * 
	 * @param image
	 *            the image to be transformed	
	 */
	public BufferedImage transformPhoto(BufferedImage image, boolean readFile) {
		numFrames++;		
		JobConfig.incrementPalFrame();
		image = this.convertToType(image, BufferedImage.TYPE_3BYTE_BGR);
		if (image.getWidth() != this.sWidth || image.getHeight() != 576) {
			image = this.getScaledImage(image, this.sWidth, 576);
		}
		
		JobConfig.setInputImage(image);
				
		//check shift X and Y
		if(shiftX != 0 || shiftY !=0) {
			image = shift.transform(image, shiftX, shiftY);
		}
		
		if(JobConfig.getColorMode() == 1){
			palEngine.setImg(image);
			image = palEngine.encode();
		}
		
		if (JobConfig.getColorMode() == 2) {
			secamEngine.setImg(image);
			image = secamEngine.encode();
		}
		
		//encode image to pal composite
		if (JobConfig.getColorMode() == 3 || JobConfig.getColorMode() == 4 ) {
			palEncoder.setImage(image);
			image = palEncoder.encode(false);
		}
		
		if (readFile == true) {
			this.readFileData();			
			this.offset = this.offSetOdd;
			this.increment = this.incrementOdd;
		}
		
		this.initPermut();
		this.crypt();
		
		this.cryptOddFrame(image);		
		
		//ajout
		if (readFile == true) {
			this.offset = this.offSetEven;
			this.increment = this.incrementEven;	
		}
		
		this.initPermut();
		this.crypt();
		
		this.cryptEvenFrame(image);
				
		return this.getCompletFrame();
	}
	
	
	
	
	private void cryptOddFrame(BufferedImage image){
		raster2 = image.getRaster(); //deepCopy(image).getRaster();
		newImage = new BufferedImage(this.sWidth, 288, BufferedImage.TYPE_3BYTE_BGR);
		raster = newImage.getRaster();
		
				
		for (int i = 0; i < order.length; i++) {
			raster.setPixels(0, i, this.sWidth, 1, raster2
					.getPixels(0, (order[i])*2 , this.sWidth, 1,
							new int[sWidth * 3]));			
		}
		raster.setPixels(0, 287, sWidth, 1, raster2.getPixels(0, 574, sWidth,1,new int[sWidth * 3]));
		
		if (JobConfig.isWantSysterTags()){
			tagLine();
		}
		
		manageVectorFrame(newImage);		
	}
	
	private void cryptEvenFrame(BufferedImage image){
        raster2 = image.getRaster(); //deepCopy(image).getRaster();
		newImage = new BufferedImage(this.sWidth, 288, BufferedImage.TYPE_3BYTE_BGR);
		raster = newImage.getRaster();
				
		
		for (int i = 0; i < order.length; i++) {
			raster.setPixels(0, i, this.sWidth, 1, raster2
					.getPixels(0, (order[i])*2 +1, this.sWidth, 1,
							new int[sWidth * 3]));			
		}
		
		raster.setPixels(0, 287, sWidth, 1, raster2.getPixels(0, 575, sWidth,1,new int[sWidth * 3]));
		
		if (JobConfig.isWantSysterTags()){
			tagLine();
		}
		
		manageVectorFrame(newImage);
	}
	
	private void manageVectorFrame(BufferedImage image){
		if(vecFrame.size()==3){
			createSysterFrame();
		}
		if(numFrames > 1){
			vOffset.addElement(this.getOffset());
			vIncrement.addElement(this.getIncrement());
		}
		vecFrame.addElement(image);
	}
	
	private void createSysterFrame(){			
		// frame 1
		// push 32-287 lines to the top
		raster = vecFrame.get(0).getRaster();
		raster.setPixels(0, 0, this.sWidth, 255, raster.getPixels(0, 32, sWidth, 255, new int[sWidth * 3 * 255]));

		// push 1-32 lines from frame 2 to frame 1 256-287
		raster.setPixels(0, 255, this.sWidth, 32, vecFrame.get(1).getRaster().getPixels(0, 0, sWidth, 32, new int[sWidth * 3 * 32]));
				
		//frame 2
		//push 32-287 lines to the top
		raster = vecFrame.get(1).getRaster();
		raster.setPixels(0, 0, this.sWidth, 255, raster.getPixels(0, 32, sWidth, 255, new int[sWidth * 3 * 255]));
		
		
		// push 1-32 lines from frame 3 to frame 2 256-287			
		raster.setPixels(0, 255, this.sWidth, 32, vecFrame.get(2).getRaster().getPixels(0, 0, sWidth, 32, new int[sWidth * 3 * 32]));
		
		//create new crypted frame from frame 1 and 2
		frame = new BufferedImage(this.sWidth, 576, BufferedImage.TYPE_3BYTE_BGR);
		raster = frame.getRaster();
		
		int line = 0;
		for (int i = 0; i < 288; i++) {
			raster.setPixels(0, line, this.sWidth, 1,
					vecFrame.get(0).getRaster()
					.getPixels(0, i, this.sWidth, 1, new int[sWidth * 3]));
			line++;
			line++;
		}
		
		line = 1;
		for (int i = 0; i < 288; i++) {
			raster.setPixels(0, line, this.sWidth, 1,
					vecFrame.get(1).getRaster()
					.getPixels(0, i, this.sWidth, 1, new int[sWidth * 3]));
			line++;
			line++;
		}
		
		// remove frame 1 and 2 from vecFrame
		vecFrame.remove(0);
		vecFrame.remove(0);
		this.completFrame = frame;
		this.ready = true;		
			
		if(vOffset.size() == 3 && !this.bPreviewMode){
			this.feedFileData();
		}
	}
	
	private void tagLine(){
		String typeTable = String.format
				("%2s", Integer.toBinaryString(this.getTypeTable())).replace(" ", "0");
		String offsetBin = String.format
				("%8s", Integer.toBinaryString(this.offset)).replace(" ", "0");
		String incrementBin = String.format
				("%8s", Integer.toBinaryString(this.increment)).replace(" ", "0");
		
		int[] black, white = new int[3];
		
		if(JobConfig.getColorMode() == 1){
			black = new int[] {0,128,128};
			white = new int[] {255,128,128};
		}
		else {
			black = new int[] {0,0,0};
			white = new int[] {255,255,255};
		}
			

		int incre = 0;
		int nbPixels = 8;
		
		//tag type table
		for (int i = 0; i < typeTable.length(); i++) {
			if(typeTable.charAt(i) == '0'){
				for (int j = 0; j < nbPixels; j++) {
				raster.setPixel( incre, 287, black);
				incre++;
				}

			}
			else{
				for (int j = 0; j < nbPixels; j++) {
				raster.setPixel(incre, 287, white);
				incre++;
				}
			}		
		}
		
		incre = 0;
		
		//tag offset		
		for (int i = 0; i < offsetBin.length(); i++) {
			if(offsetBin.charAt(i) == '0'){
				for (int j = 0; j < nbPixels; j++) {
					raster.setPixel( incre + 2*nbPixels, 287, black);
					incre++;
				}				
			}
			else{
				for (int j = 0; j < nbPixels; j++) {
					raster.setPixel( incre + 2*nbPixels, 287, white);
					incre++;
				}	
			}				
		}
		
		incre = 0;
		// tag increment
		for (int i = 0; i < incrementBin.length(); i++) {
			if (incrementBin.charAt(i) == '0') {
				for (int j = 0; j < nbPixels; j++) {
				raster.setPixel(incre + 2 *nbPixels  + 8*nbPixels, 287, black);
				incre++;
				}
			} else {
				for (int j = 0; j < nbPixels; j++) {
				raster.setPixel( incre + 2*nbPixels  + 8*nbPixels , 287, white);
				incre++;
				}
			}
		}		
	}
	
	private void feedFileData(){
		try {
			fileOut.write("frame " + ( this.numFrames - 2 + this.numSkip)  + ";" + this.getTypeTable() + ";" + vOffset.get(0).intValue() + ";" 
					+ vIncrement.get(0).intValue() + ";" + vOffset.get(1).intValue()
					+ ";" + vIncrement.get(1).intValue() + "\r\n");
			
			vOffset.remove(0);
			vOffset.remove(0);
			vIncrement.remove(0);
			vIncrement.remove(0);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void feedFileDataDummy(){
		try {
			fileOut.write("frame " + ( this.numSkip)  + ";" + "skip" + ";" + "skip" + ";" 
					+ "skip" + ";" + "skip"
					+ ";" + "skip" + "\r\n");				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 public  void closeFileData(){			
		try {
			fileOut.flush();
			fileOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void skipFrame() {
		// TODO Auto-generated method stub		
		numSkip++;
		if(!this.bPreviewMode){
			this.feedFileDataDummy();
		}
	}

	@Override
	public boolean isEnable() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int getAudienceLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getKey11bits() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getKey() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isInsideRangeSliderFrames() {
		if(JobConfig.frameCount <= JobConfig.getSysterSelectedFrameEnd()) {
			return true;
		}
		else {
			return false;
		}	
	}
}

