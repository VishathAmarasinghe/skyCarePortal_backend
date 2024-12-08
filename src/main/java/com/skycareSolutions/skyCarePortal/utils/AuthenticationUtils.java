package com.skycareSolutions.skyCarePortal.utils;

import com.skycareSolutions.skyCarePortal.dto.EmployeeDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AuthenticationUtils {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // Declare SECRET_KEY as a private static final variable
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Use a strong key

    public String hashPassword(String password) {
        return encoder.encode(password);
    }

    public boolean matchPassword(String rawPassword, String hashedPassword) {
        return encoder.matches(rawPassword, hashedPassword);
    }

    public String generateToken(EmployeeDTO user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("userId", user.getEmployeeID());
        claims.put("userRole", user.getEmployeeJobRoles());

        // Assign accessRole based on user access role
        if (user.getAccessRole().equalsIgnoreCase("Admin")){
            claims.put("accessRole", List.of("admin.skyCarePortal", "superadmin.skyCarePortal"));
        } else if (user.getAccessRole().equalsIgnoreCase("CareGiver")){
            claims.put("accessRole", List.of("caregiver.skyCarePortal"));
        }

        // Create the JWT token
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // Token valid for 1 hour
                .signWith(SECRET_KEY)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            JwtParser parser = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build();
            parser.parseClaimsJws(token); // This line parses and validates the token
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractEmail(String token) {
        JwtParser parser = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build();
        return parser.parseClaimsJws(token).getBody().getSubject();
    }
}
