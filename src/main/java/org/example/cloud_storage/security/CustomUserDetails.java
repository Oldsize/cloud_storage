package org.example.cloud_storage.security;

import lombok.Getter;
import org.example.cloud_storage.models.User;

import java.util.Collections;


@Getter
public class CustomUserDetails extends org.springframework.security.core.userdetails.User {
    private final Long id;

    public CustomUserDetails(User user) {
        super(user.getUsername(), user.getPassword(), Collections.emptyList());
        this.id = user.getId();
    }
}
