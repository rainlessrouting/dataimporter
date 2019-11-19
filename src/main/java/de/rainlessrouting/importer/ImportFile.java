package de.rainlessrouting.importer;

import de.rainlessrouting.common.db.DBGridInfo;
import de.rainlessrouting.common.db.DBValueGrid;

public class ImportFile extends AbstractImporter {

	public ImportFile(boolean clearDB)
	{
		super(clearDB);
	}

    public void importData(String filePath, String gridId) throws Exception {
		
		importData(new String[] {filePath}, gridId);
	}
	
	public void importData(String[] filePaths, String gridId) throws Exception {
		
		DBGridInfo gridInfo = importGridInfo(filePaths[0], gridId);
		
		System.out.println("ImportFile: Store GridInfo " + gridInfo);
		dbHandler.saveGridInfo(gridInfo);
		
		for (int f=0; f < filePaths.length; f++)
		{
			DBValueGrid[] valueGrids = importValueGrids(filePaths[f], gridId, gridInfo);
			for (int i=0; i < valueGrids.length; i++)
			{
				System.out.println("ImportFile: #" + f + "/" + i + " Store ValueGrid " + valueGrids[i]);
				System.out.println("ImportFile: valueGrid= " + valueGrids[i].toFullString());
				
				dbHandler.saveValueGrid(valueGrids[i]);
			}
		}
		
		System.out.println("ImportFile: ... ready!");
			
	}
}
