/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.sheasepherd.ghost.net.fishing.enums;

public enum NetStatus {
    REPORTED,           // Gemeldet, wartet auf Bergung
    RETRIEVAL_PENDING,  // Bergung zugewiesen, wird bearbeitet
    RETRIEVED,          // Erfolgreich geborgen
    LOST                // Als verschollen markiert
}