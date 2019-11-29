package sacm.com.mx.compositores.domain.restservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import sacm.com.mx.compositores.common.dtos.MetadataResultDto;
import sacm.com.mx.compositores.infraestructure.repositories.SacmMetadata;

@Path("/metadata")
public class MetadaService {
    public MetadaService() {
        super();
    }

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    @Path("getmetadata")
    public MetadataResultDto getMetadata(MetadataResultDto metadataRequest) {
        return SacmMetadata.getMetadata(metadataRequest);
    }
}