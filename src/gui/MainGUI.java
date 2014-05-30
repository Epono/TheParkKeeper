package gui;

import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableCellRenderer;

import management.Map;
import management.Tri;

/** Main  GUI of the application.
 */
public class MainGUI extends JFrame implements WindowListener {
	private static final long serialVersionUID = -576898196694301064L;

	private JLabel jlTitle;
	private JButton jbNewGameMenu, jbLoadGameMenu, jbAbout, jbBackMainMenu, jbLoadGame, jbDeleteSave, jbQuit, jbEnableDisableMusic, jbEditGame, jbDeleteSavesFolder;
	private JPanel jpGlobal, jpMainMenu, jpMainMenuChoices, jpLoadGame, jpChoicesLoadGame, jpChoicesMainMenu;
	private TableModelLoadSave model;
	private JTable jtSaves;

	private AudioClip backgroundMusic;
	private boolean musicEnabled;

	private ArrayList<Map> saves;
	private boolean [] sortWay;
	
	/** Constructs the GUI.
	 * 
	 * @param title : title of the window
	 */
	public MainGUI(String title) {
		super(title);

		addWindowListener(this);

		setSize(960, 540);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setContentPane(new BackgroundImage());
		createPanelMainMenu();
		jpGlobal.add(jpMainMenu);
		getContentPane().add(jpGlobal);

		musicBackground();
		musicEnabled = true;
		InputMap inMap = jpGlobal.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inMap.put(KeyStroke.getKeyStroke("M"), "EnableDisableMusic");
		jpGlobal.getActionMap().put("EnableDisableMusic",new ActionEnableDisableMusicKeyboard());

		sortWay = new boolean [7];
		Arrays.fill(sortWay,true);

		setResizable(false);
		setVisible(true);
	}

	/** Constructs the main menu panel.
	 */
	public void createPanelMainMenu() {
		jpMainMenu = new JPanel(new BorderLayout());
		jpMainMenuChoices = new JPanel(new GridLayout(5, 1));
		jpGlobal = new JPanel();

		jlTitle = new JLabel("Park Guardian");

		jbNewGameMenu = new JButton("New Game");
		jbNewGameMenu.addActionListener(new ActionCreationPartie());

		jbLoadGameMenu = new JButton("Load Game");
		jbLoadGameMenu.addActionListener(new ActionAfficherChargerPartie());

		jbAbout = new JButton("Credits");
		jbAbout.addActionListener(new ActionAfficherAPropos());

		jbEnableDisableMusic = new JButton("Stop Music");
		jbEnableDisableMusic.addActionListener(new ActionEnableDisableMusicButton());

		jbQuit = new JButton("Exit");
		jbQuit.addActionListener(new ActionQuitter());

		jpMainMenu.add(jlTitle, BorderLayout.NORTH);

		jpMainMenu.add(jpMainMenuChoices, BorderLayout.CENTER);
		jpMainMenuChoices.add(jbNewGameMenu);
		jpMainMenuChoices.add(jbLoadGameMenu);
		jpMainMenuChoices.add(jbAbout);
		jpMainMenuChoices.add(jbQuit);
		jpMainMenuChoices.add(jbEnableDisableMusic);
	}

