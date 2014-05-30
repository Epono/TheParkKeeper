package gui;

import individual.Displacement;
import individual.Guardian;
import individual.Individual;
import individual.Intrude;

import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.DateFormat;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import management.Manager;
import management.Map;
import environnement.Coordinate;
import environnement.Element;
import environnement.ElementType;
import environnement.Grid;
import environnement.Wall;
import environnement.Water;

/** GUI of a game.
 */
public class GridGUI extends JFrame implements WindowListener {
	private static final long serialVersionUID = -552754466020918535L;

	private JPanel jpGrid, jpGlobal, jpGame, jpCreationPanel, jpGamePanel, jpChoicesTree, jpChoicesWall, jpChoicesWater, jpMove, jpAdvancedMove, jpGameDatas;
	private JButton jbSave, jbResetGrid, jbValidate, jbNorth, jbSouth, jbEast, jbWest, jbPass, jbAutoMove, jbPause;
	private JToggleButton jtbWall, jtbTree, jtbGrass, jtbWater, jtbGuardian, jtbIntrude;
	private JRadioButton jrbTreePunctual, jrbWaterPunctual, jrbWallPunctual, jrbTreeAuto, jrbWaterAuto, jrbWallAuto;
	private NoneSelectedButtonGroup nsbgElements;
	private ButtonGroup bgTree, bgWater, bgWall;
	private JCheckBox jcbManualGuardian, jcbDynamicIntrude;
	private JLabel jlGameName, jlCreationDate, jlDuration, jlNumberOfTurns;
	private JSlider jsGrass;

	private MainGUI guiParent;
	private MouseListener mlPlaceElements;
	private Manager manager;
	private ImageBank imageBank;
	private final Object lock = new Object();
	private AudioClip footstep;
	private boolean footstepSoundEnabled;

	/** Constructs the game frame for a new game.
	 * 
	 * @param dimension : the size of the <b>Grid</b>.
	 * @param name : name of the game.
	 * @param automaticallyGenerated : automatically creates a new <b>Grid</b> randomly generated if <code>true</code>, shows the creation panel otherwise.
	 */
	public GridGUI(int dimension, String name, boolean automaticallyGenerated, MainGUI mainGUI) {
		manager = new Manager(dimension, name);

		initGUI(dimension, mainGUI);

		manager.getGrid().initGrid();
		refreshGridManualCreate();
		if (automaticallyGenerated) {
			manager.getGrid().initAutoGrid();
			manager.initAutoIndividuals();
			manager.initPatrouille();
			manager.saveMap();
			initGamePanel();
			new gameManager().start();
			manager.setStartingTime(System.currentTimeMillis());
		} else
			initManualCreatePanel(automaticallyGenerated);
		isActive();

		setVisible(true);
		setResizable(false);
	}

	/** Constructs the game from an existing save (<b>Map</b>).
	 * 
	 * @param map : the <b>Map</b> to load.
	 */
	public GridGUI(Map map, MainGUI mainGUI, boolean editing) {
		manager = new Manager(map);
		int dimension = map.getGrid().getDimension();

		initGUI(dimension, mainGUI);

		if (editing) {
			initManualCreatePanel(editing);
			refreshGridManualCreate();
		}
		else {
			initGamePanel();
			new gameManager().start();
			manager.setStartingTime(System.currentTimeMillis());
		}

		setVisible(true);
		setResizable(false);
		pack();
	}

	/** Initializes the GUI
	 * 
	 * @param dimension : the size of the <b>Grid</b>.
	 * @param parent : reference towards <b>MainGUI</b> in order to redisplay it after the game is finished/closed.
	 */
	public void initGUI(int dimension, MainGUI parent) {
		this.guiParent = parent;
		imageBank = new ImageBank();

		addWindowListener(this);

		setTitle("Park Guardian");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setContentPane(new BackgroundImage());
		jpGlobal = new JPanel();
		getContentPane().add(jpGlobal);

		InputMap inMap = jpGlobal.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inMap.put(KeyStroke.getKeyStroke("M"), "EnableDisableMusic");
		jpGlobal.getActionMap().put("EnableDisableMusic",new ActionEnableDisableMusic());

		footstep = java.applet.Applet.newAudioClip(getClass().getResource("/sons/grass1.wav"));
		footstepSoundEnabled = true;

		createPanelGrid(dimension);
	}

