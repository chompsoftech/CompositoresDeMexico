package sacm.com.mx.compositores.domain.restservices.Sacm_Registro_Usuario;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import sacm.com.mx.compositores.common.dtos.Sacm_pkg_Registro_Usuario.ActivacionDto;
import sacm.com.mx.compositores.common.dtos.Sacm_pkg_Registro_Usuario.ActivacionResultDto;
import sacm.com.mx.compositores.infraestructure.repositories.SacmActivacion;

@Path("/activacion")
public class ActivacionService {
    public ActivacionService() {
        super();
    }
    
    
    /*-----------------------------------------------------sacm_activa_cuenta Service-------------------------------------------------------------------*/
    @POST
    @Produces("application/json")
    @Path("sacm_activa_cuenta")
    public ActivacionResultDto getAcativaCuenta(ActivacionDto ActivRequest) {
        return SacmActivacion.getActivacionResult(ActivRequest);
    }
    
}
