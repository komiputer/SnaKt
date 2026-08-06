# Array support gap analysis

Gap analysis of the `IntArray` domain model (PR 191, `ramon/int-array-domain-model`) against
Kotlin array semantics. This is an analysis, not a fix: no behavioural changes accompany it.
Base commit for this audit: tip of `pipeline/arrays-gap-analysis`, `88a36738` ("add intArray")
plus the later `a26f9ba0`/`43176619` commits that extend the same files.

## API surface actually implemented

Only `kotlin.IntArray` is recognized. No other array type (`Array<T>`, `DoubleArray`,
`BooleanArray`, `LongArray`, `CharArray`, `ByteArray`, `ShortArray`, `FloatArray`) is embedded —
see `formver.compiler-plugin/core/src/org/jetbrains/kotlin/formver/core/conversion/ProgramConverter.kt:630`
for the sole type-resolution branch.

- **`IntArray(size: Int)` constructor** — signature-level contract in
  `formver.compiler-plugin/core/src/org/jetbrains/kotlin/formver/core/conversion/SignatureCreation.kt:183-211`:
  precondition `size >= 0`, postconditions `result.size == size` and
  `forall i. 0 <= i && i < size ==> result[i] == 0`. No support for `IntArray(size) { i -> ... }`.
- **`IntArray.size: Int`** — `SpecialProperties.kt:74-88` (`IntArraySizeProperty`), getter lowers
  to `IntArraySize`, which reads the domain function `size(arr)` (`RuntimeTypeDomain.kt:294-299`).
- **`IntArray.get`/`set`** (`arr[index]` / `arr[index] = value`) —
  `FullySpecialKotlinFunction.kt:384-396`, lower to `IntArrayGet`/`IntArraySet`
  (`IntArrayEmbeddings.kt:26-31, 38-43`). Bounds are **not** implicitly assumed; reading with no
  precondition constraining `index` against `arr.size` fails verification
  (`testData/diagnostics/stdlib/intarray/intarray.kt:11-14`).
- **`toMultiset(arr: IntArray): Any`** (formver-internal DSL function, not real Kotlin stdlib) —
  `FullySpecialKotlinFunction.kt:431-436`, lowers to `IntArrayAsMultiset`
  (`IntArrayEmbeddings.kt:46-51`), calling `SpecialFunctions.arrayToMultisetFunction`
  (`SpecialFunctions.kt:65-84`). Used inside contracts to reason about permutation-equality of
  array contents (e.g. proving a sort is a permutation of its input).

**Uniqueness/permission model:** `IntArray` values must carry `@Unique` (full permission to every
cell) to be readable or writable at all; the `IntArray_unique` predicate
(`PretypeEmbedding.kt:80-118`) asserts `isSubtype(typeOf(arr), intArrayType())` plus
`forall j. 0<=j && j<size(arr) ==> acc(slot(arr,j).array_cell_int, write)`. Passing an array across
a call boundary while retaining access afterward requires `@Borrowed` in addition to `@Unique`.

**Underlying Viper domain** (`RuntimeTypeDomain.kt:284-313`): `slot(arr, i)` maps
(array, index) to a cell reference, `size(arr)` gives the logical size, `slotToArray`/
`slotToIndex` are inverses of `slot`. Axioms: `sizeIsNonNeg`, `allDiff` (injectivity of `slot`).
Backing field `array_cell_int` (`SpecialFields.kt:33-38`) is gated into the emitted program only
when `TypeResolver.intArrayUsed` is set (`TypeResolver.kt:29-34, 118-119, 122-124`).

## Gaps

### KI-01 — Non-`IntArray` array types silently fall into generic class embedding, likely crashing

- **Severity:** High. **Confidence:** likely (static reasoning, not run against the compiler).
- `DoubleArray`, `BooleanArray`, `LongArray`, or `Array<T>` are `ConeClassLikeType`s that resolve
  to a real `FirRegularClassSymbol` (`ProgramConverter.kt:646-651`), taking the
  `existing(embedClass(classLikeSymbol))` branch instead of the explicit
  `unimplementedTypeEmbedding` error path (`ProgramConverter.kt:674-681`). `embedClass` treats the
  type as an ordinary user class with declared properties; array intrinsics have no such
  declarations for `get`/`set`/`size`, and no special-function registration exists for them
  anywhere (`FullySpecialKotlinFunction.kt`, `SpecialFunctions.kt`, `SpecialProperties.kt` —
  zero hits beyond `IntArray` and the unrelated `booleanArrayTypeName` vararg-typing trick at
  `FullySpecialKotlinFunction.kt:52-55`).
- **Repro sketch:** an `@AlwaysVerify` function taking a `DoubleArray` parameter and reading
  `.size` or indexing it, compiled through the plugin; expect either an internal crash or a
  nonsensical Viper program instead of a clean "unsupported feature" diagnostic.

### KI-02 — No modeled exception for out-of-bounds array access

