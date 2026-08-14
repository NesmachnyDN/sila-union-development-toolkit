#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
classes="$repo_root/target/smoke-classes"
rm -rf "$classes"
mkdir -p "$classes"

mapfile -d '' sources < <(find "$repo_root/src/main/java" -name '*.java' -print0)
javac --release 21 -d "$classes" "${sources[@]}"
java -cp "$classes:$repo_root/src/main/resources" io.github.nesmachnydn.sila.app.DemoApplication >/tmp/sila-toolkit-demo-output.txt

grep -q "Architecture Repository Demo" /tmp/sila-toolkit-demo-output.txt
grep -q "Customer Portal" /tmp/sila-toolkit-demo-output.txt
grep -q "Glossary import:" /tmp/sila-toolkit-demo-output.txt

echo "Dependency-free smoke test passed."
