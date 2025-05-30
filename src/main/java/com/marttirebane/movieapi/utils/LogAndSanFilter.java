package com.marttirebane.movieapi.utils;

//import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

@Configuration
public class LogAndSanFilter implements Filter {

    /*
    @Bean 
    public Filter logAndSanFilter() {
        return new LogAndSanFilter();
    }*/

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String originalUri = httpRequest.getRequestURI();

            // Sanitize the URI by removing "%0A"
            String sanitizedUri = originalUri.replace("%0A", "");

            // Log the sanitized URI
            System.out.println("Original URI: " + originalUri);
            System.out.println("Sanitized URI: " + sanitizedUri);

            // Wrap the request to override getRequestURI
            HttpServletRequestWrapper sanitizedRequest = new HttpServletRequestWrapper(httpRequest) {
                @Override
                public String getRequestURI() {
                    return sanitizedUri;
                }
            };

            // Continue the filter chain with the sanitized request
            chain.doFilter(sanitizedRequest, response);
        } else {
            // If not an HttpServletRequest, pass it through as is
            chain.doFilter(request, response);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Optional initialization logic
    }

    @Override
    public void destroy() {
        // Optional cleanup logic
    }
}
