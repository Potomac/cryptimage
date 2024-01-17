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
 * 27 oct. 2014 Author Mannix54
 * http://ibsoftware.free.fr/cryptimage.php
 */



package com.ib.cryptimage.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;

import com.ib.cryptimage.core.CryptPhoto;
import com.ib.cryptimage.core.FramesPlayer;
import com.ib.cryptimage.core.JobConfig;
import com.ib.cryptimage.core.KeyboardCode;
import com.ib.cryptimage.core.StreamsFinder;
import com.ib.cryptimage.core.types.AudioCodecType;
import com.ib.cryptimage.core.types.ColorType;
import com.ib.cryptimage.core.types.ExtensionType;
import com.ib.cryptimage.core.types.SystemType;
import com.ib.cryptimage.core.types.VideoCodecType;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Mannix54
 *
 */
public class MainGui_ActionListener implements ActionListener, ChangeListener,
DocumentListener, FocusListener, KeyListener, MouseListener, WindowListener {
	
	private MainGui mainGui;
	private Thread thread = null;	
		
	
	public MainGui_ActionListener(MainGui mainGui) {
		this.mainGui = mainGui;		
	}

	@Override
	public void actionPerformed(ActionEvent e)  {
		Object src = e.getSource();
		
		if (src instanceof JMenuItem) {
			this.manageMenus((JMenuItem) src);
		} else if((src instanceof JButton)) {			
				this.manageButtons((JButton) src);			
		} else if (src instanceof JTextField) {
			this.manageTxtFields((JTextField) src);
		} else if (src instanceof JFormattedTextField) {
			this.manageFormatedTxtFields((JFormattedTextField) src);	
		} else if (src instanceof JRadioButton) {
			this.manageRadioButton((JRadioButton) src);
		} else if (src instanceof JComboBox<?>) {
			this.manageComboBoxes((JComboBox<?>) src);
		} else if (src instanceof JCheckBox) {
			this.manageCheckBoxes((JCheckBox) src);
		} else if (src instanceof JProgressBar) {
			this.manageProgressBar((JProgressBar) src);
		} else if (src instanceof JTextArea) {
			this.manageTextArea((JTextArea) src);
		} 
		
	}


	private void manageFormatedTxtFields(JFormattedTextField src) {
		// TODO Auto-generated method stub		
		
	}

	private void manageCheckBoxes(JCheckBox src) {		
		if (src.equals(this.mainGui.getChkLogVideocrypt())) {
			if (this.mainGui.getChkLogVideocrypt().isSelected()) {
				JobConfig.setLogVideocrypt(true);
			} 
			else{
				JobConfig.setLogVideocrypt(false);
			}
		}
		
		if(src.equals(this.mainGui.getChkJoinInputOutput())){
			if(this.mainGui.getChkJoinInputOutput().isSelected()) {
				JobConfig.setWantJoinInputOutputFrames(true);
			}
			else {
				JobConfig.setWantJoinInputOutputFrames(false);
			}
		}
		
		if (src.equals(this.mainGui.getChkChangeOffsetIncrement())) {
			if (this.mainGui.getChkChangeOffsetIncrement().isSelected()) {
				JobConfig.setOffsetIncrementChange(true);
			} 
			else{
				JobConfig.setOffsetIncrementChange(false);
			}
		}
		
		if (src.equals(this.mainGui.getCbAveragePal())) {
			if (this.mainGui.getCbAveragePal().isSelected()) {
				JobConfig.setAveragingPal(true);
			} 
			else{
				JobConfig.setAveragingPal(false);
			}
		}

		if(src.equals(this.mainGui.getChkDisableSoundSyster())){
			if(src.isSelected()){
				this.mainGui.getChkSoundSyster().setSelected(false);
				this.mainGui.getChkSoundSyster().setEnabled(false);				
			}
			else{
				this.mainGui.getChkSoundSyster().setSelected(true);
				this.mainGui.getChkSoundSyster().setEnabled(true);		
			}
		}
		
		if(src.equals(this.mainGui.getChkDisableSoundVideocrypt())){
			if(src.isSelected()){
				this.mainGui.getChkSoundVideocrypt().setSelected(false);
				this.mainGui.getChkSoundVideocrypt().setEnabled(false);				
			}
			else{
				//this.mainGui.getChkSoundVideocrypt().setSelected(true);
				this.mainGui.getChkSoundVideocrypt().setEnabled(true);		
			}
		}
		
		if(src.equals(this.mainGui.getChkMaskedBar())){
			JobConfig.setMaskedEdge(
					this.mainGui.getChkMaskedBar().isSelected());
		}
		
		if(src.equals(this.mainGui.getChkNullDelay())){
			if(src.isSelected()){
				this.mainGui.getSlidDelay1().setEnabled(false);
				this.mainGui.getSlidDelay2().setEnabled(false);
				this.mainGui.getChkDelay().setEnabled(false);
				JobConfig.setNullDelay(true);
			}
			else {
				this.mainGui.getSlidDelay1().setEnabled(true);
				this.mainGui.getSlidDelay2().setEnabled(true);
				this.mainGui.getChkDelay().setEnabled(true);
				JobConfig.setNullDelay(false);
			}
			
		}
		
		if (src.equals(this.mainGui.getChkStrictMode())) {			
			if (src.isSelected()) {
				if(this.mainGui.getRdiPhoto().isSelected()){					
					this.mainGui.getRdiDiscretDecoding().setEnabled(false);
					this.mainGui.getRdiDiscretCoding().setSelected(true);
					this.mainGui.getRdiDiscretCorrel().setEnabled(true);
					this.mainGui.getRdiDiscret68705().setEnabled(false);
				}
				if(this.mainGui.getRdiVideo().isSelected()){			
					this.mainGui.getRdiDiscretCorrel().setEnabled(true);
					this.mainGui.getRdiDiscret68705().setEnabled(true);
				}			
				setMultiCodeComboBox();
				this.mainGui.getLbl11bitsInfo().setEnabled(true);
				if(this.mainGui.getRdiDiscretDecoding().isSelected()){
					this.mainGui.getCombAudience().setSelectedIndex(0);
					disableComboAudience();					
				}
				mainGui.getRdi720().setEnabled(true);
				mainGui.getRdi768().setEnabled(true);
				mainGui.getRdi944().setEnabled(true);
			} else {
				if(this.mainGui.getRdiVideo().isSelected()){					
					this.mainGui.getRdiDiscretCorrel().setEnabled(false);
					this.mainGui.getRdiDiscret68705().setEnabled(false);
					this.mainGui.getRdiDiscretCoding().setSelected(true);
				}
				if(this.mainGui.getRdiPhoto().isSelected()){					
					this.mainGui.getRdiDiscretDecoding().setEnabled(true);
					this.mainGui.getRdiDiscretCorrel().setEnabled(false);
					this.mainGui.getRdiDiscret68705().setEnabled(false);
					this.mainGui.getRdiDiscretCoding().setSelected(true);
				}
				setNoMultiCodeComboBox();				
				enableComboAudience();				
				this.mainGui.getLbl11bitsInfo().setEnabled(true);		
				mainGui.getRdi720().setEnabled(false);
				mainGui.getRdi768().setEnabled(false);
				mainGui.getRdi944().setEnabled(false);
			}
		} else if (src.equals(this.mainGui.getChkDelay())) {
			if (src.isSelected()) {
				mainGui.getSlidDelay1().setValue(1670);
				mainGui.getSlidDelay2().setValue(3340);
			}
		} else if (src.equals(this.mainGui.getChkPlayer())) {
			if (src.isSelected()) {
				mainGui.getSlidBitrate().setEnabled(false);
				mainGui.getTxtBitrate().setEnabled(false);
				mainGui.getCombCodec().setEnabled(false);
				mainGui.getCombAudioCodec().setEnabled(false);
				mainGui.getCombAudioRate().setEnabled(false);
				mainGui.getJcbExtension().setEnabled(false);
				mainGui.getChkSound().setEnabled(false);//
				mainGui.getChkDisableSound().setEnabled(false);//
			} else if (mainGui.getRdiPhoto().isSelected() != true) {
				mainGui.getSlidBitrate().setEnabled(true);
				mainGui.getTxtBitrate().setEnabled(true);
				mainGui.getCombCodec().setEnabled(true);
				if(!mainGui.getChkDisableSound().isSelected()){
				mainGui.getCombAudioCodec().setEnabled(true);
				mainGui.getCombAudioRate().setEnabled(true);
				if(mainGui.getCombAudioCodec().getSelectedIndex()== AudioCodecType.WAV){
					mainGui.getJcbExtension().setSelectedIndex(ExtensionType.MKV);
					mainGui.getJcbExtension().setEnabled(false);					
				}
				else{
					mainGui.getJcbExtension().setEnabled(true);
				}
				mainGui.getChkSound().setEnabled(true);
				}
				else{
					mainGui.getJcbExtension().setEnabled(true);
				}
				//mainGui.getJcbExtension().setEnabled(true);
				//mainGui.getChkSound().setEnabled(true);
				mainGui.getChkDisableSound().setEnabled(true);
				mainGui.getChkHorodatage().setEnabled(true);
			}
		} else if (src.equals(this.mainGui.getChkAutorisation1())) {
			// update code
			updateKeyboardCode(mainGui.getTxtSerial().getText());
		} else if (src.equals(this.mainGui.getChkAutorisation2())) {
			// update code
			updateKeyboardCode(mainGui.getTxtSerial().getText());
		} else if (src.equals(this.mainGui.getChkAutorisation3())) {
			// update code
			updateKeyboardCode(mainGui.getTxtSerial().getText());
		} else if (src.equals(this.mainGui.getChkAutorisation4())) {
			// update code
			updateKeyboardCode(mainGui.getTxtSerial().getText());
		} else if (src.equals(this.mainGui.getChkAutorisation5())) {
			// update code
			updateKeyboardCode(mainGui.getTxtSerial().getText());
		} else if (src.equals(this.mainGui.getChkAutorisation6())) {
			// update code
			updateKeyboardCode(mainGui.getTxtSerial().getText());
		}else if (src.equals(this.mainGui.getChkHorodatage())) {			
			JobConfig.setHorodatage(mainGui.getChkHorodatage().isSelected());
		}else if (src.equals(this.mainGui.getChkDisableSound())) {			
			if(src.isSelected()){
				mainGui.getChkSound().setSelected(false);
				mainGui.getChkSound().setEnabled(false);
				mainGui.getCombAudioCodec().setEnabled(false);
				mainGui.getCombAudioRate().setEnabled(false);
				if(mainGui.getCombAudioCodec().getSelectedIndex()== AudioCodecType.WAV){
					mainGui.getJcbExtension().setSelectedIndex(ExtensionType.MKV);
					mainGui.getJcbExtension().setEnabled(true);
				}
			}
			else {
				mainGui.getChkSound().setSelected(true);
				mainGui.getChkSound().setEnabled(true);
				mainGui.getCombAudioCodec().setEnabled(true);
				mainGui.getCombAudioRate().setEnabled(true);
				if(mainGui.getCombAudioCodec().getSelectedIndex()== AudioCodecType.WAV){
					mainGui.getJcbExtension().setSelectedIndex(ExtensionType.MKV);
					mainGui.getJcbExtension().setEnabled(false);
				}
				else {
					mainGui.getJcbExtension().setEnabled(true);
				}
			}
		}		
		
	}

	private void manageTextArea(JTextArea src) {		
	
		
	}

	private void manageProgressBar(JProgressBar src) {
		// TODO Auto-generated method stub
		
	}

	private void manageComboBoxes(JComboBox<?> src) {
		
		if(src.equals(this.mainGui.getCmbPalFrameStart())){			
			JobConfig.setCurrentPalFrameDec((int)this.mainGui.getCmbPalFrameStart().getSelectedItem() - 1);
		}
		
		if(src.equals(this.mainGui.getCbYUV())){
			if(this.mainGui.getCbYUV().getSelectedIndex() == 0){
				JobConfig.setTypeYUV(0);				
			}
			if(this.mainGui.getCbYUV().getSelectedIndex() == 1){
				JobConfig.setTypeYUV(1);				
			}
			if(this.mainGui.getCbYUV().getSelectedIndex() == 2){
				JobConfig.setTypeYUV(2);				
			}
			if(this.mainGui.getCbYUV().getSelectedIndex() == 3){
				JobConfig.setTypeYUV(3);				
			}
			if(this.mainGui.getCbYUV().getSelectedIndex() == 4){
				JobConfig.setTypeYUV(4);				
			}
			if(this.mainGui.getCbYUV().getSelectedIndex() == 5){
				JobConfig.setTypeYUV(5);				
			}
		}
		
		if(src.equals(this.mainGui.getCbColorMode())){
			JobConfig.setColorMode(this.mainGui.getCbColorMode().getSelectedIndex());
			if(this.mainGui.getCbColorMode().getSelectedIndex()== ColorType.RGB){
				this.mainGui.getCbAveragePal().setEnabled(false);
				JobConfig.getGui().getCardPal().show(JobConfig.getGui().getPanOptionsPalComposite(), "dummy");
			}
			if(this.mainGui.getCbColorMode().getSelectedIndex()== ColorType.PAL){
				this.mainGui.getCbAveragePal().setEnabled(true);
				JobConfig.getGui().getCardPal().show(JobConfig.getGui().getPanOptionsPalComposite(), "dummy");
			}
			if(this.mainGui.getCbColorMode().getSelectedIndex()== ColorType.SECAM){
				JobConfig.getGui().getCardPal().show(JobConfig.getGui().getPanOptionsPalComposite(), "dummy");
			}
			if(this.mainGui.getCbColorMode().getSelectedIndex() == ColorType.SECAM
					|| this.mainGui.getCbColorMode().getSelectedIndex() == ColorType.PAL_COMPOSITE_DEC
					|| this.mainGui.getCbColorMode().getSelectedIndex() == ColorType.PAL_COMPOSITE_ENC 
					|| this.mainGui.getCbColorMode().getSelectedIndex() == ColorType.PAL_COMPOSITE_ENC_DEC){
				this.mainGui.getCbAveragePal().setEnabled(false);				
			}
			if(this.mainGui.getCbColorMode().getSelectedIndex() == ColorType.PAL_COMPOSITE_DEC
					|| this.mainGui.getCbColorMode().getSelectedIndex() == ColorType.PAL_COMPOSITE_ENC 
					|| this.mainGui.getCbColorMode().getSelectedIndex() == ColorType.PAL_COMPOSITE_ENC_DEC){
				JobConfig.getGui().getCardPal().show(JobConfig.getGui().getPanOptionsPalComposite(), "sampling_rate");
			}			
			
		}		

		if(src.equals(this.mainGui.getCombSystemCrypt())){
            int combSystemIndex = this.mainGui.getCombSystemCrypt().getSelectedIndex();
            
            if(SystemType.DISCRET11 == combSystemIndex) {
           	 setDiscret11(combSystemIndex);
            }
            else if(SystemType.SYSTER == combSystemIndex) {
           	 setSyster(combSystemIndex);
            }
            else if(SystemType.VIDEOCRYPT == combSystemIndex) {
           	 setVideocrypt(combSystemIndex);
            }
            else if(SystemType.TRANSCODE == combSystemIndex) {
           	 setTranscode(combSystemIndex);
            } 
		}
		
		if (src.equals(mainGui.getCombAudioCodec())) {
			if(mainGui.getCombAudioCodec().getSelectedIndex()== AudioCodecType.WAV 
					|| mainGui.getCombAudioCodec().getSelectedIndex()== AudioCodecType.FLAC){
		
				mainGui.getJcbExtension().setSelectedIndex(ExtensionType.MKV);
				mainGui.getJcbExtension().setEnabled(false);
			}
			else {
				if (mainGui.getTextInfos().getForeground() == Color.red){
					mainGui.getTextInfos().setText("");
				}				
				mainGui.getJcbExtension().setEnabled(true);
			}
		}
		
		if (src.equals(mainGui.getJcbExtension())) {
			if (src.getSelectedIndex() == ExtensionType.MP4
					&& this.mainGui.getCombCodec().getSelectedIndex() == VideoCodecType.HUFFYUV) {
				this.mainGui.getCombCodec().setSelectedIndex(VideoCodecType.H264);
			}
			if (src.getSelectedIndex() == ExtensionType.MPEG || src.getSelectedIndex() == ExtensionType.TS) {
				mainGui.getCombCodec().setSelectedIndex(VideoCodecType.MPEG2);
				mainGui.getCombCodec().setEnabled(false);
			} else {
				mainGui.getCombCodec().setEnabled(true);
			}
		} else if (src.equals(mainGui.getCombCodec())) {
			if (src.getSelectedIndex() == VideoCodecType.HUFFYUV
					&& this.mainGui.getJcbExtension().getSelectedIndex() == ExtensionType.MP4) {
				this.mainGui.getJcbExtension().setSelectedIndex(ExtensionType.AVI);
			}
		} else if(src.equals(mainGui.getCombAudience())){
			if (mainGui.getCombAudience().getSelectedIndex() == 7){
				mainGui.getTxtMultiCode().setEnabled(true);
				mainGui.getJspCycle().setEnabled(true);
				mainGui.getLblMultiAudience().setEnabled(true);
				mainGui.getLblCycle().setEnabled(true);
				computeAudienceMulti(mainGui.getTxtMultiCode().getText());
			}
			else {
				mainGui.getTxtMultiCode().setEnabled(false);
				mainGui.getJspCycle().setEnabled(false);
				mainGui.getLblMultiAudience().setEnabled(false);
				mainGui.getLblCycle().setEnabled(false);
				get11bitsKeyWord(mainGui.getSlid16bitsWord().getValue(),
						mainGui.getCombAudience().getSelectedIndex());				
			}			
		}
	}

	private void setDiscret11(int index) {
		JobConfig.setSystemCrypt(index);
		this.mainGui.getCmbPalFreq().setSelectedIndex(1);
		this.mainGui.getCard().show(this.mainGui.getPanSystemCrypt(), "Discret11");
		this.mainGui.getChkStrictMode().setEnabled(true);
	}

	private void setSyster(int index) {
		JobConfig.setSystemCrypt(index);
		this.mainGui.getCmbPalFreq().setSelectedIndex(1);
		this.mainGui.getCard().show(this.mainGui.getPanSystemCrypt(), "Syster");
		if (!this.mainGui.getChkStrictMode().isSelected()) {
			setMultiCodeComboBox();
		}
		this.mainGui.getChkStrictMode().setSelected(true);
		if (this.mainGui.getRdiVideo().isSelected()) {
			this.mainGui.getRdiDiscretCorrel().setEnabled(true);
			this.mainGui.getRdiDiscretDecoding().setEnabled(true);
			this.mainGui.getRdiDiscretCoding().setSelected(true);
			this.mainGui.getSlid16bitsWord().setEnabled(true);
			this.mainGui.getJsp16bitKeyword().setEnabled(true);
			enableComboAudience();
		}
		if (this.mainGui.getRdiPhoto().isSelected()) {
			this.mainGui.getRdiDiscretCorrel().setEnabled(true);
			this.mainGui.getRdiDiscretDecoding().setEnabled(false);
			this.mainGui.getRdiDiscretCoding().setSelected(true);
			this.mainGui.getSlid16bitsWord().setEnabled(true);
			this.mainGui.getJsp16bitKeyword().setEnabled(true);
			enableComboAudience();
		}
		mainGui.getRdi720().setEnabled(true);
		mainGui.getRdi768().setEnabled(true);
		mainGui.getRdi944().setEnabled(true);
		// setMultiCodeComboBox();
		this.mainGui.getChkStrictMode().setEnabled(false);
	}

	private void setVideocrypt(int index) {
		JobConfig.setSystemCrypt(index);
		this.mainGui.getCmbPalFreq().setSelectedIndex(0);
		this.mainGui.getCard().show(this.mainGui.getPanSystemCrypt(), "Videocrypt");
		if (!this.mainGui.getChkStrictMode().isSelected()) {
			setMultiCodeComboBox();
		}
		this.mainGui.getChkStrictMode().setSelected(true);
		if (this.mainGui.getRdiVideo().isSelected()) {
			this.mainGui.getRdiDiscretCorrel().setEnabled(true);
			this.mainGui.getRdiDiscretDecoding().setEnabled(true);
			this.mainGui.getRdiDiscretCoding().setSelected(true);
			this.mainGui.getSlid16bitsWord().setEnabled(true);
			this.mainGui.getJsp16bitKeyword().setEnabled(true);
			enableComboAudience();
		}
		if (this.mainGui.getRdiPhoto().isSelected()) {
			this.mainGui.getRdiDiscretCorrel().setEnabled(true);
			this.mainGui.getRdiDiscretDecoding().setEnabled(false);
			this.mainGui.getRdiDiscretCoding().setSelected(true);
			this.mainGui.getSlid16bitsWord().setEnabled(true);
			this.mainGui.getJsp16bitKeyword().setEnabled(true);
			enableComboAudience();
		}
		mainGui.getRdi720().setEnabled(true);
		mainGui.getRdi768().setEnabled(true);
		mainGui.getRdi944().setEnabled(true);
		// setMultiCodeComboBox();
		this.mainGui.getChkStrictMode().setEnabled(false);

	}

	private void setTranscode(int index) {
		JobConfig.setSystemCrypt(index);
		this.mainGui.getCmbPalFreq().setSelectedIndex(1);
		this.mainGui.getCard().show(this.mainGui.getPanSystemCrypt(), "Transcode");
		if (!this.mainGui.getChkStrictMode().isSelected()) {
			setMultiCodeComboBox();
		}
		this.mainGui.getChkStrictMode().setSelected(true);
		if (this.mainGui.getRdiVideo().isSelected()) {
			this.mainGui.getRdiDiscretCorrel().setEnabled(true);
			this.mainGui.getRdiDiscretDecoding().setEnabled(true);
			this.mainGui.getRdiDiscretCoding().setSelected(true);
			this.mainGui.getSlid16bitsWord().setEnabled(true);
			this.mainGui.getJsp16bitKeyword().setEnabled(true);
			enableComboAudience();
		}
		if (this.mainGui.getRdiPhoto().isSelected()) {
			this.mainGui.getRdiDiscretCorrel().setEnabled(true);
			this.mainGui.getRdiDiscretDecoding().setEnabled(false);
			this.mainGui.getRdiDiscretCoding().setSelected(true);
			this.mainGui.getSlid16bitsWord().setEnabled(true);
			this.mainGui.getJsp16bitKeyword().setEnabled(true);
			enableComboAudience();
		}
		mainGui.getRdi720().setEnabled(true);
		mainGui.getRdi768().setEnabled(true);
		mainGui.getRdi944().setEnabled(true);
		// setMultiCodeComboBox();
		this.mainGui.getChkStrictMode().setEnabled(false);
	}
	
	private void manageSliders(JSlider src) {
		
		
		if(src.equals(this.mainGui.getSlidBrightness())) {
			mainGui.getJtxtValBrightness().setText(String.valueOf(mainGui.getSlidBrightness().getValue()));
		}
		
		if(src.equals(this.mainGui.getSlidColor())) {
			mainGui.getJtxtValColor().setText(String.valueOf(mainGui.getSlidColor().getValue()));
		}
		
		if(src.equals(this.mainGui.getSldWhiteValue())) {
			mainGui.getSlpWhiteValue().setValue(mainGui.getSldWhiteValue().getValue());
			JobConfig.setWhiteValue((int) src.getValue());
		}
		
		if(src.equals(this.mainGui.getSlid16bitsWord())){	
			mainGui.getTxt16bitsWord().setText(String.format("%05d",
					mainGui.getSlid16bitsWord().getValue()));
			mainGui.getJsp16bitKeyword().setValue(
					mainGui.getSlid16bitsWord().getValue());
			//update code
			updateKeyboardCode(mainGui.getTxtSerial().getText());			
			if(mainGui.getCombAudience().getSelectedIndex() == 7){
				computeAudienceMulti(mainGui.getTxtMultiCode().getText());
			}
			else {
				get11bitsKeyWord(mainGui.getSlid16bitsWord().getValue(),
						mainGui.getCombAudience().getSelectedIndex());
			}
		}  else if(src.equals(this.mainGui.getSlidDelay1())){
			if(src.getValue()!=1670){
				mainGui.getChkDelay().setSelected(false);
			} else if(src.getValue()==1670 &&
					mainGui.getSlidDelay2().getValue()== 3340){
				mainGui.getChkDelay().setSelected(true);
			}
			mainGui.getTxtDelay1().setText(String.valueOf(
					mainGui.getSlidDelay1().getValue()/1000d) + "%");
		} else if(src.equals(this.mainGui.getSlidDelay2())){
			if(src.getValue()!=3340){
				mainGui.getChkDelay().setSelected(false);
			} else if(src.getValue()==3340 &&
					mainGui.getSlidDelay1().getValue()== 1670){
				mainGui.getChkDelay().setSelected(true);
			}
			mainGui.getTxtDelay2().setText(String.valueOf(
					mainGui.getSlidDelay2().getValue()/1000d) + "%");
		} else if(src.equals(mainGui.getSlideFrameStart())){
			mainGui.getJspFrameStart().setValue(
					mainGui.getSlideFrameStart().getValue());
		} 
		else if(src.equals(mainGui.getSlideFrameStartSyster())){
			mainGui.getJspFrameStartSyster().setValue(
					mainGui.getSlideFrameStartSyster().getValue());
		} 	else if(src.equals(mainGui.getSlideFrameStartVideocrypt())){
			mainGui.getJspFrameStartVideocrypt().setValue(
					mainGui.getSlideFrameStartVideocrypt().getValue());
		}else if(src.equals(mainGui.getSlidBitrate())){
			mainGui.getTxtBitrate().setText(String.valueOf(src.getValue()));
		}else if(src.equals(mainGui.getSlidFrames())){
			String time = "";
			if(JobConfig.getFrameRate() > 0){
				time = getTime((int) (mainGui.getSlidFrames().getValue()/JobConfig.getFrameRate()));
			}
			mainGui.getTxtNbFrames().setText(String.valueOf(src.getValue()) + " ("+ time +")");
		}
	}
	
	private void manageSpinners(JSpinner src){
		
		if(src.equals(this.mainGui.getSlpWhiteValue())) {
			mainGui.getSldWhiteValue().setValue((int) src.getValue());
			JobConfig.setWhiteValue((int) src.getValue());
		}
		
		if(src.equals(this.mainGui.getJsp16bitKeyword())){
			mainGui.getSlid16bitsWord().setValue(
					(int) src.getValue());			
		} else if(src.equals(this.mainGui.getJspFrameStart())){
			mainGui.getSlideFrameStart().setValue(
					(int) src.getValue());
		} else if(src.equals(this.mainGui.getJspFrameStartSyster())){
			mainGui.getSlideFrameStartSyster().setValue(
					(int) src.getValue());
		} else if(src.equals(this.mainGui.getJspFrameStartVideocrypt())){
			mainGui.getSlideFrameStartVideocrypt().setValue(
					(int) src.getValue());
		}
	}

	private void manageRadioButton(JRadioButton src) {
		
		// 4/3 options
		if(src.equals(this.mainGui.getRdiLetterbox())){
			JobConfig.setPanAndScan(false);
			JobConfig.setStretch(false);
		}
		if(src.equals(this.mainGui.getRdiPanScan())){
			JobConfig.setPanAndScan(true);
			JobConfig.setStretch(false);
		}
		if(src.equals(this.mainGui.getRdiStretch())){
			JobConfig.setPanAndScan(false);
			JobConfig.setStretch(true);
		}
		
		if(src.equals(this.mainGui.getRdiVideocryptCoding())){
			if(src.isSelected()){
				this.mainGui.getCardEncDecVideocrypt().show(this.mainGui.getPanVideocryptEncDecCard(),
						"VideocryptCoding");			
			}
		}
		
		if(src.equals(this.mainGui.getRdiVideocryptDecoding())){
			if(src.isSelected()){
				if(src.isSelected()){
					this.mainGui.getCardEncDecVideocrypt().show(this.mainGui.getPanVideocryptEncDecCard(),
							"VideocryptDecoding");			
				}			
			}
		}
		
		if(src.equals(this.mainGui.getRdiVideocryptTagsDecoding())){
			if(src.isSelected()){				
				this.mainGui.getTxtVideocryptDec().setEnabled(false);
				this.mainGui.getBtnVideocryptDec().setEnabled(false);	
			}
		}
		
		if(src.equals(this.mainGui.getRdiVideocryptCorrel())){
			if(src.isSelected()){				
				this.mainGui.getTxtVideocryptDec().setEnabled(false);
				this.mainGui.getBtnVideocryptDec().setEnabled(false);	
			}
		}
		
		if(src.equals(this.mainGui.getRdiVideocryptDecodingFile())){
			if(src.isSelected()){				
				this.mainGui.getTxtVideocryptDec().setEnabled(true);
				this.mainGui.getBtnVideocryptDec().setEnabled(true);	
			}
		}
		
		if(src.equals(this.mainGui.getRdiVideocryptCodingAuto())){
			if(src.isSelected()){				
				this.mainGui.getTxtVideocryptEnc().setEnabled(false);
				this.mainGui.getBtnVideocryptEnc().setEnabled(false);
			}
		}
		
		if(src.equals(this.mainGui.getRdiVideocryptCodingFile())){
			if(src.isSelected()){				
				this.mainGui.getTxtVideocryptEnc().setEnabled(true);
				this.mainGui.getBtnVideocryptEnc().setEnabled(true);				
			}
		}	
		
		
		if(src.equals(this.mainGui.getRdiSysterCodingRandom())){
			if(src.isSelected()){
				this.mainGui.getChkChangeOffsetIncrement().setEnabled(true);
				this.mainGui.getTxtSysterEnc().setEnabled(false);
				this.mainGui.getBtnFileSysterEnc().setEnabled(false);
				this.mainGui.getComboTableSysterEnc().setEnabled(true);
			}
		}
		if(src.equals(this.mainGui.getRdiSysterCodingFile())){
			if(src.isSelected()){
				this.mainGui.getChkChangeOffsetIncrement().setEnabled(false);
				this.mainGui.getTxtSysterEnc().setEnabled(true);
				this.mainGui.getBtnFileSysterEnc().setEnabled(true);
				this.mainGui.getComboTableSysterEnc().setEnabled(false);
			}
		}
		if(src.equals(this.mainGui.getRdiSysterDeCodingFile())){
			if(src.isSelected()){
				this.mainGui.getTxtSysterDec().setEnabled(true);
				this.mainGui.getBtnFileSysterDec().setEnabled(true);
				this.mainGui.getComboTableSysterDec().setEnabled(false);
				this.mainGui.getChkSysterReverse().setSelected(false);
				this.mainGui.getChkSysterReverse().setEnabled(false);
			}
		}
		if(src.equals(this.mainGui.getRdiSysterDeCodingTags())){
			if(src.isSelected()){
				this.mainGui.getTxtSysterDec().setEnabled(false);
				this.mainGui.getBtnFileSysterDec().setEnabled(false);
				this.mainGui.getComboTableSysterDec().setEnabled(false);
				this.mainGui.getChkSysterReverse().setSelected(false);
				this.mainGui.getChkSysterReverse().setEnabled(false);
			}
		}
		if(src.equals(this.mainGui.getRdiSysterDecodingCorrel())){
			if(src.isSelected()){
				this.mainGui.getTxtSysterDec().setEnabled(false);
				this.mainGui.getBtnFileSysterDec().setEnabled(false);
				this.mainGui.getComboTableSysterDec().setEnabled(true);
				this.mainGui.getChkSysterReverse().setSelected(false);
				this.mainGui.getChkSysterReverse().setEnabled(true);
			}
		}
		
		
		if(src.equals(this.mainGui.getRdiDiscretCorrel())){
			if(src.isSelected()){
				this.mainGui.getSlid16bitsWord().setEnabled(false);
				this.mainGui.getJsp16bitKeyword().setEnabled(false);
				disableComboAudience();	
				if(!this.mainGui.getChkStrictMode().isSelected()){
					setMultiCodeComboBox();
				}
				this.mainGui.getChkStrictMode().setSelected(true);
				if(this.mainGui.getRdiPhoto().isSelected()){
					this.mainGui.getRdiDiscretDecoding().setEnabled(false);
				}
				mainGui.getRdi720().setEnabled(true);
				mainGui.getRdi768().setEnabled(true);
				mainGui.getRdi944().setEnabled(true);
				//setMultiCodeComboBox();
				this.mainGui.getChkStrictMode().setEnabled(false);				
			}	
		}
		if(src.equals(this.mainGui.getRdiDiscretDecoding())){
			this.mainGui.getChkStrictMode().setEnabled(true);
			if(src.isSelected()){
				this.mainGui.getSlid16bitsWord().setEnabled(true);
				this.mainGui.getJsp16bitKeyword().setEnabled(true);
			}
		}
		if(src.equals(this.mainGui.getRdiDiscret68705())){
			this.mainGui.getChkStrictMode().setEnabled(true);
			disableComboAudience();	
			if(src.isSelected()){
				this.mainGui.getSlid16bitsWord().setEnabled(true);
				this.mainGui.getJsp16bitKeyword().setEnabled(true);
			}
		}
		
		if(src.equals(this.mainGui.getRdiDiscretCoding())){
			this.mainGui.getChkStrictMode().setEnabled(true);
			if(src.isSelected()){
				this.mainGui.getSlid16bitsWord().setEnabled(true);
				this.mainGui.getJsp16bitKeyword().setEnabled(true);
			}
		}
		
		if(src.equals(mainGui.getRdiSysterCodingGen())){
			if(src.isSelected()){
				this.mainGui.getCardEncDecSyster().show(this.mainGui.getPanSysterEncDecCard(),
						"SysterCoding");
			}			
		}
		if(src.equals(mainGui.getRdiSysterDecodingGen())){
			if(src.isSelected()){
				this.mainGui.getCardEncDecSyster().show(this.mainGui.getPanSysterEncDecCard(),
						"SysterDecoding");
			}			
		}		
		
		if(src.equals(mainGui.getRdiPhoto())){			
			if(src.isSelected()){
				this.mainGui.getRdiDiscret68705().setEnabled(false);
				//**setNoMultiCodeComboBox();
				if(this.mainGui.getChkStrictMode().isSelected()){
					this.mainGui.getRdiDiscretDecoding().setEnabled(false);	
					this.mainGui.getRdiDiscretCoding().setSelected(true);
					this.mainGui.getSlid16bitsWord().setEnabled(true);
					this.mainGui.getJsp16bitKeyword().setEnabled(true);
					this.mainGui.getRdiDiscretCorrel().setEnabled(true);
					this.mainGui.getRdiDiscret68705().setEnabled(false);
					if (mainGui.getCombSystemCrypt().getSelectedIndex() == SystemType.DISCRET11) {
						this.mainGui.getChkStrictMode().setEnabled(true);
					}
				}
				else{
					this.mainGui.getRdiDiscretDecoding().setEnabled(true);	
					//this.mainGui.getRdiDiscret68705().setEnabled(true);
				}
				if (!this.mainGui.getRdiDiscretDecoding().isSelected()
						&& !this.mainGui.getRdiDiscretCorrel().isSelected()) {
					enableComboAudience();
					this.mainGui.getLbl11bitsInfo().setEnabled(true);
				}
				mainGui.getTxtInputFile().setText("");				
				mainGui.getBtnEnter().setEnabled(false);
				//mainGui.getBtnOutputFile().setEnabled(false);
				mainGui.getSlidBitrate().setEnabled(false);
				mainGui.getTxtBitrate().setEnabled(false);				
				mainGui.getCombCodec().setEnabled(false);
				mainGui.getCombAudioCodec().setEnabled(false);
				mainGui.getCombAudioRate().setEnabled(false);
				mainGui.getJcbExtension().setEnabled(false);
				mainGui.getSlidFrames().setEnabled(false);
				mainGui.getTxtNbFrames().setEnabled(false);
				//**mainGui.getChkStrictMode().setSelected(false);
				//**mainGui.getChkStrictMode().setEnabled(false);				
				//**mainGui.getRdi720().setEnabled(false);
				//**mainGui.getRdi768().setEnabled(false);
				mainGui.getChkPlayer().setSelected(false);;
				mainGui.getChkPlayer().setEnabled(false);
				mainGui.getChkSound().setEnabled(false);
				mainGui.getChkDisableSound().setEnabled(false);
				//mainGui.getChkHorodatage().setEnabled(false);				
			}
		} else if(src.equals(mainGui.getRdiVideo())){
			
			this.mainGui.getRdiDiscretDecoding().setEnabled(true);
			this.mainGui.getRdiDiscret68705().setEnabled(true);
			
			if(!this.mainGui.getChkStrictMode().isSelected()){
				setMultiCodeComboBox();
				//this.mainGui.getRdiDiscret68705().setEnabled(false);
			}
			else{
				this.mainGui.getRdiDiscretCorrel().setEnabled(true);
				this.mainGui.getRdiDiscret68705().setEnabled(true);
				this.mainGui.getRdiDiscretCoding().setSelected(true);
				this.mainGui.getSlid16bitsWord().setEnabled(true);
				this.mainGui.getJsp16bitKeyword().setEnabled(true);
				if (mainGui.getCombSystemCrypt().getSelectedIndex() == SystemType.DISCRET11) {
					this.mainGui.getChkStrictMode().setEnabled(true);
				}
				enableComboAudience();
			}
			//setMultiCodeComboBox();			
			if(this.mainGui.getRdiDiscretDecoding().isSelected() || 
					this.mainGui.getRdiDiscretCorrel().isSelected()){
				disableComboAudience();
				this.mainGui.getLbl11bitsInfo().setEnabled(false);
			}
			if(src.isSelected() && mainGui.getChkPlayer().isSelected()!=true){
				mainGui.getTxtInputFile().setText("");				
				mainGui.getBtnEnter().setEnabled(false);
				//mainGui.getBtnOutputFile().setEnabled(false);
				mainGui.getSlidBitrate().setEnabled(true);
				mainGui.getTxtBitrate().setEnabled(true);				
				mainGui.getCombCodec().setEnabled(true);
				if(!mainGui.getChkDisableSound().isSelected()){
					mainGui.getCombAudioCodec().setEnabled(true);
					mainGui.getCombAudioRate().setEnabled(true);
					mainGui.getChkSound().setEnabled(true);						
				}
				if(mainGui.getCombAudioCodec().getSelectedIndex() == AudioCodecType.WAV && 
						!mainGui.getChkDisableSound().isSelected()){
					mainGui.getJcbExtension().setEnabled(false);
				}
				else{
					mainGui.getJcbExtension().setEnabled(true);
				}
				//mainGui.getJcbExtension().setEnabled(true);
				mainGui.getSlidFrames().setEnabled(true);
				mainGui.getTxtNbFrames().setEnabled(true);
				if(this.mainGui.getCombSystemCrypt().getSelectedIndex() != SystemType.VIDEOCRYPT
					 && this.mainGui.getCombSystemCrypt().getSelectedIndex() != SystemType.SYSTER
					 && !this.mainGui.getRdiDiscretCorrel().isSelected()){
					mainGui.getChkStrictMode().setEnabled(true);
					this.mainGui.getRdiDiscretCorrel().setEnabled(true);
					//this.mainGui.getRdiDiscretCoding().setSelected(true);
				}
				mainGui.getChkStrictMode().setSelected(true);				
				//mainGui.getCombAudience().setEnabled(true);
				mainGui.getRdi720().setEnabled(true);
				mainGui.getRdi768().setEnabled(true);
				mainGui.getRdi944().setEnabled(true);
				mainGui.getChkPlayer().setEnabled(true);
				//mainGui.getChkSound().setEnabled(true);
				mainGui.getChkDisableSound().setEnabled(true);
				mainGui.getChkHorodatage().setEnabled(true);				
				
			} else if(src.isSelected()){
				mainGui.getTxtInputFile().setText("");				
				mainGui.getBtnEnter().setEnabled(false);
				//mainGui.getBtnOutputFile().setEnabled(false);
				mainGui.getSlidFrames().setEnabled(true);
				if(this.mainGui.getCombSystemCrypt().getSelectedIndex() != SystemType.SYSTER){
					mainGui.getChkStrictMode().setEnabled(true);
				}
				mainGui.getChkStrictMode().setSelected(true);
				mainGui.getRdi720().setEnabled(true);
				mainGui.getRdi768().setEnabled(true);
				mainGui.getRdi944().setEnabled(true);
				mainGui.getChkPlayer().setEnabled(true);
				mainGui.getChkHorodatage().setEnabled(true);
				if(mainGui.getCombAudioCodec().getSelectedIndex()== AudioCodecType.WAV){
					mainGui.getJcbExtension().setEnabled(false);
				}
				else{
					//mainGui.getJcbExtension().setEnabled(true);
				}				
				
			}
		} else if(src.equals(mainGui.getRdiDiscretCoding())){			
			enableComboAudience();
			this.mainGui.getLbl11bitsInfo().setEnabled(true);
		} else if(src.equals(mainGui.getRdiDiscretDecoding())){
			if(this.mainGui.getChkStrictMode().isSelected() == false){
				if(this.mainGui.getCombAudience().getItemCount() == 7){
					setNoMultiCodeComboBox();
				}
				enableComboAudience();
			} else {
				disableComboAudience();
			}			
		}
	}
	
//	private void rdiVideoSelected(){
//		if(mainGui.getRdiVideo().isSelected()){
//			setMultiCodeComboBox();			
//			if(this.mainGui.getRdiDiscretDecoding().isSelected()){
//				disableComboAudience();
//				this.mainGui.getLbl11bitsInfo().setEnabled(false);
//			}
//			if(mainGui.getRdiVideo().isSelected() && mainGui.getChkPlayer().isSelected()!=true){
//				mainGui.getTxtInputFile().setText("");				
//				mainGui.getBtnEnter().setEnabled(false);
//				//mainGui.getBtnOutputFile().setEnabled(false);
//				mainGui.getSlidBitrate().setEnabled(true);
//				mainGui.getTxtBitrate().setEnabled(true);				
//				mainGui.getCombCodec().setEnabled(true);
//				if(!mainGui.getChkDisableSound().isSelected()){
//					mainGui.getCombAudioCodec().setEnabled(true);
//					mainGui.getChkSound().setEnabled(true);						
//				}
//				if(mainGui.getCombAudioCodec().getSelectedIndex()== 6 && 
//						!mainGui.getChkDisableSound().isSelected()){
//					mainGui.getJcbExtension().setEnabled(false);
//				}
//				else{
//					mainGui.getJcbExtension().setEnabled(true);
//				}
//				//mainGui.getJcbExtension().setEnabled(true);
//				mainGui.getSlidFrames().setEnabled(true);
//				mainGui.getTxtNbFrames().setEnabled(true);
//				mainGui.getChkStrictMode().setEnabled(true);
//				mainGui.getChkStrictMode().setSelected(true);
//				//mainGui.getCombAudience().setEnabled(true);
//				mainGui.getRdi720().setEnabled(true);
//				mainGui.getRdi768().setEnabled(true);
//				mainGui.getChkPlayer().setEnabled(true);
//				//mainGui.getChkSound().setEnabled(true);
//				mainGui.getChkDisableSound().setEnabled(true);
//				mainGui.getChkHorodatage().setEnabled(true);				
//				
//			} else if(mainGui.getRdiVideo().isSelected()){
//				mainGui.getTxtInputFile().setText("");				
//				mainGui.getBtnEnter().setEnabled(false);
//				//mainGui.getBtnOutputFile().setEnabled(false);
//				mainGui.getSlidFrames().setEnabled(true);
//				mainGui.getChkStrictMode().setEnabled(true);
//				mainGui.getChkStrictMode().setSelected(true);
//				mainGui.getRdi720().setEnabled(true);
//				mainGui.getRdi768().setEnabled(true);
//				mainGui.getChkPlayer().setEnabled(true);
//				mainGui.getChkHorodatage().setEnabled(true);
//				if(mainGui.getCombAudioCodec().getSelectedIndex()== 6){
//					mainGui.getJcbExtension().setEnabled(false);
//				}
//				else{
//					//mainGui.getJcbExtension().setEnabled(true);
//				}				
//			}
//		}
//	}

	private void manageTxtFields(JTextField src) {
		// TODO Auto-generated method stub
		
	}
	
	private void manageMenus(JMenuItem src){
		if(src.equals(this.mainGui.getmOpen())){
			manageFileOpen();
		}
		else if (src.equals(this.mainGui.getmExit())){
			saveConfig();
			System.exit(0);
		} else if (src.equals(this.mainGui.getmAbout())){
			showAbout();
		}else if (src.equals(this.mainGui.getmGen())){
			genRandomSyster();
		}else if (src.equals(this.mainGui.getmDocumentation())){
			launchDocumentation();
		}
		else if(src.equals(this.mainGui.getmAuto())){			
			manageLang();
			JobConfig.setLang(0);
		}
		else if(src.equals(this.mainGui.getmGerman())){			
			JobConfig.setRes(ResourceBundle.getBundle("ressources/mainGui", Locale.GERMAN)); 
			this.mainGui.refreshGUI();
			JobConfig.setLang(1);
		}else if(src.equals(this.mainGui.getmEnglish())){			
			JobConfig.setRes(ResourceBundle.getBundle("ressources/mainGui", Locale.ENGLISH)); 
			this.mainGui.refreshGUI();
			JobConfig.setLang(2);
		}else if(src.equals(this.mainGui.getmSpanish())){			
			JobConfig.setRes(ResourceBundle.getBundle("ressources/mainGui", new Locale("es"))); 
			this.mainGui.refreshGUI();
			JobConfig.setLang(3);
		}else if(src.equals(this.mainGui.getmFrench())){			
			JobConfig.setRes(ResourceBundle.getBundle("ressources/mainGui", Locale.FRENCH)); 
			this.mainGui.refreshGUI();
			JobConfig.setLang(4);
		}else if(src.equals(this.mainGui.getmItalian())){			
			JobConfig.setRes(ResourceBundle.getBundle("ressources/mainGui", new Locale("it"))); 
			this.mainGui.refreshGUI();
			JobConfig.setLang(5);
		}else if(src.equals(this.mainGui.getmPolish())){			
			JobConfig.setRes(ResourceBundle.getBundle("ressources/mainGui", new Locale("pl"))); 
			this.mainGui.refreshGUI();
			JobConfig.setLang(6);
		}
	}
	
	private void manageLang(){
		if (Locale.getDefault().getLanguage().equals("de")){
			JobConfig.setRes(ResourceBundle.getBundle("ressources/mainGui", Locale.GERMAN)); 
		} else if(Locale.getDefault().getLanguage().equals("en")){
			JobConfig.setRes(ResourceBundle.getBundle("ressources/mainGui", Locale.ENGLISH)); 
			}
		else if(Locale.getDefault().getLanguage().equals("es")){
			JobConfig.setRes(ResourceBundle.getBundle("ressources/mainGui", new Locale("es"))); 
				}
		else if(Locale.getDefault().getLanguage().equals("fr")){
			JobConfig.setRes(ResourceBundle.getBundle("ressources/mainGui", Locale.FRENCH)); 
				}
		else if(Locale.getDefault().getLanguage().equals("it")){
			JobConfig.setRes(ResourceBundle.getBundle("ressources/mainGui", new Locale("it"))); 
			}
		else if(Locale.getDefault().getLanguage().equals("pl")){
			JobConfig.setRes(ResourceBundle.getBundle("ressources/mainGui", new Locale("pl"))); 
			}		
		else{
			JobConfig.setRes(ResourceBundle.getBundle("ressources/mainGui", Locale.ENGLISH)); 
		}
		this.mainGui.refreshGUI();
	}
	
	private void launchDocumentation() {
		
		String binPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
	
		String help_name_file = "";
		if (JobConfig.getUserLanguage().equals("fr")) {
			help_name_file = "cryptimage_fr.pdf";
		} else if (JobConfig.getUserLanguage().equals("pl")) {
			help_name_file = "cryptimage_pl.pdf";
		} else if (JobConfig.getUserLanguage().equals("en")) {
			help_name_file = "cryptimage_en.pdf";
		} else {
			help_name_file = "cryptimage_en.pdf";
		}
		
		File config = new File(binPath + help_name_file);

		if (config.exists()) {			
			// run pdf program in order to launch documentation
			if (Desktop.isDesktopSupported()) {
				try {					
					Desktop.getDesktop().open(new File(binPath + help_name_file));
				} catch (IOException ex) {
					JOptionPane
					.showMessageDialog(
							mainGui.getFrame(),
							JobConfig.getRes().getString("mainGui.help.errorLaunch"),
							JobConfig.getRes().getString("mainGui.help.errorLaunch.title"),
							JOptionPane.ERROR_MESSAGE);
				}
			}

		}
		else{
			//we try to see if documentation exists in user home

			// test if cryptimage directory exists
			boolean success = new File(System.getProperty("user.home")
					+ File.separator + "cryptimage").exists();

			if (success == false) {
				success = new File(System.getProperty("user.home") 
						+ File.separator + "cryptimage").mkdir();
			}
			
			String userHome = System.getProperty("user.home");

			File configPDF = new File(userHome + File.separator + "cryptimage"
					+ File.separator + help_name_file );
			
			if (configPDF.exists()) {				
				// run pdf program in order to launch documentation
				if (Desktop.isDesktopSupported()) {
					try {						
						Desktop.getDesktop().open(configPDF);
					} catch (IOException ex) {
						JOptionPane
						.showMessageDialog(
								mainGui.getFrame(),
								JobConfig.getRes().getString("mainGui.help.errorLaunchUserDir"),
								JobConfig.getRes().getString("mainGui.help.errorLaunch.title"),
								JOptionPane.ERROR_MESSAGE);
					}
				}

			}
			else {
				// we install the documentation to the home user
				this.mainGui.installHelpFile(help_name_file);
				 
				 if (Desktop.isDesktopSupported()) {
						try {						
							Desktop.getDesktop().open(configPDF);
						} catch (IOException ex) {
							JOptionPane
							.showMessageDialog(
									mainGui.getFrame(),
									JobConfig.getRes().getString("mainGui.help.errorLaunchUserDirGeneral"),
									JobConfig.getRes().getString("mainGui.help.errorLaunch.title"),
									JOptionPane.ERROR_MESSAGE);
						}
					}				
			}			
		}
	}
	
	private void genRandomSyster(){		
		GenEncFile gen = new GenEncFile(mainGui.getFrame(), JobConfig.getRes().getString("manageGenFile.title"), true);
		gen.display();
	}

	private void manageButtons(JButton src) {
		
		if(src.equals(this.mainGui.getBtnVideocryptDec()) ||
				src.equals(this.mainGui.getBtnVideocryptEnc())){
			openFileVideocrypt();
		}
		
		if(src.equals(this.mainGui.getBtnFileSysterEnc())){			
			openFileSysterEnc();
		}
		
		if(src.equals(this.mainGui.getBtnFileSysterDec())){
			openFileSysterDec();
		}
		
		if(src.equals(mainGui.getBtnExit())){
			saveConfig();
			System.exit(0);
		} else if(src.equals(mainGui.getBtnInputFile())){
			try {
				manageFileOpen();
			} catch (Exception e) {
				JOptionPane
				.showMessageDialog(
						mainGui.getFrame(),
						JobConfig.getRes().getString("mainGui.exceptionError") + " " +
						e.getMessage(),
						JobConfig.getRes().getString("mainGui.exceptionError.title"),
						JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
			
		} else if(src.equals(mainGui.getBtnEnter())){
			try {
				manageEnter();
			} catch (Exception e) {
				JOptionPane
				.showMessageDialog(
						mainGui.getFrame(),
						JobConfig.getRes().getString("mainGui.exceptionError") + " " +
						e.getMessage(),
						JobConfig.getRes().getString("mainGui.exceptionError.title"),
						JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}			
		} else if(src.equals(mainGui.getBtnCancel())){
			manageCancel();
		} else if(src.equals(mainGui.getBtnOutputFile())){
			manageSave();
		} else if(src.equals(mainGui.getBtnAbout())){
			showAbout();
		}
		
	}
	
	private void showAbout() {
				
		JLabel label = new JLabel();

		String deb = "CryptImage v" + JobConfig.getVERSION() + "<br/>"
				+ "Copyright (C) "+ JobConfig.getReleaseDate() + " Mannix54 <br/>";

		String link = "<a href=\"http://ibsoftware.free.fr/cryptimage.php\">http://ibsoftware.free.fr/cryptimage.php</a>"
				+ "<br/>";
		
		String link2 = "<a href=\"http://cryptimage.vot.pl/\">http://cryptimage.vot.pl/</a>"
				+ "<br/><br/>";

		String end = "under the GNU GPL v3 license <br/><br/>"
				+ "CryptImage comes with ABSOLUTELY NO WARRANTY<br/>"
				+ "This is free software, and you are welcome to redistribute"
				+ " it under certain conditions.";

		JEditorPane ep = new JEditorPane("text/html", "<html><body>" //
				+ deb + link + link2 + end + "</body></html>");
		
		ep.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {					
					Desktop desktop = Desktop.isDesktopSupported() ? Desktop
							.getDesktop() : null;
					if (desktop != null
							&& desktop.isSupported(Desktop.Action.BROWSE)) {
						try {

							desktop.browse(e.getURL().toURI());
						} catch (Exception e2) {
							e2.printStackTrace();
						}
					}
				}
			}

		});

		ep.setEditable(false);
		ep.setBackground(label.getBackground());
		
		JPanel pan = new JPanel();
		JButton btnLicense = new JButton(JobConfig.getRes().getString("showAbout.license"));
		btnLicense.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ShowLicense showLicense = new ShowLicense();
				showLicense.show();				
			}
		});
		
		JPanel panBtn = new JPanel();
		panBtn.setLayout(new BorderLayout());
		panBtn.add(btnLicense, BorderLayout.WEST);
		
		pan.setLayout(new BorderLayout());
		pan.add(ep, BorderLayout.CENTER);
		pan.add(panBtn, BorderLayout.SOUTH);

		JOptionPane.showMessageDialog(null, pan, JobConfig.getRes().getString("showAbout.about"),
				JOptionPane.INFORMATION_MESSAGE);
	}

	private void manageSave() {
		JFileChooser dialogue = new JFileChooser();
				
		dialogue.setLocale(new Locale(JobConfig.getUserLanguage()));
		dialogue.updateUI();
		
		
		dialogue.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		dialogue.setDialogTitle(JobConfig.getRes().getString("manageSave.dialogTitle"));
		
		File file;
		if (dialogue.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			file = dialogue.getSelectedFile();
			if (!file.canRead()) {
				JOptionPane
						.showMessageDialog(
								dialogue,
								JobConfig.getRes().getString("manageSave.errorFolder"),
								JobConfig.getRes().getString("manageSave.errorFolder.title"),
								JOptionPane.WARNING_MESSAGE);
			} else {
				mainGui.getTxtOutputFile().setText(file.getAbsolutePath());
			}
		}
	}
	
	private void openFileSysterEnc(){
		JFileChooser dialogue = new JFileChooser();
				
		dialogue.setLocale(new Locale(JobConfig.getUserLanguage()));
		dialogue.updateUI();
		
		dialogue.setDialogTitle(JobConfig.getRes().getString("openFileSysterEnc.dialogTitle"));
		File file;

		dialogue.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter;
		String[] extension;

		extension = new String[] { "enc" };
		filter = new FileNameExtensionFilter(JobConfig.getRes().getString("openFileSysterEnc.filters"), extension);
		
		if(new File(this.mainGui.getTxtOutputFile().getText()).exists()){
			dialogue.setCurrentDirectory(new File(this.mainGui.getTxtOutputFile().getText()));
		}

		dialogue.setFileFilter(filter);

		if (dialogue.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			mainGui.getFrame().setCursor(
					Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			file = dialogue.getSelectedFile();

			if (!file.canRead()) {
				mainGui.getFrame().setCursor(
						Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				JOptionPane
						.showMessageDialog(
								dialogue,
								JobConfig.getRes().getString("openFileSysterEnc.errorIO"),
								JobConfig.getRes().getString("openFileSysterEnc.errorIO.title"),
								JOptionPane.ERROR_MESSAGE);
			} else {
				//TODO
				if (checkValidityFileEncDec(file, "enc")) {
					this.mainGui.getTxtSysterEnc().setText(file.getAbsolutePath());					
				}
				else{
					this.mainGui.getTxtSysterEnc().setText("");
				}
			}
			mainGui.getFrame().setCursor(
					Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		
	}
	
	private void openFileVideocrypt(){
		JFileChooser dialogue = new JFileChooser();
		
		dialogue.setLocale(new Locale(JobConfig.getUserLanguage()));
		dialogue.updateUI();
		
		dialogue.setDialogTitle(JobConfig.getRes().getString("openFileVideocrypt.dialogTitle"));
		File file;

		FileNameExtensionFilter filter;
		String[] extension;
		
		if(new File(this.mainGui.getTxtOutputFile().getText()).exists()){
			dialogue.setCurrentDirectory(new File(this.mainGui.getTxtOutputFile().getText()));
		}

		dialogue.setAcceptAllFileFilterUsed(false);
		extension = new String[] { "vid" };
		filter = new FileNameExtensionFilter(JobConfig.getRes().getString("openFileVideocrypt.filters"), extension);
		

		dialogue.setFileFilter(filter);

		if (dialogue.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			mainGui.getFrame().setCursor(
					Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			file = dialogue.getSelectedFile();

			if (!file.canRead()) {
				mainGui.getFrame().setCursor(
						Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				JOptionPane
						.showMessageDialog(
								dialogue,
								JobConfig.getRes().getString("openFileVideocrypt.errorIO"),
								JobConfig.getRes().getString("openFileVideocrypt.errorIO.title"),
								JOptionPane.ERROR_MESSAGE);
			} else {				
				//check the validity of the file
				if (checkValidityFileVideocrypt(file)) {
					if(this.mainGui.getRdiVideocryptDecodingFile().isSelected() 
							&& this.mainGui.getRdiVideocryptDecoding().isSelected())
					this.mainGui.getTxtVideocryptDec().setText(file.getAbsolutePath());
					
					if(this.mainGui.getRdiVideocryptCodingFile().isSelected()
							&& this.mainGui.getRdiVideocryptCoding().isSelected())
					this.mainGui.getTxtVideocryptEnc().setText(file.getAbsolutePath());
				
				}
				else{
					if(this.mainGui.getRdiVideocryptDecodingFile().isSelected() 
							&& this.mainGui.getRdiVideocryptDecoding().isSelected())
					this.mainGui.getTxtVideocryptDec().setText("");
					
					if(this.mainGui.getRdiVideocryptCodingFile().isSelected()
							&& this.mainGui.getRdiVideocryptCoding().isSelected())
						this.mainGui.getTxtVideocryptEnc().setText("");
				}
			}
			mainGui.getFrame().setCursor(
					Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	private boolean checkValidityFileVideocrypt(File file){
		BufferedReader fileInBuff = null;
		
		try {
			fileInBuff = new BufferedReader(new FileReader(file.getAbsolutePath()));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
	
	String line;
	String[] para;
	int compt = 0;
	int val;	
	int nbSkip = 0;

		try {
			while ((line = fileInBuff.readLine()) != null) {
				compt++;				
				if (!line.equals("skip")) {
					para = line.split(";");
					//check random value
					val = Integer.valueOf(para[0]);						
					if ( val > 16777216 || val < 1){						
						JOptionPane.showMessageDialog(mainGui.getFrame(),
								JobConfig.getRes().getString("mainGui.validityError.title"),
								JobConfig.getRes().getString("mainGui.validityError.title"), JOptionPane.ERROR_MESSAGE);
						return false; 
					}
					//check range start
					val = Integer.valueOf(para[1]);					
					if (val < 1 || val > 128) {
						JOptionPane.showMessageDialog(mainGui.getFrame(),
								JobConfig.getRes().getString("mainGui.validityError.title"),
								JobConfig.getRes().getString("mainGui.validityError.title"), JOptionPane.ERROR_MESSAGE);
						return false;
					}
					//check range end
					val = Integer.valueOf(para[2]);					
					if (val < 129 || val > 255) {
						JOptionPane.showMessageDialog(mainGui.getFrame(),
								JobConfig.getRes().getString("mainGui.validityError.title"),
								JobConfig.getRes().getString("mainGui.validityError.title"), JOptionPane.ERROR_MESSAGE);
						return false;
					}
				}else{
					nbSkip++;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		catch ( Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane
			.showMessageDialog(
					mainGui.getFrame(),
					JobConfig.getRes().getString("mainGui.validityError.title"),
					JobConfig.getRes().getString("mainGui.validityError.title"),
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
	
	
	try {
		fileInBuff.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	//check if possible the size of lines with the frame lenght of the video
		if(!this.mainGui.getTxtInputFile().getText().equals("")){			
			if(compt  < mainGui.getSlideFrameStart().getMaximum()
					&& this.mainGui.getRdiVideo().isSelected()){
				JOptionPane
				.showMessageDialog(
						mainGui.getFrame(),
						JobConfig.getRes().getString("mainGui.validityError.file.name") 
						+ " " + JobConfig.getRes().getString("mainGui.validityError.file.name2"),
						JobConfig.getRes().getString("mainGui.validityError.file.name3"),
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
			
			if(compt  < 1
					&& this.mainGui.getRdiPhoto().isSelected()
					&& this.mainGui.getRdiVideocryptDecoding().isSelected()){
				JOptionPane
				.showMessageDialog(
						mainGui.getFrame(),
						JobConfig.getRes().getString("mainGui.validityError.file.name") 
						+ " " + JobConfig.getRes().getString("mainGui.validityError.file.name5"),
						JobConfig.getRes().getString("mainGui.validityError.file.name3"),
						JOptionPane.ERROR_MESSAGE);
				return false;
			}		
		}
		
		JobConfig.setNbSkippedFrames(nbSkip);		
	
	return true;
	}
	
	
	private void openFileSysterDec(){
		JFileChooser dialogue = new JFileChooser();
				
		dialogue.setLocale(new Locale(JobConfig.getUserLanguage()));
		dialogue.updateUI();
		
		dialogue.setDialogTitle(JobConfig.getRes().getString("openFileSysterDec.dialogTitle"));
		File file;

		FileNameExtensionFilter filter;
		String[] extension;
		
		if(new File(this.mainGui.getTxtOutputFile().getText()).exists()){
			dialogue.setCurrentDirectory(new File(this.mainGui.getTxtOutputFile().getText()));
		}

		dialogue.setAcceptAllFileFilterUsed(false);
		extension = new String[] { "dec" };
		filter = new FileNameExtensionFilter(JobConfig.getRes().getString("openFileSysterDec.filters"), extension);
		

		dialogue.setFileFilter(filter);

		if (dialogue.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			mainGui.getFrame().setCursor(
					Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			file = dialogue.getSelectedFile();

			if (!file.canRead()) {
				mainGui.getFrame().setCursor(
						Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				JOptionPane
						.showMessageDialog(
								dialogue,
								JobConfig.getRes().getString("openFileSysterDec.errorIO"),
								JobConfig.getRes().getString("openFileSysterDec.errorIO.title"),
								JOptionPane.ERROR_MESSAGE);
			} else {
				//TODO
				//check the validity of the file
				if (checkValidityFileEncDec(file, "dec")) {
					this.mainGui.getTxtSysterDec().setText(file.getAbsolutePath());
				}
				else{
					this.mainGui.getTxtSysterDec().setText("");
				}
			}
			mainGui.getFrame().setCursor(
					Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	private boolean checkValidityFileEncDec(File file, String decEnc){
		BufferedReader fileInBuff = null;
		
			try {
				fileInBuff = new BufferedReader(new FileReader(file.getAbsolutePath()));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}		
		
		String line;
		int compt = 0;
		String[] options;	
	
			try {
				while ((line = fileInBuff.readLine()) != null ) {
					compt++;
					options = line.split(";");
					if (options.length == 1){
						JOptionPane
						.showMessageDialog(
								mainGui.getFrame(),
								JobConfig.getRes().getString("mainGui.validityError.blankLine") + " " 
								+ compt + " " 
					     		+ JobConfig.getRes().getString("mainGui.validityError.file") 
								+ " " + decEnc,
								JobConfig.getRes().getString("mainGui.validityError.title") + " " + decEnc,
								JOptionPane.ERROR_MESSAGE);
						fileInBuff.close();
						return false;
					}
					if(checkValuesTags(options[1], options[2], 
							options[4], options[3],options[5]) == false){
						JOptionPane
						.showMessageDialog(
								mainGui.getFrame(),
								JobConfig.getRes().getString("mainGui.validityError.syntax") 
								+ " " + compt + " " 
								+ JobConfig.getRes().getString("mainGui.validityError.file")  
								+ " " + decEnc,
								JobConfig.getRes().getString("mainGui.validityError.title") + " " + decEnc,
								JOptionPane.ERROR_MESSAGE);
						fileInBuff.close();
						return false;
					}
				}
			} catch ( IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			catch ( Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane
				.showMessageDialog(
						mainGui.getFrame(),
						JobConfig.getRes().getString("mainGui.validityError.title") + " " + decEnc
						+ ", " + e.getMessage() ,
						JobConfig.getRes().getString("mainGui.validityError.title") + " " + decEnc,
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
		
		
		try {
			fileInBuff.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//check if possible the size of lines with the frame lenght of the video
			if(!this.mainGui.getTxtInputFile().getText().equals("")){
				int corr = 0;
				if(decEnc.equals("dec")){
					corr = 1 ;
				}
				if(compt + corr < mainGui.getSlideFrameStart().getMaximum()
						&& this.mainGui.getRdiVideo().isSelected()){
					JOptionPane
					.showMessageDialog(
							mainGui.getFrame(),
							JobConfig.getRes().getString("mainGui.validityError.file.name") + " " + decEnc
							+ " " + JobConfig.getRes().getString("mainGui.validityError.file.name2"),
							JobConfig.getRes().getString("mainGui.validityError.file.name3") + " " + decEnc,
							JOptionPane.ERROR_MESSAGE);
					return false;
				}
				if(compt + corr < 3
						&& this.mainGui.getRdiPhoto().isSelected()
						&& this.mainGui.getRdiSysterCodingGen().isSelected()){
					JOptionPane
					.showMessageDialog(
							mainGui.getFrame(),
							JobConfig.getRes().getString("mainGui.validityError.file.name") + " " + decEnc
							+ " " + JobConfig.getRes().getString("mainGui.validityError.file.name4"),
							JobConfig.getRes().getString("mainGui.validityError.file.name3") + " " + decEnc,
							JOptionPane.ERROR_MESSAGE);
					return false;
				}
				if(compt + corr < 2
						&& this.mainGui.getRdiPhoto().isSelected()
						&& this.mainGui.getRdiSysterDecodingGen().isSelected()){
					JOptionPane
					.showMessageDialog(
							mainGui.getFrame(),
							JobConfig.getRes().getString("mainGui.validityError.file.name") + " " + decEnc
							+ " " + JobConfig.getRes().getString("mainGui.validityError.file.name5"),
							JobConfig.getRes().getString("mainGui.validityError.file.name3") + " " + decEnc,
							JOptionPane.ERROR_MESSAGE);
					return false;
				}		
			}
		
		
		
		return true;
	}
	
	private boolean checkValuesTags(String typeTable, String offsetBin1, String offsetBin2,
			String incrementBin1, String incrementBin2){
		
		try {
			if (typeTable.equals("skip") && offsetBin1.equals("skip") 
					&& offsetBin2.equals("skip")
					&& incrementBin1.equals("skip") 
					&& incrementBin2.equals("skip")) {
				return true;
			}			
			if(Integer.parseInt(typeTable) >0 && Integer.parseInt(typeTable) <3){
				if(Integer.parseInt(offsetBin1) >= 0 && Integer.parseInt(offsetBin1) < 256){
					if(Integer.parseInt(offsetBin2) >= 0 && Integer.parseInt(offsetBin2) < 256){
						if(Integer.parseInt(incrementBin1) > 0 && Integer.parseInt(incrementBin1) < 256){
							if(isOdd(Integer.parseInt(incrementBin1))){
								if(Integer.parseInt(incrementBin2) > 0 && Integer.parseInt(incrementBin2) < 256){
									if(isOdd(Integer.parseInt(incrementBin2))){
										return true;
									}
								}
							}
						}
					}
				}
			}
			
		} catch (Exception e) {
			return false;
		}
		
		return false;
	}
	
	private boolean isOdd(int val){
		double test = (double) val;
		if ((test / 2d) - (int)(test / 2d) != 0) {
			return true;
		}
		else 
			return false;
	}
	
	
	
	private void manageFileOpen() {
		JFileChooser dialogue = new JFileChooser();		

		
		dialogue.setLocale(new Locale(JobConfig.getUserLanguage()));
		dialogue.updateUI();
				
		dialogue.setDialogTitle(JobConfig.getRes().getString("manageFileOpen.dialogTitle"));
		File file;

		FileNameExtensionFilter filter;
		String[] extension;
		
		if(new File(this.mainGui.getTxtOutputFile().getText()).exists()){
			dialogue.setCurrentDirectory(new File(this.mainGui.getTxtOutputFile().getText()));
		}
		

		if (mainGui.getRdiVideo().isSelected()) {
			extension = new String[] { "avi", "mp4", "mpeg", "mkv", "mpeg2",
					"mpg","ts", "m2t", "wmv", "mov", "vob" };
			filter = new FileNameExtensionFilter(JobConfig.getRes().getString("manageFileOpen.filenameExtensionFiltersVideo"), extension);
		} else {
			extension = new String[] { "jpeg", "jpg", "bmp", "gif", "png",
					"tiff" };
			filter = new FileNameExtensionFilter(JobConfig.getRes().getString("manageFileOpen.filenameExtensionFiltersPhoto"), extension);
		}

		dialogue.setFileFilter(filter);

		if (dialogue.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			mainGui.getFrame().setCursor(
					Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			file = dialogue.getSelectedFile();

			if (!file.canRead()) {
				mainGui.getFrame().setCursor(
						Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				JOptionPane
						.showMessageDialog(
								dialogue,
								JobConfig.getRes().getString("manageFileOpen.errorOpen"),
								JobConfig.getRes().getString("manageFileOpen.errorOpenTitle"),
								JOptionPane.ERROR_MESSAGE);
			} else {

				if (mainGui.getRdiVideo().isSelected()) {
					JobConfig.setHasToBeUnsplit(false);
					setVideosInfos(file.getAbsolutePath());
					if(mainGui.getTxtOutputFile().getText().equals("")){
						mainGui.getTxtOutputFile().setText(file.getParent());
					}					
					mainGui.getBtnOutputFile().setEnabled(true);
					
//					if(JobConfig.isHasMultiAudioChannels()) {
//						showWarningSound();
//					}
					
				} else {
					JobConfig.setHasToBeUnsplit(false);
					mainGui.getTxtInputFile().setText(file.getAbsolutePath());
					if(mainGui.getTxtOutputFile().getText().equals("")){
						mainGui.getTxtOutputFile().setText(file.getParent());
					}
					mainGui.getBtnOutputFile().setEnabled(true);
					mainGui.getBtnEnter().setEnabled(true);
				}
			}
			//reset enc and dec files
			this.mainGui.getTxtSysterEnc().setText("");
			this.mainGui.getTxtSysterDec().setText("");
			this.mainGui.getTxtVideocryptDec().setText("");
			this.mainGui.getTxtVideocryptEnc().setText("");
			mainGui.getFrame().setCursor(
					Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	private void manageEnter() {
		JobConfig.setSearchCode68705(false);
		if (mainGui.getCombAudience().getSelectedIndex() == 7
				&& mainGui.getTxtMultiCode().getText().replaceAll("#", "")
						.replaceAll(" ", "").equals("")
						&& mainGui.getCombSystemCrypt().getSelectedIndex() == SystemType.DISCRET11) {
			JOptionPane
					.showMessageDialog(
							mainGui.getFrame(),
							JobConfig.getRes().getString("manageEnter.errorMulticode"),
							JobConfig.getRes().getString("manageEnter.errorMulticodeTitle"),
							JOptionPane.WARNING_MESSAGE);
		} 
		else if(mainGui.getCombSystemCrypt().getSelectedIndex() == SystemType.SYSTER
				&& mainGui.getTxtSysterEnc().getText().equals("")
				&& mainGui.getRdiSysterCodingGen().isSelected()
				&& mainGui.getRdiSysterCodingFile().isSelected()){
			JOptionPane
			.showMessageDialog(
					mainGui.getFrame(),
					JobConfig.getRes().getString("manageEnter.errorSysterFileEnc"),
					JobConfig.getRes().getString("manageEnter.errorSysterFileEncTitle"),
					JOptionPane.WARNING_MESSAGE);		
		}else if(mainGui.getCombSystemCrypt().getSelectedIndex() == SystemType.VIDEOCRYPT
				&& mainGui.getTxtVideocryptDec().getText().equals("")
				&& mainGui.getRdiVideocryptDecodingFile().isSelected()
				&& mainGui.getRdiVideocryptDecoding().isSelected()){		
			JOptionPane
			.showMessageDialog(
					mainGui.getFrame(),
					JobConfig.getRes().getString("manageEnter.errorVideocryptFile"),
					JobConfig.getRes().getString("manageEnter.errorVideocryptTitle"),
					JOptionPane.WARNING_MESSAGE);		
		}
		else if(mainGui.getCombSystemCrypt().getSelectedIndex() == SystemType.VIDEOCRYPT
				&& mainGui.getTxtVideocryptEnc().getText().equals("")
				&& mainGui.getRdiVideocryptCodingFile().isSelected()
				&& mainGui.getRdiVideocryptCoding().isSelected()){
			JOptionPane
			.showMessageDialog(
					mainGui.getFrame(),
					JobConfig.getRes().getString("manageEnter.errorVideocryptFile"),
					JobConfig.getRes().getString("manageEnter.errorVideocryptTitle"),
					JOptionPane.WARNING_MESSAGE);		
		}
		else if(mainGui.getCombSystemCrypt().getSelectedIndex() == SystemType.SYSTER
				&& mainGui.getTxtSysterDec().getText().equals("")
				&& mainGui.getRdiSysterDecodingGen().isSelected()
				&& mainGui.getRdiSysterDeCodingFile().isSelected()){
			JOptionPane
			.showMessageDialog(
					mainGui.getFrame(),
					JobConfig.getRes().getString("manageEnter.errorSysterFileDec"),
					JobConfig.getRes().getString("manageEnter.errorSysterFileDecTitle"),
					JOptionPane.WARNING_MESSAGE);		
		}		
		else {
			mainGui.getTextInfos().setForeground(Color.black);

			mainGui.getFrame().setCursor(
					Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			mainGui.getBtnEnter().setEnabled(false);
			mainGui.getBtnCancel().setEnabled(true);
			mainGui.getBtnExit().setEnabled(false);
			mainGui.getBtnInputFile().setEnabled(false);
			mainGui.getBtnOutputFile().setEnabled(false);

			JobConfig.setInput_file(mainGui.getTxtInputFile().getText());
			if (mainGui.getTxtOutputFile().getText().equals("")) {
				JobConfig.setOutput_file(
						mainGui.getTxtInputFile().getText());
			} else {
				File fic = new File(mainGui.getTxtInputFile().getText());
				if (fic.getName().lastIndexOf(".") != -1) {
					JobConfig.setOutput_file(
							mainGui.getTxtOutputFile().getText()
									+ File.separator
									+ fic.getName().substring(0,
											fic.getName().lastIndexOf(".")));
				} else {
					JobConfig.setOutput_file(
							mainGui.getTxtOutputFile().getText()
									+ File.separator + fic.getName());
				}
			}
						
			// system crypt
			JobConfig.setSystemCrypt(this.mainGui.getCombSystemCrypt().getSelectedIndex());
			
			//discret correl
			if(this.mainGui.getRdiDiscretCorrel().isSelected()
					&& JobConfig.getSystemCrypt() == SystemType.DISCRET11){
				JobConfig.setWantDecCorrel(true);
				JobConfig.setWantDec(true);
			}			
			else {
				JobConfig.setWantDecCorrel(false);
			}
						
			JobConfig.setWord16bits(
					Integer.valueOf(mainGui.getTxt16bitsWord().getText()));

			JobConfig.setVideo_frame(
					Integer.valueOf(mainGui.getSlidFrames().getValue()));			
			JobConfig.setStrictMode(
					mainGui.getChkStrictMode().isSelected());
			
			if (JobConfig.getSystemCrypt() == SystemType.DISCRET11) {
				JobConfig.setPositionSynchro(
						(int) mainGui.getJspFrameStart().getValue());
			} else if(JobConfig.getSystemCrypt() == SystemType.SYSTER) {
				JobConfig.setPositionSynchro(
						(int) mainGui.getJspFrameStartSyster().getValue());
			}else if(JobConfig.getSystemCrypt() == SystemType.VIDEOCRYPT) {
				JobConfig.setPositionSynchro(
						(int) mainGui.getJspFrameStartVideocrypt().getValue());
			}
			
			//discret dec enc normal
			if(mainGui.getRdiDiscretDecoding().isSelected()
					&& JobConfig.getSystemCrypt() == SystemType.DISCRET11){
				JobConfig.setWantDec(true);
			}
			if(mainGui.getRdiDiscretCoding().isSelected() 
					&& JobConfig.getSystemCrypt() == SystemType.DISCRET11){
				JobConfig.setWantDec(false);
			}
			//syster dec with 68705 search mode
			if(mainGui.getRdiDiscret68705().isSelected()
					&& JobConfig.getSystemCrypt() == SystemType.DISCRET11){
				JobConfig.setWantDec(true);
				JobConfig.setSearchCode68705(true);
			}
			else {
				JobConfig.setSearchCode68705(false);
			}
			
			//syster dec enc
			if(mainGui.getRdiSysterCodingGen().isSelected()
					&& JobConfig.getSystemCrypt() == SystemType.SYSTER){
				JobConfig.setWantDec(false);
				if(mainGui.getRdiSysterCodingFile().isSelected()){
					JobConfig.setFileDataEncSyster(
							this.mainGui.getTxtSysterEnc().getText());
				}
				if(mainGui.getRdiSysterCodingRandom().isSelected()){
					JobConfig.setWantSysterEncRandom(true);
					//type syster table
					JobConfig.setTableSyster(this.mainGui.getComboTableSysterEnc().getSelectedIndex() + 1);
				}
				else {
					JobConfig.setWantSysterEncRandom(false);
				}
				if(mainGui.getChkTags().isSelected()){
					JobConfig.setWantSysterTags(true);
				}
				else {
					JobConfig.setWantSysterTags(false);
				}
			}
			
			if(mainGui.getRdiSysterDecodingGen().isSelected() 
					&& JobConfig.getSystemCrypt() == SystemType.SYSTER){
				JobConfig.setWantDec(true);
				if(mainGui.getRdiSysterDecodingCorrel().isSelected()){
					JobConfig.setWantDecCorrel(true);
					//type syster table
					JobConfig.setTableSyster(this.mainGui.getComboTableSysterDec().getSelectedIndex() + 1);
				}
				else {
					JobConfig.setWantDecCorrel(false);	
				}
				if(mainGui.getRdiSysterDeCodingTags().isSelected()){
					JobConfig.setWantSysterTags(true);					
				}
				else {
					JobConfig.setWantSysterTags(false);	
				}
				if(mainGui.getRdiSysterDeCodingFile().isSelected()){
					JobConfig.setFileDataDecSyster(
							this.mainGui.getTxtSysterDec().getText());
				}
			}
			
			//videocrypt dec enc
			if(mainGui.getRdiVideocryptCoding().isSelected()
					&& JobConfig.getSystemCrypt() == SystemType.VIDEOCRYPT){
				JobConfig.setWantDec(false);
				if(mainGui.getRdiVideocryptCodingFile().isSelected()){
					JobConfig.setFileDataEncVideocrypt(
							this.mainGui.getTxtVideocryptEnc().getText());
				}
				if(mainGui.getRdiVideocryptCodingAuto().isSelected()){
					JobConfig.setWantVideocryptEncRandom(true);					
				}
				else {
					JobConfig.setWantVideocryptEncRandom(false);
				}
				if(mainGui.getChkVideocryptTags().isSelected()){
					JobConfig.setWantVideocryptTags(true);
				}
				else{
					JobConfig.setWantVideocryptTags(false);
				}
			}
			
			if(mainGui.getRdiVideocryptDecoding().isSelected() 
					&& JobConfig.getSystemCrypt() == SystemType.VIDEOCRYPT){
				JobConfig.setWantDec(true);
				if(mainGui.getRdiVideocryptCorrel().isSelected()){
					JobConfig.setWantDecCorrel(true);					
				}
				else {
					JobConfig.setWantDecCorrel(false);	
				}
				
				if(mainGui.getRdiVideocryptDecodingFile().isSelected()){
					JobConfig.setFileDataDecVideocrypt(
							this.mainGui.getTxtVideocryptDec().getText());
				}
				if(mainGui.getRdiVideocryptTagsDecoding().isSelected()){
					JobConfig.setWantVideocryptTags(true);
				}
				else{
					JobConfig.setWantVideocryptTags(false);
				}
			}
			
			
			
			JobConfig.setWantPlay(mainGui.getChkPlayer().isSelected());
			JobConfig.setModePhoto(mainGui.getRdiPhoto().isSelected());
			
			JobConfig.setExtension(
					mainGui.getJcbExtension().getSelectedItem().toString());	
			
			if (JobConfig.getSystemCrypt() == SystemType.DISCRET11) {
				JobConfig.setWantSound(mainGui.getChkSound().isSelected());
				JobConfig.setDisableSound(
					mainGui.getChkDisableSound().isSelected());
			}
			else if(JobConfig.getSystemCrypt() == SystemType.SYSTER) {
				JobConfig.setWantSound(mainGui.getChkSoundSyster().isSelected());
				JobConfig.setDisableSound(
					mainGui.getChkDisableSoundSyster().isSelected());
			}else if(JobConfig.getSystemCrypt() == SystemType.VIDEOCRYPT) {
				JobConfig.setWantSound(mainGui.getChkSoundVideocrypt().isSelected());
				JobConfig.setDisableSound(
					mainGui.getChkDisableSoundVideocrypt().isSelected());
			}
			
			JobConfig.setAutorisations(getAutorisationTab());
			
			JobConfig.setAudienceLevel(
					mainGui.getCombAudience().getSelectedIndex() + 1);
			JobConfig.setMultiCode(
					mainGui.getTxtMultiCode().getText().replaceAll("#", "")
					.replaceAll(" ", ""));
			JobConfig.setCycle((int)mainGui.getJspCycle().getValue());

			JobConfig.setVideoBitrate(
					Integer.valueOf(mainGui.getTxtBitrate().getText()));
			JobConfig.setVideoCodec(
					mainGui.getCombCodec().getSelectedIndex() + 1);
			JobConfig.setAudioCodec(
					mainGui.getCombAudioCodec().getSelectedIndex() + 1);
			JobConfig.setAudioRate(
					(Integer.valueOf(this.mainGui.getCombAudioRate()
							.getSelectedItem().toString().replaceAll("Hz", "")
							.trim())));			
			JobConfig.setPerc1(
					Double.valueOf(mainGui.getTxtDelay1().getText()
							.replace("%", "")) / 100d);
			JobConfig.setPerc2(
					Double.valueOf(mainGui.getTxtDelay2().getText()
							.replace("%", "")) / 100d);

			if (mainGui.getRdi720().isSelected()) {
				JobConfig.setsWidth(720);
			}
			if (mainGui.getRdi768().isSelected()) {
				JobConfig.setsWidth(768);
			}
			if (mainGui.getRdi944().isSelected()) {
				JobConfig.setsWidth(768);
			}

			if(JobConfig.getSystemCrypt() == SystemType.TRANSCODE) {//transcode
				JobConfig.setWantDec(false);
				JobConfig.setStrictMode(true);
			}
			
			
			JobConfig.setSerial(mainGui.getTxtSerial().getText());
			JobConfig.setCode(mainGui.getTxtCode().getText());
			
			//case of problematic codecs (E-AC3, MP2) and multiaudiochannel : set to mkv extension for outpufile
			if((JobConfig.isHasProblematicCodec() || JobConfig.isHasMultiAudioChannels()) && !JobConfig.isDisableSound()) {
				JobConfig.setExtension("mkv");
			}

			JobConfig.setStop(false);

			mainGui.getProgress().setMaximum(
					Integer.valueOf(mainGui.getSlidFrames().getValue()));

			if (JobConfig.isModePhoto()) {
				this.thread = new Thread(new Runnable() {
					@Override
					public void run() {
						CryptPhoto photo = new CryptPhoto();
						photo.run();
						mainGui.getProgress().setMaximum(2);
						mainGui.getProgress().setValue(1);
						mainGui.getProgress().setValue(2);
						mainGui.getBtnEnter().setEnabled(true);
						mainGui.getBtnCancel().setEnabled(false);
						mainGui.getBtnExit().setEnabled(true);
						mainGui.getBtnInputFile().setEnabled(true);
						mainGui.getBtnOutputFile().setEnabled(true);
						mainGui.getFrame()
								.setCursor(
										Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
				});

				thread.setPriority(5);

				thread.start();
			} else {
				this.thread = new Thread(new Runnable() {
					@Override
					public void run() {	
							FramesPlayer frmVideo = new FramesPlayer();
							frmVideo.readFrame();
							mainGui.getBtnEnter().setEnabled(true);
							mainGui.getBtnCancel().setEnabled(false);
							mainGui.getBtnExit().setEnabled(true);
							mainGui.getBtnInputFile().setEnabled(true);
							mainGui.getBtnOutputFile().setEnabled(true);
							mainGui.getFrame()
									.setCursor(
											Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						}					
				});

				thread.setPriority(5);

				thread.start();
			}
		}
	}
	
	private void manageCancel(){		
			  mainGui.getBtnCancel().setEnabled(false);
			  JobConfig.setStop(true);
			  mainGui.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	
	@SuppressWarnings("deprecation")
	private int getNumFrames(String path){
			int count = 0;

		    String filename = path;


		    // create a Xuggler container object

		    IContainer container = IContainer.make();

		    // open up the container

		    if (container.open(filename, IContainer.Type.READ, null) < 0)
		      throw new IllegalArgumentException("could not open file: " + filename);

		    // query how many streams the call to open found
		    int numStreams = container.getNumStreams();

		    // and iterate through the streams to find the first video stream
		    int videoStreamId = -1;
		    IStreamCoder videoCoder = null;
		    for(int i = 0; i < numStreams; i++)
		    {
		      // find the stream object
		      IStream stream = container.getStream(i);

		      // get the pre-configured decoder that can decode this stream;
		      IStreamCoder coder = stream.getStreamCoder();

		      if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO && stream.getIndex() == JobConfig.getDefaultIdVideoTrack())
		      {
		        videoStreamId = i;
		        videoCoder = coder;
		        break;
		      }
		    }

		    if (videoStreamId == -1)
		      throw new RuntimeException("could not find video stream in container: "+filename);

		    // Now we have found the video stream in this file.  Let's open up
		    // our decoder so it can do work
		    if (videoCoder.open() < 0)
		      throw new RuntimeException(
		        "could not open video decoder for container: " + filename);
		    // Now, we start walking through the container looking at each packet.

		    IPacket packet = IPacket.make();
		    while(container.readNextPacket(packet) >= 0)		    {
		      
		      // Now we have a packet, let's see if it belongs to our video strea

		      if (packet.getStreamIndex() == videoStreamId)
		      {
		    	  count++;
		      }
		      else
		      {
		        // This packet isn't part of our video stream, so we just
		        // silently drop it.
		        do {} while(false);
		      }
		    }

		    if (videoCoder != null)
		    {
		      videoCoder.close();
		      videoCoder = null;
		    }
		    if (container !=null)
		    {
		      container.close();
		      container = null;
		    }
		    return count;
	}
	
	
	private void setVideosInfos(String path){		
		StreamsFinder streamInfos = new StreamsFinder(path);
		streamInfos.analyzeStreams();
		JobConfig.setVideoHasAudioTrack(streamInfos.isHasAudioTrack());
		
		JobConfig.setDefaultIdAudioTrack(streamInfos.getDefaultIdAudioTrack());
		JobConfig.setDefaultIdVideoTrack(streamInfos.getDefaultIdVideoTrack());		
		
		if(streamInfos.getNumVideoStreams() > 1 || streamInfos.getNumAudioStreams() > 1) {
			streamInfos.displayTracksSelector();
		}
		
		if(JobConfig.isVideoHasAudioTrack()) {
			if(JobConfig.getAudioTrackInfos().getNumChannels() > 2) {
				JobConfig.setHasMultiAudioChannels(true);			
				//showWarningSound();
			}
			else {
				JobConfig.setHasMultiAudioChannels(false);
			}
			
			if(JobConfig.getAudioTrackInfos().getCodecShortName().toLowerCase().equals("eac3") 
					||  JobConfig.getAudioTrackInfos().getCodecID().toLowerCase().equals("CODEC_ID_EAC3".toLowerCase())
					||  JobConfig.getAudioTrackInfos().getCodecID().toLowerCase().equals("CODEC_ID_MP2".toLowerCase())				
					) {
				JobConfig.setHasProblematicCodec(true);			
			}
			else {
				JobConfig.setHasProblematicCodec(false);
			}
		}
		
		IMediaReader reader = ToolFactory.makeReader(path);
		
		reader.readPacket();
				
		int videoStreamId = -1;
		IContainer container = reader.getContainer();
		double timeBase = 0;
//		int width = 0;
//		int height =0;
		double frameRate = 0;
		long nbFrames = 0;
		long duration = 0;
		double calc = 0;
		double frames_nb = 0;
		double alt_nbframes = 0;
		IStream stream = null;
			
		
		  if(videoStreamId == -1) {
		        for(int i = 0; i < container.getNumStreams(); i++) {
		            stream = container.getStream(i);
		            IStreamCoder coder = stream.getStreamCoder();
		            if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO && stream.getIndex() == JobConfig.getDefaultIdVideoTrack()) {
		                videoStreamId = i;
		               
		                timeBase = stream.getTimeBase().getDouble();
		                frameRate = stream.getFrameRate().getValue();
		                
		                
		               	nbFrames = stream.getNumFrames();
		                
		                
		                duration = stream.getDuration();
		                //height = stream.getStreamCoder().getHeight();
		                alt_nbframes = ((stream.getStreamCoder().getStream().getContainer().getDuration())/1000d/1000d) * frameRate ;
		                //width = stream.getStreamCoder().getWidth();
		                calc = (duration* timeBase);
		                frames_nb = calc * frameRate;		 
		                
		                //System.out.println(stream.getStreamCoder().getWidth() + " " + stream.getStreamCoder().getHeight());
		                if(stream.getStreamCoder().getWidth() == 944 && stream.getStreamCoder().getHeight() == 626) {
			                int dialogResult = JOptionPane.showConfirmDialog (null, JobConfig.getRes().getString("manageFileOpen.is944"),
			                		"944x626 resolution",JOptionPane.YES_NO_OPTION);
			                if(dialogResult == JOptionPane.YES_OPTION){
			                  JobConfig.setHasToBeUnsplit(true);			                  
			                }
			                else {
			                	JobConfig.setHasToBeUnsplit(false);
			                }
		                }
		                break;
		            }
		        }
		    }
		
		int nb_frames_def = 0;
		if(nbFrames ==0 && frames_nb > 0){
			nb_frames_def = (int)frames_nb;
		}
		else if(nbFrames > 0){
			nb_frames_def = (int)nbFrames;
		} else {
			nb_frames_def = (int)alt_nbframes;
		}
		
//		if(frameRate !=25){
//			alertFrameRate(frameRate);
//		}
		
//		if(path.substring(path.lastIndexOf(".") + 1, path.length()).equals("ts")
//				|| path.substring(path.lastIndexOf(".") + 1, path.length()).equals("m2t")){
//			alertTsM2t();
//		}
//		else {
		this.mainGui.getChkDisableSound().setSelected(false);
		this.mainGui.getChkSound().setSelected(true);
		this.mainGui.getChkSound().setEnabled(true);
		
		this.mainGui.getChkDisableSoundSyster().setSelected(false);
		this.mainGui.getChkSoundSyster().setSelected(true);
		this.mainGui.getChkSoundSyster().setEnabled(true);
		
		this.mainGui.getChkDisableSoundVideocrypt().setSelected(false);
		// this.mainGui.getChkSoundVideocrypt().setSelected(true);
		this.mainGui.getChkSoundVideocrypt().setEnabled(true);
		
		
		JobConfig.setFrameRate(frameRate);	
		
		mainGui.getBtnEnter().setEnabled(true);
		
		//fix
		nb_frames_def = getNumFrames(path);
		
		mainGui.getSlideFrameStart().setMaximum(nb_frames_def);
		mainGui.getSlideFrameStart().setValue(1);
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put( new Integer( 1 ), new JLabel("1"));		
		labelTable.put( new Integer( nb_frames_def ), 
				new JLabel(String.valueOf(nb_frames_def)));
		mainGui.getSlideFrameStart().setLabelTable(labelTable);
		mainGui.getSlideFrameStart().setMajorTickSpacing((int) (0.5 * nb_frames_def));
		mainGui.getSlideFrameStart().setMinorTickSpacing((int) (0.05 * nb_frames_def));
		mainGui.getSlideFrameStart().setPaintLabels(true);		
		mainGui.getSlideFrameStart().setPaintTicks(true);
		
		mainGui.getSlideFrameStartSyster().setMaximum(nb_frames_def);
		mainGui.getSlideFrameStartSyster().setValue(1);
		Hashtable<Integer, JLabel> labelTable2 = new Hashtable<Integer, JLabel>();
		labelTable2.put( new Integer( 1 ), new JLabel("1"));		
		labelTable2.put( new Integer( nb_frames_def ), 
				new JLabel(String.valueOf(nb_frames_def)));		
		mainGui.getSlideFrameStartSyster().setLabelTable(labelTable2);
		mainGui.getSlideFrameStartSyster().setMajorTickSpacing((int) (0.5 * nb_frames_def));
		mainGui.getSlideFrameStartSyster().setMinorTickSpacing((int) (0.05 * nb_frames_def));
		mainGui.getSlideFrameStartSyster().setPaintLabels(true);		
		mainGui.getSlideFrameStartSyster().setPaintTicks(true);
		
		mainGui.getSlideFrameStartVideocrypt().setMaximum(nb_frames_def);
		mainGui.getSlideFrameStartVideocrypt().setValue(1);
		Hashtable<Integer, JLabel> labelTable3 = new Hashtable<Integer, JLabel>();
		labelTable3.put( new Integer( 1 ), new JLabel("1"));		
		labelTable3.put( new Integer( nb_frames_def ), 
				new JLabel(String.valueOf(nb_frames_def)));		
		mainGui.getSlideFrameStartVideocrypt().setLabelTable(labelTable3);
		mainGui.getSlideFrameStartVideocrypt().setMajorTickSpacing((int) (0.5 * nb_frames_def));
		mainGui.getSlideFrameStartVideocrypt().setMinorTickSpacing((int) (0.05 * nb_frames_def));
		mainGui.getSlideFrameStartVideocrypt().setPaintLabels(true);		
		mainGui.getSlideFrameStartVideocrypt().setPaintTicks(true);
		
		
		
		mainGui.getSlidFrames().setMaximum(nb_frames_def);
		double percFrame = 1;
//		if(nb_frames_def <= 15000 ){
//			percFrame = 1;
//		}
//		else{
//			percFrame = 0.10;
//		}
		mainGui.getSlidFrames().setValue((int) (percFrame * nb_frames_def));		
		mainGui.getSlidFrames().setLabelTable(labelTable);
		mainGui.getSlidFrames().setMajorTickSpacing((int) (0.5 * nb_frames_def));
		mainGui.getSlidFrames().setMinorTickSpacing((int) (0.05 * nb_frames_def));
		mainGui.getSlidFrames().setPaintLabels(true);		
		mainGui.getSlidFrames().setPaintTicks(true);
		
		mainGui.getTxtInputFile().setText(path);		
		
		mainGui.getProgress().setMaximum(nb_frames_def);
		
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {			
		
		if(e.getSource().equals(mainGui.getJsp16bitKeyword()) 
				|| e.getSource().equals(mainGui.getJspFrameStart())
				|| e.getSource().equals(mainGui.getJspFrameStartSyster())
				|| e.getSource().equals(mainGui.getJspFrameStartVideocrypt())
				|| e.getSource().equals(mainGui.getSlpWhiteValue())){
			JSpinner spi = (JSpinner)e.getSource();
			manageSpinners(spi);
		} 		
		else{
		JSlider source = (JSlider)e.getSource();
		manageSliders(source);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if(!this.mainGui.getBtnEnter().isEnabled() &&
				this.mainGui.getBtnCancel().isEnabled()){
		mainGui.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if(!this.mainGui.getBtnEnter().isEnabled() &&
				this.mainGui.getBtnCancel().isEnabled()){
			mainGui.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			}		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		
		
	}

	private boolean[] getAutorisationTab(){
		boolean[] autorisation = new boolean[6];
		
		autorisation[0] = mainGui.getChkAutorisation1().isSelected();
		autorisation[1] = mainGui.getChkAutorisation2().isSelected();
		autorisation[2] = mainGui.getChkAutorisation3().isSelected();
		autorisation[3] = mainGui.getChkAutorisation4().isSelected();
		autorisation[4] = mainGui.getChkAutorisation5().isSelected();
		autorisation[5] = mainGui.getChkAutorisation6().isSelected();
		
		return autorisation;
		
	}
	
	private void updateKeyboardCode( String serialString){
		
		serialString = serialString.replace(" ", "");
		
		if (serialString.equals("")){
			serialString = "0";
		}
		
		int serial = Integer.valueOf(serialString);		
		
		KeyboardCode code = new KeyboardCode(serial,
				Integer.valueOf(mainGui.getTxt16bitsWord().getText()),
				getAutorisationTab());
		mainGui.getTxtCode().setText(code.getKeyboardCode());
	}
	
	@Override
	public void insertUpdate(DocumentEvent arg0) {		
			try {
				String value = arg0.getDocument().getText(0,
						arg0.getDocument().getLength());

				updateKeyboardCode(value);

			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {			
			try {
				String value = arg0.getDocument().getText(0,
						arg0.getDocument().getLength());
				updateKeyboardCode(value);

			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		mainGui.getTxtSerial().setText(mainGui.getTxtSerial().getText().replace(" ", ""));
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
			
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * get the 11 bits keyword
	 * @param key16bits the 16 bits keyword
	 */
	private void get11bitsKeyWord(int key16bits, int index){
		String word = String.format
				("%16s", Integer.toBinaryString(key16bits)).replace(" ", "0");		
		
		switch (index) {
		case 0:
			//audience 1
			String audience1 = word.substring(0, 11);
			mainGui.getLbl11bitsInfo().setText( "( " +
					String.format("%04d", Integer.parseInt(audience1,2)) + " )");
			break;
		case 1:
			//audience 2
			String audience2 = word.substring(3, 14);
			mainGui.getLbl11bitsInfo().setText( "( " +
					String.format("%04d", Integer.parseInt(audience2,2))+ " )");
			break;
		case 2:
			//audience 3
			String audience3 = word.substring(6, 16) + word.charAt(0);
			mainGui.getLbl11bitsInfo().setText( "( " +
					String.format("%04d", Integer.parseInt(audience3,2))+ " )");
			break;
		case 3:
			//audience 4
			String audience4 =  word.substring(9, 16) + word.substring(0, 4);
			mainGui.getLbl11bitsInfo().setText( "( " +
					String.format("%04d", Integer.parseInt(audience4,2)) + " )");
			break;
		case 4:
			//audience 5
			String audience5 = word.substring(12, 16) +  word.substring(0, 7);
			mainGui.getLbl11bitsInfo().setText( "( " +
					String.format("%04d", Integer.parseInt(audience5,2))+ " )");
			break;
		case 5:
			//audience 6
			String audience6 =  word.charAt(15) + word.substring(0, 10) ;
			mainGui.getLbl11bitsInfo().setText( "( " +
					String.format("%04d", Integer.parseInt(audience6,2))+ " )");
			break;
		case 6:
			//audience 7		
			mainGui.getLbl11bitsInfo().setText("( 1337 )");
			break;
		default:
			break;	
		}		
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
			word11bits = word11bits + " " + ( i + 1 ) + ":" + get11bitsKeyWordMulti(this.mainGui.getSlid16bitsWord().getValue(),
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
		default:
			audience = "";
			break;	
		}
		
		return String.format("%04d", Integer.parseInt(audience,2));
	}


	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub		
		saveConfig();		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	private Boolean saveConfig(){
		
		//language
//		int language = 0;
//		if(this.mainGui.getmAuto().isSelected()){
//			language = 0;
//		} else if (this.mainGui.getmEnglish().isSelected()){
//			language = 1;
//		} else if (this.mainGui.getmFrench().isSelected()){
//			language = 2;
//		}
		
		return JobConfig.saveConfig(
				this.mainGui.getSlid16bitsWord().getValue(),
				this.mainGui.getCombAudience().getSelectedIndex() + 1,
				(double)(this.mainGui.getSlidDelay1().getValue())/100000d,
				(double)(this.mainGui.getSlidDelay2().getValue())/100000d,
				this.mainGui.getTxtSerial().getText(),
				this.mainGui.getChkHorodatage().isSelected(),
				this.mainGui.getCombCodec().getSelectedIndex() + 1,
				this.mainGui.getSlidBitrate().getValue(),
				(String)this.mainGui.getJcbExtension().getSelectedItem(),
				this.mainGui.getTxtOutputFile().getText(),
				this.mainGui.getTxtMultiCode().getText().replaceAll(" ", "").replaceAll("#", ""),
				this.mainGui.getJspCycle().getValue().toString(),
				this.mainGui.getRdi720().isSelected(),
				this.mainGui.getCombAudioCodec().getSelectedIndex(),
				this.mainGui.getCombAudioRate().getSelectedItem().toString().replaceAll("Hz", "").trim(),
				this.mainGui.getCombSystemCrypt().getSelectedIndex(),
				JobConfig.getLang());			
	}
	
	private String getTime(int timer){		  
		    int hours = timer / 3600; // get the amount of hours from the seconds
		    int remainder = timer % 3600; // get the rest in seconds
		    int minutes = remainder / 60; // get the amount of minutes from the rest
		    int seconds = remainder % 60; // get the new rest
		    String disHour = (hours < 10 ? "0" : "") + hours; // get hours and add "0" before if lower than 10
		    String disMinu = (minutes < 10 ? "0" : "") + minutes; // get minutes and add "0" before if lower than 10
		    String disSec = (seconds < 10 ? "0" : "") + seconds; // get seconds and add "0" before if lower than 10
		    String formattedTime = disHour + ":" + disMinu + ":" + disSec; //get the whole time
		    return formattedTime;
	}
	
	private void setNoMultiCodeComboBox(){
		if(this.mainGui.getCombAudience().getItemCount() == 8){
			this.mainGui.getCombAudience().removeItemAt(7);
			this.mainGui.getCombAudience().setSelectedIndex(0);			
		}		
	}
	
	private void setMultiCodeComboBox(){
		if(this.mainGui.getCombAudience().getItemCount() == 7){
			this.mainGui.getCombAudience().addItem("multicode");
		}
		this.mainGui.getCombAudience().setSelectedIndex(0);		
	}
	
	private void enableComboAudience(){		
		this.mainGui.getLabAudience().setEnabled(true);
		this.mainGui.getCombAudience().setEnabled(true);
		if(this.mainGui.getCombAudience().getSelectedIndex() == 7){
			this.mainGui.getTxtMultiCode().setEnabled(true);
			this.mainGui.getLblCycle().setEnabled(true);
			this.mainGui.getJspCycle().setEnabled(true);
			this.mainGui.getLbl11bitsInfo().setEnabled(true);
		}
	}
	
	private void disableComboAudience(){
		//this.mainGui.getCombAudience().setSelectedIndex(0);
		this.mainGui.getLabAudience().setEnabled(false);
		this.mainGui.getCombAudience().setEnabled(false);
		this.mainGui.getTxtMultiCode().setEnabled(false);
		this.mainGui.getLblCycle().setEnabled(false);
		this.mainGui.getJspCycle().setEnabled(false);
		this.mainGui.getLbl11bitsInfo().setEnabled(false);
	}

}
