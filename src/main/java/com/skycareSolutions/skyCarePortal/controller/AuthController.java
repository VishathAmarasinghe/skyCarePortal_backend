package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.EmployeeDTO;
import com.skycareSolutions.skyCarePortal.dto.LoginRequestDTO;
import com.skycareSolutions.skyCarePortal.dto.PasswordResetDTO;
import com.skycareSolutions.skyCarePortal.service.EmployeeService;
import com.skycareSolutions.skyCarePortal.utils.AuthenticationUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/auth")
public class AuthController {

    private final EmployeeService employeeService;
    private final AuthenticationUtils authConfis;

    public AuthController(EmployeeService employeeService, AuthenticationUtils authConfis) {
        this.employeeService = employeeService;
        this.authConfis = authConfis;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            EmployeeDTO user = employeeService.loginUser(loginRequestDTO.getEmail(),loginRequestDTO.getPassword());
            String token = authConfis.generateToken(user);
            return ResponseEntity.ok(token); // Return JWT token
        } catch (RuntimeException e) {
            System.out.println(e);
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetDTO passwordResetDTO){
        if (employeeService.resetPassword(passwordResetDTO)){
            return ResponseEntity.ok("Password Changed Successfully");
        }else{
            return ResponseEntity.internalServerError().body("Error occurred in password saving");
        }

    }
}
