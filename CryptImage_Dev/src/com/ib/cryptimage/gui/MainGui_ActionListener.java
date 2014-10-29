/**
 * This file is part of	CryptImage_Dev.
 *
 * CryptImage_Dev is free software: you can redistribute it and/or modify
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
 * along with CryptImage_Dev.  If not, see <http://www.gnu.org/licenses/>
 * 
 * 27 oct. 2014 Author Mannix54
 */



package com.ib.cryptimage.gui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileFilter;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.ib.cryptimage.core.CryptPhoto;
import com.ib.cryptimage.core.FramesPlayer;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;

/**
 * @author Mannix54
 *
 */
public class MainGui_ActionListener implements ActionListener, ChangeListener, MouseListener {
	
	private MainGui mainGui;
	private Thread thread = null; 
	
	public MainGui_ActionListener(MainGui mainGui) {
		this.mainGui = mainGui;
	}

	@Override
	public void actionPerformed(ActionEvent e)  {
		Object src = e.getSource();
		if ((src instanceof JButton)) {			
				this.manageButtons((JButton) src);			
		} else if (src instanceof JTextField) {
			this.manageTxtFields((JTextField) src);
		} else if (src instanceof JRadioButton) {
			this.manageRadioButton((JRadioButton) src);
		} else if (src instanceof JComboBox) {
			this.manageComboBoxes((JComboBox) src);
		} else if (src instanceof JCheckBox) {
			this.manageCheckBoxes((JCheckBox) src);
		} else if (src instanceof JProgressBar) {
			this.manageProgressBar((JProgressBar) src);
		} else if (src instanceof JTextArea) {
			this.manageTextArea((JTextArea) src);
		} 
		
	}

	private void manageCheckBoxes(JCheckBox src) {
		if(src.equals(this.mainGui.getChkStrictMode())){
			if(src.isSelected()){
				mainGui.getChkAudience7().setEnabled(true);
				mainGui.getCombAudience().setEnabled(true);
				mainGui.getRdi720().setEnabled(true);
				mainGui.getRdi768().setEnabled(true);
				}
			else
			{
				mainGui.getChkAudience7().setEnabled(false);
				mainGui.getCombAudience().setEnabled(false);
				mainGui.getRdi720().setEnabled(false);
				mainGui.getRdi768().setEnabled(false);
			}				
		}else if(src.equals(this.mainGui.getChkAudience7())){
			if(src.isSelected()){
				mainGui.getSlid11bitsWord().setValue(1337);
				mainGui.getCombAudience().setSelectedIndex(6);
			}			
		} else if(src.equals(this.mainGui.getChkDelay())){
			if(src.isSelected()){
				mainGui.getSlidDelay1().setValue(1670);
				mainGui.getSlidDelay2().setValue(3340);
			}			
		} else if(src.equals(this.mainGui.getChkPlayer())){
			if(src.isSelected()){
				mainGui.getSlidBitrate().setEnabled(false);
				mainGui.getTxtBitrate().setEnabled(false);				
				mainGui.getCombCodec().setEnabled(false);				
			}
			else if(mainGui.getRdiPhoto().isSelected() != true){				
				mainGui.getSlidBitrate().setEnabled(true);
				mainGui.getTxtBitrate().setEnabled(true);				
				mainGui.getCombCodec().setEnabled(true);	
			}
		}
	}

	private void manageTextArea(JTextArea src) {
		// TODO Auto-generated method stub
		
	}

	private void manageProgressBar(JProgressBar src) {
		// TODO Auto-generated method stub
		
	}

	private void manageComboBoxes(JComboBox src) {		
		if(src.equals(mainGui.getCombAudience())){			
			if(src.getSelectedIndex()!=6 ){				
				mainGui.getChkAudience7().setSelected(false);
			} else	if(src.getSelectedIndex()==6 && 
					(int)mainGui.getJsp11bitKeyword().getValue()==1337){
				mainGui.getChkAudience7().setSelected(true);
			}
		}
		
	}

