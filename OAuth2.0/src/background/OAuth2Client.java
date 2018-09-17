package background;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import background.util.FileIOUtils;
import background.util.FormatUtil;
import background.util.OAuthUtils;
import gui.ApplicationGui;

public class OAuth2Client
{

	private static float m_fKarmaTotal = 0.00f;
	private static float m_fSaveTotal = 0.00f;
	private static String m_strLastDonationId = null;

	public static void searchServerUpdates(String p_strAPIToken, String p_strFileName)
	{

		if (m_fKarmaTotal == 0.00f)
		{
			FileIOUtils.getStoredValueForLabel(OAuthConstants.HASHTAG_SEVENKARMA);
		}
		if (m_fSaveTotal == 0.00f)
		{
			FileIOUtils.getStoredValueForLabel(OAuthConstants.HASHTAG_SEVENSAVE);
		}
		Properties config = OAuthUtils.getClientConfigProps(OAuthConstants.CONFIG_FILE_PATH);
		config.put("client_id", "UVSUKNBhPt6RYgGQQaLa3SWCD7aw1dzYPRNAL9Tg");
		config.put("client_secret", "FYui2YoEtCDXWw6XNEjfzSP9DPK9jmzOQ2XContZ");
		config.put("authentication_server_url", "https://www.twitchalerts.com/api/v1.0/token");
		config.put("grant_type", "password");
		config.put("username", "kidofseph");
		config.put("password", "Irolld6dmg");
		config.put("scope", "donations.read");
		config.put("resource_server_url",
				"https://www.twitchalerts.com/api/donations?access_token=" + p_strAPIToken + "&currency=USD");
		config.put("access_token", p_strAPIToken);
		String resourceServerUrl = config.getProperty(OAuthConstants.RESOURCE_SERVER_URL);
		String username = config.getProperty(OAuthConstants.USERNAME);
		String password = config.getProperty(OAuthConstants.PASSWORD);
		String grantType = config.getProperty(OAuthConstants.GRANT_TYPE);
		String authenticationServerUrl = config.getProperty(OAuthConstants.AUTHENTICATION_SERVER_URL);

		if (!OAuthUtils.isValid(username) || !OAuthUtils.isValid(password)
				|| !OAuthUtils.isValid(authenticationServerUrl) || !OAuthUtils.isValid(grantType))
		{
			ApplicationGui gui = new ApplicationGui();
			gui.setInfoText(
					"Please provide valid values for username, password, authentication server url and grant type");
			System.exit(0);
		}

		if (m_strLastDonationId == null)
		{

		}
		// Resource server url is not valid. Only retrieve the access token
//		System.out.println("Retrieving Access Token");
		OAuth2Details oauthDetails = OAuthUtils.createOAuthDetails(config);
		ArrayList<HashMap<String, String>> listDonations = OAuthUtils.getProtectedResource(config);
		String strLastDonationId = null;
		if (listDonations != null)
		{
			BigDecimal bdSetSaveAmt = BigDecimal.ZERO;
			BigDecimal bdSetKarmaAmt = BigDecimal.ZERO;
			boolean bFirstPass = true;
			for (HashMap<String, String> donation : listDonations)
			{
				if (donation.get(OAuthConstants.COLUMN_DONATION_MESSAGE).toUpperCase().contains("SEVENSAVE") && 
						!donation.get(OAuthConstants.COLUMN_DONATION_MESSAGE).toUpperCase().contains("SEVENKARMA"))
				{
					bdSetSaveAmt = bdSetSaveAmt.add(new BigDecimal(donation.get(OAuthConstants.COLUMN_DONATION_AMOUNT).replaceAll("\"", "")));
				}
				else if (donation.get(OAuthConstants.COLUMN_DONATION_MESSAGE).toUpperCase().contains("SEVENKARMA") &&
						!donation.get(OAuthConstants.COLUMN_DONATION_MESSAGE).toUpperCase().contains("SEVENSAVE"))
				{
					bdSetKarmaAmt = bdSetKarmaAmt.add(new BigDecimal(donation.get(OAuthConstants.COLUMN_DONATION_AMOUNT).replaceAll("\"", "")));
				}
				else if (donation.get(OAuthConstants.COLUMN_DONATION_MESSAGE).toUpperCase().contains("SEVENKARMA") &&
						donation.get(OAuthConstants.COLUMN_DONATION_MESSAGE).toUpperCase().contains("SEVENSAVE"))
				{
					String strMessage = donation.get(OAuthConstants.COLUMN_DONATION_MESSAGE).toUpperCase();
					StringBuilder sbSearchString = new StringBuilder();
					for(int i = 0; i < strMessage.length(); i++)
					{
						sbSearchString = sbSearchString.append(strMessage.charAt(i));
						if(sbSearchString.toString().contains("SEVENSAVE"))
						{
							bdSetSaveAmt = bdSetSaveAmt.add(new BigDecimal(donation.get(OAuthConstants.COLUMN_DONATION_AMOUNT).replaceAll("\"", "")));
							break;
						}
						else if(sbSearchString.toString().contains("SEVENKARMA"))
						{
							bdSetKarmaAmt = bdSetKarmaAmt.add(new BigDecimal(donation.get(OAuthConstants.COLUMN_DONATION_AMOUNT).replaceAll("\"", "")));
							break;
						}
					}
				}

				if (bFirstPass)
				{
					strLastDonationId = donation.get(OAuthConstants.COLUMN_DONATION_ID);
					bFirstPass = false;
				}
			}

			
			ApplicationGui gui = new ApplicationGui();
			m_fKarmaTotal += bdSetKarmaAmt.floatValue();
			m_fSaveTotal += bdSetSaveAmt.floatValue();
			HashMap<String, String> mapValuesForFile = new HashMap<String, String>();
			mapValuesForFile.put(OAuthConstants.HASHTAG_SEVENKARMA, String.valueOf(m_fKarmaTotal));
			mapValuesForFile.put(OAuthConstants.HASHTAG_SEVENSAVE, String.valueOf(m_fSaveTotal));
			FileIOUtils.updateFile(mapValuesForFile);
			gui.updateSevenSaveAmount(FormatUtil.getFormattedDollarAmount(m_fSaveTotal));
			gui.updateSevenKarmaAmount(FormatUtil.getFormattedDollarAmount(m_fKarmaTotal));

			checkLead(p_strFileName);
		}

	}
	
