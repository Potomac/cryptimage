package com.ib.cryptimage.core.systems.discret12;

import java.awt.image.BufferedImage;

import com.ib.cryptimage.core.types.SystemType;

public class Discret12EncCore extends Discret12Core {

	
	@Override
	public BufferedImage transform(BufferedImage image) {
		frameDiscret12++;
		image = this.convertToType(image, BufferedImage.TYPE_3BYTE_BGR);
		
		if (image.getWidth() != 768 || image.getHeight() != 576) {
			image = this.getScaledImage(image, 768, 576);
		}
		
		if(Discret12Conf.selectedFrameStart <= frameDiscret12
			&& Discret12Conf.selectedFrameEnd >= frameDiscret12) {
			frameShifted++;		
			
			
			int z = getInitZ();

			z = getCurrentZ();

			if (this.seqFrame == 0) {				
				image = setWhite310Line(image);	
				if (this.saveIndexUse11bitsKey != -1 ) {					
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
					indexCycle +=1;	
					if(indexCycle == 8) {
						this.enable = true;
					}
					
					if(discretRNG.getDiscretType() == SystemType.DISCRET11) {
						image = setWhite310Line(image);
					}
					else {
						image = setBlack310Line(image);
					}					
		
					this.seqFrame = 0;					
				}
				else{
					if(discretRNG.getDiscretType() == SystemType.DISCRET11) {
						image = setBlack310Line(image);
					}
					else {
						image = setWhite310Line(image);
					}				
				}
				
				if(indexCycle ==  8  ){					
					changeAudience();
				}		
				

				image = modifyEvenFrame(image, z);
				this.seqFrame++;
				setAudience622Line(image);
			}
			
			return image;		
		}
		else {
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

			//int temp2 = 0;

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
	
	
}
