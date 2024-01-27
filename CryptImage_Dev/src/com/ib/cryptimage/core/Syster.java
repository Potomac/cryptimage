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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Random;
import java.util.Vector;




/**
 * @author Mannix54
 *
 */
public abstract class Syster extends Device {

	protected int increment;
	protected int offset;
	protected int[] keyTable = new int[256];
	protected int[] permut = new int[287];
	protected int[] order = new int[287];
	private int typeTable = 1;
	protected final int sWidth = 768;
	protected boolean wantCrypt;
	protected boolean ready = false;
	protected BufferedImage completFrame;
	protected int numFrames;
	protected int numSkip;
	
	protected int offSetOdd;
	protected int offSetEven;
	protected int incrementOdd;
	protected int incrementEven;
	
	protected Vector<BufferedImage> vecFrame;
	protected BufferedReader fileInBuff;
	protected FileReader fileInReader;
	
	protected String line ="";
	protected String [] parts;
	protected FileWriter fileOut;
	
	protected boolean enable;
	protected PalEngine palEngine;
	protected SecamEngine secamEngine;
	protected PalEncoder palEncoder;
	protected PalDecoder palDecoder;
	protected SplitFrames splitFrames;
	protected boolean skip;
	
	protected int shiftX;
	protected int shiftY;
	
	protected Shift shift;
	

	/**
	 * 
	 * @param typeTable the type of keyTable ( 1 or 2 )
	 * @param wantCrypt set true if we want a crypt operation	
	 */
	public Syster(int typeTable, boolean wantCrypt) {
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
		
		palEngine = new PalEngine();
		secamEngine = new SecamEngine();
		JobConfig.setCurrentPalFrameDec(JobConfig.getGui().getCmbPalFrameStart().getSelectedIndex());
		JobConfig.setCurrentPalFrame(0);
		palEncoder = new PalEncoder(false, typeGrid);
		palDecoder = new PalDecoder(freq);		
		JobConfig.setPalDecoder(palDecoder);
		JobConfig.setPalEncoder(palEncoder);
				
		this.typeTable = typeTable;		
		this.wantCrypt = wantCrypt;		
		this.vecFrame = new Vector<BufferedImage>();
		initKeyTable();
	}
	
	public abstract BufferedImage transform(BufferedImage img);
	public abstract void closeFileData();
	
	protected void genOffsetIncrement() {		
		Random rand = new Random();
		int min = 0;
		int max = 255;

		this.offset = rand.nextInt(max - min + 1) + min;

		min = 1;
		max = 255;
		double val;
		val = rand.nextInt(max - min + 1) + min;
		if ((val / 2) - (int) (val / 2) == 0) {
			this.increment = (int) (val + 1);
		} else {
			this.increment = (int) (val);
		}				
	}
	
	protected void genOffsetIncrementEven() {
		this.offset = this.offset + this.increment;
		if(this.offset > 255){
			this.offset = offset - 256;
		}	
			
	}
	
	protected void initKeyTable(){
			Reader paramReader;
			switch (typeTable) {
			case 1:				
				paramReader = new InputStreamReader(getClass().getResourceAsStream(("/ressources/level1_KEY.TXT")));
				break;
			case 2: 
				paramReader = new InputStreamReader(getClass().getResourceAsStream(("/ressources/level2_KEY.TXT")));
				break;
			default:
				paramReader = new InputStreamReader(getClass().getResourceAsStream(("/ressources/level2_KEY.TXT")));
				break;
			}
			BufferedReader br;
			br = new BufferedReader(paramReader);
			int i = 0;
			String line ;
			try {
				while ((line = br.readLine()) != null) {					  
				   keyTable[i] = Integer.valueOf(line);					   
				   i++;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
	}
	
	public void initPermut(){
		int j;
		short i;

		j = offset;
		for (i=0;i<255;i++)
		{
			permut[i]=keyTable[j];
			j+=increment;
			if (j >=256){
				j = j - 256;
			}
		}
		for (i=255;i<287;i++) 
			permut[i]=i-255;		
	}
	
	public void showPermut(){
		for (int i = 0; i < permut.length; i++) {
			System.out.print(permut[i] + ";");
		}
	}
	
	public void showOrder(){
		for (int i = 0; i < order.length; i++) {
			System.out.print(order[i] + ";");
		}
	}
	
	protected boolean readFileData(){
		try {		
			line = fileInBuff.readLine();
					
			parts = line.split(";");
			
			if(parts[1].equals("skip")){
				this.skip = true;
				return false;
			}
			
			if(Integer.valueOf(parts[1]) < 1 || Integer.valueOf(parts[1]) > 2){
				return false;
			}
			
			if(Integer.valueOf(parts[1]) != this.getTypeTable()){
				this.setTypeTable(Integer.valueOf(parts[1]));
				this.initKeyTable();
			}
			
			this.setTypeTable(Integer.valueOf(parts[1]));
			this.offSetOdd = Integer.valueOf(parts[2]);
			this.incrementOdd = Integer.valueOf(parts[3]);
			this.offSetEven = Integer.valueOf(parts[4]);
			this.incrementEven = Integer.valueOf(parts[5]);
			
			this.skip = false;
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
	

	public int getIncrement() {
		return increment;
	}

	public void setIncrement(int increment) {
		this.increment = increment;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int[] getOrder() {
		return order;
	}

	public int getTypeTable() {
		return typeTable;
	}

	public void setTypeTable(int typeTable) {
		this.typeTable = typeTable;
	}

	public boolean isWantCrypt() {
		return wantCrypt;
	}

	public void setWantCrypt(boolean wantCrypt) {
		this.wantCrypt = wantCrypt;
	}

	public boolean isReady() {
		return ready;
	}

	protected BufferedImage getCompletFrame() {
		if (this.ready) {
			this.ready = false;			
			
			if (JobConfig.getColorMode() == 2 && !JobConfig.isWantDec()) {
				secamEngine.setImg(completFrame);				
				return secamEngine.decode();
			}			
			
			if ( JobConfig.getColorMode() == 1) {
				palEngine.setImg(completFrame);				
				return palEngine.decode(this.skip);
			}
			
			//decode pal image composite
			if (JobConfig.getColorMode() == 3 || JobConfig.getColorMode() == 5 ) {
				palDecoder.setImage(completFrame);
				return palDecoder.decode();
			}			
			else {				
				return completFrame;
			}
		}
		else {			
			JobConfig.setCurrentPalFrame(JobConfig.getCurrentPalFrame() - 1);
			JobConfig.setCurrentPalFrameDec(JobConfig.getCurrentPalFrameDec() - 1);
			//JobConfig.incrementPalFrameDec();
			
//			if(!JobConfig.isWantPlay() && !JobConfig.isWantDec()) {			
//				try {
//					fileOut.write("frame " + ( this.numSkip)  + ";" + "skip" + ";" + "skip" + ";" 
//							+ "skip" + ";" + "skip"
//							+ ";" + "skip" + "\r\n");				
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			return new BufferedImage(768, 576, BufferedImage.TYPE_3BYTE_BGR);
			
			return null;
		}
	}

	public int getOffSetOdd() {
		return offSetOdd;
	}

	public int getOffSetEven() {
		return offSetEven;
	}

	public int getIncrementOdd() {
		return incrementOdd;
	}

	public int getIncrementEven() {
		return incrementEven;
	}
	
	public String getDeviceName() {
		return "Syster";
	}
	
}
