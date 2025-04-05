package com.pm.patientservice.controller;

import com.pm.patientservice.constants.RequestMappingConstants;
import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.dto.validators.CreatePatientValidationGroup;
import com.pm.patientservice.exception.EmailAlreadyExistException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = RequestMappingConstants.PATIENTS_CONTROLLER)
@Tag(name = "Patient", description = "API for managing patients")
public class PatientController {

    private static final Logger log = LoggerFactory.getLogger(PatientController.class);
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @RequestMapping(value = RequestMappingConstants.PATIENTS_GET_ALL, method = RequestMethod.GET)
    @Operation(summary = "Get Patients")
    public ResponseEntity<List<PatientResponseDTO>> getAllPatients() {
        try {
            return ResponseEntity.ok(patientService.getAllPatients());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @RequestMapping(value = RequestMappingConstants.PATIENTS_CREATE, method = RequestMethod.POST)
    @Operation(summary = "Create Patients")
    public ResponseEntity<?> createPatient(@Validated({Default.class, CreatePatientValidationGroup.class}) @RequestBody PatientRequestDTO patientRequestDTO) {
        try {
            PatientResponseDTO patientResponseDTO = patientService.createPatient(patientRequestDTO);
            return ResponseEntity.ok(patientResponseDTO);
        } catch (EmailAlreadyExistException ex) {
            log.error("Email already exist : {}", ex.getMessage());
            return ResponseEntity.badRequest().body("Invalid input: " + ex.getMessage());
        } catch (Exception ex) {
            log.error("Unexpected error while creating patient: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred. Please try again later.");
        }
    }

    /* *
     * @Validated : will check for every field inside the response dto
     *
     * */
    @RequestMapping(value = RequestMappingConstants.PATIENTS_UPDATE, params = {"id"}, method = RequestMethod.PUT)
    @Operation(summary = "Update Patients")
    public ResponseEntity<?> updatePatientById(@RequestParam String id, @Validated({Default.class}) @RequestBody PatientRequestDTO patientRequestDTO) {
        try {
            PatientResponseDTO patientResponseDTO = patientService.updatePatient(id, patientRequestDTO);
            return ResponseEntity.ok(patientResponseDTO);
        } catch (EmailAlreadyExistException ex) {
            log.error("A patient with this emailId already exists : {}", patientRequestDTO.getEmail(), ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A patient with this emailId already exists : " + patientRequestDTO.getEmail());
        } catch (PatientNotFoundException ex) {
            log.error("Patient not exist : {}", id);
            return ResponseEntity.badRequest().body("Invalid input: " + ex.getMessage());
        } catch (Exception ex) {
            log.error("Unexpected error while creating patient: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("An unexpected error occurred. Please try again later.");
        }
    }

    @RequestMapping(value = RequestMappingConstants.PATIENT_DELETE, params = {"id"}, method = RequestMethod.DELETE)
    @Operation(summary = "Delete Patients")
    public ResponseEntity<?> deletePatientById(@RequestParam String id) {
         try{
             patientService.deletePatient(id);
             return ResponseEntity.ok().build();
         }catch (PatientNotFoundException ex) {
             log.error("Patient not exist : {}", id);
             return ResponseEntity.badRequest().body("Invalid input: " + ex.getMessage());
         }
    }


}
