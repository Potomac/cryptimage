package com.ib.cryptimage.core.systems.luxcrypt;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import com.ib.cryptimage.core.Device;
import com.ib.cryptimage.core.JobConfig;
import com.ib.cryptimage.core.Shift;
import com.ib.cryptimage.core.systems.eurocrypt.EurocryptConf;


public class LuxcryptCore extends Device {
	
	protected int shiftX;
	protected int shiftY;
	
	protected Shift shift;	

	long frameLuxcrypt = 0;
	long frameShifted = 0;

	private BeamScan beamScan;
	
	private BufferedImage porchLine;
	private BufferedImage vbiLine;
	private BufferedImage vbiLine2;
	private BufferedImage videoLine;
	
	int horShift = 0;
	int verShift = 0;
	int shiftBlock = 0;
	
	int cptHorShift = 0;
	int cptVerShift = 0;

	
	public LuxcryptCore() {
		shift = new Shift();		
		shiftX = Integer.valueOf(JobConfig.getGui().getjShiftX().getValue().toString());
		shiftY = Integer.valueOf(JobConfig.getGui().getjShiftY().getValue().toString());
		
		
		beamScan = new BeamScan(!LuxcryptConf.isDisableHorSync, !LuxcryptConf.isDisableVerSync,
				768, 576);
		
		this.createPorchVbiFrames();	
		
		horShift = 0;
		verShift = 0;
	}
	
	private void createPorchVbiFrames() {
		porchLine = new BufferedImage(176, 1, BufferedImage.TYPE_3BYTE_BGR);
		vbiLine = new BufferedImage(944, 1, BufferedImage.TYPE_3BYTE_BGR);
		vbiLine2 = new BufferedImage(768, 1, BufferedImage.TYPE_3BYTE_BGR);
		

		BufferedImage orangeFrame = new BufferedImage(50, 1, BufferedImage.TYPE_3BYTE_BGR);			
		Graphics2D graphics = orangeFrame.createGraphics();
		graphics.setColor(Color.orange);
		graphics.fillRect( 0, 0, orangeFrame.getWidth(), orangeFrame.getHeight());		
			
		this.porchLine.getRaster().setPixels(100, 0, 50, 1, 
				                              orangeFrame.getRaster().getPixels(0, 0, 50, 1, new int[50 * 3]));
		
		BufferedImage greenFrame = new BufferedImage(768, 1, BufferedImage.TYPE_3BYTE_BGR);			
		graphics = greenFrame.createGraphics();
		graphics.setColor(Color.green);
		graphics.fillRect( 0, 0, greenFrame.getWidth(), greenFrame.getHeight());	
		
		this.vbiLine.getRaster().setPixels(176, 0, 768, 1, 
				greenFrame.getRaster().getPixels(0, 0, 768, 1, new int[768 * 3]));
		
		graphics.dispose();		
	}

	@Override
	public BufferedImage transform(BufferedImage img) {	
		frameLuxcrypt++;
		img = this.convertToType(img, BufferedImage.TYPE_3BYTE_BGR);
		
		if (img.getWidth() != 768 || img.getHeight() != 576) {
			img = this.getScaledImage(img, 768, 576);
		}
		
		if(LuxcryptConf.selectedFrameStart <= frameLuxcrypt
			&& LuxcryptConf.selectedFrameEnd >= frameLuxcrypt) {
			frameShifted++;
			return encodeToLuxcrypt(img);
		}
		else {
			frameShifted = 0;
			return img;
		}		
	}
	
