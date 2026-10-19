package com.Franz.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * SQLite connection + table creation. All JDBC setup lives here.
 */
public class DatabaseConnection {

    // Created in the project root (working directory) on first run.
    private static final String URL = "jdbc:sqlite:scenario.db";

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        try (Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    public static void initialize() {
        String sql = """
                CREATE TABLE IF NOT EXISTS medicines (
                    id         INTEGER PRIMARY KEY AUTOINCREMENT,
                    brand_name TEXT    NOT NULL,
                    category   TEXT    NOT NULL
                               CHECK (category IN ('ANTIBIOTIC', 'PAINKILLER', 'VITAMIN')),
                    price      REAL    NOT NULL,
                    stock      INTEGER NOT NULL
                )
                """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.execute();
        } catch (SQLException e) {
            System.out.println("Database setup failed: " + e.getMessage());
        }
    }
}
