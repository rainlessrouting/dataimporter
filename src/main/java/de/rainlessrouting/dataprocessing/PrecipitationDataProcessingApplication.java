package de.rainlessrouting.dataprocessing;

import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import de.rainlessrouting.importer.*;

@SpringBootApplication
public class PrecipitationDataProcessingApplication {
	private static final org.slf4j.Logger log = LoggerFactory.getLogger(PrecipitationDataProcessingApplication.class);

	private static final String ARG_CLEAR_AUTO_MIN = "autoclear"; //= Number of Minutes
	private static final String ARG_CLEAR_DB = "cleardb";	
	private static final String ARG_CLEAR_FILES = "clearfiles";

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = SpringApplication.run(PrecipitationDataProcessingApplication.class, args);
		
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
				log.error("Unknown startup parameter: " + args[i]);
			}
		}
		log.debug("Startup with clearDB=" + clearDB + " clearFiles=" + true + " clearAutoMinutes=" + clearAutoMinutes);

		/* Import data from a live source. Use ftp.properties file */
		//new ImportLive("ftp.properties", clearDB, clearFiles);

		/* Import artificially created data. */
		new ImportArtificial(clearDB);

		/* Import historical data from a folder. */
		// String path = "src/main/resources";
		// new ImportReplay(path, 1560343702000L, 1560348657000L, 30, true);
		
		if (clearAutoMinutes > 0)
			new AutoClear(clearAutoMinutes);
		
		SpringApplication.exit(context, () -> 0);
	}
}
