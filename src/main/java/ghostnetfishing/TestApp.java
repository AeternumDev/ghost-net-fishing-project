package ghostnetfishing;

import ghostnetfishing.dao.AbandonedNetDAO;
import ghostnetfishing.model.AbandonedNet;
import ghostnetfishing.model.NetCondition;

import java.math.BigDecimal;
import java.util.List;

public class TestApp {
    public static void main(String[] args) {
        AbandonedNetDAO abandonedNetDAO = new AbandonedNetDAO();

        AbandonedNet abandonedNet = new AbandonedNet();
        abandonedNet.setId(1L); // Use Long type for ID
        abandonedNet.setStatus(NetCondition.GEMELDET); // Initial status: GEMELDET
        abandonedNet.setGroesse(50);
        abandonedNet.setBeschreibung("Ein großes Geisternetz im nördlichen Meer.");
        abandonedNet.setLatitude(new BigDecimal("57.1234")); // Set latitude as BigDecimal
        abandonedNet.setLongitude(new BigDecimal("-1.2345")); // Set longitude as BigDecimal

        abandonedNetDAO.save(abandonedNet);

        List<AbandonedNet> abandonedNets = abandonedNetDAO.findAll();
        System.out.println("Retrieved Geisternetze:");
        for (AbandonedNet net : abandonedNets) {
            System.out.println(net);
        }

        abandonedNet.setStatus(NetCondition.GEBORGEN); // Change status to GEBORGEN
        abandonedNetDAO.update(abandonedNet);

        abandonedNetDAO.delete(abandonedNet);

        System.out.println("Geisternetz operations completed.");
    }
}
