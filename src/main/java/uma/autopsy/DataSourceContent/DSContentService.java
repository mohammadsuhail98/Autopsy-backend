package uma.autopsy.DataSourceContent;

import org.sleuthkit.datamodel.AbstractFile;
import uma.autopsy.DataSourceContent.Models.AnalysisResult;
import uma.autopsy.DataSourceContent.Models.FileNode;

public interface DSContentService {
    FileNode getDataSourceContentById(int dataSourceId, String deviceId);
    FileNode getFileContent(int caseId, String deviceId, int fileId);
    String getHexFile(int caseId, String deviceId, int fileId);
    byte[] getTextFile(int caseId, String deviceId, int fileId);
    AbstractFile getApplicationFile(int caseId, String deviceId, int fileId);
    AnalysisResult getAnalysisResult(int caseId, String deviceId, int fileId);

}
