package com.example.trace.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class IdtokenController {

    private final IdtokenService idtokenService;
    @GetMapping("/idtoken")
    public ResponseEntity<?> getIdToken(@RequestParam("code") String code) {
        log.info("Received id_token request with code: {}", code);
        return idtokenService.getTokenWithAuthorizationCode(code);
    }
}
