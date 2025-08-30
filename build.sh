#!/bin/sh
# Build helper for Gradle projects (Interactive Debug/Release/Both)

set -eu

# Detect project root and gradlew
PROJECT_PATH="$(cd "$(dirname "$0")" && pwd)"
GRADLEW="$PROJECT_PATH/gradlew"

if [ ! -f "$GRADLEW" ]; then
  echo "⏭ No gradlew found in this directory."
  exit 1
fi

chmod +x "$GRADLEW"

echo "Select build type:"
echo "1) Release"
echo "2) Debug"
echo "3) Both (Release + Debug)"
printf "➡ Your choice [1/2/3]: "
read -r CHOICE

case "$CHOICE" in
  1)
    BUILD_TYPE="release"
    TASK="assembleRelease"
    ;;
  2)
    BUILD_TYPE="debug"
    TASK="assembleDebug"
    ;;
  3)
    BUILD_TYPE="all"
    TASK="assembleRelease assembleDebug"
    ;;
  *)
    echo "❌ Invalid choice. Please enter 1, 2 or 3."
    exit 1
    ;;
esac

# Performance flags (daemon can be toggled via env)
if [ "${GRADLE_NO_DAEMON:-0}" = "1" ]; then
  DAEMON_FLAG="--no-daemon"
else
  DAEMON_FLAG="--daemon"
fi

DEFAULT_FLAGS="--parallel --max-workers=4 $DAEMON_FLAG"

# Forward extra args if any
EXTRA_OPTS="$*"

printf "\n📦 Building project (%s): %s\n" "$BUILD_TYPE" "$(basename "$PROJECT_PATH")"
# Run Gradle
# shellcheck disable=SC2086
sh "$GRADLEW" $TASK $DEFAULT_FLAGS $EXTRA_OPTS && \
  echo "✅ Build successful ($BUILD_TYPE)" || \
  echo "❌ Build failed ($BUILD_TYPE)"