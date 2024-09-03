package uma.autopsy.GeoLocation;

import java.util.List;

public interface GeoLocationService {

    List<GeoLocation> getAllGeoLocations(String deviceId, int caseId);
    List<GeoLocation> getGeoLocationByDataSources(String deviceId, int caseId, List<Long> dataSourceIds);

    byte[] getImage(String path);
}
