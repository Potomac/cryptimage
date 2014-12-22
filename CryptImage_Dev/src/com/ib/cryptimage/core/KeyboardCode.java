/**
 * This file is part of	CryptImage_Dev.
 *
 * CryptImage_Dev is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * CryptImage_Dev is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with CryptImage_Dev.  If not, see <http://www.gnu.org/licenses/>
 * 
 * 21 déc. 2014 Author Mannix54
 */



package com.ib.cryptimage.core;

/**
 * @author Mannix54
 *
 */
public class KeyboardCode {

	private short[] nu = new short[8];

	private int ret, masq, cletra, codeutil;
	private int af, cle;
	private boolean[] autorisation = new boolean[6];

	private short  j;

	private short[] table1 = { 0x08, 0x02, 0x0c, 0x06, 0x00, 0x0f, 0x0e, 0x04,
			0x03, 0x0d, 0x09, 0x01, 0x05, 0x0a, 0x07, 0x0b };

	/**
	 * constructor
	 * @param serial the serial number of the decoder
	 * @param key16bits the 16 bits keyword
	 * @param autorisation the array of the 6 autorisation levels
	 */
	public KeyboardCode(int serial, int key16bits, boolean[] autorisation) {

		this.autorisation = autorisation;
		this.cle = key16bits;

		String key = String.format("%8s",serial);
		key = key.replace(" ","0");
		System.out.println("key length " + key.length() + " " + key);

		for (int i = 0; i < 8; i++) {	
				nu[i] = Short.valueOf(String.valueOf(key.charAt(i)));
			}
		}

	

	private void galois() {

		for (int i = 0; i < j; i++) {
			ret = (af >> 15) & 0x1;
			af = (af << 1) & 0xfffe;
			if (ret == 1) {
				af = af ^ 0x3000;
			}
		}
	}

	/**
	 * compute the keyboard code
	 * @return the 8 digits key code
	 */
	public String getKeyboardCode() {

		int crc, nut, ptn, pte, a, b, d;

		int niv = 0;		
		
		if (this.autorisation[5] == true) {
			niv = niv | 0x20;
		}
		if (this.autorisation[4] == true) {
			niv = niv | 0x10;
		}
		if (this.autorisation[3] == true) {
			niv = niv | 0x8;
		}
		if (this.autorisation[2] == true) {
			niv = niv | 0x4;
		}
		if (this.autorisation[1] == true) {
			niv = niv | 0x2;
		}
		if (this.autorisation[0] == true) {
			niv = niv | 0x1;
		}

		this.codeutil = (cle << 8) + (niv << 2);

		for (int i = 0; i < 5; i++) {
			cletra = codeutil & 0xfffffc;
			ptn = 0;

			for (int j = 0; j < 22; j++) {
				nut = nu[(ptn & 0x7)];
				masq = (1 << (23 - nut)) | 0x800000;
				masq = 0xffffff ^ masq;
				a = ((cletra >> (23)) & 1);
				b = ((cletra >> (23 - nut)) & 1);
				cletra = cletra & masq;

				masq = (a << (23 - nut)) | (b << (23));
				cletra = cletra | masq;

				pte = (cletra >> 8) & 0xf;
				d = (table1[pte]);
				masq = 0xfff0fc;
				cletra = cletra & masq;
				masq = (d << 8);
				cletra = cletra | masq;

				cletra = (cletra << 1) | (b << 2);
				codeutil = cletra & 0xfffffc;
				ptn++;
			}
		}

		// calcul crc4
		af = (cletra >> 8) & 0xffff;
		j = 8;
		galois();
		af = (af & 0xff00) | (cletra & 0xff);
		j = 8;
		galois();
		a = (af >> 8) & 0xff;
		af = (a << 8) | a;
		j = 2;
		galois();
		crc = (af >> 12) & 0xf;

		cletra = (codeutil * 4) + crc;

		return String.format("%08d", cletra);
	}
}
