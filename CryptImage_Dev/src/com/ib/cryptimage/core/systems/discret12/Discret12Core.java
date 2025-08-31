package com.ib.cryptimage.core.systems.discret12;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Iterator;

import com.ib.cryptimage.core.Device;
import com.ib.cryptimage.core.JobConfig;
import com.ib.cryptimage.core.Shift;
import com.ib.cryptimage.core.types.SystemType;

public abstract class Discret12Core extends Device {
	
	/**
	 * enable or disable the encoder
	 */
	protected boolean enable = false;
	
	protected int shiftX;
	protected int shiftY;
	
	protected Shift shift;	

	protected int frameDiscret12 = 0;
	protected int frameShifted = 0;
	
	//private BufferedImage image;;
	
	protected int horShift = 0;
	protected int verShift = 0;
	protected int shiftBlock = 0;
	
	protected DiscretRNG discretRNG;
	
	protected WritableRaster rasterCheck;	
	protected WritableRaster raster;
	
	protected int[] tabBlack;
	protected int[] tabWhite;
	
	protected int cycle;	
	protected int cptCycle;	
	protected int maxCycle = 0;	
	protected int indexCycle;
	
	/**
	 * store the current audience level
	 */
	protected int audienceLevel = 7;
	
	protected int indexUse11bitsKey = -1;
	protected int saveIndexUse11bitsKey;
	
	/**
	 * store the current index for the 11 bits key
	 */
	protected int index11bitsKey =-1;
	
	/**
	 * store the current position from the 6 frames ( half image ) sequence
	 */
	protected int seqFrame = 0;
	
	/**
	 * the count iterator for delay array
	 */
	protected int cptArray = 0;	
	
