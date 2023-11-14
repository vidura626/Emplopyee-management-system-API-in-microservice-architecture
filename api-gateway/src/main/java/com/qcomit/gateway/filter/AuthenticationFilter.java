package com.qcomit.gateway.filter;

import com.qcomit.gateway.util.constant.SecurityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    @Autowired
    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (!exchange.getRequest().getHeaders().containsKey(SecurityConstant.JWT_HEADER_NAME)) {
                throw new RuntimeException("Missing Authentication Header");
            }
            return chain.filter(exchange);
        };
    }

    public static class Config {

    }
}
