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
 * 16 févr. 2019 Author Mannix54
 */



package com.ib.cryptimage.core;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Mannix54
 *
 */
public class Discret11ComputePixels {
	
	private BufferedImage img;
	private int cord = 21;//326;
	private int[] sample = new int[92];
	
	private WritableRaster raster;
	private WritableRaster rasterOdd;
	private WritableRaster rasterEven;	
	private BufferedImage imageOdd;
	private BufferedImage imageEven;
	private int[] relation = new int[12282];
	
	private Vector<Integer> keyVec = new Vector<Integer>();
	private Map<Integer, Integer> dicKey = new HashMap<Integer,  Integer>();
	
	private String keyfull = "";
	private int sWidth = 768;
	private int[][]  delayArrayFull = new int[12282][286]; //8188
	private int[] decaPixels = new int[3];
	
	private int searchMode = 0; //0 for brut force, 1 for 68705 algo
	private int[][] audience = new int[8][2];
	private int currentAudience = 0;
	private Vector<Integer> audienceVec = new Vector<Integer>();
	
	private Map<Integer, Integer> dicKey1 = new HashMap<Integer,  Integer>();
	private Map<Integer, Integer> dicKey2 = new HashMap<Integer,  Integer>();
	private Map<Integer, Integer> dicKey3 = new HashMap<Integer,  Integer>();
	private Map<Integer, Integer> dicKey4 = new HashMap<Integer,  Integer>();
	private Map<Integer, Integer> dicKey5 = new HashMap<Integer,  Integer>();
	private Map<Integer, Integer> dicKey6 = new HashMap<Integer,  Integer>();
	private Map<Integer, Integer> dicKey7 = new HashMap<Integer,  Integer>();
	
	private int discretCycle = 0;
	private int discretSeq = 0;
	private int minRepetition = 20;
		
	
	/**
	 * 
	 */
	public Discret11ComputePixels(int searchMode) {
		this.searchMode = searchMode;
		if(this.searchMode == 0) {	
			initDecaPixels(JobConfig.getPerc1(),JobConfig.getPerc2());
			loadFullArray();
			
			for (int i = 0; i < 2047; i++) {
				for (int j = 0; j < 6; j++) {
					for (int k = 0; k < 286; k++) {					
						relation[i + 2047 * j] = i;
					}
				}
			}
		//init audience
			this.audience[0][0] = 0;
			this.audience[0][1] = 0;
			this.audience[1][0] = 0;
			this.audience[1][1] = 0;
			this.audience[2][0] = 0;
			this.audience[2][1] = 0;
			this.audience[3][0] = 0;
			this.audience[3][1] = 0;
			this.audience[4][0] = 0;
			this.audience[4][1] = 0;
			this.audience[5][0] = 0;
			this.audience[5][1] = 0;
			this.audience[6][0] = 0;
			this.audience[6][1] = 0;
			this.audience[7][0] = 0;
			this.audience[7][1] = 0;
		}
	}

	public void setImg(BufferedImage img, int audience) {
		this.img = img;
		this.currentAudience = audience;
		this.discretSeq++;
		if(this.discretSeq == 9) {
			this.discretSeq = 1;
			this.discretCycle++;
		}
		
		if(this.searchMode == 0) {
			if(!audienceVec.contains(audience)) {
				audienceVec.add(audience);				
			}
			search11bitKey();
		}
		else {
			getSamplesEven();
			System.out.println(img.getHeight());
		}		
	}

	public boolean isKeyReady() {		
		Vector<Integer> aud = new Vector<Integer>();
		boolean isOk = true;
		
		if(this.discretCycle > 30) {
			for (int i = 0; i < this.audienceVec.size(); i++) {
//				System.out.println(this.audienceVec.get(i) + " : " 
//									+ this.audience[this.audienceVec.get(i)][0] 
//									+ " (" + this.audience[this.audienceVec.get(i)][1] + ")");
				aud.add(this.audience[this.audienceVec.get(i)][1]);				
			}
			
			for (int i = 0; i < aud.size(); i++) {
				if(aud.get(i) < this.minRepetition) {
					isOk = false;
					break;
				}
			}
			return isOk;
		}
		else {
			return false;
		}
	}
	
