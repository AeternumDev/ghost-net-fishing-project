/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sheasepherd.ghost.net.fishing.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ghost_nets")
public class GhostNet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Double latitude;
    
    @Column(nullable = false)
    private Double longitude;
    
    @Column(nullable = false)
    private String estimatedSize;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NetStatus status = NetStatus.REPORTED;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    private Person reporter;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retriever_id")
    private Person retriever;
    
    // Constructors
    public GhostNet() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public GhostNet(Double latitude, Double longitude, String estimatedSize) {
        this();
        this.latitude = latitude;
        this.longitude = longitude;
        this.estimatedSize = estimatedSize;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
    public String getEstimatedSize() {
        return estimatedSize;
    }
    
    public void setEstimatedSize(String estimatedSize) {
        this.estimatedSize = estimatedSize;
    }
    
    public NetStatus getStatus() {
        return status;
    }
    
    public void setStatus(NetStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Person getReporter() {
        return reporter;
    }
    
    public void setReporter(Person reporter) {
        this.reporter = reporter;
    }
    
    public Person getRetriever() {
        return retriever;
    }
    
    public void setRetriever(Person retriever) {
        this.retriever = retriever;
    }
    
    public String getLocationString() {
        return String.format("%.6f, %.6f", latitude, longitude);
    }
}
