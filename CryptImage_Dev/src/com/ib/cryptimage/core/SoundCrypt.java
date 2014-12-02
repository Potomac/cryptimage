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
 * 1 déc. 2014 Author Mannix54
 */



package com.ib.cryptimage.core;

import java.lang.Math;

/**
 * @author Mannix54
 *
 */
public class SoundCrypt {
	
	private int rate;
	private int pitch;	
	private double[] m_fPostXV = new double[8];
	private double[] m_fPostYV = new double[8];
	private double[] m_fPreXV = new double[5];
	private double[] m_fPreYV = new double[5];
	private double w = 0;
	private double m_dDeccte = 0;
	private double m_dDecy1 = 0;
	private double m_dDecy0 = 0;
	private double[] coeff_pre = new double[5];
	private double[] coeff_post = new double[8];
	private int m_nPitch;
	private int m_nPrefilter=0;
	private int m_nPostfilter=0;
	private double m_nGain = 1;
	private int boost;
	private double b = Math.PI / 2d;
	private double decy;

	/**
	 * 
	 * @param rate rate in hertz for the samples
	 * @param dec true if decoding mode
	 */
	public SoundCrypt(int rate, boolean dec){
		if(dec == true){
			m_nPrefilter = 1;
			m_nPostfilter = 1;
		}
		initFilterParams(rate);	
	}
	
	private double[] ZeroMemory(double[] tab) {
		for (int i = 0; i < tab.length; ++i) {
			tab[i] = 0;
		}
		return tab;
	}
	
	private void initFilterParams(int soundrate) {
		// Init the variable for the sound processing
		rate = soundrate;
		pitch = 0;
		m_nPitch = pitch;
		
		m_fPostXV = ZeroMemory(m_fPostXV);
		m_fPostYV = ZeroMemory(m_fPostYV);
		m_fPreXV = ZeroMemory(m_fPreXV);
		m_fPreYV = ZeroMemory(m_fPreYV);

		boost = (m_nPrefilter == 2) ? 3 : 2;
		w = 2d * Math.PI * 12800d / ((double) (rate + m_nPitch));
//		m_dDeccte = 2.0 * Math.cos(w);
//		m_dDecy0 = Math.sin(-2 * w + b);
//		m_dDecy1 = Math.sin(-w + b);
		
		
	 	m_dDeccte = 2.0d * Math.cos(w);
    	m_dDecy0 = Math.cos(2d * w );
    	m_dDecy1 = Math.cos(w);

		// Init prefilter
		if (rate < 38000) {
			coeff_pre[0] = 1.497238168;
			coeff_pre[1] = -0.4498778345;
			coeff_pre[2] = -2.1450223211;
			coeff_pre[3] = -3.8977979094;
			coeff_pre[4] = -3.1936445246;

			coeff_post[0] = 1.058175797e+01;
			coeff_post[1] = 0.0158398918;
			coeff_post[2] = -0.0989743038;
			coeff_post[3] = -0.4806770487;
			coeff_post[4] = -1.5976153081;
			coeff_post[5] = -2.8744063538;
			coeff_post[6] = -3.5609112971;
			coeff_post[7] = -2.4995443421;
			boost = 2;// don't work with low Frequency
		} else if (rate < 46000) {
			coeff_pre[0] = 6.310833714;
			coeff_pre[1] = -0.0622354481;
			coeff_pre[2] = -0.0826699057;
			coeff_pre[3] = -0.7552376542;
			coeff_pre[4] = -0.6351796968;

			coeff_post[0] = 2.211596484e+02;
			coeff_post[1] = 0.0935164959;
			coeff_post[2] = -0.4657366789;
			coeff_post[3] = 1.2531881677;
			coeff_post[4] = -2.2854956347;
			coeff_post[5] = 2.9413622606;
			coeff_post[6] = -2.9346526015;
			coeff_post[7] = 1.8190505669;

			boost = 2;// don't work with low Frequency
		} else if (rate < 72000) {
			coeff_pre[0] = 8.183340972;
			coeff_pre[1] = -0.0626606278;
			coeff_pre[2] = 0.0379954621;
			coeff_pre[3] = -0.6759257285;
			coeff_pre[4] = -0.2546007635;

			coeff_post[0] = 3.834721196e+02;
			coeff_post[1] = 0.1130488058;
			coeff_post[2] = -0.6208360469;
			coeff_post[3] = 1.7554209098;
			coeff_post[4] = -3.2416570827;
			coeff_post[5] = 4.1799016509;
			coeff_post[6] = -3.9042013855;
			coeff_post[7] = 2.3845309549;
		} else {
			coeff_pre[0] = 7.486320862e+01;
			coeff_pre[1] = -0.2082551105;
			coeff_pre[2] = 0.9768363336;
			coeff_pre[3] = -1.9591638847;
			coeff_pre[4] = 1.9768595219;

			coeff_post[0] = 3.508040262e+04;
			coeff_post[1] = 0.3339406563;
			coeff_post[2] = -2.4120965667;
			coeff_post[3] = 7.7656156821;
			coeff_post[4] = -14.4501981950;
			coeff_post[5] = 16.8069704710;
			coeff_post[6] = -12.2491585080;
			coeff_post[7] = 5.2012776995;
		}
	}
	
