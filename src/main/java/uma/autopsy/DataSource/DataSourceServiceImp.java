package uma.autopsy.DataSource;

import org.sleuthkit.datamodel.*;
import org.sleuthkit.datamodel.SleuthkitCase.CaseDbTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uma.autopsy.Cases.Case;
import uma.autopsy.Cases.CaseRepository;
import uma.autopsy.Exceptions.CaseDoesNotExistException;
import uma.autopsy.GlobalProperties.GlobalProperties;

import java.io.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class DataSourceServiceImp implements DataSourceService {

    @Autowired
    private DataSourceRepository dataSourceRepository;
    @Autowired
    private CaseRepository caseRepository;
    @Autowired
    private GlobalProperties globalProperties;

    public DataSource addDataSource(int caseId, MultipartFile file, DataSource dataSource, String deviceId) {
        // TODO: Add ingest module ###UNDER-DEVELOPMENT###

        Case caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new CaseDoesNotExistException(STR."Case not found for this id : \{caseId}"));
        if (!validateDeviceId(deviceId, caseEntity)) {  throw new RuntimeException("Not Authorized for this operation"); }

        dataSource.setCaseEntity(caseEntity);

        String tempFileDir = getTempFileDir(file, caseEntity);
        SleuthkitCase skcase = null;
        CaseDbTransaction transaction = null;
        try {
            DiskImageValidator.validateDiskImage(file);
            file.transferTo(new File(tempFileDir));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            skcase = SleuthkitCase.openCase(caseEntity.getCasePath());

            var imageType = TskData.TSK_IMG_TYPE_ENUM.TSK_IMG_TYPE_DETECT;
            var sectorSize = dataSource.getSectorSize();
            var size = file.getSize();
            var displayName = file.getOriginalFilename();
            var pathList = new ArrayList<String>();
            pathList.add(tempFileDir);
            var timeZone = dataSource.getTimeZone();
            var md5Hash = "";
            if (dataSource.getMd5Hash() != null && !dataSource.getMd5Hash().isEmpty()) {
                md5Hash = dataSource.getMd5Hash();
            }
            var sha1Hash = "";
            if (dataSource.getSha1Hash() != null && !dataSource.getSha1Hash().isEmpty()) {
                sha1Hash = dataSource.getSha1Hash();
            }
            var sha256Hash = "";
            if (dataSource.getSha256Hash() != null && !dataSource.getSha256Hash().isEmpty()) {
                sha256Hash = dataSource.getSha256Hash();
            }
            transaction = skcase.beginTransaction();

            Image image = skcase.addImage(imageType, sectorSize, size, displayName, pathList, timeZone, md5Hash, sha1Hash, sha256Hash, UUID.randomUUID().toString(), transaction);

            transaction.commit();

            try {
                var addDataSourceCallbacks = new AddDataSourceCallbacks() {
                    @Override
                    public void onFilesAdded(List<Long> list) {
                        System.out.println(list);
                    }
                };
                SleuthkitJNI.CaseDbHandle.AddImageProcess process = skcase.makeAddImageProcess(dataSource.getTimeZone(), dataSource.isAddUnAllocSpace(), dataSource.isIgnoreOrphanFiles(), "");
                process.run(UUID.randomUUID().toString(),image, (int) image.getSsize(), addDataSourceCallbacks);
            } catch (TskDataException ex) {
                throw new RuntimeException(STR."Error during addImageProcess: \{ex.getMessage()}");
            }

            deleteTempFile(tempFileDir);
        } catch (TskCoreException e) {
            System.out.println(STR."Exception caught: \{e.getMessage()}");
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (TskCoreException ex) {
                    throw new RuntimeException(ex);
                }
            }
            if (skcase != null) {
                skcase.close();
            }
            throw new RuntimeException(e);
        }
        return dataSourceRepository.save(dataSource);
    }

    boolean validateDeviceId(String deviceId, Case caseEntity){
        return deviceId.equalsIgnoreCase(caseEntity.getDeviceId());
    }

    String getTempFileDir(MultipartFile file, Case caseEntity){
        String caseParentDir = STR."\{globalProperties.getBaseDir()}/\{caseEntity.getDeviceId()}";
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String tempFileName = STR."\{uuid}-\{file.getOriginalFilename()}";
        return STR."\{caseParentDir}/\{tempFileName}";
    }

    private void deleteTempFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException(STR."Failed to delete file: \{filePath}", e);
        }
    }

    public List<DataSource> getAllDataSourcesByCaseId(int caseId) {
        return dataSourceRepository.findByCaseEntity_Id(caseId);
    }

    public DataSource getDataSourceById(int caseId, int dataSourceId) {
        return dataSourceRepository.findByIdAndCaseEntity_Id(dataSourceId, caseId)
                .orElseThrow(() -> new RuntimeException("DataSource not found for this id and caseId :: " + dataSourceId + ", " + caseId));
    }

    public void deleteDataSource(int caseId, int dataSourceId) {
        DataSource dataSource = getDataSourceById(caseId, dataSourceId);
        dataSourceRepository.delete(dataSource);
    }

}

//    TODO: ADD INGEST MODULE
//     THE FOLLOWING ARE THE NON-FUNCTIONING METHODS USED TO TEST THE PROCESS OF ADDING AN INGEST MODULE TO THE CASE
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

