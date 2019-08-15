package com.backend.servicios;

import com.backend.entidades.Comercio;
import com.backend.entidades.MarketPlace;
import com.backend.entidades.Promocion;
import com.backend.entidades.Usuario;
import com.backend.recursos.LoginDatos;
import com.backend.recursos.PublicacionMercadoPago;
import com.backend.repositorios.PromocionRepositorio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@Transactional
public class PromocionServicio {

    @Autowired
    PromocionRepositorio promocionRepositorio;
    @Autowired
    UsuarioServicio usuarioServicio;
    @Autowired
    ComercioServicio comercioServicio;
    @Autowired
    MarketPlaceServicio marketPlaceServicio;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public ArrayList<Promocion> obtenerPromociones(Long idComercio){
        //ArrayList<Promocion> lp = new ArrayList<Promocion>();
        ArrayList<Promocion> lp = this.promocionRepositorio.findAllByComercio(idComercio).get();

        return lp;
    }

    public Boolean nuevaPromocion(Promocion promocion, LoginDatos loginDatos){
        //ArrayList<Promocion> lp = new ArrayList<Promocion>();
        try{
            // Llegan todos los datos de la promoción del comercio, falta que yo agregue la ganancia y la pub
            // en mpago

            float ganancia = (float) ((double) this.marketPlaceServicio.obtener().getGanancia());
            log.info(" Obtengo ganancia " +ganancia);



            Comercio c = this.comercioServicio.obtener(this.usuarioServicio.obtener(loginDatos.getIdUsuario()));
            log.info(" Obtengo comercio y datos + "+c.getId());

            log.info(" CREO OBJETO MP " );
            PublicacionMercadoPago publicacionMP = new PublicacionMercadoPago(c.getAccessToken());
            // ahora creo el objeto de Mpago
            log.info(" Publico Objeto " );

            String mpResult = publicacionMP.publicar(promocion.getTitulo(),promocion.getDescripcion(),promocion.getTipomoneda(),(float) promocion.getImporte(),ganancia, promocion.getVigencia());
            if (mpResult!=null){
                log.info(" Propiedades del Objeto MP: "+ publicacionMP.obtenerPreferencia().getInitPoint());

                promocion.setInit_point_mercadopago(publicacionMP.obtenerPreferencia().getInitPoint());
                promocion.setIdPublicacionMP(publicacionMP.obtenerPreferencia().getId());

                promocion.setId_comercio(c.getId());
                this.promocionRepositorio.save(promocion);
                return true;
            }else{
                log.info(" Por error en MP no puedo guardar la promocion");
                return false;
            }



        }catch (Exception e){
            return false;
        }
    }

    public Boolean editarPromocion (Promocion promocion, LoginDatos ld){
        log.info(" Entramos a editar promocion METODO");
        String idPublicacionMP = promocion.getIdPublicacionMP();
        log.info(" Obtuve idPubMP "+idPublicacionMP);
        Usuario u = this.usuarioServicio.obtener(ld.getIdUsuario());
        Comercio c = this.comercioServicio.obtener(u);

        PublicacionMercadoPago publicacionMP = new PublicacionMercadoPago(c.getAccessToken());
        log.info(" CREE EL OBJETO MP , ahora lo seteo para editar");
        String init_point = publicacionMP.editar(idPublicacionMP, promocion);
        log.info(" El nuevo init point es: "+init_point);

        promocion.setInit_point_mercadopago(publicacionMP.obtenerPreferencia().getInitPoint());
        promocion.setIdPublicacionMP(publicacionMP.obtenerPreferencia().getId());

        promocion.setId_comercio(c.getId());
        this.promocionRepositorio.save(promocion);
        return true;





    }
    public boolean existe (Long idComercio){
        return this.promocionRepositorio.existsByComercio(idComercio);
    }
    public boolean validarTokenUsuario (LoginDatos ld ){
        return this.usuarioServicio.validarTokenUsuario(ld);
    }

    public Promocion obtenerPromocion (Long id){
        return this.promocionRepositorio.findById(id).get();
    }

    public boolean existePromocion (Long id){
        return this.promocionRepositorio.existsById(id);
    }

}
