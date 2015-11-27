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
 * http://ibsoftware.free.fr/cryptimage.php
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
import com.ib.cryptimage.core.Device;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.IAudioSamples;


public class CryptVideo {		
	private String outputFilename;
	private int keyWord;
	private String keyWord11;
	private BufferedImage buff;
	private int height;
	private int width;	
	private boolean isDecoding;
	private boolean strictMode;
	private int positionSynchro;
	
	private VideoRecorder vid;
	private int videoLengthFrames;
	private double timeBase;///
	private int frameCount;
	private String audienceLevel;
	
	private Device device;
	
	private double perc1;
	private double perc2;	
		
	private String codePattern;
	private int cycle;
	private String fileAudienceLevel;
	private String fileKeyboardCode;
		
	
	private VideoPlayer vidPlayer;	
	
	private double framerate;///
	
	public CryptVideo() {

		if (JobConfig.getAudienceLevel() == 8) {
			this.audienceLevel = "multicode-"
					+ JobConfig.getMultiCode();
			fileAudienceLevel = "_multicode-"
					+ JobConfig.getMultiCode() + "_cycle" + 
					JobConfig.getCycle();
			this.codePattern = JobConfig.getMultiCode();
			this.cycle = JobConfig.getCycle();
		} else {
			this.audienceLevel = String.valueOf(JobConfig
					.getAudienceLevel());
			fileAudienceLevel = "_a"
					+ String.valueOf(JobConfig.getAudienceLevel());
			this.codePattern = String.valueOf(JobConfig.getAudienceLevel());
			this.cycle = 1;
		}
		
		if(JobConfig.isStrictMode() && JobConfig.isWantDec()== false){
			fileKeyboardCode = "_k" + JobConfig.getCode();
		}
		else {
			fileKeyboardCode = "";
		}

		this.keyWord11 = this.computeAudienceMulti(this.codePattern, JobConfig.getWord16bits());

		this.frameCount = 0;
		this.positionSynchro = JobConfig.getPositionSynchro();
		this.strictMode = JobConfig.isStrictMode();

		if (JobConfig.isHorodatage()) {
			File file = new File(JobConfig.getOutput_file());
			String fileName = JobConfig.getDateTime() + "_"
					+ file.getName();
			this.outputFilename = file.getParent() + File.separator + fileName;
		} else {
			this.outputFilename = JobConfig.getOutput_file();
		}
		this.keyWord = JobConfig.getWord16bits();
		this.isDecoding = JobConfig.isWantDec();
		this.videoLengthFrames = JobConfig.getVideo_frame();
		this.perc1 = JobConfig.getPerc1();
		this.perc2 = JobConfig.getPerc2();

		JobConfig.setReadyTransform(false);

		int mode;
		if (this.isDecoding) {
			mode = 1;
		} else {
			mode = 0;
		}

		IMediaReader reader = ToolFactory.makeReader(JobConfig
				.getInput_file());
		reader.readPacket();
		this.width = reader.getContainer().getStream(0).getStreamCoder()
				.getWidth();
		this.height = reader.getContainer().getStream(0).getStreamCoder()
				.getHeight();
		double frameRate = reader.getContainer().getStream(0).getStreamCoder()
				.getFrameRate().getValue();
		
		this.framerate = frameRate;

		if (JobConfig.isWantPlay()) {
			vidPlayer = new VideoPlayer(frameRate);
		}

		this.timeBase = 1000d/framerate;		

		String info = "_c";
		if (this.isDecoding) {
			info = "_d";
		}

		if (this.strictMode) { // we use "strict mode discret 11", so we resize
								// the video to 768x576 pixels
			this.width = 768;// frmv.getJob().getsWidth();
			this.height = 576;
		}

		
		if (JobConfig.getSystemCrypt() == 0) {
			if (this.strictMode) {
				if (this.isDecoding) {
					if (JobConfig.isWantDecCorrel()) {
						device = new DiscretDecCorrel();
					} else if (JobConfig.isNoBlackBar()) {
						device = new Discret11DecBlack(JobConfig.getWord16bits(), this.perc1, this.perc2);
					} else {
						device = new Discret11Dec(JobConfig.getWord16bits(), this.perc1, this.perc2);
					}
				} else {
					if (JobConfig.isNoBlackBar()) {
						device = new Discret11EncNoBlack(JobConfig.getWord16bits(), this.codePattern, this.cycle,
								this.perc1, this.perc2);
					} else {
						device = new Discret11Enc(JobConfig.getWord16bits(), this.codePattern, this.cycle, this.perc1,
								this.perc2);
					}
				}

			} else { // non strict mode
				if (this.isDecoding) { // decoding in non strict mode
					if (JobConfig.isNoBlackBar()) {
						device = new SimpleDiscret11Black(this.keyWord, Integer.valueOf(this.audienceLevel), mode,
								this.height, this.width, this.perc1,
								this.perc2);
					} else {
						device = new SimpleDiscret11(this.keyWord, Integer.valueOf(this.audienceLevel), mode,
								this.height, this.width,this.perc1,
								this.perc2);
					}
				} else { // encoding in non strict mode
					////
					if (JobConfig.isNoBlackBar()) {
						device = new SimpleDiscret11NoBlack(this.keyWord, Integer.valueOf(this.audienceLevel), mode,
								this.height, this.width, this.perc1,
								this.perc2);
					} else {
						device = new SimpleDiscret11(this.keyWord, Integer.valueOf(this.audienceLevel), mode,
								this.height, this.width, this.perc1,
								this.perc2);
					}
					////
				}
			}
		}
		else { //syster mode
			if(this.isDecoding){
				device = new SysterDec(JobConfig.getTableSyster(),
						JobConfig.getFileDataDecSyster(),
						JobConfig.isWantDecCorrel());
			}
			else {
				device = new SysterEnc(JobConfig.getTableSyster(),
						outputFilename,
						JobConfig.getFileDataEncSyster(),
						JobConfig.getGui().getChkPlayer().isSelected());
			}
		}
		//////

		if (JobConfig.isWantPlay() != true) {
			if (this.isDecoding) {
				try {
					vid = new VideoRecorder(outputFilename + "_dec" + "."
							+ JobConfig.getExtension(), width, height,
							frameRate, JobConfig.getAudioRate());

				} catch (Exception e) {
					JOptionPane.showMessageDialog(
							null,
							"Une erreur de type exception s'est produite :"
									+ e.getMessage(), "Erreur du programme",
							JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
					System.exit(1);
				}
			} else {
				try {
					if(JobConfig.getSystemCrypt() == 0){
					vid = new VideoRecorder(outputFilename + info + keyWord
							+ this.fileAudienceLevel + fileKeyboardCode + "."
							+ JobConfig.getExtension(), width, height,
							frameRate, JobConfig.getAudioRate());
					}
					else {
						vid = new VideoRecorder(outputFilename + info
								+ "syster" + "."
								+ JobConfig.getExtension(), width, height,
								frameRate, JobConfig.getAudioRate());
					}

				} catch (Exception e) {
					JOptionPane.showMessageDialog(
							null,
							"Une erreur de type exception s'est produite :"
									+ e.getMessage(), "Erreur du programme",
							JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
					System.exit(1);
				}
			}
		}
	}
	
	public void addDisplayFrameEnc(BufferedImage buff, int pos, int timingFrame){
		BufferedImage save;
		save = deepCopy(buff);	
		
		frameCount++;
		if (frameCount < this.positionSynchro){			
			vidPlayer.addImage(buff);			
			vidPlayer.showImage();
			device.skipFrame();
			updateProgress("codage");			
		}
		else{
		BufferedImage bi = save;
		
		if(this.strictMode){
			save = getScaledImage(save, this.width, 576);
			if( vidPlayer.isInverse() == false){			
				bi = this.device.transform(buff);
			}			
		}
		else
		{	if( vidPlayer.isInverse() == false){					
				bi = this.device.transform(buff);
			}
		}
			
		if( vidPlayer.isInverse() == true){	
			vidPlayer.addImage(save);
		}
		else{
				if (bi != null) {
					vidPlayer.addImage(bi);
				}
		}
		
		vidPlayer.showImage();
		if(JobConfig.isStop() || this.frameCount == this.getVideoLengthFrames()){
			vidPlayer.close();			
		}
		updateProgress("codage");		
		}
	}
	
	public void addDisplayFrameDec(BufferedImage buff, int pos, long timingFrame){		
		
		BufferedImage save;		
		save = deepCopy(buff);
		
		frameCount++;
		if (frameCount < this.positionSynchro){			
			vidPlayer.addImage(buff);
			vidPlayer.showImage();
			device.skipFrame();
			updateProgress("décodage");			
		}
		else{
		BufferedImage bi;		
		bi = this.device.transform(buff);
		
		if(vidPlayer.isInverse() == true){
			vidPlayer.addImage(save);
		}
		else{
				if (bi != null) {
					vidPlayer.addImage(bi);
				}
		}
		
		vidPlayer.showImage();
		if(JobConfig.isStop() || this.frameCount == this.getVideoLengthFrames()){
			vidPlayer.close();			
		}
		updateProgress("décodage");		
		}
	}

	public void addAudioFrame(IAudioSamples sample) {
		if (!JobConfig.isWantPlay() 
				&& JobConfig.isDisableSound() == false
				&& JobConfig.isVideoHasAudioTrack()) {
			vid.addAudioFrame(sample);
		} else if (JobConfig.isWantPlay() ){				
		}
	}
	
	public void addFrameEnc(BufferedImage buff, int pos, double timingFrame){			
		frameCount++;
		if (frameCount < this.positionSynchro) {
			if (this.strictMode) {
				buff = getScaledImage(buff, 768, 576);
			}			
			vid.addFrame(buff, (long)(this.timeBase * timingFrame));
			device.skipFrame();
			updateProgress("codage");			
		} else {			
			BufferedImage bi;	
			bi = this.device.transform(buff);
			JobConfig.setReadyTransform(device.isEnable());	
			if (bi != null) {
				vid.addFrame(bi, (long)(this.timeBase * timingFrame));
			}			
			updateProgress("codage");
		}
	}
	
	public void addFrameDec(BufferedImage buff, int pos, double timingFrame){		
		frameCount++;
		if (frameCount < this.positionSynchro) {
			if (this.strictMode) {
				buff = getScaledImage(buff, 768, 576);
			}			
			vid.addFrame(buff, (long)(this.timeBase * timingFrame));
			device.skipFrame();
			updateProgress("décodage");			
		} else {			
			BufferedImage bi;
			bi = this.device.transform(buff);
			JobConfig.setReadyTransform(device.isEnable());	
			if (bi != null) {
				vid.addFrame(bi, (long)(this.timeBase * timingFrame));
			}
			updateProgress("décodage");
		}
	}
	
	public void closeVideo(){
		vid.closeVideo();
		if(isDecoding){			
			JobConfig.getGui().getTextInfos()
				.setText(JobConfig.getGui().getTextInfos().getText() 
						+ "\n\r"
						+ "Fichier décodé : " + this.outputFilename +"_dec" +
						 "." + JobConfig.getExtension());			
		}
		else
		{			
			if(JobConfig.getSystemCrypt() == 0){
			JobConfig.getGui().getTextInfos()
				.setText(JobConfig.getGui().getTextInfos().getText() 
						+ "\n\r"
						+ "Fichier codé : " + this.outputFilename + "_c" +
						this.keyWord +
						this.fileAudienceLevel + fileKeyboardCode
								+  "." + JobConfig.getExtension());
			}
			else {
				JobConfig.getGui().getTextInfos()
				.setText(JobConfig.getGui().getTextInfos().getText() 
						+ "\n\r"
						+ "Fichier codé : " + this.outputFilename + "_c" +
						"syster" + "." + JobConfig.getExtension());
			}
		}		
	}
	
	public void saveDatFileVideo(){	
		if (JobConfig.getSystemCrypt() == 0) {
			if (isDecoding != true) {

				String word = String.format("%16s", Integer.toBinaryString(this.keyWord)).replace(" ", "0");

				String hexa = String.format("%4s", Integer.toHexString(this.keyWord)).replace(" ", "0");

				try {
					File dataFile = new File(this.outputFilename + "_c" + this.keyWord + this.fileAudienceLevel
							+ fileKeyboardCode + ".txt");
					dataFile.createNewFile();
					FileWriter ffw = new FileWriter(dataFile);
					BufferedWriter bfw = new BufferedWriter(ffw);
					bfw.write("16 bits keyword : " + this.keyWord + " ( " + word + ", " + hexa + " )" + "\r\n");
					bfw.write("Audience level : " + this.audienceLevel + "\r\n");
					bfw.write("11 bits keyword : " + this.keyWord11 + "\r\n");
					bfw.write("nb cycle : " + this.cycle + "\r\n");
					if (this.strictMode) {
						bfw.write("serial eprom : " + JobConfig.getSerial() + "\r\n");
						bfw.write("keyboard code : " + JobConfig.getCode() + "\r\n");
					}
					bfw.write("encoder started at frame n° : " + this.positionSynchro + "\r\n");
					bfw.write("Delay 1 : " + this.perc1 * 100 + "%\r\n");
					bfw.write("Delay 2 : " + this.perc2 * 100 + "%\r\n");
					bfw.write("Number of frames : " + this.frameCount + "\r\n");
					bfw.write("video framerate : " + this.framerate + "\r\n");
					bfw.write("File : " + this.outputFilename + "_c" + this.keyWord + this.fileAudienceLevel
							+ fileKeyboardCode + "." + JobConfig.getExtension() + "\r\n");

					bfw.close();
					JobConfig.getGui().getTextInfos()
							.setText(JobConfig.getGui().getTextInfos().getText() + "\n\r" + "Rapport : "
									+ this.outputFilename + "_c" + this.keyWord + this.fileAudienceLevel
									+ fileKeyboardCode + ".txt");
				} catch (IOException e) {
					JOptionPane
					.showMessageDialog(
							JobConfig.getGui().getFrame(),
							"Erreur lors de l'écriture du rapport",
							"erreur d'écriture I/O",
							JOptionPane.ERROR_MESSAGE);					
				}
			}
		}
		else {
			if (isDecoding != true) {
				JobConfig.getGui().getTextInfos()
				.setText(JobConfig.getGui().getTextInfos().getText() + "\n\r" + "Fichier de décodage : "
						+ this.outputFilename + ".dec");
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
		String stats = "";
		if (JobConfig.getSystemCrypt() == 0 && !JobConfig.isWantDecCorrel()) {
			if (this.strictMode) {
				stats = "audience en cours : " + device.getAudienceLevel();
			}
		}
		
		JobConfig.getGui().getProgress().setValue(this.frameCount);
		   JobConfig
					.getGui()
					.getTextInfos()
					.setText(
							"Trames en cours de " + step + " " + frameCount
									+ "/" + this.videoLengthFrames +
									"\n\r" + stats);
		
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
	
	private String computeAudienceMulti(String audienceMulti, int key16bits){
		String word11bits = "(";
		audienceMulti = audienceMulti.replaceAll("#", "");
		audienceMulti = audienceMulti.replaceAll(" ", "");		
		
		boolean[] audienceTab = new boolean[7];
		
		for (int i = 0; i < 7; i++) {
			audienceTab[i] = false;
		}
		
		for (int i = 0; i < audienceMulti.length(); i++) {
			switch (Integer.valueOf(audienceMulti.substring(i, i+1))) {
			case 1:
				audienceTab[0] = true;
				break;
			case 2:
				audienceTab[1] = true;
				break;
			case 3:
				audienceTab[2] = true;
				break;
			case 4:
				audienceTab[3] = true;
				break;
			case 5:
				audienceTab[4] = true;
				break;
			case 6:
				audienceTab[5] = true;
				break;
			case 7:
				audienceTab[6] = true;
				break;
			default:
				break;
			}
		}
		
		
		for (int i = 0; i < 7; i++) {
			if(audienceTab[i] == true){
			word11bits = word11bits + " " + ( i+1 ) + ":" + get11bitsKeyWordMulti(key16bits,
					i+1);	
			}
		}		
		
		word11bits = word11bits.trim();
		word11bits = word11bits + " )";
		
		return word11bits;
	}
	
	/**
	 * get the 11 bits keyword
	 * @param key16bits the 16 bits keyword
	 */
	private String get11bitsKeyWordMulti(int key16bits, int index){
		String word = String.format
				("%16s", Integer.toBinaryString(key16bits)).replace(" ", "0");	
		String audience ="";		
		
		switch (index) {
		case 1:
			//audience 1
			 audience = word.substring(0, 11);			
			break;
		case 2:
			//audience 2
			audience = word.substring(3, 14);			
			break;
		case 3:
			//audience 3
			audience = word.substring(6, 16) + word.charAt(0);			
			break;
		case 4:
			//audience 4
			audience =  word.substring(9, 16) + word.substring(0, 4);			
			break;
		case 5:
			//audience 5
			audience = word.substring(12, 16) +  word.substring(0, 7);			
			break;
		case 6:
			//audience 6
			audience =  word.charAt(15) + word.substring(0, 10) ;			
			break;
		case 7:
			//audience 7		
			return "1337";
			//break;
		default:
			audience = "";
			break;	
		}
		
		return String.format("%04d", Integer.parseInt(audience,2));
	}
	
	public Device getDevice() {
		return device;
	}
}