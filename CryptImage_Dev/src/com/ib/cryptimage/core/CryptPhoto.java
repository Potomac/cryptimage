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

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import com.ib.cryptimage.core.systems.discret12.Discret12Conf;
import com.ib.cryptimage.core.systems.discret12.Discret12DecCore;
import com.ib.cryptimage.core.systems.discret12.Discret12EncCore;
import com.ib.cryptimage.core.systems.eurocrypt.EurocryptConf;
import com.ib.cryptimage.core.systems.eurocrypt.EurocryptCore;
import com.ib.cryptimage.core.systems.luxcrypt.LuxcryptConf;
import com.ib.cryptimage.core.systems.luxcrypt.LuxcryptCore;
import com.ib.cryptimage.core.types.ColorType;
import com.ib.cryptimage.core.types.SystemType;

/**
 * @author Mannix54
 *
 */
public class CryptPhoto {
		
	private int key11bits;
	private int offset;
	private int increment;
	private String colorMode;
	private String eurocryptMode = "";
	private boolean is944 = false;
	private SplitFrames splitFrames;
	private boolean isGrey = false;
	
	public CryptPhoto(){		
		if (JobConfig.getSystemCrypt() == SystemType.SYSTER
				|| JobConfig.getSystemCrypt() == SystemType.VIDEOCRYPT || JobConfig.getSystemCrypt() == SystemType.TRANSCODE
				|| JobConfig.getSystemCrypt() == SystemType.EUROCRYPT) {
			if (JobConfig.getGui().getCbColorMode().getSelectedIndex() == ColorType.RGB) {
				this.colorMode = "rgb";
			}
			if (JobConfig.getGui().getCbColorMode().getSelectedIndex() == ColorType.PAL) {
				this.colorMode = "pal";
			}
			if (JobConfig.getGui().getCbColorMode().getSelectedIndex() == ColorType.SECAM) {
				this.colorMode = "secam";
			}
			if (JobConfig.getGui().getCbColorMode().getSelectedIndex() == ColorType.PAL_COMPOSITE_ENC_DEC) {
				this.colorMode = "pal_composite_encode_and_decode";				
			}
			if (JobConfig.getGui().getCbColorMode().getSelectedIndex() == ColorType.PAL_COMPOSITE_ENC) {
				this.colorMode = "pal_composite_encode_only";
				isGrey = true;
			}
			if (JobConfig.getGui().getCbColorMode().getSelectedIndex() == ColorType.PAL_COMPOSITE_DEC) {
				this.colorMode = "pal_composite_decode_only";
			}
		}
	
		splitFrames = new SplitFrames((int)JobConfig.getGui().getCmbPalFreq().getSelectedItem());
		
		if(JobConfig.getGui().getRdi944().isSelected() 
				&& JobConfig.getSystemCrypt() != SystemType.EUROCRYPT
				&& JobConfig.getSystemCrypt() != SystemType.LUXCRYPT) {
			is944 = true;
		}
				
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
		
        if(img.getWidth() == 944 && img.getHeight() == 626) {
            int dialogResult = JOptionPane.showConfirmDialog (null, JobConfig.getRes().getString("manageFileOpen.is944"),
            		"944x626 resolution",JOptionPane.YES_NO_OPTION);
            if(dialogResult == JOptionPane.YES_OPTION){
              JobConfig.setHasToBeUnsplit(true);
              img = splitFrames.unsplitFrames(img);
            }
            else {
            	JobConfig.setHasToBeUnsplit(false);
            }
        }
		
		
		//pan and scan
		if (JobConfig.isPanAndScan() && !JobConfig.isWantDec()){
			img = doPanAndScan(img);
		}
		
		// Stretch
		if (JobConfig.isStretch() && !JobConfig.isWantDec()){
			img = doStretch(img);
		}
		
		BufferedImage imgNew = new BufferedImage(img.getWidth(),
				img.getHeight(), BufferedImage.TYPE_INT_BGR);
		imgNew.getGraphics().drawImage(img, 0, 0, null);
		img = imgNew;
		
		if(JobConfig.isModePhoto() && JobConfig.isWantDec()){
			if(JobConfig.getSystemCrypt() == SystemType.DISCRET11 && !JobConfig.isStrictMode())
			{
			decPhotoSimpleDiscret(img);
			}
			else {
				if (JobConfig.getSystemCrypt() == SystemType.DISCRET11 && JobConfig.isStrictMode()
						&& JobConfig.isWantDecCorrel()){
					decPhotoDiscretCorrelStrict(img);
				}
				else if (JobConfig.getSystemCrypt() == SystemType.DISCRET11 && JobConfig.isStrictMode()){
					decPhotoDiscretStrict(img);
				}
			}
			
			if(JobConfig.getSystemCrypt() == SystemType.SYSTER && JobConfig.isStrictMode()
					&& !JobConfig.isWantDecCorrel())
			{
			decPhotoSyster(img);
			} else if (JobConfig.getSystemCrypt() == SystemType.SYSTER && JobConfig.isStrictMode()
					&& JobConfig.isWantDecCorrel())
			{
			decPhotoSysterCorrel(img);
			}		
			
			//videocrypt
			if(JobConfig.getSystemCrypt() == SystemType.VIDEOCRYPT && JobConfig.isStrictMode()
					&& !JobConfig.isWantDecCorrel())
			{
			decPhotoVideocrypt(img);
			} else if (JobConfig.getSystemCrypt() == SystemType.VIDEOCRYPT && JobConfig.isStrictMode()
					&& JobConfig.isWantDecCorrel())
			{
			decPhotoVideocryptCorrel(img);
			}		
			
			
		}
		
		else if(JobConfig.isModePhoto() && JobConfig.isWantDec()!=true
				&& JobConfig.getSystemCrypt() == SystemType.DISCRET11
				&& !JobConfig.isStrictMode()){
			encPhotoSimpleDiscret(img);
		}  else if(JobConfig.isModePhoto() && JobConfig.isWantDec() != true
				&& JobConfig.getSystemCrypt() == SystemType.DISCRET11
				&& JobConfig.isStrictMode()){
			encPhotoDiscretStrict(img);
		}
		else if(JobConfig.isModePhoto() && JobConfig.isWantDec() != true
				&& JobConfig.getSystemCrypt() == SystemType.SYSTER){
			encPhotoSyster(img);
		}else if(JobConfig.isModePhoto() && !JobConfig.isWantDec()
				&& JobConfig.getSystemCrypt() == SystemType.VIDEOCRYPT){
			encPhotoVideocrypt(img);
		}

		
		if(JobConfig.getSystemCrypt() == SystemType.TRANSCODE) {
			transcode(img);
		}
		
		if(JobConfig.getSystemCrypt() == SystemType.EUROCRYPT) {
			EurocryptConf.getGui().getRangeSlider().setMinimum(1);
			EurocryptConf.getGui().refreshSlider();
			
			if(EurocryptConf.isEurocryptSingleCut) {
				this.eurocryptMode="single-cut_key-" + EurocryptConf.seedCode;
				
				if(EurocryptConf.isEncodeMacDecode576pNoEurocrypt){
					this.eurocryptMode="dmac_no-eurocrypt-management_" + this.eurocryptMode;
				}
			}
			else if(EurocryptConf.isEurocryptDoubleCut) {
				this.eurocryptMode="double-cut_key-" + EurocryptConf.seedCode;
				if(EurocryptConf.isEncodeMacDecode576pNoEurocrypt){
					this.eurocryptMode="dmac_no-eurocrypt-management_" + this.eurocryptMode;
				}
			}
			else if(EurocryptConf.isDisableEurocrypt && EurocryptConf.isEncodeMacDecode576pNoEurocrypt) {
				this.eurocryptMode="dmac_no-eurocrypt-management_eurocrypt-disabled";
			}
			else if(EurocryptConf.isDisableEurocrypt && EurocryptConf.isEncodeMac) {
				this.eurocryptMode="eurocrypt-disabled_mac-only";
			}
			
			eurocrypt(img);
		}
		
		if(JobConfig.getSystemCrypt() == SystemType.LUXCRYPT) {
			LuxcryptConf.getGui().getRangeSlider().setMinimum(1);
			LuxcryptConf.getGui().refreshSlider();
			
			luxcrypt(img);
		}
		
		if(JobConfig.getSystemCrypt() == SystemType.DISCRET12) {
			Discret12Conf.getGui().getRangeSlider().setMinimum(1);
			Discret12Conf.getGui().refreshSlider();
			
			discret12(img);
		}
		
		return true;
	}
	
