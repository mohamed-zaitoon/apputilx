#!/usr/bin/env python3
import os, shutil, stat, time

def _handle_remove_readonly(func, path, exc_info):
    # جرّب تفتح الإذن وبعدين أعد المحاولة مرة واحدة
    try:
        os.chmod(path, stat.S_IWRITE)
        func(path)
    except PermissionError:
        print(f"⚠️ Locked, skipping: {path}")

def remove_dir(path):
    if os.path.isdir(path):
        for attempt in range(3):
            try:
                shutil.rmtree(path, onerror=_handle_remove_readonly)
                print(f"Removed: {path}")
                return
            except PermissionError:
                time.sleep(0.5)
        print(f"⚠️ Some files were locked. Skipped parts of: {path}")

def remove_file(path):
    if os.path.isfile(path):
        try:
            os.chmod(path, stat.S_IWRITE)
            os.remove(path)
            print(f"Removed: {path}")
        except PermissionError:
            print(f"⚠️ Locked, skipping: {path}")

def find_and_remove_iml_files():
    for root, _, files in os.walk('.'):
        for file in files:
            if file.endswith('.iml'):
                remove_file(os.path.join(root, file))

def remove_dot_dirs():
    for entry in os.listdir('.'):
        if entry.startswith('.') and os.path.isdir(entry) and entry != '.git':
            remove_dir(entry)

def main():
    # الأفضل تنظيف Gradle أولًا
    # (بديل: شغّل .\gradlew clean قبل السكربت)
    remove_dir('.gradle')
    remove_dir('.kotlin')
    remove_dir('build')
    remove_dir('app/.cxx')
    remove_dir('app/build')
    remove_dir('.idea')
    remove_file('local.properties')
    remove_file("version.properties")
    remove_file("build.log")
    find_and_remove_iml_files()
    remove_dot_dirs()
    print("Clean completed.")

if __name__ == '__main__':
    main()
