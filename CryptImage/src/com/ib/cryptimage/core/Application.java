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


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;  
import java.io.PrintWriter;  

import org.apache.commons.cli.CommandLine;  
import org.apache.commons.cli.CommandLineParser;  
import org.apache.commons.cli.Option;
import org.apache.commons.cli.PosixParser; 
import org.apache.commons.cli.HelpFormatter;  
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;  
import org.apache.commons.cli.ParseException;  

import javax.imageio.ImageIO;


public class Application {	

	public static void main(String[] args) {
		JobConfig job = new JobConfig();
				
		CommandLineParser cmdLinePosixParser = new PosixParser() ; 
		Options posixOptions = constructPosixOptions(); 
		
		CommandLine commandLine;  
	      try  
	      {  
	         commandLine = cmdLinePosixParser.parse(posixOptions, args, false);	         
	          
	         if ( commandLine.hasOption("v") && args.length == 1 )  
	         {  
	        	System.out.println("CryptImage v0.0.2");
	 			System.out.println("Copyright (C) 2014-09-09 Mannix54");
	 			printLicense();
	 			System.exit(0);
	         }
	         if ( commandLine.hasOption("h") && args.length == 1)  
	         {  
	        	 //printUsage("java -jar cryptimage.jar", constructGnuOptions(), System.out);
	             printUsage("cryptimage.jar", posixOptions, System.out);
	        	 System.exit(0);
	         }	         
	         if (commandLine.hasOption('f')){	        	 
	        	 job.setVideo_frame(Integer.parseInt(commandLine.getOptionValue('f')));
	         }
	         if (commandLine.hasOption('k')){	        	
	        	 job.setPositionSynchro(Integer.parseInt(commandLine.getOptionValue('k')));
	         }
	         if (commandLine.hasOption('p')){	        	
	        	 job.setWantPlay(true);
	         }
	         if ( commandLine.hasOption("i") )  
	         {  
	        	job.setInput_file(commandLine.getOptionValue("i"));
	            job.setOutput_file(commandLine.getOptionValue("i"));
	         }  
	         if ( commandLine.hasOption("d")  )  
	         {  	        
	        	job.setDiscret11Word(Integer.parseInt(commandLine.getOptionValue("d")));
	        	job.setWantDec(true);
	         }
	         if (commandLine.hasOption("c")){	      
		        job.setDiscret11Word(Integer.parseInt(commandLine.getOptionValue("c")));
		        job.setWantDec(false);
	         }
	         if (commandLine.hasOption("s")){	        	
	        	 job.setStrictMode(true);
	         }
	         if (commandLine.hasOption("m")){	        	
	        	 job.setModePhoto(true);
	         }
	         if ( commandLine.hasOption("o") )  
	         { 	           
	            job.setOutput_file(commandLine.getOptionValue("o"));
	         }	         
	         if ( commandLine.hasOption("i") != true )  
	         {  
	        	System.out.println("missing input file");
	        	printUsage("cryptimage.jar", posixOptions, System.out);
	 			System.exit(0);
	         }	         
	         if ( commandLine.hasOption("c") != true && commandLine.hasOption("d") != true )  
	         {  
	        	System.out.println("missing argument -c or -d");
	        	printUsage("cryptimage.jar", posixOptions, System.out);
	 			System.exit(0);
	         }	  
	         
	      }  
	      catch (ParseException parseException)  // checked exception  
	      {  
	         System.err.println(  
	            parseException.getMessage() ); 
	         	printUsage("cryptimage.jar", posixOptions, System.out);
	 			System.exit(0);
	      }  
				

	    printArgs(args);
	    
	    //video mode	 
	    if(job.getVideo_frame() ==0 && job.isModePhoto() == false  ){
	    	System.out.println("you must use the -f option in order to set the number of video frames to capture");
			System.exit(1);	
	    }
	    if(job.getVideo_frame() !=0 && job.isModePhoto() == false  ){
	    	videoManager(job);
	    	System.exit(0);		    
	    }
	    
	    //mode picture only
		BufferedImage img = null;
		
		try{
			 img = ImageIO.read(new File(job.getInput_file()));
		}
		catch(IOException e){			
			System.out.println("I/O error during the load of the input file");
			System.exit(1);
		}
		
				
		CryptImage cryptImg = new CryptImage(img, 1,job.isStrictMode());
		System.out.println("Height : " + cryptImg.getImgRef().getHeight());
		System.out.println("Width : " + cryptImg.getImgRef().getWidth());
				
		//decrypt with code
		if(job.getDiscret11Word() !=0 && job.isWantDec() == true) {
			decryptWithCode(job.getDiscret11Word(), cryptImg, job.getOutput_file());
			System.exit(0);
		}
		
		//crypt with code
		if(job.isWantDec() == false){
			cryptWithCode(cryptImg, job.getDiscret11Word(),job.getOutput_file());
			System.exit(0);
		}		
		
	}
	
