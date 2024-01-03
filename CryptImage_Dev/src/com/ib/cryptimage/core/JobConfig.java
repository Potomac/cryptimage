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
 * http://ibsoftware.free.fr/cryptimage.php
 */


package com.ib.cryptimage.core;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import com.ib.cryptimage.gui.MainGui;

public final class JobConfig {

	private static BufferedImage inputImage;
	private static boolean isWantJoinInputOutputFrames = false;
	private static String input_file = "";
	private static String report_file = "";
	private static String output_file = "";		
	private static int discret11Word = 0;	
	private static int video_frame = 0;
	private static boolean strictMode = false;
	private static int positionSynchro = 1;
	private static boolean wantDec = false;
	private static boolean searchCode68705 = false;
	private static boolean wantPlay = false;	
	private static boolean modePhoto = false;
	private static int audienceLevel = 0;
	private static int videoBitrate = 2000;
	private static int videoCodec = 3;
	private static double perc1 = 0.0167;
	private static double perc2 = 0.0334;	
	private static int sWidth = 768;
	private static boolean hasGUI = false;
	private static MainGui gui;
	private static boolean isStop = false;
	private static String extension = "mkv";
	private static boolean wantSound = true;
	private static int word16bits = 0;
	private static boolean horodatage = false;
	private static String serial = "";
	private static String code = "";
	private static double frameRate = 25;
	private static boolean disableSound = false;
	private static boolean readyTransform = false;
	private static boolean videoHasAudioTrack = false;
	private static String multiCode = "";
	private static int cycle;
	private static int resolution;
	private static boolean[] autorisations;
	private static boolean isMaskedEdge = false;
	private static int delay1;
	private static int delay2;
	private static int audioCodec = 3;
	
	private static int audioRate = 0;
	private static double ampEnc = 1;
	private static double ampDec = 3; //3;
	private static int systemCrypt = 0;
		
	private static int tableSyster = 2;
	private static String fileDataEncSyster ="";
	private static String fileDataDecSyster = "";
	private static String fileDataEncVideocrypt ="";
	private static String fileDataDecVideocrypt = "";
	
	private static boolean wantDecCorrel = false;
	private static boolean wantSysterTags = false;
	private static boolean wantSysterEncRandom = false;
	
	private static String launch = "first";
	private static String VERSION = "1.6.6";
	private static String releaseDate = "2024-01-03";
	private static int lang = 0; // 0 --> auto, 1--> german, 2--> english, 3--> spanish, 4--> french, 5--> italian, 6-->polish 
	private static ResourceBundle res;
	private static int colorMode = 0; // 0--> RGB, 1--> Pal, 2--> Secam 
	private static boolean averagingPal = false;
	private static int typeYUV = 0; // 0 -> bt.601, 1 -> bt.709, 2 -> other
	
	private static String fileDataVideocrypt ="";	
	private static boolean wantDecCorrelVideoCrypt = false;
	private static boolean wantVideocryptEncRandom = false;
	private static int nbSkippedFrames = 0;
	private static boolean offsetIncrementChange = false;
	private static boolean logVideocrypt = false;
	private static boolean wantVideocryptTags = false;
	private static boolean nullDelay = false;
	private static boolean panAndScan = false;
	private static boolean stretch = false;

	private static int whiteValue = 80;
	private static boolean hasToBeUnsplit = false;
	
	private static PalEncoder palEncoder;
	private static PalDecoder palDecoder;
	
	private static int currentPalFrame = 0;
	private static int currentPalFrameDec = 0;
	
	private static boolean hasMultiAudioChannels = false;
		
	public static boolean isHasMultiAudioChannels() {
		return hasMultiAudioChannels;
	}

	public static void setHasMultiAudioChannels(boolean hasMultiAudioChannels) {
		JobConfig.hasMultiAudioChannels = hasMultiAudioChannels;
	}
	
