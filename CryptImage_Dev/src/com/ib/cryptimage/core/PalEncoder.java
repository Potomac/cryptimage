/**
 * This file is part of	CryptImage.
 *
 * CryptImage is free software: you can redistribute it and/or modify
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
 * along with CryptImage.  If not, see <http://www.gnu.org/licenses/>
 * 
 * 6 juil. 2018 Author Mannix54
 */



package com.ib.cryptimage.core;

/**
 * @author Mannix54
 *
 */
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RescaleOp;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;

public class PalEncoder {

	YuvCalc yuvCalc;
	WritableRaster rasterInput;
	WritableRaster rasterOutput;
	Raster rasterGrid;
	int[] pixelTab = new int[3];
	BufferedImage imgInput;
	BufferedImage imgOutput;
	BufferedImage imgGrid;
	boolean split = false;
	
	int currentFrame = 0;
	
	String grid = "/ressources/subcarrier_2.png";
	String burst1;
	String burst2;
	
	int typeGrid;

	
	public PalEncoder(boolean splitFrames, int typeGrid) {
		yuvCalc = new YuvCalc();
		this.split = splitFrames;	
		
		this.typeGrid = typeGrid;

		if (typeGrid == 1) {
			grid = "/ressources/subcarrier_2.png";
		}
		else {
			grid = "/ressources/PAL_subcarrier_ori.png";
		}
	

	}
	
	private void initFrame() {
		
		currentFrame = JobConfig.getCurrentPalFrame();
		//System.out.println("enc " + JobConfig.getCurrentPalFrame());
		
		if(typeGrid == 1) {
			if(JobConfig.getGui().getChkWorkaroundSysterCapture().isSelected()) {
				switch (currentFrame) {
				case 1:				
					grid = "/ressources/subcarrier_phase_1.bmp";				
					burst1 = "/ressources/burst_top_phase_4.bmp";
					burst2 = "/ressources/burst_bot_phase_4.bmp";
					break;
				case 2:
					grid = "/ressources/subcarrier_phase_2.bmp";
					burst1 = "/ressources/burst_top_phase_1.bmp";
					burst2 = "/ressources/burst_bot_phase_1.bmp";
					break;
				case 3:
					grid = "/ressources/subcarrier_phase_3.bmp";
					burst1 = "/ressources/burst_top_phase_2.bmp";
					burst2 = "/ressources/burst_bot_phase_2.bmp";
					break;
				case 4:
					grid = "/ressources/subcarrier_phase_4.bmp";
					burst1 = "/ressources/burst_top_phase_3.bmp";
					burst2 = "/ressources/burst_bot_phase_3.bmp";
					break;
				default:
					System.out.println("error pal frame number");
					break;
				}
			}
			else {
				switch (currentFrame) {
				case 1:
					grid = "/ressources/subcarrier_phase_1.bmp";
					burst1 = "/ressources/burst_top_phase_1.bmp";
					burst2 = "/ressources/burst_bot_phase_1.bmp";
					break;
				case 2:
					grid = "/ressources/subcarrier_phase_2.bmp";
					burst1 = "/ressources/burst_top_phase_2.bmp";
					burst2 = "/ressources/burst_bot_phase_2.bmp";
					break;
				case 3:
					grid = "/ressources/subcarrier_phase_3.bmp";
					burst1 = "/ressources/burst_top_phase_3.bmp";
					burst2 = "/ressources/burst_bot_phase_3.bmp";
					break;
				case 4:
					grid = "/ressources/subcarrier_phase_4.bmp";
					burst1 = "/ressources/burst_top_phase_4.bmp";
					burst2 = "/ressources/burst_bot_phase_4.bmp";
					break;
				default:
					System.out.println("error pal frame number");
					break;
				}
			}
		}
		else if(typeGrid == 0) {
			switch (currentFrame) {
			case 1:
				grid = "/ressources/grid_14.75_phase_1_new.bmp";			
				burst1 = "/ressources/burst_14.75_phase_1_top.bmp";
				burst2 = "/ressources/burst_14.75_phase_1_bot.bmp";
				break;
			case 2:
				grid = "/ressources/grid_14.75_phase_2_new.bmp";
				burst1 = "/ressources/burst_14.75_phase_2_top.bmp";
				burst2 = "/ressources/burst_14.75_phase_2_bot.bmp";
				break;
			case 3:
				grid = "/ressources/grid_14.75_phase_3_new.bmp";
				burst1 = "/ressources/burst_14.75_phase_3_top.bmp";
				burst2 = "/ressources/burst_14.75_phase_3_bot.bmp";
				break;
			case 4:
				grid = "/ressources/grid_14.75_phase_4_new.bmp";
				burst1 = "/ressources/burst_14.75_phase_4_top.bmp";
				burst2 = "/ressources/burst_14.75_phase_4_bot.bmp";
				break;
			default:
				System.out.println("error pal frame number");
				break;
			}
		}
		

		
	}
	
