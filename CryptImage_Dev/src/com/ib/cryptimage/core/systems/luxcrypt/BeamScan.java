package com.ib.cryptimage.core.systems.luxcrypt;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.LinkedList;

public class BeamScan {

	private boolean isHorSync;
	private boolean isVerSync;
	
	private int valX;
	private int valY;
	
	private LinkedList<BufferedImage> readyFrames;
	private LinkedList<BufferedImage> bufferPixels;

	
	private BufferedImage currentFrame;
	private BufferedImage blackFrame;
	private WritableRaster rasterCurrentFrame;
	private WritableRaster rasterBuffer;
	
	private int pixelsLine;
	private int remainingPixels;
		
	private int width;
	private int height;
	


	public BeamScan(boolean isHorSync, boolean isVerSync, int width, int height) {
		this.isHorSync = isHorSync;
		this.isVerSync = isVerSync;
		
		this.width = width;
		this.height = height;
		
		this.valX = 0;
		this.valY = 0;
		
		currentFrame = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		rasterCurrentFrame = currentFrame.getRaster();
		
		blackFrame = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);		
		
		this.readyFrames = new LinkedList<BufferedImage>();
		this.bufferPixels = new LinkedList<BufferedImage>();
	}
	
	public void addLine(BufferedImage buffImage, boolean isVbiLine) {				
		/*
		 * if(isHorSync && isVerSync) { if(!isVbiLine) {
		 * rasterCurrentFrame.setPixels(valX, valY, 768, 1,
		 * buffImage.getRaster().getPixels(176, 0, 768, 1, new int[768 * 3]));
		 * 
		 * valX += 768; checkValX(); } } else if(!isHorSync && isVerSync) {
		 * if(!isVbiLine) { useBuffer(); drawLine(buffImage); updateBuffer(buffImage); }
		 * }
		 */
		useBuffer();
        drawLine(buffImage);
        updateBuffer(buffImage);
		
	}

	private void useBuffer() {
		if(this.bufferPixels.size() > 0) {
			drawLine(this.bufferPixels.getFirst());
			this.bufferPixels.removeFirst();			
		}
	}
	
	private void drawLine(BufferedImage img) {		
		if((img.getWidth() + valX ) > width) {
			pixelsLine = width - valX;
			remainingPixels = img.getWidth() - pixelsLine;			
		}
		else {
			pixelsLine = img.getWidth();
			remainingPixels = 0;
		}
		
		rasterCurrentFrame.setPixels(valX, valY, pixelsLine, 1,
	    		img.getRaster().getPixels(0, 0, pixelsLine, 1, new int[pixelsLine * 3]));		
	   		
		valX += pixelsLine;		
		checkValX();		
	}
	
	private void updateBuffer(BufferedImage img) {
		if(remainingPixels > 0) {
			BufferedImage newBufferLine = new BufferedImage(this.remainingPixels, 1, BufferedImage.TYPE_3BYTE_BGR);
			rasterBuffer = newBufferLine.getRaster();
			
			rasterBuffer.setPixels(0, 0, this.remainingPixels, 1,
					img.getRaster().getPixels(img.getWidth() - this.remainingPixels, 0,
							   this.remainingPixels, 1, new int[this.remainingPixels * 3]));
			
			this.bufferPixels.addLast(newBufferLine);	
		}	
	}

	private void checkValX() {
		if(valX == width) {
			valX = 0;
		
		    valY++;		
			
			checkValY();
		}
	}
	
	private void checkValY() {
		if(valY == height) {
			valY = 0;
			
			BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);					

			newImage.getGraphics().drawImage(currentFrame, 0, 0, null);
			currentFrame = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
			rasterCurrentFrame = currentFrame.getRaster();
			
			this.readyFrames.addLast(newImage);							
		}
	}	
	
	public BufferedImage getFrame() {
		if(this.readyFrames.size() > 0) {
			BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);					

			newImage.getGraphics().drawImage(this.readyFrames.getFirst(), 0, 0, null);
			
			this.readyFrames.removeFirst();
			
			return newImage;			
		}
		else {
			return this.blackFrame;
		}
	}
	
	public boolean isFrameReady() {
		if(this.readyFrames.size() > 0) {
			return true;
		}
		else {
			return false;
		}		
	}
	
	
}
