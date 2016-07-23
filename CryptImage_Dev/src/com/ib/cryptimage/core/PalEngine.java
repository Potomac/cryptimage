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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Random;

/**
 * @author Mannix54
 *
 */
public class PalEngine {
	BufferedImage img;
	int[][] tab;
	int[] tabPixels;// = new int[3*768*576];
	YuvCalc yuvCalc;
	WritableRaster raster;
	WritableRaster rasterOut;
	BufferedImage newImage;
	float[][] randomColor = new float[4][2];
	int min = 0;
	int max = 3;
	
	public PalEngine(){
		yuvCalc = new YuvCalc();
		initRandomColor();
		newImage = new BufferedImage(768, 576, BufferedImage.TYPE_3BYTE_BGR);
		rasterOut = newImage.getRaster();
	}
	
	/**
	 * 
	 */
	public PalEngine(BufferedImage img) {
		initRandomColor();
		newImage = new BufferedImage(768, 576, BufferedImage.TYPE_3BYTE_BGR);
		rasterOut = newImage.getRaster();
		setImg(img);		
		
		yuvCalc = new YuvCalc();
		
		//tab[x + y * 768]		
	}
	
	public void setImg(BufferedImage img_){		
		this.img = img_;
		raster = img.getRaster();
				
		//int[] tab2;
		
		tab = new int[768*3][576];
		//raster.getPixels(0, 0, 768, 576, tab);
		
//		for (int i = 0; i < tab2.length; i++) {
//			tab[i] = (float) tab2[i];
//		}
	}
	
	private void swapColor(){
		min = 0;
		max = 3;
		int[] test;
		int[] swap = new int[3];
		int red, green, blue;
		int rand;
		for (int y = 0; y < 576; y++) {
			rand = getRandomColor();
			for (int x = 0; x < 768 ; x++) {
				
				test = raster.getPixel(x, y, new int[3]);
//				System.out.println("R "  + test[0]);
//				System.out.println("G " + test[1]);
//				System.out.println("B " + test[2]);
				
				red = test[0];
				green = test[1];
				blue = test[2];
				
				switch (rand) {
				case 0:
					//BGR
					swap[0] = blue;
					swap[1] = green;
					swap[2] = red;
					break;
				case 1:
					//BRG
					swap[0] = blue;
					swap[1] = red;
					swap[2] = green;
					break;
				case 2:
					//GBR
					swap[0] = green;
					swap[1] = blue;
					swap[2] = red;
					break;
				case 3:
					//GRB
					swap[0] = green;
					swap[1] = red;
					swap[2] = blue;
					break;
				default:
					break;
				}
				
				rasterOut.setPixel(x, y, swap);
				
//				red = ( img.getRGB(x, y) >> 16 ) & 0xFF;
//				green = ( img.getRGB(x, y) >> 8 ) & 0xFF;
//				blue = ( img.getRGB(x, y) >> 0 )  & 0xFF;				
					
			}
		}		
	}
	
//	private void swapUV(){
//		min = 0;
//		max = 3;
//		
//		
//		float swapU, swapV;
//		int rand;
//		for (int y = 0; y < 576; y++) {
//			rand = getRandomColor();
//			for (int x = 0; x < 768 ; x++) {				
//				switch (rand) {
//				case 0:
//					//VU
//					swapU = tab[3*x+1][y];
//					swapV = tab[3*x+2][y];
//					tab[3*x+1][y] = swapV;
//					tab[3*x+2][y] = swapU;
//					break;
//				case 1:
//					//VV
//					swapU = tab[3*x+1][y];
//					swapV = tab[3*x+2][y];
//					tab[3*x+1][y] = swapV;
//					tab[3*x+2][y] = swapV;
//					break;
//				case 2:
//					//UU
//					swapU = tab[3*x+1][y];
//					swapV = tab[3*x+2][y];
//					tab[3*x+1][y] = swapU;
//					tab[3*x+2][y] = swapU;
//					break;
//				case 3:
//					//UV
//					swapU = tab[3*x+1][y];
//					swapV = tab[3*x+2][y];
//					tab[3*x+1][y] = swapU;
//					tab[3*x+2][y] = swapV;
//					
//					break;
//				default:
//					break;
//				}			
//				
//			}
//		}		
//	}
	
	
	private void initRandomColor(){
		//45 degrees
		randomColor[0][0] = 80;//95.79402f; //0.04f;
		randomColor[0][1] = 149;//142.2161f;//-0.08f;
		
		//315 degrees
		randomColor[1][0] = 151;//144.00732f;//0.09f;
		randomColor[1][1] = 178;//161.7839f;//0.04f;
		
		//135 degrees
		randomColor[2][0] = 104;//111.992676f;//-0.09f;
		randomColor[2][1] = 77;//94.216095f;//-0.04f;
		
		//225 degrees
		randomColor[3][0] = 175;//160.206f;//-0.04f;
		randomColor[3][1] = 106;//113.783905f;//0.08f;
		
	}
	
