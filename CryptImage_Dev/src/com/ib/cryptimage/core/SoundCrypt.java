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

import java.lang.StrictMath;
import biz.source_code.dsp.filter.*;

/**
 * @author Mannix54
 *
 */
public class SoundCrypt {
	
	private double rate;
	private double pitch;	
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
	private double m_nPitch;
	private int m_nPrefilter=1;
	private int m_nPostfilter=1;
	private double m_nGain =1; //10;
	private int boost;
	private double b = StrictMath.PI / 2d;
	private double decy;
	private boolean dec;
	private double freqDec = 12800d;
	private int sampleCount = 0;
	private boolean initCheby = false;
	private boolean initButterWorth = false;
	private boolean initButterWorthChorus = false;
	private boolean initChebyChorus = false;
	
	private int NZEROS_butterWorth;
    private int NPOLES_butterWorth;
	private double GAIN_butterWorth;
	private double[] xv_butterWorth;
	private double[] yv_butterWorth;
	
	private int NZEROS_cheby;
    private int NPOLES_cheby;
	private double gain_cheby;
	private double[] xv_cheby;
	private double[] yv_cheby;
	
	//low pass butterWorth Chorus2
	private int nzeros_butterChorus;
    private int npoles_butterChorus;
	private double gain_buttterChorus;
	private double[] xv_butterChorus;
	private double[] yv_butterChorus;
	
	//init cheby chorus
	private int nzeros_chebyChorus;
    private int npoles_chebyChorus;
	private double gain_chebyChorus;
	private double[] xv_chebyChorus;
	private double[] yv_chebyChorus;
	
	
	/**
	 * 
	 * @param rate rate in hertz for the samples
	 * @param dec true if decoding mode
	 */
	public SoundCrypt(int rate, boolean dec){
		System.out.println("initialisation sound crypt");
		this.dec = dec;
		if(this.dec == true){
			m_nGain = 1.5;
		}
		else{
			m_nGain = 1;
		}
		this.rate = rate;
		this.pitch = 0;
		this.m_nPitch = pitch;
		//m_dDeccte = 2.0d * StrictMath.cos(w);
		int NZEROS = 5;
        int NPOLES = 5;
		double GAIN  =  1.357575728e+01;
		double[] xv = new double[NZEROS+1];
		double[] yv = new double[NPOLES+1];
		
		
		
		initCrypt();	  	
		
		//initFilterParams(rate);	
	}
	
	private void initCrypt(){		
		w = 2 * Math.PI * 12800d/rate;
		m_dDeccte = 2.0 * Math.cos(w);
		


//		m_dDecy0 = StrictMath.sin(-2d * w + b);
//		m_dDecy1 = StrictMath.sin(-w + b);
		
		m_dDecy0 = Math.cos(2 * w );
		m_dDecy1 = Math.cos(w);
		
		
		
		//m_nGain = 1;
		sampleCount = 0;
	}
	
	private double[] ZeroMemory(double[] tab) {
		for (int i = 0; i < tab.length; ++i) {
			tab[i] = 0;
		}
		return tab;
	}
	
