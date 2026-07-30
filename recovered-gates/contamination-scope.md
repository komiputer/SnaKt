# Update-pass contamination: scope, narrowed by soren

## The hazard as first stated

An update pass regenerates goldens for cases the running solver does not own
and cannot judge. Observed in briar's pass, which rewrote goldens for a2 and b2
files, and confirmed by briar itself.

## soren's correction, which is right

The hazard requires a tree that HOLDS other solvers' files. soren's worktree
holds only its own nine `custom_predicates_n*` files, so its dead update pass
could not have touched a2 or b2 goldens. Its `git status` confirmed it modified
exactly two of its own files and created one, nothing else.

This does not weaken the ruling. It identifies who is exposed.

## The discriminator

- A worktree on **`feature/custom-predicates`** carries every solver's landed
  files, so an update pass there rewrites goldens across owners. briar's did.
- A worktree on a **slug branch** (`solve/solver-*`) carries only its own
  solver's new files, so an update pass there is self-contained.

So the check before running an update pass is which branch the tree is on, not
who is running it. Solvers on the feature branch must not run update passes
without expecting cross-owner writes; solvers on slug branches may.

## soren's second point, on detectability

A queued update pass acquiring the lock unattended — overnight, say — would
write goldens that look like deliberate work in a later `git status`.
Timestamps would not distinguish it from an authored pass. That is the reason a
WAITING update pass is more dangerous than a waiting plain gate, and why
stopping it was the correct call rather than a cautious one.
