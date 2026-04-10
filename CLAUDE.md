# CLAUDE.md — 코딩 관례

이 프로젝트에서 코드를 작성하거나 수정할 때 반드시 아래 관례를 따른다.

---

## 1. 기술 스택

| 영역 | 기술 |
|------|------|
| Language | Kotlin 2.1.x |
| Framework | Spring Boot 4.0.x |
| ORM | Spring Data JPA (Hibernate) |
| DB (개발) | H2 In-memory |
| Build | Gradle 8.14+, Kotlin DSL |
| JVM | Java 21 |
| Security | Spring Security 7.x + JJWT 0.12.x (stateless JWT) |

---

## 2. 패키지 구조

비즈니스 도메인(모듈) 단위로 패키지를 구성한다.

```
com.example.demo
├── common/                           # 공통 인프라 (도메인 로직 없음)
│   ├── domain/
│   │   └── BaseEntity.kt             # @MappedSuperclass — createdAt/updatedAt
│   ├── security/
│   │   ├── SecurityConfig.kt         # SecurityFilterChain, PasswordEncoder
│   │   ├── SecurityExtensions.kt
│   │   ├── jwt/
│   │   │   ├── JwtTokenProvider.kt   # 토큰 생성·검증·파싱
│   │   │   └── JwtAuthenticationFilter.kt
│   │   ├── principal/
│   │   │   └── CustomPrincipal.kt    # SecurityContext에 저장되는 주체
│   │   └── resolver/
│   │       ├── CurrentMember.kt      # @CurrentMember 파라미터 어노테이션
│   │       └── CurrentMemberArgumentResolver.kt
│   ├── config/
│   │   └── JacksonConfig.kt
│   ├── GlobalExceptionHandler.kt
│   └── WebConfig.kt
├── member/                           # Member 모듈
│   ├── domain/
│   │   ├── Member.kt
│   │   ├── RefreshToken.kt
│   │   └── Role.kt
│   ├── repository/
│   │   ├── MemberRepository.kt
│   │   └── RefreshTokenRepository.kt
│   ├── service/
│   │   ├── ports/inp/
│   │   │   └── MemberService.kt      # 공개 인터페이스
│   │   ├── DefaultMemberService.kt
│   │   └── CustomUserDetailsService.kt
│   ├── controller/
│   │   ├── AuthController.kt         # /api/auth/**
│   │   └── MemberController.kt       # /api/members/**
│   └── dto/
└── reminder/                         # Reminder 모듈
    ├── domain/
    │   ├── ReminderList.kt
    │   ├── Reminder.kt
    │   └── Priority.kt
    ├── repository/
    │   ├── ReminderListRepository.kt
    │   └── ReminderRepository.kt
    ├── service/
    │   ├── ports/inp/
    │   │   ├── ReminderListService.kt
    │   │   └── ReminderService.kt
    │   ├── DefaultReminderListService.kt
    │   └── DefaultReminderService.kt
    ├── controller/
    │   ├── ReminderListController.kt
    │   └── ReminderController.kt
    └── dto/
```

### 모듈 간 의존 규칙
- 모듈 내부 레이어끼리는 자유롭게 참조한다.
- **모듈 간 참조는 service 인터페이스(`ports/inp/`)를 통해서만 허용한다.**
- 다른 모듈의 `domain` / `repository`를 직접 import하지 않는다.
- `common`은 도메인 로직 없이 인프라/설정만 담는다. `common`이 `member`·`reminder`를 import하면 안 된다.

---

## 3. 도메인 엔티티

- 패키지명은 `entity`가 아닌 `domain`을 사용한다.
- 공통 날짜 필드는 `BaseEntity`를 상속해 사용한다.

### BaseEntity
```kotlin
@MappedSuperclass
abstract class BaseEntity {
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime

    @Column(nullable = false)
    var updatedAt: LocalDateTime

    init {
        val now = LocalDateTime.now()
        createdAt = now
        updatedAt = now
    }
}
```

