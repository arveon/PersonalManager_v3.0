package SPAREBITS;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JOptionPane;

/** 
 * Method used to handle user actions such as login and display errors for these actions
 * @author Aleksejs Loginovs
 *
 */
public class UserHandler 
{
	/**
	 * Method used to check if the login attempt was successful and return an appropriate code to check if it was
	 * @param username username that the user is trying to log in with
	 * @param password password that the user is using to log in	
	 * @return error/success message number
	 */
	public static int loginCheck(String userName, String password)
	{
		int match = 0;
		
		String userfile = "userfile.users";//filepath to the file that contains all the users
		FileReader stream = null;
		BufferedReader reader = null;
		try
		{
			//establish the connection with the file
			stream = new FileReader(userfile);
			reader = new BufferedReader(stream);
			
			//start reading from the file
			String temp = reader.readLine();
			while(temp != null)
			{
				match = 0;
				String[] tempData = temp.split(" - ");//read login and password stored in the file
				if(userName.equals(tempData[0]))
				{//if username matches, compare passwords
					if(password.equals(tempData[1]))
					{//if passwords match message code is 1
						match = 1;
						break;
					}
					else
					{//if passwords don't match message code is 3
						match = 3;
						break;
					}
				}
				else
				{//if usernames don't match message code is 2
					match = 2;
				}
				temp = reader.readLine();//read the next line
			}
			reader.close();//close the reader
			password = "";//reset the password variable to remove it from the memory
		}
		catch(IOException e)
		{
			JOptionPane.showMessageDialog(null, "Failed to acquire user file.");
		}
		
		return match;
	}
	
	/**
	 * Method is used to display the login error/success message
	 * @param number message code
	 */
	public static void loginMessages(int number)
	{
		switch(number)
		{
		case 0: JOptionPane.showMessageDialog(null, "Checking failed.");
				break;
		case 1: JOptionPane.showMessageDialog(null, "You have successfully logged in!");
				break;
		case 2: JOptionPane.showMessageDialog(null, "Non-existing username");
				break;
		case 3: JOptionPane.showMessageDialog(null, "Incorrect password.");
				break;
		}
	}
}
