package tools.descartes.teastore.apis;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import tools.descartes.teastore.entities.Icon;

@Path("/rest")
@Consumes("application/json")
@RegisterRestClient
public interface ImageService extends AutoCloseable {
    @POST
    @Path("/image/getWebImages")
    @Consumes(MediaType.APPLICATION_JSON)
    Icon getWebImages(Icon icon) throws ProcessingException;
}
