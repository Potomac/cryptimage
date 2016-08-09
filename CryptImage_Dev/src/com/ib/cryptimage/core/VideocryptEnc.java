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
			if(JobConfig.isRestrictRangeCuttingPoints()){
				this.rangeStart = 20;
				this.rangeEnd = 236;
				this.typeRange = 1;
			}
			else{
				this.rangeStart = 1;
				this.rangeEnd = 255;
				this.typeRange = 2;
			}
		}		
	}

	@Override
	BufferedImage transform(BufferedImage image) {
		numFrame++;
		this.enable = true;
				
		image = this.convertToType(image, BufferedImage.TYPE_3BYTE_BGR);
		if (image.getWidth() != this.sWidth || image.getHeight() != 576) {
			image = this.getScaledImage(image, this.sWidth, 576);
		}
		
		raster = image.getRaster();
		newImage = new BufferedImage(this.sWidth, 576, BufferedImage.TYPE_3BYTE_BGR);
		raster2 = newImage.getRaster();
		
//		//encodage pal
//		if(JobConfig.getColorMode() == 1){			
//			palEngine.setImg(image);
//			image = palEngine.encode();
//		}
//		//encodage secam
//		if(JobConfig.getColorMode() == 2){			
//			secamEngine.setImg(image);
//			image = secamEngine.encode();			
//		}
		
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
		
		cutAndRotate();
		
		if(!this.bPreviewMode){
			this.feedFileData();
		}
		
		//JobConfig.setNbFrames(JobConfig.getNbFrames() + 1);
		
		//encodage pal
		if(JobConfig.getColorMode() == 1){			
			palEngine.setImg(newImage);
			newImage = palEngine.average();
		}
		//encodage secam
		if(JobConfig.getColorMode() == 2){			
			secamEngine.setImg(newImage);
			newImage = secamEngine.averageSpecial();			
		}
			
		return newImage;
	}
	
	private void cutAndRotate(){
		for (int i = 0; i < 576; i++) {
			//System.out.println("i " + i + " valTab " + valTab[i]);
			raster2.setPixels(0, i, this.sWidth - valTab[i], 1, 
					raster.getPixels(valTab[i], i, this.sWidth - valTab[i],
							1,new int[(this.sWidth - valTab[i])*3]));
			raster2.setPixels(this.sWidth - valTab[i], i, valTab[i], 1, 
					raster.getPixels(0, i, valTab[i], 1,
							new int[valTab[i]*3]));			
		}
	}
	
	private void feedFileData(){
		try {
			fileOut.write(this.typeRange + ";" + this.seed + "\r\n");		
			
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
	

	@Override
	boolean isEnable() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	int getAudienceLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	int getKey11bits() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	void closeFileData() {
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
	void skipFrame() {
		if(!this.bPreviewMode){
			this.feedFileDataDummy();
		}		
		
	}

}