	private void eurocrypt(BufferedImage img) {
		Device device;
		device = new EurocryptCore();
		BufferedImage imgRes = device.transform(img);
		
		if(EurocryptConf.isDecodeMac) {
			saveDecryptFile(imgRes, JobConfig.getOutput_file(), this.key11bits);
		}
		else {
			saveCryptImage(imgRes);
		}	
	}
	
	private void luxcrypt(BufferedImage img) {
		Device device;
		device = new LuxcryptCore();
		BufferedImage imgRes = device.transform(img);
		imgRes = device.transform(img);
		
		saveCryptImage(imgRes);	
	}
	
	private void discret12(BufferedImage img) {
		Device device;
		
		if(Discret12Conf.isDecode) {
			device = new Discret12DecCore();
		}
		else {
			device = new Discret12EncCore();
		}
		
		BufferedImage imgRes = device.transform(img);
		for (int i = 0; i < 26; i++) {
		   imgRes = device.transform(img);
		}		
		
		if(Discret12Conf.isEncode) {
			saveCryptImage(imgRes);	
		}
		else {
			saveDecryptDiscret12(imgRes);
		}
		
	}
	
	private void transcode(BufferedImage img) {
		Device device;
		device = new Transcode();
		BufferedImage imgRes = device.transform(img);
		
		if(is944) {
			imgRes = splitFrames.splitFrames(isGrey, imgRes);
		}
		
		saveDecryptFile(imgRes, JobConfig.getOutput_file(), this.key11bits);
		
	}
	
	
	private void decPhotoVideocryptCorrel(BufferedImage img) {
		Device device;
		device = new VideocryptDec(
				JobConfig.getFileDataDecVideocrypt(),
				JobConfig.isWantDecCorrel());
		
		BufferedImage imgRes = device.transform(img);		
		
		device.closeFileData();
		
		if(is944) {
			imgRes = splitFrames.splitFrames(isGrey, imgRes);
		}
		
		saveDecryptFile(imgRes, JobConfig.getOutput_file(), this.key11bits);
		
	}

