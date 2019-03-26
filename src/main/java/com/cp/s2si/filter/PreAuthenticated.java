package com.cp.s2si.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.annotation.Priority;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.filter.GenericFilterBean;

@Component
@Priority(value = 30)
public class PreAuthenticated extends GenericFilterBean {
	
	private Set<String> EXCLUDE_FILTERS = new ConcurrentSkipListSet<>(Arrays.asList(
			"/actuator/**"
			));
	
	private AntPathMatcher PATH_MATCHER = new AntPathMatcher();
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		String username = (String) RequestContextHolder.currentRequestAttributes().getAttribute("username", RequestAttributes.SCOPE_REQUEST);
		
		if (username != null) {
			chain.doFilter(request, response);
		} else {
			String uri = getRequestPath((HttpServletRequest) request);
			boolean matched = false;
			for(String excludeURI : EXCLUDE_FILTERS) {
				matched |= PATH_MATCHER.match(excludeURI, uri);
				if(matched) {
					break;
				}
			}
			if (matched) {
				chain.doFilter(request, response);
			} else {
				((HttpServletResponse) response).sendError( HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized" );
				return;
			}
		}
	}
	
	private String getRequestPath(HttpServletRequest request) {
		String url = request.getServletPath();

		if (request.getPathInfo() != null) {
			url += request.getPathInfo();
		}

		return url;
	}
	
	public void exposeEndpoints(List<String> endpoints) {
		EXCLUDE_FILTERS.addAll(endpoints);
	}
	
	public void blockEndpoints(List<String> endpoints) {
		EXCLUDE_FILTERS.removeAll(endpoints);
	}

}
