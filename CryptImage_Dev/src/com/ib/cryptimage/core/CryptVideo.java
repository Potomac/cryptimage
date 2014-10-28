/**
 * This file is part of	CryptImage_Dev.
 *
 * CryptImage_Dev is free software: you can redistribute it and/or modify
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
 * along with CryptImage_Dev.  If not, see <http://www.gnu.org/licenses/>
 * 
 * 29 sept. 2014 Author Mannix54
 */



package com.ib.cryptimage.core;


import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.ib.cryptimage.gui.VideoPlayer;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.ToolFactory;


public class CryptVideo {		
	private String outputFilename;
	private int keyWord;	
	private BufferedImage buff;
	private int height;
	private int width;	
	private boolean isDecoding;
	private boolean strictMode;
	private int positionSynchro;
	
	private VideoRecorder vid;
	private int videoLengthFrames;
	private double timeBase;
	private int frameCount;
	private int audienceLevel;
	
	private Discret11 discret;
	private SimpleDiscret11 simpleDiscret;
	private double perc1;
	private double perc2;
	
	private int step1 =0;
	private int step20 = 0;
	private int step40 =0;
	private int step60 = 0;
	private int step80 = 0;
	private int step100 = 0;
	private FramesPlayer frmv;
	
	
	private VideoPlayer vidPlayer;	
	

	public CryptVideo(FramesPlayer frmv){	
		this.audienceLevel = frmv.getJob().getAudienceLevel();
		this.frameCount = 0;
		this.positionSynchro = frmv.getJob().getPositionSynchro();
		this.strictMode = frmv.getJob().isStrictMode();
		this.outputFilename = frmv.getJob().getOutput_file();
		this.keyWord = frmv.getJob().getDiscret11Word();		
		this.isDecoding = frmv.getJob().isWantDec();
		this.videoLengthFrames = frmv.getJob().getVideo_frame();
		this.perc1 = frmv.getJob().getPerc1();
		this.perc2 = frmv.getJob().getPerc2();		
		this.frmv = frmv;
		
		int mode;
		if(this.isDecoding){
			mode = Discret11.MODE_DEC;
		}
		else{
			mode = Discret11.MODE_ENC;			
		}
		
			
		IMediaReader reader = ToolFactory.makeReader(frmv.getJob().getInput_file());
		reader.readPacket();
		this.width =reader.getContainer().getStream(0).getStreamCoder().getWidth();
		this.height = reader.getContainer().getStream(0).getStreamCoder().getHeight();
		double frameRate =  reader.getContainer().getStream(0).getStreamCoder().getFrameRate().getValue();
		
		if (frmv.getJob().isWantPlay()){
			vidPlayer = new VideoPlayer(frameRate, this.frmv.getJob());
		}
		
		//System.out.println((reader.getContainer().getDuration()/1000/1000)*frameRate);
		
		this.timeBase = 1000d/frameRate;		
		
		String info = "_crypt_";
		 if (this.isDecoding){
			 info = "_decrypt_";
		 }
		
		 if(this.strictMode){ // we use "stric mode discret 11", so we resize the video to 768x576 pixels
			 this.width = 768;//frmv.getJob().getsWidth();
			 this.height = 576;
		 }
		 
		if (this.strictMode) {
			discret = new Discret11(this.keyWord, mode, this.audienceLevel,
					this.perc1, this.perc2);
		} else {
			simpleDiscret = new SimpleDiscret11(this.keyWord, mode,
					this.height, this.width);
		}
		
		if(frmv.getJob().isWantPlay() !=true){
	    vid = new VideoRecorder(outputFilename + info + keyWord + "_audience_" 
		+ this.audienceLevel + ".mp4", width,
				height, frmv.getJob().getVideoBitrate(), frmv.getJob().getVideoCodec(),
				frmv.getJob().getsWidth(), frameRate);
		}
	}
	
