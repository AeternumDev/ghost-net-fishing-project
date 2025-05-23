package ghostnetfishing.dao;

import ghostnetfishing.model.Spotter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.io.Serializable;
import java.util.List;

@ApplicationScoped
public class SpotterDAO implements Serializable {

    @PersistenceContext
    private EntityManager em;    @Transactional
    public void update(Spotter spotter) {
        em.merge(spotter);
    }

    @Transactional
    public void delete(Spotter spotter) {
        Spotter toDelete = em.merge(spotter);
        em.remove(toDelete);
    }

    public List<Spotter> findAll() {
        return em.createQuery("SELECT m FROM Spotter m", Spotter.class).getResultList();
    }
}
