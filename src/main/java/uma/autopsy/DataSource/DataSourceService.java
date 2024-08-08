package uma.autopsy.DataSource;

import org.springframework.stereotype.Service;

import java.util.List;

public interface DataSourceService {
    List<DataSource> getAllDataSourcesByCaseId(int caseId);
    DataSource getDataSourceById(int caseId, int dataSourceId);
    DataSource addDataSource(int caseId, DataSource dataSource);
    void deleteDataSource(int caseId, int dataSourceId);
}
