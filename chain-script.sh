#!/usr/bin/env bash
# Method B gate chain. Each stage takes the lock separately, emits LOCK-ACQUIRED, and the chain
# ABORTS if a stage does not reach BUILD SUCCESSFUL — so a lock timeout can never let a later stage
# run against an unregenerated runner and report a vacuous green.
set -u
R=/home/silverbot/dev/SnaKt-wip11
JVM='-Xmx6g -XX:MaxMetaspaceSize=512m -Dfile.encoding=UTF-8'
LOCK=/tmp/snakt-gradle.lock
cd "$R" || exit 1

run() {   # run <logfile> <gradle args...>
    local log=$1; shift
    echo "### $(date -Is) starting: $* -> $log"
    flock -w 3600 -E 75 "$LOCK" bash -c "echo LOCK-ACQUIRED-\$(date -Is); ./gradlew $* -Dorg.gradle.jvmargs=\"$JVM\"" > "$log" 2>&1
    local rc=$?
    echo "### client exit=$rc  lock_marker=$(grep -c LOCK-ACQUIRED "$log")  verdict=$(grep -Eo 'BUILD (FAILED|SUCCESSFUL)' "$log" | tail -1)"
    if [ "$rc" = 75 ]; then echo "### ABORT: lock timeout (exit 75), never acquired"; return 1; fi
    if ! grep -q 'BUILD SUCCESSFUL' "$log"; then echo "### ABORT: no BUILD SUCCESSFUL in $log"; return 1; fi
    return 0
}

run /tmp/b1-gen2.log ':formver.compiler-plugin:generateTests' || exit 1

echo "### runner entries for my cases:"
grep -o 'custom_predicates_b1[a-zA-Z_]*' \
  formver.compiler-plugin/test-gen/org/jetbrains/kotlin/formver/plugin/runners/PhasedDiagnosticTestGenerated.java \
  | sort -u
if [ "$(grep -c 'custom_predicates_b1' formver.compiler-plugin/test-gen/org/jetbrains/kotlin/formver/plugin/runners/PhasedDiagnosticTestGenerated.java)" = 0 ]; then
    echo "### ABORT: runner names none of my cases"; exit 1
fi

# Goldens do not exist yet for these three files, so the first pass must create them.
echo "GOLDEN_START=$(date +%s)"
run /tmp/b1-update2.log ':formver.compiler-plugin:test -Pkotlin.test.update.test.data=true'
echo "### golden-update stage finished (non-fatal; failures here are expected while goldens are absent)"

echo "GATE_START=$(date +%s)"
run /tmp/b1-gate2.log ':formver.compiler-plugin:test'
echo "### gate stage verdict recorded"
echo ALLDONE
