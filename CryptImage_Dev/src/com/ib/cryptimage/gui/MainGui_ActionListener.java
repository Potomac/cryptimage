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
 */



package com.ib.cryptimage.gui;

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
import java.io.File;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
import com.ib.cryptimage.core.KeyboardCode;
import com.ib.cryptimage.core.StreamsFinder;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;

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
		if ((src instanceof JButton)) {			
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
		if (src.equals(this.mainGui.getChkStrictMode())) {
			if (src.isSelected()) {
				mainGui.getRdi720().setEnabled(true);
				mainGui.getRdi768().setEnabled(true);
			} else {
				mainGui.getRdi720().setEnabled(false);
				mainGui.getRdi768().setEnabled(false);
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
				mainGui.getJcbExtension().setEnabled(false);
				// mainGui.getChkSound().setEnabled(false);
			} else if (mainGui.getRdiPhoto().isSelected() != true) {
				mainGui.getSlidBitrate().setEnabled(true);
				mainGui.getTxtBitrate().setEnabled(true);
				mainGui.getCombCodec().setEnabled(true);
				mainGui.getJcbExtension().setEnabled(true);
				mainGui.getChkSound().setEnabled(true);
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
			mainGui.getJob().setHorodatage(mainGui.getChkHorodatage().isSelected());
		}else if (src.equals(this.mainGui.getChkDisableSound())) {			
			if(src.isSelected()){
				mainGui.getChkSound().setSelected(false);
				mainGui.getChkSound().setEnabled(false);				
			}
			else {
				mainGui.getChkSound().setSelected(true);
				mainGui.getChkSound().setEnabled(true);
			}
		}
		
	}

	private void manageTextArea(JTextArea src) {
		// TODO Auto-generated method stub
	
		
	}

	private void manageProgressBar(JProgressBar src) {
		// TODO Auto-generated method stub
		
	}

	private void manageComboBoxes(JComboBox<?> src) {
		if (src.equals(mainGui.getJcbExtension())) {
			if (src.getSelectedIndex() == 0
					&& this.mainGui.getCombCodec().getSelectedIndex() == 3) {
				this.mainGui.getCombCodec().setSelectedIndex(0);
			}
			if (src.getSelectedIndex() == 3 || src.getSelectedIndex() == 4) {
				mainGui.getCombCodec().setSelectedIndex(1);
				mainGui.getCombCodec().setEnabled(false);
			} else {
				mainGui.getCombCodec().setEnabled(true);
			}
		} else if (src.equals(mainGui.getCombCodec())) {
			if (src.getSelectedIndex() == 3
					&& this.mainGui.getJcbExtension().getSelectedIndex() == 0) {
				this.mainGui.getJcbExtension().setSelectedIndex(1);
			}
		} else if(src.equals(mainGui.getCombAudience())){
			get11bitsKeyWord(mainGui.getSlid16bitsWord().getValue(),
					mainGui.getCombAudience().getSelectedIndex());
		}
	}

	private void manageSliders(JSlider src) {		
		if(src.equals(this.mainGui.getSlid16bitsWord())){	
			mainGui.getTxt16bitsWord().setText(String.format("%05d",
					mainGui.getSlid16bitsWord().getValue()));
			mainGui.getJsp16bitKeyword().setValue(
					mainGui.getSlid16bitsWord().getValue());
			//update code
			updateKeyboardCode(mainGui.getTxtSerial().getText());
			get11bitsKeyWord(mainGui.getSlid16bitsWord().getValue(),
					mainGui.getCombAudience().getSelectedIndex());
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
			String time = "";
			if(this.mainGui.getJob().getFrameRate() > 0){
				time = getTime((int) (mainGui.getSlidFrames().getValue()/mainGui.getJob().getFrameRate()));
			}
			mainGui.getTxtNbFrames().setText(String.valueOf(src.getValue()) + " ("+ time +")");
		}
	}
	
	private void manageSpinners(JSpinner src){
		if(src.equals(this.mainGui.getJsp16bitKeyword())){
			mainGui.getSlid16bitsWord().setValue(
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
				mainGui.getBtnEnter().setEnabled(false);
				//mainGui.getBtnOutputFile().setEnabled(false);
				mainGui.getSlidBitrate().setEnabled(false);
				mainGui.getTxtBitrate().setEnabled(false);				
				mainGui.getCombCodec().setEnabled(false);
				mainGui.getJcbExtension().setEnabled(false);
				mainGui.getSlidFrames().setEnabled(false);
				mainGui.getTxtNbFrames().setEnabled(false);
				mainGui.getChkStrictMode().setSelected(false);
				mainGui.getChkStrictMode().setEnabled(false);				
				mainGui.getRdi720().setEnabled(false);
				mainGui.getRdi768().setEnabled(false);
				mainGui.getChkPlayer().setEnabled(false);
				mainGui.getChkSound().setEnabled(false);
				mainGui.getChkHorodatage().setEnabled(false);				
			}
		} else if(src.equals(mainGui.getRdiVideo())){
			if(src.isSelected() && mainGui.getChkPlayer().isSelected()!=true){
				mainGui.getTxtInputFile().setText("");				
				mainGui.getBtnEnter().setEnabled(false);
				//mainGui.getBtnOutputFile().setEnabled(false);
				mainGui.getSlidBitrate().setEnabled(true);
				mainGui.getTxtBitrate().setEnabled(true);				
				mainGui.getCombCodec().setEnabled(true);
				mainGui.getJcbExtension().setEnabled(true);
				mainGui.getSlidFrames().setEnabled(true);
				mainGui.getTxtNbFrames().setEnabled(true);
				mainGui.getChkStrictMode().setEnabled(true);
				mainGui.getChkStrictMode().setSelected(true);
				mainGui.getCombAudience().setEnabled(true);
				mainGui.getRdi720().setEnabled(true);
				mainGui.getRdi768().setEnabled(true);
				mainGui.getChkPlayer().setEnabled(true);
				mainGui.getChkSound().setEnabled(true);
				mainGui.getChkHorodatage().setEnabled(true);				
				
			} else if(src.isSelected()){
				mainGui.getTxtInputFile().setText("");				
				mainGui.getBtnEnter().setEnabled(false);
				//mainGui.getBtnOutputFile().setEnabled(false);
				mainGui.getSlidFrames().setEnabled(true);
				mainGui.getChkStrictMode().setEnabled(true);
				mainGui.getChkStrictMode().setSelected(true);
				mainGui.getRdi720().setEnabled(true);
				mainGui.getRdi768().setEnabled(true);
				mainGui.getChkPlayer().setEnabled(true);
				mainGui.getChkHorodatage().setEnabled(true);
				
				
			}
		}
	}

	private void manageTxtFields(JTextField src) {
		// TODO Auto-generated method stub
		
	}

	private void manageButtons(JButton src) {
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
						"Une erreur de type exception s'est produite :" +
						e.getMessage(),
						"Erreur du programme",
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
						"Une erreur de type exception s'est produite :" +
						e.getMessage(),
						"Erreur du programme",
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

		String deb = "CryptImage v0.0.8" + "<br/>"
				+ "Copyright (C) 2014-12-29 Mannix54 <br/>";

		String link = "<a href=\"http://ibsoftware.free.fr/cryptimage.php\">http://ibsoftware.free.fr/cryptimage.php</a>"
				+ "<br/><br/>";

		String end = "under the GNU GPL v3 license <br/><br/>"
				+ "CryptImage comes with ABSOLUTELY NO WARRANTY<br/>"
				+ "This is free software, and you are welcome to redistribute"
				+ " it under certain conditions.";

		JEditorPane ep = new JEditorPane("text/html", "<html><body>" //
				+ deb + link + end + "</body></html>");
		
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

		JOptionPane.showMessageDialog(null, ep, "À propos...",
				JOptionPane.INFORMATION_MESSAGE);
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
			filter = new FileNameExtensionFilter("vidéos *.avi *.mp4 *.mpeg *.mpeg2 *.mkv *.ts *.m2t", extension);
		} else {
			extension = new String[] { "jpeg", "jpg", "bmp", "gif", "png",
					"tiff" };
			filter = new FileNameExtensionFilter("images *.jpeg *.jpg *.bmp *.gif *.png *.tiff", extension);
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
					if(mainGui.getTxtOutputFile().getText().equals("")){
						mainGui.getTxtOutputFile().setText(file.getParent());
					}					
					mainGui.getBtnOutputFile().setEnabled(true);
				} else {
					mainGui.getTxtInputFile().setText(file.getAbsolutePath());
					if(mainGui.getTxtOutputFile().getText().equals("")){
						mainGui.getTxtOutputFile().setText(file.getParent());
					}
					mainGui.getBtnOutputFile().setEnabled(true);
					mainGui.getBtnEnter().setEnabled(true);
				}
			}
		}
	}

	private void manageEnter(){
		mainGui.getTextInfos().setForeground(Color.black);
		
		mainGui.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
		mainGui.getBtnEnter().setEnabled(false);
		mainGui.getBtnCancel().setEnabled(true);
		mainGui.getBtnExit().setEnabled(false);
		mainGui.getBtnInputFile().setEnabled(false);
		mainGui.getBtnOutputFile().setEnabled(false);
		
		mainGui.getJob().setInput_file(mainGui.getTxtInputFile().getText());
		if (mainGui.getTxtOutputFile().getText().equals("")) {
			mainGui.getJob()
					.setOutput_file(mainGui.getTxtInputFile().getText());
		} else {
			File fic = new File(mainGui.getTxtInputFile().getText());
			if (fic.getName().lastIndexOf(".") != -1) {
				mainGui.getJob().setOutput_file(
						mainGui.getTxtOutputFile().getText()
								+ File.separator
								+ fic.getName().substring(0,
										fic.getName().lastIndexOf(".")));
			} else {
				mainGui.getJob().setOutput_file(
						mainGui.getTxtOutputFile().getText()
								+ File.separator
								+ fic.getName());
			}
		}
		mainGui.getJob().setWord16bits(Integer.valueOf(mainGui.getTxt16bitsWord().getText()));
		
		mainGui.getJob().setVideo_frame(Integer.valueOf(mainGui.getSlidFrames().getValue()));
		mainGui.getJob().setStrictMode(mainGui.getChkStrictMode().isSelected());
		mainGui.getJob().setPositionSynchro((int)mainGui.getJspFrameStart().getValue());
		mainGui.getJob().setWantDec(mainGui.getRdiDecoding().isSelected());
		mainGui.getJob().setWantPlay(mainGui.getChkPlayer().isSelected());
		mainGui.getJob().setModePhoto(mainGui.getRdiPhoto().isSelected());
		mainGui.getJob().setExtension(mainGui.getJcbExtension().getSelectedItem().toString());
		mainGui.getJob().setWantSound(mainGui.getChkSound().isSelected());
		mainGui.getJob().setDisableSound(mainGui.getChkDisableSound().isSelected());
				
		
		mainGui.getJob().setAudienceLevel(mainGui.getCombAudience().getSelectedIndex() + 1);
			
		
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
		
		mainGui.getJob().setSerial(mainGui.getTxtSerial().getText());
		mainGui.getJob().setCode(mainGui.getTxtCode().getText());
				
		mainGui.getJob().setStop(false);
		
		mainGui.getProgress().setMaximum(Integer.valueOf(mainGui.getSlidFrames().getValue()));
		
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
		
		StreamsFinder findAudio = new StreamsFinder(path);
		this.mainGui.getJob().setVideoHasAudioTrack(findAudio.isHasAudioTrack());
		
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
		            if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
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
		
		if(frameRate !=25){
			alertFrameRate(frameRate);
		}
		
		if(path.substring(path.lastIndexOf(".") + 1, path.length()).equals("ts")
				|| path.substring(path.lastIndexOf(".") + 1, path.length()).equals("m2t")){
			alertTsM2t();
		}
		
		mainGui.getJob().setFrameRate(frameRate);
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
	
	private void alertTsM2t() {
		mainGui.getTextInfos().setForeground(Color.red);
		mainGui.getTextInfos().append("Attention, les vidéos au format TS,"
				+ " ainsi que les vidéos"
				+ " au format M2T "
				+ "génèrent\r\n"
				+ "un bug"
				+ " dans la gestion du son avec cryptimage,"
				+ " en conséquence la gestion du son sera désactivée.");
		this.mainGui.getChkDisableSound().setSelected(true);
		this.mainGui.getChkSound().setSelected(false);
		this.mainGui.getChkSound().setEnabled(false);
	}

	private void alertFrameRate(double frameRate){
		mainGui.getTextInfos().setForeground(Color.red);
		mainGui.getTextInfos().append("Attention, la vidéo a un FPS de " +
								String.format("%.3f", frameRate) +
								", \r\nla vidéo traitée ne sera pas pleinement compatible avec"
								+ " la norme discret11.");		
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {			
		
		if(e.getSource().equals(mainGui.getJsp16bitKeyword()) 
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
			String  value = arg0.getDocument().getText(0, arg0.getDocument().getLength());
						

			updateKeyboardCode(value);
			
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {		
		
		try {
			String  value = arg0.getDocument().getText(0, arg0.getDocument().getLength());
						
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
			mainGui.getLbl11bitsInfo().setText( "( mot 11 bits: " +
					String.format("%04d", Integer.parseInt(audience1,2)) + " )");
			break;
		case 1:
			//audience 2
			String audience2 = word.substring(3, 14);
			mainGui.getLbl11bitsInfo().setText( "( mot 11 bits: " +
					String.format("%04d", Integer.parseInt(audience2,2))+ " )");
			break;
		case 2:
			//audience 3
			String audience3 = word.substring(6, 16) + word.charAt(0);
			mainGui.getLbl11bitsInfo().setText( "( mot 11 bits: " +
					String.format("%04d", Integer.parseInt(audience3,2))+ " )");
			break;
		case 3:
			//audience 4
			String audience4 =  word.substring(9, 16) + word.substring(0, 4);
			mainGui.getLbl11bitsInfo().setText( "( mot 11 bits: " +
					String.format("%04d", Integer.parseInt(audience4,2)) + " )");
			break;
		case 4:
			//audience 5
			String audience5 = word.substring(12, 16) +  word.substring(0, 7);
			mainGui.getLbl11bitsInfo().setText( "( mot 11 bits: " +
					String.format("%04d", Integer.parseInt(audience5,2))+ " )");
			break;
		case 5:
			//audience 6
			String audience6 =  word.charAt(15) + word.substring(0, 10) ;
			mainGui.getLbl11bitsInfo().setText( "( mot 11 bits: " +
					String.format("%04d", Integer.parseInt(audience6,2))+ " )");
			break;
		case 6:
			//audience 7		
			mainGui.getLbl11bitsInfo().setText("( mot 11 bits: 1337 )");
			break;
		default:
			break;	
		}		
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
		return this.mainGui.getJob().saveConfig(
				this.mainGui.getSlid16bitsWord().getValue(),
				this.mainGui.getCombAudience().getSelectedIndex() + 1,
				(double)(this.mainGui.getSlidDelay1().getValue())/100000d,
				(double)(this.mainGui.getSlidDelay2().getValue())/100000d,
				this.mainGui.getTxtSerial().getText(),
				this.mainGui.getChkHorodatage().isSelected(),
				this.mainGui.getCombCodec().getSelectedIndex() + 1,
				this.mainGui.getSlidBitrate().getValue(),
				(String)this.mainGui.getJcbExtension().getSelectedItem(),
				this.mainGui.getTxtOutputFile().getText());
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

}
