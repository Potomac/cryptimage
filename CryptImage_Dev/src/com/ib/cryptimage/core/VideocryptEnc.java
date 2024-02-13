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
 * 4 août 2016 Author Mannix54
 */



package com.ib.cryptimage.core;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * @author Mannix54
 *
 */
public class VideocryptEnc extends Videocrypt {
		
	WritableRaster raster;
	WritableRaster raster2;
	BufferedImage newImage;	
	private boolean bPreviewMode;

	/**
	 * 
	 */
	public VideocryptEnc( String nameDataFile,
			String nameManualEncodingFile, boolean bPreviewMode) {
		super(true);
		this.bPreviewMode = bPreviewMode;
		
		try {
			if(!bPreviewMode){				
				fileOut = new FileWriter(nameDataFile + ".vid");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(JobConfig.isWantVideocryptEncRandom() == false ){
			try {
				fileInBuff = new BufferedReader(new FileReader(nameManualEncodingFile));
				this.fileInReader = new FileReader(nameManualEncodingFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			this.rangeStart = Integer.valueOf(JobConfig.getGui().getComboRangeStart().getSelectedItem().toString());
			this.rangeEnd = Integer.valueOf(JobConfig.getGui().getComboRangeEnd().getSelectedItem().toString());
//			if(JobConfig.isRestrictRangeCuttingPoints()){
//				this.rangeStart = 20;
//				this.rangeEnd = 236;
//				this.typeRange = 1;
//			}
//			else{
//				this.rangeStart = 1;
//				this.rangeEnd = 255;
//				this.typeRange = 2;
//			}
		}		
	}

	@Override
	public BufferedImage transform(BufferedImage image) {
		numFrame++;
		JobConfig.incrementPalFrame();
		JobConfig.incrementPalFrameDec();
		
		this.enable = true;
				
		image = this.convertToType(image, BufferedImage.TYPE_3BYTE_BGR);
		if (image.getWidth() != this.sWidth || image.getHeight() != 576) {
			image = this.getScaledImage(image, this.sWidth, 576);
		}
		
		JobConfig.setInputImage(image);
		
		//check shift X and Y
		if(shiftX != 0 || shiftY !=0) {
			image = shift.transform(image, shiftX, shiftY);
		}
		
		if(JobConfig.frameCount <= JobConfig.getVideocryptSelectedFrameEnd()) {
			raster = image.getRaster();
			newImage = new BufferedImage(this.sWidth, 576, BufferedImage.TYPE_3BYTE_BGR);
			raster2 = newImage.getRaster();
			
			
			if(JobConfig.isWantVideocryptEncRandom() == false ){
				if(!this.readFileData()){
					this.enable = false;
					//skip
					if (this.skip){
						this.skipFrame();
					}
					return image;
				}
			}
			else{
				int min = 1;
				int max = 16777216;
				Random rand = new Random();

				int valVideocrypt;			
							
				valVideocrypt = (int) (rand.nextInt(max - min + 1) + min);
				this.seed = valVideocrypt;
				generateValues(valVideocrypt);
			}
			
			//encode image to pal composite
			if (JobConfig.getColorMode() == 3 || JobConfig.getColorMode() == 4 ) {
				palEncoder.setImage(image);
				image = palEncoder.encode(false);
				raster = image.getRaster();
			}
			
			cutAndRotate();
			
			//decode pal image composite
			if (JobConfig.getColorMode() == 3 || JobConfig.getColorMode() == 5 ) {
				palDecoder.setImage(newImage);
				newImage = palDecoder.decode();
			}	
			
			if(!this.bPreviewMode){
				this.feedFileData();
			}
					
			//encodage pal
			if(JobConfig.getColorMode() == 1){			
				palEngine.setImg(newImage);
				newImage = palEngine.average(valTab, strictMode);
			}
			//encodage secam
			if(JobConfig.getColorMode() == 2){			
				secamEngine.setImg(newImage);
				newImage = secamEngine.averageSpecial();			
			}
			
			//tags
			if(JobConfig.isWantVideocryptTags()){
				tagLine();
			}
				
			return newImage;
		}
		else {
			this.skipFrame();
			return image;
		}
		

	}
	
	private void cutAndRotate(){
		
		if(this.strictMode == false) {
			for (int i = 0; i < 576; i++) {			
				raster2.setPixels(0, i, this.sWidth - valTab[i], 1, 
						raster.getPixels(valTab[i], i, this.sWidth - valTab[i],
								1,new int[(this.sWidth - valTab[i])*3]));
				raster2.setPixels(this.sWidth - valTab[i], i, valTab[i], 1, 
						raster.getPixels(0, i, valTab[i], 1,
								new int[valTab[i]*3]));			
			}
		}
		else {
			for (int i = 0; i < 576; i++) {
				
				raster2.setPixels(0 , i, 12, 1, 
						raster.getPixels(0, i, 12, 1,
								new int[(12)*3]));
				
				raster2.setPixels(0 + 12, i, this.sWidth - valTab[i] - 12, 1, 
						raster.getPixels(valTab[i], i, this.sWidth - valTab[i] - 12,
								1,new int[(this.sWidth - valTab[i] - 12)*3]));
				raster2.setPixels(this.sWidth - valTab[i], i, valTab[i] -12, 1, 
						raster.getPixels(0 + 12, i, valTab[i] -12, 1,
								new int[(valTab[i]-12)*3]));
				
				raster2.setPixels(this.sWidth - 12 , i, 12, 1, 
						raster.getPixels(valTab[i], i, 12, 1,
								new int[(12)*3]));
				
				
			}
		}

	}
	
	private void feedFileData(){
		try {
			fileOut.write(this.seed + ";" + this.rangeStart + ";" + this.rangeEnd + "\r\n");		
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void feedFileDataDummy(){
		try {
			fileOut.write("skip" + "\r\n");				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void tagLine(){
		String binRangeStart = String.format
				("%8s", Integer.toBinaryString(this.rangeStart)).replace(" ", "0");
		String binRangeEnd = String.format
				("%8s", Integer.toBinaryString(this.rangeEnd)).replace(" ", "0");
		String binSeed = String.format
				("%24s", Integer.toBinaryString(seed)).replace(" ", "0");
				
		int[] black = {0,0,0};
		int[] white = {255,255,255};
		int incre = 0;
		int nbPixels = 8;
		
		//tag seed		
		for (int i = 0; i < binSeed.length(); i++) {
			if(binSeed.charAt(i) == '0'){
				for (int j = 0; j < nbPixels; j++) {
					raster2.setPixel( incre, 575, black);
					incre++;
				}				
			}
			else{
				for (int j = 0; j < nbPixels; j++) {
					raster2.setPixel( incre, 575, white);
					incre++;
				}	
			}				
		}
		
		incre = 0;
		
		//tag range start
		for (int i = 0; i < binRangeStart.length(); i++) {
			if(binRangeStart.charAt(i) == '0'){
				for (int j = 0; j < nbPixels; j++) {
				raster2.setPixel( incre  + 24 * nbPixels, 575, black);
				incre++;
				}

			}
			else{
				for (int j = 0; j < nbPixels; j++) {
				raster2.setPixel(incre  + 24 * nbPixels, 575, white);
				incre++;
				}
			}		
		}
		
		incre = 0;
		
		//tag range end		
		for (int i = 0; i < binRangeEnd.length(); i++) {
			if(binRangeEnd.charAt(i) == '0'){
				for (int j = 0; j < nbPixels; j++) {
					raster2.setPixel( incre + 24 * nbPixels + 8 * nbPixels, 575, black);
					incre++;
				}				
			}
			else{
				for (int j = 0; j < nbPixels; j++) {
					raster2.setPixel( incre +24 * nbPixels + 8 * nbPixels, 575, white);
					incre++;
				}	
			}				
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
	public void closeFileData() {
		try {
			fileOut.flush();
			fileOut.close();
			if(JobConfig.isLogVideocrypt() 
					&& !JobConfig.getGui().getChkPlayer().isSelected()){
				this.fileLog.flush();
				this.fileLog.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	@Override
	public void skipFrame() {
		if(!this.bPreviewMode){
			this.feedFileDataDummy();
		}		
		
	}

	@Override
	public int getKey() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isInsideRangeSliderFrames() {
		if(JobConfig.frameCount <= JobConfig.getVideocryptSelectedFrameEnd()) {
			return true;
		}
		else {
			return false;
		}
	}

}
