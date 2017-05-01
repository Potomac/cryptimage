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
 * 1 déc. 2014 Author Mannix54
 * http://ibsoftware.free.fr/cryptimage.php
 */



package com.ib.cryptimage.core;


/**
 * @author Mannix54
 *
 */
public class SoundCrypt {
	
	private double rate;
	private double w = 0;
	private double m_dDeccte = 0;
	private double m_dDecy1 = 0;
	private double m_dDecy0 = 0;
	private double m_nGain =1;		
	private double decy;
	private boolean dec;
	private int sampleCount = 0;
	//private boolean initButterWorthChorus = false;
	private boolean initChebyChorus = false;
	private boolean initChebyChorus2 = false;
	

	
//	//low pass butterWorth Chorus2
//	private int nzeros_butterChorus;
//    private int npoles_butterChorus;
//	private double gain_buttterChorus;
//	private double[] xv_butterChorus;
//	private double[] yv_butterChorus;
	
	//init cheby chorus
	private int nzeros_chebyChorus;
    private int npoles_chebyChorus;
	private double gain_chebyChorus;
	private double[] xv_chebyChorus;
	private double[] yv_chebyChorus;
	
	private int nzeros_chebyChorus2;
    private int npoles_chebyChorus2;
	private double gain_chebyChorus2;
	private double[] xv_chebyChorus2;
	private double[] yv_chebyChorus2;
	
	
	/**
	 * 
	 * @param rate rate in hertz for the samples
	 * @param dec true if decoding mode
	 */
	public SoundCrypt(int rate, boolean dec, int ampEnc, int ampDec){		
		this.dec = dec;
		if(this.dec == true){
			m_nGain = ampDec; //3
		}
		else{
			m_nGain = ampEnc; //1
		}
		this.rate = rate;
		
		initCrypt();	  			
	}
	
	private void initCrypt(){		
		w = 2 * Math.PI * 12800d/rate;
		m_dDeccte = 2.0 * Math.cos(w);
		
		m_dDecy0 = Math.cos(2 * w );
		m_dDecy1 = Math.cos(w);		
	
		sampleCount = 0;
	}
	
	public double[] transform(double[] sound, boolean enable) {

		if (this.dec) {
			if (this.rate == 44100) {				
				sound = lowPassChebyShevChorus44100_pre(sound);
			} else {
				sound = lowPassChebyShevChorus48000_pre(sound);
			}
		}
//		else
//			if (this.rate == 44100) {				
//				sound = lowPassChebyShevChorus44100_post(sound);
//			} else {
//				sound = lowPassChebyShevChorus48000_post(sound);
//			}
		
		sound = crypt(sound, enable);

		if (this.dec) {
			if (this.rate == 44100) {				
				sound = lowPassChebyShevChorus44100_post(sound);
			} else {
				sound = lowPassChebyShevChorus48000_post(sound);
			}
		}
//		else
//			if (this.rate == 44100) {				
//				sound = lowPassChebyShevChorus44100_pre(sound);
//			} else {
//				sound = lowPassChebyShevChorus48000_pre(sound);
//			}
		
		return sound;
	}
	
	private double[] crypt(double[] sound, boolean enable) {
	
			for (int i = 0; i < sound.length; i++) {
				decy = (m_dDeccte * m_dDecy1) - m_dDecy0;
				m_dDecy0 = m_dDecy1;
				m_dDecy1 = decy;

				if (enable) {
					sound[i] = (sound[i] * decy) * m_nGain;
				}

				sampleCount++;

				if (sampleCount == this.rate) {
					m_dDecy0 = Math.cos(2 * w);
					m_dDecy1 = Math.cos(w);
					sampleCount = 0;
				}
			}				
		return sound;
	}

