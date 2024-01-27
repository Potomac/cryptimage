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
 * 2024-01-18 Author Mannix54
 * http://ibsoftware.free.fr/cryptimage.php
 */

package com.ib.cryptimage.core.systems.eurocrypt;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Random;

import com.ib.cryptimage.core.Device;
import com.ib.cryptimage.core.JobConfig;
import com.ib.cryptimage.core.Shift;
import com.ib.cryptimage.core.YuvCalc;

public class EurocryptCore extends Device {	
	
	protected int shiftX;
	protected int shiftY;
	
	protected Shift shift;
	BufferedImage imageSource;
	
	private int[] valTab = new int[576];
	private int[] valTabDoubleCutLuma = new int[576];
	private int[] valTabSound = new int[331776];
	private int rangeStart;
	private int rangeEnd;
	Random rand;
	Random randDoubleCut;
	Random randSound;
	
	WritableRaster raster;
	WritableRaster raster2;
	
	WritableRaster raster1Sound;
	WritableRaster raster1RGB;
	WritableRaster raster1Luma;
	WritableRaster raster1Chroma;
	WritableRaster raster1Mac;
	WritableRaster raster1SingleCut;
	WritableRaster raster1DoubleCut;
	
	WritableRaster raster2Sound;
	WritableRaster raster2Luma;
	WritableRaster raster2Chroma;
	WritableRaster raster2Mac;	
	WritableRaster raster2SingleCut;
	WritableRaster raster2DoubleCut;
	
	BufferedImage newImage;		
	BufferedImage newImageMac;
	BufferedImage newImageSoundPart;
	BufferedImage newImageLumaPart;
	BufferedImage newImageChromaPart;
	BufferedImage newImageSingleCut;
	BufferedImage newImageDoubleCut;
	BufferedImage newImageSingleCutRotated;
	BufferedImage newImageDoubleCutRotated;	
	
    public EurocryptCore() {
		shift = new Shift();		
		shiftX = Integer.valueOf(JobConfig.getGui().getjShiftX().getValue().toString());
		shiftY = Integer.valueOf(JobConfig.getGui().getjShiftY().getValue().toString());
		
		this.rangeStart = 40;
		this.rangeEnd = 215;	

		this.rand = new Random(Integer.valueOf(EurocryptConf.seedCode));
	    this.randDoubleCut = new Random(Integer.valueOf(EurocryptConf.seedCode));
		this.randSound = new Random(Integer.valueOf(EurocryptConf.seedCode));
		
	}
	
	@Override
	public	void closeFileData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BufferedImage transform(BufferedImage image) {
		image = this.convertToType(image, BufferedImage.TYPE_3BYTE_BGR);
		
		if(EurocryptConf.isEncodeMac) {
			return encodeToMac(image);
		}
		else if(EurocryptConf.isEncodeMacDecode576pNoEurocrypt) {
			image = encodeToMac(image);
			return decodeFromMac(image);
		}
		else {
			return decodeFromMac(image);
		}		
		
	}
	
	private BufferedImage encodeToMac(BufferedImage image) {
		if (image.getWidth() != 768 || image.getHeight() != 576) {
			image = this.getScaledImage(image, 768, 576);
		}
		
		JobConfig.setInputImage(image);
		
		//check shift X and Y
		if(shiftX != 0 || shiftY !=0) {
			image = shift.transform(image, shiftX, shiftY);
		}
		
		raster = image.getRaster();
		newImage = new BufferedImage(768, 576, BufferedImage.TYPE_3BYTE_BGR);
		raster2 = newImage.getRaster();

		createLumaChromaImages(image);
		
		if(EurocryptConf.isEurocryptSingleCut) {
			assembleChromaLumaSingleCut();
			generateValues();
			cutAndRotateSingleCutEncode();
		}
		
		if(EurocryptConf.isEurocryptDoubleCut) {
			generateValues();
			generateValuesDoubleCutLuma();
			cutAndRotateDoubleCutEncode();
		}
		
		newImageMac = new BufferedImage(1344, 576, BufferedImage.TYPE_3BYTE_BGR);
		
		createSoundPart();
		
		if(EurocryptConf.isEurocryptSingleCut) {
			assembleEncodedMacImageSingleCut();
		}
		else if(EurocryptConf.isEurocryptDoubleCut) {
				assembleEncodedMacImageDoubleCut();
			}
		else {
			assembleEncodedMacImage();
		}	
		
		EurocryptConf.width = newImageMac.getWidth();
		EurocryptConf.height = newImageMac.getHeight();
		
		return newImageMac;
	}

