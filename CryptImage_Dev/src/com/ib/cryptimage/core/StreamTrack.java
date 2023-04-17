package com.ib.cryptimage.core;

import com.xuggle.xuggler.ICodec;

public class StreamTrack {
	private int id;
	private ICodec.ID codecIDInt;
	public ICodec.ID getCodecIDInt() {
		return codecIDInt;
	}

	public void setCodecIDInt(ICodec.ID codecIDInt) {
		this.codecIDInt = codecIDInt;
	}

	private String type;
	private String codecLongName;
	private String codecShortName;
	public void setCodecShortName(String codecShortName) {
		this.codecShortName = codecShortName;
	}

	public String getCodecShortName() {
		return codecShortName;
	}

	private String codecID;
	public String getCodecID() {
		return codecID;
	}

	public void setCodecID(String codecID) {
		this.codecID = codecID;
	}

	private int numChannels;
	
	private long duration;
	
	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}
	
	private long numFrames;

	public long getNumFrames() {
		return numFrames;
	}

	public void setNumFrames(long numFrames) {
		this.numFrames = numFrames;
	}

	private int audioRate;
	
	public int getAudioRate() {
		return audioRate;
	}

	public void setAudioRate(int audioRate) {
		this.audioRate = audioRate;
	}

	public StreamTrack(int id, String type, int numChannels, String codecName) {
		this.id = id;
		this.type = type;
		this.numChannels = numChannels;
		this.codecLongName = codecName;
	}
	
	public StreamTrack(int id, String type, String codecName) {
		this.id = id;
		this.type = type;
		this.codecLongName = codecName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getNumChannels() {
		return numChannels;
	}

	public void setNumChannels(int numChannels) {
		this.numChannels = numChannels;
	}

	public String getCodecLongName() {
		return codecLongName;
	}

	public void setCodecLongName(String codecName) {
		this.codecLongName = codecName;
	}	
	
}
