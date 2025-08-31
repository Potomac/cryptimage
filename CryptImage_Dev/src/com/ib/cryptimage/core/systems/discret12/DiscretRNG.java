package com.ib.cryptimage.core.systems.discret12;
import com.ib.cryptimage.core.types.SystemType;

public class DiscretRNG {
	
	private int discretType;
	private int key;
	private int initialKey;
	private int frame;
	private int line;
    private int cycleType;
    private int currentZ;
	
	/**
	 * store the 7 or 14 11 bit words according to the audience level and the discret mode (discret11 or discret12)
	 */
	private int[] key11BitsTab = new int[14];	
	
	/**
	 * store the delay in pixels value
	 * in 3 dimension array who represents
	 * the 6 or 12 TV frames, 7 audience levels in discret11 mode, 14 in discret12,
	 * 
	 * structure of the 3 dimensions array : audience, number of frame, number of the line
	 */
	protected int[][][] delayArray = new int[14][12][286];
	
	public int[][][] getDelayArray() {
		return delayArray;
	}

	/**
	 * array for storing the delay in pixels
	 * 3 types of delay
	 */
	private int[] decaPixels = new int[3];
	
	/**
	 * the count iterator for delay array
	 */
	protected int cptArray = 0;	
	/**
	 * the default width of the image which is 768
	 */
	protected final int sWidth = 768;	
	
    
	/**
	 * store the value for the truth table
	 * first dimension is z, second is b0, third is b10
	 * delay[z][b0][b10]
	 */
	private int[][][] truthTable = new int[2][2][2];
	
	
	public DiscretRNG(int discretType, int cycleType, int key) throws Exception {
		
		if(key < 0 || key > 2047) {
			throw new Exception("Illegal range for key");
		}
		
		if(cycleType != 6 && discretType == SystemType.DISCRET11) {
			throw new Exception("Illegal range for cycleType when system is Discret11");
		}
		
		if((cycleType != 6 && cycleType != 12) && discretType == SystemType.DISCRET12) {
			throw new Exception("Illegal range for cycleType when system is Discret12");
		}
		
		if(discretType == SystemType.DISCRET11) {
			this.discretType = discretType;
			this.initTruthTableD11();
			this.cycleType = 6;
			this.initialKey = key;
			this.key = key;		
		}
		else if(discretType == SystemType.DISCRET12) {
			this.discretType = discretType;
			
			if(Discret12Conf.isDecode) {
				this.initTruthTableD11Decode();
			}
			else {
				this.initTruthTableD11();
			}			
			
			this.cycleType = cycleType;
			this.initialKey = key;
			this.key = key;
		}
		else {
			throw new Exception("Illegal discret type");
		}
		
		initDecaPixels(0.0167,0.0334);
		initKey11BitsTab();
		initDelayArray();
	}
	
	public void iterate() {
		this.key = getXorPoly(this.key);
	}
    
