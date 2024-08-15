package uma.autopsy.DataSourceContent;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

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

    @GetMapping("/file_hex")
    public ResponseEntity<InputStreamResource> getFileHex(@PathVariable("dataSourceId") int dataSourceId,
                                                            @RequestParam("fileId") int fileId,
                                                            @RequestHeader("deviceId") String deviceId) throws IOException {
        byte[] fileBytes = dsContentService.getHexFile(dataSourceId, deviceId, fileId);
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(fileBytes));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=file.hex");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(fileBytes.length)
                .body(resource);
    }

    @GetMapping("/file_strings")
    public ResponseEntity<InputStreamResource> getFileText(@PathVariable("dataSourceId") int dataSourceId,
                                                            @RequestParam("fileId") int fileId,
                                                            @RequestHeader("deviceId") String deviceId) throws IOException {
        byte[] fileBytes = dsContentService.getTextFile(dataSourceId, deviceId, fileId);
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(fileBytes));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=file.txt");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(fileBytes.length)
                .body(resource);
    }

}
