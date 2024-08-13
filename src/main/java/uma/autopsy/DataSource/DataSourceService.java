package uma.autopsy.DataSource;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DataSourceService {
    List<DataSource> getAllDataSourcesByCaseId(int caseId, String deviceId);
    DataSource getDataSourceById(int caseId, int dataSourceId,  String deviceId);
    DataSource addDataSource(int caseId, MultipartFile file, DataSource dataSource, String deviceId);
    void deleteDataSource(int caseId, int dataSourceId, String deviceId);
}
