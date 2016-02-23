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
 * 23 nov. 2015 Author Mannix54
 */



package com.ib.cryptimage.core;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


/**
 * @author Mannix54
 *
 */
public class SysterDec extends Syster {	
	
	long[][] matdist;
	boolean wantCorrel = false;
	WritableRaster raster;
	WritableRaster raster2;
	WritableRaster rasterCorrel;
	BufferedImage frame;
	BufferedImage newImage;
	BufferedImage imageSource;
	
	public SysterDec(int typeTable, String nameDataFile,
			boolean wantCorrel) {
		super(typeTable, false);
		this.wantCorrel = wantCorrel;
		// TODO Auto-generated constructor stub
		if (!nameDataFile.equals("")) {
			try {
				fileInBuff = new BufferedReader(new FileReader(nameDataFile));
				this.fileInReader = new FileReader(nameDataFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (wantCorrel == true) {
			matdist = new long[287][287];
		}
	}

	public void deCrypt() {

		int i;
		int j;
		int[] buffer = new int[32];

		for (i = 0; i < 32; i++)
			buffer[i] = -1;

		j = offset;

		for (i = 0; i < 255; i++) {
			if (buffer[keyTable[j]] == -1)
				order[i] = keyTable[j];
			else
				order[i] = buffer[keyTable[j]];

			buffer[keyTable[j]] = 32 + i;
			j += increment; // Note that j is a BYTE
			if (j >= 256) {
				j = j - 256;
			}
		}

		for (i = 0; i < 32; i++)
			order[255 + i] = buffer[i];

		// offset=j;
	}

	@Override
	public BufferedImage transform(BufferedImage image) {
		numFrames++;
		image = this.convertToType(image, BufferedImage.TYPE_3BYTE_BGR);
		if (image.getWidth() != this.sWidth || image.getHeight() != 576) {
			image = this.getScaledImage(image, this.sWidth, 576);
		}
		this.imageSource = deepCopy(image);		
		
		if(numFrames > 1){
		deCryptOddFrame(image);
		}		
		deCryptEvenFrame(image);
						
		return this.getCompletFrame();
		
	}
	
	private void deCryptOddFrame(BufferedImage image){
		//WritableRaster raster;
		raster2 = image.getRaster(); //deepCopy(image).getRaster();
		newImage = new BufferedImage(this.sWidth, 288, BufferedImage.TYPE_3BYTE_BGR);
		raster = newImage.getRaster();
		
				
		for (int i = 0; i < order.length; i++) {
			raster.setPixels(0, i, this.sWidth, 1, raster2
					.getPixels(0, i*2 , this.sWidth, 1,
							new int[sWidth * 3]));			
		}
		
		raster.setPixels(0, 287, sWidth, 1, raster2.getPixels(0, 574, sWidth,1,new int[sWidth * 3]));
		
		manageVectorFrame(newImage);		
	}
	
	private void deCryptEvenFrame(BufferedImage image){
		//WritableRaster raster;
		raster2 = image.getRaster(); //deepCopy(image).getRaster();
		newImage = new BufferedImage(this.sWidth, 288, BufferedImage.TYPE_3BYTE_BGR);
		raster = newImage.getRaster();
				
		
		for (int i = 0; i < order.length; i++) {
			raster.setPixels(0, i, this.sWidth, 1, raster2
					.getPixels(0, i*2 +1, this.sWidth, 1,
							new int[sWidth * 3]));			
		}
		
		raster.setPixels(0, 287, sWidth, 1, raster2.getPixels(0, 575, sWidth,1,new int[sWidth * 3]));
		
		manageVectorFrame(newImage);
	}
	
	private void manageVectorFrame(BufferedImage image){
		if(vecFrame.size()==3){
			createDecryptableFrame();
		}
		vecFrame.addElement(image);
	}
	
	private boolean createDecryptableFrame(){
		//WritableRaster raster;		
	
		// frame 1
		// push 256-287 lines to the top 1-32
		raster = vecFrame.get(0).getRaster();

		raster.setPixels(0, 0, this.sWidth, 32, raster.getPixels(0, 255, sWidth, 32, new int[sWidth * 3 * 32]));

		// push 1-255 lines from frame 2 to frame 1 33-287		
		raster.setPixels(0, 32, this.sWidth, 255, vecFrame.get(1).getRaster().getPixels(0, 0, sWidth, 255, new int[sWidth * 3 * 255]));
		
		//push line 288 from frame 2 to line 288 frame 1
		raster.setPixels(0, 287, this.sWidth, 1, vecFrame.get(1).getRaster().getPixels(0, 287, sWidth, 1, new int[sWidth * 3]));
		
		
		// frame 2
		// push 256-287 lines to the top 1-32
		raster = vecFrame.get(1).getRaster();

		raster.setPixels(0, 0, this.sWidth, 32, raster.getPixels(0, 255, sWidth, 32, new int[sWidth * 3 * 32]));
		
		// push 1-255 lines from frame 3 to frame 2 33-287
		raster.setPixels(0, 32, this.sWidth, 255, vecFrame.get(2).getRaster().getPixels(0, 0, sWidth, 255, new int[sWidth * 3 * 255]));
		
		//push line 288 from frame 3 to line 288 frame 2
		raster.setPixels(0, 287, this.sWidth, 1, vecFrame.get(2).getRaster().getPixels(0, 287, sWidth, 1, new int[sWidth * 3]));
				
		
		
		if (!this.wantCorrel) {
			if (JobConfig.isWantSysterTags()) {
				if(!tagLine(vecFrame.get(0).getRaster(), vecFrame.get(1).getRaster())){
					//newDecryptFullFrame();
					this.completFrame = this.imageSource;
					// remove frame 1 and 2 from vecFrame
					vecFrame.remove(0);
					vecFrame.remove(0);	
					this.ready = true;
					this.enable = false;
					return false;
				}
			} else {
				if(!this.readFileData()){
					this.completFrame = this.imageSource;
					// remove frame 1 and 2 from vecFrame
					vecFrame.remove(0);
					vecFrame.remove(0);	
					this.ready = true;
					this.enable = false;
					return false;
				}
			}
		}
		
		//decrypt frame 1 and 2
		raster = vecFrame.get(0).getRaster();
		raster2 = deepCopy(vecFrame.get(0)).getRaster();
		
		if (!this.wantCorrel) {			
				this.offset = this.offSetOdd;
				this.increment = this.incrementOdd;			
		} else {
			computeSolution(vecFrame.get(0));
		}
		
		if (this.offset != -1 && this.increment != -1) {
			this.deCrypt();

			for (int i = 0; i < order.length; i++) {
				raster.setPixels(0, i, this.sWidth, 1,
						raster2.getPixels(0, order[i], this.sWidth, 1, new int[sWidth * 3]));
			}
		}
		
		raster = vecFrame.get(1).getRaster();
		raster2 = deepCopy(vecFrame.get(1)).getRaster();
		
		if (!this.wantCorrel) {			
				this.offset = this.offSetEven;
				this.increment = this.incrementEven;			
		} else {
			computeSolution(vecFrame.get(1));
		}
		
		if (this.offset != -1 && this.increment != -1) {
			this.deCrypt();

			for (int i = 0; i < order.length; i++) {
				raster.setPixels(0, i, this.sWidth, 1,
						raster2.getPixels(0, order[i], this.sWidth, 1, new int[sWidth * 3]));
			}
		}
		
		newDecryptFullFrame();
		
		if (this.offset == -1 && this.increment == -1) {
			this.completFrame = this.imageSource;
			this.ready = true;
			this.enable = false;
			return false;
		}
		
		return true;
		
	}
	
	private void newDecryptFullFrame(){
		// create new decrypt frame from decrypted frame 1 and 2
		frame = new BufferedImage(this.sWidth, 576, BufferedImage.TYPE_3BYTE_BGR);
		raster = frame.getRaster();

		int line = 0;
		for (int i = 0; i < 288; i++) {
			raster.setPixels(0, line, this.sWidth, 1,
					vecFrame.get(0)
					.getRaster().getPixels(0, i, this.sWidth, 1, new int[sWidth * 3]));
			line++;
			line++;
		}

		line = 1;
		for (int i = 0; i < 288; i++) {
			raster.setPixels(0, line, this.sWidth, 1,
					vecFrame.get(1)
					.getRaster().getPixels(0, i, this.sWidth, 1, new int[sWidth * 3]));
			line++;
			line++;
		}
		
		// remove frame 1 and 2 from vecFrame
		vecFrame.remove(0);
		vecFrame.remove(0);	
		
		this.completFrame = frame;
		this.enable = true;
		this.ready = true;	
	}
	
	@Override
	void closeFileData() {
		// TODO Auto-generated method stub
		
	}
	
	private boolean tagLine(Raster ra1, Raster ra2){		
		String typeTable = "";
		String offsetBin1 = "";
		String offsetBin2 = "";
		String incrementBin1 = "";
		String incrementBin2 = "";
		int nbPixels = 8;
		int whiteMin = 200;
		int whiteMax = 255;
		int blackMin = 0;
		int blackMax = 127;
		
		int[] tab = new int[3];
		double somme = 0;
		int incre = 0;
		
		// tag typeTable
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < nbPixels; j++) {
				somme += (ra1.getPixel(incre, 287, tab)[0] + ra1.getPixel(incre, 287, tab)[1] + ra1.getPixel(incre, 287, tab)[2]) / 3d;
			incre++;
			}
			somme = somme/nbPixels;
			if (somme >= blackMin && somme < blackMax) {
				typeTable = typeTable + "0";
				somme = 0;
			} else {
				if (somme > whiteMin && somme <= whiteMax) {
					typeTable = typeTable + "1";
					somme = 0;
				}
			}			
		}
		
		somme = 0;
		incre = 0;
		
		//tag offset1		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < nbPixels; j++) {
			somme += ( ra1.getPixel(incre + 2*nbPixels, 287,tab)[0] + ra1.getPixel(incre + 2*nbPixels, 287,tab)[1] +ra1.getPixel(incre + 2*nbPixels, 287,tab)[2] )/3d;
			incre++;
			}
			somme = somme/nbPixels;
			if(somme >=blackMin && somme < blackMax){
				offsetBin1 = offsetBin1 + "0";
				somme = 0;
			}
			else{
				if(somme >whiteMin && somme <= whiteMax){
					offsetBin1 = offsetBin1 + "1";
					somme = 0;
				}
			}				
		}
		
