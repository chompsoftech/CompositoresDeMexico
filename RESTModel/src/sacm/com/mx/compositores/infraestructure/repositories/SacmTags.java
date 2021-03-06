package sacm.com.mx.compositores.infraestructure.repositories;

import java.io.Serializable;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import java.util.Map;

import java.util.TreeMap;

import oracle.adf.share.logging.ADFLogger;

import sacm.com.mx.compositores.common.dtos.Sacm_pkg_Registro_Usuario.EstadoResultDto;
import sacm.com.mx.compositores.common.dtos.HeaderDto;
import sacm.com.mx.compositores.common.dtos.Sacm_pkg_Buscador.ObraResultDto;
import sacm.com.mx.compositores.common.dtos.Sacm_pkg_Buscador.ParticipanteDto;
import sacm.com.mx.compositores.common.dtos.Sacm_pkg_Buscador.Tag;
import sacm.com.mx.compositores.common.dtos.Sacm_pkg_Buscador.TagN1;
import sacm.com.mx.compositores.common.dtos.Sacm_pkg_Buscador.TagN2;
import sacm.com.mx.compositores.common.dtos.Sacm_pkg_Buscador.TagsDto;
import sacm.com.mx.compositores.common.dtos.Sacm_pkg_Buscador.TagsResultDto;
import sacm.com.mx.compositores.common.dtos.Sacm_pkg_Buscador.TrackInfoDto;
import sacm.com.mx.compositores.infraestructure.utils.AppModule;

public class SacmTags implements Serializable {
    @SuppressWarnings("compatibility:5838653610898359998")
    private static final long serialVersionUID = 1L;

    private static ADFLogger _logger = ADFLogger.createADFLogger(SacmEstado.class);
    private static TagsResultDto tagsResponse;

    public SacmTags() {
        super();
    }

    /*-------------------------------------------------------------- sacm_cat_tags --------------------------------------------------------------------------*/
    public static TagsResultDto getTagsByIdTag(TagsDto tagsRequest) {
        List<Tag> tagListResult = new ArrayList<Tag>();
        CallableStatement cstmt = null;
        ResultSet rs = null;
        Connection conn = null;        
        try {
            conn = AppModule.getDbConexionJDBC();
            // 2. Define the PL/SQL block for the statement to invoke
            cstmt = conn.prepareCall("{call SACM_PKG_BUSCADOR.PRC_CONSULTA_TAGS(?,?,?,?,?)}");
            // 3. Set the bind values of the IN parameters
            cstmt.setObject(1, tagsRequest.getIdTag());
            cstmt.setObject(2, tagsRequest.getIdTagHijo());
            // 4. Register the positions and types of the OUT parameters
            cstmt.registerOutParameter(3, Types.INTEGER);
            cstmt.registerOutParameter(4, Types.VARCHAR);
            cstmt.registerOutParameter(5, -10);
            // 5. Execute the statement
            cstmt.executeUpdate();
            if (cstmt.getInt(3) == 0) {
                rs = (ResultSet) cstmt.getObject(5);
                List<Tag> tagList = new ArrayList<Tag>();               
                while (rs.next()) {
                    Tag tag = new Tag();
                    TagN1 tagN1 = new TagN1();
                    TagN2 tagN2 = new TagN2();
                    //Asignamiento de valores al objeto Tag
                    tag.setIdTag(rs.getInt(1));
                    tag.setTagName(rs.getString(2));
                    //Asignamiento de valores al objeto Tag nivel 1
                    tagN1.setIdTag(rs.getInt(3));
                    tagN1.setTagName(rs.getString(4));
                    //Asignamiento de valores al objeto Tag nivel 2
                    tagN2.setIdTag(Integer.toString(rs.getInt(5)));
                    tagN2.setTagName(rs.getString(6));
                    //Se agrega el elemento Tag de nivel 2 en el objeto Tag nivel 1
                    tagN1.getTagsList().add(tagN2);
                    //Se agrega el elemento Tag de nivel 1 en el objeto Tag nivel 2
                    tag.getTagsList().add(tagN1);
                    tagList.add(tag);
                }                
                organizaList(tagListResult, tagList);
                rs.close();
            }
            // 6. Set value of dateValue property using first OUT param
            tagsResponse = new TagsResultDto();
            tagsResponse.setResponseBD(new HeaderDto());
            tagsResponse.getResponseBD().setCodErr(cstmt.getInt(3));
            tagsResponse.getResponseBD().setCodMsg(cstmt.getString(4));
            tagsResponse.setResponseService(new HeaderDto());
            tagsResponse.getResponseService().setCodErr(cstmt.getInt(3));
            tagsResponse.getResponseService().setCodMsg(cstmt.getString(4));
            tagsResponse.setTagsList(tagListResult);
            // 9. Close the JDBC CallableStatement
            cstmt.close();
            conn.close();
            conn = null;

        } catch (Exception e) {
            // a failure occurred log message;
            _logger.severe(e.getMessage());
            tagsResponse = new TagsResultDto();
            tagsResponse.setResponseService(new HeaderDto());
            tagsResponse.getResponseService().setCodErr(1);
            tagsResponse.getResponseService().setCodMsg(e.getMessage());
            return tagsResponse;
        }
        _logger.info("Finish getEstados");
        // 9. Return the result
        return tagsResponse;
    }


