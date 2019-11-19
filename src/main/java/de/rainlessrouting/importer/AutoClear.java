package de.rainlessrouting.importer;

import java.io.File;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

import de.rainlessrouting.common.util.DateTimeFormatter;

public class AutoClear {

	private File localDataDirectory;
	private long nextDeleteTimestamp;
	private long millis;
	
	public AutoClear(int minutes)
	{
		localDataDirectory = new File(Paths.get(System.getProperty("user.dir"), "data").toString());
		millis = minutes * 60 * 1000;
		
		Timer timer = new Timer("RainlessRoutingImporter-AutoClear");
		TimerTask tt = new TimerTask() {
			
			public void run() {
				deleteFiles();
			}
		};
		timer.schedule(tt, millis, 1000*60);
		System.out.println("AutoClear scheduled in " + minutes + " minutes, delete files older than " + minutes + " minutes");
	}
	
	private void deleteFiles()
	{
		File[] files = localDataDirectory.listFiles();
		
		for (int i=0; i < files.length; i++)
		{
			if (isFileDelete(files[i]))
			{
				System.out.println("AutoClear.deleteFile: now=" + DateTimeFormatter.getDateTimeSeconds(System.currentTimeMillis()) + " file=" + files[i].getName());
				files[i].delete();
			}
		}
	}

	private boolean isFileDelete(File file) {
		long now = System.currentTimeMillis();
		long old = now - millis;
		
		// delete file if it is older than 'old'
		
		String fileName = file.getName();
		String timestamp = fileName.substring(0, fileName.indexOf(" "));
		long fileTime = Long.parseLong(timestamp);
		
		if (fileTime < old)
			return true;
		else
			return false;
	}
}
