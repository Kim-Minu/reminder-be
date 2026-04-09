package com.example.demo.member.service

import com.example.demo.member.domain.Member
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

    @Nested
    inner class Register {

        @Test
        fun `이메일, 비밀번호, 이름으로 회원을 생성한다`() {

            val member = service.register("user@example.com", "password1234", "홍길동")

            assertThat(member.id).isPositive()
            assertThat(member.email).isEqualTo("user@example.com")
            assertThat(member.name).isEqualTo("홍길동")
            assertThat(passwordEncoder.matches("password1234", member.password)).isTrue()
        }

        @Test
        fun `중복 이메일로 가입하면 IllegalArgumentException을 던진다`() {

            service.register("dup@example.com", "password1234", "첫 번째")

            assertThatThrownBy { service.register("dup@example.com", "password1234", "두 번째") }
                .isInstanceOf(IllegalArgumentException::class.java)
        }
    }

    @Nested
    inner class Login {

        @Test
        fun `올바른 이메일과 비밀번호로 로그인하면 accessToken과 refreshToken을 반환한다`() {
            service.register("login@example.com", "password1234", "테스터")

            val result = service.login("login@example.com", "password1234")

            assertThat(result.accessToken).isNotBlank()
            assertThat(result.refreshToken).isNotBlank()
        }

        @Test
        fun `존재하지 않는 이메일로 로그인하면 NoSuchElementException을 던진다`() {
            assertThatThrownBy { service.login("notfound@example.com", "password") }
                .isInstanceOf(NoSuchElementException::class.java)
        }

        @Test
        fun `잘못된 비밀번호로 로그인하면 IllegalArgumentException을 던진다`() {
            service.register("wrong@example.com", "password1234", "테스터")

            assertThatThrownBy { service.login("wrong@example.com", "wrongpassword") }
                .isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `로그인 시 refreshToken이 DB에 저장된다`() {
            service.register("save@example.com", "password1234", "테스터")
            val result = service.login("save@example.com", "password1234")

            val stored = refreshTokenRepository.findByToken(result.refreshToken)
            assertThat(stored).isNotNull()
        }
    }

    @Nested
    inner class Refresh {

        @Test
        fun `유효한 refreshToken으로 새 토큰을 발급한다`() {
            service.register("refresh@example.com", "password1234", "테스터")
            val loginResult = service.login("refresh@example.com", "password1234")

            val result = service.refresh(loginResult.refreshToken)

            assertThat(result.accessToken).isNotBlank()
            assertThat(result.refreshToken).isNotBlank()
            assertThat(result.refreshToken).isNotEqualTo(loginResult.refreshToken)
        }

        @Test
        fun `사용된 refreshToken은 DB에서 삭제된다`() {
            service.register("del@example.com", "password1234", "테스터")
            val loginResult = service.login("del@example.com", "password1234")
            service.refresh(loginResult.refreshToken)

            val stored = refreshTokenRepository.findByToken(loginResult.refreshToken)
            assertThat(stored).isNull()
        }

        @Test
        fun `존재하지 않는 refreshToken으로 재발급하면 NoSuchElementException을 던진다`() {
            assertThatThrownBy { service.refresh("invalid-token") }
                .isInstanceOf(NoSuchElementException::class.java)
        }
    }

    @Nested
    inner class Logout {

        @Test
        fun `로그아웃하면 refreshToken이 DB에서 삭제된다`() {
            service.register("logout@example.com", "password1234", "테스터")
            val loginResult = service.login("logout@example.com", "password1234")

            service.logout(loginResult.refreshToken)

            val stored = refreshTokenRepository.findByToken(loginResult.refreshToken)
            assertThat(stored).isNull()
        }

        @Test
        fun `존재하지 않는 refreshToken으로 로그아웃해도 예외가 발생하지 않는다`() {
            service.logout("nonexistent-token")
        }
    }
}
