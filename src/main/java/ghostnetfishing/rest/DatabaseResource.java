package ghostnetfishing.rest;

import ghostnetfishing.util.DatabaseUtil;
import ghostnetfishing.util.JPAUtil;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * REST Endpoint für Database Health Checks und Statistiken
 * Nützlich für Monitoring und Debugging
 */
@Path("/database")
public class DatabaseResource {
    
    private static final Logger LOGGER = Logger.getLogger(DatabaseResource.class.getName());
    
    /**
     * Health Check für die Datenbank
     * @return JSON Response mit Health Status
     */
    @GET
    @Path("/health")
    @Produces(MediaType.APPLICATION_JSON)
    public Response healthCheck() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Teste JPA Initialisierung
            boolean jpaInitialized = JPAUtil.isInitialized();
            response.put("jpaInitialized", jpaInitialized);
            
            // Teste Datenbankverbindung
            boolean connectionTest = DatabaseUtil.testConnection();
            response.put("connectionTest", connectionTest);
            
            // JPA Konfiguration
            response.put("jpaConfig", JPAUtil.getConfigurationInfo());
            
            // Gesamtstatus
            boolean healthy = jpaInitialized && connectionTest;
            response.put("healthy", healthy);
            response.put("timestamp", System.currentTimeMillis());
            
            if (healthy) {
                return Response.ok(response).build();
            } else {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                              .entity(response)
                              .build();
            }
            
        } catch (Exception e) {
            LOGGER.severe("Health check failed: " + e.getMessage());
            response.put("healthy", false);
            response.put("error", e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity(response)
                          .build();
        }
    }
    
    /**
     * Datenbank-Statistiken abrufen
     * @return JSON Response mit Statistiken
     */
    @GET
    @Path("/statistics")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatistics() {
        try {
            Map<String, Object> stats = DatabaseUtil.getDatabaseStatistics();
            stats.put("timestamp", System.currentTimeMillis());
            
            return Response.ok(stats).build();
            
        } catch (Exception e) {
            LOGGER.severe("Failed to retrieve statistics: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to retrieve statistics");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity(errorResponse)
                          .build();
        }
    }
    
    /**
     * Performance-Statistiken abrufen
     * @return JSON Response mit Performance-Daten
     */
    @GET
    @Path("/performance")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPerformanceStats() {
        try {
            Map<String, Object> performanceStats = DatabaseUtil.getPerformanceStats();
            performanceStats.put("timestamp", System.currentTimeMillis());
            
            return Response.ok(performanceStats).build();
            
        } catch (Exception e) {
            LOGGER.severe("Failed to retrieve performance stats: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to retrieve performance statistics");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity(errorResponse)
                          .build();
        }
    }
}
