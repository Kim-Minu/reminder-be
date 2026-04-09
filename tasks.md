# Tasks — Apple Reminders Web Version

> `plan.md` 기반 세부 작업 목록. 완료 시 `[ ]` → `[x]` 체크.

---

## Phase 1 — 프로젝트 기반 구축

### Backend

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

### Frontend

#### 프로젝트 셋업
- [ ] Next.js 15 프로젝트 생성 (`/workspace/frontend`, App Router, TypeScript)
- [ ] TailwindCSS v4 설치 및 설정
- [ ] pnpm 패키지 매니저 설정
- [ ] 패키지 설치: `axios`, `@tanstack/react-query`, `zustand`, `lucide-react`, `date-fns`
- [ ] `lib/api.ts` — axios 인스턴스 생성 (baseURL: `http://localhost:8080`)
- [ ] `app/providers.tsx` — TanStack Query Provider, QueryClient 설정
- [ ] `app/layout.tsx` — Provider 적용, 전역 폰트/스타일 설정

#### 타입 정의
- [ ] `types/index.ts` — `ReminderList`, `Reminder` 인터페이스 정의

#### API Hooks
- [ ] `hooks/useLists.ts`
  - [ ] `useGetLists()` — `GET /api/lists`
  - [ ] `useCreateList()` — `POST /api/lists`
- [ ] `hooks/useReminders.ts`
  - [ ] `useGetReminders(listId)` — `GET /api/lists/{listId}/reminders`
  - [ ] `useCreateReminder(listId)` — `POST /api/lists/{listId}/reminders`

#### 레이아웃 & 컴포넌트
- [ ] `components/layout/AppLayout.tsx` — 3-panel 레이아웃 뼈대
- [ ] `components/layout/Sidebar.tsx` — 목록 리스트 렌더링 (API 연동)
- [ ] `components/reminder/ReminderListView.tsx` — 목록 클릭 시 리마인더 표시
- [ ] `store/uiStore.ts` — 선택된 listId 상태 관리 (Zustand)
- [ ] `app/page.tsx` — AppLayout 진입점

### Phase 1 완료 기준
- [ ] `./gradlew bootRun` 실행 후 API 응답 확인
- [ ] `pnpm dev` 실행 후 브라우저에서 목록 조회/생성 동작 확인
- [ ] 리마인더 추가 후 목록에 표시 확인

---

## Phase 2 — 핵심 CRUD + Apple 스타일 UI

### Backend

#### API 추가
- [ ] `ReminderListController` 확장
  - [ ] `PUT /api/lists/{id}` — 목록 수정
  - [ ] `DELETE /api/lists/{id}` — 목록 삭제
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

### Frontend

#### API Hooks 확장
- [ ] `useUpdateList()` — `PUT /api/lists/{id}`
- [ ] `useDeleteList()` — `DELETE /api/lists/{id}`
- [ ] `useUpdateReminder()` — `PUT /api/reminders/{id}`
- [ ] `useDeleteReminder()` — `DELETE /api/reminders/{id}`
- [ ] `useToggleComplete()` — `PATCH /api/reminders/{id}/complete`

#### 사이드바 UI (Apple Reminders 스타일)
- [ ] 스마트 목록 섹션
  - [ ] `SmartListIcon.tsx` — 컬러 원형 배경 + 아이콘 컴포넌트
  - [ ] 오늘 (파란 달력 아이콘), 예정됨 (빨간 달력), 전체 (회색 목록), 완료됨 (회색 체크), 플래그됨 (주황 깃발)
  - [ ] 항목 클릭 시 파란 하이라이트 선택 처리
- [ ] 나의 목록 섹션
  - [ ] 색상 원형 + 목록명 + 미완료 개수 배지
  - [ ] 목록 클릭 시 선택 처리
  - [ ] `+ 목록 추가` 버튼 (하단 고정)
- [ ] 사이드바 전체 배경: `#F2F2F7`, 텍스트: `#1C1C1E`

#### 리마인더 행 (`ReminderRow.tsx`)
- [ ] 원형 체크박스 (목록 색상 테두리/배경)
- [ ] 완료 클릭 시 체크 애니메이션 (0.3s) → 페이드아웃 후 완료 섹션 이동
- [ ] 제목 텍스트 (완료 시 `line-through` + 회색)
- [ ] 메모 미리보기 1줄 (`text-ellipsis overflow-hidden`)
- [ ] 마감일 표시 (기한 초과 시 빨간색)
- [ ] 우선순위 느낌표 (`!` / `!!` / `!!!`, 빨간색, 우측 정렬)
- [ ] 플래그 아이콘 (주황색, 우측)
- [ ] 호버 시 배경 미세 변경 + `...` 버튼 노출

