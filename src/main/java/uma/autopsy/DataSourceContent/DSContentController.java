package uma.autopsy.DataSourceContent;


import org.sleuthkit.datamodel.AbstractFile;
import org.sleuthkit.datamodel.TskCoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import uma.autopsy.DataSourceContent.Models.AnalysisResult;
import uma.autopsy.DataSourceContent.Models.FileNode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping(value = "/api/datasource")
public class DSContentController {

    @Autowired
    private DSContentService dsContentService;

    @GetMapping("/{dataSourceId}/content")
    public ResponseEntity<FileNode> getDataSourceContent(@PathVariable int dataSourceId,
                                                         @RequestHeader("deviceId") String deviceId) {
        FileNode fileContent = dsContentService.getDataSourceContentById(dataSourceId, deviceId);
        return new ResponseEntity<>(fileContent, HttpStatus.OK);
    }

    @GetMapping("/file")
    public ResponseEntity<FileNode> getFileContent(@RequestParam("caseId") int caseId,
                                               @RequestParam("fileId") int fileId,
                                               @RequestHeader("deviceId") String deviceId) {
        FileNode fileContent = dsContentService.getFileContent(caseId, deviceId, fileId);
        return new ResponseEntity<>(fileContent, HttpStatus.OK);
    }

    @GetMapping("/file_hex")
    public ResponseEntity<String> getFileHex(@RequestParam("caseId") int caseId,
                                             @RequestParam("fileId") int fileId,
                                             @RequestHeader("deviceId") String deviceId) throws IOException {
        String fileBytes = dsContentService.getHexFile(caseId, deviceId, fileId);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=file.txt");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileBytes);
    }

    @GetMapping("/file_strings")
    public ResponseEntity<InputStreamResource> getFileText(@RequestParam("caseId") int caseId,
                                                            @RequestParam("fileId") int fileId,
                                                            @RequestHeader("deviceId") String deviceId) throws IOException {
        byte[] fileBytes = dsContentService.getTextFile(caseId, deviceId, fileId);
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(fileBytes));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=file.txt");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(fileBytes.length)
                .body(resource);
    }

    @GetMapping("/file_application")
    public ResponseEntity<InputStreamResource> getFileApplication(@RequestParam("caseId") int caseId,
                                                           @RequestParam("fileId") int fileId,
                                                           @RequestHeader("deviceId") String deviceId) throws IOException, TskCoreException {
        AbstractFile file = dsContentService.getApplicationFile(caseId, deviceId, fileId);
        byte[] contentBytes = new byte[(int) file.getSize()];
        file.read(contentBytes, 0, contentBytes.length);

        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(contentBytes));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"");

        String mimeType = Files.probeContentType(Paths.get(file.getName()));
        if (mimeType == null) {
            mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        headers.add(HttpHeaders.CONTENT_TYPE, mimeType);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(contentBytes.length)
                .body(resource);
    }

    @GetMapping("/analysis_results")
    public ResponseEntity<AnalysisResult> getAnalysisResults(@RequestParam("caseId") int caseId,
                                                                  @RequestParam("fileId") int fileId,
                                                                  @RequestHeader("deviceId") String deviceId) throws IOException, TskCoreException {
        AnalysisResult analysisResult = dsContentService.getAnalysisResult(caseId, deviceId, fileId);
        return new ResponseEntity<>(analysisResult, HttpStatus.OK);
    }

}
