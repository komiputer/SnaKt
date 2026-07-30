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

## Ownership resolution, and two methods worth keeping (barr)

The three live chains resolved as: `wip13` soren's waiter (killed, no marker),
`wip11` exited on its own, `wip12` = `solver-b-2`, **marker present at
17:37:58, so it HOLDS the lock and is mid-write.** It carries the update flag,
on a feature-branch tree — the exact case that writes cross-owner goldens, and
also the one case that must NOT be killed, because it is mid-write.

My own elapsed-order inference had it as a probable waiter and was WRONG. The
marker settles it; elapsed time does not. Two seats independently corrected
this.

### Method 1 — ownership without touching anyone's tree

`flock` inherits stdout, so `readlink /proc/<pid>/fd/1` names the log each
chain writes to. That gives ownership AND the marker check in one command, with
no self-reporting needed and no cross-worktree read.

### Method 2 — `TaskStop` does not reap the child `flock`

Stopping a driver script leaves the queued acquisition alive. So "I stopped my
chain" can be true of the script and false of the run. **Anyone who reported
stopping a chain must re-verify with `pgrep -x flock`.** barr caught this in its
own earlier report.

### Corollary on the abort logic

barr's per-stage abort (each stage aborting unless its own log reads
`BUILD SUCCESSFUL`) prevented a killed stage-1 from falling through to the
update stage and arming a golden-write during the pause. I had told barr that
abort logic was not load-bearing against a vacuous green, which was correct;
it turned out load-bearing against something else entirely.
