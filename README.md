# 탈출록
방탈출 후기를 작성하고 공유하는 커뮤니티 웹 애플리케이션

### 핵심 기능
- 회원가입 및 로그인
- 방탈출 후기 게시판 (댓글, 좋아요)
- 팔로우 및 알림

### 기술 스택
- frontend : Vue.js
- backend : Springboot 3.x, JPA, Redis, MariaDB, Jwt, Swagger(spring-doc), docker

### 인증 및 보안
- JWT 기반 인증 구조
- RefreshToken 및 BlackList 저장(Redis)
- Role 기반 접근 제어

### 브랜치 전략
- git flow 사용
- main : 출시 된 branch
- feature : 기능별 개발 branch
- develop : 다음에 배포할 개발 branch
- release : 배포를 위한 버그 수정 및 QA branch
- hotfix : 배포한 버전 긴급 수정 branch

### commit 규칙
- Feat : 새로운 기능에 대한 커밋
- Fix : 버그 수정에 대한 커밋
- Build : 빌드(패키징) 관련 파일 수정에 대한 커밋
- Chore : 그 외 자잘한 수정에 대한 커밋
- Docs : 문서 수정에 대한 커밋
- Style : 코드 스타일 혹은 포맷 등에 관한 커밋
- Refactor : 리팩토링에 대한 커밋
- Test : 테스트 코드 추가 및 수정 커밋
 
