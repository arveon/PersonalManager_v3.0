package db.main;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import logics.main.Appointment;
import logics.main.AppointmentList;

/**
 * Class handles saving the appointments to the database
 * @author arveon
 *
 */
public class SavePrivateAppointmentsDB 
{
	/**
	 * Method used to send given appointment list and username to the server
	 * @param list list of of appointments to be saved
	 * @param user user to save appointments to
	 */
	public static int saveApps(AppointmentList list, String user)
	{
		int success = 0;
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
				String[] msg = {"jsonlist="+jsonlist.toString(), "&user="+user};
				
				//send the list to the server
				ArrayList<String> response = DBInteraction.communicate(msg, DBInteraction.connect(ActionURLs.UPLOAD_PRIVATE_URL));
				success = Integer.parseInt(response.get(0));
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
	
	
}
