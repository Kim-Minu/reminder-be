# 개발 계획 — Apple Reminders Web Version

> spec.md 기반 단계적 개발 계획.
> 각 Phase는 독립적으로 동작 가능한 상태로 완성한다.

---

## 기술 스택

### Backend
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

### Frontend
| 항목 | 기술 | 버전 |
|------|------|------|
| 프레임워크 | Next.js (App Router) | 15 |
| 언어 | TypeScript | 5.x |
| 스타일 | TailwindCSS | v4 |
| 서버 상태 | TanStack Query (React Query) | v5 |
| 클라이언트 상태 | Zustand | v5 |
| 아이콘 | Lucide React | latest |
| 날짜 | date-fns | v4 |
| 드래그 정렬 | @dnd-kit/core | latest |
| HTTP 클라이언트 | axios | latest |
| 테스트 | Jest + React Testing Library | - |

### 개발 환경
| 항목 | 내용 |
|------|------|
| JDK | 17 |
| Node.js | 20 LTS |
| 패키지 매니저 | pnpm |
| API 포트 | Backend `:8080`, Frontend `:3000` |
| CORS | Spring CORS 설정으로 `localhost:3000` 허용 |

---

## Phase 1 — 프로젝트 기반 구축

> 목표: 프론트-백엔드 연결 확인, Hello World 수준의 동작

### Backend
- [x] Spring Boot + Kotlin + JPA/H2 프로젝트 생성 ✅ (완료)
- [ ] CORS 설정 (`WebMvcConfigurer`)
- [ ] `ReminderList` 엔티티 + 테이블 생성
- [ ] `Reminder` 엔티티 + 테이블 생성
- [ ] `GET /api/lists` — 목록 조회 API
- [ ] `POST /api/lists` — 목록 생성 API
- [ ] `GET /api/lists/{listId}/reminders` — 리마인더 조회 API
- [ ] `POST /api/lists/{listId}/reminders` — 리마인더 생성 API

### Frontend
- [ ] Next.js 15 프로젝트 생성 (`/workspace/frontend`)
- [ ] TailwindCSS v4 설정
- [ ] axios 인스턴스 설정 (`/lib/api.ts`)
- [ ] TanStack Query Provider 설정
- [ ] 3-panel 레이아웃 뼈대 (`Sidebar` / `ReminderListView` / `DetailPanel`)
- [ ] 사이드바에 목록 리스트 렌더링 (API 연동)
- [ ] 목록 클릭 시 리마인더 목록 표시 (API 연동)

### 완료 기준
- 브라우저에서 목록을 생성하고 리마인더를 추가할 수 있다.
- 새로고침 후에도 데이터가 유지된다 (H2 in-memory 범위 내).

---

## Phase 2 — 핵심 CRUD + Apple 스타일 UI

> 목표: Apple Reminders의 기본 외형과 핵심 동작 완성

### Backend
- [ ] `PUT /api/lists/{id}` — 목록 수정
- [ ] `DELETE /api/lists/{id}` — 목록 삭제
- [ ] `PUT /api/reminders/{id}` — 리마인더 수정
- [ ] `DELETE /api/reminders/{id}` — 리마인더 삭제
- [ ] `PATCH /api/reminders/{id}/complete` — 완료 토글
- [ ] `Reminder` 필드 확장: `notes`, `isFlagged`, `priority`, `dueDate`, `dueTime`

### Frontend — UI 구현 (Apple Reminders 동일)
- [ ] 사이드바 스타일링
  - 스마트 목록 (오늘/예정됨/전체/완료됨/플래그됨) + 컬러 원형 아이콘
  - 나의 목록: 색상 원형 + 목록명 + 미완료 배지
  - 선택 시 파란 하이라이트
- [ ] 리마인더 행 (`ReminderRow`)
  - 원형 체크박스 (목록 색상)
  - 완료 체크 시 페이드아웃 애니메이션
  - 메모 미리보기 (1줄 말줄임)
  - 기한 초과 날짜 빨간색 표시
  - 우선순위 느낌표 (`!` / `!!` / `!!!`)
  - 플래그 아이콘 (주황색)
- [ ] 인라인 리마인더 추가 입력 (`InlineReminderInput`)
  - Enter → 저장 + 다음 입력 즉시 시작
  - Escape → 취소
- [ ] 완료 항목 섹션 (`▾ 완료됨`) 접기/펼치기
- [ ] 목록 생성 모달 (`ListFormModal`)
  - 이름 입력 + 16색 팔레트 색상 선택
  - 색상 원형 미리보기
- [ ] 색상 팔레트 컴포넌트 (`ColorPicker`)

### 완료 기준
- Apple Reminders와 시각적으로 유사한 UI
- 리마인더 추가/완료/삭제 동작
- 목록 생성/삭제 동작

---

## Phase 3 — 상세 편집 패널 + 스마트 목록

> 목표: 우측 상세 패널 완성, 스마트 목록 필터 뷰

### Backend
- [ ] `PATCH /api/reminders/{id}/flag` — 플래그 토글
- [ ] `GET /api/smart/today` — 오늘 (마감일 = 오늘 또는 기한 초과, 미완료)
- [ ] `GET /api/smart/scheduled` — 예정됨 (마감일 > 오늘, 미완료)
- [ ] `GET /api/smart/all` — 전체 미완료
- [ ] `GET /api/smart/completed` — 완료됨
- [ ] `GET /api/smart/flagged` — 플래그됨

