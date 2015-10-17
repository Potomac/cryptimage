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

import com.ib.cryptimage.gui.MainGui;

public final class JobConfig {

	private static String input_file = "";
	private static String report_file = "";
	private static String output_file = "";		
	private static int discret11Word = 0;	
	private static int video_frame = 0;
	private static boolean strictMode = false;
	private static int positionSynchro = 1;
	private static boolean wantDec = false;
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
	private static boolean noBlackBar = false;


	private JobConfig() {
		// TODO Auto-generated constructor stub
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
			String[] options = new String[13];
			FileInputStream fis;
			try {
				fis = new FileInputStream(config);
				// Construct BufferedReader from InputStreamReader
				BufferedReader br = new BufferedReader(new InputStreamReader(
						fis));

				String line = null;
				int compt = 0;

				try {
					while ((line = br.readLine()) != null && compt < 13) {
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
			String multiMode, String cycle, boolean resolution720) {

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
			if(Integer.valueOf(options[6]) < 1 || Integer.valueOf(options[6]) > 5 ) {
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
		
		return options;
		
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
	
	public static boolean isNoBlackBar() {
		return noBlackBar;
	}

	public static void setNoBlackBar(boolean noBlackBar) {
		JobConfig.noBlackBar = noBlackBar;
	}
	
}
