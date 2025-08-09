package org.prabhu.bchat.controller;

import org.prabhu.bchat.config.WebhookProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhooks")
public class WebhookController {
    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);
    private final WebhookProperties properties;

    public WebhookController(WebhookProperties properties) {
        this.properties = properties;
    }

    @GetMapping
    public ResponseEntity<String> verifyWebhook(
            @RequestParam(name = "hub.mode", required = false) String mode,
            @RequestParam(name = "hub.verify_token", required = false) String verifyToken,
            @RequestParam(name = "hub.challenge", required = false) String challenge) {

        log.info("Webhook verification attempt: mode={}, challengePresent={}",
                mode, StringUtils.hasText(challenge));

        String expectedToken = properties.getToken();
        if (!"subscribe".equals(mode)) {
            log.warn("Invalid mode: {}", mode);
            return ResponseEntity.badRequest().body("invalid_mode");
        }

        if (!StringUtils.hasText(expectedToken)) {
            log.error("No token configured.");
            return ResponseEntity.status(500).body("server_misconfigured");
        }

        if (expectedToken.equals(verifyToken)) {
            log.info("Token matched successfully.");
            return ResponseEntity.ok(challenge == null ? "" : challenge);
        } else {
            log.warn("Token verification failed.");
            return ResponseEntity.badRequest().body("invalid_token");
        }
    }
}
