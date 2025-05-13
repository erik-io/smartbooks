package de.albbw.smartbooks.model;

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
public enum DataSource {
    CSV,
    JSON,
    XML,
    API,
    UNKNOWN
}
