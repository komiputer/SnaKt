// FULL_JDK

// #247 (parent PR #44): class-qualified property reads are unsupported.
// visitResolvedQualifier only handles the Unit qualifier; reading a property off a
// companion object via a user-defined class qualifier (as opposed to a stdlib type
// like Int) hits the same handleUnimplementedElement fallback.
// Currently fails with INTERNAL_ERROR: "Unsupported resolved qualifier FirRegularClassSymbol".

class Config {
    companion object {
        val DEFAULT_LIMIT: Int = 10
    }
}

fun useCompanionConstant(): Int = <!INTERNAL_ERROR!>Config<!>.DEFAULT_LIMIT