	private void initFilterParams(double soundrate) {
		// Init the variable for the sound processing
		rate = soundrate;
		pitch = 0;//33.333333333333;
		m_nPitch = pitch;
		
		m_fPostXV = ZeroMemory(m_fPostXV);
		m_fPostYV = ZeroMemory(m_fPostYV);
		m_fPreXV = ZeroMemory(m_fPreXV);
		m_fPreYV = ZeroMemory(m_fPreYV);

		boost = (m_nPrefilter == 2) ? 3 : 2;
		w = 2d * StrictMath.PI * freqDec / ((double) (rate + m_nPitch));
		
	 	m_dDeccte = 2.0d * StrictMath.cos(w);
    	m_dDecy0 = StrictMath.cos(2d * w );
    	m_dDecy1 = StrictMath.cos(w);

		// Init prefilter
		if (rate < 38000) {
			coeff_pre[0] = 1.497238168;
			coeff_pre[1] = -0.4498778345;
			coeff_pre[2] = -2.1450223211;
			coeff_pre[3] = -3.8977979094;
			coeff_pre[4] = -3.1936445246;

			coeff_post[0] = -2.058175797e+01;
			coeff_post[1] = -0.0158398918;
			coeff_post[2] = 0.0989743038;
			coeff_post[3] = 0.4806770487;
			coeff_post[4] = 1.5976153081;
			coeff_post[5] = 2.8744063538;
			coeff_post[6] = 3.5609112971;
			coeff_post[7] = 2.4995443421;
			boost = 2;// don't work with low Frequency
		} else if (rate < 46000) {
			
//			coeff_pre[0] = 8.500833714;
//			coeff_pre[1] = -0.1022354481;
//			coeff_pre[2] = -0.1966699057;
//			coeff_pre[3] = -0.5662376542;
//			coeff_pre[4] = -0.7361796968;
//			
//			coeff_pre[0] = 8.310833714;
//			coeff_pre[1] = -0.1022354481;
//			coeff_pre[2] = -0.1926699057;
//			coeff_pre[3] = -0.7552376542;
//			coeff_pre[4] = -0.7351796968;
			
//			original
			coeff_pre[0] = 6.310833714;
			coeff_pre[1] = -0.0622354481;
			coeff_pre[2] = -0.0826699057;
			coeff_pre[3] = -0.7552376542;
			coeff_pre[4] = -0.6351796968;
			
			
//			interessant
//			coeff_pre[0] = 8.500833714;
//			coeff_pre[1] = -0.1022354481;
//			coeff_pre[2] = -0.1966699057;
//			coeff_pre[3] = -0.5662376542;
//			coeff_pre[4] = -0.7361796968;

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

//		for (int i = 0; i < 2; i++) {
//			sound = preFilter(sound, 2);
//		}
//
////		for (int i = 0; i < 1; i++) {
////			sound = postFilter(sound);
////		}
//		
//		sound = lowPass(sound,12800d);
//		sound = lowPass(sound,12800d);
//
//	
//		sound = crypt(sound);
//
//		for (int i = 0; i < 2; i++) {
//			sound = preFilter(sound, 2);
//		}
//		sound = lowPass(sound,12800d);
//		sound = lowPass(sound,12800d);
	//sound = lowPass(sound, 12800d);
	//sound = lowPassChebyshev(sound);
	//sound = otherLowPass(sound, 12800);
//	
		
	if(this.dec){
		//sound = preFilter(sound, 2);
		
		//sound = lowPassButterWorth(sound); //default
		sound = lowPassButterWorthChorus2(sound);
	}
	else {
		//sound = postFilter(sound);
		//sound = highPassButterWorth(sound);
		//sound = lowPassChebyshev(sound); //default
		sound = lowPassChebyShevChorus(sound);
	}
	
	//sound = lowPass(sound, 12800);
	sound = crypt(sound);
	//sound = crypt(sound);
	//sound = process(sound, 12800d);
	if(this.dec){
		//sound = postFilter(sound);
		//sound = highPassButterWorth(sound);
		//sound = lowPassChebyshev(sound); //default
		sound = lowPassChebyShevChorus(sound); //chorus prefilter
	}
	else {
		//sound = preFilter(sound, 2);
		//sound = lowPassButterWorth(sound); //default
		
		sound = lowPassButterWorthChorus2(sound);
		
	}
//	//sound = lowPass(sound, 12800);
//	//sound = highPassBessel(sound);
//	//sound = otherLowPass(sound, 12800);
		
/*		if(this.dec){
			sound = decode(sound);
		}
		else {
			sound = encode(sound);
		}
	*/

	
		return sound;
	}

	private double[] preFilter(double[] sound, int boost) {
		initFilterParams(rate);
		double data_in;

		for (int i = 0; i < sound.length; i++) {
			data_in = sound[i];

			m_fPreXV[0] = m_fPreXV[1];
			m_fPreXV[1] = m_fPreXV[2];
			m_fPreXV[2] = m_fPreXV[3];
			m_fPreXV[3] = m_fPreXV[4];
			m_fPreXV[4] = (data_in / coeff_pre[0]);// GDS

			m_fPreYV[0] = m_fPreYV[1];
			m_fPreYV[1] = m_fPreYV[2];
			m_fPreYV[2] = m_fPreYV[3];
			m_fPreYV[3] = m_fPreYV[4];
			m_fPreYV[4] = ((m_fPreXV[0] + m_fPreXV[4]) + 4
					* (m_fPreXV[1] + m_fPreXV[3]) + 6 * m_fPreXV[boost]/* boost */
					+ (coeff_pre[1] * m_fPreYV[0])
					+ (coeff_pre[2] * m_fPreYV[1])
					+ (coeff_pre[3] * m_fPreYV[2]) + (coeff_pre[4] * m_fPreYV[3]));

			data_in = m_fPreYV[4];
			sound[i] = data_in;
		}
		return sound;
	}
	
	private double[] postFilter(double[] sound) {
		initFilterParams(rate);
		double data_out;

		for (int i = 0; i < sound.length; i++) {
			data_out = sound[i];

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

			m_fPostYV[7] = ((m_fPostXV[0] + m_fPostXV[7]) + 7
					* (m_fPostXV[1] + m_fPostXV[6]) + 21
					* (m_fPostXV[2] + m_fPostXV[5]) + 35
					* (m_fPostXV[3] + m_fPostXV[4])
					+ (coeff_post[1] * m_fPostYV[0])
					+ (coeff_post[2] * m_fPostYV[1])
					+ (coeff_post[3] * m_fPostYV[2])
					+ (coeff_post[4] * m_fPostYV[3])
					+ (coeff_post[5] * m_fPostYV[4])
					+ (coeff_post[6] * m_fPostYV[5]) + (coeff_post[7] * m_fPostYV[6]));

			data_out = m_fPostYV[7];
			sound[i] = data_out;
		}
		return sound;
	}
	
	private double[] crypt(double[] sound){	
//		w = 2d * Math.PI * 12800d / ((double) (rate + m_nPitch));
//		m_dDeccte = 2.0d * Math.cos(w);
//    	m_dDecy0 = Math.cos( 2d * w );
//    	m_dDecy1 = Math.cos(w);
    	
    	//alternative
//    	w = 2d * StrictMath.PI * freqDec / ((double) rate + m_nPitch);
//		m_dDeccte = 2.0d * StrictMath.cos(w);
//		m_dDecy0 = StrictMath.sin(-2d * w + b);
//		m_dDecy1 = StrictMath.sin(-w + b);

		//m_dDeccte = 2.0d * StrictMath.cos(w);
    	
		//System.out.println(sound.length);
		//System.out.println("m_dDeccte : " + m_dDeccte);
		double backup = 0;
		
		for (int i = 0; i < sound.length; i++) {				
			
			
			decy = (m_dDeccte * m_dDecy1) - m_dDecy0;
			m_dDecy0 = m_dDecy1;		
			m_dDecy1 = decy;
			
			
			if(sound[i] !=-100000){
			sound[i] = (sound[i] * decy) * m_nGain;
			sampleCount++;
			}
			else{
			//sound[i] = (backup * decy) * m_nGain;
			}
			
			backup = sound[i];
			
			if(sampleCount == this.rate ){
				m_dDecy0 = Math.cos(2 * w );
				m_dDecy1 = Math.cos(w);
				sampleCount = 0;
			}
									
			//sound[i]=  ((Math.cos(Math.PI* i *(2d* 12800d/rate)) * ( sound[i])  )-1);
			
		}
		return sound;
		
		//sound[1]=((cos(Math.PI* i *(2.* 12800d/rate)) )-1);
		
	}
	
	private double[] lowPass(double[] sound, double frequency_cut) {

		double r = Math.sqrt(2d); // rez amount, from sqrt(2) to ~ 0.1
		double f = frequency_cut; // cut off frequency

		double c = 1.0d / Math.tan(Math.PI * f / (double) (this.rate + m_nPitch ));

		double a1;
		double a2;
		double a3;
		double b1;
		double b2;
		double in1 = 0;
		double in2 = 0;
		double out1 = 0;
		double out2 = 0;

		a1 = 1.0d / (1.0d + r * c + c * c);
		a2 = 2d * a1;
		a3 = a1;
		b1 = 2.0d * (1.0d - c * c) * a1;
		b2 = (1.0d - r * c + c * c) * a1;
		double output = 0;

		// out(n) = a1 * in + a2 * in(n-1) + a3 * in(n-2) - b1*out(n-1) -
		// b2*out(n-2)

		for (int i = 0; i < sound.length; i++) {
			 double input = sound[i];

			for (int j = 0; j < 30; j++) {
				
			
			output = a1 * input + a2 * in1 + a3 * in2 - b1 * out1 - b2
					* out2;

			in2 = in1;
			in1 = input;

			out2 = out1;
			out1 = output;
			}

			sound[i] = output;

		}
		return sound;

	}
	
	private double[] process(double[] sound, double frequency_cut){
		
		//init butterworth low pass
		double r = 0.8d;//Math.sqrt(2d); // rez amount, from sqrt(2) to ~ 0.1
		double f = frequency_cut; // cut off frequency

		double c = 1.0d / Math.tan(Math.PI * f / (double) (this.rate + m_nPitch ));

		double a1;
		double a2;
		double a3;
		double b1;
		double b2;
		double in1 = 0;
		double in2 = 0;
		double out1 = 0;
		double out2 = 0;
		double output = 0;
		
		a1 = 1.0d / (1.0d + r * c + c * c);
		a2 = 2d * a1;
		a3 = a1;
		b1 = 2.0d * (1.0d - c * c) * a1;
		b2 = (1.0d - r * c + c * c) * a1;
		
		//init crypt
		w = 2d * Math.PI * freqDec / ((double) (rate + m_nPitch));
		m_dDeccte = 2.0d * Math.cos(w);
    	m_dDecy0 = Math.cos(2d * w );
    	m_dDecy1 = Math.cos(w);
		
    	//process loop
		for (int i = 0; i < sound.length; i++) {
			//butterworth low pass pass 1
			double input = sound[i];

//			double output = a1 * input + a2 * in1 + a3 * in2 - b1 * out1 - b2
//					* out2;
//
//			in2 = in1;
//			in1 = input;
//
//			out2 = out1;
//			out1 = output;
//
//			input = output;
//			sound[i] = input;
			
			//crypt part
			decy = (m_dDeccte * m_dDecy1) - m_dDecy0;
			m_dDecy0 = m_dDecy1;
			m_dDecy1 = decy;
			input = (input * decy) * m_nGain;
			
//			//butterworth low pass pass2			
//	
			 output = a1 * input + a2 * in1 + a3 * in2 - b1 * out1 - b2
					* out2;

			in2 = in1;
			in1 = input;

			out2 = out1;
			out1 = output;
			
			sound[i] = output;
		}
		
		
		
		return sound;
	}
	
	private double[] otherLowPass(double[] sound, double cutoff){
		//init
		int NZEROS = 2;
        int NPOLES = 2;
		double GAIN  = 2.694936923e+00;
		double[] xv = new double[NZEROS+1];
		double[] yv = new double[NPOLES+1];
		
	    for (int i=0;i<sound.length;i++)
	    {
	    	double input = sound[i];
	    	xv[0] = xv[1]; xv[1] = xv[2]; 
	        xv[2] = input / GAIN;
	        yv[0] = yv[1]; yv[1] = yv[2]; 
	        yv[2] =   (xv[0] + xv[2]) + 2 * xv[1]
	                     + ( -0.1872146844 * yv[0]) + ( -0.2970501108 * yv[1]);
	        sound[i]= yv[2];
	    	
	    }
		
		
		return sound;
	}
	
	private double[] bandStop(double[] sound){
		
		int NZEROS = 8;
        int NPOLES = 8;
		double GAIN  = 3.757862200e+00;
		double[] xv = new double[NZEROS+1];
		double[] yv = new double[NPOLES+1];
		
		for (int i=0;i<sound.length;i++)
	    {
			double input = sound[i];
			xv[0] = xv[1]; xv[1] = xv[2]; xv[2] = xv[3];
			xv[3] = xv[4]; xv[4] = xv[5]; 
			xv[5] = xv[6]; xv[6] = xv[7]; xv[7] = xv[8]; 
	        xv[8] = input / GAIN;
	        yv[0] = yv[1];
	        yv[1] = yv[2];
	        yv[2] = yv[3];
	        yv[3] = yv[4];
	        yv[4] = yv[5];
	        yv[5] = yv[6];
	        yv[6] = yv[7];
	        yv[7] = yv[8]; 
	        yv[8] =   (xv[0] + xv[8]) +   7.9991173535 * (xv[1] + xv[7]) +  27.9947044130 * (xv[2] + xv[6])
                    +  55.9867614700 * (xv[3] + xv[5]) +  69.9823488220 * xv[4]
                    + ( -0.0255888497 * yv[0]) + ( -0.2440770023 * yv[1])
                    + ( -1.3318537451 * yv[2]) + ( -4.0278610308 * yv[3])
                    + ( -7.7785146357 * yv[4]) + (-10.3631259230 * yv[5])
                    + ( -9.1282234014 * yv[6]) + ( -4.6291166764 * yv[7]);
	        
	        sound[i] = yv[8];
	    	
	    }		
		
		return sound;
	}
	
	
private double[] bandPassChebyshev(double[] sound){
		
		int NZEROS = 8;
        int NPOLES = 8;
		double GAIN  = 2.067511925e+01;
		double[] xv = new double[NZEROS+1];
		double[] yv = new double[NPOLES+1];
		
		for (int i=0;i<sound.length;i++)
	    {
			double input = sound[i];
			xv[0] = xv[1]; xv[1] = xv[2]; xv[2] = xv[3]; xv[3] = xv[4]; xv[4] = xv[5]; xv[5] = xv[6]; xv[6] = xv[7]; xv[7] = xv[8]; 
	        xv[8] = input / GAIN;
	        yv[0] = yv[1]; yv[1] = yv[2]; yv[2] = yv[3]; yv[3] = yv[4]; yv[4] = yv[5]; yv[5] = yv[6]; yv[6] = yv[7]; yv[7] = yv[8]; 
	        yv[8] =   (xv[0] + xv[8]) - 4 * (xv[2] + xv[6]) + 6 * xv[4]
                    + ( -0.5273140443 * yv[0]) + (  2.7088516620 * yv[1])
                    + ( -6.7295501701 * yv[2]) + ( 10.9265622390 * yv[3])
                    + (-13.1314234630 * yv[4]) + ( 12.5712184200 * yv[5])
                    + ( -9.3685762216 * yv[6]) + (  4.5502315772 * yv[7]);
	       
	        sound[i] = yv[8];
	    	
	    }		
		
		return sound;
	}
	
	
	/**
	 * ripple -1
	 * 10000 hz
	 * order 10
	 * @param sound
	 * @return
	 */
	private double[] lowPassChebyshev(double[] sound){
		
		//init
		if(this.initCheby == false){
		NZEROS_cheby = 10;
        NPOLES_cheby = 10;
		gain_cheby  =  8.553646722e+03;
		 xv_cheby = new double[NZEROS_cheby+1];
		 yv_cheby = new double[NPOLES_cheby+1];
		this.initCheby = true;
		}
	
	    for (int i=0;i<sound.length;i++)
	    {
	    	
	    	double input = sound[i];	    	
	    	xv_cheby[0] = xv_cheby[1];
	    	xv_cheby[1] = xv_cheby[2];
	    	xv_cheby[2] = xv_cheby[3];
	    	xv_cheby[3] = xv_cheby[4];
	    	xv_cheby[4] = xv_cheby[5];
	    	xv_cheby[5] = xv_cheby[6];
	    	xv_cheby[6] = xv_cheby[7];
	    	xv_cheby[7] = xv_cheby[8];
	    	xv_cheby[8] = xv_cheby[9];
	    	xv_cheby[9] = xv_cheby[10]; 
	        xv_cheby[10] = input / gain_cheby;
	        yv_cheby[0] = yv_cheby[1];
	        yv_cheby[1] = yv_cheby[2];
	        yv_cheby[2] = yv_cheby[3];
	        yv_cheby[3] = yv_cheby[4];
	        yv_cheby[4] = yv_cheby[5];
	        yv_cheby[5] = yv_cheby[6];
	        yv_cheby[6] = yv_cheby[7];
	        yv_cheby[7] = yv_cheby[8];
	        yv_cheby[8] = yv_cheby[9];
	        yv_cheby[9] = yv_cheby[10]; 
	        yv_cheby[10] =   (xv_cheby[0] + xv_cheby[10]) 
	        		+ 10 * (xv_cheby[1] + xv_cheby[9]) 
	        		+ 45 * (xv_cheby[2] + xv_cheby[8])
	                     + 120 * (xv_cheby[3] + xv_cheby[7]) 
	                     + 210 * (xv_cheby[4] + xv_cheby[6]) 
	                     + 252 * xv_cheby[5]
	                     + ( -0.2720064188 * yv_cheby[0]) 
	                     + (  1.6419605469 * yv_cheby[1])
	                     + ( -5.3209688540 * yv_cheby[2]) 
	                     + ( 11.7240523930 * yv_cheby[3])
	                     + (-19.1851097200 * yv_cheby[4]) 
	                     + ( 24.2219019850 * yv_cheby[5])
	                     + (-23.9026057280 * yv_cheby[6]) 
	                     + ( 18.2997001200 * yv_cheby[7])
	                     + (-10.5625140400 * yv_cheby[8]) 
	                     + (  4.2358746938 * yv_cheby[9]);
	        
	        
	        	sound[i] = yv_cheby[10];
	        	        
	    }	
		
		return sound;		
	}	
	
	
	/**
	 * postfilter chorus
	 * 
	 * @param sound
	 * @return
	 */
	private double[] lowPassChebyShevChorus(double[] sound){
		//init
		if (this.initChebyChorus == false) {
			 nzeros_chebyChorus = 10;
			 npoles_chebyChorus = 10;
			 gain_chebyChorus = 7.432679797e+02;
			 xv_chebyChorus = new double[nzeros_chebyChorus + 1];
			 yv_chebyChorus = new double[npoles_chebyChorus + 1];
			 this.initChebyChorus = true;
		}
		
	    for (int i=0;i<sound.length;i++)
	    {
	    	double input = sound[i];
	    	xv_chebyChorus[0] = xv_chebyChorus[1]; xv_chebyChorus[1] = xv_chebyChorus[2]; xv_chebyChorus[2] = xv_chebyChorus[3];
			xv_chebyChorus[3] = xv_chebyChorus[4]; xv_chebyChorus[4] = xv_chebyChorus[5]; xv_chebyChorus[5] = xv_chebyChorus[6];
			xv_chebyChorus[6] = xv_chebyChorus[7]; xv_chebyChorus[7] = xv_chebyChorus[8]; xv_chebyChorus[8] = xv_chebyChorus[9];
			xv_chebyChorus[9] = xv_chebyChorus[10];
			
			xv_chebyChorus[10] = input / gain_chebyChorus;
			
			yv_chebyChorus[0] = yv_chebyChorus[1]; yv_chebyChorus[1] = yv_chebyChorus[2]; yv_chebyChorus[2] = yv_chebyChorus[3];
			yv_chebyChorus[3] = yv_chebyChorus[4]; yv_chebyChorus[4] = yv_chebyChorus[5]; yv_chebyChorus[5] = yv_chebyChorus[6];
			yv_chebyChorus[6] = yv_chebyChorus[7]; yv_chebyChorus[7] = yv_chebyChorus[8]; yv_chebyChorus[8] = yv_chebyChorus[9];
			yv_chebyChorus[9] = yv_chebyChorus[10];
			
			yv_chebyChorus[10] =   ((xv_chebyChorus[0] + xv_chebyChorus[10])
				+ 10 * (xv_chebyChorus[1] + xv_chebyChorus[9]) + 45 * (xv_chebyChorus[2] + xv_chebyChorus[8])
				+ 120 * (xv_chebyChorus[3] + xv_chebyChorus[7]) + 210 * (xv_chebyChorus[4] + xv_chebyChorus[6])
				+ 252 * xv_chebyChorus[5]
				+ ( -0.0000357500 * yv_chebyChorus[0]) + (  0.0006094904 * yv_chebyChorus[1])
				+ ( -0.0061672448 * yv_chebyChorus[2]) + (  0.0298428117 * yv_chebyChorus[3])
				+ ( -0.1344215690 * yv_chebyChorus[4]) + (  0.3160380663 * yv_chebyChorus[5])
				+ ( -0.8283557827 * yv_chebyChorus[6]) + (  1.0214778438 * yv_chebyChorus[7])
				+ ( -1.7026748890 * yv_chebyChorus[8]) + (  0.9259874211 * yv_chebyChorus[9]));
			sound[i]=yv_chebyChorus[10];
	    	
	    }	
		
		return sound;		
	}
	
	
	private double[] lowPassBessel(double[] sound){
		//init
		int NZEROS = 4;
        int NPOLES = 4;
		double GAIN  =  4.271890233e+00;
		double[] xv = new double[NZEROS+1];
		double[] yv = new double[NPOLES+1];
		
	    for (int i=0;i<sound.length;i++)
	    {
	    	double input = sound[i];
	    	xv[0] = xv[1]; xv[1] = xv[2]; xv[2] = xv[3]; xv[3] = xv[4]; 
	        xv[4] = input / GAIN;
	        yv[0] = yv[1]; yv[1] = yv[2]; yv[2] = yv[3]; yv[3] = yv[4];  
	        yv[4] =   (xv[0] + xv[4]) + 4 * (xv[1] + xv[3]) + 6 * xv[2]
                    + ( -0.0379367931 * yv[0]) + ( -0.3014891660 * yv[1])
                    + ( -0.9628709710 * yv[2]) + ( -1.4431176584 * yv[3]);
	        sound[i] = yv[4];
	    	
	    }	
		
		return sound;		
	}
	
	private double[] highPassButterWorth(double[] sound){
		//init
		int NZEROS = 1;
        int NPOLES = 1;
		double GAIN  =  1.002849525e+00;
		double[] xv = new double[NZEROS+1];
		double[] yv = new double[NPOLES+1];
		
	    for (int i=0;i<sound.length;i++)
	    {
	    	double input = sound[i];
	    	xv[0] = xv[1]; 
	        xv[1] = input / GAIN;
	        yv[0] = yv[1]; 
	        yv[1] =   (xv[1] - xv[0])
	                     + (  0.9943171437 * yv[0]);
	        sound[i] = yv[1];
	    	
	    }	
		
		return sound;		
	}
	
	
	/**
	 * low pass chorus order 5
	 * @param sound
	 * @return
	 */
	private double[] lowPassButterWorthChorus2(double[] sound){
		//init
		if(this.initButterWorthChorus == false){
		nzeros_butterChorus =5;
        npoles_butterChorus = 5;
		gain_buttterChorus  =  8.513711711;
		xv_butterChorus = new double[nzeros_butterChorus+1];
		yv_butterChorus = new double[npoles_butterChorus+1];
		this.initButterWorthChorus = true;
		}
		
		
	    for (int i=0;i<sound.length;i++)
	    {
	    	double input = sound[i];
			xv_butterChorus[0] = xv_butterChorus[1]; xv_butterChorus[1] = xv_butterChorus[2]; xv_butterChorus[2] = xv_butterChorus[3];
			xv_butterChorus[3] = xv_butterChorus[4]; xv_butterChorus[4] = xv_butterChorus[5];
			xv_butterChorus[5] =input/ gain_buttterChorus;
			
			yv_butterChorus[0] = yv_butterChorus[1]; yv_butterChorus[1] = yv_butterChorus[2]; yv_butterChorus[2] = yv_butterChorus[3];
			yv_butterChorus[3] = yv_butterChorus[4]; yv_butterChorus[4] = yv_butterChorus[5];
			yv_butterChorus[5] =(float)  ((xv_butterChorus[0] + xv_butterChorus[5])
				+ 5 * (xv_butterChorus[1] + xv_butterChorus[4]) + 10 * (xv_butterChorus[2] + xv_butterChorus[3])
				+ ( -0.0134199638 * yv_butterChorus[0]) + ( -0.1268802975 * yv_butterChorus[1])
				+ ( -0.4492748641 * yv_butterChorus[2]) + ( -1.0628245463 * yv_butterChorus[3])
				+ ( -1.1062429845 * yv_butterChorus[4]));//GDS
			
			sound[i] = yv_butterChorus[5];
	    }			
		return sound;		
	}
	
	
	/**
	 * low pass chorus  order 20
	 * @param sound
	 * @return
	 */
	private double[] lowPassButterWorthChorus(double[] sound){
		//init
		int NZEROS =20;
        int NPOLES = 20;
		double GAIN  =  6.360761019e+02;
		double[] xv = new double[NZEROS+1];
		double[] yv = new double[NPOLES+1];
		
	    for (int i=0;i<sound.length;i++)
	    {
	    	double input = sound[i];
	    	xv[0] = xv[1]; xv[1] = xv[2];
			xv[2] = xv[3]; xv[3] = xv[4];
			xv[4] = xv[5]; xv[5] = xv[6];
			xv[6] = xv[7]; xv[7] = xv[8];
			xv[8] = xv[9]; xv[9] = xv[10];
			xv[10] = xv[11]; xv[11] = xv[12];
			xv[12] = xv[13]; xv[13] = xv[14];
			xv[14] = xv[15]; xv[15] = xv[16];
			xv[16] = xv[17]; xv[17] = xv[18];
			xv[18] = xv[19]; xv[19] = xv[20];

			xv[20] = input / GAIN;
			
			yv[0] = yv[1]; yv[1] = yv[2];
			yv[2] = yv[3]; yv[3] = yv[4];
			yv[4] = yv[5]; yv[5] = yv[6];
			yv[6] = yv[7]; yv[7] = yv[8];
			yv[8] = yv[9]; yv[9] = yv[10];
			yv[10] = yv[11]; yv[11] = yv[12];
			yv[12] = yv[13]; yv[13] = yv[14];
			yv[14] = yv[15]; yv[15] = yv[16];
			yv[16] = yv[17]; yv[17] = yv[18];
			yv[18] = yv[19]; yv[19] = yv[20];
			
			yv[20] =( (xv[0] + xv[20])
				- 10 * (xv[2] + xv[18])
				+ 45 * (xv[4] + xv[16])
				- 120 * (xv[6] + xv[14])
				+ 210 * (xv[8] + xv[12])
				- 252 * xv[10]
				+ ( -0.0000285513 * yv[0])  +(  0.0004243200 * yv[1])
				+ ( -0.0038994067 * yv[2]) + (  0.0239205708 * yv[3])
				+ ( -0.1097550138 * yv[4]) + (  0.4105287126 * yv[5])
				+ ( -1.2856568178 * yv[6]) + (  3.4099354198 * yv[7])
				+ ( -7.8166126001 * yv[8]) + ( 15.7188208510 * yv[9])
				+ (-27.7768174090 * yv[10]) + ( 43.1624307710 * yv[11])
				+ (-59.3076356630 * yv[12]) + ( 72.0460190760 * yv[13])
				+ (-76.5020112640 * yv[14]) + ( 70.1965013340 * yv[15])
				+ (-55.2569550190 * yv[16]) + ( 36.3344492590 * yv[17])
				+ (-18.4361976250 * yv[18]) + (  6.1915619488 * yv[19]));
			sound[i]=yv[20];
	    }			
		return sound;		
	}
	
	/**
	 * low pass butterworth 12000 hz order 5
	 * @param sound
	 * @return
	 */
	private double[] lowPassButterWorth(double[] sound){
		
		if(this.initButterWorth == false){
		//init
		 NZEROS_butterWorth = 5;
         NPOLES_butterWorth = 5;
		 GAIN_butterWorth  =  1.357575728e+01;
		 xv_butterWorth = new double[NZEROS_butterWorth+1];
		 yv_butterWorth = new double[NPOLES_butterWorth+1];
		this.initButterWorth = true;
		}
		
	    for (int i=0;i<sound.length;i++)
	    {
	    	if(sound[i] !=-100000){
	    	double input = sound[i];
	    	xv_butterWorth[0] = xv_butterWorth[1];
	    	xv_butterWorth[1] = xv_butterWorth[2];
	    	xv_butterWorth[2] = xv_butterWorth[3];
	    	xv_butterWorth[3] = xv_butterWorth[4];
	    	xv_butterWorth[4] = xv_butterWorth[5]; 
	        xv_butterWorth[5] = input / GAIN_butterWorth;
	        yv_butterWorth[0] = yv_butterWorth[1];
	        yv_butterWorth[1] = yv_butterWorth[2];
	        yv_butterWorth[2] = yv_butterWorth[3];
	        yv_butterWorth[3] = yv_butterWorth[4];
	        yv_butterWorth[4] = yv_butterWorth[5]; 
	        yv_butterWorth[5] =   (xv_butterWorth[0] + xv_butterWorth[5]) + 5 * (xv_butterWorth[1] + xv_butterWorth[4]) + 10 * (xv_butterWorth[2] + xv_butterWorth[3])
	                     + ( -0.0040793387 * yv_butterWorth[0]) + ( -0.0659486571 * yv_butterWorth[1])
	                     + ( -0.1520043648 * yv_butterWorth[2]) + ( -0.6997513807 * yv_butterWorth[3])
	                     + ( -0.4353591675 * yv_butterWorth[4]);
	        
	        
	          sound[i] = yv_butterWorth[5];
	    	}
	        
	    }			
		return sound;		
	}
	
	private double[] decode(double[] sound){
		//low pass
		int NZEROS = 5;
        int NPOLES = 5;
		double GAIN  =  1.357575728e+01;
		double[] xv = new double[NZEROS+1];
		double[] yv = new double[NPOLES+1];
		double input =0;
		
		//crypt
		double b = StrictMath.PI / 2d;
		w = 2d * StrictMath.PI * freqDec / ((double) rate + m_nPitch);
		m_dDeccte = 2.0d * StrictMath.cos(w);
		m_dDecy0 = StrictMath.sin(-2d * w + b);
		m_dDecy1 = StrictMath.sin(-w + b);
		
		//Chebychev
		int NZEROS2 = 10;
        int NPOLES2 = 10;
		double GAIN2  =  8.553646722e+03;
		double[] xv2 = new double[NZEROS2+1];
		double[] yv2 = new double[NPOLES2+1];
		
		for (int i=0;i<sound.length;i++){
			//prefilter
			input = sound[i];
	    	xv[0] = xv[1];
	    	xv[1] = xv[2];
	    	xv[2] = xv[3];
	    	xv[3] = xv[4];
	    	xv[4] = xv[5]; 
	        xv[5] = input / GAIN;
	        yv[0] = yv[1];
	        yv[1] = yv[2];
	        yv[2] = yv[3];
	        yv[3] = yv[4];
	        yv[4] = yv[5]; 
	        yv[5] =   (xv[0] + xv[5]) + 5 * (xv[1] + xv[4]) + 10 * (xv[2] + xv[3])
	                     + ( -0.0040793387 * yv[0]) + ( -0.0659486571 * yv[1])
	                     + ( -0.1520043648 * yv[2]) + ( -0.6997513807 * yv[3])
	                     + ( -0.4353591675 * yv[4]);
	        sound[i] = yv[5];
	        
	        //crypt
	        decy = (m_dDeccte * m_dDecy1) - m_dDecy0;
			m_dDecy0 = m_dDecy1;		
			m_dDecy1 = decy;
			sound[i] = (sound[i] * decy) * m_nGain;
			
			//postfilter
			 input = sound[i];
	    	xv2[0] = xv2[1]; xv2[1] = xv2[2]; xv2[2] = xv2[3]; xv2[3] = xv2[4]; xv2[4] = xv2[5]; xv2[5] = xv2[6]; xv2[6] = xv2[7]; xv2[7] = xv2[8]; xv2[8] = xv2[9]; xv2[9] = xv2[10]; 
	        xv2[10] = input / GAIN2;
	        yv2[0] = yv2[1]; yv2[1] = yv2[2]; yv2[2] = yv2[3]; yv2[3] = yv2[4]; yv2[4] = yv2[5]; yv2[5] = yv2[6]; yv2[6] = yv2[7]; yv2[7] = yv2[8]; yv2[8] = yv2[9]; yv2[9] = yv2[10]; 
	        yv2[10] =   (xv2[0] + xv2[10]) + 10 * (xv2[1] + xv2[9]) + 45 * (xv2[2] + xv2[8])
	                     + 120 * (xv2[3] + xv2[7]) + 210 * (xv2[4] + xv2[6]) + 252 * xv2[5]
	                     + ( -0.2720064188 * yv2[0]) + (  1.6419605469 * yv2[1])
	                     + ( -5.3209688540 * yv2[2]) + ( 11.7240523930 * yv2[3])
	                     + (-19.1851097200 * yv2[4]) + ( 24.2219019850 * yv2[5])
	                     + (-23.9026057280 * yv2[6]) + ( 18.2997001200 * yv2[7])
	                     + (-10.5625140400 * yv2[8]) + (  4.2358746938 * yv2[9]);
	        sound[i] = yv2[10];
			
			
		 }
		
		
		return sound;
		
	}
	
	private double[] encode(double[] sound){
		//low pass
		int NZEROS = 5;
        int NPOLES = 5;
		double GAIN  =  1.357575728e+01;
		double[] xv = new double[NZEROS+1];
		double[] yv = new double[NPOLES+1];
		double input =0;
		
		//crypt
		double b = StrictMath.PI / 2d;
		w = 2d * StrictMath.PI * freqDec / ((double) rate + m_nPitch);
		m_dDeccte = 2.0d * StrictMath.cos(w);
		m_dDecy0 = StrictMath.sin(-2d * w + b);
		m_dDecy1 = StrictMath.sin(-w + b);
		
		//Chebychev
		int NZEROS2 = 10;
        int NPOLES2 = 10;
		double GAIN2  =  8.553646722e+03;
		double[] xv2 = new double[NZEROS2+1];
		double[] yv2 = new double[NPOLES2+1];
		
		for (int i=0;i<sound.length;i++){			
	       
			//prefilter Chebychev
			 input = sound[i];
	    	xv2[0] = xv2[1]; xv2[1] = xv2[2]; xv2[2] = xv2[3]; xv2[3] = xv2[4]; xv2[4] = xv2[5]; xv2[5] = xv2[6]; xv2[6] = xv2[7]; xv2[7] = xv2[8]; xv2[8] = xv2[9]; xv2[9] = xv2[10]; 
	        xv2[10] = input / GAIN2;
	        yv2[0] = yv2[1]; yv2[1] = yv2[2]; yv2[2] = yv2[3]; yv2[3] = yv2[4]; yv2[4] = yv2[5]; yv2[5] = yv2[6]; yv2[6] = yv2[7]; yv2[7] = yv2[8]; yv2[8] = yv2[9]; yv2[9] = yv2[10]; 
	        yv2[10] =   (xv2[0] + xv2[10]) + 10 * (xv2[1] + xv2[9]) + 45 * (xv2[2] + xv2[8])
	                     + 120 * (xv2[3] + xv2[7]) + 210 * (xv2[4] + xv2[6]) + 252 * xv2[5]
	                     + ( -0.2720064188 * yv2[0]) + (  1.6419605469 * yv2[1])
	                     + ( -5.3209688540 * yv2[2]) + ( 11.7240523930 * yv2[3])
	                     + (-19.1851097200 * yv2[4]) + ( 24.2219019850 * yv2[5])
	                     + (-23.9026057280 * yv2[6]) + ( 18.2997001200 * yv2[7])
	                     + (-10.5625140400 * yv2[8]) + (  4.2358746938 * yv2[9]);
	        sound[i] = yv2[10];
	        
	        
	        //crypt
	        decy = (m_dDeccte * m_dDecy1) - m_dDecy0;
			m_dDecy0 = m_dDecy1;		
			m_dDecy1 = decy;
			sound[i] = (sound[i] * decy) * m_nGain;
			
	        
	      //postfilter lowpass
			input = sound[i];
	    	xv[0] = xv[1];
	    	xv[1] = xv[2];
	    	xv[2] = xv[3];
	    	xv[3] = xv[4];
	    	xv[4] = xv[5]; 
	        xv[5] = input / GAIN;
	        yv[0] = yv[1];
	        yv[1] = yv[2];
	        yv[2] = yv[3];
	        yv[3] = yv[4];
	        yv[4] = yv[5]; 
	        yv[5] =   (xv[0] + xv[5]) + 5 * (xv[1] + xv[4]) + 10 * (xv[2] + xv[3])
	                     + ( -0.0040793387 * yv[0]) + ( -0.0659486571 * yv[1])
	                     + ( -0.1520043648 * yv[2]) + ( -0.6997513807 * yv[3])
	                     + ( -0.4353591675 * yv[4]);
	        sound[i] = yv[5];			
			
		 }
		
		
		return sound;
		
	}
}