	/**
	 * the default width of the image which is 768
	 */
	protected final int sWidth = 768;
	
	
	public Discret12Core() {
		shift = new Shift();		
		shiftX = Integer.valueOf(JobConfig.getGui().getjShiftX().getValue().toString());
		shiftY = Integer.valueOf(JobConfig.getGui().getjShiftY().getValue().toString());

		horShift = 0;
		verShift = 0;	
		
		initTabsColor();
		initAudienceList();
		JobConfig.setWantSound(true);
		
		try {
			 if(Discret12Conf.isDiscret11) {
				 discretRNG = new DiscretRNG(SystemType.DISCRET11, 6, 1337);
			 }
			 else {
				 discretRNG = new DiscretRNG(SystemType.DISCRET12, 6, 1337);
			 }
			 
			 
//			 System.out.println("line/value/binary/delay : " + discretRNG.getLine() + ", " 
//			                    + discretRNG.getKey() + ", " + discretRNG.getBinaryKey() + ", " 
//	                            + discretRNG.getDelay(1));	
//			 
//			 for (int i = 0; i < 100; i++) {
//				discretRNG.iterate();
//				System.out.println("value/binary/delay : " + discretRNG.getKey() + ", " + discretRNG.getBinaryKey() + ", " 
//				                   + discretRNG.getDelay(1));				
//			}
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public abstract BufferedImage transform(BufferedImage img);
	public abstract void closeFileData();
	
	private void initAudienceList(){
		this.audienceLevel = 7;
		this.saveIndexUse11bitsKey = -1;				
		this.index11bitsKey = 6;
		this.indexUse11bitsKey = this.index11bitsKey;
		//this.key11bits = this.key11BitsTab[this.indexUse11bitsKey];
		cptArray = 0;		
		this.enable = false;
	}


	
	protected int getInitZ() {
		if(discretRNG.getDiscretType() == SystemType.DISCRET11) {
			return 0;
		}
		else {
			return 1;
		}
	}
	
	protected int getCurrentZ() {
		int z = 0;
		if(discretRNG.getDiscretType() == SystemType.DISCRET11) {
			switch (this.seqFrame) {
			case 0:
				z = 0;
				break;
			case 1:
				z = 0;
				break;
			case 2:
				z = 0;
				break;
			case 3:
				z = 1;
				break;
			case 4:
				z = 1;
				break;
			case 5:
				z = 1;
				break;

			default:
				break;
			}
		}
		else {
			switch (this.seqFrame) {
			case 0:
				z = 1;
				break;
			case 1:
				z = 1;
				break;
			case 2:
				z = 1;
				break;
			case 3:
				z = 0;
				break;
			case 4:
				z = 0;
				break;
			case 5:
				z = 0;
				break;
			default:
				break;
			}
		}
		
		return z;
	}
	
	protected void initTabsColor(){
		tabWhite = new int[768 * 3];
		
		for (int i = 0; i < tabWhite.length; i++) {
			tabWhite[i] = 255;
		}
		
		tabBlack = new int[768 * 3];
		
		for (int i = 0; i < tabBlack.length; i++) {
			tabBlack[i] = 0;
		}
	}
	
	
	protected void changeAudience(){
		maxCycle++;
		if(maxCycle == this.cycle ){
			maxCycle = 0;
			cptCycle++;
		}
		indexCycle = 0;
		
		
		if(cptCycle == 14){			
			cptCycle = 0;
		}
		this.audienceLevel = 7;
		this.saveIndexUse11bitsKey = 6;
		this.indexUse11bitsKey = 6;		
		this.index11bitsKey = 6;		
		//cptArray = 0;
		//cptPoly = 0;
		//this.seqFrame = 0;
		this.enable = true;	
		//this.key11bits = this.key11BitsTab[this.indexUse11bitsKey];	
	}
	

	/**
	 * we set the color of the 622 line according to the audience level	
	 * @param buff the BufferedImage
	 */
	protected void setAudience622Line(BufferedImage buff) {
		switch (this.audienceLevel) {
		case 1:
			if (this.seqFrame == 1) {
				setBlack622Line(buff);
			}
			if (this.seqFrame == 3) {
				setBlack622Line(buff);
			}
			if (this.seqFrame == 5) {
				setWhite622Line(buff);
			}
			break;
		case 2:
			if (this.seqFrame == 1) {
				setBlack622Line(buff);
			}
			if (this.seqFrame == 3) {
				setWhite622Line(buff);
			}
			if (this.seqFrame == 5) {
				setBlack622Line(buff);
			}
			break;
		case 3:
			if (this.seqFrame == 1) {
				setBlack622Line(buff);
			}
			if (this.seqFrame == 3) {
				setWhite622Line(buff);
			}
			if (this.seqFrame == 5) {
				setWhite622Line(buff);
			}
			break;
		case 4:
			if (this.seqFrame == 1) {
				setWhite622Line(buff);
			}
			if (this.seqFrame == 3) {
				setBlack622Line(buff);
			}
			if (this.seqFrame == 5) {
				setBlack622Line(buff);
			}
			break;
		case 5:
			if (this.seqFrame == 1) {
				setWhite622Line(buff);
			}
			if (this.seqFrame == 3) {
				setBlack622Line(buff);
			}
			if (this.seqFrame == 5) {
				setWhite622Line(buff);
			}
			break;
		case 6:
			if (this.seqFrame == 1) {
				setWhite622Line(buff);
			}
			if (this.seqFrame == 3) {
				setWhite622Line(buff);
			}
			if (this.seqFrame == 5) {
				setBlack622Line(buff);
			}
			break;
		case 7:
			if (this.seqFrame == 1) {
				setWhite622Line(buff);
			}
			if (this.seqFrame == 3) {
				setWhite622Line(buff);
			}
			if (this.seqFrame == 5) {
				setWhite622Line(buff);
			}
			break;
		default:
			break;
		}
	}
	
	protected void drawLine(int delay, int y){
		// draw black line at start of delay
		raster.setPixels(0, y, delay , 1, 
				new int[delay  * 3]);	
	}	
	
	/**
	 * set to white the 310 line
	 * @param buff the BufferedImage
	 * @return a modified BufferedImage with 310 line set to white
	 */
	protected BufferedImage setWhite310Line(BufferedImage buff){
//		for (int i = 0; i < buff.getWidth(); i++) {
//			buff.setRGB(i, 574, new Color(255,255, 255).getRGB());
//		}
		
		rasterCheck = buff.getRaster();		

		
		rasterCheck.setPixels(0, 574, 768, 1, tabWhite);
		
		return buff;		
	}
	
	/**
	 * set to black the 310 line
	 * @param buff the BufferedImage
	 * @return a modified BufferedImage with 310 line set to black
	 */
	protected BufferedImage setBlack310Line(BufferedImage buff){
//		for (int i = 0; i < buff.getWidth(); i++) {
//			buff.setRGB(i, 574, new Color(0, 0, 0).getRGB());
//		}
		
		rasterCheck = buff.getRaster();
		
		rasterCheck.setPixels(0, 574, 768, 1, tabBlack);		
		
		return buff;		
	}
	
	/**
	 * set to black the 622 line
	 * @param buff the BufferedImage
	 * @return a modified BufferedImage with 622 line set to black
	 */
	private BufferedImage setBlack622Line(BufferedImage buff){
		
		rasterCheck = buff.getRaster();	
		
		rasterCheck.setPixels(0, 573, 768, 1, tabBlack);		
		
		return buff;		
	}
	
	/**
	 * set to white the 622 line
	 * @param buff the BufferedImage
	 * @return a modified BufferedImage with 622 line set to white
	 */
	private BufferedImage setWhite622Line(BufferedImage buff){
//		for (int i = 0; i < buff.getWidth(); i++) {
//			buff.setRGB(i, 573, new Color(255,255, 255).getRGB());
//		}
		
		rasterCheck = buff.getRaster();		
		
		rasterCheck.setPixels(0, 573, 768, 1, tabWhite);	
		
		return buff;		
	}	
	
	
	/**
	 * Convert a source image to a desired BufferedImage type
	 * @param sourceImage the image source
	 * @param targetType the target type
	 * @return a converted BufferedImage
	 */
	protected BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
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

	@Override
	public boolean isEnable() {
		// TODO Auto-generated method stub
		return enable;
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
		return 0;
	}

	@Override
	public String getDeviceName() {
		// TODO Auto-generated method stub
		return "Discret12";
	}

	@Override
	public boolean isInsideRangeSliderFrames() {
		if(JobConfig.frameCount <= Discret12Conf.selectedFrameEnd) {
			return true;
		}
		else {
			return false;
		}
	}

}
