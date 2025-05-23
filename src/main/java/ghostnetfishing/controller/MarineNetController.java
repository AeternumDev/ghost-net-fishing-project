package ghostnetfishing.controller;

import ghostnetfishing.dao.RecoveryDiverDAO;
import ghostnetfishing.dao.AbandonedNetDAO;
import ghostnetfishing.dao.SpotterDAO;
import ghostnetfishing.model.AbandonedNet;
import ghostnetfishing.model.NetCondition;
import ghostnetfishing.model.Spotter;
import ghostnetfishing.model.RecoveryDiver;
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
    private List<RecoveryDiver> alleBergendenPersonen;

    private AbandonedNet geisternetz;
    private AbandonedNet selectedGeisternetz;
    private Spotter meldendePerson;
    private boolean anonymMelden;
    private boolean editMode;

    @Inject
    private RecoveryDiverDAO bergendePersonDAO;

    private RecoveryDiver selectedBergendePerson = new RecoveryDiver();

    private Long selectedBergendePersonId;

    @Inject
    private AbandonedNetDAO geisternetzDAO;

    @Inject
    private SpotterDAO meldendePersonDAO;    @PostConstruct
    public void init() {
        try {
            alleBergendenPersonen = bergendePersonDAO.findAll(); // ✅ Liste wird beim Start geladen
            selectedGeisternetz = new AbandonedNet();  // Initialisiere das Objekt, um NullPointerException zu vermeiden

            alleGeisternetze = geisternetzDAO.findAll();
            gefilterteGeisternetze = alleGeisternetze;
            statusOptions = Arrays.stream(NetCondition.values())
                    .map(Enum::name)
                    .toList();
            geisternetz = new AbandonedNet();
            meldendePerson = new Spotter();
            selectedBergendePerson = new RecoveryDiver();
            anonymMelden = false;
            editMode = false;

        } catch (Exception e) {
            System.err.println("ERROR: Fehler bei der Initialisierung von MarineNetController: " + e.getMessage());
        }
    }    public void speichernOderAktualisieren() {

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
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Das Geisternetz konnte nicht gespeichert werden."));
        }
    }    public void meldeGeisternetz() {

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
    }    public void setStatusToBergungBevorstehend(AbandonedNet netz) {

        if (selectedBergendePersonId == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Bitte eine bergende Person auswählen."));
            return;
        }

        RecoveryDiver neuePerson = bergendePersonDAO.findById(selectedBergendePersonId);
        if (neuePerson == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Ausgewählte Person nicht gefunden."));
            return;
        }

        if (netz == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Geisternetz ist null."));
            return;
        }

        netz.setBergendePerson(neuePerson);
        netz.setStatus(NetCondition.BERGUNG_BEVORSTEHEND);

        try {
            geisternetzDAO.update(netz);

            aktualisiereGeisternetzListe();
            PrimeFaces.current().ajax().update("geisternetzForm");

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Erfolg", "Geisternetz wurde erfolgreich der neuen Person zugewiesen."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Fehler beim Speichern der Änderung."));
            e.printStackTrace();
        }
    }    public void setStatusToGeborgen(AbandonedNet netz) {
        if (netz.getBergendePerson() == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Dieses Geisternetz wurde noch keiner bergenden Person zugewiesen."));
            return;
        }

        netz.setStatus(NetCondition.GEBORGEN);
        geisternetzDAO.update(netz);
        aktualisiereGeisternetzListe();

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Erfolg", "Geisternetz wurde als geborgen markiert."));
    }    public void loescheGeisternetz(AbandonedNet netz) {
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
    }    private void resetForm() {
        geisternetz = new AbandonedNet();
        meldendePerson = new Spotter();
        anonymMelden = false;
        editMode = false;
    }

    public void toggleAnonymMelden() {
        if (anonymMelden) {
            meldendePerson = new Spotter(); // Setzt Name und Telefonnummer auf NULL
        }
    }    public List<RecoveryDiver> getAlleBergendenPersonen() {
        return bergendePersonDAO.findAll();
    }

    public RecoveryDiver getSelectedBergendePerson() {
        return selectedBergendePerson;
    }

    public void setSelectedBergendePerson(RecoveryDiver selectedBergendePerson) {
        this.selectedBergendePerson = selectedBergendePerson;
    }

    public AbandonedNet getSelectedGeisternetz() {
        return selectedGeisternetz;
    }

    public void setSelectedGeisternetz(AbandonedNet selectedGeisternetz) {
        this.selectedGeisternetz = selectedGeisternetz;
    }    public Long getSelectedBergendePersonId() {
        return selectedBergendePersonId;
    }

    public void setSelectedBergendePersonId(Long selectedBergendePersonId) {
        this.selectedBergendePersonId = selectedBergendePersonId;
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
    }    public List<String> getStatusOptions() {
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

    public List<AbandonedNet> getGeisternetzeReserviert() {
        return alleGeisternetze.stream()
                .filter(netz -> netz.getStatus() == NetCondition.BERGUNG_BEVORSTEHEND)
                .collect(Collectors.toList());
    }    public void addBergendePerson() {
        if (selectedBergendePerson == null) {
            selectedBergendePerson = new RecoveryDiver();
        }

        if (selectedBergendePerson.getName() == null || selectedBergendePerson.getName().trim().isEmpty() ||
                selectedBergendePerson.getTelefonnummer() == null || selectedBergendePerson.getTelefonnummer().trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Bitte Name und Telefonnummer eingeben."));
            return;
        }

        try {
            bergendePersonDAO.save(selectedBergendePerson);

            List<RecoveryDiver> aktualisierteListe = bergendePersonDAO.findAll();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Erfolg", "Bergende Person erfolgreich hinzugefügt."));

            selectedBergendePerson = new RecoveryDiver();

            PrimeFaces.current().ajax().update("bergendePersonForm", "addPersonForm");

        } catch (Exception e) {
            System.err.println("ERROR: Fehler beim Speichern der bergenden Person: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Fehler beim Speichern der Person."));
        }
    }
}
