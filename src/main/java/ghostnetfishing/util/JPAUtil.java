package ghostnetfishing.util;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.annotation.PreDestroy;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * JPA Utility Klasse für Ghost Net Fishing
 * Erweiterte Version mit Lifecycle-Management und Fehlerbehandlung
 */
@ApplicationScoped
public class JPAUtil {

    private static final Logger LOGGER = Logger.getLogger(JPAUtil.class.getName());
    private static EntityManagerFactory emf;
    
    static {
        initializeEntityManagerFactory();
    }
    
    /**
     * Initialisiert die EntityManagerFactory
     */
    private static void initializeEntityManagerFactory() {
        try {
            // Ermittle Persistence Unit basierend auf Umgebung
            String persistenceUnit = determinePersistenceUnit();
            LOGGER.info("Initializing JPA with persistence unit: " + persistenceUnit);
            
            emf = Persistence.createEntityManagerFactory(persistenceUnit);
            
            // Teste die Verbindung
            testConnection();
            
            LOGGER.info("JPA EntityManagerFactory successfully initialized");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize JPA EntityManagerFactory", e);
            throw new RuntimeException("JPA initialization failed", e);
        }
    }
    
    /**
     * Ermittelt die zu verwendende Persistence Unit basierend auf der Umgebung
     * @return Name der Persistence Unit
     */
    private static String determinePersistenceUnit() {
        String environment = System.getProperty("app.environment");
        
        if (environment != null) {
            switch (environment.toLowerCase()) {
                case "dev":
                case "development":
                    return "ghostnet_fishing_dev";
                case "prod":
                case "production":
                    return "ghostnet_fishing_prod";
                case "test":
                    return "ghostnet_fishing_test";
                default:
                    return "ghostnet_fishing";
            }
        }
        
        return "ghostnet_fishing"; // Default
    }
    
    /**
     * Erstellt einen neuen EntityManager
     * @return EntityManager
     */
    public static EntityManager getEntityManager() {
        if (emf == null || !emf.isOpen()) {
            LOGGER.warning("EntityManagerFactory is not available, attempting to reinitialize");
            initializeEntityManagerFactory();
        }
        
        try {
            return emf.createEntityManager();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to create EntityManager", e);
            throw new RuntimeException("Could not create EntityManager", e);
        }
    }
    
    /**
     * CDI Producer für EntityManager
     * @return EntityManager
     */
    @Produces
    public EntityManager produceEntityManager() {
        return getEntityManager();
    }
    
    /**
     * Testet die Datenbankverbindung
     */
    private static void testConnection() {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            em.createNativeQuery("SELECT 1").getSingleResult();
            LOGGER.info("Database connection test successful");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Database connection test failed", e);
            // Nicht kritisch beim Start, wird protokolliert
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
    
    /**
     * Prüft ob JPA korrekt initialisiert ist
     * @return true wenn JPA verfügbar ist
     */
    public static boolean isInitialized() {
        return emf != null && emf.isOpen();
    }
    
    /**
     * Gibt Informationen über die JPA-Konfiguration zurück
     * @return Konfigurations-Info
     */
    public static String getConfigurationInfo() {
        if (emf != null) {
            return "JPA initialized with factory: " + emf.getClass().getSimpleName();
        }
        return "JPA not initialized";
    }
    
    /**
     * Schließt die EntityManagerFactory beim Anwendungsende
     */
    @PreDestroy
    public void destroy() {
        shutdown();
    }
    
    /**
     * Schließt die EntityManagerFactory
     */
    public static void shutdown() {
        if (emf != null && emf.isOpen()) {
            try {
                emf.close();
                LOGGER.info("EntityManagerFactory closed successfully");
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error closing EntityManagerFactory", e);
            }
        }
    }
}
