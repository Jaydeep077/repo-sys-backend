package com.tensor.repo_sys.service;

import com.tensor.repo_sys.dto.FileDTO;
import com.tensor.repo_sys.exception.ResourceNotFoundException;
import com.tensor.repo_sys.model.File;
import com.tensor.repo_sys.model.RepositoryEntity;
import com.tensor.repo_sys.model.User;
import com.tensor.repo_sys.repository.FileRepository;
import com.tensor.repo_sys.repository.RepositoryEntityRepository;
import com.tensor.repo_sys.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private RepositoryEntityRepository repositoryRepository;

    @Autowired
    private UserRepository userRepository;

    public List<FileDTO> listFiles(Long repoId) {
        // Validate repository exists
        repositoryRepository.findById(repoId)
                .orElseThrow(() -> new ResourceNotFoundException("Repository", "id", repoId));

        // Get files for repository id
        List<File> files = fileRepository.findByRepositoryId(repoId);

        // Map to DTOs, safely checking for null author
        return files.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public FileDTO uploadFile(Long repoId, MultipartFile multipartFile) {
        RepositoryEntity repository = repositoryRepository.findById(repoId)
                .orElseThrow(() -> new ResourceNotFoundException("Repository", "id", repoId));

        String currentUsername = getCurrentUsername();
        if (currentUsername == null) {
            throw new ResourceNotFoundException("Authenticated user not found");
        }

        User author = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUsername));

        File file = new File();
        file.setName(multipartFile.getOriginalFilename());
        file.setFileType(multipartFile.getContentType());
        file.setRepository(repository);
        file.setAuthor(author);

        try {
            file.setFileData(multipartFile.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file bytes", e);
        }

        File saved = fileRepository.save(file);
        return toDTO(saved);
    }

    private FileDTO toDTO(File file) {
        FileDTO dto = new FileDTO();
        dto.setId(file.getId());
        dto.setName(file.getName());
        dto.setFileType(file.getFileType());
        dto.setCreatedAt(file.getCreatedAt());
        dto.setUpdatedAt(file.getUpdatedAt());

        // Null-safe authorName
        if (file.getAuthor() != null && file.getAuthor().getUsername() != null) {
            dto.setAuthorName(file.getAuthor().getUsername());
        } else {
            dto.setAuthorName("Unknown");
        }

        return dto;
    }

    // This method now returns the DB BLOB
    public ResponseEntity<byte[]> downloadFile(Long repoId, Long fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File", "id", fileId));

        if (file.getRepository() == null || !file.getRepository().getId().equals(repoId)) {
            throw new ResourceNotFoundException("File does not belong to repository!");
        }

        String fileName = file.getName() != null ? file.getName() : "file";
        String contentType = file.getFileType() != null ? file.getFileType() : "application/octet-stream";

        byte[] fileData = file.getFileData();
        if (fileData == null) {
            throw new ResourceNotFoundException("File data missing");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(fileData);
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null) ? authentication.getName() : null;
    }
}