	/** Brings up 3 windows successively to ask the user : </br>
	 * - the creation type of the game (automatic or manual).</br>
	 * - the size of the <b>Grid</b> (between 12 included and 24 included).</br>
	 * - the name of the game. </br>
	 * and creates an instance of <b>GridGUI</b> if everything is OK.
	 */
	class ActionCreationPartie implements ActionListener {
		public void actionPerformed(ActionEvent ae) {

			String name = null;
			int dimension = -1;
			int gameType = 2;

			// choix du type
			String[] choices = { "Automatic", "Manual", "Cancel" };
			gameType = JOptionPane.showOptionDialog(getParent(),	"Choose creation type of the Grid :\n",
					"Choose creation type", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, null);

			// choix de la taille
			if (gameType == 0 || gameType == 1) {
				String strDimension;
				boolean validSize = false;
				boolean continew = true;
				do {
					strDimension = JOptionPane.showInputDialog("Enter the size of the grid :\n(Minimum size : 12)" +
							"\n(Maximum size : 24)\n(0 => Random size)\n","0");
					if (strDimension == null) {
						continew = false;
						break;
					} 
					else if (strDimension.length() == 0)
						JOptionPane.showMessageDialog(getParent(), "Please enter a size");
					else {
						try {
							dimension = Integer.parseInt(strDimension);
							if (dimension > 11 && dimension < 25)
								validSize = true;
							else if (dimension == 0) {
								Random rand = new Random();
								dimension = rand.nextInt(13) + 12;
								validSize = true;
							} else
								JOptionPane.showMessageDialog(getParent(), "The minimum size is 12x12 !\nThe maximum size is 24x24 !",
										"Sizing error",	JOptionPane.ERROR_MESSAGE);
						} 
						catch (NumberFormatException e) {
							JOptionPane.showMessageDialog(getParent(),"Please enter a valid size");
							validSize = false;
						}
					}

				} while (!validSize);

				// choix du nom
				if (continew) {
					do {
						name = JOptionPane.showInputDialog("Enter the name of the game :", "New Game");
						if (name == null) 
							break;
						if (name.length() == 0)
							JOptionPane.showMessageDialog(getParent(),"Please enter a name");
					} while (name.length() == 0);
				}

				boolean validName = false;
				try {
					FileReader fr = new FileReader("saves/" + name + ".savj");
					fr.close();
				} catch (FileNotFoundException e) {
					validName = true;
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (!validName){
					int i = 1;
					while (!validName) {
						try {
							FileReader fr = new FileReader("saves/" + name + i + ".savj");
							fr.close();
						} 
						catch (FileNotFoundException e) {
							name = name + i;
							validName = true;
						} 
						catch (IOException e) {
							e.printStackTrace();
						}
						i++;
					}
				}
			}

			if (name != null) {
				putToSleep(true);
				if (gameType == 0) 
					// creation auto true
					new GridGUI(dimension, name, true, MainGUI.this);
				else if (gameType == 1) 
					// creation semi-auto ou manuelle false
					new GridGUI(dimension, name, false, MainGUI.this);
			}
		}
	}

	/** Shows an "About" window.
	 */
	class ActionAfficherAPropos implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			JOptionPane.showMessageDialog(getParent(),"Park Guardian\n\nVersion : 1.0\nAuthors : Chaumienne Charles & Ambrois Guillaume" +
					"\nMusic and Sounds : C418 - Minecraft\nIcons and Background image : Minecraft\n ","Credits", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/** Calls <b>quitter()</b> which asks the user if he really wants to quit. 
	 */
	class ActionQuitter implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			quit();
		}
	}

	/** Constructs the loading menu panel.
	 */
	public void initFenetreChargerPartie() {
		jpLoadGame = new JPanel(new BorderLayout());
		jpChoicesMainMenu = new JPanel(new GridLayout(2,1));
		jpChoicesLoadGame = new JPanel(new GridLayout(1, 4));

		model = new TableModelLoadSave(getSaves());
		jtSaves = new JTable(model);
		centerTable();
		jtSaves.getTableHeader().addMouseListener(new ActionTri());

		jbBackMainMenu = new JButton("Cancel");
		jbBackMainMenu.addActionListener(new ActionBackToMainMenu());

		jbLoadGame = new JButton("Load");
		jbLoadGame.addActionListener(new ActionChargerSauvegardePartie());

		jbDeleteSave = new JButton("Delete");
		jbDeleteSave.addActionListener(new ActionSupprimerSauvegardePartie());

		jbEditGame = new JButton("Edit");
		jbEditGame.addActionListener(new ActionEditerSauvegardePartie());

		jbDeleteSavesFolder = new JButton("Delete Saves Folder");
		jbDeleteSavesFolder.addActionListener(new ActionDeleteFolder());

		jpChoicesLoadGame.add(jbLoadGame);
		jpChoicesLoadGame.add(jbDeleteSave);
		jpChoicesLoadGame.add(jbEditGame);
		jpChoicesLoadGame.add(jbBackMainMenu);

		jpChoicesMainMenu.add(jpChoicesLoadGame);
		jpChoicesMainMenu.add(jbDeleteSavesFolder);

		jpLoadGame.add(new JScrollPane(jtSaves), BorderLayout.CENTER);
		jpLoadGame.add(jpChoicesMainMenu, BorderLayout.SOUTH);
		jpGlobal.add(jpLoadGame);
	}

