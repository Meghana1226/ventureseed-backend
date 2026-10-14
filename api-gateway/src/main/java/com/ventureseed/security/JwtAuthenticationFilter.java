package com.ventureseed.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final String SECRET =
            "VentureSeedDemoSecretKeyMustBeAtLeast32Chars!";

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        String path = exchange.getRequest()
                .getURI()
                .getPath();

        HttpMethod method = exchange.getRequest()
                .getMethod();

        // =========================================
        // PUBLIC APIs
        // =========================================

        // Login
        if (path.equals("/auth/login")
                && method == HttpMethod.POST) {

            return chain.filter(exchange);
        }

        // Registration
        if (path.equals("/auth/register")
                && method == HttpMethod.POST) {

            return chain.filter(exchange);
        }

        // Email verification
        if (path.equals("/auth/verify-email")
                && method == HttpMethod.POST) {

            return chain.filter(exchange);
        }

        // Forgot password
        if (path.equals("/auth/forgot-password")
                && method == HttpMethod.POST) {

            return chain.filter(exchange);
        }

        // Reset password
        if (path.equals("/auth/reset-password")
                && method == HttpMethod.POST) {

            return chain.filter(exchange);
        }

        // View all campaigns
        if (path.equals("/campaigns")
                && method == HttpMethod.GET) {

            return chain.filter(exchange);
        }

        // View one campaign
        if (path.matches("/campaigns/\\d+")
                && method == HttpMethod.GET) {

            return chain.filter(exchange);
        }

        // =========================================
        // ALL OTHER APIs REQUIRE JWT
        // =========================================

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null) {
            return unauthorized(exchange);
        }

        if (!authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        String token = authHeader
                .substring(7)
                .trim();

        if (token.isEmpty()) {
            return unauthorized(exchange);
        }

        // =========================================
        // VALIDATE JWT
        // =========================================

        try {

            Claims claims = Jwts.parser()
                    .verifyWith(
                            Keys.hmacShaKeyFor(
                                    SECRET.getBytes(
                                            StandardCharsets.UTF_8
                                    )
                            )
                    )
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // =========================================
            // PASS USER INFORMATION DOWNSTREAM
            // =========================================

            ServerWebExchange modifiedExchange =
                    exchange.mutate()
                            .request(request ->
                                    request.headers(headers -> {

                                        headers.remove(
                                                "X-User-Email"
                                        );

                                        headers.remove(
                                                "X-User-Role"
                                        );

                                        headers.add(
                                                "X-User-Email",
                                                claims.getSubject()
                                        );

                                        Object role =
                                                claims.get("role");

                                        if (role != null) {

                                            headers.add(
                                                    "X-User-Role",
                                                    role.toString()
                                            );
                                        }
                                    })
                            )
                            .build();

            return chain.filter(
                    modifiedExchange
            );

        } catch (Exception e) {

            System.out.println(
                    "JWT validation failed: "
                            + e.getMessage()
            );

            return unauthorized(exchange);
        }
    }


    // =========================================
    // UNAUTHORIZED RESPONSE
    // =========================================

    private Mono<Void> unauthorized(
            ServerWebExchange exchange) {

        exchange.getResponse()
                .setStatusCode(
                        HttpStatus.UNAUTHORIZED
                );

        return exchange.getResponse()
                .setComplete();
    }


    // =========================================
    // FILTER ORDER
    // =========================================

    @Override
    public int getOrder() {
        return -100;
    }
}