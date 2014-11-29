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
	
	public StreamsFinder(String filename){
		container = IContainer.make();
		if (container.open(filename, IContainer.Type.READ, null) < 0)
		      throw new IllegalArgumentException("could not open file: " + filename);		
	}
	
	public int getNumStreams(){
		return container.getNumStreams();
	}
	
	public IContainer getContainer(){
		int numStreams = container.getNumStreams();

		  int streamVideo = 0;
		  int streamAudio = 0;
		  boolean audio = false;
		  boolean video = false;
		  
		  //find the first video stream
		  for(int i = 0; i < numStreams; i++)
		    {
			  IStream stream = container.getStream(i);
			  IStreamCoder coder = stream.getStreamCoder();
			  if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO)
		      {
				  if(audio == false) {
					  streamAudio = i;
					  audio = true;
				  }
		      }
			  else if(coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO)
		      {
				  if(video == false) {
					  streamVideo = i;
					  video = true;
				  }
		      }		      
		    }		  
		  
		  IContainer container2 = IContainer.make();
		  container2.addNewStream(container.getStream(streamVideo).getStreamCoder());
		  container2.addNewStream(container.getStream(streamAudio).getStreamCoder());
		  return container2;
	}
}