	/** Reads files contained in the saves folder and store them in a <b>Map</b> list.
	 * 
	 * @return a list of <b>Maps</b>.
	 */
	public ArrayList<Map> getSaves() {
		HashMap<String, Map> HMSaves = new HashMap<String, Map>();
		ObjectInputStream ois;
		Object objTemp = null;
		File directory = new File("saves");
		File[] list = directory.listFiles();
		for (int i = 0; i < list.length; i++) {
			if (list[i].getName().endsWith(".savj"))
				try {
					ois = new ObjectInputStream(new FileInputStream("saves/" + list[i].getName()));
					try {
						objTemp = ois.readObject();
					} 
					catch (ClassCastException cce) {
						File save = new File("saves/" + list[i].getName() + ".savj");
						save.delete();
					} 
					catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					if (objTemp.getClass() == Map.class) {
						Map mapTemp = (Map) objTemp;
						HMSaves.put(mapTemp.getName(), mapTemp);
					}
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		saves = new ArrayList<Map>(HMSaves.values());
		return saves;
	}

	/** Refreshes the table with the given <b>Map</b> list.
	 * @param saves : a list of <b>Maps</b>.
	 */
	private void updateTable(ArrayList<Map> saves) {
		model.setData(saves);
	}
	
	/** Centers the elements in the <b>Map</b> table.
	 */
	private void centerTable() {
		DefaultTableCellRenderer custom = new DefaultTableCellRenderer(); 
		custom.setHorizontalAlignment(JLabel.CENTER); 
		for (int i=2 ; i<jtSaves.getColumnCount() ; i++) {
			jtSaves.getColumnModel().getColumn(i).setCellRenderer(custom); 
		}
	}

	/** Shows the load menu.</br>
	 * If the saves folder doesn't exist, it's created.
	 */
	class ActionAfficherChargerPartie implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			File repertoire = new File("saves");
			File[] list = repertoire.listFiles();
			if (list == null)
				new File("saves").mkdir();
			jpGlobal.removeAll();
			initFenetreChargerPartie();
			jpGlobal.updateUI();
		}
	}

