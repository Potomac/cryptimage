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

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Random;


/**
 * @author Mannix54
 *
 */
public class PalEngine {
	BufferedImage img;	
	YuvCalc yuvCalc;
	WritableRaster raster;
	int[] linePixels = new int[768*3];
	int[] linePixels2 = new int[768*3];
	int[] pixelTab = new int[3];
	int numFrame = 0;	

	public PalEngine() {
		if (JobConfig.getSystemCrypt() == 2 && !JobConfig.isModePhoto() 
				&& JobConfig.isWantDec()) {
			int val = Integer.valueOf(JobConfig.getGui().getJspFrameStartVideocrypt().getValue().toString());
			if (val > 1 && JobConfig.getNbSkippedFrames() == 0) {
				numFrame =  val - 1;
			}
			if (val -1 > JobConfig.getNbSkippedFrames()) {
				numFrame =   ( val - 1 - JobConfig.getNbSkippedFrames() ) ;
			}
		}
		yuvCalc = new YuvCalc();
	}
	
	/**
	 * 
	 */
	public PalEngine(BufferedImage img) {		
		setImg(img);				
		yuvCalc = new YuvCalc();			
	}
		
	public void setImg(BufferedImage img_){		
		numFrame++;				
		this.img = img_;
		raster = img.getRaster();
	}	
	
		
	public BufferedImage decode(boolean skip){
		if(JobConfig.isWantDec() && skip == true){			
			return img;
		}
		

			//tagLines();
		
		
		palInversePhase();	
		
		if(JobConfig.isWantDec()) {
			firstChromaRotation();
		}	
	
		if (JobConfig.isAveragingPal() == true) {
			//convertToYUV();
		    palAverageEvenFrame();
			palAverageOddFrame();			
			//convertToRGB();
		}
				
	    convertToRGB();
		removeTags();
		
		return img;
	}
	
	public BufferedImage ori_decode(boolean skip){
		if(JobConfig.isWantDec() && skip == true){			
			return img;
		}
		
		if(JobConfig.isWantDec()){
			tagLines();
		}
		
		palInversePhase();	
	
		if (JobConfig.isAveragingPal() == true) {
			convertToYUV();
		    palAverageEvenFrame();
			palAverageOddFrame();			
			convertToRGB();
		}
				
		removeTags();
		
		return img;
	}
	
	
	public BufferedImage average(int[] valTab){		
		palInversePhaseVideocrypt(valTab);
		
		if (JobConfig.isAveragingPal() == true) {
			convertToYUV();
			palAverageEvenFrame();
			palAverageOddFrame();
			convertToRGB();
		}
		
		return img;
	}
	
	public BufferedImage encode(){	
		
		convertToYUV();
		
		if(!JobConfig.isWantDec()) {
			firstChromaRotation();
		}		
		tagLines();
				
		return img;
	}
	
	public BufferedImage ori_encode(){	
		if(!JobConfig.isWantDec()){			
			tagLines();
		}		
		return img;
	}
				
	private void removeTags(){
		for (int y = 0; y < 576; y++) {		
				raster.setPixel(0, y, raster.getPixel(1, y, pixelTab));			
		}
	}
	
	

