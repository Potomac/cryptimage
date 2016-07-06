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
 * 3 janv. 2015 Author Mannix54
 * http://ibsoftware.free.fr/cryptimage.php
 */



package com.ib.cryptimage.gui;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author Mannix54
 *
 */

public class ShowLicense{	
	private JScrollPane jScrollPane1;
	private JTextArea textArea;
	private JPanel pan;



	public ShowLicense() {
		initComponents();				
	}	
	
	public void show(){
		JOptionPane.showMessageDialog(null, pan, "GNU GPL V3 license",
				JOptionPane.INFORMATION_MESSAGE);	
	}

	private void initComponents() {
		
		textArea = new JTextArea();		

		textArea.setColumns(80);
		textArea.setLineWrap(true);
		textArea.setRows(30);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		jScrollPane1 = new JScrollPane(textArea);
		
		InputStream in = getClass().getResourceAsStream("license.txt");
		try {
			textArea.read(new InputStreamReader(in), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		pan = new JPanel();
		pan.setLayout(new BorderLayout());
		pan.add(jScrollPane1, BorderLayout.CENTER);		
		
		
	}	

}
