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
 * 26 oct. 2014 Author Mannix54
 * http://ibsoftware.free.fr/cryptimage.php
 */



package com.ib.cryptimage.gui;


import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;
import javax.swing.text.MaskFormatter;

import com.ib.cryptimage.core.JobConfig;


/**
 * @author Mannix54
 *
 */
public class MainGui {
	
	private JFrame frame;
	
	private JPanel panGlobal;
	
	private JPanel panMode;
	private JRadioButton rdiVideo;
	private JRadioButton rdiPhoto;
	private JButton btnAbout;
	
	private JPanel panFile;
	private JTextField txtInputFile;
	private JTextField txtOutputFile;
	private JButton btnInputFile;
	private JButton btnOutputFile;
	
	private JPanel panOptionsDiscret11;
	private JPanel panKeyboardCode;
	private JRadioButton rdiDiscretCoding;
	private JRadioButton rdiDiscretDecoding;
	private JRadioButton rdiDiscretCorrel;
	private JRadioButton rdiSysterCodingGen;
	private JRadioButton rdiSysterDecodingGen;
	private JRadioButton rdiSysterDecodingCorrel;
	private JRadioButton rdiSysterCodingFile;
	private JRadioButton rdiSysterCodingRandom;
	private JRadioButton rdiSysterDecodingFile;
	private JRadioButton rdiSysterDeCodingTags;
	private JCheckBox chkTags;
	private JButton btnFileSysterEnc;
	private JButton btnFileSysterDec;
	
	private JLabel lab16bitsWord;
	private JFormattedTextField txt16bitsWord;
	private JSlider slid16bitsWord;
	private JLabel labAudience;
	private JComboBox<String> combAudience;
	private JLabel labNbFrames;
	private JTextField txtNbFrames;
	private JSlider slidFrames;	
	private JCheckBox chkPlayer;
	private JCheckBox chkStrictMode;
	private JLabel labDelay1;
	private JTextField txtDelay1;
	private JSlider slidDelay1;
	private JLabel labDelay2;
	private JTextField txtDelay2;
	private JSlider slidDelay2;
	private JCheckBox chkDelay;
	private JSpinner jsp16bitKeyword;
	private JLabel labFrameStart;
	private JLabel labFrameStartSyster;
	private JSpinner jspFrameStart;
	private JSpinner jspFrameStartSyster;
	private JSlider slideFrameStart;
	private JSlider slideFrameStartSyster;
	private JCheckBox chkAutorisation1;
	private JCheckBox chkAutorisation2;
	private JCheckBox chkAutorisation3;
	private JCheckBox chkAutorisation4;
	private JCheckBox chkAutorisation5;
	private JCheckBox chkAutorisation6;
	private JLabel labSerial;
	private JFormattedTextField txtSerial;
	private JLabel labCode;
	private JTextField txtCode;
	private JCheckBox chkSound;
	private JCheckBox chkSoundSyster;
	private JLabel lbl11bitsInfo;
	private JCheckBox chkDisableSound;
	private JCheckBox chkDisableSoundSyster;
	private JFormattedTextField txtMultiCode;
	private JSpinner jspCycle;
	private JLabel lblMultiAudience;
	private JLabel lblCycle;
	private JCheckBox chkNoBlackBar;

	private JPanel panVideoOptions;
	private JLabel labCodec;
	private JComboBox<String> combCodec;
	private JLabel labBitrate;
	private JTextField txtBitrate;
	private JSlider slidBitrate;
	private JRadioButton rdi720;
	private JRadioButton rdi768;
	private JCheckBox chkHorodatage;
	private JLabel labAudioCodec;
	private JComboBox<String> combAudioCodec;
	private JComboBox<String> combAudioRate;
	
	private JPanel panProgress;
	private JProgressBar progress;
	private JTextArea textInfos;
	

	private JButton btnEnter;
	private JButton btnExit;
	private JButton btnCancel;
	
	private JLabel lblExtension;
	private JComboBox<String> jcbExtension;
	
	private JLabel lblSystemCrypt;
	private JComboBox<String> combSystemCrypt;

	private JPanel panOptionsSyster;

	private JPanel panCoderSyster;

	private JPanel panDecoderSyster;
	
	private JPanel panSystemCrypt;
	private CardLayout card;
	private CardLayout cardEncDecSyster;
	private JTextField txtSysterEnc;
	private JTextField txtSysterDec;
	private JPanel panSysterMode;
	private JPanel panSysterEncDecCard;
	private JPanel panSysterMisc;
	private JComboBox<String> comboTableSysterEnc;
	private JComboBox<String> comboTableSysterDec;
	private MainGui_ActionListener controler;
	
	private JMenuBar menuBar;
	private JMenu mFile;
	private JMenu mTools;
	private JMenu mLang;
	private JMenu mHelp;
	private JMenuItem mOpen;
	private JMenuItem mExit;
	private JMenuItem mGen;
	private JMenuItem mDocumentation;
	private JMenuItem mAbout;
	private JRadioButtonMenuItem  mAuto;
	private JRadioButtonMenuItem  mEnglish;
	private JRadioButtonMenuItem  mGerman;
	private JRadioButtonMenuItem  mItalian;
	private JRadioButtonMenuItem  mSpanish;
	private JRadioButtonMenuItem  mPolish;
	private JRadioButtonMenuItem  mFrench;
	
	private TitledBorder titlePanLog;
	private TitledBorder titlePanMode;
	private TitledBorder titlePanFile;
	private TitledBorder titlePanDiscret11;
	private TitledBorder titlePanKeyboard;
	private TitledBorder titlePanVideo;
	private TitledBorder titlePanSyster;
	private TitledBorder titlePanSysterCodingGen;
	private TitledBorder titlePanSysterDecodingGen;
	private TitledBorder titleColorMode;
	
	private JComboBox<String> cbColorMode;
	private JLabel lblColorMode;
	private JCheckBox cbAveragePal;
	private JLabel lblYUV;
	private JComboBox<String> cbYUV;
	
	private JRadioButton rdiVideocryptCoding;
	private JRadioButton rdiVideocryptDecoding;
	private JRadioButton rdiVideocryptCorrel;
	private JRadioButton rdiVideocryptDecodingFile;
	private JRadioButton rdiVideocryptCodingAuto;
	private JRadioButton rdiVideocryptCodingFile;
	private CardLayout cardEncDecVideocrypt;
	private JPanel panVideocryptOptions;
	private JTextField txtVideocryptDec;
	private JButton btnVideocryptDec;	
	
	private JTextField txtVideocryptEnc;
	private JButton btnVideocryptEnc;
	
	
	private JCheckBox chkSoundVideocrypt;	
	private JCheckBox chkDisableSoundVideocrypt;
	private JLabel labFrameStartVideocrypt;
	private JSpinner jspFrameStartVideocrypt;	
	private JSlider slideFrameStartVideocrypt;
	private TitledBorder titlePanVideocrypt;
	private JPanel panColorMode;
	
	private TitledBorder titlePanVideocryptCodingGen;
	private TitledBorder titlePanVideocryptDecodingGen;
	private JPanel panVideocryptEncDecCard;
	
	private JTabbedPane tabbedPane;
	
	private JCheckBox chkChangeOffsetIncrement;
	private JCheckBox chkRestrictRange;
	private JCheckBox chkLogVideocrypt;
	private JCheckBox chkVideocryptTags;
	private JRadioButton rdiVideocryptTagsDecoding;
	
