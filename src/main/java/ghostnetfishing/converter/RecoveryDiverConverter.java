package ghostnetfishing.converter;

import ghostnetfishing.dao.RecoveryDiverDAO;
import ghostnetfishing.model.RecoveryDiver;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;

@FacesConverter(value = "recoveryDiverConverter")
public class RecoveryDiverConverter implements Converter<RecoveryDiver> {

    private final RecoveryDiverDAO bergendePersonDAO = new RecoveryDiverDAO();

    @Override
    public RecoveryDiver getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            Long id = Long.parseLong(value);
            return bergendePersonDAO.findById(id);
        } catch (NumberFormatException e) {
            throw new ConverterException("Ungültige ID: " + value, e);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, RecoveryDiver person) {
        if (person == null || person.getId() == null) {
            return "";
        }
        return String.valueOf(person.getId());
    }
}
