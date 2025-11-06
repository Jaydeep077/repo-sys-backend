package com.tensor.repo_sys.controller;

import com.tensor.repo_sys.dto.FileDTO;
import com.tensor.repo_sys.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/repositories/{repoId}/files")
@CrossOrigin(origins = "http://localhost:3000")
public class FileController {

    @Autowired
    private FileService fileService;

    @GetMapping
    public ResponseEntity<List<FileDTO>> listFiles(@PathVariable Long repoId) {
        return ResponseEntity.ok(fileService.listFiles(repoId));
    }
  @PostMapping
    public ResponseEntity<FileDTO> uploadFile(@PathVariable Long repoId,@RequestParam("file") MultipartFile multipartFile) {
        return ResponseEntity.ok(fileService.uploadFile(repoId, multipartFile));
    }

    

    @GetMapping("/{fileId}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long repoId, @PathVariable Long fileId) {
        return fileService.downloadFile(repoId, fileId);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<String> deleteFile(@PathVariable Long repoId, @PathVariable Long fileId) {
        try {
            fileService.deleteFile(repoId, fileId);
            return ResponseEntity.ok("File deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error deleting file: " + e.getMessage());
        }
    }

    }
