/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sheasepherd.ghost.net.fishing.services;

import com.sheasepherd.ghost.net.fishing.entities.GhostNet;
import com.sheasepherd.ghost.net.fishing.enums.NetStatus; // Corrected import
import com.sheasepherd.ghost.net.fishing.entities.Person; // Corrected import
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Stateless
public class GhostNetService {
    
    @PersistenceContext(unitName = "ghost-net-pu")
    private EntityManager em;
    
    public void reportGhostNet(GhostNet ghostNet, String reporterName, String reporterPhone) {
        Person reporter = null;
        if (reporterName != null && !reporterName.trim().isEmpty()) {
            reporter = new Person(reporterName.trim(), 
                                reporterPhone != null ? reporterPhone.trim() : null);
            em.persist(reporter);
            ghostNet.setReporter(reporter);
        }
        
        ghostNet.setStatus(NetStatus.REPORTED);
        em.persist(ghostNet);
    }
    
    public List<GhostNet> getNetsPendingRetrieval() {
        TypedQuery<GhostNet> query = em.createQuery(
            "SELECT g FROM GhostNet g WHERE g.status = :status ORDER BY g.createdAt ASC", 
            GhostNet.class);
        query.setParameter("status", NetStatus.REPORTED);
        return query.getResultList();
    }
    
    public List<GhostNet> getNetsInRetrieval() {
        TypedQuery<GhostNet> query = em.createQuery(
            "SELECT g FROM GhostNet g WHERE g.status = :status ORDER BY g.updatedAt ASC", 
            GhostNet.class);
        query.setParameter("status", NetStatus.RETRIEVAL_PENDING);
        return query.getResultList();
    }
    
    public void assignRetriever(Long netId, String retrieverName, String retrieverPhone) {
        GhostNet net = em.find(GhostNet.class, netId);
        if (net != null && net.getStatus() == NetStatus.REPORTED) {
            Person retriever = new Person(retrieverName.trim(), retrieverPhone.trim());
            em.persist(retriever);
            
            net.setRetriever(retriever);
            net.setStatus(NetStatus.RETRIEVAL_PENDING);
            em.merge(net);
        }
    }
    
    public void markAsRetrieved(Long netId) {
        GhostNet net = em.find(GhostNet.class, netId);
        if (net != null && net.getStatus() == NetStatus.RETRIEVAL_PENDING) {
            net.setStatus(NetStatus.RETRIEVED);
            em.merge(net);
        }
    }
    
    public void markAsLost(Long netId) {
        GhostNet net = em.find(GhostNet.class, netId);
        if (net != null && net.getStatus() != NetStatus.RETRIEVED) {
            net.setStatus(NetStatus.LOST);
            em.merge(net);
        }
    }
    
    public GhostNet findById(Long id) {
        return em.find(GhostNet.class, id);
    }
    
    public List<GhostNet> getAllNets() {
        TypedQuery<GhostNet> query = em.createQuery(
            "SELECT g FROM GhostNet g ORDER BY g.createdAt DESC", 
            GhostNet.class);
        return query.getResultList();
    }
}