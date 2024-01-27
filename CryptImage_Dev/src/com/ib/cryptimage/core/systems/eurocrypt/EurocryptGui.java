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
 * 2024-01-18 Author Mannix54
 * http://ibsoftware.free.fr/cryptimage.php
 */

package com.ib.cryptimage.core.systems.eurocrypt;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.ParseException;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.text.MaskFormatter;

import com.ib.cryptimage.core.JobConfig;


public class EurocryptGui {
	
	private TitledBorder titlePanEurocrypt;	
	private JPanel panOptionsEurocrypt;
	
	private TitledBorder titleMacOptions;	
	private JPanel panRdiMac;
	private JRadioButton rdiMacCoding;

	private JRadioButton rdiMacCoding576p;
	private JRadioButton rdiMacDecoding;

	private TitledBorder titleEurocryptModes;
	private JPanel panEurocryptModes;
	private JRadioButton rdiEurocryptDisable;	
	private JRadioButton rdiEurocryptSingleCut;
	private JRadioButton rdiEurocryptDoubleCut;
	
	private EurocryptListener eurocryptListener;
	
	
	
	private JFormattedTextField txtSeedCode;
	private JLabel lblSeedCode;
	private ResourceBundle res;
	
	
	public EurocryptGui() {
		eurocryptListener = new EurocryptListener(this);
		this.res = JobConfig.getRes();
		
		EurocryptConf.setGui(this);
		
		this.createPanEurocrypt();		
	}

	private void createPanEurocrypt() {
		GridBagLayout gbl = new GridBagLayout();
		
		// global panel
		panOptionsEurocrypt = new JPanel();		
		titlePanEurocrypt = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				res.getString("panEurocrypt.global.options"));
		panOptionsEurocrypt.setBorder(titlePanEurocrypt);
		
