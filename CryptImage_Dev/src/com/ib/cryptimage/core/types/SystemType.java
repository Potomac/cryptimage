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
 * 9 jan. 2024 Author Mannix54
 * http://ibsoftware.free.fr/cryptimage.php
 */

package com.ib.cryptimage.core.types;

public final class SystemType {
	
	public static int DISCRET11 = 0;
	public static int SYSTER = 1;
	public static int VIDEOCRYPT = 2;
	public static int TRANSCODE = 3;
	
	public static String[] getSystems() {
		return new String[] {"Discret11", "Nagravision syster", "Videocrypt", "Transcode"};
	}

}
