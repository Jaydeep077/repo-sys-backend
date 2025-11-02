package com.tensor.repo_sys.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tensor.repo_sys.model.File;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByRepositoryId(Long repositoryId);
    List<File> findByRepositoryIdAndBranchId(Long repositoryId, Long branchId);
}
