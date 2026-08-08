package io.backbone.kit.security.api.domain.exception;

/**
 * Exception thrown when authentication is required but no authenticated user is found in the request context. This is
 * handled by {@link io.backbone.kit.security.impl.rest.exception.AuthenticationExceptionMapper} to return a 401 Unauthorized response.
 */
public final class AuthenticationException extends RuntimeException
{
    public AuthenticationException(final String message)
    {
        super(message);
    }
}
