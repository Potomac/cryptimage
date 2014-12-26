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

import java.awt.image.BufferedImage;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.ToolFactory;

public class FramesPlayer {


	private ImageSnapListener imgListen;
	private IMediaReader mediaReader;
	private JobConfig job;
	
	
	
	/**
	 * constructor for the frames extractor from a video file
	 * @param job the JobConfig object who stores all settings	
	 */
	public FramesPlayer( JobConfig job) {
		this.job = job;
		
		StreamsFinder streamFinder = new StreamsFinder(job.getInput_file());
		
		this.mediaReader = ToolFactory.makeReader(job.getInput_file());		
		
		mediaReader
				.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
		
		
		
		this.imgListen = new ImageSnapListener(job.getVideo_frame(), this,
				streamFinder.getStreamsVideo()[0], streamFinder.getStreamsAudio()[0]);
		mediaReader.addListener(imgListen);
		
	}
		
	
	public void readFrame(){
		try {
			while(imgListen.getCount()< job.getVideo_frame() && job.isStop() != true){		
				mediaReader.readPacket();		
			}
			if(job.isStop() && !job.isWantPlay()){
				job.getGui().getTextInfos().setText(
						job.getGui().getTextInfos().getText()
						+ "\n\r"
						+ "Opération interrompue par l'utilisateur");
				imgListen.getCryptVid().closeVideo();
				imgListen.getCryptVid().saveDatFileVideo();
				job.getGui().getBtnEnter().setEnabled(true);
				job.getGui().getBtnCancel().setEnabled(false);
				job.getGui().getBtnExit().setEnabled(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if(job.isHasGUI()){
				job.getGui().getTextInfos().setText(
						job.getGui().getTextInfos().getText()
						+ "\n\r"
						+ e.getMessage());
			}
			job.setVideo_frame(imgListen.getCount()-1);
			imgListen.getCryptVid().closeVideo();
			imgListen.getCryptVid().saveDatFileVideo();
		}		
	}
	
	public BufferedImage getBufImage(){
		return imgListen.getImg();
	}
	
	public String getVideoFilename() {
		return job.getInput_file();
	}


	public int getNbFrames() {
		return job.getVideo_frame();
	}


	public String getOutputFilename() {
		return job.getOutput_file();
	}


	public String getData_url() {
		return job.getReport_file();
	}


	public int getKeyWord() {
		return job.getDiscret11Word();
	}
	
	public boolean isStrictMode() {
		return job.isStrictMode();
	}


	public boolean isbDec() {
		return job.isWantDec();
	}
	
	public int getPositionSynchro() {
		return job.getPositionSynchro();
	}


	public JobConfig getJob() {
		return job;
	}


	public ImageSnapListener getImgListen() {
		return imgListen;
	}

}