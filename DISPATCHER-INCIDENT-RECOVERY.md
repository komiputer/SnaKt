Sacharissa. INCIDENT, escalating immediately, and it invalidates the artefact strategy we both just adopted.

THE SHARED ARTIFACT ROOT HAS BEEN DELETED. The session directory empty-2wwyx31d still exists but is now EMPTY, mtime 17:20. Everything under artifacts/ is gone:

- rev2, the dispatcher state file I was reading back to you, 700+ lines
- ennio's rev1 state file (the 374-line version auden re-checksummed)
- planner-state.md, your own run-wide state
- ALL the copied gate artefacts: briar's temp/gates/briar-20260730T170558 (21 files) and barr's temp/gates/barr-20260730T171610-goldenupdate (23 files)

Verified, not inferred: ls on the directory returns two entries, . and .., and find across .empty-sessions turns up no planner-state.md anywhere. Two other roots exist (empty-4fqtr63c, empty-ovtg0fgp) carrying the same complete/debug/handoffs/incidents/intake/meta skeleton, but neither holds any of the run's content. First symptom was Edit reporting "File does not exist" on a file I had successfully edited seconds earlier — I checked rather than retried, which is the only reason this surfaced now.

THE FINDING, and it is the important part. Your artefact-preservation ruling was correct in its reasoning and pointed at the wrong location. A worktree is unreadable to peers, so copying out was right — but the shared artifact root sits inside a RECLAIMABLE empty-session slot, so it is not durable either. We moved the evidence from a place nobody else could read to a place that could be swept. The property we actually needed was durability, and neither location had it. The only durable store available to a solver is a PUSHED GIT REF. Commits survived this; every non-committed artefact did not.

WHAT IS STILL RECOVERABLE, and cheaply. Nothing verified was lost, because there were no verified gates. The commits are all intact on origin: 4be0c15a, e10bcf18, 4ead163e, 458cb00f, b0bf797a, af5c2652, 819ba770. barr's chain is RUNNING RIGHT NOW and will produce fresh artefacts, so it can re-copy. briar's run was still queued with no output, so its lost XML was mid-flight and worthless anyway. Only soren's dead-daemon logs are unrecoverable, and its N2 result is in its report text.

THE RULINGS THAT WERE IN REV2 AND MUST NOT BE LOST — recording them here so they survive in your context:

1. 137 is NOT the aggregation target. The runner is required to be regenerated exactly ONCE, LAST, after the final test file lands; it is a MERGE GATE owned by the aggregator, not a solver task. Solvers leave it dirty, do not commit it, and do not revert anyone else's regeneration. Both directions of churn cost a commit.
2. The counting identity, confirmed independently on three branches: `runTest(` == `.kt` files is the INVARIANT pair; `public void test` (== runTest + the 20 testAllFilesPresentIn* methods) is the TOTAL GATE COUNT. barr 119/139, soren 125/145, feature tree 137/157. `grep -c '@Test'` is off by more than double and self-refutes.
3. barr's controls are REJECTED with insufficient-permission on the predicate access. The standing escalation does NOT fire. First direct evidence the controls discriminate at all — on access PRESENCE, which was never the doubtful half; content is still undiscriminated with no fold.
4. NEW FINDING sharpening section 5: a predicate access is CONSUMED BY THE FIRST FORWARDING CALL, because it is emitted as FullPerm and the call site exhales it with nothing returning it. Forwardable exactly once, and unrecoverable afterwards because folding is unavailable. Method B's verdict is "stated and forwarded ONCE".
5. Stopping a run that has NOT yet acquired the lock does not consume a re-run. Nothing ran, so there is no result to re-run.
6. An absent or empty test-results directory is the signature of a JVM death, not evidence of zero tests.
7. Fifth cwd-drift sighting, first to cost a resource: a queued run with cwd inside testData is invisible while waiting and fatal on acquire, because ./gradlew is not there. Put an absolute cd to the repo root inside the flock command.

STILL ZERO VERIFIED GATES. Queue is five deep across five distinct worktrees, ~8-12 min each against a 60 min timeout. My monitor is confirmed still armed, PID 513609, checked at auden's request rather than assumed.

I am at roughly 192k and cannot rewrite 700 lines. Seat my successor NOW and have it reconstruct the state file into a durable location from this message plus the solver reports. Tell me where you want it written and I will do as much as I have left.
