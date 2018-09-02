package benchmark.core;

public class Logger {

    public static LogType logType = LogType.Verbose;

    public static void logCsv(String log) {
        if (logType == LogType.Csv  || logType == LogType.All) {
            System.out.print(log);
        }
    }

    public static void logCsv(String logFormat, Object... args) {
        logCsv(String.format(logFormat, args));
    }

    public static void log(String log) {
        if (logType == LogType.Verbose  || logType == LogType.All) {
            System.out.print(log);
        }
    }

    public static void log(String logFormat, Object... args) {
        log(String.format(logFormat, args));
    }
}
