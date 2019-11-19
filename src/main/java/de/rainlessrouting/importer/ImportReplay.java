package de.rainlessrouting.importer;

import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.rainlessrouting.common.util.DateTimeFormatter;


public class ImportReplay {

	public final static String GRID_ID = "replay";
	
	private File localDataDirectory;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm");
	
	private List<String> fileList;
	
	private ImportFile importFile;
	
	public ImportReplay(String fileDir, long startTimestamp, long endTimestamp, long period, boolean clearDB)
	{
		init(fileDir, startTimestamp, endTimestamp, period, clearDB);
	}
	
	private void init(String fileDir, long startTimestamp, long endTimestamp, long period, boolean clearDB)
	{
		fileList = new LinkedList<String>();
		
		if (fileDir == null)
			localDataDirectory = new File(Paths.get(System.getProperty("user.dir"), "data").toString());
		else
		{
			localDataDirectory = new File(fileDir);
		}
		
		if (!localDataDirectory.exists())
		{
			System.err.println("ImportReplay.init: Directory '" + localDataDirectory + "' does not exist.");
			return;
		}

		File[] files = localDataDirectory.listFiles();
		for (int i=0; i < files.length; i++)
		{
			File oneFile = files[i];
			String fileName = oneFile.getName();
			if (fileName.endsWith(".png"))
				continue;
			
			long timestamp = Long.parseLong(fileName.substring(0, fileName.indexOf(" ")));
			
			if ((timestamp >= startTimestamp) && (timestamp <= endTimestamp))
				fileList.add(oneFile.getAbsolutePath());
		}
		
		Collections.sort(fileList);
		
		System.out.println("ImportReplay: Found " + fileList.size() + " files to replay: " + fileList);
		if (fileList.size() == 0)
		{
			System.err.println("ImportReplay: No files found in '" + localDataDirectory + "' for time range " + DateTimeFormatter.getDateTimeSeconds(startTimestamp) + " -> " + DateTimeFormatter.getDateTimeSeconds(endTimestamp));
			return;
		}
		
		importFile = new ImportFile(clearDB);
		
		Timer timer = new Timer("RainlessRoutingImporter-ReplayData");
		TimerTask tt = new TimerTask() {
			
			int counter = 0;
			
			public void run() 
			{
				try 
				{
					String fileName = fileList.get(counter);
					System.out.println("ImportReplay: #" + counter + ": " + fileName);
					
					importFile.importData(fileName, GRID_ID);
					if (counter == fileList.size()-1) // we reached the end of the list
						timer.cancel();
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
					System.err.println("ImportReplay.importData: Failed to import: " + e.getMessage());
				}
				
				counter++;
			}
		};
		timer.schedule(tt, 0, period);
	}
}