	private void decPhotoVideocrypt(BufferedImage img) {
		Device device;
		device = new VideocryptDec(
				JobConfig.getFileDataDecVideocrypt(),
				JobConfig.isWantDecCorrel());
		
		BufferedImage imgRes = device.transform(img);		
		
		device.closeFileData();
		
		if(is944) {
			imgRes = splitFrames.splitFrames(isGrey, imgRes);
		}
		
		saveDecryptFile(imgRes, JobConfig.getOutput_file(), this.key11bits);
		
	}

	public void encPhotoVideocrypt(BufferedImage img){
		VideocryptEnc videoCryptEnc;
		
		String output;
		if (JobConfig.isHorodatage()) {
			File file = new File(JobConfig.getOutput_file());
			String fileName = JobConfig.getDateTime() + "_"
					+ file.getName();
			output = file.getParent() + File.separator + fileName +"_cvideocrypt_" + colorMode;
		} else {
			output = JobConfig.getOutput_file() +"_cvideocrypt_" + colorMode;
		}
		
		videoCryptEnc = new VideocryptEnc(output,
					JobConfig.getFileDataEncVideocrypt(),
					JobConfig.getGui().getChkPlayer().isSelected());
		
		BufferedImage imgRes = null;
		
		imgRes = videoCryptEnc.transform(img);
		
		videoCryptEnc.closeFileData();
		
		if(is944) {
			imgRes = splitFrames.splitFrames(isGrey, imgRes);
		}
		
		saveCryptImage(imgRes);
		
		
	}
	
