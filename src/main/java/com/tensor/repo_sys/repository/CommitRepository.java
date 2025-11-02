package com.tensor.repo_sys.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tensor.repo_sys.model.Commit;

import java.util.List;

@Repository
public interface CommitRepository extends JpaRepository<Commit, Long> {
    List<Commit> findByRepositoryIdOrderByCreatedAtDesc(Long repositoryId);
    List<Commit> findByBranchIdOrderByCreatedAtDesc(Long branchId);
}