	private void manageSliders(JSlider src) {		
		if(src.equals(this.mainGui.getSlid11bitsWord())){
			if(src.getValue()!=1337){
				mainGui.getChkAudience7().setSelected(false);
			} else if(src.getValue()==1337 &&
					mainGui.getCombAudience().getSelectedIndex()==6){
				mainGui.getChkAudience7().setSelected(true);
			}
			mainGui.getTxt11bitsWord().setText(String.valueOf(
					mainGui.getSlid11bitsWord().getValue()));
			mainGui.getJsp11bitKeyword().setValue(
					mainGui.getSlid11bitsWord().getValue());
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
		} else if(src.equals(mainGui.getSlidBitrate())){
			mainGui.getTxtBitrate().setText(String.valueOf(src.getValue()));
		}else if(src.equals(mainGui.getSlidFrames())){
			mainGui.getTxtNbFrames().setText(String.valueOf(src.getValue()));
		}
	}
	
	private void manageSpinners(JSpinner src){
		if(src.equals(this.mainGui.getJsp11bitKeyword())){
			mainGui.getSlid11bitsWord().setValue(
					(int) src.getValue());
		} else if(src.equals(this.mainGui.getJspFrameStart())){
			mainGui.getSlideFrameStart().setValue(
					(int) src.getValue());
		}
	}

	private void manageRadioButton(JRadioButton src) {
		if(src.equals(mainGui.getRdiPhoto())){
			if(src.isSelected()){				
				mainGui.getTxtInputFile().setText("");
				mainGui.getTxtOutputFile().setText("");
				mainGui.getBtnEnter().setEnabled(false);
				mainGui.getBtnOutputFile().setEnabled(false);
				mainGui.getSlidBitrate().setEnabled(false);
				mainGui.getTxtBitrate().setEnabled(false);				
				mainGui.getCombCodec().setEnabled(false);
				mainGui.getSlidFrames().setEnabled(false);
				mainGui.getChkStrictMode().setSelected(false);
				mainGui.getChkStrictMode().setEnabled(false);
				mainGui.getCombAudience().setEnabled(false);
				mainGui.getRdi720().setEnabled(false);
				mainGui.getRdi768().setEnabled(false);
				mainGui.getChkPlayer().setEnabled(false);
				
			}
		} else if(src.equals(mainGui.getRdiVideo())){
			if(src.isSelected() && mainGui.getChkPlayer().isSelected()!=true){
				mainGui.getTxtInputFile().setText("");
				mainGui.getTxtOutputFile().setText("");
				mainGui.getBtnEnter().setEnabled(false);
				mainGui.getBtnOutputFile().setEnabled(false);
				mainGui.getSlidBitrate().setEnabled(true);
				mainGui.getTxtBitrate().setEnabled(true);				
				mainGui.getCombCodec().setEnabled(true);
				mainGui.getSlidFrames().setEnabled(true);
				mainGui.getChkStrictMode().setEnabled(true);
				mainGui.getChkStrictMode().setSelected(true);
				mainGui.getCombAudience().setEnabled(true);
				mainGui.getRdi720().setEnabled(true);
				mainGui.getRdi768().setEnabled(true);
				mainGui.getChkPlayer().setEnabled(true);
				
				
			} else if(src.isSelected()){
				mainGui.getTxtInputFile().setText("");
				mainGui.getTxtOutputFile().setText("");
				mainGui.getBtnEnter().setEnabled(false);
				mainGui.getBtnOutputFile().setEnabled(false);
				mainGui.getSlidFrames().setEnabled(true);
				mainGui.getChkStrictMode().setEnabled(true);
				mainGui.getChkStrictMode().setSelected(true);
				mainGui.getRdi720().setEnabled(true);
				mainGui.getRdi768().setEnabled(true);
				mainGui.getChkPlayer().setEnabled(true);
				
			}
		}
	}

	private void manageTxtFields(JTextField src) {
		// TODO Auto-generated method stub
		
	}

