package com.tensor.repo_sys.service;



import com.tensor.repo_sys.dto.RepositoryDTO;
import com.tensor.repo_sys.exception.ResourceNotFoundException;
import com.tensor.repo_sys.model.Branch;
import com.tensor.repo_sys.model.RepositoryEntity;
import com.tensor.repo_sys.model.User;
import com.tensor.repo_sys.repository.BranchRepository;
import com.tensor.repo_sys.repository.RepositoryEntityRepository;
import com.tensor.repo_sys.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class RepositoryService {
    
    @Autowired
    private RepositoryEntityRepository repositoryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BranchRepository branchRepository;
    
    public List<RepositoryDTO> getAllRepositories() {
        String currentUsername = getCurrentUsername();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUsername));
        
        // Get public repositories and user's own repositories
        List<RepositoryEntity> repositories = repositoryRepository.findAll().stream()
                .filter(repo -> !repo.getIsPrivate() || repo.getOwner().getId().equals(currentUser.getId()))
                .collect(Collectors.toList());
        
        return repositories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public RepositoryDTO getRepositoryById(Long id) {
        RepositoryEntity repository = repositoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repository", "id", id));
        
        return convertToDTO(repository);
    }
    
    @Transactional
    public RepositoryDTO createRepository(RepositoryDTO repositoryDTO) {
        String currentUsername = getCurrentUsername();
        User owner = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUsername));
        
        // Check if repository with same name already exists for this user
        if (repositoryRepository.findByNameAndOwnerId(repositoryDTO.getName(), owner.getId()).isPresent()) {
            throw new RuntimeException("Repository with name '" + repositoryDTO.getName() + "' already exists");
        }
        
        RepositoryEntity repository = new RepositoryEntity();
        repository.setName(repositoryDTO.getName());
        repository.setDescription(repositoryDTO.getDescription());
        repository.setIsPrivate(repositoryDTO.getIsPrivate());
        repository.setOwner(owner);
        
        RepositoryEntity savedRepository = repositoryRepository.save(repository);
        
        // Create default "main" branch
        Branch mainBranch = new Branch();
        mainBranch.setName("main");
        mainBranch.setIsDefault(true);
        mainBranch.setRepository(savedRepository);
        branchRepository.save(mainBranch);
        
        return convertToDTO(savedRepository);
    }
    
    public RepositoryDTO updateRepository(Long id, RepositoryDTO repositoryDTO) {
        RepositoryEntity repository = repositoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repository", "id", id));
        
        // Check ownership
        String currentUsername = getCurrentUsername();
        if (!repository.getOwner().getUsername().equals(currentUsername)) {
            throw new RuntimeException("You don't have permission to update this repository");
        }
        
        repository.setName(repositoryDTO.getName());
        repository.setDescription(repositoryDTO.getDescription());
        repository.setIsPrivate(repositoryDTO.getIsPrivate());
        
        RepositoryEntity updatedRepository = repositoryRepository.save(repository);
        return convertToDTO(updatedRepository);
    }
    
    public void deleteRepository(Long id) {
        RepositoryEntity repository = repositoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repository", "id", id));
        
        // Check ownership
        String currentUsername = getCurrentUsername();
        if (!repository.getOwner().getUsername().equals(currentUsername)) {
            throw new RuntimeException("You don't have permission to delete this repository");
        }
        
        repositoryRepository.delete(repository);
    }
    
    private RepositoryDTO convertToDTO(RepositoryEntity repository) {
        RepositoryDTO dto = new RepositoryDTO();
        dto.setId(repository.getId());
        dto.setName(repository.getName());
        dto.setDescription(repository.getDescription());
        dto.setIsPrivate(repository.getIsPrivate());
        dto.setOwnerUsername(repository.getOwner().getUsername());
        dto.setOwnerId(repository.getOwner().getId());
        dto.setCreatedAt(repository.getCreatedAt());
        dto.setUpdatedAt(repository.getUpdatedAt());
        return dto;
    }
    
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
