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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * @author Mannix54
 *
 */
public class DiscretDecCorrel extends Discret {	

	/**
	 * store the delay in pixels value
	 * in 3 dimension array who represents
	 * the 6 TV frames,
	 */
	private int[][][]  delayArrayFull = new int[2047][6][286];
	
	/**
	 * array for storing the delay in pixels
	 * 3 types of delay
	 */
	private int[] decaPixels = new int[3];
	/**
	 * the count iterator for delay array
	 */
	protected int cptArray = 0;	
	/**
	 * the default width of the image which is 768
	 */	
	protected final int sWidth = 768;	
	/**
	 * store the current position from the 6 frames ( half image ) sequence
	 */
	protected int seqFrame = 0;	

	protected WritableRaster raster;
	WritableRaster rasterOdd;
	WritableRaster rasterEven;	
	private BufferedImage imageOdd;
	private BufferedImage imageEven;
	
	long[][] matdist;
	private int key11Sol;
	private int seqSol;
	BufferedImage imgFinal;
	
	private boolean enable = false;
	

	public DiscretDecCorrel()  {
		imgFinal = new BufferedImage(768, 576, BufferedImage.TYPE_3BYTE_BGR);		
		initDecaPixels(JobConfig.getPerc1(),JobConfig.getPerc2());
		loadFullArray();
	}
	
