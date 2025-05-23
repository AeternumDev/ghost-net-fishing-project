package ghostnetfishing.dao;

import ghostnetfishing.model.RecoveryDiver;
import ghostnetfishing.util.JPAUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.io.Serializable;
import java.util.List;

@ApplicationScoped
public class RecoveryDiverDAO implements Serializable {

    private EntityManager em = JPAUtil.getEntityManager();    public void save(RecoveryDiver recoveryDiver) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(recoveryDiver);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Fehler beim Speichern: " + e.getMessage(), e);
        }
    }    public void update(RecoveryDiver recoveryDiver) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.merge(recoveryDiver);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Fehler beim Aktualisieren: " + e.getMessage(), e);
        }
    }    public void delete(RecoveryDiver recoveryDiver) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            RecoveryDiver toDelete = em.merge(recoveryDiver);
            em.remove(toDelete);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Fehler beim Löschen: " + e.getMessage(), e);
        }
    }

    public RecoveryDiver findById(Long id) {
        return em.find(RecoveryDiver.class, id);
    }

    public List<RecoveryDiver> findAll() {
        return em.createQuery("SELECT b FROM RecoveryDiver b", RecoveryDiver.class).getResultList();
    }
}
