# PRD — Apple Reminders Web Version (Backend)

## 1. 프로젝트 개요

### 배경
Apple Reminders는 iOS/macOS 생태계에서만 동작하는 할 일 관리 앱이다. 플랫폼에 종속되지 않고 브라우저에서 동일한 경험을 제공하는 웹 버전을 개발한다.

### 목표
- Apple Reminders의 핵심 UX를 웹에서 재현한다.
- 빠르고 직관적인 할 일 관리 경험을 제공한다.
- 모바일/데스크탑 반응형으로 어느 기기에서도 사용 가능하게 한다.

### 범위 (MVP)
- 리마인더 목록(List) 및 리마인더(Reminder) CRUD
- 스마트 목록 (오늘, 예정됨, 전체, 완료됨)
- 마감일/시간, 우선순위, 메모, 플래그, 반복 설정
- 검색

---

## 2. 사용자 스토리

| ID | 스토리 |
|----|--------|
| U1 | 사용자는 새 목록을 만들고 이름과 색상을 지정할 수 있다. |
| U2 | 사용자는 목록 안에 리마인더를 추가하고 완료 체크를 할 수 있다. |
| U3 | 사용자는 리마인더에 마감일/시간을 설정할 수 있다. |
| U4 | 사용자는 리마인더에 우선순위(낮음/중간/높음)를 지정할 수 있다. |
| U5 | 사용자는 리마인더에 메모를 작성할 수 있다. |
| U6 | 사용자는 리마인더에 플래그(깃발)를 달 수 있다. |
| U7 | 사용자는 반복 규칙(매일/매주/매월/매년)을 설정할 수 있다. |
| U8 | 사용자는 "오늘", "예정됨", "전체", "완료됨" 스마트 목록으로 리마인더를 필터링할 수 있다. |
| U9 | 사용자는 키워드로 리마인더를 검색할 수 있다. |
| U10 | 사용자는 완료된 리마인더를 숨기거나 볼 수 있다. |

---

## 3. 핵심 기능

### 3.1 리마인더 목록 (List) 관리
- 목록 생성 / 이름 수정 / 삭제
- 목록별 색상 설정 (Apple 기본 팔레트 16색)
- 목록별 리마인더 개수 배지 표시
- 목록 순서 드래그 정렬

### 3.2 리마인더 (Reminder) 관리
- 완료 토글 (체크박스)
- 제목 인라인 편집
- 마감일 / 마감 시간 설정
- 우선순위: 없음 / 낮음(`!`) / 중간(`!!`) / 높음(`!!!`)
- 메모(notes) 멀티라인 입력
- 플래그(깃발 아이콘) 토글
- 반복 설정: 없음 / 매일 / 매주 / 매월 / 매년
- 하위 리마인더(sub-reminder) — 선택 기능

### 3.3 스마트 목록
| 스마트 목록 | 조건 |
|------------|------|
| 오늘 | 마감일이 오늘이거나 기한 초과 (미완료) |
| 예정됨 | 마감일이 내일 이후 (미완료) |
| 전체 | 모든 미완료 리마인더 |
| 완료됨 | 완료 처리된 리마인더 |
| 플래그됨 | 플래그가 설정된 미완료 리마인더 |

### 3.4 검색
- 전체 리마인더 제목 / 메모 대상 실시간 검색
- 검색 결과에서 해당 목록 정보 표시

---

## 4. 기술 스택

| 항목 | 선택 |
|------|------|
| 언어 | Kotlin |
| 프레임워크 | Spring Boot 3.4.4 |
| ORM | Spring Data JPA (Hibernate) |
| DB (개발) | H2 In-memory |
| DB (운영 고려) | PostgreSQL |
| 빌드 | Gradle Kotlin DSL |
| API 스타일 | REST JSON |

---

## 5. 데이터 모델

### ERD 요약

```
ReminderList
─────────────
id          Long (PK)
name        String
color       String        # hex 코드
displayOrder Int
createdAt   LocalDateTime
updatedAt   LocalDateTime

Reminder
─────────────
id          Long (PK)
listId      Long (FK → ReminderList)
parentId    Long? (FK → Reminder, 하위 리마인더)
title       String
notes       String?
isCompleted Boolean
isFlagged   Boolean
priority    Enum(NONE, LOW, MEDIUM, HIGH)
dueDate     LocalDate?
dueTime     LocalTime?
recurrence  Enum(NONE, DAILY, WEEKLY, MONTHLY, YEARLY)
completedAt LocalDateTime?
displayOrder Int
createdAt   LocalDateTime
updatedAt   LocalDateTime
```

---

## 6. API 설계

### ReminderList

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/api/lists` | 전체 목록 조회 |
| POST | `/api/lists` | 목록 생성 |
| PUT | `/api/lists/{id}` | 목록 수정 |
| DELETE | `/api/lists/{id}` | 목록 삭제 |
| PATCH | `/api/lists/reorder` | 순서 변경 |

### Reminder

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/api/lists/{listId}/reminders` | 목록별 리마인더 조회 |
| POST | `/api/lists/{listId}/reminders` | 리마인더 생성 |
| PUT | `/api/reminders/{id}` | 리마인더 수정 |
| DELETE | `/api/reminders/{id}` | 리마인더 삭제 |
| PATCH | `/api/reminders/{id}/complete` | 완료 토글 |
| PATCH | `/api/reminders/{id}/flag` | 플래그 토글 |

### 스마트 목록

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/api/smart/today` | 오늘 리마인더 |
| GET | `/api/smart/scheduled` | 예정됨 |
| GET | `/api/smart/all` | 전체 미완료 |
| GET | `/api/smart/completed` | 완료됨 |
| GET | `/api/smart/flagged` | 플래그됨 |
| GET | `/api/search?q={keyword}` | 검색 |

---

## 7. 비기능 요건

| 항목 | 요건 |
|------|------|
| 성능 | 목록/리마인더 로딩 < 500ms |
| 오프라인 | MVP 범위 외 |
| 인증 | MVP 범위 외 (단일 사용자 가정) |
