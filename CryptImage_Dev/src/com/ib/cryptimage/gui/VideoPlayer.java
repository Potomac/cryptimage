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
 * http://ibsoftware.free.fr/cryptimage.php
 */



package com.ib.cryptimage.gui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;

import com.ib.cryptimage.core.JobConfig;
//import com.xuggle.xuggler.IAudioSamples;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;

//import javax.sound.sampled.AudioFormat;
//import javax.sound.sampled.AudioSystem;
//import javax.sound.sampled.DataLine;
//import javax.sound.sampled.LineUnavailableException;
//import javax.sound.sampled.SourceDataLine;


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
	
	//private SourceDataLine mLine;
	

	public VideoPlayer(double frameRate) {		
				
//		if (JobConfig.isWantSound()){
//			openJavaSound();
//		}
		
		frame = new JDialog();	
		frame.addWindowListener(this);
			
		
		
		this.frame.setLayout(new BorderLayout());
		frame.setIconImage(new ImageIcon(this.getClass().getResource("/icons/logo_jframe.png")).getImage());
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
				Thread.sleep((long) time  );
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}							
		
		this.systemPreviousCurrentTime = System.currentTimeMillis();
	}
	
	
	public void close(){
		this.frame.dispose();
	}
	
//	private void openJavaSound()
//	  {	    
//	    AudioFormat audioFormat = new AudioFormat(48000, 16, 2, true, false);
//	    DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
//	    try
//	    {
//	      mLine = (SourceDataLine) AudioSystem.getLine(info);
//	      /**
//	       * if that succeeded, try opening the line.
//	       */
//	      mLine.open(audioFormat);
//	      /**
//	       * And if that succeed, start the line.
//	       */
//	      mLine.start();
//	    }
//	    catch (LineUnavailableException e)
//	    {
//	      throw new RuntimeException("could not open audio line");
//	    }	    
//	  }
	
//	public void playJavaSound(IAudioSamples aSamples)
//	  {
//	    /**
//	     * We're just going to dump all the samples into the line.
//	     */
//	    byte[] rawBytes = aSamples.getData().getByteArray(0, aSamples.getSize());	 
//	    
//	    mLine.write(rawBytes, 0, aSamples.getSize());
//	  }
//
//	  public void closeJavaSound()
//	  {
//	    if (mLine != null)
//	    {
//	      /*
//	       * Wait for the line to finish playing
//	       */
//	      mLine.drain();
//	      /*
//	       * Close the line.
//	       */
//	      mLine.close();
//	      mLine=null;
//	    }
//	  }
	

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
				if(JobConfig.isStop()==false){
					JobConfig.setStop(true);			
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
		if(JobConfig.isStop()==false){
			JobConfig.setStop(true);			
			}
			else {				
				this.frame.dispose();
			}
	}

}
