#!/bin/sh
# -----------------------
# Sign APK or AAB
# -----------------------

APKSIGNER="/data/data/com.itsaky.androidide/files/home/android-sdk/build-tools/36.0.0/apksigner"
JARSIGNER="jarsigner"
KEYSTORE="/storage/emulated/0/AndroidIDEProjects/TikTokCoinApp/app/hrm.keystore"
KEYPASS="123456"
ALIAS="hrm"

# -----------------------
# Take file path from arg or prompt
# -----------------------
if [ -n "$1" ]; then
  INPUT="$1"
else
  printf "Enter the full path to the APK or AAB: "
  read INPUT
fi

# -----------------------
# Clean extension and base name
# -----------------------
EXT="${INPUT##*.}"
BASENAME="${INPUT%.*}"

# remove 'unsigned'
CLEAN_BASENAME=$(echo "$BASENAME" | sed 's/-\?unsigned//g')

OUTSIGNED="${CLEAN_BASENAME}-signed.${EXT}"

case "$EXT" in
  apk|APK)
    echo ">>> Signing APK..."
    TEMPAPK="${CLEAN_BASENAME}-temp.apk"
    cp "$INPUT" "$TEMPAPK"

    "$APKSIGNER" sign \
      --ks "$KEYSTORE" \
      --ks-key-alias "$ALIAS" \
      --ks-pass pass:$KEYPASS \
      --key-pass pass:$KEYPASS \
      --v1-signing-enabled true \
      --v2-signing-enabled true \
      --v3-signing-enabled true \
      --v4-signing-enabled true \
      --out "$OUTSIGNED" "$TEMPAPK"

    echo ">>> Verifying APK..."
    "$APKSIGNER" verify --verbose "$OUTSIGNED"

    rm -f "$TEMPAPK" "${OUTSIGNED}.idsig"
    ;;
  aab|AAB)
    echo ">>> Signing AAB..."
    "$JARSIGNER" \
      -keystore "$KEYSTORE" \
      -storepass "$KEYPASS" \
      -keypass "$KEYPASS" \
      -signedjar "$OUTSIGNED" \
      "$INPUT" "$ALIAS"

    echo ">>> Verifying AAB..."
    "$JARSIGNER" -verify "$OUTSIGNED"
    ;;
  *)
    echo "Error: file is not APK or AAB!"
    exit 1
    ;;
esac

echo ">>> Done! Final signed file: $OUTSIGNED"