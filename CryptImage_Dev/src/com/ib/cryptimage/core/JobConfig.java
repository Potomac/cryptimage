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

public class JobConfig {

	private String input_file = "";
	private String report_file = "";
	private String output_file = "";		
	private int discret11Word = 0;	
	private int video_frame = 0;
	private boolean strictMode = false;
	private int positionSynchro = 1;
	private boolean wantDec = false;
	private boolean wantPlay = false;	
	private boolean modePhoto = false;
	private int audienceLevel = 7;
	private int videoBitrate = 2000;
	private int videoCodec = 3;
	private double perc1 = 0.0167;
	private double perc2 = 0.0334;	
	private int sWidth = 768;
	private boolean hasGUI = false;
	private MainGui gui;
	private boolean isStop = false;
	private String extension = "mkv";
	private boolean wantSound = true;
	private int word16bits = 0;
	private boolean horodatage = false;
	private String serial = "";
	private String code = "";
	private double frameRate = 25;
	private boolean disableSound = false;
	private boolean readyTransform = false;
	private boolean videoHasAudioTrack = false;
	private String multiCode = "";
	private int cycle;
	private int resolution;
	

	public JobConfig() {
		// TODO Auto-generated constructor stub
	}
	
	public String getDateTime(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
		Date date = new Date();
		return dateFormat.format(date); 
	}
	
	public boolean loadConfig() {
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
				this.setWord16bits(Integer.valueOf(options[0]));
				// audience
				this.setAudienceLevel(Integer.valueOf(options[1]) -1 );
				// delay 1
				this.setPerc1(Double.valueOf(options[2]));
				// delay2
				this.setPerc2(Double.valueOf(options[3]));
				// serial
				this.setSerial(options[4]);
				// horodatage
				this.setHorodatage(Boolean.parseBoolean(options[5]));
				// codec
				this.setVideoCodec(Integer.valueOf(options[6]) - 1);
				// bitrate
				this.setVideoBitrate(Integer.valueOf(options[7]));
				// extension
				this.setExtension(options[8]);
				//working directory
				this.setOutput_file(options[9]);
				//multimode
				this.setMultiCode(options[10]);
				//cycle
				this.setCycle(Integer.valueOf(options[11]));
				//resolution
				if(Integer.valueOf(options[12]) == 1){
					this.setResolution(1);
				}
				else {
					this.setResolution(2);
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
	
	public boolean saveConfig(int key16bits, int audienceIndex, double delay1,
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
	
	private String[] checkOptions(String[] options){
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
			e.printStackTrace();
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
			if (options[10].length() < 8 && options[10].length() > 0 ) {
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
			e.printStackTrace();
			options[10] = "6425";
		}
		
		// cycle
		try {			
			if (Integer.valueOf(options[11]) < 1
					|| Integer.valueOf(options[11]) > 99) {
				options[11] = "1";
			}
		} catch (Exception e) {
			e.printStackTrace();
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
	

	public String getInput_file() {
		return input_file;
	}

	public double getPerc1() {
		return perc1;
	}

	public void setPerc1(double perc1) {
		this.perc1 = perc1;
	}

	public double getPerc2() {
		return perc2;
	}

	public void setPerc2(double perc2) {
		this.perc2 = perc2;
	}

	public void setInput_file(String input_file) {
		this.input_file = input_file;
	}

	public String getReport_file() {
		return report_file;
	}

	public void setReport_file(String report_file) {
		this.report_file = report_file;
	}

	public String getOutput_file() {
		return output_file;
	}

	public void setOutput_file(String output_file) {
		this.output_file = output_file;
	}

	public int getDiscret11Word() {
		return discret11Word;
	}

	public void setDiscret11Word(int discret11Word) {
		this.discret11Word = discret11Word;
	}

	public int getVideo_frame() {
		return video_frame;
	}

	public void setVideo_frame(int video_frame) {
		this.video_frame = video_frame;
	}

	public boolean isStrictMode() {
		return strictMode;
	}

	public int getAudienceLevel() {
		return audienceLevel;
	}

	public void setAudienceLevel(int audienceLevel) {
		this.audienceLevel = audienceLevel;
	}

	public int getVideoBitrate() {
		return videoBitrate;
	}

	public void setVideoBitrate(int videoBitrate) {
		this.videoBitrate = videoBitrate;
	}

	public int getVideoCodec() {
		return videoCodec;
	}

	public void setVideoCodec(int videoCodec) {
		this.videoCodec = videoCodec;
	}

	public void setStrictMode(boolean strictMode) {
		this.strictMode = strictMode;
	}

	public int getPositionSynchro() {
		return positionSynchro;
	}

	public void setPositionSynchro(int positionSynchro) {
		this.positionSynchro = positionSynchro;
	}

	public boolean isWantDec() {
		return wantDec;
	}

	public void setWantDec(boolean wantDec) {
		this.wantDec = wantDec;
	}
	
	public boolean isWantPlay() {
		return wantPlay;
	}

	public void setWantPlay(boolean wantPlay) {
		this.wantPlay = wantPlay;
	}

	public boolean isModePhoto() {
		return modePhoto;
	}

	public void setModePhoto(boolean modePhoto) {
		this.modePhoto = modePhoto;
	}

	public int getsWidth() {
		return sWidth;
	}

	public void setsWidth(int sWidth) {
		this.sWidth = sWidth;
	}
	
	public boolean isHasGUI() {
		return hasGUI;
	}

	public void setHasGUI(boolean hasGUI) {
		this.hasGUI = hasGUI;
	}

	public MainGui getGui() {
		return gui;
	}

	public void setGui(MainGui gui) {
		this.gui = gui;
	}

	public boolean isStop() {
		return isStop;
	}

	public void setStop(boolean isStop) {
		this.isStop = isStop;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public boolean isWantSound() {
		return wantSound;
	}

	public void setWantSound(boolean wantSound) {
		this.wantSound = wantSound;
	}

	public int getWord16bits() {
		return word16bits;
	}

	public void setWord16bits(int word16bits) {
		this.word16bits = word16bits;
	}

	public boolean isHorodatage() {
		return horodatage;
	}

	public void setHorodatage(boolean horodatage) {
		this.horodatage = horodatage;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public double getFrameRate() {
		return frameRate;
	}

	public void setFrameRate(double frameRate) {
		this.frameRate = frameRate;
	}

	public boolean isDisableSound() {
		return disableSound;
	}

	public void setDisableSound(boolean disableSound) {
		this.disableSound = disableSound;
	}

	public boolean isReadyTransform() {
		return readyTransform;
	}

	public void setReadyTransform(boolean readyTransform) {
		this.readyTransform = readyTransform;
	}

	public boolean isVideoHasAudioTrack() {
		return videoHasAudioTrack;
	}

	public void setVideoHasAudioTrack(boolean videoHasAudioTrack) {
		this.videoHasAudioTrack = videoHasAudioTrack;
	}

	public String getMultiCode() {
		return multiCode;
	}

	public void setMultiCode(String multiCode) {
		this.multiCode = multiCode;
	}

	public int getCycle() {
		return cycle;
	}

	public void setCycle(int cycle) {
		this.cycle = cycle;
	}

	public int getResolution() {
		return resolution;
	}

	public void setResolution(int resolution) {
		this.resolution = resolution;
	}	
}