	/** Thread managing the turn-by-turn game.
	 */
	class gameManager extends Thread {
		public void run() {
			ArrayList<Intrude> intrudes = manager.getIntrudes();
			ArrayList<Individual> individuals = manager.getIndividuals();
			ArrayList<Guardian> guardians = manager.getGuardians();
			new updateDuree().start();
			JOptionPane.showMessageDialog(getParent(), "Have Fun !");
			while (intrudes.size() > 0) {
				if (manager.isPlaying()) {
					individuals.clear();
					individuals.addAll(guardians);
					individuals.addAll(intrudes);
					for (Individual indiTemp : individuals) {
						indiTemp.setPlayed(false);
					}
					for (int i = 0; i < individuals.size(); i++) {
						int numberOfIntrudes = intrudes.size();
						Individual indiTemp = individuals.get(i);
						if (!indiTemp.hasPlayed()) {
							if (indiTemp.getElementType() == ElementType.GUARDIAN) {
								Guardian guardTemp = (Guardian) indiTemp;
								if (guardTemp.isControled()) {
									refreshMovingGuardian(manager.calculVisualFieldAndDestinations(), guardTemp.getPosition());
									manager.setAuthorizedMovement(true);
									manager.setGuardianMoving(guardTemp);
									synchronized (lock) {
										try {
											while (!guardTemp.hasPlayed()) {
												if(!guardTemp.isControled()) 
													autoMove();
												else
													lock.wait();
											}
											playFootstep();
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
									}
									manager.setAuthorizedMovement(false);
									manager.setGuardianMoving(null);
									if (individuals.size() != numberOfIntrudes) {
										if(intrudes.size() == 0) {
											refreshGridVisualField(manager.calculVisualFieldAndDestinations());
											refreshPanelInfos();
											break;
										}
										else
											i--;
									}
								} 
								else {
									manager.autoMove(guardTemp);
								}
							} 
							else {
								Intrude intruTemp = (Intrude) indiTemp;
								if (intruTemp.isDynamic()) {
									manager.moveIndividual(intruTemp, manager.getAi().intrudeDisplacementCalculation(intruTemp));
								}
							}

							indiTemp.setPlayed(true);

							refreshGridVisualField(manager.calculVisualFieldAndDestinations());
							refreshPanelInfos();


							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					manager.setNumberOfTurns(manager.getNumberOfTurns()+1);
				}
			}
			JOptionPane.showMessageDialog(getParent(), "Congratulations, you've caught all the intrudes !\n Click OK or close this window to return to the main menu.");
			guiParent.putToSleep(false);
			dispose();
		}
	}

	/** Updates the duration of the game every secondes, and refreshes the informations panel.
	 */
	class updateDuree extends Thread {
		public void run() {
			new Timer(1000, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					manager.updateDuree();
					refreshPanelInfos();
				}
			}).start();
		}
	}

	/** Constructs the panel containing the GUI of the <b>Grid</b>.
	 * 
	 * @param dimension : the size of the <b>Grid</b>.
	 */
	public void createPanelGrid(int dimension) {
		jpGrid = new JPanel(new GridLayout(dimension, dimension, -1, -1));
		jpGrid.addMouseListener(new ActionDetectClic());
	}

	/** Constructs the panel containing datas about the game and buttons to move, pause, pass.
	 */
	public void createPanelInfos() {
		jpGamePanel = new JPanel(new GridLayout(4, 1));
		jpGameDatas = new JPanel(new GridLayout(4, 1));
		jpMove = new JPanel(new GridLayout(2, 3));
		jpAdvancedMove = new JPanel(new GridLayout(3, 1));

		jlGameName = new JLabel("Game name : " + manager.getName());
		jlCreationDate = new JLabel("Creation date : " + DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT).format(manager.getCreationDate()));
		jlNumberOfTurns = new JLabel("Number of turns : " + manager.getNumberOfTurns());
		jlDuration = new JLabel("Duration : " + DateFormat.getTimeInstance().format(manager.getDuration() - 3600000));

		jpGameDatas.add(jlGameName);
		jpGameDatas.add(jlNumberOfTurns);
		jpGameDatas.add(jlCreationDate);
		jpGameDatas.add(jlDuration);

		jbSouth = new JButton("South");
		jbNorth = new JButton("North");
		jbWest = new JButton("West");
		jbEast = new JButton("East");

		jpMove.add(new JPanel());
		jpMove.add(jbNorth);
		jpMove.add(new JPanel());
		jpMove.add(jbWest);
		jpMove.add(jbSouth);
		jpMove.add(jbEast);

		jbPass = new JButton("Skip turn");
		jbAutoMove = new JButton("Automatic movement");
		jpAdvancedMove.add(jbPass);
		jpAdvancedMove.add(jbAutoMove);

		jbPause = new JButton("Pause");
		jbPause.addActionListener(new ActionPause());
		jpAdvancedMove.add(jbPause);

		jbSouth.addActionListener(new ActionButtonMoveSouth());
		jbNorth.addActionListener(new ActionButtonMoveNorth());
		jbWest.addActionListener(new ActionButtonMoveWest());
		jbEast.addActionListener(new ActionButtonMoveEast());
		jbPass.addActionListener(new ActionButtonPass());
		jbAutoMove.addActionListener(new ActionAutoMove());

		jpGamePanel.add(jpGameDatas);
		jpGamePanel.add(jpMove);
		jpGamePanel.add(jpAdvancedMove);
		jpGamePanel.add(new JPanel());
	}

