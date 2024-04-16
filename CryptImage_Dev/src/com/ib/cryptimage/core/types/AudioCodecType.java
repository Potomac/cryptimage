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

public class AudioCodecType {

	public static int MP3_96KBS = 0;
	public static int MP3_128KBS = 1;
	public static int MP3_160KBS = 2;
	public static int MP3_192KBS = 3;
	public static int MP3_224KBS = 4;
	public static int MP3_320KBS = 5;
	public static int WAV = 6;
	public static int FLAC = 7;
	
	public static String[] getAudioCodecs() {
		return new String[] {"mp3 96 kbs","mp3 128 kbs","mp3 160 kbs",
				"mp3 192 kbs","mp3 224 kbs","mp3 320 kbs","wav (mkv)", "flac (mkv)"};
	}
	
}
