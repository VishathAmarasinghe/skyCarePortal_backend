package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.*;
import com.skycareSolutions.skyCarePortal.service.JobAssignerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/job-assigner")
public class JobAssignerController {

    @Autowired
    private JobAssignerService jobAssignerService;

    @PostMapping
    public ResponseEntity<ApiResponse> createJobAssigner(@RequestBody JobAssignCreationDTO dto) {
        jobAssignerService.createJobAssigner(dto,"All");
        return ResponseEntity.ok(new ApiResponse("Job assigned to employees",true));
    }

    @GetMapping
    public ResponseEntity<List<JobAssignerDTO>> getAllJobAssigners() {
        List<JobAssignerDTO> jobAssigners = jobAssignerService.getAllJobAssigners();
        return ResponseEntity.ok(jobAssigners);
    }

    @GetMapping("/careGiver-assigns/{id}")
    public ResponseEntity<List<JobAssignCareGiverViewerDTO>> getJobAssignCareTOCareGivers(@PathVariable String id){
        return ResponseEntity.ok(jobAssignerService.jobAssignTOCareGivers(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobAssignerDTO> getJobAssignerById(@PathVariable String id) {
        return jobAssignerService.getJobAssignerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/careGiver-assigns/all/{id}")
    public ResponseEntity<List<JobAssignCareGiverViewerDTO>> getAllJobsAssignCareTOCareGivers(@PathVariable String id){
        return ResponseEntity.ok(jobAssignerService.allJobsAssignedToCareGivers(id));
    }


    @GetMapping("/assignment/{appointmentID}")
    public ResponseEntity<List<JobAssignShowerDTO>> getJobAssignmentToCareGivers(@PathVariable(name = "appointmentID") String appointmentID){
        return ResponseEntity.ok(jobAssignerService.jobAssignerShower(appointmentID));
    }

    @PostMapping("/updateAssigners")
    public ResponseEntity<Boolean> updateJobAssingersByRecurrentAppointmentID(@RequestBody UpdateAssignersDTO updateAssignersDTO){
        return ResponseEntity.ok(jobAssignerService.updateAssigners(updateAssignersDTO));
    }

    @PutMapping("/acceptanceType/{id}")
    public ResponseEntity<ApiResponse> updateJobAcceptanceType(@PathVariable String id,@RequestParam(name = "status") String status){
        boolean result = jobAssignerService.updateJobAcceptanceState(status,id);
        if (result){
            return ResponseEntity.ok(new ApiResponse("Acceptance Type Updated Successfully",true));
        }else{
            return ResponseEntity.internalServerError().body(new ApiResponse("Fail to update acceptance Type",false));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobAssignerDTO> updateJobAssigner(@PathVariable String id, @RequestBody JobAssignerDTO dto) {
        return jobAssignerService.updateJobAssigner(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJobAssigner(@PathVariable String id) {
        if (jobAssignerService.deleteJobAssigner(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
