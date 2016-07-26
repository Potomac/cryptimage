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
 * 18 juil. 2016 Author Mannix54
 */



package com.ib.cryptimage.core;

import com.ib.cryptimage.core.InitException;

/**
 * @author Mannix54
 *
 */
public class YuvCalc extends InitException {	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private float y;
	private float u;
	private float v;
	private float red;
	private float green;
	private float blue;
	private boolean yuv = false;
	private boolean rgb = false;
	private CosSinus cosSin;
	float redCoef1, redCoef2, redCoef3, greenCoef1, 
	greenCoef2, greenCoef3, blueCoef1, blueCoef2, blueCoef3;
	float uCoef1, uCoef2, uCoef3, vCoef1, vCoef2, vCoef3;

	/**
	 * 
	 */
	public YuvCalc() {
		// TODO Auto-generated constructor stub
		
		switch (JobConfig.getTypeYUV()) {
		case 0:			
			// bt.601
			redCoef1 = 0.299f;
			redCoef2 = -0.14713f;
			redCoef3 = 0.615f;

			greenCoef1 = 0.587f;
			greenCoef2 = -0.28886f;
			greenCoef3 = -0.51499f;

			blueCoef1 = 0.114f;
			blueCoef2 = 0.436f;
			blueCoef3 = -0.10001f;

			uCoef1 = 0;
			uCoef2 = -0.39465f;
			uCoef3 = 2.03211f;

			vCoef1 = 1.13983f;
			vCoef2 = -0.5860f;
			vCoef3 = 0;
			break;
		case 1:			
			// bt.709
			redCoef1 = 0.2126f;
			redCoef2 = -0.09991f;
			redCoef3 = 0.615f;

			greenCoef1 = 0.7152f;
			greenCoef2 = -0.33609f;
			greenCoef3 = -0.55861f;

			blueCoef1 = 0.0722f;
			blueCoef2 = 0.436f;
			blueCoef3 = -0.05639f;

			uCoef1 = 0;
			uCoef2 = -0.21482f;
			uCoef3 = 2.12798f;

			vCoef1 = 1.28033f;
			vCoef2 = -0.38059f;
			vCoef3 = 0;
			break;
		case 2:			
			// other
			redCoef1 = 0.299f;
			redCoef2 = -0.168736f;
			redCoef3 = 0.5f;

			greenCoef1 = 0.587f;
			greenCoef2 = -0.331264f;
			greenCoef3 = -0.418688f;

			blueCoef1 = 0.114f;
			blueCoef2 = 0.5f;
			blueCoef3 = -0.081312f;

			uCoef1 = 0;
			uCoef2 = -0.3455f;
			uCoef3 = 1.7790f;

			vCoef1 = 1.4075f;
			vCoef2 = -0.7169f;
			vCoef3 = 0;

			break;
		default:
			break;
		}
		
		
		
		
		
		
		
		
		cosSin = new CosSinus();
	}
	
	public void setRGB(float r, float g, float b){
		this.red = r;
		this.green = g;
		this.blue = b;
		rgb = true;
	}
	
	public void setYUV(float y, float u, float v){
		this.y =y;
		this.u = u;
		this.v = v;
		yuv = true;
	}
	
	public int[] getRotateRGB(int[] tabRGB,float angle){
		setRGB((float)tabRGB[0], (float)tabRGB[1],(float)tabRGB[2]);
				
		convertRGBtoYUV();				
		
		float u_temp = u -128;
		float v_temp = v -128;		
	
		float new_u_temp, new_v_temp;		
		
//		new_u_temp = (float) (u_temp * Math.cos(angle * Math.PI/180d)
//				- v_temp * Math.sin(angle*Math.PI/180d));
//		new_v_temp = (float) (u_temp * Math.sin(angle * Math.PI/180d)
//				+ v_temp * Math.cos(angle*Math.PI/180d));	
		
		new_u_temp =  (float) ((u_temp * cosSin.getCos((int) angle)
				- v_temp * cosSin.getSin((int) angle)));
		new_v_temp = (float) ((u_temp * cosSin.getSin((int) angle)
				+ v_temp * cosSin.getCos((int) angle)));	
				
		float y_temp = y;
		new_u_temp = new_u_temp +128;
		new_v_temp = new_v_temp +128;
		
		setYUV(y_temp, new_u_temp, new_v_temp);
		convertYUVtoRGB();
		
		int[] rgbTab = new int[3];
		rgbTab[0] = (int) red;
		rgbTab[1] = (int) green;
		rgbTab[2] = (int) blue;
		
		return rgbTab;		
	}
	