	/** Constructs the panel containing the elements to manually create a <b>Grid</b>.
	 * 
	 * @param editing : indicates whether it's a new game creation, or just the modification on an existing save.
	 */
	public void createPanelCreation(boolean editing) {
		// Grass
		jtbGrass = new JToggleButton(new ImageIcon(getClass().getResource("/images/grass.png")));
		jtbGrass.setPreferredSize(new Dimension(50, 50));

		jsGrass = new JSlider(SwingConstants.HORIZONTAL, 1, 3, 1);
		jsGrass.setMajorTickSpacing(1);
		jsGrass.setPaintTicks(true);
		jsGrass.setPaintLabels(true);

		// Tree
		jtbTree = new JToggleButton(new ImageIcon(getClass().getResource("/images/wood.png")));
		jtbTree.setPreferredSize(new Dimension(50, 50));

		jrbTreePunctual = new JRadioButton("Punctual");
		jrbTreePunctual.setSelected(true);
		jrbTreeAuto = new JRadioButton("Automatic");
		bgTree = new ButtonGroup();
		bgTree.add(jrbTreePunctual);
		bgTree.add(jrbTreeAuto);
		jpChoicesTree = new JPanel();
		jpChoicesTree.add(jrbTreePunctual);
		jpChoicesTree.add(jrbTreeAuto);

		// Water
		jtbWater = new JToggleButton(new ImageIcon(getClass().getResource("/images/water.png")));
		jtbWater.setPreferredSize(new Dimension(50, 50));

		jrbWaterPunctual = new JRadioButton("Punctual");
		jrbWaterPunctual.setSelected(true);
		jrbWaterAuto = new JRadioButton("Automatic");
		bgWater = new ButtonGroup();
		bgWater.add(jrbWaterPunctual);
		bgWater.add(jrbWaterAuto);
		jpChoicesWater = new JPanel();
		jpChoicesWater.add(jrbWaterPunctual);
		jpChoicesWater.add(jrbWaterAuto);

		// Wall
		jtbWall = new JToggleButton(new ImageIcon(getClass().getResource("/images/wall.png")));
		jtbWall.setPreferredSize(new Dimension(50, 50));

		jrbWallPunctual = new JRadioButton("Punctual");
		jrbWallPunctual.setSelected(true);
		jrbWallAuto = new JRadioButton("Automatic");
		bgWall = new ButtonGroup();
		bgWall.add(jrbWallPunctual);
		bgWall.add(jrbWallAuto);
		jpChoicesWall = new JPanel();
		jpChoicesWall.add(jrbWallPunctual);
		jpChoicesWall.add(jrbWallAuto);

		// Guardian
		jtbGuardian = new JToggleButton(new ImageIcon(getClass().getResource("/images/guardian.png")));
		jtbGuardian.setPreferredSize(new Dimension(50, 50));
		jcbManualGuardian = new JCheckBox("Controled");

		// Intrude
		jtbIntrude = new JToggleButton(new ImageIcon(getClass().getResource("/images/intrude.png")));
		jtbIntrude.setPreferredSize(new Dimension(50, 50));
		jcbDynamicIntrude = new JCheckBox("Dynamic");

		// ajout des jtboutons a un groupe
		nsbgElements = new NoneSelectedButtonGroup();
		nsbgElements.add(jtbTree);
		nsbgElements.add(jtbWall);
		nsbgElements.add(jtbWater);
		nsbgElements.add(jtbGrass);
		nsbgElements.add(jtbGuardian);
		nsbgElements.add(jtbIntrude);

		// bouton valier
		jbValidate = new JButton("Ok");
		if (editing)
			jbValidate.addActionListener(new ActionValidateEdition());
		else
			jbValidate.addActionListener(new ActionValidateCreation());

		// bouton reset
		jbResetGrid = new JButton("Reset");
		jbResetGrid.addActionListener(new ActionResetGrid());

		jpCreationPanel = new JPanel(new GridLayout(7, 2));

		jpCreationPanel.add(jtbGrass);
		jpCreationPanel.add(jsGrass);

		jpCreationPanel.add(jtbTree);
		jpCreationPanel.add(jpChoicesTree);

		jpCreationPanel.add(jtbWater);
		jpCreationPanel.add(jpChoicesWater);

		jpCreationPanel.add(jtbWall);
		jpCreationPanel.add(jpChoicesWall);

		jpCreationPanel.add(jtbGuardian);
		jpCreationPanel.add(jcbManualGuardian);

		jpCreationPanel.add(jtbIntrude);
		jpCreationPanel.add(jcbDynamicIntrude);

		jpCreationPanel.add(jbResetGrid);
		jpCreationPanel.add(jbValidate);
	}

	/** Constructs and shows the GUI of manual creation of a game.
	 * 
	 * @param editing : indicates whether it's a new game creation, or just the modification on an existing save.
	 */
	public void initManualCreatePanel(boolean editing) {
		createPanelCreation(editing);

		mlPlaceElements = new ActionPlaceElements();
		jpGrid.addMouseListener(mlPlaceElements);

		jpGame = new JPanel(new BorderLayout());

		jpGame.add(jpCreationPanel, BorderLayout.EAST);

		jpGame.add(jpGrid, BorderLayout.CENTER);
		jpGlobal.add(jpGame);
		pack();
	}

	/** Constructs and shows the GUI of a game.
	 */
	public void initGamePanel() {
		createPanelInfos();

		refreshGridVisualField(manager.calculVisualField());

		jpGame = new JPanel(new BorderLayout());

		jpGame.add(jpGamePanel, BorderLayout.EAST);

		jbSave = new JButton("Sauvegarder");
		jbSave.addActionListener(new ActionSave());

		jpGame.add(jbSave, BorderLayout.SOUTH);
		jpGame.add(jpGrid, BorderLayout.CENTER);

		jpGrid.setFocusable(true);
		jpGrid.requestFocusInWindow();

		jpGrid.addKeyListener(new ActionKeyboard());

		jpGlobal.add(jpGame);
		pack();
	}


	/** Validates the manual creation (saves a <b>Map</b>).
	 */
	class ActionValidateCreation implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			jpGame.remove(jpCreationPanel);
			jpGrid.removeMouseListener(mlPlaceElements);
			createPanelInfos();
			jpGame.add(jpGamePanel, BorderLayout.EAST);

			jbSave = new JButton("Save");
			jbSave.addActionListener(new ActionSave());
			jpGame.add(jbSave, BorderLayout.SOUTH);