	public MainGui(){			
		JobConfig.setRes(ResourceBundle.getBundle("ressources/mainGui", Locale.getDefault())); 		
		
		controler = new MainGui_ActionListener(this);
			
		JobConfig.setGui(this);
		
		 try {	            
	        UIManager.setLookAndFeel(
	        		"com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
	    } 
	    catch (UnsupportedLookAndFeelException e) {
	       // handle exception
	    }
	    catch (ClassNotFoundException e) {
	       // handle exception
	    }
	    catch (InstantiationException e) {
	       // handle exception
	    }
	    catch (IllegalAccessException e) {
	       // handle exception
	    }
		
		frame = new JFrame("CryptImage v" + JobConfig.getVERSION());
		frame.addWindowListener(controler);
		frame.setLayout(new GridLayout(1,1));
		panGlobal = new JPanel();
		
		frame.setSize(740,700);
		frame.setLocationRelativeTo(null);
		frame.setAutoRequestFocus(true);
		frame.setMinimumSize(new Dimension(740, 700));
		frame.setResizable(true);		
		frame.setIconImage(new ImageIcon(this.getClass().getResource("/icons/logo_jframe.png")).getImage());
		
		createMenu();
		
		createPanMode();
		createPanFile();		
		createPanDiscret11();
		createColorMode();
		createPanSyster();
		createPanVideo();
		createPanLog();
		createPanVideocrypt();
				
		panSystemCrypt = new JPanel();
		card = new CardLayout();
		panSystemCrypt.setLayout(card);
		panSystemCrypt.add(panOptionsDiscret11, "Discret11");
		panSystemCrypt.add(panOptionsSyster, "Syster");
		panSystemCrypt.add(panVideocryptOptions, "Videocrypt");
		
		tabbedPane = new JTabbedPane();
		tabbedPane.add(JobConfig.getRes().getString("tabbedPane.Device"), panSystemCrypt);
		tabbedPane.add(JobConfig.getRes().getString("tabbedPane.Colors"), panColorMode);
		tabbedPane.add(JobConfig.getRes().getString("tabbedPane.AudioVideo"), panVideoOptions);
		
		panGlobal.setLayout(new BoxLayout(panGlobal, BoxLayout.Y_AXIS ));
		panGlobal.add(panMode);
		panGlobal.add(panFile);		
		panGlobal.add(tabbedPane);		
		panGlobal.add(panProgress);

		//load config
		if(JobConfig.loadConfig()){
			
			switch (JobConfig.getLang()) {
			case 0:
				JobConfig.setRes(ResourceBundle.getBundle("ressources/mainGui", Locale.getDefault()));
				mAuto.setSelected(true);
				break;
			case 1:
				JobConfig.setRes(ResourceBundle.getBundle("ressources/mainGui", Locale.GERMAN));
				mGerman.setSelected(true);				
				break;
			case 2:
				JobConfig.setRes(ResourceBundle.getBundle("ressources/mainGui", Locale.ENGLISH));
				mEnglish.setSelected(true);				
				break;
			case 3:
				JobConfig.setRes(ResourceBundle.getBundle("ressources/mainGui", new Locale("es")));
				mSpanish.setSelected(true);				
				break;
			case 4:
				JobConfig.setRes(ResourceBundle.getBundle("ressources/mainGui", Locale.FRENCH));
				mFrench.setSelected(true);				
				break;
			case 5:
				JobConfig.setRes(ResourceBundle.getBundle("ressources/mainGui", Locale.ITALIAN));
				mItalian.setSelected(true);				
				break;
			case 6:
				JobConfig.setRes(ResourceBundle.getBundle("ressources/mainGui", new Locale("pl")));
				mPolish.setSelected(true);				
				break;
			default:
				break;
			}
						
			this.refreshGUI();
			
			this.slid16bitsWord.setValue(JobConfig.getWord16bits());
			this.combAudience.setSelectedIndex(JobConfig.getAudienceLevel());
			this.slidDelay1.setValue((int) (JobConfig.getPerc1() * 100000d));
			this.slidDelay2.setValue((int) (JobConfig.getPerc2() * 100000d));
			this.txtSerial.setText(JobConfig.getSerial());
			this.chkHorodatage.setSelected(JobConfig.isHorodatage());
			this.combCodec.setSelectedIndex(JobConfig.getVideoCodec());
			this.slidBitrate.setValue(JobConfig.getVideoBitrate());
			switch (JobConfig.getExtension()) {
			case "mp4":
				this.jcbExtension.setSelectedIndex(0);
				break;
			case "avi":
				this.jcbExtension.setSelectedIndex(1);
				break;
			case "mkv":
				this.jcbExtension.setSelectedIndex(2);
				break;
			case "mpeg":
				this.jcbExtension.setSelectedIndex(3);
				break;
			case "ts":
				this.jcbExtension.setSelectedIndex(4);
				break;
			default:
				break;
			}
			this.txtOutputFile.setText(JobConfig.getOutput_file());
			this.txtMultiCode.setText(JobConfig.getMultiCode());			
			this.jspCycle.setValue(JobConfig.getCycle());
			if(JobConfig.getResolution() == 1){
				this.rdi720.setSelected(true);
			}
			else{
				this.rdi768.setSelected(true);
			}
			//audio codec
			this.combAudioCodec.setSelectedIndex(JobConfig.getAudioCodec());
			//audio rate
			if(JobConfig.getAudioRate() == 44100){
				this.combAudioRate.setSelectedIndex(0);
			}
			else{
				this.combAudioRate.setSelectedIndex(1);
			}
			this.combSystemCrypt.setSelectedIndex(JobConfig.getSystemCrypt());
		}
		
		//check documentation
		copyNewHelpVersion();

		frame.setLayout(new GridLayout(1,0));
		frame.getContentPane().add(panGlobal);		
		frame.setVisible(true);
		frame.repaint();		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.combAudience.setSelectedIndex(0);
		this.combAudience.setSelectedIndex(JobConfig.getAudienceLevel());
	}
	
	private void copyNewHelpVersion(){
		if (!JobConfig.getLaunch().equals(JobConfig.getVERSION())) {
			
			// test if cryptimage directory exists
			boolean success = new File(System.getProperty("user.home")
					+ File.separator + "cryptimage").exists();
			
			if(success == false){
			success = new File(System.getProperty("user.home")
					+ File.separator + "cryptimage").mkdir();
			}		
			
			// we install the documentation to the home user
			// french version
			installHelpFile("cryptimage_fr.pdf");
			// english version
			installHelpFile("cryptimage_en.pdf");
			// polish version
			installHelpFile("cryptimage_pl.pdf");
		}
	}
	
	public void installHelpFile(String fileName){
		// we install the documentation to the home user
		String userHome = System.getProperty("user.home");
		File configPDF = new File(userHome + File.separator + "cryptimage" + File.separator + fileName);

		InputStream is = this.getClass().getResourceAsStream("/ressources/" + fileName);

		File newFile = new File(System.getProperty("java.io.tmpdir") + File.separator + fileName);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(newFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		int read = 0;
		byte[] bytes = new byte[1024];

		try {
			while ((read = is.read(bytes)) != -1) {
				try {
					fos.write(bytes, 0, read);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			fos.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			Files.copy(newFile.toPath(), configPDF.toPath(), REPLACE_EXISTING);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this.frame, JobConfig.getRes().getString("mainGui.help.errorInstall"),
					JobConfig.getRes().getString("mainGui.help.errorInstall.title"), JOptionPane.ERROR_MESSAGE);
		}

		try {
			Files.delete(newFile.toPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void refreshGUI(){
		//menu bar
		mFile.setText(JobConfig.getRes().getString("mFile"));
		mTools.setText(JobConfig.getRes().getString("mTools"));
		mLang.setText(JobConfig.getRes().getString("mLang"));
		mHelp.setText(JobConfig.getRes().getString("mHelp"));
		mOpen.setText(JobConfig.getRes().getString("mOpenFile"));
		mExit.setText(JobConfig.getRes().getString("mExit"));
		mGen.setText(JobConfig.getRes().getString("mGen"));
		mEnglish.setText(JobConfig.getRes().getString("mEnglish"));
		mFrench.setText(JobConfig.getRes().getString("mFrench"));
		mSpanish.setText(JobConfig.getRes().getString("mSpanish"));
		mGerman.setText(JobConfig.getRes().getString("mGerman"));
		mItalian.setText(JobConfig.getRes().getString("mItalian"));
		mPolish.setText(JobConfig.getRes().getString("mPolish"));
		mDocumentation.setText(JobConfig.getRes().getString("mDocumentation"));
		mAbout.setText(JobConfig.getRes().getString("mAbout"));
		
		//panLog
		titlePanLog.setTitle(JobConfig.getRes().getString("panLog.informations"));
		btnEnter.setText(JobConfig.getRes().getString("panLog.enter"));
		btnExit.setText(JobConfig.getRes().getString("panLog.exit"));
		btnCancel.setText(JobConfig.getRes().getString("panLog.cancel"));
		
		//panMode
		titlePanMode.setTitle(JobConfig.getRes().getString("panMode.mode"));
		rdiVideo.setText(JobConfig.getRes().getString("panMode.video"));
		rdiVideo.setToolTipText(JobConfig.getRes().getString("panMode.tooltip.video"));
		rdiPhoto.setText(JobConfig.getRes().getString("panMode.photo"));
		rdiPhoto.setToolTipText(JobConfig.getRes().getString("panMode.tooltip.photo"));
		chkStrictMode.setText(JobConfig.getRes().getString("panMode.respectNorme"));
		chkStrictMode.setToolTipText(JobConfig.getRes().getString("panMode.tooltip.respectNorme"));
		lblSystemCrypt.setText(JobConfig.getRes().getString("panMode.lblSystem"));
		btnAbout.setText(JobConfig.getRes().getString("mAbout"));
		
		//panfile
		titlePanFile.setTitle(JobConfig.getRes().getString("panFile.title"));
		btnInputFile.setText(JobConfig.getRes().getString("panFile.open"));
		btnInputFile.setToolTipText(JobConfig.getRes().getString("panFile.tooltip.open"));
		btnOutputFile.setText(JobConfig.getRes().getString("panFile.folder"));
		btnOutputFile.setToolTipText(JobConfig.getRes().getString("panFile.tooltip.folder"));
		
		//panDiscret11
		titlePanDiscret11.setTitle(JobConfig.getRes().getString("panDiscret11.title"));
		rdiDiscretCoding.setText(JobConfig.getRes().getString("panDiscret11.coding"));
		rdiDiscretDecoding.setText(JobConfig.getRes().getString("panDiscret11.decoding"));
		rdiDiscretCorrel.setText(JobConfig.getRes().getString("panDiscret11.decodingCorrel"));
		lab16bitsWord.setText(JobConfig.getRes().getString("panDiscret11.lab16bits"));
		
		labAudience.setText(JobConfig.getRes().getString("panDiscret11.labAudience"));
		int index = combAudience.getSelectedIndex();
		String[] tab = {JobConfig.getRes().getString("panDiscret11.AudienceLevel1"),JobConfig.getRes().getString("panDiscret11.AudienceLevel2"),JobConfig.getRes().getString("panDiscret11.AudienceLevel3"),JobConfig.getRes().getString("panDiscret11.AudienceLevel4"),
				JobConfig.getRes().getString("panDiscret11.AudienceLevel5"),JobConfig.getRes().getString("panDiscret11.AudienceLevel6"),JobConfig.getRes().getString("panDiscret11.AudienceLevel7"),JobConfig.getRes().getString("panDiscret11.AudienceLevelMulti")};
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>( tab );
		combAudience.setModel(model);
		combAudience.setSelectedIndex(index);
		
		lbl11bitsInfo.setToolTipText(JobConfig.getRes().getString("panDiscret11.lab11bits.tooltip"));
		txtMultiCode.setToolTipText(JobConfig.getRes().getString("panDiscret11.txtMultiCode.tooltip"));
		lblMultiAudience.setText(JobConfig.getRes().getString("panDiscret11.labMultiAudience"));
		lblCycle.setText(JobConfig.getRes().getString("panDiscret11.labCycle"));
		jspCycle.setToolTipText(JobConfig.getRes().getString("panDiscret11.jspCycle.tooltip"));
		chkDelay.setText(JobConfig.getRes().getString("panDiscret11.chkDelay"));
		labDelay1.setText(JobConfig.getRes().getString("panDiscret11.labDelay1"));
		labDelay2.setText(JobConfig.getRes().getString("panDiscret11.labDelay2"));
		labFrameStart.setText(JobConfig.getRes().getString("panDiscret11.labFrameStart"));
		slideFrameStart.setToolTipText(JobConfig.getRes().getString("panDiscret11.slideFrameStart.tooltip"));
		chkNoBlackBar.setText(JobConfig.getRes().getString("panDiscret11.chkNoBlackBar"));
		chkNoBlackBar.setToolTipText(JobConfig.getRes().getString("panDiscret11.chkNoBlackBar.tooltip"));
		chkSound.setText(JobConfig.getRes().getString("panDiscret11.chkSound"));
		chkSound.setToolTipText(JobConfig.getRes().getString("panDiscret11.chkSound.tooltip"));
		chkDisableSound.setText(JobConfig.getRes().getString("panDiscret11.chkDisableSound"));
		chkDisableSound.setToolTipText(JobConfig.getRes().getString("panDiscret11.chkDisableSound.tooltip"));
			
		//createPanKeyboard
		titlePanKeyboard.setTitle(JobConfig.getRes().getString("panKeyboard.titlePanKeyboard"));
		labSerial.setText(JobConfig.getRes().getString("panKeyboard.labSerial"));
		labCode.setText(JobConfig.getRes().getString("panKeyboard.labCode"));
		
		//createPanVideo
		titlePanVideo.setTitle(JobConfig.getRes().getString("panVideo.titlePanVideo"));
		labAudioCodec.setText(JobConfig.getRes().getString("panVideo.labAudioCodec"));
		chkPlayer.setText(JobConfig.getRes().getString("panVideo.chkPlayer"));
		chkPlayer.setToolTipText(JobConfig.getRes().getString("panVideo.chkPlayer.tooltip"));
		chkHorodatage.setText(JobConfig.getRes().getString("panVideo.chkHorodatage"));
		chkHorodatage.setToolTipText(JobConfig.getRes().getString("panVideo.chkHorodatage.tooltip"));
		labCodec.setText(JobConfig.getRes().getString("panVideo.labCodec"));
		labBitrate.setText(JobConfig.getRes().getString("panVideo.labBitrate"));
		labNbFrames.setText(JobConfig.getRes().getString("panVideo.labNbFrames"));
		lblExtension.setText(JobConfig.getRes().getString("panVideo.lblExtension"));
		
		//createPanSyster
		titlePanSyster.setTitle(JobConfig.getRes().getString("panSyster.titlePanSyster"));
		titlePanSysterCodingGen.setTitle(JobConfig.getRes().getString("panSyster.titlePanSysterCodingGen"));
		titlePanSysterDecodingGen.setTitle(JobConfig.getRes().getString("panSyster.titlePanSysterDecodingGen"));
		rdiSysterCodingGen.setText(JobConfig.getRes().getString("panSyster.rdiSysterCodingGen"));
		rdiSysterDecodingGen.setText(JobConfig.getRes().getString("panSyster.rdiSysterDecodingGen"));
		btnFileSysterEnc.setText(JobConfig.getRes().getString("panSyster.btnFileSysterEnc"));
		btnFileSysterDec.setText(JobConfig.getRes().getString("panSyster.btnFileSysterDec"));
		rdiSysterCodingRandom.setText(JobConfig.getRes().getString("panSyster.rdiSysterCodingRandom"));
		rdiSysterCodingFile.setText(JobConfig.getRes().getString("panSyster.rdiSysterCodingFile"));
		chkTags.setText(JobConfig.getRes().getString("panSyster.chkTags"));
		
		int indexSysterEnc = getComboTableSysterEnc().getSelectedIndex();
		int indexSysterDec = comboTableSysterDec.getSelectedIndex();
		String[] tabSyster ={JobConfig.getRes().getString("panSyster.comboTableSysterEnc.menu1"),
				JobConfig.getRes().getString("panSyster.comboTableSysterEnc.menu2")};
		
		DefaultComboBoxModel<String> modelSysterEnc = new DefaultComboBoxModel<String>( tabSyster );
		DefaultComboBoxModel<String> modelSysterDec = new DefaultComboBoxModel<String>( tabSyster );
		getComboTableSysterEnc().setModel(modelSysterEnc);
		getComboTableSysterEnc().setSelectedIndex(indexSysterEnc);
		comboTableSysterDec.setModel(modelSysterDec);
		comboTableSysterDec.setSelectedIndex(indexSysterDec);
		
		rdiSysterDecodingFile.setText(JobConfig.getRes().getString("panSyster.rdiSysterDecodingFile"));
		rdiSysterDeCodingTags.setText(JobConfig.getRes().getString("panSyster.rdiSysterDeCodingTags"));
		rdiSysterDecodingCorrel.setText(JobConfig.getRes().getString("panSyster.rdiSysterDecodingCorrel"));
		labFrameStartSyster.setText(JobConfig.getRes().getString("panSyster.labFrameStartSyster"));
		slideFrameStartSyster.setToolTipText(JobConfig.getRes().getString("panSyster.labFrameStartSyster.tooltip"));
		chkSoundSyster.setText(JobConfig.getRes().getString("panSyster.chkSoundSyster"));
		chkSoundSyster.setToolTipText(JobConfig.getRes().getString("panSyster.chkSoundSyster.tooltip"));
		chkDisableSoundSyster.setText(JobConfig.getRes().getString("panSyster.chkDisableSoundSyster"));
		chkDisableSoundSyster.setToolTipText(JobConfig.getRes().getString("panSyster.chkDisableSoundSyster.tooltip"));
		
		chkChangeOffsetIncrement.setText(JobConfig.getRes().getString("panSyster.chkOffsetIncrement"));
		
		int indexColor = cbColorMode.getSelectedIndex();
		String[] tabColor = {JobConfig.getRes().getString("cbColorMode.rgb"),
				JobConfig.getRes().getString("cbColorMode.pal"),
				JobConfig.getRes().getString("cbColorMode.secam")};
		DefaultComboBoxModel<String> modelColor = new DefaultComboBoxModel<String>( tabColor );
		cbColorMode.setModel(modelColor);
		cbColorMode.setSelectedIndex(indexColor);		
		
		lblColorMode.setText(JobConfig.getRes().getString("lblColorMode.text"));
		cbAveragePal.setText(JobConfig.getRes().getString("cbAveragePal.text"));
		
		lblYUV.setText(JobConfig.getRes().getString("lblYUV.title"));
	    int indexYUV = cbYUV.getSelectedIndex();
		String[] tabYUV = {"bt.601", "bt.709",
				JobConfig.getRes().getString("cbYUV.special")};
		DefaultComboBoxModel<String> modelYUV = new DefaultComboBoxModel<String>( tabYUV );
		cbYUV.setModel(modelYUV);
		cbYUV.setSelectedIndex(indexYUV);	
		
		//videocrypt options
		titlePanVideocrypt.setTitle(JobConfig.getRes().getString("panVideocrypt.titlePanVideocrypt"));
		rdiVideocryptCoding.setText(JobConfig.getRes().getString("panVideocrypt.rdiVideocryptCoding"));
		rdiVideocryptDecoding.setText(JobConfig.getRes().getString("panVideocrypt.rdiVideocryptDecoding"));
		rdiVideocryptCorrel.setText(JobConfig.getRes().getString("panVideocrypt.rdiVideocryptCorrel"));
		chkSoundVideocrypt.setText(JobConfig.getRes().getString("panVideocrypt.chkSoundVideocrypt"));
		chkDisableSoundVideocrypt.setText(JobConfig.getRes().getString("panVideocrypt.chkDisableSoundVideocrypt"));
		btnVideocryptDec.setText(JobConfig.getRes().getString("panVideocrypt.btnDec"));
		labFrameStartVideocrypt.setText(JobConfig.getRes().getString("panVideocrypt.lblFrameStart"));
		
		titleColorMode.setTitle(JobConfig.getRes().getString("panColorMode.title"));
		
		titlePanVideocryptCodingGen.setTitle(JobConfig.getRes().getString("panSyster.titlePanSysterCodingGen"));
		titlePanVideocryptDecodingGen.setTitle(JobConfig.getRes().getString("panSyster.titlePanSysterDecodingGen"));
		btnVideocryptEnc.setText(JobConfig.getRes().getString("panVideocrypt.btnDec"));
		rdiVideocryptCodingAuto.setText(JobConfig.getRes().getString("panSyster.rdiSysterCodingRandom"));
		rdiVideocryptDecodingFile.setText(JobConfig.getRes().getString("panSyster.rdiSysterDecodingFile"));
		rdiVideocryptCodingFile.setText(JobConfig.getRes().getString("panSyster.rdiSysterCodingFile"));
	
		chkRestrictRange.setText(JobConfig.getRes().getString("panVideocrypt.chkRestrictRange"));
		chkLogVideocrypt.setText(JobConfig.getRes().getString("panVideocrypt.chkLogVideocrypt"));
		
		tabbedPane.setTitleAt(0, JobConfig.getRes().getString("tabbedPane.Device"));
		tabbedPane.setTitleAt(1, JobConfig.getRes().getString("tabbedPane.Colors"));
		tabbedPane.setTitleAt(2, JobConfig.getRes().getString("tabbedPane.AudioVideo"));
		
		chkVideocryptTags.setText(JobConfig.getRes().getString("panVideocrypt.chkTags"));
		rdiVideocryptTagsDecoding.setText(JobConfig.getRes().getString("panVideocrypt.rdiTagsDecoding"));
	}
	
	private void createMenu(){				
		menuBar = new JMenuBar();
		mFile = new JMenu(JobConfig.getRes().getString("mFile"));
		mTools = new JMenu(JobConfig.getRes().getString("mTools"));
		mLang = new JMenu(JobConfig.getRes().getString("mLang"));
		mHelp = new JMenu(JobConfig.getRes().getString("mHelp"));
		mOpen = new JMenuItem(JobConfig.getRes().getString("mOpenFile"));
		mOpen.setIcon(new ImageIcon(this.getClass().getResource("/icons/filenew.png")));
		mOpen.addActionListener(controler);
		mExit = new JMenuItem(JobConfig.getRes().getString("mExit"));
		mExit.addActionListener(controler);
		mExit.setIcon(new ImageIcon(this.getClass().getResource("/icons/exit.png")));
		mGen = new JMenuItem(JobConfig.getRes().getString("mGen"));
		mGen.addActionListener(controler);
		mGen.setIcon(new ImageIcon(this.getClass().getResource("/icons/encrypted.png")));
		
		mAuto = new JRadioButtonMenuItem("Auto");
		mAuto.addActionListener(controler);
		mEnglish = new JRadioButtonMenuItem(JobConfig.getRes().getString("mEnglish"));
		mEnglish.setIcon(new ImageIcon(this.getClass().getResource("/icons/gb.png")));
		mEnglish.addActionListener(controler);
		
		mFrench = new JRadioButtonMenuItem(JobConfig.getRes().getString("mFrench"));
		mFrench.setIcon(new ImageIcon(this.getClass().getResource("/icons/fr.png")));
		mFrench.addActionListener(controler);
		
		mGerman = new JRadioButtonMenuItem(JobConfig.getRes().getString("mGerman"));
		mGerman.setIcon(new ImageIcon(this.getClass().getResource("/icons/de.png")));
		mGerman.addActionListener(controler);
		
		mSpanish = new JRadioButtonMenuItem(JobConfig.getRes().getString("mSpanish"));
		mSpanish.setIcon(new ImageIcon(this.getClass().getResource("/icons/es.png")));
		mSpanish.addActionListener(controler);
		
		mItalian = new JRadioButtonMenuItem(JobConfig.getRes().getString("mItalian"));
		mItalian.setIcon(new ImageIcon(this.getClass().getResource("/icons/it.png")));
		mItalian.addActionListener(controler);
		
		mPolish = new JRadioButtonMenuItem(JobConfig.getRes().getString("mPolish"));
		mPolish.setIcon(new ImageIcon(this.getClass().getResource("/icons/pl.png")));
		mPolish.addActionListener(controler);
		
		mAbout = new JMenuItem(JobConfig.getRes().getString("mAbout"));		
		mAbout.addActionListener(controler);
		mAbout.setIcon(new ImageIcon(this.getClass().getResource("/icons/help.png")));
		mDocumentation = new JMenuItem(JobConfig.getRes().getString("mDocumentation"));
		mDocumentation.addActionListener(controler);
		mDocumentation.setIcon(new ImageIcon(this.getClass().getResource("/icons/documentation.png")));
		
		menuBar.add(mFile);
		menuBar.add(mTools);
		menuBar.add(mLang);
		menuBar.add(mHelp);
				
		mFile.add(mOpen);
		mFile.setMnemonic('F');
		mOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
		mFile.add(mExit);
		mTools.add(mGen);
		mTools.setMnemonic('t');
		
		mLang.add(mAuto);
		mLang.add(mGerman);
		mLang.add(mEnglish);
		mLang.add(mSpanish);
		mLang.add(mFrench);
		mLang.add(mItalian);
		mLang.add(mPolish);
		ButtonGroup btgLang = new ButtonGroup();		
		btgLang.add(mAuto);
		btgLang.add(mGerman);
		btgLang.add(mEnglish);
		btgLang.add(mSpanish);
		btgLang.add(mFrench);
		btgLang.add(mItalian);
		btgLang.add(mPolish);
		mAuto.setSelected(true); // TODO to be configured by preference file
		
		mHelp.add(mDocumentation);
		mHelp.add(mAbout);
		mHelp.setMnemonic('A');
		frame.setJMenuBar(menuBar);
		
	}
	
	private void createPanMode(){
		panMode = new JPanel();		
				
		titlePanMode = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				JobConfig.getRes().getString("panMode.mode"));
		panMode.setBorder(titlePanMode);
		
		rdiVideo = new JRadioButton(JobConfig.getRes().getString("panMode.video"));
		rdiVideo.setSelected(true);
		rdiVideo.addActionListener(controler);
		rdiVideo.setToolTipText(JobConfig.getRes().getString("panMode.tooltip.video")); //("pour coder ou décoder des fichiers vidéos");
		rdiPhoto = new JRadioButton(JobConfig.getRes().getString("panMode.photo"));
		rdiPhoto.addActionListener(controler);
		rdiPhoto.setToolTipText(JobConfig.getRes().getString("panMode.tooltip.photo"));//("pour coder ou décoder des fichiers images");
		ButtonGroup btnGroup = new ButtonGroup();
		btnGroup.add(rdiVideo);
		btnGroup.add(rdiPhoto);
		
		chkStrictMode = new JCheckBox(JobConfig.getRes().getString("panMode.respectNorme"));		
		chkStrictMode.addActionListener(controler);
		chkStrictMode.setSelected(true);
		chkStrictMode.setToolTipText(JobConfig.getRes().getString("panMode.tooltip.respectNorme"));
		
		lblSystemCrypt = new JLabel(JobConfig.getRes().getString("panMode.lblSystem"));
		String [] tab = {"Discret11", "Nagravision syster", "Videocrypt"};
		combSystemCrypt = new JComboBox<>(tab);
		combSystemCrypt.addActionListener(controler);
		
		
		btnAbout = new JButton(JobConfig.getRes().getString("mAbout"));
		btnAbout.setIcon(new ImageIcon(this.getClass().getResource("/icons/help.png")));
		btnAbout.addActionListener(controler);
		
		GridBagLayout gbl = new GridBagLayout();
				
		this.placerComposants(panMode,
				gbl,
				rdiVideo,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				0, 0,
				1,1,
				25,0.5,
				1, 1,1,1);	
		this.placerComposants(panMode,
				gbl,
				rdiPhoto,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				1, 0,
				1,1,
				25,0.5,
				1, 1,1,1);
		this.placerComposants(panMode,
				gbl,
				chkStrictMode,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				2, 0,
				1,1,
				25,0.5,
				1, 1,1,1);
		this.placerComposants(panMode,
				gbl,
				lblSystemCrypt,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				3, 0,
				1,1,
				25,0.5,
				1, 1,1,1);	
		this.placerComposants(panMode,
				gbl,
				combSystemCrypt,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				4, 0,
				1,1,
				25,0.5,
				1, 1,1,1);	
		this.placerComposants(panMode,
				gbl,
				btnAbout,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				5, 0,
				1,1,
				25,0.5,
				1, 1,1,1);	
	}
	
	private void createPanFile(){
		panFile = new JPanel();
				
		titlePanFile = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				JobConfig.getRes().getString("panFile.title"));
		panFile.setBorder(titlePanFile);
		
		txtInputFile = new JTextField(40);
		txtInputFile.setEditable(false);
		txtOutputFile = new JTextField(40);
		txtOutputFile.setEditable(false);
		btnInputFile = new JButton(JobConfig.getRes().getString("panFile.open"));
		btnInputFile.setIcon(new ImageIcon(this.getClass().getResource("/icons/filenew.png")));
		btnInputFile.setToolTipText(JobConfig.getRes().getString("panFile.tooltip.open"));
		btnInputFile.addActionListener(controler);
		btnOutputFile = new JButton(JobConfig.getRes().getString("panFile.folder"));
		btnOutputFile.setIcon(new ImageIcon(this.getClass().getResource("/icons/fileopen.png")));
		btnOutputFile.setEnabled(true);
		btnOutputFile.addActionListener(controler);
		btnOutputFile.setToolTipText(JobConfig.getRes().getString("panFile.tooltip.folder"));
		
		
		GridBagLayout gbl = new GridBagLayout();
		this.placerComposants(panFile,
				gbl,
				txtInputFile,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				0, 0,
				1,1,
				99,50,
				1, 1,1,1);
		this.placerComposants(panFile,
				gbl,
				btnInputFile,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				1, 0,
				1,1,
				1,50,
				1, 1,1,1);
		this.placerComposants(panFile,
				gbl,
				txtOutputFile,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				0, 1,
				1,1,
				99,50,
				1, 1,1,1);	
		this.placerComposants(panFile,
				gbl,
				btnOutputFile,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				1, 1,
				1,1,
				1,50,
				1, 1,1,1);		
	}
	
	private void createPanKeyboardCode(){
		panKeyboardCode = new JPanel();
		
		titlePanKeyboard = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				JobConfig.getRes().getString("panKeyboard.titlePanKeyboard"));
		panKeyboardCode.setBorder(titlePanKeyboard);
		
		GridBagLayout gbl = new GridBagLayout();
		
		chkAutorisation1 = new JCheckBox("A1");
		chkAutorisation1.addActionListener(controler);
		chkAutorisation2 = new JCheckBox("A2");
		chkAutorisation2.addActionListener(controler);
		chkAutorisation3 = new JCheckBox("A3");
		chkAutorisation3.addActionListener(controler);
		chkAutorisation4 = new JCheckBox("A4");
		chkAutorisation4.addActionListener(controler);
		chkAutorisation5 = new JCheckBox("A5");
		chkAutorisation5.addActionListener(controler);
		chkAutorisation6 = new JCheckBox("A6");
		chkAutorisation6.addActionListener(controler);
		
		chkAutorisation1.setSelected(true);
		chkAutorisation2.setSelected(true);
		chkAutorisation3.setSelected(true);
		chkAutorisation4.setSelected(true);
		chkAutorisation5.setSelected(true);
		chkAutorisation6.setSelected(true);
		
		
		labSerial = new JLabel(JobConfig.getRes().getString("panKeyboard.labSerial"));		
		labCode = new JLabel(JobConfig.getRes().getString("panKeyboard.labCode"));
		
		MaskFormatter mask;
		
		try {
			mask = new MaskFormatter("########");		
			mask.setPlaceholderCharacter('0');
			txtSerial = new JFormattedTextField(mask);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		txtSerial.setColumns(7);
		txtSerial.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);	
		txtSerial.setHorizontalAlignment(JTextField.RIGHT);
		
	
			
		txtSerial.getDocument().addDocumentListener(controler);		
		txtCode = new JTextField(7);
		txtCode.setEditable(false);
		txtCode.setHorizontalAlignment(JTextField.RIGHT);
		
		
		this.placerComposants(panKeyboardCode,
				gbl,
				chkAutorisation1,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 0,
				1,1,
				5,25,
				1, 1,1,1);
		this.placerComposants(panKeyboardCode,
				gbl,
				chkAutorisation2,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				1, 0,
				1,1,
				5,25,
				1, 1,1,1);
		this.placerComposants(panKeyboardCode,
				gbl,
				chkAutorisation3,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				2, 0,
				1,1,
				5,25,
				1, 1,1,1);
		this.placerComposants(panKeyboardCode,
				gbl,
				chkAutorisation4,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				3, 0,
				1,1,
				5,25,
				1, 1,1,1);
		this.placerComposants(panKeyboardCode,
				gbl,
				chkAutorisation5,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				4, 0,
				1,1,
				5,25,
				1, 1,1,1);
		this.placerComposants(panKeyboardCode,
				gbl,
				chkAutorisation6,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				5, 0,
				1,1,
				5,25,
				1, 1,1,1);
		this.placerComposants(panKeyboardCode,
				gbl,
				labSerial,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				6, 0,
				1,1,
				5,25,
				1, 1,1,1);
		this.placerComposants(panKeyboardCode,
				gbl,
				txtSerial,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				7, 0,
				1,1,
				5,25,
				1, 1,1,1);
		this.placerComposants(panKeyboardCode,
				gbl,
				labCode,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				8, 0,
				1,1,
				5,25,
				1, 1,1,1);
		this.placerComposants(panKeyboardCode,
				gbl,
				txtCode,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				9, 0,
				1,1,
				5,25,
				1, 1,1,1);
		
		
	}
	
	private void createColorMode(){
		panColorMode = new JPanel();		
		
		titleColorMode = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				JobConfig.getRes().getString("panColorMode.title"));
		panColorMode.setBorder(titleColorMode);
		
		
		String[] tab = {JobConfig.getRes().getString("cbColorMode.rgb"),
				JobConfig.getRes().getString("cbColorMode.pal"),
				JobConfig.getRes().getString("cbColorMode.secam")};
		cbColorMode = new JComboBox<String>(tab);
		cbColorMode.addActionListener(controler);
		
		lblColorMode = new JLabel(JobConfig.getRes().getString("lblColorMode.text"));
	
		cbAveragePal = new JCheckBox(JobConfig.getRes().getString("cbAveragePal.text"));
		cbAveragePal.setSelected(true);
		JobConfig.setAveragingPal(true);
		cbAveragePal.addActionListener(controler);
		
		lblYUV = new JLabel(JobConfig.getRes().getString("lblYUV.title"));
		String[] tabYUV = {"bt.601", "bt.709", JobConfig.getRes().getString("cbYUV.special")};
		cbYUV = new JComboBox<String>(tabYUV);
		cbYUV.addActionListener(controler);
		
	
		GridBagLayout gblColorMode = new GridBagLayout();
		
		this.placerComposants(panColorMode,
				gblColorMode,
				lblColorMode,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 0,
				1,1,
				0,20,
				1, 1,1,1);
		this.placerComposants(panColorMode,
				gblColorMode,
				cbColorMode,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				1, 0,
				1,1,
				0,20,
				1, 1,1,1);
		this.placerComposants(panColorMode,
				gblColorMode,
				cbAveragePal,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				2, 0,
				1,1,
				10,20,
				1, 1,1,1);
		this.placerComposants(panColorMode,
				gblColorMode,
				lblYUV,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
			    3, 0,
				1,1,
				0,20,
				1, 1,1,1);
		this.placerComposants(panColorMode,
				gblColorMode,
				cbYUV,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				4, 0,
				1,1,
				33,20,
				1, 1,1,1);
		
		
		
		
	}
	
	private void createPanVideocrypt(){
		panVideocryptOptions = new JPanel();
		
		titlePanVideocrypt = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				JobConfig.getRes().getString("panVideocrypt.titlePanVideocrypt"));
		panVideocryptOptions.setBorder(titlePanVideocrypt);
		
		rdiVideocryptCoding = new JRadioButton(JobConfig.getRes().getString("panVideocrypt.rdiVideocryptCoding"));
		rdiVideocryptCoding.addActionListener(controler);
		
		rdiVideocryptDecoding = new JRadioButton(JobConfig.getRes().getString("panVideocrypt.rdiVideocryptDecoding"));
		rdiVideocryptDecoding.addActionListener(controler);		
		
		rdiVideocryptCodingAuto = new JRadioButton(JobConfig.getRes().getString("panSyster.rdiSysterCodingRandom"));
		rdiVideocryptCodingAuto.addActionListener(controler);
		
		rdiVideocryptCodingFile = new JRadioButton(JobConfig.getRes().getString("panSyster.rdiSysterCodingFile"));
		rdiVideocryptCodingFile.addActionListener(controler);
			
		rdiVideocryptDecodingFile = new JRadioButton(JobConfig.getRes().getString("panSyster.rdiSysterDecodingFile"));
		rdiVideocryptDecodingFile.addActionListener(controler);
		
		rdiVideocryptCorrel = new JRadioButton(JobConfig.getRes().getString("panVideocrypt.rdiVideocryptCorrel"));
		rdiVideocryptCorrel.addActionListener(controler);
		
		rdiVideocryptTagsDecoding = new JRadioButton(JobConfig.getRes().getString("panVideocrypt.rdiTagsDecoding"));
		rdiVideocryptTagsDecoding.addActionListener(controler);
		
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(rdiVideocryptCoding);
		buttonGroup.add(rdiVideocryptDecoding);
		rdiVideocryptCoding.setSelected(true);
		
		ButtonGroup buttonGroup2 = new ButtonGroup();
		buttonGroup2.add(rdiVideocryptCodingAuto);
		buttonGroup2.add(rdiVideocryptCodingFile);		
		rdiVideocryptCodingAuto.setSelected(true);
		
		ButtonGroup buttonGroup3 = new ButtonGroup();
		buttonGroup3.add(rdiVideocryptCorrel);
		buttonGroup3.add(rdiVideocryptDecodingFile);
		buttonGroup3.add(rdiVideocryptTagsDecoding);
		rdiVideocryptDecodingFile.setSelected(true);
		
		JPanel panRdi = new JPanel();
		panRdi.add(rdiVideocryptCoding);
		panRdi.add(rdiVideocryptDecoding);
		
		
		JPanel panVideocryptCodingGen = new JPanel();
		titlePanVideocryptCodingGen = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				JobConfig.getRes().getString("panSyster.titlePanSysterCodingGen"));
		
		
		panVideocryptCodingGen.setBorder(titlePanVideocryptCodingGen);
		
		JPanel panVideocryptDecodingGen = new JPanel();
		titlePanVideocryptDecodingGen = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				JobConfig.getRes().getString("panSyster.titlePanSysterDecodingGen"));
		
		
		panVideocryptDecodingGen.setBorder(titlePanVideocryptDecodingGen);
		