	public void convertRGBtoYUV(){
		if(!rgb){
			try {
				throw new InitException("rgb values not initialized");
			} catch (InitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		y = red *  redCoef1 + green *  greenCoef1 + blue *  blueCoef1;//coef1 * red + coef2 * green + coef3 * blue;
		u = red * redCoef2 + green * greenCoef2 + blue *  blueCoef2 + 128f; //coef4 * (blue - y);
		v = red *  redCoef3 + green * greenCoef3 + blue * blueCoef3 + 128f;
		
		if(y > 255){
			y = 255;			
		}
		if(y < 0){
			y = 0;			
		}
		if(u > 255){
			u = 255;			
		}
		if(u < 0){
			u = 0;			
		}
		if(v > 255){
			v = 255;			
		}
		if(v < 0){
			v = 0;			
		}
		
	}
	
	public int[] convertYUVtoRGB(int[] pixelYUV){
		setYUV(pixelYUV[0], pixelYUV[1], pixelYUV[2]);
		convertYUVtoRGB();
		
		int[] pixRGB = new int[3];
		pixRGB[0] = this.getRed();
		pixRGB[1] = this.getGreen();
		pixRGB[2] = this.getBlue();
		return pixRGB;		
	}
	
	public int[] convertRGBtoYUV(int[] pixelRGB){
		setRGB(pixelRGB[0], pixelRGB[1], pixelRGB[2]);
		convertRGBtoYUV();
		
		int[] pixYUV = new int[3];
		pixYUV[0] = (int)this.getY();
		pixYUV[1] = (int)this.getU();
		pixYUV[2] = (int)this.getV();
		return pixYUV;		
	}	
	
	public void convertYUVtoRGB(){
		if(!yuv){
			try {
				throw new InitException("yuv values not initialized");
			} catch (InitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		red = y + vCoef1 * (v - 128f) + uCoef1* (u - 128f);
		green = y + uCoef2 * (u - 128f) + vCoef2 * (v - 128f);
		blue = y + uCoef3 * (u - 128f) + vCoef3 * (v - 128f);
		if(red > 255){
			red = 255;			
		}
		if(red < 0){
			red = 0;			
		}
		if(green > 255){
			green = 255;			
		}
		if(green < 0){
			green = 0;			
		}
		if(blue > 255){
			blue = 255;			
		}
		if(blue < 0){
			blue = 0;			
		}
	}

	public float getY() {
		//System.out.println("Y : " + y);
		if(y < 0 || y> 255){
			System.out.println("Y bad " + y);
		}
		return y;
	}

	public void setY(float y) {			
		this.y = y;
	}

	public float getU() {
		if(u < 0 || u> 255){
			System.out.println("u bad " + u);
		}
		return u;
	}

	public void setU(float u) {
		this.u = u;
	}

	public float getV() {
		if(v < 0 || v> 255){
			System.out.println("v bad " + v);
		}
		return v;
	}

	public void setV(float v) {
		this.v = v;
	}

	public int getRed() {
		//System.out.println("Red : " + red);
		if(red < 0){
			red = 0;
		}
		if(red > 255){
			red = 255;
		}
		return (int)red;
	}

	public void setRed(float red) {
		this.red = red;
	}

	public int getGreen() {
		//System.out.println("Green : " + green);
		if(green < 0){
			green = 0;
		}
		if(green > 255){
			green = 255;
		}
		return (int)green;
	}

	public void setGreen(float green) {
		this.green = green;
	}

	public int getBlue() {
		//System.out.println("Blue : " + blue);
		if(blue < 0){
			blue = 0;
		}
		if(blue > 255){
			blue = 255;
		}
		return (int)blue;
	}

	public void setBlue(float blue) {
		this.blue = blue;
	}

	public boolean isYuv() {
		return yuv;
	}

	public boolean isRgb() {		
		return rgb;
	}		

}

 class InitException extends Exception{  
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public InitException() {}  
	
	public InitException(String message) {  
		super(message); 
	}  	
	public InitException(Throwable cause) {  
		super(cause); 
	}	
	public InitException(String message, Throwable cause) {  
		super(message, cause); 
	} 
}

