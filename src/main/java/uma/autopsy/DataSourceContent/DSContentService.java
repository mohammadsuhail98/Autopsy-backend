package uma.autopsy.DataSourceContent;

import org.sleuthkit.datamodel.AbstractFile;
import uma.autopsy.DataSourceContent.Models.FileNode;

public interface DSContentService {
    FileNode getDataSourceContentById(int dataSourceId, String deviceId);
    FileNode getFileContent(int dataSourceId, String deviceId, int fileId);
    byte[] getHexFile(int dataSourceId, String deviceId, int fileId);
    byte[] getTextFile(int dataSourceId, String deviceId, int fileId);
    AbstractFile getApplicationFile(int dataSourceId, String deviceId, int fileId);

}
