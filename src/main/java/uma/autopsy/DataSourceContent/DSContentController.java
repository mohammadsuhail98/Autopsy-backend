package uma.autopsy.DataSourceContent;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/datasource/{dataSourceId}/content")
public class DSContentController {

    @Autowired
    private DSContentService dsContentService;

    @GetMapping
    public ResponseEntity<FileNode> getDataSource(@PathVariable("dataSourceId") int dataSourceId,
                                                        @RequestHeader("deviceId") String deviceId) {
        FileNode fileContent = dsContentService.getDataSourceContentById(dataSourceId, deviceId);
        return new ResponseEntity<>(fileContent, HttpStatus.OK);
    }
}
