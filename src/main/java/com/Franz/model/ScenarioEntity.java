package com.Franz.model;

/**
 * One row of the medicines table (5 attributes).
 */
public record ScenarioEntity(int id, String brandName, String category, double price, int stock) {

    // For a new medicine that has no id yet; SQLite assigns it on insert.
    public ScenarioEntity(String brandName, String category, double price, int stock) {
        this(0, brandName, category, price, stock);
    }
}
