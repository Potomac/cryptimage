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
 * 11 jan. 2024 Author Mannix54
 * http://ibsoftware.free.fr/cryptimage.php
 */

package com.ib.cryptimage.core.types;


public final class VideoCodecType {

	public static int H264 = 0;
	public static int MPEG2 = 1;
	public static int DIVX = 2;
	public static int HUFFYUV = 3;
	public static int H264V2 = 4;
	public static int FFV1 = 5;
	
	public static String[] getVideoCodecs() {
		return new String[] {"h264","mpeg2", "divx", "huffyuv", "h264 v2", "FFV1"};		
	}
	
	
}
