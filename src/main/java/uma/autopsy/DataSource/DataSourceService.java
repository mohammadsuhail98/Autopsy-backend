package uma.autopsy.DataSource;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DataSourceService {
    List<DataSource> getAllDataSourcesByCaseId(int caseId);
    DataSource getDataSourceById(int caseId, int dataSourceId);
    DataSource addDataSource(int caseId, MultipartFile file, DataSource dataSource, String deviceId);
    void deleteDataSource(int caseId, int dataSourceId);
}
