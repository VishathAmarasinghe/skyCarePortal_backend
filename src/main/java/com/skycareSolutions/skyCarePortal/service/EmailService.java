package com.skycareSolutions.skyCarePortal.service;

import com.skycareSolutions.skyCarePortal.model.EmailTemplates;
import com.skycareSolutions.skyCarePortal.repository.EmailRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private EmailRepository emailRepository;

    // Send individual email
    private void sendEmail(String recipient, String template,String title) throws MessagingException {
        String htmlTemplate = template;
        if (htmlTemplate.startsWith("'") && htmlTemplate.endsWith("'")) {
            htmlTemplate = htmlTemplate.substring(1, htmlTemplate.length() - 1);
        }

        // Create a MIME message (supports HTML content)
        try{
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("no-reply@skycaresolutions.com.au");
            helper.setTo(recipient);
            helper.setSubject(title);
            helper.setText(htmlTemplate, true);  // true means HTML content

            javaMailSender.send(message);
            System.out.println("email sent");
        }catch (Exception e){
            throw new MessagingException();
        }

    }

    // Send bulk emails
    private void sendBulkEmails(List<String> recipients, String template,String title) throws MessagingException {

        for (String recipient : recipients) {
            sendEmail(recipient, template,title);
        }
    }

    public void generateWelcomeLoginEmail(String userName, String userEmail, String generatedPassword, String loginUrl) {
        // Retrieve the email template by template ID
        EmailTemplates emailTemplate = emailRepository.findById("ET00005").orElse(null);

        if (emailTemplate == null) {
            throw new RuntimeException("Template not found for ID: ET00005");
        }

        // Get the content of the template
        String emailContent = emailTemplate.getContent();

        // Replace dynamic fields with actual values
        emailContent = emailContent.replace("[User Name]", userName)
                .replace("[User Email]", userEmail)
                .replace("[Generated Password]", generatedPassword)
                .replace("[Login URL]", loginUrl);

        try{
            sendEmail(userEmail,emailContent,emailTemplate.getTitle());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    // Generate Reset Password Email
    public void generateResetPasswordEmail(String userName, String otpCode, String userEmail) {
        // Retrieve the email template by template ID
        EmailTemplates emailTemplate = emailRepository.findById("ET00006").orElse(null);

        if (emailTemplate == null) {
            throw new RuntimeException("Template not found for ID: ET00006");
        }

        // Get the content of the template
        String emailContent = emailTemplate.getContent();

        // Replace dynamic fields with actual values
        emailContent = emailContent.replace("[User Name]", userName)
                .replace("[OTP Code]", otpCode);

        try {
            sendEmail(userEmail, emailContent, emailTemplate.getTitle());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Generate Documents Expired Email
    public void generateDocumentsExpiredEmail(String userName, String uploadUrl, String userEmail) {
        // Retrieve the email template by template ID
        EmailTemplates emailTemplate = emailRepository.findById("ET00004").orElse(null);

        if (emailTemplate == null) {
            throw new RuntimeException("Template not found for ID: ET00004");
        }

        // Get the content of the template
        String emailContent = emailTemplate.getContent();

        // Replace dynamic fields with actual values
        emailContent = emailContent.replace("[User Name]", userName)
                .replace("[Upload URL]", uploadUrl);

        try {
            sendEmail(userEmail, emailContent, emailTemplate.getTitle());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Generate Document Submission Acknowledgment Email
    public void generateDocumentSubmissionAcknowledgmentEmail(String userName, String userEmail) {
        // Retrieve the email template by template ID
        EmailTemplates emailTemplate = emailRepository.findById("ET00003").orElse(null);

        if (emailTemplate == null) {
            throw new RuntimeException("Template not found for ID: ET00003");
        }

        // Get the content of the template
        String emailContent = emailTemplate.getContent();

        // Replace dynamic fields with actual values
        emailContent = emailContent.replace("[User Name]", userName);

        try {
            sendEmail(userEmail, emailContent, emailTemplate.getTitle());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Generate Appointment Reminder Email
    public void generateAppointmentReminderEmail(String userName, String userEmail, String appointmentDate, String appointmentTitle, String appointmentDetailsUrl) {
        // Retrieve the email template by template ID
        EmailTemplates emailTemplate = emailRepository.findById("ET00002").orElse(null);

        if (emailTemplate == null) {
            throw new RuntimeException("Template not found for ID: ET00002");
        }

        // Get the content of the template
        String emailContent = emailTemplate.getContent();

        // Replace dynamic fields with actual values
        emailContent = emailContent.replace("[User Name]", userName)
                .replace("[Appointment Date]", appointmentDate)
                .replace("[AppointmentTitle]", appointmentTitle)
                .replace("[Appointment Details URL]", appointmentDetailsUrl);

        try {
            sendEmail(userEmail, emailContent, emailTemplate.getTitle());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Generate Appointment Confirmation Email
    public void generateAppointmentConfirmationEmail(String userName, String userEmail, String appointmentUrl) {
        // Retrieve the email template by template ID
        EmailTemplates emailTemplate = emailRepository.findById("ET00001").orElse(null);

        if (emailTemplate == null) {
            throw new RuntimeException("Template not found for ID: ET00001");
        }

        // Get the content of the template
        String emailContent = emailTemplate.getContent();

        // Replace dynamic fields with actual values
        emailContent = emailContent.replace("[User Name]", userName)
                .replace("[Appointment URL]", appointmentUrl);

        try {
            sendEmail(userEmail, emailContent, emailTemplate.getTitle());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }




}
