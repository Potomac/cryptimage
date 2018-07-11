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
 * 9 janv. 2015 Author Mannix54
 * http://ibsoftware.free.fr/cryptimage.php
 */



package com.ib.cryptimage.core;

import java.awt.image.BufferedImage;

/**
 * @author Mannix54
 *
 */
public abstract class Discret extends Device {

	abstract BufferedImage transform(BufferedImage img);
	abstract boolean isEnable();
	abstract int getAudienceLevel();
	abstract int getKey11bits();
	
	protected int shiftX;
	protected int shiftY;
	
	protected Shift shift;
	
	protected PalEncoder palEncoder;
	protected PalDecoder palDecoder;
	
	public Discret() {
		int typeGrid = 0;
		int freq = 0;
		freq = (int)JobConfig.getGui().getCmbPalFreq().getSelectedItem();
		
		palEncoder = new PalEncoder(false, typeGrid);
		palDecoder = new PalDecoder(freq);
		JobConfig.setCurrentPalFrame(0);
		JobConfig.setPalDecoder(palDecoder);
		JobConfig.setPalEncoder(palEncoder);
	}
	
	public BufferedImage decodePal(BufferedImage img) {
		//decode pal image composite
		if (JobConfig.getColorMode() == 3 || JobConfig.getColorMode() == 5 ) {
			palDecoder.setImage(img);
			return palDecoder.decode();
		}
		else return img;
	}
	
	public BufferedImage encodePal(BufferedImage img) {
		//decode pal image composite
		if (JobConfig.getColorMode() == 3 || JobConfig.getColorMode() == 4 ) {
			palEncoder.setImage(img);
			return palEncoder.encode(false);
		}
		else return img;
	}
	
}