- **Severity:** Medium. **Confidence:** confirmed (by design — bounds are required as an explicit
  precondition, per `testData/diagnostics/stdlib/intarray/intarray.kt:11-19`).
- **File:** `IntArrayEmbeddings.kt:26-31, 38-43`.
- Kotlin programs that rely on catching `ArrayIndexOutOfBoundsException` around array access have
  no corresponding modeled exception value; the verifier's only response to an unprovable access
  is a static `VIPER_VERIFICATION_ERROR`. Not a false-accept risk, but future `try`/`catch`
  support for this exception would need new work here.

### KI-03 — `IntArray(size) { init }` and `arrayOf`/`intArrayOf` unmodeled

- **Severity:** Medium. **Confidence:** confirmed (grep for `arrayOf` and for a second/lambda
  constructor parameter under `formver.compiler-plugin/core/src` returns zero hits).
- **File:** `SignatureCreation.kt:183-211`.
- Only the plain zero-fill `IntArray(size)` constructor has a contract. `IntArray(size) { i -> i *
  2 }` and `intArrayOf(1, 2, 3)` fall back to generic constructor/call handling with none of the
  size/content postconditions applied — despite both being extremely common Kotlin idioms.

### KI-04 — All-or-nothing uniqueness model has no shared/read-only array access

- **Severity:** Medium. **Confidence:** confirmed (every array-consuming function in the sole test
  file is `@Unique @Borrowed`; the `IntArray_unique` predicate grants only full (`write`)
  permission, no fractional-permission variant exists).
- **File:** `PretypeEmbedding.kt:80-118`.
- Two functions that both only want to *read* the same array cannot both hold permission
  simultaneously under the current predicate — every array parameter must be `@Unique`, forcing
  exclusive ownership even for pure read access. Overly conservative, not unsound; blocks any
  pattern with concurrent/shared readers.

### KI-05 — No automatic per-cell frame axiom; mutation framing is fully manual

- **Severity:** Low. **Confidence:** confirmed (every postcondition in `intarray.kt` that needs
  "cell `j != i` unchanged" restates it via an explicit `forAll`, e.g. lines 48-53, 86-89 — no
  helper or automatic lemma exists).
- **File:** `IntArrayEmbeddings.kt:33-43` (`IntArraySet` carries no framing).
- Scales poorly for larger mutating algorithms; a maintainability gap for future array-heavy
  proofs, not a correctness issue in what's already proven.

### KI-06 — Nested/generic arrays (`Array<IntArray>`, jagged/2D arrays) unsupported

- **Severity:** High. **Confidence:** confirmed (no `Array` handling anywhere in the type resolver
  beyond the unrelated vararg-typing trick).
- **File:** same as KI-01 (`ProgramConverter.kt:630-651`) — no branch for `Array<T>` at any `T`.
- 2D/jagged arrays are a common real-world Kotlin pattern; any attempt to verify a function using
  `Array<IntArray>` hits the same crash-risk as KI-01.

### KI-07 — Global `intArrayUsed` gate is a unique, stateful initialization dependency

- **Severity:** Low. **Confidence:** confirmed.
- **File:** `TypeResolver.kt:29-34, 118-119, 122-124`.
- No other special type in the codebase gates its backing field/predicate emission behind a
  mutable flag set during type resolution and read later during program assembly. Not observed to
  misbehave, but inconsistent with how every other special type is emitted (unconditionally), and
  is the kind of one-off state that breaks silently if emission order is refactored without
  knowing this dependency exists.

## Usage evidence (community search)

General Kotlin-language questions about arrays, used as usage-pattern evidence rather than as
defects (there is no public bug tracker for code that hasn't shipped):

- [IntArray vs Array<Int> in Kotlin](https://stackoverflow.com/questions/45090808/intarray-vs-arrayint-in-kotlin) — score 133 — confirms `IntArray` vs `Array<Int>` is a live distinction callers reason about; motivates KI-01/KI-06.
- [2D Array in Kotlin](https://stackoverflow.com/questions/34145495/2d-array-in-kotlin) — score 115 — evidence for KI-06.
- [How can I create an array in Kotlin like in Java by just providing a size?](https://stackoverflow.com/questions/35253368/how-can-i-create-an-array-in-kotlin-like-in-java-by-just-providing-a-size) — score 140 — evidence for KI-03 (sized construction is common, not niche).
- [How to initialize an array in Kotlin with values?](https://stackoverflow.com/questions/31366229/how-to-initialize-an-array-in-kotlin-with-values) — score 421 — evidence for KI-03; `arrayOf`/literal-style init is the highest-scored array question found, and entirely unmodeled.
- [How to create an empty array in kotlin?](https://stackoverflow.com/questions/29743160/how-to-create-an-empty-array-in-kotlin) — score 123 — `IntArray(0)` is covered by the existing zero-fill contract (`SignatureCreation.kt:183-211`, `size == 0` case); no gap, noted for completeness.

## Build status

`./gradlew :formver.compiler-plugin:compileKotlin` succeeds cleanly on this branch. No build
breakage from this analysis (no code was changed).
