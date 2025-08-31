package com.ib.cryptimage.core.systems.luxcrypt;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.ParseException;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.text.MaskFormatter;

import com.ib.cryptimage.core.JobConfig;
import com.ib.cryptimage.core.RangeSlider;
import com.ib.cryptimage.core.systems.eurocrypt.EurocryptConf;


public class LuxcryptGui {
	private TitledBorder titlePanLuxcrypt;	
	private JPanel panOptionsLuxcrypt;
	
	private TitledBorder titleLuxSynchroModes;
	private JPanel panLuxcryptSynchroModes;
	
	private TitledBorder titleLuxScreenOptions;
	private JPanel panLuxcryptScreenOptions;
	
	private LuxcryptListener luxcryptListener;	
	
	private ResourceBundle res;
	
	private RangeSlider rangeSlider;
	private JLabel lblRangeSlider;
    private JTextField txtValueMinRangeSlider;
    private JTextField txtValueMaxRangeSlider;
    
    private JCheckBox chkDisableHorSync;
    private JCheckBox chkDisableVerSync;
    private JCheckBox chkInverse;

	public LuxcryptGui() {
		luxcryptListener = new LuxcryptListener(this);
		this.res = JobConfig.getRes();
		
		LuxcryptConf.setGui(this);
		
		this.createPanLuxcrypt();		
	}
    
