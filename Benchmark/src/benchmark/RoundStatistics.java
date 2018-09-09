package benchmark;

import edu.put.paxosstm.messaging.core.utils.Statistics;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class RoundStatistics implements Serializable {

    public RoundStatistics() {
        this.statistics = new Statistics();
        this.executionTime = 0;
        this.threadExecutionTimes = new LinkedHashMap<>();
        this.csvPrefix = "";
    }

    public RoundStatistics(Statistics statistics, long executionTime, Map<String, Long> threadExecutionTimes, String csvPrefix) {
        this.statistics = statistics;
        this.executionTime = executionTime;
        this.threadExecutionTimes = threadExecutionTimes;
        this.csvPrefix = csvPrefix;
    }

    public final Statistics statistics;
    public final long executionTime;
    public String csvPrefix;
    public final Map<String, Long> threadExecutionTimes;

    public String getCsv() {
        StringBuilder builder = new StringBuilder();
        if (!csvPrefix.isEmpty()) {
            builder.append(csvPrefix);
            builder.append(",");
        }
        builder.append(executionTime);
        builder.append(",");
        builder.append(statistics.toCsv());
        if(threadExecutionTimes != null && !threadExecutionTimes.isEmpty()) {
            builder.append(",");
            builder.append(
                    threadExecutionTimes.entrySet().stream()
                            .mapToLong(Map.Entry::getValue)
                            .sum()
            );
            builder.append('\n');

            String header = String.join(",", threadExecutionTimes.keySet());
            String values = threadExecutionTimes.values().stream().map(Objects::toString).collect(Collectors.joining(","));
            builder.append(header);
            builder.append('\n');
            builder.append(values);
        }
        return builder.toString();
    }

    public RoundStatistics add(RoundStatistics v) {
        Statistics s = statistics.add(v.statistics);
        long t = executionTime + v.executionTime;

        Map<String, Long> tt = new LinkedHashMap<>();
        if (v.threadExecutionTimes != null) {
            v.threadExecutionTimes.forEach(
                    (name, value) -> {
                        if (threadExecutionTimes.containsKey(name)) {
                            tt.put(name, threadExecutionTimes.get(name) + value);
                        } else {
                            tt.put(name, value);
                        }
                    }
            );
        }

        return new RoundStatistics(s, t, tt, "");
    }
}