		//pan cardlayout
		panVideocryptEncDecCard = new JPanel();
		
		cardEncDecVideocrypt = new CardLayout();
		panVideocryptEncDecCard.setLayout(cardEncDecVideocrypt);
		panVideocryptEncDecCard.add(panVideocryptCodingGen,"VideocryptCoding");
		panVideocryptEncDecCard.add(panVideocryptDecodingGen,"VideocryptDecoding");
		
		txtVideocryptDec = new JTextField(80);
		txtVideocryptDec.setEditable(false);		
		btnVideocryptDec = new JButton(JobConfig.getRes().getString("panVideocrypt.btnDec"));
		btnVideocryptDec.setIcon(new ImageIcon(this.getClass().getResource("/icons/filenew.png")));
		btnVideocryptDec.addActionListener(controler);
		
		txtVideocryptEnc = new JTextField(80);
		txtVideocryptEnc.setEditable(false);		
		btnVideocryptEnc = new JButton(JobConfig.getRes().getString("panVideocrypt.btnDec"));
		btnVideocryptEnc.setIcon(new ImageIcon(this.getClass().getResource("/icons/filenew.png")));
		btnVideocryptEnc.addActionListener(controler);
		
		chkRestrictRange = new JCheckBox(JobConfig.getRes().getString("panVideocrypt.chkRestrictRange"));
		chkRestrictRange.addActionListener(controler);
		chkRestrictRange.setSelected(true);
		