    private static void organizaList(List<Tag> tagListResult, List<Tag> tagList) {
        Map<Integer, TagN1> mapN1 = new TreeMap<Integer, TagN1>();
        Map<Integer, Tag> map = new HashMap<Integer, Tag>();
        List<TagN1> tagsListN1 = new ArrayList<TagN1>();
        
        // Creación de MAp para eliminar elementos Tag repetidos
        for (Tag str : tagList) {
            map.put(str.getIdTag(), str);
        }        
        for (Tag value : map.values()) {
            tagListResult.add(value);
        }
        
        //Organización y eliminación de elementos Tag nivel 1 repetidos
        for (Tag strTLR : tagListResult) {
            mapN1 = new TreeMap<Integer, TagN1>();
            tagsListN1 = new ArrayList<TagN1>();
            for (Tag strTL : tagList) {
                if (strTL.getIdTag() == strTLR.getIdTag()) {
                    TagN1 partN1 = new TagN1();
                    partN1.setIdTag(strTL.getTagsList()
                                            .get(0)
                                            .getIdTag());
                    partN1.setTagName(strTL.getTagsList()
                                                .get(0)
                                                .getTagName());
                    mapN1.put(partN1.getIdTag(), partN1);
                }
            }
            for (TagN1 value : mapN1.values()) {
                tagsListN1.add(value);
            }
            //Organización y eliminación de elementos Tag nivel 2 repetidos
            OrganizaTagN1(tagsListN1,tagList);
            strTLR.setTagsList(tagsListN1);

        }
    }

    private static void OrganizaTagN1(List<TagN1> tagsListN1, List<Tag> tagList) {
        List<TagN2> tagsListN2 = new ArrayList<TagN2>();
        for (TagN1 strN1 : tagsListN1) {
            tagsListN2 = new ArrayList<TagN2>();
            for (Tag strTL : tagList) {
                if (strN1.getIdTag() == strTL.getTagsList()
                                                .get(0)
                                                .getIdTag()) {
                    TagN2 partN2 = new TagN2();
                    partN2.setIdTag(strTL.getTagsList()
                                            .get(0)
                                            .getTagsList()
                                            .get(0)
                                            .getIdTag());
                    partN2.setTagName(strTL.getTagsList()
                                               .get(0)
                                               .getTagsList()
                                               .get(0)
                                               .getTagName());
                    strN1.getTagsList().add(partN2);
                }
            }
        }
    }
}
