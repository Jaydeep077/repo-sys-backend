package com.tensor.repo_sys.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepositoryDTO {
    private Long id;
    
    @NotBlank
    @Size(min = 3, max = 100)
    private String name;
    
    @Size(max = 500)
    private String description;
    
    private Boolean isPrivate = false;
    
    private String ownerUsername;
    private Long ownerId;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