	public String displayAudiences() {
		String audiencesLst = "- 11 bit key :\n";
		for (int i = 0; i < this.audienceVec.size(); i++) {
			if(this.audience[this.audienceVec.get(i)][1] > 10 || this.audienceVec.get(i) == 7) {
				audiencesLst += "audience " + this.audienceVec.get(i) + " : " 
						+ this.audience[this.audienceVec.get(i)][0] 
						+ " (" + this.audience[this.audienceVec.get(i)][1] + ")"
						+ "\n";		
			}	
		}
		
		return audiencesLst;
	}
	
	public int get16bitKey() {		
		Discret11FindKey keyFinder = new Discret11FindKey();
		int key16bit = 0;
		
		for (int i = 0; i < this.audienceVec.size(); i++) {
			switch (this.audienceVec.get(i)) {
			case 1:		
				if(this.audience[this.audienceVec.get(i)][1] > 10)
					keyFinder.setAudience1(this.audience[this.audienceVec.get(i)][0]);							
				break;
			case 2:			
				if(this.audience[this.audienceVec.get(i)][1] > 10)
					keyFinder.setAudience2(this.audience[this.audienceVec.get(i)][0]);
				break;
			case 3:	
				if(this.audience[this.audienceVec.get(i)][1] > 10)
					keyFinder.setAudience3(this.audience[this.audienceVec.get(i)][0]);
				break;
			case 4:
				if(this.audience[this.audienceVec.get(i)][1] > 10)
					keyFinder.setAudience4(this.audience[this.audienceVec.get(i)][0]);
				break;
			case 5:	
				if(this.audience[this.audienceVec.get(i)][1] > 10)
					keyFinder.setAudience5(this.audience[this.audienceVec.get(i)][0]);
				break;
			case 6:		
				if(this.audience[this.audienceVec.get(i)][1] > 10)
					keyFinder.setAudience6(this.audience[this.audienceVec.get(i)][0]);
				break;
			case 7:
				key16bit = 1337;
				break;

			default:
				key16bit = 1337;
				break;
			}			
		}		
		
		if(key16bit != 1337) {			
			return keyFinder.getKey16bits();
		}
		else {
			return key16bit;
		}
	}
		
	
	private void getSamplesEven() {
		int i = 0;
		int val = 0;
		for (int y = 1; y < 185; y++) {			
			val = img.getRGB(cord, y);
			//System.out.println((((val >>16 ) & 0xFF) + ((val >>8 ) & 0xFF) + (val & 0xFF))/3);
			sample[i] = discriminate((((val >>16 ) & 0xFF) + ((val >>8 ) & 0xFF) + (val & 0xFF))/3);
			
			System.out.print(sample[i] + " ");
			i++;
			y++; // add one to y in order to have only odd lines frame
		}
		System.out.println();
		
	}
	
	
	private int discriminate(int val) {
		if(val < 123) {
			return 0;
		}
		else {
			return 1;
		}
	}
	
	private void search11bitKey() {
		raster = img.getRaster();
		createImageOddEven();
		computeSolution(imageOdd);
		//decryptImgOdd();
		//computeSolution(imageEven);
		//decryptImgEven();	
	}
	