	private BufferedImage decodeFromMac(BufferedImage image) {
		if (image.getWidth() != 1344 || image.getHeight() != 576) {
			image = this.getScaledImage(image, 1344, 576);
		}
		
		JobConfig.setInputImage(image);
		
		//check shift X and Y
		if(shiftX != 0 || shiftY !=0) {
			image = shift.transform(image, shiftX, shiftY);
		}
		
		if(EurocryptConf.isEurocryptSingleCut && !EurocryptConf.isEncodeMacDecode576pNoEurocrypt) {
			newImageSingleCut =  extractChromaLumaSingleCut(image);
			generateValues();
			cutAndRotateSingleCutDecode();
			
			raster2Mac = image.getRaster();
			raster1SingleCut = newImageSingleCutRotated.getRaster();
			
			raster2Mac.setPixels(192, 0, 1152, 576, raster1SingleCut.getPixels(0, 0, 1152, 576, new int[1152 * 576 * 3]));			
			
		}		
		
		if(EurocryptConf.isEurocryptDoubleCut && !EurocryptConf.isEncodeMacDecode576pNoEurocrypt) {
			newImageDoubleCut =  extractChromaLumaDoubleCut(image);
			generateValues();
			generateValuesDoubleCutLuma();
			cutAndRotateDoubleCutDecode();
			
			raster2Mac = image.getRaster();
			raster1DoubleCut = newImageDoubleCutRotated.getRaster();
			
			raster2Mac.setPixels(192, 0, 1152, 576, raster1DoubleCut.getPixels(0, 0, 1152, 576, new int[1152 * 576 * 3]));			
			
		}	
		
		// extract chroma part
		newImageChromaPart = extractChromaPart(image);
		
		// extract luma part
		newImageLumaPart = extractLumaPart(image);
		
		// assemble chroma and luma parts
		newImage = assembleDecodedMacImage();
		
		
		EurocryptConf.width = newImage.getWidth();
		EurocryptConf.height = newImage.getHeight();
		
		return newImage;
	}

	
	private void createSoundPart() {	
		newImageSoundPart = new BufferedImage(192, 576, BufferedImage.TYPE_3BYTE_BGR);
		raster2Sound = newImageSoundPart.getRaster();
		
		int[] lumaSound = new int[192 * 576 * 3];
		int[] lumaRGB = new int[192 * 576 * 3];
		
		int min = 1;
		int max = 255;
		
		for (int i = 0; i < 331776; i++) {			
		    valTabSound[i] = (int) (this.randSound.nextInt(max - min + 1) + min);
		    lumaSound[i] = valTabSound[i];
		}
		
		//raster2Sound.setPixels(0, 0, 192, 576, valTabSound);	
		
		YuvCalc yuvCalc = new YuvCalc();
		int[] pixYUV = new int[3];
		int[] pixRGB = new int[3];
		
		for (int i = 0; i < 576; i++) {
			for(int j = 0; j < 192; j++) {
				pixRGB[0] = lumaSound[3 * i * 192 + 3 * j]; 
				pixRGB[1] = lumaSound[3 * i * 192 + 3 * j + 1];
				pixRGB[2] = lumaSound[3 * i * 192 + 3 * j + 2]; //   raster2Sound.getPixel(j, i, pixRGB);
				
				pixYUV = yuvCalc.convertRGBtoYUV(pixRGB);
				pixYUV[1] = 128;
				pixYUV[2] = 128;
				
				pixRGB = yuvCalc.convertYUVtoRGB(pixYUV);
				
				lumaRGB[3 * i * 192 + 3 * j] = pixRGB[0]; 
				lumaRGB[3 * i * 192 + 3 * j + 1] = pixRGB[1]; 
				lumaRGB[3 * i * 192 + 3 * j + 2] = pixRGB[2];							
			}			
		}
		
		raster2Sound.setPixels(0, 0, 192, 576, lumaRGB);
		
	}
	
