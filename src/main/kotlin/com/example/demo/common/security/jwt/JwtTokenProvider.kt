package com.example.demo.common.security.jwt

import com.example.demo.common.security.principal.CustomPrincipal
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.access-token-expiration}") private val accessTokenExpiration: Long,
) {
    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))
    }

    fun generateAccessToken(memberId: Long, email: String): String =
        Jwts.builder()
            .subject(memberId.toString())
            .claim("email", email)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + accessTokenExpiration))
            .signWith(key)
            .compact()

    fun parseAuthentication(token: String): Authentication {
        val claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload

        val principal = CustomPrincipal(
            memberId = claims.subject.toLong(),
            email = claims["email"] as String
        )

        return UsernamePasswordAuthenticationToken(
            principal,
            null,
            emptyList()
        )
    }
    fun validateToken(token: String): Boolean = try {
        Jwts.parser().verifyWith(key).build().parseSignedClaims(token)
        true
    } catch (e: Exception) {
        false
    }
}