	private BufferedImage encodeToLuxcrypt(BufferedImage image) {		
		JobConfig.setInputImage(image);
		
		//check shift X and Y
		if(shiftX != 0 || shiftY !=0) {
			image = shift.transform(image, shiftX, shiftY);
		}
		
		if(LuxcryptConf.isInverse) {
			image = inverseColors(image);
		}
		
	    image = scanImage(image);		
		
		return image;
	}
	
	
//	private BufferedImage scanImage_(BufferedImage image) {
//		for(int i = 0; i < 625; i++) {			
//			if(i < 576) {
//				videoLine = new BufferedImage(944, 1, BufferedImage.TYPE_3BYTE_BGR);
//				
//				videoLine.getRaster().setPixels(0, 0, 176, 1, 
//                        porchLine.getRaster().getPixels(0, 0, 176, 1, new int[176 * 3]));
//
//				videoLine.getRaster().setPixels(176, 0, 768, 1, 
//						image.getRaster().getPixels(0, i, 768, 1, new int[768 * 3]));
//				
//				scanImage2(videoLine, i);	
//			}
//			else if(i >= 576 && i < 625) {				
//				scanImage2(vbiLine, i);	
//			}	
//		}		
//
//		return beamScan.getFrame();
//	}
//	
//	private void scanImage2(BufferedImage image, int i) {			
//			if(frameShifted == 1 && LuxcryptConf.isDisableVerSync && i == 0) {
//				verShift = (int)(Math.random() * 300) + 20;
//	    		i = verShift;	    		
//			}
//			
//			if(i < 576) {
//			    if(frameShifted == 1 && i == verShift) {
//			    	
//			    	if(!LuxcryptConf.isDisableHorSync) {
//			    		horShift = 0;
//			    	}
//			    	else {
//			    		horShift = (int)(Math.random() * 300) + 50;
//			    		//horShift = (int)(Math.random() * 384) + 1;
//			    	}
//			    	
//					videoLine = new BufferedImage(image.getWidth() - horShift, 1, BufferedImage.TYPE_3BYTE_BGR);
//					//videoLine = new BufferedImage(384 - horShift, 1, BufferedImage.TYPE_3BYTE_BGR);
//					
//					videoLine.getRaster().setPixels(0, 0, videoLine.getWidth(), 1, 
//							image.getRaster().getPixels(0 + horShift, 0, image.getWidth() - horShift, 1, 
//									new int[(image.getWidth() - horShift) * 3]));
//			    }
//			    else {
//			    	horShift = 0;
//			    	verShift = -1;
//					videoLine = new BufferedImage(image.getWidth() - horShift, 1, BufferedImage.TYPE_3BYTE_BGR);
//					
//					videoLine.getRaster().setPixels(0, 0, videoLine.getWidth(), 1, 
//							image.getRaster().getPixels(0 + horShift, 0, image.getWidth() - horShift, 1, 
//									new int[(image.getWidth() - horShift) * 3]));
//			    }
//				
//				beamScan.addLine(videoLine, false);	
//			}
//			else {
//				if(LuxcryptConf.isDisableVerSync) {
//					beamScan.addLine(vbiLine, true);	
//				}				
//			}
//		
//
//		//return beamScan.getFrame();
//	}
//	
//	private BufferedImage scanImage3(BufferedImage image) {
//		for(int i = 0; i < 576; i++) {	
//			
//			if(frameShifted == 1 && LuxcryptConf.isDisableVerSync && i == 0) {
//				verShift = (int)(Math.random() * 300) + 20;
//	    		i = verShift;	    		
//			}
//			
//			if(i < 576) {
//			    	if(!LuxcryptConf.isDisableHorSync) {
//			    		horShift = 0;
//			    	}
//			    	else {
//			    		shiftBlock++;
//			    		horShift = 0; 
//			    		
//			    		if(shiftBlock == 23040) {
//			    			shiftBlock = 0;
//				    		horShift = 2;
//				    		}
//
//			    	}
//			    	
//					videoLine = new BufferedImage(image.getWidth() - horShift, 1, BufferedImage.TYPE_3BYTE_BGR);
//					//videoLine = new BufferedImage(384 - horShift, 1, BufferedImage.TYPE_3BYTE_BGR);
//					
//					videoLine.getRaster().setPixels(0, 0, videoLine.getWidth(), 1, 
//							image.getRaster().getPixels(0 + horShift, i, image.getWidth() - horShift, 1, 
//									new int[(image.getWidth() - horShift) * 3]));
//			    
//
//				
//				beamScan.addLine(videoLine, false);	
//			}
//			else {
//				if(LuxcryptConf.isDisableVerSync) {
//					beamScan.addLine(vbiLine2, true);	
//				}				
//			}
//		}		
//
//		return beamScan.getFrame();
//	}
	
	
	private BufferedImage scanImage(BufferedImage image) {
		int horShift = 0;
		int verShift = 0;
		
		for(int i = 0; i < 576; i++) {	
			
			//if(frameShifted == 1 && LuxcryptConf.isDisableVerSync && i == 0) {
			if(LuxcryptConf.isDisableVerSync && i == 0) {
				verShift = getVerValue(); // (int)(Math.random() * 300) + 20;
	    		i = verShift;	    		
			}
			
			if(i < 576) {
				//if(frameShifted == 1 && i == verShift) {
			    if(i == verShift) {
			    	
			    	if(!LuxcryptConf.isDisableHorSync) {
			    		horShift = 0;
			    	}
			    	else {
			    		horShift = getHorValue(); //5; // (int)(Math.random() * 700) + 50;
			    		//horShift = (int)(Math.random() * 384) + 1;
			    	}
			    	
					videoLine = new BufferedImage(image.getWidth() - horShift, 1, BufferedImage.TYPE_3BYTE_BGR);
					//videoLine = new BufferedImage(384 - horShift, 1, BufferedImage.TYPE_3BYTE_BGR);
					
					videoLine.getRaster().setPixels(0, 0, videoLine.getWidth(), 1, 
							image.getRaster().getPixels(0 + horShift, i, image.getWidth() - horShift, 1, 
									new int[(image.getWidth() - horShift) * 3]));
			    }
			    else {
			    	horShift = 0;
			    	verShift = -1;
					videoLine = new BufferedImage(image.getWidth() - horShift, 1, BufferedImage.TYPE_3BYTE_BGR);
					
					videoLine.getRaster().setPixels(0, 0, videoLine.getWidth(), 1, 
							image.getRaster().getPixels(0 + horShift, i, image.getWidth() - horShift, 1, 
									new int[(image.getWidth() - horShift) * 3]));
			    }
				
				beamScan.addLine(videoLine, false);	
			}
			else {
				if(LuxcryptConf.isDisableVerSync) {
					beamScan.addLine(vbiLine2, true);	
				}				
			}
		}		

		return beamScan.getFrame();
	}
	
