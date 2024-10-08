package uma.autopsy.Cases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import org.sleuthkit.datamodel.SleuthkitCase;
import org.sleuthkit.datamodel.TskCoreException;
import uma.autopsy.Cases.Models.Case;
import uma.autopsy.Cases.Models.UpdateCaseRequest;
import uma.autopsy.Exceptions.CaseAlreadyExistsException;
import uma.autopsy.Exceptions.CaseNotFoundException;
import uma.autopsy.Devices.Device;
import uma.autopsy.Devices.DeviceRepository;
import uma.autopsy.GlobalProperties.GlobalProperties;

@Primary
@Service
public class CaseServiceImp implements CaseService {

    @Autowired
    private CaseRepository caseRepository;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private GlobalProperties globalProperties;

    @Override
    public Case createCase(Case caseEntity) {
        caseExistWithDeviceId(caseEntity);
        caseEntity.setDevice(getOrCreateDevice(caseEntity));
        try {
            SleuthkitCase skCase = SleuthkitCase.newCase(STR."\{getCaseDir(caseEntity.getDeviceId(), caseEntity.getName())}/\{caseEntity.getName()}");
            caseEntity.setCasePath(STR."\{skCase.getDbDirPath()}/\{skCase.getDatabaseName()}");

        } catch (TskCoreException e) {
            throw new RuntimeException(e);
        }
        return caseRepository.save(caseEntity);
    }

    public void caseExistWithDeviceId(Case caseEntity) throws CaseAlreadyExistsException {
        Optional<Case> existingCase = caseRepository.findByNameAndDeviceId(caseEntity.getName(), caseEntity.getDeviceId());
        if (existingCase.isPresent()) {
            throw new CaseAlreadyExistsException(STR."Case with name \{caseEntity.getName()} under this device ID \{caseEntity.getDeviceId()} already exists.");
        }
    }

    Device getOrCreateDevice(Case caseEntity){
        String deviceId = caseEntity.getDeviceId();
        Optional<Device> device = deviceRepository.findByHardwareId(deviceId);
        if (device.isEmpty()) {
            Device newDevice = new Device();
            newDevice.setHardwareId(deviceId);
            deviceRepository.save(newDevice);
            return newDevice;
        }
        return device.get();
    }

    String getCaseDir(String deviceId, String caseName){
        String deviceDirPath = globalProperties.getBaseDir() + "/" + deviceId + "/" + caseName;
        File deviceDir = new File(deviceDirPath);
        if (!deviceDir.exists()) {
            deviceDir.mkdirs();
        }
        return deviceDirPath;
    }

    public Case getCase(int id, String deviceId){
        Optional<Case> caseOptional = caseRepository.findByIdAndDeviceId(id, deviceId);
        if (caseOptional.isPresent()) {
            if (!validateDeviceId(deviceId, caseOptional.get())) {  throw new RuntimeException("Not Authorized for this operation"); }
            return caseOptional.get();
        } else {
            throw new CaseNotFoundException(STR."Case not found with id: \{id}");
        }
    }

    public List<Case> getAllCases(){
        return caseRepository.findAll();
    }

    public List<Case> getCasesByDeviceId(String deviceId) {
        return caseRepository.findByDeviceId(deviceId);
    }

    @Override
    public Case updateCase(UpdateCaseRequest caseRequest, String deviceId) {
        Optional<Case> caseOptional = caseRepository.findByIdAndDeviceId(caseRequest.getCaseId(), deviceId);

        if (caseOptional.isPresent()) {
            Case caseEntity = caseOptional.get();
            if (!validateDeviceId(deviceId, caseEntity)) {  throw new RuntimeException("Not Authorized for this operation"); }

            caseEntity.setNumber(caseRequest.getNumber());
            caseEntity.setExaminerName(caseRequest.getExaminerName());
            caseEntity.setExaminerEmail(caseRequest.getExaminerEmail());
            caseEntity.setExaminerPhone(caseRequest.getExaminerPhone());
            caseEntity.setExaminerNotes(caseRequest.getExaminerNotes());

            return caseRepository.save(caseEntity);
        } else {
            throw new CaseNotFoundException(STR."Case not found with id: \{caseRequest.getCaseId()}");
        }
    }

    public void deleteCaseByIdAndDeviceId(int id, String deviceId) {
        Optional<Case> caseEntityOptional = caseRepository.findByIdAndDeviceId(id, deviceId);
        if (caseEntityOptional.isPresent()) {
            Case caseEntity = caseEntityOptional.get();
            deleteCaseFile(caseEntity.getCasePath());
            caseRepository.deleteById(id);
        } else {
            throw new CaseNotFoundException(STR."Case not found with id: \{id}");
        }
    }

    private void deleteCaseFile(String caseFilePath) {
        try {
            Path path = Paths.get(caseFilePath);
            Files.deleteIfExists(path);
            deleteDirIfEmpty(path.getParent());
        } catch (IOException e) {
            throw new RuntimeException(STR."Failed to delete case file: \{caseFilePath}", e);
        }
    }

    private void deleteDirIfEmpty(Path directory) {
        try {
            if (isDirectoryEmpty(directory)) {
                Files.delete(directory);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete directory: " + directory, e);
        }
    }

    private boolean isDirectoryEmpty(Path directory) {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
            for (Path path : directoryStream) {
                // delete hidden files like .DS_Store
                if (Files.isHidden(path) || path.getFileName().toString().equals(".DS_Store")) {
                    Files.delete(path);
                } else {
                    return false; // Found a non-hidden file, so the directory is not empty
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to check directory: " + directory, e);
        }

        // Re-check directory contents to confirm it is empty
        try (DirectoryStream<Path> finalDirectoryStream = Files.newDirectoryStream(directory)) {
            return !finalDirectoryStream.iterator().hasNext();
        } catch (IOException e) {
            throw new RuntimeException("Failed to re-check directory: " + directory, e);
        }
    }

    boolean validateDeviceId(String deviceId, Case caseEntity){
        return deviceId.equalsIgnoreCase(caseEntity.getDeviceId());
    }

}
