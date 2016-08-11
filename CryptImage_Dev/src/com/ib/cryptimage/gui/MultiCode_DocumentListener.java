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
 * 22 déc. 2014 Author Mannix54
 * http://ibsoftware.free.fr/cryptimage.php
 */



package com.ib.cryptimage.gui;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * @author Mannix54
 *
 */
public class MultiCode_DocumentListener implements DocumentListener {
	
	private MainGui mainGui;

     public MultiCode_DocumentListener(MainGui mainGui) {
		this.mainGui = mainGui;
	}
	
	@Override
	public void changedUpdate(DocumentEvent e) {		
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertUpdate(DocumentEvent e) {		
		computeAudienceMulti(mainGui.getTxtMultiCode().getText());				
	}

	@Override
	public void removeUpdate(DocumentEvent e) {	
		computeAudienceMulti(mainGui.getTxtMultiCode().getText());			
	}
	
	private void computeAudienceMulti(String audienceMulti){
		String word11bits = "(";
		audienceMulti = audienceMulti.replaceAll("#", "");
		audienceMulti = audienceMulti.replaceAll(" ", "");		
		
		boolean[] audienceTab = new boolean[7];
		
		for (int i = 0; i < 7; i++) {
			audienceTab[i] = false;
		}
		
		for (int i = 0; i < audienceMulti.length(); i++) {
			switch (Integer.valueOf(audienceMulti.substring(i, i+1))) {
			case 1:
				audienceTab[0] = true;
				break;
			case 2:
				audienceTab[1] = true;
				break;
			case 3:
				audienceTab[2] = true;
				break;
			case 4:
				audienceTab[3] = true;
				break;
			case 5:
				audienceTab[4] = true;
				break;
			case 6:
				audienceTab[5] = true;
				break;
			case 7:
				audienceTab[6] = true;
				break;
			default:
				break;
			}
		}
		
		
		for (int i = 0; i < 7; i++) {
			if(audienceTab[i] == true){
			word11bits = word11bits + " " + ( i+1 ) + ":" + get11bitsKeyWordMulti(this.mainGui.getSlid16bitsWord().getValue(),
					i+1);	
			}
		}		
		
		word11bits = word11bits.trim();
		word11bits = word11bits + " )";
		
		this.mainGui.getLbl11bitsInfo().setText(word11bits);
	}
	
	/**
	 * get the 11 bits keyword
	 * @param key16bits the 16 bits keyword
	 */
	private String get11bitsKeyWordMulti(int key16bits, int index){
		String word = String.format
				("%16s", Integer.toBinaryString(key16bits)).replace(" ", "0");	
		String audience ="";		
		
		switch (index) {
		case 1:
			//audience 1
			 audience = word.substring(0, 11);			
			break;
		case 2:
			//audience 2
			audience = word.substring(3, 14);			
			break;
		case 3:
			//audience 3
			audience = word.substring(6, 16) + word.charAt(0);			
			break;
		case 4:
			//audience 4
			audience =  word.substring(9, 16) + word.substring(0, 4);			
			break;
		case 5:
			//audience 5
			audience = word.substring(12, 16) +  word.substring(0, 7);			
			break;
		case 6:
			//audience 6
			audience =  word.charAt(15) + word.substring(0, 10) ;			
			break;
		case 7:
			//audience 7		
			return "1337";
			//break;
		default:
			audience = "";
			break;	
		}
		
		return String.format("%04d", Integer.parseInt(audience,2));
	}
	

	
	final Runnable updateGui = new Runnable() {
	     public void run() {
	    	 mainGui.getTxtMultiCode().setText(
	    			 mainGui.getTxtMultiCode().getText().replaceAll(" ", ""));
	     }
	 };

}
