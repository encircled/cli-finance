package cz.encircled.cli.finance

import cz.encircled.cli.finance.model.TransactionEntry

/**
 * @author encircled on 04-Nov-18.
 */
interface CategoryMatcher {

    fun getCategory(transaction: TransactionEntry): String?

}

inline class Needle(val value: String)

inline class CategoryType(val value: String)

abstract class AbstractCategoryMatcher : CategoryMatcher {

    override fun getCategory(transaction: TransactionEntry): String? {
        val needle = getCategoryMappings().keys.firstOrNull {
            transaction.transactionNote.contains(it.value, true) ||
                    transaction.customNote.contains(it.value, true)
        }

        return getCategoryMappings()[needle]?.value
    }

    abstract fun getCategoryMappings(): Map<Needle, CategoryType>

}

class DefaultCategoryMatcher : AbstractCategoryMatcher(), CategoryMatcher {

    override fun getCategoryMappings(): Map<Needle, CategoryType> {
        return mapOf(
                Pair(Needle("gas"), CategoryType("Auto - gas"))
        )
    }
}

