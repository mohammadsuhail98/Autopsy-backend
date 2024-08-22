package uma.autopsy.AnalysisResults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uma.autopsy.DataSourceContent.Models.FileNode;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping(value = "/api/analysis_results")
public class AnalysisResultsController {

    @Autowired
    AnalysisResultsService analysisResultsService;

    @GetMapping("current_types")
    public ResponseEntity<List<AnalysisResultType>> getAllAnalysisResults(@RequestParam("caseId") int caseId,
                                                                @RequestHeader("deviceId") String deviceId){
        List<AnalysisResultType> analysisResultTypes = analysisResultsService.getAnalysisResultsTypes(caseId, deviceId);
        return new ResponseEntity<>(analysisResultTypes, HttpStatus.OK);
    }

    @GetMapping("files")
    public ResponseEntity<List<FileNode>> getFilesByAnalysisResults(@RequestParam("caseId") int caseId,
                                                                              @RequestParam("type") int type,
                                                                          @RequestHeader("deviceId") String deviceId){
        List<FileNode> files = analysisResultsService.getFilesByAnalysisResult(caseId, deviceId, type);
        return new ResponseEntity<>(files, HttpStatus.OK);
    }

}
