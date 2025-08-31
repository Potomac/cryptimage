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
import com.ib.cryptimage.core.systems.discret12.Discret12DecCore;
import com.ib.cryptimage.core.systems.discret12.Discret12EncCore;
import com.ib.cryptimage.core.systems.eurocrypt.EurocryptConf;
import com.ib.cryptimage.core.systems.eurocrypt.EurocryptCore;
import com.ib.cryptimage.core.systems.luxcrypt.LuxcryptCore;
import com.ib.cryptimage.core.types.ColorType;
import com.ib.cryptimage.core.types.SystemType;
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
	//private int positionSynchro;
	
	private VideoRecorder vid;
	private int videoLengthFrames;
	private double timeBase;

	private String audienceLevel;
	
	private Device device;
	
	private double perc1;
	private double perc2;	
		
	private String codePattern;
	private int cycle;
	private String fileAudienceLevel;
	private String fileKeyboardCode;
	private String colorMode;	
	
	private VideoPlayer vidPlayer;	
	
	private double framerate;///
	
	SplitFrames splitFrames;
	private boolean is944 = false;
	
	private String eurocryptMode = "";
	
	
	
	public CryptVideo() {	
		splitFrames = new SplitFrames((int)JobConfig.getGui().getCmbPalFreq().getSelectedItem());
			
		
		if (JobConfig.getSystemCrypt() == SystemType.SYSTER || JobConfig.getSystemCrypt() == SystemType.VIDEOCRYPT 
				|| JobConfig.getSystemCrypt() == SystemType.TRANSCODE 
				|| JobConfig.getSystemCrypt() == SystemType.EUROCRYPT) {
			if (JobConfig.getGui().getCbColorMode().getSelectedIndex() == ColorType.RGB) {
				this.colorMode = "rgb";
			}
			if (JobConfig.getGui().getCbColorMode().getSelectedIndex() == ColorType.PAL) {
				this.colorMode = "pal";
			}
			if (JobConfig.getGui().getCbColorMode().getSelectedIndex() == ColorType.SECAM) {
				this.colorMode = "secam";
			}
			if (JobConfig.getGui().getCbColorMode().getSelectedIndex() == ColorType.PAL_COMPOSITE_ENC_DEC) {
				this.colorMode = "pal_composite_encode_and_decode";
			}
			if (JobConfig.getGui().getCbColorMode().getSelectedIndex() == ColorType.PAL_COMPOSITE_ENC) {
				this.colorMode = "pal_composite_encode_only";
			}
			if (JobConfig.getGui().getCbColorMode().getSelectedIndex() == ColorType.PAL_COMPOSITE_DEC) {
				this.colorMode = "pal_composite_decode_only";
			}
		}

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

		JobConfig.frameCount = 0;
		//this.positionSynchro = JobConfig.getPositionSynchro();
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
		
		if (JobConfig.isPanAndScan() && JobConfig.getSystemCrypt() == SystemType.DISCRET11 && !JobConfig.isStrictMode()
				&& !JobConfig.isWantDec()) {
			if ((float) this.width / (float) this.height > 4f / 3f) {
				this.width = (int) ((4f / 3f) * (float) this.height);
			} else if ((float) this.width / (float) this.height < 4f / 3f) {
				this.height = (int) ((float) this.width / (4f / 3f));
			}
		}
		
		if (JobConfig.isStretch() && JobConfig.getSystemCrypt() == SystemType.DISCRET11 
				&& !JobConfig.isStrictMode() && !JobConfig.isWantDec()){
			this.width = 768;
			this.height = 576;
		}
		
		
		double frameRate = reader.getContainer().getStream(0).getStreamCoder()
				.getFrameRate().getValue();
		
		this.framerate = JobConfig.getFrameRate(); //frameRate;
		frameRate = JobConfig.getFrameRate(); 

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

		
		if (JobConfig.getSystemCrypt() == SystemType.DISCRET11) {
			if (this.strictMode) {
				if (this.isDecoding) {
					if(JobConfig.isSearchCode68705()) {
						device = new Discret11DecFindKey(JobConfig.getWord16bits(), this.perc1, this.perc2);
					}
					else {
						if (JobConfig.isWantDecCorrel()) {
							device = new DiscretDecCorrel();
						}
						else {
							device = new Discret11Dec(JobConfig.getWord16bits(), this.perc1, this.perc2);
						}
					}
				} else {
					if (JobConfig.isMaskedEdge()) {
						device = new Discret11EncMaskedBorder(JobConfig.getWord16bits(), this.codePattern, this.cycle,
								this.perc1, this.perc2);
					} else { //modif test discret12 retards nuls
						device = new Discret11Enc(JobConfig.getWord16bits(), this.codePattern, this.cycle, this.perc1,
								this.perc2);
					}
				}

			} else { // non strict mode
				if (this.isDecoding) { // decoding in non strict mode
					device = new SimpleDiscret11(this.keyWord, Integer.valueOf(this.audienceLevel), mode,
							this.height, this.width,this.perc1,
							this.perc2);
				} else { // encoding in non strict mode
					if (JobConfig.isMaskedEdge()) {
						device = new SimpleDiscret11MaskedBorder(this.keyWord, Integer.valueOf(this.audienceLevel), mode,
								this.height, this.width, this.perc1,
								this.perc2);
					} else {
						device = new SimpleDiscret11(this.keyWord, Integer.valueOf(this.audienceLevel), mode,
								this.height, this.width, this.perc1,
								this.perc2);
					}					
				}
			}
		}
		else if (JobConfig.getSystemCrypt() == SystemType.SYSTER) { //syster mode
			if(this.isDecoding){
				if(JobConfig.getGui().getChkSysterReverse().isSelected()) {
					device = new SysterDecReverse(JobConfig.getTableSyster(),
							JobConfig.getFileDataDecSyster(),
							JobConfig.isWantDecCorrel());
				}
				else {
					device = new SysterDec(JobConfig.getTableSyster(),
							JobConfig.getFileDataDecSyster(),
							JobConfig.isWantDecCorrel());
				}
	
			}
			else {
				device = new SysterEnc(JobConfig.getTableSyster(),
						outputFilename + "_csyster" + "_" + this.colorMode,
						JobConfig.getFileDataEncSyster(),
						JobConfig.getGui().getChkPlayer().isSelected());
			}
		}
		else if(JobConfig.getSystemCrypt() == SystemType.TRANSCODE) { //transcode
			device = new Transcode();			
		}		
		else if(JobConfig.getSystemCrypt() == SystemType.VIDEOCRYPT){ //videocrypt
			if(this.isDecoding){
				device = new VideocryptDec(
						JobConfig.getFileDataDecVideocrypt(),
						JobConfig.isWantDecCorrel());
			}
			else {
				device = new VideocryptEnc(
						outputFilename + "_cvideocrypt" + "_" + this.colorMode,
						JobConfig.getFileDataEncVideocrypt(),
						JobConfig.getGui().getChkPlayer().isSelected());
			}
		}
		else if(JobConfig.getSystemCrypt() == SystemType.EUROCRYPT) { //eurocrypt
			device = new EurocryptCore();
			
			if(EurocryptConf.isEurocryptSingleCut) {
				this.eurocryptMode="single-cut_key-" + EurocryptConf.seedCode;
				
				if(EurocryptConf.isEncodeMacDecode576pNoEurocrypt){
					this.eurocryptMode="dmac_no-eurocrypt-management_" + this.eurocryptMode;
				}
			}
			else if(EurocryptConf.isEurocryptDoubleCut) {
				this.eurocryptMode="double-cut_key-" + EurocryptConf.seedCode;
				if(EurocryptConf.isEncodeMacDecode576pNoEurocrypt){
					this.eurocryptMode="dmac_no-eurocrypt-management_" + this.eurocryptMode;
				}
			}
			else if(EurocryptConf.isDisableEurocrypt && EurocryptConf.isEncodeMacDecode576pNoEurocrypt) {
				this.eurocryptMode="dmac_no-eurocrypt-management_eurocrypt-disabled";
			}
			else if(EurocryptConf.isDisableEurocrypt && EurocryptConf.isEncodeMac) {
				this.eurocryptMode="eurocrypt-disabled_mac-only";
			}
			
		}
		else if(JobConfig.getSystemCrypt() == SystemType.LUXCRYPT) { // luxcrypt
			device = new LuxcryptCore();
		}
		else if(JobConfig.getSystemCrypt() == SystemType.DISCRET12) { // discret12
			if(this.isDecoding) {
				device = new Discret12DecCore();
			}
			else {
				device = new Discret12EncCore();
			}			
		}
		//////

		
		if(JobConfig.getGui().getRdi944().isSelected() && this.strictMode
		   && JobConfig.getSystemCrypt() != SystemType.EUROCRYPT && JobConfig.getSystemCrypt() != SystemType.LUXCRYPT) {
			this.width = 944;
			this.height = 626;
			is944 = true;
		}		
	
		
		if (JobConfig.isWantPlay() != true) {

			
			if (this.isDecoding && !JobConfig.isSearchCode68705()) {
				try {
					vid = new VideoRecorder(outputFilename + "_dec" + "."
							+ JobConfig.getExtension(), width, height,
							frameRate, JobConfig.getAudioRate());

				} catch (Exception e) {
					JOptionPane.showMessageDialog(
							null,
							JobConfig.getRes().getString("cryptVideo.exceptionError")
									+ " " + e.getMessage(), 
							JobConfig.getRes().getString("cryptVideo.exceptionError.title"),
							JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
					System.exit(1);
				}
			} else if(!this.isDecoding) { //encoding
				try {
					if(JobConfig.getSystemCrypt() == SystemType.DISCRET11 && !JobConfig.isSearchCode68705()){
					vid = new VideoRecorder(outputFilename + info + keyWord
							+ this.fileAudienceLevel + fileKeyboardCode + "."
							+ JobConfig.getExtension(), width, height,
							frameRate, JobConfig.getAudioRate());
					}
					else if(JobConfig.getSystemCrypt() == SystemType.SYSTER) { //syster
						vid = new VideoRecorder(outputFilename + info
								+ "syster" +  "_" + this.colorMode + "."
								+ JobConfig.getExtension(), width, height,
								frameRate, JobConfig.getAudioRate());
					} else if(JobConfig.getSystemCrypt() == SystemType.TRANSCODE) {//transcode
						vid = new VideoRecorder(outputFilename + info
								+ "transcode" +  "_" + this.colorMode + "."
								+ JobConfig.getExtension(), width, height,
								frameRate, JobConfig.getAudioRate());
					}					
					else if(JobConfig.getSystemCrypt() == SystemType.VIDEOCRYPT) {//videocrypt
						vid = new VideoRecorder(outputFilename + info
								+ "videocrypt" +  "_" + this.colorMode + "."
								+ JobConfig.getExtension(), width, height,
								frameRate, JobConfig.getAudioRate());
					}
					else if(JobConfig.getSystemCrypt() == SystemType.EUROCRYPT) {//eurocrypt						
						vid = new VideoRecorder(outputFilename + info
								+ "mac" + "_" + this.eurocryptMode + "."
								+ JobConfig.getExtension(), width, height,
								frameRate, JobConfig.getAudioRate());
					}
					else if(JobConfig.getSystemCrypt() == SystemType.LUXCRYPT) {//luxcrypt						
						vid = new VideoRecorder(outputFilename 
								+ "_luxcrypt" + "."
								+ JobConfig.getExtension(), width, height,
								frameRate, JobConfig.getAudioRate());
					}
					else if(JobConfig.getSystemCrypt() == SystemType.DISCRET12) {//discret12
						if(this.isDecoding) {
							vid = new VideoRecorder(outputFilename 
									+ "_discret12dec" + "."
									+ JobConfig.getExtension(), width, height,
									frameRate, JobConfig.getAudioRate());
						}
						else {
							vid = new VideoRecorder(outputFilename 
									+ "_discret12enc" + "."
									+ JobConfig.getExtension(), width, height,
									frameRate, JobConfig.getAudioRate());
						}
						
						
					}

				} catch (Exception e) {
					JOptionPane.showMessageDialog(
							null,
							JobConfig.getRes().getString("cryptVideo.exceptionError")
									+ " " + e.getMessage(), 
									JobConfig.getRes().getString("cryptVideo.exceptionError.title"),
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
		
		JobConfig.frameCount++;
		if (JobConfig.frameCount < JobConfig.getPositionSynchro()){			
			vidPlayer.addImage(buff);			
			vidPlayer.showImage();
			device.skipFrame();
			updateProgress();			
		}
		else{
		BufferedImage bi = save;
		
		if(this.strictMode  && !is944 && JobConfig.getSystemCrypt() != SystemType.EUROCRYPT
				&& JobConfig.getSystemCrypt() != SystemType.LUXCRYPT){
			save = getScaledImage(save, this.width, 576);
			if( vidPlayer.isInverse() == false){
				if(JobConfig.isHasToBeUnsplit()) {
					buff = splitFrames.unsplitFrames(buff);
				}
				bi = this.device.transform(buff);
			}			
		}
		else
		{	if( vidPlayer.isInverse() == false){
			if(JobConfig.isHasToBeUnsplit() && this.strictMode) {

				buff = splitFrames.unsplitFrames(buff);
			}
				bi = this.device.transform(buff);
			}
		}
			
		if( vidPlayer.isInverse() == true){	
			vidPlayer.addImage(save);
		}
		else{
				if (bi != null) {
					if(is944 && this.strictMode) {
						bi = splitFrames.splitFrames(false, bi);
					}
					vidPlayer.addImage(bi);
				}
		}
		
		vidPlayer.showImage();
		if(JobConfig.isStop() || JobConfig.frameCount == this.getVideoLengthFrames()){
			vidPlayer.close();			
		}
		updateProgress();		
		}
	}
	
	public void addDisplayFrameDec(BufferedImage buff, int pos, long timingFrame){		
		
		BufferedImage save;		
		save = deepCopy(buff);
		
		JobConfig.frameCount++;
		if (JobConfig.frameCount < JobConfig.getPositionSynchro()){			
			vidPlayer.addImage(buff);
			vidPlayer.showImage();
			device.skipFrame();
			updateProgress();			
		}
		else{
		BufferedImage bi;
		if(JobConfig.isHasToBeUnsplit() && this.strictMode) {
			buff = splitFrames.unsplitFrames(buff);
		}
		bi = this.device.transform(buff);
		
		if(vidPlayer.isInverse() == true){
			vidPlayer.addImage(save);
		}
		else{
				if (bi != null) {					
					if(is944) {
						bi = splitFrames.splitFrames(false, bi);
					}
					vidPlayer.addImage(bi);
				}
		}
		
		vidPlayer.showImage();
		if(JobConfig.isStop() || JobConfig.frameCount == this.getVideoLengthFrames()){
			vidPlayer.close();			
		}
		updateProgress();		
		}
	}

	public void addAudioFrame(IAudioSamples sample) {
		if (!JobConfig.isWantPlay() 
				&& JobConfig.isDisableSound() == false
				&& JobConfig.isVideoHasAudioTrack()) {
			if(!JobConfig.isSearchCode68705()) {
				vid.addAudioFrame(sample, device.isInsideRangeSliderFrames());
			}			
		} else if (JobConfig.isWantPlay() ){				
		}
	}
	
	public void addFrameEnc(BufferedImage buff, int pos, double timingFrame){				
		JobConfig.frameCount++;
		if (JobConfig.frameCount < JobConfig.getPositionSynchro()) {
			if (this.strictMode && !is944 && JobConfig.getSystemCrypt() != SystemType.EUROCRYPT
					&& JobConfig.getSystemCrypt() != SystemType.LUXCRYPT) {
				buff = getScaledImage(buff, 768, 576);
			}
			
			if(is944 && buff.getWidth() != 944 && buff.getWidth() != 626 && this.strictMode) {
				if(buff.getWidth() != 768 || buff.getHeight() != 576) {				
					buff = getScaledImage(buff, 768, 576);
				}
				buff = splitFrames.splitFrames(false, buff);
			}	
			vid.addFrame(buff, (long)(this.timeBase * timingFrame));			
			device.skipFrame();
			updateProgress();			
		} else {			
			BufferedImage bi;	
			if(JobConfig.isHasToBeUnsplit() && this.strictMode) {
				buff = splitFrames.unsplitFrames(buff);
			}
			bi = this.device.transform(buff);
			JobConfig.setReadyTransform(device.isEnable());	
			if (bi != null) {
				if(is944 && this.strictMode) {			
					bi = splitFrames.splitFrames(false, bi);
				}	
				
				vid.addFrame(bi, (long)(this.timeBase * timingFrame));				
			}			
			updateProgress();
		}
	}
	
	public void addFrameDec(BufferedImage buff, int pos, double timingFrame){			
		JobConfig.frameCount++;
		if (JobConfig.frameCount < JobConfig.getPositionSynchro()) {
			if (this.strictMode && !is944 && JobConfig.getSystemCrypt() != SystemType.EUROCRYPT
					&& JobConfig.getSystemCrypt() != SystemType.LUXCRYPT) {				
				buff = getScaledImage(buff, 768, 576);
			}
			
			if(is944 && buff.getWidth() != 944 && buff.getWidth() != 626 && this.strictMode) {
				if(buff.getWidth() != 768 || buff.getHeight() != 576) {				
					buff = getScaledImage(buff, 768, 576);
				}
				buff = splitFrames.splitFrames(false, buff);
			}
			
			if(!JobConfig.isSearchCode68705()) {
				vid.addFrame(buff, (long)(this.timeBase * timingFrame));
			}
			device.skipFrame();
			updateProgress();			
		} else {			
			BufferedImage bi;
			if(JobConfig.isHasToBeUnsplit() && this.strictMode) {
				buff = splitFrames.unsplitFrames(buff);
			}
			bi = this.device.transform(buff);
			JobConfig.setReadyTransform(device.isEnable());	
			if (bi != null) {
				if(is944) {				
					bi = splitFrames.splitFrames(false, bi);
				}				
				if(!JobConfig.isSearchCode68705()) {
					vid.addFrame(bi, (long)(this.timeBase * timingFrame));
				}
			}
			updateProgress();
		}
	}
	
	public void displayKey() {
		if(JobConfig.isSearchCode68705()) {
			device.getKey();
		}
	}
	
	public void closeVideo(){
		if(!JobConfig.isSearchCode68705()) {
			vid.closeVideo();
		}
		if(isDecoding && !JobConfig.isSearchCode68705()){			
			JobConfig.getGui().getTextInfos()
				.setText(JobConfig.getGui().getTextInfos().getText() 
						+ "\n\r"
						+ JobConfig.getRes().getString("cryptVideo.progress.fin.decodage") + this.outputFilename +"_dec" +
						 "." + JobConfig.getExtension());			
		}
		else if(!isDecoding){			
			if(JobConfig.getSystemCrypt() == SystemType.DISCRET11){
			JobConfig.getGui().getTextInfos()
				.setText(JobConfig.getGui().getTextInfos().getText() 
						+ "\n\r"
						+ JobConfig.getRes().getString("cryptVideo.progress.fin.codage") + this.outputFilename + "_c" +
						this.keyWord +
						this.fileAudienceLevel + fileKeyboardCode
								+  "." + JobConfig.getExtension());
			}
			else if(JobConfig.getSystemCrypt() == SystemType.SYSTER){
				
				JobConfig.getGui().getTextInfos()
				.setText(JobConfig.getGui().getTextInfos().getText() 
						+ "\n\r"
						+ JobConfig.getRes().getString("cryptVideo.progress.fin.codage") + this.outputFilename + "_c" +
						"syster" + "_" + this.colorMode + "." + JobConfig.getExtension());
			}else if(JobConfig.getSystemCrypt() == SystemType.VIDEOCRYPT){
				
				JobConfig.getGui().getTextInfos()
				.setText(JobConfig.getGui().getTextInfos().getText() 
						+ "\n\r"
						+ JobConfig.getRes().getString("cryptVideo.progress.fin.codage") + this.outputFilename + "_c" +
						"videocrypt" + "_" + this.colorMode + "." + JobConfig.getExtension());
			}else if(JobConfig.getSystemCrypt() == SystemType.TRANSCODE){
				
				JobConfig.getGui().getTextInfos()
				.setText(JobConfig.getGui().getTextInfos().getText() 
						+ "\n\r"
						+ JobConfig.getRes().getString("cryptVideo.progress.fin.codage") + this.outputFilename + "_c" +
						"transcode" + "_" + this.colorMode + "." + JobConfig.getExtension());
			}
			else if(JobConfig.getSystemCrypt() == SystemType.EUROCRYPT){
							
							JobConfig.getGui().getTextInfos()
							.setText(JobConfig.getGui().getTextInfos().getText() 
									+ "\n\r"
									+ JobConfig.getRes().getString("cryptVideo.progress.fin.codage") + this.outputFilename + "_c" +
									"mac" + "_" + this.eurocryptMode + "." + JobConfig.getExtension());
						}
			else if(JobConfig.getSystemCrypt() == SystemType.LUXCRYPT){
				
				JobConfig.getGui().getTextInfos()
				.setText(JobConfig.getGui().getTextInfos().getText() 
						+ "\n\r"
						+ JobConfig.getRes().getString("cryptVideo.progress.fin.codage") + this.outputFilename + "_" +
						"luxcrypt" + "." + JobConfig.getExtension());
			}
			else if(JobConfig.getSystemCrypt() == SystemType.DISCRET12){
							
							JobConfig.getGui().getTextInfos()
							.setText(JobConfig.getGui().getTextInfos().getText() 
									+ "\n\r"
									+ JobConfig.getRes().getString("cryptVideo.progress.fin.codage") + this.outputFilename + "_" +
									"discret12" + "." + JobConfig.getExtension());
						}
		}		
	}
	
	public void saveDatFileVideo(){	
		if (JobConfig.getSystemCrypt() == SystemType.DISCRET11  && !JobConfig.isSearchCode68705()) {
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
					bfw.write("encoder started at frame n° : " + JobConfig.getPositionSynchro() + "\r\n");
					bfw.write("Delay 1 : " + this.perc1 * 100 + "%\r\n");
					bfw.write("Delay 2 : " + this.perc2 * 100 + "%\r\n");
					bfw.write("Number of frames : " + JobConfig.frameCount + "\r\n");
					bfw.write("video framerate : " + this.framerate + "\r\n");
					bfw.write("File : " + this.outputFilename + "_c" + this.keyWord + this.fileAudienceLevel
							+ fileKeyboardCode + "." + JobConfig.getExtension() + "\r\n");

					bfw.close();
					JobConfig.getGui().getTextInfos()
							.setText(JobConfig.getGui().getTextInfos().getText() + "\n\r" 
					+ JobConfig.getRes().getString("cryptVideo.log") 
					+ " " + this.outputFilename + "_c" + this.keyWord + this.fileAudienceLevel
									+ fileKeyboardCode + ".txt");
				} catch (IOException e) {
					JOptionPane
					.showMessageDialog(
							JobConfig.getGui().getFrame(),
							JobConfig.getRes().getString("cryptVideo.errorLog"),
							JobConfig.getRes().getString("cryptVideo.errorIO.title"),
							JOptionPane.ERROR_MESSAGE);					
				}
			}
		}
		else {
			if (isDecoding != true) {
				String sys, extension = "";
				if(JobConfig.getGui().getCombSystemCrypt().getSelectedIndex()== SystemType.SYSTER){
					sys = "_csyster";
					extension = ".dec";
					
					JobConfig.getGui().getTextInfos()
					.setText(JobConfig.getGui().getTextInfos().getText() + "\n\r" + JobConfig.getRes().getString("cryptVideo.progress.fin.decodage.logfile")
							+ this.outputFilename + sys + "_" + this.colorMode + extension);
				}
				else if(JobConfig.getGui().getCombSystemCrypt().getSelectedIndex()== SystemType.VIDEOCRYPT){
					sys = "_cvideocrypt";
					extension = ".vid";
					
					JobConfig.getGui().getTextInfos()
					.setText(JobConfig.getGui().getTextInfos().getText() + "\n\r" + JobConfig.getRes().getString("cryptVideo.progress.fin.decodage.logfile")
							+ this.outputFilename + sys + "_" + this.colorMode + extension);
				}

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
	private void updateProgress() {	
		String stats = "";
		if (JobConfig.getSystemCrypt() == SystemType.DISCRET11 && !JobConfig.isWantDecCorrel()) {
			if (this.strictMode) {
				stats = JobConfig.getRes().getString("cryptVideo.progress.audience") + device.getAudienceLevel();
			}
		}
		
		String messFrames;
		if(isDecoding){
			if(JobConfig.getSystemCrypt() == SystemType.DISCRET11 && JobConfig.isSearchCode68705()) {
				messFrames = JobConfig.getRes().getString("cryptVideo.progress.searchMode68705");
			}
			else {
				messFrames = JobConfig.getRes().getString("cryptVideo.progress.frames.decode");
			}			
		}
		else {
			messFrames = JobConfig.getRes().getString("cryptVideo.progress.frames.code");
		}
		
		JobConfig.getGui().getProgress().setValue(JobConfig.frameCount);
		   JobConfig
					.getGui()
					.getTextInfos()
					.setText(
							messFrames + " " + JobConfig.frameCount
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