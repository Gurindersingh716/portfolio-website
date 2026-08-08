package dev.gurindersingh.portfolio.project;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(length = 80)
    private String role;

    @Column(nullable = false, length = 40)
    private String status;

    @Column(columnDefinition = "text")
    private String problem;

    @Column(columnDefinition = "text")
    private String approach;

    @Column(columnDefinition = "text")
    private String engineering;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "project_stack", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "tech", length = 60)
    private List<String> stack = new ArrayList<>();

    @Column(name = "source_url", length = 300)
    private String sourceUrl;

    @Column(name = "display_order", nullable = false)
    private int displayOrder = 0;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    protected Project() {
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
    public String getProblem() { return problem; }
    public String getApproach() { return approach; }
    public String getEngineering() { return engineering; }
    public List<String> getStack() { return stack; }
    public String getSourceUrl() { return sourceUrl; }
    public int getDisplayOrder() { return displayOrder; }
    public Instant getCreatedAt() { return createdAt; }
}
