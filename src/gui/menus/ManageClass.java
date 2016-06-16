package gui.menus;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import db.main.TeacherManipulatorDB;
import db.main.UserManipulatorDB;
import gui.merch.Table;
import logics.main.User;

/**
 * This class is responsible of displaying the current teacher's class and adding/removing users from it
 * @author Aleksejs Loginovs
 *
 */
public class ManageClass extends JDialog
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//window contents
	private JPanel content;
	private JTable usertable;
	private JButton addToClass;
	private JButton removeFromClass;
	private JButton ok;
	
	private String teachername;
	//list of all users and list of users in the current teacher's class
	private ArrayList<User> userList;
	private ArrayList<User> students;

	public ManageClass(String teachername)
	{
		this.teachername = teachername;
		//set up the title and main panel of the dialog
		this.setTitle("Manage class");
		content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
		
		//initialise button panel and all the buttons
		ok = new JButton("ok");
		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.LINE_AXIS));
		addToClass = new JButton("Add");
		addToClass.addActionListener(new ClassManageButtons());
		addToClass.setEnabled(false);
		buttons.add(addToClass);
		
		removeFromClass = new JButton("Remove");
		removeFromClass.addActionListener(new ClassManageButtons());
		removeFromClass.setEnabled(false);
		buttons.add(removeFromClass);

		//set up the table and refresh it
		usertable = new Table(new DefaultTableModel());
		refreshTable();
		
		//set selection model and selection listener
		usertable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		usertable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent event) 
			{
				if(usertable.getSelectedRow()>-1)
				{	//if row selected, buttons enabled
					String value = null;
					value = (String)usertable.getModel().getValueAt(usertable.getSelectedRow(), 1);
					if(value.equals("In class"))
					{//if selected user is in class, 'add' button disabled
						addToClass.setEnabled(false);
						removeFromClass.setEnabled(true);
					}
					else if(value.equals("Not in class"))
					{//if selected user is not in class, 'remove' button disabled
						addToClass.setEnabled(true);
						removeFromClass.setEnabled(false);
					}
				}
				else if(usertable.getSelectedRow() == -1)
				{//if no row selected, all buttons disabled
					addToClass.setEnabled(false);
					removeFromClass.setEnabled(false);
				}
			}
		});
		//add contents to the panel
		content.add(usertable);
		content.add(buttons);
		content.add(ok);
		//set ok button's action listener
		ok.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				closeFrame();
			}
		});
		
		//add content to the frame
		add(content);
		
		//set general dialog properties
		setLocationRelativeTo(null);
		setSize(new Dimension(250,250));
		setModalityType(ModalityType.APPLICATION_MODAL);
		setVisible(true);
	}
	
	/**
	 * Method used to add selected user to the teacher's class
	 */
	public void addSelectedUser()
	{
		int selectedID = usertable.getSelectedRow();
		User student = userList.get(selectedID);
		TeacherManipulatorDB.updateClass(student.getName(), teachername, "add");
		
		refreshTable();
	}
	
	/**
	 * Method used to remove selected user from teacher's class
	 */
	public void removeSelectedUser()
	{
		int selectedID = usertable.getSelectedRow();
		User student = userList.get(selectedID);
		TeacherManipulatorDB.updateClass(student.getName(), teachername, "remove");
		
		refreshTable();
	}
	
	/**
	 * Method used to load new userlist and class from the database and refresh the table
	 */
	public void refreshTable()
	{
		//load two lists
		userList = UserManipulatorDB.getAllTeachableUsers(teachername);
		students = TeacherManipulatorDB.getStudents(teachername);
		
		//transform userList into a 2d array with every user being in or not in class
		String[][] userArray = new String[userList.size()][2];
		for(int i = 0; i < userList.size(); i++)
		{
			userArray[i][0] = userList.get(i).getName();
			userArray[i][1] = "Not in class";
			
			//remove users that are not able to become students from the list(teachers and admins)
			for(int j = 0; j < students.size(); j++)
			{
				if(students.get(j).getName().equals(userList.get(i).getName()))
				{
					userArray[i][1] = "In class";
					break;
				}
			}
		}
		
		String[] headers = {"Username", "Status"};
		
		//create new model with new data and replace the old one with it
		DefaultTableModel model = new DefaultTableModel(userArray, headers);
		usertable.setModel(model);
	}
	
	/**
	 * Method used to close the dialog window
	 */
	public void closeFrame()
	{
		this.dispose();
	}
	
	/**
	 * Class is responsible for listening to addToClass/removeFromClass button clicks
	 * @author Aleksejs Loginovs
	 *
	 */
	private class ClassManageButtons implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent event) 
		{
			JButton source = (JButton)event.getSource();
			
			if(source == addToClass)
			{
				addSelectedUser();
			}
			else if(source == removeFromClass)
			{
				removeSelectedUser();
			}
			
		}
		
	}
	
}
