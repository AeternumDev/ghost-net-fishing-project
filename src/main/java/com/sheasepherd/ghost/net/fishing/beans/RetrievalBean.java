/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sheasepherd.ghost.net.fishing.beans;

import com.sheasepherd.ghost.net.fishing.entities.GhostNet;
import com.sheasepherd.ghost.net.fishing.services.GhostNetService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;

@Named
@RequestScoped
public class RetrievalBean {
    
    @Inject
    private GhostNetService ghostNetService;
    
    private List<GhostNet> availableNets;
    private List<GhostNet> netsInRetrieval;
    
    private String retrieverName;
    private String retrieverPhone;
    
    @PostConstruct
    public void init() {
        loadNets();
    }
    
    public void loadNets() {
        availableNets = ghostNetService.getNetsPendingRetrieval();
        netsInRetrieval = ghostNetService.getNetsInRetrieval();
    }
    
    public String assignRetriever(Long netId) {
        try {
            if (retrieverName == null || retrieverName.trim().isEmpty() ||
                retrieverPhone == null || retrieverPhone.trim().isEmpty()) {
                addMessage(FacesMessage.SEVERITY_ERROR, 
                          "Fehler", "Bitte Name und Telefonnummer angeben.");
                return null;
            }
            
            ghostNetService.assignRetriever(netId, retrieverName, retrieverPhone);
            
            addMessage(FacesMessage.SEVERITY_INFO, 
                      "Erfolg", "Bergung erfolgreich übernommen.");
            
            clearRetrieverForm();
            loadNets();
            return null;
            
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, 
                      "Fehler", "Fehler beim Übernehmen der Bergung: " + e.getMessage());
            return null;
        }
    }
    
    public String markAsRetrieved(Long netId) {
        try {
            ghostNetService.markAsRetrieved(netId);
            addMessage(FacesMessage.SEVERITY_INFO, 
                      "Erfolg", "Geisternetz als geborgen markiert.");
            loadNets();
            return null;
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, 
                      "Fehler", "Fehler beim Markieren als geborgen: " + e.getMessage());
            return null;
        }
    }
    
    public String markAsLost(Long netId) {
        try {
            ghostNetService.markAsLost(netId);
            addMessage(FacesMessage.SEVERITY_INFO, 
                      "Erfolg", "Geisternetz als verschollen markiert.");
            loadNets();
            return null;
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, 
                      "Fehler", "Fehler beim Markieren als verschollen: " + e.getMessage());
            return null;
        }
    }
    
    private void clearRetrieverForm() {
        retrieverName = null;
        retrieverPhone = null;
    }
    
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(severity, summary, detail));
    }
    
    // Getters and Setters
    public List<GhostNet> getAvailableNets() {
        return availableNets;
    }
    
    public void setAvailableNets(List<GhostNet> availableNets) {
        this.availableNets = availableNets;
    }
    
    public List<GhostNet> getNetsInRetrieval() {
        return netsInRetrieval;
    }
    
    public void setNetsInRetrieval(List<GhostNet> netsInRetrieval) {
        this.netsInRetrieval = netsInRetrieval;
    }
    
    public String getRetrieverName() {
        return retrieverName;
    }
    
    public void setRetrieverName(String retrieverName) {
        this.retrieverName = retrieverName;
    }
    
    public String getRetrieverPhone() {
        return retrieverPhone;
    }
    
    public void setRetrieverPhone(String retrieverPhone) {
        this.retrieverPhone = retrieverPhone;
    }
}