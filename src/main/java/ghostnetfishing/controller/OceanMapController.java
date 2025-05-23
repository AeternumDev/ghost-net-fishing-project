package ghostnetfishing.controller;

import ghostnetfishing.dao.AbandonedNetDAO;
import ghostnetfishing.model.AbandonedNet;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
import java.io.Serializable;
import java.util.List;

@Named("oceanMapController")
@SessionScoped
public class OceanMapController implements Serializable {

    private MapModel gmapModel;

    @Inject
    private AbandonedNetDAO geisternetzDAO;    @PostConstruct
    public void init() {
        gmapModel = new DefaultMapModel();
        ladeGeisternetzDaten();
    }

    public void ladeGeisternetzDaten() {
        List<AbandonedNet> netze = geisternetzDAO.findAll();
        System.out.println("DEBUG: Anzahl der geladenen Geisternetze: " + netze.size());

        for (AbandonedNet netz : netze) {
            System.out.println("DEBUG: Geisternetz ID: " + netz.getId() + " - Koordinaten: " + netz.getLatitude() + ", " + netz.getLongitude());

            Marker marker = new Marker(
                    new LatLng(netz.getLatitude().doubleValue(), netz.getLongitude().doubleValue()),
                    "Geisternetz ID: " + netz.getId()
            );
            marker.setTitle("Geisternetz ID: " + netz.getId());
            marker.setData(netz.getId());
            marker.setClickable(true);
            gmapModel.addOverlay(marker);
        }

        System.out.println("DEBUG: Anzahl der Marker im Modell: " + gmapModel.getMarkers().size());
    }

    public void aktualisiereKarte() {
        gmapModel = new DefaultMapModel();
        ladeGeisternetzDaten();
    }

    public MapModel getGmapModel() {
        return gmapModel;
    }
}