	private void createImageOddEven(){		
		imageEven = new BufferedImage(768, 288, BufferedImage.TYPE_3BYTE_BGR);
		rasterEven = imageEven.getRaster();
		imageOdd = new BufferedImage(768, 288, BufferedImage.TYPE_3BYTE_BGR);
		rasterOdd = imageOdd.getRaster();
		for (int i = 0; i < 287; i++) {
			rasterEven.setPixels(0, i, 768, 1, raster.getPixels(0,(i+1) *2 +1, 768, 1, new int[768*3]));		
			rasterOdd.setPixels(0, i, 768, 1, raster.getPixels(0, (i+1)*2 , 768, 1, new int[768*3]));
		}
	}
	
	
	private void computeSolution(BufferedImage image) {		
		long val;
		
		int[] tab ;
		tab = image.getRGB(0, 0, 768, 288, null, 0, 768);

		long distance_mini = 93466368856l;	
		int incr_opt = 0;	
		int incr = -1;
		
		//TODO
		//calcul image claire
		val = distanceClear(tab);
		if (val < distance_mini) {
			distance_mini = val;	
			incr_opt = incr;
		}		
		
			for (incr = 1; incr < 12282; incr++) {
				val = correl(incr, tab);
				if (val < distance_mini) {
					distance_mini = val;	
					incr_opt = incr;
				}
			}

		 if (incr_opt != -1) {
			 
			 keyfull = keyfull + " " + (relation[incr_opt] + 1);
			 
			 //System.out.println(relation[incr_opt]);
			 
			 if(!getdicKeyAudience().containsKey(relation[incr_opt] + 1)) {
				 getdicKeyAudience().put(relation[incr_opt] + 1, 1);
			 }
			 else {
				 getdicKeyAudience().put(relation[incr_opt] + 1, getdicKeyAudience().get(relation[incr_opt] + 1) + 1);
			 }
			 			 
			 if(!keyVec.contains(relation[incr_opt] + 1)) {
				 keyVec.add(relation[incr_opt] + 1);
				 dicKey.put(relation[incr_opt] + 1, 1);				 
			 }
			 else {
				 dicKey.put(relation[incr_opt] + 1, dicKey.get(relation[incr_opt] + 1) + 1);
			 }
			 for (int i = 0; i < keyVec.size(); i++) {
				 if(dicKey.get(keyVec.get(i)) > 0) {
				//System.out.print( keyVec.get(i) + "(" + dicKey.get(keyVec.get(i)) +"); ");
			}			
			 }
						 
			 for (int i = 0; i < keyVec.size(); i++) {
				 //System.out.print( keyVec.get(i) + ";");
			}
			 //System.out.println("");
			 //System.out.println(keyfull);
			 //update audience stats
			 
			 Map.Entry<Integer, Integer> maxEntry = null;

			 for (Map.Entry<Integer, Integer> entry : getdicKeyAudience().entrySet())
			 {
			     if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
			     {
			         maxEntry = entry;
			     }
			 }
			 
			 this.audience[this.currentAudience][0] = maxEntry.getKey(); // Integer.valueOf(relation[incr_opt] + 1);
			 this.audience[this.currentAudience][1] = maxEntry.getValue();//dicKey.get(this.audience[this.currentAudience][0]);			 
		 }
		 

	}
	
	private Map<Integer, Integer> getdicKeyAudience() {
		Map<Integer, Integer> obj = null;
		
		switch (this.currentAudience) {
		case 1:
			obj = this.dicKey1;
			break;
		case 2:
			obj =  this.dicKey2;
			break;
		case 3:
			obj =  this.dicKey3;
			break;
		case 4:
			obj =  this.dicKey4;
			break;
		case 5:
			obj =  this.dicKey5;
			break;
		case 6:
			obj =  this.dicKey6;
			break;
		case 7:
			obj =  this.dicKey7;
			break;

		default:
			obj =  this.dicKey7;
			break;
		}
		
		return obj;		
	}
	
	
	// Calculates the distance between 2 image rows
	private long distanceClear (int[] tab)
	{
		
		long res;
		int i,j,val,val2,pix1,pix2;
		
		res = 0;
		for (i=100; i<220; i+=3){ // 100 220 i++=3
			for (j = 0; j < 600; j+=150) { //j = 0; j < 600; j+=150					
				val = tab[(i + j) + i * 768];
				val2 = tab[(i + 1 + j) + (i+1) * 768];
				pix1 =((((val >>16 ) & 0xFF) + ((val >>8 ) & 0xFF) + (val & 0xFF))/3);
				pix2 =((((val2 >>16 ) & 0xFF) + ((val2 >>8 ) & 0xFF) + (val2 & 0xFF))/3);
				res += Math.abs((long)(pix1 - pix2));						
			}			
		}
		return res;
	}
	
	private long correl(int seq, int[] tab) {
		long val;
		long somme = 0; // Sum of the distances between rows.

		val = distance(seq, tab);
		somme += val;

		return somme;
	}
	
