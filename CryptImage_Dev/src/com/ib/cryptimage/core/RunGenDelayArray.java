package com.ib.cryptimage.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class RunGenDelayArray {

	public RunGenDelayArray() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		System.out.println("step 1, create delay array");
		
		int[][][] delayArrayFull = new GenDelayArray().getDelayArray();	
			
		int[][] decas = new int[12282][286];
		
		
		for (int i = 0; i < delayArrayFull.length; i++) {
			for (int j = 0; j < 6; j++) {
				for (int k = 0; k < 286; k++) {
					decas[i + 2047 * j][k] = delayArrayFull[i][j][k];
				}
			}
		}
		
		System.out.println("step 2, suppress duplicated values");
		
		int[][] comp = new int[12282][12282];
		int[] doublon = new int[12282];
		
		for (int i = 0; i < decas.length; i++) {
			for (int j = 0; j < decas.length; j++) {
				if(j != i && (comp[j][i] == 0 && comp[i][j] == 0)){
					int k = 0;					
					for (int l = 0; l < 286; l++) {
						comp[j][i] = 1;
						comp[i][j] = 1;
						if(decas[i][l] == decas[j][l]){
							k = k + 1;
						}
					}					
					if(k == 286 ){
						doublon[j] = j;						
					}
				}
			}
		}
		
		int count = 0;
		for (int i = 0; i < doublon.length; i++) {			
			if (doublon[i] != 0){
				count +=1;
			}
			
		}
		System.out.println("unique values : " + count);
		
		int[][] unique = new int[count][286];
		
		int cpt = 0;
		for (int i = 0; i < doublon.length; i++) {			
			if (doublon[i] != 0){
				for (int j = 0; j < 286; j++) {
					unique[cpt][j] = decas[doublon[i]][j];					
				}
				cpt += 1;
			}			
		}
		
		System.out.println("step 3, save unduplicated values to file 'delarray.bin' ");
		serializeDelayArray(unique);

	}
	
	public static int[][][] loadFullArray(String path){
		int[][][] delayArrayFull = null;
		ObjectInputStream inputStream = null;
        try{
            inputStream = new ObjectInputStream(new FileInputStream(path));
        }catch(IOException e){
            System.out.println("There was a problem opening the file: " + e);
            System.exit(1);
        }
        
        try{
            delayArrayFull = (int [][][])inputStream.readObject();       
            inputStream.close();
        }catch(Exception e){
            System.out.println("There was an issue reading from the file: " + e);
            System.exit(1);
        }
        
        return delayArrayFull;
        
	}
	
	public static void serializeDelayArray(int[][] delayArray){
			//serialize delayArray
			File fichier =  new File("delarray.bin") ;
			try {
				ObjectOutputStream oos =  new ObjectOutputStream(new FileOutputStream(fichier)) ;
				oos.writeObject(delayArray) ;
				oos.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   }

}
