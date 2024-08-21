package uma.autopsy.DataSourceContent;

import org.sleuthkit.autopsy.coreutils.StringExtract;
import org.sleuthkit.datamodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sleuthkit.autopsy.coreutils.StringExtract.*;
import uma.autopsy.Cases.CaseRepository;
import uma.autopsy.DataSource.DataSource;
import uma.autopsy.DataSourceContent.Models.AnalysisResult;
import uma.autopsy.DataSourceContent.Models.FileNode;
import uma.autopsy.Exceptions.ResourceNotFoundException;
import uma.autopsy.Exceptions.UnsupportedMimeTypeException;
import uma.autopsy.Utils.DirectoryTreeBuilder;
import uma.autopsy.Utils.HexExtractor;

import java.util.ArrayList;
import java.util.List;

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
        if (!validateDeviceId(deviceId, dataSource)) {  throw new RuntimeException("Not Authorized for this operation"); }

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

    @Override
    public FileNode getFileContent(int dataSourceId, String deviceId, int fileId){
        DataSource dataSource = dsContentRepository.findById(dataSourceId)
                .orElseThrow(() -> new ResourceNotFoundException("DataSource not found for this id: " + dataSourceId));
        if (!validateDeviceId(deviceId, dataSource)) {  throw new RuntimeException("Not Authorized for this operation"); }

        String caseDir = dataSource.getCaseEntity().getCasePath();
        SleuthkitCase skcase = null;

        try {
            skcase = SleuthkitCase.openCase(caseDir);
            Content content = skcase.getContentById(fileId);
            AbstractFile file = skcase.getAbstractFileById(fileId);

            if (file != null) {
                return FileNode.getNode(file);
            } else if (content != null) {
                return FileNode.getNode(content);
            } else {
                return new FileNode();
            }

        } catch (TskCoreException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public byte[] getHexFile(int dataSourceId, String deviceId, int fileId){
        DataSource dataSource = dsContentRepository.findById(dataSourceId)
                .orElseThrow(() -> new ResourceNotFoundException("DataSource not found for this id: " + dataSourceId));
        if (!validateDeviceId(deviceId, dataSource)) {  throw new RuntimeException("Not Authorized for this operation"); }

        String caseDir = dataSource.getCaseEntity().getCasePath();
        System.out.println(caseDir);
        SleuthkitCase skcase = null;

        try {
            skcase = SleuthkitCase.openCase(caseDir);
            AbstractFile file = skcase.getAbstractFileById(fileId);
            byte[] contentBytes = new byte[(int) file.getSize()];
            file.read(contentBytes, 0, contentBytes.length);
            return HexExtractor.toHexString(contentBytes).getBytes();
        } catch (TskCoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] getTextFile(int dataSourceId, String deviceId, int fileId){
        DataSource dataSource = dsContentRepository.findById(dataSourceId)
                .orElseThrow(() -> new ResourceNotFoundException("DataSource not found for this id: " + dataSourceId));
        if (!validateDeviceId(deviceId, dataSource)) {  throw new RuntimeException("Not Authorized for this operation"); }

        String caseDir = dataSource.getCaseEntity().getCasePath();
        System.out.println(caseDir);
        SleuthkitCase skcase = null;

        javax.swing.JComboBox<StringExtract.StringExtractUnicodeTable.SCRIPT> languageCombo;
        try {
            skcase = SleuthkitCase.openCase(caseDir);
            AbstractFile file = skcase.getAbstractFileById(fileId);
            byte[] contentBytes = new byte[(int) file.getSize()];
            file.read(contentBytes, 0, contentBytes.length);
            StringExtract stringExtract = new StringExtract();
            stringExtract.setEnableUTF8(true);
            stringExtract.setEnabledScript(StringExtractUnicodeTable.SCRIPT.LATIN_1);
            StringExtractResult res = stringExtract.extract(contentBytes, contentBytes.length, 0);

            return res.getText().getBytes();
        } catch (TskCoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AbstractFile getApplicationFile(int dataSourceId, String deviceId, int fileId) {
        DataSource dataSource = dsContentRepository.findById(dataSourceId)
                .orElseThrow(() -> new ResourceNotFoundException("DataSource not found for this id: " + dataSourceId));
        if (!validateDeviceId(deviceId, dataSource)) {  throw new RuntimeException("Not Authorized for this operation"); }

        String caseDir = dataSource.getCaseEntity().getCasePath();
        System.out.println(caseDir);
        SleuthkitCase skcase = null;

        try {
            skcase = SleuthkitCase.openCase(caseDir);
            AbstractFile file = skcase.getAbstractFileById(fileId);
            FileNode fileNode = FileNode.getNode(file);

            if (fileNode.getMimeType().isSupported()) {
                return file;
            } else {
                throw new UnsupportedMimeTypeException("File type not supported");
            }

        } catch (TskCoreException e) {
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }

    @Override
    public AnalysisResult getAnalysisResult(int dataSourceId, String deviceId, int fileId) {
        DataSource dataSource = dsContentRepository.findById(dataSourceId)
                .orElseThrow(() -> new ResourceNotFoundException("DataSource not found for this id: " + dataSourceId));
        if (!validateDeviceId(deviceId, dataSource)) {  throw new RuntimeException("Not Authorized for this operation"); }

        String caseDir = dataSource.getCaseEntity().getCasePath();
        System.out.println(caseDir);
        SleuthkitCase skcase = null;

        try {
            skcase = SleuthkitCase.openCase(caseDir);
            Content content = skcase.getContentById(fileId);

            var results = content.getAllAnalysisResults();

            if (!results.isEmpty()) {
                List<AnalysisResult.AnalysisResultDetail> analysisResultDetailList = new ArrayList<>();
                AnalysisResult analysisResult = new AnalysisResult();
                analysisResult.setItemName(content.getName());
                if (content.getAggregateScore() != null && content.getAggregateScore().getSignificance() != null) {
                    analysisResult.setAggregateScore(content.getAggregateScore().getSignificance().getDisplayName());
                }
                for (var result: results){
                    var analysisResultDetails = getAnalysisResultDetail(result);
                    analysisResultDetailList.add(analysisResultDetails);
                }
                analysisResult.setAnalysisResults(analysisResultDetailList);

                return analysisResult;
            } else {
                return new AnalysisResult();
            }

        } catch (TskCoreException e) {
            throw new RuntimeException(e);
        }
    }

    private static AnalysisResult.AnalysisResultDetail getAnalysisResultDetail(org.sleuthkit.datamodel.AnalysisResult result) throws TskCoreException {
        var analysisResultDetails = new AnalysisResult.AnalysisResultDetail();
        if (result.getScore() != null && result.getScore().getSignificance() != null) {
            analysisResultDetails.setScore(result.getScore().getSignificance().getDisplayName());
        }
        analysisResultDetails.setType(result.getType().getDisplayName());
        analysisResultDetails.setConclusion(result.getConclusion());
        analysisResultDetails.setConfiguration(result.getConfiguration());
        analysisResultDetails.setJustification(result.getJustification());

        for (var attribute: result.getAttributes()) {
            var type = attribute.getAttributeType();
            var displayString = attribute.getValueString();

            if (type.equals(BlackboardAttribute.Type.TSK_DATETIME_CREATED)) {
                analysisResultDetails.setDateCreated(STR."\{attribute.getValueLong()}");
            } else if (type.equals(BlackboardAttribute.Type.TSK_COMMENT)) {
                analysisResultDetails.setComment(displayString);
            } else if (type.equals(BlackboardAttribute.Type.TSK_DEVICE_MAKE)) {
                analysisResultDetails.setDeviceMake(displayString);
            } else if (type.equals(BlackboardAttribute.Type.TSK_DEVICE_MODEL)) {
                analysisResultDetails.setDeviceModel(displayString);
            } else if (type.equals(BlackboardAttribute.Type.TSK_GEO_LATITUDE)) {
                analysisResultDetails.setLatitude(displayString);
            } else if (type.equals(BlackboardAttribute.Type.TSK_GEO_LONGITUDE)) {
                analysisResultDetails.setLongitude(displayString);
            } else if (type.equals(BlackboardAttribute.Type.TSK_GEO_ALTITUDE)) {
                analysisResultDetails.setAltitude(displayString);
            }
        }
        return analysisResultDetails;
    }

    boolean validateDeviceId(String deviceId, DataSource dataSourceEntity){
        return deviceId.equalsIgnoreCase(dataSourceEntity.getCaseEntity().getDeviceId());
    }
}