	private void exp_firstChromaRotation() {
		int angle = 0;
		int pos = 0;
		int sign = 1;
		
		// odd field	
		//additional shift
		if(JobConfig.isWantDec() ){
			//angle = -45;	
			sign = -1;
			}
			else {
				//angle = 45;
				sign = 1;
			}
		for (int y = 0; y < 576; y++) {	
			if(pos == 2) {
				pos = 0;
			}
			if(pos ==0) {
				angle = 225 * sign; //225
			}
			else {
				angle = 45 * sign; //45
			}
			for (int x = 1; x < 768; x++) {
				pixelTab = raster.getPixel(x, y, pixelTab);	
				pixelTab = yuvCalc.getRotateYUV(pixelTab, angle);		
				raster.setPixel(x, y, pixelTab);					
			}
			pos++;
			y++;
		}

		pos = 0;
		// even field
		if(JobConfig.isWantDec() ){
			//angle = -315;			
			sign = -1;
			}
			else {
				//angle = 315;
				sign = 1;
			}
		for (int y = 1; y < 576; y++) {	
			if(pos == 2) {
				pos = 0;
			}
			if(pos ==0) {
				angle = 135 * sign; //135
			}
			else {
				angle = 315 * sign; //315
			}
			for (int x = 1; x < 768; x++) {	
				pixelTab = raster.getPixel(x, y, pixelTab);	
				pixelTab = yuvCalc.getRotateYUV(pixelTab, angle);		
				raster.setPixel(x, y, pixelTab);				
			}
			pos++;
			y++;
		}		
	}
	

	private void firstChromaRotation() {
		int angle = 0;
		
		// odd field	
		//additional shift
		if(JobConfig.isWantDec() ){
			angle = -45;						
			}
			else {
				angle = 45;
			}
		for (int y = 0; y < 576; y++) {										
			for (int x = 1; x < 768; x++) {
				pixelTab = raster.getPixel(x, y, pixelTab);	
				pixelTab = yuvCalc.getRotateYUV(pixelTab, angle);		
				raster.setPixel(x, y, pixelTab);					
			}	
			y++;
		}

		// even field
		if(JobConfig.isWantDec() ){
			angle = -315;						
			}
			else {
				angle = 315;
			}
		for (int y = 1; y < 576; y++) {										
			for (int x = 1; x < 768; x++) {	
				pixelTab = raster.getPixel(x, y, pixelTab);	
				pixelTab = yuvCalc.getRotateYUV(pixelTab, angle);		
				raster.setPixel(x, y, pixelTab);				
			}	
			y++;
		}		
	}
	
	private void tagLines() {
		// odd field
		int val = 0;
		int pos = 1;
		int delta = 0;
		for (int i = 0; i < 576; i++) {
			if(pos > 255) {
				delta = pos - 255; 
				val = 255;
				//System.out.println("pos :" + val + "odd :" + delta);
			}
			else {
				delta = 0;
				val = pos;
			}
			raster.setPixel(0, i, new int[] { val, delta, 1 });
			pos++;
			i++;
		}

		// even field
		pos = 1;
		delta = 0;
		for (int i = 1; i < 576; i++) {
			if(pos > 255) {
				delta = pos - 255; 
				val = 255;
				//System.out.println("pos :" + val + "even :" + delta);
			}
			else {
				delta = 0;
				val = pos;
			}
			raster.setPixel(0, i, new int[] { val, delta, 2 });
			pos++;
			i++;
		}
	}

	private void ori_tagLines() {
		// odd field
		int pol = 0;
		for (int i = 0; i < 576; i++) {
			if (pol == 4) {
				pol = 0;
			}
			pol++;

			if (pol == 1) { // 45 degrees
				raster.setPixel(0, i, new int[] { 0, 0, 0 });
			} else if (pol == 2) {// 315 degrees
				raster.setPixel(0, i, new int[] { 64, 1, 0 });
			} else if (pol == 3) {// 135 degrees
				raster.setPixel(0, i, new int[] { 128, 1, 0 });
			} else if (pol == 4) {// 225 degrees
				raster.setPixel(0, i, new int[] { 255, 1, 0 });
			}
			i++;
		}

		// even field
		pol = 0;
		for (int i = 1; i < 576; i++) {
			if (pol == 4) {
				pol = 0;
			}
			pol++;

			if (pol == 1) { // 45 degrees
				raster.setPixel(0, i, new int[] { 0, 0, 0 });
			} else if (pol == 2) {// 315 degrees
				raster.setPixel(0, i, new int[] { 64, 1, 0 });
			} else if (pol == 3) {// 135 degrees
				raster.setPixel(0, i, new int[] { 128, 1, 0 });
			} else if (pol == 4) {// 225 degrees
				raster.setPixel(0, i, new int[] { 255, 1, 0 });
			}
			i++;
		}
	}
	
