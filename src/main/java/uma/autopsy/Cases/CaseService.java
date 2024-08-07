package uma.autopsy.Cases;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import uma.autopsy.Cases.Models.Case;

import java.util.List;

public interface CaseService {
    Case createCase(Case caseEntity);
    Case getCase(int id);
    List<Case> getAllCases();
    void deleteCaseById(int id);
}
