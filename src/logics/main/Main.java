package logics.main;

import gui.main.LoginWindow;

/**
 * Class contains the main method used to launch the program
 * @author Aleksejs Loginovs
 *
 */
public class Main 
{
	public static void main(String[] args)
	{
		LoginWindow login = new LoginWindow();
		login.toggleVisible();
	}
}
