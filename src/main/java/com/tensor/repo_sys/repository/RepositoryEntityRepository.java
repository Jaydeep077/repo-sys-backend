package com.tensor.repo_sys.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tensor.repo_sys.model.RepositoryEntity;

import java.util.List;
import java.util.Optional;




@Repository
public interface RepositoryEntityRepository extends JpaRepository<RepositoryEntity, Long> {
    List<RepositoryEntity> findByOwnerId(Long ownerId);
    List<RepositoryEntity> findByIsPrivateFalse();
    Optional<RepositoryEntity> findByNameAndOwnerId(String name, Long ownerId);
}
