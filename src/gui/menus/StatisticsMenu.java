package gui.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import db.main.UserManipulatorDB;
import logics.menus.UserStatistics;

/**
 * Class is responsible for displaying current user statistics
 * @author Aleksejs Loginovs
 *
 */
public class StatisticsMenu extends JDialog
{

	private static final long serialVersionUID = 1L;
	
	//window contents
	private JPanel content;
	private JLabel today;
	private JLabel month;
	private JLabel all;
	private JLabel username;
	private JButton ok;
	
	/**
	 * Constructor that opens the dialog and displays statistics on it
	 */
	public StatisticsMenu(String user)
	{
		//load statistics from the database
		UserStatistics statistics = UserManipulatorDB.getStatistics(user);
		//set the content panel
		content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
		
		//set label values
		username = new JLabel("Username: " + user);
		today = new JLabel("Appointments today: " + statistics.getToday());
		month = new JLabel("Appointments this month: " + statistics.getMonth());
		all = new JLabel("All appointments: " + statistics.getAll());
		
		ok = new JButton("OK");
		
		//add content to the frame
		content.add(username);
		content.add(today);
		content.add(month);
		content.add(all);
		content.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		content.add(ok);
		
		//set an action listener for the ok button
		ok.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				closeFrame();
			}
		});
		
		this.add(content);
		//set general dialog properties
		this.setTitle("Statistics - " + user);
		this.setLocationRelativeTo(null);
		this.pack();
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setVisible(true);
	}
	
	/**
	 * Method used to close the frame
	 */
	public void closeFrame()
	{
		this.dispose();
	}
}
