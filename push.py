#!/usr/bin/env python3
import sys
import subprocess
import os
from datetime import datetime, timezone

DEFAULT_GIT_NAME = "mohamed-zaitoon"
DEFAULT_GIT_EMAIL = "mohamedzaitoon01@gmail.com"

def run(cmd, cwd=None, silent=False, check=False):
    """Run a shell command and return (exit_code, output)."""
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

def ensure_git_identity():
    """Ensure git global user.name and user.email are set."""
    _, name = run("git config --global user.name", silent=True)
    _, email = run("git config --global user.email", silent=True)
    if not name.strip():
        run(f"git config --global user.name \"{DEFAULT_GIT_NAME}\"", check=True)
    if not email.strip():
        run(f"git config --global user.email \"{DEFAULT_GIT_EMAIL}\"", check=True)

def get_remote_url():
    code, url = run("git remote get-url origin", silent=True)
    return url.strip() if code == 0 else ""

def ensure_https_remote(expected_https=None):
    """
    Ensure the remote 'origin' is HTTPS.
    If it's SSH, convert it to HTTPS. Never convert HTTPS to SSH.
    """
    current = get_remote_url()
    if not current:
        if expected_https:
            run(f"git remote add origin {expected_https}", check=True)
        return
    if current.startswith("git@github.com:"):
        tail = current.split("git@github.com:")[-1]
        if tail.endswith(".git"):
            tail = tail[:-4]
        https_url = f"https://github.com/{tail}.git"
        run(f"git remote set-url origin {https_url}", check=True)

def git_current_branch():
    _, br = run("git rev-parse --abbrev-ref HEAD", silent=True)
    return (br or "").strip() or "main"

def main():
    project_dir = os.getcwd()
    run(f"git config --global --add safe.directory \"{project_dir}\"", silent=True)
    ensure_git_identity()

    expected = os.environ.get("GIT_REMOTE", "").strip()
    ensure_https_remote(expected_https=expected)

    code, changes = run("git status --porcelain", silent=True)
    if code != 0:
        sys.exit(code)
    if not changes.strip():
        print("No changes to commit.")
        sys.exit(0)

    run("git add -A", check=True)
    utc_time = datetime.now(timezone.utc).strftime("UTC %Y-%m-%d %H:%M:%S")
    commit_msg = f"Commit {utc_time}"
    code, _ = run(f'git commit -m "{commit_msg}"', silent=False)
    if code != 0:
        _, _ = run("git diff --cached --name-only", silent=True)

    branch = git_current_branch()
    push_cmd = f"git push -u origin {branch}"
    code, _ = run(push_cmd)
    if code != 0:
        print("\n❌ Push failed.")
        print("Quick tips:")
        print("  • Check that the remote HTTPS URL is correct:", get_remote_url() or expected or "<no remote>")
        print("  • If this is your first push, Git will ask for credentials.")
        print("    Use your GitHub username and a Personal Access Token (PAT) instead of a password.")
        print("  • To explicitly set the remote URL:")
        print("      git remote set-url origin https://github.com/mohamed-zaitoon/{tail}.git")
        print("  • Generate a PAT: GitHub → Settings → Developer settings → Personal access tokens")
        sys.exit(code)

    print("✅ Push completed successfully.")

if __name__ == '__main__':
    main()
