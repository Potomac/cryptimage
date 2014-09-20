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
 * 18 sept. 2014 Author Mannix54
 */




package com.ib.cryptimage.core;

public class Lfsr {
	// declare instance variables
	private int N; // number of bits in the LFSR
	private int[] reg; // reg[i] = ith bit of LFSR, reg[0] is rightmost bit
	private int tap; // index of the tap bit
	private String _seed;

	// constructor to create LFSR with the given initial seed and tap
	public Lfsr(String seed, int t) {
		this._seed = new String(seed);
		N = seed.length();
		tap = t;
		reg = new int[N];
		int j = N - 1;
		for (int i = 0; i < N; i++) {
			if (seed.charAt(i) == '0') {
				reg[j] = 0;
			} else {
				reg[j] = 1;
			}
			j--;
		}
	}

	public void resetLFSR(){	
		reg = new int[N];
		int j = N - 1;
		for (int i = 0; i < N; i++) {
			if (this._seed.charAt(i) == '0') {
				reg[j] = 0;
			} else {
				reg[j] = 1;
			}
			j--;
		}
	}
	
	// simulate one step and return the new bit as 0 or 1
	public int step() {
		int new_bit = reg[tap-1] ^ reg[N - 1];
		for (int i = N - 1; i > 0; i--) {
			reg[i] = reg[i - 1];
		}
		reg[0] = new_bit;
		return new_bit;
	}
	

	// simulate k steps and return k-bit integer
	public int generate(int k) {
		int x = 0;
		for (int i = 0; i < k; i++) {
			int new_bit = this.step();
			x = x * 2 + new_bit;
		}
		//resetLFSR();
		return x;
	}

	// return a string representation of the LFSR
	public String toString() {
		String s = "";
		for (int i = N - 1; i >= 0; i--) {
			if (reg[i] == 0) {
				s = s.concat("0");
			} else {
				s = s.concat("1");
			}
		}
		return s;
	}
}
