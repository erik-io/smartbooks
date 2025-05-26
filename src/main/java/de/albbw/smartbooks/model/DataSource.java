package de.albbw.smartbooks.model;

import lombok.Getter;

/**
 * Das Enum DataSource repräsentiert verschiedene Arten von Datenquellen,
 * die genutzt werden können, um Informationen zu beziehen.
 * <p>
 * Die möglichen Werte sind:
 * <ul>
 * <li>CSV: Kennzeichnet eine Datenquelle in Form einer CSV-Datei.</li>
 * <li>JSON: Kennzeichnet eine Datenquelle in Form einer JSON-Datei.</li>
 * <li>XML: Kennzeichnet eine Datenquelle in Form einer XML-Datei.</li>
 * <li>API: Kennzeichnet eine Datenquelle in Form einer API.</li>
 * <li>UNKNOWN: Die Datenquelle ist unbekannt oder nicht spezifiziert.</li>
 * </ul>
 */
@Getter
public enum DataSource {
    CSV("CSV-Datei"),
    JSON("JSON-Datei"),
    XML("XML-Datei"),
    API("API"),
    UNKNOWN("Unbekannt");

    private final String label;

    DataSource(String label) {
        this.label = label;
    }
}
