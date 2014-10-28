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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStreamCoder;
//import com.xuggle.xuggler.IProperty;
//import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;



public class VideoRecorder {
	
	private IMediaWriter writer;
	private boolean is720 = false;
	
	public VideoRecorder(String outputFilename, int width, int height,
			int videoBitrate, int videoCodec, int sWidth, double framerate) {		
		
		if(sWidth== 720){
			this.is720 = true;
			width = 720;
		}
		
		writer = ToolFactory.makeWriter(outputFilename);
		//writer.addListener(new RateChange());
		
		
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
		IRational frame_rate = IRational.make(framerate);
		writer.getContainer().getStream(0).getStreamCoder().setFrameRate(frame_rate);
		IStreamCoder coder = writer.getContainer().getStream(0).getStreamCoder();
		coder.setFrameRate(frame_rate);
		System.out.println("frame rate: " + framerate);
	
		
/*		IRational ratio = IRational.make(4,3);		
		writer.getContainer().getStream(0).setSampleAspectRatio(ratio);*/
	
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
	
	public void addFrame(BufferedImage buff, double timeMilliseconds){
		if(this.is720){
			buff = getScaledImage(buff, 720, 576);			
		}
		 writer.encodeVideo(0, buff,(int)(timeMilliseconds *1000d),TimeUnit.MICROSECONDS);
		
	}
	
	public void closeVideo(){
		if(this.is720){
			IRational ratio = IRational.make(16,15);			 
			writer.getContainer().getStream(0).setSampleAspectRatio(ratio);
		}		
		writer.close();
	}
	
	
	/**
	 * Scale an image to a new size
	 * @param src the image source
	 * @param w the new width
	 * @param h the new height
	 * @return the resized image
	 */
	private BufferedImage getScaledImage(BufferedImage src, int w, int h){
	    int finalw = w;
	    int finalh = h;
	    double factor = 1.0d;	    
	    double shiftw = 1d;
	    
	    if(src.getWidth()==720 && src.getHeight()==576 ){
	    	shiftw = (double)src.getWidth()/(double)w; // case of if width = 720 and height = 576
	    }
	    else if(src.getWidth()==768 && src.getHeight() == 576 && w == 720){
	    	shiftw = (double)src.getWidth()/720d;	    	
	    	//shiftw = 768d/(double)src.getWidth();
	    	//finalw = (int)(finalh * shiftw);
	    }
	    else if(src.getWidth()==720 && src.getHeight()!=576 && w == 720){
	    	//shiftw = (double)src.getWidth()/768d;	    	
	    	shiftw = 768d/(double)src.getWidth();
	    	//finalw = (int)(finalh * shiftw);
	    }
//	    else if(w ==720){
//	    	//shiftw = (double)src.getWidth()/768d;	    	
//	    	shiftw = 768d/720d;
//	    	//finalw = (int)(finalh * shiftw);
//	    }
	    
	    if(src.getWidth() > src.getHeight()){
	        factor = ((double)src.getHeight()/(double)src.getWidth());
	        finalh = (int)(finalw * factor * shiftw);	       
	    }else{
	        factor = ((double)src.getWidth()/(double)src.getHeight());
	        finalw = (int)(finalh * factor);
	    }   

	    BufferedImage resizedImg = new BufferedImage(finalw, finalh, BufferedImage.TRANSLUCENT);
	    Graphics2D g2 = resizedImg.createGraphics();
	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(src, 0, 0, finalw, finalh, null);
	    g2.dispose();
	    
	    //step 2 create a bufferedimage with exact size
	    
	    BufferedImage target = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
	    
	    Graphics g3 = target.getGraphics();	    
	    g3.drawImage(resizedImg, 0, (target.getHeight() - resizedImg.getHeight())/2, null);
		    
	    return target;
	}

}



//class RateChange extends MediaListenerAdapter {
//
//	public RateChange(){
//		super();
//	}
//	
//	public void onAddStream(IAddStreamEvent event) {
///*
//		IStreamCoder coder = event.getSource().getContainer().getStream(0).getStreamCoder();
//		coder.setBitRate(50000000);
//		//coder.setBitRateTolerance(0);
//*/		//System.out.println(event.getSource().getContainer().getStream(0).getStreamCoder().getBitRate());
//
//	}
//
//}