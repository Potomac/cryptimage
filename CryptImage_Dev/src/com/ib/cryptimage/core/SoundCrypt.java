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
	private boolean initButterWorthChorus = false;
	private boolean initChebyChorus = false;
	

	
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
		this.dec = dec;
		if(this.dec == true){
			m_nGain = 1.5;
		}
		else{
			m_nGain = 1;
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
	
	public double[] transform(double[] sound) {

		if (this.dec) {
			sound = lowPassButterWorthChorus2(sound);
		} else {
			sound = lowPassChebyShevChorus(sound);
		}

		sound = crypt(sound);

		if (this.dec) {
			sound = lowPassChebyShevChorus(sound);
		} else {
			sound = lowPassButterWorthChorus2(sound);
		}
		return sound;
	}
	
	private double[] crypt(double[] sound) {

		for (int i = 0; i < sound.length; i++) {
			decy = (m_dDeccte * m_dDecy1) - m_dDecy0;
			m_dDecy0 = m_dDecy1;
			m_dDecy1 = decy;

			sound[i] = (sound[i] * decy) * m_nGain;
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
	
}
