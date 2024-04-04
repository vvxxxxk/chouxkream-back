#!/bin/bash

# Docker Compose를 사용하여 컨테이너 중지 및 삭제
echo "Docker Compose를 사용하여 컨테이너를 중지하고 삭제합니다..."
if docker compose down -v; then
  echo "컨테이너 중지 및 삭제가 성공적으로 완료되었습니다."
else
  echo "컨테이너 중지 및 삭제 과정에서 오류가 발생했습니다."
  exit 1
fi

# 특정 Docker 이미지 제거
IMAGE_NAME="kream"
echo "$IMAGE_NAME 이미지를 제거합니다..."
if docker rmi "$IMAGE_NAME"; then
  echo "$IMAGE_NAME 이미지 제거가 성공적으로 완료되었습니다."
else
  echo "$IMAGE_NAME 이미지 제거 과정에서 오류가 발생했습니다."
  exit 1
fi

# Docker Compose를 사용하여 컨테이너 다시 실행
echo "Docker Compose를 사용하여 컨테이너를 다시 실행합니다..."
if docker compose up -d; then
  echo "컨테이너가 성공적으로 다시 실행되었습니다."
else
  echo "컨테이너를 다시 실행하는 과정에서 오류가 발생했습니다."
  exit 1
fi

echo "모든 과정이 성공적으로 완료되었습니다."
