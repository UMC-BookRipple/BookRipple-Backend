<img width="382" height="96" alt="로고" src="https://github.com/user-attachments/assets/fc74f414-bec9-4947-9289-f8746b9cc28d" />
# BookRipple-Backend

BookRipple(북리플) 은 “독서를 기록하고 나누며, 질문과 연결을 통해 책 너머의 경험을 확장하는 서비스”입니다.

백엔드는 독서 기록/질문/커뮤니티 기능과 함께, 중고 도서 거래 및 결제 흐름을 안정적으로 처리하는 API 서버를 제공합니다.

### 핵심 기능

📖 Read: 독서 범위/시간/감상평 기록 및 개인 독서 이력 조회

❓ Question: 완독 시 Gemini API 기반 연관 질문 3개 자동 생성 및 저장/조회

🗣 Ask: 독서 중 직접 질문 등록, 답변 조회

✍️ Share: 감상평/추천글 작성

🌱 Community: 인상 깊은 구절을 통한 블라인드 도서 판매/구매 연결

## 사용한 브랜치 전략
1. main-develop-feature 순으로 최상위 branch
2. 이슈 템플릿 활용해서 이슈 생성
3. 생성된 이슈 번호에 맞게 브랜치 생성
4. 브랜치에서 작업 후 commit, push
5. PR 템플릿 활용해서 develop 브랜치로 PR
    - 2명 이상 approve
    - PR 후 팀원에게 공지
6. merge시 자동으로 브랜치 사라짐, 다시 이슈부터 시작

## 기술 스택
### Backend
* Java 17
* Spring Boot 3.5.9
* Spring Data JPA / Hibernate
* Spring Security + JWT 기반 인증/인가
### Database
* MySQL 8.0
### Infra / DevOps
* AWS
* Docker
### External APIs
* 알라딘 API: 도서 정보 조회
* Gemini API: 질문 자동 생성
### Project Conventions
* API Prefix: /api/v1
* Success Response: BaseSuccessCode + ApiResponse.onSuccess(code, result)
* Error Handling: ApiException(BaseErrorCode) + GlobalExceptionHandler
* Auth Header: Authorization: Bearer <token>


## 프로젝트 구조
```
src/main
src/main/java/com
└── bookripple
    └── api
        ├── ApiApplication.java      # Spring Boot Application Entry Point
        ├── domain            # 도메인 별 기능 모듈
        │   ├── blindsalepost
        │   ├── book
        │   ├── library
        │   ├── member
        │   ├── memo
        │   ├── notification
        │   ├── order
        │   ├── question
        │   ├── reading
        │   ├── recommendation
        │   ├── review
        │   └── verification
        ├── global            # 전역 공통 모듈
        │   ├── annotation
        │   ├── aop
        │   ├── auth
        │   ├── code            # 성공/에러 코드 (BaseSuccessCode, BaseErrorCode 등)
        │   ├── config
        │   ├── controller         # 공통/테스트용 컨트롤러
        │   ├── converter
        │   ├── dto
        │   ├── entity            # BaseEntity 공통 엔티티
        │   ├── error            # 예외 처리 (ApiException, GlobalExceptionHandler 등)
        │   ├── response
        │   ├── security         # 보안 설정
        │   ├── service
        │   └── validation
        └── infrastructure         # 외부 시스템 연동 및 통신 (외부 API 및 이메일 검증)
            ├── ai
            ├── aladin
            └── email
```

## 팀원 및 주요 개발 사항
* **고준섭** - 알림 관련 API 구현, AWS/DOCKER 기반의 배포 환경 및 CI/CD 파이프라인 구축, 문서화 작업
* **박인영** - 마이페이지 관련 API 구현, 인증/인가 (JWT/Security), AWS SES 기반 이메일 인증
* **오영훈** - 도서 관련 API 구현, Gemini API 연동 및 프롬프트 작성, 쿼리 최적화, Caffeine 기반 캐시 시스템 구축
* **조현정** - 도서 관련 API 구현, 알라딘 API 연동 및 도서 도서 정보 DB 저장, Swagger 정리 및 리펙토링
* **최서준** - 커뮤니티 관련 API 구현, 블라인드 도서 관련 API 구현, 도메인 및 ERD 모델링



## 서버 아키텍처
<img width="943" height="551" alt="image" src="https://github.com/user-attachments/assets/40115358-5720-403f-9c52-118fdd01dbaf" />

* Database 보안 격리 (Private Subnet)
* Keyless Access (IAM Role)
* HTTPS 구성 (Route53-ALB, ACM)
* CloudWatch 기반 로그 관측
