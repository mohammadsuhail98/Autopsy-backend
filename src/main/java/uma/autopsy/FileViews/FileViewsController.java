package uma.autopsy.FileViews;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uma.autopsy.AnalysisResults.AnalysisResultType;
import uma.autopsy.DataSourceContent.Models.FileNode;

import java.util.List;

@RestController
@RequestMapping(value = "/api/file_views")
public class FileViewsController {

    @Autowired
    FileViewsService fileViewsService;

    @GetMapping("files_by_view_type")
    public ResponseEntity<List<FileNode>> getFilesByViewType(@RequestParam("caseId") int caseId,
                                                                @RequestParam("type") int type,
                                                                @RequestHeader("deviceId") String deviceId){
        List<FileNode> files = fileViewsService.getFilesByViewType(caseId, deviceId, type);
        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    @GetMapping("mime_types")
    public ResponseEntity<List<CurrentMimeType>> getCurrentMimeTypes(@RequestParam("caseId") int caseId,
                                                                @RequestHeader("deviceId") String deviceId){
        List<CurrentMimeType> currentMimeTypes = fileViewsService.getCurrentMimeTypes(caseId, deviceId);
        return new ResponseEntity<>(currentMimeTypes, HttpStatus.OK);
    }

    @GetMapping("files_by_mime_type")
    public ResponseEntity<List<FileNode>> getFilesByMimeType(@RequestParam("caseId") int caseId,
                                                             @RequestParam("mimeType") String mimeType,
                                                             @RequestHeader("deviceId") String deviceId){
        List<FileNode> files = fileViewsService.getFilesByMimeTypes(caseId, deviceId, mimeType);
        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    @GetMapping("deleted_files")
    public ResponseEntity<List<FileNode>> getDeletedFiles(@RequestParam("caseId") int caseId,
                                                          @RequestParam("type") int type,
                                                          @RequestHeader("deviceId") String deviceId){
        List<FileNode> files = fileViewsService.getFilesByViewType(caseId, deviceId, type);
        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    @GetMapping("files_by_size")
    public ResponseEntity<List<FileNode>> getFilesBySize(@RequestParam("caseId") int caseId,
                                                          @RequestParam("type") int type,
                                                          @RequestHeader("deviceId") String deviceId){
        List<FileNode> files = fileViewsService.getFilesByViewType(caseId, deviceId, type);
        return new ResponseEntity<>(files, HttpStatus.OK);
    }
}