	private int getRandomColor(){		
		Random rand = new Random();
		return rand.nextInt(max - min + 1) + min;
	}
	
	
	
	
	public BufferedImage decode(){	    
		if(JobConfig.isWantDec()){
			tagLines();
		}
		if(JobConfig.isWantDec()){
			palInverseOddFrame(-45); //-45
		}
		else {
			palInverseOddFrame(45); //45
		}		
	
		if (JobConfig.isAveragingPal() == true) {
			convertToYUV();
			palAverageEvenFrame();
			palAverageOddFrame();
			convertToRGB();
		}
				
		removeTags();
		
		//BufferedImage image = new BufferedImage(768, 576, BufferedImage.TYPE_3BYTE_BGR);
//		WritableRaster raster = (WritableRaster) image.getRaster();
//		raster.setDataElements(0, 0, tab);
		
		return img;
	}
	
	public BufferedImage encode(){		
		if(JobConfig.isWantDec()){
			rephase45();
		}
		if(!JobConfig.isWantDec()){
			//phase45Broadcast();
			tagLines();
		}
		
		return img; //newImage;
	}
		
	
	private void phase45Broadcast(){
		// odd field
		int pol = 1;		
		for (int y = 0; y < 576; y++) {
			for (int x = 0; x < 768; x++) {
				if (pol == 1) { // 45 degrees
					raster.setPixel(x, y, yuvCalc.getRotateRGB(raster.getPixel(x, y, new int[3]), 45));
				} else {// -45 degrees
					raster.setPixel(x, y, yuvCalc.getRotateRGB(raster.getPixel(x, y, new int[3]), -45));
				}
			}
			pol = pol * -1;
			y++;
		}

		pol=1;
		// odd field
		for (int y = 1; y < 576; y++) {
			for (int x = 0; x < 768; x++) {
				if (pol == 1) { // 45 degrees
					raster.setPixel(x, y, yuvCalc.getRotateRGB(raster.getPixel(x, y, new int[3]), 45));
				} else {// -45 degrees
					raster.setPixel(x, y, yuvCalc.getRotateRGB(raster.getPixel(x, y, new int[3]), -45));
				}
			}
			pol = pol * -1;
			y++;
		}
	}
	
	private void rephase45(){		
		// odd field
		int pol = 1;
		
		for (int y = 0; y < 576; y++) {
			for (int x = 0; x < 768; x++) {
				if (pol == 1) { // 45 degrees
					raster.setPixel(x, y, yuvCalc.getRotateRGB(raster.getPixel(x, y, new int[3]), -45));
				} else {// -45 degrees
					raster.setPixel(x, y, yuvCalc.getRotateRGB(raster.getPixel(x, y, new int[3]), 45));
				}
			}
			pol = pol * -1;
			y++;
		}

		pol=1;
		// odd field
		for (int y = 1; y < 576; y++) {
			for (int x = 0; x < 768; x++) {
				if (pol == 1) { // 45 degrees
					raster.setPixel(x, y, yuvCalc.getRotateRGB(raster.getPixel(x, y, new int[3]), -45));
				} else {// -45 degrees
					raster.setPixel(x, y, yuvCalc.getRotateRGB(raster.getPixel(x, y, new int[3]), 45));
				}
			}
			pol = pol * -1;
			y++;
		}
	}
	