	private static int defaultIdAudioTrack = 0;
	public static int getDefaultIdAudioTrack() {
		return defaultIdAudioTrack;
	}

	public static void setDefaultIdAudioTrack(int defaultIdAudioTrack) {
		JobConfig.defaultIdAudioTrack = defaultIdAudioTrack;
	}

	private static int defaultIdVideoTrack = 0;
	
	
	public static int getDefaultIdVideoTrack() {
		return defaultIdVideoTrack;
	}

	public static void setDefaultIdVideoTrack(int defaultIdVideoTrack) {
		JobConfig.defaultIdVideoTrack = defaultIdVideoTrack;
	}
	
	private static boolean hasProblematicCodec = false;

	public static boolean isHasProblematicCodec() {
		return hasProblematicCodec;
	}

	public static void setHasProblematicCodec(boolean hasProblematicCodec) {
		JobConfig.hasProblematicCodec = hasProblematicCodec;
	}
	
	private static Vector<StreamTrack> StreamTracksAudio;
	public static Vector<StreamTrack> getStreamTracksAudio() {
		return StreamTracksAudio;
	}

	public static void setStreamTracksAudio(Vector<StreamTrack> streamTracksAudio) {
		StreamTracksAudio = streamTracksAudio;
	}

	private static Vector<StreamTrack> StreamTracksVideo;
	
	public static Vector<StreamTrack> getStreamTracksVideo() {
		return StreamTracksVideo;
	}

	public static void setStreamTracksVideo(Vector<StreamTrack> streamTracksVideo) {
		StreamTracksVideo = streamTracksVideo;
	}

	private static StreamTrack audioTrackInfos;
	public static StreamTrack getAudioTrackInfos() {
		return audioTrackInfos;
	}

	public static void setAudioTrackInfos(StreamTrack audioTrackInfos) {
		JobConfig.audioTrackInfos = audioTrackInfos;
	}

	private static StreamTrack videoTrackInfos;

	public static StreamTrack getVideoTrackInfos() {
		return videoTrackInfos;
	}

	public static void setVideoTrackInfos(StreamTrack videoTrackInfos) {
		JobConfig.videoTrackInfos = videoTrackInfos;
	}

	public static void incrementPalFrame() {
		//currentPalFrame = 1;
		JobConfig.currentPalFrame++;
		if(JobConfig.currentPalFrame > 4) {
			JobConfig.currentPalFrame = 1;
		}

		//System.out.println(JobConfig.getCurrentPalFrame());
	}
	
	public static void incrementPalFrameDec() {
		//currentPalFrame = 1;
		JobConfig.currentPalFrameDec++;
		if(JobConfig.currentPalFrameDec > 4) {
			JobConfig.currentPalFrameDec = 1;
		}

		//System.out.println(JobConfig.getCurrentPalFrame());
	}
	
	
	public static String getDateTime(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
		Date date = new Date();
		return dateFormat.format(date); 
	}
	
