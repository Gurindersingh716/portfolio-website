package dev.gurindersingh.portfolio.contact;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;

@Service
public class ContactService {

    private static final Logger log = LoggerFactory.getLogger(ContactService.class);

    private final ContactRepository repository;
    private final Optional<JavaMailSender> mailSender;
    private final String notifyTo;
    private final String notifyFrom;

    public ContactService(ContactRepository repository,
                          Optional<JavaMailSender> mailSender,
                          @Value("${portfolio.notify-to:}") String notifyTo,
                          @Value("${portfolio.notify-from:}") String notifyFrom) {
        this.repository = repository;
        this.mailSender = mailSender;
        this.notifyTo = notifyTo;
        this.notifyFrom = notifyFrom;
    }

    @Transactional
    public void submit(ContactRequest request, String clientIp) {
        ContactMessage saved = repository.save(new ContactMessage(
                request.name().trim(),
                request.email().trim().toLowerCase(),
                request.subject() == null ? null : request.subject().trim(),
                request.message().trim(),
                hash(clientIp)
        ));
        notify(saved);
    }

    /**
     * Email failure must never fail the request. The message is already persisted,
     * so a dropped notification is recoverable; a 500 to the sender is not.
     */
    private void notify(ContactMessage msg) {
        if (mailSender.isEmpty() || notifyTo.isBlank()) {
            log.info("Contact message {} stored; email notification not configured", msg.getId());
            return;
        }
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(notifyTo);
            mail.setFrom(notifyFrom.isBlank() ? notifyTo : notifyFrom);
            mail.setReplyTo(msg.getEmail());
            mail.setSubject("Portfolio contact: " + (msg.getSubject() == null ? "(no subject)" : msg.getSubject()));
            mail.setText("""
                    From: %s <%s>

                    %s
                    """.formatted(msg.getName(), msg.getEmail(), msg.getMessage()));
            mailSender.get().send(mail);
        } catch (Exception e) {
            log.error("Failed to send notification for contact message {}", msg.getId(), e);
        }
    }

    private String hash(String ip) {
        if (ip == null || ip.isBlank()) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(ip.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
