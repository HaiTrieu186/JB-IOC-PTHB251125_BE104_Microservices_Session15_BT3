package re.edu.courseservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Bắt lấy Header do API Gateway truyền xuống
        String role = request.getHeader("X-User-Role");
        String username = request.getHeader("X-User-Username"); // Có thể Gateway truyền thêm cái này

        if (role != null && !role.isEmpty()) {
            // Chuyển Role String thành Quyền (Authority) trong Spring Security
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.toUpperCase());

            // Tạo đối tượng xác thực và nạp vào SecurityContext
            String principal = (username != null) ? username : "Unknown-User";
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    principal, null, Collections.singletonList(authority)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}