	/**
	 * initialize the key11BitsTab
	 * @param key16bits the 16 bits keyword
	 */
	private void initKey11BitsTab(){
		
		//audience 1
		String audience1 = "00000000001";
		this.key11BitsTab[0] = Integer.parseInt(audience1,2);
		
		//audience 2
		String audience2 = "00000000001";
		this.key11BitsTab[1] = Integer.parseInt(audience2,2);
		
		//audience 3
		String audience3 = "00000000001";
		this.key11BitsTab[2] = Integer.parseInt(audience3,2);
		
		//audience 4
		String audience4 =  "00000000001";
		this.key11BitsTab[3] = Integer.parseInt(audience4,2);
		
		//audience 5
		String audience5 = "00000000001";
		this.key11BitsTab[4] = Integer.parseInt(audience5,2);
		
		//audience 6
		String audience6 =  "00000000001";
		this.key11BitsTab[5] = Integer.parseInt(audience6,2);
		
		//audience 7		
		this.key11BitsTab[6] = 1337;
		
		//audience 8		
		this.key11BitsTab[7] = 1;
		
		//audience 9		
		this.key11BitsTab[8] = 1;
		
		//audience 10		
		this.key11BitsTab[9] = 1;
		
		//audience 11		
		this.key11BitsTab[10] = 1;
		
		//audience 12		
		this.key11BitsTab[11] = 1;
		
		//audience 13		
		this.key11BitsTab[12] = 1;
		
		//audience 14		
		this.key11BitsTab[13] = 1;		
	}
	
	
	/**
	 * set the 3 shift pixels value for the decaPixels array
	 * @param perc1 the percentage value of retard 1
	 * @param perc2 the percentage value of retard 2
	 */
	private void initDecaPixels(double perc1, double perc2){
			decaPixels[0] = 0;
			decaPixels[1] = (int)(Math.round(perc1 * this.sWidth )); // previous value : 0.018 0.0167 0.0167
			decaPixels[2] = (int)(Math.round(perc2 * this.sWidth )); // previous value : 0.036 0.0347 0.0334
			
			if (decaPixels[1] == 0){
				decaPixels[1] = 1;
			}
			if (decaPixels[2] == 0 ){
				decaPixels[2] = 2;
			}				
	}
	
	/**
	 * initialize the delay array for the 6 TV frames
	 */
	private void initDelayArray() {
		int z;
		String word = "";
		
		if(this.discretType == SystemType.DISCRET11) {
			z = 0;
			for (int i = 0; i < 6; i++) {				
				for (int j = 0; j < 286; j++) {
					if (i == 0 || i == 1 || i == 2) {
						z = 0;
					} else {
						z = 1;
					}
					
					if(j == 0 && i == 0) {
						this.key = this.initialKey;
						
						word = String.format("%11s",
								Integer.toBinaryString(this.key)).replace(" ", "0");
						word = new StringBuilder(word).reverse().toString();

						key = Integer.parseInt(word, 2);
						
						delayArray[6][i][j] = decaPixels[getDelay(z)];
					}
					else {
						key = getXorPoly(key);
						delayArray[6][i][j] = decaPixels[getDelay(z)];
					}					
				}
			}
		}
		else { // discret12
			z = 1;
			for (int i = 0; i < 6; i++) {
				
				if(i == 1) {
					additionnalIteration(2);
				}
				if(i == 2) {
					additionnalIteration(3);
				}
				if(i == 3) {
					additionnalIteration(4);
				}
				if(i == 4) {
					additionnalIteration(5);
				}
				if(i == 5) {
					additionnalIteration(6);
				}

				
				for (int j = 0; j < 286; j++) {
					if (i == 0 || i == 1 || i == 2) {
						z = 1;
					} else {
						z = 0;
					}
					
					if(j == 0 && i == 0) {
						this.key = this.initialKey;						
						word = String.format("%11s",
								Integer.toBinaryString(this.key)).replace(" ", "0");
						word = new StringBuilder(word).reverse().toString();

						key = Integer.parseInt(word, 2);
						
						iterate(1);
						additionnalIteration(1);
						
						delayArray[6][i][j] = decaPixels[getDelay(z)];
					}
					else {
						key = getXorPoly(key);
						delayArray[6][i][j] = decaPixels[getDelay(z)];
					}					
				}
			}
		}
	}

	private void additionnalIteration(int nbFrame) {
		switch(nbFrame) {
		case 1:
			iterate(2);
			break;
		case 2:
			iterate(1);
			break;
		case 3:
			iterate(0);
			break;
		case 4:
			iterate(3);
			break;
		case 5:
			iterate(2);
			break;
		case 6:
			iterate(1);
			break;		
		}
	}
	
	private void iterate(int nb) {
		for(int i = 0; i < nb; i++) {
			key = getXorPoly(key);
		}
	}
	
