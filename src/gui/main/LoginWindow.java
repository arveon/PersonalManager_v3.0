package gui.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JTextField;

import db.main.LoadGlobalAppointmentsDB;
import db.main.LoadPrivateAppointmentsDB;
import db.main.UserManipulatorDB;
import logics.main.User;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * Class represents and handles login window and is the first window user is presented with in the program
 * @author Aleksejs Loginovs
 */
public class LoginWindow extends JFrame implements ActionListener, KeyListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//inner system variables
	private boolean visible = false;
	private boolean pressed = false;
	
	//variables for elements in the window
	private JTextField login;
	private JPasswordField password;
	private JButton submit;
	private JButton exit;
	private JLabel loginLabel;
	private JLabel passwordLabel;
	private JPanel content;
	
	/**
	 * Default constructor that sets up the window and adds action listeners to buttons
	 */
	public LoginWindow()
	{		
		//set up the content panel
		addKeyListener(this);
		content = new JPanel();
		content.setLayout(new GridBagLayout());
		content.addKeyListener(this);
		GridBagConstraints constraints = new GridBagConstraints();
		
		//labels
		loginLabel = new JLabel("Login:", JLabel.LEFT);
		passwordLabel = new JLabel("Password:", JLabel.LEFT);
		
		//fields
		login = new JTextField();
		login.addKeyListener(this);
		login.setPreferredSize(new Dimension(200,22));
		
		password = new JPasswordField();
		password.addKeyListener(this);
		password.setPreferredSize(new Dimension(200, 22));
		
		//buttons
		submit = new JButton("Login");
		submit.addKeyListener(this);
		submit.setPreferredSize(new Dimension(70,25));
		submit.addActionListener(this);
		
		exit = new JButton("Exit");
		exit.addKeyListener(this);
		exit.setPreferredSize(new Dimension(70,25));
		exit.addActionListener(this);
		
		//add the components to appropriate places on the panel
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(0,5,5,0);
		constraints.gridx = 0;
		content.add(loginLabel, constraints);
		
		constraints.gridy = 1;
		content.add(passwordLabel, constraints);
		
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 3;
		content.add(login, constraints);
		
		constraints.gridy = 1;
		content.add(password, constraints);
		
		constraints.insets = new Insets(10,5,0,0);
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		content.add(exit, constraints);
		
		constraints.gridx = 2;
		constraints.gridy = 2;
		content.add(submit, constraints);
		
		//set up the window
		pack();
		setLocationRelativeTo(null);
		add(content);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Personal Manager - Login");
		setResizable(false);
		setSize(300, 200);
	}
	
	/**
	 * Method is called when the button is clicked
	 */
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == exit)
			System.exit(-1);
		else if(event.getSource() == submit)
		{
			login();
		}
	}
	
	/**
	 * Method is called when user is trying to log in and handles the logging in logic
	 */
	public void login()
	{		
		if(login.getText().length() == 0 || password.getPassword().length == 0)
		{
			JOptionPane.showMessageDialog(null, "Username and password fields can't be empty");
			return;
		}
		
		//stores value from the password field as a string variable
		char[] passw = password.getPassword();
		String tempP = "";
		int i = 0;
		while(i < passw.length)
		{
			tempP +=passw[i];
			i++;
		}
		
		//checks if login attempt was successful and proceeds to the main window if it was
		System.out.println(tempP);
		User user = UserManipulatorDB.login(login.getText(), tempP);
		//if successful, open the main window and close this one
		if(user != null)
		{
			MainWindow mainWindow = new MainWindow(LoadPrivateAppointmentsDB.loadList(login.getText()), LoadGlobalAppointmentsDB.loadList(), user);
			dispose();
			mainWindow.toggleVisible();
		}
		else
		{//if unsuccessful, display the message
			JOptionPane.showMessageDialog(null, "Login unsuccessful");;//displays a message
		}
		
		//reset the password variables to erase them from the memory
		tempP = "";
		password.setText("");
		for(int counter = 0; counter < passw.length; counter++)
		{
			passw[counter] = 0;
		}
	}
	
	/**
	 * Toggles the visibility
	 */
	public void toggleVisible()
	{
		setVisible(!visible);
	}

	@Override
	public void keyPressed(KeyEvent event) 
	{		
		if(!pressed)
		{
			switch(event.getKeyCode())
			{
			case KeyEvent.VK_ENTER: login();
									pressed = true;
									break;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) 
	{
		pressed = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {}
}
