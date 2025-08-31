package com.ib.cryptimage.core.systems.discret12;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.ib.cryptimage.core.JobConfig;
import com.ib.cryptimage.core.RangeSlider;

public class Discret12Gui {
	
	private TitledBorder titlePanDiscret12;	
	private JPanel panOptionsDiscret12;
	
	private TitledBorder titleDiscret12Options;	
	private JPanel panRdiDiscret12CodingMode;
	
	private JRadioButton rdiDiscret12Coding;
	private JRadioButton rdiDiscret12Decoding;


	private Discret12Listener discret12Listener;	
	
	private ResourceBundle res;
	
	private RangeSlider rangeSlider;
	private JLabel lblRangeSlider;
	
    private JTextField txtValueMinRangeSlider;
    private JTextField txtValueMaxRangeSlider;
	
	public Discret12Gui() {
		discret12Listener = new Discret12Listener(this); 
		this.res = JobConfig.getRes();
		
		Discret12Conf.setGui(this);
		
		this.createPanDiscret12();		
	}

	private void createPanDiscret12() {
		GridBagLayout gbl = new GridBagLayout();
		
		// global panel
		panOptionsDiscret12 = new JPanel();		
		titlePanDiscret12 = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				res.getString("panDiscret12.global.options"));
		panOptionsDiscret12.setBorder(titlePanDiscret12);

		
		// discret12 coding options
		panRdiDiscret12CodingMode = new JPanel();		
		titleDiscret12Options = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				res.getString("panDiscret12.coding.options"));
		panRdiDiscret12CodingMode.setBorder(titleDiscret12Options);
		
		rdiDiscret12Coding = new JRadioButton(res.getString("panDiscret12.encode"));
		rdiDiscret12Coding.addActionListener(discret12Listener);
		rdiDiscret12Coding.setSelected(true);
		
		rdiDiscret12Decoding = new JRadioButton(res.getString("panDiscret12.decode"));
		rdiDiscret12Decoding.addActionListener(discret12Listener);
        
		ButtonGroup btnGroup = new ButtonGroup();
		btnGroup.add(rdiDiscret12Coding);
		btnGroup.add(rdiDiscret12Decoding);

		
		//Discret12 coding placement
		this.placerComposants(panRdiDiscret12CodingMode,
				gbl,
				rdiDiscret12Coding,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 0,
				1,1,
				1,1,
				1, 1,1,1);
		this.placerComposants(panRdiDiscret12CodingMode,
				gbl,
				rdiDiscret12Decoding,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				1, 0,
				1,1,
				1,1,
				1, 1,1,1);


            
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

		rangeSlider.addChangeListener(discret12Listener);
            
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


        // global panel placement    		
        this.placerComposants(panOptionsDiscret12,
        		gbl,
        		panRdiDiscret12CodingMode,
        		GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
        		0, 0,
        		1,1,
        		0.5,0.5,
        		1, 1,1,1);
        this.placerComposants(panOptionsDiscret12,
        		gbl,
        		panRangeSlider,
        		GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
        		0, 1,
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
	
	public JPanel getPanOptionsDiscret12() {
		return this.panOptionsDiscret12;
	}
	
	public void refreshGui() {
		this.res = JobConfig.getRes();
		
		titlePanDiscret12.setTitle(res.getString("panDiscret12.global.options"));
		titleDiscret12Options.setTitle(res.getString("panDiscret12.coding.options"));
		
		rdiDiscret12Coding.setText(res.getString("panDiscret12.encode"));
		rdiDiscret12Decoding.setText(res.getString("panDiscret12.decode"));	


		lblRangeSlider.setText(res.getString("rangeSlider.lblFrameStart"));
	}
	
	public void refreshSlider() {		
        rangeSlider.setMinimum(Discret12Conf.frameStart);
        rangeSlider.setMaximum(Discret12Conf.frameEnd);
        rangeSlider.setValue(Discret12Conf.frameStart);
        rangeSlider.setUpperValue(Discret12Conf.frameEnd);
        
        Discret12Conf.selectedFrameStart = Discret12Conf.frameStart;
        Discret12Conf.selectedFrameEnd = Discret12Conf.frameEnd;
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

	public JRadioButton getRdiDiscret12Coding() {
		return rdiDiscret12Coding;
	}

	public void setRdiDiscret12Coding(JRadioButton rdiDiscret12Coding) {
		this.rdiDiscret12Coding = rdiDiscret12Coding;
	}

	public JRadioButton getRdiDiscret12Decoding() {
		return rdiDiscret12Decoding;
	}

	public void setRdiDiscret12Decoding(JRadioButton rdiDiscret12Decoding) {
		this.rdiDiscret12Decoding = rdiDiscret12Decoding;
	}
	
}