	private void loadFullArray(){		
		//get the zip file content
		byte[] buffer = new byte[1024];
    	try {
    		InputStream is = this.getClass().getResourceAsStream("/ressources/delarray.zip");
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
            inputStream = new ObjectInputStream(new FileInputStream((System.getProperty("java.io.tmpdir")+ File.separator +"delarray.bin")));
        }catch(IOException e){
            System.out.println("There was a problem opening the file: " + e);
            System.exit(0);
        }
        
        try{
            delayArrayFull = (int [][][])inputStream.readObject();            
            inputStream.close();
        }catch(Exception e){
            System.out.println("There was an issue reading from the file: " + e);
            System.exit(0);
        }
        Path path = FileSystems.getDefault().getPath(System.getProperty("java.io.tmpdir"), "delarray.bin");
        
        try {
			Files.delete(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	/**
	 * set the 3 shift pixels value for the decaPixels array
	 * @param perc1 the percentage value of retard 1
	 * @param perc2 the percentage value of retard 2
	 */
	private void initDecaPixels(double perc1, double perc2){
						
		decaPixels[0] = 0;
		decaPixels[1] = (int)(Math.round(perc1 * this.sWidth )); // previous value : 0.018 0.0167 0.0167
		decaPixels[2] = (int)(Math.round(perc2 * this.sWidth )); // previous value : 0.036 0.0347 0.0334
		
		if (decaPixels[1] == 0){
			decaPixels[1] = 1;
		}
		if (decaPixels[2] == 0 ){
			decaPixels[2] = 2;
		}
		
		JobConfig.setDelay1(decaPixels[1]);
		JobConfig.setDelay2(decaPixels[2]);
	}
	
	


	
	// Calculates the distance between 2 image rows
	private long distance (int key11bits, int seq, int[] tab)
	{
		
		long res;
		int i,val,val2,pix1,pix2;
		
		res = 0;
		for (i=100; i<220; i+=3){ // 100 220 i++=3
			for (int j = 0; j < 600; j+=150) {
				//res += Math.abs((long)(raster.getPixel(decaPixels[2] - decaPixels[delayArrayFull[key11bits][seq][ i]] + j, i, new int[3])[0] - raster.getPixel(decaPixels[2] - decaPixels[delayArrayFull[key11bits][seq][ i + 1]] + j, i+1, new int[3])[0]));
				val = tab[(decaPixels[2] - decaPixels[delayArrayFull[key11bits][seq][ i]] + j) + i * 768];
				val2 = tab[(decaPixels[2] - decaPixels[delayArrayFull[key11bits][seq][ i + 1]] + j) + (i+1) * 768];
				pix1 =((((val >>16 ) & 0xFF) + ((val >>8 ) & 0xFF) + (val & 0xFF))/3);
				pix2 =((((val2 >>16 ) & 0xFF) + ((val2 >>8 ) & 0xFF) + (val2 & 0xFF))/3);
				res += Math.abs((long)(pix1 - pix2));	
				//				val = image.getRGB(decaPixels[2] - decaPixels[delayArrayFull[key11bits][seq][ i]] + j, i ) - image.getRGB(decaPixels[2] - decaPixels[delayArrayFull[key11bits][seq][ i + 1]] + j, i+1 );
//				res += (((val >>16 ) & 0xFF) + ((val >>8 ) & 0xFF) + (val & 0xFF))/3;
			}			
		}
		return res;
	}
	
	private long correl(int key11bits, int seq, int[] tab) {
		long val;
		long somme = 0; // Sum of the distances between rows.

		val = distance(key11bits, seq, tab);
		somme += val;

		return somme;
	}

	private void computeSolution(BufferedImage image) {
		long val;
		
		int[] tab ;
		tab = image.getRGB(0, 0, 768, 288, null, 0, 768);

		long distance_mini = 93466368856l;
		int offset_opt = 0;
		int incr_opt = 0;
		int offset = -1;
		int incr = -1;
		
		//TODO
		//calcul image claire
		val = distanceClear(tab);
		if (val < distance_mini) {
			distance_mini = val;
			offset_opt = offset;
			incr_opt = incr;
		}		
		
		for (offset = 1; offset < 2047; offset++) {//
			
				
			for (incr = 0; incr < 6; incr++) {
				val = correl(offset,incr, tab);
				if (val < distance_mini) {
					distance_mini = val;
					offset_opt = offset;
					incr_opt = incr;
				}
			}
		}
		 
		 this.key11Sol = offset_opt;
		 this.seqSol = incr_opt;		
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

	@Override
	public BufferedImage transform(BufferedImage img) {		
		img = this.convertToType(img, BufferedImage.TYPE_3BYTE_BGR);
		if (img.getWidth() != this.sWidth || img.getHeight() != 576) {
			img = this.getScaledImage(img, this.sWidth, 576);
		}
				
		raster = img.getRaster();
		createImageOddEven();
		computeSolution(imageOdd);
		decryptImgOdd();
		computeSolution(imageEven);
		decryptImgEven();
		return this.imgFinal;
	}
	
	
	private void decryptImgOdd() {
		// clear image
		if (key11Sol == -1 && seqSol == -1) {
			this.enable = false;
			WritableRaster raster2;
			raster2 = imgFinal.getRaster();
			raster = imageOdd.getRaster();

			int ligne = 0;

			for (int i = 0; i < 286; i++) {
				raster2.setPixels(0, ligne * 2,
						768, 1,
						raster.getPixels(0, i + 0, this.sWidth, 1,
								new int[this.sWidth * 3]));				
				ligne++;
			}
		}

		else {
			this.enable = true;
			WritableRaster raster2;
			raster2 = imgFinal.getRaster();
			raster = imageOdd.getRaster();

			int ligne = 0;

			for (int i = 0; i < 286; i++) {
				raster2.setPixels(decaPixels[delayArrayFull[key11Sol][seqSol][i]], ligne * 2,
						768 - decaPixels[delayArrayFull[key11Sol][seqSol][i]], 1,
						raster.getPixels(0, i + 0, this.sWidth - decaPixels[delayArrayFull[key11Sol][seqSol][i]], 1,
								new int[(this.sWidth - decaPixels[delayArrayFull[key11Sol][seqSol][i]]) * 3]));
				// draw black line at start of delay
				raster2.setPixels(0, ligne * 2, decaPixels[delayArrayFull[key11Sol][seqSol][i]], 1,
						new int[decaPixels[delayArrayFull[key11Sol][seqSol][i]] * 3]);
				ligne++;
			}
		}
	}
	
	private void decryptImgEven() {
		// clear image
		if (key11Sol == -1 && seqSol == -1) {
			this.enable = false;
			WritableRaster raster2;
			raster2 = imgFinal.getRaster();
			raster = imageEven.getRaster();

			int ligne = 0;

			for (int i = 0; i < 286; i++) {
				raster2.setPixels(0, ligne * 2 + 1,
						768, 1,
						raster.getPixels(0, i, this.sWidth, 1,
								new int[(this.sWidth) * 3]));
				// draw black line at start of delay
				raster2.setPixels(0, ligne * 2 + 1, 0, 1,
						new int[0 * 3]);
				ligne++;
			}			
			
		} else {
			this.enable = true;
			WritableRaster raster2;
			raster2 = imgFinal.getRaster();
			raster = imageEven.getRaster();

			int ligne = 0;

			for (int i = 0; i < 286; i++) {
				raster2.setPixels(decaPixels[delayArrayFull[key11Sol][seqSol][i]], ligne * 2 + 1,
						768 - decaPixels[delayArrayFull[key11Sol][seqSol][i]], 1,
						raster.getPixels(0, i, this.sWidth - decaPixels[delayArrayFull[key11Sol][seqSol][i]], 1,
								new int[(this.sWidth - decaPixels[delayArrayFull[key11Sol][seqSol][i]]) * 3]));
				// draw black line at start of delay
				raster2.setPixels(0, ligne * 2 + 1, decaPixels[delayArrayFull[key11Sol][seqSol][i]], 1,
						new int[decaPixels[delayArrayFull[key11Sol][seqSol][i]] * 3]);
				ligne++;
			}
		}
	}
	
	@Override
	public void skipFrame() {
		// TODO Auto-generated method stub		
	}
	
	/**
	 * clone a BufferedImage
	 * @param bi the BufferedImage to clone
	 * @return a cloned BufferedImage
	 */
	protected BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
	
	/**
	 * Scale an image to a new size
	 * @param src the image source
	 * @param w the new width
	 * @param h the new height
	 * @return the resized image
	 */
	protected BufferedImage getScaledImage(BufferedImage src, int w, int h){
	    int finalw = w;
	    int finalh = h;
	    double factor = 1.00d;
	    double shiftw = 1d;	 
	    
	    if(src.getWidth()==720 && src.getHeight()==576 ){
	    	shiftw = (double)src.getWidth()/(double)w; // case of if width = 720 and height = 576
	    }
	    
	    if(src.getWidth() > src.getHeight()){
	        factor = ((double)src.getHeight()/(double)src.getWidth());
	        finalh = (int)(finalw * factor * shiftw);                
	    }else{
	        factor = ((double)src.getWidth()/(double)src.getHeight());
	        finalw = (int)(finalh * factor  );
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
	protected  BufferedImage convertToType(BufferedImage sourceImage,
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
		return key11Sol;
	}

	@Override
	void closeFileData() {
		// TODO Auto-generated method stub
		
	}	

}
