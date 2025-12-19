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
        print("Install: https://cli.github.com/")
        sys.exit(1)

def ensure_gh_auth():
    code, _ = run("gh auth status", silent=True)
    if code != 0:
        print("❌ gh not authenticated.")
        print("Run: gh auth login")
        sys.exit(1)

def ensure_https_remote_from_gh():
    code, url = run("gh repo view --json url -q .url", silent=True)
    if code == 0 and url.strip():
        run(f"git remote set-url origin {url.strip()}.git", silent=True)

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
    print("🔄 Checking for local changes...")

    _, changes = run("git status --porcelain", silent=True)
    has_local_changes = bool(changes.strip())

    if has_local_changes:
        print("💾 Stashing local uncommitted changes...")
        run("git stash push --include-untracked -m 'auto-stash-before-sync'", check=True)

    branch = git_current_branch()
    print(f"📦 Current branch: {branch}")

    print("\n🌍 Syncing with GitHub (gh repo sync)...")
    code, _ = run(f"gh repo sync --branch {branch}")

    if code != 0:
        print("⚠️ gh repo sync failed, falling back to git pull --rebase")
        code, _ = run(f"git pull --rebase origin {branch}")
        if code != 0:
            print("\n❌ Pull (rebase) failed.")
            print("Resolve conflicts then run:")
            print("  git rebase --continue")
            sys.exit(code)

    if has_local_changes:
        print("\n♻️ Restoring stashed local changes...")
        code, _ = run("git stash pop")
        if code != 0:
            print("\n⚠️ Conflicts while restoring stash.")
            print("Resolve manually, then commit.")
            sys.exit(1)

    utc_time = datetime.now(timezone.utc).strftime("UTC %Y-%m-%d %H:%M:%S")
    print(f"\n✅ Sync completed successfully at {utc_time}")
    print(f"📡 Device: {system_name}")
    sys.exit(0)

if __name__ == "__main__":
    main()