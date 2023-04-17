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
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.JOptionPane;

import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.event.IAudioSamplesEvent;
import com.xuggle.mediatool.event.IReadPacketEvent;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.IAudioResampler;
import com.xuggle.xuggler.IAudioSamples;


public class ImageSnapListener extends MediaListenerAdapter {
	 
	private int count = 0;
	private BufferedImage img;
	private CryptVideo cryptVid;
	private int posFrame;
	private boolean isDec;
	private FramesPlayer frmV;
	private IAudioResampler audioResampler = null;
	private int AUDIORATE;


	/**
	 * constructor
	 * @param nbFrames number of frames to extract from the video
	 */
	public ImageSnapListener(int nbFrames, FramesPlayer frmV,
			int videoIndex, int audioIndex) {		
		super();
		
		this.AUDIORATE = JobConfig.getAudioRate();
		this.frmV = frmV;
		this.count = 0;
		cryptVid = new CryptVideo();
		posFrame = 0;
		this.isDec = frmV.isbDec();
		
	}
	
	public void onAudioSamples(IAudioSamplesEvent event) {
		//System.out.println(event.getStreamIndex() + ", " + JobConfig.getDefaultIdAudioTrack());

		if (event.getStreamIndex() == JobConfig.getDefaultIdAudioTrack() && !JobConfig.isDisableSound() 
				&& JobConfig.isVideoHasAudioTrack() && !JobConfig.isHasMultiAudioChannels()) {
			if (event.getAudioSamples().isComplete()) {

				IAudioSamples samples = event.getAudioSamples();

				if (samples.getChannels() > 2) {
					JobConfig.setDisableSound(true);
					JobConfig.setWantSound(false);
					
					JobConfig.getGui().getChkSound().setSelected(false);
					JobConfig.getGui().getChkSound().setEnabled(false);
					JobConfig.getGui().getChkSoundSyster().setSelected(false);
					JobConfig.getGui().getChkSoundSyster().setEnabled(false);
					JobConfig.getGui().getChkSoundVideocrypt().setSelected(false);
					JobConfig.getGui().getChkSoundVideocrypt().setEnabled(false);
					
					JobConfig.getGui().getChkDisableSound().setSelected(true);
					JobConfig.getGui().getChkDisableSoundSyster().setSelected(true);
					JobConfig.getGui().getChkDisableSoundVideocrypt().setSelected(true);					
					
					showWarningSound();
				} else {

					// IAudioResampler audioResampler = IAudioResampler.make(2,
					// samples.getChannels(), 48000, samples.getSampleRate());

					if (audioResampler == null) {
						audioResampler = IAudioResampler.make(2,
								samples.getChannels(), AUDIORATE,
								samples.getSampleRate(),
								IAudioSamples.Format.FMT_S16,
								samples.getFormat());
					}

					// if(audioResampler == null){
					// audioResampler = IAudioResampler.make(1,
					// samples.getChannels(), AUDIORATE,
					// samples.getSampleRate(),
					// IAudioSamples.Format.FMT_S16, samples.getFormat(),
					// 2048,
					// 1024,
					// true,
					// (double)AUDIORATE/2d
					// );
					// }

					if (event.getAudioSamples().getNumSamples() > 0) {
						IAudioSamples out = IAudioSamples.make(
								samples.getNumSamples(), 2); // samples.getNumSamples(),
																// 1);
						audioResampler.resample(out, samples,
								samples.getNumSamples());

						cryptVid.addAudioFrame(out);	//out					

						out.delete();
					}
				}
			} else {
				System.out.println("paquet non complet init");
			}
		}
		
		// case of multichannel audio
		if (JobConfig.isHasMultiAudioChannels() && event.getStreamIndex() == JobConfig.getDefaultIdAudioTrack() && !JobConfig.isDisableSound() 
				&& JobConfig.isVideoHasAudioTrack() ) {
			if (event.getAudioSamples().isComplete()) {
				cryptVid.addAudioFrame(event.getAudioSamples());				
			}		
		}
		
	}

	private void showWarningSound(){
		JOptionPane
		.showMessageDialog(
				null,
				JobConfig.getRes().getString("imageSnapListener.multicanalSound"),
				JobConfig.getRes().getString("imageSnapListener.multicanalSound.title"),
				JOptionPane.WARNING_MESSAGE);		
	}

	public void onReadPacket(IReadPacketEvent event){
		
	}
	

