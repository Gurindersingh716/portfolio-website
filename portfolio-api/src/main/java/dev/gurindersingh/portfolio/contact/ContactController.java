package dev.gurindersingh.portfolio.contact;

import dev.gurindersingh.portfolio.common.ApiMessage;
import dev.gurindersingh.portfolio.common.RateLimiter;
import dev.gurindersingh.portfolio.common.TooManyRequestsException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact")
@Tag(name = "Contact", description = "Public contact form")
public class ContactController {

    private final ContactService service;
    private final RateLimiter rateLimiter;

    public ContactController(ContactService service, RateLimiter rateLimiter) {
        this.service = service;
        this.rateLimiter = rateLimiter;
    }

    @PostMapping
    @Operation(summary = "Send a message. Limited to 3 submissions per IP per hour.")
    public ResponseEntity<ApiMessage> submit(@Valid @RequestBody ContactRequest request,
                                             HttpServletRequest servletRequest) {
        String ip = clientIp(servletRequest);

        if (!rateLimiter.tryConsume(ip)) {
            throw new TooManyRequestsException("Too many messages sent. Please try again later.");
        }

        // Silently accept bot submissions so the bot sees success and does not retry.
        if (request.isLikelyBot()) {
            return ResponseEntity.accepted().body(new ApiMessage("Message received."));
        }

        service.submit(request, ip);
        return ResponseEntity.accepted().body(new ApiMessage("Message received. I'll get back to you soon."));
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("CF-Connecting-IP");
        if (forwarded == null || forwarded.isBlank()) {
            forwarded = request.getHeader("X-Forwarded-For");
        }
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
