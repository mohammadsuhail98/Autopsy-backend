package uma.autopsy.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping(value = "/api/cases/{caseId}/datasources")
public class DataSourceController {
    @Autowired
    private DataSourceService dataSourceService;

    @PostMapping
    public ResponseEntity<DataSource> addDataSource(@PathVariable("caseId") int caseId,
                                                    @RequestHeader("deviceId") String deviceId,
                                                    @RequestParam("file") MultipartFile file,
                                                    @RequestParam("timeZone") String timeZone,
                                                    @RequestParam("sectorSize") int sectorSize,
                                                    @RequestParam("ignoreOrphanFiles") boolean ignoreOrphanFiles,
                                                    @RequestParam("addUnAllocSpace") boolean addUnAllocSpace,
                                                    @RequestParam("exifParser") boolean exifParser,
                                                    @RequestParam(value = "md5", required = false) String md5,
                                                    @RequestParam(value = "sha1", required = false) String sha1,
                                                    @RequestParam(value = "sha256", required = false) String sha256) {
        DataSource dataSource = new DataSource();
        dataSource.setTimeZone(timeZone);
        dataSource.setSectorSize(sectorSize);
        dataSource.setIgnoreOrphanFiles(ignoreOrphanFiles);
        dataSource.setAddUnAllocSpace(addUnAllocSpace);
        dataSource.setMd5Hash(md5);
        dataSource.setSha1Hash(sha1);
        dataSource.setSha256Hash(sha256);
        dataSource.setExifParser(exifParser);

        DataSource createdDataSource = dataSourceService.addDataSource(caseId, file, dataSource, deviceId);
        return new ResponseEntity<>(createdDataSource, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<DataSource>> getAllDataSources(@PathVariable("caseId") int caseId,
                                                              @RequestHeader("deviceId") String deviceId) {
        List<DataSource> dataSources = dataSourceService.getAllDataSourcesByCaseId(caseId, deviceId);
        return new ResponseEntity<>(dataSources, HttpStatus.OK);
    }

    @GetMapping("/{dataSourceId}")
    public ResponseEntity<DataSource> getDataSourceById(@PathVariable("caseId") int caseId,
                                                        @PathVariable("dataSourceId") int dataSourceId,
                                                        @RequestHeader("deviceId") String deviceId) {
        DataSource dataSource = dataSourceService.getDataSourceById(caseId, dataSourceId, deviceId);
        return new ResponseEntity<>(dataSource, HttpStatus.OK);
    }

    @DeleteMapping("/{dataSourceId}")
    public ResponseEntity<Void> deleteDataSource(@PathVariable("caseId") int caseId,
                                                 @PathVariable("dataSourceId") int dataSourceId,
                                                 @RequestHeader("deviceId") String deviceId) {
        dataSourceService.deleteDataSource(caseId, dataSourceId, deviceId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
