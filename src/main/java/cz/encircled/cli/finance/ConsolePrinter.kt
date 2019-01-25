package cz.encircled.cli.finance

import cz.encircled.cli.finance.model.TransactionEntry
import java.text.NumberFormat
import kotlin.reflect.full.declaredMemberProperties

class ConsolePrinter {

    fun pattern(inputCommand: InputCommand, output: Any): String {
        val pattern = inputCommand.namedArgs["p"]?.split(",") ?: defaultPattern(output)

        return if (output is List<*>) {
            output.joinToString("\n") {
                printMap(applyPattern(it!!, pattern))
            }
        } else {
            printMap(applyPattern(output, pattern))
        }
    }

    private fun printMap(map: Map<String, String>): String {
        return map.entries.joinToString("") { e ->
            val printKey = getPrintAlias(e.key)
            when {
                printKey.isEmpty() -> "| ${e.value} "
                else -> "| $printKey: ${e.value} "
            }
        } + "|"
    }

    private fun getPrintAlias(src: String): String {
        return if (src.contains("_as_")) {
            src.substring(src.indexOf("_as_") + 4)
        } else src
    }

    private fun getAttrName(src: String): String {
        return if (src.contains("_as_")) {
            src.substring(0, src.indexOf("_as_"))
        } else src
    }

    private fun prettyNum(number: Number) = NumberFormat.getInstance().format(number)

    private fun defaultPattern(output: Any): List<String> {
        if (output is List<*> && output.isNotEmpty()) {
            return defaultPattern(output[0]!!)
        }

        return when (output) {
            is TransactionEntry -> listOf("id", "transactionDate", "amount", "transactionNote", "customNote")
            is Pair<*, *> -> listOf("first_as_", "second_as_")
            else -> listOf()
        }
    }

    private fun applyPattern(target: Any, attrs: List<String>): Map<String, String> {
        if (target is String) return mapOf(Pair("", target))

        val attributes = target.javaClass.kotlin.declaredMemberProperties
                .associateBy({ it.name }, { it.get(target) })

        return attrs.associateBy(
                { it },
                { it ->
                    convertToString(attributes[getAttrName(it)] ?: "")
                }
        )
    }

    private fun convertToString(any: Any): String {
        return when (any) {
            is Number -> prettyNum(any)
            else -> any.toString()
        }
    }

}