package db.main;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import logics.main.User;
import logics.menus.UserStatistics;

public class UserManipulatorDB
{
	/**
	 * Method that is used to check if the login attempt was successful or not
	 * @param login the username under which user is trying to log in
	 * @param password the password with which user is trying to log in
	 * @return the user object if such user exists in the database or null, if not
	 */
	public static User login(String login, String password)
	{
		//initialise all the variables that are going to be used
		User result = null;
		ArrayList<String> response = new ArrayList<String>();
		String[] data = new String[2];
		
		//set the message to the server
		data[0] = "username="+login;
		data[1] = "&password="+password;
		//send and receive data to and from the server
		response = DBInteraction.communicate(data, DBInteraction.connect(ActionURLs.LOGIN_URL));
		
		if(!response.get(0).equals("failed"))
		{
			try
			{
				JSONObject user = new JSONObject(response.get(0));
				if(!user.has("teachers"))
				{
					result = new User((String)user.get("username"), (String)user.get("level"));
				}
				else
				{
					String teacherStr = (String)user.get("teachers");
					String[] teacherArr = teacherStr.split(", ");
					ArrayList<String> teacherList = new ArrayList<String>();
					for(int i = 0; i < teacherArr.length; i++)
					{
						teacherList.add(teacherArr[i]);
					}
					result = new User((String)user.get("username"), (String)user.get("level"), teacherList);
				}
			}
			catch(JSONException e)
			{
				System.out.println("Failed to extract user form JSON");
			}
		}
		
		return result;
	}
	
	/**
	 * Program gets all existing users from the database
	 * @param username the name of the currently logged in user
	 * @return the list of users in the database (except the user who is currently logged in)
	 */
	public static ArrayList<User> getAllUsers(String username)
	{
		ArrayList<User> list = new ArrayList<User>();
		ArrayList<String> response = new ArrayList<String>();
		String[] msg = {"username = " + username};
		
		response = DBInteraction.communicate(msg, DBInteraction.connect(ActionURLs.LOAD_USERS));
		
		try
		{
			JSONArray userJSONArray = new JSONArray(response.get(0));
			if(userJSONArray.length() > 1)
			{//more than one user
				for(int i = 0; i < userJSONArray.length(); i++)
				{
					JSONObject tempUser = userJSONArray.getJSONObject(i);
					list.add(new User(tempUser.getString("username"), tempUser.getString("level")));
				}
			}
			else if(userJSONArray.length() == 1)
			{//just one user in the array
				JSONObject tempUser = userJSONArray.getJSONObject(0);
				list.add(new User(tempUser.getString("username"), tempUser.getString("level")));
			}
			
			//remove the currently logged in user from the user arraylist
			int i = 0;
			while(i < list.size())
			{
				if(list.get(i).getName().equals(username))
				{
					list.remove(i);
					break;
				}
				i++;
			}
			
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
	
	/**
	 * Get the list of users who have a permission level of 0 or 1
	 * @param username name of the currently logged in user
	 * @return the arraylist of users
	 */
	public static ArrayList<User> getAllTeachableUsers(String username)
	{
		ArrayList<User> list = new ArrayList<User>();
		ArrayList<String> response = new ArrayList<String>();
		String[] msg = {"username = " + username};
		
		response = DBInteraction.communicate(msg, DBInteraction.connect(ActionURLs.LOAD_USERS));
		
		try
		{
			JSONArray userJSONArray = new JSONArray(response.get(0));
			if(userJSONArray.length() > 1)
			{//more than one user
				for(int i = 0; i < userJSONArray.length(); i++)
				{
					JSONObject tempUser = userJSONArray.getJSONObject(i);
					list.add(new User(tempUser.getString("username"), tempUser.getString("level")));
				}
			}
			else if(userJSONArray.length() == 1)
			{//just one user in the array
				JSONObject tempUser = userJSONArray.getJSONObject(0);
				list.add(new User(tempUser.getString("username"), tempUser.getString("level")));
			}
			
			//remove users who have a permission level higher than one and the user who is logged in as well
			for(int i = 0; i < list.size(); i++)
			{
				if(list.get(i).getName().equals(username) || list.get(i).getLevel() >=2)
				{
					list.remove(i);
					i--;
				}
			}
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * Method is used to update the permissions of one of the users in the database
	 * @param username the users who's permissions are to be updated
	 * @param action promote/demote the user
	 * @return 0 - action unsuccessful, 1 - action successful
	 */
	public static int updateUserList(String username, String action)
	{
		int result = 0;
		try
		{
			//form the request, send it, get the respoinse
			JSONObject user = new JSONObject();
			user.put("username", username);
			user.put("action", action);
			System.out.println(username);
			String[] msg = {"stuff=" +user.toString()};
			ArrayList<String> response = DBInteraction.communicate(msg, DBInteraction.connect(ActionURLs.UPLOAD_USERS));

			result = Integer.parseInt(response.get(0));
		}
		catch(JSONException ex)
		{
			System.out.println("Weird stuff just happened");
		}
		catch(NumberFormatException e)
		{
			System.out.println("Incomprehensible response from the server");
		}
		return result;
	}
	
	/**
	 * Method is used to get statistics of the given user
	 * @param user the user who's statistics are to be loaded
	 * @return the UserStatistics object
	 */
	public static UserStatistics getStatistics(String user)
	{
		UserStatistics result = new UserStatistics();
		String[] msg = {"user="+user};
		ArrayList<String> response = DBInteraction.communicate(msg, DBInteraction.connect(ActionURLs.GET_USER_STATISTICS));
		try
		{//parse the infomation from the server response
			JSONObject statisticsJSON = new JSONObject(response.get(0));
			result = new UserStatistics(statisticsJSON.get("today").toString(), statisticsJSON.get("month").toString(), statisticsJSON.get("all").toString());
		}
		catch(JSONException e)
		{
			System.out.println(e.getMessage());
		}
		
		return result;
	}
}
