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
 * 15 jan. 2024 Author Mannix54
 * http://ibsoftware.free.fr/cryptimage.php
 */

package com.ib.cryptimage.core.types;

import com.ib.cryptimage.core.JobConfig;

public class ColorType {
	
	public static int RGB = 0;
	public static int PAL = 1;
	public static int SECAM = 2;
	public static int PAL_COMPOSITE_ENC_DEC = 3;
	public static int PAL_COMPOSITE_ENC = 4;
	public static int PAL_COMPOSITE_DEC = 5;
	
	public static String[] getColors() {		
		return new String[]{JobConfig.getRes().getString("cbColorMode.rgb"),
			JobConfig.getRes().getString("cbColorMode.pal"),
			JobConfig.getRes().getString("cbColorMode.secam"),
			JobConfig.getRes().getString("cbColorMode.pal.composite.full"),
			JobConfig.getRes().getString("cbColorMode.pal.composite.encode"),
			JobConfig.getRes().getString("cbColorMode.pal.composite.decode")};
	}

}
