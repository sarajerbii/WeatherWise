package com.weatherwise.trip.entity;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

public enum RiskLevel {
    LOW, MODERATE, HIGH;

    /** Retourne le niveau le plus élevé (null si aucun niveau). */
    public static RiskLevel max(Collection<RiskLevel> levels) {
        return levels.stream().filter(Objects::nonNull).max(Comparator.naturalOrder()).orElse(null);
    }
}
