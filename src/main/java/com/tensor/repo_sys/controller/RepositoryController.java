package com.tensor.repo_sys.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tensor.repo_sys.dto.MessageResponse;
import com.tensor.repo_sys.dto.RepositoryDTO;
import com.tensor.repo_sys.service.RepositoryService;

import java.util.List;



@RestController
@RequestMapping("/api/repositories")
@CrossOrigin(origins = "http://localhost:3000")
public class RepositoryController {
    
    @Autowired
    private RepositoryService repositoryService;
    
    @GetMapping
    public ResponseEntity<List<RepositoryDTO>> getAllRepositories() {
        return ResponseEntity.ok(repositoryService.getAllRepositories());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RepositoryDTO> getRepositoryById(@PathVariable Long id) {
        return ResponseEntity.ok(repositoryService.getRepositoryById(id));
    }
    
    @PostMapping
    public ResponseEntity<RepositoryDTO> createRepository(@Valid @RequestBody RepositoryDTO repositoryDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repositoryService.createRepository(repositoryDTO));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<RepositoryDTO> updateRepository(
            @PathVariable Long id,
            @Valid @RequestBody RepositoryDTO repositoryDTO) {
        return ResponseEntity.ok(repositoryService.updateRepository(id, repositoryDTO));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteRepository(@PathVariable Long id) {
        repositoryService.deleteRepository(id);
        return ResponseEntity.ok(new MessageResponse("Repository deleted successfully"));
    }
}