### 날짜 필드 규칙
- `@PrePersist` / `@PreUpdate`를 **사용하지 않는다**.
- `BaseEntity`를 상속하지 않는 엔티티는 `init` 블록에서 직접 초기화한다.
- `createdAt`은 `val` + `@Column(updatable = false)`로 불변 유지.

### 상태 변경
- 엔티티 필드를 외부에서 직접 변경하지 않는다.
- 상태 변경은 엔티티 내부의 명시적 `update()` 메서드를 통해 수행한다.
- `update()`에서 `updatedAt = LocalDateTime.now()`를 직접 설정한다.

```kotlin
fun update(name: String, color: String, displayOrder: Int) {
    this.name = name
    this.color = color
    this.displayOrder = displayOrder
    this.updatedAt = LocalDateTime.now()
}
```

### 연관관계
- `@OneToMany`: `cascade = [CascadeType.ALL]`, `orphanRemoval = true` 기본 설정.
- `@ManyToOne`: `fetch = FetchType.LAZY` 명시.

---

## 4. 서비스

### 인터페이스 / 구현체 분리
- 서비스 인터페이스는 `ports/inp/` 패키지에 위치한다 (Inbound Port).
- 인터페이스에는 메서드 시그니처만 선언하고, Spring / 트랜잭션 어노테이션을 붙이지 않는다.
- 구현체는 `service/` 패키지에 위치하며, 클래스명은 **`Default` 접두사**를 붙인다.

```
service/
├── ports/
│   └── inp/
│       └── SomeService.kt      # 인터페이스 — Inbound Port
└── DefaultSomeService.kt       # 구현체 — @Service, @Transactional
```

### 트랜잭션
- 구현체 클래스 레벨에 `@Transactional(readOnly = true)` 기본 선언.
- 쓰기 작업 메서드에만 `@Transactional` 개별 추가.

### 예외
- 단건 조회 실패 시 `NoSuchElementException`을 던진다 (`findByIdOrNull` + Elvis 연산자).

### 서비스 반환 타입
- 서비스 인터페이스는 DTO를 반환할 수 있다 (컨트롤러에서 `.from()` 변환 불필요).
- 내부 서비스 간 호출(ownership 검증 등)에는 도메인 객체를 반환한다.

---

## 5. Spring Security & JWT

### 인증 흐름
1. 클라이언트가 `Authorization: Bearer <accessToken>` 헤더 전송.
2. `JwtAuthenticationFilter`가 토큰을 검증하고 `CustomPrincipal`을 SecurityContext에 저장.
3. 컨트롤러에서 `@CurrentMember memberId: Long`으로 인증된 사용자 ID를 받는다.

### CustomPrincipal
SecurityContext에 저장되는 주체. `UserDetails` 구현체.

```kotlin
data class CustomPrincipal(
    val memberId: Long,
    val email: String,
    val roles: Collection<GrantedAuthority> = emptyList()
) : UserDetails {
    override fun getUsername(): String = email
    // ...
}
```

### @CurrentMember
컨트롤러 파라미터 어노테이션. `CurrentMemberArgumentResolver`가 `CustomPrincipal.memberId`를 주입한다.

```kotlin
// 사용 예
@GetMapping
fun getAll(@CurrentMember memberId: Long): List<ReminderListResponse> =
    reminderListService.findAll(memberId)
```

- `@AuthenticationPrincipal UserDetails`를 직접 쓰지 않는다. `@CurrentMember`를 사용한다.

### JWT 토큰
- accessToken: JJWT 0.12.x, subject = `memberId.toString()`, claim `"email"`.
- refreshToken: UUID, DB(`refresh_tokens` 테이블)에 저장, 사용 시 rotation.

### SecurityConfig
- `/api/auth/**`, `/h2-console/**`은 `permitAll()`.
- 나머지는 `authenticated()`.
- `JwtAuthenticationFilter`는 `@Component` 없이 `SecurityConfig` 내부에서 직접 생성 (이중 등록 방지).

---

## 6. 컨트롤러

- `@CurrentMember memberId: Long`으로 인증된 사용자 ID를 받는다.
- `@AuthenticationPrincipal`을 직접 사용하지 않는다.
- 요청 DTO는 `@Valid @RequestBody`로 받는다.
- `@ResponseStatus(HttpStatus.CREATED)` / `@ResponseStatus(HttpStatus.NO_CONTENT)` 명시.

