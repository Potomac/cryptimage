package com.ib.cryptimage.core.systems.discret12;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;


import com.ib.cryptimage.core.JobConfig;
import com.ib.cryptimage.core.types.SystemType;

public class Discret12DecCore extends Discret12Core{


	/**
	 * the queue for the state of the color of the 310 and 622 lines
	 */
	private Queue<String> queueLines622=new LinkedList<String>();
	
	/**
	 * string for storing the motif of a 310 and 622 lines on a frame
	 */
	private String motif622 ="";
	
	/**
	 * the position inside an dimension of 3 full frames
	 */
	private int indexPos = 0;
	
	private String motif310 = "";
	
	private Queue<String> queueLines310 = new LinkedList<String>();
	
	/**
	 * enable or disable the decoder
	 */
	protected boolean enable = false;
	
	protected boolean enableDecode = false;
	
	/**
	 * store the current audience level
	 */
	private int audienceLevel;
	
	/**
	 * store the number of frames that has been transformed by Discret11
	 */
	protected int currentframePos = 0;
	
	/**
	 * store the current position from the 6 frames ( half image ) sequence
	 */
	protected int seqFrame = 0;	
	
	private boolean synchro = false;
	
	/**
	 * the count iterator for delay array
	 */
	protected int cptArray = 0;	
	
	protected boolean start = true;
	
	/**
	 * store the current index for the 11 bits key
	 */
	protected int index11bitsKey;
	
	protected int saveIndex11bitsKey = -1;
	
	
	
/////////////////////////////////////////
	@Override
	public BufferedImage transform(BufferedImage image) {
		frameDiscret12++;
		image = this.convertToType(image, BufferedImage.TYPE_3BYTE_BGR);

		if (image.getWidth() != 768 || image.getHeight() != 576) {
			image = this.getScaledImage(image, 768, 576);
		}

		if (Discret12Conf.selectedFrameStart <= frameDiscret12 && Discret12Conf.selectedFrameEnd >= frameDiscret12) {

			/////
			frameShifted++;
			int z = getInitZ();

			z = getCurrentZ();

			if (this.seqFrame == 0) {
				image = setWhite310Line(image);
				if (this.saveIndexUse11bitsKey != -1 ){
					int saveKey = this.indexUse11bitsKey;
					this.indexUse11bitsKey = this.saveIndexUse11bitsKey;
					image = modifyOddFrame2(image, 1);
					cptArray = 0;
					this.indexUse11bitsKey = saveKey;
				}
				// we compute only the even part of
				// the image
				image = modifyEvenFrame(image, z);
				this.seqFrame++;
				setAudience622Line(image);

			} else { // we compute both the odd and even parts of the image (
						// impaire, paire )

				image = modifyOddFrame(image, z);
				this.seqFrame++;

				if (this.seqFrame == 6) {
					indexCycle += 1;
					if (indexCycle == 8) {
						this.enable = true;
					}

					if (discretRNG.getDiscretType() == SystemType.DISCRET11) {
						image = setWhite310Line(image);
					} else {
						image = setBlack310Line(image);
					}

					this.seqFrame = 0;
				} else {
					if (discretRNG.getDiscretType() == SystemType.DISCRET11) {
						image = setBlack310Line(image);
					} else {
						image = setWhite310Line(image);
					}
				}

				if (indexCycle == 8) {
					changeAudience();
				}

				image = modifyEvenFrame(image, z);
				this.seqFrame++;
				setAudience622Line(image);
			}

			return image;
			
		} else {
			
			if(this.seqFrame == 0) {
				this.seqFrame++;
			}
			else {
				this.seqFrame++;
				
				if (this.seqFrame == 6) {
					indexCycle += 1;					
					if (indexCycle == 8) {
						this.enable = true;
					}
					
					this.seqFrame = 0;				
				}
				
				if (indexCycle == 8) {
					changeAudience();
				}				
				this.seqFrame++;				
			}
			
			frameShifted = 0;
			return image;
		}
	}

	
	/**
	 * Transform the lines of the odd part of an image ( trame impaire )
	 * @param image
	 * @param z the z value for the delay array
	 * @return
	 */
	protected BufferedImage modifyOddFrame2(BufferedImage image, int z) {		
		if (enable) {		
			raster = image.getRaster();
			
			for (int y = 2; y < 576; y++) {
				if (cptArray == 286) {
					this.cptArray = 0;
				}

				if (y != 574) { // we don't increment if it's line 310 ( 575 in
					// digital image )		
					
					raster.setPixels(this.discretRNG.getDelayArray()[indexUse11bitsKey][5][cptArray], y, 
					         this.sWidth - this.discretRNG.getDelayArray()[indexUse11bitsKey][5][cptArray], 1, raster
					.getPixels(0, y, this.sWidth - this.discretRNG.getDelayArray()[indexUse11bitsKey][5][cptArray], 1,
							new int[(this.sWidth - this.discretRNG.getDelayArray()[indexUse11bitsKey][5][cptArray]) * 3]));
					
					// draw black line at start of delay
					drawLine(this.discretRNG.getDelayArray()[indexUse11bitsKey][5][cptArray], y);
					cptArray++;
				}
				y++; // add one to y in order to have only odd lines frame
			}
		}
		return image;
	}
	
