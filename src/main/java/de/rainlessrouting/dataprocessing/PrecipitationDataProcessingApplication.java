package de.rainlessrouting.dataprocessing;

import java.nio.file.Paths;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import de.rainlessrouting.importer.*;

@SpringBootApplication
public class PrecipitationDataProcessingApplication {

	private static final String ARG_CLEAR_AUTO_MIN = "autoclear"; //=5 => 5 Minutes
	private static final String ARG_CLEAR_DB = "cleardb";	
	private static final String ARG_CLEAR_FILES = "clearfiles";

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = SpringApplication.run(PrecipitationDataProcessingApplication.class, args);
		
		//new LoadingDualCorrectedTornado().updateDatabaseWithStaticPrecipitation();
		//new ImportTestData().importData("src/main/resources/dual_corrected_tornado.nc", "DualCorrectedTornado");
		
		boolean clearDB = false;
		boolean clearFiles = false;
		int clearAutoMinutes = 0;
		
		for (int i=0; i < args.length; i++)
		{
			if (args[i].equalsIgnoreCase(ARG_CLEAR_DB))
			{
				clearDB = true;
			}
			else if (args[i].equalsIgnoreCase(ARG_CLEAR_FILES))
			{
				clearFiles = true;
			}
			else if (args[i].toLowerCase().startsWith(ARG_CLEAR_AUTO_MIN))
			{
				clearAutoMinutes = Integer.parseInt(args[i].substring(args[i].indexOf("=")+1));
			}
			else
			{
				System.err.println("Unknown startup parameter: " + args[i]);
			}
		}
		System.out.println("Startup with clearDB=" + clearDB + " clearFiles=" + true + " clearAutoMinutes=" + clearAutoMinutes);
		
		//new ImportLive("ftp.properties", clearDB, clearFiles);

		new ImportArtificial(clearDB);

		String path = Paths.get(System.getProperty("user.dir"), "data_test", "Unwetter-2019-06-12").toString();
		System.out.println("Path: " + path);

		new ImportReplay(path, 1560343702000L, 1560348657000L, 30, true);
		
		if (clearAutoMinutes > 0)
			new AutoClear(clearAutoMinutes);
		
		SpringApplication.exit(context, () -> 0);
	}
}