	public static boolean loadConfig() {
		// check if the config file exists

		String userHome = System.getProperty("user.home");

		File config = new File(userHome + File.separator + "cryptimage"
				+ File.separator + "cryptimage.conf");

		if (config.exists()) {			
			String[] options = new String[18];
			FileInputStream fis;
			try {
				fis = new FileInputStream(config);
				// Construct BufferedReader from InputStreamReader
				BufferedReader br = new BufferedReader(new InputStreamReader(
						fis));

				String line = null;
				int compt = 0;

				try {
					while ((line = br.readLine()) != null && compt < 18) {
						options[compt] = line;
						compt++;						
					}
					br.close();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println(e.getMessage());
					return false;
				}

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
				return false;
			}

			//check options
			options = checkOptions(options);
			
			try {
				// 16 bits key
				JobConfig.setWord16bits(Integer.valueOf(options[0]));
				// audience
				JobConfig.setAudienceLevel(Integer.valueOf(options[1]) -1 );
				// delay 1
				JobConfig.setPerc1(Double.valueOf(options[2]));
				// delay2
				JobConfig.setPerc2(Double.valueOf(options[3]));
				// serial
				JobConfig.setSerial(options[4]);
				// horodatage
				JobConfig.setHorodatage(Boolean.parseBoolean(options[5]));
				// codec
				JobConfig.setVideoCodec(Integer.valueOf(options[6]) - 1);
				// bitrate
				JobConfig.setVideoBitrate(Integer.valueOf(options[7]));
				// extension
				JobConfig.setExtension(options[8]);
				//working directory
				JobConfig.setOutput_file(options[9]);
				//multimode
				JobConfig.setMultiCode(options[10]);
				//cycle
				JobConfig.setCycle(Integer.valueOf(options[11]));
				//resolution
				if(Integer.valueOf(options[12]) == 1){
					JobConfig.setResolution(1);
				}
				else {
					JobConfig.setResolution(2);
				}
				//audio codec
				JobConfig.setAudioCodec(Integer.valueOf(options[13]));
				//audio rate
				JobConfig.setAudioRate(Integer.valueOf(options[14]));
				//system crypt
				JobConfig.setSystemCrypt(Integer.valueOf(options[15]));
				//version
				JobConfig.setLaunch(options[16]);
				//language
				JobConfig.setLang(Integer.valueOf(options[17]));
				
				
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(e.getMessage());
				return false;
			}

			return true;
		}
		return false;
	}
	
	
	
	public static boolean saveConfig(int key16bits, int audienceIndex, double delay1,
			double delay2, String serial, boolean horodatage, int codecIndex,
			int bitrate, String extension, String workingDirectory,
			String multiMode, String cycle, boolean resolution720, int audioCodecValue,
			String audioRateValue, int systemCrypt, int language) {

		try {

			// test if cryptimage directory exists
			boolean success = new File(System.getProperty("user.home")
					+ File.separator + "cryptimage").exists();
			
			if(success == false){
			success = new File(System.getProperty("user.home")
					+ File.separator + "cryptimage").mkdir();
			}
			
			if (success) {
				File config = new File(System.getProperty("user.home")
						+ File.separator + "cryptimage" + File.separator
						+ "cryptimage.conf");

				config.createNewFile();

				FileWriter ffw = new FileWriter(config);

				String lineSeparator = System.getProperty("line.separator");
				// lineSeparator = "\r\n";

				BufferedWriter bfw = new BufferedWriter(ffw);
				bfw.write(key16bits + lineSeparator);
				bfw.write(audienceIndex + lineSeparator);
				bfw.write(delay1 + lineSeparator);
				bfw.write(delay2 + lineSeparator);
				bfw.write(serial + lineSeparator);
				bfw.write(String.valueOf(horodatage) + lineSeparator);
				bfw.write(codecIndex + lineSeparator);
				bfw.write(bitrate + lineSeparator);
				bfw.write(extension + lineSeparator);
				bfw.write(workingDirectory + lineSeparator);
				bfw.write(multiMode + lineSeparator);
				bfw.write(cycle + lineSeparator);
				if (resolution720) {
					bfw.write(1 + lineSeparator);
				} else {
					bfw.write(2 + lineSeparator);
				}
				bfw.write(audioCodecValue + lineSeparator);
				bfw.write(audioRateValue + lineSeparator);
				bfw.write(systemCrypt + lineSeparator);
				bfw.write(VERSION + lineSeparator);
				bfw.write(language + lineSeparator);
				bfw.close();

				return true;
			}
			return false;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}

	}
	
