package uma.autopsy.DataSourceContent;

public interface DSContentService {
    FileNode getDataSourceContentById(int dataSourceId, String deviceId);

    FileNode getFileContent(int dataSourceId, String deviceId, int fileId);

}
