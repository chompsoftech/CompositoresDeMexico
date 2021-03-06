package sacm.com.mx.compositores.infraestructure.repositories;

import java.io.Serializable;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;

import java.util.ArrayList;
import java.util.List;

import java.util.Map;
import java.util.TreeMap;

import oracle.adf.share.logging.ADFLogger;

import sacm.com.mx.compositores.common.dtos.HeaderDto;
import sacm.com.mx.compositores.common.dtos.Sacm_pkg_Buscador.MetadataResultDto;
import sacm.com.mx.compositores.common.dtos.Sacm_pkg_Buscador.ParticipanteDto;
import sacm.com.mx.compositores.common.dtos.Sacm_pkg_Buscador.TrackInfoDto;
import sacm.com.mx.compositores.common.dtos.Sacm_pkg_Buscador.TrackInfoResultDto;
import sacm.com.mx.compositores.infraestructure.utils.AppModule;

public class SacmTrackInfo implements Serializable {
    @SuppressWarnings("compatibility:-8117944343936983832")
    private static final long serialVersionUID = 1L;

    private static ADFLogger _logger = ADFLogger.createADFLogger(SacmEstado.class);
    private static TrackInfoResultDto trackInfoResponse;

    public SacmTrackInfo() {
        super();
    }

    /*------------------------------------------------------------ sacm_track_info --------------------------------------------------------------------------*/
    public static TrackInfoResultDto getTrackInfoByIdObra(TrackInfoDto trackinfoRequest) {
        List<TrackInfoDto> trackInfoListResult = new ArrayList<TrackInfoDto>();
        CallableStatement cstmt = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = AppModule.getDbConexionJDBC();
            // 2. Define the PL/SQL block for the statement to invoke
            cstmt = conn.prepareCall("{call SACM_PKG_BUSCADOR.PRC_CONSULTA_TRACKINFO(?,?,?,?)}");
            // 3. Set the bind values of the IN parameters
            cstmt.setObject(1, trackinfoRequest.getIdObra());
            // 4. Register the positions and types of the OUT parameters
            cstmt.registerOutParameter(2, Types.INTEGER);
            cstmt.registerOutParameter(3, Types.VARCHAR);
            cstmt.registerOutParameter(4, -10);
            // 5. Execute the statement
            cstmt.executeUpdate();
            if (cstmt.getInt(2) == 0) {
                rs = (ResultSet) cstmt.getObject(4);
                // print the results
                List<TrackInfoDto> trackInfoList = new ArrayList<TrackInfoDto>();
                while (rs.next()) {
                    TrackInfoDto trackInfo = new TrackInfoDto();
                    ParticipanteDto participante = new ParticipanteDto();
                    trackInfo.setIdObra(rs.getInt(1));
                    trackInfo.setNumeroObra(rs.getInt(2));
                    trackInfo.setTituloObra(rs.getString(3));
                    trackInfo.setDescripcionObra(rs.getString(4));
                    trackInfo.setIdAlbum(rs.getInt(5));
                    trackInfo.setNombreAlbum(rs.getString(6));
                    participante.setId_participante(rs.getInt(7));
                    participante.setParticipante(rs.getString(8));
                    //Se agrega el valor Participnate al objet TrackInfo
                    trackInfo.getParticipante().add(participante);
                    trackInfoList.add(trackInfo);
                }
                //Organización de Participantes correspondientes a los objetos Track Info
                OrganizarTrackInfo(trackInfoListResult, trackInfoList);
                rs.close();
            }
            trackInfoResponse = new TrackInfoResultDto();
            // 6. Set value of dateValue property using first OUT param
            trackInfoResponse.setResponseBD(new HeaderDto());
            trackInfoResponse.getResponseBD().setCodErr(cstmt.getInt(2));
            trackInfoResponse.getResponseBD().setCodMsg(cstmt.getString(3));
            trackInfoResponse.setResponseService(new HeaderDto());
            trackInfoResponse.getResponseService().setCodErr(cstmt.getInt(2));
            trackInfoResponse.getResponseService().setCodMsg(cstmt.getString(3));
            trackInfoResponse.setTrackInfoList(trackInfoListResult);
            // 9. Close the JDBC CallableStatement
            cstmt.close();
            conn.close();
            conn = null;

        } catch (Exception e) {
            // a failure occurred log message;
            _logger.severe(e.getMessage());
            trackInfoResponse = new TrackInfoResultDto();
            trackInfoResponse.setResponseService(new HeaderDto());
            trackInfoResponse.getResponseService().setCodErr(1);
            trackInfoResponse.getResponseService().setCodMsg(e.getMessage());
            return trackInfoResponse;
        }
        _logger.info("Finish getEstados");
        // 9. Return the result
        return trackInfoResponse;
    }

    private static void OrganizarTrackInfo(List<TrackInfoDto> trackInfoListResult, List<TrackInfoDto> trackInfoList) {
        Map<Integer, TrackInfoDto> map = new TreeMap<Integer, TrackInfoDto>();
       //Eliminación de elementos TrackInfo repetidos
        for (TrackInfoDto str : trackInfoList) {
            map.put(str.getIdObra(), str);
        }
        for (TrackInfoDto value : map.values()) {
            trackInfoListResult.add(value);
        }
        // Organizacion de Participantes dentro de los Track List conrrespondientes
        for (TrackInfoDto strTIR : trackInfoListResult) {
            for (TrackInfoDto strTI : trackInfoList) {
                if (strTI.getIdObra() == strTIR.getIdObra()) {
                    ParticipanteDto part = new ParticipanteDto();
                    part.setId_participante(strTI.getParticipante()
                                                 .get(0)
                                                 .getId_participante());
                    part.setParticipante(strTI.getParticipante()
                                              .get(0)
                                              .getParticipante());
                    strTIR.getParticipante().add(part);
                }
            }
            strTIR.getParticipante().remove(0);
        }
    }
}
