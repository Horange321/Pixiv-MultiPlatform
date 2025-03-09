package top.kagg886.pmf.util

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import kotlinx.datetime.*
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.char
import okio.Path
import okio.buffer
import org.koin.ext.getFullName
import top.kagg886.pmf.BuildConfig
import top.kagg886.pmf.backend.cachePath

val Any.logger: Logger
    get() = Logger.withTag(this::class.getFullName())

fun initFileLogger() {
    Logger.setTag(BuildConfig.APP_BASE_PACKAGE)
    Logger.addLogWriter(FileLogger(cachePath.resolve("log")))
}

/**
 * # 写入到文件的Logger。
 *
 * 传入一个文件夹以代表base-path。在里面会拥有：latest.log和archive/YYYY-MM-dd.log
 *
 * @param baseFile 基础文件。
 * @author kagg886
 */
class FileLogger(baseFile: Path) : LogWriter(), AutoCloseable {
    private val latestPath = baseFile.resolve("latest.log")
    private val archivePath = baseFile.resolve("archive")

    init {
        if (!archivePath.exists()) {
            archivePath.mkdirs()
        }
        if (latestPath.exists()) {
            latestPath.delete()
        }
    }

    private val latest = latestPath.sink().buffer()
    private val archive =
        archivePath.resolve("${Clock.System.now().toLogFileString()}.log").sink(append = true).buffer()

    //2023-10-01 12:34:56 INFO  com.example.MyClass - User login successfully
    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        val time = Clock.System.now().toLogTimeString()
        val lines = message.lines()

        val header = "$time ${severity.name.uppercase().padEnd(5, ' ')} $tag - "
        val body = lines.drop(1).joinToString("\n") { it.padStart(header.length + it.length, ' ') }

        val print = "$header${lines[0]}${if (body.isNotBlank()) "\n$body" else ""}"

        latest.writeUtf8(print)
        latest.writeUtf8("\n")
        archive.writeUtf8(print)
        archive.writeUtf8("\n")
    }

    override fun close() {
        latest.flush()
        latest.close()
        archive.flush()
        archive.close()
    }
}

private fun Instant.toLogTimeString() = toLocalDateTime(TimeZone.currentSystemDefault())
    .toString()
    .substringBefore(".")

private fun Instant.toLogFileString() = format(DateTimeComponents.Format {
    year()
    char('-')
    monthNumber()
    char('-')
    dayOfMonth()
})
