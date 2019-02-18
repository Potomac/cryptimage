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
 * 9 févr. 2019 Author Mannix54
 */



package com.ib.cryptimage.core;

/**
 * @author Mannix54
 *
 */
public class Discret11FindKey {
	
	String audience1 = "11111111111";
	String audience2 = "11111111111";
	String audience3 = "11111111111";
	String audience4 = "11111111111";
	String audience5 = "11111111111";
	String audience6 = "11111111111";
	String key16bits = "0000000000000000";
	
	
	/**
	 * store the 7 11 bit words according to the audience level
	 */
	private int[] key11BitsTab = new int[7];	
	
	/**
	 * 
	 */
	public Discret11FindKey() {
		initTabKey11bits();
	}
	
	private void initTabKey11bits() {
		key11BitsTab[0] = Integer.parseInt(audience1,2);
		key11BitsTab[1] = Integer.parseInt(audience2,2);
		key11BitsTab[2] = Integer.parseInt(audience3,2);
		key11BitsTab[3] = Integer.parseInt(audience4,2);
		key11BitsTab[4] = Integer.parseInt(audience5,2);
		key11BitsTab[5] = Integer.parseInt(audience6,2);
		key11BitsTab[6] = 1337;
	}
	
	private void rebuildKey16bitsFromAudience(int audience) {
		switch (audience) {
		case 1:
			 key16bits = audience1 + key16bits.substring(11, 16);
			break;
		case 2:
			key16bits = key16bits.substring(0, 3) + audience2 + key16bits.substring(14, 16);;
			break;
		case 3:
			key16bits = audience3.charAt(10) + key16bits.substring(1, 6) +  audience3.substring(0,10);
			break;
		case 4:
			key16bits = audience4.substring(7,11) + key16bits.substring(4, 9) + audience4.substring(0, 7);
			break;
		case 5:
			key16bits = audience5.substring(4, 11) + key16bits.substring(7, 12) + audience5.substring(0, 4);
			break;
		case 6:
			key16bits = audience6.substring(1,11) + key16bits.substring(10, 15) + audience6.charAt(0);
			break;	
		}
		rebuildAudiences();
	}
	
	/**
	 * initialize the key11BitsTab
	 * @param key16bits the 16 bits keyword
	 */
	private void rebuildAudiences(){		
		
		//audience 1
		audience1 = key16bits.substring(0, 11);
		this.key11BitsTab[0] = Integer.parseInt(audience1,2);
		
		//audience 2
		audience2 = key16bits.substring(3, 14);
		this.key11BitsTab[1] = Integer.parseInt(audience2,2);
		
		//audience 3
		audience3 = key16bits.substring(6, 16) + key16bits.charAt(0);
		this.key11BitsTab[2] = Integer.parseInt(audience3,2);
		
		//audience 4
		audience4 =  key16bits.substring(9, 16) + key16bits.substring(0, 4);
		this.key11BitsTab[3] = Integer.parseInt(audience4,2);
		
		//audience 5
		audience5 = key16bits.substring(12, 16) +  key16bits.substring(0, 7);
		this.key11BitsTab[4] = Integer.parseInt(audience5,2);
		
		//audience 6
		audience6 =  key16bits.charAt(15) + key16bits.substring(0, 10) ;
		this.key11BitsTab[5] = Integer.parseInt(audience6,2);
		
		//audience 7		
		this.key11BitsTab[6] = 1337;
		
	}

	public int getAudience1() {
		return Integer.parseInt(audience1,2);
	}

	public void setAudience1(int val) {
		this.audience1 = String.format
				("%11s", Integer.toBinaryString(val)).replace(" ", "0");
		rebuildKey16bitsFromAudience(1);
	}

	public int getAudience2() {
		return Integer.parseInt(audience2,2);
	}

	public void setAudience2(int val) {
		this.audience2 = String.format
				("%11s", Integer.toBinaryString(val)).replace(" ", "0");
		rebuildKey16bitsFromAudience(2);
	}

	public int getAudience3() {
		return Integer.parseInt(audience3,2);
	}

	public void setAudience3(int val) {
		this.audience3 = String.format
				("%11s", Integer.toBinaryString(val)).replace(" ", "0");
		rebuildKey16bitsFromAudience(3);
	}

	public int getAudience4() {
		return Integer.parseInt(audience4,2);
	}

	public void setAudience4(int val) {
		this.audience4 = String.format
				("%11s", Integer.toBinaryString(val)).replace(" ", "0");
		rebuildKey16bitsFromAudience(4);
	}

	public int getAudience5() {
		return Integer.parseInt(audience5,2);
	}

	public void setAudience5(int val) {
		this.audience5 = String.format
				("%11s", Integer.toBinaryString(val)).replace(" ", "0");
		rebuildKey16bitsFromAudience(5);
	}

	public int getAudience6() {
		return Integer.parseInt(audience6,2);
	}

	public int getAudience7() {
		return 1337;
	}
	
	public void setAudience6(int val) {
		this.audience6 = String.format
				("%11s", Integer.toBinaryString(val)).replace(" ", "0");
		rebuildKey16bitsFromAudience(6);
	}

	public int getKey16bits() {
		return Integer.parseInt(key16bits,2);
	}
	
	public int getKey16bitsReverseInt() {
		String word = new StringBuilder(key16bits).reverse().toString();
		return Integer.parseInt(word,2);
	}

}