	/**
	 * filter for 44100 Hz
	 * @param sound
	 * @return
	 */
	private double[] lowPassChebyShevChorus44100_pre(double[] sound) {
		// init
		if (this.initChebyChorus == false) {
			nzeros_chebyChorus = 4;
			npoles_chebyChorus = 4;
			gain_chebyChorus = 6.310833714e+00;
			xv_chebyChorus = new double[nzeros_chebyChorus + 1];
			yv_chebyChorus = new double[npoles_chebyChorus + 1];
			this.initChebyChorus = true;
		}

		for (int i = 0; i < sound.length; i++) {
			double input = sound[i];
			xv_chebyChorus[0] = xv_chebyChorus[1];
			xv_chebyChorus[1] = xv_chebyChorus[2];
			xv_chebyChorus[2] = xv_chebyChorus[3];
			xv_chebyChorus[3] = xv_chebyChorus[4];

			xv_chebyChorus[4] = input / gain_chebyChorus;

			yv_chebyChorus[0] = yv_chebyChorus[1];
			yv_chebyChorus[1] = yv_chebyChorus[2];
			yv_chebyChorus[2] = yv_chebyChorus[3];
			yv_chebyChorus[3] = yv_chebyChorus[4];

			yv_chebyChorus[4] = (xv_chebyChorus[0] + xv_chebyChorus[4]) + 4 * (xv_chebyChorus[1] + xv_chebyChorus[3])
					+ 6 * xv_chebyChorus[2] + (-0.0622354481 * yv_chebyChorus[0]) + (-0.0826699057 * yv_chebyChorus[1])
					+ (-0.7552376542 * yv_chebyChorus[2]) + (-0.6351796968 * yv_chebyChorus[3]);

			sound[i] = yv_chebyChorus[4];
		}
		return sound;

	}
	
	private double[] lowPassChebyShevChorus44100_post(double[] sound) {
		// init
		if (this.initChebyChorus2 == false) {
			nzeros_chebyChorus2 = 7;
			npoles_chebyChorus2 = 7;
			gain_chebyChorus2 = 2.211596484e+02;
			xv_chebyChorus2 = new double[nzeros_chebyChorus2 + 1];
			yv_chebyChorus2 = new double[npoles_chebyChorus2 + 1];
			this.initChebyChorus2 = true;
		}

		for (int i = 0; i < sound.length; i++) {
			double input = sound[i];
			xv_chebyChorus2[0] = xv_chebyChorus2[1];
			xv_chebyChorus2[1] = xv_chebyChorus2[2];
			xv_chebyChorus2[2] = xv_chebyChorus2[3];
			xv_chebyChorus2[3] = xv_chebyChorus2[4];
			xv_chebyChorus2[4] = xv_chebyChorus2[5];
			xv_chebyChorus2[5] = xv_chebyChorus2[6];
			xv_chebyChorus2[6] = xv_chebyChorus2[7];
			xv_chebyChorus2[7] = input / gain_chebyChorus2;
			yv_chebyChorus2[0] = yv_chebyChorus2[1];
			yv_chebyChorus2[1] = yv_chebyChorus2[2];
			yv_chebyChorus2[2] = yv_chebyChorus2[3];
			yv_chebyChorus2[3] = yv_chebyChorus2[4];
			yv_chebyChorus2[4] = yv_chebyChorus2[5];
			yv_chebyChorus2[5] = yv_chebyChorus2[6];
			yv_chebyChorus2[6] = yv_chebyChorus2[7];
			yv_chebyChorus2[7] = (xv_chebyChorus2[0] + xv_chebyChorus2[7])
					+ 7 * (xv_chebyChorus2[1] + xv_chebyChorus2[6]) + 21 * (xv_chebyChorus2[2] + xv_chebyChorus2[5])
					+ 35 * (xv_chebyChorus2[3] + xv_chebyChorus2[4]) + (0.0935164959 * yv_chebyChorus2[0])
					+ (-0.4657366789 * yv_chebyChorus2[1]) + (1.2531881677 * yv_chebyChorus2[2])
					+ (-2.2854956347 * yv_chebyChorus2[3]) + (2.9413622606 * yv_chebyChorus2[4])
					+ (-2.9346526015 * yv_chebyChorus2[5]) + (1.8190505669 * yv_chebyChorus2[6]);
			sound[i] = yv_chebyChorus2[7];
		}
		return sound;
	}
	
	
	/**
	 * filter for 48000 hz case
	 * @param sound
	 * @return
	 */
	private double[] lowPassChebyShevChorus48000_pre(double[] sound) {
		// init
		if (this.initChebyChorus == false) {
			nzeros_chebyChorus = 4;
			npoles_chebyChorus = 4;
			gain_chebyChorus = 8.183340972e+00;
			xv_chebyChorus = new double[nzeros_chebyChorus + 1];
			yv_chebyChorus = new double[npoles_chebyChorus + 1];
			this.initChebyChorus = true;
		}

		for (int i = 0; i < sound.length; i++) {
			double input = sound[i];
			xv_chebyChorus[0] = xv_chebyChorus[1];
			xv_chebyChorus[1] = xv_chebyChorus[2];
			xv_chebyChorus[2] = xv_chebyChorus[3];
			xv_chebyChorus[3] = xv_chebyChorus[4];

			xv_chebyChorus[4] = input / gain_chebyChorus;

			yv_chebyChorus[0] = yv_chebyChorus[1];
			yv_chebyChorus[1] = yv_chebyChorus[2];
			yv_chebyChorus[2] = yv_chebyChorus[3];
			yv_chebyChorus[3] = yv_chebyChorus[4];
			yv_chebyChorus[4] = (xv_chebyChorus[0] + xv_chebyChorus[4]) + 4 * (xv_chebyChorus[1] + xv_chebyChorus[3])
					+ 6 * xv_chebyChorus[2] + (-0.0626606278 * yv_chebyChorus[0]) + (0.0379954621 * yv_chebyChorus[1])
					+ (-0.6759257285 * yv_chebyChorus[2]) + (-0.2546007635 * yv_chebyChorus[3]);
			sound[i] = yv_chebyChorus[4];
		}

		return sound;
	}
		
