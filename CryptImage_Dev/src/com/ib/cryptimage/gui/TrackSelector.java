package com.ib.cryptimage.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.text.MaskFormatter;

import com.ib.cryptimage.core.JobConfig;
import com.ib.cryptimage.core.StreamTrack;

public class TrackSelector extends JDialog {
	private static final long serialVersionUID = -4376588786721498267L;
	private Vector<StreamTrack> videoTracks;
	private Vector<StreamTrack> audioTracks;
	
	JComboBox<String> comboTrackVideos;
	JComboBox<String> comboTrackAudios;
	

	public TrackSelector(JFrame parent, String title, 
			Vector<StreamTrack> videoTracks, Vector<StreamTrack> audioTracks, boolean modal) {
		super(parent, title, modal);
		this.setSize(600, 300);
		this.setLocationRelativeTo(null);
		
		this.videoTracks = videoTracks;
		this.audioTracks = audioTracks;		

		initGUI();

		this.setResizable(false);
	}

	public void display() {
		this.setVisible(true);
	}

	private void initGUI() {

		JPanel panGlobal = new JPanel();
		panGlobal.setLayout(new BoxLayout(panGlobal, BoxLayout.Y_AXIS));
		
		JLabel lblVideoTrack = new JLabel(JobConfig.getRes().getString("trackSelector.label.video"));
		
		String[] labelsVideos = new String[videoTracks.size()];
		
		int count = 0;
		for(StreamTrack videoTrack : videoTracks) {
			labelsVideos[count] = JobConfig.getRes().getString("trackSelector.label.track") + " " + (count + 1) + " : " + videoTrack.getCodecLongName();
			count++;
		}
		
		comboTrackVideos = new JComboBox<String>(labelsVideos);
		
		JLabel lblAudioTrack = new JLabel(JobConfig.getRes().getString("trackSelector.label.audio"));
		
		String[] labelsAudios = new String[audioTracks.size()];
		
		count = 0;
		for(StreamTrack audioTrack : audioTracks) {
			labelsAudios[count] = JobConfig.getRes().getString("trackSelector.label.track") + " " + (count + 1) + " : " + audioTrack.getCodecLongName();
			count++;
		}
		
		comboTrackAudios = new JComboBox<String>(labelsAudios);
		
		JPanel panTracksVideo = new JPanel();		
		panTracksVideo.add(lblVideoTrack);
		panTracksVideo.add(comboTrackVideos);
		
		JPanel panTracksAudio = new JPanel();
		panTracksAudio.add(lblAudioTrack);
		panTracksAudio.add(comboTrackAudios);


		JPanel panBtn = new JPanel();
		panBtn.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		JButton btnValid = new JButton(JobConfig.getRes().getString("genEncFile.btnValid"));
		btnValid.setIcon(new ImageIcon(this.getClass().getResource("/icons/apply.png")));
		btnValid.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {				
				JobConfig.setVideoTrackInfos(JobConfig.getStreamTracksVideo().elementAt(comboTrackVideos.getSelectedIndex()));
				JobConfig.setAudioTrackInfos(JobConfig.getStreamTracksAudio().elementAt(comboTrackAudios.getSelectedIndex()));
				
				JobConfig.setDefaultIdAudioTrack(JobConfig.getStreamTracksAudio().elementAt(comboTrackAudios.getSelectedIndex()).getId());
				JobConfig.setDefaultIdVideoTrack(JobConfig.getStreamTracksVideo().elementAt(comboTrackVideos.getSelectedIndex()).getId());
			
				dispose();				
			}
		});		
		
		
		panBtn.add(btnValid);
		
		panGlobal.add(panTracksVideo);
		panGlobal.add(panTracksAudio);
		panGlobal.add(panBtn);
		
		this.getContentPane().add(panGlobal, BorderLayout.CENTER);

	}

}
