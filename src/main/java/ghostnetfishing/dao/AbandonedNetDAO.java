package ghostnetfishing.dao;

import ghostnetfishing.model.AbandonedNet;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import java.io.Serializable;
import java.util.List;

@ApplicationScoped
public class AbandonedNetDAO implements Serializable {

    @PersistenceUnit(unitName = "ghostnet_fishing")
    private EntityManagerFactory emf;

    public void save(AbandonedNet abandonedNet) {
        System.out.println("DEBUG: Starting save operation for AbandonedNet with ID: " + abandonedNet.getId());
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            System.out.println("DEBUG: Persisting AbandonedNet: " + abandonedNet);
            em.persist(abandonedNet);
            em.getTransaction().commit();
            System.out.println("DEBUG: AbandonedNet successfully saved.");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("ERROR: Exception during save operation: " + e.getMessage());
            throw e;
        } finally {
            em.close();
        }
    }

    public void update(AbandonedNet abandonedNet) {
        System.out.println("DEBUG: Starting update operation for AbandonedNet with ID: " + abandonedNet.getId());
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            System.out.println("DEBUG: Merging AbandonedNet: " + abandonedNet);
            em.merge(abandonedNet);
            em.getTransaction().commit();
            System.out.println("DEBUG: AbandonedNet successfully updated.");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("ERROR: Exception during update operation: " + e.getMessage());
            throw e;
        } finally {
            em.close();
        }
    }

    public void delete(AbandonedNet abandonedNet) {
        System.out.println("DEBUG: Starting delete operation for AbandonedNet with ID: " + abandonedNet.getId());
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            System.out.println("DEBUG: Merging AbandonedNet before deletion: " + abandonedNet);
            AbandonedNet toDelete = em.merge(abandonedNet);
            em.remove(toDelete);
            em.getTransaction().commit();
            System.out.println("DEBUG: AbandonedNet successfully deleted.");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("ERROR: Exception during delete operation: " + e.getMessage());
            throw e;
        } finally {
            em.close();
        }
    }

    public AbandonedNet findById(Long id) {
        System.out.println("DEBUG: Starting findById operation for AbandonedNet with ID: " + id);
        EntityManager em = emf.createEntityManager();
        try {
            AbandonedNet result = em.find(AbandonedNet.class, id);
            System.out.println("DEBUG: Found AbandonedNet: " + result);
            return result;
        } finally {
            em.close();
        }
    }

    public List<AbandonedNet> findAll() {
        System.out.println("DEBUG: Starting findAll operation.");
        EntityManager em = emf.createEntityManager();
        try {
            List<AbandonedNet> results = em.createQuery("SELECT g FROM AbandonedNet g", AbandonedNet.class).getResultList();
            System.out.println("DEBUG: Found " + results.size() + " AbandonedNets.");
            return results;
        } finally {
            em.close();
        }
    }
}
