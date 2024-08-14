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
    public ResponseEntity<FileNode> getDataSourceContent(@PathVariable("dataSourceId") int dataSourceId,
                                                         @RequestHeader("deviceId") String deviceId) {
        FileNode fileContent = dsContentService.getDataSourceContentById(dataSourceId, deviceId);
        return new ResponseEntity<>(fileContent, HttpStatus.OK);
    }

    @GetMapping("file")
    public ResponseEntity<FileNode> getFileContent(@PathVariable("dataSourceId") int dataSourceId,
                                               @RequestParam("fileId") int fileId,
                                               @RequestHeader("deviceId") String deviceId) {
        FileNode fileContent = dsContentService.getFileContent(dataSourceId, deviceId, fileId);
        return new ResponseEntity<>(fileContent, HttpStatus.OK);
    }

}