	/**
	 * Transform the lines of the even part of an image ( trame paire )
	 * @param image
	 * @param z the z value for the delay array
	 * @return
	 */
	protected BufferedImage modifyEvenFrame(BufferedImage image, int z) {		
		if (enable) {			
			raster = image.getRaster();

			for (int y = 1; y < 576; y++) {
				if (cptArray == 286) {
					this.cptArray = 0;
				}				

				if (y != 573 && y != 575) { // we don't increment if next line
											// is 622 ( 574 in
					// digital image ) or if next line is 623 ( 576 in digital
					// image )					
					raster.setPixels(discretRNG.getDelayArray()[indexUse11bitsKey][this.seqFrame][cptArray], y,
					         this.sWidth - discretRNG.getDelayArray()[indexUse11bitsKey][this.seqFrame][cptArray], 1, raster
					.getPixels(0, y, this.sWidth - discretRNG.getDelayArray()[indexUse11bitsKey][this.seqFrame][cptArray], 1,
							new int[(this.sWidth - discretRNG.getDelayArray()[indexUse11bitsKey][this.seqFrame][cptArray]) * 3]));
					
					// draw black line at start of delay
					drawLine(discretRNG.getDelayArray()[indexUse11bitsKey][this.seqFrame][cptArray], y);
					cptArray++;
				}
				y++; // add one to y in order to have only even lines frame
			}
		}
		return image;
	}
	
	/**
	 * Transform the lines of the odd part of an image ( trame impaire )
	 * @param image
	 * @param z the z value for the delay array
	 * @return
	 */
	protected BufferedImage modifyOddFrame(BufferedImage image, int z) {
		if (enable) {
			raster = image.getRaster();			

			for (int y = 2; y < 576; y++) {
				if (cptArray == 286) {
					this.cptArray = 0;
				}
				

				if (y != 574) { // we don't increment if it's line 310 ( 575 in
					// digital image )					
					raster.setPixels(discretRNG.getDelayArray()[indexUse11bitsKey][this.seqFrame][cptArray], y, 
					         this.sWidth - discretRNG.getDelayArray()[indexUse11bitsKey][this.seqFrame][cptArray], 1, raster
					.getPixels(0, y, this.sWidth - discretRNG.getDelayArray()[indexUse11bitsKey][this.seqFrame][cptArray], 1,
							new int[(this.sWidth - discretRNG.getDelayArray()[indexUse11bitsKey][this.seqFrame][cptArray]) * 3]));
					
					// draw black line at start of delay	
					drawLine(discretRNG.getDelayArray()[indexUse11bitsKey][this.seqFrame][cptArray], y);
					cptArray++;
				}
				y++; // add one to y in order to have only odd lines frame
			}
		}
		return image;
	}	

	
	
	@Override
	public void closeFileData() {
		// TODO Auto-generated method stub
		
	}
	
	
///////////////////////////////////////
	
	private boolean isMotifSynchro310() {
		if(queueLines310.size() == 4) {
			int tot = 0;
			String testMotif = "110"; // "100"
			//check majoritaire
			for (int i = 0; i < queueLines310.size(); i++) {				
				if (queueLines310.toArray()[i].equals(testMotif)) {
					tot++;
				}
			}
			
			if(tot >= 3) {
				return true;
			}
			else {
				return false;
			}
		}
		return false;
	}
	
	
	protected void checkMotif(BufferedImage buff) {	
		int val310 = 0;
		if(is310WhiteLine(buff)) {
			val310 = 1;
		}
		
		updateMotifSynchro310(val310);
		//showMotifSynchro310();
		//showMotif622();


		if ( val310 == 1 && indexPos == 0 ) { //if (is310WhiteLine(buff) && indexPos == 0) {
			//System.out.println(indexPos + " ligne 310 blanche pos 1 ");
			//updateMotifSynchro310(val310);
			this.synchro = true;
			//showMotifSynchro310();			
		}
		
		if ( indexPos < 3 && synchro == true) {
			if (is622WhiteLine(buff) ) {
				motif622 = motif622 + "1";
				indexPos++;
			} else {
				if (is622BlackLine(buff)) {
					motif622 = motif622 + "0";
					indexPos++;
				} else {
					motif622 = motif622 + "2";
					//System.out.println("erreur motif 2");
					indexPos++;
				}
			}
		} 
		else {			
			if(synchro == false){
				if(!isMotifSynchro310()) {
					razMotif(); //desactivé
				}
				else {
					razMotif();
					cptArray = 0;	//desactive
					this.seqFrame = 0; //desactive
					this.start = true; //desactive
					this.currentframePos = 0; //desactive
					indexPos = 0; //desactive
					motif622 = "";
					this.queueLines622.clear(); //desactive
					motif310 = "";
					//this.queueLines310.clear();
					//this.synchro = true;
				}
			}
		}


		if (motif622.length() == 3 ) {			
			this.queueLines622.add(motif622);
			if (queueLines622.size() == 8) { //8
				//showMotif622();
				checkAudience();				
			}
			motif622 = "";
			indexPos = 0;
			this.synchro = false; //desactivé			
		}	
		
	}
	
