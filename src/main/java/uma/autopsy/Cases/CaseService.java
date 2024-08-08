package uma.autopsy.Cases;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import uma.autopsy.Cases.Models.Case;

import java.util.List;
import java.util.Optional;

public interface CaseService {
    Case createCase(Case caseEntity);
    Case getCase(int id, String deviceId);
    List<Case> getAllCases();
    List<Case> getCasesByDeviceId(String deviceId);
    void deleteCaseByIdAndDeviceId(int id, String deviceId);;
}
