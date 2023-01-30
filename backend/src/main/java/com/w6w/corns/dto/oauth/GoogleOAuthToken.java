package com.w6w.corns.dto.oauth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GoogleOAuthToken {

    private String access_token;
    private int expires_in;
    private String scope;
    private String token_type;
    private String refresh_token;
}
