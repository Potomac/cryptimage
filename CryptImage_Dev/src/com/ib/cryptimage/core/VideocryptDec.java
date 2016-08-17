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
import java.awt.image.Raster;
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
		numFrame++;
		image = this.convertToType(image, BufferedImage.TYPE_3BYTE_BGR);
		if (image.getWidth() != this.sWidth || image.getHeight() != 576) {
			image = this.getScaledImage(image, this.sWidth, 576);
		}
		
		raster = image.getRaster();
		newImage = new BufferedImage(this.sWidth, 576, BufferedImage.TYPE_3BYTE_BGR);
		raster2 = newImage.getRaster();
		
		if (this.wantCorrel == false) {

			if (JobConfig.isWantVideocryptTags()) {
				if (!tagLine()) {
					this.enable = false;
					return image;
				}
			} else {
				if (!this.readFileData()) {
					this.enable = false;
					return image;
				}
			}
			
			cutAndRotate();
		
		} else {
			searchCorrel();
		}		
		
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
			raster2.setPixels(0, i, valTab[i], 1, 
					raster.getPixels(this.sWidth - valTab[i], i, valTab[i], 1,
							new int[valTab[i]*3]));
			raster2.setPixels(valTab[i], i, this.sWidth - valTab[i], 1, 
					raster.getPixels(0, i, this.sWidth - valTab[i], 1,
							new int[(this.sWidth - valTab[i])*3]));			
		}
	}
	
	private void searchCorrel() {
		int maxsum = (int) -1e20;
		int x, i = 0;
		int sum = 0;
		int[] offset = new int[768];

		int valCut1 = 0;
		int[] pixels1 = new int[768 * 3];
		int[] pixels2 = new int[768 * 3];
		int[] tab;
		int coef, coef2;

		tab = raster.getPixels(0, 0, 768, 576, new int[768 * 3 * 576]);

		for (int y = 1; y < 576; y++) {
			maxsum = (int) -1e20;
			// line 1
			for (int x1 = 0; x1 < 768; x1++) {
				pixels1[x1 * 3] = tab[x1 * 3 + 768 * 3 * y];
				pixels1[x1 * 3 + 1] = tab[x1 * 3 + 1 + 768 * 3 * y];
				pixels1[x1 * 3 + 2] = tab[x1 * 3 + 2 + 768 * 3 * y];
			}

			// line 2
			for (int x1 = 0; x1 < 768; x1++) {
				pixels2[x1 * 3] = tab[x1 * 3 + 768 * 3 * (y - 1)];
				pixels2[x1 * 3 + 1] = tab[x1 * 3 + 1 + 768 * 3 * (y - 1)];
				pixels2[x1 * 3 + 2] = tab[x1 * 3 + 2 + 768 * 3 * (y - 1)];
			}

			for (x = 0; x < 768; x++) {

				sum = 0;
				coef = x;

				for (i = 0; i < 768 - x; i++) {
					sum += (0.299 * pixels1[i * 3] + 0.587 * pixels1[i * 3 + 1] + 0.114 * pixels1[i * 3 + 2])
							* (0.299 * pixels2[coef * 3] + 0.587 * pixels2[coef * 3 + 1]
									+ 0.114 * pixels2[coef * 3 + 2]);
					coef++;
				}

				coef2 = 0;

				for (; i < 768; i++) {
					sum += (0.299 * pixels1[i * 3] + 0.587 * pixels1[i * 3 + 1] + 0.114 * pixels1[i * 3 + 2])
							* (0.299 * pixels2[coef2 * 3] + 0.587 * pixels2[coef2 * 3 + 1]
									+ 0.114 * pixels2[coef2 * 3 + 2]);
					coef2++;
				}

				if (sum > maxsum) {
					maxsum = sum;
					offset[y] = x;
				}

			}
			offset[y] = (offset[y] + offset[y - 1]) % 768;
		}

		for (int j = 0; j < 576; j++) {
			valCut1 = offset[j];
			raster2.setPixels(0, j, valCut1, 1,
					raster.getPixels(this.sWidth - valCut1, j, valCut1, 1, new int[valCut1 * 3]));
			raster2.setPixels(valCut1, j, this.sWidth - valCut1, 1,
					raster.getPixels(0, j, this.sWidth - valCut1, 1, new int[(this.sWidth - valCut1) * 3]));
			
			if(JobConfig.isLogVideocrypt() && !JobConfig.getGui().getChkPlayer().isSelected()){
				feedLog(numFrame, valCut1/3);
			}
		}

	}
	
	private boolean tagLine(){		
		String binTypeRange = "";
		String binSeed = "";
		
		int nbPixels = 8;
		int whiteMin = 200;
		int whiteMax = 255;
		int blackMin = 0;
		int blackMax = 127;
		
		int[] tab = new int[3];
		double somme = 0;
		int incre = 0;
		
		// tag type range
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < nbPixels; j++) {
				somme += (raster.getPixel(incre, 0, tab)[0] + raster.getPixel(incre, 0, tab)[1] + raster.getPixel(incre, 0, tab)[2]) / 3d;
			incre++;
			}
			somme = somme/nbPixels;
			if (somme >= blackMin && somme < blackMax) {
				binTypeRange = binTypeRange + "0";
				somme = 0;
			} else {
				if (somme > whiteMin && somme <= whiteMax) {
					binTypeRange = binTypeRange + "1";
					somme = 0;
				}
			}			
		}
		
		somme = 0;
		incre = 0;
		
		//tag seed		
		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < nbPixels; j++) {
			somme += ( raster.getPixel(incre + 2*nbPixels, 0,tab)[0] + raster.getPixel(incre + 2*nbPixels, 0,tab)[1] +raster.getPixel(incre + 2*nbPixels, 0,tab)[2] )/3d;
			incre++;
			}
			somme = somme/nbPixels;
			if(somme >=blackMin && somme < blackMax){
				binSeed = binSeed + "0";
				somme = 0;
			}
			else{
				if(somme >whiteMin && somme <= whiteMax){
					binSeed = binSeed + "1";
					somme = 0;
				}
			}				
		}
		
		//check values
		if (!checkValuesTags(binTypeRange, binSeed)){
			this.skip = true;
			if (!JobConfig.getGui().getChkPlayer().isSelected() && JobConfig.isLogVideocrypt()) {
				feedLog(numFrame, -1);
			}
			return false;
		}				
			
			this.typeRange = Integer.parseInt(binTypeRange, 2);
			this.seed = Integer.parseInt(binSeed, 2);
			
			
			if(this.typeRange == 1 ){
				this.rangeStart = 20;
				this.rangeEnd = 236;
				this.typeRange = 1;
			} else{
				this.rangeStart = 1;
				this.rangeEnd = 255;
				this.typeRange = 2;
			}		
			
			generateValues(this.seed);						
			
			this.skip = false;
			return true;		
	}
	
	private boolean checkValuesTags(String binTypeRange, String binSeed) {

		try {
			if (Integer.parseInt(binTypeRange, 2) == 1 || Integer.parseInt(binTypeRange, 2) == 2) {
				if (Integer.parseInt(binSeed, 2) >= 1 && Integer.parseInt(binSeed, 2) <= 16777216) {
					return true;
				}
			}

		} catch (Exception e) {
			return false;
		}
		return false;
	}

	@Override
	void closeFileData() {
		try {			
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
