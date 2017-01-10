package cz.koto.misak.securityshowcase.utility

import android.util.Log


object Logcat {
    private var sEnabled = true
    private var sTag = "LOGCAT"

    private var sShowCodeLocation = true
    private var sShowCodeThread = true
    private var sShowCodeLine = true


    fun initialize(tag: String, logsEnabled: Boolean) {
        sTag = tag
        sEnabled = logsEnabled
    }


    fun setEnabled(enabled: Boolean) {
        sEnabled = enabled
    }


    fun setTag(tag: String) {
        sTag = tag
    }


    fun setShowCodeLocation(showCodeLocation: Boolean) {
        sShowCodeLocation = showCodeLocation
    }


    fun setShowCodeThread(showCodeThread: Boolean) {
        sShowCodeThread = showCodeThread
    }


    fun setShowCodeLine(showCodeLine: Boolean) {
        sShowCodeLine = showCodeLine
    }


    fun d(msg: String, vararg args: Any) {
        if (sEnabled) Log.d(sTag, codeLocation.toString() + formatMessage(msg, *args))
    }


    fun e(msg: String, vararg args: Any) {
        if (sEnabled) Log.e(sTag, codeLocation.toString() + formatMessage(msg, *args))
    }


    fun e(tr: Throwable, msg: String, vararg args: Any) {
        if (sEnabled) Log.e(sTag, codeLocation.toString() + formatMessage(msg, *args), tr)
    }


    fun i(msg: String, vararg args: Any) {
        if (sEnabled) Log.i(sTag, codeLocation.toString() + formatMessage(msg, *args))
    }


    fun v(msg: String, vararg args: Any) {
        if (sEnabled) Log.v(sTag, codeLocation.toString() + formatMessage(msg, *args))
    }


    fun w(msg: String, vararg args: Any) {
        if (sEnabled) Log.w(sTag, codeLocation.toString() + formatMessage(msg, *args))
    }


    fun wtf(msg: String, vararg args: Any) {
        if (sEnabled) Log.wtf(sTag, codeLocation.toString() + formatMessage(msg, *args))
    }


    private fun formatMessage(msg: String, vararg args: Any): String {
        return if (args.size == 0) msg else String.format(msg, *args)
    }


    private val codeLocation: CodeLocation
        get() = getCodeLocation(3)


    private fun getCodeLocation(depth: Int): CodeLocation {
        val stackTrace = Throwable().stackTrace
        val filteredStackTrace = arrayOfNulls<StackTraceElement>(stackTrace.size - depth)
        System.arraycopy(stackTrace, depth, filteredStackTrace, 0, filteredStackTrace.size)
        return CodeLocation(filteredStackTrace)
    }


    private class CodeLocation internal constructor(var mStackTrace: Array<StackTraceElement?>) {
        val mThread: String?
        val mClassName: String?
        val mFileName: String?
        val mMethod: String?
        val mLineNumber: Int?


        init {
            val root = mStackTrace[0]
            mThread = Thread.currentThread().name
            mFileName = root?.fileName
            val className = root?.className
            mClassName = className?.substring(className.lastIndexOf('.') + 1)
            mMethod = root?.methodName
            mLineNumber = root?.lineNumber
        }


        override fun toString(): String {
            val builder = StringBuilder()
            if (sShowCodeLocation) {
                if (sShowCodeThread) {
                    builder.append('[')
                    builder.append(mThread)
                    builder.append("] ")
                }
                builder.append("(")
                builder.append(mFileName)
                if (sShowCodeLine) {
                    builder.append(':')
                    builder.append(mLineNumber)
                }
                builder.append(") ")
                builder.append(mMethod)
                builder.append("(): ")
            }
            return builder.toString()
        }
    }
}

fun <A : Any> A.log(msg: String) = apply { Logcat.d("$msg %s", this.toString()) }
