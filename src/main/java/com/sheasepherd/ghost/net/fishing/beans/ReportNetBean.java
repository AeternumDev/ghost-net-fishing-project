/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sheasepherd.ghost.net.fishing.beans;

import com.sheasepherd.ghost.net.fishing.entities.GhostNet;
import com.sheasepherd.ghost.net.fishing.services.GhostNetService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.Date;

@Named
@RequestScoped
public class ReportNetBean {

    @Inject
    private GhostNetService ghostNetService;    private GhostNet newNet = new GhostNet();
    private String reporterName;
    private String reporterPhone;
    private boolean anonymousReport = false;
    
    // Properties for form binding
    private Double latitude;
    private Double longitude;
    private String estimatedSize;
    private boolean anonymous = false;    public String reportNet() {
        try {
            // Set coordinates and size from form
            if (latitude != null && longitude != null) {
                newNet.setLatitude(latitude);
                newNet.setLongitude(longitude);
            }
            newNet.setDescription(estimatedSize); // Store estimated size in description
            
            if (anonymous || anonymousReport) {
                reporterName = null;
                reporterPhone = null;
            } else {
                if ((reporterName == null || reporterName.trim().isEmpty()) && 
                    (reporterPhone != null && !reporterPhone.trim().isEmpty())) {
                     addMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Bitte geben Sie einen Namen an, wenn Sie eine Telefonnummer angeben.");
                     return null; 
                }
            }
            
            newNet.setCreatedAt(new Date());
            newNet.setUpdatedAt(new Date());
            ghostNetService.reportGhostNet(newNet, reporterName, reporterPhone);
            
            addMessage(FacesMessage.SEVERITY_INFO, "Erfolg", "Geisternetz erfolgreich gemeldet!");
            
            // Reset form
            newNet = new GhostNet();
            reporterName = null;
            reporterPhone = null;
            anonymousReport = false;
            anonymous = false;
            latitude = null;
            longitude = null;
            estimatedSize = null;
            
            return "viewNets.xhtml?faces-redirect=true"; 
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Fehler beim Melden des Geisternetzes: " + e.getMessage());
            return null;
        }
    }
    
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    // Getters and Setters
    public GhostNet getNewNet() {
        return newNet;
    }

    public void setNewNet(GhostNet newNet) {
        this.newNet = newNet;
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
    
    public boolean isAnonymousReport() {
        return anonymousReport;
    }

    public void setAnonymousReport(boolean anonymousReport) {
        this.anonymousReport = anonymousReport;
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

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }
}