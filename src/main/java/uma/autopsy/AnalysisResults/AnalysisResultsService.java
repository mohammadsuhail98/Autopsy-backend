package uma.autopsy.AnalysisResults;

import uma.autopsy.DataSourceContent.Models.FileNode;

import java.util.List;

public interface AnalysisResultsService {
    List<AnalysisResultType> getAnalysisResultsTypes(int caseId, String deviceId);
    List<FileNode> getFilesByAnalysisResult(int caseId, String deviceId, int analysisType);
}
