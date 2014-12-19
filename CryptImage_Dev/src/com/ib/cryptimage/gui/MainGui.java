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
 * 26 oct. 2014 Author Mannix54
 */



package com.ib.cryptimage.gui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
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
	
	private JPanel panMode;
	private JRadioButton rdiVideo;
	private JRadioButton rdiPhoto;
	
	private JPanel panFile;
	private JTextField txtInputFile;
	private JTextField txtOutputFile;
	private JButton btnInputFile;
	private JButton btnOutputFile;
	
	private JPanel panOptionsDiscret11;
	private JRadioButton rdiCoding;
	private JRadioButton rdiDecoding;
	private JLabel lab11bitsWord;
	private JTextField txt11bitsWord;
	private JSlider slid11bitsWord;
	private JLabel labAudience;
	private JComboBox<String> combAudience;
	private JLabel labNbFrames;
	private JTextField txtNbFrames;
	private JSlider slidFrames;
	private JCheckBox chkAudience7;
	private JCheckBox chkPlayer;
	private JCheckBox chkStrictMode;
	private JLabel labDelay1;
	private JTextField txtDelay1;
	private JSlider slidDelay1;
	private JLabel labDelay2;
	private JTextField txtDelay2;
	private JSlider slidDelay2;
	private JCheckBox chkDelay;
	private JSpinner jsp11bitKeyword;
	private JLabel labFrameStart;
	private JSpinner jspFrameStart;
	private JSlider slideFrameStart;

	private JPanel panVideoOptions;
	private JLabel labCodec;
	private JComboBox<String> combCodec;
	private JLabel labBitrate;
	private JTextField txtBitrate;
	private JSlider slidBitrate;
	private JRadioButton rdi720;
	private JRadioButton rdi768;
	private JCheckBox chkSound;
	
	private JPanel panProgress;
	private JProgressBar progress;
	private JTextArea textInfos;
	
	private JPanel panButtons;
	private JButton btnEnter;
	private JButton btnExit;
	private JButton btnCancel;
	
	private JLabel lblExtension;
	private JComboBox<String> jcbExtension;
	
	private JobConfig job;
	
	public MainGui(JobConfig job){
		this.job = job;
		this.job.setGui(this);
		
		 try {
	            // Set cross-platform Java L&F (also called "Metal")
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
		
		frame = new JFrame("CryptImage v0.0.7");			
		frame.setLayout(new GridLayout(2,1));
		JPanel panGlobal = new JPanel();
		//panGlobal.setLayout(new BoxLayout(panGlobal,BoxLayout.LINE_AXIS));
		frame.setSize(670,780);
		frame.setAutoRequestFocus(true);
		frame.setMinimumSize(new Dimension(670, 780));
		frame.setResizable(true);		
		
		createPanMode();
		createPanFile();
		createPanDiscret11();
		createPanVideo();
		createPanLog();
		
		GridBagLayout gbl = new GridBagLayout();
		
		this.placerComposants(panGlobal,
				gbl,
				panMode,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				0, 0,
				1,1,
				100,10,
				1, 1,1,1);	
		this.placerComposants(panGlobal,
				gbl,
				panFile,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				0, 1,
				1,1,
				100,10,
				1, 1,1,1);
		this.placerComposants(panGlobal,
				gbl,
				panOptionsDiscret11,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				0, 2,
				1,1,
				100,30,
				1, 1,1,1);
		this.placerComposants(panGlobal,
				gbl,
				panVideoOptions,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				0, 3,
				1,1,
				100,20,
				1, 1,1,1);
		this.placerComposants(panGlobal,
				gbl,
				panProgress,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				0, 4,
				1,1,
				100,30,
				1, 1,1,1);

		
		
		//panGlobal.add(panMode);
		//panGlobal.add(panFile);
		frame.setLayout(new GridLayout(1,0));
		frame.getContentPane().add(panGlobal);		
		frame.setVisible(true);
		frame.repaint();		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);		
	}
	
	private void createPanMode(){
		panMode = new JPanel();		
		
		TitledBorder title;
		title = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				"Mode");
		panMode.setBorder(title);
		
		rdiVideo = new JRadioButton("Vidéo");
		rdiVideo.setSelected(true);
		rdiVideo.addActionListener(new MainGui_ActionListener(this));
		rdiVideo.setToolTipText("pour coder ou décoder des fichiers vidéos");
		rdiPhoto = new JRadioButton("Photo");
		rdiPhoto.addActionListener(new MainGui_ActionListener(this));
		rdiPhoto.setToolTipText("pour coder ou décoder des fichiers images");
		ButtonGroup btnGroup = new ButtonGroup();
		btnGroup.add(rdiVideo);
		btnGroup.add(rdiPhoto);
		
		chkStrictMode = new JCheckBox("Respect de la norme");		
		chkStrictMode.addActionListener(new MainGui_ActionListener(this));
		chkStrictMode.setSelected(true);
		chkStrictMode.setToolTipText("<html>-si option activée : gestion des lignes 310/622,"
				+ "<br/>redimensionnement automatique vers le format 4/3 720x576 ou 768x576<br/>"
				+ "-si option désactivée : pas de gestion des lignes 310/622 et "
				+ "la vidéo garde sa résolution originale</html>");
		
		
		
		GridBagLayout gbl = new GridBagLayout();
		
		//panMode.setLayout(new BoxLayout(panMode, BoxLayout.LINE_AXIS));
		this.placerComposants(panMode,
				gbl,
				rdiVideo,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				0, 0,
				1,1,
				33,0.5,
				1, 1,1,1);	
		this.placerComposants(panMode,
				gbl,
				rdiPhoto,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				1, 0,
				1,1,
				33,0.5,
				1, 1,1,1);
		this.placerComposants(panMode,
				gbl,
				chkStrictMode,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				2, 0,
				1,1,
				33,0.5,
				1, 1,1,1);		
	}
	
	private void createPanFile(){
		panFile = new JPanel();
		//panFile.setSize(frame.getWidth(), 200);
		TitledBorder title;
		title = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				"Fichiers");
		panFile.setBorder(title);
		
		txtInputFile = new JTextField(40);
		txtInputFile.setEditable(false);
		txtOutputFile = new JTextField(40);
		txtOutputFile.setEditable(false);
		btnInputFile = new JButton("Ouvrir");
		btnInputFile.addActionListener(new MainGui_ActionListener(this));
		btnOutputFile = new JButton("Dossier");
		btnOutputFile.setEnabled(false);
		btnOutputFile.addActionListener(new MainGui_ActionListener(this));
		btnOutputFile.setToolTipText("le dossier de travail où seront stockés les fichiers générés");
		
