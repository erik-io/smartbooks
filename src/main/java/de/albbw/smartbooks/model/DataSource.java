package de.albbw.smartbooks.model;

/**
 * Das Enum DataSource repräsentiert verschiedene Arten von Datenquellen,
 * die genutzt werden können, um Informationen zu beziehen.
 *
 * Die möglichen Werte sind:
 * - CSV: Kennzeichnet eine Datenquelle in Form einer CSV-Datei.
 * - JSON: Kennzeichnet eine Datenquelle in Form einer JSON-Datei.
 * - XML: Kennzeichnet eine Datenquelle in Form einer XML-Datei.
 * - API: Kennzeichnet eine Datenquelle in Form einer API.
 * - UNKNOWN: Die Datenquelle ist unbekannt oder nicht spezifiziert.
 */
public enum DataSource {
    CSV,
    JSON,
    XML,
    API,
    UNKNOWN
}
