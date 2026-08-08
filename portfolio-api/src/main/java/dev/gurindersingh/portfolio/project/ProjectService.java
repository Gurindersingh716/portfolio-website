package dev.gurindersingh.portfolio.project;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository repository;

    public ProjectService(ProjectRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> findAll() {
        return repository.findAllByOrderByDisplayOrderAsc()
                .stream()
                .map(ProjectResponse::from)
                .toList();
    }
}
