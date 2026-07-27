# Complete: f6-exists solver-a-1, iteration 1

Method A (Feature Contract), slot 1 of 3. Model: Opus.

Report: `testing/f6-exists-solver-A-1-iter-1.md`
Branch: `solver/f6-exists-a-1-iter1` (off `origin/polish/f6-exists`, tip `e99de81a`)

## Deliverables

Six test files, thirteen specification functions, covering all six contract properties in the
brief. Suite is green (`BUILD SUCCESSFUL`, clean tree): markers pin observed behaviour, with the
contract expectation recorded in each file's comments where the two diverge.

| File | Property | Result |
|---|---|---|
| `verification/user_invariants/exists_contract_basic.kt` | 1 — basic satisfiability, empty domain | 3 of 4 violate the contract |
| `verification/user_invariants/exists_ref_domain.kt` | 2 — reference-type guard (QT-1) | guard correct, pinned textually |
| `verification/user_invariants/exists_char_domain.kt` | 3 — Char domain bound (CH-1) | defect confirmed |
| `expensive_verification/algorithms/contains_character.kt` | 4 — combined quantifiers | contract satisfied |
| `expensive_verification/algorithms/exists_search_flag.kt` | 5 — `exists` in loop invariant | contract satisfied |
| `verification/user_invariants/exists_nested.kt` | 6 — nested quantifiers | lowering correct, none verify |

## Findings, most significant first

1. **New, and larger than any catalogued issue:** a postcondition `exists` cannot be discharged
   unless the witness is already available as a ground term in the proof context.
   `exists<Int> { it == 0 }` fails to verify as a postcondition. Twelve of thirteen standalone
   postcondition existentials fail; the two that verify are the ones whose witness comes from a
   loop invariant or from program state at the proof site — the `max_character.kt` shape. The
   shipped corpus is systematically blind to this. Cause confirmed, not inferred: Silicon's
   bundled `z3config.smt2` sets `smt.mbqi false` and `auto_config false`, leaving E-matching as
   the only instantiation strategy, and SnaKt passes `emptyList()` for Silicon's args at both
   construction sites, so it inherits that default wholesale.
2. **CH-1 confirmed**, on textual evidence: `exists<Char>` emits a bare `Int` with no
   `0 <= x && x < 65536` guard. The verification failure alone proves nothing, given finding 1.
3. **QT-1 resolved in the feature's favour** and now pinned by a test: `exists` uses `&&`,
   `forAll` uses `==>`, as claimed.
4. **QT-2 partially filled.** Loop-invariant, combined-quantifier and nested coverage added and
   passing or correctly lowering. The empty-domain case is uninformative as written, since it
   fails for the same blanket reason everything else does.
5. Incidental: `it == 1 && it == 2` over `Int?` lowers two identical comparisons differently
   (`anon == intToRef(1) && intFromRef(anon) == 2`). No unsoundness claimed.

## Action needed from later steps

- Method N cases 1 and 4 cannot distinguish their intended defects from the blanket failure in
  finding 1. Their expected outcomes need restating against emitted Viper text.
- The Debugger should know that restoring `charDomainBoundOrNull()` will not make `exists<Char>`
  usable in a postcondition; the bound is not what blocks it.
- DI-1 untouched by this slot; no run of mine crashed the compiler.

Problems encountered (host memory, host-wide `--gradlew --stop`, broken pre-commit,
`ugrep` BRE alternation, unwritable char escapes) are listed in full in the report.
