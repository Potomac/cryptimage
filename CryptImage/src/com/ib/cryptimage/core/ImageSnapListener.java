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
 * 18 sept. 2014 Author Mannix54
 */




package com.ib.cryptimage.core;

import java.awt.image.BufferedImage;

import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.event.IVideoPictureEvent;


public class ImageSnapListener extends MediaListenerAdapter {
	 
	private int count = 0;
	private BufferedImage img;
	private CryptVideo cryptVid;
	private int posFrame;
	private boolean isDec;
	private FramesPlayer frmV;
		

	/**
	 * constructor
	 * @param nbFrames number of frames to extract from the video
	 */
	public ImageSnapListener(int nbFrames, FramesPlayer frmV) {
		super();
		this.frmV = frmV;
		this.count = 0;
		cryptVid = new CryptVideo(frmV.getOutputFilename(), frmV.getKeyWord(), 
				frmV.getVideoFilename(), frmV.getNbFrames(), frmV.isbDec(),
				frmV.isStrictMode(), frmV.getPositionSynchro());
		posFrame = 0;
		this.isDec = frmV.isbDec();
		
	}

	public void onVideoPicture(IVideoPictureEvent event)  {	 	
		dumpFrameToBufferedImage(event.getImage());
		count = count + 1;
	 
	if(count == cryptVid.getVideoLengthFrames()){
		
		cryptVid.closeVideo();
		cryptVid.saveDatFileVideo();    
	}
   }

	public void dumpFrameToBufferedImage(BufferedImage image) {
	
		this.img = image;
		
		posFrame++;
		if (this.isDec) {			
			cryptVid.addFrameDec(image, posFrame, count);
			if (frmV.getPositionSynchro()  > this.count +1  ){
				posFrame =0;
			}
		}
		else {
			cryptVid.addFrameEnc(image, posFrame, count);
		}
		if (posFrame == 3) {
			posFrame = 0;
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
   
   
 }