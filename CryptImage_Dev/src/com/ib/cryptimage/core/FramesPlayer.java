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

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.ToolFactory;

public class FramesPlayer {


	private ImageSnapListener imgListen;
	private IMediaReader mediaReader;	
	
	
	
	/**
	 * constructor for the frames extractor from a video file
	 * @param job the JobConfig object who stores all settings	
	 */
	public FramesPlayer() {
				
		StreamsFinder streamFinder = new StreamsFinder(JobConfig.getInput_file());
		
		this.mediaReader = ToolFactory.makeReader(JobConfig.getInput_file());		
		
		mediaReader
				.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
		
		
		
		this.imgListen = new ImageSnapListener(JobConfig.getVideo_frame(), this,
				streamFinder.getStreamsVideo()[0], streamFinder.getStreamsAudio()[0]);		
		mediaReader.addListener(imgListen);
		
	}
		
	
	public void readFrame(){
		try {
			while( imgListen.getCount() < JobConfig.getVideo_frame()
					&& JobConfig.isStop() != true){				
				mediaReader.readPacket();		
			}
			
			if(JobConfig.isStop() && !JobConfig.isWantPlay()){
				JobConfig.getGui().getTextInfos().setText(
						JobConfig.getGui().getTextInfos().getText()
						+ "\n\r"
						+ "Opération interrompue par l'utilisateur");
				imgListen.getCryptVid().closeVideo();
				imgListen.getCryptVid().saveDatFileVideo();
				JobConfig.getGui().getBtnEnter().setEnabled(true);
				JobConfig.getGui().getBtnCancel().setEnabled(false);
				JobConfig.getGui().getBtnExit().setEnabled(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if(JobConfig.isHasGUI()){
				JobConfig.getGui().getTextInfos().setText(
						JobConfig.getGui().getTextInfos().getText()
						+ "\n\r"
						+ e.getMessage());
			}
			JobConfig.setVideo_frame(imgListen.getCount()-1);
			imgListen.getCryptVid().closeVideo();
			imgListen.getCryptVid().saveDatFileVideo();
		}		
	}
	
	public BufferedImage getBufImage(){
		return imgListen.getImg();
	}
	
	public String getVideoFilename() {
		return JobConfig.getInput_file();
	}


	public int getNbFrames() {
		return JobConfig.getVideo_frame();
	}


	public String getOutputFilename() {
		return JobConfig.getOutput_file();
	}


	public String getData_url() {
		return JobConfig.getReport_file();
	}


	public int getKeyWord() {
		return JobConfig.getDiscret11Word();
	}
	
	public boolean isStrictMode() {
		return JobConfig.isStrictMode();
	}


	public boolean isbDec() {
		return JobConfig.isWantDec();
	}
	
	public int getPositionSynchro() {
		return JobConfig.getPositionSynchro();
	}


//	public JobConfig getJob() {
//		return job;
//	}


	public ImageSnapListener getImgListen() {
		return imgListen;
	}

}