		chkLogVideocrypt = new JCheckBox(JobConfig.getRes().getString("panVideocrypt.chkLogVideocrypt"));
		chkLogVideocrypt.addActionListener(controler);
		chkLogVideocrypt.setSelected(false);
		
		chkVideocryptTags = new JCheckBox(JobConfig.getRes().getString("panVideocrypt.chkTags"));
		chkVideocryptTags.addActionListener(controler);
		chkVideocryptTags.setSelected(false);
		
		//pan coding options
		GridBagLayout gblCodingGen = new GridBagLayout();
		
		this.placerComposants(panVideocryptCodingGen,
				gblCodingGen,
				rdiVideocryptCodingAuto,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 0,
				1,1,
				0,33,
				1, 1,1,1);
		this.placerComposants(panVideocryptCodingGen,
				gblCodingGen,
				rdiVideocryptCodingFile,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,				
				0, 1,
				1,1,
				0,33,
				1, 1,1,1);
		this.placerComposants(panVideocryptCodingGen,
				gblCodingGen,
				txtVideocryptEnc,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				1, 1,
				2,1,
				70,33,
				1, 1,1,1);
		this.placerComposants(panVideocryptCodingGen,
				gblCodingGen,
				btnVideocryptEnc,
				GridBagConstraints.LINE_START, GridBagConstraints.EAST,
				3, 1,
				1,1,
				1,33,
				1, 1,1,1);
		this.placerComposants(panVideocryptCodingGen,
				gblCodingGen,
				chkRestrictRange,
				GridBagConstraints.LINE_START, GridBagConstraints.EAST,
				0, 2,
				1,1,
				1,33,
				1, 1,1,1);
		this.placerComposants(panVideocryptCodingGen,
				gblCodingGen,
				chkVideocryptTags,
				GridBagConstraints.LINE_START, GridBagConstraints.EAST,
				1, 2,
				1,1,
				1,33,
				1, 1,1,1);
		
		//pan decoding options
				GridBagLayout gblDecodingGen = new GridBagLayout();
				
				this.placerComposants(panVideocryptDecodingGen,
						gblDecodingGen,
						rdiVideocryptDecodingFile,
						GridBagConstraints.LINE_START, GridBagConstraints.NONE,
						0, 0,
						1,1,
						0,33,
						1, 1,1,1);				
				this.placerComposants(panVideocryptDecodingGen,
						gblDecodingGen,
						txtVideocryptDec,
						GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
						1, 0,
						2,1,
						99,33,
						1, 1,1,1);
				this.placerComposants(panVideocryptDecodingGen,
						gblDecodingGen,
						btnVideocryptDec,
						GridBagConstraints.LINE_START, GridBagConstraints.EAST,
						3, 0,
						1,1,
						1,33,
						1, 1,1,1);
				this.placerComposants(panVideocryptDecodingGen,
						gblDecodingGen,
						rdiVideocryptTagsDecoding,
						GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,				
						0, 1,
						1,1,
						0,33,
						1, 1,1,1);
				this.placerComposants(panVideocryptDecodingGen,
						gblDecodingGen,
						rdiVideocryptCorrel,
						GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,				
						0, 2,
						1,1,
						0,33,
						1, 1,1,1);
		
		
		
		chkSoundVideocrypt = new JCheckBox(JobConfig.getRes().getString("panVideocrypt.chkSoundVideocrypt"));
		chkSoundVideocrypt.setSelected(false);
		chkSoundVideocrypt.setToolTipText(JobConfig.getRes().getString("panSyster.chkSoundSyster.tooltip"));
		chkSoundVideocrypt.addActionListener(controler);
		
		chkDisableSoundVideocrypt = new JCheckBox(JobConfig.getRes().getString("panVideocrypt.chkDisableSoundVideocrypt"));		
		chkDisableSoundVideocrypt.setSelected(false);
		chkDisableSoundVideocrypt.setToolTipText(JobConfig.getRes().getString("panSyster.chkDisableSoundSyster.tooltip"));
		chkDisableSoundVideocrypt.addActionListener(controler);
				
						
		JPanel panSound = new JPanel();	
		panSound.add(chkSoundVideocrypt);
		panSound.add(chkDisableSoundVideocrypt);
		
		labFrameStartVideocrypt = new JLabel(JobConfig.getRes().getString("panVideocrypt.lblFrameStart"));
		slideFrameStartVideocrypt = new JSlider(JSlider.HORIZONTAL,1,200000,1);
		slideFrameStartVideocrypt.setToolTipText(JobConfig.getRes().getString("panSyster.labFrameStartSyster.tooltip"));
		jspFrameStartVideocrypt = new JSpinner();	
		jspFrameStartVideocrypt.addChangeListener(controler);		
		JSpinner.NumberEditor spinnerEditor3 = new JSpinner.NumberEditor(jspFrameStartVideocrypt);
		jspFrameStartVideocrypt.setEditor(spinnerEditor3);
		JComponent editor2 = jspFrameStartVideocrypt.getEditor();
		JFormattedTextField tf2 = ((JSpinner.DefaultEditor) editor2).getTextField();
		tf2.setColumns(5);
		tf2.setEditable(false);
		spinnerEditor3.getModel().setMinimum(1);
		spinnerEditor3.getModel().setMaximum(200000);
		spinnerEditor3.getModel().setStepSize(1);
		spinnerEditor3.getModel().setValue(1);
		
		slideFrameStartVideocrypt.addChangeListener(controler);			
		slideFrameStartVideocrypt.setValue(1);
		slideFrameStartVideocrypt.setMajorTickSpacing(50000);
		slideFrameStartVideocrypt.setMinorTickSpacing(10000);
		Hashtable<Integer, JLabel> labelTable4 = new Hashtable<Integer, JLabel>();
		labelTable4.put( new Integer( 1 ), new JLabel("1"));		
		labelTable4.put( new Integer( 200000 ), new JLabel("200000"));
		slideFrameStartVideocrypt.setLabelTable(labelTable4);
		slideFrameStartVideocrypt.setPaintLabels(true);		
		slideFrameStartVideocrypt.setPaintTicks(true);
		
		JPanel panSlide = new JPanel();
		GridBagLayout gblSlide = new GridBagLayout();
		
		this.placerComposants(panSlide,
				gblSlide,
				labFrameStartVideocrypt,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 0,
				1,1,
				10,33,
				1, 1,1,1);
		this.placerComposants(panSlide,
				gblSlide,
				slideFrameStartVideocrypt,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				1, 0,
				1,1,
				70,33,
				1, 1,1,1);
		this.placerComposants(panSlide,
				gblSlide,
				jspFrameStartVideocrypt,
				GridBagConstraints.LINE_START, GridBagConstraints.EAST,
				2, 0,
				1,1,
				20,33,
				1, 1,1,1);
		
		
		JPanel panLog = new JPanel();
		panLog.add(chkLogVideocrypt);
		
		GridBagLayout gblVideocrypt = new GridBagLayout();
		
		this.placerComposants(panVideocryptOptions,
				gblVideocrypt,
				panRdi,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 0,
				3,1,
				33,20,
				1, 1,1,1);
		this.placerComposants(panVideocryptOptions,
				gblVideocrypt,
				panVideocryptEncDecCard,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				0, 1,
				1,1,
				100,20,
				1, 1,1,1);
		this.placerComposants(panVideocryptOptions,
				gblVideocrypt,
				panLog,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 2,
				3,1,
				0,20,
				1, 1,1,1);
		this.placerComposants(panVideocryptOptions,
				gblVideocrypt,
				panSound,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 3,
				3,1,
				0,20,
				1, 1,1,1);
		this.placerComposants(panVideocryptOptions,
				gblVideocrypt,
				panSlide,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				0, 4,
				3,1,
				0,20,
				1, 1,1,1);		
		
