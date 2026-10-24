package com.scc.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void initialize() {
        String createUsers = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                roles TEXT NOT NULL,
                status TEXT NOT NULL
            );
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createUsers);
            ensureDefaultAdmin(conn);
            System.out.println("✅ Database initialized");

        } catch (SQLException e) {
            System.err.println("❌ DB Init Error: " + e.getMessage());
        }
    }

    private static void ensureDefaultAdmin(Connection conn) throws SQLException {
        String check = "SELECT COUNT(*) FROM users WHERE roles = 'SECRETARY'";
        String insert = """
            INSERT OR IGNORE INTO users(username, password, roles, status)
            VALUES('admin', 'admin123', 'SECRETARY', 'APPROVED');
            """;

        try (Statement stmt = conn.createStatement();
             var rs = stmt.executeQuery(check)) {
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.executeUpdate(insert);
                System.out.println("🔑 Default Admin: admin / admin123");
            }
        }
    }
}