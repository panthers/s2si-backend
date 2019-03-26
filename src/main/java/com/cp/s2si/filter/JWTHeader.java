package com.cp.s2si.filter;

import java.io.IOException;

import javax.annotation.Priority;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import com.cp.s2si.exception.JWTAudienceException;
import com.cp.s2si.exception.JWTBadEncryptionEception;
import com.cp.s2si.exception.JWTBadSignException;
import com.cp.s2si.exception.JWTExpiredException;
import com.cp.s2si.exception.JWTNoPayloadException;
import com.cp.s2si.exception.JWTNotATokenException;
import com.cp.s2si.service.JWTService;

@Component
@Priority(value = 10)
public class JWTHeader extends GenericFilterBean  {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JWTHeader.class);
	
	@Autowired
	private JWTService jwtService;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		String username = (String) RequestContextHolder.currentRequestAttributes().getAttribute("username", RequestAttributes.SCOPE_REQUEST);
		
		if (username == null) {
			String token = ((HttpServletRequest) request).getHeader("Authorization");
			if (token != null && token.startsWith("Bearer ")) {
				try {
					String user = jwtService.consumeRequestJWT(token.replace("Bearer ", ""));
					RequestContextHolder.currentRequestAttributes().setAttribute("username", user, RequestAttributes.SCOPE_REQUEST);
				} catch (JWTNotATokenException | JWTBadEncryptionEception | JWTNoPayloadException | JWTBadSignException
						| JWTExpiredException | JWTAudienceException e) {
					LOGGER.error("JWT consumption failed", e);
				}
				
			}
		}
		chain.doFilter(request, response);
	}
	
}
