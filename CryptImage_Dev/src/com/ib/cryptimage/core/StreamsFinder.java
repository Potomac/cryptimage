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
 * 28 nov. 2014 Author Mannix54
 */



package com.ib.cryptimage.core;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;


/**
 * @author Mannix54
 *
 */
public class StreamsFinder {

	private IContainer container;	
	private int[] numStreamsAudio;
	private int[] numStreamsVideo;
	private int numStreams = 0;
	
	public StreamsFinder(String filename){
		container = IContainer.make();
		if (container.open(filename, IContainer.Type.READ, null) < 0)
		      throw new IllegalArgumentException("could not open file: " + filename);
		numStreamsAudio = new int[container.getNumStreams()];
		numStreamsVideo = new int[container.getNumStreams()];
		searchStreams();
		
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
	
	public IContainer getContainer(){
		return this.container;
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
				numStreamsAudio[comptAudio] = i;
				comptAudio++;
			} else if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
				numStreamsVideo[comptVideo] = i;
				comptVideo++;
			}
		}
	}
}