---

## 7. 예외 처리

`GlobalExceptionHandler`에서 공통 처리:

| 예외 | HTTP Status |
|------|------------|
| `NoSuchElementException` | 404 |
| `IllegalArgumentException` | 400 |
| `MethodArgumentNotValidException` | 400 (필드별 메시지) |
| `Exception` | 500 (log.error 포함) |

응답 형식: `{ "message": "..." }`

---

## 8. 테스트

### 원칙
- **기능을 추가하거나 수정할 때는 반드시 테스트를 함께 작성한다.**

### 테스트 종류별 방식

| 대상 | 방식 |
|------|------|
| 도메인 엔티티 | 순수 단위 테스트 (Spring 컨텍스트 없음) |
| 서비스 | `@SpringBootTest` + `@Transactional` 통합 테스트 |
| 컨트롤러 | `@SpringBootTest(webEnvironment = MOCK)` + MockMvc + Spring Security |

- MockK 사용 금지.
- 서비스 테스트: `@Autowired`로 인터페이스 타입으로 주입.

### 서비스 통합 테스트 구조
```kotlin
@SpringBootTest
@Transactional
class SomeServiceTest {
    @Autowired lateinit var service: SomeService        // 인터페이스 타입
    @Autowired lateinit var repository: SomeRepository  // 데이터 세팅용
}
```

### 컨트롤러 테스트 구조
```kotlin
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@WithMockCustomUser                    // memberId = 1L, email = "test@example.com"
class SomeControllerTest {

    @Autowired lateinit var context: WebApplicationContext
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder>(springSecurity())
            .build()
    }
}
```

- `@WithMockCustomUser`를 사용한다 (`@WithMockUser` 금지 — username이 Long으로 파싱 불가).
- `springSecurity()`를 반드시 `.apply()`한다.
- MockMvc는 `@BeforeEach`에서 초기화한다.

### @WithMockCustomUser
```kotlin
@Retention(AnnotationRetention.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory::class)
annotation class WithMockCustomUser(
    val memberId: Long = 1L,
    val email: String = "test@example.com"
)
```
`test/kotlin/.../common/security/` 패키지에 위치.

### 테스트 구조
- `@Nested` inner class로 동작 그룹 분리.
- 테스트 메서드명은 **한글 backtick 문자열**로 작성.

### Assertion
- AssertJ `assertThat(...)` 사용. JUnit `assertEquals` 금지.

---

## 9. 코드 스타일

- `kotlinOptions` deprecated DSL 대신 `kotlin { compilerOptions { ... } }` 블록 사용.
- 불필요한 세미콜론, 명시적 타입 선언 생략 (타입 추론 활용).
- 함수 본문이 단일 표현식이면 `= ...` 형태로 작성.

---

## 10. 변경 이력 요약

| 시점 | 결정 내용 |
|------|---------|
| 초기 | `entity` 패키지 → `domain` 으로 변경 |
| 초기 | `@PreUpdate` 제거, `init` 블록에서 날짜 초기화 |
| 초기 | 엔티티 상태 변경은 `update()` 메서드를 통해서만 |
| 서비스 | 서비스 인터페이스/구현체 분리 도입 |
| 서비스 | 구현체 네이밍 `*ServiceImpl` → `Default*Service` |
| 서비스 | 인터페이스는 `ports/inp/` 패키지로 분리 |
| 테스트 | 서비스 테스트: `@SpringBootTest` 통합 테스트 (MockK 금지) |
| 보안 | Spring Security + JJWT stateless 인증 도입 |
| 보안 | `CustomPrincipal` + `@CurrentMember` 파라미터 리졸버 도입 |
| 보안 | refreshToken rotation — UUID, DB 저장, 재발급 시 교체 |
| 테스트 | 컨트롤러 테스트: `@WithMockCustomUser` + `springSecurity()` 도입 |
| 구조 | `common/domain/BaseEntity` 도입 — createdAt/updatedAt 공통 처리 |