//                    var artifacts = skcase.getBlackboardArtifacts(BlackboardArtifact.ARTIFACT_TYPE.TSK_METADATA_EXIF);
//                    System.out.println(artifacts.size());
//                    for (Content content : contents) {
//                        System.out.println(content.getAllAnalysisResults());
//                        System.out.println(content.getArtifacts(BlackboardArtifact.ARTIFACT_TYPE.TSK_METADATA_EXIF));
////                        repeatArtifactsContent(skcase, content);
//                    }
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//                    IngestModuleTemplate template = IngestUtils.getIngestModuleTemplate(new PictureAnalyzerIngestModuleFactory());
//
//                    var templates = new ArrayList<IngestModuleTemplate>();
//                    templates.add(template);
//
//                    IngestJobSettings jobSettings = new IngestJobSettings(
//                            DataSourceServiceImp.class.getCanonicalName(),
//                            IngestJobSettings.IngestType.ALL_MODULES,
//                            templates);
//
//                    IngestUtils.runIngestJob(contents, jobSettings);
//    var factory = new PictureAnalyzerIngestModuleFactory();
//
//    IngestModuleInfo.IngestModuleType type = IngestModuleInfo.IngestModuleType.FILE_LEVEL;
//
//    IngestModuleInfo pictureAnalyzerModule = skcase.addIngestModule(
//            factory.getModuleDisplayName(),
//            FactoryClassNameNormalizer.normalize(factory.getClass().getCanonicalName()),
//            type,
//            String.valueOf(factory.getDefaultIngestJobSettings().getVersionNumber())
//    );
//
//    String hostName = "localhost";
//    List<IngestModuleInfo> ingestModules = new ArrayList<>();
//                    ingestModules.add(pictureAnalyzerModule);
//
//    Date jobStart = new Date();
//
//                    for (Content content : contents) {
//        IngestJobInfo ingestJob = skcase.addIngestJob(content,
//                hostName,
//                ingestModules,
//                jobStart,
//                new Date(0),
//                IngestJobInfo.IngestJobStatusType.STARTED,
//                "");
//    }
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//    public void processContent(SleuthkitCase skcase, Content content) throws TskCoreException {
//
//        if (content instanceof AbstractFile) {
////            PictureAnalyzerIngestModule panalyzer = new PictureAnalyzerIngestModule();
////            panalyzer.process((AbstractFile) content);
//            try {
//                createAndRunIngestJob(skcase, content);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        List<Content> children = content.getChildren();
//        for (Content child : children) {
//            processContent(skcase, child);
//        }
//    }
//
//    public void createAndRunIngestJob(SleuthkitCase skcase, Content content) throws TskCoreException, InterruptedException {
//
//        var factory = new PictureAnalyzerIngestModuleFactory();
//
////        String factoryClassName = "org.sleuthkit.autopsy.modules.pictureanalyzer.PictureAnalyzerIngestModuleFactory";
//        IngestModuleInfo.IngestModuleType type = IngestModuleInfo.IngestModuleType.FILE_LEVEL;
//
//        IngestModuleInfo pictureAnalyzerModule = skcase.addIngestModule(
//                factory.getModuleDisplayName(),
//                FactoryClassNameNormalizer.normalize(factory.getClass().getCanonicalName()),
//                type,
//                String.valueOf(factory.getDefaultIngestJobSettings().getVersionNumber())
//        );
//
//        String hostName = "localhost:8081";
//        List<IngestModuleInfo> ingestModules = new ArrayList<>();
//        ingestModules.add(pictureAnalyzerModule);
//
//        Date jobStart = new Date();
//
//        System.out.println("Starting the creation of the ingest job at: " + new Date());
//        System.out.println("Adding module: " + factory.getModuleDisplayName());
//
//        // Create and start the ingest job
//        IngestJobInfo ingestJob = skcase.addIngestJob(content, hostName, ingestModules, jobStart, new Date(0), IngestJobInfo.IngestJobStatusType.STARTED, "");
//        var manager = IngestManager.getInstance();
//
////        IngestJobStartResult result = manager.beginIngestJob(ingestJob);
//
//        //        ingestJobInfo.setEndDateTime(new Date());
////        ingestJobInfo.setIngestJobStatus(IngestJobInfo.IngestJobStatusType.COMPLETED);
//    }
//
//}
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//public void repeatArtifactsContent(SleuthkitCase skcase, Content content) throws TskCoreException {
//
//    if (content instanceof AbstractFile) {
//        printArtifacts(skcase, content);
//    }
//
//    List<Content> children = content.getChildren();
//    for (Content child : children) {
//        repeatArtifactsContent(skcase, child);
//    }
//}
//
//public void printArtifacts(SleuthkitCase skcase, Content content) throws TskCoreException {
//    List<BlackboardArtifact> exifArtifacts = skcase.getBlackboard().getArtifacts(16, content.getId());
//
//    System.out.println("Number of EXIF artifacts found: " + exifArtifacts.size());
//
//    for (BlackboardArtifact artifact : exifArtifacts) {
//        List<BlackboardAttribute> attributes = artifact.getAttributes();
//        for (BlackboardAttribute attribute : attributes) {
//            System.out.println(attribute.getAttributeType().getDisplayName() + ": " + attribute.getValueString());
//        }
//    }
//}
