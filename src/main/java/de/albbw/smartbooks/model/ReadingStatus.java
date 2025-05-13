package de.albbw.smartbooks.model;

/**
 * Das Enum ReadingStatus repräsentiert den Status des Lesevorgangs eines Buches.
 *
 * Es gibt an, ob ein bestimmtes Buch gelesen wird, bereits gelesen wurde,
 * noch ungelesen ist oder ob der Status unbekannt ist.
 *
 * Die möglichen Werte sind:
 * - READING: Das Buch wird gerade gelesen.
 * - READ: Das Buch wurde gelesen.
 * - PLANNED: Das Buch wurde noch nicht gelesen.
 * - UNKNOWN: Der Status des Buches ist unbekannt.
 */
public enum ReadingStatus {
    READING,    // Das Buch wird gerade gelesen
    READ,       // Das Buch wurde gelesen
    PLANNED,    // Das Buch wurde noch nicht gelesen
    UNKNOWN     // Der Status des Buches ist unbekannt
}
