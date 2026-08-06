# Debug delta — custom-predicates-polish, iteration 2

## Triage

**Test wrong**, both Method A programs. Evidence:

- `custom_predicates_ki04_full_permission.kt`: regenerating the goldens with `--update`
  produced only two changes: (1) new `.fir.diag.txt` / `.viper.diag.txt` goldens (these
  didn't exist before — Method A had never been run through `--verify`), and (2) the
  `<!VIPER_TEXT!>` markers on all four function declarations. The pre-existing
  `<!VIPER_VERIFICATION_ERROR!>inspect(i)<!>` marker inside `inspectAfterConsume`, which
  encodes the actual contract under test (full-permission framing), did not change at all
  — it already matched actual verifier behavior. `RENDER_PREDICATES` attaches an info-level
  `VIPER_TEXT` diagnostic to every verified function's name; existing precedent
  (`custom_predicates.kt:17,33,47`) confirms this is standard behavior for that flag, not
  something introduced by this feature. The Tester's file simply predated the marker
  requirement on this flag.
- `custom_predicates_ki05_generic_class.kt`: same shape. Only the `VIPER_TEXT` markers
  changed; the conversion output (predicate sharing one embedding across `Cell<Int>` /
  `Cell<String>` instantiations, type argument erased) is exactly what KI-05 says to expect
  and is now captured in the new `.fir.diag.txt` golden.
- `custom_predicates_ki01_overload_collision.kt`, `ki02_value_parameter.kt`,
  `ki03_nonlambda_argument.kt`, `ki03_nonlambda_spec_block.kt`: the Gate's "Build" failure
  ("32 tests failed") and this run's initial "Expected data file did not exist" errors are
  the same root cause as the two files above — none of the six programs had ever been
  golden-generated under `--verify`, since the Tester's manifest explicitly left goldens
  absent pending the Implementer. No marker changes were needed for these four; their
  `MALFORMED_PREDICATE_DECLARATION` / spec-block diagnostics already matched.

No code changes were made this iteration.

## Fixes

- Added `<!VIPER_TEXT!>` markers to the four verified function declarations in
  `custom_predicates_ki04_full_permission.kt` (`inspect`, `consume`, `twoInspections`,
  `inspectAfterConsume`).
- Added `<!VIPER_TEXT!>` markers to the three verified function declarations in
  `custom_predicates_ki05_generic_class.kt` (`useIntCell`, `useStringCell`,
  `useSameIdentity`).
- Generated the missing `.fir.diag.txt` / `.viper.diag.txt` goldens for all six programs
  via `./scripts/test.sh --update custom_predicates_ki` (first `--verify` run for this
  batch).
- Updated `testing/custom-predicates-polish-specs.md` is unchanged — the manifest's
  diagnostic names/marker-placement notes did not need revision, only the two Method A
  `.kt` files needed the `VIPER_TEXT` markers the manifest's own precedent-check missed.

Verification: `./scripts/test.sh --verify custom_predicates_ki` now exits 0 for all six
tests.

## Open

The Gate's "Build" check ran the full `./gradlew build -q` suite and reported 32 unrelated
test failures (10+ minute timeouts on retry). Not investigated here — out of scope for this
feature's six tests, and per project memory `./gradlew build` has pre-existing breakage
unrelated to this work (generateTests/detekt wiring). Re-running `./scripts/check-all.sh`
to confirm before handoff; result not yet in at delta-write time.
