package background.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import background.OAuthConstants;
import gui.ApplicationGui;

public class FileIOUtils {

	private final static String FILE_NAME = "donation_totals.txt";
	private final static String FILE_NAME_SEVEN_KARMA = "seven_karma_total.txt";
	private final static String FILE_NAME_SEVEN_SAVE = "seven_save_total.txt";
	
	public static float getStoredValueForLabel(String p_strLabel)
	{
		String strLine = null;
		BufferedReader bufferedReader = null;
		
		try
		{
			FileReader fileReader = new FileReader(FILE_NAME);
			bufferedReader = new BufferedReader(fileReader);
			
			while((strLine = bufferedReader.readLine()) != null)
			{
				if(strLine.contains(p_strLabel + ":"))
				{
					return Float.parseFloat(strLine.substring(p_strLabel.length()+2, strLine.length()));
				}
			}
			bufferedReader.close();
			fileReader.close();
		}
		catch(FileNotFoundException e)
		{
			//It's OK it means the file just hasn't been created yet.
			return 0;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return 0;
		}
		
		return 0;
	}
	
	public static String getLastDonationId()
	{
		String strLine = null;
		BufferedReader bufferedReader = null;
		
		try
		{
			FileReader fileReader = new FileReader(FILE_NAME);
			bufferedReader = new BufferedReader(fileReader);
			
			while((strLine = bufferedReader.readLine()) != null)
			{
				if(strLine.contains(OAuthConstants.LAST_ID + ":"))
				{
					return strLine.substring(OAuthConstants.LAST_ID.length()+1, strLine.length());
				}
			}
			bufferedReader.close();
			fileReader.close();
		}
		catch(FileNotFoundException e)
		{
			//It's OK it means the file just hasn't been created yet.
			return "0";
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return "0";
		}
		
		return "0";
	}
	
	public static void updateFile(HashMap<String, String> p_mapValuesForFile)
	{
		try
		{
			FileWriter fileWriter = new FileWriter(FILE_NAME);
			FileWriter fwSevenKarma = new FileWriter(FILE_NAME_SEVEN_KARMA);
			FileWriter fwSevenSave = new FileWriter(FILE_NAME_SEVEN_SAVE);
			
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			BufferedWriter bwSevenKarma = new BufferedWriter(fwSevenKarma);
			BufferedWriter bwSevenSave = new BufferedWriter(fwSevenSave);
			
			bufferedWriter.write(OAuthConstants.HASHTAG_SEVENKARMA + ":");
			bufferedWriter.write(p_mapValuesForFile.get(OAuthConstants.HASHTAG_SEVENKARMA).toString());
			bufferedWriter.newLine();
			
			bwSevenKarma.write(FormatUtil.getFormattedDollarAmount(Float.parseFloat(p_mapValuesForFile.get(OAuthConstants.HASHTAG_SEVENKARMA))));
			
			bufferedWriter.write(OAuthConstants.HASHTAG_SEVENSAVE + ":");
			bufferedWriter.write(p_mapValuesForFile.get(OAuthConstants.HASHTAG_SEVENSAVE).toString());
			bufferedWriter.newLine();
			
			bwSevenSave.write(FormatUtil.getFormattedDollarAmount(Float.parseFloat(p_mapValuesForFile.get(OAuthConstants.HASHTAG_SEVENSAVE).toString())));
			
			bufferedWriter.write(OAuthConstants.LAST_ID + ":");
//			bufferedWriter.write(p_mapValuesForFile.get(OAuthConstants.LAST_ID).toString());
			bufferedWriter.newLine();
			
			bufferedWriter.close();
			bwSevenKarma.close();
			bwSevenSave.close();
		}
		catch(IOException e)
		{
			ApplicationGui gui = new ApplicationGui();
			gui.setInfoText("Error writing to files.");
		}
	}
}