	private void removeTags(){
		for (int y = 0; y < 576; y++) {		
				raster.setPixel(0, y, raster.getPixel(1, y, new int[3]));			
		}
	}
	
	private void tagLines(){
		// odd field
		int pol = 1;
		for (int i = 0; i < 576; i++) {
			if (pol == 1) { // 45 degrees
				raster.setPixel(0, i, new int[] { 0, 0, 0 });
			} else {// 315 degrees
				raster.setPixel(0, i, new int[] { 64, 1, 0  });
			}
			i++;
			pol = pol * -1;
		}

		// odd field
		pol = 1;
		for (int i = 1; i < 576; i++) {
			if (pol == 1) { // 135 degrees
				raster.setPixel(0, i, new int[] { 128, 0, 0 });
			} else {// 225 degrees
				raster.setPixel(0, i, new int[] { 255, 1, 0 });
			}
			i++;
			pol = pol * -1;
		}
	}
	
	private void swapRGB(){
		
	}
	
	private void convertToYUV(){
		int[] test;
		int red, green, blue;

		for (int y = 0; y < 576; y++) {
			for (int x = 0; x < 768 ; x++) {
				
				test = raster.getPixel(x, y, new int[3]);
				
				red = test[0];
				green = test[1];
				blue = test[2];
				
				yuvCalc.setRGB(red,green,blue);
				yuvCalc.convertRGBtoYUV();
				tab[3*x][y] = (int) yuvCalc.getY();
				tab[3*x+1][y] = (int) yuvCalc.getU();
				tab[3*x+2][y] = (int) yuvCalc.getV();				
			}
		}		
	}
	
//	private void randomAngle(){
//		int valRand = 0;
//		int phase = -1;
//		//trame impaire
//				
//		for (int y = 0; y < 576; y++) { //2	
//			phase = phase * -1;
//			if(phase == 1){
//				min = 0;
//				max = 1;	
//			}
//			else{
//				min = 2;
//				max = 3;	
//			}
//			valRand = getRandomColor();
//			//System.out.println("impaire rand : " + valRand);
//			for (int x = 0; x < 768 ; x++) {												
//				tab[3*x+1][y] = randomColor[valRand][0];
//				tab[3*x+2][y] = randomColor[valRand][1];		
//			}
//			y++;
//		}
//		
//		phase = -1;
//		//trame paire			
//		for (int y = 1; y < 576; y++) { //3
//			phase = phase * -1;
//			if(phase == 1){
//				min = 0;
//				max = 1;	
//			}
//			else{
//				min = 2;
//				max = 3;	
//			}
//			valRand = getRandomColor();	
//			//System.out.println("paire rand : " + valRand);
//			for (int x = 0; x < 768 ; x++) {						
//				tab[3*x+1][y] = randomColor[valRand][0];
//				tab[3*x+2][y] = randomColor[valRand][1];	
//			}
//			y++;
//		}		
//	}
	
	private int getAngle(int[] pixel, int line){
		float op = 0;
		
		op = line %2;
		
		if(pixel[0] == 0 ){//45
			//System.out.println("45" + " " + line);
			if(JobConfig.isWantDec() ){ //if(op ==0 ){
			return -315;//315;
			}
			else {
				//System.out.println("inverse 45 180");
				return 315;
			}
		}
		
		if(pixel[0] == 64){//315			
			//System.out.println("315" + " " + line);
			if(JobConfig.isWantDec() ){ //if(op ==0 ){
			return -45;//45;
			}
			else {
				//System.out.println("inverse 315 180");
				return 45;
			}
		}
		
		if(pixel[0] == 128){//135			
			//System.out.println("135" + " " + line);
			if(JobConfig.isWantDec() ){ //if(op != 0 ){
			return -225;//225;
			}
			else {
				//System.out.println("inverse 135 180");
				return 225;//225;
			}
		}
		
		if(pixel[0] == 255){//225			
			//System.out.println("225" + " " + line);
			if(JobConfig.isWantDec() ){ //if(op != 0 ){
			return -135 ;//135;
			}
			else {
				//System.out.println("inverse 225 180");
				return 135;
			}
		}
		System.out.println("pas de code pixel");
		
		return 0;
	}	
	
	
	private void copyTabPixelToTab(){
		int cpt = 0,line = 0;
		
			for (int size = 0; size < 768*3*576; size+=3) {
				if(cpt==768*3){
					cpt = 0;
					line++;
				}
				tab[cpt][line] = tabPixels[size];
				cpt++;
				tab[cpt][line] = tabPixels[size +1];
				cpt++;
				tab[cpt][line] = tabPixels[size +2 ];
				cpt++;
			}
		
	}
	
