/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sheasepherd.ghost.net.fishing.enums;

public enum NetStatus {
    REPORTED("Gemeldet"),
    RETRIEVAL_PENDING("Bergung bevorstehend"),
    RETRIEVED("Geborgen"),
    LOST("Verschollen");
    
    private final String displayName;
    
    NetStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
