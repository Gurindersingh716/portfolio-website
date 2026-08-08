package dev.gurindersingh.portfolio.project;

import java.util.List;

public record ProjectResponse(
        Long id,
        String title,
        String role,
        String status,
        String problem,
        String approach,
        String engineering,
        List<String> stack,
        String sourceUrl
) {
    static ProjectResponse from(Project p) {
        return new ProjectResponse(
                p.getId(), p.getTitle(), p.getRole(), p.getStatus(),
                p.getProblem(), p.getApproach(), p.getEngineering(),
                p.getStack(), p.getSourceUrl()
        );
    }
}
