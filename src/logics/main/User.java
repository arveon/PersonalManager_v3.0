package logics.main;

import java.util.ArrayList;

public class User 
{
	private String name;
	private int level;
	private ArrayList<String> teachers;
	
	/**
	 * Constructor that initialises fields if user doesn't have teachers
	 * @param name username of the user
	 * @param level permission level of the user
	 */
	public User(String name, String level)
	{
		this.name = name;
		this.level = Integer.parseInt(level);
	}
	
	/**
	 * Constructor that initialises fields if the user has teachers
	 * @param name
	 * @param level
	 */
	public User(String name, String level, ArrayList<String> teacher)
	{
		this.teachers = teacher;
		this.name = name;
		this.level = Integer.parseInt(level);
	}
	
	/**
	 * Returns a level of the access to the program user has
	 * @return permission level
	 */
	public int getLevel()
	{
		return level;
	}
	
	/**
	 * Returns the name of the user
	 * @return user name
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Sets user permission level to the one given
	 * @param level new permission level
	 */
	public void setLevel(int level)
	{
		this.level = level;
	}
	
	/**
	 * Sets the name of the user to the given one
	 * @param name new user name
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Get the ArrayList of teachers who's classes user is in
	 * @return arraylist of user's teachers
	 */
	public ArrayList<String> getTeacherNames()
	{
		return teachers;
	}
	
	
}
