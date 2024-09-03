package uma.autopsy.GeoLocation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping(value = "/api/geolocation")
public class GeoLocationController {

    @Autowired
    GeoLocationService geoLocationService;
    @Autowired
    private ResourceLoader resourceLoader;

    @GetMapping
    public ResponseEntity<List<GeoLocation>> getAllGeoLocations(@RequestParam("caseId") int caseId,
                                                                @RequestHeader("deviceId") String deviceId){
        List<GeoLocation> geoLocationList = geoLocationService.getAllGeoLocations(deviceId, caseId);
        return new ResponseEntity<>(geoLocationList, HttpStatus.OK);
    }

    @GetMapping("data_sources")
    public ResponseEntity<List<GeoLocation>> getGeoLocationByDataSources(@RequestParam("caseId") int caseId,
                                                         @RequestParam(value = "dataSourceIds", required = false) List<Long> dataSourceIds,
                                                         @RequestHeader("deviceId") String deviceId){
        List<GeoLocation> geoLocationList = geoLocationService.getGeoLocationByDataSources(deviceId, caseId, dataSourceIds);
        return new ResponseEntity<>(geoLocationList, HttpStatus.OK);
    }

    @GetMapping("image")
    public @ResponseBody ResponseEntity<InputStreamResource> getImageWithMediaType(@RequestParam String path) throws IOException {

        File imgFile = new File(path);

        byte[] imageBytes = Files.readAllBytes(imgFile.toPath());

        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(imageBytes));

        HttpHeaders headers = new HttpHeaders();
        String mimeType = Files.probeContentType(Paths.get(imgFile.getName()));
        headers.add(HttpHeaders.CONTENT_TYPE, mimeType);
        headers.setContentLength(imageBytes.length);

        // Return the image bytes in the response
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

}