		rdiVideocryptCoding.setSelected(true);			
		rdiVideocryptCodingAuto.setSelected(true);
		txtVideocryptEnc.setEnabled(false);
		btnVideocryptEnc.setEnabled(false);	
		
	}
	
	private void createPanSyster(){	
		
		panOptionsSyster = new JPanel();
		//panOptionsSyster.setLayout(new GridLayout(3, 1));
		
		titlePanSyster = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				JobConfig.getRes().getString("panSyster.titlePanSyster"));
		panOptionsSyster.setBorder(titlePanSyster);		
				
		panCoderSyster = new JPanel();
		
		titlePanSysterCodingGen = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				JobConfig.getRes().getString("panSyster.titlePanSysterCodingGen"));
		panCoderSyster.setBorder(titlePanSysterCodingGen);
		//panCoderSyster.setLayout(new BoxLayout(panCoderSyster, BoxLayout.Y_AXIS));
		
		panDecoderSyster = new JPanel();
		
		titlePanSysterDecodingGen = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				JobConfig.getRes().getString("panSyster.titlePanSysterDecodingGen"));
		panDecoderSyster.setBorder(titlePanSysterDecodingGen);
		panDecoderSyster.setLayout(new BoxLayout(panDecoderSyster, BoxLayout.Y_AXIS));
		
		rdiSysterCodingGen = new JRadioButton(JobConfig.getRes().getString("panSyster.rdiSysterCodingGen"));
		rdiSysterCodingGen.addActionListener(controler);
		rdiSysterDecodingGen = new JRadioButton(JobConfig.getRes().getString("panSyster.rdiSysterDecodingGen"));
		rdiSysterDecodingGen.addActionListener(controler);
		
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(rdiSysterCodingGen);
		buttonGroup.add(rdiSysterDecodingGen);
		
		
		
		btnFileSysterEnc = new JButton(JobConfig.getRes().getString("panSyster.btnFileSysterEnc"));
		btnFileSysterEnc.setIcon(new ImageIcon(this.getClass().getResource("/icons/filenew.png")));
		btnFileSysterEnc.addActionListener(controler);
		btnFileSysterDec = new JButton(JobConfig.getRes().getString("panSyster.btnFileSysterDec"));
		btnFileSysterDec.setIcon(new ImageIcon(this.getClass().getResource("/icons/filenew.png")));
		btnFileSysterDec.addActionListener(controler);
		
		//sub rdi coding
		rdiSysterCodingRandom = new JRadioButton(JobConfig.getRes().getString("panSyster.rdiSysterCodingRandom"));
		rdiSysterCodingRandom.addActionListener(controler);
		rdiSysterCodingFile = new JRadioButton(JobConfig.getRes().getString("panSyster.rdiSysterCodingFile"));
		rdiSysterCodingFile.addActionListener(controler);
		chkTags = new JCheckBox(JobConfig.getRes().getString("panSyster.chkTags"));
		chkTags.addActionListener(controler);
		
		ButtonGroup btnSubOptionsCoding = new ButtonGroup();
		btnSubOptionsCoding.add(rdiSysterCodingRandom);
		btnSubOptionsCoding.add(rdiSysterCodingFile);		
				
		String[] tab = {JobConfig.getRes().getString("panSyster.comboTableSysterEnc.menu1"),JobConfig.getRes().getString("panSyster.comboTableSysterEnc.menu2")};
		comboTableSysterEnc = new JComboBox<String>(tab);
		comboTableSysterEnc.setSelectedIndex(0);
		
		
		JPanel subRdiCoding = new JPanel();
				
		txtSysterEnc = new JTextField(80);
		txtSysterEnc.setEditable(false);		
		txtSysterDec = new JTextField(80);
		txtSysterDec.setEditable(false);
		
		txtSysterEnc.setEnabled(false);
		btnFileSysterEnc.setEnabled(false);
				
		chkChangeOffsetIncrement = new JCheckBox(JobConfig.getRes().getString("panSyster.chkOffsetIncrement"));
		chkChangeOffsetIncrement.addActionListener(controler);
		chkChangeOffsetIncrement.setSelected(false);
		
		GridBagLayout gblRdiCoding = new GridBagLayout();
		
		this.placerComposants(subRdiCoding,
				gblRdiCoding,
				rdiSysterCodingRandom,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 0,
				1,1,
				0,20,
				1, 1,1,1);
		this.placerComposants(subRdiCoding,
				gblRdiCoding,
				comboTableSysterEnc,
				GridBagConstraints.LINE_START, GridBagConstraints.WEST,
				1, 0,
				1,1,
				10,20,
				1, 1,1,1);
		this.placerComposants(subRdiCoding,
				gblRdiCoding,
				rdiSysterCodingFile,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 2,
				1,1,
				0,20,
				1, 1,1,1);
		this.placerComposants(subRdiCoding,
				gblRdiCoding,
				txtSysterEnc,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				1, 2,
				1,1,
				99,20,
				1, 1,1,1);
		this.placerComposants(subRdiCoding,
				gblRdiCoding,
				btnFileSysterEnc,
				GridBagConstraints.LINE_START, GridBagConstraints.EAST,
				2, 2,
				1,1,
				1,20,
				1, 1,1,1);
		
		JPanel miscOptionsCoding = new JPanel();
		GridBagLayout gblCoderMiscOptions = new GridBagLayout();

		this.placerComposants(miscOptionsCoding,
				gblCoderMiscOptions,
				chkTags,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 0,
				1,1,
				0,25,
				1, 1,1,1);		
		this.placerComposants(miscOptionsCoding,
				gblCoderMiscOptions,
				chkChangeOffsetIncrement,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				1, 0,
				1,1,
				0,25,
				1, 1,1,1);
				
		
		GridBagLayout gblCoderSyster = new GridBagLayout();
		this.placerComposants(panCoderSyster,
				gblCoderSyster,
				subRdiCoding,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				0, 0,
				1,1,
				100,25,
				1, 1,1,1);		
		this.placerComposants(panCoderSyster,
				gblCoderSyster,
				miscOptionsCoding,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 1,
				1,1,
				100,25,
				1, 1,1,1);
		
		
		//decoding
		rdiSysterDecodingFile = new JRadioButton(JobConfig.getRes().getString("panSyster.rdiSysterDecodingFile"));
		rdiSysterDecodingFile.addActionListener(controler);
		rdiSysterDeCodingTags = new JRadioButton(JobConfig.getRes().getString("panSyster.rdiSysterDeCodingTags"));
		rdiSysterDeCodingTags.addActionListener(controler);
		rdiSysterDecodingCorrel = new JRadioButton(JobConfig.getRes().getString("panSyster.rdiSysterDecodingCorrel"));
		rdiSysterDecodingCorrel.addActionListener(controler);
		
		ButtonGroup btnSubOptionsDecoding = new ButtonGroup();
		btnSubOptionsDecoding.add(rdiSysterDecodingFile);
		btnSubOptionsDecoding.add(rdiSysterDeCodingTags);
		btnSubOptionsDecoding.add(rdiSysterDecodingCorrel);
		
		JPanel subRdiDecoding = new JPanel();
	
		comboTableSysterDec = new JComboBox<String>(tab);
		comboTableSysterDec.setSelectedIndex(0);
		comboTableSysterDec.setEnabled(false);
		
		GridBagLayout gblRdiDecoding = new GridBagLayout();
		
		this.placerComposants(subRdiDecoding,
				gblRdiDecoding,
				rdiSysterDecodingFile,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 0,
				1,1,
				0,33,
				1, 1,1,1);
		this.placerComposants(subRdiDecoding,
				gblRdiDecoding,
				txtSysterDec,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				1, 0,
				2,1,
				99,33,
				1, 1,1,1);
		this.placerComposants(subRdiDecoding,
				gblRdiDecoding,
				btnFileSysterDec,
				GridBagConstraints.LINE_START, GridBagConstraints.EAST,
				3, 0,
				1,1,
				1,33,
				1, 1,1,1);
		this.placerComposants(subRdiDecoding,
				gblRdiDecoding,
				rdiSysterDeCodingTags,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 1,
				3,1,
				100,33,
				1, 1,1,1);
		this.placerComposants(subRdiDecoding,
				gblRdiDecoding,
				rdiSysterDecodingCorrel,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 2,
				1,1,
				0,33,
				1, 1,1,1);
		this.placerComposants(subRdiDecoding,
				gblRdiDecoding,
				comboTableSysterDec,
				GridBagConstraints.LINE_START, GridBagConstraints.WEST,
				1, 2,
				2,1,
				80,33,
				1, 1,1,1);
				
		panDecoderSyster.add(subRdiDecoding);
		
		//pan mode
		panSysterMode = new JPanel();
		GridBagLayout gblSysterMode = new GridBagLayout();
		this.placerComposants(panSysterMode,
				gblSysterMode,
				rdiSysterCodingGen,
				GridBagConstraints.LINE_START, GridBagConstraints.WEST,
				0, 0,
				1,1,
				0,100,
				1, 1,1,1);
		this.placerComposants(panSysterMode,
				gblSysterMode,
				rdiSysterDecodingGen,
				GridBagConstraints.WEST, GridBagConstraints.WEST,
				1, 0,
				1,1,
				20,100,
				1, 1,1,1);		
	
		
		//pan cardlayout syster enc dec
		panSysterEncDecCard = new JPanel();
		cardEncDecSyster = new CardLayout();
		panSysterEncDecCard.setLayout(cardEncDecSyster);
		panSysterEncDecCard.add(panCoderSyster,"SysterCoding");
		panSysterEncDecCard.add(panDecoderSyster,"SysterDecoding");
		
		//pan syster misc
		labFrameStartSyster = new JLabel(JobConfig.getRes().getString("panSyster.labFrameStartSyster"));
		slideFrameStartSyster = new JSlider(JSlider.HORIZONTAL,1,200000,1);
		slideFrameStartSyster.setToolTipText(JobConfig.getRes().getString("panSyster.labFrameStartSyster.tooltip"));
		jspFrameStartSyster = new JSpinner();	
		jspFrameStartSyster.addChangeListener(controler);		
		JSpinner.NumberEditor spinnerEditor3 = new JSpinner.NumberEditor(jspFrameStartSyster);
		jspFrameStartSyster.setEditor(spinnerEditor3);
		JComponent editor2 = jspFrameStartSyster.getEditor();
		JFormattedTextField tf2 = ((JSpinner.DefaultEditor) editor2).getTextField();
		tf2.setColumns(5);
		tf2.setEditable(false);
		spinnerEditor3.getModel().setMinimum(1);
		spinnerEditor3.getModel().setMaximum(200000);
		spinnerEditor3.getModel().setStepSize(1);
		spinnerEditor3.getModel().setValue(1);
		
		
		slideFrameStartSyster.addChangeListener(controler);			
		slideFrameStartSyster.setValue(1);
		slideFrameStartSyster.setMajorTickSpacing(50000);
		slideFrameStartSyster.setMinorTickSpacing(10000);
		Hashtable<Integer, JLabel> labelTable4 = new Hashtable<Integer, JLabel>();
		labelTable4.put( new Integer( 1 ), new JLabel("1"));		
		labelTable4.put( new Integer( 200000 ), new JLabel("200000"));
		slideFrameStartSyster.setLabelTable(labelTable4);
		slideFrameStartSyster.setPaintLabels(true);		
		slideFrameStartSyster.setPaintTicks(true);
		
		chkSoundSyster = new JCheckBox(JobConfig.getRes().getString("panSyster.chkSoundSyster"));
		chkSoundSyster.setSelected(true);
		chkSoundSyster.setToolTipText(JobConfig.getRes().getString("panSyster.chkSoundSyster.tooltip"));
		
		chkDisableSoundSyster = new JCheckBox(JobConfig.getRes().getString("panSyster.chkDisableSoundSyster"));		
		chkDisableSoundSyster.setSelected(false);
		chkDisableSoundSyster.setToolTipText(JobConfig.getRes().getString("panSyster.chkDisableSoundSyster.tooltip"));
		chkDisableSoundSyster.addActionListener(controler);
						
		panSysterMisc = new JPanel();
		GridBagLayout gblSysterMisc = new GridBagLayout();
		
		JPanel panSound = new JPanel();				
				
		panSound.add(chkSoundSyster);
		panSound.add(chkDisableSoundSyster);
		
		this.placerComposants(panSysterMisc,
				gblSysterMisc,
				panSound,
				GridBagConstraints.LINE_START, GridBagConstraints.WEST,
				0, 0,
				3,1,
				0,30,
				1, 1,1,1);		
		this.placerComposants(panSysterMisc,
				gblSysterMisc,
				labFrameStartSyster,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 1,
				1,1,
				10,33,
				1, 1,1,1);
		this.placerComposants(panSysterMisc,
				gblSysterMisc,
				slideFrameStartSyster,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				1, 1,
				1,1,
				70,33,
				1, 1,1,1);
		this.placerComposants(panSysterMisc,
				gblSysterMisc,
				jspFrameStartSyster,
				GridBagConstraints.LINE_START, GridBagConstraints.EAST,
				2, 1,
				1,1,
				20,33,
				1, 1,1,1);

		GridBagLayout gblOptionsSyster = new GridBagLayout();
		this.placerComposants(panOptionsSyster,
				gblOptionsSyster,
				panSysterMode,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				0, 0,
				1,1,
				100,10,
				1, 1,1,1);
		this.placerComposants(panOptionsSyster,
				gblOptionsSyster,
				panSysterEncDecCard,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				0, 1,
				1,1,
				100,45,
				1, 1,1,1);
		this.placerComposants(panOptionsSyster,
				gblOptionsSyster,
				panSysterMisc,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				0, 2,
				1,1,
				100,45,
				1, 1,1,1);		
		
		rdiSysterCodingGen.setSelected(true);
		rdiSysterCodingRandom.setSelected(true);
		rdiSysterDecodingFile.setSelected(true);
		
	}
	
	private void createPanDiscret11(){
		
		createPanKeyboardCode();
		
		panOptionsDiscret11 = new JPanel();
		
		titlePanDiscret11 = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				JobConfig.getRes().getString("panDiscret11.title"));
		panOptionsDiscret11.setBorder(titlePanDiscret11);
		GridBagLayout gbl = new GridBagLayout();
		
		rdiDiscretCoding = new JRadioButton(JobConfig.getRes().getString("panDiscret11.coding"));
		rdiDiscretCoding.setSelected(true);
		rdiDiscretDecoding = new JRadioButton(JobConfig.getRes().getString("panDiscret11.decoding"));
		rdiDiscretCorrel = new JRadioButton(JobConfig.getRes().getString("panDiscret11.decodingCorrel"));
		
		rdiDiscretCoding.addActionListener(controler);
		rdiDiscretDecoding.addActionListener(controler);
		rdiDiscretCorrel.addActionListener(controler);
		
		ButtonGroup btnGroup = new ButtonGroup();
		btnGroup.add(rdiDiscretCoding);
		btnGroup.add(rdiDiscretDecoding);
		btnGroup.add(rdiDiscretCorrel);
		
		//init 16 bits word slider
		JPanel pan16bits = new JPanel();
		GridBagLayout gbl_pan16bits = new GridBagLayout();
		
		lab16bitsWord = new JLabel(JobConfig.getRes().getString("panDiscret11.lab16bits"));
		slid16bitsWord = new JSlider(JSlider.HORIZONTAL,32,65535,58158);
		slid16bitsWord.addChangeListener(controler);			
		
		
		slid16bitsWord.setValue(58158);
		slid16bitsWord.setMajorTickSpacing(10000);
		slid16bitsWord.setMinorTickSpacing(5000);
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put( new Integer( 32 ), new JLabel("32"));
		labelTable.put( new Integer( 65535 ), new JLabel("65535"));
		labelTable.put( new Integer( 32768 ), new JLabel("32768"));

		slid16bitsWord.setLabelTable(labelTable);
		slid16bitsWord.setPaintLabels(true);
		
		
		slid16bitsWord.setPaintTicks(true);	
	
			
		MaskFormatter mask;
		
		try {
			mask = new MaskFormatter("#####");		
			mask.setPlaceholderCharacter('0');
			txt16bitsWord = new JFormattedTextField(mask);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		txt16bitsWord.setText(String.valueOf(slid16bitsWord.getValue()));
		
		txt16bitsWord.setEditable(true);		
				
		txt16bitsWord.setColumns(10);
		
		jsp16bitKeyword = new JSpinner();
		
		
		JSpinner.NumberEditor spinnerEditor = new JSpinner.NumberEditor(jsp16bitKeyword);
		jsp16bitKeyword.setEditor(spinnerEditor);
		
		JComponent editor1 = jsp16bitKeyword.getEditor();
		JFormattedTextField tf = ((JSpinner.DefaultEditor) editor1).getTextField();
		tf.setColumns(5);
		tf.setEditable(false);
		
		spinnerEditor.getModel().setMinimum(32);
		spinnerEditor.getModel().setMaximum(65536);
		spinnerEditor.getModel().setStepSize(1);
		spinnerEditor.getModel().setValue(58158);	
		  
		    this.placerComposants(pan16bits,
					gbl_pan16bits,
					lab16bitsWord,
					GridBagConstraints.LINE_START, GridBagConstraints.NONE,
					0, 0,
					1,1,
					10,33,
					1, 1,1,1);
		    this.placerComposants(pan16bits,
					gbl_pan16bits,
					slid16bitsWord,
					GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
					1, 0,
					1,1,
					70,33,
					1, 1,1,1);
		    this.placerComposants(pan16bits,
					gbl_pan16bits,
					jsp16bitKeyword,
					GridBagConstraints.LINE_START, GridBagConstraints.EAST,
					2, 0,
					1,1,
					20,33,
					1, 1,1,1);
		
		//init audience
		JPanel panAudience = new JPanel();				
		
		labAudience = new JLabel(JobConfig.getRes().getString("panDiscret11.labAudience"));		
		String[] tab = {JobConfig.getRes().getString("panDiscret11.AudienceLevel1"),JobConfig.getRes().getString("panDiscret11.AudienceLevel2"),JobConfig.getRes().getString("panDiscret11.AudienceLevel3"),JobConfig.getRes().getString("panDiscret11.AudienceLevel4"),
				JobConfig.getRes().getString("panDiscret11.AudienceLevel5"),JobConfig.getRes().getString("panDiscret11.AudienceLevel6"),JobConfig.getRes().getString("panDiscret11.AudienceLevel7"),JobConfig.getRes().getString("panDiscret11.AudienceLevelMulti")};
		combAudience = new JComboBox<String>(tab);
		combAudience.setSelectedIndex(0);
		combAudience.addActionListener(controler);
		
		lbl11bitsInfo = new JLabel();
		lbl11bitsInfo.setFont(new Font("Serif", Font.PLAIN, 11));
		lbl11bitsInfo.setToolTipText(JobConfig.getRes().getString("panDiscret11.lab11bits.tooltip"));
		
		//multimode components		
		
		MaskFormatter maskMultiMode;
		
		try {
			maskMultiMode = new MaskFormatter("##########");
			maskMultiMode.setPlaceholderCharacter('#');
			maskMultiMode.setValidCharacters("1234567");
			
			txtMultiCode = new JFormattedTextField(maskMultiMode);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		txtMultiCode.setColumns(10);
		txtMultiCode.setText("6425");
		txtMultiCode.getDocument().addDocumentListener(new MultiCode_DocumentListener(this));
				
		txtMultiCode.setFocusLostBehavior(JFormattedTextField.COMMIT);	
				
		txtMultiCode.setToolTipText(JobConfig.getRes().getString("panDiscret11.txtMultiCode.tooltip"));
		
		lblMultiAudience = new JLabel(JobConfig.getRes().getString("panDiscret11.labMultiAudience"));
		lblCycle = new JLabel(JobConfig.getRes().getString("panDiscret11.labCycle"));
				
		jspCycle = new JSpinner();
		
		jspCycle.setToolTipText(JobConfig.getRes().getString("panDiscret11.jspCycle.tooltip"));
		
		JSpinner.NumberEditor spinnerEditorMultiMode = new JSpinner.NumberEditor(jspCycle);
		jspCycle.setEditor(spinnerEditorMultiMode);
		
		JComponent editorMultiMode = jspCycle.getEditor();
		JFormattedTextField tfMultiMode = ((JSpinner.DefaultEditor) editorMultiMode).getTextField();
		tfMultiMode.setColumns(2);
		tfMultiMode.setEditable(false);
		
		spinnerEditorMultiMode.getModel().setMinimum(1);
		spinnerEditorMultiMode.getModel().setMaximum(99);
		spinnerEditorMultiMode.getModel().setStepSize(1);
		
		jspCycle.setValue(1);
		
	
		panAudience.add(labAudience);
		panAudience.add(combAudience);		  
	    	    
	    panAudience.add(txtMultiCode);
	    panAudience.add(lblCycle);
	    panAudience.add(jspCycle);
	    panAudience.add(lbl11bitsInfo);	    

		  
		chkDelay = new JCheckBox(JobConfig.getRes().getString("panDiscret11.chkDelay"));
		chkDelay.setSelected(true);
		chkDelay.addActionListener(controler);
		
		labDelay1 = new JLabel(JobConfig.getRes().getString("panDiscret11.labDelay1"));
		txtDelay1 = new JTextField(5);
		labDelay2 = new JLabel(JobConfig.getRes().getString("panDiscret11.labDelay2"));
		txtDelay2 = new JTextField(5);
		txtDelay1.setEditable(false);
		txtDelay2.setEditable(false);
		slidDelay1 = new JSlider(JSlider.HORIZONTAL,500,3000,1000);
		slidDelay1.addChangeListener(controler);	
		slidDelay2 = new JSlider(JSlider.HORIZONTAL,2000,5000,3000);
		slidDelay2.addChangeListener(controler);
		Hashtable<Integer, JLabel> labelTableDelay1 = new Hashtable<Integer, JLabel>();
		labelTableDelay1.put( new Integer( 500 ), new JLabel("0.5") );		
		labelTableDelay1.put( new Integer( 1750 ), new JLabel("1.75") );
		labelTableDelay1.put( new Integer( 3000 ), new JLabel("3") );
		Hashtable<Integer, JLabel> labelTableDelay2 = new Hashtable<Integer, JLabel>();
		labelTableDelay2.put( new Integer( 2000 ), new JLabel("2") );		
		labelTableDelay2.put( new Integer( 3500 ), new JLabel("3.5") );
		labelTableDelay2.put( new Integer( 5000 ), new JLabel("5") );
		slidDelay1.setLabelTable(labelTableDelay1);
		slidDelay1.setValue(1670);
		slidDelay1.setMajorTickSpacing(500);
		slidDelay1.setMinorTickSpacing(100);
		slidDelay1.setPaintLabels(true);
		slidDelay1.setPaintTicks(true);
		slidDelay2.setLabelTable(labelTableDelay2);
		slidDelay2.setValue(3340);
		slidDelay2.setMajorTickSpacing(500);
		slidDelay2.setMinorTickSpacing(100);
		slidDelay2.setPaintLabels(true);
		slidDelay2.setPaintTicks(true);
		txtDelay1.setText(String.valueOf(slidDelay1.getValue()/1000d) +"%");
		txtDelay2.setText(String.valueOf(slidDelay2.getValue()/1000d) + "%");
		
		//init delay1	    
	    JPanel panDelay1 = new JPanel();
	    JPanel panDelay2 = new JPanel();
	    GridBagLayout gbl_delay1 = new GridBagLayout();
	    GridBagLayout gbl_delay2 = new GridBagLayout();
	    
	    this.placerComposants(panDelay1,
				gbl_delay1,
				labDelay1,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 0,
				1,1,
				10,33,
				1, 1,1,15);
	    this.placerComposants(panDelay1,
				gbl_delay1,
				slidDelay1,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				1, 0,
				1,1,
				70,33,
				1, 1,1,15);
	    this.placerComposants(panDelay1,
				gbl_delay1,
				txtDelay1,
				GridBagConstraints.LINE_START, GridBagConstraints.EAST,
				2, 0,
				1,1,
				20,33,
				1, 1,1,15);
	    
	  //init delay2
	    this.placerComposants(panDelay2,
				gbl_delay2,
				labDelay2,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 0,
				1,1,
				10,33,
				1, 1,1,15);
	    this.placerComposants(panDelay2,
				gbl_delay2,
				slidDelay2,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				1, 0,
				1,1,
				70,33,
				1, 1,1,15);
	    this.placerComposants(panDelay2,
				gbl_delay2,
				txtDelay2,
				GridBagConstraints.LINE_START, GridBagConstraints.EAST,
				2, 0,
				1,1,
				20,33,
				1, 1,1,15);
		
		
		//frame start    
		labFrameStart = new JLabel(JobConfig.getRes().getString("panDiscret11.labFrameStart"));
		slideFrameStart = new JSlider(JSlider.HORIZONTAL,1,200000,1);
		slideFrameStart.setToolTipText(JobConfig.getRes().getString("panDiscret11.slideFrameStart.tooltip"));
		jspFrameStart = new JSpinner();	
		jspFrameStart.addChangeListener(controler);		
		JSpinner.NumberEditor spinnerEditor2 = new JSpinner.NumberEditor(jspFrameStart);
		jspFrameStart.setEditor(spinnerEditor2);
		JComponent editor = jspFrameStart.getEditor();
		JFormattedTextField tf2 = ((JSpinner.DefaultEditor) editor).getTextField();
		tf2.setColumns(5);
		tf2.setEditable(false);
		spinnerEditor2.getModel().setMinimum(1);
		spinnerEditor2.getModel().setMaximum(200000);
		spinnerEditor2.getModel().setStepSize(1);
		spinnerEditor2.getModel().setValue(1);
		
		
		slideFrameStart.addChangeListener(controler);			
		slideFrameStart.setValue(1);
		slideFrameStart.setMajorTickSpacing(50000);
		slideFrameStart.setMinorTickSpacing(10000);
		Hashtable<Integer, JLabel> labelTable3 = new Hashtable<Integer, JLabel>();
		labelTable3.put( new Integer( 1 ), new JLabel("1"));		
		labelTable3.put( new Integer( 200000 ), new JLabel("200000"));
		slideFrameStart.setLabelTable(labelTable3);
		slideFrameStart.setPaintLabels(true);		
		slideFrameStart.setPaintTicks(true);
		
		JPanel panFrameStart = new JPanel();
		GridBagLayout gblFrameStart = new GridBagLayout();
		
		this.placerComposants(panFrameStart,
				gblFrameStart,
				labFrameStart,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 0,
				1,1,
				10,25,
				1, 1,1,1);
		this.placerComposants(panFrameStart,
				gblFrameStart,
				slideFrameStart,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				1, 0,
				1,1,
				70,25,
				1, 1,1,1);
		this.placerComposants(panFrameStart,
				gblFrameStart,
				jspFrameStart,
				GridBagConstraints.LINE_START, GridBagConstraints.EAST,
				2, 0,
				1,1,
				20,25,
				1, 1,1,1);
		
		chkNoBlackBar = new JCheckBox(JobConfig.getRes().getString("panDiscret11.chkNoBlackBar"));
		chkNoBlackBar.addActionListener(controler);
		chkNoBlackBar.setToolTipText(JobConfig.getRes().getString("panDiscret11.chkNoBlackBar.tooltip"));
		chkSound = new JCheckBox(JobConfig.getRes().getString("panDiscret11.chkSound"));
		chkSound.setSelected(true);
		chkSound.setToolTipText(JobConfig.getRes().getString("panDiscret11.chkSound.tooltip"));
		
		chkDisableSound = new JCheckBox(JobConfig.getRes().getString("panDiscret11.chkDisableSound"));		
		chkDisableSound.setSelected(false);
		chkDisableSound.setToolTipText(JobConfig.getRes().getString("panDiscret11.chkDisableSound.tooltip"));
		chkDisableSound.addActionListener(controler);
		
		//initialisation serial decoder
		txtSerial.setText("12345678");
		
		//selection mode
		this.placerComposants(panOptionsDiscret11,
				gbl,
				rdiDiscretCoding,
				GridBagConstraints.LINE_START, GridBagConstraints.WEST,
				0, 0,
				1,1,
				33,25,
				1, 1,1,1);
		this.placerComposants(panOptionsDiscret11,
				gbl,
				rdiDiscretDecoding,
				GridBagConstraints.WEST, GridBagConstraints.WEST,
				1, 0,
				1,1,
				33,25,
				1, 1,1,1);
		this.placerComposants(panOptionsDiscret11,
				gbl,
				rdiDiscretCorrel,
				GridBagConstraints.WEST, GridBagConstraints.WEST,
				2, 0,
				2,1,
				33,25,
				1, 1,1,1);
		
		//selection 16 bits keyword
		this.placerComposants(panOptionsDiscret11,
				gbl,
				pan16bits,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				0, 1,
				6,1,
				100,25,
				1, 1,1,1);		
		
		//audience level
		
		this.placerComposants(panOptionsDiscret11,
				gbl,
				panAudience,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 2,
				6,1,
				100,25,
				1, 1,1,1);		
	
		
		//delay1
		this.placerComposants(panOptionsDiscret11,
				gbl,
				panDelay1,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				0, 3,
				6,1,
				100,25,
				1, 1,1,1);
		//delay2
		this.placerComposants(panOptionsDiscret11,
				gbl,
				panDelay2,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				0, 4,
				6,1,
				100,25,
				1, 1,1,1);		

		//delay by default
		this.placerComposants(panOptionsDiscret11,
				gbl,
				chkDelay,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 5,
				2,1,
				10,25,
				1, 1,1,1);
		
		// enable/disable noBlackBar
		this.placerComposants(panOptionsDiscret11,
				gbl,
				chkNoBlackBar,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				2, 5,
				1,1,
				10,25,
				1, 1,1,1);
		
		//check sound
		this.placerComposants(panOptionsDiscret11,
				gbl,
				chkSound,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				3, 5,
				1,1,
				10,25,
				1, 1,1,1);
		//disable sound
		this.placerComposants(panOptionsDiscret11,
				gbl,
				chkDisableSound,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				4, 5,
				1,1,
				10,25,
				1, 1,1,1);
		
		//frame start
		this.placerComposants(panOptionsDiscret11,
				gbl,
				panFrameStart,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				0, 6,
				6,1,
				100,25,
				1, 1,1,1);
		this.placerComposants(panOptionsDiscret11,
				gbl,
				panKeyboardCode,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				0, 7,
				6,1,
				100,25,
				1, 1,1,1);
		
		
		
		jsp16bitKeyword.addChangeListener(controler);
	}
	
	private void createPanVideo(){
		
		panVideoOptions = new JPanel();
		
		titlePanVideo = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				JobConfig.getRes().getString("panVideo.titlePanVideo"));
		panVideoOptions.setBorder(titlePanVideo);
		GridBagLayout gbl = new GridBagLayout();
		
		rdi720 = new JRadioButton("720x576");
		rdi768 = new JRadioButton("768x576");
		rdi768.setSelected(true);
		ButtonGroup btngrp = new ButtonGroup();
		btngrp.add(rdi720);
		btngrp.add(rdi768);
		
		labAudioCodec = new JLabel(JobConfig.getRes().getString("panVideo.labAudioCodec"));
		String[] tabAudio = {"mp3 96 kbs","mp3 128 kbs","mp3 160 kbs",
				"mp3 192 kbs","mp3 224 kbs","mp3 320 kbs","wav (mkv)"};
		combAudioCodec = new JComboBox<String>(tabAudio);		
		combAudioCodec.setSelectedIndex(3);
		combAudioCodec.addActionListener(controler);
		
		String[] tabAudioRate = {"44100 Hz", "48000 Hz"};
		combAudioRate = new JComboBox<String>(tabAudioRate);
		combAudioRate.addActionListener(controler);
		
		chkPlayer = new JCheckBox(JobConfig.getRes().getString("panVideo.chkPlayer"));
		chkPlayer.addActionListener(controler);
		chkPlayer.setToolTipText(JobConfig.getRes().getString("panVideo.chkPlayer.tooltip"));
		
		chkHorodatage = new JCheckBox(JobConfig.getRes().getString("panVideo.chkHorodatage"));		
		chkHorodatage.setToolTipText(JobConfig.getRes().getString("panVideo.chkHorodatage.tooltip"));
		chkHorodatage.addActionListener(controler);
		
		
		String[] tab = {"h264","mpeg2","divx", "huffyuv", "h264 v2", "FFV1"};
		combCodec = new JComboBox<String>(tab);	
		combCodec.addActionListener(controler);				
		labCodec = new JLabel(JobConfig.getRes().getString("panVideo.labCodec"));
		
		labBitrate = new JLabel(JobConfig.getRes().getString("panVideo.labBitrate"));
		slidBitrate = new JSlider(JSlider.HORIZONTAL,1,20000,10000);
		slidBitrate.addChangeListener(controler);
		slidBitrate.setMajorTickSpacing(5000);		
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put( new Integer( 1 ), new JLabel("1"));	
		labelTable.put( new Integer( 20000 ), new JLabel("20000"));
		slidBitrate.setLabelTable(labelTable);
		slidBitrate.setPaintLabels(true);				
		txtBitrate = new JTextField(4);
		txtBitrate.setEditable(false);
		txtBitrate.setText("10000");
		
		slidFrames = new JSlider(JSlider.HORIZONTAL,1,200000,200000);
		slidFrames.addChangeListener(controler);	
		slidFrames.setMajorTickSpacing(50000);
		slidFrames.setMinorTickSpacing(10000);
		Hashtable<Integer, JLabel> labelTable2 = new Hashtable<Integer, JLabel>();
		labelTable2.put( new Integer( 1 ), new JLabel("1"));		
		labelTable2.put( new Integer( 200000 ), new JLabel("200000"));
		slidFrames.setLabelTable(labelTable2);
		slidFrames.setPaintLabels(true);		
		slidFrames.setPaintTicks(true);
		labNbFrames = new JLabel(JobConfig.getRes().getString("panVideo.labNbFrames"));
		txtNbFrames = new JTextField(10);		
		txtNbFrames.setEditable(false);		
		txtNbFrames.setText("200000");
		
		
		lblExtension = new JLabel(JobConfig.getRes().getString("panVideo.lblExtension"));		
		String[] tabExtension = {"mp4","avi","mkv", "mpeg", "ts"};
		jcbExtension = new JComboBox<String>(tabExtension);
		jcbExtension.setSelectedIndex(0);
		jcbExtension.addActionListener(controler);
		
		//init codec video panel
		JPanel panVideoCodec = new JPanel();
		GridBagLayout gblCodecVideo = new GridBagLayout();
		
		this.placerComposants(panVideoCodec,
				gblCodecVideo,
				labCodec,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 0,
				1,1,
				10,50,
				1, 1,1,1);
		this.placerComposants(panVideoCodec,
				gblCodecVideo,
				combCodec,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				1, 0,
				1,1,
				15,50,
				1, 1,1,1);
		this.placerComposants(panVideoCodec,
				gblCodecVideo,
				labBitrate,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				2, 0,
				1,1,
				5,50,
				1, 1,1,1);
		this.placerComposants(panVideoCodec,
				gblCodecVideo,
				slidBitrate,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				3, 0,
				1,1,
				60,50,
				1, 1,1,1);
		this.placerComposants(panVideoCodec,
				gblCodecVideo,
				txtBitrate,
				GridBagConstraints.LINE_START, GridBagConstraints.EAST,
				4, 0,
				1,1,
				10,50,
				1, 1,1,1);
		
		//init video extension panel
		JPanel panExtensionVideo = new JPanel();
		GridBagLayout gblExtension = new GridBagLayout();
		
		this.placerComposants(panExtensionVideo,
				gblExtension,
				lblExtension,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 0,
				1,1,
				10,50,
				1, 1,1,1);
		this.placerComposants(panExtensionVideo,
				gblExtension,
				jcbExtension,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				1, 0,
				1,1,
				15,50,
				1, 1,1,1);
		this.placerComposants(panExtensionVideo,
				gblExtension,
				labNbFrames,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				2, 0,
				1,1,
				5,50,
				1, 1,1,1);
		this.placerComposants(panExtensionVideo,
				gblExtension,
				slidFrames,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				3, 0,
				1,1,
				60,50,
				1, 1,1,1);
		this.placerComposants(panExtensionVideo,
				gblExtension,
				txtNbFrames,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				4, 0,
				1,1,
				10,50,
				1, 1,1,1);
		
		//init panVideoOption
		
		this.placerComposants(panVideoOptions,
				gbl,
				rdi720,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 0,
				1,1,
				25,50,
				1, 1,1,1);
		this.placerComposants(panVideoOptions,
				gbl,
				rdi768,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				1, 0,
				1,1,
				25,50,
				1, 1,1,1);
		
		this.placerComposants(panVideoOptions,
				gbl,
				labAudioCodec,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				2, 0,
				1,1,
				25,50,
				1, 1,1,1);
		this.placerComposants(panVideoOptions,
				gbl,
				combAudioCodec,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				3, 0,
				1,1,
				25,50,
				1, 1,1,1);
		this.placerComposants(panVideoOptions,
				gbl,
				combAudioRate,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				4, 0,
				1,1,
				25,50,
				1, 1,1,1);		
		this.placerComposants(panVideoOptions,
				gbl,
				chkPlayer,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				5, 0,
				1,1,
				25,50,
				1, 1,1,1);
		this.placerComposants(panVideoOptions,
				gbl,
				chkHorodatage,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				6, 0,
				1,1,
				25,50,
				1, 1,1,1);
		
		//codec video
		this.placerComposants(panVideoOptions,
				gbl,
				panVideoCodec,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				0, 1,
				7,1,
				100,50,
				1, 1,1,1);
		
		//extension
		this.placerComposants(panVideoOptions,
				gbl,
				panExtensionVideo,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				0, 2,
				7,1,
				100,50,
				1, 1,1,1);
		
		slidFrames.setValue(199999);
		slidFrames.setValue(200000);
	}
	
	private void createPanLog(){
		panProgress = new JPanel();		
		titlePanLog = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				JobConfig.getRes().getString("panLog.informations"));
		panProgress.setBorder(titlePanLog);		
		GridBagLayout gbl = new GridBagLayout();	
		
		progress = new JProgressBar(0,this.getSlidFrames().getValue());
		progress.setValue(0);
		progress.setStringPainted(true);
		
		textInfos = new JTextArea(5,40); //5,40
		textInfos.setEditable(false);
		textInfos.setAutoscrolls(true);
		textInfos.setLineWrap(true);
		JScrollPane scrollPane = new JScrollPane(textInfos,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				
		btnEnter = new JButton(JobConfig.getRes().getString("panLog.enter"));
		btnEnter.setIcon(new ImageIcon(this.getClass().getResource("/icons/apply.png")));
		btnEnter.setEnabled(false);
		btnEnter.addActionListener(controler);
		
		btnExit = new JButton(JobConfig.getRes().getString("panLog.exit"));
		btnExit.setIcon(new ImageIcon(this.getClass().getResource("/icons/exit.png")));
		btnExit.addActionListener(controler);
		
		btnCancel = new JButton(JobConfig.getRes().getString("panLog.cancel"));
		btnCancel.setIcon(new ImageIcon(this.getClass().getResource("/icons/cancel.png")));
		btnCancel.addActionListener(controler);
		btnCancel.addMouseListener(controler);
		btnCancel.setEnabled(false);
		
		this.placerComposants(panProgress,
				gbl,
				progress,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				0, 0,
				6,1,
				100,5,
				1, 1,1,1);
		this.placerComposants(panProgress,
				gbl,
				scrollPane,
				GridBagConstraints.LINE_START, GridBagConstraints.BOTH,
				0, 1,
				6,1,
				100,80,
				1, 1,1,1);
		this.placerComposants(panProgress,
				gbl,
				btnEnter,
				GridBagConstraints.EAST, GridBagConstraints.NONE,
				3, 2,
				1,1,
				1500,5,
				1, 1,1,1);
		this.placerComposants(panProgress,
				gbl,
				btnCancel,
				GridBagConstraints.EAST, GridBagConstraints.EAST,
				4, 2,
				1,1,
				100,5,
				1, 1,1,1);
		this.placerComposants(panProgress,
				gbl,
				btnExit,
				GridBagConstraints.EAST, GridBagConstraints.EAST,
				5, 2,
				1,1,
				100,5,
				1, 1,1,1);
	}
	
	public void showUI(){		
		frame.repaint();
		frame.setVisible(true);
	}
	
	/**
	 * <p>
	 * La méthode <code>placerComposants()</code> permet de disposer le
	 * composant spécifié au sein d'un panneau de contenu possédant un
	 * gestionnaire de placement <code>GridBagLayout</code>.
	 * </p>
	 * 
	 * @param pere
	 *            représente le panneau de contenu.
	 * @param gbl
	 *            représente le gestionnaire de placement.
	 * @param composant
	 *            représente le composant à disposer.
	 * @param anchor
	 *            le type d'auto agrandissement (<code>anchor<code>)
	 * @param fill indique la manière dont doit être rempli l'espace libre autour du composant (<code>fill</code>).
	 * @param gridx spécifie un numéro de colonnes (<code>gridx</code>).
	 * @param gridy spécifie un numéro de lignes (<code>gridy</code>).
	 * @param gridwidth spécifie un nombre de colonnes (<code>gridwidth</code>).
	 * @param gridheight spécifie un nombre de lignes (<code>gridheight</code>).
	 * @param weightx spécifie l'étirement sur l'espace horizontal (<code>weightx</code>).
	 * @param weighty spécifie l'étirement sur l'espace vertical (<code>weighty</code>).
	 * @param haut spécifie une marge supérieure.
	 * @param bas spécifie une marge inférieure.
	 * @param gauche spécifie une marge à gauche.
	 * @param droite spécifie une marge à droite.
	 */
	private void placerComposants(JPanel pere, GridBagLayout gbl,
			JComponent composant, int anchor, int fill, int gridx, int gridy,
			int gridwidth, int gridheight, double weightx, double weighty,
			int haut, int gauche, int bas, int droite) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(haut, gauche, bas, droite);
		gbc.anchor = anchor;
		gbc.fill = fill;
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.gridwidth = gridwidth;
		gbc.gridheight = gridheight;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		gbl.setConstraints(composant, gbc);
		pere.setLayout(gbl);
		pere.add(composant);
	}

	
	public JFrame getFrame() {
		return frame;
	}

	public JRadioButton getRdiVideo() {
		return rdiVideo;
	}

	public JRadioButton getRdiPhoto() {
		return rdiPhoto;
	}

	public JTextField getTxtInputFile() {
		return txtInputFile;
	}

	public JTextField getTxtOutputFile() {
		return txtOutputFile;
	}

	public JButton getBtnInputFile() {
		return btnInputFile;
	}

	public JButton getBtnOutputFile() {
		return btnOutputFile;
	}

	public JRadioButton getRdiDiscretCoding() {
		return rdiDiscretCoding;
	}

	public JRadioButton getRdiDiscretDecoding() {
		return rdiDiscretDecoding;
	}

	public JFormattedTextField getTxt16bitsWord() {
		return txt16bitsWord;
	}

	public JSlider getSlid16bitsWord() {
		return slid16bitsWord;
	}

	public JComboBox<String> getCombAudience() {
		return combAudience;
	}

	public JTextField getTxtNbFrames() {
		return txtNbFrames;
	}

	public JSlider getSlidFrames() {
		return slidFrames;
	}

	public JCheckBox getChkPlayer() {
		return chkPlayer;
	}

	public JCheckBox getChkStrictMode() {
		return chkStrictMode;
	}

	public JTextField getTxtDelay1() {
		return txtDelay1;
	}

	public JSlider getSlidDelay1() {
		return slidDelay1;
	}

	public JTextField getTxtDelay2() {
		return txtDelay2;
	}

	public JSlider getSlidDelay2() {
		return slidDelay2;
	}

	public JComboBox<String> getCombCodec() {
		return combCodec;
	}

	public JTextField getTxtBitrate() {
		return txtBitrate;
	}

	public JSlider getSlidBitrate() {
		return slidBitrate;
	}

	public JRadioButton getRdi720() {
		return rdi720;
	}

	public JRadioButton getRdi768() {
		return rdi768;
	}

	public JProgressBar getProgress() {
		return progress;
	}

	public JTextArea getTextInfos() {
		return textInfos;
	}

	public JButton getBtnEnter() {
		return btnEnter;
	}

	public JButton getBtnExit() {
		return btnExit;
	}

	public JCheckBox getChkDelay() {
		return chkDelay;
	}

	public JSpinner getJsp16bitKeyword() {
		return jsp16bitKeyword;
	}

	public JButton getBtnCancel() {
		return btnCancel;
	}

	public JSpinner getJspFrameStart() {
		return jspFrameStart;
	}

	public JSlider getSlideFrameStart() {
		return slideFrameStart;
	}

	public JPanel getPanVideoOptions() {
		return panVideoOptions;
	}

	public JComboBox<String> getJcbExtension() {
		return jcbExtension;
	}

	public JCheckBox getChkSound() {
		return chkSound;
	}

	public JCheckBox getChkAutorisation1() {
		return chkAutorisation1;
	}

	public void setChkAutorisation1(JCheckBox chkAutorisation1) {
		this.chkAutorisation1 = chkAutorisation1;
	}

	public JCheckBox getChkAutorisation2() {
		return chkAutorisation2;
	}

	public void setChkAutorisation2(JCheckBox chkAutorisation2) {
		this.chkAutorisation2 = chkAutorisation2;
	}

	public JCheckBox getChkAutorisation3() {
		return chkAutorisation3;
	}

	public void setChkAutorisation3(JCheckBox chkAutorisation3) {
		this.chkAutorisation3 = chkAutorisation3;
	}

	public JCheckBox getChkAutorisation4() {
		return chkAutorisation4;
	}

	public void setChkAutorisation4(JCheckBox chkAutorisation4) {
		this.chkAutorisation4 = chkAutorisation4;
	}

	public JCheckBox getChkAutorisation5() {
		return chkAutorisation5;
	}

	public void setChkAutorisation5(JCheckBox chkAutorisation5) {
		this.chkAutorisation5 = chkAutorisation5;
	}

	public JCheckBox getChkAutorisation6() {
		return chkAutorisation6;
	}

	public void setChkAutorisation6(JCheckBox chkAutorisation6) {
		this.chkAutorisation6 = chkAutorisation6;
	}

	public JFormattedTextField getTxtSerial() {
		return txtSerial;
	}

	public void setTxtSerial(JFormattedTextField txtSerial) {
		this.txtSerial = txtSerial;
	}

	public JTextField getTxtCode() {
		return txtCode;
	}

	public void setTxtCode(JTextField txtCode) {
		this.txtCode = txtCode;
	}

	public JCheckBox getChkHorodatage() {
		return chkHorodatage;
	}

	public void setChkHorodatage(JCheckBox chkHorodatage) {
		this.chkHorodatage = chkHorodatage;
	}

	public JLabel getLbl11bitsInfo() {
		return lbl11bitsInfo;
	}

	public void setLbl11bitsInfo(JLabel lbl11bitsInfo) {
		this.lbl11bitsInfo = lbl11bitsInfo;
	}

	public JCheckBox getChkDisableSound() {
		return chkDisableSound;
	}

	public void setChkDisableSound(JCheckBox chkDisableSound) {
		this.chkDisableSound = chkDisableSound;
	}

	public JButton getBtnAbout() {
		return btnAbout;
	}

	public JFormattedTextField getTxtMultiCode() {
		return txtMultiCode;
	}

	public JSpinner getJspCycle() {
		return jspCycle;
	}

	public JLabel getLblMultiAudience() {
		return lblMultiAudience;
	}

	public JLabel getLblCycle() {
		return lblCycle;
	}

	public JLabel getLabAudience() {
		return labAudience;
	}
	
	public JCheckBox getChkNoBlackBar() {
		return chkNoBlackBar;
	}

	public JLabel getAudioCodec() {
		return labAudioCodec;
	}

	public JComboBox<String> getCombAudioCodec() {
		return combAudioCodec;
	}

	public JComboBox<String> getCombAudioRate() {
		return combAudioRate;
	}

	public JComboBox<String> getCombSystemCrypt() {
		return combSystemCrypt;
	}

	public void setCombSystemCrypt(JComboBox<String> combSystemCrypt) {
		this.combSystemCrypt = combSystemCrypt;
	}

	public JRadioButton getRdiDiscretCorrel() {
		return rdiDiscretCorrel;
	}

	public void setRdiDiscretCorrel(JRadioButton rdiCorrel) {
		this.rdiDiscretCorrel = rdiCorrel;
	}

	public JRadioButton getRdiSysterCodingGen() {
		return rdiSysterCodingGen;
	}

	public void setRdiSysterCodingGen(JRadioButton rdiSysterCodingGen) {
		this.rdiSysterCodingGen = rdiSysterCodingGen;
	}

	public JRadioButton getRdiSysterDecodingGen() {
		return rdiSysterDecodingGen;
	}

	public void setRdiSysterDecodingGen(JRadioButton rdiSysterDecodingGen) {
		this.rdiSysterDecodingGen = rdiSysterDecodingGen;
	}

	public JRadioButton getRdiSysterDecodingCorrel() {
		return rdiSysterDecodingCorrel;
	}

	public void setRdiSysterDecodingCorrel(JRadioButton rdiSysterDecodingCorrel) {
		this.rdiSysterDecodingCorrel = rdiSysterDecodingCorrel;
	}

	public JRadioButton getRdiSysterCodingFile() {
		return rdiSysterCodingFile;
	}

	public void setRdiSysterCodingFile(JRadioButton rdiSysterCodingFile) {
		this.rdiSysterCodingFile = rdiSysterCodingFile;
	}

	public JRadioButton getRdiSysterCodingRandom() {
		return rdiSysterCodingRandom;
	}

	public void setRdiSysterCodingRandom(JRadioButton rdiSysterCodingRandom) {
		this.rdiSysterCodingRandom = rdiSysterCodingRandom;
	}

	public JRadioButton getRdiSysterDeCodingFile() {
		return rdiSysterDecodingFile;
	}

	public void setRdiSysterDeCodingFile(JRadioButton rdiSysterDeCodingFile) {
		this.rdiSysterDecodingFile = rdiSysterDeCodingFile;
	}

	public JRadioButton getRdiSysterDeCodingTags() {
		return rdiSysterDeCodingTags;
	}

	public void setRdiSysterDeCodingTags(JRadioButton rdiSysterDeCodingTags) {
		this.rdiSysterDeCodingTags = rdiSysterDeCodingTags;
	}

	public JCheckBox getChkTags() {
		return chkTags;
	}

	public void setChkTags(JCheckBox chkTags) {
		this.chkTags = chkTags;
	}

	public JButton getBtnFileSysterEnc() {
		return btnFileSysterEnc;
	}

	public void setBtnFileSysterEnc(JButton btnFileSysterEnc) {
		this.btnFileSysterEnc = btnFileSysterEnc;
	}

	public JButton getBtnFileSysterDec() {
		return btnFileSysterDec;
	}

	public void setBtnFileSysterDec(JButton btnFileSysterDec) {
		this.btnFileSysterDec = btnFileSysterDec;
	}

	public JPanel getPanSystemCrypt() {
		return panSystemCrypt;
	}

	public CardLayout getCard() {
		return card;
	}

	public CardLayout getCardEncDecSyster() {
		return cardEncDecSyster;
	}

	public JTextField getTxtSysterEnc() {
		return txtSysterEnc;
	}

	public void setTxtSysterEnc(JTextField txtSysterEnc) {
		this.txtSysterEnc = txtSysterEnc;
	}

	public JTextField getTxtSysterDec() {
		return txtSysterDec;
	}

	public void setTxtSysterDec(JTextField txtSysterDec) {
		this.txtSysterDec = txtSysterDec;
	}

	public JPanel getPanSysterEncDecCard() {
		return panSysterEncDecCard;
	}

	public JCheckBox getChkSoundSyster() {
		return chkSoundSyster;
	}

	public void setChkSoundSyster(JCheckBox chkSoundSyster) {
		this.chkSoundSyster = chkSoundSyster;
	}

	public JCheckBox getChkDisableSoundSyster() {
		return chkDisableSoundSyster;
	}

	public void setChkDisableSoundSyster(JCheckBox chkDisableSoundSyster) {
		this.chkDisableSoundSyster = chkDisableSoundSyster;
	}

	public JLabel getLabFrameStartSyster() {
		return labFrameStartSyster;
	}

	public void setLabFrameStartSyster(JLabel labFrameStartSyster) {
		this.labFrameStartSyster = labFrameStartSyster;
	}

	public JSpinner getJspFrameStartSyster() {
		return jspFrameStartSyster;
	}

	public void setJspFrameStartSyster(JSpinner jspFrameStartSyster) {
		this.jspFrameStartSyster = jspFrameStartSyster;
	}

	public JSlider getSlideFrameStartSyster() {
		return slideFrameStartSyster;
	}

	public void setSlideFrameStartSyster(JSlider slideFrameStartSyster) {
		this.slideFrameStartSyster = slideFrameStartSyster;
	}

	public JComboBox<String> getComboTableSysterEnc() {
		return comboTableSysterEnc;
	}

	public JComboBox<String> getComboTableSysterDec() {
		return comboTableSysterDec;
	}

	public JMenuItem getmOpen() {
		return mOpen;
	}

	public JMenuItem getmExit() {
		return mExit;
	}

	public JMenuItem getmGen() {
		return mGen;
	}

	public JMenuItem getmAbout() {
		return mAbout;
	}

	public JMenuItem getmDocumentation() {
		return mDocumentation;
	}

	public JMenuItem getmAuto() {
		return mAuto;
	}

	public JMenuItem getmEnglish() {
		return mEnglish;
	}

	public JMenuItem getmFrench() {
		return mFrench;
	}