	private void convertToYUV(){
		for (int y = 0; y < 576; y++) {
			for (int x = 0; x < 768 ; x++) {				
				raster.setPixel(x, y,
						yuvCalc.convertRGBtoYUV(raster.getPixel(x, y,pixelTab)));
			}
		}		
	}

	
	private int getAngle(int[] pixel, int line){
		
		int posLineBefore = pixel[0] + pixel[1];
		int diff = line - posLineBefore;
		//System.out.println("line before :" + posLineBefore + " ,line after :" + line + " ,diff:" + diff);
		
		int modulo = Math.abs(diff) % 4;
		//System.out.println("modulo " + modulo);
		
		
		if(modulo == 0 ){// 0		
			if(JobConfig.isWantDec() ){
			return 0;
			}
			else {				
				return 0;
			}
		}
		
		if(modulo == 1){//90			
			if(JobConfig.isWantDec() ){
			return -90;
			}
			else {				
				return 90;
			}
		}
		
		if(modulo == 2){//180			
			if(JobConfig.isWantDec() ){
			return -180;
			}
			else {			
				return 180;
			}
		}
		
		if(modulo == 3){//270					
			if(JobConfig.isWantDec() ){
			return -270;
			}
			else {			
				return 270;
			}
		}
		
		System.out.println("pas de valeur modulo trouvée");
		
		return 0;
	}	

	
	private void palInversePhase(){
		
		int pos = 1;
		int angle = 0;
		
		// odd field		
		for (int y = 0; y < 576; y++) {
			// check angle
			pixelTab = raster.getPixel(0, y, pixelTab);			
			angle = getAngle(pixelTab, pos);			
										
			for (int x = 1; x < 768; x++) {				
				pixelTab = raster.getPixel(x, y, pixelTab);				
				pixelTab = yuvCalc.getRotateYUV(pixelTab, angle);		
				raster.setPixel(x, y, pixelTab);
			}	
			pos++;
			y++;
		}

		// even field
		pos = 1;
		for (int y = 1; y < 576; y++) {
			// check angle
			pixelTab = raster.getPixel(0, y, pixelTab);			
			angle = getAngle(pixelTab, pos);			
										
			for (int x = 1; x < 768; x++) {				
				pixelTab = raster.getPixel(x, y, pixelTab);				
				pixelTab = yuvCalc.getRotateYUV(pixelTab, angle);		
				raster.setPixel(x, y, pixelTab);	
     		}	
			pos++;
			y++;
		}		
		
	}
	
	
	private int ori_getAngle(int[] pixel, int line){		
		if(pixel[0] == 0 ){//45			
			if(JobConfig.isWantDec() ){
			return -315;//315;
			}
			else {				
				return 315;
			}
		}
		
		if(pixel[0] == 64){//315			
			if(JobConfig.isWantDec() ){
			return -45;//45;
			}
			else {				
				return 45;
			}
		}
		
		if(pixel[0] == 128){//135			
			if(JobConfig.isWantDec() ){
			return -225;//225;
			}
			else {			
				return 225;//225;
			}
		}
		
		if(pixel[0] == 255){//225						
			if(JobConfig.isWantDec() ){
			return -135 ;//135;
			}
			else {			
				return 135;
			}
		}
		
		return 0;
	}	

	
	private void ori_palInversePhase(){			
		int angle = 0;
		
		for (int y = 0; y < 576; y++) {
			// check angle
			pixelTab = raster.getPixel(0, y, pixelTab);			
			angle = getAngle(pixelTab, y);			
										
			for (int x = 1; x < 768; x++) {				
				pixelTab = raster.getPixel(x, y, pixelTab);				
				pixelTab = yuvCalc.getRotateRGB(pixelTab, angle);		
				raster.setPixel(x, y, pixelTab);				
			}
		}		
	}
	
