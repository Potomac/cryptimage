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
 * 29 sept. 2014 Author Mannix54
 */



package com.ib.cryptimage.core;

 

import com.ib.cryptimage.gui.MainGui;


public class Application {

	public static void main(String[] args) {
		JobConfig job = new JobConfig();

		job.setHasGUI(true);
		MainGui mainUI = new MainGui(job);		
		mainUI.showUI();
	}

}
