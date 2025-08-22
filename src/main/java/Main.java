import service.LogParser;
import service.StatisticsCalculator;
import model.LogEntry;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("🚀 Запуск анализатора лог-файлов");
        System.out.println("=================================");

        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Введите путь к лог-файлу: ");
            String filePath = scanner.nextLine().trim();

            if (filePath.isEmpty()) {
                System.out.println("❌ Путь к файлу не может быть пустым!");
                return;
            }

            System.out.println("\n⏳ Начинаем парсинг файла...");
            System.out.println("Файл большой (36.7 МБ), это займет время...");
            System.out.println("Для отмены нажми Ctrl+C");

            long startTime = System.currentTimeMillis();

            // Парсим файл с прогрессом
            LogParser parser = new LogParser();
            List<LogEntry> logEntries = parser.parseLogFile(filePath);

            long parseTime = System.currentTimeMillis() - startTime;

            if (logEntries == null || logEntries.isEmpty()) {
                System.out.println("❌ Не удалось распарсить файл");
                return;
            }

            System.out.printf("✅ Парсинг завершен за %,d мс%n", parseTime);
            System.out.printf("📊 Найдено записей: %,d%n", logEntries.size());

            // Быстрая статистика без детального анализа
            System.out.println("\n📈 Базовая статистика:");
            showQuickStats(logEntries);

            // Предлагаем полный анализ
            System.out.print("\n🔍 Выполнить полный анализ статистики? (y/n): ");
            String choice = scanner.nextLine().trim().toLowerCase();

            if (choice.equals("y") || choice.equals("yes")) {
                System.out.println("\n⏳ Выполняем полный анализ...");
                StatisticsCalculator calculator = new StatisticsCalculator();
                calculator.calculateStatistics(logEntries);
            }

        } catch (Exception e) {
            System.err.println("💥 Ошибка: " + e.getMessage());
        } finally {
            scanner.close();
            System.out.println("\n👋 Работа завершена");
        }
    }

    // Быстрая статистика без Stream API
    private static void showQuickStats(List<LogEntry> entries) {
        int total = entries.size();
        System.out.printf("Общее количество запросов: %,d%n", total);

        // Простой подсчет кодов ответов
        int success = 0, errors = 0;
        for (LogEntry entry : entries) {
            int code = entry.getResponseCode();
            if (code >= 200 && code < 300) success++;
            if (code >= 400) errors++;
        }

        System.out.printf("Успешные запросы: %,d (%.1f%%)%n",
                success, (success * 100.0 / total));
        System.out.printf("Ошибки: %,d (%.1f%%)%n",
                errors, (errors * 100.0 / total));
    }
}