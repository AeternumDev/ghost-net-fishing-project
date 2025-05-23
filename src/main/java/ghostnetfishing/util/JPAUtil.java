package ghostnetfishing.util;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@ApplicationScoped
public class JPAUtil {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("ghostnet_fishing");

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
