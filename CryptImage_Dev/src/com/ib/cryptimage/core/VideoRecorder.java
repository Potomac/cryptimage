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
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import com.xuggle.xuggler.IAudioResampler;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IProperty;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStream.Direction;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.ferry.IBuffer;
//import com.xuggle.xuggler.IProperty;
//import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;



public class VideoRecorder {
	
	private IMediaWriter writer;
	private boolean is720 = false;
	private boolean wantDec = false;
	private boolean wantSoundCryptDecrypt = false;
	private static int AUDIORATE = 44100;
	private static int BUFFERSIZE = 44100;
	
	private SoundCrypt soundCrypt;
	private double[] tabSound;
	private int samplesCount = 0;
	private long audioPTS = (long)((1d/AUDIORATE) * 1E06) * -1;
	
	
	public VideoRecorder(String outputFilename, int width, int height,
			int videoBitrate, int videoCodec, int sWidth, 
			boolean isStrictMode, double framerate, boolean wantSound,
			boolean wantDec) {	
		
		this.wantDec = wantDec;
		this.wantSoundCryptDecrypt = wantSound;
		
		if(wantSoundCryptDecrypt){
			soundCrypt = new SoundCrypt(AUDIORATE, this.wantDec);
			tabSound = new double[BUFFERSIZE];
		}
		
		
		
		if(sWidth== 720 && isStrictMode == true ){
			this.is720 = true;
			width = 720;
		}
		
		writer = ToolFactory.makeWriter(outputFilename);
		//writer.addListener(new RateChange());
		IRational frame_rate = IRational.make(framerate);		
		
		
		switch (videoCodec) {
		case 1:
			writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264,frame_rate, width, height);
			writer.getContainer().getStream(0).getStreamCoder().setPixelType(IPixelFormat.Type.YUV420P);
//			writer.getContainer().getStream(0).getStreamCoder().setNumPicturesInGroupOfPictures(25);
			
			break;
		case 2:
			writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG2VIDEO,frame_rate, width, height);
			/*IRational frameRate = IRational.make(1, 25);
			writer.getContainer().getStream(0).getStreamCoder().setFrameRate(frameRate);
			//writer.getContainer().getStream(0).getStreamCoder().setTimeBase(IRational.make(frameRate.getDenominator(),
			//		frameRate.getNumerator()));
*/			break;
		case 3:
			writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4,frame_rate, width, height);
			break;
		case 4:
			writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_FFVHUFF,frame_rate, width, height);
			//writer.getContainer().getStream(0).getStreamCoder().setPixelType(IPixelFormat.Type.YUV422P);
			break;
		case 5:
			writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264,frame_rate, width, height);
			writer.getContainer().getStream(0).getStreamCoder().setPixelType(IPixelFormat.Type.YUV422P);			
			break;
		default:
			break;
		}
				
		//writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264,
        //        width, height);
				
		//if( wantSound == true){
		writer.addAudioStream(1, 0, ICodec.ID.CODEC_ID_MP3, 1,AUDIORATE);
		//writer.addAudioStream(1, 0, ICodec.ID.CODEC_ID_PCM_S16LE, 1,AUDIORATE);
	    //writer.getContainer().getStream(1).getStreamCoder().setFlag(IStreamCoder.Flags.FLAG_QSCALE, false);
		writer.getContainer().getStream(1).getStreamCoder().setBitRate(192*1000);
		//writer.getContainer().getStream(1).getContainer().getStream(0).getStreamCoder().setBitRate(192000);
		//writer.getContainer().getStream(1).getStreamCoder().setSampleFormat(IAudioSamples.Format.FMT_S16);
		//writer.getContainer().getStream(1).getStreamCoder().setSampleRate(48000);
		//writer.getContainer().getStream(1).getStreamCoder().setProperty("frame_size", "576");
		//writer.getContainer().getStream(1).getStreamCoder().setProperty("frame_bits", "5760000");
		//writer.getContainer().getStream(1).getStreamCoder().setDefaultAudioFrameSize(1152);
		//System.out.println(writer.getContainer().getStream(1).getStreamCoder().getDefaultAudioFrameSize());
		//}
		
		writer.getContainer().getStream(0).getStreamCoder().setBitRate(videoBitrate*1024);
		
		if(videoCodec != 3){		
			//writer.getContainer().getStream(0).getStreamCoder().setPixelType(IPixelFormat.Type.YUV422P);
		}		
		
		/*int prop = writer.getContainer().getStream(0).getStreamCoder().getNumProperties();
		Collection<String> tab = writer.getContainer().getStream(0).getStreamCoder().getPropertyNames();
		
		System.out.println("props " + prop);
		for (Iterator iterator = tab.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			System.out.println(string + " : " + writer.getContainer().getStream(0).getStreamCoder().getPropertyAsLong(string));
			
		}*/
		//writer.getContainer().getStream(0).getStreamCoder().setProperty("qmin", -1);
		//writer.getContainer().getStream(0).getStreamCoder().setBitRate(1000);