//	public Locale getLocale() {
//		return locale;
//	}

//	public void setLocale(Locale locale) {
//		this.locale = locale;
//	}

//	public ResourceBundle getRes() {
//		return res;
//	}
//
//	public void setRes(ResourceBundle res) {
//		this.res = res;
//	}

	public JPanel getPanGlobal() {
		return panGlobal;
	}

	public JRadioButtonMenuItem getmGerman() {
		return mGerman;
	}

	public JRadioButtonMenuItem getmItalian() {
		return mItalian;
	}

	public JRadioButtonMenuItem getmSpanish() {
		return mSpanish;
	}

	public JRadioButtonMenuItem getmPolish() {
		return mPolish;
	}

	public JComboBox<String> getCbColorMode() {
		return cbColorMode;
	}

	public void setCbColorMode(JComboBox<String> cbColorMode) {
		this.cbColorMode = cbColorMode;
	}

	public JCheckBox getCbAveragePal() {
		return cbAveragePal;
	}

	public void setCbAveragePal(JCheckBox cbAveragePal) {
		this.cbAveragePal = cbAveragePal;
	}

	public JComboBox<String> getCbYUV() {
		return cbYUV;
	}

	public CardLayout getCardEncDecVideocrypt() {
		return cardEncDecVideocrypt;
	}

	public void setCardEncDecVideocrypt(CardLayout cardEncDecVideocrypt) {
		this.cardEncDecVideocrypt = cardEncDecVideocrypt;
	}

	public JRadioButton getRdiVideocryptCoding() {
		return rdiVideocryptCoding;
	}

	public JRadioButton getRdiVideocryptDecoding() {
		return rdiVideocryptDecoding;
	}

	public JRadioButton getRdiVideocryptCorrel() {
		return rdiVideocryptCorrel;
	}

	public JButton getBtnVideocryptDec() {
		return btnVideocryptDec;
	}

	public JCheckBox getChkSoundVideocrypt() {
		return chkSoundVideocrypt;
	}

	public void setChkSoundVideocrypt(JCheckBox chkSoundVideocrypt) {
		this.chkSoundVideocrypt = chkSoundVideocrypt;
	}

	public JCheckBox getChkDisableSoundVideocrypt() {
		return chkDisableSoundVideocrypt;
	}

	public void setChkDisableSoundVideocrypt(JCheckBox chkDisableSoundVideocrypt) {
		this.chkDisableSoundVideocrypt = chkDisableSoundVideocrypt;
	}

	public JSpinner getJspFrameStartVideocrypt() {
		return jspFrameStartVideocrypt;
	}

	public JSlider getSlideFrameStartVideocrypt() {
		return slideFrameStartVideocrypt;
	}

	public JPanel getPanVideocryptOptions() {
		return panVideocryptOptions;
	}

	public JTextField getTxtVideocryptDec() {
		return txtVideocryptDec;
	}

	public void setTxtVideocryptDec(JTextField txtVideocryptDec) {
		this.txtVideocryptDec = txtVideocryptDec;
	}	

	public JRadioButton getRdiVideocryptDecodingFile() {
		return rdiVideocryptDecodingFile;
	}

	public JRadioButton getRdiVideocryptCodingAuto() {
		return rdiVideocryptCodingAuto;
	}

	public JRadioButton getRdiVideocryptCodingFile() {
		return rdiVideocryptCodingFile;
	}

	public JPanel getPanVideocryptEncDecCard() {
		return panVideocryptEncDecCard;
	}

	public JTextField getTxtVideocryptEnc() {
		return txtVideocryptEnc;
	}

	public JButton getBtnVideocryptEnc() {
		return btnVideocryptEnc;
	}

	public JCheckBox getChkChangeOffsetIncrement() {
		return chkChangeOffsetIncrement;
	}

	public void setChkChangeOffsetIncrement(JCheckBox chkChangeOffsetIncrement) {
		this.chkChangeOffsetIncrement = chkChangeOffsetIncrement;
	}

	public JCheckBox getChkRestrictRange() {
		return chkRestrictRange;
	}

	public JCheckBox getChkLogVideocrypt() {
		return chkLogVideocrypt;
	}

	public JCheckBox getChkVideocryptTags() {
		return chkVideocryptTags;
	}

	public JRadioButton getRdiVideocryptTagsDecoding() {
		return rdiVideocryptTagsDecoding;
	}
	

}