	public void addDisplayFrameEnc(BufferedImage buff, int pos, int timingFrame){
		BufferedImage save;
		save = deepCopy(buff);	
		
		frameCount++;
		if (frameCount < this.positionSynchro){
			//we add a non decrypted frame because we are not at the synchro frame ( line 310 )
			//vid.addFrame(buff,this.timeBase * timingFrame);
			vidPlayer.addImage(buff);
			vidPlayer.showImage();
			updateProgress("encoded");
			//System.out.println("Frame non decoded : " + (timingFrame+1) + " /" +this.videoLengthFrames);
		}
		else{
		BufferedImage bi;
		
		if(this.strictMode){
			bi = this.discret.transform(buff);
			save = getScaledImage(save, this.width, 576);
		}
		else
		{
			bi = this.simpleDiscret.transform(buff);
		}
			
		if( vidPlayer.isInverse() == true){	
			vidPlayer.addImage(save);
		}
		else{			
			vidPlayer.addImage(bi);
		}
		
		vidPlayer.showImage();
		if(this.frmv.getJob().isStop()){
			vidPlayer.close();
		}
		updateProgress("encoded");
		//System.out.println("Frames encoded : " + (timingFrame+1) + " /" +this.videoLengthFrames);
		}
	}
	
	public void addDisplayFrameDec(BufferedImage buff, int pos, int timingFrame){		
		
		BufferedImage save;		
		save = deepCopy(buff);
		
		frameCount++;
		if (frameCount < this.positionSynchro){
			//we add a non decrypted frame because we are not at the synchro frame ( line 310 )
			//vid.addFrame(buff,this.timeBase * timingFrame);
			vidPlayer.addImage(buff);
			vidPlayer.showImage();
			updateProgress("decoded");
			//System.out.println("Frame non decoded : " + (timingFrame+1) + " /" +this.videoLengthFrames);
		}
		else{
		BufferedImage bi;		
		if(this.strictMode){			
			bi = this.discret.transform(buff);		
			//save = getScaledImage(save, 768, 576);
		}
		else
		{
			bi = this.simpleDiscret.transform(buff);
		}
		
		if(vidPlayer.isInverse() == true){
			vidPlayer.addImage(save);
		}
		else{
			vidPlayer.addImage(bi);
		}
		
		vidPlayer.showImage();
		if(this.frmv.getJob().isStop()){
			vidPlayer.close();
		}
		updateProgress("decoded");
		//System.out.println("Frames decoded : " + (timingFrame+1) + " /" +this.videoLengthFrames);
		}
	}
	
	
	
	public void addFrameEnc(BufferedImage buff, int pos, int timingFrame){			
		frameCount++;
		if (frameCount < this.positionSynchro){
			if(this.strictMode){
				buff = getScaledImage(buff, 768,576);
			}
			//we add a non decrypted frame because we are not at the synchro frame ( line 310 )
			vid.addFrame(buff,this.timeBase * ( timingFrame  ));
			updateProgress("Encoded");
			//System.out.println("Frame non decoded : " + (timingFrame+1) + " /" +this.videoLengthFrames);
		}
		else {
		BufferedImage bi;
		if(this.strictMode){
			bi = this.discret.transform(buff);
		}
		else
		{
			bi = this.simpleDiscret.transform(buff);
		}
		//bi = new CryptImage(buff, pos, this.strictMode).getCryptDiscret11(keyWord);		
		vid.addFrame(bi, this.timeBase * ( timingFrame ));
		updateProgress("encoded");
		}
		//System.out.println("Frames encoded : " + (timingFrame+1) + " /" +this.videoLengthFrames);
	}
	
	public void addFrameDec(BufferedImage buff, int pos, int timingFrame){		
		frameCount++;
		if (frameCount < this.positionSynchro){
			if(this.strictMode){
				buff = getScaledImage(buff, 768,576);
			}
			//we add a non decrypted frame because we are not at the synchro frame ( line 310 )
			vid.addFrame(buff,this.timeBase * ( timingFrame  ));
			updateProgress("decoded");
			//System.out.println("Frame non decoded : " + (timingFrame+1) + " /" +this.videoLengthFrames);
		}
		else{
		BufferedImage bi;
		if(this.strictMode){
			bi = this.discret.transform(buff);
		}
		else
		{
			bi = this.simpleDiscret.transform(buff);
		}
		//bi = new CryptImage(buff, pos,this.strictMode).getDecryptDiscret11WithCode(keyWord);
		vid.addFrame(bi, this.timeBase * timingFrame);
		updateProgress("decoded");
		//System.out.println("Frames decoded : " + (timingFrame+1) + " /" +this.videoLengthFrames);
		}
	}
	