//		writer.getContainer().getStream(0).getStreamCoder().setProperty("b", videoBitrate*1000);
//		writer.getContainer().getStream(0).getStreamCoder().setProperty("ab", videoBitrate*1000);
//		writer.getContainer().getStream(0).getStreamCoder().setProperty("bufsize", (videoBitrate*1000 )/10);
//		writer.getContainer().getStream(0).getStreamCoder().setProperty("minrate", videoBitrate*1000);
//		writer.getContainer().getStream(0).getStreamCoder().setProperty("maxrate", videoBitrate*1000);
		//System.out.println(writer.getContainer().getStream(0).getStreamCoder().getPropertyAsLong("qmax"));
		
		//IRational frame_rate = IRational.make(framerate);
		//writer.getContainer().getStream(0).getStreamCoder().setFrameRate(frame_rate);
		IStreamCoder coder = writer.getContainer().getStream(0).getStreamCoder();
		//coder.setFrameRate(frame_rate);
		System.out.println("frame rate: " + framerate);
		writer.getContainer().getStream(0).getStreamCoder().setNumPicturesInGroupOfPictures(30);
		
/*		IRational ratio = IRational.make(4,3);		
		writer.getContainer().getStream(0).setSampleAspectRatio(ratio);*/
	
		//IStreamCoder coder = writer.getContainer().getStream(0).getStreamCoder();
