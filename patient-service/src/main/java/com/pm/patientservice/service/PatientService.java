package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.EmailAlreadyExistException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.grpc.BillingServiceGrpcClient;
import com.pm.patientservice.kafka.KafkaProducer;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PatientService {

    private PatientRepository patientRepository;

    private BillingServiceGrpcClient billingServiceGrpcClient;

    private KafkaProducer kafkaProducer;

    public PatientService(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient, KafkaProducer kafkaProducer) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
    }

    public List<PatientResponseDTO> getAllPatients() {
        List<Patient> patientList = patientRepository.findAll();
        return patientList.stream().map(PatientMapper::toDTO).toList();
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO){
        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())){
           throw new EmailAlreadyExistException("Email already exist : " + patientRequestDTO.getEmail());
        }
        Patient patient = PatientMapper.toModel(patientRequestDTO);
        Patient savedPatient = patientRepository.save(patient);
        billingServiceGrpcClient.createBillingAccount(
                savedPatient.getId().toString(),
                savedPatient.getName(),
                savedPatient.getEmail()
        );
        kafkaProducer.sendEvent(savedPatient);
        return PatientMapper.toDTO(savedPatient);
    }

    public PatientResponseDTO updatePatient(String patientId, PatientRequestDTO patientRequestDTO){
        UUID uuid = UUID.fromString(patientId);
        Patient patient = patientRepository.findById(uuid).orElseThrow(
                () -> new PatientNotFoundException("Patient not found with this ID : {}", UUID.fromString(patientId)));
        if(patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), uuid)){
            throw new EmailAlreadyExistException("A patient with " + patientRequestDTO.getEmail() + " Email already exist");
        }
        patient.setName(patientRequestDTO.getName());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));
        patient.setRegisteredDate(LocalDate.parse(patientRequestDTO.getRegisteredDate()));
        return PatientMapper.toDTO(patientRepository.save(patient));
    }

    public void deletePatient(String patientId){
        UUID uuid = UUID.fromString(patientId);
        patientRepository.deleteById(uuid);
    }


}