	private void updateMotifSynchro310(int val310) {
		if(motif310.length() == 3) {			
			if(this.enableDecode) {
				queueLines310.add(motif310);				
			}
			motif310 = "";
			
			if(queueLines310.size() == 5) {
				queueLines310.remove();
			}
		}
		
		if(val310 == 1) {
			motif310 = motif310 + "1";
		}
		else if(val310 == 0 ) {
			motif310 = motif310 + "0";
		}
	
	}
		
	
	private void checkAudience() {				
		
		int total = 0;
		
		//String motif = "";

		for (int i = 0; i < queueLines622.size(); i++) {
			
			motif622 += queueLines622.toArray()[i] + ",";
			total = total + Integer.valueOf((String) queueLines622.toArray()[i]);
		}	
		
		
		if((double)(total)/8d - total/8 == 0 ){
			total = Integer.valueOf((String) queueLines622.toArray()[0]);
		}
		else
			total = -1;
		
		//this.queueLines.clear();
		
		switch (total) {
//		case 1:			
//			this.currentframePos = 0;
//			if(!enableDecode){
//				this.currentframePos = 0;
//			}
//			this.currentframePos = 0;
//			this.seqFrame = 0;
//			this.saveIndex11bitsKey = this.index11bitsKey;
//			this.audienceLevel = 1;
//			this.index11bitsKey = 0;
//			cptArray = 0;			
//			this.enableDecode = true;			
//			break;
//		case 10:			
//			this.currentframePos = 0;
//			if(!enableDecode){
//				this.currentframePos = 0;
//			}
//			this.currentframePos = 0;
//			this.seqFrame = 0;
//			this.saveIndex11bitsKey = this.index11bitsKey;
//			this.audienceLevel = 2;
//			this.index11bitsKey = 1;
//			cptArray = 0;			
//			this.enableDecode = true;
//			//this.seqFrame = 0;
//			//this.currentframePos = 0;			
//			break;
//		case 11:			
//			if(!enableDecode){
//				this.currentframePos = 0;
//			}
//			this.saveIndex11bitsKey = this.index11bitsKey;
//			this.currentframePos = 0;
//			this.seqFrame = 0;
//			this.audienceLevel = 3;
//			this.index11bitsKey = 2;
//			cptArray = 0;			
//			this.enableDecode = true;			
//			break;
//		case 100:			
//			if(!enableDecode){
//				this.currentframePos = 0;
//			}
//			this.currentframePos = 0;
//			this.seqFrame = 0;
//			this.saveIndex11bitsKey = this.index11bitsKey;
//			this.audienceLevel = 4;
//			this.index11bitsKey = 3;
//			cptArray = 0;			
//			this.enableDecode = true;					
//			break;
//		case 101:			
//			if(!enableDecode){
//				this.currentframePos = 0;
//			}
//			this.currentframePos = 0;
//			this.seqFrame = 0;
//			this.saveIndex11bitsKey = this.index11bitsKey;
//			this.audienceLevel = 5;
//			this.index11bitsKey = 4;
//			cptArray = 0;			
//			this.enableDecode = true;						
//			break;
//		case 110:			
//			if(!enableDecode){
//				this.currentframePos = 0;
//			}
//			this.currentframePos = 0;
//			this.seqFrame = 0;			
//			this.saveIndex11bitsKey = this.index11bitsKey;
//			this.audienceLevel = 6;
//			this.index11bitsKey = 5;
//			cptArray = 0;			
//			this.enableDecode = true;					
//			break;
		case 111:			
			this.currentframePos = 0;
			if(!enableDecode){
				this.currentframePos = 0;
			}
			this.currentframePos = 0;
			this.seqFrame = 0;
			this.saveIndex11bitsKey = this.index11bitsKey;
			this.audienceLevel = 7;
			this.index11bitsKey = 6; // 1337
			cptArray = 0;			
			this.enableDecode = true;					
			break;
		case 0: // clair
			this.currentframePos = 0;
			this.seqFrame = 0;
			this.saveIndex11bitsKey = this.index11bitsKey;
			this.audienceLevel = 0;
			//this.index11bitsKey = 6;
			cptArray = 0;			
			this.enableDecode = false;			
			break;
		default:
//			this.currentframePos = 0;
//			this.saveIndex11bitsKey = this.index11bitsKey;
//			this.index11bitsKey = this.saveIndex11bitsKey;
			//this.synchro = true;
			//this.enableDecode = true;
//			this.start = true;
//			this.currentframePos = 0;
//			this.seqFrame = 0;
//			cptArray = 0;
//			cptPoly = 0;
			break;
		}

		this.queueLines622.remove();

	}
	
