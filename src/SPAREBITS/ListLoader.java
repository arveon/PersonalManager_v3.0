package SPAREBITS;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;

import java.util.ArrayList;

import logics.main.Appointment;
import logics.main.AppointmentList;

/**
 * This class handles list loading and saving
 * 
 * REFERENCE CLASS, NOT USED IN THE PROGRAM
 * 
 * @author Aleksejs Loginovs
 */
public class ListLoader 
{
	/**
	 * Method used to save given appointment list to the default file
	 * @param list represents a list of appointments to be saved
	 * @return true if saving was successful and false if it was unsuccessful
	 */
	public static boolean saveList(AppointmentList list, String username)
	{
		boolean success = false;
		String filePath  = username + ".list";
		
		FileOutputStream stream = null;
		PrintWriter writer = null;
		
		try
		{
			stream = new FileOutputStream(filePath, false);
			writer = new PrintWriter(stream);
			ArrayList<Appointment> appArray = list.getAllAppointments();
			
			for(Appointment temp: appArray)
			{
				writer.println("Description: " + temp.getDescription());
				writer.println("Place: " + temp.getPlace());
				writer.println("Date: " + temp.getDate());
				writer.println("Time: " + temp.getTime());
			}
			
			writer.close();
			success = true;
		}
		catch(IOException e)
		{
			success = false;
		}
		
		return success;
	}
	
	/**
	 * The method is used to load appointment list from the default load file
	 * @return the list of appointments from file or null if there are none
	 */
	public static AppointmentList loadList(String username)
	{
		AppointmentList list = new AppointmentList();
		String filePath = username + ".list";
		
		FileReader stream = null;
		BufferedReader reader = null;
		
		File file = new File(filePath);
		if(file.exists())
		{
			try
			{
				//establish connection with the file
				stream = new FileReader(filePath);
				reader = new BufferedReader(stream);
				
				//start reading
				String line = reader.readLine();
				while(line != null)
				{
					String[] data;
					String description = "EMPTY";
					String place = "EMPTY";
					String date = "00000000";
					String time = "0000";
					if(line.substring(0, 11).equals("Description"))
					{//load the description
						data = line.split(": ");
						if(data.length >1)
						{
							description = data[1];
						}
						line = reader.readLine();//read the next line
					}
					
					if(line.substring(0,5).equals("Place"))
					{//load the place
						data = line.split(": ");
						if(data.length > 1)
						{
							place = data[1];
						}
						line = reader.readLine();
					}
					
					if(line.substring(0,4).equals("Date"))
					{//load the date
						data = line.split(": ");
						if(data.length > 1)
						{
							date = data[1];
						}
						line = reader.readLine();
					}
					
					if(line.substring(0,4).equals("Time"))
					{//load the time
						data = line.split(": ");
						if(data.length > 1)
						{
							time = data[1];
						}
						line = reader.readLine();
					}
					//create a new appointment from the data loaded and add it to the list
					list.addAppointment(new Appointment(description, place, date.substring(0, 4), date.substring(4,6), date.substring(6,8), time.substring(0, 2), time.substring(2,4), "asd"), list.getRoot());
				}
			}
			catch(IOException e)
			{
				System.out.println("File wasn't loaded");
			}
		}
		return list;
	}
}
