package com.ib.cryptimage.core.systems.luxcrypt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.ib.cryptimage.core.JobConfig;


public class LuxcryptListener implements ActionListener, DocumentListener, ChangeListener{

	private LuxcryptGui luxcryptGui;
	
	public LuxcryptListener(LuxcryptGui luxcryptGui) {
		this.luxcryptGui = luxcryptGui;
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		String time = getTime((int)(LuxcryptConf.getGui().getRangeSlider().getValue()/JobConfig.getFrameRate()));
		LuxcryptConf.getGui().getTxtValueMinRangeSlider()
		                       .setText(LuxcryptConf.getGui().getRangeSlider().getValue() + " (" + time + ")");
		
		time = getTime((int)(LuxcryptConf.getGui().getRangeSlider().getUpperValue()/JobConfig.getFrameRate()));
		LuxcryptConf.getGui().getTxtValueMaxRangeSlider()
		                       .setText(LuxcryptConf.getGui().getRangeSlider().getUpperValue() + " (" + time + ")") ;
		
        LuxcryptConf.selectedFrameStart = LuxcryptConf.getGui().getRangeSlider().getValue();
        LuxcryptConf.selectedFrameEnd = LuxcryptConf.getGui().getRangeSlider().getUpperValue();
		
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		
		if (src instanceof JCheckBox) {
			manageCheckBoxes((JCheckBox) src);
		}		
	}
	
	private void manageCheckBoxes(JCheckBox src) {
		if(src.equals(this.luxcryptGui.getChkDisableHorSync())){
			if(src.isSelected()){
				LuxcryptConf.isDisableHorSync = true;
			}
			else {
				LuxcryptConf.isDisableHorSync = false;
			}
		}
		else if(src.equals(this.luxcryptGui.getChkDisableVerSync())){
			if(src.isSelected()){
				LuxcryptConf.isDisableVerSync = true;
			}
			else {
				LuxcryptConf.isDisableVerSync = false;
			}
		}
		else if(src.equals(this.luxcryptGui.getChkInverse())){
			if(src.isSelected()){
				LuxcryptConf.isInverse = true;
			}
			else {
				LuxcryptConf.isInverse = false;
			}
		}
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