	public static void videoManager(JobConfig job){	
					FramesPlayer frmVideo = new FramesPlayer( job);
					frmVideo.readFrame();				
					System.exit(0);					
	}
	
	public static void cryptWithCode(CryptImage cryptImg, int discret11Word, String output_file){
		BufferedImage bi;
		bi = cryptImg.getCryptDiscret11(discret11Word);
		System.out.println("shift pixels : " + cryptImg.getShiftValues());
		saveCryptImage(bi, cryptImg, output_file, discret11Word);
	}
	
	public static void saveCryptImage(BufferedImage bi, CryptImage cryptImg,
			String output_file, int key11) {
		try {
			// retrieve image
			File outputfile = new File(output_file + "_crypt" + key11 + ".png");
			ImageIO.write(bi, "png", outputfile);
			System.out.println("Discret11 crypted image : " + output_file
					+ "_crypt"+ key11 +".png");
		} catch (IOException e) {
			System.out.println("I/O error during the write of the crypted image");
			System.exit(1);
		}

		try {
			File dataFile = new File(output_file + "_crypt" + key11 + ".dat");
			dataFile.createNewFile();
			FileWriter ffw = new FileWriter(dataFile);			
			ffw.write(cryptImg.getsDecaList() + "\r\n");
			ffw.write(cryptImg.getDelayArrayAtFrame(1) + "\r\n");
			ffw.write(cryptImg.getDelayArrayAtFrame(2) + "\r\n");
			ffw.write(cryptImg.getDelayArrayAtFrame(3) + "\r\n");
			ffw.write("key : " + key11 + "\r\n");
			ffw.write("file : " + output_file + "_crypt" + key11 + ".dat");
			ffw.close();
			System.out.println("Report shift file : " + output_file
					+ "_crypt" + key11 + ".dat");
		} catch (IOException e) {
			System.out
					.println("I/O error during the write of the report file");
			System.exit(1);
		}
	}
	
	public static void decryptWithCode(int key11, CryptImage cryptImg,
			String output_file) {
		// get the discret11 buffered image
		BufferedImage bi = null;

		bi = cryptImg.getDecryptDiscret11WithCode(key11);
		saveDecryptFile(bi, output_file, String.valueOf(key11));		
	}	

	public static void saveDecryptFile(BufferedImage bi,String output_file, String key11){
		try {
			// retrieve image
			File outputfile = new File(output_file + "_decrypt_" + key11 + ".png");
			ImageIO.write(bi, "png", outputfile);
			System.out.println( "Output File decrypted : " + output_file + "_decrypt_" + key11 + ".png" );
		} catch (IOException e) {
			System.out.println("I/O error during the write of the decrypted image");			
			System.exit(1);
		}
	}	
	
	
	public static void printArgs(String[] args){
		System.out.print("Arguments : ");
		for(int i=0;i<args.length;i++){
			System.out.print(args[i] + " ");
		}
		System.out.println();
	}
	
