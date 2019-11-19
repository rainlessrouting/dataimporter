package de.rainlessrouting.dataprocessing.model;

import java.io.IOException;
import java.util.HashMap;

import de.rainlessrouting.common.model.SimplePrecipitation;
import de.rainlessrouting.dataprocessing.RedisDAO;
import redis.clients.jedis.GeoCoordinate;

public class PrecipitationOfTimepoint {
	
	private HashMap<byte[], GeoCoordinate> coordinateMap; 
	
	private long timeMillis; 
	
	public PrecipitationOfTimepoint(long timeMillis) {
		this.coordinateMap = new HashMap<byte[], GeoCoordinate>();
		this.timeMillis = timeMillis;
	}
	
	public void addPrecipitation(SimplePrecipitation a, double lon, double lat) {
		try {
			this.getCoordinateMap().put(RedisDAO.serializeObject(a), new GeoCoordinate(lon, lat));
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	public long getTimeMillis() {
		return timeMillis;
	}

	public HashMap<byte[], GeoCoordinate> getCoordinateMap() {
		return coordinateMap;
	}

	@Override
	public String toString() {
		return "PrecipitationOfTimepoint [coordinateMap=" + coordinateMap + ", timeMillis=" + timeMillis + "]";
	}
}
