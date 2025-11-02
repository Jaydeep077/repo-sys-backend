package com.tensor.repo_sys.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tensor.repo_sys.model.Branch;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    List<Branch> findByRepositoryId(Long repositoryId);
    Optional<Branch> findByRepositoryIdAndName(Long repositoryId, String name);
    Optional<Branch> findByRepositoryIdAndIsDefaultTrue(Long repositoryId);
}
