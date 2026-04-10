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

## Phase 6 — 신규 메뉴 API (장바구니 / 예산 / 마이페이지)

### 장바구니 (`/api/cart`)

> 월 단위 주간별 관리 — 해당 월을 1~5주차로 구분하여 장바구니 생성/조회

#### 엔티티
- [x] `CartWeek.kt` 엔티티 생성 — 주차 단위 장바구니 묶음
  - [x] 필드: `id`, `memberId`, `year`, `month`, `weekOfMonth` (1~5), `label` (예: "4월 1주차"), `createdAt`
  - [x] `@Entity`, `@Table(name = "cart_weeks")` 선언
  - [x] `(memberId, year, month, weekOfMonth)` unique 제약
- [x] `CartItem.kt` 엔티티 생성
  - [x] 필드: `id`, `cartWeekId` (FK → CartWeek), `name`, `quantity`, `unitPrice`, `isChecked`, `displayOrder`, `createdAt`, `modifiedAt`
  - [x] `@ManyToOne` 연관관계 설정 (`CartWeek`)

#### Repository / Service / Controller
- [x] `CartWeekRepository.kt` + `CartWeekRepositoryCustom` (QueryDSL — fetchJoin으로 items 로딩)
- [x] `CartItemRepository.kt`
- [x] `CartService.kt`
  - [x] `findWeeksByMonth(memberId, year, month)` — 해당 월의 주차 목록 + 아이템 목록 반환
  - [x] `findOrCreateWeek(memberId, request)` — 주차 없으면 자동 생성
  - [x] `createItem(weekId, memberId, request)` 구현
  - [x] `updateItem(id, memberId, request)` 구현
  - [x] `deleteItem(id, memberId)` 구현
  - [x] `toggleCheck(id, memberId)` 구현
  - [x] `weekTotal` — totalAmount / checkedAmount를 CartWeekResponse에 포함
- [x] `CartController.kt`
  - [x] `GET /api/cart?year={YYYY}&month={M}` — 해당 월 주차별 장바구니 목록 반환
  - [x] `POST /api/cart/weeks` — 주차 수동 생성 (body: year, month, weekOfMonth)
  - [x] `POST /api/cart/weeks/{weekId}/items` — 아이템 추가
  - [x] `PUT /api/cart/items/{id}` — 아이템 수정 (이름/수량/단가)
  - [x] `DELETE /api/cart/items/{id}` — 아이템 삭제
  - [x] `PATCH /api/cart/items/{id}/check` — 체크 토글
  - [x] `DELETE /api/cart/weeks/{weekId}/checked` — 주차 내 체크 항목 일괄 삭제

#### DTO
- [x] `CartWeekResponse.kt` (id, label, items, totalAmount, checkedAmount)
- [x] `CartItemRequest.kt` (name, quantity, unitPrice)
- [x] `CartItemResponse.kt` (id, name, quantity, unitPrice, lineTotal, isChecked)

---

### 예산 (`/api/budget`)

#### 엔티티
- [ ] `Budget.kt` 엔티티 생성
  - [ ] 필드: `id`, `userId`, `category`, `amount`, `month` (YearMonth), `createdAt`, `updatedAt`
- [ ] `Expense.kt` 엔티티 생성
  - [ ] 필드: `id`, `budgetId`, `description`, `amount`, `spentAt`, `createdAt`

#### Repository / Service / Controller
- [ ] `BudgetRepository.kt`
- [ ] `ExpenseRepository.kt`
- [ ] `BudgetService.kt`
  - [ ] `findByMonth(userId, month)` — 월별 예산 목록 + 지출 합산
  - [ ] `create(request)` 구현
  - [ ] `update(id, request)` 구현
  - [ ] `delete(id)` 구현
- [ ] `ExpenseService.kt`
  - [ ] `findByBudgetId(budgetId)` 구현
  - [ ] `create(budgetId, request)` 구현
  - [ ] `delete(id)` 구현
- [ ] `BudgetController.kt`
  - [ ] `GET /api/budget?month={YYYY-MM}` — 월별 예산 목록 (지출 합산 포함)
  - [ ] `POST /api/budget` — 예산 항목 생성
  - [ ] `PUT /api/budget/{id}` — 예산 수정
  - [ ] `DELETE /api/budget/{id}` — 예산 삭제
- [ ] `ExpenseController.kt`
  - [ ] `GET /api/budget/{budgetId}/expenses` — 지출 목록
  - [ ] `POST /api/budget/{budgetId}/expenses` — 지출 추가
  - [ ] `DELETE /api/budget/expenses/{id}` — 지출 삭제

#### DTO
- [ ] `BudgetRequest.kt` (category, amount, month)
- [ ] `BudgetResponse.kt` (spent, remaining 포함)
- [ ] `ExpenseRequest.kt` (description, amount, spentAt)
- [ ] `ExpenseResponse.kt`

---

### 마이페이지 (`/api/users`)

#### API
- [ ] `UserController.kt` 확장
  - [ ] `GET /api/users/me` — 내 정보 조회 (이름, 이메일)
  - [ ] `PUT /api/users/me` — 내 정보 수정 (이름)
  - [ ] `PUT /api/users/me/password` — 비밀번호 변경

#### DTO
- [ ] `UpdateUserRequest.kt` (name)
- [ ] `ChangePasswordRequest.kt` (currentPassword, newPassword)
- [ ] `UserResponse.kt` (id, name, email)

---

### 홈 요약 (`/api/home`)
- [ ] `HomeController.kt` 생성
  - [ ] `GET /api/home/summary` — 통합 요약 응답
    - [ ] 오늘 리마인더 수 (dueDate = 오늘, 미완료)
    - [ ] 장바구니 미체크 아이템 수
    - [ ] 이번 달 총 예산 / 총 지출 / 잔액
    - [ ] 최근 리마인더 5개 (생성일 기준)
- [ ] `HomeSummaryResponse.kt` DTO

---

### Phase 6 완료 기준
- [ ] 장바구니 CRUD + 체크 토글 API 동작 확인
- [ ] 예산 월별 조회 — 지출 합산 정확성 확인
- [ ] 홈 요약 API 데이터 정확성 확인
- [ ] 마이페이지 정보 수정 API 동작 확인

---

## 전체 진행 현황

| Phase | 설명 | 진행 |
|-------|------|------|
| Phase 1 | 프로젝트 기반 구축 | ✅ |
| Phase 2 | 핵심 CRUD + Apple UI | 🔲 |
| Phase 3 | 상세 패널 + 스마트 목록 | 🔲 |
| Phase 4 | 고급 기능 | 🔲 |
| Phase 5 | 다크모드 + 마무리 | 🔲 |
| Phase 6 | 신규 메뉴 API | 🔲 |
