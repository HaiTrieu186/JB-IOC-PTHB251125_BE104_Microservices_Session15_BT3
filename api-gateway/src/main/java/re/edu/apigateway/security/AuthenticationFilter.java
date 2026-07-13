package re.edu.apigateway.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import re.edu.apigateway.jwt.JwtUtils;
import java.util.List;

//@Component
//@RequiredArgsConstructor
//public class AuthenticationFilter implements GlobalFilter, Ordered {
//    private final JwtUtils jwtUtils;
//    private final AntPathMatcher pathMatcher = new AntPathMatcher();
//    private final List<String> openEndpoints = List.of("/api/auth/login");
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        ServerHttpRequest request = exchange.getRequest();
//
//        if (openEndpoints.stream().anyMatch(uri -> pathMatcher.match("**" + uri, request.getURI().getPath()))) {
//            return chain.filter(exchange);
//        }
//
//        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            return onError(exchange);
//        }
//
//        String token = authHeader.substring(7);
//        if (!jwtUtils.validateToken(token)) return onError(exchange);
//
//        Claims claims = jwtUtils.getClaimsFromToken(token);
//        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
//                .header("X-User-Username", claims.getSubject())
//                .header("X-User-Role", claims.get("role", String.class))
//                .build();
//
//        return chain.filter(exchange.mutate().request(mutatedRequest).build());
//    }
//
//    private Mono<Void> onError(ServerWebExchange exchange) {
//        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//        return exchange.getResponse().setComplete();
//    }
//
//    @Override public int getOrder() { return -1; }
//}

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import re.edu.apigateway.jwt.JwtUtils;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtils jwtUtils;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 1. Thả cửa cho các API public (Ví dụ: Đăng nhập thì không cần Token)
        if (pathMatcher.match("/api/auth/**", path)) {
            return chain.filter(exchange);
        }

        // 2. Chặn các request khác, kiểm tra xem có mang thẻ (Header Authorization) không
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(exchange, HttpStatus.UNAUTHORIZED); // 401
        }

        // 3. Có mang thẻ, giờ cắt chữ "Bearer " ra để lấy đúng chuỗi JWT kiểm tra
        String token = authHeader.substring(7);
        if (!jwtUtils.validateToken(token)) {
            return onError(exchange, HttpStatus.UNAUTHORIZED); // 401
        }

        // 4. THẺ HỢP LỆ: Trích xuất Role từ Payload
        Claims claims = jwtUtils.getClaimsFromToken(token);
        String role = claims.get("role", String.class);

        // 5. Gắn Role vào Header tên là "X-User-Role" trước khi forward xuống Course-Service
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header("X-User-Role", role)
                .build();

        // Cho đi qua cổng
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    // Hàm báo lỗi 401
    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }

    // Đặt thứ tự cho Filter chạy càng sớm càng tốt (-1)
    @Override
    public int getOrder() {
        return -1;
    }
}