	private static String[] checkOptions(String[] options){
		//16 bits key
		try {
			if(Integer.valueOf(options[0]) < 32 
					|| Integer.valueOf(options[0]) > 65535 ) {
				options[0] = "58158";
			}
		} catch (Exception e) {			
			options[0] = "58158";
		}
		
		//audience index
		try {
			if(Integer.valueOf(options[1]) < 1 
					|| Integer.valueOf(options[1]) > 8 ) {
				options[1] = "1";
			}
		} catch (Exception e) {			
			options[1] = "1";
		}
		
		//delay1
		try {
			if(Double.valueOf(options[2]) < 0.005d 
					|| Double.valueOf(options[2]) > 0.03d ) {
				options[2] = "0.0167";
			}
		} catch (Exception e) {			
			options[2] = "0.0167";
		}
		
		//delay2
		try {
			if(Double.valueOf(options[3]) < 0.02d 
					|| Double.valueOf(options[3]) > 0.05d ) {
				options[3] = "0.0334";
			}
		} catch (Exception e) {			
			options[3] = "0.0334";
		}
		
		//serial
		try {
			if(Integer.valueOf(options[4]) < 0 
					|| Integer.valueOf(options[4]) > 99999999 ) {
				options[4] = "12345678";
			}
		} catch (Exception e) {			
			options[4] = "12345678";
		}
		
		//horodatage
		try {
			if(Boolean.valueOf(options[5]) !=true && 
					Boolean.valueOf(options[5]) != false ) {
				options[5] = "false";
			}
		} catch (Exception e) {			
			options[5] = "false";
		}
		
		//codec
		try {
			if(Integer.valueOf(options[6]) < 1 || Integer.valueOf(options[6]) > 6 ) {
				options[6] = "1";
			}
		} catch (Exception e) {			
			options[6] = "1";
		}
		
		//bitrate
		try {
			if(Integer.valueOf(options[7]) < 1 
					|| Integer.valueOf(options[7]) > 20000 ) {
				options[7] = "10000";
			}
		} catch (Exception e) {			
			options[7] = "10000";
		}
		
		//extension
		try {
			if(!options[8].equals("mp4") && !options[8].equals("avi") 
					&& !options[8].equals("mkv")
					&& !options[8].equals("ts")
					&& !options[8].equals("mpeg")) {
				options[8] = "mkv";
			}
		} catch (Exception e) {			
			options[8] = "mkv";
		}

		//working directory
		try {
			
			File file = new File(options[9]);
			if(!file.exists()){
				options[9]="";
			}			
		} catch (Exception e) {			
			options[9] = "";
		}		
		
		// multicode
		try {			
			if (options[10].length() < 11 && options[10].length() > 0 ) {
				boolean check = true;
				for (int i = 0; i < options[10].length(); i++) {
					if (Integer.valueOf(options[10].substring(i,i+1)) < 1
						|| Integer.valueOf(options[10].substring(i,i+1)) > 7){
						check = false;
						break;
					}
				}
				if (check == false) {
					options[10] = "6425";
				}
			} else {
				options[10] = "6425";
			}
		} catch (Exception e) {			
			options[10] = "6425";
		}
		
		// cycle
		try {			
			if (Integer.valueOf(options[11]) < 1
					|| Integer.valueOf(options[11]) > 99 ) {
				options[11] = "1";
			}
		} catch (Exception e) {			
			options[11] = "1";
		}
		
		//resolution
		try {
			if(Integer.valueOf(options[12]) != 1 && 
					Integer.valueOf(options[12]) != 2 ) {
				options[12] = "2";
			}
		} catch (Exception e) {
			options[12] = "2";
		}
		//audio codec
		try {
			if(Integer.valueOf(options[13]) < 0 || 
					Integer.valueOf(options[13]) > 7 ) {
				options[13] = "3";
			}
		} catch (Exception e) {
			options[13] = "3";
		}
		//audio rate
		try {
			if(Integer.valueOf(options[14]) != 44100 && 
					Integer.valueOf(options[14]) != 48000 ) {
				options[14] = "44100";
			}
		} catch (Exception e) {
			options[14] = "44100";
		}
		// system crypt
		try {
			if (Integer.valueOf(options[15]) != 0 && Integer.valueOf(options[15]) != 1
					 && Integer.valueOf(options[15]) != 2 && Integer.valueOf(options[15]) != 3 ) {
				options[15] = "0";
			}
		} catch (Exception e) {
			options[15] = "0";
		}
		//version
		try {
			if (options[16].equals(null)) {
				options[16] = "first";
			}
		} catch (Exception e) {
			options[16] = "first";
		}
		// language
		try {
			if (Integer.valueOf(options[17]) < 0 || Integer.valueOf(options[17]) > 6) {
				options[17] = "0";
				}
			} catch (Exception e) {
			options[17] = "0";
				}
		return options;		
	}
	
