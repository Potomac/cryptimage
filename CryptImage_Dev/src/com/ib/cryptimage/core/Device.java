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

/**
 * @author Mannix54
 *
 */
public abstract class Device {		
public	abstract BufferedImage transform(BufferedImage img);
public	abstract boolean isEnable();
public	abstract int getAudienceLevel();
public	abstract int getKey11bits();
public	abstract void closeFileData();
public	abstract void skipFrame();
public	abstract int getKey();
public	abstract String getDeviceName();

}
