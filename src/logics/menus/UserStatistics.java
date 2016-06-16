package logics.menus;

/**
 * Class used to store user statistics
 * @author Aleksejs Loginovs
 *
 */
public class UserStatistics 
{
	//number of appointments
	private int today;
	private int month;
	private int all;	
	
	/**
	 * Default constructor sets fields to 0
	 */
	public UserStatistics()
	{
		today = 0;
		month = 0;
		all = 0;
	}
	
	/**
	 * Constructor that sets all fields to given values
	 * @param today number of appointments today
	 * @param month number of appointments this month
	 * @param all number of all appointments
	 */
	public UserStatistics(String today, String month, String all)
	{
		this.today = Integer.parseInt(today);
		this.month = Integer.parseInt(month);
		this.all = Integer.parseInt(all);
	}
	
	/**
	 * Gets the number of appointments user has today
	 * @return number of today's appointments
	 */
	public int getToday()
	{
		return today;
	}
	
	/**
	 * Gets the number of appointments user has this month
	 * @return number of this month's appointments
	 */
	public int getMonth()
	{
		return month;
	}
	
	/**
	 * Gets the number of appointments user has
	 * @return number of appointmetns user has
	 */
	public int getAll()
	{
		return all;
	}
	
}
