package service;

import model.LogEntry;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticsCalculator {

    public void calculateStatistics(List<LogEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            System.out.println("Нет данных для анализа");
            return;
        }

        System.out.println("=== СТАТИСТИКА ПО ЛОГАМ ===");
        System.out.println("Общее количество запросов: " + entries.size());

        calculateResponseCodeStats(entries);
        calculateTopIPs(entries);
        calculateTrafficStats(entries);
        calculateMethodStats(entries);
        calculateHourlyActivity(entries);
        calculateBrowserStats(entries);
    }

    private void calculateResponseCodeStats(List<LogEntry> entries) {
        System.out.println("\n1. Статистика по кодам ответов:");

        Map<Integer, Long> responseCodeStats = entries.stream()
                .collect(Collectors.groupingBy(LogEntry::getResponseCode, Collectors.counting()));

        responseCodeStats.forEach((code, count) -> {
            double percentage = (count * 100.0) / entries.size();
            System.out.printf("  Код %d: %d запросов (%.1f%%)%n", code, count, percentage);
        });
    }

    private void calculateTopIPs(List<LogEntry> entries) {
        System.out.println("\n2. Топ 10 IP-адресов:");

        Map<String, Long> ipStats = entries.stream()
                .collect(Collectors.groupingBy(LogEntry::getIpAddress, Collectors.counting()));

        ipStats.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(10)
                .forEach(entry -> {
                    double percentage = (entry.getValue() * 100.0) / entries.size();
                    System.out.printf("  %s: %d запросов (%.1f%%)%n",
                            entry.getKey(), entry.getValue(), percentage);
                });
    }

    private void calculateTrafficStats(List<LogEntry> entries) {
        System.out.println("\n3. Статистика трафика:");

        long totalTraffic = entries.stream()
                .mapToLong(LogEntry::getBytesSent)
                .sum();

        long maxTraffic = entries.stream()
                .mapToLong(LogEntry::getBytesSent)
                .max()
                .orElse(0);

        double avgTraffic = entries.stream()
                .mapToLong(LogEntry::getBytesSent)
                .average()
                .orElse(0);

        System.out.printf("  Общий трафик: %d байт (%.2f MB)%n",
                totalTraffic, totalTraffic / (1024.0 * 1024.0));
        System.out.printf("  Максимальный размер ответа: %d байт%n", maxTraffic);
        System.out.printf("  Средний размер ответа: %.0f байт%n", avgTraffic);
    }

    private void calculateMethodStats(List<LogEntry> entries) {
        System.out.println("\n4. Статистика по HTTP-методам:");

        Map<String, Long> methodStats = entries.stream()
                .collect(Collectors.groupingBy(LogEntry::getMethod, Collectors.counting()));

        methodStats.forEach((method, count) -> {
            double percentage = (count * 100.0) / entries.size();
            System.out.printf("  %s: %d запросов (%.1f%%)%n", method, count, percentage);
        });
    }

    private void calculateHourlyActivity(List<LogEntry> entries) {
        System.out.println("\n5. Активность по часам:");

        Map<Integer, Long> hourlyStats = entries.stream()
                .collect(Collectors.groupingBy(
                        entry -> entry.getTimestamp().getHour(),
                        Collectors.counting()
                ));

        hourlyStats.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    System.out.printf("  Час %02d:00: %d запросов%n",
                            entry.getKey(), entry.getValue());
                });
    }

    private void calculateBrowserStats(List<LogEntry> entries) {
        System.out.println("\n6. Статистика по браузерам:");

        Map<String, Long> browserStats = entries.stream()
                .collect(Collectors.groupingBy(
                        entry -> {
                            String userAgent = entry.getUserAgent().toLowerCase();
                            if (userAgent.contains("chrome")) return "Chrome";
                            if (userAgent.contains("firefox")) return "Firefox";
                            if (userAgent.contains("safari")) return "Safari";
                            if (userAgent.contains("edge")) return "Edge";
                            if (userAgent.contains("opera")) return "Opera";
                            return "Other";
                        },
                        Collectors.counting()
                ));

        browserStats.forEach((browser, count) -> {
            double percentage = (count * 100.0) / entries.size();
            System.out.printf("  %s: %d запросов (%.1f%%)%n", browser, count, percentage);
        });
    }
}
