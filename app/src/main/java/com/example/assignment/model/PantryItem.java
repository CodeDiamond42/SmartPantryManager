package com.example.assignment.model;


public class PantryItem {

    private long id;
    private String name;            // the name the user enters
    private String normalizedName;  // lower-case singular form used for matching
    private double quantity;
    private String unit;
    private String expiryDate;

    // Constructor for a new item that has not been saved yet (no id yet).
    public PantryItem(String name, double quantity, String unit, String expiryDate) {
        this(-1, name, null, quantity, unit, expiryDate);
    }

    // Constructor used when reading an existing row from the database.
    public PantryItem(long id, String name, String normalizedName,
                      double quantity, String unit, String expiryDate) {
        this.id = id;
        this.name = name;
        this.normalizedName = normalizedName;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNormalizedName() { return normalizedName; }
    public void setNormalizedName(String normalizedName) { this.normalizedName = normalizedName; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
}
