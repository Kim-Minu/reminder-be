package com.example.demo.member.service

import com.example.demo.member.dto.LoginRequest
import com.example.demo.member.dto.RefreshRequest
import com.example.demo.member.dto.RegisterRequest
import com.example.demo.member.repository.MemberRepository
import com.example.demo.member.repository.RefreshTokenRepository
import com.example.demo.member.service.ports.inp.MemberService
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired lateinit var service: MemberService
    @Autowired lateinit var memberRepository: MemberRepository
    @Autowired lateinit var refreshTokenRepository: RefreshTokenRepository
    @Autowired lateinit var passwordEncoder: PasswordEncoder

    private fun register(email: String = "user@example.com", password: String = "password1234", name: String = "홍길동") =
        service.register(RegisterRequest(email = email, password = password, name = name))

    private fun login(email: String, password: String = "password1234") =
        service.login(LoginRequest(email = email, password = password))

    @Nested
    inner class Register {

        @Test
        fun `이메일, 비밀번호, 이름으로 회원을 생성한다`() {
            val member = register()

            assertThat(member.id).isPositive()
            assertThat(member.email).isEqualTo("user@example.com")
            assertThat(member.name).isEqualTo("홍길동")
        }

        @Test
        fun `중복 이메일로 가입하면 IllegalArgumentException을 던진다`() {
            register(email = "dup@example.com")

            assertThatThrownBy { register(email = "dup@example.com", name = "두 번째") }
                .isInstanceOf(IllegalArgumentException::class.java)
        }
    }

    @Nested
    inner class Login {

        @Test
        fun `올바른 이메일과 비밀번호로 로그인하면 accessToken과 refreshToken을 반환한다`() {
            register(email = "login@example.com")

            val result = login("login@example.com")

            assertThat(result.accessToken).isNotBlank()
            assertThat(result.refreshToken).isNotBlank()
        }

        @Test
        fun `존재하지 않는 이메일로 로그인하면 NoSuchElementException을 던진다`() {
            assertThatThrownBy { login("notfound@example.com") }
                .isInstanceOf(NoSuchElementException::class.java)
        }

        @Test
        fun `잘못된 비밀번호로 로그인하면 IllegalArgumentException을 던진다`() {
            register(email = "wrong@example.com")

            assertThatThrownBy { service.login(LoginRequest(email = "wrong@example.com", password = "wrongpassword")) }
                .isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `로그인 시 refreshToken이 DB에 저장된다`() {
            register(email = "save@example.com")
            val result = login("save@example.com")

            val stored = refreshTokenRepository.findByToken(result.refreshToken)
            assertThat(stored).isNotNull()
        }
    }

    @Nested
    inner class Refresh {

        @Test
        fun `유효한 refreshToken으로 새 토큰을 발급한다`() {
            register(email = "refresh@example.com")
            val loginResult = login("refresh@example.com")

            val result = service.refresh(RefreshRequest(loginResult.refreshToken))

            assertThat(result.accessToken).isNotBlank()
            assertThat(result.refreshToken).isNotBlank()
            assertThat(result.refreshToken).isNotEqualTo(loginResult.refreshToken)
        }

        @Test
        fun `사용된 refreshToken은 DB에서 삭제된다`() {
            register(email = "del@example.com")
            val loginResult = login("del@example.com")
            service.refresh(RefreshRequest(loginResult.refreshToken))

            val stored = refreshTokenRepository.findByToken(loginResult.refreshToken)
            assertThat(stored).isNull()
        }

        @Test
        fun `존재하지 않는 refreshToken으로 재발급하면 NoSuchElementException을 던진다`() {
            assertThatThrownBy { service.refresh(RefreshRequest("invalid-token")) }
                .isInstanceOf(NoSuchElementException::class.java)
        }
    }

    @Nested
    inner class Logout {

        @Test
        fun `로그아웃하면 refreshToken이 DB에서 삭제된다`() {
            register(email = "logout@example.com")
            val loginResult = login("logout@example.com")

            service.logout(RefreshRequest(loginResult.refreshToken))

            val stored = refreshTokenRepository.findByToken(loginResult.refreshToken)
            assertThat(stored).isNull()
        }

        @Test
        fun `존재하지 않는 refreshToken으로 로그아웃해도 예외가 발생하지 않는다`() {
            service.logout(RefreshRequest("nonexistent-token"))
        }
    }
}
