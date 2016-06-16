package db.main;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import logics.main.Appointment;
import logics.main.AppointmentList;

/**
 * Class used to handle the loading the list of appointments from the server
 * @author Aleksejs Loginovs
 *
 */
public class LoadPrivateAppointmentsDB 
{
	/**
	 * Method that is used to load the list of the appointments from the server and
	 * process them into an AppointmentList object
	 * @param username the username of the user trying to access the list
	 * @return an AppointmentList object that represents the loaded list of appointments
	 */
	public static AppointmentList loadList(String username)
	{
		AppointmentList list = new AppointmentList();
		String[] name = {"username="+username};
		//communicate with the server
		ArrayList<String> response = DBInteraction.communicate(name,DBInteraction.connect(ActionURLs.LOAD_PRIVATE_URL));
		

		//if the list sent by the server is not empty
		if(!response.get(0).equals("empty"))
		{
			try
			{
				JSONArray appJsonList = new JSONArray(response.get(0));
				//pull separate appointments from the json appointment array and add them to the list
				for(int i = 0; i < appJsonList.length(); i++)
				{
					JSONObject appJson = appJsonList.getJSONObject(i);
					String tempplace = appJson.get("place").toString();
					String temptime = appJson.get("time").toString();
					String tempdate = appJson.get("date").toString();
					String tempdesc = appJson.get("description").toString();
					list.addAppointment(new Appointment(tempdesc, tempplace, tempdate.substring(0, 4), tempdate.substring(4,6), tempdate.substring(6,8), temptime.substring(0, 2), temptime.substring(2,4), username), list.getRoot());
				}
			}
			catch(JSONException e)
			{
				System.out.println("JSONEXCEPTION: " + e.getMessage());
			}
		}
		return list;
	}
}
