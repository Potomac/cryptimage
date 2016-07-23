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
 * 28 oct. 2014 Author Mannix54
 * http://ibsoftware.free.fr/cryptimage.php
 */



package com.ib.cryptimage.core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 * @author Mannix54
 *
 */
public class CryptPhoto {
		
	private int key11bits;
	private int offset;
	private int increment;	
	
	public CryptPhoto(){	
		
	}
	
	public boolean run(){
		BufferedImage img = null;		
		try{
			 img = ImageIO.read(new File(JobConfig.getInput_file()));
		}
		catch(IOException e){
			JOptionPane
			.showMessageDialog(
					JobConfig.getGui().getFrame(),
					JobConfig.getRes().getString("cryptPhoto.errorLoad"),
					JobConfig.getRes().getString("cryptPhoto.errorLoadIO"),
					JOptionPane.ERROR_MESSAGE);		
			return false;
		}		
		
		if(JobConfig.isModePhoto() && JobConfig.isWantDec()){
			if(JobConfig.getSystemCrypt() == 0 && !JobConfig.isStrictMode())
			{
			decPhotoSimpleDiscret(img);
			}
			else {
				if (JobConfig.getSystemCrypt() == 0 && JobConfig.isStrictMode()
						&& JobConfig.isWantDecCorrel()){
					decPhotoDiscretCorrelStrict(img);
				}
				else if (JobConfig.getSystemCrypt() == 0 && JobConfig.isStrictMode()){
					decPhotoDiscretStrict(img);
				}
			}
			
			if(JobConfig.getSystemCrypt() == 1 && JobConfig.isStrictMode()
					&& !JobConfig.isWantDecCorrel())
			{
			decPhotoSyster(img);
			} else if (JobConfig.getSystemCrypt() == 1 && JobConfig.isStrictMode()
					&& JobConfig.isWantDecCorrel())
			{
			decPhotoSysterCorrel(img);
			}			
			
		}
		else if(JobConfig.isModePhoto() && JobConfig.isWantDec()!=true
				&& JobConfig.getSystemCrypt() == 0
				&& !JobConfig.isStrictMode()){
			encPhotoSimpleDiscret(img);
		}  else if(JobConfig.isModePhoto() && JobConfig.isWantDec() != true
				&& JobConfig.getSystemCrypt() == 0
				&& JobConfig.isStrictMode()){
			encPhotoDiscretStrict(img);
		}
		else if(JobConfig.isModePhoto() && JobConfig.isWantDec() != true
				&& JobConfig.getSystemCrypt() == 1){
			encPhotoSyster(img);
		}
		return true;
	}
	
	public void encPhotoSyster(BufferedImage img){		
		SysterEnc systerEnc;
		int offset1, increment1, offset2, increment2;
		
		String output;
		if (JobConfig.isHorodatage()) {
			File file = new File(JobConfig.getOutput_file());
			String fileName = JobConfig.getDateTime() + "_"
					+ file.getName();
			output = file.getParent() + File.separator + fileName;
		} else {
			output = JobConfig.getOutput_file();
		}
				
		systerEnc = new SysterEnc(JobConfig.getTableSyster(),
				output,
					JobConfig.getFileDataEncSyster(),
					JobConfig.getGui().getChkPlayer().isSelected());
		
		BufferedImage imgRes = null;
		
		if (JobConfig.getGui().getRdiSysterCodingFile().isSelected()) {
			imgRes = systerEnc.transformPhoto(img,true);
			if (imgRes == null) {
				imgRes = systerEnc.transformPhoto(img,false);
			}
			imgRes = systerEnc.transformPhoto(img,false);
			
			
			
			systerEnc.closeFileData();
		}		
		else{
			genOffsetIncrement();
			offset1 = this.offset;
			increment1 = this.increment;
			genOffsetIncrement();
			offset2 = offset1; //this.offset;
//			if(offset2 > 255){
//				offset2 = offset2 - 256;
//			}			
			
			increment2 = increment1; //this.increment;
			imgRes = systerEnc.transformPhoto(img, offset1,increment1, offset2, increment2);
			if (imgRes == null) {				
				imgRes = systerEnc.transformPhoto(img, offset1,increment1, offset2, increment2);
			}		
			imgRes = systerEnc.transformPhoto(img, offset1,increment1, offset2, increment2);
			systerEnc.closeFileData();
		}
				
		saveCryptImage(imgRes);
		
	}
	
	public void decPhotoSysterCorrel(BufferedImage img){		
		Device device;
		device = new SysterDec(JobConfig.getTableSyster(),
				JobConfig.getFileDataDecSyster(),
				JobConfig.isWantDecCorrel());
		
		BufferedImage imgRes = device.transform(img);		
		while(imgRes == null){			
			imgRes = device.transform(img);
		}
		
		device.closeFileData();
		
		saveDecryptFile(imgRes, JobConfig.getOutput_file(), this.key11bits);	
	}
	
