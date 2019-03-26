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

import com.cp.s2si.filter.PreAuthenticated;

/**
 * 
 * @author panther
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PreAuthenticatedTests {
	
	@Mock private RequestAttributes requestAttributes;
	@Mock private HttpServletRequest httpServletRequest;
	@Mock private HttpServletResponse httpServletResponse;
	@Mock private FilterChain filterChain;
	
	@Spy @InjectMocks
	private PreAuthenticated preAuthenticated = new PreAuthenticated();
	
	@Before
	public void before() {
		RequestContextHolder.setRequestAttributes(requestAttributes);
	}

	@Test
	public void chainIfAlreadyAuthenticated() throws Exception {
		// Mocking
		given(requestAttributes.getAttribute("username", RequestAttributes.SCOPE_REQUEST)).willReturn("USERNAME");
		
		// Executing
		preAuthenticated.doFilter(httpServletRequest, httpServletResponse, filterChain);
		
		//Verifying
		verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
	}
	
	@Test
	public void respondErrorIfNotAuthenticatedAndOtherPath() throws Exception {
		// Mocking
		given(httpServletRequest.getServletPath()).willReturn("/other");
		given(requestAttributes.getAttribute("username", RequestAttributes.SCOPE_REQUEST)).willReturn(null);
		
		// Executing
		preAuthenticated.doFilter(httpServletRequest, httpServletResponse, filterChain);
		
		//Verifying
		verify(httpServletResponse, times(1)).sendError( HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized" );
	}
	
}
