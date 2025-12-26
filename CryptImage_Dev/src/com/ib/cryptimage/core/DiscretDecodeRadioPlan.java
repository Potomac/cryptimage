package com.ib.cryptimage.core;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class DiscretDecodeRadioPlan extends Device {
    private int BLACK_THRESHOLD = 25; //25; 
    private int MAX_SEARCH_RANGE = 26; //60; 
    private int TOLERANCE = 4; // Marge d'erreur pour la classification //4    
    
    private int frameTot = 0;
    private int frameSelectedStart = 1;
    private int frameSelectedEnd = 20000;
    
    private String deviceName = "";
    
    WritableRaster rasterInput;
    WritableRaster rasterOutput;
    
    private FileWriter fileCSV;
    private boolean bLog;
    int delay = 0;

    private Shift clShift;
	private int iShiftX;
	private int iShiftY;	
    
	public DiscretDecodeRadioPlan(int frameSelectedStart, int frameSelectedEnd, String deviceName, boolean bLog){
		super();
		
		clShift = new Shift();		
		iShiftX = Integer.valueOf(JobConfig.getGui().getjShiftX().getValue().toString());
		iShiftY = Integer.valueOf(JobConfig.getGui().getjShiftY().getValue().toString());
		
		this.deviceName = deviceName;
		this.frameSelectedStart = frameSelectedStart;
		this.frameSelectedEnd = frameSelectedEnd;	
		
		if(bLog && !JobConfig.isWantPlay()) {
			this.bLog = true;
			
			try {
				fileCSV = new FileWriter(JobConfig.getOutput_file() + "_delays.csv");
				fileCSV.write("frame;line;delay in pixels" + "\r\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			this.bLog = false;
		}
	}	


	@Override
	public BufferedImage transform(BufferedImage img) {
		frameTot++;
		
		img = this.convertToType(img, BufferedImage.TYPE_3BYTE_BGR);
		
		if (img.getWidth() != 768 || img.getHeight() != 576) {
			img = this.getScaledImage(img, 768, 576);
		}
		
		JobConfig.setInputImage(img);
		
		//check iShiftX and iShiftY
		if(iShiftX != 0 || iShiftY !=0) {			
			img = clShift.transform(img, iShiftX, iShiftY);
		}
		
		if (frameSelectedStart <= frameTot && frameSelectedEnd >= frameTot) {	 
		
	        BufferedImage outputImage = new BufferedImage(img.getWidth(), 
	        		                                      img.getHeight(), img.getType());
	        rasterInput = img.getRaster();
	        rasterOutput = outputImage.getRaster();
	        
	        int width = img.getWidth();
	        int height = img.getHeight();

	        int[] measuredDelays = new int[height];
	        Map<Integer, Integer> histogram = new HashMap<>();

	        // --- PHASE 1 : ANALYSE DES LIGNES ---
	        for (int y = 0; y < height; y++) {
	            int blackLength = countLeadingBlackPixels(img, y);
	            measuredDelays[y] = blackLength;
	            if (blackLength > 5) { // On ignore les bruits de moins de 5px
	                histogram.put(blackLength, histogram.getOrDefault(blackLength, 0) + 1);
	            }
	        }

	        // --- PHASE 2 : IDENTIFICATION DES PICS STATISTIQUES ---
	        List<Map.Entry<Integer, Integer>> sortedPeaks = new ArrayList<>(histogram.entrySet());
	        sortedPeaks.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

	        int realShort = 13; 
	        int realLong = 26;

	        if (sortedPeaks.size() >= 2) {
	            // On identifie les deux valeurs les plus fréquentes
	            int p1 = sortedPeaks.get(0).getKey();
	            int p2 = sortedPeaks.get(1).getKey();
	            realShort = Math.min(p1, p2);
	            realLong = Math.max(p1, p2);
	        }

	        // Affichage des statistiques pour contrôle
//	        System.out.println("------ STATISTIQUES DE DÉTECTION ------");
//	        System.out.println("Retard Court détecté (Pic 1) : " + realShort + " px");
//	        System.out.println("Retard Long détecté  (Pic 2) : " + realLong + " px");
//	        System.out.println("---------------------------------------");

	        // --- PHASE 3 : TRAITEMENT ET LOGGING ---
	        
	        int countNone = 0, countShort = 0, countLong = 0;

	        for (int y = 0; y < height; y++) {
	            int current = measuredDelays[y];
	            int delayToAdd;

	            if (Math.abs(current - realLong) <= TOLERANCE) {
	                delayToAdd = 0;
	                delay = realLong;
	                countLong++;
	            } else if (Math.abs(current - realShort) <= TOLERANCE) {
	                delayToAdd = realLong - realShort;
	                delay = realShort;
	                countShort++;
	            } else {
	                delayToAdd = realLong;
	                delay = 0;
	                countNone++;
	            }
	            applyDelay(y, delayToAdd, width);
	            
	            if(bLog) {
	            	try {
						fileCSV.write(frameTot + ";" + (y + 1) + ";" + delay + "\r\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
	        }

//	        System.out.println("Répartition des lignes :");
//	        System.out.println("- Lignes sans retard : " + countNone);
//	        System.out.println("- Lignes retard court: " + countShort);
//	        System.out.println("- Lignes retard long : " + countLong);
	        
		    return outputImage;
		}
		else {
			return img;
		}              
	}

    private int countLeadingBlackPixels(BufferedImage img, int y) {
        int count = 0;
        while (count < MAX_SEARCH_RANGE && count < img.getWidth()) {
            int rgb = img.getRGB(count, y);
            int r = (rgb >> 16) & 0xFF;
            int g = (rgb >> 8) & 0xFF;
            int b = (rgb) & 0xFF;
            if (r < BLACK_THRESHOLD && g < BLACK_THRESHOLD && b < BLACK_THRESHOLD) count++;
            else break;
        }
        return count;
    }

    private void applyDelay(int y, int shift, int width) {
        rasterOutput.setPixels(shift, y, width - shift, 1,
	               rasterInput.getPixels(0, y, width - shift, 1, new int[width * 3]));
    }
	
	/**
	 * Convert a source image to a desired BufferedImage type
	 * @param sourceImage the image source
	 * @param targetType the target type
	 * @return a converted BufferedImage
	 */
	protected BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
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
	
	@Override
	public boolean isEnable() {
		return true;
	}

	@Override
	public int getAudienceLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getKey11bits() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void closeFileData() {
		// TODO Auto-generated method stub
		if(bLog) {
			try {
				fileCSV.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void skipFrame() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getKey() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isInsideRangeSliderFrames() {
		if(JobConfig.frameCount <= this.frameSelectedEnd) {
			return true;
		}
		else {
			return false;
		}	
	}

	@Override
	public String getDeviceName() {
		return this.deviceName;
	}
}