	private void assembleEncodedMacImage() {
		// resize chroma part to 384x576 pixels
		newImageChromaPart = getResizeImage(newImageChromaPart, 384, 576);
		
		raster2Mac = newImageMac.getRaster();		
		raster1Sound = newImageSoundPart.getRaster();
		raster1Chroma = newImageChromaPart.getRaster();	
		raster1Luma = newImageLumaPart.getRaster();

		
		for (int i = 0; i < 576; i++) {
			// sound part
			raster2Mac.setPixels(0, i, 192, 1, raster1Sound.getPixels(0, i, 192, 1,new int[576]));
			
			// chroma part
			raster2Mac.setPixels(192, i, 384, 1, raster1Chroma.getPixels(0, i, 384, 1,new int[1152]));
			
			// luma part
			raster2Mac.setPixels(576, i, 768, 1, raster1Luma.getPixels(0, i, 768, 1,new int[2304]));
		}		
		
	}
	
	private void assembleEncodedMacImageSingleCut() {		
		raster2Mac = newImageMac.getRaster();		
		raster1Sound = newImageSoundPart.getRaster();
		raster1SingleCut = newImageSingleCutRotated.getRaster();	
		//raster1SingleCut = newImageSingleCut.getRaster();	
		
		// sound part
		raster2Mac.setPixels(0, 0, 192, 576, raster1Sound.getPixels(0, 0, 192, 576,new int[192 * 576 * 3]));
		
		// luma + chroma
		raster2Mac.setPixels(192, 0, 1152, 576, raster1SingleCut.getPixels(0, 0, 1152, 576, new int[1152 * 576 * 3]));
		
		
	}

	
	private void assembleEncodedMacImageDoubleCut() {		
		raster2Mac = newImageMac.getRaster();		
		raster1Sound = newImageSoundPart.getRaster();
		raster1DoubleCut = newImageDoubleCutRotated.getRaster();	
		
		// sound part
		raster2Mac.setPixels(0, 0, 192, 576, raster1Sound.getPixels(0, 0, 192, 576,new int[192 * 576 * 3]));
		
		// luma + chroma
		raster2Mac.setPixels(192, 0, 1152, 576, raster1DoubleCut.getPixels(0, 0, 1152, 576, new int[1152 * 576 * 3]));		
	}

	
	private void generateValues(){
		int min = this.rangeStart;// 20;//1
		int max = this.rangeEnd;// 236; //255

		int valVideocrypt;
		
		for (int i = 0; i < valTab.length; i++) {		
			valVideocrypt = (int) (rand.nextInt(max - min + 1) + min);
			
			valTab[i] = (int) (valVideocrypt * 1.5);
		}		

	}
	
	private void generateValuesDoubleCutLuma(){
		int min = this.rangeStart;// 20;//1
		int max = this.rangeEnd;// 236; //255

		int valVideocryptDoubleCut;
		
		for (int i = 0; i < valTabDoubleCutLuma.length; i++) {			
			valVideocryptDoubleCut = (int) (rand.nextInt(max - min + 1) + min);
			
			valTabDoubleCutLuma[i] = (int) (valVideocryptDoubleCut * 3);
		}
	}

	
	private void cutAndRotateSingleCutEncode() {
		if(EurocryptConf.isEurocryptSingleCut) {
			raster1SingleCut = newImageSingleCut.getRaster();
			
			newImageSingleCutRotated = new BufferedImage(1152, 576, BufferedImage.TYPE_3BYTE_BGR);			
			raster2SingleCut = newImageSingleCutRotated.getRaster();
			
			for (int i = 0; i < 576; i++) {
				raster2SingleCut.setPixels(0, i, 1152 - valTab[i], 1,
						raster1SingleCut.getPixels(valTab[i], i, 1152 - valTab[i], 1, new int[(1152 - valTab[i]) * 3]));
				raster2SingleCut.setPixels(1152 - valTab[i], i, valTab[i], 1,
						raster1SingleCut.getPixels(0, i, valTab[i], 1, new int[valTab[i] * 3]));
			}				
		}		
	}
	
	private void cutAndRotateDoubleCutEncode() {
		newImageChromaPart = getResizeImage(newImageChromaPart, 384, 576);
		newImageDoubleCutRotated = new BufferedImage(1152, 576, BufferedImage.TYPE_3BYTE_BGR);

		raster1Chroma = newImageChromaPart.getRaster();
		raster1Luma = newImageLumaPart.getRaster();

		raster2DoubleCut = newImageDoubleCutRotated.getRaster();

		// rotate chroma part
		for (int i = 0; i < 576; i++) {
			raster2DoubleCut.setPixels(0, i, 384 - valTab[i], 1,
					raster1Chroma.getPixels(valTab[i], i, 384 - valTab[i], 1, new int[(384 - valTab[i]) * 3]));
			raster2DoubleCut.setPixels(384 - valTab[i], i, valTab[i], 1,
					raster1Chroma.getPixels(0, i, valTab[i], 1, new int[valTab[i] * 3]));
		}

		// rotate luma part
		for (int i = 0; i < 576; i++) {
			raster2DoubleCut.setPixels(384, i, 768 - valTabDoubleCutLuma[i], 1,
					raster1Luma.getPixels(valTabDoubleCutLuma[i], i, 768 - valTabDoubleCutLuma[i], 1,
							new int[(768 - valTabDoubleCutLuma[i]) * 3]));

			raster2DoubleCut.setPixels(1152 - valTabDoubleCutLuma[i], i, valTabDoubleCutLuma[i], 1,
					raster1Luma.getPixels(0, i, valTabDoubleCutLuma[i], 1, new int[valTabDoubleCutLuma[i] * 3]));
		}
	}
	

