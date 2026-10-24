package com.scc.dao;

import com.scc.model.User;
import java.sql.*;

public class UserDao {

    public User login(String username, String password) {
        String sql = "SELECT id, username, password, roles, status FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                if ("PENDING".equalsIgnoreCase(rs.getString("status"))) {
                    System.out.println("⏳ Account PENDING — wait for approval");
                    return null;
                }
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("roles"),
                        rs.getString("status")
                );
            }
            System.out.println("❌ Invalid credentials");
        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
        }
        return null;
    }

    public boolean registerUser(String username, String password, String role) {
        String sql = "INSERT INTO users(username, password, roles, status) VALUES (?, ?, ?, 'PENDING')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role.toUpperCase());
            pstmt.executeUpdate();
            System.out.println("✅ Registered — awaiting approval");
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Registration failed: " + e.getMessage());
            return false;
        }
    }

    public ResultSet getAllUsers() throws SQLException {
        return DatabaseConnection.getConnection().createStatement()
                .executeQuery("SELECT id, username, roles, status FROM users");
    }

    public ResultSet getPendingUsers() throws SQLException {
        return DatabaseConnection.getConnection().createStatement()
                .executeQuery("SELECT id, username, roles, status FROM users WHERE status = 'PENDING'");
    }

    public boolean approveUser(int userId) {
        String sql = "UPDATE users SET status = 'APPROVED' WHERE id = ? AND status = 'PENDING'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean addUserWithStatus(String username, String password, String role, String status) {
        String sql = "INSERT INTO users(username, password, roles, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);
            pstmt.setString(4, status);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean updateUser(int id, String newRole, String newStatus) {
        String sql = "UPDATE users SET roles = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newRole);
            pstmt.setString(2, newStatus);
            pstmt.setInt(3, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
}