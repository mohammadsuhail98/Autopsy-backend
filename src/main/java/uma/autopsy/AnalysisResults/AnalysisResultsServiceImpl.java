package uma.autopsy.AnalysisResults;

import org.sleuthkit.autopsy.coreutils.Logger;
import org.sleuthkit.autopsy.datamodel.AnalysisResults;
import org.sleuthkit.datamodel.AbstractFile;
import org.sleuthkit.datamodel.BlackboardArtifact;
import org.sleuthkit.datamodel.SleuthkitCase;
import org.sleuthkit.datamodel.TskCoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uma.autopsy.Cases.Case;
import uma.autopsy.Cases.CaseRepository;
import uma.autopsy.DataSourceContent.Models.FileNode;
import uma.autopsy.Exceptions.CaseDoesNotExistException;
import uma.autopsy.Utils.ExifProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@Service
public class AnalysisResultsServiceImpl implements AnalysisResultsService {

    @Autowired
    CaseRepository caseRepository;

    private static final Logger logger = Logger.getLogger(ExifProcessor.class.getName());

    @Override
    public List<AnalysisResultType> getAnalysisResultsTypes(int caseId, String deviceId) {
        Case caseEntity = getCase(caseId);

        SleuthkitCase skcase = null;

        try {
            skcase = SleuthkitCase.openCase(caseEntity.getCasePath());
            List<AnalysisResultType> analysisResultTypes = new ArrayList<>();
            for (var dataSource: skcase.getDataSources()){
                var artifactTypesInUse = skcase.getBlackboard().getArtifactTypesInUse(dataSource.getId());
                for (var artifactTypeInUse: artifactTypesInUse) {
                    AnalysisResultType analysisResultType = new AnalysisResultType(artifactTypeInUse.getTypeID(), artifactTypeInUse.getDisplayName());
                    analysisResultTypes.add(analysisResultType);
                }
            }
            return analysisResultTypes;
        } catch (TskCoreException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<FileNode> getFilesByAnalysisResult(int caseId, String deviceId, int analysisType) {
        Case caseEntity = getCase(caseId);

        SleuthkitCase skcase = null;

        try {
            skcase = SleuthkitCase.openCase(caseEntity.getCasePath());

            var artifacts = skcase.getBlackboardArtifacts(BlackboardArtifact.ARTIFACT_TYPE.fromID(analysisType));
            List<FileNode> files = new ArrayList<>();

            artifacts.forEach( blackboardArtifact -> {
                try {
                    var parent =  blackboardArtifact.getParent();
                    if (parent instanceof AbstractFile file) {
                        files.add(FileNode.getNode(file));
                    }
                } catch (TskCoreException e) {
                    logger.log(Level.WARNING, e.getLocalizedMessage(), e);
                }
            });

            return files;
        } catch (TskCoreException e) {
            throw new RuntimeException(e);
        }
    }

    private Case getCase(int caseId){
        return caseRepository.findById(caseId)
                .orElseThrow(() -> new CaseDoesNotExistException(STR."Case not found for this id : \{caseId}"));
    }
}