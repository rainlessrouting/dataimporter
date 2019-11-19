package de.rainlessrouting.dataprocessing;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.rainlessrouting.common.model.PrecipitationReadingGrid;
import de.rainlessrouting.common.model.PrecipitationReadingPoint;
import de.rainlessrouting.dataprocessing.model.PrecipitationOfTimepoint;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Protocol;

public class RedisDAO {

	public final static String REDIS_SERVER = Protocol.DEFAULT_HOST; // "192.168.3.100"; // Protocol.DEFAULT_HOST
	private Jedis jedis;

	/** Key=serialized PrecipitationReadingPoint, Value=GeoCoodinate */
	private HashMap<byte[], GeoCoordinate> coordinateMap;

	public RedisDAO() {

		// connect to redis on localhost port 6379 and set timeout to n ms
		this.jedis = new Jedis(REDIS_SERVER, Protocol.DEFAULT_PORT, 30000);
		jedis.connect();
		if (this.jedis.isConnected())
			System.out.println("RedisDAO: connected to Redis DB @ " + REDIS_SERVER);
		else
			System.err.println("RedisDAO: Failed to connect to Redis DB @ " + REDIS_SERVER);

		this.coordinateMap = new HashMap<byte[], GeoCoordinate>();
	}

	/**
	 * Clean DB.
	 * 
	 * @throws Exception
	 */
	public void flushDB() throws Exception {
		this.jedis.flushDB();
	}

	/**
	 * Called by Loading*.
	 * Stores coordinateMap<PrecipitationReadingPoint, GeoCoordinate> in RedisDB.
	 * @throws IOException
	 */
	public void saveMap() throws IOException {
		// Adds the specified geospatial items (latitude, longitude, name) to the specified key
		jedis.geoadd("PrecipitationReadingPoint".getBytes(), this.coordinateMap);
		this.coordinateMap = new HashMap<byte[], GeoCoordinate>();
	}

	
	public void putMap(PrecipitationReadingGrid precGrid) throws IOException {
		
		// first store metadata, then the contents
		jedis.append("PrecipitationReadingGrid_LongGridSize", precGrid.getLongGridSize()+"");
		jedis.append("PrecipitationReadingGrid_LatGridSize", precGrid.getLatGridSize()+"");
		
		List<PrecipitationReadingPoint> precs = precGrid.getPrecipitationReadingPoints();
		for (int i=0; i < precs.size(); i++)
		{
			PrecipitationReadingPoint prp = precs.get(i);
			putMap(prp, prp.getLat(), prp.getLon());
		}
	}
	
	public void putMap(PrecipitationReadingPoint a, Double lat, Double lon) throws IOException {
		this.coordinateMap.put(serializeObject(a), new GeoCoordinate(lon, lat));
	}

	/**
	 * 
	 * @param precipitationOfTimepoints  Array containing n (e.g. 60) elements and each of these elements
	 *                                   contains ~18.000 key-value pairs <millis, <lat,lon>>
	 */
	public void savePrecipitationOfTimepoints(PrecipitationOfTimepoint[] precipitationOfTimepoints) {

		for (int i = 0; i < precipitationOfTimepoints.length; i++) {

			PrecipitationOfTimepoint precipitationOfTimepoint = precipitationOfTimepoints[i];

			String millis = precipitationOfTimepoint.getTimeMillis() + "";
			Map<byte[], GeoCoordinate> map = precipitationOfTimepoint.getCoordinateMap();

			jedis.geoadd(millis.getBytes(), map);
		}
	}

	public void saveCurrentTimeArrayToRedis(long[] currentTimeArray) throws IOException {

		jedis.append("CurrentTimeArray".getBytes(), serializeObject(currentTimeArray));
	}

	public static byte[] serializeObject(Object obj) throws IOException {
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bytesOut);
		oos.writeObject(obj);
		oos.flush();
		byte[] bytes = bytesOut.toByteArray();
		bytesOut.close();
		oos.close();
		return bytes;
	}
}
