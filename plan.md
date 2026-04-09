# 개발 계획 — Apple Reminders Web Version (Backend)

> spec.md 기반 단계적 개발 계획.
> 각 Phase는 독립적으로 동작 가능한 상태로 완성한다.

---

## 기술 스택

| 항목 | 기술 | 버전 |
|------|------|------|
| 언어 | Kotlin | 1.9.x |
| 프레임워크 | Spring Boot | 3.4.4 |
| ORM | Spring Data JPA (Hibernate) | - |
| DB (개발) | H2 In-memory | - |
| DB (운영) | PostgreSQL | 16+ |
| 빌드 | Gradle Kotlin DSL | 8.11 |
| API | REST / JSON | - |
| 테스트 | JUnit 5 + MockMvc | - |

### 개발 환경
| 항목 | 내용 |
|------|------|
| JDK | 17 |
| API 포트 | Backend `:8080` |
| CORS | Spring CORS 설정으로 `localhost:3000` 허용 |

---

## Phase 1 — 프로젝트 기반 구축

> 목표: 프론트-백엔드 연결 확인, Hello World 수준의 동작

- [x] Spring Boot + Kotlin + JPA/H2 프로젝트 생성 ✅ (완료)
- [ ] CORS 설정 (`WebMvcConfigurer`)
- [ ] `ReminderList` 엔티티 + 테이블 생성
- [ ] `Reminder` 엔티티 + 테이블 생성
- [ ] `GET /api/lists` — 목록 조회 API
- [ ] `POST /api/lists` — 목록 생성 API
- [ ] `GET /api/lists/{listId}/reminders` — 리마인더 조회 API
- [ ] `POST /api/lists/{listId}/reminders` — 리마인더 생성 API

### 완료 기준
- API 서버가 정상 기동되고 응답을 반환한다.

---

## Phase 2 — 핵심 CRUD

> 목표: 전체 CRUD API 완성

- [ ] `PUT /api/lists/{id}` — 목록 수정
- [ ] `DELETE /api/lists/{id}` — 목록 삭제
- [ ] `PUT /api/reminders/{id}` — 리마인더 수정
- [ ] `DELETE /api/reminders/{id}` — 리마인더 삭제
- [ ] `PATCH /api/reminders/{id}/complete` — 완료 토글
- [ ] `Reminder` 필드 확장: `notes`, `isFlagged`, `priority`, `dueDate`, `dueTime`

### 완료 기준
- CRUD API 전체 동작

---

## Phase 3 — 스마트 목록

> 목표: 스마트 목록 필터 API 완성

- [ ] `PATCH /api/reminders/{id}/flag` — 플래그 토글
- [ ] `GET /api/smart/today` — 오늘 (마감일 = 오늘 또는 기한 초과, 미완료)
- [ ] `GET /api/smart/scheduled` — 예정됨 (마감일 > 오늘, 미완료)
- [ ] `GET /api/smart/all` — 전체 미완료
- [ ] `GET /api/smart/completed` — 완료됨
- [ ] `GET /api/smart/flagged` — 플래그됨

### 완료 기준
- 스마트 목록 5종 전부 올바른 필터링

---

## Phase 4 — 고급 기능

> 목표: 순서 변경, 반복, 하위 리마인더, 검색 API

- [ ] `PATCH /api/lists/reorder` — 목록 순서 변경
- [ ] `PATCH /api/reminders/reorder` — 리마인더 순서 변경
- [ ] `recurrence` 필드 처리 (NONE / DAILY / WEEKLY / MONTHLY / YEARLY)
- [ ] 하위 리마인더 (`parentId`) 조회/생성
- [ ] `GET /api/search?q={keyword}` — 제목 + 메모 전문 검색

### 완료 기준
- 순서 변경 API 동작
- 검색 API 결과 반환

---

## Phase 5 — 안정화

> 목표: 예외 처리, 입력 검증, 응답 포맷 통일

- [ ] 전역 예외 핸들러 (`@ControllerAdvice`)
- [ ] 입력값 검증 (`@Valid`, Bean Validation)
- [ ] API 응답 포맷 통일 (`ApiResponse<T>` 래퍼)

### 완료 기준
- API 오류 시 적절한 HTTP 상태코드 응답
- 전체 API 응답 포맷 통일

---

## 디렉토리 구조

```
src/main/kotlin/com/example/demo/
├── reminder/
│   ├── domain/
│   │   ├── ReminderList.kt
│   │   └── Reminder.kt
│   ├── repository/
│   ├── service/
│   │   ├── ports/inp/
│   │   └── Default*.kt
│   └── controller/
└── config/
    └── WebConfig.kt   # CORS
```

---

## Phase 진행 상태

| Phase | 내용 | 상태 |
|-------|------|------|
| Phase 1 | 프로젝트 기반 구축 | 🔲 진행 전 |
| Phase 2 | 핵심 CRUD | 🔲 진행 전 |
| Phase 3 | 스마트 목록 | 🔲 진행 전 |
| Phase 4 | 고급 기능 (검색, 순서 변경, 반복) | 🔲 진행 전 |
| Phase 5 | 안정화 | 🔲 진행 전 |
