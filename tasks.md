# Tasks — Apple Reminders Web Version (Backend)

> `plan.md` 기반 세부 작업 목록. 완료 시 `[ ]` → `[x]` 체크.

---

## Phase 1 — 프로젝트 기반 구축

#### 환경 설정
- [x] Spring Boot + Kotlin + JPA/H2 프로젝트 생성
- [x] `build.gradle.kts` 의존성 정리 (기존 `Item` 예제 코드 제거)
- [x] `WebConfig.kt` 생성 — CORS 설정 (`localhost:3000` 허용)
- [x] `application.yml` — H2 콘솔, JPA ddl-auto, show-sql 설정 확인

#### 엔티티
- [x] `ReminderList.kt` 엔티티 생성
  - [x] 필드: `id`, `name`, `color`, `displayOrder`, `createdAt`, `updatedAt`
  - [x] `@Entity`, `@Table(name = "reminder_lists")` 선언
- [x] `Reminder.kt` 엔티티 생성
  - [x] 필드: `id`, `listId`, `title`, `isCompleted`, `displayOrder`, `createdAt`, `updatedAt`
  - [x] `@ManyToOne` 연관관계 설정 (`ReminderList`)

#### Repository / Service / Controller
- [x] `ReminderListRepository.kt` — `JpaRepository<ReminderList, Long>`
- [x] `ReminderRepository.kt` — `JpaRepository<Reminder, Long>`
- [x] `ReminderListService.kt`
  - [x] `findAll()` 구현
  - [x] `create(name, color)` 구현
- [x] `ReminderService.kt`
  - [x] `findByListId(listId)` 구현
  - [x] `create(listId, title)` 구현
- [x] `ReminderListController.kt`
  - [x] `GET /api/lists`
  - [x] `POST /api/lists`
- [x] `ReminderController.kt`
  - [x] `GET /api/lists/{listId}/reminders`
  - [x] `POST /api/lists/{listId}/reminders`

#### DTO
- [x] `ReminderListRequest.kt` (name, color)
- [x] `ReminderListResponse.kt`
- [x] `ReminderRequest.kt` (title)
- [x] `ReminderResponse.kt`

### Phase 1 완료 기준
- [x] `./gradlew bootRun` 실행 후 API 응답 확인

---

## Phase 2 — 핵심 CRUD + Apple 스타일 UI

#### API 추가
- [x] `ReminderListController` 확장
  - [x] `PUT /api/reminder-lists/{id}` — 목록 수정
  - [x] `DELETE /api/reminder-lists/{id}` — 목록 삭제
- [ ] `ReminderController` 확장
  - [ ] `PUT /api/reminders/{id}` — 리마인더 수정
  - [ ] `DELETE /api/reminders/{id}` — 리마인더 삭제
  - [ ] `PATCH /api/reminders/{id}/complete` — 완료 토글

#### 엔티티 필드 확장
- [ ] `Reminder.kt` 필드 추가
  - [ ] `notes: String?`
  - [ ] `isFlagged: Boolean`
  - [ ] `priority: Enum(NONE, LOW, MEDIUM, HIGH)`
  - [ ] `dueDate: LocalDate?`
  - [ ] `dueTime: LocalTime?`
  - [ ] `completedAt: LocalDateTime?`
- [ ] `ReminderRequest.kt` 필드 추가 (위 항목 반영)
- [ ] `ReminderResponse.kt` 필드 추가

### Phase 2 완료 기준
- [ ] CRUD API 전체 동작 확인

---

## Phase 3 — 상세 편집 패널 + 스마트 목록

#### 스마트 목록 API
- [ ] `SmartListController.kt` 생성
- [ ] `PATCH /api/reminders/{id}/flag` — 플래그 토글
- [ ] `GET /api/smart/today` — 오늘 (dueDate ≤ 오늘, 미완료)
- [ ] `GET /api/smart/scheduled` — 예정됨 (dueDate > 오늘, 미완료)
- [ ] `GET /api/smart/all` — 전체 미완료
- [ ] `GET /api/smart/completed` — 완료됨
- [ ] `GET /api/smart/flagged` — 플래그됨
- [ ] `SmartListService.kt` — 각 필터 쿼리 구현 (JPQL 또는 Specification)

### Phase 3 완료 기준
- [ ] 스마트 목록 5종 필터 결과 정확성 확인

---

## Phase 4 — 고급 기능

#### 순서 변경 API
- [ ] `PATCH /api/lists/reorder` — `[{id, displayOrder}]` 배열 받아 일괄 업데이트
- [ ] `PATCH /api/reminders/reorder` — 동일 방식

#### 반복 & 하위 리마인더
- [ ] `Reminder.kt` `recurrence` 필드 추가 (`Enum: NONE/DAILY/WEEKLY/MONTHLY/YEARLY`)
- [ ] `Reminder.kt` `parentId` 필드 추가 (자기참조 FK)
- [ ] 하위 리마인더 조회: `findByParentId(parentId)`

#### 검색 API
- [ ] `GET /api/search?q={keyword}` — `title` + `notes` LIKE 검색
- [ ] 응답에 `listId`, `listName`, `listColor` 포함

### Phase 4 완료 기준
- [ ] 드래그 정렬 API 동작 확인
- [ ] 검색 API 결과 확인

---

## Phase 5 — 다크모드 + 마무리

#### 안정화
- [ ] `GlobalExceptionHandler.kt` (`@ControllerAdvice`)
  - [ ] `NoSuchElementException` → 404
  - [ ] `MethodArgumentNotValidException` → 400
  - [ ] 그 외 → 500
- [ ] Bean Validation 적용 (`@Valid`, `@NotBlank`, `@NotNull`)
  - [ ] `ReminderListRequest`
  - [ ] `ReminderRequest`
- [ ] `ApiResponse<T>` 래퍼 클래스 — 모든 응답 포맷 통일
  - [ ] `{ "data": ..., "message": ..., "status": ... }`

### Phase 5 완료 기준
- [ ] API 오류 시 적절한 HTTP 상태코드 응답 확인
- [ ] 전체 API 응답 포맷 통일 확인

---

## 전체 진행 현황

| Phase | 설명 | 진행 |
|-------|------|------|
| Phase 1 | 프로젝트 기반 구축 | ✅ |
| Phase 2 | 핵심 CRUD + Apple UI | 🔲 |
| Phase 3 | 상세 패널 + 스마트 목록 | 🔲 |
| Phase 4 | 고급 기능 | 🔲 |
| Phase 5 | 다크모드 + 마무리 | 🔲 |
