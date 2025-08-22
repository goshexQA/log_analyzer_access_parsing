import java.io.File;

public class FileDiagnostic {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("❌ Укажите путь к файлу как аргумент командной строки");
            System.out.println("Пример: java FileDiagnostic F:\\java_projects\\access");
            return;
        }

        String filePath = args[0];
        diagnoseFile(filePath);
    }

    public static void diagnoseFile(String filePath) {
        System.out.println("🔍 ДИАГНОСТИКА ФАЙЛА");
        System.out.println("====================");

        File file = new File(filePath);

        System.out.println("📋 Основная информация:");
        System.out.println("   Путь: " + filePath);
        System.out.println("   Абсолютный путь: " + file.getAbsolutePath());
        System.out.println("   Канонический путь: " + getCanonicalPathSafe(file));

        System.out.println("\n📊 Статус файла:");
        System.out.println("   Существует: " + (file.exists() ? "✅ ДА" : "❌ НЕТ"));
        System.out.println("   Это файл: " + (file.isFile() ? "✅ ДА" : "❌ НЕТ"));
        System.out.println("   Это папка: " + (file.isDirectory() ? "✅ ДА" : "❌ НЕТ"));
        System.out.println("   Скрытый: " + (file.isHidden() ? "✅ ДА" : "❌ НЕТ"));

        System.out.println("\n🔐 Права доступа:");
        System.out.println("   Можно читать: " + (file.canRead() ? "✅ ДА" : "❌ НЕТ"));
        System.out.println("   Можно писать: " + (file.canWrite() ? "✅ ДА" : "❌ НЕТ"));
        System.out.println("   Можно выполнять: " + (file.canExecute() ? "✅ ДА" : "❌ НЕТ"));

        System.out.println("\n📏 Размеры:");
        if (file.exists()) {
            System.out.println("   Размер: " + formatFileSize(file.length()));
            System.out.println("   Последнее изменение: " + new java.util.Date(file.lastModified()));
        }

        System.out.println("\n📂 Информация о папке:");
        if (file.isDirectory()) {
            System.out.println("   Это папка, содержимое:");
            String[] files = file.list();
            if (files != null && files.length > 0) {
                for (int i = 0; i < Math.min(10, files.length); i++) {
                    System.out.println("      - " + files[i]);
                }
                if (files.length > 10) {
                    System.out.println("      ... и еще " + (files.length - 10) + " файлов");
                }
            } else {
                System.out.println("      Папка пуста или нет доступа");
            }
        }

        System.out.println("\n💡 Рекомендации:");
        if (!file.exists()) {
            System.out.println("   ❌ Файл не существует. Проверьте путь.");
        } else if (!file.isFile()) {
            System.out.println("   ❌ Это не файл. Возможно, это папка.");
        } else if (!file.canRead()) {
            System.out.println("   ❌ Нет прав на чтение файла.");
            System.out.println("   💡 Попробуйте:");
            System.out.println("      1. Запустить IDE от имени Администратора");
            System.out.println("      2. Проверить права доступа к файлу");
            System.out.println("      3. Скопировать файл в другую папку");
        } else {
            System.out.println("   ✅ Файл доступен для чтения!");
        }
    }

    private static String getCanonicalPathSafe(File file) {
        try {
            return file.getCanonicalPath();
        } catch (Exception e) {
            return "Ошибка: " + e.getMessage();
        }
    }

    private static String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " байт";
        if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
        if (bytes < 1024 * 1024 * 1024) return (bytes / (1024 * 1024)) + " MB";
        return (bytes / (1024 * 1024 * 1024)) + " GB";
    }
}