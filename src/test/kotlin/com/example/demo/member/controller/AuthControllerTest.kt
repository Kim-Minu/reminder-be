package com.example.demo.member.controller

import com.example.demo.member.dto.LoginRequest
import com.example.demo.member.dto.RegisterRequest
import com.example.demo.member.repository.MemberRepository
import com.example.demo.member.service.ports.inp.MemberService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
class AuthControllerTest {

    @Autowired lateinit var context: WebApplicationContext
    @Autowired lateinit var memberService: MemberService
    @Autowired lateinit var memberRepository: MemberRepository

    private lateinit var mockMvc: MockMvc
    private val objectMapper = jacksonObjectMapper()

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply<org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder>(springSecurity())
            .build()
    }

    private fun register(email: String, password: String = "password1234", name: String = "테스터") =
        memberService.register(RegisterRequest(email = email, password = password, name = name))

    private fun login(email: String, password: String = "password1234") =
        memberService.login(LoginRequest(email = email, password = password))

    @Nested
    inner class Register {

        @Test
        fun `유효한 요청으로 회원가입하면 201과 회원 정보를 반환한다`() {
            val body = mapOf("email" to "new@example.com", "password" to "password1234", "name" to "홍길동")

            mockMvc.post("/api/auth/register") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(body)
            }.andExpect {
                status { isCreated() }
                jsonPath("$.email") { value("new@example.com") }
                jsonPath("$.name") { value("홍길동") }
                jsonPath("$.id") { isNumber() }
            }
        }

        @Test
        fun `중복 이메일로 가입하면 400을 반환한다`() {
            register("dup@example.com")
            val body = mapOf("email" to "dup@example.com", "password" to "password1234", "name" to "신규")

            mockMvc.post("/api/auth/register") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(body)
            }.andExpect { status { isBadRequest() } }
        }

        @Test
        fun `이메일 형식이 올바르지 않으면 400을 반환한다`() {
            val body = mapOf("email" to "not-an-email", "password" to "password1234", "name" to "홍길동")

            mockMvc.post("/api/auth/register") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(body)
            }.andExpect { status { isBadRequest() } }
        }

        @Test
        fun `비밀번호가 8자 미만이면 400을 반환한다`() {
            val body = mapOf("email" to "short@example.com", "password" to "1234567", "name" to "홍길동")

            mockMvc.post("/api/auth/register") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(body)
            }.andExpect { status { isBadRequest() } }
        }
    }

    @Nested
    inner class Login {

        @Test
        fun `올바른 자격증명으로 로그인하면 200과 토큰을 반환한다`() {
            register("login@example.com")
            val body = mapOf("email" to "login@example.com", "password" to "password1234")

            mockMvc.post("/api/auth/login") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(body)
            }.andExpect {
                status { isOk() }
                jsonPath("$.accessToken") { isString() }
                jsonPath("$.refreshToken") { isString() }
                jsonPath("$.tokenType") { value("Bearer") }
            }
        }

        @Test
        fun `잘못된 비밀번호로 로그인하면 400을 반환한다`() {
            register("wrong@example.com")
            val body = mapOf("email" to "wrong@example.com", "password" to "wrongpassword")

            mockMvc.post("/api/auth/login") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(body)
            }.andExpect { status { isBadRequest() } }
        }

        @Test
        fun `존재하지 않는 이메일로 로그인하면 404를 반환한다`() {
            val body = mapOf("email" to "notfound@example.com", "password" to "password1234")

            mockMvc.post("/api/auth/login") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(body)
            }.andExpect { status { isNotFound() } }
        }
    }

    @Nested
    inner class Refresh {

        @Test
        fun `유효한 refreshToken으로 새 토큰을 발급한다`() {
            register("refresh@example.com")
            val tokens = login("refresh@example.com")
            val body = mapOf("refreshToken" to tokens.refreshToken)

            mockMvc.post("/api/auth/refresh") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(body)
            }.andExpect {
                status { isOk() }
                jsonPath("$.accessToken") { isString() }
                jsonPath("$.refreshToken") { isString() }
            }
        }

        @Test
        fun `유효하지 않은 refreshToken으로 재발급하면 404를 반환한다`() {
            val body = mapOf("refreshToken" to "invalid-token")

            mockMvc.post("/api/auth/refresh") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(body)
            }.andExpect { status { isNotFound() } }
        }
    }

    @Nested
    inner class Logout {

        @Test
        fun `로그아웃하면 204를 반환한다`() {
            register("logout@example.com")
            val tokens = login("logout@example.com")
            val body = mapOf("refreshToken" to tokens.refreshToken)

            mockMvc.post("/api/auth/logout") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(body)
            }.andExpect { status { isNoContent() } }
        }
    }
}
