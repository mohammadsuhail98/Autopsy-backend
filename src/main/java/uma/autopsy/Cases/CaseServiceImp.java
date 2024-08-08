package uma.autopsy.Cases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import org.sleuthkit.datamodel.SleuthkitCase;
import org.sleuthkit.datamodel.TskCoreException;
import uma.autopsy.Exceptions.BadRequestException;
import uma.autopsy.Exceptions.CaseAlreadyExistsException;
import uma.autopsy.Exceptions.CaseDoesNotExistException;
import uma.autopsy.Cases.Models.Case;
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
            SleuthkitCase skCase = SleuthkitCase.newCase(STR."\{getCaseDir(caseEntity.getDeviceId())}/\{caseEntity.getName()}");
            caseEntity.setCasePath(skCase.getDbDirPath());
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

    String getCaseDir(String deviceId){
        String deviceDirPath = globalProperties.getBaseDir() + "/" + deviceId;
        File deviceDir = new File(deviceDirPath);
        if (!deviceDir.exists()) {
            deviceDir.mkdirs();
        }
        return deviceDirPath;
    }

    public Case getCase(int id, String deviceId){
        Optional<Case> caseOptional = caseRepository.findByIdAndDeviceId(id, deviceId);
        if (caseOptional.isPresent()) {
            return caseOptional.get();
        } else {
            throw new CaseDoesNotExistException(STR."Case not found with id: \{id}");
        }
    }

    public List<Case> getAllCases(){
        return caseRepository.findAll();
    }

    public List<Case> getCasesByDeviceId(String deviceId) {
        return caseRepository.findByDeviceId(deviceId);
    }

    public void deleteCaseByIdAndDeviceId(int id, String deviceId) {
        Optional<Case> caseEntity = caseRepository.findByIdAndDeviceId(id, deviceId);
        if (caseEntity.isPresent()) {
            caseRepository.deleteById(id);
        } else {
            throw new CaseDoesNotExistException(STR."Case not found with id: \{id}");
        }
    }


}
