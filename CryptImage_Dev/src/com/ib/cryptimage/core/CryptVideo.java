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


import java.awt.image.BufferedImage;
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
	private int timeBase;
	private int frameCount;
	private int audienceLevel;
	
	private Discret11 discret;
	private double perc1;
	private double perc2;
	
	private int step1 =0;
	private int step20 = 0;
	private int step40 =0;
	private int step60 = 0;
	private int step80 = 0;
	private int step100 = 0;
	
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
		
		
		int mode;
		if(this.isDecoding){
			mode = Discret11.MODE_DEC;
		}
		else{
			mode = Discret11.MODE_ENC;			
		}
		
		
		discret = new Discret11(this.keyWord, mode, this.audienceLevel,
				this.perc1, this.perc2);

			
		IMediaReader reader = ToolFactory.makeReader(frmv.getJob().getInput_file());
		reader.readPacket();
		this.width =reader.getContainer().getStream(0).getStreamCoder().getWidth();
		this.height = reader.getContainer().getStream(0).getStreamCoder().getHeight();
		int frameRate = (int) reader.getContainer().getStream(0).getStreamCoder().getFrameRate().getValue();
		
		if (frmv.getJob().isWantPlay()){
			vidPlayer = new VideoPlayer(frameRate);
		}
		
		//System.out.println((reader.getContainer().getDuration()/1000/1000)*frameRate);
		
		this.timeBase = 1000/frameRate;		
		
		String info = "_crypt_";
		 if (this.isDecoding){
			 info = "_decrypt_";
		 }
		
		 if(this.strictMode){ // we use "stric mode discret 11", so we resize the video to 768x576 pixels
			 this.width = 768;
			 this.height = 576;
		 }
		
		if(frmv.getJob().isWantPlay() !=true){
	    vid = new VideoRecorder(outputFilename + info + keyWord + "_audience_" 
		+ this.audienceLevel + ".mp4", width,
				height, frmv.getJob().getVideoBitrate(), frmv.getJob().getVideoCodec());
		}
	}
	
	public void addDisplayFrameEnc(BufferedImage buff, int pos, int timingFrame){
		frameCount++;
		BufferedImage bi;		
		bi = this.discret.transform(buff);
		
		//vid.addFrame(bi, this.timeBase * timingFrame);
		vidPlayer.addImage(bi);
		vidPlayer.showImage();
		updateProgress("encoded");
		//System.out.println("Frames encoded : " + (timingFrame+1) + " /" +this.videoLengthFrames);
	}
	
	public void addDisplayFrameDec(BufferedImage buff, int pos, int timingFrame){
		
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
		bi = this.discret.transform(buff);
		vidPlayer.addImage(bi);
		vidPlayer.showImage();
		updateProgress("decoded");
		//System.out.println("Frames decoded : " + (timingFrame+1) + " /" +this.videoLengthFrames);
		}
	}
	
	
	
	public void addFrameEnc(BufferedImage buff, int pos, int timingFrame){
		frameCount++;
		BufferedImage bi;
		bi = this.discret.transform(buff);
		//bi = new CryptImage(buff, pos, this.strictMode).getCryptDiscret11(keyWord);		
		vid.addFrame(bi, this.timeBase * ( timingFrame ));
		updateProgress("encoded");
		//System.out.println("Frames encoded : " + (timingFrame+1) + " /" +this.videoLengthFrames);
	}
	
	public void addFrameDec(BufferedImage buff, int pos, int timingFrame){
		
		frameCount++;
		if (frameCount < this.positionSynchro){
			//we add a non decrypted frame because we are not at the synchro frame ( line 310 )
			vid.addFrame(buff,this.timeBase * ( timingFrame  ));
			updateProgress("decoded");
			//System.out.println("Frame non decoded : " + (timingFrame+1) + " /" +this.videoLengthFrames);
		}
		else{
		BufferedImage bi;
		bi = this.discret.transform(buff);
		//bi = new CryptImage(buff, pos,this.strictMode).getDecryptDiscret11WithCode(keyWord);
		vid.addFrame(bi, this.timeBase * timingFrame);
		updateProgress("decoded");
		//System.out.println("Frames decoded : " + (timingFrame+1) + " /" +this.videoLengthFrames);
		}
	}
	
	public void closeVideo(){
		vid.closeVideo();
		if(isDecoding){
			System.out.println("Decrypted video file : " + this.outputFilename +"_decrypt_" +
					this.keyWord + "_audience_" + this.audienceLevel +".mp4");
		}
		else
		{
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
			bfw.write("debug lines : " + "\r\n" + discret.getsDebugLines());
			bfw.close();
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
		int progress = (int)(((double)this.frameCount/(double)this.videoLengthFrames) * 100);
		
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
	
	public int getVideoLengthFrames() {
		return videoLengthFrames;
	}
	
	public BufferedImage getBuff() {
		return buff;
	}

	public void setBuff(BufferedImage buff) {
		this.buff = buff;
	}
	
	
}