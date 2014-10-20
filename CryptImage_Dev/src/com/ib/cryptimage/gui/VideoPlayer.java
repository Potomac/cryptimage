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
 * 29 sept. 2014 Author Mannix54
 */



package com.ib.cryptimage.gui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;

import com.ib.cryptimage.core.JobConfig;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;


public class VideoPlayer implements MouseListener {

	private JLabel label;
	private JLabel labelState;
	private ImageIcon icon;
	private JFrame frame;
	private JPanel panLabel;
	private JPanel panBtn;
	
	private JButton btnInverse;
	private double frameRate;
	private long systemPreviousCurrentTime = 0;	
	private boolean inverse;
	

	public VideoPlayer(double frameRate) {
		frame = new JFrame();
		this.frame.setLayout(new BorderLayout());
		this.frameRate = frameRate;
		this.inverse = false;
		icon = new ImageIcon();
		frame = new JFrame();
		label = new JLabel(icon);
		panLabel = new JPanel();
		panLabel.add(label);
		panLabel.setSize(icon.getIconWidth()+10, icon.getIconHeight()+10);
			
		
		this.btnInverse = new JButton("On/Off");		
		panBtn = new JPanel();
		panBtn.setLayout(new BoxLayout(panBtn, BoxLayout.LINE_AXIS));
		panBtn.add(btnInverse);
		
		labelState = new JLabel("Device is On");
		labelState.setForeground(Color.green);
		panBtn.add(labelState);
		
		this.btnInverse.addMouseListener(this);
		
		frame.getContentPane().add(panLabel, BorderLayout.NORTH);
		frame.getContentPane().add(panBtn,BorderLayout.SOUTH);
		
		frame.setSize(panLabel.getWidth(), panLabel.getHeight() + 60);
		
		frame.setAutoRequestFocus(false);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void addImage(BufferedImage img) {		
		this.icon.setImage(img);		
		label.setIcon(icon);
		
		panLabel.setSize(icon.getIconWidth()+10, icon.getIconHeight()+10);
		frame.setSize(panLabel.getWidth() + 15, panLabel.getHeight() + 60);
		
		//label.revalidate();
		//label.repaint();
		//panLabel.revalidate();
		//panLabel.repaint();
		//panBtn.revalidate();
		//panBtn.repaint();
		//frame.revalidate();
		//frame.repaint();
		
		
	
		//frame.setSize(panLabel.getWidth() + 10, panLabel.getHeight() + 50);

//		frame.setMaximumSize(new Dimension(icon.getIconWidth() + 10, icon
//				.getIconHeight() + 100));
//		frame.setMinimumSize(new Dimension(icon.getIconWidth() + 10, icon
//				.getIconHeight() + 100));
//		frame.setPreferredSize(new Dimension(icon.getIconWidth() + 10, icon
//				.getIconHeight() + 100));
		
	}

	public void showImage() {
		// frame.setLocation(200,100);
		frame.setVisible(true);
		
		double systemClockCurrentTime = System.currentTimeMillis();
		
		double diffTime = systemClockCurrentTime - this.systemPreviousCurrentTime;
		double time;
		
		if (diffTime >  (1000/frameRate) && this.systemPreviousCurrentTime !=0){
			 time =  0;//(1000/frameRate);			 
		}
		else if(this.systemPreviousCurrentTime == 0){
			time = (1000/frameRate);			
		}
		else {
			time = (1000/frameRate) - diffTime;			
		}		
		
			try {
				Thread.sleep((long) time );
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
			
			double speed = (double)(1000/frameRate);
			double tot = time + diffTime;
			int speed_pourc = (int)((speed/tot) * 100);		
		
		this.systemPreviousCurrentTime = System.currentTimeMillis();
	}

	public boolean isInverse() {
		return inverse;
	}
	

	@Override
	public void mouseClicked(MouseEvent arg0) {		
		if(inverse == true){
			this.inverse = false;
			this.labelState.setText("Device is On");
			labelState.setForeground(Color.green);
		}
		else{
			this.inverse = true;
			this.labelState.setText("Device is Off");
			labelState.setForeground(Color.red);
		}		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
