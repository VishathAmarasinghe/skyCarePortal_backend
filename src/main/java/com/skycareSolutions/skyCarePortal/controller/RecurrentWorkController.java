package com.skycareSolutions.skyCarePortal.controller;

import com.skycareSolutions.skyCarePortal.dto.RecurrentWorkDTO;
import com.skycareSolutions.skyCarePortal.service.RecurrentWorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/recurrent-Work")
public class RecurrentWorkController {

    @Autowired
    private RecurrentWorkService recurrentWorkService;

    @PostMapping
    public ResponseEntity<RecurrentWorkDTO> createRecurrentWork(@RequestBody RecurrentWorkDTO dto) {
        RecurrentWorkDTO created = recurrentWorkService.createRecurrentWork(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<RecurrentWorkDTO>> getAllRecurrentWorks() {
        List<RecurrentWorkDTO> works = recurrentWorkService.getAllRecurrentWorks();
        return ResponseEntity.ok(works);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecurrentWorkDTO> getRecurrentWorkById(@PathVariable String id) {
        return recurrentWorkService.getRecurrentWorkById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecurrentWorkDTO> updateRecurrentWork(
            @PathVariable String id, @RequestBody RecurrentWorkDTO dto) {
        return recurrentWorkService.updateRecurrentWork(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecurrentWork(@PathVariable String id) {
        boolean deleted = recurrentWorkService.deleteRecurrentWork(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
