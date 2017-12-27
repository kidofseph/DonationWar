package gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import background.OAuth2Client;
import background.OAuthConstants;

public class ApplicationGui extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3320657702299522826L;
	private static boolean m_bSearching = true;
	private static String m_strWinning = "";
	private static final JTextArea m_jtaAPIKey = new JTextArea();
	private static final JLabel m_jlCurrentlyWinning = new JLabel("");
	private static final JLabel m_jlSevenSaveAmount = new JLabel("$0.00");
	private static final JLabel m_jlSevenKarmaAmount = new JLabel("$0.00");
	private static String m_strFileName = "";
	private static final JFileChooser m_jfcSoundFile = new JFileChooser();
	private static final JLabel m_jlSoundClipFile = new JLabel("");
	private static final JTextArea m_jtaAdjustmentAmt = new JTextArea();
	private static final JRadioButton m_jrKarma = new JRadioButton("#SEVENKARMA");
	private static final JRadioButton m_jrSave = new JRadioButton("#SEVENSAVE");
	private static final JLabel m_jlInfoText = new JLabel("Test text");
	
	public static final String SEVEN_KARMA = "sevenKARMA";
	public static final String SEVEN_SAVE = "sevenSAVE";
	

	public static void main(String args[])
	{
		m_jfcSoundFile.setFileFilter(new FileNameExtensionFilter("wav files", "wav"));
		
		JFrame jFrame = new JFrame();
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JLabel jlAPIKey = new JLabel("Twitch Alerts API Token:");
		jlAPIKey.setBounds(0, 5, 160, 20);
		jFrame.add(jlAPIKey);

		m_jtaAPIKey.setBounds(165, 5, 180, 20);
		m_jtaAPIKey.setRows(1);
		jFrame.add(m_jtaAPIKey);
		
		
		JLabel jlSoundClipLabel = new JLabel("Sound file: ");
		jlSoundClipLabel.setBounds(0, 30, 65, 20);
		jFrame.add(jlSoundClipLabel);
		
		m_jlSoundClipFile.setBounds(67, 30, 150, 20);
		jFrame.add(m_jlSoundClipFile);
		
		JButton jbBrowse = new JButton("Browse...");
		jbBrowse.addActionListener(getActionSoundBrowse());
		jbBrowse.setBounds(240, 30, 90, 20);
		jFrame.add(jbBrowse);

		JButton jbRun = new JButton("Run");
		jbRun.setBounds(0, 90, 60, 30);
		jbRun.addActionListener(getActionListenerSearch());
		jFrame.add(jbRun);
		
		JButton jbStop = new JButton("Stop");
		jbStop.setBounds(70, 90, 60, 30);
		jbStop.addActionListener(getActionListenerStop());
		jFrame.add(jbStop);
		

		JLabel jlSevenSaveText = new JLabel("#sevenSave Total:");
		jlSevenSaveText.setBounds(0, 125, 120, 20);
		jFrame.add(jlSevenSaveText);
		
		m_jlSevenSaveAmount.setBounds(125, 125, 60, 20);
		jFrame.add(m_jlSevenSaveAmount);
		
		JLabel jlSevenKarmaText = new JLabel("#sevenKarma Total:");
		jlSevenKarmaText.setBounds(0, 140, 120, 20);
		jFrame.add(jlSevenKarmaText);
		
		m_jlSevenKarmaAmount.setBounds(125, 140, 60, 20);
		jFrame.add(m_jlSevenKarmaAmount);
		
		JLabel jlWinningLabel = new JLabel("Currently winning: ");
		jlWinningLabel.setBounds(0, 170, 140, 20);
		jFrame.add(jlWinningLabel);
		
		m_jlCurrentlyWinning.setBounds(145, 170, 140, 20);
		jFrame.add(m_jlCurrentlyWinning);
		
		
		JLabel jlAdjustment = new JLabel("Adjustments:");
		jlAdjustment.setBounds(0, 195, 80, 20);
		jFrame.add(jlAdjustment);
		
		
		m_jtaAdjustmentAmt.setBounds(85, 195, 40, 20);
		jFrame.add(m_jtaAdjustmentAmt);
		
		ButtonGroup bgAdjustments = new ButtonGroup();
		
		
		bgAdjustments.add(m_jrKarma);
		bgAdjustments.add(m_jrSave);
		
		m_jrKarma.setBounds(130, 195, 120, 20);
		m_jrSave.setBounds(250, 195, 120, 20);
		
		jFrame.add(m_jrKarma);
		jFrame.add(m_jrSave);
		
		JButton jbAdjustment = new JButton("Make Adjustment");
		jbAdjustment.setBounds(0, 220, 140, 20);
		jbAdjustment.addActionListener(getActionAdjustment());
		
		JLabel jlInfo = new JLabel("Info");
		jlInfo.setBounds(0, 300, 50, 20);		
		jFrame.add(jlInfo);
		
		m_jlInfoText.setBounds(0, 325, 300, 50);
		m_jlInfoText.setBackground(Color.BLACK);
		m_jlInfoText.setForeground(Color.GREEN);
		m_jlInfoText.setOpaque(true);
		m_jlInfoText.setVerticalAlignment(JLabel.TOP);
		jFrame.add(m_jlInfoText);
		
		jFrame.add(jbAdjustment);
		

		
//		m_jfcChangeSound.setBounds(0, 195, 140, 80);
//		jFrame.add(m_jfcChangeSound);

		jFrame.setSize(400, 500);
		jFrame.setLayout(null);
		jFrame.setVisible(true);

	}

	private static ActionListener getActionListenerSearch()
	{
		return new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				Runnable r = new Runnable()
				{

					@Override
					public void run()
					{

						while (m_bSearching)
						{
							try
							{
								OAuth2Client.searchServerUpdates(m_jtaAPIKey.getText(), m_strFileName);
								Thread.sleep(3000);
							} catch (InterruptedException e)
							{
								m_bSearching = false;
							}
						}
						setInfoText("Stopped");

					}
				};
				new Thread(r).start();
			}
		};
	}

	private static ActionListener getActionListenerStop()
	{
		return new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				m_bSearching = false;
				setInfoText("Stopping...");
			}
		};
	}
	
	private static ActionListener getActionSoundBrowse()
	{
		return new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				int iReturnVal = m_jfcSoundFile.showOpenDialog(null);
				if(iReturnVal == JFileChooser.APPROVE_OPTION)
				{
					File file = m_jfcSoundFile.getSelectedFile();
					m_strFileName = file.getAbsolutePath();
					m_jlSoundClipFile.setText(m_strFileName.substring(m_strFileName.lastIndexOf("\\") + 1));
				}
				
				
				
			}
		};
	}
	
	private static ActionListener getActionAdjustment()
	{
		return new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Float fAdjustmentAmt = Float.parseFloat(m_jtaAdjustmentAmt.getText().replaceAll(",", "").replaceAll("$", "").replaceAll(" ", "").trim());
				if(m_jrKarma.isSelected())
				{
					OAuth2Client.makeAdjustment(OAuthConstants.HASHTAG_SEVENKARMA, fAdjustmentAmt, m_strFileName);
				}
				else if(m_jrSave.isSelected())
				{
					OAuth2Client.makeAdjustment(OAuthConstants.HASHTAG_SEVENSAVE, fAdjustmentAmt, m_strFileName);
				}
				else
				{
					setInfoText("No button selected");
				}
				
			}
		};
	}
	
	public static void updateSevenSaveAmount(String p_strValue)
	{
		m_jlSevenSaveAmount.setText(p_strValue);
	}
	
	public static void updateSevenKarmaAmount(String p_strValue)
	{
		m_jlSevenKarmaAmount.setText(p_strValue);
	}
	
	public static String getWinning()
	{
		return m_strWinning;
	}
	
	public static void setWinning(String p_strValue)
	{
		m_strWinning = p_strValue;
	}
	
	public static String getSoundFileName()
	{
		return m_strFileName;
	}
	
	public static void setInfoText(String p_strInfoText)
	{
		m_jlInfoText.setText(p_strInfoText);
	}

}
