package uma.autopsy.Cases;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uma.autopsy.Cases.Models.Case;
import uma.autopsy.Cases.Models.UpdateCaseRequest;

import java.util.List;

@RestController
@RequestMapping("/api/cases")
public class CaseController {

    @Autowired
    private CaseService caseService;

    @PostMapping
    public ResponseEntity<?> createCase(@Valid @ModelAttribute Case caseEntity) {
        return new ResponseEntity<>(caseService.createCase(caseEntity), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCase(@PathVariable int id, @RequestHeader("deviceId") String deviceId) {
        Case caseEntity = caseService.getCase(id, deviceId);
        return new ResponseEntity<>(caseEntity, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCaseById(@PathVariable int id, @RequestHeader("deviceId") String deviceId) {
        caseService.deleteCaseByIdAndDeviceId(id, deviceId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("all")
    public ResponseEntity<List<Case>> getAllCases() {
        List<Case> cases = caseService.getAllCases();
        return new ResponseEntity<>(cases, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getCasesByDeviceId(@RequestHeader("deviceId") String deviceId) {
        List<Case> cases = caseService.getCasesByDeviceId(deviceId);
        return new ResponseEntity<>(cases, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCase(@ModelAttribute UpdateCaseRequest caseRequest,
                                        @RequestHeader("deviceId") String deviceId) {
        return new ResponseEntity<>(caseService.updateCase(caseRequest, deviceId), HttpStatus.OK);
    }

}
