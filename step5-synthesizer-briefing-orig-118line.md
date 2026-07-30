# Step 5 — Synthesizer, custom-predicates

You are the Synthesizer for the `custom-predicates` run. Your bundle is placed, unopened, at
`/home/silverbot/dev/.empty-sessions/empty-2wwyx31d/artifacts/handoffs/synthesizer.zip`
(md5 `d843046ea69f9106e4585186ce657684`, 1104 bytes compressed, one entry `instructions.md`).
Unpack it **into your own worktree**, never the artifact root. Your Run Context arrives as a
separate message.

## Provenance you must know, and must not paper over

This bundle is **cross-generation**. It is not part of our own delivered set — `orchestrator_3.zip`
contains no synthesizer at any depth. It comes from `/home/silverbot/data/received/orchestrator-x/`,
dated three days older than our chain. **The operator was told of the mismatch risk and ruled
proceed.** That is a documented operator decision, not a substitution anyone inferred, and it goes
in your step doc that way.

Consequence: your bundle's Run Context fields may not line up with what this run's role files pass
through. **If they do not line up, that is the mismatch surfacing, not a worker error.** Report it.
Do not invent field values to make an artifact fit a role file — that is how a run looks complete
without being complete.

## Read first, in order

Under the artifact root above:
1. `handoffs/planner-state.md` — run state, revision 4. Its "The gate" and "HEADLINE FINDING"
   sections are load-bearing.
2. `complete/custom-predicates-step-4-iter-1.md` — the Step 4 results you are synthesising.
3. `complete/custom-predicates-step-{1,2,3}.md`, `testing/custom-predicates-strategy.md` (§5),
   `testing/custom-predicates-method-b-planner-ruling.md`.
4. `surface/custom-predicates-api.md` — **re-check before citing.**

## The verification standard, which is not negotiable here

This run's central error was a conversion-only gate read as verification evidence, and it cleared
three consecutive gates before anyone caught it. Four distinct false-green classes are enumerated in
the state file. What binds you:

- **A gate counts as verified only when you hold the command line and its captured output.** Not a
  reported result, not a remembered green, not a manifest state.
- `:untilConversion` sets `CHECK_CONVERSION` and Z3 never runs. The gate for a new feature is FULL
  `:test`.
- **The golden-update trap is the sharpest one and it is live.** An update pass rewrites the test
  *source* as well as the goldens, inserting inline markers, so program and expectation are both
  bent to observed behaviour. **No golden is ever accepted from an update run without a
  human-legible diff you have read** (Launcher ruling). Require a final non-update run that is green
  without having rewritten any golden.
- A run with **no `BUILD` line** is no run. Distinguish causes by the `LOCK-ACQUIRED` marker: absent
  means the lock was never obtained and re-running is correct; present with no `BUILD` line is the
  genuinely unexplained class and must be reported with its log. `flock -E 75` means timeout and
  nothing else. **Re-run budget is two attempts, then hand the case back as a non-result.**
- Judge every gradle run by its captured log text. Reported exit codes are unreliable in this run.
- **A test count arrives with two commands or it is not a result: the command that ran the tests, and
  the command that extracted the number.** Gradle prints no counts on a successful run, so any count
  attributed to a success log is misattributed — every count in this run came from aggregating JUnit
  XML, and the ones recorded without that step are under-specified rather than wrong. That is exactly
  how the earlier false greens got through. PR 30's Gates section has already been amended once for
  this; do not reintroduce it.

## Host constraint — do not remove any part of this

Wrap **every** gradle invocation:

    flock -w 3600 -E 75 /tmp/snakt-gradle.lock bash -c 'echo LOCK-ACQUIRED-$(date -Is); ./gradlew test -Dorg.gradle.jvmargs=-Xmx6g'

The host has ~11 GB. Concurrency was always the problem, never the 6g ceiling. Baseline `:test` at
`e1cd7c1c` measured **11m39s**; runs carrying new Silicon-invoking cases will be slower.

## The finding your synthesis turns on

Predicate accesses enter a program **only** at `Stmt.Inhale`. `Stmt.Fold` has **no constructor
anywhere in the plugin**, and that absence is **pre-existing upstream** — verified at `bf32366c`,
the commit the feature branch was cut from. `Exp.Unfolding` (the pure-expression form, spec
contexts) **works and was also inherited**, not built by this run. `Stmt.Unfold` has two live sites
that cannot fire, both behind `unfoldToAccess`, true only for a policy already diverted to `havoc`.

So of the two halves of the operator's instruction "implement fold/unfold automation": one existed
before this run started, and the other cannot be automated because there is nothing to automate.

Consequence, **stated as inference and not proof**: a custom predicate can be assumed and consumed
but never established. No constructor can show that what it produces satisfies its predicate, so
**positive cases risk passing vacuously**. This is **not unsoundness** — Viper preconditions are
assumptions by design. Keep that qualification attached; it is the difference between a limitation
and a bug.

Related, and do not over-read a green control: the negative controls discriminate predicate-access
**presence**, not **content**. With no `fold`, nobody has a body-content discriminator at all.

## Branch and PR state

- Feature branch `origin/feature/custom-predicates`. **One PR for the run, komiputer/SnaKt#30.
  Never open a second.** Commits append. `origin` is the fork `komiputer/SnaKt`; `upstream` is
  `JetBrains/SnaKt` — do not open PRs against upstream.
- **Convention in force: any commit whose work is not gate-passed carries `[UNVERIFIED]` in its
  subject line.** A commit subject stating its own verification status cannot be misread by someone
  scanning `git log`, which is exactly how a false green gets adopted here. Keep it on anything
  ungated; drop it only when you hold that case's gate output.
- Solver commits are already on the branch under that tag, including 5 Method A test sources at
  `4be0c15a` with no goldens and no inline markers — never through an update pass. The
  golden-update discipline above applies to exactly those.

## Standing rules

- **Do not open another agent's bundle.** Yours is yours; a subordinate's is not.
- **Never `git init` the artifact root** — it breaks every worker's writes.
- Never name another instance's worktree in a Bash command, message bodies included.
- Read-only `git -C <main checkout>` is sanctioned; any writing subcommand is refused.
- **Never pattern-kill** (`pkill -f`): it may succeed and it may SIGKILL sibling Claude sessions.
- Write your state file and report at **130k** context. Do not run to the wall. Trust your turn
  footer over your sense of your own usage — three agents in this run misjudged it by 25k+.
- Spawn any worker with an **explicit model**, and brief it to tag turn-ends; untagged turn-ends
  reach nobody.

## Reporting

Report to me (`intersession send remo`) at step boundaries, tagging turns `%notify: spawner`.
Everything reaching the operator goes through the Launcher via me — do not attempt direct contact.
When you catch your own error, say so **with the remediation attached, in the same turn**. Three
such self-catches are the only reason this run is recoverable.
