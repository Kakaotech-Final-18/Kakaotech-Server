#!/bin/bash

#####################
# 1. 권한추가 
# chmod +x build.sh

# 2. 실행
# ./build.sh

# 필요시 아래 파일 수정 후 main에 바로 push
# Parrotalk-Manifests/apps/auth
# 공통: base/configmap.yaml
# 환경별: overlays/[dev/prod]/configmap-patch.yaml 

# 3. argocd 들어가서 deployment > restart
#####################

set -e  # 오류 발생 시 스크립트 종료

# 현재 브랜치 확인
BRANCH=$(git rev-parse --abbrev-ref HEAD)

if [[ "$BRANCH" != "main" && "$BRANCH" != "develop" ]]; then
    echo "현재 브랜치는 main 또는 develop이 아닙니다. 스크립트를 종료합니다."
    exit 1
fi

echo "현재 브랜치는 '$BRANCH'입니다."
read -p "계속 진행하시겠습니까? (y/n): " CONTINUE

if [[ "$CONTINUE" != "y" ]]; then
    echo "작업을 취소했습니다."
    exit 1
fi

# 태그 설정
if [[ "$BRANCH" == "main" ]]; then
    TAG="ptk-be-prod"
else
    TAG="ptk-be-dev"
fi

# ECR 로그인
echo "ECR 로그인 중..."
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin 703671911294.dkr.ecr.ap-northeast-2.amazonaws.com

# buildx 설정
echo "Docker buildx 설정 중..."
docker buildx create --use || { echo "Docker buildx 생성에 실패했습니다."; exit 1; }

# 이미지 빌드 및 푸시
echo "이미지 빌드 및 푸시 중..."
docker buildx build \
 --platform linux/amd64,linux/arm64 \
 -t 703671911294.dkr.ecr.ap-northeast-2.amazonaws.com/ptk-dev-ecr-argocd:$TAG \
 -f Dockerfile \
 --push \
 . || { echo "이미지 빌드 및 푸시에 실패했습니다."; exit 1; }

echo "이미지 빌드 및 푸시 완료. 태그: $TAG"
