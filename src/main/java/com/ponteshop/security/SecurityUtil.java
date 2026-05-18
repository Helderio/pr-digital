package com.ponteshop.security;

import com.ponteshop.exception.UnauthorizedException;
import com.ponteshop.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtil {
    private final UserRepository userRepository;

    public UUID requireCurrentUserId() {
        String email = requireCurrentEmail();
        return userRepository.findByEmail(email)
            .map(u -> u.getId())
            .orElseThrow(() -> new UnauthorizedException("Utilizador inválido"));
    }

    public String requireCurrentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) throw new UnauthorizedException("Não autenticado");
        return auth.getName();
    }
}