	public static String getUserLanguage(){
		String userlanguage ="";
		
		if(JobConfig.getLang() == 0){
			userlanguage = Locale.getDefault().getLanguage();
		} else if (JobConfig.getLang() == 1){
			userlanguage ="de";
		} else if (JobConfig.getLang() == 2){
			userlanguage = "en";
		} else if (JobConfig.getLang() == 3){
			userlanguage = "es";
		} else if (JobConfig.getLang() == 4){
			userlanguage = "fr";
		} else if (JobConfig.getLang() == 5){
			userlanguage = "it";
		} else if (JobConfig.getLang() == 6){
			userlanguage = "pl";
		}
		
		return userlanguage;
	}
	
	public static void set720InputImage() {
		if(isWantJoinInputOutputFrames) {
			JobConfig.inputImage = Utils.getScaledImage(inputImage, 720, 576);			
		}
	}
	

	public static String getInput_file() {
		return input_file;
	}

	public static double getPerc1() {
		return perc1;
	}

	public static void setPerc1(double perc1) {
		JobConfig.perc1 = perc1;
	}

	public static double getPerc2() {
		return perc2;
	}

	public static void setPerc2(double perc2) {
		JobConfig.perc2 = perc2;
	}

	public static void setInput_file(String input_file) {
		JobConfig.input_file = input_file;
	}

	public static String getReport_file() {
		return report_file;
	}

	public static void setReport_file(String report_file) {
		JobConfig.report_file = report_file;
	}

	public static String getOutput_file() {
		return output_file;
	}

	public static void setOutput_file(String output_file) {
		JobConfig.output_file = output_file;
	}

	public static int getDiscret11Word() {
		return discret11Word;
	}

	public static void setDiscret11Word(int discret11Word) {
		JobConfig.discret11Word = discret11Word;
	}

	public static int getVideo_frame() {
		return video_frame;
	}

	public static void setVideo_frame(int video_frame) {
		JobConfig.video_frame = video_frame;
	}

	public static boolean isStrictMode() {
		return strictMode;
	}

	public static int getAudienceLevel() {
		return audienceLevel;
	}

	public static void setAudienceLevel(int audienceLevel) {
		JobConfig.audienceLevel = audienceLevel;
	}

	public static int getVideoBitrate() {
		return videoBitrate;
	}

	public static void setVideoBitrate(int videoBitrate) {
		JobConfig.videoBitrate = videoBitrate;
	}

	public static int getVideoCodec() {
		return videoCodec;
	}

	public static void setVideoCodec(int videoCodec) {
		JobConfig.videoCodec = videoCodec;
	}

	public static void setStrictMode(boolean strictMode) {
		JobConfig.strictMode = strictMode;
	}

	public static int getPositionSynchro() {
		return positionSynchro;
	}

	public static void setPositionSynchro(int positionSynchro) {
		JobConfig.positionSynchro = positionSynchro;
	}

	public static boolean isWantDec() {
		return wantDec;
	}

	public static void setWantDec(boolean wantDec) {
		JobConfig.wantDec = wantDec;
	}
	
	public static boolean isWantPlay() {
		return wantPlay;
	}

	public static void setWantPlay(boolean wantPlay) {
		JobConfig.wantPlay = wantPlay;
	}

	public static boolean isModePhoto() {
		return modePhoto;
	}

