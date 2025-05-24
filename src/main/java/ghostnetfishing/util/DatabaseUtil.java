package ghostnetfishing.util;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Erweiterte Database Utility Klasse für Ghost Net Fishing
 * Bietet erweiterte Datenbankfunktionen und Performance-Monitoring
 */
@ApplicationScoped
public class DatabaseUtil {
    
    private static final Logger LOGGER = Logger.getLogger(DatabaseUtil.class.getName());
    private static EntityManagerFactory emf;
    private static final Map<String, Object> performanceStats = new HashMap<>();
    
    static {
        try {
            // Ermittle aktive Umgebung (default: ghostnet_fishing)
            String persistenceUnit = System.getProperty("app.environment", "ghostnet_fishing");
            LOGGER.info("Initializing EntityManagerFactory with persistence unit: " + persistenceUnit);
            
            emf = Persistence.createEntityManagerFactory(persistenceUnit);
            LOGGER.info("EntityManagerFactory successfully initialized");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize EntityManagerFactory", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }
    
    /**
     * Erstellt einen neuen EntityManager
     * @return EntityManager
     */
    public static EntityManager getEntityManager() {
        if (emf == null || !emf.isOpen()) {
            throw new IllegalStateException("EntityManagerFactory is not initialized or closed");
        }
        return emf.createEntityManager();
    }
    
    /**
     * Prüft die Datenbankverbindung
     * @return true wenn Verbindung erfolgreich
     */
    public static boolean testConnection() {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query query = em.createNativeQuery("SELECT 1");
            query.getSingleResult();
            LOGGER.info("Database connection test: SUCCESS");
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Database connection test: FAILED", e);
            return false;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
    
    /**
     * Führt ein natives SQL-Query aus
     * @param sql SQL Statement
     * @return Ergebnis als Liste
     */
    @SuppressWarnings("unchecked")
    public static List<Object[]> executeNativeQuery(String sql) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query query = em.createNativeQuery(sql);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to execute native query: " + sql, e);
            throw new RuntimeException("Query execution failed", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
    
    /**
     * Holt Datenbankstatistiken
     * @return Map mit Statistiken
     */
    public static Map<String, Object> getDatabaseStatistics() {
        Map<String, Object> stats = new HashMap<>();
        EntityManager em = null;
        
        try {
            em = getEntityManager();
            
            // Anzahl Geisternetze pro Status
            Query statusQuery = em.createNativeQuery(
                "SELECT status, COUNT(*) as count FROM AbandonedNet GROUP BY status"
            );
            List<Object[]> statusResults = statusQuery.getResultList();
            Map<String, Long> statusStats = new HashMap<>();
            for (Object[] row : statusResults) {
                statusStats.put((String) row[0], ((Number) row[1]).longValue());
            }
            stats.put("netsByStatus", statusStats);
            
            // Gesamtanzahl Geisternetze
            Query totalNetsQuery = em.createNativeQuery("SELECT COUNT(*) FROM AbandonedNet");
            Long totalNets = ((Number) totalNetsQuery.getSingleResult()).longValue();
            stats.put("totalNets", totalNets);
            
            // Gesamtanzahl Spotter
            Query totalSpottersQuery = em.createNativeQuery("SELECT COUNT(*) FROM Spotter");
            Long totalSpotters = ((Number) totalSpottersQuery.getSingleResult()).longValue();
            stats.put("totalSpotters", totalSpotters);
            
            // Durchschnittliche Netzgröße
            Query avgSizeQuery = em.createNativeQuery("SELECT AVG(groesse) FROM AbandonedNet");
            Object avgSizeResult = avgSizeQuery.getSingleResult();
            Double avgSize = avgSizeResult != null ? ((Number) avgSizeResult).doubleValue() : 0.0;
            stats.put("averageNetSize", avgSize);
            
            // Neueste Meldung
            Query latestReportQuery = em.createNativeQuery(
                "SELECT MAX(created_at) FROM AbandonedNet"
            );
            Object latestReport = latestReportQuery.getSingleResult();
            stats.put("latestReport", latestReport);
            
            LOGGER.info("Database statistics retrieved successfully");
            
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to retrieve database statistics", e);
            stats.put("error", "Failed to retrieve statistics: " + e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        
        return stats;
    }
    
    /**
     * Bereinigt die Datenbank (entfernt verwaiste Datensätze)
     * @return Anzahl der bereinigten Datensätze
     */
    public static int cleanupDatabase() {
        EntityManager em = null;
        int cleanedRecords = 0;
        
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            
            // Entferne Spotter ohne zugehörige Geisternetze (optional)
            Query cleanupQuery = em.createNativeQuery(
                "DELETE FROM Spotter WHERE id NOT IN " +
                "(SELECT DISTINCT meldendePerson_id FROM AbandonedNet WHERE meldendePerson_id IS NOT NULL)"
            );
            
            // Führe Cleanup nur aus wenn explizit erwünscht
            // cleanedRecords = cleanupQuery.executeUpdate();
            
            em.getTransaction().commit();
            LOGGER.info("Database cleanup completed. Records cleaned: " + cleanedRecords);
            
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            LOGGER.log(Level.SEVERE, "Database cleanup failed", e);
            throw new RuntimeException("Database cleanup failed", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        
        return cleanedRecords;
    }
    
    /**
     * Exportiert Daten als CSV
     * @param tableName Name der Tabelle
     * @return CSV-Daten als String
     */
    public static String exportTableToCSV(String tableName) {
        EntityManager em = null;
        StringBuilder csv = new StringBuilder();
        
        try {
            em = getEntityManager();
            
            // Hole alle Daten der Tabelle
            Query query = em.createNativeQuery("SELECT * FROM " + tableName);
            List<Object[]> results = query.getResultList();
            
            if (!results.isEmpty()) {
                // CSV Header (vereinfacht - in Produktion sollten Spaltennamen abgefragt werden)
                switch (tableName.toLowerCase()) {
                    case "abandonednet":
                        csv.append("id,status,groesse,beschreibung,latitude,longitude,meldendePerson_id,created_at,updated_at\n");
                        break;
                    case "spotter":
                        csv.append("id,name,telefonnummer,created_at,updated_at\n");
                        break;
                }
                
                // CSV Daten
                for (Object[] row : results) {
                    for (int i = 0; i < row.length; i++) {
                        if (i > 0) csv.append(",");
                        Object value = row[i];
                        if (value != null) {
                            // Escape CSV special characters
                            String stringValue = value.toString();
                            if (stringValue.contains(",") || stringValue.contains("\"") || stringValue.contains("\n")) {
                                stringValue = "\"" + stringValue.replace("\"", "\"\"") + "\"";
                            }
                            csv.append(stringValue);
                        }
                    }
                    csv.append("\n");
                }
            }
            
            LOGGER.info("CSV export completed for table: " + tableName);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "CSV export failed for table: " + tableName, e);
            throw new RuntimeException("CSV export failed", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        
        return csv.toString();
    }
    
    /**
     * Schließt die EntityManagerFactory (sollte nur beim Anwendungsende aufgerufen werden)
     */
    public static void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            LOGGER.info("EntityManagerFactory closed");
        }
    }
    
    /**
     * Getter für Performance-Statistiken
     * @return Performance-Statistiken
     */
    public static Map<String, Object> getPerformanceStats() {
        return new HashMap<>(performanceStats);
    }
}
