package dev.gurindersingh.portfolio.contact;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "contact_messages")
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 254)
    private String email;

    @Column(length = 160)
    private String subject;

    @Column(nullable = false, columnDefinition = "text")
    private String message;

    @Column(name = "source_ip_hash", length = 64)
    private String sourceIpHash;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt = Instant.now();

    protected ContactMessage() {
    }

    public ContactMessage(String name, String email, String subject, String message, String sourceIpHash) {
        this.name = name;
        this.email = email;
        this.subject = subject;
        this.message = message;
        this.sourceIpHash = sourceIpHash;
        this.receivedAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getSubject() { return subject; }
    public String getMessage() { return message; }
    public Instant getReceivedAt() { return receivedAt; }
}
