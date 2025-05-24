package ghostnetfishing.controller;

import ghostnetfishing.dao.AbandonedNetDAO;
import ghostnetfishing.dao.SpotterDAO;
import ghostnetfishing.model.AbandonedNet;
import ghostnetfishing.model.NetCondition;
import ghostnetfishing.model.Spotter;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Named("marineNetController")
@SessionScoped
public class MarineNetController implements Serializable {

    private List<AbandonedNet> alleGeisternetze;
    private List<AbandonedNet> gefilterteGeisternetze;
    private List<String> statusOptions;

    private AbandonedNet geisternetz;
    private AbandonedNet selectedGeisternetz;
    private Spotter meldendePerson;
    private boolean anonymMelden;

    @Inject
    private AbandonedNetDAO geisternetzDAO;

    @Inject
    private SpotterDAO meldendePersonDAO;

    @PostConstruct
    public void init() {
        try {
            selectedGeisternetz = new AbandonedNet();  // Initialisiere das Objekt, um NullPointerException zu vermeiden

            alleGeisternetze = geisternetzDAO.findAll();
            gefilterteGeisternetze = alleGeisternetze;
            statusOptions = Arrays.stream(NetCondition.values())
                    .map(Enum::name)
                    .toList();
            geisternetz = new AbandonedNet();
            meldendePerson = new Spotter();
            anonymMelden = false;

        } catch (Exception e) {
            System.err.println("ERROR: Fehler bei der Initialisierung von MarineNetController: " + e.getMessage());
        }
    }

    public void speichernOderAktualisieren() {
        if (selectedGeisternetz == null) {
            selectedGeisternetz = new AbandonedNet();
        }

        try {
            if (selectedGeisternetz.getId() != null) {
                geisternetzDAO.update(selectedGeisternetz);
            } else {
                geisternetzDAO.save(selectedGeisternetz);
            }

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Erfolg", "Geisternetz erfolgreich gespeichert!"));

            aktualisiereGeisternetzListe();
            PrimeFaces.current().executeScript("PF('editDialog').hide()");
        } catch (Exception e) {
            System.err.println("ERROR: Fehler beim Speichern oder Aktualisieren: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Speichern fehlgeschlagen."));
        }
    }

    public void meldeGeisternetz() {
        if (geisternetz == null) {
            geisternetz = new AbandonedNet();
        }

        if (anonymMelden) {
            meldendePerson.setName(null);
            meldendePerson.setTelefonnummer(null);
        }

        geisternetz.setMeldendePerson(meldendePerson);
        geisternetz.setStatus(NetCondition.GEMELDET);

        try {
            geisternetzDAO.save(geisternetz);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Erfolg", "Geisternetz erfolgreich gemeldet!"));

            aktualisiereGeisternetzListe();

            geisternetz = new AbandonedNet();
            meldendePerson = new Spotter();

            PrimeFaces.current().ajax().update("geisternetzForm", "messages");

        } catch (Exception e) {
            System.err.println("ERROR: Fehler beim Melden des Geisternetzes: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Das Geisternetz konnte nicht gemeldet werden."));
        }
    }

    public void loescheGeisternetz(AbandonedNet netz) {
        if (netz == null) {
            System.err.println("ERROR: Das zu löschende Geisternetz ist NULL! Methode wird nicht ausgeführt.");
            return;
        }

        if (netz.getId() == null) {
            System.err.println("ERROR: Geisternetz hat keine gültige ID! Methode wird nicht ausgeführt.");
            return;
        }

        try {
            geisternetzDAO.delete(netz);
            aktualisiereGeisternetzListe();
            PrimeFaces.current().ajax().update("geisternetzForm messages");

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Erfolg", "Geisternetz wurde erfolgreich gelöscht."));

        } catch (Exception e) {
            System.err.println("ERROR: Fehler beim Löschen des Geisternetzes: " + e.getMessage());
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Löschen fehlgeschlagen."));
        }
    }

    private void aktualisiereGeisternetzListe() {
        alleGeisternetze = geisternetzDAO.findAll();
        gefilterteGeisternetze = alleGeisternetze;
        PrimeFaces.current().ajax().update("geisternetzForm");
    }

    public void toggleAnonymMelden() {
        if (anonymMelden) {
            meldendePerson = new Spotter(); // Setzt Name und Telefonnummer auf NULL
        }
    }

    // Getters and Setters
    public AbandonedNet getSelectedGeisternetz() {
        return selectedGeisternetz;
    }

    public void setSelectedGeisternetz(AbandonedNet selectedGeisternetz) {
        this.selectedGeisternetz = selectedGeisternetz;
    }

    public Spotter getMeldendePerson() {
        if (meldendePerson == null) {
            meldendePerson = new Spotter();
        }
        return meldendePerson;
    }

    public void setMeldendePerson(Spotter meldendePerson) {
        this.meldendePerson = meldendePerson;
    }

    public List<AbandonedNet> getAlleGeisternetze() {
        return alleGeisternetze;
    }

    public List<String> getStatusOptions() {
        return statusOptions;
    }

    public AbandonedNet getGeisternetz() {
        return geisternetz;
    }

    public boolean isAnonymMelden() {
        return anonymMelden;
    }

    public void setAnonymMelden(boolean anonymMelden) {
        this.anonymMelden = anonymMelden;
    }

    public List<AbandonedNet> getGeisternetzeGemeldet() {
        return alleGeisternetze.stream()
                .filter(netz -> netz.getStatus() == NetCondition.GEMELDET)
                .collect(Collectors.toList());
    }
}
