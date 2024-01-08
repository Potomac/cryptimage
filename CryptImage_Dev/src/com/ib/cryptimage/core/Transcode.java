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
 * 10 juil. 2018 Author Mannix54
 */



package com.ib.cryptimage.core;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

/**
 * @author Mannix54
 *
 */
public class Transcode extends Device {
	
	
	private boolean enable;
	private PalEngine palEngine;
	private SecamEngine secamEngine;
	private PalEncoder palEncoder;
	private PalDecoder palDecoder;
	private SplitFrames splitFrames;
	private boolean skip;
	
	protected int shiftX;
	protected int shiftY;
	
	protected Shift shift;
	private int sWidth = 768;
	BufferedImage imageSource;

	/**
	 * 
	 */
	public Transcode() {
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
		JobConfig.setCurrentPalFrameDec(JobConfig.getGui().getCmbPalFrameStart().getSelectedIndex());
		JobConfig.setCurrentPalFrame(0);
		palEngine = new PalEngine();
		secamEngine = new SecamEngine();
		palEncoder = new PalEncoder(false, typeGrid);
		palDecoder = new PalDecoder(freq);		
		JobConfig.setPalDecoder(palDecoder);
		JobConfig.setPalEncoder(palEncoder);
	}

	@Override
	BufferedImage transform(BufferedImage image) {
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
		
		this.imageSource = deepCopy(image);		
		
		// encodage pal
		if (JobConfig.getColorMode() == 1) {
			palEngine.setImg(image);
			image = palEngine.encode();
		}
		
		//encode image to pal composite
		if (JobConfig.getColorMode() == 3 || JobConfig.getColorMode() == 4 ) {
			palEncoder.setImage(image);
			image = palEncoder.encode(false);
		}
		
		//encode secam
		if(JobConfig.getColorMode() == 2){			
			secamEngine.setImg(image);
			image = secamEngine.encode();			
		}
		
		//decode secam
		if (JobConfig.getColorMode() == 2) {
			secamEngine.setImg(image);				
			return secamEngine.decode();
		}			
		
		//decode pal composite
		if ( JobConfig.getColorMode() == 1) {
			palEngine.setImg(image);				
			return palEngine.decode(false);
		}
		
		//decode pal image composite
		if (JobConfig.getColorMode() == 3 || JobConfig.getColorMode() == 5 ) {
			palDecoder.setImage(image);
			return palDecoder.decode();
		}	
				
		return image;
	}

	@Override
	boolean isEnable() {
		// TODO Auto-generated method stub
		return false;
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
		// TODO Auto-generated method stub
		
	}

	@Override
	void skipFrame() {
		// TODO Auto-generated method stub
		
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
	 * clone a BufferedImage
	 * @param bi the BufferedImage to clone
	 * @return a cloned BufferedImage
	 */
	private BufferedImage deepCopy(BufferedImage bi) {		
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	@Override
	int getKey() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public String getDeviceName() {
		return "Transcode";
	}

}
