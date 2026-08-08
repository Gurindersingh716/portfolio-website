package dev.gurindersingh.portfolio.contact;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Inbound payload for the public contact form.
 * The honeypot field is never shown to real users; anything that fills it is a bot.
 */
public record ContactRequest(

        @NotBlank(message = "Name is required")
        @Size(max = 120, message = "Name must be 120 characters or fewer")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Enter a valid email address")
        @Size(max = 254, message = "Email must be 254 characters or fewer")
        String email,

        @Size(max = 160, message = "Subject must be 160 characters or fewer")
        String subject,

        @NotBlank(message = "Message is required")
        @Size(min = 10, max = 5000, message = "Message must be between 10 and 5000 characters")
        String message,

        String website
) {
    public boolean isLikelyBot() {
        return website != null && !website.isBlank();
    }
}
