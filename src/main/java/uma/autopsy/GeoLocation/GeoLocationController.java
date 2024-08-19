package uma.autopsy.GeoLocation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/geolocation")
public class GeoLocationController {

    @Autowired
    GeoLocationService geoLocationService;

    @GetMapping
    public ResponseEntity<List<GeoLocation>> getAllGeoLocations(@RequestParam("caseId") int caseId,
                                                                @RequestHeader("deviceId") String deviceId){
        List<GeoLocation> geoLocationList = geoLocationService.getAllGeoLocations(deviceId, caseId);
        return new ResponseEntity<>(geoLocationList, HttpStatus.OK);
    }

    @GetMapping("data_sources")
    public ResponseEntity<List<GeoLocation>> getGeoLocationByDataSources(@RequestParam("caseId") int caseId,
                                                         @RequestParam("dataSourceIds") List<Long> dataSourceIds,
                                                         @RequestHeader("deviceId") String deviceId){
        List<GeoLocation> geoLocationList = geoLocationService.getGeoLocationByDataSources(deviceId, caseId, dataSourceIds);
        return new ResponseEntity<>(geoLocationList, HttpStatus.OK);
    }

}