//		panFile.setLayout(new GridLayout(2,2));
//		panFile.add(txtInputFile);
//		panFile.add(btnInputFile);
//		panFile.add(txtOutputFile);
//		panFile.add(btnOutputFile);
		
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
	
	private void createPanDiscret11(){
		panOptionsDiscret11 = new JPanel();
		TitledBorder title;
		title = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				"Options Discret11");
		panOptionsDiscret11.setBorder(title);
		GridBagLayout gbl = new GridBagLayout();
		
		rdiCoding = new JRadioButton("coder");
		rdiCoding.setSelected(true);
		rdiDecoding = new JRadioButton("décoder");
				
		ButtonGroup btnGroup = new ButtonGroup();
		btnGroup.add(rdiCoding);
		btnGroup.add(rdiDecoding);
		
		lab11bitsWord = new JLabel("mot de 11 bits");
		slid11bitsWord = new JSlider(JSlider.HORIZONTAL,1,2047,1337);
		slid11bitsWord.addChangeListener(new MainGui_ActionListener(this));			
		
		//slid11bitsWord.setMaximum(2047);
		//slid11bitsWord.setMinimum(1);
		slid11bitsWord.setValue(1337);
		slid11bitsWord.setMajorTickSpacing(500);
		slid11bitsWord.setMinorTickSpacing(100);
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put( new Integer( 1 ), new JLabel("1"));
		labelTable.put( new Integer( 2047 ), new JLabel("2047"));
		labelTable.put( new Integer( 1000 ), new JLabel("1000"));
		labelTable.put( new Integer( 500 ), new JLabel("500"));
		labelTable.put( new Integer( 1500 ), new JLabel("1500"));
		slid11bitsWord.setLabelTable(labelTable);
		slid11bitsWord.setPaintLabels(true);
		
		
		slid11bitsWord.setPaintTicks(true);
		//slid11bitsWord.setPaintLabels(true);	
	
		
		txt11bitsWord = new JTextField();
		
		txt11bitsWord.setEditable(false);
		txt11bitsWord.setText(String.valueOf(slid11bitsWord.getValue()));
		
		labAudience = new JLabel("Audience");		
		String[] tab = {"niveau 1","niveau 2","niveau 3","niveau 4",
				"niveau 5","niveau 6","niveau 7"};
		combAudience = new JComboBox<String>(tab);
		combAudience.setSelectedIndex(6);
		combAudience.addActionListener(new MainGui_ActionListener(this));
		
		chkAudience7 = new JCheckBox("code de fin de mois");
		chkAudience7.setSelected(true);
		chkAudience7.addActionListener(new MainGui_ActionListener(this));
		

		
		chkDelay = new JCheckBox("Retards par défaut");
		chkDelay.setSelected(true);
		chkDelay.addActionListener(new MainGui_ActionListener(this));
		
		labDelay1 = new JLabel("Retard 1");
		txtDelay1 = new JTextField(5);
		labDelay2 = new JLabel("Retard 2");
		txtDelay2 = new JTextField(5);
		txtDelay1.setEditable(false);
		txtDelay2.setEditable(false);
		slidDelay1 = new JSlider(JSlider.HORIZONTAL,500,3000,1000);
		slidDelay1.addChangeListener(new MainGui_ActionListener(this));	
		slidDelay2 = new JSlider(JSlider.HORIZONTAL,2000,5000,3000);
		slidDelay2.addChangeListener(new MainGui_ActionListener(this));
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
		
		
		
		jsp11bitKeyword = new JSpinner();
		jsp11bitKeyword.addChangeListener(new MainGui_ActionListener(this));
		JSpinner.NumberEditor spinnerEditor = new JSpinner.NumberEditor(jsp11bitKeyword);
		jsp11bitKeyword.setEditor(spinnerEditor);
		spinnerEditor.getModel().setMinimum(1);
		spinnerEditor.getModel().setMaximum(2047);
		spinnerEditor.getModel().setStepSize(1);
		spinnerEditor.getModel().setValue(1337);
	
		 JFormattedTextField tf = ((JSpinner.DefaultEditor) jsp11bitKeyword.getEditor()).getTextField();
		    tf.setEditable(false);
		   // tf.setBackground(Color.GRAY);
		    
		labFrameStart = new JLabel("démarrer à la trame ");
		slideFrameStart = new JSlider(JSlider.HORIZONTAL,1,2000,1);
		slideFrameStart.setToolTipText("permet de commencer le codage/décodage qu'à partir d'un numéro de trame précis");
		jspFrameStart = new JSpinner();	
		jspFrameStart.addChangeListener(new MainGui_ActionListener(this));		
		JSpinner.NumberEditor spinnerEditor2 = new JSpinner.NumberEditor(jspFrameStart);
		jspFrameStart.setEditor(spinnerEditor2);
		JComponent editor = jspFrameStart.getEditor();
		JFormattedTextField tf2 = ((JSpinner.DefaultEditor) editor).getTextField();
		tf2.setColumns(5);
		tf2.setEditable(false);
		spinnerEditor2.getModel().setMinimum(1);
		spinnerEditor2.getModel().setMaximum(2000);
		spinnerEditor2.getModel().setStepSize(1);
		spinnerEditor2.getModel().setValue(1);
		
		
		slideFrameStart.addChangeListener(new MainGui_ActionListener(this));			
		slideFrameStart.setValue(1);
		slideFrameStart.setMajorTickSpacing(1000);
		slideFrameStart.setMinorTickSpacing(100);
		Hashtable<Integer, JLabel> labelTable3 = new Hashtable<Integer, JLabel>();
		labelTable3.put( new Integer( 1 ), new JLabel("1"));		
		labelTable3.put( new Integer( 2000 ), new JLabel("2000"));
		slideFrameStart.setLabelTable(labelTable3);
		slideFrameStart.setPaintLabels(true);		
		slideFrameStart.setPaintTicks(true);
		
		
		this.placerComposants(panOptionsDiscret11,
				gbl,
				rdiCoding,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 0,
				1,1,
				80,25,
				1, 1,1,1);
		this.placerComposants(panOptionsDiscret11,
				gbl,
				rdiDecoding,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				1, 0,
				1,1,
				20,25,
				1, 1,1,1);
		this.placerComposants(panOptionsDiscret11,
				gbl,
				lab11bitsWord,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 1,
				1,1,
				5,25,
				1, 1,1,1);
		this.placerComposants(panOptionsDiscret11,
				gbl,
				slid11bitsWord,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				1, 1,
				1,1,
				85,25,
				1, 1,1,1);		
		this.placerComposants(panOptionsDiscret11,
				gbl,
				jsp11bitKeyword,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				2, 1,
				1,1,
				10,25,
				1, 1,1,1);
		this.placerComposants(panOptionsDiscret11,
				gbl,
				labAudience,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 2,
				1,1,
				30,25,
				1, 1,1,1);
		this.placerComposants(panOptionsDiscret11,
				gbl,
				combAudience,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				1, 2,
				1,1,
				70,25,
				1, 1,1,1);
		this.placerComposants(panOptionsDiscret11,
				gbl,
				chkAudience7,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 3,
				1,1,
				100,25,
				1, 1,1,1);		
		this.placerComposants(panOptionsDiscret11,
				gbl,
				labDelay1,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 4,
				1,1,
				5,25,
				1, 1,1,1);
		this.placerComposants(panOptionsDiscret11,
				gbl,
				slidDelay1,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				1, 4,
				1,1,
				85,25,
				1, 1,1,1);
		this.placerComposants(panOptionsDiscret11,
				gbl,
				txtDelay1,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				2, 4,
				1,1,
				10,25,
				1, 1,1,1);
		this.placerComposants(panOptionsDiscret11,
				gbl,
				labDelay2,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 5,
				1,1,
				5,25,
				1, 1,1,1);
		this.placerComposants(panOptionsDiscret11,
				gbl,
				slidDelay2,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				1, 5,
				1,1,
				85,25,
				1, 1,1,1);
		this.placerComposants(panOptionsDiscret11,
				gbl,
				txtDelay2,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				2, 5,
				1,1,
				10,25,
				1, 1,1,1);
		this.placerComposants(panOptionsDiscret11,
				gbl,
				chkDelay,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 6,
				1,1,
				100,25,
				1, 1,1,1);
		this.placerComposants(panOptionsDiscret11,
				gbl,
				labFrameStart,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 7,
				1,1,
				5,25,
				1, 1,1,1);
		this.placerComposants(panOptionsDiscret11,
				gbl,
				slideFrameStart,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				1, 7,
				1,1,
				85,25,
				1, 1,1,1);
		this.placerComposants(panOptionsDiscret11,
				gbl,
				jspFrameStart,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				2, 7,
				1,1,
				10,25,
				1, 1,1,1);	
	}
	
	private void createPanVideo(){
		
		panVideoOptions = new JPanel();
		TitledBorder title;
		title = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				"Options vidéo");
		panVideoOptions.setBorder(title);
		GridBagLayout gbl = new GridBagLayout();
		
		rdi720 = new JRadioButton("720x576 pixels");
		rdi768 = new JRadioButton("768x576 pixels");
		rdi768.setSelected(true);
		ButtonGroup btngrp = new ButtonGroup();
		btngrp.add(rdi720);
		btngrp.add(rdi768);
		
		chkPlayer = new JCheckBox("mode lecteur");
		chkPlayer.addActionListener(new MainGui_ActionListener(this));
		chkPlayer.setToolTipText("permet de regarder le résultat dans une fenêtre au lieu de créer la vidéo");
		
		chkSound = new JCheckBox("activer le son");		
		
		String[] tab = {"h264","mpeg2","divx", "huffyuv", "h264 v2"};
		combCodec = new JComboBox<String>(tab);	
		combCodec.addActionListener(new MainGui_ActionListener(this));				
		labCodec = new JLabel("codec");
		
		labBitrate = new JLabel("Bitrate");
		slidBitrate = new JSlider(JSlider.HORIZONTAL,1,20000,10000);
		slidBitrate.addChangeListener(new MainGui_ActionListener(this));
		slidBitrate.setMajorTickSpacing(5000);
		//slidBitrate.setMinorTickSpacing(1000);
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put( new Integer( 1 ), new JLabel("1"));	
		labelTable.put( new Integer( 20000 ), new JLabel("20000"));
		slidBitrate.setLabelTable(labelTable);
		slidBitrate.setPaintLabels(true);		
		//slidBitrate.setPaintTicks(true);
		txtBitrate = new JTextField(4);
		txtBitrate.setEditable(false);
		txtBitrate.setText("10000");
		
		slidFrames = new JSlider(JSlider.HORIZONTAL,1,2000,2000);
		slidFrames.addChangeListener(new MainGui_ActionListener(this));	
		slidFrames.setMajorTickSpacing(500);
		slidFrames.setMinorTickSpacing(100);
		Hashtable<Integer, JLabel> labelTable2 = new Hashtable<Integer, JLabel>();
		labelTable2.put( new Integer( 1 ), new JLabel("1"));		
		labelTable2.put( new Integer( 2000 ), new JLabel("2000"));
		slidFrames.setLabelTable(labelTable2);
		slidFrames.setPaintLabels(true);		
		slidFrames.setPaintTicks(true);
		labNbFrames = new JLabel("Nb trames");
		txtNbFrames = new JTextField(10);		
		txtNbFrames.setEditable(false);
		txtNbFrames.setText("2000");
		
		lblExtension = new JLabel("extension");		
		String[] tabExtension = {"mp4","avi","mkv", "mpeg", "ts"};
		jcbExtension = new JComboBox<String>(tabExtension);
		jcbExtension.setSelectedIndex(0);
		jcbExtension.addActionListener(new MainGui_ActionListener(this));
		
		
		this.placerComposants(panVideoOptions,
				gbl,
				rdi720,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 0,
				2,1,
				25,50,
				1, 1,1,1);
		this.placerComposants(panVideoOptions,
				gbl,
				rdi768,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				2, 0,
				1,1,
				25,50,
				1, 1,1,1);
		this.placerComposants(panVideoOptions,
				gbl,
				chkPlayer,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				3, 0,
				1,1,
				25,50,
				1, 1,1,1);
		this.placerComposants(panVideoOptions,
				gbl,
				chkSound,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				4, 0,
				1,1,
				25,50,
				1, 1,1,1);
		this.placerComposants(panVideoOptions,
				gbl,
				labCodec,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 1,
				1,1,
				5,50,
				1, 1,1,1);
		this.placerComposants(panVideoOptions,
				gbl,
				combCodec,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				1, 1,
				1,1,
				5,50,
				1, 1,1,1);
		this.placerComposants(panVideoOptions,
				gbl,
				labBitrate,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				2, 1,
				1,1,
				5,50,
				1, 1,1,1);
		this.placerComposants(panVideoOptions,
				gbl,
				slidBitrate,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				3, 1,
				2,1,
				80,50,
				1, 1,0,0);
		this.placerComposants(panVideoOptions,
				gbl,
				txtBitrate,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				5, 1,
				1,1,
				5,50,
				1, 1,1,1);
		this.placerComposants(panVideoOptions,
				gbl,
				lblExtension,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 2,
				1,1,
				5,50,
				1, 1,1,1);
		this.placerComposants(panVideoOptions,
				gbl,
				jcbExtension,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				1, 2,
				1,1,
				5,50,
				1, 1,1,1);		
		this.placerComposants(panVideoOptions,
				gbl,
				labNbFrames,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				2, 2,
				1,1,
				5,50,
				1, 1,1,1);
		this.placerComposants(panVideoOptions,
				gbl,
				slidFrames,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				3, 2,
				2,1,
				80,50,
				1, 1,1,1);
		this.placerComposants(panVideoOptions,
				gbl,
				txtNbFrames,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
				5, 2,
				1,1,
				5,50,
				1, 1,1,1);		
	}
	
	private void createPanLog(){
		panProgress = new JPanel();
		//panProgress.setLayout(new BorderLayout());
		TitledBorder title;
		title = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				"Informations");
		panProgress.setBorder(title);		
		GridBagLayout gbl = new GridBagLayout();
		
		progress = new JProgressBar(0,this.getSlidFrames().getValue());
		progress.setValue(0);
		progress.setStringPainted(true);
		
		textInfos = new JTextArea(5,40);
		textInfos.setEditable(false);
		textInfos.setAutoscrolls(true);
		textInfos.setLineWrap(true);
		JScrollPane scrollPane = new JScrollPane(textInfos,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		//textInfos.setSize(frame.getWidth(), 50);
		//scrollPane.setSize(500, 100);
		
		
		btnEnter = new JButton("Valider");
		btnEnter.setEnabled(false);
		btnEnter.addActionListener(new MainGui_ActionListener(this));
		
		btnExit = new JButton("Quitter");
		btnExit.addActionListener(new MainGui_ActionListener(this));
		
		btnCancel = new JButton("Annuler");
		btnCancel.addActionListener(new MainGui_ActionListener(this));
		btnCancel.addMouseListener(new MainGui_ActionListener(this));
		btnCancel.setEnabled(false);
		
//		panProgress.add(progress, BorderLayout.NORTH);
//		panProgress.add(textInfos, BorderLayout.CENTER);
//		panProgress.add(btnExit, BorderLayout.SOUTH);
//		panProgress.add(btnCancel, BorderLayout.SOUTH);
//		panProgress.add(btnEnter, BorderLayout.SOUTH);
		
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
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
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

	public JRadioButton getRdiCoding() {
		return rdiCoding;
	}

	public JRadioButton getRdiDecoding() {
		return rdiDecoding;
	}

	public JTextField getTxt11bitsWord() {
		return txt11bitsWord;
	}

	public JSlider getSlid11bitsWord() {
		return slid11bitsWord;
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

	public JCheckBox getChkAudience7() {
		return chkAudience7;
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

	public JSpinner getJsp11bitKeyword() {
		return jsp11bitKeyword;
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

	public JobConfig getJob() {
		return job;
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

}