	private int getAngleVideocrypt(Random rand){		
		int min = 1;
		int max = 4;		
		
		int valVideocrypt;
					
		valVideocrypt = (int) (rand.nextInt(max - min + 1) + min);
		
		switch (valVideocrypt) {
		case 1:
			if(JobConfig.isWantDec())
				return -45;
			else
			return 45;			
		case 2:
			if(JobConfig.isWantDec())
				return -315;
			return 315;			
		case 3:
			if(JobConfig.isWantDec())
				return -135;
			return 135;			
		case 4:
			if(JobConfig.isWantDec())
				return -225;
			return 225;			
		default:
			break;
		}
		return 0;
	}
	
	
	private void palInversePhaseVideocrypt(int[] valTab){		
		int pos = 0;
		int angle = 0;
		
		int sign = 1;
		int signDec = 1;
		int delta = 0;
						
		if(JobConfig.isWantDec()) {
			sign = -1;
			signDec = 1;
			delta = 0;
		}
		else {
			sign = 1;
			signDec = -1;
			delta = 768;
		}

		
		// odd field		
		for (int y = 0; y < 576; y++) {
			
			int magic = delta + valTab[y] * signDec;
			
			int chroma_shift = (int)( Math.sin( ((double)magic)/3.937801333918d ) * 180d );

			
			if(chroma_shift < 0) {
				chroma_shift = chroma_shift + 360;
			}
			
			
			//System.out.println((int)( Math.sin( ((double)213)/3.937801333918d ) * 180d ) + " " + chroma_shift);
			
			if(pos == 2) {
				pos = 0;
			}
			if(pos ==0) {
				if(JobConfig.isWantDec()) {
					angle = 45 * sign;
				}
				else {
					angle = 225 * sign;
				}			
			}
			else {
				if(JobConfig.isWantDec()) {
					angle = 225 * sign;
				}
				else {
					angle = 45 * sign;
				}				
			}
			
			for (int x = 0; x < 768; x++) {				
				pixelTab = raster.getPixel(x, y, pixelTab);			
				pixelTab = yuvCalc.getRotateRGB(pixelTab, angle);		
				raster.setPixel(x, y, pixelTab);
			}
			
			
			for (int x = 0; x < 768; x++) {				
				pixelTab = raster.getPixel(x, y, pixelTab);			
				pixelTab = yuvCalc.getRotateRGB(pixelTab, chroma_shift);		
				raster.setPixel(x, y, pixelTab);
			}
			
			for (int x = 0; x < delta + valTab[y] * signDec; x++) {				
				pixelTab = raster.getPixel(x, y, pixelTab);			
				pixelTab = yuvCalc.getRotateRGB(pixelTab, 180);		
				raster.setPixel(x, y, pixelTab);
			}
			
//			for (int x = 0; x < delta + valTab[y] * signDec; x++) {				
//				pixelTab = raster.getPixel(x, y, pixelTab);			
//				pixelTab = yuvCalc.getRotateRGB(pixelTab, chroma_shift);		
//				raster.setPixel(x, y, pixelTab);
//			}
			
//			for (int x = delta + valTab[y] * signDec ; x < 768; x++) {				
//				pixelTab = raster.getPixel(x, y, pixelTab);			
//				pixelTab = yuvCalc.getRotateRGB(pixelTab, 180);		
//				raster.setPixel(x, y, pixelTab);
//			}
			pos++;
			y++;
		}

		// even field
		pos = 0;
		for (int y = 1; y < 576; y++) {	
			
			int magic = delta + valTab[y] * signDec;
			int chroma_shift = (int)( Math.sin( ((double)magic)/3.937801333918d ) * 180d );
			
			if(chroma_shift < 0) {
				chroma_shift = chroma_shift + 360;
			}
						
			if(pos == 2) {
				pos = 0;
			}
			if(pos ==0) {
				if(JobConfig.isWantDec()) {
					angle = 315 * sign;
				}
				else {
					angle = 135 * sign;
				}				
			}
			else {
				if(JobConfig.isWantDec()) {
					angle = 135 * sign;
				}
				else {
					angle = 315 * sign;
				}				
			}			
			
			for (int x = 0; x < 768; x++) {				
				pixelTab = raster.getPixel(x, y, pixelTab);			
				pixelTab = yuvCalc.getRotateRGB(pixelTab, angle);		
				raster.setPixel(x, y, pixelTab);
			}
			
			for (int x = 0; x < 768; x++) {				
				pixelTab = raster.getPixel(x, y, pixelTab);			
				pixelTab = yuvCalc.getRotateRGB(pixelTab, chroma_shift);		
				raster.setPixel(x, y, pixelTab);
			}
			
			
			for (int x = 0; x < delta + valTab[y] * signDec; x++) {				
				pixelTab = raster.getPixel(x, y, pixelTab);					
				pixelTab = yuvCalc.getRotateRGB(pixelTab, 180);		
				raster.setPixel(x, y, pixelTab);	
     		}
			
//			for (int x = 0; x < delta + valTab[y] * signDec; x++) {				
//				pixelTab = raster.getPixel(x, y, pixelTab);					
//				pixelTab = yuvCalc.getRotateRGB(pixelTab, chroma_shift);		
//				raster.setPixel(x, y, pixelTab);	
//     		}
			
			
//			for (int x = delta + valTab[y] * signDec; x < 768; x++) {				
//				pixelTab = raster.getPixel(x, y, pixelTab);			
//				pixelTab = yuvCalc.getRotateRGB(pixelTab, 180);		
//				raster.setPixel(x, y, pixelTab);
//			}
			

			
			pos++;
			y++;
		}		
	}
	