	public static void setModePhoto(boolean modePhoto) {
		JobConfig.modePhoto = modePhoto;
	}

	public static int getsWidth() {
		return sWidth;
	}

	public static void setsWidth(int sWidth) {
		JobConfig.sWidth = sWidth;
	}
	
	public static boolean isHasGUI() {
		return hasGUI;
	}

	public static void setHasGUI(boolean hasGUI) {
		JobConfig.hasGUI = hasGUI;
	}

	public static MainGui getGui() {
		return gui;
	}

	public static void setGui(MainGui gui) {
		JobConfig.gui = gui;
	}

	public static boolean isStop() {
		return isStop;
	}

	public static void setStop(boolean isStop) {
		JobConfig.isStop = isStop;
	}

	public static String getExtension() {
		return extension;
	}

	public static void setExtension(String extension) {
		JobConfig.extension = extension;
	}

	public static boolean isWantSound() {
		return wantSound;
	}

	public static void setWantSound(boolean wantSound) {
		JobConfig.wantSound = wantSound;
	}

	public static int getWord16bits() {
		return word16bits;
	}

	public static void setWord16bits(int word16bits) {
		JobConfig.word16bits = word16bits;
	}

	public static boolean isHorodatage() {
		return horodatage;
	}

	public static void setHorodatage(boolean horodatage) {
		JobConfig.horodatage = horodatage;
	}

	public static String getSerial() {
		return serial;
	}

	public static void setSerial(String serial) {
		JobConfig.serial = serial;
	}

	public static String getCode() {
		return code;
	}

	public static void setCode(String code) {
		JobConfig.code = code;
	}

	public static double getFrameRate() {
		return frameRate;
	}

	public static void setFrameRate(double frameRate) {
		JobConfig.frameRate = frameRate;
	}

	public static boolean isDisableSound() {
		return disableSound;
	}

	public static void setDisableSound(boolean disableSound) {
		JobConfig.disableSound = disableSound;
	}

	public static boolean isReadyTransform() {
		return readyTransform;
	}

	public static void setReadyTransform(boolean readyTransform) {
		JobConfig.readyTransform = readyTransform;
	}

	public static boolean isVideoHasAudioTrack() {
		return videoHasAudioTrack;
	}

	public static void setVideoHasAudioTrack(boolean videoHasAudioTrack) {
		JobConfig.videoHasAudioTrack = videoHasAudioTrack;
	}

	public static String getMultiCode() {
		return multiCode;
	}

	public static void setMultiCode(String multiCode) {
		JobConfig.multiCode = multiCode;
	}

	public static int getCycle() {
		return cycle;
	}

	public static void setCycle(int cycle) {
		JobConfig.cycle = cycle;
	}

	public static int getResolution() {
		return resolution;
	}

	public static void setResolution(int resolution) {
		JobConfig.resolution = resolution;
	}

	public static boolean[] getAutorisations() {
		return autorisations;
	}

	public static void setAutorisations(boolean[] autorisations) {
		JobConfig.autorisations = autorisations;
	}	
	
	public static boolean isMaskedEdge() {
		return isMaskedEdge;
	}

	public static void setMaskedEdge(boolean maskedEdge) {
		JobConfig.isMaskedEdge = maskedEdge;
	}

	public static int getDelay1() {
		return delay1;
	}

	public static void setDelay1(int delay1) {
		JobConfig.delay1 = delay1;
	}

	public static int getDelay2() {
		return delay2;
	}

	public static void setDelay2(int delay2) {
		JobConfig.delay2 = delay2;
	}

	public static int getAudioCodec() {
		return audioCodec;
	}

	public static void setAudioCodec(int audioCodec) {
		JobConfig.audioCodec = audioCodec;
	}

	public static int getAudioRate() {
		return audioRate;
	}

	public static void setAudioRate(int audioRate) {
		JobConfig.audioRate = audioRate;
	}

	public static double getAmpEnc() {
		return ampEnc;
	}