	// Calculates the distance between 2 image rows
	private long distance ( int seq, int[] tab)
	{
		
		long res;
		int i,val,val2,pix1,pix2;
		
		res = 0;
		for (i=100; i<220; i+=3){ // 100 220 i++=3
			for (int j = 0; j < 600; j+=150) {
				//res += Math.abs((long)(raster.getPixel(decaPixels[2] - decaPixels[delayArrayFull[key11bits][seq][ i]] + j, i, new int[3])[0] - raster.getPixel(decaPixels[2] - decaPixels[delayArrayFull[key11bits][seq][ i + 1]] + j, i+1, new int[3])[0]));
				val = tab[( decaPixels[delayArrayFull[seq][ i]] + j) + i * 768];
				val2 = tab[( decaPixels[delayArrayFull[seq][ i + 1]] + j) + (i+1) * 768];
				pix1 =((((val >>16 ) & 0xFF) + ((val >>8 ) & 0xFF) + (val & 0xFF))/3);
				pix2 =((((val2 >>16 ) & 0xFF) + ((val2 >>8 ) & 0xFF) + (val2 & 0xFF))/3);
				res += Math.abs((long)(pix1 - pix2));	
				//				val = image.getRGB(decaPixels[2] - decaPixels[delayArrayFull[key11bits][seq][ i]] + j, i ) - image.getRGB(decaPixels[2] - decaPixels[delayArrayFull[key11bits][seq][ i + 1]] + j, i+1 );
//				res += (((val >>16 ) & 0xFF) + ((val >>8 ) & 0xFF) + (val & 0xFF))/3;
			}			
		}
		return res;
	}

	
	/**
	 * set the 3 shift pixels value for the decaPixels array
	 * 
	 * @param perc1 the percentage value of retard 1
	 * @param perc2 the percentage value of retard 2
	 */
	private void initDecaPixels(double perc1, double perc2) {
		decaPixels[0] = 0;
		decaPixels[1] = (int) (Math.round(perc1 * this.sWidth)); // previous value : 0.018 0.0167 0.0167
		decaPixels[2] = (int) (Math.round(perc2 * this.sWidth)); // previous value : 0.036 0.0347 0.0334

		if (decaPixels[1] == 0) {
			decaPixels[1] = 1;
		}
		if (decaPixels[2] == 0) {
			decaPixels[2] = 2;
		}

		JobConfig.setDelay1(decaPixels[1]);
		JobConfig.setDelay2(decaPixels[2]);

	}
	
	private void loadFullArray(){		
		//get the zip file content
		byte[] buffer = new byte[1024];
    	try {
    		//InputStream is = this.getClass().getResourceAsStream("/ressources/delarray.zip");
    		InputStream is = this.getClass().getResourceAsStream("/ressources/delarray_special.zip");
			ZipInputStream zis = 
				new ZipInputStream(is);
		   	
			//get the zipped file list entry
	    	ZipEntry ze = null;
			try {
				ze = zis.getNextEntry();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    		
	    	while(ze!=null){
	    			
	    	   String fileName = ze.getName();
	           File newFile = new File(System.getProperty("java.io.tmpdir") + File.separator + fileName);
	           	            	              
	           FileOutputStream fos = new FileOutputStream(newFile);             

	            int len;
	            try {
					while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        		
	            try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}   
	            try {
					ze = zis.getNextEntry();
					zis.closeEntry();
			    	zis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	   	    	    	
    	} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    				
		
		ObjectInputStream inputStream = null;
        try{
            inputStream = new ObjectInputStream(new FileInputStream((System.getProperty("java.io.tmpdir")+ File.separator +"delarray_special.bin")));
        }catch(IOException e){
            System.out.println("There was a problem opening the file: " + e);
            System.exit(0);
        }
        
        try{
            delayArrayFull = (int [][])inputStream.readObject();       
            inputStream.close();
        }catch(Exception e){
            System.out.println("There was an issue reading from the file: " + e);
            System.exit(0);
        }
        Path path = FileSystems.getDefault().getPath(System.getProperty("java.io.tmpdir"), "delarray_special.bin");
        
        try {
			Files.delete(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Vector<Integer> getAudienceVec() {
		return audienceVec;
	}
	
}
