package io.agileintelligence.ppmt.repository;

import io.agileintelligence.ppmt.domain.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {

    Project findByProjectIdentifier(String projectIdentifier);

}
