package com.appbase.components

import timber.log.Timber
import java.util.regex.Matcher
import java.util.regex.Pattern

class LoggingTree : Timber.Tree() {
    private val CALL_STACK_INDEX = 4
    private val ANONYMOUS_CLASS : Pattern = Pattern.compile("(\\$\\d+)+$")

    override fun log(priority : Int , tag : String? , message : String , t : Throwable?) {
        // DO NOT switch this to Thread.getCurrentThread().getStackTrace(). The test will pass
        // because Robolectric runs them on the JVM but on Android the elements are different.
        var modifiedMessage = message
        val stackTrace =
            Throwable().stackTrace
        check(stackTrace.size > CALL_STACK_INDEX) { "Synthetic stacktrace didn't have enough elements: are you using proguard?" }
        val clazz = extractClassName(stackTrace[CALL_STACK_INDEX])
        val lineNumber = stackTrace[CALL_STACK_INDEX].lineNumber
        modifiedMessage = ".($clazz.java:$lineNumber) - $modifiedMessage"
        super.log(priority , tag , modifiedMessage , t)
    }

    /**
     * Extract the class name without any anonymous class suffixes (e.g., `Foo$1`
     * becomes `Foo`).
     */
    private fun extractClassName(element : StackTraceElement) : String {
        var tag = element.className
        val m : Matcher = ANONYMOUS_CLASS.matcher(tag)
        if (m.find()) {
            tag = m.replaceAll("")
        }
        return tag.substring(tag.lastIndexOf('.') + 1)
    }
}