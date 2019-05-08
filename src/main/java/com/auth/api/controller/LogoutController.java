package com.auth.api.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogoutController {

	@Autowired
	private TokenStore tokenStore;
    
   /* @Autowired
    private DefaultTokenServices tokenService;*/
	
	/* 30-Apr
	 * Not working right now
	 */
	@RequestMapping(value = "/oauth/logout", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public boolean logout(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null) {
			String tokenValue = authHeader.replace("Bearer", "").trim();
			OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
			if (accessToken == null) {
				//tokenService.revokeToken(tokenValue);
			}
			if (accessToken.getRefreshToken() != null) {
				tokenStore.removeRefreshToken(accessToken.getRefreshToken());
			}
		}
		return false;
	}

}
