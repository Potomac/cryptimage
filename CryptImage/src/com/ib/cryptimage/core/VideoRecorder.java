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
import com.xuggle.xuggler.IRational;
//import com.xuggle.xuggler.IProperty;
//import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IAddStreamEvent;
import com.xuggle.xuggler.IPixelFormat;

public class VideoRecorder {
	
	private IMediaWriter writer;
	
	public VideoRecorder(String outputFilename, int width, int height,
			int videoBitrate, int videoCodec) {		
		writer = ToolFactory.makeWriter(outputFilename);
		writer.addListener(new RateChange());
		
		
		switch (videoCodec) {
		case 1:
			writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, width, height);
//			writer.getContainer().getStream(0).getStreamCoder().setPixelType(IPixelFormat.Type.YUV420P);
//			writer.getContainer().getStream(0).getStreamCoder().setNumPicturesInGroupOfPictures(25);
			
			break;
		case 2:
			writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG2VIDEO, width, height);
			/*IRational frameRate = IRational.make(1, 25);
			writer.getContainer().getStream(0).getStreamCoder().setFrameRate(frameRate);
			//writer.getContainer().getStream(0).getStreamCoder().setTimeBase(IRational.make(frameRate.getDenominator(),
			//		frameRate.getNumerator()));
*/			break;
		case 3:
			writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, width, height);
			break;
		default:
			break;
		}
				
		//writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264,
        //        width, height);
				
		writer.getContainer().getStream(0).getStreamCoder().setBitRate(videoBitrate*1024);
	
		//IStreamCoder coder = writer.getContainer().getStream(0).getStreamCoder();
//		writer.getContainer().getStream(0).getStreamCoder()
//		.setProperty("flags", "+cgop+umv");
//		writer.getContainer().getStream(0).getStreamCoder()
//		.setProperty("me_method", "+esa");
//		writer.getContainer().getStream(0).getStreamCoder().setProperty("qcomp", 0.6);
		//writer.getContainer().getStream(0).getStreamCoder().setGlobalQuality(5000000);
		
		/* int j = 0;
	       String test;
	       
	       IStreamCoder coder = writer.getContainer().getStream(0).getStreamCoder();
	       ICodec codec = ICodec.findEncodingCodec(ICodec.ID.CODEC_ID_H264);
	       
	       String codecname = codec.getLongName();
	       System.out.println("Codec Name: " + codecname);
	       
	       int numproperties = coder.getNumProperties();
	       System.out.println("Number of Properties: " + numproperties);
	       
	       
	       
	       IProperty testProperty = coder.getPropertyMetaData(j);
	     
	       while (j < numproperties)
	       {
	           testProperty = coder.getPropertyMetaData(j);
	           String testname = testProperty.getName();
	           test = coder.getPropertyAsString(testname);
	           System.out.println("Property " + j + " = " + testname + "  Value = " + test);
	           j++;
	       }*/
		

		
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