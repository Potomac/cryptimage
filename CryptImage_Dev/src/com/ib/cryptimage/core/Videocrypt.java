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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * @author Mannix54
 *
 */
public abstract class Videocrypt extends Device {
	
	
	protected boolean enable;
	protected PalEngine palEngine;
	protected SecamEngine secamEngine;
	protected PalEncoder palEncoder;
	protected PalDecoder palDecoder;
	protected SplitFrames splitFrames;
	
	protected boolean skip;
	protected final int sWidth = 768;
	protected boolean wantCrypt;
	protected String line ="";
	protected String[] para;
	protected FileWriter fileOut;
	protected FileWriter fileLog;
	protected BufferedReader fileInBuff;
	protected FileReader fileInReader;
	
	protected int[] valTab = new int[576];
	protected int seed;
	protected int rangeStart;
	protected int rangeEnd;
	//protected int typeRange;
	protected int numFrame;
	protected int frameLine;
	private boolean bPreviewMode;
	
	private final int MAXLINES = 65089;
	private int cptLines;
	private int part = 1;
	private String fileName;
	private String colorMode = "";
	private String modeMachine = "";
	
	protected int shiftX;
	protected int shiftY;
	
	protected Shift shift;
	
	protected boolean strictMode = false;
	

	/**
	 * 
	 */
	public Videocrypt(boolean wantCrypt) {
		int typeGrid = 0;
		int freq = 0;
		if(JobConfig.getGui().getCmbPalFreq().getSelectedIndex() == 0) {
			typeGrid = 0;
			freq = 14750000;
		}
		else {
			typeGrid = 1;
			freq = 17750000;
		}
		
		
		shift = new Shift();		
		shiftX = Integer.valueOf(JobConfig.getGui().getjShiftX().getValue().toString());
		shiftY = Integer.valueOf(JobConfig.getGui().getjShiftY().getValue().toString());
		
		strictMode = JobConfig.getGui().getChkVideocryptStrictMode().isSelected();
		
		if (!JobConfig.isModePhoto()) {
			numFrame = Integer.valueOf(JobConfig.getGui().getJspFrameStartVideocrypt().getValue().toString())-1;			
		}
				
		palEngine = new PalEngine();
		secamEngine = new SecamEngine();		
		this.wantCrypt = wantCrypt;	
		bPreviewMode = JobConfig.getGui().getChkPlayer().isSelected();
		
		palEngine = new PalEngine();
		secamEngine = new SecamEngine();
		palEncoder = new PalEncoder(false, typeGrid);
		palDecoder = new PalDecoder(freq);
		JobConfig.setCurrentPalFrame(0);
		JobConfig.setPalDecoder(palDecoder);
		JobConfig.setPalEncoder(palEncoder);
		
		
		try {
			if (!bPreviewMode && JobConfig.isLogVideocrypt()) {				
				if (JobConfig.getGui().getCbColorMode().getSelectedIndex() == 0) {
					colorMode = "rgb";
				}
				if (JobConfig.getGui().getCbColorMode().getSelectedIndex() == 1) {
					colorMode = "pal";
				}
				if (JobConfig.getGui().getCbColorMode().getSelectedIndex() == 2) {
					colorMode = "secam";
				}
				if (JobConfig.getGui().getCbColorMode().getSelectedIndex() == 3) {
					colorMode = "pal_composite_encode_and_decode";
				}
				if (JobConfig.getGui().getCbColorMode().getSelectedIndex() == 4) {
					colorMode = "pal_composite_encode_only";
				}
				if (JobConfig.getGui().getCbColorMode().getSelectedIndex() == 5) {
					colorMode = "pal_composite_decode_only";
				}
				
				if (JobConfig.isWantDec()) {
					modeMachine = "d";
				} else {
					modeMachine = "c";
				}

				fileName = JobConfig.getOutput_file();
				if (JobConfig.isHorodatage()) {
					File file = new File(fileName);
					fileName = JobConfig.getDateTime() + "_" + file.getName();
					fileName = file.getParent() + File.separator + fileName;
				}

				if (JobConfig.getVideo_frame()
						- Integer.valueOf(JobConfig.getGui().getJspFrameStartVideocrypt().getValue().toString()) < 113
						|| JobConfig.isModePhoto()) {
					fileLog = new FileWriter(fileName + "_" + modeMachine + "videocrypt_" + colorMode + ".csv");

				} else {
					fileLog = new FileWriter(
							fileName + "_" + modeMachine + "videocrypt_" + colorMode + "_" + "part" + part + ".csv");
				}
				fileLog.write("frame;line;cut point for encrypting;cut point for decrypting" + "\r\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	abstract BufferedImage transform(BufferedImage img);
	abstract void closeFileData();
	
	protected void feedLog(int nbFrame, int cuttingPoint){
		frameLine++;		
		if ( frameLine == 577){
			frameLine = 1;
		}
		
		cptLines++;
		if(cptLines == MAXLINES){
			try {
				this.fileLog.flush();
				this.fileLog.close();
				part++;
				cptLines = 1;
				fileLog = new FileWriter(fileName + "_" + modeMachine +"videocrypt_" + colorMode  
				+  "_part" + part +  ".csv");
				fileLog.write("frame;line;cut point for encrypting;cut point for decrypting"
						+ "\r\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
		if (cuttingPoint == -1) {
			frameLine--;
			try {
				fileLog.write("frame " + numFrame + ";" + frameLine + ";"
						+ "skip;skip" + "\r\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {

			int encCuttingPoint,decCuttingPoint;			
				encCuttingPoint = cuttingPoint;
				decCuttingPoint = 256 - cuttingPoint;
				
			try {
				fileLog.write("frame " + numFrame + ";" + frameLine +";" 
						+ encCuttingPoint + ";"
						+ decCuttingPoint + "\r\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	protected boolean readFileData(){
		
		try {		
			line = fileInBuff.readLine();				
			
			if(line.equals("skip")){
				this.skip = true;
				if (!bPreviewMode && JobConfig.isLogVideocrypt()) {
					feedLog(numFrame, -1);
				}
				return false;
			}
			
			para = line.split(";");
			
			//check random value
			if(Integer.valueOf(para[0]) < 1 || Integer.valueOf(para[0]) > 16777216){
				return false;
			}		
			
			//check cut points value
			if(Integer.valueOf(para[1]) < 1 || Integer.valueOf(para[1]) > 255){
				return false;
			}	
			if(Integer.valueOf(para[2]) < 1 || Integer.valueOf(para[2]) > 255){
				return false;
			}	
					
			this.rangeStart = Integer.valueOf(para[1]);
			this.rangeEnd = Integer.valueOf(para[2]);
			
			this.skip = false;
			
			this.seed = Integer.valueOf(para[0]);
			generateValues(this.seed);			
			return true;
						
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	protected void readFileDataDummy(){
		try {		
			line = fileInBuff.readLine();			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void generateValues(int seed){
		int min = this.rangeStart;// 20;//1
		int max = this.rangeEnd;// 236; //255
		Random rand = new Random(seed);
		
		int valVideocrypt;
		
		for (int i = 0; i < valTab.length; i++) {			
			
			valVideocrypt = (int) (rand.nextInt(max - min + 1) + min);
			
			if(JobConfig.isLogVideocrypt() && !bPreviewMode){
				feedLog(numFrame, valVideocrypt);
			}
			
			valTab[i] = valVideocrypt*3;
		}		

	}
	
	/**
	 * clone a BufferedImage
	 * @param bi the BufferedImage to clone
	 * @return a cloned BufferedImage
	 */
	protected BufferedImage deepCopy(BufferedImage bi) {		
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
	
	/**
	 * Scale an image to a new size
	 * @param src the image source
	 * @param w the new width
	 * @param h the new height
	 * @return the resized image
	 */
	protected BufferedImage getScaledImage(BufferedImage src, int w, int h){
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

}
