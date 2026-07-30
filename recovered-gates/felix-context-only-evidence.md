# Context-only evidence rescued from felix (solver-a-1)

Quoted from felix's report so it survives outside a live seat.

## 1. FIVE COMMITTED NEGATIVE CONTROLS ARE CURRENTLY INVERTED ASSERTIONS

felix hand-wrote `<!VIPER_TEXT!>` markers but deliberately left the
`<!VIPER_VERIFICATION_ERROR!>` markers for the update pass to insert. The
update pass died. With no golden and no error marker, these five cases assert
that programs felix expects Viper to REJECT verify cleanly:

- `claimOrderedUnbacked`
- `claimOrderedInLoop`
- `swapOrderedForTight`
- `claimEvenChainUnbacked`
- `swapOddChainForEvenChain`

**Anyone adopting `b0bf797a` as-is adopts five inverted assertions.** This is
the `[UNVERIFIED]` convention doing real work rather than ceremony.

## 2. A12 preliminary conversion output — answers two structural worries

Salvaged from a contaminated run and NOT a gate result, but informative.

Composition works. An implicit-receiver predicate call inside a predicate body
resolves; `sane` emitted as:

```
acc(Segment_unique(v_this_extension), write) && (acc(ordered(v_this_extension), write) && acc(nonNegative(v_this_extension), write))
```

Mutual recursion works and does not hang the embedder: `evenChain` and
`oddChain` each emitted referencing the other, across a forward reference.
Postconditions carry through as `ensures acc(sane(s), write)`.

## 3. Host instability — two daemon deaths out of three runs

- `generateTests`: `LOCK-ACQUIRED-2026-07-30T17:03:17`, `BUILD SUCCESSFUL in 10s`
- golden update: `LOCK-ACQUIRED-2026-07-30T17:27:46`, daemon disappeared, no BUILD line
- earlier baseline: died the same way at 109 log lines, no marker (predates wrapper)

`build/test-results/test/` held 0 XML files — the JVM-death signature, not zero
tests. No goldens written; the six `.kt` files retained their 16:54 copy-in
timestamps.

## 4. Feature-level defect, unrelated to felix's cases

`Builtins.kt`'s KDoc for `predicate` demonstrates `next!!.sorted()`, but the
plugin cannot compile `!!` at all. The feature's public doc comment shows code
the feature rejects.

## 5. Counts, with the tree named

- post-build working tree: 122 `runTest(` == 122 `.kt`, plus 20 = **142**
- its commit: `runTest(` is 116, the regeneration correctly dropped

felix ran no FULL gate and claims no gate result. A11 remains the top gap.
