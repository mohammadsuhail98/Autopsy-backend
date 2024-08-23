package uma.autopsy.FileViews;

import uma.autopsy.DataSourceContent.Models.FileNode;

import java.util.List;

public interface FileViewsService {
    List<FileNode> getFilesByQuery(int caseId, String deviceId, String query);
    List<FileNode> getFilesByViewType(int caseId, String deviceId, int extensionId);
    List<CurrentMimeType> getCurrentMimeTypes(int caseId, String deviceId);
    List<FileNode> getFilesByMimeTypes(int caseId, String deviceId, String mimeType);
    List<FileNode> getDeletedFiles(int caseId, String deviceId, int type);

}