	private BufferedImage extractChromaLumaSingleCut(BufferedImage image) {
		newImageSingleCut = new BufferedImage(1152, 576, BufferedImage.TYPE_3BYTE_BGR);		
		raster2SingleCut = newImageSingleCut.getRaster();
		raster1Mac = image.getRaster(); 
		
		raster2SingleCut.setPixels(0, 0, 1152, 576, raster1Mac.getPixels(192, 0, 1152, 576, new int[1152 * 576 * 3]));
		
		return newImageSingleCut;
	}
	
	private BufferedImage extractChromaLumaDoubleCut(BufferedImage image) {
		newImageDoubleCut = new BufferedImage(1152, 576, BufferedImage.TYPE_3BYTE_BGR);		
		raster2DoubleCut = newImageDoubleCut.getRaster();
		raster1Mac = image.getRaster(); 
		
		raster2DoubleCut.setPixels(0, 0, 1152, 576, raster1Mac.getPixels(192, 0, 1152, 576, new int[1152 * 576 * 3]));
		
		return newImageDoubleCut;
	}
	
	private void cutAndRotateSingleCutDecode() {
		if(EurocryptConf.isEurocryptSingleCut) {
			raster1SingleCut = newImageSingleCut.getRaster();
			
			newImageSingleCutRotated = new BufferedImage(1152, 576, BufferedImage.TYPE_3BYTE_BGR);			
			raster2SingleCut = newImageSingleCutRotated.getRaster();			

			
			for (int i = 0; i < 576; i++) {
				raster2SingleCut.setPixels(0, i, valTab[i], 1,
						raster1SingleCut.getPixels(1152 - valTab[i], i, valTab[i], 1, new int[valTab[i] * 3]));
				raster2SingleCut.setPixels(valTab[i], i, 1152 - valTab[i], 1,
						raster1SingleCut.getPixels(0, i, 1152 - valTab[i], 1, new int[(1152 - valTab[i]) * 3]));
			}			
		}
	}
	
	private void cutAndRotateDoubleCutDecode() {
			raster1DoubleCut = newImageDoubleCut.getRaster();
			
			
			newImageDoubleCutRotated = new BufferedImage(1152, 576, BufferedImage.TYPE_3BYTE_BGR);			
			raster2DoubleCut = newImageDoubleCutRotated.getRaster();			

			
			for (int i = 0; i < 576; i++) {
				//chroma part
				raster2DoubleCut.setPixels(0, i, valTab[i], 1, 
						raster1DoubleCut.getPixels(384 - valTab[i], i, valTab[i], 1,
								new int[valTab[i]*3]));
				raster2DoubleCut.setPixels(valTab[i], i, 384 - valTab[i], 1, 
						raster1DoubleCut.getPixels(0, i, 384 - valTab[i], 1,
								new int[(384 - valTab[i])*3]));				
				                
				
				raster2DoubleCut.setPixels(384, i, valTabDoubleCutLuma[i], 1, 
						raster1DoubleCut.getPixels(1152 - valTabDoubleCutLuma[i], i, valTabDoubleCutLuma[i], 1,
								new int[valTabDoubleCutLuma[i]*3]));
				raster2DoubleCut.setPixels(384 + valTabDoubleCutLuma[i], i, 768 - valTabDoubleCutLuma[i], 1, 
						raster1DoubleCut.getPixels(384, i, 768 - valTabDoubleCutLuma[i], 1,
								new int[(768 - valTabDoubleCutLuma[i])*3]));				
			}		
	}
	
