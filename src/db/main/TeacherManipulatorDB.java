package db.main;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import logics.main.Appointment;
import logics.main.AppointmentList;
import logics.main.User;

/**
 * Class is used to perform teacher actions (manage class, manage teacher appointments)
 * @author Aleksejs Loginovs
 *
 */
public class TeacherManipulatorDB 
{
	/**
	 * Method used to get teacher appointments of the appropriate teacher
	 * @param teachername name of the teacher who's appointments are being loaded
	 * @return list of the appointments
	 */
	public static AppointmentList getAppointments(String teachername)
	{
		//forming the request to the server
		AppointmentList result = new AppointmentList();
		String[] message = {"name="+teachername};
		
		//sending the request to the server
		ArrayList<String> response = DBInteraction.communicate(message, DBInteraction.connect(ActionURLs.LOAD_TEACHER_URL));
		
		//try to get information from the response
		try
		{
			JSONArray appJSONarray = new JSONArray(response.get(0));
			//try to treat the response as the array
			for(int i = 0; i < appJSONarray.length(); i++)
			{
				JSONObject tempobj = appJSONarray.getJSONObject(i);
				String tempplace = tempobj.getString("place");
				String temptime = tempobj.getString("time");
				String tempdate = tempobj.getString("date");
				String tempdesc = tempobj.getString("description");
				String tempauthor = tempobj.getString("author");
				Appointment tempapp = new Appointment(tempdesc, tempplace, tempdate.substring(0, 4), tempdate.substring(4,6), tempdate.substring(6,8), temptime.substring(0, 2), temptime.substring(2,4), tempauthor);
				result.addAppointment(tempapp, result.getRoot());
			}
		}
		catch(JSONException e)
		{
			try
			{
				JSONObject appJson = new JSONObject(response.get(0));
				//pull separate appointments from the json appointment array and add them to the list
				String tempplace = appJson.getString("place");
				String temptime = appJson.getString("time");
				String tempdate = appJson.getString("date");
				String tempdesc = appJson.getString("description");
				String tempauthor = appJson.getString("author");
				Appointment tempapp = new Appointment(tempdesc, tempplace, tempdate.substring(0, 4), tempdate.substring(4,6), tempdate.substring(6,8), temptime.substring(0, 2), temptime.substring(2,4), tempauthor);
				result.addAppointment(tempapp , result.getRoot());
			}
			catch(JSONException ex)
			{
				System.out.println("TEACHERJSONEXCEPTION: " + e.getMessage());
			}
		}
		return result;
	}
	
	/**
	 * Method used to save an appointment list as a class appointment list
	 * @param list to be saved
	 * @param teachername name of the teacher who's class appointments those will be
	 * @return 1 - if save successful, 0 - unsuccessful
	 */
	public static int saveAppointments(AppointmentList list, String teachername)
	{		
		int success = 0;
		ArrayList<String> response = new ArrayList<String>();
		try
		{
			JSONArray jsonlist = new JSONArray();
			if(list.getSize()!=0)
			{
				//create a json array out of appointments list
				for(int i = 0; i < list.getSize(); i++)
				{
					//create a json appointment object
					JSONObject jsonapp = new JSONObject();
					Appointment tempapp = list.getAppointmentAt(i);
					jsonapp.put("place", tempapp.getPlace());
					jsonapp.put("date", tempapp.getDate());
					jsonapp.put("time", tempapp.getTime());
					jsonapp.put("description", tempapp.getDescription());
					jsonapp.put("author", tempapp.getAuthor());
					//add json appointment object to a json array
					jsonlist.put(jsonapp);
				}
				//create a string containing a json appointment array and the username
				String[] msg = {"jsonlist="+jsonlist.toString(), "&teachername="+teachername};
				
				for(String temp: msg)
				{
					System.out.println(temp);
				}
				
				//send the list to the server and process the response
				response = DBInteraction.communicate(msg, DBInteraction.connect(ActionURLs.SAVE_TEACHER_URL));
				success = Integer.parseInt(response.get(0).replace(",", ""));
			}
			else
			{
				System.out.println("empty!");
			}
		}
		catch(JSONException e)
		{
			System.out.println("JSONEXCEPTION: " + e.getMessage());
		}
		return success;
	}
	
	/**
	 * The method is used to get the students in given teacher class from the database
	 * @param teachername teacher who's class to be loaded
	 * @return the list of users in this teacher's class
	 */
	public static ArrayList<User> getStudents(String teachername)
	{
		//form a request to the server
		ArrayList<User> studentList = new ArrayList<User>();
		ArrayList<String> response = new ArrayList<String>();
		String[] msg = {"teachername=" + teachername};
		//send the request and get the response
		response = DBInteraction.communicate(msg, DBInteraction.connect(ActionURLs.LOAD_CLASS_URL));
		try
		{//transfrom user json array into an arraylist of users
			JSONArray userJSONArray = new JSONArray(response.get(0));
			for(int i = 0; i < userJSONArray.length(); i++)
			{
				JSONObject tempUser = userJSONArray.getJSONObject(i);
				studentList.add(new User(tempUser.getString("username"), "1"));
			}
		}
		catch(JSONException e)
		{
			try
			{//if the response can't be treatet as a json array try treating it as a json object
				JSONObject student = new JSONObject(response.get(0));
				studentList.add(new User((String)student.get("username"), "1"));
			}
			catch(JSONException ex)
			{
				System.out.println("No students in class");
				studentList = new ArrayList<User>();
			}
		}	
		return studentList;
	}
	
	/**
	 * This method is used to send request to server to add/remove users from the teacher's class
	 * @param student student to be added/removed
	 * @param teachername teacher who's class is to be updated
	 * @param action action to perform with the student
	 * @return 1 - update has been successful, 0 - unsuccessful
	 */
	public static int updateClass(String student, String teachername, String action)
	{
		int result = 0;
		
		try
		{
			//form a request
			JSONObject message = new JSONObject();
			message.put("studentname", student);
			message.put("teacher", teachername);
			message.put("action", action);
			String[] msg = {"object=" + message.toString()};
			//send the request
			DBInteraction.communicate(msg, DBInteraction.connect(ActionURLs.SAVE_CLASS_URL));
		}
		catch(JSONException ex)
		{
			System.out.println("Couldnt add data to a jsonObject");
		}
		catch(NumberFormatException e)
		{
			System.out.println(e.getMessage());
		}
		return result;
	}	
}
