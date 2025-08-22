package service;

import model.LogEntry;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParser {

    private static final String LOG_PATTERN =
            "^([\\d.]+) \\- \\- \\[([^\\]]+)\\] \"([A-Z]+) ([^\"]+?) HTTP/[\\d.]+\" (\\d{3}) (\\d+) \"([^\"]*)\" \"([^\"]*)\"";

    private static final Pattern PATTERN = Pattern.compile(LOG_PATTERN);
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

    public List<LogEntry> parseLogFile(String filePath) {
        List<LogEntry> entries = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath), 65536)) { // 64KB buffer {
            String line;
            int lineCount = 0;

            while ((line = reader.readLine()) != null) {
                lineCount++;

                // Пропускаем пустые строки сразу
                if (line.trim().isEmpty()) {
                    continue;
                }

                LogEntry entry = parseLine(line);
                if (entry != null) {
                    entries.add(entry);
                }

                // Вывод прогресса каждые 10,000 строк
                if (lineCount % 10000 == 0) {
                    System.out.printf("⏳ Обработано строк: %,d%n", lineCount);
                }
            }

            System.out.printf("✅ Всего обработано строк: %,d%n", lineCount);
            System.out.printf("✅ Успешно распарсено записей: %,d%n", entries.size());

        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        }

        return entries;
    }

    private LogEntry parseLine(String line) {
        System.out.println("=== НАЧАЛО ПАРСИНГА ===");
        System.out.println("Строка: " + line);

        Matcher matcher = PATTERN.matcher(line);

        if (matcher.find()) {
            System.out.println("✅ Regex совпал! Групп найдено: " + matcher.groupCount());

            try {
                // Извлекаем все группы и выводим их для отладки
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    System.out.println("Группа " + i + ": '" + matcher.group(i) + "'");
                }

                String ipAddress = matcher.group(1);
                String dateTimeStr = matcher.group(2);
                String method = matcher.group(3);
                String path = matcher.group(4);
                int responseCode = Integer.parseInt(matcher.group(5));
                long bytesSent = Long.parseLong(matcher.group(6));
                String referer = matcher.group(7);
                String userAgent = matcher.group(8);

                System.out.println("📋 Извлеченные данные:");
                System.out.println("IP: " + ipAddress);
                System.out.println("DateTime: " + dateTimeStr);
                System.out.println("Method: " + method);
                System.out.println("Path: " + path);
                System.out.println("Response: " + responseCode);
                System.out.println("Bytes: " + bytesSent);
                System.out.println("Referer: " + referer);
                System.out.println("UserAgent: " + userAgent);

                // Преобразуем дату - пробуем разные форматы
                LocalDateTime timestamp;
                try {
                    timestamp = LocalDateTime.parse(dateTimeStr, DATE_FORMATTER);
                    System.out.println("✅ Дата преобразована: " + timestamp);
                } catch (Exception e) {
                    System.err.println("❌ Ошибка преобразования даты: " + e.getMessage());
                    System.err.println("Пробуем альтернативный формат...");

                    // Пробуем убрать квадратные скобки если они есть
                    if (dateTimeStr.startsWith("[") && dateTimeStr.endsWith("]")) {
                        dateTimeStr = dateTimeStr.substring(1, dateTimeStr.length() - 1);
                    }

                    // Пробуем другой форматтер
                    try {
                        DateTimeFormatter altFormatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z");
                        timestamp = LocalDateTime.parse(dateTimeStr, altFormatter);
                        System.out.println("✅ Дата преобразована альтернативным способом: " + timestamp);
                    } catch (Exception e2) {
                        System.err.println("❌ Альтернативный формат тоже не сработал: " + e2.getMessage());
                        return null;
                    }
                }

                // Создаем объект LogEntry
                LogEntry entry = new LogEntry(ipAddress, timestamp, method, path,
                        responseCode, bytesSent, referer, userAgent);

                System.out.println("✅ Объект LogEntry успешно создан!");
                System.out.println("=== КОНЕЦ ПАРСИНГА ===");
                return entry;

            } catch (Exception e) {
                System.err.println("❌ Ошибка при извлечении данных: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        } else {
            System.err.println("❌ Regex не совпал со строкой");
            System.err.println("Проверь регулярное выражение: " + LOG_PATTERN);
            return null;
        }
    }

    public static void main(String[] args) {
          LogParser parser = new LogParser();

        String testLine = "37.231.123.209 - - [25/Sep/2022:06:25:04 +0300] \"GET /engine.php?rss=1&json=1&p=156&lg=1 HTTP/1.0\" 200 61096 \"https://nova-news.ru/search/?rss=1&lg=1\" \"Mozilla/5.0 (Linux; Android 6.0.1; SM-J500M Build/MMB29M) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.91 Mobile Safari/537.36\"";

        System.out.println("🧪 ТЕСТИРУЕМ ПАРСИНГ");
        LogEntry entry = parser.parseLine(testLine);

        if (entry != null) {
            System.out.println("🎉 УСПЕХ! Объект создан:");
            System.out.println("IP: " + entry.getIpAddress());
            System.out.println("Time: " + entry.getTimestamp());
            System.out.println("Method: " + entry.getMethod());
        } else {
            System.out.println("💥 ПАРСИНГ ПРОВАЛИЛСЯ");
        }
    }
}
