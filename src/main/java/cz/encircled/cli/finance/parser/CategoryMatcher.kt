@file:Suppress("EXPERIMENTAL_FEATURE_WARNING")

package cz.encircled.cli.finance.parser

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import cz.encircled.cli.finance.model.TransactionEntry
import java.io.File
import java.util.HashMap
import kotlin.collections.ArrayList
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.any
import kotlin.collections.firstOrNull
import kotlin.collections.forEach
import kotlin.collections.listOf
import kotlin.collections.map
import kotlin.collections.mapOf
import kotlin.collections.mapValues

/**
 * @author encircled on 04-Nov-18.
 */
interface CategoryMatcher {

    fun getCategory(transaction: TransactionEntry): String?

}

abstract class AbstractCategoryMatcher : CategoryMatcher {

    override fun getCategory(transaction: TransactionEntry): String? {
        val category = getCategoryMappings().entries.firstOrNull {
            it.value.any { regex ->
                regex.matches(transaction.transactionNote.toLowerCase()) ||
                        regex.matches(transaction.customNote.toLowerCase())
            }
        }

        return category?.key
    }

    abstract fun getCategoryMappings(): Map<String, List<Regex>>

}

class FileBackingCategoryMatcher(private val filePath: String) : AbstractCategoryMatcher() {

    var typeRef: TypeReference<HashMap<String, MutableList<String>>> = object : TypeReference<HashMap<String, MutableList<String>>>() {}

    var rawData: Map<String, MutableList<String>>

    private lateinit var mappedData: Map<String, MutableList<Regex>>

    init {
        rawData = ObjectMapper().readValue<Map<String, MutableList<String>>>(File(filePath), typeRef)
        mapRawData()
    }

    override fun getCategoryMappings(): Map<String, List<Regex>> = mappedData

    fun persistToFile(values: Map<String, String>) {
        val updated = HashMap(rawData)
        values.entries.forEach {
            updated.putIfAbsent(it.key, ArrayList())
            updated[it.key]!!.add(it.value)
        }

        rawData = updated
        mapRawData()

        ObjectMapper().writeValue(File(filePath), rawData)
    }

    private fun mapRawData() {
        mappedData = rawData.mapValues {
            ArrayList(it.value.map { str ->
                Regex(str)
            })
        }
    }

}

class DefaultCategoryMatcher : AbstractCategoryMatcher(), CategoryMatcher {

    private val data: Map<String, List<Regex>> = mapOf(
            Pair("GAS", listOf(Regex(".*gas.*")))
    )

    override fun getCategoryMappings(): Map<String, List<Regex>> = data
}

class DelegatingCategoryMatcher(val matchers: MutableList<CategoryMatcher>) : CategoryMatcher {

    override fun getCategory(transaction: TransactionEntry): String? =
            matchers.firstOrNull { it.getCategory(transaction) != null }?.getCategory(transaction)

}