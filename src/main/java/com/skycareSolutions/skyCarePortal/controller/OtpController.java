package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.EmployeeDTO;
import com.skycareSolutions.skyCarePortal.dto.OTPValidResponseDTO;
import com.skycareSolutions.skyCarePortal.dto.PasswordResetDTO;
import com.skycareSolutions.skyCarePortal.model.Client;
import com.skycareSolutions.skyCarePortal.repository.ClientRepository;
import com.skycareSolutions.skyCarePortal.service.ClientService;
import com.skycareSolutions.skyCarePortal.service.EmailService;
import com.skycareSolutions.skyCarePortal.service.EmployeeService;
import com.skycareSolutions.skyCarePortal.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@CrossOrigin("*")
@RequestMapping("/otp")
public class OtpController {

    @Autowired
    private OtpService otpService;

    @Autowired
    @Lazy
    private EmailService emailService;

    @Autowired
    @Lazy
    private EmployeeService employeeService;

    @Autowired
    @Lazy
    private ClientRepository clientRepository;

    @PostMapping("/send")
    public ResponseEntity<String> sendOtp(@RequestParam String email) {
        EmployeeDTO employee = employeeService.findEmployeeByEmail(email);
        if (employee!=null){
            String otp = otpService.generateAndStoreOtp(email);
            CompletableFuture.runAsync(()->emailService.generateResetPasswordEmail(employee.getFirstName(),otp,email));
            return ResponseEntity.ok("OTP sent successfully");
        }else{
            Client client = clientRepository.findByEmail(email);
            if (client!=null){
                String otp = otpService.generateAndStoreOtp(email);
                CompletableFuture.runAsync(()->emailService.generateResetPasswordEmail(client.getFirstName(),otp,email));
                return ResponseEntity.ok("OTP sent successfully");
            }
            return ResponseEntity.badRequest().body("Employee not found with the given email");
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<OTPValidResponseDTO> validateOtp(@RequestParam String email, @RequestParam String otp) {
        boolean isValid = otpService.validateOtp(email, otp);
        OTPValidResponseDTO otpValidResponseDTO = new OTPValidResponseDTO();
        if (isValid) {
            Client client = clientRepository.findByEmail(email);
            if (client!=null){
                otpValidResponseDTO.setEmployeeID(client.getClientID());
            }else{
                EmployeeDTO employee = employeeService.findEmployeeByEmail(email);
                otpValidResponseDTO.setEmployeeID(employee.getEmployeeID());
                otpValidResponseDTO.setMessage("OTP validated Successfully");
            }
            return ResponseEntity.ok(otpValidResponseDTO);
        } else {
            otpValidResponseDTO.setMessage("Invalid or expired OTP");
            return ResponseEntity.status(400).body(otpValidResponseDTO);
        }
    }
}
