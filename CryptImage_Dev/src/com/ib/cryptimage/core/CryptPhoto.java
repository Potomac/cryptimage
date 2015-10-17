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

import javax.imageio.ImageIO;

/**
 * @author Mannix54
 *
 */
public class CryptPhoto {
		
	private int key11bits;
	
	public CryptPhoto(){			
	}
	
	public void run(){
		BufferedImage img = null;		
		try{
			 img = ImageIO.read(new File(JobConfig.getInput_file()));
		}
		catch(IOException e){			
			System.out.println("I/O error during the load of the input file.");
			System.exit(1);
		}		
		
		if(JobConfig.isModePhoto() && JobConfig.isWantDec()){
			decPhoto(img);
		}
		else if(JobConfig.isModePhoto() && JobConfig.isWantDec()!=true){
			encPhoto(img);
		}
	}
	
	
	public  void decPhoto(BufferedImage img){
		
		SimpleDiscret11 simpleDiscret = new SimpleDiscret11(JobConfig.getWord16bits(),
				JobConfig.getAudienceLevel(),
				SimpleDiscret11.MODE_DEC, img.getHeight(), img.getWidth());
		this.key11bits = simpleDiscret.getKey11bits();
		BufferedImage imgRes = simpleDiscret.transform(img);
		saveDecryptFile(imgRes, JobConfig.getOutput_file(), this.key11bits);		
	}
	
	public  void encPhoto(BufferedImage img){
		Discret discret;
		if(JobConfig.isNoBlackBar()){
			discret = new SimpleDiscret11NoBlack(JobConfig.getWord16bits(),
					JobConfig.getAudienceLevel(),
					SimpleDiscret11.MODE_ENC, img.getHeight(), img.getWidth());
		}
		else {
			discret = new SimpleDiscret11(JobConfig.getWord16bits(),
					JobConfig.getAudienceLevel(),
					SimpleDiscret11.MODE_ENC, img.getHeight(), img.getWidth());
		}	
		this.key11bits = discret.getKey11bits();
		BufferedImage imgRes = discret.transform(img);
		saveCryptImage(imgRes);		
	}
	
	public  void saveCryptImage(BufferedImage bi) {
		try {
			// retrieve image
			File outputfile = new File(JobConfig.getOutput_file() + "_c" +
					JobConfig.getWord16bits() + "-" + this.key11bits + ".png");
			ImageIO.write(bi, "png", outputfile);
			if(JobConfig.isHasGUI()){
				JobConfig.getGui().getTextInfos().setText(
						"Image codée : " + JobConfig.getOutput_file()
						+"_c" 
						+ JobConfig.getWord16bits() + "-" + this.key11bits + ".png");
			}
			System.out.println("SimpleDiscret11 crypted image : " + JobConfig.getOutput_file()
					+ "_c"
					+ JobConfig.getWord16bits() + "-" + this.key11bits + ".png");
		} catch (IOException e) {
			System.out.println("I/O error during the write of the crypted image");
			System.exit(1);
		}

		try {
			File dataFile = new File(JobConfig.getOutput_file() + "_c" 
						+ JobConfig.getWord16bits() + "-" 
						+  this.key11bits + ".txt");
			dataFile.createNewFile();
			FileWriter ffw = new FileWriter(dataFile);
			ffw.write("key 16 bits : " + JobConfig.getWord16bits() + "\r\n");
			ffw.write("Audience level : " + JobConfig.getAudienceLevel() + "\r\n");
			ffw.write("key 11 bits : " + this.key11bits + "\r\n");			
			ffw.write("file : " + JobConfig.getOutput_file() + "_c" 
			+ JobConfig.getWord16bits() + "-" + this.key11bits +
			 ".txt");
			ffw.close();
			if(JobConfig.isHasGUI()){
				JobConfig.getGui().getTextInfos().setText(
						JobConfig.getGui().getTextInfos().getText() 
						+ "\n\r"
						+
						"Rapport : " + JobConfig.getOutput_file() +"_c" 
						+ JobConfig.getWord16bits() + "-" + this.key11bits + ".txt");
			}
			System.out.println("Report shift file : " + JobConfig.getOutput_file()
					+ "_c" + JobConfig.getWord16bits() + "-" + this.key11bits + ".txt");
		} catch (IOException e) {
			System.out
					.println("I/O error during the write of the report file");
			System.exit(1);
		}
	}
	
	public  void saveDecryptFile(BufferedImage bi,String output_file, int key11){
		try {
			// retrieve image
			File outputfile = new File(output_file + "_d" + JobConfig.getWord16bits() 
								+ "-" + key11 + ".png");
			ImageIO.write(bi, "png", outputfile);
			if(JobConfig.isHasGUI()){
				JobConfig.getGui().getTextInfos().setText(
						"Image décodée : " 
				+ output_file + "_d" + JobConfig.getWord16bits() + "-" + key11 + ".png");
			}
			System.out.println( "Output File decrypted : " + output_file + "_d"
			+ JobConfig.getWord16bits() + "-"
			+ key11 + ".png" );
		} catch (IOException e) {
			System.out.println("I/O error during the write of the decrypted image");			
			System.exit(1);
		}
	}	

}
