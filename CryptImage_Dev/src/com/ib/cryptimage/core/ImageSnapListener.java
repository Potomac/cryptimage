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
import java.awt.image.BufferedImage;
import java.util.Locale;
import java.util.ResourceBundle;

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
	private ResourceBundle res;

	/**
	 * constructor
	 * @param nbFrames number of frames to extract from the video
	 */
	public ImageSnapListener(int nbFrames, FramesPlayer frmV,
			int videoIndex, int audioIndex) {		
		super();
		res = ResourceBundle.getBundle("ressources/mainGui", new Locale(JobConfig.getUserLanguage()));
		this.AUDIORATE = JobConfig.getAudioRate();
		this.frmV = frmV;
		this.count = 0;
		cryptVid = new CryptVideo();
		posFrame = 0;
		this.isDec = frmV.isbDec();	
	}
	
	public void onAudioSamples(IAudioSamplesEvent event) {

		if (JobConfig.isDisableSound() == false 
				&& JobConfig.isVideoHasAudioTrack()) {
			if (event.getAudioSamples().isComplete()) {

				IAudioSamples samples = event.getAudioSamples();

				if (samples.getChannels() > 2) {
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
	}

	private void showWarningSound(){
		JOptionPane
		.showMessageDialog(
				null,
				res.getString("imageSnapListener.multicanalSound"),
				res.getString("imageSnapListener.multicanalSound.title"),
				JOptionPane.WARNING_MESSAGE);
		JobConfig.setDisableSound(true);
	}

	public void onReadPacket(IReadPacketEvent event){
		
	}
	

	public void onVideoPicture(IVideoPictureEvent event)  {

		dumpFrameToBufferedImage(event.getImage());
		count = count + 1;		
	 
	if(count  == cryptVid.getVideoLengthFrames() 
			&& JobConfig.isWantPlay() !=true
			 ){		
		cryptVid.closeVideo();
		cryptVid.saveDatFileVideo();
		cryptVid.getDevice().closeFileData();		
	} else if(count == cryptVid.getVideoLengthFrames()){
		JobConfig.setStop(true);
		}
   }

	public void dumpFrameToBufferedImage(BufferedImage image) {		
		this.img = image;

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