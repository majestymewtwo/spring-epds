package com.example.springepds.config;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthContext {
    public static String getCurrentUser() {
        return SecurityContextHolder.getDeferredContext().get().getAuthentication().getName();
    }
}
