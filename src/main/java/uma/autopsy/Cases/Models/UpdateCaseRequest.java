package uma.autopsy.Cases.Models;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateCaseRequest {
    @NotBlank(message = "Case ID is mandatory")
    private int caseId;
    private int number;
    private String examinerName;
    private String examinerPhone;
    private String examinerEmail;
    private String examinerNotes;
}