	private void copyTabTotabPixels(){
		int cpt = 0,line = 0;
		
			for (int size = 0; size < 768*3*576; size+=3) {
				if(cpt==768*3){
					cpt = 0;
					line++;
				}
				tabPixels[size] = tab[cpt][line];
				cpt++;
				tabPixels[size + 1] = tab[cpt][line];
				cpt++;
				tabPixels[size + 2] = tab[cpt][line];
				cpt++;
			}
		
	}
	
	private void palInverseOddFrame(int pol){		
		//int pol = sens;	
		//pol = -1;
		int[] pixel = new int[3];
		int cpt = 0;	
		int angle = 0;
		int phase = pol;
		
		for (int y = 0; y < 576; y++) {
			// check angle
			pixel = raster.getPixel(0, y, new int[3]);			
			angle = getAngle(pixel, y);			
			if(cpt == 2){
				cpt = 0;
				phase = phase * -1;	
			}
			cpt++;			
						
			for (int x = 0; x < 768; x++) {				
					pixel = raster.getPixel(x, y, new int[3]);				
				
				//Additional step : -45 degrees for odd lines, 45 degrees for even lines
				if(JobConfig.isWantDec()){
					pixel = yuvCalc.getRotateRGB(pixel, angle );
				}
				else{					
					pixel = yuvCalc.getRotateRGB(pixel, angle + phase);
				}
				raster.setPixel(x, y, pixel);				
			}
		}		
	}
	
	private void palInverseEvenFrame(int sens){		
		int pol = sens;		
		int[] pixel = new int[3];
		int cpt=0;
		
		tabPixels = raster.getPixels(0, 0, 768, 576, tabPixels);
		
		for (int y = 1; y < 576; y++) {
			// sens = sens * -1;
			// System.out.println("impaire " + y);			

			if (pol == -1) {
				for (int x = 0; x < 768; x++) {
					for (int i = 0; i < pixel.length; i++) {
						pixel[0] = tabPixels[3 * x + 768*y];
						pixel[1] = tabPixels[3 * x + 1 + 768*y];
						pixel[2] = tabPixels[3 * x + 2 + 768*y];
					}
					cpt++;
					pixel = yuvCalc.getRotateRGB(pixel, 90);
					for (int i = 0; i < pixel.length; i++) {
						tabPixels[x * 3 + 768*y] = pixel[0];
						tabPixels[x * 3 + 1 + 768*y] = pixel[1];
						tabPixels[x * 3 + 2 + 768*y] = pixel[2];
					}
				}
			} else {
				// tab[3 * x + 2][y] = (float) (vector * Math.sin(angle2)) +
				// 128;
				// tab[3 * x + 1][y] = (float) (vector * Math.cos(angle2)) +
				// 128;
			}

			
			 y += 1; //3
			// pol= pol*-1;
		}
		raster.setPixels(0, 0, 768, 576, tabPixels);
		//System.out.println(cpt);
	}
	
	private void palAverageOddFrame(){		
		for (int y = 0; y < 576; y++) { //2
			//System.out.println("average impaire " + y);
			for (int x = 0; x < 768 ; x++) {				
				//tab[3*x][y] = (tab[3*x][y] + tab[3*x][y+2] ) /2f;					
				tab[3*x+1][y] = (int) ((tab[3*x+1][y] + tab[3*x+1][y+2] ) /2f);
				tab[3*x+2][y] = (int) ((tab[3*x+2][y] + tab[3*x+2][y+2] ) /2f);	
				tab[3*x+1][y+2] = tab[3*x+1][y]; //
				tab[3*x+2][y+2] = tab[3*x+2][y]; //	
			}
			y+=3;//3
		}
	}
	