	public int getCurrentFrame(){
		return this.currentFrame;
	}
	

	public void setImage(BufferedImage image) {
		this.imgInput = image;
		imgInput = convertToType(imgInput, BufferedImage.TYPE_3BYTE_BGR);
		convertToYUV();	
				
		rasterInput = imgInput.getRaster();
			
	}
	
	private void convertToYUV(){
		WritableRaster rasterYuv = imgInput.getRaster();
		
		for (int y = 0; y < 576; y++) {
			for (int x = 0; x < 768 ; x++) {				
				rasterYuv.setPixel(x, y,
						yuvCalc.convertRGBtoYUV(rasterYuv.getPixel(x, y,pixelTab)));
			}
		}		
	}
	
		
	private void filter() {
		long timeStart = System.currentTimeMillis();
		double [][] values;
		FilterPixels filt;
		// Y 6.0 MHz low pass
		values = new double[][]{
			  { 0, 0, 0, 0, 0, 0, 0},
			  { 0, 0, 0, 0, 0, 0, 0},
			  { 0, 0, 0, 0, 0, 0, 0},
			  { 0, -14, 39, 78, 39, -14, 0},
			  { 0, 0, 0, 0, 0, 0, 0},
			  { 0, 0, 0, 0, 0, 0, 0},
			  { 0, 0, 0, 0, 0, 0, 0}
			};
		
		filt = new FilterPixels(7, 7, values, 1d/128d, 0);
		
		imgInput = filt.transform(imgInput, true, false, false);
		
//		//rasterInput = imgInput.getRaster();
//		
		//5.5 MHz low pass
		values = new double[][]{
			  { 0, 0, 0, 0, 0, 0, 0},
			  { 0, 0, 0, 0, 0, 0, 0},
			  { 0, 0, 0, 0, 0, 0, 0},
			  { 0, -27, 54, 74, 54, -27, 0},
			  { 0, 0, 0, 0, 0, 0, 0},
			  { 0, 0, 0, 0, 0, 0, 0},
			  { 0, 0, 0, 0, 0, 0, 0}
			};
		
		filt = new FilterPixels(7, 7, values, 1d/128d, 0);
			
		imgInput = filt.transform(imgInput, true, false, false);
		
		//Lowpass U, V
		values = new double[][]{
			  { 0, 0, 0, 0, 0, 0, 0},
			  { 0, 0, 0, 0, 0, 0, 0},
			  { 0, 0, 0, 0, 0, 0, 0},
			  { 3, 4, 6, 6, 6, 4, 3},
			  { 0, 0, 0, 0, 0, 0, 0},
			  { 0, 0, 0, 0, 0, 0, 0},
			  { 0, 0, 0, 0, 0, 0, 0}
			};
		
		filt = new FilterPixels(7, 7, values, 1d/32d, 0);
			
		imgInput = filt.transform(imgInput, false, true, true);
		imgInput = filt.transform(imgInput, false, true, true);
		
		rasterInput = imgInput.getRaster();		
	}
	
	
	public BufferedImage encode(boolean filtering) {	
		initFrame();		
		
		try {			
			imgGrid = ImageIO.read(getClass().getResource(grid));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		rasterGrid = imgGrid.getRaster();	
		
		if(filtering) {
			filter();
		}
		
		int width = imgInput.getWidth();
		int height = imgInput.getHeight();

		imgOutput = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

		rasterOutput = imgOutput.getRaster();
		

		
		int[] rgbim1 = new int[width * 3];
		int[] rgbim2 = new int[width * 3];
		int[] rgbim3 = new int[width * 3];
		int r1, g1, b1, r2, b2, r3, g3, b3;
		int sum;

		//int[] moy = new int[3];
		
		for (int row = 0; row < height; row++) {
			rgbim1 = scanLineInput(row);
			rgbim2 = scanLineGrid(row);

			for (int i = 0; i < width * 3; i++) {
				
				//scale y to 64-211
				//rgbim1[i] = (int)(rgbim1[i] * 147f/255f) + 64;
//				rgbim1[i] = (int)(rgbim1[i] * 219f/255f) + 16;
//				rgbim1[i +1] = (int)(rgbim1[i + 1] * 219f/255f) + 16;
//				rgbim1[i +2] = (int)(rgbim1[i + 2] * 219f/255f) + 16;
			
				
				
				r1 = rgbim1[i]; // y
				g1 = rgbim1[i + 1]; // u
				b1 = rgbim1[i + 2]; // v

				r2 = rgbim2[i]; // v
				b2 = rgbim2[i + 2]; // u

				r3 = (int) (r1);
				g3 =   (Math.abs(b2 - g1) + (255 - Math.abs(255 - b2 - g1)))/2; //(int)  (Math.abs((b2 - g1)));
				b3 = (Math.abs(r2 - b1) + (255 - Math.abs(255 - r2 - b1)))/2; //(int)  (Math.abs((r2 - b1)));
				
				//scale u and v				
				//System.out.println(g3 + " " + b3);
				//g3 = (int)(g3 * 128f/255f) + 128 ;
				//b3 = (int)(b3 * 128f/255f) + 128 ;
				
				
				
				sum = (int) ((r3 + g3 + b3)/3f);
				
				rgbim3[i] =  r3; //(int) ((r3 +  g3 +  b3)/3) ;//(r3 + g3 + b3) /3 ;			
				rgbim3[i +1] = sum;// (int) ((r3  +  g3 +  b3)/3);//(int) ((r3 +  g3 +  b3)/3);
				rgbim3[i +2] = sum;//(int) ((r3 +  g3 +  b3)/3);//(int) ((r3 +  g3 +  b3)/3);				
		
				i++;
				i++;
			}

			rasterOutput.setPixels(0, row, 768, 1, rgbim3);
		}

		//convertToRGB();
		
		
		//long timeStart = System.currentTimeMillis();
		imgOutput = convertToType(imgOutput, BufferedImage.TYPE_BYTE_GRAY);
		imgOutput = convertToType(imgOutput, BufferedImage.TYPE_3BYTE_BGR);
		
	
		//System.out.println("temps de codage : " + (timeEnd - timeStart)/1000f);
		
		//RescaleOp rescaleOp = new RescaleOp(1.3f, 1.3f,null);
		//rescaleOp.filter(imgOutput, imgOutput);  // Source and destination are the same.

		return imgOutput;
	}
	
	
	public BufferedImage encode2() {
		int width = imgInput.getWidth();
		int height = imgInput.getHeight();
		
		imgOutput = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		
		rasterOutput = imgOutput.getRaster();
		
		int[] rgbim1 = new int[width];
		int[] rgbim2 = new int[width];
		int[] rgbim3 = new int[width];
		int r1, g1, b1, r2, b2, r3, g3, b3;
	
		for (int row = 0; row < height; row++) {
			imgInput.getRGB(0, row, width, 1, rgbim1, 0, width);
			imgGrid.getRGB(0, row, width, 1, rgbim2, 0, width);

			for (int col = 0; col < width; col++) {
				int rgb1 = rgbim1[col];
				r1 = (rgb1 >> 16) & 255; //y
				g1 = (rgb1 >> 8) & 255; //u
				b1 = rgb1 & 255; //v

				int rgb2 = rgbim2[col];
				r2 = (rgb2 >> 16) & 255; //v			
				b2 = rgb2 & 255; //u			
				
				r3 = (int) (r1 * 1 );//+ g2 * (1.0 - weight));
				g3 = (int) ((Math.abs((b2 - g1))) );
				b3 = (int) ((Math.abs((r2 - b1))) );
				
				
				
				//rgbim3[col] = (r3 << 16) | (g3 << 8) | b3;
				
				rgbim3[col] = ((r3 + g3 + b3)/3 << 16) | (128 << 8) | 128;
			}			
			
			imgOutput.setRGB(0, row, width, 1, rgbim3, 0, width);
		}
		rasterOutput = imgOutput.getRaster();
		convertToRGB();
				
		return imgOutput;
	}
	
    private int[] scanLineInput(int y) {
    	int [] buff = rasterInput.getPixels(0, y, rasterInput.getWidth(), 1, new int[rasterInput.getWidth() * 3]);
    	return buff;
    }
	
    private int[] scanLineGrid(int y) {
    	int [] buff = rasterGrid.getPixels(0, y, rasterGrid.getWidth(), 1, new int[rasterGrid.getWidth() * 3]);
    	return buff;
    }
	
	
	private void convertToRGB(){		
		for (int y = 0; y < 576; y++) {
			for (int x = 0; x < 768; x++) {
				rasterOutput.setPixel(x, y, yuvCalc.convertYUVtoRGB(rasterOutput.getPixel(x, y,
						pixelTab)));
			}			
		}		
	}
	
	private void convertToRGB2(){	
		WritableRaster rasterYuv = imgInput.getRaster();
		for (int y = 0; y < 576; y++) {
			for (int x = 0; x < 768; x++) {
				rasterYuv.setPixel(x, y, yuvCalc.convertYUVtoRGB(rasterYuv.getPixel(x, y,
						pixelTab)));
			}			
		}		
	}
	
	/**
	 * Convert a source image to a desired BufferedImage type
	 * @param sourceImage the image source
	 * @param targetType the target type
	 * @return a converted BufferedImage
	 */
	protected  BufferedImage convertToType(BufferedImage sourceImage,
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
	
	
	public BufferedImage splitFrames(BufferedImage fullFrame) throws IOException {
		BufferedImage buff = new  BufferedImage(944, 625, BufferedImage.TYPE_3BYTE_BGR);
		
		fullFrame = convertToType(fullFrame, BufferedImage.TYPE_3BYTE_BGR);
		
		WritableRaster raster = buff.getRaster();
		
		BufferedImage burst_top = ImageIO.read(getClass().getResource(burst1));// ImageIO.read(getClass().getResource("/ressources/burst_1_fix_c2.png"));
		BufferedImage burst_bot = ImageIO.read(getClass().getResource(burst2)); //ImageIO.read(getClass().getResource("/ressources/burst_2_fix_c2.png"));
				
		Raster rasterBurst1 = burst_top.getRaster();
		Raster rasterBurst2 = burst_bot.getRaster();
		
		Raster rasterfullFrame = fullFrame.getRaster();
		
		raster.setPixels(76, 21, 36, 288,
				rasterBurst1.getPixels(0, 0, 36, 288, new int[36 * 288 * 3]));
		
		raster.setPixels(76, 332, 36, 288,
				rasterBurst2.getPixels(0, 0, 36, 288, new int[36 * 288 * 3]));
		
		//buff = convertToType(buff, BufferedImage.TYPE_BYTE_GRAY);
		
		int j = 0;
		
		for (int i = 0; i < 576;  i++) {			
			//System.out.println(i);
			raster.setPixels(156, j + 21, 768, 1,
					rasterfullFrame.getPixels(0, i, 768, 1, new int[768 *3]));						
			i++;
			j++;			
		}
		
		j=0;
		for (int i = 1; i < 576;  i++) {			
			//System.out.println(i);
			raster.setPixels(156, j + 332, 768, 1,
					rasterfullFrame.getPixels(0, i , 768, 1, new int[768 *3]));						
			i++;
			j++;			
		}
		
		return convertToType(buff, BufferedImage.TYPE_BYTE_GRAY);
	}
	
}
	