	private void createLumaChromaImages(BufferedImage image) {
		YuvCalc yuvCalc = new YuvCalc();
		
		newImageLumaPart = new BufferedImage(768, 576, BufferedImage.TYPE_3BYTE_BGR);	
		newImageChromaPart = new BufferedImage(768, 576, BufferedImage.TYPE_3BYTE_BGR);
		
		raster1RGB = image.getRaster();
		raster2Luma = newImageLumaPart.getRaster();
		raster2Chroma = newImageChromaPart.getRaster();
		
		int[] lumaRGB;
		lumaRGB = raster1RGB.getPixels(0, 0, 768, 576, new int[768 * 576 * 3]);
		
		int[] lumaY = new int[768 * 576 * 3];
		int[] chromaUV = new int[768 * 576 * 3];		

		int[] pixRGB = new int[3];
		int[] pixYUV = new int[3];
		
		int[] pixYUVluma = new int[3];
		int[] pixRGBluma = new int[3];
		int[] pixYUVchroma = new int[3];
		int[] pixRGBchroma = new int[3];
	
		int inverse = -1;
		
		int y;
		int u;
		int v;
			
		for (int i = 0; i < 576; i++) {
			inverse = inverse * -1;	
			for(int j = 0; j < 768; j++) {
				
				// luma image				
				pixRGB[0] = lumaRGB[3 * i * 768 + 3 * j];
				pixRGB[1] = lumaRGB[3 * i * 768 + 3 * j + 1];
				pixRGB[2] = lumaRGB[3 * i * 768 + 3 * j + 2];
											
				pixYUV = yuvCalc.convertRGBtoYUV(pixRGB);
				
				y = pixYUV[0];
				u = pixYUV[1];
				v = pixYUV[2];

				pixYUVluma[0] = y;			
				pixYUVluma[1] = 128;
				pixYUVluma[2] = 128;
				
				pixRGBluma = yuvCalc.convertYUVtoRGB(pixYUVluma);				
				
				lumaY[3 * i * 768 + 3 * j] = pixRGBluma[0];
				lumaY[3 * i * 768 + 3 * j + 1] = pixRGBluma[1];
				lumaY[3 * i * 768 + 3 * j + 2] = pixRGBluma[2];			
				
				
				// chroma image								
				if(inverse > 0) {
					pixYUVchroma[0] = v;
				}
				else {
					pixYUVchroma[0] = u;
				}
				
				pixYUVchroma[1] = 128;
				pixYUVchroma[2] = 128;
												
				pixRGBchroma = yuvCalc.convertYUVtoRGB(pixYUVchroma);
				
				chromaUV[3 * i * 768 + 3 * j] = pixRGBchroma[0];
				chromaUV[3 * i * 768 + 3 * j + 1] = pixRGBchroma[1];
				chromaUV[3 * i * 768 + 3 * j + 2] = pixRGBchroma[2];				
			}			
		}
		
		raster2Luma.setPixels(0, 0, 768, 576, lumaY);
		raster2Chroma.setPixels(0, 0, 768, 576, chromaUV);
		
	}
	
	private void assembleChromaLumaSingleCut() {
		// resize chroma part to 384x576 pixels
		newImageChromaPart = getResizeImage(newImageChromaPart, 384, 576);
		
		raster1Luma = newImageLumaPart.getRaster();
		raster1Chroma = newImageChromaPart.getRaster();
		
		newImageSingleCut = new BufferedImage(1152, 576, BufferedImage.TYPE_3BYTE_BGR);
		
		raster2SingleCut = newImageSingleCut.getRaster();
		
		raster2SingleCut.setPixels(0, 0, 384, 576, raster1Chroma.getPixels(0, 0, 384, 576, new int[384 * 576 *3]));		
		raster2SingleCut.setPixels(384, 0, 768, 576, raster1Luma.getPixels(0, 0, 768, 576, new int[768 * 576 *3]));
		
	}
	
	private BufferedImage extractChromaPart(BufferedImage imageMac) {
		raster1Mac = imageMac.getRaster();
		BufferedImage imageChromaPart = new BufferedImage(384, 576, BufferedImage.TYPE_3BYTE_BGR);
		raster2Chroma = imageChromaPart.getRaster();
		
		raster2Chroma.setPixels(0, 0, 384, 576, 
				raster1Mac.getPixels(192, 0, 384, 576, new int[384 * 576 * 3]));
		
		imageChromaPart = getResizeImage(imageChromaPart, 768, 576);
		
		return imageChromaPart;		
	}
	