//		writer.getContainer().getStream(0).getStreamCoder()
//		.setProperty("flags", "+cgop+umv");
//		writer.getContainer().getStream(0).getStreamCoder()
//		.setProperty("me_method", "+esa");
//		writer.getContainer().getStream(0).getStreamCoder().setProperty("qcomp", 0.6);
		//writer.getContainer().getStream(0).getStreamCoder().setGlobalQuality(5000000);
		
	/*	 int j = 0;
	       String test;
	       
	       IStreamCoder coder3 = writer.getContainer().getStream(1).getStreamCoder();
	      // ICodec codec = ICodec.findEncodingCodec(ICodec.ID.CODEC_ID_H264);
	       
	       //String codecname = codec.getLongName();
	       //System.out.println("Codec Name: " + codecname);
	       
	       int numproperties = coder3.getNumProperties();
	       //System.out.println("Number of Properties: " + numproperties);
	       
	       
	       
	       IProperty testProperty = coder3.getPropertyMetaData(j);
	     
	       while (j < numproperties)
	       {
	           testProperty = coder3.getPropertyMetaData(j);
	           String testname = testProperty.getName();
	           test = coder.getPropertyAsString(testname);
	           System.out.println("Property " + j + " = " + testname + "  Value = " + test);
	           j++;
	       }*/
	       
	       
		writer.getContainer().getStream(0).getStreamCoder().open(null, null);
		//if (wantSound == true) {
			writer.getContainer().getStream(1).getStreamCoder()
					.open(null, null);
		//}
		writer.getContainer().writeHeader();
		
	}
	
	public void addFrame(BufferedImage buff, double timeMilliseconds){		
		if(this.is720){
			buff = getScaledImage(buff, 720, 576);			
		}
			
//		File outputfile = new File("/home/cesar/vms/test/debug_images/image_" + timeMilliseconds + ".png");
//		try {
//			ImageIO.write(buff, "png", outputfile);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		//buff = convertToType(buff, BufferedImage.TYPE_4BYTE_ABGR);
		
		 writer.encodeVideo(0, buff,(int)(timeMilliseconds *1000d),TimeUnit.MICROSECONDS);
	}
		

	private void feedSoundTab(IAudioSamples sample) {

		int ajout, reste = 0;

		int size = (int) sample.getNumSamples();

		int tot = size + samplesCount;

		if (tot < BUFFERSIZE) {
			ajout = size;
			for (int i = 0; i < sample.getNumSamples(); i++) {
				tabSound[i + this.samplesCount] = sample.getSample(i, 0,
						IAudioSamples.Format.FMT_S16);
			}
			samplesCount += ajout;

		} else if (tot > BUFFERSIZE) {
			ajout = BUFFERSIZE - samplesCount;
			reste = tot - BUFFERSIZE;
			for (int i = 0; i < ajout; i++) {
				tabSound[i + this.samplesCount] = sample.getSample(i, 0,
						IAudioSamples.Format.FMT_S16);
			}
			samplesCount += ajout;
			// on a atteint BUFFERSIZE
			encodeAudioFrame();
			resetTabSound();
			//on ajoute le reste
			for (int i = 0; i < reste; i++) {
				tabSound[i + this.samplesCount] = sample.getSample(i, 0,
						IAudioSamples.Format.FMT_S16);
			}
			samplesCount += reste;
		}
		
		if(samplesCount == BUFFERSIZE){
			encodeAudioFrame();
			resetTabSound();
		}

	}
	
	private void resetTabSound(){
		for (int i = 0; i < tabSound.length; i++) {
			tabSound[i] = 0;
		}
		samplesCount = 0;
	}
	
	private void encodeAudioFrame(){
		
		System.out.println("debut recherche zero");
		for (int i = 0; i < tabSound.length; i++) {			
			if (tabSound[i] == 0) {
				System.out.println("zéro1" + " " + i + " sur " + tabSound.length);
			}
		}
		System.out.println("fin recherche zero");
		
		tabSound = soundCrypt.transform(tabSound);
		
		IAudioSamples smp = IAudioSamples.make(BUFFERSIZE,1);		
		
		System.out.println("debut recherche zero2");
		for (int i = 0; i < tabSound.length; i++) {					
			smp.setSample(i,0, IAudioSamples.Format.FMT_S16, (int)tabSound[i]);
			if (tabSound[i] == 0) {
				System.out.println("zéro2" + " " + i+ " sur " + tabSound.length);
			}
		}
		System.out.println("fin recherche zero2");
		
		this.audioPTS += (1d/AUDIORATE) * 1E06 * BUFFERSIZE/AUDIORATE;
		
		smp.setComplete(true, tabSound.length, AUDIORATE, 1, 
				IAudioSamples.Format.FMT_S16, this.audioPTS);		 
		
		writer.encodeAudio(1, smp);		
		
	}
	
	public void addAudioFrame(IAudioSamples sample){

		/* SoundCrypt soundCrypt = new SoundCrypt(48000);
		 byte[] rawBytes = sample.getData().getByteArray(0, sample.getSize());
		 rawBytes = soundCrypt.transform(rawBytes, (int)sample.getNumSamples());*/
	/*	 byte[] rawBytes = sample.getData().getByteArray(0, sample.getSize());
		 IBuffer iBuf = IBuffer.make(null,rawBytes,0,rawBytes.length); 
		 IAudioSamples smp = IAudioSamples.make(iBuf, 1,IAudioSamples.Format.FMT_S16);
		 smp.setComplete(true, sample.getNumSamples(), 48000, 2,
				 IAudioSamples.Format.FMT_S16, sample.getPts());*/
		
		if(sample.isComplete()){
		    //feedSoundTab(sample);
			if(this.wantSoundCryptDecrypt){
		 addAudioFrameTemp(sample);
			}
			else{				
				writer.encodeAudio(1, sample);	
			}			
		}
		else {
		System.out.println("pas complet");
		}
//		sample.setComplete(true, sample.getNumSamples(), AUDIORATE, 1, 
//				IAudioSamples.Format.FMT_S16, sample.getPts());
//		
//		int size = (int)(sample.getNumSamples());
//		double[] tabL = new double[size];
//		//double[] tabR = new double[size];
//
//		
//		sample.setComplete(true, sample.getNumSamples(), AUDIORATE, 1, 
//				IAudioSamples.Format.FMT_S16, sample.getPts());
//		
//		for (int i = 0; i < size; i++) {			
//			tabL[i] = sample.getSample(i, 0, IAudioSamples.Format.FMT_S16);
//			//tabR[i] = sample.getSample(i, 1, IAudioSamples.Format.FMT_S16);
//		}
//		
//		
//		tabL = soundCrypt.transform(tabL);
//		//tabR = soundCrypt.transform(tabR);
//		
////		byte[] rawBytes = new byte[ sample.getSize()];
////		IBuffer iBuf = IBuffer.make(null,rawBytes,0,rawBytes.length ); 
////		IAudioSamples smp = IAudioSamples.make(iBuf, 1,IAudioSamples.Format.FMT_S16);
////		
//		
//		
//		IAudioSamples smp = IAudioSamples.make(sample.getNumSamples(),1);
//		
//		
//		
//		for (int i = 0; i < size; i++) {					
//			smp.setSample(i,0, IAudioSamples.Format.FMT_S16, (int)tabL[i]);
////			smp.setComplete(false, size, AUDIORATE, 1, 
////			IAudioSamples.Format.FMT_S16, sample.getPts());
//			//smp.setSample(i, 1, IAudioSamples.Format.FMT_S16, (int)tabR[i]);
//			
//		}
//		
//		smp.setComplete(true, size, AUDIORATE, 1, 
//				IAudioSamples.Format.FMT_S16, sample.getPts());
//		//System.out.println(size + " - " + smp.getNumSamples() + " - " + sample.getPts());
//		 
//		
//		writer.encodeAudio(1, smp);		
		
	}
	
	private void addAudioFrameTemp(IAudioSamples sample) {
//		sample.setComplete(true, sample.getNumSamples(), AUDIORATE, 1,
//				IAudioSamples.Format.FMT_S16, sample.getPts());
		
//		 byte[] rawBytes = new byte[ sample.getSize()];
//		 IBuffer iBuf = IBuffer.make(null,rawBytes,0,rawBytes.length );
//		
//		 
//		 IAudioSamples smp2 = IAudioSamples.make(iBuf,
//		 1,IAudioSamples.Format.FMT_S16);	
		
//	    IAudioResampler resample = IAudioResampler.make(1, 1, AUDIORATE,
//	    		AUDIORATE,
//	    		IAudioSamples.Format.FMT_S16, sample.getFormat());
//	    
//	    IAudioSamples test = IAudioSamples.make(5000, 1);
//	    
//	    resample.resample(test, sample, sample.getNumSamples());
//	    
	    
		
		int badSample = 0;
		int goodSample = 0;

		int[] size = new int[(int)sample.getNumSamples()];
		double[] tabL = new double[(int)sample.getNumSamples()];
		
		for (int i = 0; i < sample.getNumSamples(); i++) {
			if (sample.getSample(i, 0, IAudioSamples.Format.FMT_S16) != 0){
				//size[i] = i;
				tabL[i] = sample.getSample(i, 0, IAudioSamples.Format.FMT_S16);
				goodSample++;
			}
			else {
				tabL[i] = -100000;
				//size[i] = -100000;
				badSample++;
			}
		}
		
		System.out.println("bad sample :" + badSample);
//		double[] tabL = new double[goodSample];
		// double[] tabR = new double[size];
		
		//System.out.println("sample complet --- : " + sample.isComplete());

//		sample.setComplete(true, sample.getNumSamples(), AUDIORATE, 1,
//				IAudioSamples.Format.FMT_S16, sample.getPts());

//		int count = 0;
//		for (int i = 0; i < size.length; i++) {
//			if (size[i] !=-100000){
//				tabL[count] = sample.getSample(size[i], 0, IAudioSamples.Format.FMT_S16);				
//				count++;
//			}			
//		}
		
		//lissage en cas de badSample
		for (int i = 0; i < tabL.length; i++) {
			if(tabL[i] == -100000 && i< tabL.length -2 && i > 0){
				tabL[i] = (int) ((tabL[i+1] + tabL[i-1])/2d);
				System.out.println("correctif zéro : " + tabL[i]);
			} else if (tabL[i] == -100000 && i< tabL.length -2 && i == 0){
				tabL[i] = (int) ((tabL[i+1] + tabL[i+2])/2d);
				System.out.println("correctif zéro : " + tabL[i]);
			} else if (tabL[i] == -100000 && i< tabL.length -1 && i > 0){
				tabL[i] = (int) ((tabL[i-1] + tabL[i+1])/2d);
				System.out.println("correctif zéro : " + tabL[i]);
			}else if (tabL[i] == -100000 && i< tabL.length  && i > 0){
				tabL[i] = (int) ((tabL[i-1] + tabL[i-2])/2d);
				System.out.println("correctif zéro : " + tabL[i]);
			}else if (tabL[i] == -100000 ){
				tabL[i] = 0;
				System.out.println("correctif zéro !!");				
			}
			if(tabL[i] < - 32768){
				tabL[i] = 0;
				System.out.println("--------re-correctif zéro !!");		
			}			
		}		
		
		

		tabL = soundCrypt.transform(tabL);
		// tabR = soundCrypt.transform(tabR);

		// byte[] rawBytes = new byte[ sample.getSize()];
		// IBuffer iBuf = IBuffer.make(null,rawBytes,0,rawBytes.length );
		// IAudioSamples smp = IAudioSamples.make(iBuf,
		// 1,IAudioSamples.Format.FMT_S16);
		//


		
		int total = (int)sample.getNumSamples() - badSample;
		
		System.out.println("good samples : " + total + " table length: " + tabL.length );
		
		IAudioSamples smp = IAudioSamples.make(tabL.length, 1);

		for (int i = 0; i < tabL.length; i++) {
			if (tabL[i] != -100000) {
				smp.setSample(i, 0, IAudioSamples.Format.FMT_S16, (int) tabL[i]);				
			}
			else{
				System.out.println("correctif");
				smp.setSample(i, 0,IAudioSamples.Format.FMT_S16, 
						0);
//				smp.setSample(i, 0,IAudioSamples.Format.FMT_S16, 
//						(int) ((tabL[i-2] + tabL[i-1])/2d));
			}
			//sample.setSample(i, 0, IAudioSamples.Format.FMT_S16, (int) tabL[i]);
			// smp.setComplete(false, size, AUDIORATE, 1,
			// IAudioSamples.Format.FMT_S16, sample.getPts());
			// smp.setSample(i, 1, IAudioSamples.Format.FMT_S16, (int)tabR[i]);

		}

		
		
		smp.setComplete(true, tabL.length, AUDIORATE, 1, IAudioSamples.Format.FMT_S16,
				sample.getPts());
		// System.out.println(size + " - " + smp.getNumSamples() + " - " +
		// sample.getPts());

		writer.encodeAudio(1, smp);
	}
	
	public void closeVideo(){
		if(this.is720){
			IRational ratio = IRational.make(16,15);			 
			writer.getContainer().getStream(0).setSampleAspectRatio(ratio);
		}
		//writer.getContainer().getStream(0).getStreamCoder().close();
		//writer.getContainer().getStream(0).getContainer().writeTrailer();
		//writer.getContainer().getStream(0).getStreamCoder().close();
		//writer.getContainer().getStream(1).getStreamCoder().close();
		//writer.getContainer().writeTrailer();
		//writer.getContainer().close();
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
	
	/**
	 * Convert a source image to a desired BufferedImage type
	 * @param sourceImage the image source
	 * @param targetType the target type
	 * @return a converted BufferedImage
	 */
	private  BufferedImage convertToType(BufferedImage sourceImage,
			int targetType) {
		BufferedImage image;

		// if the source image is already the target type, return the source
		// image
		if (sourceImage.getType() == targetType) {
			image = sourceImage;
		}
		// otherwise create a new image of the target type and draw the new
		// image
		else {
			image = new BufferedImage(sourceImage.getWidth(),
					sourceImage.getHeight(), targetType);
			image.getGraphics().drawImage(sourceImage, 0, 0, null);
		}
		return image;
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