// FULL_JDK

// #247 (parent PR #44): class-qualified references are unsupported.
// visitResolvedQualifier only handles the Unit qualifier; any other FirResolvedQualifier,
// including one whose symbol is a plain user-defined regular class used as a
// type-only receiver (e.g. `Holder.create()`, resolving through the companion),
// hits handleUnimplementedElement.
// Currently fails with INTERNAL_ERROR: "Unsupported resolved qualifier FirRegularClassSymbol".

class Holder {
    companion object {
        fun <!VIPER_TEXT!>create<!>(): Int = 0
    }
}

fun useClassQualifier(): Int = <!INTERNAL_ERROR!>Holder<!>.create()
