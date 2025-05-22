/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sheasepherd.ghost.net.fishing.beans;

import com.sheasepherd.ghostnet.entity.GhostNet;
import com.sheasepherd.ghostnet.service.GhostNetService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@RequestScoped
public class ReportNetBean {
    
    @Inject
    private GhostNetService ghostNetService;
    
    private Double latitude;
    private Double longitude;
    private String estimatedSize;
    private String reporterName;
    private String reporterPhone;
    private boolean anonymous = false;
    
    public String reportNet() {
        try {
            if (latitude == null || longitude == null || 
                estimatedSize == null || estimatedSize.trim().isEmpty()) {
                addMessage(FacesMessage.SEVERITY_ERROR, 
                          "Fehler", "Bitte alle Pflichtfelder ausfüllen.");
                return null;
            }
            
            if (!anonymous && (reporterName == null || reporterName.trim().isEmpty())) {
                addMessage(FacesMessage.SEVERITY_ERROR, 
                          "Fehler", "Bitte Namen angeben oder anonyme Meldung wählen.");
                return null;
            }
            
            GhostNet net = new GhostNet(latitude, longitude, estimatedSize.trim());
            
            String name = anonymous ? null : reporterName;
            String phone = anonymous ? null : reporterPhone;
            
            ghostNetService.reportGhostNet(net, name, phone);
            
            addMessage(FacesMessage.SEVERITY_INFO, 
                      "Erfolg", "Geisternetz erfolgreich gemeldet.");
            
            clearForm();
            return "index?faces-redirect=true";
            
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, 
                      "Fehler", "Fehler beim Melden des Geisternetzes: " + e.getMessage());
            return null;
        }
    }
    
    private void clearForm() {
        latitude = null;
        longitude = null;
        estimatedSize = null;
        reporterName = null;
        reporterPhone = null;
        anonymous = false;
    }
    
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, summary, detail));
    }
    
    // Getters and Setters
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
    
    public String getReporterName() {
        return reporterName;
    }
    
    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }
    
    public String getReporterPhone() {
        return reporterPhone;
    }
    
    public void setReporterPhone(String reporterPhone) {
        this.reporterPhone = reporterPhone;
    }
    
    public boolean isAnonymous() {
        return anonymous;
    }
    
    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }
}