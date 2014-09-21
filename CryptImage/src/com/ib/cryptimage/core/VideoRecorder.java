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
import java.util.concurrent.TimeUnit;
import com.xuggle.xuggler.ICodec;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IAddStreamEvent;


public class VideoRecorder {
	
	private IMediaWriter writer;
	
	public VideoRecorder(String outputFilename, int width, int height) {		
		writer = ToolFactory.makeWriter(outputFilename);
		writer.addListener(new RateChange());
				
//		writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264,
//		                   width, height);
		writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4,
                width, height);
				
		writer.getContainer().getStream(0).getStreamCoder().setBitRate(7000*1024);

		
	}
	
	public void addFrame(BufferedImage buff, int timeMilliseconds){
		 writer.encodeVideo(0, buff,timeMilliseconds,TimeUnit.MILLISECONDS);
		
	}
	
	public void closeVideo(){
		writer.close();
	}

}

class RateChange extends MediaListenerAdapter {

	public RateChange(){
		super();
	}
	
	public void onAddStream(IAddStreamEvent event) {
/*
		IStreamCoder coder = event.getSource().getContainer().getStream(0).getStreamCoder();
		coder.setBitRate(50000000);
		//coder.setBitRateTolerance(0);
*/		//System.out.println(event.getSource().getContainer().getStream(0).getStreamCoder().getBitRate());

	}

}