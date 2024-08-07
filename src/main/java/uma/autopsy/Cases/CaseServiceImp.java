package uma.autopsy.Cases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import org.sleuthkit.datamodel.SleuthkitCase;
import org.sleuthkit.datamodel.TskCoreException;
import uma.autopsy.Cases.Exceptions.CaseAlreadyExistsException;
import uma.autopsy.Cases.Exceptions.CaseDoesNotExistException;
import uma.autopsy.Cases.Models.Case;
import uma.autopsy.Devices.Device;
import uma.autopsy.Devices.DeviceRepository;

@Primary
@Service
public class CaseServiceImp implements CaseService {

    @Autowired
    private CaseRepository caseRepository;
    @Autowired
    private DeviceRepository deviceRepository;
    String caseDir = "/Users/mohammadsuhail/Desktop/cases";

    @Override
    public Case createCase(Case caseEntity) {

        Optional<Case> existingCase = caseRepository.findByNameAndDeviceId(caseEntity.getName(), caseEntity.getDeviceId());
        if (existingCase.isPresent()) {
            throw new CaseAlreadyExistsException("Case with name " + caseEntity.getName() + " and hardware ID " + caseEntity.getDeviceId() + " already exists.");
        }

        Optional<Device> device = deviceRepository.findByHardwareId(caseEntity.getDeviceId());
        if (!device.isPresent()) {
            // If the device doesn't exist, create a new one
            Device newDevice = new Device(caseEntity.getDeviceId());
            deviceRepository.save(newDevice);
            device = Optional.of(newDevice);
            caseEntity.setDevice(device.get());
        } else {
            caseEntity.setDevice(device.get());
        }

        // Ensure the directory structure exists
        String deviceDirPath = caseDir + "/" + caseEntity.getDeviceId();
        File deviceDir = new File(deviceDirPath);
        if (!deviceDir.exists()) {
            deviceDir.mkdirs();
        }

        try {
            SleuthkitCase skCase = SleuthkitCase.newCase(deviceDirPath + "/" + caseEntity.getName());
            caseEntity.setCasePath(skCase.getDbDirPath());
        } catch (TskCoreException e) {
            throw new RuntimeException(e);
        }
        return caseRepository.save(caseEntity);
    }

    public Case getCase(int id){
        Optional<Case> caseOptional = caseRepository.findById(id);
        if (caseOptional.isPresent()) {
            return caseOptional.get();
        } else {
            throw new RuntimeException(STR."Case not found with id: \{id}");
        }
    }

    public List<Case> getAllCases(){
        return caseRepository.findAll();
    }

    public void deleteCaseById(int id){
        Optional<Case> existingCase = caseRepository.findById(id);
        if (existingCase.isEmpty()) {
            throw new CaseDoesNotExistException(STR."Case with id \{id} does not exist.");
        }
        caseRepository.deleteById(id);
    }


}
