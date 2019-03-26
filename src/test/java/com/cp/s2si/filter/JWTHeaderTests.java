package com.cp.s2si.filter;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.cp.s2si.exception.JWTAudienceException;
import com.cp.s2si.exception.JWTBadEncryptionEception;
import com.cp.s2si.exception.JWTBadSignException;
import com.cp.s2si.exception.JWTExpiredException;
import com.cp.s2si.exception.JWTNoPayloadException;
import com.cp.s2si.exception.JWTNotATokenException;
import com.cp.s2si.filter.JWTHeader;
import com.cp.s2si.service.JWTService;

/**
 * 
 * @author panther
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class JWTHeaderTests {
	
	@Mock private JWTService jwtService;
	@Mock private RequestAttributes requestAttributes;
	@Mock private HttpServletRequest httpServletRequest;
	@Mock private HttpServletResponse httpServletResponse;
	@Mock private FilterChain filterChain;
	
	@Spy @InjectMocks
	private JWTHeader jwtHeader = new JWTHeader();
	
	@Before
	public void before() {
		RequestContextHolder.setRequestAttributes(requestAttributes);
	}
	
	@Test
	public void chainIfAlreadyAuthenticated() throws Exception {
		// Mocking
		given(requestAttributes.getAttribute("username", RequestAttributes.SCOPE_REQUEST)).willReturn("USERNAME");
		
		// Executing
		jwtHeader.doFilter(httpServletRequest, httpServletResponse, filterChain);
		
		//Verifying
		verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
	}
	
	@Test
	public void chainIfNoAuthorizationHeader() throws Exception {
		// Mocking
		given(requestAttributes.getAttribute("username", RequestAttributes.SCOPE_REQUEST)).willReturn(null);
		given(httpServletRequest.getHeader("Authorization")).willReturn(null);
		
		// Executing
		jwtHeader.doFilter(httpServletRequest, httpServletResponse, filterChain);
		
		//Verifying
		verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
	}
	
	@Test
	public void chainIfNoBearerAuthorizationHeader() throws Exception {
		// Mocking
		given(requestAttributes.getAttribute("username", RequestAttributes.SCOPE_REQUEST)).willReturn(null);
		given(httpServletRequest.getHeader("Authorization")).willReturn("Bear ");
		
		// Executing
		jwtHeader.doFilter(httpServletRequest, httpServletResponse, filterChain);
		
		//Verifying
		verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
	}
	
	@Test
	public void chainAndConsumeWithBearerAuthorizationHeader() throws Exception {
		// Mocking
		given(requestAttributes.getAttribute("username", RequestAttributes.SCOPE_REQUEST)).willReturn(null);
		given(httpServletRequest.getHeader("Authorization")).willReturn("Bearer ABC.MNO.XYZ");
		given(jwtService.consumeRequestJWT("ABC.MNO.XYZ")).willReturn("TEST-USER");
		
		// Executing
		jwtHeader.doFilter(httpServletRequest, httpServletResponse, filterChain);
		
		//Verifying
		verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
		verify(requestAttributes, times(1)).setAttribute("username", "TEST-USER", RequestAttributes.SCOPE_REQUEST);
	}
	
	@Test
	public void chainAndDonotAuthorizeOnException() throws Exception {
		// Mocking
		given(requestAttributes.getAttribute("username", RequestAttributes.SCOPE_REQUEST)).willReturn(null);
		given(httpServletRequest.getHeader("Authorization")).willReturn("Bearer ABC.MNO.XYZ");
		given(jwtService.consumeRequestJWT("ABC.MNO.XYZ")).willThrow(new JWTNotATokenException(), new JWTBadEncryptionEception(), new JWTNoPayloadException(), new JWTBadSignException(), new JWTAudienceException(), new JWTExpiredException());
		
		// Executing
		jwtHeader.doFilter(httpServletRequest, httpServletResponse, filterChain);
		
		//Verifying
		verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
	}
	
}