	public void closeVideo(){
		vid.closeVideo();
		if(isDecoding){
			if(this.frmv.getJob().isHasGUI()){
				this.frmv.getJob().getGui().getTextInfos()
				.setText(this.frmv.getJob().getGui().getTextInfos().getText() 
						+ "\n\r"
						+ "Decrypted video file : " + this.outputFilename +"_decrypt_" +
						this.keyWord + "_audience_" + this.audienceLevel +".mp4");
			}
			System.out.println("Decrypted video file : " + this.outputFilename +"_decrypt_" +
					this.keyWord + "_audience_" + this.audienceLevel +".mp4");
		}
		else
		{
			if(this.frmv.getJob().isHasGUI()){
				this.frmv.getJob().getGui().getTextInfos()
				.setText(this.frmv.getJob().getGui().getTextInfos().getText() 
						+ "\n\r"
						+ "Crypted video file : " + this.outputFilename + "_crypt_" +
						this.keyWord + "_audience_" + this.audienceLevel + ".mp4");
			}
			System.out.println("Crypted video file : " + this.outputFilename + "_crypt_" +
		this.keyWord + "_audience_" + this.audienceLevel + ".mp4");
		}		
	}
	
	public void saveDatFileVideo(){		
		if(isDecoding !=true){
		buff = new BufferedImage(this.width,
				this.height, 12);
		
		//int [][] delayTab = cryptImg.getDelayTabCrypt();		
		
		//save the data file
		String messDebug = "";
		if(this.strictMode){
			messDebug = this.discret.getsDebugLines();
		}
		else
		{
			messDebug = this.simpleDiscret.getsDebugLines();
		}
		
		
		try {
			File dataFile = new File(this.outputFilename + "_crypt_" + this.keyWord +
					"_audience_" + this.audienceLevel + ".txt");
			dataFile.createNewFile();
			FileWriter ffw = new FileWriter(dataFile);
			BufferedWriter bfw = new BufferedWriter(ffw);	
			bfw.write("11 bits keyword : " + this.keyWord + "\r\n");
			bfw.write("Audience level : " + this.audienceLevel + "\r\n");
			bfw.write("File : " + this.outputFilename +"_crypt_" +
					this.keyWord + "_audience_" + this.audienceLevel + ".mp4" +"\r\n");
			bfw.write("debug lines : " + "\r\n" + messDebug);
			bfw.close();
			if(this.frmv.getJob().isHasGUI()){
				this.frmv.getJob().getGui().getTextInfos()
				.setText(this.frmv.getJob().getGui().getTextInfos().getText() 
						+ "\n\r"
						+ "Data report : " + this.outputFilename
						+ "_crypt_" + this.keyWord + "_audience_" 
						+ this.audienceLevel + ".txt");
			}
			System.out.println("Data report : " + this.outputFilename
					+ "_crypt_" + this.keyWord + "_audience_" 
					+ this.audienceLevel + ".txt");
		} catch (IOException e) {
			System.out
					.println("I/O error during the write of the report file");
			System.exit(1);
		}
		}
	}

	
	/**
	 * update the status in the console for encoding/decoding process creation of the video
	 * @param step the type of process ( encoded or decoded )
	 */
	private void updateProgress(String step){
		int progress = (int) (((double) this.frameCount / (double) this.videoLengthFrames) * 100);

		if (this.frmv.getJob().isHasGUI() == true) {
			frmv.getJob().getGui().getProgress().setValue(this.frameCount );
			frmv.getJob().getGui().getTextInfos().setText("Frames " + step + " " + frameCount +"/" + this.videoLengthFrames);
//			if (progress == 1 && step1 == 0) {
//				frmv.getJob().getGui().getProgress().setValue((int) (0.01 * this.videoLengthFrames));
//				frmv.getJob().getGui().getTextInfos().setText("Frames " + step + " 1%");
//				step1 = 1;	
//			}
//
//			if (progress == 20 && step20 == 0) {
//				frmv.getJob().getGui().getProgress().setValue((int) (0.20 * this.videoLengthFrames));
//				frmv.getJob().getGui().getTextInfos().setText("Frames " + step + " 20%");				
//				step20 = 1;	
//			}
//			if (progress == 40 && step40 == 0) {
//				frmv.getJob().getGui().getProgress().setValue((int) (0.40 * this.videoLengthFrames));
//				frmv.getJob().getGui().getTextInfos().setText("Frames " + step + " 40%");				
//				step40 = 1;	
//			}
//			if (progress == 60 && step60 == 0) {
//				frmv.getJob().getGui().getProgress().setValue((int) (0.60 * this.videoLengthFrames));
//				frmv.getJob().getGui().getTextInfos().setText("Frames " + step + " 60%");				
//				step60 = 1;
//				frmv.getJob().getGui().getFrame().repaint();
//				frmv.getJob().getGui().getFrame().revalidate();
//			}
//			if (progress == 80 && step80 == 0) {
//				frmv.getJob().getGui().getProgress().setValue((int) (0.80 * this.videoLengthFrames));
//				frmv.getJob().getGui().getTextInfos().setText("Frames " + step + " 80%");				
//				step80 = 1;		
//			}
//			if (progress == 100 && step100 == 0) {
//				frmv.getJob().getGui().getProgress().setValue((int) (1 * this.videoLengthFrames));
//				frmv.getJob().getGui().getTextInfos().setText("Frames " + step + " 100%");				
//				step100 = 1;
//			}			
		}
		else {
			if (progress == 1 && step1 == 0) {
				System.out.println("Frames " + step + " 1%");
				step1 = 1;
			}

			if (progress == 20 && step20 == 0) {
				System.out.println("Frames " + step + " 20%");
				step20 = 1;
			}
			if (progress == 40 && step40 == 0) {
				System.out.println("Frames " + step + " 40%");
				step40 = 1;
			}
			if (progress == 60 && step60 == 0) {
				System.out.println("Frames " + step + " 60%");
				step60 = 1;
			}
			if (progress == 80 && step80 == 0) {
				System.out.println("Frames " + step + " 80%");
				step80 = 1;
			}
			if (progress == 100 && step100 == 0) {
				System.out.println("Frames " + step + " 100%");
				step100 = 1;
			}
		}
	}
	