### Frontend
- [ ] 상세 편집 패널 (`ReminderDetailPanel`)
  - 우측 슬라이드인 (350px, CSS transition)
  - 제목 / 메모 / 마감일 / 마감시간 / 우선순위 / 목록 / 플래그 / 반복 필드
  - iOS Settings 스타일 행 (아이콘 + 레이블 + 값)
  - 날짜 피커 / 시간 피커
  - 우선순위 세그먼트 선택 UI
- [ ] 스마트 목록 뷰 (사이드바 항목 클릭 시 해당 API 연동)
- [ ] 반응형 레이아웃
  - `≥ 1024px`: 3-panel
  - `768~1023px`: 2-panel (상세패널 오버레이)
  - `< 768px`: 1-panel (순차 이동)

### 완료 기준
- 리마인더 클릭 시 상세 패널이 슬라이드인
- 스마트 목록 5종 전부 올바른 필터링

---

## Phase 4 — 고급 기능

> 목표: 드래그 정렬, 반복, 하위 리마인더, 검색, 컨텍스트 메뉴

### Backend
- [ ] `PATCH /api/lists/reorder` — 목록 순서 변경
- [ ] `PATCH /api/reminders/reorder` — 리마인더 순서 변경
- [ ] `recurrence` 필드 처리 (NONE / DAILY / WEEKLY / MONTHLY / YEARLY)
- [ ] 하위 리마인더 (`parentId`) 조회/생성
- [ ] `GET /api/search?q={keyword}` — 제목 + 메모 전문 검색

### Frontend
- [ ] 드래그 정렬 (`@dnd-kit`) — 목록 내 리마인더 순서 변경
- [ ] 반복 설정 드롭다운 (상세 패널)
- [ ] 하위 리마인더 들여쓰기 표시 + 추가
- [ ] 검색 오버레이 (`SearchOverlay`)
  - 사이드바 검색창 클릭 시 전체화면 오버레이
  - 실시간 디바운스 검색 (300ms)
  - 결과에 목록명 함께 표시
- [ ] 컨텍스트 메뉴 (`ContextMenu`) — 우클릭
  - 삭제 / 플래그 토글 / 마감일 설정 / 목록 이동

### 완료 기준
- 드래그로 순서 변경 가능
- 검색으로 리마인더 즉시 탐색
- 반복 설정 저장/표시

---

## Phase 5 — 다크모드 + 마무리

> 목표: 완성도 향상, 다크모드, 접근성

### Frontend
- [ ] 다크모드 (`prefers-color-scheme` 자동 감지 + 수동 토글)
  - 사이드바: `#1C1C1E`, 메인: `#2C2C2E` 등 Apple 다크 팔레트 적용
- [ ] 키보드 접근성 (Tab 이동, Enter 활성화, Esc 닫기)
- [ ] ARIA 레이블 추가
- [ ] 빈 상태 UI (목록에 리마인더 없을 때 안내 문구)
- [ ] 에러 상태 처리 (API 실패 시 토스트 알림)
- [ ] 로딩 스켈레톤 UI
- [ ] 목록 이름 더블클릭 인라인 편집

### Backend
- [ ] 전역 예외 핸들러 (`@ControllerAdvice`)
- [ ] 입력값 검증 (`@Valid`, Bean Validation)
- [ ] API 응답 포맷 통일 (`ApiResponse<T>` 래퍼)

### 완료 기준
- 다크모드 전환 동작
- 키보드만으로 전체 기능 사용 가능
- 에러/로딩 상태 모두 처리됨

---

## 디렉토리 구조 (예정)

```
workspace/
├── spec.md
├── plan.md
├── build.gradle.kts          # Backend (Spring Boot)
├── src/
│   └── main/kotlin/com/example/demo/
│       ├── entity/
│       │   ├── ReminderList.kt
│       │   └── Reminder.kt
│       ├── repository/
│       ├── service/
│       ├── controller/
│       └── config/
│           └── WebConfig.kt   # CORS
└── frontend/                  # Next.js 프로젝트
    ├── app/
    │   ├── layout.tsx
    │   ├── page.tsx
    │   └── providers.tsx
    ├── components/
    │   ├── layout/
    │   │   ├── AppLayout.tsx
    │   │   └── Sidebar.tsx
    │   ├── reminder/
    │   │   ├── ReminderListView.tsx
    │   │   ├── ReminderRow.tsx
    │   │   ├── ReminderDetailPanel.tsx
    │   │   └── InlineReminderInput.tsx
    │   └── ui/
    │       ├── ColorPicker.tsx
    │       ├── ContextMenu.tsx
    │       └── ListFormModal.tsx
    ├── lib/
    │   ├── api.ts
    │   └── utils.ts
    ├── hooks/
    │   ├── useLists.ts
    │   └── useReminders.ts
    └── store/
        └── uiStore.ts          # Zustand (선택 상태, 패널 열림 등)
```

---

## Phase 진행 상태

| Phase | 내용 | 상태 |
|-------|------|------|
| Phase 1 | 프로젝트 기반 구축 | 🔲 진행 전 |
| Phase 2 | 핵심 CRUD + Apple UI | 🔲 진행 전 |
| Phase 3 | 상세 패널 + 스마트 목록 | 🔲 진행 전 |
| Phase 4 | 고급 기능 (검색, 드래그, 반복) | 🔲 진행 전 |
| Phase 5 | 다크모드 + 마무리 | 🔲 진행 전 |