	private void palAverageEvenFrame(){		
		for (int y = 1; y < 574; y++) { //3
			//System.out.println("average paire " + y);
			for (int x = 0; x < 768 ; x++) {				
				//tab[3*x][y] = (tab[3*x][y] + tab[3*x][y+2] ) /2f;				
				tab[3*x+1][y] = (int) ((tab[3*x+1][y] + tab[3*x+1][y+2] ) /2f);
				tab[3*x+2][y] = (int) ((tab[3*x+2][y] + tab[3*x+2][y+2] ) /2f);
				tab[3*x+1][y+2] = tab[3*x+1][y]; //
				tab[3*x+2][y+2] = tab[3*x+2][y]; //		
			}
			y+=3;//3
		}		
	}
	
//	private void palAverageRGBOddFrame(){
//		int cpt = 0;
//		for (int y = 2; y < 576; y++) {
//			//System.out.println("average impaire " + y);
//			for (int x = 0; x < 768 ; x++) {
//				cpt++;
//				tab[3*x][y] = (tab[3*x][y] + tab[3*x][y-2] ) /2f;				
//				tab[3*x+1][y] = (tab[3*x+1][y] + tab[3*x+1][y-2] ) /2f;
//				tab[3*x+2][y] = (tab[3*x+2][y] + tab[3*x+2][y-2] )/2f;
//				if(cpt == 200){
//				//System.out.println("v impaire " + tab[3*x+2][y]);
//				cpt = 0;
//				}
//			}
//			y+=3;
//		}
//	}
	
//	private void palAverageRGBEvenFrame(){
//		int cpt = 0;
//		for (int y = 3; y < 576; y++) {
//			//System.out.println("average paire " + y);
//			for (int x = 0; x < 768 ; x++) {
//				cpt++;
//				tab[3*x][y] = (tab[3*x][y] + tab[3*x][y-2] ) /2f;				
//				tab[3*x+1][y] = (tab[3*x+1][y] + tab[3*x+1][y-2] ) /2f;
//				tab[3*x+2][y] = (tab[3*x+2][y] + tab[3*x+2][y-2] )/2f;	
//				if(cpt == 200){
//					System.out.println("v impaire " + tab[3*x+2][y]);
//					cpt = 0;
//					}
//			}
//			y+=3;
//		}		
//	}
	
	private void convertToRGB(){
		convertToRGBtoTab();
		
		float[] in = new float[3*768];		
		
		for (int y = 0; y < 576; y++) {
			for (int i = 0; i < 768; i++) {
				in[3*i ] = tab[3*i][y];
				in[3*i+1] = tab[3*i+1][y];
				in[3*i+2] = tab[3*i+2][y];
			}
			raster.setPixels(0, y, 768, 1, in);
		}		
		
	}
	
	private void convertToRGB2(){
		int rgb;
		
		for (int y = 0; y < 576; y++) {
			for (int x = 0; x < 768 ; x++) {				
				rgb =  (int)tab[3*x][y] << 16  | (int)tab[3*x+1][y] << 8  | 
						(int)tab[3*x+2][y];
				//rgb =  yuvCalc.getBlue() << 16  | yuvCalc.getGreen() << 8  | yuvCalc.getBlue();
				img.setRGB(x, y, rgb);				
			}
		}		
	}
	
	private void convertToRGBtoTab(){		
		for (int y = 0; y < 576; y++) {
			for (int x = 0; x < 768 ; x++) {
				yuvCalc.setYUV(tab[3*x][y], tab[3*x+1][y],
						tab[3*x+2][y]);
				yuvCalc.convertYUVtoRGB();
				tab[3*x][y] = yuvCalc.getRed();
				tab[3*x+1][y] = yuvCalc.getGreen();
				tab[3*x+2][y] = yuvCalc.getBlue();
			}
		}		
	}
	

}
