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
 * 22 déc. 2014 Author Mannix54
 */



package com.ib.cryptimage.gui;

import java.awt.event.KeyListener;

import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * @author Mannix54
 *
 */
public class Key16bits_DocumentListener implements DocumentListener {
	
	private MainGui mainGui;

     public Key16bits_DocumentListener(MainGui mainGui) {
		this.mainGui = mainGui;
	}
	
	@Override
	public void changedUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		System.out.println("ok");
		// TODO Auto-generated method stub
		SwingUtilities.invokeLater(updateGui);
		
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		System.out.println("ok2");
		SwingUtilities.invokeLater(updateGui);
		
	}
	

	
	final Runnable updateGui = new Runnable() {
	     public void run() {
	    	 mainGui.getSlid16bitsWord().setValue(
	 				Integer.valueOf(mainGui.getTxt16bitsWord().getText()));
	     }
	 };

}
