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

	/**
	 * 
	 */
	public YuvCalc() {
		// TODO Auto-generated constructor stub
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
		
		new_u_temp = (float) (u_temp * Math.cos(angle * Math.PI/180d)
				- v_temp * Math.sin(angle*Math.PI/180d));
		new_v_temp = (float) (u_temp * Math.sin(angle * Math.PI/180d)
				+ v_temp * Math.cos(angle*Math.PI/180d));		
				
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
		y = red *  0.299f + green *  0.587f + blue *  0.114f;//coef1 * red + coef2 * green + coef3 * blue;
		u = red * -0.168736f + green * -0.331264f + blue *  0.500000f + 128f; //coef4 * (blue - y);
		v = red *  0.5f + green * -0.418688f + blue * -0.081312f + 128f;
		
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
	
	public void convertYUVtoRGB(){
		if(!yuv){
			try {
				throw new InitException("yuv values not initialized");
			} catch (InitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		red = y + 1.4075f * (v - 128f);
		green = y - 0.3455f * (u - 128f) - (0.7169f * (v - 128f));
		blue = y + 1.7790f * (u - 128f);
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

