package uma.autopsy.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uma.autopsy.Cases.Case;
import uma.autopsy.Cases.CaseRepository;

import java.util.List;

@Service
public class DataSourceServiceImp implements DataSourceService {

    @Autowired
    private DataSourceRepository dataSourceRepository;

    @Autowired
    private CaseRepository caseRepository;

    public List<DataSource> getAllDataSourcesByCaseId(int caseId) {
        return dataSourceRepository.findByCaseEntity_Id(caseId);
    }

    public DataSource getDataSourceById(int caseId, int dataSourceId) {
        return dataSourceRepository.findByIdAndCaseEntity_Id(dataSourceId, caseId)
                .orElseThrow(() -> new RuntimeException("DataSource not found for this id and caseId :: " + dataSourceId + ", " + caseId));
    }

    public DataSource addDataSource(int caseId, DataSource dataSource) {
//        Case caseEntity = caseRepository.findById(caseId)
//                .orElseThrow(() -> new RuntimeException("Case not found for this id :: " + caseId));
////        dataSource.setCase(caseEntity);
        return dataSourceRepository.save(dataSource);
    }

    public void deleteDataSource(int caseId, int dataSourceId) {
        DataSource dataSource = getDataSourceById(caseId, dataSourceId);
        dataSourceRepository.delete(dataSource);
    }

}
