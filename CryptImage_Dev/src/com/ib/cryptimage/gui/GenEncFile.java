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
 * 22 nov. 2015 Author Mannix54
 */



package com.ib.cryptimage.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Locale;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.MaskFormatter;

import com.ib.cryptimage.core.JobConfig;

/**
 * @author Mannix54
 *
 */
public class GenEncFile extends JDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8168779716821128524L;
	private JLabel labTable;
	private JComboBox<String> combTable;
	private JTextField txtFile;
	private JButton btnOpen;
	private JButton btnValid;
	private JButton btnExit;
	private JLabel labLines;
	private JFormattedTextField txtLines;


	/**
	 * 
	 */
	public GenEncFile(JFrame parent, String title, boolean modal) {
		super(parent, title, modal);
		this.setSize(580, 180);
		this.setLocationRelativeTo(null);	
		
		initGUI();
		
	    this.setResizable(false);	    
	    	    
	    
	}
	
	public void display(){
		this.setVisible(true);
	}
	
	private void initGUI(){		
		
		
		JPanel panGlobal = new JPanel();
		panGlobal.setLayout(new BoxLayout(panGlobal, BoxLayout.Y_AXIS));
		
		JPanel panTable = new JPanel();
		JPanel panLines = new JPanel();
		JPanel panFile = new JPanel();
		
		JPanel panBtn = new JPanel();
		panBtn.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		labTable = new JLabel(JobConfig.getRes().getString("genEncFile.labTable"));
		
		String[] tab = {JobConfig.getRes().getString("genEncFile.tab1"), JobConfig.getRes().getString("genEncFile.tab2")};
		combTable = new JComboBox<String>(tab);
		combTable.setSelectedIndex(1);
		
		labLines = new JLabel(JobConfig.getRes().getString("genEncFile.labLines"));
		MaskFormatter mask;
		try {
			mask = new MaskFormatter("######");
			mask.setValidCharacters("0123456789");
			mask.setPlaceholderCharacter('0');			
			//mask.setOverwriteMode(false);			
			
			txtLines = new JFormattedTextField(mask);
			txtLines.setColumns(6);
			txtLines.setHorizontalAlignment(JTextField.RIGHT);
		
			
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		txtLines.setText("200000");
		
		btnOpen = new JButton(JobConfig.getRes().getString("genEncFile.btnOpen"));
		btnOpen.setIcon(new ImageIcon(this.getClass().getResource("/icons/filesave.png")));
		btnOpen.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				setSaveFile();				
			}
		});
		
		
		txtFile = new JTextField(40);
		txtFile.setEditable(false);
		
		btnValid = new JButton(JobConfig.getRes().getString("genEncFile.btnValid"));
		btnValid.setIcon(new ImageIcon(this.getClass().getResource("/icons/apply.png")));
		btnValid.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (genFile()) {
						JOptionPane.showMessageDialog(null, 
								JobConfig.getRes().getString("genEncFile.success"),								
								JobConfig.getRes().getString("genEncFile.successTitle"),
								JOptionPane.INFORMATION_MESSAGE);
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					JOptionPane
					.showMessageDialog(
							null,
						e1.getMessage(),
						JobConfig.getRes().getString("genEncFile.errorIO"),
							JOptionPane.ERROR_MESSAGE);
					dispose();
				}
				
			}
		});
		
		btnExit = new JButton(JobConfig.getRes().getString("genEncFile.btnExit"));
		btnExit.setIcon(new ImageIcon(this.getClass().getResource("/icons/exit.png")));
		btnExit.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();				
			}
		});
		
		
		panTable.add(labTable);
		panTable.add(combTable);
		panLines.add(labLines);
		panLines.add(txtLines);
		panFile.add(txtFile);
		panFile.add(btnOpen);
		
		panBtn.add(btnValid);
		panBtn.add(btnExit);
		
		panGlobal.add(panTable);
		panGlobal.add(panLines);
		panGlobal.add(panFile);
		panGlobal.add(panBtn);
		
		this.getContentPane().add(panGlobal, BorderLayout.CENTER);
		
	}
	
	private void setSaveFile() {
				
		JFileChooser dialogue = new JFileChooser();
				
		dialogue.setLocale(new Locale(JobConfig.getUserLanguage()));
		dialogue.updateUI();
		
		dialogue.setAcceptAllFileFilterUsed(false);
		dialogue.setDialogTitle(JobConfig.getRes().getString("genEncFile.dialogTitle"));

		FileNameExtensionFilter filter;
		String[] extension;
		
		if(new File(JobConfig.getGui().getTxtOutputFile().getText()).exists()){
			dialogue.setCurrentDirectory(new File(JobConfig.getGui().getTxtOutputFile().getText()));
		}

		extension = new String[] { "enc" };
		filter = new FileNameExtensionFilter(JobConfig.getRes().getString("genEncFile.filenameExtensionFilter"), extension);

		dialogue.setFileFilter(filter);

		File file;
		if (dialogue.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			file = dialogue.getSelectedFile();
			String path = file.getAbsolutePath();
			if (!path.endsWith(".enc")) {
				path = path + ".enc";
			}
			this.txtFile.setText(path);
		}

	}
	
	private boolean genFile() throws IOException{		
		
		if( this.txtLines.getText().trim().equals("") 
				|| Integer.valueOf(this.txtLines.getText().toString().trim()) < 1 ){
			JOptionPane
			.showMessageDialog(
					this,
					JobConfig.getRes().getString("genEncFile.errorGenFileFrames"),
					JobConfig.getRes().getString("genEncFile.errorGenFileFramesTitle"),
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
		if( txtFile.getText().equals("") ){
			JOptionPane
			.showMessageDialog(
					this,
					JobConfig.getRes().getString("genEncFile.errorNoFileName"),
					JobConfig.getRes().getString("genEncFile.errorNoFileNameTitle"),
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
		
		Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
		btnValid.setEnabled(false);
		btnExit.setEnabled(false);
		
		FileWriter fileOut;
				
		int lines = Integer.valueOf(this.txtLines.getText().toString().trim());
		int typeTable = combTable.getSelectedIndex() + 1;
		int offset1, increment1, offset2;//, increment2;
		int min = 0;
		int max = 255;
		double val;
		min = 1;
		max = 127;	
		
		Random rand;
		
		fileOut = new FileWriter(txtFile.getText());

		for (int i = 0; i < lines; i++) {
			rand = new Random();
			min = 0;
			max = 255;
			
			offset1 = rand.nextInt(max - min + 1) + min;

			min = 1;
			max = 127;
			
			val = rand.nextInt(max - min + 1) + min;
			if ((val / 2) - (int) (val / 2) == 0) {
				increment1 = (int) (val + 1);
			} else {
				increment1 = (int) (val);
			}
			////
			rand = new Random();
			
			min = 0;
			max = 255;

//			offset2 = rand.nextInt(max - min + 1) + min;

//			min = 1;
//			max = 127;
//			
//			val = rand.nextInt(max - min + 1) + min;
//			if ((val / 2) - (int) (val / 2) == 0) {
//				increment2 = (int) (val + 1);
//			} else {
//				increment2 = (int) (val);
//			}

			offset2 = offset1;
//			if(offset2 > 255){
//				offset2 = offset2 - 256;
//			}
			
			fileOut.write("frame " + (i + 1) + ";" + typeTable + ";" 
			+ offset1 + ";" + increment1 + ";" + offset2 + ";"
					+ increment1 + "\r\n");			
			
		}

		fileOut.close();		
		Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
		btnValid.setEnabled(true);
		btnExit.setEnabled(true);
		
		return true;
	}

}