	private boolean is310WhiteLine(BufferedImage buff) {
		int val, total = 0;	
		//Raster pix = buff.getData();
	    
		for (int i = 0; i < buff.getWidth(); i++) {
			val = buff.getRGB(i, 574);			
			total += (((val >>16 ) & 0xFF) + ((val >>8 ) & 0xFF) + (val & 0xFF))/3;
//			total += (pix.getPixel(i, 574, new int[3])[0]+
//					 pix.getPixel(i, 574, new int[3])[1]+
//					 pix.getPixel(i, 574,new int[3])[2])/3;
		}
		
		if ((total / Discret12Conf.width) >= 160) { //200  40 //80
			return true;
		} else {
			return false;
		}
	}	
	
	private boolean is310BlackLine(BufferedImage buff) {
		int val, total = 0;
//		Raster pix = buff.getData();
	    
		for (int i = 0; i < buff.getWidth(); i++) {
			val = buff.getRGB(i, 574);			
			total += (((val >>16 ) & 0xFF) + ((val >>8 ) & 0xFF) + (val & 0xFF))/3;
//			total += pix.getPixel(i, 574, new int[this.sWidth * 3])[0]+
//					 pix.getPixel(i, 574, new int[this.sWidth * 3])[1]+
//					 pix.getPixel(i, 574, new int[this.sWidth * 3])[2];
		}	

		if ((total / Discret12Conf.width) < 160) { //30 40 //80
			return true;
		} else {
			return false;
		}
	}	
	
	private boolean is622WhiteLine(BufferedImage buff){
		int val, total = 0;
//		Raster pix = buff.getData();
		
		for (int i = 0; i < buff.getWidth(); i++) {
			val = buff.getRGB(i, 573);			
			total += (((val >>16 ) & 0xFF) + ((val >>8 ) & 0xFF) + (val & 0xFF))/3;
//			total += pix.getPixel(i, 573, new int[this.sWidth * 3])[0]+
//					 pix.getPixel(i, 573, new int[this.sWidth * 3])[1]+
//					 pix.getPixel(i, 573, new int[this.sWidth * 3])[2];
		}		

		if ((total / Discret12Conf.width) >= 160) { //200  40 //80 //30
			return true;
		} else {
			return false;
		}		
	}
	
	private boolean is622BlackLine(BufferedImage buff){
		int val, total = 0;
//		Raster pix = buff.getData();
		
		for (int i = 0; i < buff.getWidth(); i++) {
			val = buff.getRGB(i, 573);		
			total += (((val >>16 ) & 0xFF) + ((val >>8 ) & 0xFF) + (val & 0xFF))/3;
//			total += pix.getPixel(i, 573, new int[this.sWidth * 3])[0]+
//					 pix.getPixel(i, 573, new int[this.sWidth * 3])[1]+
//					 pix.getPixel(i, 573, new int[this.sWidth * 3])[2];
		}	

		if ((total / Discret12Conf.width) < 160 ) { //30 40 //80
			return true;
		} else {
			return false;
		}		
	}
	
	private void razMotif(){	
		//this.synchro = true;
		// fix end of encryption
		this.enableDecode = false; //desactive
		cptArray = 0;	//desactive
		this.seqFrame = 0; //desactive
		this.start = true; //desactive
		this.currentframePos = 0; //desactive
		indexPos = 0; //desactive
		motif622 = "";
		this.queueLines622.clear(); //desactive
		motif310 = "";
		//this.queueLines310.clear();
		//this.saveIndex11bitsKey = this.index11bitsKey;
	}
	
}