	private int getHorValue() {		
		cptHorShift++;
		if (cptHorShift == 100) {
			cptHorShift = 1;
		}
		
		if(cptHorShift == 1) {
			return 5;
		}		
	
		return 0;

	}
	
	private int getVerValue() {		
		cptVerShift++;
		
		if (cptVerShift == 100) {
			cptVerShift = 1;
		}
		
		if(cptVerShift == 1) {
			return 5;
		}
		
		
		return 0;
	}
	
	private BufferedImage inverseColors(BufferedImage img) {		
		WritableRaster raster = img.getRaster();
		double[] tabPixel = new double[3];
		double[] pixArray = new double[3];
		
		for (int i = 0; i < img.getWidth(); i++) {
			for (int j = 0; j < img.getHeight(); j++) {
				tabPixel = raster.getPixel(i, j, pixArray);
				
				tabPixel[0] = 255 - tabPixel[0];
				tabPixel[1] = 255 - tabPixel[1];
				tabPixel[2] = 255 - tabPixel[2];
				
				raster.setPixel(i, j, tabPixel);				
			}
		}	
		
		return img;		
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
	public void closeFileData() {
		// TODO Auto-generated method stub
		
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
		return "Luxcrypt";
	}

	@Override
	public boolean isInsideRangeSliderFrames() {
		if(JobConfig.frameCount <= LuxcryptConf.selectedFrameEnd) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	/**
	 * Convert a source image to a desired BufferedImage type
	 * @param sourceImage the image source
	 * @param targetType the target type
	 * @return a converted BufferedImage
	 */
	private BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
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

}
