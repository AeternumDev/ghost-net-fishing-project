/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sheasepherd.ghost.net.fishing.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "persons")
public class Person {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @OneToMany(mappedBy = "reporter", fetch = FetchType.LAZY)
    private List<GhostNet> reportedNets;
    
    @OneToMany(mappedBy = "retriever", fetch = FetchType.LAZY)
    private List<GhostNet> retrievedNets;
    
    // Constructors
    public Person() {}
    
    public Person(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public List<GhostNet> getReportedNets() {
        return reportedNets;
    }
    
    public void setReportedNets(List<GhostNet> reportedNets) {
        this.reportedNets = reportedNets;
    }
    
    public List<GhostNet> getRetrievedNets() {
        return retrievedNets;
    }
    
    public void setRetrievedNets(List<GhostNet> retrievedNets) {
        this.retrievedNets = retrievedNets;
    }
    
    public boolean isAnonymous() {
        return phoneNumber == null || phoneNumber.trim().isEmpty();
    }
}