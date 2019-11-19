package de.rainlessrouting.importer;

import java.io.File;
import java.util.Date;

import it.sauronsoftware.ftp4j.FTPClient;

public class FtpClient {

	private final String server;
	private final int port;
	private final String user;
	private final String password;
	private FTPClient client;

	public FtpClient(String server, int port, String user, String password) {
		this.server = server;
		this.port = port;
		this.user = user;
		this.password = password;
	}

	public void open() throws Exception {
		client = new FTPClient();

		// Get the FTP Connection from the Utility class

		client.connect(server);

		client.login(user, password);

		// if (client != null)
	}

	public void close() throws Exception {
		client.disconnect(true);
	}

	public String getModificationTime(String pathname) throws Exception {
		Date d = client.modifiedDate(pathname);
		long time = d.getTime();
		long timezoneOffset = d.getTimezoneOffset();
		time = time + (timezoneOffset * 1000 * 60 * -1); // time zone needs to be corrected
		
		return time + "";
	}

	public void downloadFile(String source, String destination) throws Exception {
		File fileDownload = new File(destination);
		fileDownload.createNewFile();

		client.download(source, fileDownload);

	}

}