		somme = 0;
		incre = 0;
		// tag offset2		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < nbPixels; j++) {
			somme += (ra2.getPixel(incre+ 2*nbPixels, 287, tab)[0] + ra2.getPixel(incre+ 2*nbPixels, 287, tab)[1] + ra2.getPixel(incre+ 2*nbPixels, 287, tab)[2]) / 3d;
			incre++;
			}
			somme = somme/nbPixels;
			if (somme >= blackMin && somme < blackMax) {
				offsetBin2 = offsetBin2 + "0";
				somme = 0;
			} else {
				if (somme > whiteMin && somme <= whiteMax) {
					offsetBin2 = offsetBin2 + "1";
					somme = 0;
				}
			}
		}
		
		somme = 0;
		incre = 0;
		
		//tag increment1		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < nbPixels; j++) {
			somme += ( ra1.getPixel(incre + 2*nbPixels + 8*nbPixels, 287,tab)[0] + ra1.getPixel(incre +2*nbPixels + 8*nbPixels, 287,tab)[1] +ra1.getPixel(incre +2*nbPixels + 8*nbPixels, 287,tab)[2] )/3d;
			incre++;
			}
			somme = somme/nbPixels;
			if(somme >=blackMin && somme < blackMax){
				incrementBin1 = incrementBin1 + "0";
				somme = 0;
			}
			else{
				if(somme >whiteMin && somme <= whiteMax){
				    incrementBin1 = incrementBin1 + "1";
				    somme = 0;
				}
			}				
		}
		
		somme = 0;
		incre = 0;
		// tag increment2		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < nbPixels; j++) {
			somme += (ra2.getPixel(incre + 2*nbPixels + 8*nbPixels, 287, tab)[0] + ra2.getPixel(incre + 2*nbPixels + 8*nbPixels, 287, tab)[1] + ra2.getPixel(incre + 2*nbPixels + 8*nbPixels, 287, tab)[2]) / 3d;
			incre++;
			}
			somme = somme/nbPixels;
			if (somme >= blackMin && somme < blackMax) {
				incrementBin2 = incrementBin2 + "0";
				somme = 0;
			} else {
				if (somme > whiteMin && somme <= whiteMax) {
					incrementBin2 = incrementBin2 + "1";
					somme = 0;
				}
			}
		}
		
		//check values
		if (!checkValuesTags(typeTable, offsetBin1, offsetBin2, incrementBin1, incrementBin2)){
			return false;
		}
		
		if(Integer.parseInt(typeTable, 2) != this.getTypeTable()){
			this.setTypeTable(Integer.parseInt(typeTable, 2));
			this.initKeyTable();
		}
		
			
			this.offSetOdd = Integer.parseInt(offsetBin1, 2);
			this.offSetEven = Integer.parseInt(offsetBin2, 2);
			this.incrementOdd = Integer.parseInt(incrementBin1, 2);
			this.incrementEven = Integer.parseInt(incrementBin2, 2);
			
			return true;		
	}
	
	private boolean checkValuesTags(String typeTable, String offsetBin1, String offsetBin2,
			String incrementBin1, String incrementBin2){
		
		try {
			if(Integer.parseInt(typeTable, 2) >0 && Integer.parseInt(typeTable, 2) <3){
				if(Integer.parseInt(offsetBin1, 2) >= 0 && Integer.parseInt(offsetBin1, 2) < 256){
					if(Integer.parseInt(offsetBin2, 2) >= 0 && Integer.parseInt(offsetBin2, 2) < 256){
						if(Integer.parseInt(incrementBin1, 2) > 0 && Integer.parseInt(incrementBin1, 2) < 128){
							if(isOdd(Integer.parseInt(incrementBin1, 2))){
								if(Integer.parseInt(incrementBin2, 2) > 0 && Integer.parseInt(incrementBin2, 2) < 128){
									if(isOdd(Integer.parseInt(incrementBin2, 2))){
										return true;
									}
								}
							}
						}
					}
				}
			}
			
		} catch (Exception e) {
			return false;
		}
		
		return false;
	}
	
	private boolean isOdd(int val){
		double test = (double) val;
		if ((test / 2d) - (int)(test / 2d) != 0) {
			return true;
		}
		else 
			return false;
	}
	