	public int getVideoLengthFrames() {
		return videoLengthFrames;
	}
	
	public BufferedImage getBuff() {
		return buff;
	}

	public void setBuff(BufferedImage buff) {
		this.buff = buff;
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
	    double factor = 1.0d;	    
	    double shiftw = 1d;
	    
	    if(src.getWidth()==720 && src.getHeight()==576 ){
	    	shiftw = (double)src.getWidth()/(double)w; // case of if width = 720 and height = 576
	    }
//	    else if(src.getWidth()==768 && src.getHeight() == 576 && w == 720){
//	    	shiftw = (double)src.getWidth()/720d;	    	
//	    	//shiftw = 768d/(double)src.getWidth();
//	    	//finalw = (int)(finalh * shiftw);
//	    }
//	    else if(src.getWidth()==720 && src.getHeight()!=576 && w == 720){
//	    	//shiftw = (double)src.getWidth()/768d;	    	
//	    	shiftw = 768d/(double)src.getWidth();
//	    	//finalw = (int)(finalh * shiftw);
//	    }
//	    else if(w ==720){
//	    	//shiftw = (double)src.getWidth()/768d;	    	
//	    	shiftw = 768d/720d;
//	    	//finalw = (int)(finalh * shiftw);
//	    }
	    
	    if(src.getWidth() > src.getHeight()){
	        factor = ((double)src.getHeight()/(double)src.getWidth());
	        finalh = (int)(finalw * factor * shiftw);	       
	    }else{
	        factor = ((double)src.getWidth()/(double)src.getHeight());
	        finalw = (int)(finalh * factor);
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
	
}