	/** Deletes the selected save.
	 */
	class ActionSupprimerSauvegardePartie implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			int ligne = jtSaves.getSelectedRow();
			if (ligne != -1) {
				int aSupprimer = JOptionPane.showConfirmDialog(getParent(),	"Confirm deleting the save :\n"	+ saves.get(ligne),
						"Deleting the save", JOptionPane.OK_CANCEL_OPTION);
				if (aSupprimer == 0) {
					new File("saves/" + saves.get(ligne).getName() + ".savj").delete();
					updateTable(getSaves());
				}
			}
		}
	}

	/** Deletes the saves folder.
	 */
	class ActionDeleteFolder implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			int aSupprimer = JOptionPane.showConfirmDialog(getParent(),	"Confirm deleting the save folder ?" , "Deleting Saves Folder", JOptionPane.OK_CANCEL_OPTION);
			if(aSupprimer == 0) {
				ArrayList<Map> delete = getSaves();
				for(Map map : delete) {
					new File("saves/" + map.getName()+".savj").delete();
				}
				new File("saves").delete();
				backToMainMenu();
			}
		}
	}

	/** Loads the selected save.
	 */
	class ActionChargerSauvegardePartie implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			int ligne = jtSaves.getSelectedRow();
			if (ligne != -1) {
				int aCharger = JOptionPane.showConfirmDialog(getParent(),"Confirm the loading of the save :\n" +
						saves.get(ligne), "Loading of the save", JOptionPane.OK_CANCEL_OPTION);
				if (aCharger == 0) {
					backToMainMenu();
					putToSleep(true);
					new GridGUI(saves.get(ligne), MainGUI.this, false);
				}
			}
		}
	}

	/** Loads the selected save in editor mode.
	 */
	class ActionEditerSauvegardePartie implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			int ligne = jtSaves.getSelectedRow();
			if (ligne != -1) {
				int aCharger = JOptionPane.showConfirmDialog(getParent(), "Confirm the loading of the save for edition :\n"
						+ saves.get(ligne), "Editing of the save", JOptionPane.OK_CANCEL_OPTION);
				if (aCharger == 0) {
					backToMainMenu();
					putToSleep(true);
					new GridGUI(saves.get(ligne), MainGUI.this, true);
				}
			}
		}
	}

	/**Sorts the table of <b>Maps</b> depending on which header the users clicked.</br>
	 * Sorts in reverse order if the users clicks on the same header.
	 */
	class ActionTri extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			int column = jtSaves.columnAtPoint(e.getPoint());
			ArrayList <Map> unsortedList = saves;
			ArrayList <Map> sortedList;

			boolean sens = sortWay[column];
			switch (column) {
			case 0:
				sortedList = Tri.sortByName(unsortedList, sens);
				updateTable(sortedList);
				break;
			case 1:
				sortedList = Tri.sortByDate(unsortedList, sens);
				updateTable(sortedList);
				break;
			case 2:
				sortedList = Tri.sortByTurnsNumber(unsortedList, sens);
				updateTable(sortedList);
				break;
			case 3:
				sortedList = Tri.sortByDuration(unsortedList, sens);
				updateTable(sortedList);
				break;
			default:
				throw new IllegalArgumentException();
			}

			saves = sortedList;

			Arrays.fill(sortWay,true);
			sortWay[column] = sens^true;
			updateTable(saves);
		}
	}

	/** Calls the <b>backToMainMenu</b> method to show the main menu.
	 */
	class ActionBackToMainMenu implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			backToMainMenu();
		}
	}

	/** Shows the main menu.
	 */
	public void backToMainMenu() {
		jpGlobal.removeAll();
		jpGlobal.add(jpMainMenu);
		setSize(960, 540);
		jpGlobal.updateUI();
	}

	/** Creates the AudioClip of the background music and starts it.
	 */
	public void musicBackground() {
		backgroundMusic = java.applet.Applet.newAudioClip(getClass().getResource("/sons/Background.wav"));
		backgroundMusic.loop();
		musicEnabled = true;
	}

	/** Calls the <b>EnableDisableMusic()</b> method to reverse the state of the background music.</br>
	 * Associated with a button.
	 */
	class ActionEnableDisableMusicButton implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			EnableDisableMusic();
		}
	}

	/** Calls the <b>EnableDisableMusic()</b> method to reverse the state of the background music.</br>
	 * Associated with an inputMap.
	 */
	class ActionEnableDisableMusicKeyboard extends AbstractAction {
		private static final long serialVersionUID = 2637064803335193267L;
		public void actionPerformed(ActionEvent e) {
			EnableDisableMusic();
		}
	}

	/** Reverse the state of the background music and refreshes the button label.
	 */
	public void EnableDisableMusic() {
		if (musicEnabled) {
			backgroundMusic.stop();
			jbEnableDisableMusic.setText("Play Music");
		} else {
			backgroundMusic.loop();
			jbEnableDisableMusic.setText("Stop Music");
		}
		musicEnabled = musicEnabled ^ true;
	}

	/** Shows the background image.
	 */
	class BackgroundImage extends JPanel {
		private static final long serialVersionUID = 3398376666866798977L;
		Image backgroundImage;

		BackgroundImage() {
			backgroundImage = getToolkit().getImage(getClass().getResource("/images/parc.jpg"));
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
		}
	}

	/** Calls <b>quit()</b> if the user tries to close the application.
	 * @param e : the <b>WindowEvent</b>
	 */
	public void windowClosing(WindowEvent e) {
		quit();
	}

	/** Shows a windows asking confirmation to the user before exiting.
	 */
	public void quit() {
		int reponse = JOptionPane.showOptionDialog(getParent(),"Do you really want to quit this fantastic game ?\n",
				"Exit", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE, null, null, null);
		if (reponse == 0) {
			backgroundMusic.stop();
			System.exit(0);
		}
	}

	/** Wakes up (<b>setVisible(true)</b> and <b>setEnabled(true)</b>) the MainGUI frame if <b>putToSleep</b> is <b>false</b>,
	 *  put it to sleep (<b>setVisible(false)</b> and <b>setEnabled(false)</b>) otherwise.
	 * 
	 * @param putToSleep : <b>Boolean</b> indicating whether waking up (<code>false</code>) the MainGUI frame or putting it to sleep (<code>true</code>).
	 */
	public void putToSleep(boolean putToSleep) {
		setVisible(!putToSleep);
		setEnabled(!putToSleep);
	}

	public void windowOpened(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
}