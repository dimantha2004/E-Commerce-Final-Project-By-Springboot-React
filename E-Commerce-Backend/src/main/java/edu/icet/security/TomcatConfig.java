package edu.icet.security;

import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class TomcatConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> cookieProcessorCustomizer() {
        return factory -> factory.addContextCustomizers(context -> {
            Rfc6265CookieProcessor cookieProcessor = new Rfc6265CookieProcessor();
            cookieProcessor.setSameSiteCookies(SameSiteCookies.LAX.getValue());
            context.setCookieProcessor(cookieProcessor);
        });
    }

    @Bean
    public CookieCleanupFilter cookieCleanupFilter() {
        return new CookieCleanupFilter();
    }

    public static class CookieCleanupFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {

            HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(request) {
                @Override
                public String getHeader(String name) {
                    String header = super.getHeader(name);
                    if ("cookie".equalsIgnoreCase(name) && header != null && header.contains("g_state=")) {

                        return header.replaceAll("g_state=[^;]*;?\\s*", "");
                    }
                    return header;
                }

                @Override
                public java.util.Enumeration<String> getHeaders(String name) {
                    java.util.Enumeration<String> headers = super.getHeaders(name);
                    if ("cookie".equalsIgnoreCase(name)) {
                        java.util.List<String> result = new java.util.ArrayList<>();
                        while (headers.hasMoreElements()) {
                            String header = headers.nextElement();
                            if (header != null && header.contains("g_state=")) {

                                header = header.replaceAll("g_state=[^;]*;?\\s*", "");
                                if (!header.trim().isEmpty()) {
                                    result.add(header);
                                }
                            } else {
                                result.add(header);
                            }
                        }
                        final java.util.List<String> finalResult = result;
                        return new java.util.Enumeration<String>() {
                            private int index = 0;

                            @Override
                            public boolean hasMoreElements() {
                                return index < finalResult.size();
                            }

                            @Override
                            public String nextElement() {
                                return finalResult.get(index++);
                            }
                        };
                    }
                    return headers;
                }
            };

            filterChain.doFilter(wrappedRequest, response);
        }
    }
    private static class HttpServletRequestWrapper extends jakarta.servlet.http.HttpServletRequestWrapper {
        public HttpServletRequestWrapper(HttpServletRequest request) {
            super(request);
        }
    }
}