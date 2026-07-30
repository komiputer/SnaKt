# Context-only evidence rescued from barr (solver-b-1)

barr reported these as surviving nowhere but its own context after the artifact
root was reclaimed, having deliberately deleted the goldens that held them.
Quoted verbatim from its report so they exist outside a live seat.

## 1. Negative controls discriminate — first such evidence in the run

```
warning: Viper verification error: The precondition of method applyDiscount might not hold.
  There might be insufficient permission to access valid(p)
```

Identical for `linked(p)` and `wellFormed(b)`. A caller omitting the predicate
does NOT verify. The standing stop-and-escalate does not fire.

## 2. Marker placement proving consumption on first forward

In the two-call shape, line 32's call was clean and line 33's carried
`<!VIPER_VERIFICATION_ERROR!>`; two errors per file, one for the second forward
and one for the control.

That asymmetry is what distinguishes "forwarding never works" from "forwarding
works exactly once". It is reconstructible by re-running the two-call shape,
which `custom_predicates_b1_single_use.kt` now is.

## 3. barr's own correction to my citation

barr's `139 tests, 3 failed` was an UPDATE pass whose 3 failures were
`Expected data file did not exist. Generating: …` — golden creation. It
corroborates the count rule exactly, but it is NOT independent evidence for
absent-golden semantics and must not be cited as such. What does corroborate
that point: the controls produced real Viper errors while their goldens were
being written.

## Refs holding barr's work

- `refs/pipeline/solver-b-1-pause-state` c60b15d32e8ff605e4a851e393062f61555f9dad
- `refs/pipeline/solver-b-1-logs` 97f688b535163daeeff4ff600b98c8f04920b201
