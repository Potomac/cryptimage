package com.ib.cryptimage.core.systems.discret12;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.ib.cryptimage.core.JobConfig;
import com.ib.cryptimage.core.systems.discret12.Discret12Conf;

public class Discret12Listener implements ActionListener, DocumentListener, ChangeListener {
	
	private Discret12Gui discret12Gui;
	
	public Discret12Listener(Discret12Gui discret12Gui) {
		this.discret12Gui = discret12Gui;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		String time = getTime((int)(Discret12Conf.getGui().getRangeSlider().getValue()/JobConfig.getFrameRate()));
		Discret12Conf.getGui().getTxtValueMinRangeSlider()
		                       .setText(Discret12Conf.getGui().getRangeSlider().getValue() + " (" + time + ")");
		
		time = getTime((int)(Discret12Conf.getGui().getRangeSlider().getUpperValue()/JobConfig.getFrameRate()));
		Discret12Conf.getGui().getTxtValueMaxRangeSlider()
		                       .setText(Discret12Conf.getGui().getRangeSlider().getUpperValue() + " (" + time + ")") ;
		
        Discret12Conf.selectedFrameStart = Discret12Conf.getGui().getRangeSlider().getValue();
        Discret12Conf.selectedFrameEnd = Discret12Conf.getGui().getRangeSlider().getUpperValue();
		
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
		
		if (src instanceof JRadioButton) {
			manageRadioButton((JRadioButton) src);
		}
		
	}
	
	private void manageRadioButton(JRadioButton src) {
		if(src.equals(this.discret12Gui.getRdiDiscret12Coding())){
			if(src.isSelected()){
				Discret12Conf.isEncode = true;
				Discret12Conf.isDecode = false;				
				JobConfig.setWantDec(false);	
			}
		}
		else if(src.equals(this.discret12Gui.getRdiDiscret12Decoding())){
			if(src.isSelected()){
				Discret12Conf.isEncode = false;
				Discret12Conf.isDecode = true;				
				JobConfig.setWantDec(true);
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