	public static void setAmpEnc(int ampEnc) {
		JobConfig.ampEnc = ampEnc;
	}

	public static double getAmpDec() {
		return ampDec;
	}

	public static void setAmpDec(int ampDec) {
		JobConfig.ampDec = ampDec;
	}

	public static int getSystemCrypt() {
		return systemCrypt;
	}

	public static void setSystemCrypt(int systemCrypt) {
		JobConfig.systemCrypt = systemCrypt;
	}

	public static int getTableSyster() {
		return tableSyster;
	}

	public static void setTableSyster(int tableSyster) {
		JobConfig.tableSyster = tableSyster;
	}

	public static String getFileDataEncSyster() {
		return fileDataEncSyster;
	}

	public static void setFileDataEncSyster(String fileDataEncSyster) {
		JobConfig.fileDataEncSyster = fileDataEncSyster;
	}

	public static String getFileDataDecSyster() {
		return fileDataDecSyster;
	}

	public static void setFileDataDecSyster(String fileDataDecSyster) {
		JobConfig.fileDataDecSyster = fileDataDecSyster;
	}

	public static boolean isWantDecCorrel() {
		return wantDecCorrel;
	}

	public static void setWantDecCorrel(boolean wantDecCorrel) {
		JobConfig.wantDecCorrel = wantDecCorrel;
	}

	public static boolean isWantSysterTags() {
		return wantSysterTags;
	}

	public static void setWantSysterTags(boolean wantSysterTags) {
		JobConfig.wantSysterTags = wantSysterTags;
	}

	public static boolean isWantSysterEncRandom() {
		return wantSysterEncRandom;
	}

	public static void setWantSysterEncRandom(boolean wantSysterEncRandom) {
		JobConfig.wantSysterEncRandom = wantSysterEncRandom;
	}

	public static String getLaunch() {
		return launch;
	}

	public static void setLaunch(String launch) {
		JobConfig.launch = launch;
	}

	public static String getVERSION() {
		return VERSION;
	}

	public static int getLang() {
		return lang;
	}

	public static void setLang(int lang) {
		JobConfig.lang = lang;
	}

	public static ResourceBundle getRes() {
		return res;
	}

	public static void setRes(ResourceBundle res) {
		JobConfig.res = res;
	}

	public static String getReleaseDate() {
		return releaseDate;
	}

	public static int getColorMode() {
		return colorMode;
	}

	public static void setColorMode(int colorMode) {
		JobConfig.colorMode = colorMode;
	}

	public static boolean isAveragingPal() {
		return averagingPal;
	}

	public static void setAveragingPal(boolean averagingPal) {
		JobConfig.averagingPal = averagingPal;
	}

	public static int getTypeYUV() {
		return typeYUV;
	}

	public static void setTypeYUV(int typeYUV) {
		JobConfig.typeYUV = typeYUV;
	}

	public static String getFileDataVideocrypt() {
		return fileDataVideocrypt;
	}

	public static void setFileDataVideocrypt(String fileDataVideocrypt) {
		JobConfig.fileDataVideocrypt = fileDataVideocrypt;
	}

	public static boolean isWantDecCorrelVideoCrypt() {
		return wantDecCorrelVideoCrypt;
	}

	public static void setWantDecCorrelVideoCrypt(boolean wantDecCorrelVideoCrypt) {
		JobConfig.wantDecCorrelVideoCrypt = wantDecCorrelVideoCrypt;
	}

	public static boolean isWantVideocryptEncRandom() {
		return wantVideocryptEncRandom;
	}

	public static void setWantVideocryptEncRandom(boolean wantVideocryptEncRandom) {
		JobConfig.wantVideocryptEncRandom = wantVideocryptEncRandom;
	}

	public static String getFileDataEncVideocrypt() {
		return fileDataEncVideocrypt;
	}

	public static void setFileDataEncVideocrypt(String fileDataEncVideocrypt) {
		JobConfig.fileDataEncVideocrypt = fileDataEncVideocrypt;
	}

