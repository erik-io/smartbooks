package de.albbw.smartbooks.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Das Enum ReadingStatus repräsentiert den Status des Lesevorgangs eines Buches.
 * <p>
 * Es gibt an, ob ein bestimmtes Buch gelesen wird, bereits gelesen wurde,
 * noch ungelesen ist oder ob der Status unbekannt ist.
 * <p>
 * Die möglichen Werte sind:
 * <ul>
 * <li>READING: Das Buch wird gerade gelesen.</li>
 * <li>READ: Das Buch wurde gelesen.</li>
 * <li>PLANNED: Das Buch wurde noch nicht gelesen.</li>
 * <li>UNKNOWN: Der Status des Buches ist unbekannt.</li>
 * </ul>
 */
public enum ReadingStatus {
    READING,    // Das Buch wird gerade gelesen
    @JsonProperty("gelesen") READ,       // Das Buch wurde gelesen
    @JsonProperty("geplant") PLANNED,    // Das Buch wurde noch nicht gelesen
    UNKNOWN     // Der Status des Buches ist unbekannt
}
