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
 * 21 sept. 2014 Author Mannix54
 */

package com.ib.cryptimage.gui;

/**
 * @author Mannix54
 *
 */

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Dimension;
import java.awt.image.BufferedImage;


public class VideoPlayer {

	private JLabel label;
	private ImageIcon icon;
	private JFrame frame;
	private int frameRate;
	private long systemPreviousCurrentTime = 0;	

	public VideoPlayer(int frameRate) {
		this.frameRate = frameRate;
		icon = new ImageIcon();
		frame = new JFrame();
		label = new JLabel(icon);
		frame.getContentPane().add(label);
		frame.setAutoRequestFocus(false);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void addImage(BufferedImage img) {		
		this.icon.setImage(img);		
		label.setIcon(icon);
		label.revalidate();
		label.repaint();

		frame.setSize(icon.getIconWidth() + 10, icon.getIconHeight() + 40);

		frame.setMaximumSize(new Dimension(icon.getIconWidth() + 10, icon
				.getIconHeight() + 40));
		frame.setMinimumSize(new Dimension(icon.getIconWidth() + 10, icon
				.getIconHeight() + 40));
		frame.setPreferredSize(new Dimension(icon.getIconWidth() + 10, icon
				.getIconHeight() + 40));
		
	}

	public void showImage() {
		// frame.setLocation(200,100);
		frame.setVisible(true);
		
		long systemClockCurrentTime = System.currentTimeMillis();
		
		long diffTime = systemClockCurrentTime - this.systemPreviousCurrentTime;
		long time;
		
		if (diffTime >  (1000/frameRate) && this.systemPreviousCurrentTime !=0){
			 time =  diffTime - (1000/frameRate);
		}
		else if(this.systemPreviousCurrentTime == 0){
			time = (1000/frameRate);
		}
		else {
			time = (1000/frameRate) - diffTime;
		}		
		
			try {
				Thread.sleep(time );
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}		
		
		this.systemPreviousCurrentTime = System.currentTimeMillis();
	}

}
