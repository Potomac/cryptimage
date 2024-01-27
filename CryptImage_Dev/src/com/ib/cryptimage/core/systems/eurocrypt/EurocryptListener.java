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
 * 2024-01-18 Author Mannix54
 * http://ibsoftware.free.fr/cryptimage.php
 */

package com.ib.cryptimage.core.systems.eurocrypt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFormattedTextField;
import javax.swing.JRadioButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.ib.cryptimage.core.JobConfig;

public class EurocryptListener implements ActionListener, DocumentListener {
	
	private EurocryptGui eurocryptGui;
	
	public EurocryptListener(EurocryptGui eurocryptGui) {
		this.eurocryptGui = eurocryptGui;
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
				
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		manageFormatedTxtFields(this.eurocryptGui.getTxtSeedCode());
		
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		
		if (src instanceof JRadioButton) {
			manageRadioButton((JRadioButton) src);
		}

	}

	private void manageFormatedTxtFields(JFormattedTextField src) {
		if(src.equals(this.eurocryptGui.getTxtSeedCode())){
			EurocryptConf.seedCode = src.getText();
		}		
	}

	private void manageRadioButton(JRadioButton src) {
		if(src.equals(this.eurocryptGui.getRdiMacCoding())){
			if(src.isSelected()){
				EurocryptConf.isEncodeMac = true;
				EurocryptConf.isEncodeMacDecode576pNoEurocrypt = false;
				EurocryptConf.isDecodeMac = false;
				
				JobConfig.setWantDec(false);
				EurocryptConf.width = 1344;
			}
		}
		else if(src.equals(this.eurocryptGui.getRdiMacCoding576p())){
			if(src.isSelected()){
				EurocryptConf.isEncodeMac = false;
				EurocryptConf.isEncodeMacDecode576pNoEurocrypt = true;
				EurocryptConf.isDecodeMac = false;
				
				EurocryptConf.width = 768;
				
				JobConfig.setWantDec(false);
			}		
	    }
		else if(src.equals(this.eurocryptGui.getRdiMacDecoding())){
			if(src.isSelected()){
				EurocryptConf.isEncodeMac = false;
				EurocryptConf.isEncodeMacDecode576pNoEurocrypt = false;
				EurocryptConf.isDecodeMac = true;
				
				EurocryptConf.width = 768;
				
				JobConfig.setWantDec(true);
			}		
	    }
		else if(src.equals(this.eurocryptGui.getRdiEurocryptDisable())){
			if(src.isSelected()){
				EurocryptConf.isDisableEurocrypt = true;
				EurocryptConf.isEurocryptSingleCut = false;
				EurocryptConf.isEurocryptDoubleCut = false;
			}		
	    }
		else if(src.equals(this.eurocryptGui.getRdiEurocryptSingleCut())){
			if(src.isSelected()){
				EurocryptConf.isDisableEurocrypt = false;
				EurocryptConf.isEurocryptSingleCut = true;
				EurocryptConf.isEurocryptDoubleCut = false;
			}		
	    }
		else if(src.equals(this.eurocryptGui.getRdiEurocryptDoubleCut())) {
				EurocryptConf.isDisableEurocrypt = false;
				EurocryptConf.isEurocryptSingleCut = false;
				EurocryptConf.isEurocryptDoubleCut = true;
			}		
	    }	
	

	}

