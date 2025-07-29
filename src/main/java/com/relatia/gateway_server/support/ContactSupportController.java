package com.relatia.gateway_server.support;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequestMapping("/contact-support")
@RestController
public class ContactSupportController {
    @GetMapping
    Mono<String> contactSupport() {
        return Mono.just("An error has occurred. Please contact support.");
    }
}
