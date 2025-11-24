package pl.kwec.authservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS = 100;
    private static final long WINDOW_SIZE_MS = 60000;

    private final Map<String, RequestCounter> cache = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
            final FilterChain filterChain)
            throws ServletException, IOException {

        final String clientIp = getClientIp(request);
        RequestCounter counter = cache.computeIfAbsent(clientIp, k -> new RequestCounter());

        if (counter.isAllowed()) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Too many requests. Please try again later.\"}");
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0];
        }
        return request.getRemoteAddr();
    }

    private static class RequestCounter {
        private long firstRequestTime = 0;
        private int count = 0;

        synchronized boolean isAllowed() {
            long now = System.currentTimeMillis();

            if (firstRequestTime == 0 || now - firstRequestTime > WINDOW_SIZE_MS) {
                firstRequestTime = now;
                count = 1;
                return true;
            }

            if (count < MAX_REQUESTS) {
                count++;
                return true;
            }

            return false;
        }
    }
}