//	private void tagLineBack(Raster ra1, Raster ra2){		
//		String typeTable = "";
//		String offsetBin1 = "";
//		String offsetBin2 = "";
//		String incrementBin1 = "";
//		String incrementBin2 = "";
//		
//		int[] tab = new int[3];
//		double somme = 0;
//		
//		// tag typeTable
//		for (int i = 0; i < 2; i++) {
//			somme = (ra1.getPixel(i, 287, tab)[0] + ra1.getPixel(i, 287, tab)[1] + ra1.getPixel(i, 287, tab)[2]) / 3d;
//			if (somme >= 0 && somme < 100) {
//				typeTable = typeTable + "0";
//			} else {
//				if (somme > 200 && somme <= 255) {
//					typeTable = typeTable + "1";
//				}
//			}
//		}
//		
//		//tag offset1		
//		for (int i = 0; i < 8; i++) {
//			somme = ( ra1.getPixel(i + 2, 287,tab)[0] + ra1.getPixel(i + 2, 287,tab)[1] +ra1.getPixel(i + 2, 287,tab)[2] )/3d;
//			if(somme >=0 && somme < 100){
//				offsetBin1 = offsetBin1 + "0";				
//			}
//			else{
//				if(somme >200 && somme <= 255){
//					offsetBin1 = offsetBin1 + "1";		
//				}
//			}				
//		}
//		// tag offset2		
//		for (int i = 0; i < 8; i++) {
//			somme = (ra2.getPixel(i + 2, 287, tab)[0] + ra2.getPixel(i + 2, 287, tab)[1] + ra2.getPixel(i + 2, 287, tab)[2]) / 3d;
//			if (somme >= 0 && somme < 100) {
//				offsetBin2 = offsetBin2 + "0";	
//			} else {
//				if (somme > 200 && somme <= 255) {
//					offsetBin2 = offsetBin2 + "1";		
//				}
//			}
//		}
//		
//		
//		//tag increment1		
//		for (int i = 8; i < 16; i++) {
//			somme = ( ra1.getPixel(i + 2, 287,tab)[0] + ra1.getPixel(i +2 , 287,tab)[1] +ra1.getPixel(i + 2, 287,tab)[2] )/3d;
//			if(somme >=0 && somme < 10){
//				incrementBin1 = incrementBin1 + "0";	
//			}
//			else{
//				if(somme >240 && somme <= 255){
//				    incrementBin1 = incrementBin1 + "1";	
//				}
//			}				
//		}
//		// tag increment2		
//		for (int i = 8; i < 16; i++) {
//			somme = (ra2.getPixel(i + 2, 287, tab)[0] + ra2.getPixel(i + 2, 287, tab)[1] + ra2.getPixel(i + 2, 287, tab)[2]) / 3d;
//			if (somme >= 0 && somme < 10) {
//				incrementBin2 = incrementBin2 + "0";
//			} else {
//				if (somme > 240 && somme <= 255) {
//					incrementBin2 = incrementBin2 + "1";
//				}
//			}
//		}
//		
//		this.setTypeTable(Integer.parseInt(typeTable,2));
//		this.offSetOdd = Integer.parseInt(offsetBin1, 2);
//		this.offSetEven = Integer.parseInt(offsetBin2, 2);
//		this.incrementOdd = Integer.parseInt(incrementBin1, 2);
//		this.incrementEven = Integer.parseInt(incrementBin2, 2);
//		
////		if(offSetOdd > 255 || offSetEven > 255 || incrementOdd > 127 || incrementEven > 127 
////				|| incrementEven == 0 || incrementOdd == 0){
////			this.offSetOdd = 20;
////			this.offSetEven = 20;
////			this.incrementOdd = 1;
////			this.incrementEven = 1;
////		}
//		
//		
////		if (this.getTypeTable() < 1 || this.getTypeTable() > 2) {
////			this.setTypeTable(2);
////		}
//		if(numFrames == 1){			
//			this.initKeyTable();
//			}
//		
//		
////		System.out.println(this.offSetOdd + " - " + this.incrementOdd );
////		System.out.println(this.offSetEven + " - " + this.incrementEven );
////		System.out.println("---------");
//		
//		
//	}
	
	// Calculates the distance between 2 image rows
		private long distance (int n, int m, int[] tab)
		{
			long res;
			int i,val,val2;			
			//rasterCorrel = image.getRaster();			

			res = 0;
			for (i=0; i<768; i+=50) { // 80 100 20 100
				//res += Math.abs((long)(rasterCorrel.getPixel(i, n, new int[3])[0] - rasterCorrel.getPixel(i, m, new int[3])[0]));
				val = tab[i + n * 768];
				val2 = tab[i + m * 768];
				res += Math.abs((long)(((((val >>16 ) & 0xFF) + ((val >>8 ) & 0xFF) + (val & 0xFF))/3) - ((((val2 >>16 ) & 0xFF) + ((val2 >>8 ) & 0xFF) + (val2 & 0xFF))/3)));
			}
			return res;
		}

		// Compute the correlation of the scrambled image with one of the 32768 possible permutations
		private long correl (int offset, int increment, int[] tab)
		{
		   short i;
		   long val;
		   long somme=0;	// Sum of the distances between rows.

		   this.offset = offset;
		   this.increment = increment;
		   deCrypt();   // Build the permutation sequence
		   for (i=50; i<236 ; i+=3) // 0 286 100 200 50 250 50-236 +3
			{
		   	val = matdist[order[i]] [order[i+1]]; // Distance between successive rows
				if (val == -1)	// If the distance has not been computed yet, it is.
				{
						val = distance (order[i], order[i+1], tab);
						matdist[order[i]] [order[i+1]] = val;
						matdist[order[i+1]] [order[i]] = val;
				}
		   	somme += val;
		   }
		   return somme;
		}
		
		private void computeSolution(BufferedImage image){
			long val;
			int i,j;
			int[] tab ;//= new int[768*288*3];
			tab = image.getRGB(0, 0, 768, 288, null, 0, 768);
			
			//(((val >>16 ) & 0xFF) + ((val >>8 ) & 0xFF) + (val & 0xFF))/3
			
			long distance_mini = 93466368856l;
			   int offset_opt = -1;
			   int incr_opt = -1;
			   int offset = -1;
			   int incr = -1;
			
			val = distanceClear(tab);
			if (val < distance_mini) {
				distance_mini = val;
				offset_opt = offset;
				incr_opt = incr;
			}	
			
			// Reset Matrix
			for (i=0; i<287; i++)
				for (j=i+1; j<287; j++)
					matdist[i][j] = matdist [j][i] = -1; 	// Distance unknown

			// Search the 32768 possible permutations , to find the lowest distance
			//System.out.println("Searching the best solution... Please wait \r\n");
		   
		   
			for (offset = 0; offset < 256; offset++) {//
				for (incr = 1; incr < 256; incr += 2) {
					val = correl(offset, incr, tab);
					if (val < distance_mini) {
						distance_mini = val;
						offset_opt = offset;
						incr_opt = incr;
					}
				}
			}
						
		   this.offset = offset_opt;
		   this.increment = incr_opt;		   
		}
		
		// Calculates the distance between 2 image rows
	private long distanceClear(int[] tab) {
		long res;
		int i, j, val, val2;
		// rasterCorrel = image.getRaster();

		res = 0;
		for (j = 50; j < 236; j += 3) { // 80 100 20 100
			// res += Math.abs((long)(rasterCorrel.getPixel(i, n, new int[3])[0]
			// - rasterCorrel.getPixel(i, m, new int[3])[0]));
			for (i = 0; i < 768; i += 50) {
				val = tab[i + j * 768];
				val2 = tab[i + (j + 1) * 768];
				res += Math.abs((long) (((((val >> 16) & 0xFF) + ((val >> 8) & 0xFF) + (val & 0xFF)) / 3)
						- ((((val2 >> 16) & 0xFF) + ((val2 >> 8) & 0xFF) + (val2 & 0xFF)) / 3)));
			}
		}
		return res;
	}

		
		@Override
		void skipFrame() {
			// TODO Auto-generated method stub
			//numSkip++;
			//numFrames = 0;
			if(fileInBuff != null)					
					{
				this.readFileDataDummy();
			}
		}

		@Override
		boolean isEnable() {
			// TODO Auto-generated method stub
			return this.enable;
		}

		@Override
		int getAudienceLevel() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		int getKey11bits() {
			// TODO Auto-generated method stub
			return 0;
		}
}