	public void decPhotoSyster(BufferedImage img){			
		Device device;
		device = new SysterDec(JobConfig.getTableSyster(),
				JobConfig.getFileDataDecSyster(),
				JobConfig.isWantDecCorrel());
		
		BufferedImage imgRes = device.transform(img);		
		while(imgRes == null){			
			imgRes = device.transform(img);
		}		
		
		device.closeFileData();
		
		saveDecryptFile(imgRes, JobConfig.getOutput_file(), this.key11bits);	
	}
	
	
	public void encPhotoDiscretStrict(BufferedImage img){		
		Device device;
		String codePattern;
		int cycle;		
		
		if (JobConfig.getAudienceLevel() == 8) {
			codePattern = JobConfig.getMultiCode();
			cycle = JobConfig.getCycle();
		} else {					
			codePattern = String.valueOf(JobConfig.getAudienceLevel());
			cycle = 1;
		}
						
		if(JobConfig.isNoBlackBar()){
			device = new Discret11EncNoBlack(JobConfig.getWord16bits(), 
					codePattern, cycle,
					JobConfig.getPerc1(), JobConfig.getPerc2());
		}
		else {
			device =new Discret11Enc(JobConfig.getWord16bits(), 
					codePattern, cycle,
					JobConfig.getPerc1(), JobConfig.getPerc2());
		}	
		this.key11bits = device.getKey11bits();
		BufferedImage imgRes = device.transform(img);
		for (int i = 0; i < 26; i++) {
			imgRes = device.transform(img);
		}		
		saveCryptImage(imgRes);		
	}
	
	public void decPhotoDiscretStrict(BufferedImage img){
		decPhotoDiscretCorrelStrict(img);	
	}
	
	public void decPhotoDiscretCorrelStrict(BufferedImage img){		
		Device device;
		device = new DiscretDecCorrel();
		BufferedImage imgRes = device.transform(img);
		this.key11bits = device.getKey11bits();
		saveDecryptFile(imgRes, JobConfig.getOutput_file(), this.key11bits);
	}
	
	
	public  void decPhotoSimpleDiscret(BufferedImage img){		
		Device device;
		if(JobConfig.isNoBlackBar()){
			device = new SimpleDiscret11Black(JobConfig.getWord16bits(),
					JobConfig.getAudienceLevel(),
					SimpleDiscret11.MODE_DEC, img.getHeight(), img.getWidth(),
					JobConfig.getPerc1(), JobConfig.getPerc2());
		}
		else {
			device = new SimpleDiscret11(JobConfig.getWord16bits(),
					JobConfig.getAudienceLevel(),
					SimpleDiscret11.MODE_DEC, img.getHeight(), img.getWidth(),
					JobConfig.getPerc1(), JobConfig.getPerc2());
		}		
		this.key11bits = device.getKey11bits();
		BufferedImage imgRes = device.transform(img);
		saveDecryptFile(imgRes, JobConfig.getOutput_file(), this.key11bits);		
	}
	
	public  void encPhotoSimpleDiscret(BufferedImage img){		
		Device device;
		if(JobConfig.isNoBlackBar()){
			device = new SimpleDiscret11NoBlack(JobConfig.getWord16bits(),
					JobConfig.getAudienceLevel(),
					SimpleDiscret11.MODE_ENC, img.getHeight(), img.getWidth(),
					JobConfig.getPerc1(), JobConfig.getPerc2());
		}
		else {
			device = new SimpleDiscret11(JobConfig.getWord16bits(),
					JobConfig.getAudienceLevel(),
					SimpleDiscret11.MODE_ENC, img.getHeight(), img.getWidth(),
					JobConfig.getPerc1(), JobConfig.getPerc2());
		}	
		this.key11bits = device.getKey11bits();
		BufferedImage imgRes = device.transform(img);
		saveCryptImage(imgRes);		
	}
	
