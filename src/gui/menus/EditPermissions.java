package gui.menus;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import db.main.UserManipulatorDB;
import logics.main.User;

import gui.merch.Table;

/**
 * This class controls the dialog that allows admins to edit user permissions
 * @author Aleksejs Loginovs
 *
 */
public class EditPermissions extends JDialog
{
	//contents of the window
	private JPanel content;
	private JTable usertable;
	private JButton promote;
	private JButton demote;
	private JButton ok;
	private User user;
	
	private ArrayList<User> userList;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor that initialises all the contents and builds the window
	 * @param user the object of the currently logged in user
	 */
	public EditPermissions(User user)
	{
		this.user = user;
		this.setTitle("Edit user permissions");
		content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
		
		ok = new JButton("ok");
		
		//initialise button panel and all the buttons
		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.LINE_AXIS));
		
		promote = new JButton("Promote");
		promote.setPreferredSize(new Dimension(50,20));
		promote.addActionListener(new PromoDemoListener());
		promote.setEnabled(false);
		demote = new JButton("Demote");
		demote.setPreferredSize(new Dimension(50,20));
		demote.addActionListener(new PromoDemoListener());
		demote.setEnabled(false);
		
		//get the arraylist of users and transform it into a 2d array
		userList = UserManipulatorDB.getAllUsers(user.getName());
		
		String[][] userArray = new String[userList.size()][2];
		for(int i = 0; i < userList.size(); i++)
		{
			userArray[i][0] = userList.get(i).getName();
			int templevel = userList.get(i).getLevel();
			switch(templevel)
			{
			case 0: userArray[i][1] = "User";
					break;
			case 1: userArray[i][1] = "Student";
					break;
			case 2: userArray[i][1] = "Teacher";
					break;
			case 3: userArray[i][1] = "Admin";
					break;
			default:userArray[i][1] = "ERROR";
			}
		}
		//set up the names of columns
		String[] headers = {"Username", "Permission"};
		
		//set up the table model and add it to the table
		DefaultTableModel model = new DefaultTableModel(userArray, headers);
		usertable = new Table(model);
		
		//set selection model and selection listener
		usertable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		usertable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				//if no row selected, all buttons disabled
				if(usertable.getSelectedRow() == -1)
				{
					promote.setEnabled(false);
					demote.setEnabled(false);
				}
				else
				{
					//if row selected, buttons enabled
					promote.setEnabled(true);
					demote.setEnabled(true);
					
					//if selected row can't be promoted further, 'promote' button disabled
					if(userList.get(usertable.getSelectedRow()).getLevel() >= 3)
					{
						promote.setEnabled(false);
					}
					else if(userList.get(usertable.getSelectedRow()).getLevel() <= 0)
					{//if selected row can't be demoted (already lowest permission), block 'demote' button
						demote.setEnabled(false);
					}
				}
			}		
		});
		
		//add buttons to button panel
		buttons.add(promote);
		buttons.add(demote);
		
		//add stuff to content panel
		content.add(usertable);
		content.add(buttons);
		content.add(ok);
		
		ok.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				closeFrame();
			}
		});
		
		add(content);
		
		//set up general dialog properties
		setLocationRelativeTo(null);
		setSize(new Dimension(250,250));
		setModalityType(ModalityType.APPLICATION_MODAL);
		setVisible(true);
	}
	
	/**
	 * Method used to update table contents when the user is being promoted/demoted
	 */
	public void updateWindow()
	{
		//loads all users from db and transforms an arraylist into a 2d array
		userList = UserManipulatorDB.getAllUsers(user.getName());
		String[][] userArray = new String[userList.size()][2];
		for(int i = 0; i < userList.size(); i++)
		{
			userArray[i][0] = userList.get(i).getName();
			int templevel = userList.get(i).getLevel();
			switch(templevel)
			{
			case 0: userArray[i][1] = "User";
					break;
			case 1: userArray[i][1] = "Student";
					break;
			case 2: userArray[i][1] = "Teacher";
					break;
			case 3: userArray[i][1] = "Admin";
					break;
			default:userArray[i][1] = "ERROR";
			}
		}
		
		String[] headers = {"Username", "Permission"};
		
		//create a new table model with new array and replace the old one
		DefaultTableModel model = new DefaultTableModel(userArray, headers);
		usertable.setModel(model);
	}
	
	/**
	 * Method is used to close the frame
	 */
	public void closeFrame()
	{
		this.dispose();
	}
	
	/**
	 * Class responsible for listening for user promotion/demotion
	 * @author Aleksejs Loginovs
	 *
	 */
	public class PromoDemoListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			//get the source and selected user
			JButton source = (JButton) e.getSource();
			int selRow = usertable.getSelectedRow();
			
			//get username of the selected user and set a default action
			String username = userList.get(selRow).getName();
			String action = "nothing";
			
			//set an action appropriately to the button clicked
			if(source.getText().equals("Promote"))
			{
				action = "promote";
			}
			else if(source.getText().equals("Demote"))
			{
				action = "demote";
			}
			
			//send request to the server and get response from it
			int succ = UserManipulatorDB.updateUserList(username, action);
			if(succ == 1)
			{
				updateWindow();
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Failed to change user permission");
			}
		}
	}
}
