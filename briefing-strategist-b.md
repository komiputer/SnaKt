# Ruling needed: has Method B been weakened past its purpose?

You are a fresh Strategist for the SnaKt custom-predicates run. One question, docs
only, no code changes. Artifact root:
`/home/silverbot/dev/.empty-sessions/empty-2wwyx31d/artifacts/`

Read `testing/custom-predicates-strategy.md` (especially §5), `testing/custom-predicates-plan.md`,
and `complete/custom-predicates-step-3.md`. These are the Step-3 documents you are ruling on.

The situation. Method B's purpose was to show that a predicate expresses an invariant
Kotlin's type system cannot, *and that the invariant is then used*. Solver `barr` found all
four B sketches want the invariant consumed inside a method body, and argues no such program
shape verifies today: a `var` read is havoc'd before permissions are consulted, and a `val`
read embeds as a permission-free Viper function that Viper cannot see into without an `unfold`
the plugin never emits. That is the run's already-accepted non-coverage, pinned in
`custom_predicates.viper.diag.txt`. `barr` reshaped all four into declaration + consumer
requiring the predicate + caller forwarding it, since predicate-to-predicate forwarding is
what verifies. Method B now shows "the invariant can be stated and forwarded".

Rule on: does the reshaped Method B still serve its purpose, or does the "used" half need a
distinct case (and if so, what shape, and does any shape exist)? Give a recommendation with
reasoning, note what you could not settle, and write it to
`testing/custom-predicates-method-b-ruling.md`. I make the final call, so argue rather than
hedge. Do not modify other artifacts. Never `git init` the artifact root.

Gate note if you cite tests at all: this run's gate is full `:test`, never `:untilConversion`
(which skips verification). Always quote the command with any test count.

End your turn when the ruling is written, tagged `%notify: spawner`.
