package benchmark.core;

public class Logger {

    public static LogType logType = LogType.Verbose;

    public static void log(LogType type, String log) {
        if (type == LogType.All || type == logType || logType == LogType.All ||
                (type == LogType.CsvMinimal && logType == LogType.Csv) ) {
            System.out.print(log);
        }
    }

    public static void log(LogType type, String logFormat, Object... args) {
        log(type, String.format(logFormat, args));
    }

    public static void logln(LogType type, String log) {
        log(type, "%s\n", log);
    }
}
