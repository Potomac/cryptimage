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
import java.util.concurrent.TimeUnit;

import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IRational;
import com.ib.cryptimage.core.systems.discret12.Discret12Conf;
import com.ib.cryptimage.core.systems.eurocrypt.EurocryptConf;
import com.ib.cryptimage.core.systems.luxcrypt.LuxcryptConf;
import com.ib.cryptimage.core.types.AudioCodecType;
import com.ib.cryptimage.core.types.SystemType;
import com.ib.cryptimage.core.types.VideoCodecType;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;



public class VideoRecorder {
	
	private IMediaWriter writer;	
	private boolean is720 = false;
	private boolean wantDec = false;
	private boolean wantSoundCryptDecrypt = false;
	private boolean disableSound;
	private int AUDIORATE;		
	private SoundCrypt soundCryptR;
	private SoundCrypt soundCryptL;	
	
	
	public VideoRecorder(String outputFilename, int width, int height,
			double framerate, int audioRateValue) {	
				
		this.AUDIORATE = audioRateValue;
		
		if(JobConfig.isHasMultiAudioChannels() || JobConfig.isHasProblematicCodec()) {
			this.AUDIORATE = JobConfig.getAudioTrackInfos().getAudioRate();
		}
		
		this.wantDec = JobConfig.isWantDec();
		this.wantSoundCryptDecrypt = JobConfig.isWantSound();
		this.disableSound = JobConfig.isDisableSound();
		
		if(wantSoundCryptDecrypt){
			soundCryptR = new SoundCrypt(AUDIORATE, this.wantDec,
					JobConfig.getAmpEnc(), JobConfig.getAmpDec());
			soundCryptL = new SoundCrypt(AUDIORATE, this.wantDec, 
					JobConfig.getAmpEnc(), JobConfig.getAmpDec());
		}
		
		
		
		if(JobConfig.getsWidth()== 720 && JobConfig.isStrictMode() == true ){
			this.is720 = true;
			width = 720;
		}
		
		
		if(JobConfig.getSystemCrypt() == SystemType.EUROCRYPT) {			
			width = EurocryptConf.width;
			height = EurocryptConf.height;		
		}
		
		if(JobConfig.getSystemCrypt() == SystemType.LUXCRYPT) {			
			width = LuxcryptConf.width;
			height = LuxcryptConf.height;		
		}
		
		if(JobConfig.getSystemCrypt() == SystemType.DISCRET12) {			
			width = Discret12Conf.width;
			height = Discret12Conf.height;		
		}
		
		if(JobConfig.isWantJoinInputOutputFrames()) {
			width = 1920;
			height = 1080;
		}
		
		writer = ToolFactory.makeWriter(outputFilename);		
		IRational frame_rate = IRational.make(framerate);	
		
				
		if(JobConfig.getVideoTrackInfos() != null) {
			int videoCodecIndex = JobConfig.getVideoCodec() - 1;
			
			if(videoCodecIndex == VideoCodecType.H264) {
				writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264,frame_rate, width, height);
				writer.getContainer().getStream(0).getStreamCoder().setPixelType(IPixelFormat.Type.YUV420P);
				writer.getContainer().getStream(0).getStreamCoder().setProperty("preset", "medium");
			}
			else if(videoCodecIndex == VideoCodecType.MPEG2) {
				writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG2VIDEO,frame_rate, width, height);
			}
			else if(videoCodecIndex == VideoCodecType.DIVX) {
				writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4,frame_rate, width, height);
			}
			else if(videoCodecIndex == VideoCodecType.HUFFYUV) {
				writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_FFVHUFF,frame_rate, width, height);
				writer.getContainer().getStream(0).getStreamCoder().setPixelType(IPixelFormat.Type.RGB24);	
			}
			else if(videoCodecIndex == VideoCodecType.H264V2) {
				writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264,frame_rate, width, height);
				writer.getContainer().getStream(0).getStreamCoder().setPixelType(IPixelFormat.Type.YUV444P);		
			}
			else if(videoCodecIndex == VideoCodecType.FFV1) {
				writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_FFV1,frame_rate, width, height);
				writer.getContainer().getStream(0).getStreamCoder().setPixelType(IPixelFormat.Type.YUV444P);
			}			
			
		}
		
				
		if( this.disableSound != true 
				&& !JobConfig.isHasMultiAudioChannels() && !JobConfig.isHasProblematicCodec()
				&& JobConfig.isVideoHasAudioTrack()){
			
			int audioCodecIndex = JobConfig.getAudioCodec() - 1;
			
			if(audioCodecIndex == AudioCodecType.MP3_96KBS) {
				writer.addAudioStream(1, 0, ICodec.ID.CODEC_ID_MP3, 2,AUDIORATE);	
				writer.getContainer().getStream(1).getStreamCoder().setBitRate(96*1000);	
			}
			else if(audioCodecIndex == AudioCodecType.MP3_128KBS) {
				writer.addAudioStream(1, 0, ICodec.ID.CODEC_ID_MP3, 2,AUDIORATE);	
				writer.getContainer().getStream(1).getStreamCoder().setBitRate(128*1000);			
			}
			else if(audioCodecIndex == AudioCodecType.MP3_160KBS) {
				writer.addAudioStream(1, 0, ICodec.ID.CODEC_ID_MP3, 2,AUDIORATE);	
				writer.getContainer().getStream(1).getStreamCoder().setBitRate(160*1000);			
			}
			else if(audioCodecIndex == AudioCodecType.MP3_192KBS) {
				writer.addAudioStream(1, 0, ICodec.ID.CODEC_ID_MP3, 2,AUDIORATE);	
				writer.getContainer().getStream(1).getStreamCoder().setBitRate(192*1000);			
			}
			else if(audioCodecIndex == AudioCodecType.MP3_224KBS) {
				writer.addAudioStream(1, 0, ICodec.ID.CODEC_ID_MP3, 2,AUDIORATE);	
				writer.getContainer().getStream(1).getStreamCoder().setBitRate(224*1000);			
			}
			else if(audioCodecIndex == AudioCodecType.MP3_320KBS) {
				writer.addAudioStream(1, 0, ICodec.ID.CODEC_ID_MP3, 2,AUDIORATE);	
				writer.getContainer().getStream(1).getStreamCoder().setBitRate(320*1000);			
			}
			else if(audioCodecIndex == AudioCodecType.WAV) {
				writer.addAudioStream(1, 0, ICodec.ID.CODEC_ID_PCM_S16LE, 2,AUDIORATE);		
			}
			else if(audioCodecIndex == AudioCodecType.FLAC) {
				writer.addAudioStream(1, 0, ICodec.ID.CODEC_ID_FLAC, 2,AUDIORATE);		
			}		
		}
		
		// case of multichannel audio track or E-AC3 codec in the input video
		if( this.disableSound != true 
				&& (JobConfig.isHasMultiAudioChannels() || JobConfig.isHasProblematicCodec())
				&& JobConfig.isVideoHasAudioTrack()) {
			if(JobConfig.getAudioCodec() == (AudioCodecType.WAV + 1)) { // wav
				writer.addAudioStream(1, 0, ICodec.ID.CODEC_ID_PCM_S16LE, 
						  JobConfig.getAudioTrackInfos().getNumChannels() , JobConfig.getAudioTrackInfos().getAudioRate());			
			}
			else { // flac
				writer.addAudioStream(1, 0, ICodec.ID.CODEC_ID_FLAC, 
						  JobConfig.getAudioTrackInfos().getNumChannels() , JobConfig.getAudioTrackInfos().getAudioRate());			
			}		
		}
		
		writer.getContainer().getStream(0).getStreamCoder().setBitRate(JobConfig.getVideoBitrate()*1000);
			
		writer.getContainer().getStream(0).getStreamCoder().setNumPicturesInGroupOfPictures(6);
		
		//writer.getContainer().getStream(0).getStreamCoder().setFlag(IStreamCoder.Flags.FLAG_QSCALE, false);
		
		
		writer.getContainer().getStream(0).getStreamCoder().open(null, null);
		
		if( this.disableSound != true && JobConfig.isVideoHasAudioTrack()){
			writer.getContainer().getStream(1).getStreamCoder()
					.open(null, null);
		}
				
		writer.getContainer().writeHeader();		
		
	}
	
	public void addFrame(BufferedImage buff, long timeMilliseconds){		
		if(this.is720 && !JobConfig.isWantJoinInputOutputFrames() 
				&& JobConfig.getSystemCrypt() != SystemType.EUROCRYPT
				&& JobConfig.getSystemCrypt() != SystemType.LUXCRYPT){
			buff = getScaledImage(buff, 720, 576);		
		}
		
		if(JobConfig.isWantJoinInputOutputFrames()) {
			writer.encodeVideo(0, Utils.joinImages(JobConfig.getInputImage(), buff) ,(timeMilliseconds *1000l),TimeUnit.MICROSECONDS);
		}
		else {
			writer.encodeVideo(0, buff ,(timeMilliseconds *1000l),TimeUnit.MICROSECONDS);
		}		 
	}
	
	
	
	public void addAudioFrame(IAudioSamples sample, boolean isInsideRangeSlider){		
		if (sample.isComplete()) {
			if(this.wantSoundCryptDecrypt && JobConfig.isHasMultiAudioChannels()
					&& isInsideRangeSlider) {
				transformMultiChannelsAudioFrame(sample, JobConfig.isReadyTransform());
			}			
			else if (this.wantSoundCryptDecrypt && !JobConfig.isHasMultiAudioChannels()
					&& isInsideRangeSlider) {
				addAudioFrameTemp(sample, JobConfig.isReadyTransform());
			} else {
				//addAudioFrameTemp(sample, false);
				writer.encodeAudio(1, sample);
			}
		} else {
			System.out.println("pas complet");
		}
	}
	
	private void transformMultiChannelsAudioFrame(IAudioSamples sample, boolean enable) {		
		double[] tabL = new double[(int)sample.getNumSamples()];
				
		IAudioSamples smp = IAudioSamples.make(tabL.length, sample.getChannels());				
		
		double lastGoodValue = 0;
		
		for (int j = 0; j < sample.getChannels(); j++) {
			for (int i = 0; i < sample.getNumSamples(); i++) {
				if (sample.getSample(i, j, sample.getFormat()) != 0){				
					tabL[i] = sample.getSample(i, j, sample.getFormat());	
					lastGoodValue = tabL[i];
				}
				else {				
					tabL[i] = lastGoodValue;
				}				
			}
			
			tabL = soundCryptL.transform(tabL, enable);				
			
			for (int i = 0; i < tabL.length; i++) {			
				smp.setSample(i, j, sample.getFormat(), (int) tabL[i]);						
			}			
		}		
		
		smp.setComplete(true, tabL.length, AUDIORATE, sample.getChannels(), sample.getFormat(),
				sample.getPts());
		
		writer.encodeAudio(1, smp);
	}

	
	private void addAudioFrameTemp(IAudioSamples sample, boolean enable) {
				
		double[] tabL = new double[(int)sample.getNumSamples()];
		
		double[] tabR = new double[(int)sample.getNumSamples()];
		
		for (int i = 0; i < sample.getNumSamples(); i++) {
			if (sample.getSample(i, 0, IAudioSamples.Format.FMT_S16) != 0){				
				tabL[i] = sample.getSample(i, 0, IAudioSamples.Format.FMT_S16);				
			}
			else {				
				tabL[i] = -100000;			
			}
			
			if (sample.getSample(i, 1, IAudioSamples.Format.FMT_S16) != 0){				
				tabR[i] = sample.getSample(i, 1, IAudioSamples.Format.FMT_S16);				
			}
			else {				
				tabR[i] = -100000;			
			}
		}
		
		//lissage en cas de badSample
		for (int i = 0; i < tabL.length; i++) {
			if(tabL[i] == -100000 && i< tabL.length -2 && i > 0){
				tabL[i] = (int) ((tabL[i+1] + tabL[i-1])/2d);				
			} else if (tabL[i] == -100000 && i< tabL.length -2 && i == 0){
				tabL[i] = (int) ((tabL[i+1] + tabL[i+2])/2d);				
			} else if (tabL[i] == -100000 && i< tabL.length -1 && i > 0){
				tabL[i] = (int) ((tabL[i-1] + tabL[i+1])/2d);				
			}else if (tabL[i] == -100000 && i< tabL.length  && i > 0){
				tabL[i] = (int) ((tabL[i-1] + tabL[i-2])/2d);				
			}else if (tabL[i] == -100000 ){
				tabL[i] = 0;							
			}
			if(tabL[i] < - 32768){
				tabL[i] = 0;					
			}
			
			if(tabR[i] == -100000 && i< tabR.length -2 && i > 0){
				tabR[i] = (int) ((tabR[i+1] + tabR[i-1])/2d);				
			} else if (tabR[i] == -100000 && i< tabR.length -2 && i == 0){
				tabR[i] = (int) ((tabR[i+1] + tabR[i+2])/2d);				
			} else if (tabR[i] == -100000 && i< tabR.length -1 && i > 0){
				tabR[i] = (int) ((tabR[i-1] + tabR[i+1])/2d);				
			}else if (tabR[i] == -100000 && i< tabR.length  && i > 0){
				tabR[i] = (int) ((tabR[i-1] + tabR[i-2])/2d);				
			}else if (tabR[i] == -100000 ){
				tabR[i] = 0;							
			}
			if(tabR[i] < - 32768){
				tabR[i] = 0;					
			}			
		}		

		tabL = soundCryptL.transform(tabL, enable);
		tabR = soundCryptR.transform(tabR, enable);
		
		IAudioSamples smp = IAudioSamples.make(tabL.length, 2);

		for (int i = 0; i < tabL.length; i++) {			
				smp.setSample(i, 0, IAudioSamples.Format.FMT_S16, (int) tabL[i]);			
				smp.setSample(i, 1, IAudioSamples.Format.FMT_S16, (int) tabR[i]);				
		}
		
		smp.setComplete(true, tabL.length, AUDIORATE, 2, IAudioSamples.Format.FMT_S16,
				sample.getPts());
		writer.encodeAudio(1, smp);
	}
	
	public void closeVideo(){
		if(this.is720 && JobConfig.getSystemCrypt() != SystemType.EUROCRYPT 
				&& JobConfig.getSystemCrypt() != SystemType.LUXCRYPT){
			IRational ratio = IRational.make(16,15);			 
			writer.getContainer().getStream(0).setSampleAspectRatio(ratio);
		}		
		writer.close();		
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
	    else if(src.getWidth()==768 && src.getHeight() == 576 && w == 720){
	    	shiftw = (double)src.getWidth()/720d;
	    }
	    else if(src.getWidth()==720 && src.getHeight()!=576 && w == 720){	    	   	
	    	shiftw = 768d/(double)src.getWidth();	    	
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