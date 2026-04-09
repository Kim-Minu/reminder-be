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

---

## 2. 패키지 구조 — 모듈러 모놀리스

비즈니스 도메인(모듈) 단위로 패키지를 구성한다. 기술 레이어(domain/repository/service/controller)는 각 모듈 내부에 위치한다.

```
com.example.demo
├── reminder/                     # Reminder 모듈
│   ├── domain/                   # JPA 엔티티
│   ├── repository/               # Spring Data JPA Repository
│   ├── service/                  # 비즈니스 로직
│   │   ├── ports/
│   │   │   └── inp/              # Inbound Port 인터페이스 (공개 API)
│   │   └── Default*.kt           # 구현체
│   └── controller/               # REST API
└── item/                         # Item 모듈
    ├── domain/
    ├── repository/
    ├── service/
    │   ├── ports/
    │   │   └── inp/
    │   └── Default*.kt
    └── controller/
```

### 모듈 간 의존 규칙
- 모듈 내부 레이어끼리는 자유롭게 참조한다.
- **모듈 간 참조는 service 인터페이스를 통해서만 허용한다.**
- 다른 모듈의 `domain` / `repository`를 직접 import하지 않는다.

---

## 3. 도메인 엔티티

- 패키지명은 `entity`가 아닌 `domain`을 사용한다.

### 날짜 필드
- `createdAt` / `updatedAt`은 `@PrePersist` / `@PreUpdate` 를 **사용하지 않는다**.
- `init` 블록에서 `LocalDateTime.now()`를 한 번 캡처해 두 필드에 동시 할당한다 (`createdAt == updatedAt` 보장).
- `createdAt`은 `val` + `@Column(updatable = false)` 로 선언해 불변으로 유지한다.

```kotlin
init {
    val now = LocalDateTime.now()
    createdAt = now
    updatedAt = now
}
```

### 상태 변경
- 엔티티 필드를 외부에서 직접 변경하지 않는다.
- 상태 변경은 엔티티 내부의 명시적 메서드(`update()` 등)를 통해 수행한다.
- `update()` 메서드는 변경과 함께 `updatedAt = LocalDateTime.now()`를 직접 설정한다.

```kotlin
fun update(name: String, color: String, displayOrder: Int) {
    this.name = name
    this.color = color
    this.displayOrder = displayOrder
    this.updatedAt = LocalDateTime.now()
}
```

### 연관관계
- `@OneToMany`는 `cascade = [CascadeType.ALL]`, `orphanRemoval = true`를 기본으로 설정한다.
- `@ManyToOne`은 `fetch = FetchType.LAZY`를 명시한다.

---

## 4. 서비스

### 인터페이스 / 구현체 분리
- 서비스 인터페이스는 `ports/inp/` 패키지에 위치한다 (Inbound Port).
- 인터페이스에는 메서드 시그니처만 선언하고, Spring / 트랜잭션 어노테이션을 붙이지 않는다.
- 구현체는 `service/` 패키지에 위치하며, 클래스명은 **`Default` 접두사**를 붙인다.
- 구현체에 `@Service`, `@Transactional` 등 인프라 관심사를 위치시킨다.

```
service/
├── ports/
│   └── inp/
│       └── SomeService.kt      # 인터페이스 — Inbound Port (공개 API)
└── DefaultSomeService.kt       # 구현체 — @Service, @Transactional
```

```kotlin
// service/ports/inp/SomeService.kt
package com.example.demo.some.service.ports.inp

interface SomeService {
    fun findById(id: Long): SomeEntity
    fun create(name: String): SomeEntity
}

// service/DefaultSomeService.kt
package com.example.demo.some.service


@Service
@Transactional(readOnly = true)
class DefaultSomeService(private val repo: SomeRepository) : SomeService {

    override fun findById(id: Long): SomeEntity =
        repo.findByIdOrNull(id) ?: throw NoSuchElementException("Not found: $id")

    @Transactional
    override fun create(name: String): SomeEntity = repo.save(SomeEntity(name = name))
}
```

### 트랜잭션
- 구현체 클래스 레벨에 `@Transactional(readOnly = true)`를 기본으로 선언한다.
- 쓰기 작업 메서드에만 `@Transactional`을 개별 추가한다.

### 예외
- 단건 조회 실패 시 `NoSuchElementException`을 던진다 (`findByIdOrNull` + Elvis 연산자 사용).

---

## 5. 테스트

### 원칙
- **기능을 추가하거나 수정할 때는 반드시 테스트를 함께 작성한다.**

### 테스트 종류별 방식

| 대상 | 방식 |
|------|------|
| 도메인 엔티티 | 순수 단위 테스트 (Spring/JPA 컨텍스트 없음) |
| 서비스 | `@SpringBootTest` + `@Transactional` 통합 테스트 |

- 도메인 엔티티: `@DataJpaTest`, `TestEntityManager`, MockK 사용 금지.
- 서비스: MockK 사용 금지. `@Autowired`로 인터페이스 타입으로 주입한다.

```kotlin
// 서비스 통합 테스트 기본 구조
@SpringBootTest
@Transactional
class SomeServiceTest {

    @Autowired lateinit var service: SomeService        // 인터페이스 타입
    @Autowired lateinit var repository: SomeRepository  // 데이터 세팅용
}
```

### 테스트 구조
- `@Nested` inner class로 동작 그룹을 분리한다 (예: `Constructor`, `Update`, `DateAutoRegistration`).
- 테스트 메서드명은 **한글 backtick 문자열**로 작성해 의도를 명확히 한다.

```kotlin
@Nested
inner class Update {

    @Test
    fun `update 호출 시 name이 변경된다`() { ... }
}
```

### Assertion
- `assertThat(...)`은 AssertJ를 사용한다 (`org.assertj.core.api.Assertions.assertThat`).
- JUnit의 `assertEquals` 등은 사용하지 않는다.

### 테스트 파일 위치
- 소스와 동일한 패키지 구조를 유지한다.
  - `domain/ReminderList.kt` → `test/.../domain/ReminderListTest.kt`

---

## 6. 코드 스타일

- Kotlin DSL Gradle 빌드 설정에서 `kotlinOptions` deprecated DSL 대신 `kotlin { compilerOptions { ... } }` 블록을 사용한다.
- 불필요한 세미콜론, 명시적 타입 선언은 생략한다 (Kotlin 타입 추론 활용).
- 함수 본문이 단일 표현식이면 `= ...` 형태로 작성한다.

---

## 7. 변경 이력 요약

| 시점 | 결정 내용 |
|------|---------|
| 초기 | `entity` 패키지 → `domain` 으로 변경 |
| 초기 | `@PreUpdate` 제거, `init` 블록에서 날짜 초기화 |
| 초기 | 엔티티 상태 변경은 `update()` 메서드를 통해서만 |
| 서비스 | 서비스 인터페이스/구현체 분리 도입 |
| 서비스 | 구현체 네이밍 `*ServiceImpl` → `Default*Service` |
| 서비스 | 인터페이스는 `ports/inp/` 패키지로 분리 |
| 테스트 | 도메인 엔티티 테스트: 순수 단위 테스트 (JPA 슬라이스 금지) |
| 테스트 | 서비스 테스트: `@SpringBootTest` 통합 테스트 (MockK 금지) |
