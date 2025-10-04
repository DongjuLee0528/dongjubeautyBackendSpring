🧴 DongjuBeauty

개인 퍼스널 컬러 분석을 위한 Spring Boot 기반 웹 서비스입니다.
이미지를 업로드하면 Python API와 연동하여 퍼스널 컬러를 분석하고, 그 결과를 웹에서 제공합니다.

📌 프로젝트 개요

Spring Boot와 Python API를 연동하여 퍼스널 컬러 자동 분석 서비스를 제공합니다.

REST API 기반으로 프론트엔드와 통신할 수 있도록 설계되어 있습니다.

Gradle을 사용한 빌드 환경으로 구성되어 있으며, Spring의 RestClient를 통해 외부 API와 통신합니다.

🛠 기술 스택
구분	기술
Backend Framework	Spring Boot
Language	Java 17
Build Tool	Gradle
External API	Python API (퍼스널 컬러 분석)
Configuration	YAML/Properties 기반 설정
기타	RestClient, DTO 구조화, Utility 클래스
📂 디렉토리 구조
dongjubeauty/
├── build.gradle
├── settings.gradle
├── gradle/
├── src/
│   ├── main/
│   │   ├── java/com/example/dongjubeauty/
│   │   │   ├── DongjubeautyApplication.java  # Spring Boot 실행 진입점
│   │   │   ├── config/
│   │   │   │   ├── PythonApiProperties.java  # Python API 설정
│   │   │   │   └── RestClientConfig.java     # RestClient 설정
│   │   │   ├── dto/
│   │   │   │   └── AnalyzeRequest.java       # 분석 요청 DTO
│   │   │   ├── service/
│   │   │   │   └── PersonalColorService.java # 퍼스널 컬러 분석 로직
│   │   │   ├── web/
│   │   │   │   └── PersonalColorController.java # REST API 엔드포인트
│   │   │   └── util/
│   │   │       └── LocalizationUtils.java    # 로컬라이징 유틸
│   │   └── resources/
│   └── test/

🚀 실행 방법
1️⃣ 프로젝트 클론
git clone <repository-url>
cd dongjubeauty

2️⃣ Gradle 빌드
./gradlew build

3️⃣ 애플리케이션 실행
./gradlew bootRun


혹은

java -jar build/libs/dongjubeauty-0.0.1-SNAPSHOT.jar

🌐 API 개요
엔드포인트	메서드	설명
/personal-color/analyze	POST	이미지 업로드 후 퍼스널 컬러 분석 요청

AnalyzeRequest DTO를 통해 Python API로 전달할 데이터를 구성합니다.

PersonalColorService에서 RestClient를 통해 Python API 호출 → 결과 반환

🧰 주요 클래스 설명

DongjubeautyApplication.java
→ Spring Boot 애플리케이션 시작점

PersonalColorController.java
→ 퍼스널 컬러 분석 요청을 처리하는 REST 컨트롤러

PersonalColorService.java
→ Python API 연동 및 비즈니스 로직 처리

RestClientConfig.java / PythonApiProperties.java
→ API 통신 설정 및 환경변수 주입 담당

LocalizationUtils.java
→ 분석 결과를 사용자의 언어 환경에 맞게 변환

📝 기타

.gitignore가 포함되어 있어 불필요한 파일은 Git에 업로드되지 않습니다.

HELP.md 파일은 Spring 초기 템플릿에서 제공되는 가이드입니다.