		// MAC options
		panRdiMac = new JPanel();		
		titleMacOptions = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				res.getString("panEurocrypt.mac.options"));
		panRdiMac.setBorder(titleMacOptions);
		
		rdiMacCoding = new JRadioButton(res.getString("panEurocrypt.mac.options.encode.toMac"));
		rdiMacCoding.addActionListener(eurocryptListener);		
		rdiMacCoding.setSelected(true);
		
		rdiMacCoding576p = new JRadioButton(res.getString("panEurocrypt.mac.options.encode.decode"));
		rdiMacCoding576p.addActionListener(eurocryptListener);
		
		rdiMacDecoding = new JRadioButton(res.getString("panEurocrypt.mac.options.decode"));
		rdiMacDecoding.addActionListener(eurocryptListener);
		
		ButtonGroup btnGroupMac = new ButtonGroup();
		btnGroupMac.add(rdiMacCoding);
		btnGroupMac.add(rdiMacCoding576p);
		btnGroupMac.add(rdiMacDecoding);
		
		//Mac coding placement
		JPanel panRdiMacCoding = new JPanel();
		this.placerComposants(panRdiMacCoding,
				gbl,
				rdiMacCoding,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 0,
				1,1,
				1,1,
				1, 1,1,1);
		this.placerComposants(panRdiMacCoding,
				gbl,
				rdiMacCoding576p,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 1,
				1,1,
				1,1,
				1, 1,1,1);
		this.placerComposants(panRdiMacCoding,
				gbl,
				rdiMacDecoding,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 2,
				1,1,
				1,1,
				1, 1,1,1);
		
		
		// Mac panel placement		
		this.placerComposants(panRdiMac,
				gbl,
				panRdiMacCoding,
				GridBagConstraints.LINE_START, GridBagConstraints.NONE,
				0, 0,
				1,1,
				1,1,
				1, 1,1,1);
		
		// eurocrypt options
		panEurocryptModes = new JPanel();		
		titleEurocryptModes = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
				res.getString("panEurocrypt.global.eurocrypt.options"));
		panEurocryptModes.setBorder(titleEurocryptModes);
		
		rdiEurocryptDisable = new JRadioButton(res.getString("panEurocrypt.eurocrypt.options.disable"));
		rdiEurocryptDisable.addActionListener(eurocryptListener);		
		rdiEurocryptDisable.setSelected(false);
		
		rdiEurocryptSingleCut = new JRadioButton(res.getString("panEurocrypt.eurocrypt.options.singleCut"));
		rdiEurocryptSingleCut.addActionListener(eurocryptListener);
		rdiEurocryptSingleCut.setSelected(true);
		
		rdiEurocryptDoubleCut = new JRadioButton(res.getString("panEurocrypt.eurocrypt.options.doubleCut"));
		rdiEurocryptDoubleCut.addActionListener(eurocryptListener);
		rdiEurocryptDoubleCut.setSelected(false);		
		
		ButtonGroup btnGroupEurocrypt = new ButtonGroup();
		btnGroupEurocrypt.add(rdiEurocryptDisable);
		btnGroupEurocrypt.add(rdiEurocryptSingleCut);
		btnGroupEurocrypt.add(rdiEurocryptDoubleCut);
		
		// mask for seed code
		MaskFormatter mask;
		try {
			mask = new MaskFormatter("########");
			
			mask.setPlaceholderCharacter('0');
			mask.setValidCharacters("1234567890");
			
			txtSeedCode = new JFormattedTextField(mask);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		    
		    txtSeedCode.setColumns(8);
		    txtSeedCode.getDocument().addDocumentListener(eurocryptListener);
		    
		    txtSeedCode.setText(EurocryptConf.seedCode);
		    txtSeedCode.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);	
		    txtSeedCode.setHorizontalAlignment(JTextField.RIGHT);
		    
            lblSeedCode = new JLabel(res.getString("panEurocrypt.eurocrypt.options.seed"));
            
            JPanel panSeedCode = new JPanel();
            
            panSeedCode.add(lblSeedCode);
            panSeedCode.add(txtSeedCode);
		
		// eurocrypt modes panel placement
		this.placerComposants(panEurocryptModes,
				gbl,
				rdiEurocryptDisable,
				GridBagConstraints.LINE_START, GridBagConstraints.WEST,
				0, 0,
				1,1,
				1,1,
				1, 1,1,1);
		
		this.placerComposants(panEurocryptModes,
				gbl,
				rdiEurocryptSingleCut,
				GridBagConstraints.LINE_START, GridBagConstraints.WEST,
				0, 1,
				1,1,
				1,1,
				1, 1,1,1);
		
		this.placerComposants(panEurocryptModes,
				gbl,
				rdiEurocryptDoubleCut,
				GridBagConstraints.LINE_START, GridBagConstraints.WEST,
				0, 2,
				1,1,
				1,1,
				1, 1,1,1);
		
		this.placerComposants(panEurocryptModes,
				gbl,
				panSeedCode,
				GridBagConstraints.LINE_START, GridBagConstraints.WEST,
				0, 3,
				1,1,
				1,1,
				1, 1,1,1);
		
		// global panel placement		
		this.placerComposants(panOptionsEurocrypt,
				gbl,
				panRdiMac,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				0, 0,
				1,1,
				0.5,0.5,
				1, 1,1,1);
		
		this.placerComposants(panOptionsEurocrypt,
				gbl,
				panEurocryptModes,
				GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
				0, 1,
				1,1,
				0.5,0.5,
				1, 1,1,1);

		
	}
	
	public JPanel getPanOptionsEurocrypt() {
		return this.panOptionsEurocrypt;
	}
	
	public void refreshGui() {
		this.res = JobConfig.getRes();
		
		titlePanEurocrypt.setTitle(res.getString("panEurocrypt.global.options"));
		titleMacOptions.setTitle(res.getString("panEurocrypt.mac.options"));
		titleEurocryptModes.setTitle(res.getString("panEurocrypt.global.eurocrypt.options"));
		
		rdiMacCoding.setText(res.getString("panEurocrypt.mac.options.encode.toMac"));
		rdiMacCoding576p.setText(res.getString("panEurocrypt.mac.options.encode.decode"));
		rdiMacDecoding.setText(res.getString("panEurocrypt.mac.options.decode"));
		
		rdiEurocryptDisable.setText(res.getString("panEurocrypt.eurocrypt.options.disable"));
		rdiEurocryptSingleCut.setText(res.getString("panEurocrypt.eurocrypt.options.singleCut"));
		rdiEurocryptDoubleCut.setText(res.getString("panEurocrypt.eurocrypt.options.doubleCut"));
		lblSeedCode.setText(res.getString("panEurocrypt.eurocrypt.options.seed"));		
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
	
	public JRadioButton getRdiMacCoding() {
		return rdiMacCoding;
	}

	public JRadioButton getRdiMacCoding576p() {
		return rdiMacCoding576p;
	}

	public JRadioButton getRdiMacDecoding() {
		return rdiMacDecoding;
	}

	public JRadioButton getRdiEurocryptDisable() {
		return rdiEurocryptDisable;
	}

	public JRadioButton getRdiEurocryptSingleCut() {
		return rdiEurocryptSingleCut;
	}

	public JRadioButton getRdiEurocryptDoubleCut() {
		return rdiEurocryptDoubleCut;
	}

	public JFormattedTextField getTxtSeedCode() {
		return txtSeedCode;
	}
	
}