#### 인라인 리마인더 추가 (`InlineReminderInput.tsx`)
- [ ] `+ 새 리마인더` 플레이스홀더 표시
- [ ] 클릭 시 즉시 input 포커스
- [ ] `Enter` → 저장 + 새 input 활성화 (연속 입력)
- [ ] `Escape` → 입력 취소

#### 완료 항목 섹션
- [ ] `▾ 완료됨` 헤더 클릭으로 접기/펼치기 토글
- [ ] 완료 항목 취소선 + 회색 텍스트

#### 목록 생성/수정 모달 (`ListFormModal.tsx`)
- [ ] 이름 입력 필드
- [ ] `ColorPicker.tsx` — 16색 팔레트 선택
  - [ ] Apple 기본 16색 정의 (Red, Orange, Yellow, Green, Mint, Teal, Cyan, Blue, Indigo, Purple, Pink, Brown, Gray 등)
  - [ ] 선택된 색상 체크 표시
- [ ] 색상 원형 아이콘 미리보기 (실시간)
- [ ] `취소` / `완료` 버튼
- [ ] 사이드바 `+ 목록 추가` 클릭 시 모달 오픈

### Phase 2 완료 기준
- [ ] Apple Reminders와 시각적으로 유사한 사이드바/목록 화면 확인
- [ ] 리마인더 체크 → 애니메이션 → 완료 섹션 이동 확인
- [ ] 목록 생성 → 색상 지정 → 사이드바 반영 확인

---

## Phase 3 — 상세 편집 패널 + 스마트 목록

### Backend

#### 스마트 목록 API
- [ ] `SmartListController.kt` 생성
- [ ] `PATCH /api/reminders/{id}/flag` — 플래그 토글
- [ ] `GET /api/smart/today` — 오늘 (dueDate ≤ 오늘, 미완료)
- [ ] `GET /api/smart/scheduled` — 예정됨 (dueDate > 오늘, 미완료)
- [ ] `GET /api/smart/all` — 전체 미완료
- [ ] `GET /api/smart/completed` — 완료됨
- [ ] `GET /api/smart/flagged` — 플래그됨
- [ ] `SmartListService.kt` — 각 필터 쿼리 구현 (JPQL 또는 Specification)

### Frontend

#### API Hooks 추가
- [ ] `useToggleFlag()` — `PATCH /api/reminders/{id}/flag`
- [ ] `useSmartList(type)` — 스마트 목록 타입별 API 조회

#### 상세 편집 패널 (`ReminderDetailPanel.tsx`)
- [ ] 우측 슬라이드인 애니메이션 (350px, `transform translateX`, CSS transition)
- [ ] 패널 외부 클릭 시 닫기
- [ ] 필드 구성 (iOS Settings 행 스타일 — 아이콘 + 레이블 + 값):
  - [ ] 제목 (대형 텍스트 입력, 자동 저장)
  - [ ] 메모 (멀티라인, 자동 저장)
  - [ ] 구분선
  - [ ] 마감일 (`<input type="date">` 커스텀 스타일)
  - [ ] 마감 시간 (`<input type="time">` 커스텀 스타일)
  - [ ] 반복 (드롭다운: 없음/매일/매주/매월/매년)
  - [ ] 구분선
  - [ ] 우선순위 (4단계 세그먼트 버튼: 없음 / ! / !! / !!!)
  - [ ] 목록 이동 (드롭다운, 목록 목록 나열)
  - [ ] 플래그 토글 (스위치 UI)
- [ ] 변경 시 디바운스 자동 저장 (500ms)
- [ ] `ReminderRow` 클릭 → 패널 오픈 + 선택 리마인더 하이라이트

#### 스마트 목록 뷰
- [ ] 사이드바 스마트 목록 클릭 → 해당 API 호출 + `ReminderListView` 렌더링
- [ ] 스마트 목록 뷰 타이틀 (오늘/예정됨/전체 등, 색상 적용)
- [ ] 예정됨 뷰: 날짜별 섹션 그룹핑 (오늘/내일/이번 주/다음 주)

#### 반응형 레이아웃
- [ ] `≥ 1024px` — 3-panel 고정 (사이드바 260px + 목록 + 상세패널 350px)
- [ ] `768~1023px` — 2-panel (상세패널 오버레이로 위에 표시)
- [ ] `< 768px` — 1-panel (사이드바 → 목록 → 상세 화면 전환)