	public void onVideoPicture(IVideoPictureEvent event)  {
		if(event.getStreamIndex() == JobConfig.getDefaultIdVideoTrack()) {
			dumpFrameToBufferedImage(event.getImage());
			count = count + 1;		
			 
			if(count  == cryptVid.getVideoLengthFrames() 
					&& JobConfig.isWantPlay() !=true
					 ){		
				cryptVid.closeVideo();
				cryptVid.saveDatFileVideo();
				cryptVid.getDevice().closeFileData();
				if(JobConfig.isSearchCode68705()) {
					cryptVid.displayKey();
				}
				
			} else if(count == cryptVid.getVideoLengthFrames()){
				if(JobConfig.isSearchCode68705()) {
					cryptVid.displayKey();
				}
				JobConfig.setStop(true);
				}
		}
   }

	public void dumpFrameToBufferedImage(BufferedImage image) {		
		if(JobConfig.isPanAndScan() && !JobConfig.isWantDec() && !JobConfig.isHasToBeUnsplit()){
			image = doPanAndScan(image);
		}
		
		if(JobConfig.isStretch() && !JobConfig.isWantDec() && !JobConfig.isHasToBeUnsplit()){
			image = doStretch(image);
		}
		
		// add input frame to JobConfig if device is not ready for transform
		if(count < JobConfig.getPositionSynchro()) {
			JobConfig.setInputImage(image);	
		}
			

		if (JobConfig.isWantPlay()) {
			posFrame++;
			if (this.isDec) {
				cryptVid.addDisplayFrameDec(image, posFrame, count);
				if (frmV.getPositionSynchro() > this.count + 1) {
					posFrame = 0;
				}
			} else {
				cryptVid.addDisplayFrameEnc(image, posFrame, count);
			}
			if (posFrame == 3) {
				posFrame = 0;
			}
		}

		else {
			posFrame++;
			if (this.isDec) {
				cryptVid.addFrameDec(image, posFrame, count);
				if (frmV.getPositionSynchro() > this.count + 1) {
					posFrame = 0;
				}
			} else {
				cryptVid.addFrameEnc(image, posFrame, count);
			}
			if (posFrame == 3) {
				posFrame = 0;
			}
		}
	}
	
   private BufferedImage doPanAndScan(BufferedImage ori_img){
	   int optimal_width = 0;
	   int optimal_height = 0;
	   int hori_cropBorders = 0;
	   int verti_cropBorders = 0;
	   BufferedImage imgCropped;
	   
	   //JobConfig.setHasToBeUnsplit(false);
	   
	   if((float) ori_img.getWidth()/ (float)ori_img.getHeight() > 4f/3f){
		   optimal_height = ori_img.getHeight();
		   optimal_width = (int) ((4f/3f) * (float) ori_img.getHeight());
		   hori_cropBorders = ( ori_img.getWidth() - optimal_width ) / 2;
		   
		   imgCropped = ori_img.getSubimage(hori_cropBorders, 0, ori_img.getWidth() - (hori_cropBorders * 2), optimal_height);				
	   }
	   else if ((float) ori_img.getWidth()/ (float)ori_img.getHeight() < 4f/3f){
		   optimal_width = ori_img.getWidth();
		   optimal_height = (int)((float)ori_img.getWidth() / (4f/3f));
		   verti_cropBorders = ( ori_img.getHeight() - optimal_height ) / 2;
		   
		   imgCropped = ori_img.getSubimage(0, verti_cropBorders, optimal_width, optimal_height);		   
	   }
	   else {
		   return ori_img;
	   }
	   
	   BufferedImage copyOfImage = new BufferedImage(imgCropped.getWidth(), imgCropped.getHeight(), imgCropped.getType());
	   Graphics g = copyOfImage.createGraphics();
	   g.drawImage(imgCropped, 0, 0, null);
	   
	   
	   
	   return copyOfImage;	   
   }
   
/**
 * Stretch image pixels horizontally and vertically to 4/3 ratio
 * @param ori_img bufferedimage to stretch
 * @return stretch bufferedimage
 */
   private BufferedImage doStretch(BufferedImage ori_img){	   
	   return resize(ori_img, 768, 576, ori_img.getType());	   
   }
   
   private BufferedImage resize(BufferedImage img, int width, int height, int typeBufferedImage) {
       Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
       BufferedImage resized = new BufferedImage(width, height, typeBufferedImage);
       Graphics g = resized.createGraphics();
       g.drawImage(tmp, 0, 0, null);
       return resized;
   }
	
   public BufferedImage getImg() {
		return img;
	}

	public void setImg(BufferedImage img) {
		this.img = img;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public CryptVideo getCryptVid() {
		return cryptVid;
	}
	
   
 }