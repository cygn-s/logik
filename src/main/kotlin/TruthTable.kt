package logik

import logik.Logik.logikString
import java.lang.Integer.max
import java.lang.StringBuilder

class TruthTable(val statement: LogikStatement) {

    val entries: Map<VariableContext, Boolean>

    init {
        val variableCount = statement.variables.size
        val possibilities = arrayOfNulls<Array<Boolean>>(Math.pow(2.0, variableCount.toDouble()).toInt())
        for (possibiltyIndex in possibilities.indices) {
            val subPossibilities = arrayOfNulls<Boolean>(variableCount)
            for (variableIndex in statement.variables.indices) {
                val switchFrequency = 1 / (Math.pow(2.0, (variableIndex + 1).toDouble()))
                val period = ((possibilities.size * switchFrequency).toInt())
                subPossibilities[variableIndex] = (possibiltyIndex % (2 * period)) / period < 1
            }
            possibilities[possibiltyIndex] = subPossibilities.requireNoNulls()
        }
        possibilities.requireNoNulls()
        val contexts = mutableListOf<VariableContext>()
        for (possibility in possibilities) {
            val context = VariableContext(statement)
            for ((index, value) in possibility!!.withIndex()) {
                val prep = statement.variables[index]
                context.setValue(prep, value)
            }
            contexts.add(context)
        }
        entries = contexts.map { it to statement.evaluate(it) }.toMap()
    }

    override fun toString(): String {
        val booleanSize = max(Logik.trueText.length, Logik.falseText.length)
        val builder = StringBuilder()
        for(variable in statement.variables) {
            builder.append("|_${variable.token.value}".padEnd(3 + booleanSize, '_'))
        }
        builder.appendln("|_${statement.text}_|")
        for((context, value) in entries) {
            for(variable in statement.variables) {
                val variableValue = context.getValue(variable)
                builder.append("| ${variableValue.logikString()}".padEnd(3 + booleanSize))
            }
            builder.appendln("| ${value.logikString()}".padEnd(statement.text.length + 2) + " |")
        }
        return builder.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TruthTable
        val theseEntries = entries.entries.toList()
        val otherEntries = other.entries.entries.toList()
        for(i in theseEntries.indices) {
            val thisEntry = theseEntries[i]
            val otherEntry = otherEntries[i]
            if(thisEntry.key != otherEntry.key) {
                return false
            }
            if(thisEntry.value != otherEntry.value) {
                return false
            }
        }

        return true
    }

    override fun hashCode(): Int {
        return entries.hashCode()
    }
}