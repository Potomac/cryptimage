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
 * 2024-01-18 Author Mannix54
 * http://ibsoftware.free.fr/cryptimage.php
 */

package com.ib.cryptimage.core.systems.eurocrypt;

public final class EurocryptConf {
	
	public static boolean isEncodeMac = true;
	public static boolean isEncodeMacDecode576pNoEurocrypt = false;
	public static boolean isDecodeMac = false;
	
	public static boolean isDisableEurocrypt = false;
	public static boolean isEurocryptSingleCut = true;
	public static boolean isEurocryptDoubleCut = false;
	
	public static String seedCode = "12345678";
	
	private static EurocryptGui gui;
	
	public static EurocryptGui getGui() {
		return gui;
	}
	
	public static void setGui(EurocryptGui eurocryptGui) {
		gui = eurocryptGui;
	}
	public static int width = 1344;
	public static int height = 576;

}