	public static String getFileDataDecVideocrypt() {
		return fileDataDecVideocrypt;
	}

	public static void setFileDataDecVideocrypt(String fileDataDecVideocrypt) {
		JobConfig.fileDataDecVideocrypt = fileDataDecVideocrypt;
	}

	public static int getNbSkippedFrames() {
		return nbSkippedFrames;
	}

	public static void setNbSkippedFrames(int nbSkippedFrames) {
		JobConfig.nbSkippedFrames = nbSkippedFrames;
	}

	public static boolean isOffsetIncrementChange() {
		return offsetIncrementChange;
	}

	public static void setOffsetIncrementChange(boolean offsetIncrementChange) {
		JobConfig.offsetIncrementChange = offsetIncrementChange;
	}

	public static boolean isLogVideocrypt() {
		return logVideocrypt;
	}

	public static void setLogVideocrypt(boolean logVideocrypt) {
		JobConfig.logVideocrypt = logVideocrypt;
	}

	public static boolean isWantVideocryptTags() {
		return wantVideocryptTags;
	}

	public static void setWantVideocryptTags(boolean wantVideocryptTags) {
		JobConfig.wantVideocryptTags = wantVideocryptTags;
	}

	public static boolean isNullDelay() {
		return nullDelay;
	}

	public static void setNullDelay(boolean nullDelay) {
		JobConfig.nullDelay = nullDelay;
	}

	public static boolean isPanAndScan() {
		return panAndScan;
	}

	public static void setPanAndScan(boolean panAndScan) {
		JobConfig.panAndScan = panAndScan;
	}

	public static int getWhiteValue() {
		return whiteValue;
	}

	public static void setWhiteValue(int whiteValue) {
		JobConfig.whiteValue = whiteValue;
	}

	public static boolean isHasToBeUnsplit() {
		return JobConfig.hasToBeUnsplit;
	}

	public static void setHasToBeUnsplit(boolean hasToBeUnsplit) {
		JobConfig.hasToBeUnsplit = hasToBeUnsplit;
	}

	public static PalEncoder getPalEncoder() {
		return palEncoder;
	}

	public static void setPalEncoder(PalEncoder palEncoder) {
		JobConfig.palEncoder = palEncoder;
	}

	public static PalDecoder getPalDecoder() {
		return palDecoder;
	}

	public static void setPalDecoder(PalDecoder palDecoder) {
		JobConfig.palDecoder = palDecoder;
	}

	public static int getCurrentPalFrame() {
		return currentPalFrame;
	}

	public static void setCurrentPalFrame(int currentPalFrame) {
		JobConfig.currentPalFrame = currentPalFrame;
	}

	public static int getCurrentPalFrameDec() {
		return currentPalFrameDec;
	}

	public static void setCurrentPalFrameDec(int currentPalFrameDec) {
		JobConfig.currentPalFrameDec = currentPalFrameDec;
	}

	public static boolean isSearchCode68705() {
		return searchCode68705;
	}

	public static void setSearchCode68705(boolean searchCode68705) {
		JobConfig.searchCode68705 = searchCode68705;
	}

	public static boolean isStretch() {
		return stretch;
	}

	public static void setStretch(boolean stretch) {
		JobConfig.stretch = stretch;
	}

	public static BufferedImage getInputImage() {
		return inputImage;
	}

	public static void setInputImage(BufferedImage inputImage) {
		if (isWantJoinInputOutputFrames) {
			JobConfig.inputImage = Utils.deepCopy(inputImage);
		}		
	}

	public static boolean isWantJoinInputOutputFrames() {
		return isWantJoinInputOutputFrames;
	}

	public static void setWantJoinInputOutputFrames(boolean isWantJoinInputOutputFrames) {
		JobConfig.isWantJoinInputOutputFrames = isWantJoinInputOutputFrames;
	}	
}
