#!/bin/bash
# -----------------------
# Paths and signing configuration
# -----------------------
APKSIGNER="/data/data/com.itsaky.androidide/files/home/android-sdk/build-tools/36.0.0/apksigner"
KEYSTORE="/storage/emulated/0/AndroidIDEProjects/apputils/app/appkey.jks"
KEYPASS="123456"
ALIAS="hrm"

echo "Enter the full path to the APK:"
read APK

# -----------------------
# Copy APK to a temporary file for cleaning
# -----------------------
TEMPAPK="${APK%.*}-unsigned-clean.apk"
cp "$APK" "$TEMPAPK"


# -----------------------
# Output APK path
# -----------------------
OUTAPK="${APK%.*}-signed.apk"

# -----------------------
# Sign the APK
# -----------------------
$APKSIGNER sign   --ks "$KEYSTORE"   --ks-key-alias "$ALIAS"   --ks-pass pass:$KEYPASS   --key-pass pass:$KEYPASS   --v1-signing-enabled true   --v2-signing-enabled true   --v3-signing-enabled true   --v4-signing-enabled true   --out "$OUTAPK" "$TEMPAPK"

echo
echo "Verifying signed APK:"
$APKSIGNER verify --verbose "$OUTAPK"

# -----------------------
# Clean up temporary and old files
# -----------------------
rm -f "$APK" "$TEMPAPK"
rm -f "${OUTAPK}.idsig"

echo
echo "✅ Done! Final signed APK: $OUTAPK"