	private void manageButtons(JButton src) {
		if(src.equals(mainGui.getBtnExit())){
			System.exit(0);
		} else if(src.equals(mainGui.getBtnInputFile())){
			manageFileOpen();
		} else if(src.equals(mainGui.getBtnEnter())){
			manageEnter();
		} else if(src.equals(mainGui.getBtnCancel())){
			manageCancel();
		} else if(src.equals(mainGui.getBtnOutputFile())){
			manageSave();
		}
	}
	
	private void manageSave() {
		JFileChooser dialogue = new JFileChooser();
		dialogue.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		dialogue.setDialogTitle("Sélectionnez le répertoire où sera enregistré le fichier");
		
		File file;
		if (dialogue.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			file = dialogue.getSelectedFile();
			if (!file.canRead()) {
				JOptionPane
						.showMessageDialog(
								dialogue,
								"Le répertoire tapé n'existe pas, veuillez le créer d'abord.",
								"répertoire non existant",
								JOptionPane.WARNING_MESSAGE);
			} else {
				mainGui.getTxtOutputFile().setText(file.getAbsolutePath());
			}
		}
	}
	
	private void manageFileOpen() {
		JFileChooser dialogue = new JFileChooser();
		dialogue.setDialogTitle("Sélectionnez le fichier d'entrée");
		File file;

		FileNameExtensionFilter filter;
		String[] extension;

		if (mainGui.getRdiVideo().isSelected()) {
			extension = new String[] { "avi", "mp4", "mpeg", "mkv", "mpeg2",
					"ts", "m2t" };
			filter = new FileNameExtensionFilter("fichiers vidéos", extension);
		} else {
			extension = new String[] { "jpeg", "jpg", "bmp", "gif", "png",
					"tiff" };
			filter = new FileNameExtensionFilter("fichiers images", extension);
		}

		dialogue.setFileFilter(filter);

		if (dialogue.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			file = dialogue.getSelectedFile();

			if (!file.canRead()) {
				JOptionPane
						.showMessageDialog(
								dialogue,
								"Le fichier n'est pas accessible en lecture ou bien est endommagé",
								"erreur de lecture de fichier",
								JOptionPane.ERROR_MESSAGE);
			} else {

				if (mainGui.getRdiVideo().isSelected()) {
					setVideosInfos(file.getAbsolutePath());
					mainGui.getTxtOutputFile().setText(file.getParent());
					mainGui.getBtnOutputFile().setEnabled(true);
				} else {
					mainGui.getTxtInputFile().setText(file.getAbsolutePath());
					mainGui.getTxtOutputFile().setText(file.getParent());
					mainGui.getBtnOutputFile().setEnabled(true);
					mainGui.getBtnEnter().setEnabled(true);
				}
			}
		}
	}