	public  boolean saveCryptImage(BufferedImage bi) {
		try {
			if (JobConfig.getSystemCrypt() == 0) {
				// retrieve image
				File outputfile = new File(
						JobConfig.getOutput_file() + "_c" + 
				JobConfig.getWord16bits() + "-" + this.key11bits + ".png");
				ImageIO.write(bi, "png", outputfile);
				if (JobConfig.isHasGUI()) {

					JobConfig.getGui().getTextInfos().setText(JobConfig.getRes().getString("cryptPhoto.saveImage.codage") + JobConfig.getOutput_file() + "_c"
							+ JobConfig.getWord16bits() + "-" + this.key11bits + ".png");
				}
			}
		} catch (IOException e) {
			JOptionPane
			.showMessageDialog(
					JobConfig.getGui().getFrame(),
					JobConfig.getRes().getString("cryptPhoto.errorCryptIO"),
					JobConfig.getRes().getString("cryptPhoto.errorCryptIO.title"),
					JOptionPane.ERROR_MESSAGE);		
			return false;
		}
		
		if (JobConfig.getSystemCrypt() == 1) { //syster
			// retrieve image
			File outputfile = new File(
					JobConfig.getOutput_file() + "_c" + 
			"syster.png");
			try {
				ImageIO.write(bi, "png", outputfile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				JOptionPane
				.showMessageDialog(
						JobConfig.getGui().getFrame(),
						JobConfig.getRes().getString("cryptPhoto.errorCryptIO"),
						JobConfig.getRes().getString("cryptPhoto.errorCryptIO.title"),
						JOptionPane.ERROR_MESSAGE);	
				return false;
			}
			if (JobConfig.isHasGUI()) {
				JobConfig.getGui().getTextInfos().setText(JobConfig.getRes().getString("cryptPhoto.saveImage.codage") + JobConfig.getOutput_file() + "_c"
						+ "syster.png");
			}
		}
		
		
		if (JobConfig.getSystemCrypt() == 0) {
			try {
				File dataFile = new File(
						JobConfig.getOutput_file() + "_c" + JobConfig.getWord16bits() + "-" + this.key11bits + ".txt");
				dataFile.createNewFile();
				FileWriter ffw = new FileWriter(dataFile);
				ffw.write("key 16 bits : " + JobConfig.getWord16bits() + "\r\n");
				ffw.write("Audience level : " + JobConfig.getAudienceLevel() +
						"\r\n");
				ffw.write("key 11 bits : " + this.key11bits + "\r\n");
				ffw.write("file : " + JobConfig.getOutput_file() + "_c" +
				JobConfig.getWord16bits() + "-"
						+ this.key11bits + ".txt");
				ffw.close();

				if (JobConfig.isHasGUI()) {
					JobConfig.getGui().getTextInfos()
							.setText(JobConfig.getGui().getTextInfos().getText() + "\n\r" + JobConfig.getRes().getString("cryptPhoto.saveImage.rapport")
									+ JobConfig.getOutput_file() + "_c" + JobConfig.getWord16bits() + "-"
									+ this.key11bits + ".txt");
				}

			} catch (IOException e) {
				JOptionPane
				.showMessageDialog(
						JobConfig.getGui().getFrame(),
						JobConfig.getRes().getString("cryptPhoto.errorLog"),
						JobConfig.getRes().getString("cryptPhoto.errorCryptIO.title"),						
						JOptionPane.ERROR_MESSAGE);	
				return false;
			}
		}
		return true;
	}
	
	public  boolean saveDecryptFile(BufferedImage bi,String output_file, int key11){
		
		if (JobConfig.getSystemCrypt() == 0) {
			try {
				// retrieve image
				File outputfile = new File(output_file + "_d" + 
				JobConfig.getWord16bits() + "-" + key11 + ".png");
				ImageIO.write(bi, "png", outputfile);
				if (JobConfig.isHasGUI()) {
					JobConfig.getGui().getTextInfos().setText(
							JobConfig.getRes().getString("cryptPhoto.saveImage.decodage") + output_file + "_d" + 
					JobConfig.getWord16bits() + "-" + key11 + ".png");
				}				
			} catch (IOException e) {
				JOptionPane
				.showMessageDialog(
						JobConfig.getGui().getFrame(),
						JobConfig.getRes().getString("cryptPhoto.errorDecryptIO"),
						JobConfig.getRes().getString("cryptPhoto.errorDecryptIO.title"),
						
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		else {
			try {
				// retrieve image
				File outputfile = new File(output_file + "_d" + 
				"syster.png");
				ImageIO.write(bi, "png", outputfile);
				if (JobConfig.isHasGUI()) {
					JobConfig.getGui().getTextInfos().setText(
							JobConfig.getRes().getString("cryptPhoto.saveImage.decodage") + output_file + "_d" + 
					"syster.png");
				}				
			} catch (IOException e) {
				JOptionPane
				.showMessageDialog(
						JobConfig.getGui().getFrame(),
						JobConfig.getRes().getString("cryptPhoto.errorDecryptIO"),
						JobConfig.getRes().getString("cryptPhoto.errorDecryptIO.title"),
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		return true;
	}
	
	protected void genOffsetIncrement() {		
		Random rand = new Random();
		int min = 0;
		int max = 255;

		this.offset = rand.nextInt(max - min + 1) + min;

		min = 1;
		max = 127;
		double val;
		val = rand.nextInt(max - min + 1) + min;
		if ((val / 2) - (int) (val / 2) == 0) {
			this.increment = (int) (val + 1);
		} else {
			this.increment = (int) (val);
		}				
	}

}
