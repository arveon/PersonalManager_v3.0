package gui.main;

import db.main.SaveGlobalAppointmentsDB;
import db.main.SavePrivateAppointmentsDB;
import db.main.TeacherManipulatorDB;
import gui.menus.EditPermissions;
import gui.menus.ManageClass;
import gui.menus.StatisticsMenu;
import gui.merch.Table;
import logics.main.Appointment;
import logics.main.AppointmentList;
import logics.main.User;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.GridBagConstraints;
import java.awt.Dimension;

import java.util.ArrayList;

/**
 * Class represents the main program frame and contains methods used to handle it
 * @author Aleksejs Loginovs
 *
 */
public class MainWindow extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean visible;
	private AppointmentList curList;
	private int curTab;
	private AppointmentList privateList;
	private AppointmentList globalList;
	private AppointmentList studAppList;
	private ListSelectionModel model;
	private User user;
	
	private JPanel content;
	private JMenuBar menu;
	
	private JButton addNew;
	private JButton edit;
	private JButton remove;
	
	private Table appTable;
	private JScrollPane tableScroll;
	
	private JTabbedPane tabs;
	
	/**
	 * Constructor used to create the window
	 * @param list list of the appointments loaded from file
	 */
	public MainWindow(AppointmentList privateList, AppointmentList globalList, User user)
	{
		this.user = user;
		this.curList = privateList;
		this.privateList = privateList;
		this.globalList = globalList;
		this.studAppList = new AppointmentList();
		
		curTab = 0;
		visible = false;
		
		addWindowListener(new WinListen());
		
		tabs = new JTabbedPane();
		content = buildPrivatePane();
		menu = new JMenuBar();
		menu = buildMenuBar(menu);
		tabs.add("Private", content);
		tabs.add("Global", null);
		
		if(user.getLevel() == 1)
		{
			for(String teachername: user.getTeacherNames())
			{
				tabs.add(teachername, null);
			}
		}
		
		if(user.getLevel() == 2)
		{
			studAppList = TeacherManipulatorDB.getAppointments(user.getName());
			tabs.add(user.getName(), null);
		}
		
		tabs.addChangeListener(new AppointmentSwitchListener());
		add(tabs);		
		
		setJMenuBar(menu);
		pack();
		setLocationRelativeTo(null);
		setResizable(false);
		setTitle("Personal Manager - " + user.getName());
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}
	
	/**
	 * Method used to initialise all window components and add them to the frame
	 */
	public JPanel buildPrivatePane()
	{
		content = new JPanel();
		content.setLayout(new GridBagLayout());
		
		//initialise buttons
		addNew = new JButton("New");
		addNew.setPreferredSize(new Dimension(150,30));
		addNew.addActionListener(new MainActionListener(this));
		edit = new JButton("Edit");
		edit.setPreferredSize(new Dimension(150,30));
		edit.addActionListener(new MainActionListener(this));
		edit.setEnabled(false);
		remove = new JButton("Remove");
		remove.setPreferredSize(new Dimension(150,30));
		remove.addActionListener(new MainActionListener(this));
		remove.setEnabled(false);
		
		//SET UP THE TABLE
		//set up the table data array and fill it
		
	
		appTable = new Table(getNewTableModel(curList));
		appTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableScroll = new JScrollPane(appTable);
		tableScroll.setPreferredSize(new Dimension(450,400));
		//add listeners to the table
		appTable.addMouseListener(new TableClickListener());
		this.model = appTable.getSelectionModel();
		this.model.addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent event)
			{
				if(appTable.getSelectionModel().isSelectionEmpty())
				{
						edit.setEnabled(false);
						remove.setEnabled(false);
				}
				else
				{
					switch(tabs.getSelectedIndex())
					{
					case 0:	edit.setEnabled(true);
							remove.setEnabled(true);
							addNew.setEnabled(true);
							break;
							
					case 1:	if(user.getLevel() < 3)
							{
								edit.setEnabled(false);
								remove.setEnabled(false);
								addNew.setEnabled(false);
							}
							else
							{
								edit.setEnabled(true);
								remove.setEnabled(true);
								addNew.setEnabled(true);
							}
							break;
							
					default: 	if(tabs.getTitleAt(tabs.getSelectedIndex()) != user.getName() && user.getLevel() < 3)
								{
									edit.setEnabled(false);
									remove.setEnabled(false);
									addNew.setEnabled(false);
								}
								else
								{
									edit.setEnabled(true);
									remove.setEnabled(true);
									addNew.setEnabled(true);
								}
							
							
					}
				}
			}
		});
		
		//add elements to the frame
		GridBagConstraints constr = new GridBagConstraints();
		constr.gridx = 0;
		constr.gridy = 0;
		content.add(addNew, constr);
		
		constr.gridx = 1;
		content.add(edit, constr);
		
		constr.gridx = 2;
		content.add(remove, constr);
		
		constr.fill = GridBagConstraints.BOTH;
		constr.gridx = 0;
		constr.gridwidth = 3;
		constr.gridy = 1;
		content.add(tableScroll, constr);
			
		return content;
	}
	
	/**
	 * Method used to initialise menu bar and add options to it
	 * @param menu the menu bar object to add elements to
	 * @return the complete menu bar
	 */
	public JMenuBar buildMenuBar(JMenuBar menu)
	{
		//set up file section of the menu
		JMenu file = new JMenu("File");
		JMenuItem save = new JMenuItem("Save");
		file.add(save);
		file.addSeparator();
		JMenuItem exit = new JMenuItem("Exit");
		file.add(exit);
		menu.add(file);
		
		save.addActionListener(new ProgramMenuListener());
		exit.addActionListener(new ProgramMenuListener());
		
		//set up user section of the menu
		JMenu user = new JMenu("User");
		JMenuItem statistics = new JMenuItem("Statistics");
		statistics.addActionListener(new ProgramMenuListener());
		
		user.add(statistics);
		user.addSeparator();
		
		JMenuItem changePermissions = new JMenuItem("Change permissions");
		changePermissions.addActionListener(new ProgramMenuListener());
		user.add(changePermissions);
		if(this.user.getLevel() < 3)
		{
			changePermissions.setEnabled(false);
		}
		menu.add(user);
		
		JMenu teacher = new JMenu("Class");
		JMenuItem manageClass = new JMenuItem("Manage class");
		manageClass.addActionListener(new ProgramMenuListener());
		teacher.add(manageClass);
		if(this.user.getLevel()!=2)
		{
			manageClass.setEnabled(false);
		}
		menu.add(teacher);
		
		return menu;
	}
	
	/**
	 * Method returns the list of the appointments that should be used by the program
	 * @return the currently used list of appointments
	 */
	public AppointmentList getCurrentList()
	{
		return curList;
	}
	
	/**
	 * Method used to toggle the visibility of the main frame
	 */
	public void toggleVisible()
	{
		setVisible(!visible);
	}
	
	/**
	 * Method used to confirm if user really wants to exit the program
	 */
	public void exit()
	{
		int confirmation = 1;
		String message = "Would you like to save all changes before exiting?";
		confirmation = JOptionPane.showConfirmDialog(null, message, "Exit confirmation", JOptionPane.YES_NO_OPTION);
		if(confirmation == 0)
		{
			save();
			this.dispose();
			LoginWindow login = new LoginWindow();
			login.toggleVisible();
		}
		else
		{
			this.dispose();
			LoginWindow login = new LoginWindow();
			login.toggleVisible();
		}
	}
	
	/**
	 * Method used to confirm appointment removal and process it
	 */
	public void confirmAndRemove()
	{
		int confirmation = 1;
		String message =  "Are you sure you want to remove that appointment?";
		confirmation = JOptionPane.showConfirmDialog(null, message, "Delete confirmation", JOptionPane.YES_NO_OPTION);
		if(confirmation == 0)
		{
			Appointment removed = curList.getAppointmentAt(appTable.getSelectedRow());
			curList.removeAppointment(removed, curList.getRoot());
		}
	}
	
	/**
	 * Method used to call the file saving process and show the error/success message
	 */
	public void save()
	{
		if(tabs.getSelectedIndex() == 0)
		{
			privateList = curList;
		}
		else if(tabs.getSelectedIndex() == 1)
		{
			globalList = curList;
		}
		
		int teacherSuccess = 0;
		if(user.getLevel() == 2)
		{
			if(tabs.getTitleAt(tabs.getSelectedIndex()).equals(user.getName()))
			{
				studAppList = curList;
			}
			teacherSuccess = TeacherManipulatorDB.saveAppointments(studAppList, user.getName());
		}
		else
		{
			teacherSuccess = 1;
		}
		
		int globalSuccess = 0;
		int privateSuccess = 0;
		privateSuccess = SavePrivateAppointmentsDB.saveApps(privateList, user.getName());
		globalSuccess = SaveGlobalAppointmentsDB.saveApps(globalList);
		if(privateSuccess == 1 && globalSuccess == 1 && teacherSuccess == 1)
		{
			JOptionPane.showMessageDialog(null, "Your appointment list was successfully saved.");
		}
		else if(privateSuccess == 0)
		{
			JOptionPane.showMessageDialog(null, "Sorry, the private appointment list wasn't saved.");
		}
		else if(globalSuccess == 0)
		{
			JOptionPane.showMessageDialog(null, "Sorry, the global appointment list wasn't saved.");
		}
		else if(teacherSuccess == 0)
		{
			JOptionPane.showMessageDialog(null, "Sorry, the teacher appointment list wasn't saved.");

		}
		else
		{
			JOptionPane.showMessageDialog(null, "Sorry, the appointment list wasn't saved.");
		}
	}
	
	/**
	 * Method used to set the list of the appointments for this object
	 * @param list
	 */
	public void setList(AppointmentList list)
	{
		this.curList = list;
	}
	
	/**
	 * Method used to update the table model of the table that displays the appointments
	 * @return new updated table model
	 */
	public DefaultTableModel getNewTableModel(AppointmentList list)
	{
		ArrayList<Appointment> applist = list.getAllAppointments();
		list.printAllAppointments();
		String[][] appDataList = new String[applist.size()][Appointment.getFieldNames().length];
		for(int i = 0; i < applist.size(); i++)
		{
			appDataList[i][0] = applist.get(i).getPlace();
			appDataList[i][2] = applist.get(i).getTimeString();
			appDataList[i][1] = applist.get(i).getDateString();
			appDataList[i][3] = applist.get(i).getAuthor();
		}
		
		DefaultTableModel model = new DefaultTableModel(appDataList, Appointment.getFieldNames());
		
		return model;
	}
	
	/**
	 * Method used to update the table and refresh it
	 */
	public void updateTable(AppointmentList list)
	{
		appTable.setModel(getNewTableModel(list));
	}
	
	/**
	 * Class used as a mouse listener and listens to mouse clicks in the table
	 * 
	 *
	 */
	public class TableClickListener extends MouseAdapter
	{
		/**
		 * Method gets called when one of the rows is clicked
		 */
		public void mousePressed(MouseEvent event)
		{
			JTable table = (JTable)event.getSource();
			Point mousePoint = event.getPoint();
			int row = table.rowAtPoint(mousePoint);
			if(event.getClickCount() == 2)
			{
				Appointment selectedApp = curList.getAppointmentAt(row);
				JOptionPane.showMessageDialog(null,"Description: " + selectedApp.getDescription() + "\n" + "Place: " + selectedApp.getPlace() + "\nTime: " +selectedApp.getTimeString()
												+ "\nDate: " + selectedApp.getDateString(), "Appointment - " + row, JOptionPane.PLAIN_MESSAGE);
			}
		}
	}
	
	/**
	 * Class used to listen to button clicks on the main frame
	 * 
	 *
	 */
	public class MainActionListener implements ActionListener 
	{
		private MainWindow mainwindow;
		/**
		 * Constructor that 
		 * @param mainwindow instance of a main window class that is currently being used
		 */
		public MainActionListener(MainWindow mainwindow){super(); this.mainwindow = mainwindow;}
		
		/**
		 * Method used to listen to button clicks
		 */
		@Override
		public void actionPerformed(ActionEvent event)
		{
			if(event.getSource() == addNew)
			{
				new AddAppointmentWindow(mainwindow, user);
			}
			else if(event.getSource() == remove)
			{
				confirmAndRemove();
				updateTable(curList);
			}
			else if(event.getSource() == edit)
			{
				new EditAppointmentWindow(mainwindow, curList.getAppointmentAt(appTable.getSelectedRow()), user);
			}
		}
	}
	
	/**
	 * Class used to listen to window manipulations
	 * 
	 *
	 */
	public class WinListen extends WindowAdapter
	{
		/**
		 * Method called when user attempts to close window and asks for his confirmation
		 */
		@Override
		public void windowClosing(WindowEvent e) 
		{
			MainWindow window = (MainWindow)e.getSource();
			window.exit();
		}
	}
	
	/**
	 * Class handle switching between different tabs
	 * 
	 *
	 */
	public class AppointmentSwitchListener implements ChangeListener
	{
		public void stateChanged(ChangeEvent e)
		{
			//if the first tab is selected, display the private appointment list in the table
			if(tabs.getSelectedIndex() == 0)
			{
				if(curTab == 1)
				{
					globalList = curList;
				}
				curList = privateList;
				updateTable(curList);
				addNew.setEnabled(true);
				edit.setEnabled(false);
				remove.setEnabled(false);
				curTab = 0;
			}
			else if(tabs.getSelectedIndex() == 1)
			{//if the second tab is selected, display the global appointments list
				if(curTab == 0)
				{
					privateList = curList;
				}
				curList = globalList;
				updateTable(curList);
				
				edit.setEnabled(false);
				remove.setEnabled(false);
				
				if(user.getLevel() < 3)
				{
					addNew.setEnabled(false);
				}
				curTab = 1;
			}
			else
			{				
				if(curTab == 0)
				{
					privateList = curList;
				}
				else if(curTab == 1)
				{
					globalList = curList;
				}
				
				
				if(user.getLevel() == 2)
				{
					curList = studAppList;
				}
				else
				{
					curList = TeacherManipulatorDB.getAppointments(tabs.getTitleAt(tabs.getSelectedIndex()));
				}
				
				
				if(tabs.getTitleAt(tabs.getSelectedIndex()).equals(user.getName()))
				{
					edit.setEnabled(true);
					remove.setEnabled(true);
					addNew.setEnabled(true);
				}
				else
				{
					edit.setEnabled(false);
					remove.setEnabled(false);
					addNew.setEnabled(false);
				}
				updateTable(curList);
				
				curTab = tabs.getSelectedIndex();
			}
		}
	}
	
	/**
	 * Class used to listen to drop-down menus
	 *
	 */
	public class ProgramMenuListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			JMenuItem source = (JMenuItem)e.getSource();
			System.out.println(source.getText());
			switch(source.getText())
			{
			case "Save": 	save();
							break;
			case "Exit":	exit();
							break;
			case "Change permissions": 	new EditPermissions(user);
										break;
			case "Statistics":			new StatisticsMenu(user.getName());
										break;
			case "Manage class":		new ManageClass(user.getName());
										break;
			}
		}
	}
}
