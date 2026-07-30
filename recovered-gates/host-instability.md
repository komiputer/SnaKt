# Four daemon deaths, and no monitor armed

`Gradle build daemon disappeared unexpectedly` has now killed four runs across
three seats:

| seat | run | evidence |
|---|---|---|
| felix (a-1) | clean baseline | died at 109 log lines, no marker (predates wrapper) |
| felix (a-1) | golden update | marker `17:27:46`, no BUILD line |
| soren (n-1) | golden update | marker `17:03:33`, no BUILD line, exit 1 |
| saskia (b-2) | golden update | marker `17:37:58`, second death on its pass |

That is felix's "two of three runs on this host" generalising across seats
rather than being local to one tree.

## Why this matters more than the individual non-results

**There is currently NO memory monitor armed on the host.** ennio's expired and
mine hit its exit condition at 17:35 after seeing the queue drain 5 to 3. A
daemon disappearing is the signature a monitor exists to catch, and four of them
have now gone unobserved.

The runs that die are disproportionately **golden-update** passes, which are the
longest and most memory-hungry stage. Three of the four deaths were update
passes. Anyone planning the next iteration should assume an update pass on this
host has a material chance of dying, and sequence so that a death costs one
stage rather than a chain.

## The compensating good news

Every death was correctly classified as a NON-RESULT by the marker rule
(`LOCK-ACQUIRED` present, no `BUILD` line), and no death consumed re-run budget
under the ruling that a run producing no result cannot be re-run. The rule set
handled this cleanly; the host did not.

saskia's death also meant the cross-owner golden write never happened — a2's
file was left untouched. The worst-case scenario I escalated did not
materialise, for reasons unrelated to any safeguard.
