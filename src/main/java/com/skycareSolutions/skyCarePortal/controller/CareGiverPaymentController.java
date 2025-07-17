package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.ApiResponse;
import com.skycareSolutions.skyCarePortal.dto.CareGiverPaymentsDTO;
import com.skycareSolutions.skyCarePortal.service.CareGiverPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/care-givers-payments")
public class CareGiverPaymentController {

    @Autowired
    private CareGiverPaymentService careGiverPaymentService;

    @GetMapping
    public ResponseEntity<List<CareGiverPaymentsDTO>> getAllPayments() {
        return ResponseEntity.ok(careGiverPaymentService.getAllPayments());
    }

    @GetMapping("/{careGiverID}")
    public ResponseEntity<List<CareGiverPaymentsDTO>> getPaymentsByCareGiverId(@PathVariable String careGiverID) {
        List<CareGiverPaymentsDTO> payments = careGiverPaymentService.getPaymentsByCareGiverId(careGiverID);
        return payments.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(payments);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> addPayment(@RequestBody List<CareGiverPaymentsDTO> dto) {
        return ResponseEntity.ok(new ApiResponse("Successfully Saved payment inforamtion",true));
    }

    @PutMapping("/{careGiverID}/{paymentTypeID}")
    public ResponseEntity<CareGiverPaymentsDTO> updatePayment(
            @PathVariable String careGiverID,
            @PathVariable String paymentTypeID,
            @RequestBody CareGiverPaymentsDTO dto
    ) {
        return ResponseEntity.ok(careGiverPaymentService.updatePayment(careGiverID, paymentTypeID, dto));
    }

//    @PutMapping("/{careGiverID}")
//    public ResponseEntity<List<CareGiverPaymentsDTO>> updatePaymentDetails(
//            @PathVariable String careGiverID,
//            @RequestBody CareGiverPaymentsDTO dto
//    ) {
//        return ResponseEntity.ok(careGiverPaymentService.updatePayment(careGiverID, paymentTypeID, dto));
//    }

    @DeleteMapping("/{careGiverID}/{paymentTypeID}")
    public ResponseEntity<Void> deletePayment(@PathVariable String careGiverID, @PathVariable String paymentTypeID) {
        careGiverPaymentService.deletePayment(careGiverID, paymentTypeID);
        return ResponseEntity.noContent().build();
    }
}