	public static Options constructPosixOptions()  
	   {  
	      final Options posixOptions = new Options();  
	      
	      posixOptions.addOption("i", "input-file", true, "pathname to the input image/video file") 
	      			.addOption("o", "output-file", true, "pathname for the destination file")	      			
	      			.addOption("f", "video-frames", true, "the number of frames to capture from a video filename")
	      			.addOption("s", "strict-mode", false, "use a true discret11 mode by resizing the image to 768x576 pixels")
	      			.addOption("k", "keyframe", true, "start the decryption with the given key frame position ")
	      			.addOption("p", "play", false, "play the result instead of creating a video file")
					.addOption("m", "mode-photo", false, "mode photo, works only with image file");
	      
	          
	      posixOptions.getOption("i").setRequired(true);
	      posixOptions.getOption("i").setArgName("input file");
	      posixOptions.getOption("i").setArgs(1);
	      posixOptions.getOption("i").setType(String.class);
	      
	      posixOptions.getOption("o").setRequired(false);
	      posixOptions.getOption("o").setArgName("output file");
	      posixOptions.getOption("o").setArgs(1);
	      posixOptions.getOption("o").setType(String.class);  
	      
	      posixOptions.getOption("f").setRequired(false);
	      posixOptions.getOption("f").setArgName("frames quantity");
	      posixOptions.getOption("f").setArgs(1);
	      posixOptions.getOption("f").setType(Number.class);
	      
	      posixOptions.getOption("k").setRequired(false);
	      posixOptions.getOption("k").setArgName("key frame position");
	      posixOptions.getOption("k").setArgs(1);
	      posixOptions.getOption("k").setType(Number.class);
	      
	      posixOptions.getOption("p").setRequired(false);
	      posixOptions.getOption("p").setArgs(0);
	      
	      posixOptions.getOption("m").setRequired(false);
	      posixOptions.getOption("m").setArgs(0);	 
	      
	     	     
	      OptionGroup grpOptCrypt = new OptionGroup();
	      grpOptCrypt.addOption(new Option("c", "crypt", true, "crypt with code number"));	     
	      grpOptCrypt.addOption(new Option("d", "decrypt",true, "decrypt with code number")) ;
	      	           
	      posixOptions.addOptionGroup(grpOptCrypt);
	      
	      posixOptions.getOption("c").setRequired(true);
	      posixOptions.getOption("c").setArgName("11 bits key word");
	      posixOptions.getOption("c").setArgs(1);
	      posixOptions.getOption("c").setType(Number.class);
	      
	      posixOptions.getOption("d").setRequired(true);
	      posixOptions.getOption("d").setArgName("11 bits key word");
	      posixOptions.getOption("d").setArgs(1);
	      posixOptions.getOption("d").setType(Number.class);	      

	      
	      OptionGroup grpOptHelp = new OptionGroup();
	      grpOptHelp.addOption(new Option("h", "help", false, "show help"));
	      grpOptHelp.addOption(new Option("v", "version", false, "show version")) ;
	      posixOptions.addOptionGroup(grpOptHelp);
	      
	      return posixOptions;  
	   }
	
	 /** 
	    * Print usage information to provided OutputStream. 
	    *  
	    * @param applicationName Name of application to list in usage. 
	    * @param options Command-line options to be part of usage. 
	    * @param out OutputStream to which to write the usage information. 
	    */  
	   public static void printUsage(  
	      final String applicationName,  
	      final Options options,  
	      final OutputStream out)  
	   {  
	      final PrintWriter writer = new PrintWriter(out);  
	      final HelpFormatter usageFormatter = new HelpFormatter();  
	      usageFormatter.printUsage(writer, 80, applicationName, options); 
	      usageFormatter.printOptions(writer, 80, options,1,1);  
	      writer.flush();  
	   }
	   
	public static BufferedImage convertToType(BufferedImage sourceImage,
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
	
	public static void printLicense(){
		System.out.println("CryptImage comes with ABSOLUTELY NO WARRANTY;");
		System.out.println("This is free software, and you are welcome to redistribute it under certain conditions");
	}

}
