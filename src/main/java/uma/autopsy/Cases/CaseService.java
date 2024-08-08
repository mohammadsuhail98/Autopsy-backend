package uma.autopsy.Cases;

import java.util.List;

public interface CaseService {
    Case createCase(Case caseEntity);
    Case getCase(int id, String deviceId);
    List<Case> getAllCases();
    List<Case> getCasesByDeviceId(String deviceId);
    void deleteCaseByIdAndDeviceId(int id, String deviceId);;
}