### Phase 3 완료 기준
- [ ] 리마인더 클릭 → 상세 패널 슬라이드인 확인
- [ ] 상세 패널 수정 → 자동 저장 → 목록에 즉시 반영 확인
- [ ] 스마트 목록 5종 필터 결과 정확성 확인
- [ ] 모바일/태블릿/데스크탑 반응형 동작 확인

---

## Phase 4 — 고급 기능

### Backend

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

### Frontend

#### 드래그 정렬
- [ ] `@dnd-kit/core`, `@dnd-kit/sortable` 설치
- [ ] `ReminderListView` — 리마인더 드래그 정렬 적용
- [ ] 드롭 완료 시 `PATCH /api/reminders/reorder` 호출
- [ ] 사이드바 목록 드래그 정렬 + `PATCH /api/lists/reorder` 호출

#### 반복 설정
- [ ] 상세 패널 반복 드롭다운 — API 연동 저장
- [ ] 리마인더 행에 반복 아이콘 표시 (반복 설정된 경우)

#### 하위 리마인더
- [ ] `ReminderRow` 하위 리마인더 들여쓰기 (16px) 표시
- [ ] 리마인더 행 내 `+ 하위 항목 추가` 버튼 (호버 시 노출)
- [ ] 하위 리마인더 접기/펼치기

#### 검색 (`SearchOverlay.tsx`)
- [ ] 사이드바 검색창 클릭 → 전체화면 오버레이 활성화
- [ ] 검색어 입력 디바운스 300ms → `GET /api/search?q=` 호출
- [ ] 결과 렌더링: 리마인더 제목 + 목록명 + 색상 원형
- [ ] 결과 클릭 → 해당 목록 이동 + 상세 패널 오픈
- [ ] `Escape` → 오버레이 닫기

#### 컨텍스트 메뉴 (`ContextMenu.tsx`)
- [ ] 리마인더 행 우클릭 → 포지션 계산 후 메뉴 표시
- [ ] 메뉴 항목: 삭제 / 플래그 토글 / 마감일 설정 / 목록 이동
- [ ] 외부 클릭 → 메뉴 닫기

### Phase 4 완료 기준
- [ ] 드래그로 리마인더 순서 변경 + 서버 반영 확인
- [ ] 검색어 입력 → 실시간 결과 표시 확인
- [ ] 반복 설정 저장 → 행에 아이콘 표시 확인
- [ ] 컨텍스트 메뉴 동작 확인

---

## Phase 5 — 다크모드 + 마무리

### Backend

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

### Frontend

#### 다크모드
- [ ] TailwindCSS `dark:` 클래스 전략 설정 (`class` 기반)
- [ ] `prefers-color-scheme` 자동 감지 + 수동 토글 (헤더 버튼)
- [ ] 다크 팔레트 적용
  - [ ] 사이드바: `#1C1C1E`
  - [ ] 메인 배경: `#2C2C2E`
  - [ ] 텍스트: `#FFFFFF` / `#EBEBF5`
  - [ ] 구분선: `#3A3A3C`
- [ ] 다크모드 상태 localStorage 저장

#### 접근성
- [ ] 키보드 탐색 (Tab → 사이드바 → 목록 → 리마인더 행)
- [ ] `Enter`로 리마인더 활성화, `Escape`로 패널 닫기
- [ ] 모든 버튼/아이콘에 `aria-label` 추가
- [ ] `role`, `aria-selected`, `aria-expanded` 적절히 설정

#### UX 마무리
- [ ] 빈 상태 UI — 목록에 리마인더 없을 때 "리마인더 없음" 안내 문구
- [ ] 로딩 스켈레톤 UI (목록/리마인더 로딩 중)
- [ ] 에러 토스트 알림 (API 실패 시)
- [ ] 목록 이름 더블클릭 → 인라인 편집 모드
- [ ] 리마인더 삭제 시 `Undo` 스낵바 (3초 내 취소 가능)
- [ ] 페이지 타이틀 동적 변경 (`<title>` 현재 목록명)

### Phase 5 완료 기준
- [ ] 다크모드 토글 → 전체 UI 색상 전환 확인
- [ ] 키보드만으로 리마인더 추가/완료/삭제 가능 확인
- [ ] API 오류 시 토스트 표시 확인
- [ ] 로딩/빈 상태 UI 표시 확인

---

## 전체 진행 현황

| Phase | 설명 | 진행 |
|-------|------|------|
| Phase 1 | 프로젝트 기반 구축 | 🔲 |
| Phase 2 | 핵심 CRUD + Apple UI | 🔲 |
| Phase 3 | 상세 패널 + 스마트 목록 | 🔲 |
| Phase 4 | 고급 기능 | 🔲 |
| Phase 5 | 다크모드 + 마무리 | 🔲 |
