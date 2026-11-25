package com.weatherwise.airquality.entity;

// Cette classe est aussi stockée DANS le document de l'observation
public class AirQualityIndex {

    private Integer aqiValue;
    private AqiCategory category;
    private PollutantType dominantPollutant;

    public AirQualityIndex() {
    }

    public Integer getAqiValue() {
        return aqiValue;
    }

    public void setAqiValue(Integer aqiValue) {
        this.aqiValue = aqiValue;
    }

    public AqiCategory getCategory() {
        return category;
    }

    public void setCategory(AqiCategory category) {
        this.category = category;
    }

    public PollutantType getDominantPollutant() {
        return dominantPollutant;
    }

    public void setDominantPollutant(PollutantType dominantPollutant) {
        this.dominantPollutant = dominantPollutant;
    }
}
