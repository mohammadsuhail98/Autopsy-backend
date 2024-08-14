package uma.autopsy.DataSourceContent;

import org.sleuthkit.datamodel.SleuthkitCase;
import org.sleuthkit.datamodel.TskCoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uma.autopsy.Cases.CaseRepository;
import uma.autopsy.DataSource.DataSource;
import uma.autopsy.Exceptions.ResourceNotFoundException;

@Service
public class DSContentServiceImp implements DSContentService {

    @Autowired
    DSContentRepository dsContentRepository;
    @Autowired
    private CaseRepository caseRepository;

    @Override
    public FileNode getDataSourceContentById(int dataSourceId, String deviceId) {
        DataSource dataSource = dsContentRepository.findById(dataSourceId)
                .orElseThrow(() -> new ResourceNotFoundException("DataSource not found for this id: " + dataSourceId));

        String caseDir = dataSource.getCaseEntity().getCasePath();
        SleuthkitCase skcase = null;
        FileNode tree = null;

        try {
            skcase = SleuthkitCase.openCase(caseDir);
            var content = skcase.getImageById(dataSource.getDataSourceId());
            DirectoryTreeBuilder treeBuilder = new DirectoryTreeBuilder();
            tree = treeBuilder.buildTree(content);

        } catch (TskCoreException e) {
            throw new RuntimeException(e);
        }

        return tree;
    }
}
