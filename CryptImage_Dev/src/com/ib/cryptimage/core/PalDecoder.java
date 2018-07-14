/**
 * This file is part of	CryptImage.
 *
 * CryptImage is free software: you can redistribute it and/or modify
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
 * along with CryptImage.  If not, see <http://www.gnu.org/licenses/>
 * 
 * 6 juil. 2018 Author Mannix54
 */


package com.ib.cryptimage.core;

/**
 * @author Mannix54
 *
 */
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class PalDecoder {
	
	 
	double gamma_new = 1 / 0.72d;	 
	int[] gamma_LUT = gamma_LUT(gamma_new);

	WritableRaster image;
	WritableRaster pColImage;
	BufferedImage buffInput;
	BufferedImage buffOutput;
	
	int[] ptr;
	
	int digRate = 17750000;
	//int digRate = 14750000;
	int Fsc = 4433619;
	String colsys = "P";
	long sat = 64; // colour saturation control: 0-100, 64typ.
	int brightness = 70; // 70ish for a perfect-level capture 85
	
	int sizePixelsWidth = 0;
		
	String imgDest;
	String imgSource;
	
	int currentFrame;
	String burst1;
	String burst2;
	String grid;
	
	float bright;
	int w;
	int Pstart;
	int Ystart;
	int[] sine;
	int[] cosine;
	int refAmpl;

	int a;
	float cdiv;
	float ydiv;
	float ca;
	float ya;
	int[][] cfilt;
	int[][] yfilt;

	int Vsw;

	int[] buffer;
	int[] b1;
	int[] b2;
	int[] b3;
	int[] b4;
	int[] b5;
	int[] b6;
	int[] Y;

	// short int pu[1280], qu[1280], pv[1280], qv[1280], py[1280], qy[1280];
	int[] pu; 
	int[] qu;
	int[] pv;
	int[] qv;
	int[] py;
	int[] qy;

	// short int m[1280], n[1280];
	int[] m;
	int[] n;

	// short int m1[1280], n1[1280], m2[1280], n2[1280];
	int[] m1;
	int[] n1;
	int[] m2;
	int[] n2;

	// short int m3[1280], n3[1280], m4[1280], n4[1280];
	int[] m3;
	int[] n3;
	int[] m4;
	int[] n4;

	// short int m5[1280], n5[1280], m6[1280], n6[1280];
	int[] m5;
	int[] n5;
	int[] m6;
	int[] n6;
	int H;
	
	

	public PalDecoder(int digRate){
		//this.imgSource = imgSource;
		//this.imgDest = imgDest;
		this.digRate = digRate;
		this.brightness = JobConfig.getGui().getSlidBrightness().getValue();
		this.sat = JobConfig.getGui().getSlidColor().getValue();
						
//		int[] blanc = new int[350 * 288 * 3 ];
//		for (int i = 0; i < blanc.length; i++) {
//			blanc[i] = 255;
//		}
//		
//		pColImage.setPixels(0, 0, 350, 288,
//				 blanc);
//		File outputfile = new File(imgDest);
//		ImageIO.write(buffOutput, "png", outputfile);
		
		
		
	}
	
	
	public void saveSplitInput() throws IOException {
		File outputfile = new File(imgDest + "_944x625.png");
		ImageIO.write(buffInput, "png", outputfile);
	}
	
	public void setImage(BufferedImage inputImg) {		

		
		buffInput = inputImg;
		
		if(buffInput.getWidth() != 944 && buffInput.getHeight() != 625) {
			if(buffInput.getWidth() != 768 && buffInput.getHeight() != 576) {
				//System.out.println("image input should have a 768x576 pixels resolution");
				System.exit(1);
			}
			else {
				//System.out.println("splitting image to 944x625");
				try {
					buffInput = splitFrames(buffInput);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		buffInput = convertToType(buffInput, BufferedImage.TYPE_BYTE_GRAY);
							
		buffOutput = new BufferedImage(buffInput.getWidth(), buffInput.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		
		ptr = new int[buffInput.getHeight() * (buffInput.getWidth() * 3)];
		
		image = buffInput.getRaster();
		pColImage = buffOutput.getRaster();		
		
		sizePixelsWidth = image.getWidth() * 3;
				
		image = buffInput.getRaster();
		pColImage = buffOutput.getRaster();	
		
		initDecoder();
	}

	private void initDecoder() {		
		bright = brightness / 50.0f;

		w = image.getWidth();
		Pstart = (int) (w * 5.2f / 64f);
		Ystart = (int) (w * 9.9f / 64f);
		
		//System.out.println(Pstart + " " + Ystart);

		sine = new int[1280];
		cosine = new int[1280];
		refAmpl = 128; // 128

		// Step 1: create sine/cosine lookups
		float rad;
		for (int i = 0; i < w; i++) {
			rad = (float) (2 * Math.PI * i * Fsc / digRate);
			sine[i] = (int) (refAmpl * Math.sin(rad));
			cosine[i] = (int) (refAmpl * Math.cos(rad));
		}

		// Create filter-profile lookup
		a = 8;
		cdiv = 0;
		ydiv = 0;
		ca = 8f;
		ya = 7.8f;// 7.8; // (8 or 10), 7
		cfilt = new int[4][a + 1];
		yfilt = new int[4][a + 1];

		// Note that y-filter is *much* less selective in the vertical direction:
		// - this is to prevent castlation on horizontal colour boundaries.

		// may wish to broaden vertical bandwidth *slightly* so as to better pass
		// one- or two-line colour bars - underlines/graphics etc.

		// Note also that if Y-bandwidth was made the same as C,
		// and that 'lines' of the masks were equivalent, then
		// significant time-savings could be made.

		for (int f = 0; f <= a; f++) {
			float fc = f;
			if (fc > ca)
				fc = ca;
			float ff = (float) Math.sqrt(f * f + 2 * 2);

			if (ff > ca)
				ff = ca; // 2 -- 4 -- 6 sequence

			float fff = (float) Math.sqrt(f * f + 4 * 4);
			if (fff > ca)
				fff = ca; // because only one FIELD!
			float ffff = (float) Math.sqrt(f * f + 6 * 6);
			if (ffff > ca)
				ffff = ca;

			int d;
			if (f == 0)
				d = 2;
			else
				d = 1;

			cfilt[0][f] = (int) (256 * (1 + Math.cos(Math.PI * fc / ca)) / d);
			cfilt[1][f] = (int) (256 * (1 + Math.cos(Math.PI * ff / ca)) / d);
			cfilt[2][f] = (int) (256 * (1 + Math.cos(Math.PI * fff / ca)) / d);
			cfilt[3][f] = (int) (256 * (1 + Math.cos(Math.PI * ffff / ca)) / d);

			cdiv += cfilt[0][f] + 2 * cfilt[1][f] + 2 * cfilt[2][f] + 2 * cfilt[3][f];

			float fy = f;
			if (fy > ya)
				fy = ya;

			float fffy = (float) Math.sqrt(f * f + 4 * 4);
			if (fffy > ya)
				fffy = ya;

			yfilt[0][f] = (int) (256 * (1 + Math.cos(Math.PI * fy / ya)) / d);
			yfilt[1][f] = 0;
			yfilt[2][f] = (int) (0.2 * 256 * (1 + Math.cos(Math.PI * fffy / ya)) / d);
			yfilt[3][f] = 0;

			ydiv += yfilt[0][f] + 2 * yfilt[2][f];
		}
		cdiv *= 2;
		ydiv *= 2;

		// Step 2:
		// BYTE *buffer, *b1, *b2, *b3, *b4, *b5, *b6, Y[1280];

		buffer = new int[image.getWidth()];
		b1 = new int[image.getWidth()];
		b2 = new int[image.getWidth()];
		b3 = new int[image.getWidth()];
		b4 = new int[image.getWidth()];
		b5 = new int[image.getWidth()];
		b6 = new int[image.getWidth()];
		Y = new int[1280];

		// short int pu[1280], qu[1280], pv[1280], qv[1280], py[1280], qy[1280];
		pu = new int[1280];
		qu = new int[1280];
		pv = new int[1280];
		qv = new int[1280];
		py = new int[1280];
		qy = new int[1280];

		// short int m[1280], n[1280];
		m = new int[1280];
		n = new int[1280];

		// short int m1[1280], n1[1280], m2[1280], n2[1280];
		m1 = new int[1280];
		n1 = new int[1280];
		m2 = new int[1280];
		n2 = new int[1280];

		// short int m3[1280], n3[1280], m4[1280], n4[1280];
		m3 = new int[1280];
		n3 = new int[1280];
		m4 = new int[1280];
		n4 = new int[1280];

		// short int m5[1280], n5[1280], m6[1280], n6[1280];
		m5 = new int[1280];
		n5 = new int[1280];
		m6 = new int[1280];
		n6 = new int[1280];

		// short int H=image->Height;
		H = image.getHeight();	
		
	}
	
	
	public BufferedImage decode(){
	
		//System.out.println("colourising");

		for (int l = 3; l < H - 3; l++) {
			buffer = scanLine(l);
			b1 = scanLine(l - 1);
			b2 = scanLine(l + 1);
			b3 = scanLine(l - 2);
			b4 = scanLine(l + 2);
			b5 = scanLine(l - 3);
			b6 = scanLine(l + 3);

			for (int i = 0; i < w; i++) {
				m[i] = buffer[i] * sine[i];
				n[i] = buffer[i] * cosine[i];

				m1[i] = b1[i] * sine[i];
				n1[i] = b1[i] * cosine[i];

				m2[i] = b2[i] * sine[i];
				n2[i] = b2[i] * cosine[i];

				m3[i] = b3[i] * sine[i];
				n3[i] = b3[i] * cosine[i];

				m4[i] = b4[i] * sine[i];
				n4[i] = b4[i] * cosine[i];

				m5[i] = b5[i] * sine[i];
				n5[i] = b5[i] * cosine[i];

				m6[i] = b6[i] * sine[i];
				n6[i] = b6[i] * cosine[i];
			}

			// Find absolute burst phase
			int bp = 0, bq = 0, bpo = 0, bqo = 0;
			// Btstart/end: 8-20 (14MHz), 85-115 (15MHz), 90-120 (16MHz), 110-140 (20MHz)
			int Btstart = Pstart;
			int Btend = (Pstart + Ystart) / 2;

			for (int i = Btstart; i < Btend; i++) {
				bp += (m[i] - (m3[i] + m4[i]) / 2) / 2;
				bq += (n[i] - (n3[i] + n4[i]) / 2) / 2;
				bpo += (m2[i] - m1[i]) / 2;
				bqo += (n2[i] - n1[i]) / 2;
			}

			bp /= (Btend - Btstart);
			bq /= (Btend - Btstart);
			bpo /= (Btend - Btstart);
			bqo /= (Btend - Btstart);

			// Generate V-switch phase
			if (((bp - bpo) * (bp - bpo) + (bq - bqo) * (bq - bqo)) < (bp * bp + bq * bq) * 2)
				Vsw = 1;
			else
				Vsw = -1;
			if (colsys == "N")
				Vsw = 1; // NTSC fixup!

			// NB bp and bq will be of the order of 1000.
			bp = (bp - bqo) / 2;
			bq = (bq + bpo) / 2; // ave two lines to get -U phase out
			/*
			 * // Rotate burst phase according to V-switch int tbp=(bp*0.707-bq*Vsw*0.707);
			 * bq=bq*0.707+bp*Vsw*0.707; bp=tbp;
			 */

			int norm = (int) (Math.sqrt(bp * bp + bq * bq) * refAmpl * 16); // 16 empirical scaling factor
			if (norm < 130000)
				norm = 130000; // kill colour if burst too weak!

			// p & q should be sine/cosine components' amplitudes
			// NB: Multiline averaging/filtering assumes perfect
			// inter-line phase registration...

			int PU, QU, PV, QV, PY, QY;
			for (int i = Ystart; i < w - a; i++) {
				PU = QU = 0;
				PV = QV = 0;
				PY = QY = 0;

				// Carry out 2D filtering. P and Q are the two arbitrary SINE & COS
				// phases components. U filters for U, V for V, and Y for Y
				// U and V are the same for lines n, n+/-2, but differ in sine for
				// n+/-1, n+/-3 owing to the forward/backward axis slant / \
				// For Y, only use lines n, n+/-2: the others cancel!!!
				// *have tried* using lines +/-1 & 3 --- can be made to work, but
				// introduces *phase-sensitivity* to the filter -> leaks too much
				// subcarrier if *any* phase-shifts!

				int l_, r;
				for (int b = 0; b <= a; b++) {
					l_ = i - b;
					r = i + b;

					PU += (m[r] + m[l_]) * cfilt[0][b] + (+n1[r] + n1[l_] - n2[l_] - n2[r]) * cfilt[1][b]
							- (m3[l_] + m3[r] + m4[l_] + m4[r]) * cfilt[2][b]
							+ (-n5[r] - n5[l_] + n6[l_] + n6[r]) * cfilt[3][b];
					QU += (n[r] + n[l_]) * cfilt[0][b] + (-m1[r] - m1[l_] + m2[l_] + m2[r]) * cfilt[1][b]
							- (n3[l_] + n3[r] + n4[l_] + n4[r]) * cfilt[2][b]
							+ (+m5[r] + m5[l_] - m6[l_] - m6[r]) * cfilt[3][b];
					PV += (m[r] + m[l_]) * cfilt[0][b] + (-n1[r] - n1[l_] + n2[l_] + n2[r]) * cfilt[1][b]
							- (m3[l_] + m3[r] + m4[l_] + m4[r]) * cfilt[2][b]
							+ (+n5[r] + n5[l_] - n6[l_] - n6[r]) * cfilt[3][b];
					QV += (n[r] + n[l_]) * cfilt[0][b] + (+m1[r] + m1[l_] - m2[l_] - m2[r]) * cfilt[1][b]
							- (n3[l_] + n3[r] + n4[l_] + n4[r]) * cfilt[2][b]
							+ (-m5[r] - m5[l_] + m6[l_] + m6[r]) * cfilt[3][b];

					PY += (m[r] + m[l_]) * yfilt[0][b] - (m3[l_] + m3[r] + m4[l_] + m4[r]) * yfilt[2][b];
					QY += (n[r] + n[l_]) * yfilt[0][b] - (n3[l_] + n3[r] + n4[l_] + n4[r]) * yfilt[2][b];
				}
				pu[i] = (int) (PU / cdiv);
				qu[i] = (int) (QU / cdiv);
				pv[i] = (int) (PV / cdiv);
				qv[i] = (int) (QV / cdiv);
				
				if (colsys == "N") {
					pv[i] = (int) (PU / cdiv);
					qv[i] = (int) (QU / cdiv);
				} // NTSC fixup
				
				py[i] = (int) (PY / ydiv);
				qy[i] = (int) (QY / ydiv);
			}

			// Obtain the black level from the "back porch"
			int Bkstart = Pstart, Bkend = Ystart; // rem 50-60 (14MHz), 120-150 (15MHz), 130-160 (16MHz), 160-185
													// (20MHz)
			int blacklevel = 0;
			for (int i = Bkstart; i < Bkend; i++)
				blacklevel += buffer[i] + b1[i] + b2[i] + b3[i] + b4[i];
			blacklevel /= (Bkend - Bkstart) * 5;

			int normalise = refAmpl * refAmpl / 2;
		
			// Generate the luminance (Y), by filtering out Fsc
			for (int i = Ystart; i < w; i++) {
				int tmp = buffer[i] - (py[i] * sine[i] + qy[i] * cosine[i]) / normalise - blacklevel;
				if (tmp < 0)
					tmp = 0;
				if (tmp > 255)
					tmp = 255;
				Y[i] = tmp;
			}

						//
			// ptr = scanLine(l);
			

			for (int i = Ystart; i < w - a; i++) {
				int R, G, B;
				int U, V;
				U = (int) (-(sat * (pu[i] * bp + qu[i] * bq)) / norm);
				V = (int) (-(sat * Vsw * (qv[i] * bp - pv[i] * bq)) / norm);

				R = (int) (bright * (Y[i] + 1.14 * V));
				if (R < 0)
					R = 0;
				if (R > 255)
					R = 255;
				G = (int) (bright * (Y[i] - 0.341 * V - 0.231 * U));
				if (G < 0)
					G = 0;
				if (G > 255)
					G = 255;
				B = (int) (bright * (Y[i] + 2.03 * U));
				if (B < 0)
					B = 0;
				if (B > 255)
					B = 255;
				
				int pp = i * 3;
				int delta = l * sizePixelsWidth;
				
//				//gamma correction	
				if(JobConfig.getGui().getChkGammaCorrection().isSelected()) {
		            R = gamma_LUT[R];
		            G = gamma_LUT[G];
		            B = gamma_LUT[B];
				}

				
				ptr[delta + pp]=(int)R;
				ptr[delta + pp + 1]=(int)G;
				ptr[delta + pp + 2]=(int)B;
				

//				int[] pixels = new int[3];
//				pixels[0] = (int) R;
//				pixels[1] = (int) G;
//				pixels[2] = (int) B;
			

				//System.out.println(pColImage.getWidth() + " " + pColImage.getHeight());
				
				//System.out.println(i + " "  + l);
				//pColImage.setPixel(i, l, pixels);

			}
			
			//pColImage.setPixels(0, l, image.getWidth(), 1, ptr);
			//ptr[Pstart*3]=255; // show where colour burst taken from
			//ptr[Btend*3]=255; //

		}
		
		//System.out.println(Pstart + " " + (Pstart + Ystart) / 2);
		
		pColImage.setPixels(0, 0, image.getWidth(), image.getHeight(), ptr);
		
		//copy pal burst to outputimage
//		pColImage.setPixels(70, 0, 40, 625, 
//				image.getPixels(70, 0, 40, 625, new int[40 * 625 *3]));
		
		
//		BufferedImage burst1 = ImageIO.read(getClass().getResource("/ressources/burst_1_fix_c.png"));
//		BufferedImage burst2 = ImageIO.read(getClass().getResource("/ressources/burst_2_fix_c.png"));
//		
//		System.out.println(burst1.getType());
//		
//		Raster raster = burst1.getRaster();
//		Raster raster2 = burst2.getRaster();
//		
//		pColImage.setPixels(76, 21, 35, 288,
//				raster.getPixels(0, 0, 35, 288, new int[35 * 288 * 3]));
//		
//		pColImage.setPixels(76, 332, 35, 288,
//				raster2.getPixels(0, 0, 35, 288, new int[35 * 288 * 3]));
//		
//		long timeEnd = System.currentTimeMillis();
//		System.out.println("temps : " + (timeEnd - timeStart)/1000f);
//		
//		System.out.println("saving decoded pal image");
//		File outputfile = new File(imgDest);
//		ImageIO.write(buffOutput, "png", outputfile);
		
		BufferedImage fullFrame = new BufferedImage(768, 576, BufferedImage.TYPE_3BYTE_BGR);
		WritableRaster rasterFullFrame = fullFrame.getRaster();
				
		int j = 0;
		
		for (int i = 0; i < 576;  i++) {			
			//System.out.println(i);
			rasterFullFrame.setPixels(0, i, 768, 1,
					pColImage.getPixels(156, j + 21, 768, 1, new int[768 * 3]));						
			i++;
			j++;
			
		}
		
		j=0;
		for (int i = 1; i < 576;  i++) {			
			//System.out.println(i);
			rasterFullFrame.setPixels(0, i, 768, 1,
					pColImage.getPixels(156, j + 332, 768, 1, new int[768 * 3]));						
			i++;
			j++;
			
		}
		
		Long timeEnd = System.currentTimeMillis();
		//System.out.println("temps2 : " + (timeEnd - timeStart)/1000f);
		
//		File outputfileFullFrame = new File(imgDest + "_full.png");
//		ImageIO.write(fullFrame, "png", outputfileFullFrame);
		
		return fullFrame;

	}
	
    private int[] scanLine(int y) {
    	int [] buff = image.getPixels(0, y, image.getWidth(), 1, new int[image.getWidth() * 3]);
    	return buff;
    }
    
	/**
	 * Convert a source image to a desired BufferedImage type
	 * @param sourceImage the image source
	 * @param targetType the target type
	 * @return a converted BufferedImage
	 */
	protected  BufferedImage convertToType(BufferedImage sourceImage,
			int targetType) {
		BufferedImage image;

		// if the source image is already the target type, return the source
		// image
		if (sourceImage.getType() == targetType) {
			image = sourceImage;
		}
		// otherwise create a new image of the target type and draw the new
		// image
		else {
			image = new BufferedImage(sourceImage.getWidth(),
					sourceImage.getHeight(), targetType);
			image.getGraphics().drawImage(sourceImage, 0, 0, null);
		}
		return image;
	}
	

	private void initFrame() {
		currentFrame = JobConfig.getCurrentPalFrame();

		if (digRate == 17750000) {
			switch (currentFrame) {
			case 1:
				grid = "/ressources/subcarrier_phase_1.bmp";
				burst1 = "/ressources/burst_top_phase_4.bmp";
				burst2 = "/ressources/burst_bot_phase_4.bmp";
				break;
			case 2:
				grid = "/ressources/subcarrier_phase_2.bmp";
				burst1 = "/ressources/burst_top_phase_1.bmp";
				burst2 = "/ressources/burst_bot_phase_1.bmp";
				break;
			case 3:
				grid = "/ressources/subcarrier_phase_3.bmp";
				burst1 = "/ressources/burst_top_phase_2.bmp";
				burst2 = "/ressources/burst_bot_phase_2.bmp";
				break;
			case 4:
				grid = "/ressources/subcarrier_phase_4.bmp";
				burst1 = "/ressources/burst_top_phase_3.bmp";
				burst2 = "/ressources/burst_bot_phase_3.bmp";
				break;
			default:
				System.out.println("error pal frame number");
				break;
			}
		} else if (digRate == 14750000) {
			switch (currentFrame) {			
			case 1:
				grid = "/ressources/grid_14.75_phase_1_new.bmp";
				burst1 = "/ressources/burst_14.75_phase_1_top.bmp";
				burst2 = "/ressources/burst_14.75_phase_1_bot.bmp";
				break;
			case 2:				
				grid = "/ressources/grid_14.75_phase_2_new.bmp";
				burst1 = "/ressources/burst_14.75_phase_2_top.bmp";
				burst2 = "/ressources/burst_14.75_phase_2_bot.bmp";
				break;
			case 3:
				grid = "/ressources/grid_14.75_phase_3_new.bmp";
				burst1 = "/ressources/burst_14.75_phase_3_top.bmp";
				burst2 = "/ressources/burst_14.75_phase_3_bot.bmp";
				break;
			case 4:
				grid = "/ressources/grid_14.75_phase_4_new.bmp";
				burst1 = "/ressources/burst_14.75_phase_4_top.bmp";
				burst2 = "/ressources/burst_14.75_phase_4_bot.bmp";
				break;
			default:
				System.out.println("error pal frame number");
				break;
			}
		}

	}
	
	
	
	
	
	private void initFrame_backup() {
		currentFrame = JobConfig.getCurrentPalFrame();

		if (digRate == 17750000) {
			switch (currentFrame) {
			case 1:
				grid = "/ressources/subcarrier_phase_1.bmp";
				burst1 = "/ressources/burst_top_phase_1.bmp";
				burst2 = "/ressources/burst_bot_phase_1.bmp";
				break;
			case 2:
				grid = "/ressources/subcarrier_phase_2.bmp";
				burst1 = "/ressources/burst_top_phase_2.bmp";
				burst2 = "/ressources/burst_bot_phase_2.bmp";
				break;
			case 3:
				grid = "/ressources/subcarrier_phase_3.bmp";
				burst1 = "/ressources/burst_top_phase_3.bmp";
				burst2 = "/ressources/burst_bot_phase_3.bmp";
				break;
			case 4:
				grid = "/ressources/subcarrier_phase_4.bmp";
				burst1 = "/ressources/burst_top_phase_4.bmp";
				burst2 = "/ressources/burst_bot_phase_4.bmp";
				break;
			default:
				System.out.println("error pal frame number");
				break;
			}
		} else if (digRate == 14750000) {
			switch (currentFrame) {			
			case 1:
				grid = "/ressources/grid_14.75_phase_1_new.bmp";
				burst1 = "/ressources/burst_14.75_phase_1_top.bmp";
				burst2 = "/ressources/burst_14.75_phase_1_bot.bmp";
				break;
			case 2:				
				grid = "/ressources/grid_14.75_phase_2_new.bmp";
				burst1 = "/ressources/burst_14.75_phase_2_top.bmp";
				burst2 = "/ressources/burst_14.75_phase_2_bot.bmp";
				break;
			case 3:
				grid = "/ressources/grid_14.75_phase_3_new.bmp";
				burst1 = "/ressources/burst_14.75_phase_3_top.bmp";
				burst2 = "/ressources/burst_14.75_phase_3_bot.bmp";
				break;
			case 4:
				grid = "/ressources/grid_14.75_phase_4_new.bmp";
				burst1 = "/ressources/burst_14.75_phase_4_top.bmp";
				burst2 = "/ressources/burst_14.75_phase_4_bot.bmp";
				break;
			default:
				System.out.println("error pal frame number");
				break;
			}
		}

	}
	
	
	private BufferedImage splitFrames(BufferedImage fullFrame) throws IOException {
		initFrame();
		
		BufferedImage buff = new  BufferedImage(944, 625, BufferedImage.TYPE_3BYTE_BGR);
		
		fullFrame = convertToType(fullFrame, BufferedImage.TYPE_3BYTE_BGR);
		
		WritableRaster raster = buff.getRaster();
		
		BufferedImage burst_top = ImageIO.read(getClass().getResource(burst1));
		BufferedImage burst_bot = ImageIO.read(getClass().getResource(burst2));
				
		Raster rasterBurst1 = burst_top.getRaster();
		Raster rasterBurst2 = burst_bot.getRaster();
		
		Raster rasterfullFrame = fullFrame.getRaster();
		
		raster.setPixels(76, 21, 36, 288,
				rasterBurst1.getPixels(0, 0, 36, 288, new int[36 * 288 * 3]));
		
		raster.setPixels(76, 332, 36, 288,
				rasterBurst2.getPixels(0, 0, 36, 288, new int[36 * 288 * 3]));
		
		//buff = convertToType(buff, BufferedImage.TYPE_BYTE_GRAY);
		
		int j = 0;
		
		for (int i = 0; i < 576;  i++) {			
			//System.out.println(i);
			raster.setPixels(156, j + 21, 768, 1,
					rasterfullFrame.getPixels(0, i, 768, 1, new int[768 *3]));						
			i++;
			j++;			
		}
		
		j=0;
		for (int i = 1; i < 576;  i++) {			
			//System.out.println(i);
			raster.setPixels(156, j + 332, 768, 1,
					rasterfullFrame.getPixels(0, i , 768, 1, new int[768 *3]));						
			i++;
			j++;			
		}
		
		return convertToType(buff, BufferedImage.TYPE_BYTE_GRAY);
	}
	
	public void decode2() throws IOException {	

		System.out.println("colourising");


	}	
	
	// Create the gamma correction lookup table
	private static int[] gamma_LUT(double gamma_new) {
	    int[] gamma_LUT = new int[256];
	 
	    for(int i=0; i<gamma_LUT.length; i++) {
	        gamma_LUT[i] = (int) (255 * (Math.pow((double) i / (double) 255, gamma_new)));
	    }
	 
	    return gamma_LUT;
	}	
	
}