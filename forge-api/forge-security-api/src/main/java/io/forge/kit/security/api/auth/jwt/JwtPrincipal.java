package io.forge.kit.security.api.auth.jwt;

public sealed interface JwtPrincipal permits JwtPrincipal.Service,JwtPrincipal.User,JwtPrincipal.Anonymous
{
    String rateLimitKey();

    record Service(String serviceId) implements JwtPrincipal
    {
        @Override
        public String rateLimitKey()
        {
            return "service:" + serviceId;
        }
    }

    record User(String username) implements JwtPrincipal
    {
        @Override
        public String rateLimitKey()
        {
            return "user:" + username;
        }
    }

    record Anonymous() implements JwtPrincipal
    {
        @Override
        public String rateLimitKey()
        {
            return "auth:unidentified";
        }
    }
}