			jpGrid.setFocusable(true);
			jpGrid.requestFocusInWindow();
			jpGrid.addKeyListener(new ActionKeyboard());

			manager.initIndividuals();
			manager.initPatrouille();
			refreshGridVisualField(manager.calculVisualFieldAndDestinations());

			jpGlobal.updateUI();

			manager.saveMap();
			manager.setStartingTime(System.currentTimeMillis());

			new gameManager().start();

			pack();
		}
	}

	/** Validates the edition a an existing <b>Map</b> (updates it).
	 */
	class ActionValidateEdition implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			manager.initIndividuals();
			manager.initPatrouille();
			manager.saveMap();
			GridGUI.this.dispose();
			guiParent.putToSleep(false);
		}
	}

	/** Resets a <b>Map</b> by clearing all its <b>Elements</b>.</br>
	 * Unchecks all the manual create options.  
	 */
	class ActionResetGrid implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			manager.getGrid().emptyGrid();
			manager.getGrid().initGrid();
			nsbgElements.clearSelection();
			jsGrass.setValue(1);
			bgTree.setSelected(jrbTreePunctual.getModel(), true);
			bgWall.setSelected(jrbWallPunctual.getModel(), true);
			bgWater.setSelected(jrbWaterPunctual.getModel(), true);
			jcbManualGuardian.setSelected(false);
			jcbDynamicIntrude.setSelected(false);
			refreshGridManualCreate();
		}
	}


	/** Places an <b>Element</b> on the <b>Grid</b>, depending on the buttons/checkboxes/sliders states.</br>
	 * - Punctual or automatic placement of <b>Walls</b>, <b>Trunks</b> with <b>Leaves</b> and <b>Water</b>.</br>
	 * - "Erase" a single point, a 3*3 or a 5*5 square, replacing it with <b>Grass</b>.</br>
	 * - Placement of <b>Individuals</b> and <b>Guardians</b>.</br>
	 */
	class ActionPlaceElements implements MouseListener {
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				JLabel labelSelectionne = (JLabel) jpGrid.getComponentAt(e.getPoint());
				if (labelSelectionne != null) {
					Coordinate coord = new Coordinate(
							labelSelectionne.getX() / 32,
							labelSelectionne.getY() / 32);

					if (jtbGrass.isSelected()) 
						manager.getGrid().expandGrass(jsGrass.getValue(), coord);

					else if (jtbWall.isSelected()) {
						if (jrbWallAuto.isSelected())
							manager.getGrid().initWall(coord);
						else if (jrbWallPunctual.isSelected())
							manager.getGrid().setCell(coord, Wall.getInstance());
					}

					else if (jtbTree.isSelected()) {
						if (jrbTreeAuto.isSelected())
							manager.getGrid().initTree(coord);
						else if (jrbTreePunctual.isSelected()) 
							manager.getGrid().setTree(coord);
					}

					else if (jtbWater.isSelected()) {
						if (jrbWaterAuto.isSelected())
							manager.getGrid().initLake(coord);
						else if (jrbWaterPunctual.isSelected())
							manager.getGrid().setCell(coord, Water.getInstance());
					}

					else if (jtbGuardian.isSelected()) {
						if ((!(manager.getGrid().getCell(coord).getElementType() == ElementType.WATER
								|| manager.getGrid().getCell(coord).getElementType() == ElementType.WALL
								|| manager.getGrid().getCell(coord).getElementType() == ElementType.TRUNK 
								|| manager.getGrid().getCell(coord).getElementType() == ElementType.LEAFWATER))) {
							if ((!manager.getGrid().isDirectlyAccessible(coord))) 
								JOptionPane.showMessageDialog(null,	"Useless guard", "Error element placement", JOptionPane.ERROR_MESSAGE);
							else 
								manager.getGrid().setCell(coord, new Guardian(0, coord, jcbManualGuardian.isSelected()));
						}
					}

					else if (jtbIntrude.isSelected()) {
						if ((!(manager.getGrid().getCell(coord).getElementType() == ElementType.WATER
								|| manager.getGrid().getCell(coord).getElementType() == ElementType.WALL
								|| manager.getGrid().getCell(coord).getElementType() == ElementType.TRUNK 
								|| manager.getGrid().getCell(coord).getElementType() == ElementType.LEAFWATER))) {
							if ((!manager.getGrid().isDirectlyAccessible(coord))) 
								JOptionPane.showMessageDialog(null,	"Intrude not reachable", "Error element placement",JOptionPane.ERROR_MESSAGE);
							else 
								manager.getGrid().setCell(coord, new Intrude(1, coord, jcbDynamicIntrude.isSelected()));
						}
					}
					refreshGridManualCreate();
				} 
				else
					JOptionPane.showMessageDialog(null, "Error element placement", "Error element placement", JOptionPane.ERROR_MESSAGE);
			}
		}

		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
	}

	/** Detects a right click and pops an informative window about the <b>Element</b> of the <b>Grid</b> at the clicked <b>Coordinate</b> (Debug).</br>
	 * Allows to take or release control of a <b>Guardian</b> by clicking on it while the game is paused.
	 */
	class ActionDetectClic implements MouseListener {
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON3) { //debug
				JLabel label = (JLabel) jpGrid.getComponentAt(e.getPoint());
				if (label != null) {
					int x = label.getX() / 32;
					int y = label.getY() / 32;
					JOptionPane.showMessageDialog(getParent(),new Coordinate(x, y)	+ " : " + manager.getGrid().getCell(new Coordinate(x, y)).toString());
				}
			}
			else if (e.getButton() == MouseEvent.BUTTON1 && !manager.isPlaying()) { //permet de prendre/lacher le controle d'un gardien
				JLabel label = (JLabel) jpGrid.getComponentAt(e.getPoint());
				if (label != null) {
					int x = label.getX() / 32;
					int y = label.getY() / 32;
					Element o = manager.getGrid().getCell(new Coordinate(x, y));
					if(o.getElementType() == ElementType.GUARDIAN) {
						Guardian guard = (Guardian) o;
						guard.setControled(guard.isControled()^true);
					}
				}
				ArrayList<Coordinate> coordGuardiansManual = new ArrayList<Coordinate>();
				for(Guardian guard : manager.getGuardians()) {
					if(guard.isControled())
						coordGuardiansManual.add(guard.getPosition());
				}
				refreshManualGuardians(manager.calculVisualFieldAndDestinations(), coordGuardiansManual);
			}
			jpGrid.requestFocus();
		}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
	}


	/** Calls the <b>saveMap()</b> method of the <b>Manager</b>.</br>
	 * ActionListener of jbSave.
	 */
	class ActionSave implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			manager.saveMap();
		}
	}

	/** Calls the <b>moveGuardian()</b> method of the <b>Manager</b> with <b>NORTH</b> as argument.</br>
	 * ActionListener of jbNorth.
	 */
	class ActionButtonMoveNorth implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			moveGuardian(Displacement.NORTH);
		}
	}

	/** Calls the <b>moveGuardian()</b> method of the <b>Manager</b> with <b>SOUTH</b> as argument.</br>
	 * ActionListener of jbSouth.
	 */
	class ActionButtonMoveSouth implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			moveGuardian(Displacement.SOUTH);
		}
	}

	/** Calls the <b>moveGuardian()</b> method of the <b>Manager</b> with <b>WEST</b> as argument.</br>
	 * ActionListener of jbWest.
	 */
	class ActionButtonMoveWest implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			moveGuardian(Displacement.WEST);
		}
	}

	/** Calls the <b>moveGuardian()</b> method of the <b>Manager</b> with <b>EAST</b> as argument.</br>
	 * ActionListener of jbEast.
	 */
	class ActionButtonMoveEast implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			moveGuardian(Displacement.EAST);
		}
	}

	/** Calls the <b>moveGuardian()</b> method of the <b>Manager</b> with <b>NONE</b> as argument.</br>
	 * ActionListener of jbPass.
	 */
	class ActionButtonPass implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			moveGuardian(Displacement.NONE);
		}
	}

	/** Calls the <b>pause()</b> method to pause the game at the next turn. 
	 */
	class ActionPause implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			pause();
		}
	}

	/** Pauses the game at the next turn.
	 * While the game is paused, controlled <b>Guardians</b> are highlighted in blue.
	 */
	public void pause() {
		if (manager.isPlaying()) {
			jbPause.setText("Resume");
			ArrayList<Coordinate> coordGuardiansManual = new ArrayList<Coordinate>();
			for(Guardian guard : manager.getGuardians()) {
				if(guard.isControled())
					coordGuardiansManual.add(guard.getPosition());
			}
			refreshManualGuardians(manager.calculVisualFieldAndDestinations(), coordGuardiansManual);
		}
		else {
			jbPause.setText("Pause");
			synchronized (lock) {
				lock.notifyAll();
			}
		}
		manager.setIsPlaying(manager.isPlaying() ^ true);
	}

	/** Keyboard listener.
	 * Calls the corresponding method when a valid key is pressed.
	 */
	class ActionKeyboard implements KeyListener {
		public void keyPressed(KeyEvent event) {
			if (event.getKeyCode() == KeyEvent.VK_UP)// NORTH
				moveGuardian(Displacement.NORTH);
			else if (event.getKeyCode() == KeyEvent.VK_DOWN)// SOUTH
				moveGuardian(Displacement.SOUTH);
			else if (event.getKeyCode() == KeyEvent.VK_LEFT) //WEST
				moveGuardian(Displacement.WEST);
			else if (event.getKeyCode() == KeyEvent.VK_RIGHT) //EAST
				moveGuardian(Displacement.EAST);
			else if (event.getKeyCode() == KeyEvent.VK_SPACE) //NONE / PASS
				moveGuardian(Displacement.NONE);
			else if (event.getKeyCode() == KeyEvent.VK_A) //Auto
				autoMove();
			else if (event.getKeyCode() == KeyEvent.VK_P) //Pause
				pause();
			else if (event.getKeyCode() == KeyEvent.VK_S) //Sonsde pas
				footstepSoundEnabled = footstepSoundEnabled ^ true;

			//debug
			else if (event.getKeyCode() == KeyEvent.VK_C) //Cheat
				refreshGridManualCreate();
			else if (event.getKeyCode() == KeyEvent.VK_I) //itineraire de la patrouille
				refreshGridVisualField(manager.getPatrolsRoutes());
			else if (event.getKeyCode() == KeyEvent.VK_K) //coordonnees de la patrouille
				refreshGridVisualField(manager.getPatrolsCoordinates());
		}

		public void keyReleased(KeyEvent event) {
			if (event.getKeyCode() == KeyEvent.VK_C) {
				if(manager.isAuthorizedMovement())
					refreshMovingGuardian(manager.calculVisualFieldAndDestinations(), manager.getGuardianMoving().getPosition());
				else
					refreshGridVisualField(manager.calculVisualFieldAndDestinations());
			}	
			else if (event.getKeyCode() == KeyEvent.VK_K || event.getKeyCode() == KeyEvent.VK_I) {
				if(manager.isAuthorizedMovement())
					refreshMovingGuardian(manager.calculVisualFieldAndDestinations(), manager.getGuardianMoving().getPosition());
				else
					refreshGridVisualField(manager.calculVisualFieldAndDestinations());
			}	
		}
		public void keyTyped(KeyEvent event) {}
	}

	/** Calls the <b>moveIndividual()</b> method of the <b>Manager</b> with the <b>Guardian</b> whose turn it is to play, and the associated <b>Displacement</b>.</br>
	 * Plays a footstep sound if the <b>Displacement</b> is not <b>NONE</b>.
	 * 
	 * @param displacement : the <b>Displacement</b> to move the <b>Guardian</b>.
	 */
	public void moveGuardian(Displacement displacement) {
		if (manager.isAuthorizedMovement()) {
			switch (displacement) {
			case NORTH:
				manager.moveIndividual(manager.getGuardianMoving(), Displacement.NORTH);
				break;
			case SOUTH:
				manager.moveIndividual(manager.getGuardianMoving(), Displacement.SOUTH);
				break;
			case WEST:
				manager.moveIndividual(manager.getGuardianMoving(), Displacement.WEST);
				break;
			case EAST:
				manager.moveIndividual(manager.getGuardianMoving(), Displacement.EAST);
				break;
			case NONE:
				manager.moveIndividual(manager.getGuardianMoving(), Displacement.NONE);
				break;
			default:
				break;
			}
			synchronized (lock) {
				lock.notifyAll();
			}
		}
	}

	/** Calls the <b>autoMove</b> method, to move automatically the <b>Guardian</b> whose turn it is to play.
	 */
	class ActionAutoMove implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			if (manager.isAuthorizedMovement()) {
				autoMove();
			}
		}
	}

	/** Automatically moves the <b>Guardian</b> whose turn it is to play.
	 */
	public void autoMove() {
		manager.autoMove(manager.getGuardianMoving());
		synchronized (lock) {
			lock.notifyAll();
		}
	}

	/** Refreshes the GUI of the <b>Grid</b>.</br>
	 * Shows all the boxes of the grid enlightened.
	 */
	public void refreshGridManualCreate() {
		jpGrid.removeAll();
		JLabel labelTemp;
		Element element;
		Coordinate coordTemp;
		Grid grid = manager.getGrid();
		for (int row = 0; row < grid.getDimension(); row++) {
			for (int col = 0; col < grid.getDimension(); col++) {

				coordTemp = new Coordinate(col, row);
				element = (Element) grid.getCell(coordTemp);
				ElementType elementType = element.getElementType();

				switch (elementType) {
				case GRASS:
					labelTemp = imageBank.grassLabel();
					break;
				case LEAF:
					labelTemp = imageBank.leafLabel();
					break;
				case LEAFWATER:
					labelTemp = imageBank.leafWaterLabel();
					break;
				case TRUNK:
					labelTemp = imageBank.trunkLabel();
					break;
				case WALL:
					labelTemp = imageBank.wallLabel();
					break;
				case WATER:
					labelTemp = imageBank.waterLabel();
					break;
				case GUARDIAN:
					labelTemp = imageBank.guardianLabel();
					break;
				case INTRUDE:
					labelTemp = imageBank.intrudeLabel();
					break;
				default:
					labelTemp = imageBank.grassLabel();
					break;
				}
				jpGrid.add(labelTemp);
			}
		}
		jpGrid.revalidate();
		jpGrid.repaint();
		//		jpGrid.updateUI();
	}

	/** Refreshes the GUI of the <b>Grid</b>.</br>
	 * Enlightened the boxes visible by the <b>Guardians</b> only.
	 * 
	 * @param visibles : the list of <b>Coordinates</b> visible by <b>Guardians</b>.
	 */
	public void refreshGridVisualField(ArrayList<Coordinate> visibles) {
		jpGrid.removeAll();
		JLabel labelTemp;
		Element element;
		Coordinate coordTemp;
		Grid grid = manager.getGrid();
		for (int row = 0; row < grid.getDimension(); row++) {
			for (int col = 0; col < grid.getDimension(); col++) {
				coordTemp = new Coordinate(col, row);
				element = (Element) grid.getCell(coordTemp);
				ElementType elementType = element.getElementType();

				switch (elementType) {
				case GRASS:
					if (visibles.contains(coordTemp))
						labelTemp = imageBank.grassLabel();
					else
						labelTemp = imageBank.darkGrassLabel();
					break;
				case LEAF:
					if (visibles.contains(coordTemp))
						labelTemp = imageBank.leafLabel();
					else
						labelTemp = imageBank.darkLeafLabel();
					break;
				case LEAFWATER:
					if (visibles.contains(coordTemp))
						labelTemp = imageBank.leafWaterLabel();
					else
						labelTemp = imageBank.darkLeafWaterLabel();
					break;
				case TRUNK:
					if (visibles.contains(coordTemp))
						labelTemp = imageBank.trunkLabel();
					else
						labelTemp = imageBank.darkTrunkLabel();
					break;
				case WALL:
					if (visibles.contains(coordTemp))
						labelTemp = imageBank.wallLabel();
					else
						labelTemp = imageBank.darkWallLabel();
					break;
				case WATER:
					if (visibles.contains(coordTemp))
						labelTemp = imageBank.waterLabel();
					else
						labelTemp = imageBank.darkWaterLabel();
					break;
				case INTRUDE:
					if (visibles.contains(coordTemp))
						labelTemp = imageBank.intrudeLabel();
					else {
						if (manager.getGrid().isNearTree(coordTemp))
							labelTemp = imageBank.darkLeafLabel();
						else
							labelTemp = imageBank.darkGrassLabel();
					}
					break;
				case GUARDIAN:
					labelTemp = imageBank.guardianLabel();
					break;
				default:
					if (visibles.contains(coordTemp))
						labelTemp = imageBank.grassLabel();
					else
						labelTemp = imageBank.darkGrassLabel();
					break;
				}
				jpGrid.add(labelTemp);
			}
		}
		jpGrid.revalidate();
		jpGrid.repaint();
		//		jpGrid.updateUI();
	}


	/** Refreshes the GUI of the <b>Grid</b>.</br>
	 * Enlightened the boxes visible by the <b>Guardians</b> only and highlight in blue the <b>Guardian</b> whose turn it is to play.
	 * 
	 * @param visibles : the list of <b>Coordinates</b> visible by <b>Guardians</b>.
	 * @param coordGuardian : the <b>Coordinate</b> of the <b>Guardian</b> whose turn it is to play.
	 */
	public void refreshMovingGuardian(ArrayList<Coordinate> visibles, Coordinate coordGuardian) {
		jpGrid.removeAll();
		JLabel labelTemp;
		Element element;
		Coordinate coordTemp;
		Grid grid = manager.getGrid();
		for (int row = 0; row < grid.getDimension(); row++) {
			for (int col = 0; col < grid.getDimension(); col++) {
				coordTemp = new Coordinate(col, row);
				element = (Element) grid.getCell(coordTemp);
				ElementType elementType = element.getElementType();

				switch (elementType) {
				case GRASS:
					if (visibles.contains(coordTemp))
						labelTemp = imageBank.grassLabel();
					else
						labelTemp = imageBank.darkGrassLabel();
					break;
				case LEAF:
					if (visibles.contains(coordTemp))
						labelTemp = imageBank.leafLabel();
					else
						labelTemp = imageBank.darkLeafLabel();
					break;
				case LEAFWATER:
					if (visibles.contains(coordTemp))
						labelTemp = imageBank.leafWaterLabel();
					else
						labelTemp = imageBank.darkLeafWaterLabel();
					break;
				case TRUNK:
					if (visibles.contains(coordTemp))
						labelTemp = imageBank.trunkLabel();
					else
						labelTemp = imageBank.darkTrunkLabel();
					break;
				case WALL:
					if (visibles.contains(coordTemp))
						labelTemp = imageBank.wallLabel();
					else
						labelTemp = imageBank.darkWallLabel();
					break;
				case WATER:
					if (visibles.contains(coordTemp))
						labelTemp = imageBank.waterLabel();
					else
						labelTemp = imageBank.darkWaterLabel();
					break;
				case INTRUDE:
					if (visibles.contains(coordTemp))
						labelTemp = imageBank.intrudeLabel();
					else {
						if (manager.getGrid().isNearTree(coordTemp))
							labelTemp = imageBank.darkLeafLabel();
						else
							labelTemp = imageBank.darkGrassLabel();
					}
					break;
				case GUARDIAN:
					if (coordTemp.equals(coordGuardian))
						labelTemp = imageBank.guardianMovingLabel();
					else
						labelTemp = imageBank.guardianLabel();
					break;
				default:
					if (visibles.contains(coordTemp))
						labelTemp = imageBank.grassLabel();
					else
						labelTemp = imageBank.darkGrassLabel();
					break;
				}
				jpGrid.add(labelTemp);
			}
		}
		jpGrid.revalidate();
		jpGrid.repaint();
		//		jpGrid.updateUI();
	}

	/** Refreshes the GUI of the <b>Grid</b>.</br>
	 * Enlightened the boxes visible by the <b>Guardians</b> only and highlight in blue all the controlled <b>Guardians</b>.
	 * 
	 * @param visibles : the list of <b>Coordinates</b> visible by <b>Guardians</b>.
	 * @param coordGuardian : the list of <b>Coordinate</b> of the controlled <b>Guardians</b>.
	 */
	public void refreshManualGuardians(ArrayList<Coordinate> visibles, ArrayList<Coordinate> coordGuardian) {
		jpGrid.removeAll();
		JLabel labelTemp;
		Element element;
		Coordinate coordTemp;
		Grid grid = manager.getGrid();
		for (int row = 0; row < grid.getDimension(); row++) {
			for (int col = 0; col < grid.getDimension(); col++) {
				coordTemp = new Coordinate(col, row);
				element = (Element) grid.getCell(coordTemp);
				ElementType elementType = element.getElementType();

				switch (elementType) {
				case GRASS:
					if (visibles.contains(coordTemp))
						labelTemp = imageBank.grassLabel();
					else
						labelTemp = imageBank.darkGrassLabel();
					break;
				case LEAF:
					if (visibles.contains(coordTemp))
						labelTemp = imageBank.leafLabel();
					else
						labelTemp = imageBank.darkLeafLabel();
					break;
				case LEAFWATER:
					if (visibles.contains(coordTemp))
						labelTemp = imageBank.leafWaterLabel();
					else
						labelTemp = imageBank.darkLeafWaterLabel();
					break;
				case TRUNK:
					if (visibles.contains(coordTemp))
						labelTemp = imageBank.trunkLabel();
					else
						labelTemp = imageBank.darkTrunkLabel();
					break;
				case WALL:
					if (visibles.contains(coordTemp))
						labelTemp = imageBank.wallLabel();
					else
						labelTemp = imageBank.darkWallLabel();
					break;
				case WATER:
					if (visibles.contains(coordTemp))
						labelTemp = imageBank.waterLabel();
					else
						labelTemp = imageBank.darkWaterLabel();
					break;
				case INTRUDE:
					if (visibles.contains(coordTemp))
						labelTemp = imageBank.intrudeLabel();
					else {
						if (manager.getGrid().isNearTree(coordTemp))
							labelTemp = imageBank.darkLeafLabel();
						else
							labelTemp = imageBank.darkGrassLabel();
					}
					break;
				case GUARDIAN:
					if (coordGuardian.contains(coordTemp))
						labelTemp = imageBank.guardianMovingLabel();
					else
						labelTemp = imageBank.guardianLabel();
					break;
				default:
					if (visibles.contains(coordTemp))
						labelTemp = imageBank.grassLabel();
					else
						labelTemp = imageBank.darkGrassLabel();
					break;
				}
				jpGrid.add(labelTemp);
			}
		}
		jpGrid.revalidate();
		jpGrid.repaint();
		//		jpGrid.updateUI();
	}

	/** Refreshes the informations panel.
	 */
	public void refreshPanelInfos() {
		jlNumberOfTurns.setText("Number of turns : " + manager.getNumberOfTurns());
		jlDuration.setText("Duration : "+ DateFormat.getTimeInstance().format(manager.getDuration() - 3600000));

		jpGameDatas.revalidate();
		jpGameDatas.repaint();
		//		jpGameDatas.updateUI();
	}

	/** Calls the <b>quit()</b> method when the user tries to quit the application.
	 */
	public void windowClosing(WindowEvent e) {
		quit();
	}

	/** Shows a windows asking confirmation to the user before exiting and whether to save or not.
	 */
	public void quit() {
		int reponse = JOptionPane.showConfirmDialog(getParent(), "Do you want to save before exiting ?", "Exit", JOptionPane.YES_NO_CANCEL_OPTION);
		if (reponse == 0 || reponse == 1) {
			if (reponse == 0) {
				manager.saveMap();
			}
			this.removeAll();
			guiParent.putToSleep(false);
			this.dispose();
		}
	}

	/** Reverse the state of the background music.
	 */
	class ActionEnableDisableMusic extends AbstractAction {
		private static final long serialVersionUID = 2637064803335193267L;
		public void actionPerformed(ActionEvent e) {
			guiParent.EnableDisableMusic();
		}
	}

	/** Plays the sound of a footstep (can be disabled).
	 */
	public void playFootstep() {
		if(footstepSoundEnabled) {
			footstep.stop();
			footstep.play();
		}
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

	/** Class extending <b>ButtonGroup</b> to allow the unselection of a button.
	 */
	class NoneSelectedButtonGroup extends ButtonGroup {
		private static final long serialVersionUID = -4105887775992858074L;

		public void setSelected(ButtonModel model, boolean selected) {
			if (selected) {
				super.setSelected(model, selected);
			} else {
				clearSelection();
			}
		}
	}

	public void windowOpened(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
}

// //////////////////////////////////////////////////////Tests//////////////////////////////////////////////////////////////////////////////////////////////////

///**
// * Test pour le PathFinding
// */
//class ActionAlgoPathFinding implements ActionListener {
//	public void actionPerformed(ActionEvent ae) {
//		int departX = Integer.parseInt(jtfDepartX.getText());
//		int departY = Integer.parseInt(jtfDepartY.getText());
//		int arriveeX = Integer.parseInt(jtfArriveeX.getText());
//		int arriveeY = Integer.parseInt(jtfArriveeY.getText());
//		ArrayList<Coordinate> itineraire = manager.getAi().calculItineraire(new Coordinate(departX, departY),new Coordinate(arriveeX, arriveeY), true);
//		for (Coordinate coord : itineraire) {
//			manager.getGrid().setCell(coord, manager.getGrid().wall);
//		}
//		refreshGridManualCreate();
//	}
//}
//
///**
// * Test pour le champ de vision
// */
//class ActionAlgoVisualField implements ActionListener {
//	public void actionPerformed(ActionEvent ae) {
//		int departX = Integer.parseInt(jtfDepartX2.getText());
//		int departY = Integer.parseInt(jtfDepartY2.getText());
//		ArrayList<Coordinate> visualField = manager.getAi()
//				.calculChampDeVision(new Coordinate(departX, departY));
//		jpGrid.removeAll();
//		refreshGridVisualField(visualField);
//	}
//}

// //////////////////////////////////////////////////////Tests//////////////////////////////////////////////////////////////////////////////////////////////////

///** Plays the sound of a footstep.
//*/
//class playFootstep extends Thread {
//	public void run() {
//		java.applet.Applet.newAudioClip(getClass().getResource("/sons/grass1.wav")).play();
//	}
//}