	public void encPhotoSyster(BufferedImage img){		
		SysterEnc systerEnc;
		int offset1, increment1, offset2, increment2;
		
		String output;
		if (JobConfig.isHorodatage()) {
			File file = new File(JobConfig.getOutput_file());
			String fileName = JobConfig.getDateTime() + "_"
					+ file.getName();
			output = file.getParent() + File.separator + fileName + "_csyster_" +colorMode;
		} else {
			output = JobConfig.getOutput_file()+ "_csyster_" +colorMode;
		}
				
		systerEnc = new SysterEnc(JobConfig.getTableSyster(),
				output,
					JobConfig.getFileDataEncSyster(),
					JobConfig.getGui().getChkPlayer().isSelected());
		
		BufferedImage imgRes = null;
		
		if (JobConfig.getGui().getRdiSysterCodingFile().isSelected()) {
			imgRes = systerEnc.transformPhoto(img,true);
			if (imgRes == null) {
				imgRes = systerEnc.transformPhoto(img,true); //false
			}
			//imgRes = systerEnc.transformPhoto(img,false); //false
			imgRes = systerEnc.transformPhoto(img, systerEnc.getOffSetOdd(), systerEnc.getIncrementOdd(),systerEnc.getOffSetEven(), systerEnc.getIncrementEven());
			
			
			systerEnc.closeFileData();
		}		
		else{
			genOffsetIncrement();
			offset1 = this.offset;
			increment1 = this.increment;
			
			if (JobConfig.getGui().getChkChangeOffsetIncrement().isSelected()) {
				genOffsetIncrement();
				offset2 = this.offset;
				increment2 = this.increment;
			} else {
				offset2 = offset1;
				increment2 = increment1;
			}
			
			imgRes = systerEnc.transformPhoto(img, offset1,increment1, offset2, increment2);
			if (imgRes == null) {				
				imgRes = systerEnc.transformPhoto(img, offset1,increment1, offset2, increment2);
			}		
			imgRes = systerEnc.transformPhoto(img, offset1,increment1, offset2, increment2);
			systerEnc.closeFileData();
		}
		
		if(is944) {
			imgRes = splitFrames.splitFrames(isGrey, imgRes);
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
		
		if(is944) {
			imgRes = splitFrames.splitFrames(isGrey, imgRes);
		}
		
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
		
		if(is944) {
			imgRes = splitFrames.splitFrames(isGrey, imgRes);
		}
		
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
						
		if(JobConfig.isMaskedEdge()){
			device = new Discret11EncMaskedBorder(JobConfig.getWord16bits(), 
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
		
		if(is944) {
			imgRes = splitFrames.splitFrames(isGrey, imgRes);
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
		if(is944) {
			imgRes = splitFrames.splitFrames(isGrey, imgRes);
		}
		
		saveDecryptFile(imgRes, JobConfig.getOutput_file(), this.key11bits);
		
	}
	
	
	public  void decPhotoSimpleDiscret(BufferedImage img){		
		Device device;
	
		device = new SimpleDiscret11(JobConfig.getWord16bits(),
				JobConfig.getAudienceLevel(),
				SimpleDiscret11.MODE_DEC, img.getHeight(), img.getWidth(),
				JobConfig.getPerc1(), JobConfig.getPerc2());
		
		this.key11bits = device.getKey11bits();
		BufferedImage imgRes = device.transform(img);
				
		saveDecryptFile(imgRes, JobConfig.getOutput_file(), this.key11bits);		
	}
	
	public  void encPhotoSimpleDiscret(BufferedImage img){		
		Device device;
		if(JobConfig.isMaskedEdge()){
			device = new SimpleDiscret11MaskedBorder(JobConfig.getWord16bits(),
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
		if(JobConfig.isWantJoinInputOutputFrames()) {
			bi = Utils.joinImages(JobConfig.getInputImage(), bi);
		}		
		
		String output = JobConfig.getOutput_file();
		if (JobConfig.isHorodatage()) {
			File file = new File(JobConfig.getOutput_file());
			String fileName = JobConfig.getDateTime() + "_"
					+ file.getName();
			output = file.getParent() + File.separator + fileName;		
		}
		
		try {
			if (JobConfig.getSystemCrypt() == SystemType.DISCRET11) {
				// retrieve image
				File outputfile = new File(
						output + "_c" + 
				JobConfig.getWord16bits() + "-" + this.key11bits + ".png");
				ImageIO.write(bi, "png", outputfile);
				if (JobConfig.isHasGUI()) {

					JobConfig.getGui().getTextInfos().setText(JobConfig.getRes().getString("cryptPhoto.saveImage.codage") + output + "_c"
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
		
		
		if (JobConfig.getSystemCrypt() == SystemType.VIDEOCRYPT) { //videocrypt
			// retrieve image
			File outputfile = new File(
					output + "_c" + 
			"videocrypt" + "_" + colorMode + ".png");
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
				JobConfig.getGui().getTextInfos().setText(JobConfig.getRes().getString("cryptPhoto.saveImage.codage") + output + "_c"
						+ "videocrypt" + "_" + colorMode + ".png");
			}
		}
		
		if (JobConfig.getSystemCrypt() == SystemType.EUROCRYPT) { //eurocrypt
			// retrieve image
			File outputfile = new File(
					output + "_c" + 
			"mac" + "_" + this.eurocryptMode + ".png");
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
				JobConfig.getGui().getTextInfos().setText(JobConfig.getRes().getString("cryptPhoto.saveImage.codage") + output + "_c"
						+ "mac" + "_" + this.eurocryptMode + ".png");
			}
		}
		
		if (JobConfig.getSystemCrypt() == SystemType.LUXCRYPT) { //luxcrypt
			// retrieve image
			File outputfile = new File(
					output + "_" + 
			"luxcrypt.png");
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
				JobConfig.getGui().getTextInfos().setText(JobConfig.getRes().getString("cryptPhoto.saveImage.codage") + output + "_c"
						+ "luxcrypt.png");
			}
		}
		
		if (JobConfig.getSystemCrypt() == SystemType.DISCRET12) { //discret12
			// retrieve image
			File outputfile = new File(
					output + "_" + 
			"discret12.png");
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
				JobConfig.getGui().getTextInfos().setText(JobConfig.getRes().getString("cryptPhoto.saveImage.codage") + output + "_c"
						+ "discret12.png");
			}
		}
		
		
		if (JobConfig.getSystemCrypt() == SystemType.SYSTER) { //syster
			// retrieve image
			File outputfile = new File(
					output + "_c" + 
			"syster" + "_" + colorMode + ".png");
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
				JobConfig.getGui().getTextInfos().setText(JobConfig.getRes().getString("cryptPhoto.saveImage.codage") + output + "_c"
						+ "syster" + "_" + colorMode + ".png");
			}
		}
		
		
		if (JobConfig.getSystemCrypt() == SystemType.DISCRET11) {
			try {
				File dataFile = new File(
						output + "_c" + JobConfig.getWord16bits() + "-" + this.key11bits + ".txt");
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
									+ output + "_c" + JobConfig.getWord16bits() + "-"
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
	
	public boolean saveDecryptDiscret12(BufferedImage bi) {
		if(JobConfig.isWantJoinInputOutputFrames()) {
			bi = Utils.joinImages(JobConfig.getInputImage(), bi);
		}		
		
		String output = JobConfig.getOutput_file();
		if (JobConfig.isHorodatage()) {
			File file = new File(JobConfig.getOutput_file());
			String fileName = JobConfig.getDateTime() + "_"
					+ file.getName();
			output = file.getParent() + File.separator + fileName;		
		}
		
		
		
		// retrieve image
		File outputfile = new File(
				output + "_" + 
		"discret12_dec.png");
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
			JobConfig.getGui().getTextInfos().setText(JobConfig.getRes().getString("cryptPhoto.saveImage.decodage") + output + "_" + 
					"discret12_dec.png");
		}
		
		return true;
	}
	
	public  boolean saveDecryptFile(BufferedImage bi,String output_file, int key11){
		if(JobConfig.isWantJoinInputOutputFrames()) {
			bi = Utils.joinImages(JobConfig.getInputImage(), bi);
		}	
		
		String output = JobConfig.getOutput_file();
		if (JobConfig.isHorodatage()) {
			File file = new File(JobConfig.getOutput_file());
			String fileName = JobConfig.getDateTime() + "_"
					+ file.getName();
			output = file.getParent() + File.separator + fileName;		
		}
		
		if (JobConfig.getSystemCrypt() == SystemType.DISCRET11) {
			try {
				// retrieve image
				File outputfile = new File(output + "_d" + 
				JobConfig.getWord16bits() + "-" + key11 + ".png");
				ImageIO.write(bi, "png", outputfile);
				if (JobConfig.isHasGUI()) {
					JobConfig.getGui().getTextInfos().setText(
							JobConfig.getRes().getString("cryptPhoto.saveImage.decodage") + output + "_d" + 
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
				String sys;
				if(JobConfig.getSystemCrypt() == SystemType.TRANSCODE ) {
					sys = "transcode";
				}
				else if(JobConfig.getSystemCrypt() == SystemType.SYSTER ){
					sys = "syster";
				}
				else if(JobConfig.getSystemCrypt() == SystemType.VIDEOCRYPT ){
					sys = "videocrypt";
				}
				else if(JobConfig.getSystemCrypt() == SystemType.EUROCRYPT ){
					sys = "mac";
				}
				else {
					sys = "";
				}
				// retrieve image
				File outputfile = new File(output + "_d" + 
						sys + "_" + colorMode + ".png");
				ImageIO.write(bi, "png", outputfile);
				if (JobConfig.isHasGUI()) {
					JobConfig.getGui().getTextInfos().setText(
							JobConfig.getRes().getString("cryptPhoto.saveImage.decodage") + output + "_d" + 
									sys + "_" + colorMode + ".png");
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
	
	private BufferedImage doPanAndScan(BufferedImage ori_img) {
		int optimal_width = 0;
		int optimal_height = 0;
		int hori_cropBorders = 0;
		int verti_cropBorders = 0;
		BufferedImage imgCropped;

		if ((float) ori_img.getWidth() / (float) ori_img.getHeight() > 4f / 3f) {
			optimal_height = ori_img.getHeight();
			optimal_width = (int) ((4f / 3f) * (float) ori_img.getHeight());
			hori_cropBorders = (ori_img.getWidth() - optimal_width) / 2;
			
			imgCropped = ori_img.getSubimage(hori_cropBorders, 0, ori_img.getWidth() - (hori_cropBorders * 2),
					optimal_height);

		} else if ((float) ori_img.getWidth() / (float) ori_img.getHeight() < 4f / 3f) {			
			optimal_width = ori_img.getWidth();
			optimal_height = (int) ((float) ori_img.getWidth() / (4f / 3f));
			verti_cropBorders = (ori_img.getHeight() - optimal_height) / 2;

			imgCropped = ori_img.getSubimage(0, verti_cropBorders, optimal_width, optimal_height);
		} else {
			return ori_img;
		}

		BufferedImage copyOfImage = new BufferedImage(imgCropped.getWidth(), imgCropped.getHeight(),
				imgCropped.getType());
		Graphics g = copyOfImage.createGraphics();
		g.drawImage(imgCropped, 0, 0, null);

		return copyOfImage;

	}
	
	/**
	 * Stretch image pixels horizontally and vertically to 4/3 ratio
	 * @param ori_img bufferedimage to stretch
	 * @return stretch bufferedimage
	 */
	   private BufferedImage doStretch(BufferedImage ori_img){	   
		   return resize(ori_img, 768, 576, ori_img.getType());	   
	   }
	   
	   private BufferedImage resize(BufferedImage img, int width, int height, int typeBufferedImage) {
	       Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
	       BufferedImage resized = new BufferedImage(width, height, typeBufferedImage);
	       Graphics g = resized.createGraphics();
	       g.drawImage(tmp, 0, 0, null);
	       return resized;
	   }

}
