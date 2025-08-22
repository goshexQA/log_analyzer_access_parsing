package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogEntry {
    private final String ipAddress;
    private final LocalDateTime timestamp;
    private final String method;
    private final String path;
    private final int responseCode;
    private final long bytesSent;
    private final String referer;
    private final String userAgent;

    // Конструктор
    public LogEntry(String ipAddress, LocalDateTime timestamp, String method,
                    String path, int responseCode, long bytesSent,
                    String referer, String userAgent) {
        this.ipAddress = ipAddress;
        this.timestamp = timestamp;
        this.method = method;
        this.path = path;
        this.responseCode = responseCode;
        this.bytesSent = bytesSent;
        this.referer = referer;
        this.userAgent = userAgent;
    }

    // Геттеры
    public String getIpAddress() { return ipAddress; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getMethod() { return method; }
    public String getPath() { return path; }
    public int getResponseCode() { return responseCode; }
    public long getBytesSent() { return bytesSent; }
    public String getReferer() { return referer; }
    public String getUserAgent() { return userAgent; }

    @Override
    public String toString() {
        return String.format("IP: %s, Time: %s, Method: %s, Path: %s, Code: %d, Bytes: %d",
                ipAddress, timestamp, method, path, responseCode, bytesSent);
    }
}
