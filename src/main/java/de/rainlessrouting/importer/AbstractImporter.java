package de.rainlessrouting.importer;

import java.io.IOException;
import java.text.SimpleDateFormat;

import de.rainlessrouting.common.db.DBGeoGrid;
import de.rainlessrouting.common.db.DBGridInfo;
import de.rainlessrouting.common.db.DBValueGrid;
import de.rainlessrouting.common.db.IDatabaseHandler;
import de.rainlessrouting.common.db.SqliteHandler;
import de.rainlessrouting.common.util.DateTimeFormatter;
import de.rainlessrouting.common.util.Logger;
import ucar.ma2.Array;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public abstract class AbstractImporter {

	protected IDatabaseHandler dbHandler;
	protected NetcdfFile ncFile = null;
	protected Variable variable = null;
	
	public AbstractImporter(boolean clearDB)
	{
		initDB(clearDB);
	}
	
	public abstract void importData(String filePath, String gridId) throws Exception;
	public abstract void importData(String[] filePaths, String gridId) throws Exception;
	
	private void initDB(boolean clearDB)
	{
		dbHandler = new SqliteHandler();
		
		try
		{
			System.out.println("AbstractImporter.initDB: init DB & delete all data & create new table");
			dbHandler.init();
			if (clearDB)
				dbHandler.dropTables();
			dbHandler.createTables();
		}
		catch(Exception exc)
		{
			System.err.println("AbstractImporter.initDB: failed to init DB :-( " + exc.getMessage());
			return;
		}
	}
	
	protected DBGridInfo importGridInfo(String filePath, String gridId) throws IOException
	{
		ncFile = NetcdfFile.open(filePath);
		
		variable = ncFile.findVariable("lat");
		Array latitudes = variable.read();
		
		variable = ncFile.findVariable("lon");
		Array longitudes = variable.read();
		
//		System.out.println("AbstractImporter: NetCDF file contains: " + latitudes.getSize() + " Lats, " + longitudes.getSize() + " Longs");
		
		DBGeoGrid grid = new DBGeoGrid((double[])latitudes.copyTo1DJavaArray(), (double[])longitudes.copyTo1DJavaArray());
		
		ncFile.close();
		
		return new DBGridInfo(gridId, grid.getWidth(), grid.getHeight(), grid);
	}

	protected DBValueGrid[] importValueGrids(String filePath, String gridId, DBGridInfo gridInfo) throws IOException
	{
		ncFile = NetcdfFile.open(filePath);
		
		variable = ncFile.findVariable("time");
		Array times = variable.read(); // timesmay look like 0 29 60 90 120  ... or days since 1970 e.g. 18055,1234567

		variable = ncFile.findVariable("rain_rate");
		Array values = variable.read();
		
//		System.out.println("AbstractImporter: NetCDF file contains: " + times.getSize() + " times and " + values.getSize() + " values");
		
		DBValueGrid[] gridArray = new DBValueGrid[(int)times.getSize()];
		
		// netcdf files contain date in days after 01.01.1970
		double day = 24 * 60 * 60 * 1000;
		long timestamp = (long)(times.getDouble(0) * day);
		
		Logger.logln("netcdf: " + timestamp + " " + DateTimeFormatter.getDateTimeSeconds(timestamp));
		Logger.log("--------");
		System.out.println(Logger.getString());
		Logger.clear();
		
		// System.out.println("Diff between time and time: " + (timestamp - timesta));
		
		// in case times.size > 1, then values contains more than only one grid, but times * grid.
		for (int t=0; t < times.getSize(); t++)
		{
			double[][] v = new double[gridInfo.getHeight()][gridInfo.getWidth()];
			for (int latIndex=0; latIndex < gridInfo.getHeight(); latIndex++)
			{
				for (int longIndex=0; longIndex < gridInfo.getWidth(); longIndex++)
				{
					int valueIndex = ((latIndex * gridInfo.getWidth()) + longIndex) * (t+1);
					double myV = values.getDouble(valueIndex); // note the t, it is for the time;
					// for whatever reason it is possible that values are NaN. Set these to -1
					if (Double.isNaN(myV))
						myV = -1;
					v[latIndex][longIndex] = myV;
				}
			}
			
			// create a ValueGrid for the time t
			gridArray[t] = new DBValueGrid(timestamp, 0, gridId, v); // attention: offset is set to 0, set to x if forecast is enabled
			
		}
		
		ncFile.close();
		
		return gridArray;
	}
}
