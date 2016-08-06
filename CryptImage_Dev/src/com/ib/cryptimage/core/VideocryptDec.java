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
import java.io.IOException;

/**
 * @author Mannix54
 *
 */
public class VideocryptDec extends Videocrypt {
	
	
	WritableRaster raster;
	WritableRaster raster2;
	BufferedImage newImage;	
	boolean wantCorrel = false;
	

	/**
	 * 
	 */
	public VideocryptDec(String nameDataFile,
			boolean wantCorrel) {
		super(false);
		this.wantCorrel = wantCorrel;
		
		if (!nameDataFile.equals("")) {
			try {
				fileInBuff = new BufferedReader(new FileReader(nameDataFile));
				this.fileInReader = new FileReader(nameDataFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	@Override
	BufferedImage transform(BufferedImage image) {		
		
		image = this.convertToType(image, BufferedImage.TYPE_3BYTE_BGR);
		if (image.getWidth() != this.sWidth || image.getHeight() != 576) {
			image = this.getScaledImage(image, this.sWidth, 576);
		}
		
		raster = image.getRaster();
		newImage = new BufferedImage(this.sWidth, 576, BufferedImage.TYPE_3BYTE_BGR);
		raster2 = newImage.getRaster();
		
		if(this.wantCorrel == false ){
			if(!this.readFileData()){				
				this.enable = false;
				return image;
			}
		}
		else{
			
		}
		
		cutAndRotate();
		
		// encodage pal
		if (JobConfig.getColorMode() == 1) {
			palEngine.setImg(newImage);
			newImage = palEngine.average();
		}
		
		this.enable = true;
			
		return newImage;
	}
	
	private void cutAndRotate(){
		for (int i = 0; i < 576; i++) {
			//System.out.println("i " + i + " valTab " + valTab[i]);
			raster2.setPixels(0, i, valTab[i], 1, 
					raster.getPixels(this.sWidth - valTab[i], i, valTab[i], 1,
							new int[valTab[i]*3]));
			raster2.setPixels(valTab[i], i, this.sWidth - valTab[i], 1, 
					raster.getPixels(0, i, this.sWidth - valTab[i], 1,
							new int[(this.sWidth - valTab[i])*3]));			
		}
	}

	@Override
	void closeFileData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	boolean isEnable() {
		// TODO Auto-generated method stub
		return this.enable;
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
	void skipFrame() {
		if (fileInBuff != null) {
			this.readFileDataDummy();
		}

	}

}