	private void manageEnter(){
		mainGui.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
//		 for (Component c : mainGui..getComponents()) {
//             c.setEnabled(true);
//         }
		
		
		
		mainGui.getBtnEnter().setEnabled(false);
		mainGui.getBtnCancel().setEnabled(true);
		mainGui.getBtnExit().setEnabled(false);
		mainGui.getBtnInputFile().setEnabled(false);
		mainGui.getBtnOutputFile().setEnabled(false);
		
		mainGui.getJob().setInput_file(mainGui.getTxtInputFile().getText());
		if(mainGui.getTxtOutputFile().getText().equals("")){			
			mainGui.getJob().setOutput_file(mainGui.getTxtInputFile().getText());
		} 
		else {
			File fic = new File(mainGui.getTxtInputFile().getText());			
		mainGui.getJob().setOutput_file(mainGui.getTxtOutputFile().getText() + "/" 
				+fic.getName());
		}
		mainGui.getJob().setDiscret11Word(Integer.valueOf(mainGui.getTxt11bitsWord().getText()));
		mainGui.getJob().setVideo_frame(Integer.valueOf(mainGui.getTxtNbFrames().getText()));
		mainGui.getJob().setStrictMode(mainGui.getChkStrictMode().isSelected());
		mainGui.getJob().setPositionSynchro((int)mainGui.getJspFrameStart().getValue());
		mainGui.getJob().setWantDec(mainGui.getRdiDecoding().isSelected());
		mainGui.getJob().setWantPlay(mainGui.getChkPlayer().isSelected());
		mainGui.getJob().setModePhoto(mainGui.getRdiPhoto().isSelected());
		
		if(mainGui.getChkStrictMode().isSelected()== false){			
			mainGui.getJob().setAudienceLevel(0);
		}
		else{
			mainGui.getJob().setAudienceLevel(mainGui.getCombAudience().getSelectedIndex() + 1);
		}		
		
		mainGui.getJob().setVideoBitrate(Integer.valueOf(mainGui.getTxtBitrate().getText()));
		mainGui.getJob().setVideoCodec(mainGui.getCombCodec().getSelectedIndex() +1);
		mainGui.getJob().setPerc1(Double.valueOf(mainGui.getTxtDelay1().getText().replace("%", ""))/100d);
		mainGui.getJob().setPerc2(Double.valueOf(mainGui.getTxtDelay2().getText().replace("%", ""))/100d);
		
		if(mainGui.getRdi720().isSelected()){
			mainGui.getJob().setsWidth(720);
		}
		if(mainGui.getRdi768().isSelected()){
			mainGui.getJob().setsWidth(768);
		}
		
		mainGui.getJob().setStop(false);
		
		mainGui.getProgress().setMaximum(Integer.valueOf(mainGui.getTxtNbFrames().getText()));
		
		if(mainGui.getJob().isModePhoto()){
			 this.thread = new Thread(new Runnable() {
					@Override
					public void run() {				
							CryptPhoto photo = new CryptPhoto(mainGui.getJob());
							photo.run();
							mainGui.getProgress().setMaximum(2);
							mainGui.getProgress().setValue(1);
							mainGui.getProgress().setValue(2);
							mainGui.getBtnEnter().setEnabled(true);
							mainGui.getBtnCancel().setEnabled(false);
							mainGui.getBtnExit().setEnabled(true);
							mainGui.getBtnInputFile().setEnabled(true);
							mainGui.getBtnOutputFile().setEnabled(true);
							mainGui.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
				});
				
				thread.setPriority(5);
				
				thread.start();
		}
		else {
		 this.thread = new Thread(new Runnable() {
			@Override
			public void run() {				
					FramesPlayer frmVideo = new FramesPlayer(mainGui.getJob());
					frmVideo.readFrame();
					mainGui.getBtnEnter().setEnabled(true);
					mainGui.getBtnCancel().setEnabled(false);
					mainGui.getBtnExit().setEnabled(true);
					mainGui.getBtnInputFile().setEnabled(true);
					mainGui.getBtnOutputFile().setEnabled(true);
					mainGui.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		});
		
		thread.setPriority(5);
		
		thread.start();
		}
	}	
	
	private void manageCancel(){		
			  mainGui.getBtnCancel().setEnabled(false);
			  mainGui.getJob().setStop(true);
			  mainGui.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	
	private void setVideosInfos(String path){
		IMediaReader reader = ToolFactory.makeReader(path);
		reader.readPacket();
			
		int videoStreamId = -1;
		IContainer container = reader.getContainer();
		double timeBase = 0;
		int width = 0;
		int height =0;
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
		            if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
		                videoStreamId = i;
		               
		                timeBase = stream.getTimeBase().getDouble();
		                frameRate = stream.getFrameRate().getValue();
		                nbFrames = stream.getNumFrames();
		                duration = stream.getDuration();
		                height = stream.getStreamCoder().getHeight();
		                alt_nbframes = ((stream.getStreamCoder().getStream().getContainer().getDuration())/1000d/1000d) * frameRate ;
		                width = stream.getStreamCoder().getWidth();
		                calc = (duration* timeBase);
		                frames_nb = calc * frameRate;		              
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
		
		mainGui.getBtnEnter().setEnabled(true);
		
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
		
		mainGui.getSlidFrames().setMaximum(nb_frames_def);
		mainGui.getSlidFrames().setValue((int) (0.10 * nb_frames_def));		
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
		if(e.getSource().equals(mainGui.getJsp11bitKeyword()) 
				|| e.getSource().equals(mainGui.getJspFrameStart())){
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

}
