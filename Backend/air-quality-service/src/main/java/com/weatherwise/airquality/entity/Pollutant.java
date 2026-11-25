package com.weatherwise.airquality.entity;

// Cette classe est stockée DANS le document de l'observation (pas de collection séparée)
public class Pollutant {

    private PollutantType type;
    private Double value;
    private String unit;

    public Pollutant() {
    }

    public Pollutant(PollutantType type, Double value, String unit) {
        this.type = type;
        this.value = value;
        this.unit = unit;
    }

    public PollutantType getType() {
        return type;
    }

    public void setType(PollutantType type) {
        this.type = type;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
