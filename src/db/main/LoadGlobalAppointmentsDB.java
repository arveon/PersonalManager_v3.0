package db.main;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import logics.main.Appointment;
import logics.main.AppointmentList;

/**
 * Class is used to load global appointments from the database
 * @author Aleksejs Loginovs
 *
 */
public class LoadGlobalAppointmentsDB 
{
	public LoadGlobalAppointmentsDB()
	{
		
	}
	
	/**
	 * Method is used to load global appointments from the database
	 * @return list of global appointments
	 */
	public static AppointmentList loadList()
	{
		AppointmentList list = new AppointmentList();
		String[] msg = new String[1];
		//communicate with the server
		ArrayList<String> response = DBInteraction.communicate(msg,DBInteraction.connect(ActionURLs.LOAD_GLOBAL_URL));
		
		System.out.println("RESPONSE");
		for(String rp: response)
		{
			System.out.println(rp);
		}
		System.out.println("ENDRESPOINSE");
		
		//if the list sent by the server is not empty
		if(!response.get(0).equals("empty"))
		{
			try
			{
				JSONArray appJsonList = new JSONArray(response.get(0));
				//pull separate appointments out from the json appointment array and add them to the list
				for(int i = 0; i < appJsonList.length(); i++)
				{
					JSONObject appJson = appJsonList.getJSONObject(i);
					String tempplace = appJson.getString("place");
					String temptime = appJson.getString("time");
					String tempdate = appJson.getString("date");
					String tempdesc = appJson.getString("description");
					String tempauthor = appJson.getString("author");
					list.addAppointment(new Appointment(tempdesc, tempplace, tempdate.substring(0, 4), tempdate.substring(4,6), tempdate.substring(6,8), temptime.substring(0, 2), temptime.substring(2,4), tempauthor), list.getRoot());
				}
			}
			catch(JSONException e)
			{
				try
				{//happens if the response doesn't contain a json array
						JSONObject appJson = new JSONObject(response.get(0));
						//pull separate appointments out from the json appointment object and add them to the list
						String tempplace = appJson.getString("place");
						String temptime = appJson.getString("time");
						String tempdate = appJson.getString("date");
						String tempdesc = appJson.getString("description");
						String tempauthor = appJson.getString("author");
						list.addAppointment(new Appointment(tempdesc, tempplace, tempdate.substring(0, 4), tempdate.substring(4,6), tempdate.substring(6,8), temptime.substring(0, 2), temptime.substring(2,4), tempauthor), list.getRoot());
				}
				catch(JSONException ex)
				{
					System.out.println("GLOBALJSONEXCEPTION: " + e.getMessage());
				}
			}
		}
		return list;
	}
	
	
}
