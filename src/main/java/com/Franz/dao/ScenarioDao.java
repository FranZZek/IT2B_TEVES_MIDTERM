package com.Franz.dao;

import com.Franz.model.ScenarioEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object. Every SQL statement is here and uses PreparedStatement.
 */
public class ScenarioDao {

    public ScenarioDao() {
        DatabaseConnection.initialize();
    }

    public boolean insert(ScenarioEntity medicine) {
        String sql = "INSERT INTO medicines (brand_name, category, price, stock) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, medicine.brandName());
            ps.setString(2, medicine.category());
            ps.setDouble(3, medicine.price());
            ps.setInt(4, medicine.stock());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Insert failed: " + e.getMessage());
            return false;
        }
    }

    public List<ScenarioEntity> findAll() {
        String sql = "SELECT id, brand_name, category, price, stock FROM medicines ORDER BY id";
        List<ScenarioEntity> medicines = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                medicines.add(new ScenarioEntity(
                        rs.getInt("id"),
                        rs.getString("brand_name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("stock")));
            }

        } catch (SQLException e) {
            System.out.println("Could not load medicines: " + e.getMessage());
        }

        return medicines;
    }
}
