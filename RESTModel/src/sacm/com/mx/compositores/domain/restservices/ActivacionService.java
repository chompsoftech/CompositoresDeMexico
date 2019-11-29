package sacm.com.mx.compositores.domain.restservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import sacm.com.mx.compositores.common.dtos.ActivacionResultDto;
import sacm.com.mx.compositores.infraestructure.repositories.SacmActivacion;

@Path("/activacion")
public class ActivacionService {
    public ActivacionService() {
        super();
    }
    
    @POST
    @Produces("application/json")
    @Path("sacm_activa_cuenta")
    public ActivacionResultDto getAcativaCuenta(ActivacionResultDto ActivRequest) {
        return SacmActivacion.getActivacionResult(ActivRequest);
    }
    
}