	private void createPanLuxcrypt() {
		GridBagLayout gbl = new GridBagLayout();
		
		// global panel
		panOptionsLuxcrypt = new JPanel();		
		titlePanLuxcrypt = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				res.getString("panLuxcrypt.global.options"));
		panOptionsLuxcrypt.setBorder(titlePanLuxcrypt);

		
		// luxcrypt synchro options
		panLuxcryptSynchroModes = new JPanel();		
		titleLuxSynchroModes = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				res.getString("panLuxcrypt.global.synchro.options"));
		panLuxcryptSynchroModes.setBorder(titleLuxSynchroModes);
		
		// luxcrypt screen options
		panLuxcryptScreenOptions = new JPanel();		
		titleLuxScreenOptions = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				res.getString("panLuxcrypt.screen.options"));
		panLuxcryptScreenOptions.setBorder(titleLuxScreenOptions);
		
		chkDisableHorSync = new JCheckBox(res.getString("panLuxcrypt.global.synchro.options.hor"));
		chkDisableHorSync.setSelected(true);
		chkDisableHorSync.addActionListener(luxcryptListener);
		LuxcryptConf.isDisableHorSync = true;
		
		chkDisableVerSync = new JCheckBox(res.getString("panLuxcrypt.global.synchro.options.ver"));
		chkDisableVerSync.addActionListener(luxcryptListener);
		chkDisableVerSync.setSelected(true);
		LuxcryptConf.isDisableVerSync = true;
		
		chkInverse = new JCheckBox(res.getString("panLuxcrypt.global.options.inverse"));
		chkInverse.addActionListener(luxcryptListener);
		chkInverse.setSelected(false);	
		LuxcryptConf.isInverse = false;
            
		// JRangeSlider
		rangeSlider = new RangeSlider();
		rangeSlider.setMinimum(1);
		rangeSlider.setMaximum(200000);
		rangeSlider.setValue(1);
		rangeSlider.setUpperValue(200000);
		// rangeSlider.setPreferredSize(new Dimension(290,
		// rangeSlider.getPreferredSize().height));

		lblRangeSlider = new JLabel(res.getString("rangeSlider.lblFrameStart"));

		txtValueMinRangeSlider = new JTextField(12);
		txtValueMinRangeSlider.setEditable(false);
		txtValueMinRangeSlider.setHorizontalAlignment(JLabel.LEFT);
		txtValueMaxRangeSlider = new JTextField(12);
		txtValueMaxRangeSlider.setEditable(false);

		String time = getTime((int) (rangeSlider.getValue() / JobConfig.getFrameRate()));
		txtValueMinRangeSlider.setText(rangeSlider.getValue() + " (" + time + ")");

		time = getTime((int) (rangeSlider.getUpperValue() / JobConfig.getFrameRate()));
		txtValueMaxRangeSlider.setText(rangeSlider.getUpperValue() + " (" + time + ")");

		rangeSlider.addChangeListener(luxcryptListener);
            
        // rangeslider
        JPanel panRangeSlider = new JPanel();            

        this.placerComposants(panRangeSlider,
        		gbl,
        		lblRangeSlider,
        		GridBagConstraints.WEST, GridBagConstraints.EAST,
        		0, 0,
        		1,1,
        		0.1,1,
        		1, 1,1,1);
        this.placerComposants(panRangeSlider,
        		gbl,
        		txtValueMinRangeSlider,
        		GridBagConstraints.BASELINE_TRAILING, GridBagConstraints.NONE,
        		0, 1,
        		1,1,
        		0.1,1,
        		1, 1,1,1);
        this.placerComposants(panRangeSlider,
        		gbl,
        		rangeSlider,
        		GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
        		1, 1,
        		1,1,
        		0.8,1,
        		1, 1,1,1);

        this.placerComposants(panRangeSlider,
        		gbl,
        		txtValueMaxRangeSlider,
        		GridBagConstraints.WEST, GridBagConstraints.NONE,
        		2, 1,
        		1,1,
        		0.1,1,
        		1, 1,1,1);                    		

        // 	panLuxcryptSynchroModes	
		 this.placerComposants(panLuxcryptSynchroModes, 
				 gbl, 
				 chkDisableHorSync,
		         GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
		         0, 0, 
		         1,1, 
		         1,1,
		         1,1,
		         1,1);
		 this.placerComposants(panLuxcryptSynchroModes, 
				 gbl, 
				 chkDisableVerSync,
		         GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
		         0, 1, 
		         1,1, 
		         1,1,
		         1,1,
		         1,1);		 
		 
		 // panLuxcryptScreenOptions
		 this.placerComposants(panLuxcryptScreenOptions, 
				 gbl, 
				 getChkInverse(),
		         GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
		         0, 0, 
		         1,1, 
		         1,1,
		         1,1,
		         1,1);

        // global panel placement    		
        this.placerComposants(panOptionsLuxcrypt,
        		gbl,
        		panLuxcryptSynchroModes,
        		GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
        		0, 0,
        		1,1,
        		0.5,0.5,
        		1, 1,1,1);
		 this.placerComposants(panOptionsLuxcrypt, 
			    gbl, 
			    panLuxcryptScreenOptions,
		        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
		        0, 1, 
		        1,1, 
		        1,1,
		        1,1,
		        1,1);
        this.placerComposants(panOptionsLuxcrypt,
        		gbl,
        		panRangeSlider,
        		GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
        		0, 2,
        		1,1,
        		0.5,0.5,
        		1, 1,1,1);
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
	
	
	public JPanel getPanOptionsLuxcrypt() {
		return this.panOptionsLuxcrypt;
	}
	
	public void refreshGui() {
		this.res = JobConfig.getRes();
		
		titlePanLuxcrypt.setTitle(res.getString("panLuxcrypt.global.options"));
		titleLuxSynchroModes.setTitle(res.getString("panLuxcrypt.global.synchro.options"));
		titleLuxScreenOptions.setTitle(res.getString("panLuxcrypt.screen.options"));
		
		chkDisableHorSync.setText(res.getString("panLuxcrypt.global.synchro.options.hor"));
		chkDisableVerSync.setText(res.getString("panLuxcrypt.global.synchro.options.ver"));
		chkInverse.setText(res.getString("panLuxcrypt.global.options.inverse"));

		lblRangeSlider.setText(res.getString("rangeSlider.lblFrameStart"));
	}
	
	public void refreshSlider() {		
        rangeSlider.setMinimum(LuxcryptConf.frameStart);
        rangeSlider.setMaximum(LuxcryptConf.frameEnd);
        rangeSlider.setValue(LuxcryptConf.frameStart);
        rangeSlider.setUpperValue(LuxcryptConf.frameEnd);
        
        LuxcryptConf.selectedFrameStart = LuxcryptConf.frameStart;
        LuxcryptConf.selectedFrameEnd = LuxcryptConf.frameEnd;
	}
	
    public RangeSlider getRangeSlider() {
		return rangeSlider;
	}

	public JTextField getTxtValueMinRangeSlider() {
		return txtValueMinRangeSlider;
	}

	public JTextField getTxtValueMaxRangeSlider() {
		return txtValueMaxRangeSlider;
	}
	
	private String getTime(int timer) {
		int hours = timer / 3600; // get the amount of hours from the seconds
		int remainder = timer % 3600; // get the rest in seconds
		int minutes = remainder / 60; // get the amount of minutes from the rest
		int seconds = remainder % 60; // get the new rest
		String disHour = (hours < 10 ? "0" : "") + hours; // get hours and add "0" before if lower than 10
		String disMinu = (minutes < 10 ? "0" : "") + minutes; // get minutes and add "0" before if lower than 10
		String disSec = (seconds < 10 ? "0" : "") + seconds; // get seconds and add "0" before if lower than 10
		String formattedTime = disHour + ":" + disMinu + ":" + disSec; // get the whole time
		return formattedTime;
	}

	public JCheckBox getChkDisableHorSync() {
		return chkDisableHorSync;
	}

	public JCheckBox getChkDisableVerSync() {
		return chkDisableVerSync;
	}

	public JCheckBox getChkInverse() {
		return chkInverse;
	}
}
