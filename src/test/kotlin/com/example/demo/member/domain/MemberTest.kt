package com.example.demo.member.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MemberTest {

    @Nested
    inner class Constructor {

        @Test
        fun `Member가 정상적으로 생성된다`() {
            val member = Member(email = "test@example.com", password = "hashed", name = "홍길동")

            assertThat(member.email).isEqualTo("test@example.com")
            assertThat(member.name).isEqualTo("홍길동")
            assertThat(member.role).isEqualTo(Role.USER)
        }
    }

    @Nested
    inner class DateAutoRegistration {

        @Test
        fun `생성 시 createdAt과 updatedAt이 동일하다`() {
            val member = Member(email = "test@example.com", password = "hashed", name = "홍길동")

            assertThat(member.createdAt).isEqualTo(member.updatedAt)
        }
    }
}
