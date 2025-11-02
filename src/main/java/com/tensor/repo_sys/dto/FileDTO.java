package com.tensor.repo_sys.dto;


import lombok.Data;
import java.time.LocalDateTime;



@Data
public class FileDTO {
    private Long id;
    private String name;
    private String fileType;
  
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String authorName;

}

