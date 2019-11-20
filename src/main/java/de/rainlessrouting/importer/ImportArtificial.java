package de.rainlessrouting.importer;

import java.util.Timer;
import java.util.TimerTask;

import de.rainlessrouting.common.db.DBGridInfo;
import de.rainlessrouting.common.db.DBValueGrid;
import de.rainlessrouting.common.util.DateTimeFormatter;

public class ImportArtificial extends AbstractImporter {

	private final static long PERIOD = 30000;
	private final static String TEST_FILE = "src/main/resources/testData1.nc";
	private final static String GRID_ID = "test";
	
	private long importCount = 0;
	
	public ImportArtificial(boolean clearDB)
	{
		super(clearDB);
		init();
	}
	
	private void init()
	{
		
		// create real gridinfo (in order to get real coordinates), only the value part will be artificial
		try
		{
			System.out.println("ImportArtificial: Load GridInfo ... ");
			final DBGridInfo gridInfo = importGridInfo(TEST_FILE, GRID_ID);
			System.out.println("ImportArtificial: Store GridInfo " + gridInfo);
			dbHandler.saveGridInfo(gridInfo);
			
			// create and schedule a timer
			Timer timer = new Timer("RainlessRoutingImporter-ArtificialData");
			TimerTask tt = new TimerTask() {
				
				public void run() {
					importData(gridInfo.getHeight(), gridInfo.getWidth());
				}
			};
			timer.schedule(tt, 0L, PERIOD);
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
			return;
		}
	}
	
	private void importData(int height, int width)
	{
		System.out.println("ImportArtificial: Import ValueGrid " + DateTimeFormatter.getTimeSeconds(System.currentTimeMillis()));
		try 
		{
			double[][] values = new double[height][width];
			for (int latIndex=0; latIndex < values.length; latIndex++)
			{
				for (int longIndex=0; longIndex < values[0].length; longIndex++)
				{
					values[latIndex][longIndex] = latIndex * 0.05 + Math.sin(importCount * (Math.PI / 10)); // latIndex is max 240, hence value range is 0 to 12
				}
			}
			
			DBValueGrid valueGrid = new DBValueGrid(System.currentTimeMillis(), 0, GRID_ID, values);
			dbHandler.saveValueGrid(valueGrid);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		importCount++;
		
	}

	public void importData(String[] filePaths, String gridId) {
		throw new RuntimeException("Not implemented! Use importData(int, int)!");
	}

	@Override
	public void importData(String filePath, String gridId) {
		throw new RuntimeException("Not implemented! Use importData(int, int)!");
	}

	
}
