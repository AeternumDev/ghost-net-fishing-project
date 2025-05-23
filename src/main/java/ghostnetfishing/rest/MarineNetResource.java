package ghostnetfishing.rest;

import ghostnetfishing.dao.AbandonedNetDAO;
import ghostnetfishing.model.AbandonedNet;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/marineNets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MarineNetResource {

    private AbandonedNetDAO geisternetzDAO = new AbandonedNetDAO();

    @GET
    public List<AbandonedNet> getGeisternetzData() {
        return geisternetzDAO.findAll();
    }
}
