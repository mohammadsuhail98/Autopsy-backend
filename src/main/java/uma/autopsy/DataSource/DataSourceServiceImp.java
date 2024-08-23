package uma.autopsy.DataSource;

import org.sleuthkit.autopsy.coreutils.Logger;
import org.sleuthkit.autopsy.modules.filetypeid.FileTypeDetector;
import org.sleuthkit.datamodel.*;
import org.sleuthkit.datamodel.SleuthkitCase.CaseDbTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uma.autopsy.Cases.Case;
import uma.autopsy.Cases.CaseRepository;
import uma.autopsy.Exceptions.CaseDoesNotExistException;
import uma.autopsy.Exceptions.ResourceNotFoundException;
import uma.autopsy.GlobalProperties.GlobalProperties;
import uma.autopsy.Utils.DiskImageValidator;
import uma.autopsy.Utils.ExifProcessor;

import java.io.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;

@Service
public class DataSourceServiceImp implements DataSourceService {

    private static final Logger logger = Logger.getLogger(ExifProcessor.class.getName());

    @Autowired
    private DataSourceRepository dataSourceRepository;
    @Autowired
    private CaseRepository caseRepository;
    @Autowired
    private GlobalProperties globalProperties;

    public DataSource addDataSource(int caseId, MultipartFile file, DataSource dataSource, String deviceId) {
        Case caseEntity = getCase(caseId);
        if (!validateDeviceId(deviceId, caseEntity)) {  throw new RuntimeException("Not Authorized for this operation"); }

        dataSource.setCaseEntity(caseEntity);

        String tempFileDir = getTempFileDir(file, caseEntity);
        SleuthkitCase skcase = null;
        CaseDbTransaction transaction = null;
        Image image = null;

        var imageType = TskData.TSK_IMG_TYPE_ENUM.TSK_IMG_TYPE_DETECT;
        try {
            imageType = DiskImageValidator.validateDiskImage(file);
            file.transferTo(new File(tempFileDir));
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }

        try {
            skcase = SleuthkitCase.openCase(caseEntity.getCasePath());

            var sectorSize = dataSource.getSectorSize();
            var size = file.getSize();
            var displayName = file.getOriginalFilename();
            var pathList = new ArrayList<String>();
            pathList.add(tempFileDir);
            var timeZone = dataSource.getTimeZone();
            var md5Hash = validHash(dataSource.getMd5Hash());
            var sha1Hash = validHash(dataSource.getSha1Hash());
            var sha256Hash = validHash(dataSource.getSha256Hash());
            var processUUID = UUID.randomUUID().toString();
            transaction = skcase.beginTransaction();

            image = skcase.addImage(imageType, sectorSize, size, displayName, pathList, timeZone, md5Hash, sha1Hash, sha256Hash, UUID.randomUUID().toString(), transaction);

            transaction.commit();

            try {
                var addDataSourceCallbacks = getAddDataSourceCallbacks(dataSource, skcase);
                SleuthkitJNI.CaseDbHandle.AddImageProcess process = skcase.makeAddImageProcess(dataSource.getTimeZone(), dataSource.isAddUnAllocSpace(), dataSource.isIgnoreOrphanFiles(), "");
                process.run(processUUID,image, (int) image.getSsize(), addDataSourceCallbacks);
            } catch (TskDataException ex) {
                if (!ex.getMessage().contains("Cannot determine file system type")) {
                    throw new RuntimeException(STR."Error during addImageProcess: \{ex.getMessage()}");
                } else {
                    dataSource.setErrors("Errors occurred while ingesting image: Cannot determine file system type");
                }
            }

            dataSource.setName(image.getName());
            dataSource.setDataSourceId(image.getId());
            dataSource.setDataSourceDeviceId(processUUID);
            dataSource.setSize(image.getSize());
            dataSource.setSectorSize((int) image.getSsize());
            dataSource.setFileType(image.getType().getName());
            dataSource.setMd5Hash(image.getMd5());
            dataSource.setSha1Hash(image.getSha1());
            dataSource.setSha256Hash(image.getSha256());
            dataSource.setTimeZone(image.getTimeZone());

            return dataSourceRepository.save(dataSource);

        } catch (TskCoreException e) {
            logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (TskCoreException ex) {
                    throw new RuntimeException(ex);
                }
            }
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }

    private AddDataSourceCallbacks getAddDataSourceCallbacks(DataSource dataSource, SleuthkitCase skcase) {
        return list -> {
            this.updateMimeTypes(skcase, list);
            if (dataSource.isExifParser()) {
                this.processExifData(skcase, list);
            }
        };
    }

    private void updateMimeTypes(SleuthkitCase skcase, List<Long> dataSourceIds){
        try {
            for (var id: dataSourceIds) {
                var content = skcase.getContentById(id);
                if (content != null && content instanceof AbstractFile abstractFile) {
                    var detector = new FileTypeDetector();
                    var mimeType = detector.getMIMEType(abstractFile);
                    abstractFile.setMIMEType(mimeType);
                    abstractFile.save();
                }
            }
        } catch (TskCoreException e) {
            throw new RuntimeException(e);
        } catch (FileTypeDetector.FileTypeDetectorInitException e) {
            logger.log(Level.WARNING, e.getLocalizedMessage(), e);
        }
    }

    private void processExifData(SleuthkitCase skcase, List<Long> dataSourceIds){
        var processor = new ExifProcessor(skcase);
        Content content = null;
        try {
            for (var id: dataSourceIds) {
                content = skcase.getContentById(id);
                if (content != null) {
                    processor.processAllDirectories(content);
                }
            }
        } catch (TskCoreException e) {
            throw new RuntimeException(e);
        }
    }

    String validHash(String hash){
        if (hash != null && !hash.isEmpty() && !hash.isBlank())
            return hash;
        return "";
    }

    boolean validateDeviceId(String deviceId, Case caseEntity){
        return deviceId.equalsIgnoreCase(caseEntity.getDeviceId());
    }

    String getTempFileDir(MultipartFile file, Case caseEntity){
        String caseParentDir = STR."\{globalProperties.getBaseDir()}/\{caseEntity.getDeviceId()}/\{caseEntity.getName()}";
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String tempFileName = STR."\{uuid}-\{file.getOriginalFilename()}";
        return STR."\{caseParentDir}/\{tempFileName}";
    }

    public List<DataSource> getAllDataSourcesByCaseId(int caseId, String deviceId) {
        return dataSourceRepository.findByCaseEntity_Id(caseId);
    }

    private void deleteTempFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException(STR."Failed to delete file: \{filePath}", e);
        }
    }

    public DataSource getDataSourceById(int caseId, int dataSourceId,  String deviceId) {
        Case caseEntity = getCase(caseId);
        if (!validateDeviceId(deviceId, caseEntity)) {  throw new RuntimeException("Not Authorized for this operation"); }
        return dataSourceRepository.findByIdAndCaseEntity_Id(dataSourceId, caseId)
                .orElseThrow(() -> new ResourceNotFoundException("DataSource not found for this id: " + dataSourceId));
    }

    public void deleteDataSource(int caseId, int dataSourceId,  String deviceId) {
        DataSource dataSource = getDataSourceById(caseId, dataSourceId, deviceId);
        dataSourceRepository.delete(dataSource);
    }

    private Case getCase(int caseId){
        return caseRepository.findById(caseId)
                .orElseThrow(() -> new CaseDoesNotExistException(STR."Case not found for this id : \{caseId}"));
    }

}