	private BufferedImage extractLumaPart(BufferedImage imageMac) {
		raster1Mac = imageMac.getRaster();
		BufferedImage imageLumaPart = new BufferedImage(768, 576, BufferedImage.TYPE_3BYTE_BGR);
		raster2Luma = imageLumaPart.getRaster();
		
		raster2Luma.setPixels(0, 0, 768, 576, 
				raster1Mac.getPixels(576, 0, 768, 576, new int[768 * 576 * 3]));	
		
		
		return imageLumaPart;		
	}
	
	
	private BufferedImage assembleDecodedMacImage() {		

		newImage = new BufferedImage(768, 576, BufferedImage.TYPE_3BYTE_BGR);
		raster2 = newImage.getRaster();
		
		raster1Chroma = newImageChromaPart.getRaster();
		raster1Luma = newImageLumaPart.getRaster();
		
		YuvCalc yuvCalc = new YuvCalc();
		int[] pixRGB = new int[3];
		int[] pixYUV = new int[3];

		int[] pixRGBchroma = new int[3];
		
		int[] pixYUVcombi = new int[3];
		int[] pixRGBcombi = new int[3];
		
		int[] lumaRGB;
		lumaRGB = raster1Luma.getPixels(0, 0, 768, 576, new int[768 * 576 * 3]);
		int[] chromaRGB;
		chromaRGB = raster1Chroma.getPixels(0, 0, 768, 576, new int[768 * 576 * 3]);

		int[] newImgRGB = new int[768 * 576 * 3];
		
		int inverse = -1;

		int [] mem_v = new int[768];

		for (int i = 0; i < 576; i++) {
			inverse = inverse * -1;	
			for(int j = 0; j < 768; j++) {
				
				// luma image
				pixRGB[0] = lumaRGB[3 * i * 768 + 3 * j];
				pixRGB[1] = lumaRGB[3 * i * 768 + 3 * j + 1];
				pixRGB[2] = lumaRGB[3 * i * 768 + 3 * j + 2];
				
				pixYUV = yuvCalc.convertRGBtoYUV(pixRGB);
				
				//y
				pixYUVcombi[0] = pixYUV[0];		
				
				
				// chroma image
				pixRGBchroma[0] = chromaRGB[3 * i * 768 + 3 * j];
				pixRGBchroma[1] = chromaRGB[3 * i * 768 + 3 * j + 1];
				pixRGBchroma[2] = chromaRGB[3 * i * 768 + 3 * j + 2];
				
				pixYUV = yuvCalc.convertRGBtoYUV(pixRGBchroma);		
							
				if(inverse > 0) {
					//v
					pixYUVcombi[2] = pixYUV[0];	
					mem_v[j] = pixYUVcombi[2];
					
					// we take u from next line
					pixRGBchroma[0] = chromaRGB[3 * (i + 1) * 768 + 3 * j];
					pixRGBchroma[1] = chromaRGB[3 * (i + 1) * 768 + 3 * j + 1];
					pixRGBchroma[2] = chromaRGB[3 * (i + 1) * 768 + 3 * j + 2];
						
					pixYUV = yuvCalc.convertRGBtoYUV(pixRGBchroma);		
					pixYUVcombi[1] = pixYUV[0];					
				}
				else {
					//u
					pixYUVcombi[1] = pixYUV[0];
					// we take v from the memory (previous line)
					pixYUVcombi[2] = mem_v[j];
				}
								
				pixRGBcombi = yuvCalc.convertYUVtoRGB(pixYUVcombi);
				newImgRGB[3 * i * 768 + 3 * j] = pixRGBcombi[0];
				newImgRGB[3 * i * 768 + 3 * j + 1] = pixRGBcombi[1];
				newImgRGB[3 * i * 768 + 3 * j + 2] = pixRGBcombi[2];				
			}			
		}
		
		raster2.setPixels(0, 0, 768, 576, newImgRGB);
		
		return newImage;		
	}
	
	@Override
	public boolean isEnable() {
		// TODO Auto-generated method stub
		return false;
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
	public void skipFrame() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getKey() {
		// TODO Auto-generated method stub
		return Integer.valueOf(EurocryptConf.seedCode);
	}

	@Override
	public String getDeviceName() {
		// TODO Auto-generated method stub
		return "MAC-Eurocrypt";
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
	

	
	/**
	 * Resize an image to a new size
	 * @param src the image source
	 * @param w the new width
	 * @param h the new height
	 * @return the resized image
	 */
	private BufferedImage getResizeImage(BufferedImage src, int w, int h){
	    int finalw = w;
	    int finalh = h;
	    
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
	
}
