#!/usr/bin/env python3
import sys
import subprocess
import os
import platform
from datetime import datetime, timezone

DEFAULT_GIT_NAME = "mohamed-zaitoon"
DEFAULT_GIT_EMAIL = "mohamedzaitoon01@gmail.com"

def run(cmd, cwd=None, silent=False, check=False):
    try:
        p = subprocess.Popen(
            cmd, cwd=cwd, stdout=subprocess.PIPE, stderr=subprocess.STDOUT,
            shell=True, universal_newlines=True
        )
        out = []
        for line in p.stdout:
            if not silent:
                print(line, end="")
            out.append(line)
        p.wait()
        output = "".join(out)
        if check and p.returncode != 0:
            print("\n‼️ Command failed:", cmd)
            sys.exit(p.returncode)
        return p.returncode, output
    except Exception as e:
        print(f"Error running command {cmd}: {e}")
        if check:
            sys.exit(1)
        return 1, ""

# ------------------------
# System detection
# ------------------------
def detect_system():
    system = platform.system().lower()
    if "linux" in system:
        if "ANDROID_ROOT" in os.environ or "TERMUX_VERSION" in os.environ:
            return "Android (Termux)"
        return "Linux"
    elif "windows" in system:
        return "Windows"
    elif "darwin" in system:
        return "macOS"
    return "Unknown"

# ------------------------
# Git identity
# ------------------------
def ensure_git_identity():
    _, name = run("git config --global user.name", silent=True)
    _, email = run("git config --global user.email", silent=True)
    if not name.strip():
        run(f'git config --global user.name "{DEFAULT_GIT_NAME}"', check=True)
    if not email.strip():
        run(f'git config --global user.email "{DEFAULT_GIT_EMAIL}"', check=True)

# ------------------------
# GH helpers
# ------------------------
def ensure_gh_installed():
    code, _ = run("gh --version", silent=True)
    if code != 0:
        print("❌ GitHub CLI (gh) not installed.")
        print("Install it first: https://cli.github.com/")
        sys.exit(1)

def ensure_gh_auth():
    code, _ = run("gh auth status", silent=True)
    if code != 0:
        print("❌ gh not authenticated.")
        print("Run: gh auth login")
        sys.exit(1)

def ensure_https_remote_from_gh():
    code, url = run("gh repo view --json url -q .url", silent=True)
    if code != 0 or not url.strip():
        return
    https_url = url.strip() + ".git"
    run(f"git remote set-url origin {https_url}", silent=True)

def git_current_branch():
    _, br = run("git rev-parse --abbrev-ref HEAD", silent=True)
    return (br or "").strip() or "main"

# ------------------------
# Main
# ------------------------
def main():
    project_dir = os.getcwd()
    system_name = detect_system()

    ensure_gh_installed()
    ensure_gh_auth()

    run(f'git config --global --add safe.directory "{project_dir}"', silent=True)
    ensure_git_identity()
    ensure_https_remote_from_gh()

    print(f"\n🖥️ Detected system: {system_name}")

    code, changes = run("git status --porcelain", silent=True)
    if code != 0:
        sys.exit(code)

    # 🧹 Remove Termux-only Gradle property
    gradle_props = os.path.join(project_dir, "gradle.properties")
    if os.path.exists(gradle_props):
        with open(gradle_props, "r", encoding="utf-8") as f:
            lines = f.readlines()
        new_lines = [
            l for l in lines
            if not l.startswith("android.aapt2FromMavenOverride")
        ]
        if lines != new_lines:
            with open(gradle_props, "w", encoding="utf-8") as f:
                f.writelines(new_lines)
            print("🧹 Removed Termux-only AAPT2 override.")

    if not changes.strip():
        print("No changes to commit.")
        sys.exit(0)

    run("git add -A", check=True)

    utc_time = datetime.now(timezone.utc).strftime("UTC %Y-%m-%d %H:%M:%S")
    commit_msg = f"Commit {utc_time} from {system_name}"
    run(f'git commit -m "{commit_msg}"', check=True)

    branch = git_current_branch()

    print(f"\n📤 Pushing via GitHub CLI → {branch}")
    code, _ = run(f"gh repo sync --branch {branch}", silent=True)
    if code != 0:
        run(f"git push -u origin {branch}", check=True)

    print(f"\n✅ Push completed successfully at {utc_time}")
    print(f"📡 Device: {system_name}")

if __name__ == "__main__":
    main()