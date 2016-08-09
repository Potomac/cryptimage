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
		numFrame++;
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
			cutAndRotate();
		}
		else{
			searchCorrel();
		}
		
		//JobConfig.setNbFrames(JobConfig.getNbFrames() + 1);
		
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
	
//	private void searchCorrel(){
//		long sum = (long) 3e20;
//		int tot = 0;
//		int valCut1 = 0;
//		int valCut2 = 0;
//		int[] pixels1 = new int[768*3];
//		int[] pixels2 = new int[768*3];
//		int[] tab;		
//		
//		tab = raster.getPixels(0,0, 768, 576, new int[768*3*576]);
//		
//		for (int i = 100; i < 300; i++) {
//			sum = (long) -3e20;
//			for (int cut1 = 1; cut1 < 255; cut1++) {				
//				for (int cut2 = 1; cut2 < 255; cut2++) {									
//					//line 1
//					for (int x = 0; x < cut1*3; x++) {
//						pixels1[x*3] = tab[(this.sWidth - cut1*3)*3 + x*3 + 768*3 * i];
//						pixels1[x*3 + 1] = tab[(this.sWidth - cut1*3)*3 + x*3 +1 + 768*3 * i];
//						pixels1[x*3 + 2] = tab[(this.sWidth - cut1*3)*3 + x*3 +2 + 768*3 * i];
//					}
//					for (int x = cut1*3; x < (this.sWidth - cut1*3); x++) {
//						pixels1[x*3] = tab[ x*3 + 768*3 * i];
//						pixels1[x*3 + 1] = tab[ x*3 +1 + 768*3 * i];
//						pixels1[x*3 + 2] = tab[ x*3 +2 + 768*3 * i];
//					}
//					
//					//line 2
//					for (int x = 0; x < cut2*3; x++) {
//						pixels2[x*3] = tab[(this.sWidth - cut2*3)*3 + x*3 + 768*3 * ( i + 1)];
//						pixels2[x*3 + 1] = tab[(this.sWidth - cut2*3)*3 + x*3 +1 + 768*3 * ( i + 1)];
//						pixels2[x*3 + 2] = tab[(this.sWidth - cut2*3)*3 + x*3 +2 + 768*3 * ( i + 1)];
//					}
//					for (int x = cut2*3; x < (this.sWidth - cut2*3); x++) {
//						pixels2[x*3] = tab[ x*3 + 768*3 * ( i + 1)];
//						pixels2[x*3 + 1] = tab[ x*3 +1 + 768*3 * ( i + 1)];
//						pixels2[x*3 + 2] = tab[ x*3 +2 + 768*3 * ( i + 1)];
//					}
//										
//					//compute sum of pixels luminance
//					tot = 0;					
//					for (int x = 0; x < 768; x+=3) {						
//						tot =  (int) (tot + (( 0.299*pixels1[3*x] +  0.587*pixels1[3*x+1] 
//								+  0.114*pixels1[3*x+2])
//						* (  0.299*pixels2[3*x] +  0.587*pixels2[3*x+1] +  0.114*pixels2[3*x+2])));
//					}	
//						
//					if(tot > sum){
//						valCut1 = cut1;
//						valCut2 = cut2;
//						sum = tot;
//					} 
//					
//				}
//			}			
//			
//			System.out.println("ligne " + i + " ok");
//			System.out.println("valCut1 " + valCut1 + " valCut2 " + valCut2);
//			
////			raster2.setPixels(0, i, 768, 1, pixels1);
////			raster2.setPixels(0, i+1, 768, 1, pixels2);
//			
//		
//			
//			//solution found
//			//line 1
//			raster2.setPixels(0, i, valCut1*3, 1, 
//					raster.getPixels(this.sWidth - valCut1*3, i, valCut1*3, 1,
//							new int[valCut1*3*3]));
//			raster2.setPixels(valCut1*3, i, this.sWidth - valCut1*3, 1, 
//					raster.getPixels(0, i, this.sWidth - valCut1*3, 1,
//							new int[(this.sWidth - valCut1*3)*3]));		
//			
//			//line 2
//			raster2.setPixels(0, i+1, valCut2*3, 1, 
//					raster.getPixels(this.sWidth - valCut2*3, i+1, valCut2*3, 1,
//							new int[valCut2*3*3]));
//			raster2.setPixels(valCut2*3, i+1, this.sWidth - valCut2*3, 1, 
//					raster.getPixels(0, i+1, this.sWidth - valCut2*3, 1,
//							new int[(this.sWidth - valCut2*3)*3]));	
//			
//		i++;
//		}
//	}

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
			//JobConfig.setNbFrames(JobConfig.getNbFrames() + 1);
			this.readFileDataDummy();
		}

	}

}
