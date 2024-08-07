package uma.autopsy.Cases;

import jakarta.validation.Valid;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uma.autopsy.Cases.Exceptions.CaseAlreadyExistsException;
import uma.autopsy.Cases.Models.Case;
import uma.autopsy.Cases.Models.ErrorResponse;

import java.util.List;

@RestController
@RequestMapping("/api/cases")
public class CaseController {

    @Autowired
    private CaseService caseService;

    @PostMapping
    public ResponseEntity<?> createCase(@Valid @ModelAttribute @RequestBody Case caseEntity) {
        try {
            return new ResponseEntity<>(caseService.createCase(caseEntity), HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (CaseAlreadyExistsException e) {
            return new ResponseEntity<>(new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage()), HttpStatus.CONFLICT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Resource not found"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCase(@PathVariable int id) {
        try {
            Case caseEntity = caseService.getCase(id);
            return new ResponseEntity<>(caseEntity, HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (CaseAlreadyExistsException e) {
            return new ResponseEntity<>(new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage()), HttpStatus.CONFLICT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Resource not found"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCaseById(@PathVariable int id) {
        try {
            caseService.deleteCaseById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (CaseAlreadyExistsException e) {
            return new ResponseEntity<>(new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Case>> getAllCases() {
        List<Case> cases = caseService.getAllCases();
        return new ResponseEntity<>(cases, HttpStatus.OK);
    }

}
