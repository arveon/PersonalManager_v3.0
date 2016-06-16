package db.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Class contains methods to handle basic interaction with the server
 * @author Aleksejs Loginovs
 *
 */
public class DBInteraction 
{
	public DBInteraction()
	{
		
	}
	
	/**
	 * Method used to establish the connection with the server script
	 * @param the url of the server script you are trying to establish the connection with
	 * @return returns the URLConnection object that represents the connection with the script
	 */
	public static URLConnection connect(String surl)
	{
		URLConnection connection = null;
		try
		{
			URL url = new URL(surl);
			connection = url.openConnection();
			//header that lets the script know that program is trying to connect to it
			connection.setRequestProperty("source", "pm");
		}
		catch(MalformedURLException e)
		{
			System.out.println("Malformed URL: " + e.getStackTrace());
		}
		catch(IOException e)
		{
			System.out.println("Connection error: " + e.getStackTrace());
		}
		
		return connection;
	}
	
	/**
	 * Method used to send request to the script and receive the server's responce
	 * @param thingToSend an array of strings to be sent to the server
	 * @param connection URLConnection object containing the connection to the server
	 * @return an array list of strings that contains the response
	 */
	public static ArrayList<String> communicate(String[] thingToSend, URLConnection connection)
	{
		URLConnection con = connection;
		String[] message = thingToSend;
		
		ArrayList<String> response = new ArrayList<String>();
		try
		{
			con.setDoOutput(true);
			//send information to the server
			PrintStream printer = new PrintStream(con.getOutputStream());
			for(int i = 0; i < message.length; i++)
			{
				printer.print(message[i]);
			}
			
			//receive information from the server
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			int respCounter = 0;
			response.add(reader.readLine());
			while(response.get(respCounter) != null)
			{
				respCounter++;
				response.add(reader.readLine());
			}
		}
		catch(IOException e)
		{
			System.out.println("Error while sending data to server: " + e.getStackTrace());
		}
		
		return response;
	}	
}
