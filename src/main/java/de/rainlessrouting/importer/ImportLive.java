package de.rainlessrouting.importer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import de.rainlessrouting.common.util.DateTimeFormatter;
import de.rainlessrouting.common.util.Logger;


public class ImportLive {

	private final static String KEY_SERVER = "server";
	private final static String KEY_PORT = "port";
	private final static String KEY_USER = "user";
	private final static String KEY_PASSWORD = "password";
	private final static String KEY_PERIOD = "period";
	private final static String KEY_GRIDID = "gridid";
	
	private final static String REMOTE_DATA_FILE = "/lawr_latlon.nc";
	private final static String REMOTE_IMAGE_FILE = "/composite.png";
	
	private File localDataDirectory;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	
	private String server;
	private int port;
	private String user;
	private String password;
	private long period;
	private String gridId;
	
	private FtpClient ftpClient;
	private String recentModificationTime;
	private String recentFilePrefix;
	
	private ImportFile importFile;
	
	public ImportLive(String propertyFile, boolean clearDB, boolean clearDataDirectory)
	{
		init(propertyFile, clearDB, clearDataDirectory);
	}
	
	private void init(String propertyFile, boolean clearDB, boolean clearDataDirectory)
	{
		localDataDirectory = new File(Paths.get(System.getProperty("user.dir"), "data").toString());
		if (!localDataDirectory.exists())
			localDataDirectory.mkdir();
		else if (clearDataDirectory)
		{
			File[] files = localDataDirectory.listFiles();
			System.out.println("ImportLive: Delete " + files.length + " files...");
			for (int i=0; i < files.length; i++)
			{
				files[i].delete();
			}
		}
			
		String propertyPath = Paths.get(System.getProperty("user.dir"), propertyFile).toString();
		Properties props = new Properties();
		try 
		{
			props.load(new FileInputStream(propertyPath));
			server = (String)props.get(KEY_SERVER);
			port = Integer.parseInt((String)props.get(KEY_PORT));
			user = (String)props.get(KEY_USER);
			password = (String)props.get(KEY_PASSWORD);
			period = Long.parseLong((String)props.get(KEY_PERIOD));
			gridId = (String)props.get(KEY_GRIDID);
			
			importFile = new ImportFile(clearDB);
		} 
		catch (Exception e) 
		{
			props.put(KEY_SERVER, "ftp.example.net");
			props.put(KEY_PORT, "21");
			props.put(KEY_USER, "username");
			props.put(KEY_PASSWORD, "password");
			props.put(KEY_PERIOD, "20000");
			props.put(KEY_GRIDID, "live");
			
			try 
			{
				props.store(new FileOutputStream(propertyPath), "Properties for FTP-Server");
				throw new RuntimeException("Credentials for FTP-server have not been been specified. Please configure in '" + propertyPath + "'");
			} 
			catch (Exception e2) 
			{
				e2.printStackTrace();
			}
		}
		
		// file on FTP gets updated on sec 00 and 30
		long delay = 0;
		long sec = Calendar.getInstance().get(Calendar.SECOND);
		if (sec < 5)
			delay = 5000 - (sec * 1000);
		else if ((sec > 30) && (sec < 35))
			delay = 35000 - (sec * 1000);
		
		Timer timer = new Timer("RainlessRoutingImporter-LiveData");
		TimerTask tt = new TimerTask() {
			
			public void run() {
				boolean newDataAvailable = download();
				if (newDataAvailable)
				{
					try 
					{
						importFile.importData(recentFilePrefix + ".nc", gridId);
					} 
					catch (Exception e) 
					{
						System.err.println("ImportLive.importData: Failed to import: " + e.getMessage());
					}
				}
			}
		};
		timer.schedule(tt, delay, period);
	}
	
	private boolean download() 
	{
		boolean newDataAvailable = false;
		try 
		{
//			System.out.println("ImportLiveData.download: Connect FTP to " + server + ":" + port + " using " + user + "/****");

			ftpClient = new FtpClient(server, port, user, password);
			ftpClient.open();

			String remoteModificationTime = ftpClient.getModificationTime(REMOTE_DATA_FILE);

			if (!remoteModificationTime.equals(recentModificationTime)) 
			{
//				System.out.println("ImportLiveData.download: New data available, download data and image ...");
				newDataAvailable = true;
				
				Date d = new Date(Long.parseLong(remoteModificationTime));
				String s = sdf.format(d);
				Logger.logln("downlo: " + System.currentTimeMillis() + " " + DateTimeFormatter.getDateTimeSeconds(System.currentTimeMillis()));
				Logger.log("remote: " + remoteModificationTime + " " + DateTimeFormatter.getDateTimeSeconds(Long.parseLong(remoteModificationTime)));
				System.out.println(Logger.getString());
				Logger.clear();
				recentFilePrefix = localDataDirectory.getPath() + File.separatorChar + remoteModificationTime + " (" + s + ")";
				
				ftpClient.downloadFile(REMOTE_DATA_FILE, recentFilePrefix + ".nc");
				ftpClient.downloadFile(REMOTE_IMAGE_FILE, recentFilePrefix + ".png");
				
				recentModificationTime = remoteModificationTime;
			} 
			else 
			{
				System.out.println("ImportLiveData.download: No new data available (timestamp=" + remoteModificationTime + ")");
			}
			
		} 
		catch (Exception e) {

			e.printStackTrace();
		}
		finally
		{
			try 
			{
				ftpClient.close();
			} 
			catch (Exception e) { }
		}
		
		return newDataAvailable;
	}
	
//	public static void main(String[] args)
//	{
//		ImportLive importer = new ImportLive();
//		importer.init("ftp.properties");
//		importer.download();
//	}
}
