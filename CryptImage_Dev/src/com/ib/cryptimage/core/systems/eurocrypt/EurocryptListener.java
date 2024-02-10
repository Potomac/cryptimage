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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.ib.cryptimage.core.JobConfig;

public class EurocryptListener implements ActionListener, DocumentListener, ChangeListener {
	
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
				EurocryptConf.isDecode576p = false;
				
				JobConfig.setWantDec(false);
				EurocryptConf.width = 1344;
			}
		}
		else if(src.equals(this.eurocryptGui.getRdiMacCoding576p())){
			if(src.isSelected()){
				EurocryptConf.isEncodeMac = false;
				EurocryptConf.isEncodeMacDecode576pNoEurocrypt = true;
				EurocryptConf.isDecodeMac = false;
				EurocryptConf.isDecode576p = false;
				
				EurocryptConf.width = 768;
				
				JobConfig.setWantDec(false);
			}		
	    }
		else if(src.equals(this.eurocryptGui.getRdiMacDecoding())){
			if(src.isSelected()){
				EurocryptConf.isEncodeMac = false;
				EurocryptConf.isEncodeMacDecode576pNoEurocrypt = false;
				EurocryptConf.isDecodeMac = true;
				EurocryptConf.isDecode576p = false;
				
				EurocryptConf.width = 768;
				
				JobConfig.setWantDec(true);
			}		
	    }
		else if(src.equals(this.eurocryptGui.getRdiMacDecoding576p())){
			if(src.isSelected()){
				EurocryptConf.isEncodeMac = false;
				EurocryptConf.isEncodeMacDecode576pNoEurocrypt = false;
				EurocryptConf.isDecodeMac = false;
				EurocryptConf.isDecode576p = true;
				
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

	@Override
	public void stateChanged(ChangeEvent arg0) {
		// TODO Auto-generated method stub
		String time = getTime((int)(EurocryptConf.getGui().getRangeSlider().getValue()/JobConfig.getFrameRate()));
		EurocryptConf.getGui().getTxtValueMinRangeSlider()
		                       .setText(EurocryptConf.getGui().getRangeSlider().getValue() + " (" + time + ")");
		
		time = getTime((int)(EurocryptConf.getGui().getRangeSlider().getUpperValue()/JobConfig.getFrameRate()));
		EurocryptConf.getGui().getTxtValueMaxRangeSlider()
		                       .setText(EurocryptConf.getGui().getRangeSlider().getUpperValue() + " (" + time + ")") ;
		
        EurocryptConf.selectedFrameStart = EurocryptConf.getGui().getRangeSlider().getValue();
        EurocryptConf.selectedFrameEnd = EurocryptConf.getGui().getRangeSlider().getUpperValue();
		
		//EurocryptConf.frameStart = EurocryptConf.getGui().getRangeSlider().getValue();
		//EurocryptConf.frameEnd = EurocryptConf.getGui().getRangeSlider().getUpperValue();
		
	}	
	
	private String getTime(int timer) {
		int hours = timer / 3600; // get the amount of hours from the seconds
		int remainder = timer % 3600; // get the rest in seconds
		int minutes = remainder / 60; // get the amount of minutes from the rest
		int seconds = remainder % 60; // get the new rest
		String disHour = (hours < 10 ? "0" : "") + hours; // get hours and add "0" before if lower than 10
		String disMinu = (minutes < 10 ? "0" : "") + minutes; // get minutes and add "0" before if lower than 10
		String disSec = (seconds < 10 ? "0" : "") + seconds; // get seconds and add "0" before if lower than 10
		String formattedTime = disHour + ":" + disMinu + ":" + disSec; // get the whole time
		return formattedTime;
	}
	

	}

