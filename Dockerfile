# ✅ 1단계: Gradle을 사용하여 애플리케이션 JAR 파일을 빌드하는 단계
FROM eclipse-temurin:17-jdk AS build

# Docker 컨테이너 내의 작업 디렉토리 설정
WORKDIR /app

# Gradle 캐시를 최적화하고 빌드 속도를 높이기 위해
# 빌드 스크립트와 Gradle Wrapper 관련 파일들을 먼저 복사
COPY build.gradle settings.gradle gradlew /app/
COPY gradle /app/gradle

# Gradle 의존성만 먼저 다운로드하고 캐싱
# 의존성 변경이 없을 경우 이 단계는 Docker 레이어 캐시를 활용하여 빠르게 진행
# '|| true'는 의존성 다운로드 실패 시에도 빌드를 계속 진행 (초기 설정용).
RUN ./gradlew dependencies --no-daemon || true

# 애플리케이션의 모든 소스 코드를 작업 디렉토리로 복사
COPY . /app

# [빌드 타임 ARG] GitHub 패키지 인증 정보를 외부에서 받아오기 (docker-compose.yml → Dockerfile)
# backend-ci.yml에서 build-args로 전달받을 ARG를 활성화
ARG GH_PACKAGES_USER
ARG GH_PACKAGES_TOKEN

# [런타임 ENV] Gradle에서 System.getenv(...)로 읽을 수 있도록 환경변수로 설정
# 이 ENV를 통해 Gradle이 ARG로 받은 값을 환경 변수로 인식
ENV GH_PACKAGES_USER=$GH_PACKAGES_USER
ENV GH_PACKAGES_TOKEN=$GH_PACKAGES_TOKEN

# PAT 값을 로그에 그대로 출력하면 토큰 유출 위험 - 주석 처리
#RUN echo "🔎 GH_PACKAGES_USER=$GH_PACKAGES_USER" && \
#    echo "🔑 GH_PACKAGES_TOKEN=$GH_PACKAGES_TOKEN"

# Dockerfile을 BuildKit secrets와 함께 사용하도록 준비 (주석 처리)
# RUN --mount=type=secret,id=gh_user,target=/run/secrets/gh_user \
#     --mount=type=secret,id=gh_token,target=/run/secrets/gh_token \
#     export GH_PACKAGES_USER=$(cat /run/secrets/gh_user) && \
#     export GH_PACKAGES_TOKEN=$(cat /run/secrets/gh_token) && \
#     ./gradlew clean bootJar --no-daemon


# Spring Boot 애플리케이션의 실행 가능한 JAR 파일을 빌드
# '--no-daemon' 옵션은 Docker 빌드 환경에서 Gradle 데몬 사용을 방지
# 이제 ARG/ENV를 통해 인증 정보를 받으므로 이 명령어를 사용
RUN ./gradlew clean bootJar --no-daemon

# ✅ 2단계: 최적화된 실제 실행 환경을 구성하는 단계
# 가볍고 보안에 유리한 Alpine Linux 기반의 JDK 이미지를 사용
FROM eclipse-temurin:17-jdk-alpine

# Docker 컨테이너 내의 작업 디렉토리 설정
WORKDIR /app

# 빌드 단계에서 생성된 JAR 파일만 최종 이미지로 복사
# 이렇게 함으로써 최종 이미지에 불필요한 빌드 도구나 소스 코드가 포함되지 않아 이미지가 가벼워짐
COPY --from=build /app/build/libs/*.jar app.jar

# 애플리케이션이 사용할 기본 네트워크 포트(8080)를 외부에 노출하도록 명시
# (실제 외부 접근을 위해서는 'docker run -p' 또는 docker-compose.yml의 'ports' 설정이 필요)
EXPOSE 8080

# 컨테이너 시작 시 실행될 명령어를 정의
# 'sh -c'를 사용하여 JAVA_OPTS 환경 변수를 통해 JVM 설정을 동적으로 주입할 수 있도록 함
# 예: docker run -e "JAVA_OPTS=-Xmx512m" ...
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]