	/**
	 * return the result of the poly P(x) = x11 + x9 + 1
	 * @param val the value for the poly
	 * @return the result of the poly
	 */
	private int getXorPoly(int val){
		String key = String.format
				("%11s", Integer.toBinaryString(val)).replace(" ", "0");
		
		int msb = getMSB(val);
		
		int b09 = Integer.parseInt(Character.toString(key.charAt(2)));
		
		int res = msb ^ b09;
		
		key = key.substring(1, 11) + String.valueOf(res);		
		
		return Integer.parseInt(key,2);		
	}
	
	/**
	 * return the LSB value from a binary word
	 * @param word the binary word
	 * @return the LSB value
	 */
	private int getLSB(int word){
		String key = String.format
				("%11s", Integer.toBinaryString(word)).replace(" ", "0");
		
		return Integer.parseInt(Character.toString(key.charAt(key.length()-1)));		
	}
	
	/**
	 * return the MSB value from a binary word
	 * @param word the binary word
	 * @return the MSB value
	 */
	private int getMSB(int word){
		String key = String.format
				("%11s", Integer.toBinaryString(word)).replace(" ", "0");
		
		return Integer.parseInt(Character.toString(key.charAt(0))); 		
	}
	
	
	/**
	 * initialize the delay table for D12, 
	 * we choose the right truth table to be compatible with
	 * the current operational mode of the Discret11 object
	 * truthTable[z][b0][b10]
	 */
//	private void initTruthTableD12() {
//		truthTable[0][0][0] = 2;
//		truthTable[0][0][1] = 1;
//		truthTable[0][1][0] = 0;
//		truthTable[0][1][1] = 0;
//		truthTable[1][0][0] = 0;
//		truthTable[1][0][1] = 2;
//		truthTable[1][1][0] = 2;
//		truthTable[1][1][1] = 1;	
//	}
	
	/**
	 * initialize the delay table for D11, 
	 * we choose the right truth table to be compatible with
	 * the current operational mode of the Discret11 object
	 * truthTable[z][b0][b10]
	 */
	private void initTruthTableD11() {
		truthTable[0][0][0] = 0;
		truthTable[0][0][1] = 1;
		truthTable[0][1][0] = 2;
		truthTable[0][1][1] = 2;
		truthTable[1][0][0] = 2;
		truthTable[1][0][1] = 0;
		truthTable[1][1][0] = 0;
		truthTable[1][1][1] = 1;		
	}
	
	/**
	 * initialize the delay table for D11 in decode mode, 
	 * we choose the right truth table to be compatible with
	 * the current operational mode of the Discret11 object
	 * truthTable[z][b0][b10]
	 */
	private void initTruthTableD11Decode() {
		truthTable[0][0][0] = 2;
		truthTable[0][0][1] = 1;
		truthTable[0][1][0] = 0;
		truthTable[0][1][1] = 0;
		truthTable[1][0][0] = 0;
		truthTable[1][0][1] = 2;
		truthTable[1][1][0] = 2;
		truthTable[1][1][1] = 1;		
	}

	/**
	 * return the theorical delay according the LFSR value
	 * and z value
	 * @param valPoly the LFSR value
	 * @param z the z value
	 * @return the delay to apply on a line
	 */
	public int getDelay(int z){
		int b0 = this.getLSB(this.key);
		int b10 = this.getMSB(this.key);
		return truthTable[z][b0][b10];
	}	
	
	public String getBinaryKey() {
		String word = String.format("%11s", Integer.toBinaryString(this.key)).replace(" ", "0");

		return word;
	}
	
	public int getDiscretType() {
		return discretType;
	}

	public int getKey() {
		return key;
	}

	public int getInitialKey() {
		return initialKey;
	}

	public int getFrame() {
		return frame;
	}

	public int getLine() {
		return line;
	}

	public int getCycleType() {
		return cycleType;
	}

	public int getCurrentZ() {
		return currentZ;
	}
}
