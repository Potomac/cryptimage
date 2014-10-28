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

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.WindowConstants;
import javax.swing.plaf.SliderUI;

import com.ib.cryptimage.core.JobConfig;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;


public class VideoPlayer  implements MouseListener, ActionListener, WindowListener {

	private JLabel label;
	private JLabel labelState;
	private ImageIcon icon;
	private JDialog frame;
	private JPanel panLabel;
	private JPanel panBtn;
	
	private JButton btnInverse;
	private JButton btnExit;
	private double frameRate;
	private long systemPreviousCurrentTime = 0;	
	private boolean inverse;
	private JobConfig job;
	

	public VideoPlayer(double frameRate, JobConfig job) {		
		this.job = job;
		frame = new JDialog();	
		frame.addWindowListener(this);
			
		
		
		this.frame.setLayout(new BorderLayout());
		this.frameRate = frameRate;
		this.inverse = false;
		icon = new ImageIcon();		
		label = new JLabel(icon);
		panLabel = new JPanel();
		panLabel.add(label);
		panLabel.setSize(icon.getIconWidth()+10, icon.getIconHeight()+10);
			
		
		this.btnInverse = new JButton("On/Off");
		this.btnExit = new JButton("Fermer");
		panBtn = new JPanel();
		panBtn.setLayout(new BorderLayout());
		panBtn.add(btnInverse, BorderLayout.WEST);
		
		labelState = new JLabel("Device is On");
		labelState.setForeground(Color.green);
		panBtn.add(labelState,BorderLayout.CENTER);
		panBtn.add(btnExit,BorderLayout.EAST);
		
		this.btnInverse.addActionListener(this);
		this.btnExit.addActionListener(this);
		
		frame.getContentPane().add(panLabel, BorderLayout.NORTH);
		frame.getContentPane().add(panBtn,BorderLayout.SOUTH);
		
		
		frame.setSize(panLabel.getWidth(), panLabel.getHeight() + 60);
		
		//frame.setAutoRequestFocus(true);
		frame.setAlwaysOnTop(true);
		//frame.setModal(true);
		frame.setResizable(false);
		//frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
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
	
	public void close(){
		this.frame.dispose();
	}
	

	public boolean isInverse() {
		return inverse;
	}
	

	@Override
	public void mouseClicked(MouseEvent arg0) {			

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

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if ((src instanceof JButton)) {
			if(src.equals(this.btnInverse)){
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
			} else if(src.equals(this.btnExit)){
				if(this.job.isStop()==false){
					this.job.setStop(true);			
					}
					else {				
						this.frame.dispose();
					}						
			}
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

	@Override
	public void windowClosing(WindowEvent e) {		
		if(this.job.isStop()==false){
			this.job.setStop(true);			
			}
			else {				
				this.frame.dispose();
			}
	}

}