	private double[] lowPassChebyShevChorus48000_post(double[] sound) {
		// init
		if (this.initChebyChorus2 == false) {
			nzeros_chebyChorus2 = 7;
			npoles_chebyChorus2 = 7;
			gain_chebyChorus2 = 3.834721196e+02;
			xv_chebyChorus2 = new double[nzeros_chebyChorus2 + 1];
			yv_chebyChorus2 = new double[npoles_chebyChorus2 + 1];
			this.initChebyChorus2 = true;
		}

		for (int i = 0; i < sound.length; i++) {
			double input = sound[i];
			xv_chebyChorus2[0] = xv_chebyChorus2[1];
			xv_chebyChorus2[1] = xv_chebyChorus2[2];
			xv_chebyChorus2[2] = xv_chebyChorus2[3];
			xv_chebyChorus2[3] = xv_chebyChorus2[4];
			xv_chebyChorus2[4] = xv_chebyChorus2[5];
			xv_chebyChorus2[5] = xv_chebyChorus2[6];
			xv_chebyChorus2[6] = xv_chebyChorus2[7];
			
			xv_chebyChorus2[7] = input / gain_chebyChorus2;
			
			yv_chebyChorus2[0] = yv_chebyChorus2[1];
			yv_chebyChorus2[1] = yv_chebyChorus2[2];
			yv_chebyChorus2[2] = yv_chebyChorus2[3];
			yv_chebyChorus2[3] = yv_chebyChorus2[4];
			yv_chebyChorus2[4] = yv_chebyChorus2[5];
			yv_chebyChorus2[5] = yv_chebyChorus2[6];
			yv_chebyChorus2[6] = yv_chebyChorus2[7];
			yv_chebyChorus2[7] = (xv_chebyChorus2[0] + xv_chebyChorus2[7]) + 7 * (xv_chebyChorus2[1] + xv_chebyChorus2[6])
					+ 21 * (xv_chebyChorus2[2] + xv_chebyChorus2[5]) + 35 * (xv_chebyChorus2[3] + xv_chebyChorus2[4])
					+ (0.1130488058 * yv_chebyChorus2[0]) + (-0.6208360469 * yv_chebyChorus2[1])
					+ (1.7554209098 * yv_chebyChorus2[2]) + (-3.2416570827 * yv_chebyChorus2[3])
					+ (4.1799016509 * yv_chebyChorus2[4]) + (-3.9042013855 * yv_chebyChorus2[5])
					+ (2.3845309549 * yv_chebyChorus2[6]);
			sound[i] = yv_chebyChorus2[7];
		}
		return sound;
	}
	
}
