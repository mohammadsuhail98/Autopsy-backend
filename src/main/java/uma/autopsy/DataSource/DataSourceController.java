package uma.autopsy.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/cases/{caseId}/datasources")
public class DataSourceController {
    @Autowired
    private DataSourceService dataSourceService;

    @GetMapping
    public ResponseEntity<List<DataSource>> getAllDataSources(@PathVariable("id") int caseId) {
        List<DataSource> dataSources = dataSourceService.getAllDataSourcesByCaseId(caseId);
        return new ResponseEntity<>(dataSources, HttpStatus.OK);
    }

    @GetMapping("/{dataSourceId}")
    public ResponseEntity<DataSource> getDataSourceById(@PathVariable("id") int caseId, @PathVariable("dataSourceId") int dataSourceId) {
        DataSource dataSource = dataSourceService.getDataSourceById(caseId, dataSourceId);
        return new ResponseEntity<>(dataSource, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<DataSource> addDataSource(@PathVariable("id") int caseId, @RequestBody DataSource dataSource) {
        DataSource createdDataSource = dataSourceService.addDataSource(caseId, dataSource);
        return new ResponseEntity<>(createdDataSource, HttpStatus.CREATED);
    }

    @DeleteMapping("/{dataSourceId}")
    public ResponseEntity<Void> deleteDataSource(@PathVariable("id") int caseId, @PathVariable("dataSourceId") int dataSourceId) {
        dataSourceService.deleteDataSource(caseId, dataSourceId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
