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
 * 28 nov. 2014 Author Mannix54
 * http://ibsoftware.free.fr/cryptimage.php
 */



package com.ib.cryptimage.core;

import com.ib.cryptimage.gui.TrackSelector;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import java.util.Vector;


/**
 * @author Mannix54
 *
 */
public class StreamsFinder {

	private IContainer container;	
	private int[] numStreamsAudio;
	private int numAudioChannels;
	public int getNumAudioChannels() {
		return numAudioChannels;
	}

	private int[] numStreamsVideo;
	private int numStreams = 0;
	private boolean hasVideoTrack = false;
	private boolean hasAudioTrack = false;	
	
	private int defaultIdAudioTrack = 0;
	private int defaultIdVideoTrack = 0;
	private String defaultNameAudioCodec = "";
	public String getDefaultNameAudioCodec() {
		return defaultNameAudioCodec;
	}

	private String defaultNameIDAudioCodec = "";
	
	public String getDefaultNameIDAudioCodec() {
		return defaultNameIDAudioCodec;
	}

	private Vector<StreamTrack> audioTracks = new Vector<StreamTrack>();  
	private Vector<StreamTrack> videoTracks = new Vector<StreamTrack>(); 
	
	
	public StreamsFinder(String filename){
		container = IContainer.make();
		if (container.open(filename, IContainer.Type.READ, null) < 0)
		      throw new IllegalArgumentException("could not open file: " + filename);
		numStreamsAudio = new int[container.getNumStreams()];
		numStreamsVideo = new int[container.getNumStreams()];
		searchStreams();
		
	}
	
	private void searchStreams() {
		int numStreams = container.getNumStreams();
		int comptAudio = 0;
		int comptVideo = 0;

		// find the first video stream
		for (int i = 0; i < numStreams; i++) {
			IStream stream = container.getStream(i);
			IStreamCoder coder = stream.getStreamCoder();
			if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
				this.hasAudioTrack = true;
				
				StreamTrack audioTrack = new StreamTrack(coder.getStream().getIndex(), "audio", coder.getChannels(), coder.getCodec().getLongName());
				
				audioTrack.setCodecShortName(coder.getCodec().getName());
				audioTrack.setCodecID(coder.getCodec().getID().toString());
				audioTrack.setCodecIDInt(coder.getCodecID());				
				audioTrack.setAudioRate(coder.getSampleRate());
				
				audioTrack.setDuration(stream.getDuration());
				audioTrack.setNumFrames(stream.getNumFrames());				
				
				this.audioTracks.add(audioTrack);				
				
				numStreamsAudio[comptAudio] = i;
				comptAudio++;
			} else if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
				this.hasVideoTrack = true;
				
				StreamTrack videoTrack = new StreamTrack(coder.getStream().getIndex(), "video", coder.getCodec().getLongName());
				videoTrack.setCodecIDInt(coder.getCodecID());
				
				videoTrack.setDuration(stream.getDuration());
				videoTrack.setNumFrames(stream.getNumFrames());
				
				this.videoTracks.add(videoTrack);	
				
				numStreamsVideo[comptVideo] = i;
				comptVideo++;
			}
		}
		
		JobConfig.setStreamTracksVideo(videoTracks);
		JobConfig.setStreamTracksAudio(audioTracks);
		
		this.numStreams = this.audioTracks.size() + this.videoTracks.size();
		
		//analyzeStreams();
	}
	
	public void analyzeStreams() {
		// set default audio stream
		if(audioTracks.size() > 0) {
			this.numAudioChannels = audioTracks.get(0).getNumChannels();
			this.defaultIdAudioTrack = audioTracks.get(0).getId();
			this.defaultNameAudioCodec = audioTracks.get(0).getCodecShortName();
			this.defaultNameIDAudioCodec = audioTracks.get(0).getCodecID();
			JobConfig.setAudioTrackInfos(audioTracks.get(0));			
		}
		
//		for(StreamTrack audioTrack : this.audioTracks ) {
//			if(audioTrack.getNumChannels() >= 1 && audioTrack.getNumChannels() < 3) {
//				this.numAudioChannels = audioTrack.getNumChannels();
//				this.defaultIdAudioTrack = audioTrack.getId();
//				this.defaultNameAudioCodec = audioTrack.getCodecShortName();
//				this.defaultNameIDAudioCodec = audioTrack.getCodecID();
//				JobConfig.setAudioTrackInfos(audioTrack);
//				break;
//			}
//			if(audioTrack.getNumChannels() > 2) {
//				this.numAudioChannels = audioTrack.getNumChannels();
//				this.defaultIdAudioTrack = audioTrack.getId();
//				this.defaultNameAudioCodec = audioTrack.getCodecShortName();
//				this.defaultNameIDAudioCodec = audioTrack.getCodecID();
//				JobConfig.setAudioTrackInfos(audioTrack);
//			}			
//		}
		
		// set default video stream
		if(this.hasVideoTrack) {
			this.defaultIdVideoTrack = this.videoTracks.elementAt(0).getId();
			JobConfig.setVideoTrackInfos(this.videoTracks.elementAt(0));
		}		
		
		
		
	}
	
	public void displayTracksSelector() {
		TrackSelector trackSelector = new TrackSelector(JobConfig.getGui().getFrame(), 
									   JobConfig.getRes().getString("trackSelector.label.title"),
									   videoTracks, audioTracks, true);
		trackSelector.display();
	}
	
	
	public int getNumStreams(){
		return this.numStreams;
	}
	
	public int[] getStreamsAudio(){
		return this.numStreamsAudio;
	}
	
	public int[] getStreamsVideo(){
		return this.numStreamsVideo;
	}
	
	public int getNumAudioStreams() {
		return this.audioTracks.size();
	}
	
	public int getNumVideoStreams() {
		return this.videoTracks.size();
	}
	
	public IContainer getContainer(){
		return this.container;
	}	

	public boolean isHasVideoTrack() {
		return hasVideoTrack;
	}

	public boolean isHasAudioTrack() {
		return hasAudioTrack;
	}

	public int getDefaultIdAudioTrack() {
		return defaultIdAudioTrack;
	}

	public int getDefaultIdVideoTrack() {
		return defaultIdVideoTrack;
	}
}
