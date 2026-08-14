#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$repo_root"

fail=0

tracked_candidates() {
  find . -type f \
    ! -path './.git/*' \
    ! -path './target/*' \
    ! -path './.idea/*' \
    ! -path './scripts/check-public-safety.sh' \
    ! -name '*.class' \
    -print0
}

check_content() {
  local description="$1"
  local pattern="$2"
  if tracked_candidates | xargs -0 grep -IEni -- "$pattern" >/tmp/sila-public-safety-matches.txt 2>/dev/null; then
    echo "ERROR: $description"
    cat /tmp/sila-public-safety-matches.txt
    fail=1
  fi
}

check_content "internal-environment marker found" \
  '([.]internal|[.]corp)([/ :]|$)|corpnet|intranet-only'

check_content "credential/token-like material found" \
  'AKIA[0-9A-Z]{16}|ghp_[A-Za-z0-9]{20,}|github_pat_[A-Za-z0-9_]{20,}|-----BEGIN ([A-Z ]+ )?PRIVATE KEY-----'

check_content "private IPv4 address found" \
  '(^|[^0-9])(10\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}|192\.168\.[0-9]{1,3}\.[0-9]{1,3}|172\.(1[6-9]|2[0-9]|3[01])\.[0-9]{1,3}\.[0-9]{1,3})([^0-9]|$)'

for forbidden in .env application-dev.properties application-local.properties; do
  if [[ -f "$forbidden" ]]; then
    echo "ERROR: forbidden local configuration file is present: $forbidden"
    fail=1
  fi
done

if find . -type f \( -name '*.jks' -o -name '*.p12' -o -name '*.pfx' -o -name '*.key' -o -name '*.pem' \) -print -quit | grep -q .; then
  echo "ERROR: key/certificate material is present"
  find . -type f \( -name '*.jks' -o -name '*.p12' -o -name '*.pfx' -o -name '*.key' -o -name '*.pem' \) -print
  fail=1
fi

if [[ "$fail" -ne 0 ]]; then
  exit 1
fi

echo "Public-safety scan passed."
