package tools.descartes.teastore.apis;

import jakarta.ws.rs.*;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import tools.descartes.teastore.entities.Category;

import java.util.List;


@Path("/rest")
@RegisterRestClient
public interface CategoryService extends AutoCloseable {
    @GET
    @Path("/categories")
    List<Category> getCategories(@QueryParam("start") int start, @QueryParam("limit") int limit) throws ProcessingException;
}