	public double[] transform(double[] sound) {
		// processing the sound
		int nframes = sound.length;
		double data_in, data_out;

		double[] result = new double[sound.length];

		for (int i = 0; i < nframes; i++) {
			data_in = sound[i];
			if (m_nPrefilter == 1) {
//				m_fPreXV[0] = m_fPreXV[1];
//				m_fPreXV[1] = m_fPreXV[2];
//				m_fPreXV[2] = m_fPreXV[3];
//				m_fPreXV[3] = m_fPreXV[4];
//				m_fPreXV[4] = (double) ((double) data_in / (coeff_pre[0]));// GDS
//
//				m_fPreYV[0] = m_fPreYV[1];
//				m_fPreYV[1] = m_fPreYV[2];
//				m_fPreYV[2] = m_fPreYV[3];
//				m_fPreYV[3] = m_fPreYV[4];
//				m_fPreYV[4] = (double) ((m_fPreXV[0] + m_fPreXV[4]) + 4
//						* (m_fPreXV[1] + m_fPreXV[3]) + 6 * m_fPreXV[boost]
//						+ (coeff_pre[1] * m_fPreYV[0])
//						+ (coeff_pre[2] * m_fPreYV[1])
//						+ (coeff_pre[3] * m_fPreYV[2]) + (coeff_pre[4] * m_fPreYV[3]));

				m_fPreXV[0] = m_fPreXV[1];
				m_fPreXV[1] = m_fPreXV[2]; 
				m_fPreXV[2] = m_fPreXV[3];
				m_fPreXV[3] = m_fPreXV[4]; 
				m_fPreXV[4] =( data_in / coeff_pre[0]);//GDS
				
				m_fPreYV[0] = m_fPreYV[1]; 
				m_fPreYV[1] = m_fPreYV[2]; 
				m_fPreYV[2] = m_fPreYV[3];
				m_fPreYV[3] = m_fPreYV[4];
				m_fPreYV[4] = ((m_fPreXV[0] + m_fPreXV[4])
					+ 4 * (m_fPreXV[1] + m_fPreXV[3]) + 6 * m_fPreXV[boost]/*boost*/
					+ ( coeff_pre[1] * m_fPreYV[0]) 
					+ ( coeff_pre[2] * m_fPreYV[1])
					+ ( coeff_pre[3] * m_fPreYV[2]) 
					+ ( coeff_pre[4] * m_fPreYV[3]));
				
				
				
				data_in = m_fPreYV[4];
			}

			decy = (m_dDeccte * m_dDecy1) - m_dDecy0;
			m_dDecy0 = m_dDecy1;
			m_dDecy1 = decy;
			data_out = (data_in * decy) ;

			if (m_nPostfilter == 1) {
//				m_fPostXV[0] = m_fPostXV[1];
//				m_fPostXV[1] = m_fPostXV[2];
//				m_fPostXV[2] = m_fPostXV[3];
//				m_fPostXV[3] = m_fPostXV[4];
//				m_fPostXV[4] = m_fPostXV[5];
//				m_fPostXV[5] = m_fPostXV[6];
//				m_fPostXV[6] = m_fPostXV[7];
//
//				m_fPostXV[7] = (double) ((double) (data_out) / coeff_post[0]);
//
//				m_fPostYV[0] = m_fPostYV[1];
//				m_fPostYV[1] = m_fPostYV[2];
//				m_fPostYV[2] = m_fPostYV[3];
//				m_fPostYV[3] = m_fPostYV[4];
//				m_fPostYV[4] = m_fPostYV[5];
//				m_fPostYV[5] = m_fPostYV[6];
//				m_fPostYV[6] = m_fPostYV[7];
//
//				m_fPostYV[7] = (double) ((m_fPostXV[0] + m_fPostXV[7]) + 7
//						* (m_fPostXV[1] + m_fPostXV[6]) + 21
//						* (m_fPostXV[2] + m_fPostXV[5]) + 35
//						* (m_fPostXV[3] + m_fPostXV[4])
//						+ (coeff_post[1] * m_fPostYV[0])
//						+ (coeff_post[2] * m_fPostYV[1])
//						+ (coeff_post[3] * m_fPostYV[2])
//						+ (coeff_post[4] * m_fPostYV[3])
//						+ (coeff_post[5] * m_fPostYV[4])
//						+ (coeff_post[6] * m_fPostYV[5]) + (coeff_post[7] * m_fPostYV[6]));
				
				m_fPostXV[0] = m_fPostXV[1];
				m_fPostXV[1] = m_fPostXV[2];
				m_fPostXV[2] = m_fPostXV[3];
				m_fPostXV[3] = m_fPostXV[4]; 
				m_fPostXV[4] = m_fPostXV[5]; 
				m_fPostXV[5] = m_fPostXV[6];
				m_fPostXV[6] = m_fPostXV[7]; 
				
				m_fPostXV[7] = ((data_out) / coeff_post[0]);
				
				m_fPostYV[0] = m_fPostYV[1]; 
				m_fPostYV[1] = m_fPostYV[2]; 
				m_fPostYV[2] = m_fPostYV[3];
				m_fPostYV[3] = m_fPostYV[4]; 
				m_fPostYV[4] = m_fPostYV[5]; 
				m_fPostYV[5] = m_fPostYV[6];
				m_fPostYV[6] = m_fPostYV[7]; 
				
				m_fPostYV[7] =   ((m_fPostXV[0] + m_fPostXV[7]) + 7 
					* (m_fPostXV[1] + m_fPostXV[6]) + 21 
					* (m_fPostXV[2] + m_fPostXV[5]) + 35 
					* (m_fPostXV[3] + m_fPostXV[4]) 
					+ ( coeff_post[1] * m_fPostYV[0]) 
					+ (  coeff_post[2] * m_fPostYV[1])
					+ ( coeff_post[3] * m_fPostYV[2]) 
					+ (  coeff_post[4] * m_fPostYV[3])
					+ ( coeff_post[5] * m_fPostYV[4]) 
					+ (  coeff_post[6] * m_fPostYV[5]) + ( coeff_post[7] * m_fPostYV[6]) );
				
				data_out = m_fPostYV[7];
			}
			data_out = data_out * m_nGain;
			
			

			result[i] = data_out;
		}
		return result;
	}

}
