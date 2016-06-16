package SPAREBITS;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import db.main.UserManipulatorDB;
import logics.menus.UserStatistics;

public class test extends JDialog
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JPanel content;
	private JLabel today;
	private JLabel month;
	private JLabel all;
	private JLabel username;
	private JButton ok;
	
	public test(String user)
	{
		UserStatistics statistics = UserManipulatorDB.getStatistics(user);
		username = new JLabel("Username: " + user);
		content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
		
		
		today = new JLabel("Appointments today: " + statistics.getToday());
		month = new JLabel("Appointments this month: " + statistics.getMonth());
		all = new JLabel("All appointments: " + statistics.getAll());
		
		ok = new JButton("OK");
		
		content.add(username);
		content.add(today);
		content.add(month);
		content.add(all);
		
		content.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		content.add(ok);
		ok.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				closeFrame();
			}
		});
		
		this.add(content);
		this.pack();
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setVisible(true);
	}
	
	public void closeFrame()
	{
		this.dispose();
	}
	
	public static void main(String[] args)
	{
		new test("admin");
		
		
	}
	
}