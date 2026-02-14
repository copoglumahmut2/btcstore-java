package com.btc_store.security.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class AuthToken {
    private String accessToken;
    private String refreshToken;
    private String username;
    private String firstName;
    private String lastName;
    private String language;
    private String picture;
    private Set<String> userGroups;
}

