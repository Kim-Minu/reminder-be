package com.example.demo.member.service

import com.example.demo.common.security.jwt.JwtTokenProvider
import com.example.demo.member.domain.Member
import com.example.demo.member.domain.RefreshToken
import com.example.demo.member.dto.LoginRequest
import com.example.demo.member.dto.MemberResponse
import com.example.demo.member.dto.RefreshRequest
import com.example.demo.member.dto.RegisterRequest
import com.example.demo.member.dto.TokenResponse
import com.example.demo.member.repository.MemberRepository
import com.example.demo.member.repository.RefreshTokenRepository
import com.example.demo.member.service.ports.inp.MemberService
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
@Transactional(readOnly = true)
class DefaultMemberService(
    private val memberRepository: MemberRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    @Value("\${jwt.refresh-token-expiration}") private val refreshTokenExpiration: Long,
) : MemberService {

    @Transactional
    override fun register(request: RegisterRequest): MemberResponse {

        val email: String = request.email;

        if (memberRepository.existsByEmail(email)) {
            throw IllegalArgumentException("이미 사용 중인 이메일입니다: $email")
        }

        val member = memberRepository.save(request.toEntity(passwordEncoder))

        return MemberResponse.from(member)
    }

    @Transactional
    override fun login(request: LoginRequest): TokenResponse {

        val email: String = request.email
        val password: String = request.password

        val member = memberRepository.findByEmail(email)
            ?: throw NoSuchElementException("존재하지 않는 회원입니다: $email")

        if (!passwordEncoder.matches(password, member.password)) {
            throw IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다")
        }

        return issueTokens(member)
    }

    @Transactional
    override fun refresh(request: RefreshRequest): TokenResponse {

        val refreshToken = request.refreshToken

        val stored = refreshTokenRepository.findByToken(refreshToken)
            ?: throw NoSuchElementException("유효하지 않은 리프레시 토큰입니다")

        if (stored.isExpired()) {
            refreshTokenRepository.delete(stored)
            throw IllegalArgumentException("만료된 리프레시 토큰입니다")
        }
        val member = memberRepository.findByIdOrNull(stored.memberId)
            ?: throw NoSuchElementException("존재하지 않는 회원입니다")
        refreshTokenRepository.delete(stored)
        return issueTokens(member)
    }

    @Transactional
    override fun logout(request: RefreshRequest) {

        val refreshToken = request.refreshToken

        refreshTokenRepository.findByToken(refreshToken)
            ?.let { refreshTokenRepository.delete(it) }
    }

    private fun issueTokens(member: Member): TokenResponse {

        val accessToken = jwtTokenProvider.generateAccessToken(member.id, member.email)
        val rawRefreshToken = UUID.randomUUID().toString()
        val expiresAt = LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000)

        refreshTokenRepository.save(
            RefreshToken(token = rawRefreshToken, memberId = member.id, expiresAt = expiresAt)
        )

        return TokenResponse(accessToken = accessToken, refreshToken = rawRefreshToken)
    }
}
