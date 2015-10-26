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
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;



public class VideoRecorder {
	
	private IMediaWriter writer;	
	private boolean is720 = false;
	private boolean wantDec = false;
	private boolean wantSoundCryptDecrypt = false;
	private boolean disableSound;
	private static int AUDIORATE = 44100;		
	private SoundCrypt soundCryptR;
	private SoundCrypt soundCryptL;	
	
	
	public VideoRecorder(String outputFilename, int width, int height,
			double framerate) {	
				
		this.wantDec = JobConfig.isWantDec();
		this.wantSoundCryptDecrypt = JobConfig.isWantSound();
		this.disableSound = JobConfig.isDisableSound();
		
		if(wantSoundCryptDecrypt){
			soundCryptR = new SoundCrypt(AUDIORATE, this.wantDec);
			soundCryptL = new SoundCrypt(AUDIORATE, this.wantDec);
		}
		
		
		
		if(JobConfig.getsWidth()== 720 && JobConfig.isStrictMode() == true ){
			this.is720 = true;
			width = 720;
		}
		
		writer = ToolFactory.makeWriter(outputFilename);		
		IRational frame_rate = IRational.make(framerate);		
		
		
		switch (JobConfig.getVideoCodec()) {
		case 1:
			writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264,frame_rate, width, height);
			writer.getContainer().getStream(0).getStreamCoder().setPixelType(IPixelFormat.Type.YUV420P);
			
			break;
		case 2:
			writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG2VIDEO,frame_rate, width, height);
		break;
		case 3:
			writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4,frame_rate, width, height);
			break;
		case 4:		
			writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_FFVHUFF,frame_rate, width, height);
			writer.getContainer().getStream(0).getStreamCoder().setPixelType(IPixelFormat.Type.RGB24);			
			break;
		case 5:
			writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264,frame_rate, width, height);
			writer.getContainer().getStream(0).getStreamCoder().setPixelType(IPixelFormat.Type.YUV444P);			
			break;
		case 6:
			writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_FFV1,frame_rate, width, height);
			writer.getContainer().getStream(0).getStreamCoder().setPixelType(IPixelFormat.Type.YUV444P);	
			break;
		default:
			break;
		}		
				
		if( this.disableSound != true && JobConfig.isVideoHasAudioTrack()){
			switch (JobConfig.getAudioCodec()) {
			case 1:
				writer.addAudioStream(1, 0, ICodec.ID.CODEC_ID_MP3, 2,AUDIORATE);	
				writer.getContainer().getStream(1).getStreamCoder().setBitRate(192*1000);		
				break;
			case 2:
				writer.addAudioStream(1, 0, ICodec.ID.CODEC_ID_PCM_S16LE, 2,AUDIORATE);	
				//writer.getContainer().getStream(1).getStreamCoder().setBitRate(192*1000);		
				break;
			default:
				break;
			}		
		}
		
		writer.getContainer().getStream(0).getStreamCoder().setBitRate(JobConfig.getVideoBitrate()*1024);
			
		writer.getContainer().getStream(0).getStreamCoder().setNumPicturesInGroupOfPictures(25);
	       
		writer.getContainer().getStream(0).getStreamCoder().open(null, null);
		
		if( this.disableSound != true && JobConfig.isVideoHasAudioTrack()){
			writer.getContainer().getStream(1).getStreamCoder()
					.open(null, null);
		}
		
		writer.getContainer().writeHeader();		
		
	}
	
	public void addFrame(BufferedImage buff, double timeMilliseconds){		
		if(this.is720){
			buff = getScaledImage(buff, 720, 576);			
		}		
		 writer.encodeVideo(0, buff,(int)(timeMilliseconds *1000d),TimeUnit.MICROSECONDS);
	}
	
	
	
	public void addAudioFrame(IAudioSamples sample){	
		
		if (sample.isComplete()) {
			if (this.wantSoundCryptDecrypt) {
				addAudioFrameTemp(sample, JobConfig.isReadyTransform());
			} else {
				//addAudioFrameTemp(sample, false);
				writer.encodeAudio(1, sample);
			}
		} else {
			System.out.println("pas complet");
		}
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
		if(this.is720){
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