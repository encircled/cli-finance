package cz.encircled.cli.finance

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ContextTest {

    @Test
    fun testCommandParsing() {
        val context = FinanceContext()
        val command = context.parseCommand("cat list -f=/Users/test -max=50 -t")
        assertEquals("cat list", command.commandName)
        assertEquals(mapOf(
                Pair("f", "/Users/test"),
                Pair("max", "50"),
                Pair("t", "")
        ), command.namedArgs)
    }

}