	private int getCounterPhase(int phase) {
		int res = phase + 180;
		
		if(res > 360) {
			res = res - 360;
		}
		
		return res;
	}
	
	private void ori_palInversePhaseVideocrypt(){		
		Random rand = new Random(numFrame);

		int angle = 0;		
		
		for (int y = 0; y < 576; y++) {
			// check angle			
			angle = getAngleVideocrypt(rand);					
						
			for (int x = 0; x < 768; x++) {				
				pixelTab = raster.getPixel(x, y, pixelTab);									
				pixelTab = yuvCalc.getRotateRGB(pixelTab, angle);				
				raster.setPixel(x, y, pixelTab);				
			}
		}		
	}
	
	private void palAverageOddFrame(){		
		for (int y = 0; y < 576; y++) { // 2			
			raster.setPixels(0, y, 768, 1, averageLine(raster.getPixels(
					0, y, 768, 1, linePixels),
					raster.getPixels(0, y + 2, 768, 1, linePixels2),y));
			y += 3;
		}

	}
	
	private void palAverageEvenFrame(){		
		for (int y = 1; y < 574; y++) { //3
			raster.setPixels(0, y, 768, 1,						 
					averageLine(raster.getPixels(0, y, 768,1,linePixels),
						raster.getPixels(0, y + 2, 768, 1, linePixels2),y));	
			y+=3;//3
		}		
	}
	
	private int[] averageLine(int[] line1, int[] line2, int y){	
		
		for (int i = 1; i < 768; i++) {
			line1[3*i +1] = ( line1[3*i +1] + line2[3*i +1] ) /2;
			line1[3*i +2] = ( line1[3*i +2] + line2[3*i +2] ) /2;
			line2[3*i +1] = line1[3*i +1];
			line2[3*i +2] = line1[3*i +2];
		}
		raster.setPixels(0, y+2, 768,1,line2);
		return line1;
	}
	
	
	private void convertToRGB(){		
		for (int y = 0; y < 576; y++) {
			for (int x = 0; x < 768; x++) {
				raster.setPixel(x, y, yuvCalc.convertYUVtoRGB(raster.getPixel(x, y,
						pixelTab)));
			}			
		}		
	}	

}