	private static void checkLead(String p_strFileName)
	{
		ApplicationGui gui = new ApplicationGui();
		if (m_fSaveTotal - m_fKarmaTotal >= 25)
		{
			if (gui.getWinning().equals(ApplicationGui.SEVEN_KARMA) || gui.getWinning().equals(""))
			{
				gui.setWinning(ApplicationGui.SEVEN_SAVE);
				try
				{
					Clip clip = AudioSystem.getClip();
					AudioInputStream ais = AudioSystem
							.getAudioInputStream(new File(p_strFileName).getAbsoluteFile());
					clip.open(ais);
					clip.start();
				}
				catch (LineUnavailableException e)
				{
					gui.setInfoText("Error playing sound!");
				}
				catch (UnsupportedAudioFileException e)
				{
					gui.setInfoText("Error playing sound!");
				}
				catch (IOException e)
				{
					gui.setInfoText("Error playing sound!");
				}
			}
		}
		else if (m_fKarmaTotal - m_fSaveTotal >= 25)
		{
			if (gui.getWinning().equals(ApplicationGui.SEVEN_SAVE) || gui.getWinning().equals(""))
			{
				gui.setWinning(ApplicationGui.SEVEN_KARMA);
				try
				{
					File file = new File(p_strFileName);
					AudioInputStream ais = AudioSystem.getAudioInputStream(file);

					Clip clip = AudioSystem.getClip();
					clip.open(ais);
					clip.start();
				}
				catch (LineUnavailableException e)
				{
					e.printStackTrace();
				}
				catch (UnsupportedAudioFileException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void makeAdjustment(String p_strHashTag, Float p_fAmount, String p_strFileName)
	{
		ApplicationGui gui = new ApplicationGui();
		if(p_strHashTag.equals(OAuthConstants.HASHTAG_SEVENKARMA))
		{
			m_fKarmaTotal += p_fAmount;
			gui.updateSevenKarmaAmount(FormatUtil.getFormattedDollarAmount(m_fKarmaTotal));
			
		}
		else if(p_strHashTag.equals(OAuthConstants.HASHTAG_SEVENSAVE))
		{
			m_fSaveTotal += p_fAmount;
			gui.updateSevenSaveAmount(FormatUtil.getFormattedDollarAmount(m_fSaveTotal));
		}
		
		HashMap<String, String> mapValuesForFile = new HashMap<String, String>();
		mapValuesForFile.put(OAuthConstants.HASHTAG_SEVENKARMA, String.valueOf(m_fKarmaTotal));
		mapValuesForFile.put(OAuthConstants.HASHTAG_SEVENSAVE, String.valueOf(m_fSaveTotal));
		mapValuesForFile.put(OAuthConstants.LAST_ID, FileIOUtils.getLastDonationId());
		FileIOUtils.updateFile(mapValuesForFile);

		checkLead(p_strFileName);
	}
}
