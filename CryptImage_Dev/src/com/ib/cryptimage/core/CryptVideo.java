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

import javax.swing.JOptionPane;

import com.ib.cryptimage.gui.VideoPlayer;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.IAudioSamples;


public class CryptVideo {		
	private String outputFilename;
	private int keyWord;
	private int keyWord11;
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
	
	private double framerate;
	
	public CryptVideo(FramesPlayer frmv) {		
		this.audienceLevel = frmv.getJob().getAudienceLevel();
		this.frameCount = 0;
		this.positionSynchro = frmv.getJob().getPositionSynchro();
		this.strictMode = frmv.getJob().isStrictMode();
		
		if(frmv.getJob().isHorodatage()){			
			File file = new File(frmv.getJob().getOutput_file());
			String fileName = frmv.getJob().getDateTime() + "_" +
					file.getName();			
			this.outputFilename = file.getParent() + File.separator + fileName;
		}
		else{
			this.outputFilename = frmv.getJob().getOutput_file();
		}
		this.keyWord = frmv.getJob().getWord16bits();
		this.isDecoding = frmv.getJob().isWantDec();
		this.videoLengthFrames = frmv.getJob().getVideo_frame();
		this.perc1 = frmv.getJob().getPerc1();
		this.perc2 = frmv.getJob().getPerc2();
		
		this.frmv = frmv;
		this.frmv.getJob().setReadyTransform(false);

		int mode;
		if (this.isDecoding) {
			mode = Discret11.MODE_DEC;
		} else {
			mode = Discret11.MODE_ENC;
		}

		IMediaReader reader = ToolFactory.makeReader(frmv.getJob()
				.getInput_file());
		reader.readPacket();
		this.width = reader.getContainer().getStream(0).getStreamCoder()
				.getWidth();
		this.height = reader.getContainer().getStream(0).getStreamCoder()
				.getHeight();
		double frameRate = reader.getContainer().getStream(0).getStreamCoder()
				.getFrameRate().getValue();

		if (frmv.getJob().isWantPlay()) {
			vidPlayer = new VideoPlayer(frameRate, this.frmv.getJob());
		}		

		this.timeBase = 1000d / frameRate;
		this.framerate = frameRate;

		String info = "_c";
		if (this.isDecoding) {
			info = "_d";
		}

		if (this.strictMode) { // we use "stric mode discret 11", so we resize
								// the video to 768x576 pixels
			this.width = 768;// frmv.getJob().getsWidth();
			this.height = 576;
		}

		if (this.strictMode) {
			discret = new Discret11(this.keyWord, mode, this.audienceLevel,
					this.perc1, this.perc2);
			this.keyWord11 = discret.getKey11bits();
		} else {
			simpleDiscret = new SimpleDiscret11(this.keyWord, this.audienceLevel, mode,
					this.height, this.width);
			this.keyWord11 = simpleDiscret.getKey11bits();
		}

		if (frmv.getJob().isWantPlay() != true) {

			try {
				vid = new VideoRecorder(outputFilename + info + keyWord + "-" 
			+ this.keyWord11 
						+ "_a"
						+ this.audienceLevel + "_k" +
						frmv.getJob().getCode()
						+ "."
						+ frmv.getJob().getExtension(), width, height,
						frameRate, frmv.getJob());

			} catch (Exception e) {
				JOptionPane.showMessageDialog(
						null,
						"Une erreur de type exception s'est produite :"
								+ e.getMessage(), "Erreur du programme",
						JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
		}
	}
	
	public void addDisplayFrameEnc(BufferedImage buff, int pos, int timingFrame){
		BufferedImage save;
		save = deepCopy(buff);	
		
		frameCount++;
		if (frameCount < this.positionSynchro){
			//we add a non decrypted frame because we are not at the synchro frame ( line 310 )
			vidPlayer.addImage(buff);			
			vidPlayer.showImage();
			updateProgress("codage");			
		}
		else{
		BufferedImage bi = save;
		
		if(this.strictMode){
			save = getScaledImage(save, this.width, 576);
			if( vidPlayer.isInverse() == false){	
			bi = this.discret.transform(buff);
			}			
		}
		else
		{	if( vidPlayer.isInverse() == false){	
				bi = this.simpleDiscret.transform(buff);
			}
		}
			
		if( vidPlayer.isInverse() == true){	
			vidPlayer.addImage(save);
		}
		else{			
			vidPlayer.addImage(bi);
		}
		
		vidPlayer.showImage();
		if(this.frmv.getJob().isStop() || this.frameCount == this.getVideoLengthFrames()){
			vidPlayer.close();
			vidPlayer.closeJavaSound();
		}
		updateProgress("codage");
		//System.out.println("Frames encoded : " + (timingFrame+1) + " /" +this.videoLengthFrames);
		}
	}
	
	public void addDisplayFrameDec(BufferedImage buff, int pos, int timingFrame){		
		
		BufferedImage save;		
		save = deepCopy(buff);
		
		frameCount++;
		if (frameCount < this.positionSynchro){
			//we add a non decrypted frame because we are not at the synchro frame ( line 310 )
			vidPlayer.addImage(buff);
			vidPlayer.showImage();
			updateProgress("décodage");			
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
		
		if(vidPlayer.isInverse() == true){
			vidPlayer.addImage(save);
		}
		else{
			vidPlayer.addImage(bi);
		}
		
		vidPlayer.showImage();
		if(this.frmv.getJob().isStop() || this.frameCount == this.getVideoLengthFrames()){
			vidPlayer.close();
			vidPlayer.closeJavaSound();
		}
		updateProgress("décodage");		
		}
	}

	public void addAudioFrame(IAudioSamples sample) {
		if (!this.frmv.getJob().isWantPlay() 
				&& this.frmv.getJob().isDisableSound() == false) {
			vid.addAudioFrame(sample);
		} else if (this.frmv.getJob().isWantPlay() ){
			//vidPlayer.playJavaSound(sample);			
		}
	}
	
	public void addFrameEnc(BufferedImage buff, int pos, int timingFrame){			
		frameCount++;
		if (frameCount < this.positionSynchro) {
			if (this.strictMode) {
				buff = getScaledImage(buff, 768, 576);
			}
			// we add a non decrypted frame because we are not at the synchro
			// frame ( line 310 )
			vid.addFrame(buff, this.timeBase * (timingFrame));
			updateProgress("codage");
			// System.out.println("Frame non decoded : " + (timingFrame+1) +
			// " /" +this.videoLengthFrames);
		} else {
			this.frmv.getJob().setReadyTransform(true);
			BufferedImage bi;
			if (this.strictMode) {
				bi = this.discret.transform(buff);
			} else {
				bi = this.simpleDiscret.transform(buff);
			}
			// bi = new CryptImage(buff, pos,
			// this.strictMode).getCryptDiscret11(keyWord);
			vid.addFrame(bi, this.timeBase * (timingFrame));
			updateProgress("codage");
		}
		// System.out.println("Frames encoded : " + (timingFrame+1) + " /"
		// +this.videoLengthFrames);
	}
	
	public void addFrameDec(BufferedImage buff, int pos, int timingFrame){		
		frameCount++;
		if (frameCount < this.positionSynchro) {
			if (this.strictMode) {
				buff = getScaledImage(buff, 768, 576);
			}
			// we add a non decrypted frame because we are not at the synchro
			// frame ( line 310 )
			vid.addFrame(buff, this.timeBase * (timingFrame));
			updateProgress("décodage");
			// System.out.println("Frame non decoded : " + (timingFrame+1) +
			// " /" +this.videoLengthFrames);
		} else {
			this.frmv.getJob().setReadyTransform(true);
			BufferedImage bi;
			if (this.strictMode) {
				bi = this.discret.transform(buff);
			} else {
				bi = this.simpleDiscret.transform(buff);
			}
			// bi = new CryptImage(buff,
			// pos,this.strictMode).getDecryptDiscret11WithCode(keyWord);
			vid.addFrame(bi, this.timeBase * timingFrame);
			updateProgress("décodage");
			// System.out.println("Frames decoded : " + (timingFrame+1) + " /"
			// +this.videoLengthFrames);
		}
	}
	
	public void closeVideo(){
		vid.closeVideo();
		if(isDecoding){
			if(this.frmv.getJob().isHasGUI()){
				this.frmv.getJob().getGui().getTextInfos()
				.setText(this.frmv.getJob().getGui().getTextInfos().getText() 
						+ "\n\r"
						+ "Fichier décodé : " + this.outputFilename +"_d" +
						this.keyWord + "-" + this.keyWord11 +
						"_a" + this.audienceLevel + "_k" +
						frmv.getJob().getCode() + "." + frmv.getJob().getExtension());
			}
			System.out.println("Decrypted video file : " + this.outputFilename +"_d" +
					this.keyWord + "-" + this.keyWord11 +
					"_a" + this.audienceLevel + "_k" +
							frmv.getJob().getCode()
							+  "." + frmv.getJob().getExtension());
		}
		else
		{
			if(this.frmv.getJob().isHasGUI()){
				this.frmv.getJob().getGui().getTextInfos()
				.setText(this.frmv.getJob().getGui().getTextInfos().getText() 
						+ "\n\r"
						+ "Fichier codé : " + this.outputFilename + "_c" +
						this.keyWord + "-" + this.keyWord11 +
						"_a" + this.audienceLevel + "_k" +
								frmv.getJob().getCode()
								+  "." + frmv.getJob().getExtension());
			}
			System.out.println("Crypted video file : " + this.outputFilename + "_c" +
		this.keyWord + "-" + this.keyWord11 + "_a" 
					+ this.audienceLevel + "_k" +
							frmv.getJob().getCode()
							+  "." + frmv.getJob().getExtension());
		}		
	}
	
	public void saveDatFileVideo(){		
		if(isDecoding !=true){
		buff = new BufferedImage(this.width,
				this.height, 12);		
		
		try {
			File dataFile = new File(this.outputFilename + "_c" + this.keyWord +
					"-" + this.keyWord11 +
					"_a" + this.audienceLevel + "_k" +
					frmv.getJob().getCode()
					+  ".txt");
			dataFile.createNewFile();
			FileWriter ffw = new FileWriter(dataFile);
			BufferedWriter bfw = new BufferedWriter(ffw);	
			bfw.write("16 bits keyword : " + this.keyWord + "\r\n");
			bfw.write("Audience level : " + this.audienceLevel + "\r\n");
			bfw.write("11 bits keyword : " + this.keyWord11 + "\r\n");
			bfw.write("serial eprom : " + this.frmv.getJob().getSerial() + "\r\n");
			bfw.write("keyboard code : " + this.frmv.getJob().getCode() + "\r\n");
			bfw.write("P(0) at progressive frame n° : " + this.positionSynchro +"\r\n" );
			bfw.write("Delay 1 : " + this.perc1 * 100 +"%\r\n" );
			bfw.write("Delay 2 : " + this.perc2 * 100 +"%\r\n" );
			bfw.write("Number of frames : " + this.frameCount +"\r\n" );
			bfw.write("video framerate : " + this.framerate +"\r\n" );
			bfw.write("File : " + this.outputFilename +"_c" +
					this.keyWord + 
					"-" + this.keyWord11 + "_a" + this.audienceLevel + "_k" +
							frmv.getJob().getCode()	+ "." 
					+ frmv.getJob().getExtension() +"\r\n");			
			
			bfw.close();
			if(this.frmv.getJob().isHasGUI()){
				this.frmv.getJob().getGui().getTextInfos()
				.setText(this.frmv.getJob().getGui().getTextInfos().getText() 
						+ "\n\r"
						+ "Rapport : " + this.outputFilename
						+ "_c" + this.keyWord 
						+ "-" + this.keyWord11 
						+ "_a" 
						+ this.audienceLevel + "_k" +
						frmv.getJob().getCode()
						+  ".txt");
			}
			System.out.println("Data report : " + this.outputFilename
					+ "_c" + this.keyWord +
					"-" + this.keyWord11 + "_a" 
					+ this.audienceLevel + "_k" +
					frmv.getJob().getCode()
					+ ".txt");
		} catch (IOException e) {
			System.out
					.println("I/O error during the write of the report file");
			System.exit(1);
		}
		}
	}

	
	/**
	 * update the status in the console for encoding/decoding process creation
	 * of the video
	 * 
	 * @param step
	 *            the type of process ( encoded or decoded )
	 */
	private void updateProgress(String step) {
		int progress = (int) (((double) this.frameCount / (double) this.videoLengthFrames) * 100);

		if (this.frmv.getJob().isHasGUI() == true) {
			frmv.getJob().getGui().getProgress().setValue(this.frameCount);
			frmv.getJob()
					.getGui()
					.getTextInfos()
					.setText(
							"Trames en cours de " + step + " " + frameCount
									+ "/" + this.videoLengthFrames);

		} else {
			if (step == "codage") {
				step = "encoded";
			} else {
				step = "decoded";
			}
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
	    
	    if(this.strictMode){
	    	if(src.getHeight()== 576 && src.getWidth() == 768){
	    		return src;
	    	}
	    }
	    
	    if(src.getWidth()==720  && src.getHeight()==576 ){
	    	shiftw = (double)src.getWidth()/(double)w; // case of if width = 720 and height = 576
	    }
	    
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
}