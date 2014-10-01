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

public class JobConfig {

	private String input_file = "";
	private String report_file = "";
	private String output_file = "";		
	private int discret11Word = 0;	
	private int video_frame = 0;
	private boolean strictMode = false;
	private int positionSynchro = 1;
	private boolean wantDec = false;
	private boolean wantPlay = false;	
	private boolean modePhoto = false;
	private int audienceLevel = 7;
	private int videoBitrate = 2000;
	private int videoCodec = 3;
	private double perc1 = 0.0167;
	private double perc2 = 0.0347;

	public JobConfig() {
		// TODO Auto-generated constructor stub
	}

	public String getInput_file() {
		return input_file;
	}

	public double getPerc1() {
		return perc1;
	}

	public void setPerc1(double perc1) {
		this.perc1 = perc1;
	}

	public double getPerc2() {
		return perc2;
	}

	public void setPerc2(double perc2) {
		this.perc2 = perc2;
	}

	public void setInput_file(String input_file) {
		this.input_file = input_file;
	}

	public String getReport_file() {
		return report_file;
	}

	public void setReport_file(String report_file) {
		this.report_file = report_file;
	}

	public String getOutput_file() {
		return output_file;
	}

	public void setOutput_file(String output_file) {
		this.output_file = output_file;
	}

	public int getDiscret11Word() {
		return discret11Word;
	}

	public void setDiscret11Word(int discret11Word) {
		this.discret11Word = discret11Word;
	}

	public int getVideo_frame() {
		return video_frame;
	}

	public void setVideo_frame(int video_frame) {
		this.video_frame = video_frame;
	}

	public boolean isStrictMode() {
		return strictMode;
	}

	public int getAudienceLevel() {
		return audienceLevel;
	}

	public void setAudienceLevel(int audienceLevel) {
		this.audienceLevel = audienceLevel;
	}

	public int getVideoBitrate() {
		return videoBitrate;
	}

	public void setVideoBitrate(int videoBitrate) {
		this.videoBitrate = videoBitrate;
	}

	public int getVideoCodec() {
		return videoCodec;
	}

	public void setVideoCodec(int videoCodec) {
		this.videoCodec = videoCodec;
	}

	public void setStrictMode(boolean strictMode) {
		this.strictMode = strictMode;
	}

	public int getPositionSynchro() {
		return positionSynchro;
	}

	public void setPositionSynchro(int positionSynchro) {
		this.positionSynchro = positionSynchro;
	}

	public boolean isWantDec() {
		return wantDec;
	}

	public void setWantDec(boolean wantDec) {
		this.wantDec = wantDec;
	}
	
	public boolean isWantPlay() {
		return wantPlay;
	}

	public void setWantPlay(boolean wantPlay) {
		this.wantPlay = wantPlay;
	}

	public boolean isModePhoto() {
		return modePhoto;
	}

	public void setModePhoto(boolean modePhoto) {
		this.modePhoto = modePhoto;
	}
	
}
