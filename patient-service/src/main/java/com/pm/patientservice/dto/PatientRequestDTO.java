package com.pm.patientservice.dto;


import com.pm.patientservice.dto.validators.CreatePatientValidationGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class PatientRequestDTO {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name size 100")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "email should be valid")
    private String email;

    @NotBlank(groups = CreatePatientValidationGroup.class, message = "Address is required")
    private String address;

    @NotBlank(message = "DOB is required")
    private String dateOfBirth;

    @NotBlank(message = "Registered date is required")
    private String registeredDate;

}
