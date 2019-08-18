package com.backend.controladores;

import com.backend.dto.Mensaje;
import com.backend.entidades.*;
import com.backend.recursos.LoginDatos;

import com.backend.servicios.ComercioServicio;
import com.backend.servicios.PromocionServicio;

import com.backend.servicios.UsuarioServicio;
import com.backend.wrappers.PromocionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/promocion/")
@CrossOrigin(origins = "*")

public class PromocionControlador {


    @Autowired
    PromocionServicio promocionServicio;

    @Autowired
    UsuarioServicio usuarioServicio;

    @Autowired
    ComercioServicio comercioServicio;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    @PostMapping("listar")
    public ResponseEntity<?> registrar(@RequestBody LoginDatos ld){
        log.info(" POST -> /listar/ \n User Logged: "+ld.getNombreUsuario());
        try{
            if (promocionServicio.validarTokenUsuario(ld)){
            	if(promocionServicio.permisos(ld)){
	                log.info("Buscando promociones");
	                Usuario user = this.usuarioServicio.obtener(ld.getIdUsuario());
	                Comercio comercio = this.comercioServicio.obtener(user);
	                if (this.promocionServicio.existe(comercio.getId())){
	                    ArrayList<Promocion> promociones = this.promocionServicio.obtenerPromociones(comercio.getId());
	                    log.info ("Cantidad de promociones: "+promociones.size());
	                    return new ResponseEntity<ArrayList<Promocion>>(promociones, HttpStatus.OK);
	                }else{
	                    log.info (" No hay promociones" );
	                    return new ResponseEntity<String>("no hay promociones", HttpStatus.OK);
	                }
            	}else {
            		return new ResponseEntity<Mensaje>(new Mensaje("Acceso no autorizado"), HttpStatus.UNAUTHORIZED);
            	}
            }
            return new ResponseEntity<String>("Token de autenticación no válido", HttpStatus.BAD_REQUEST);

        }catch (Exception e){
            log.info("Estamos saliendo por except "+e.getMessage());
            return new ResponseEntity<Object>(null, HttpStatus.BAD_REQUEST);
        }


    }

    @PostMapping("nuevo")
    public ResponseEntity<?> nuevo(@RequestBody PromocionRequest request){
        log.info(" POST -> /nuevo/ ");
        LoginDatos ld = request.getLoginDatos();
        Promocion promocion = request.getPromocion();

        log.info(" TU -> /nuevo/ -> User: "+ld.getNombreUsuario());
        log.info(" Articulo -> /nuevo/ -> Titulo: "+promocion.getTitulo());
        try{
            if (promocionServicio.validarTokenUsuario(ld)) {
            	if(promocionServicio.permisos(ld)) {
                    String resultadoNuevaPromocion = this.promocionServicio.nuevaPromocion(promocion, ld);
                    if (resultadoNuevaPromocion.equals("ok")){
                        log.info(" GUARDE CON EXITO, respondo");
                        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
                    }else {
                        if (resultadoNuevaPromocion.equals("No se pudo publicar en MP la promocion")){
                            log.info(" Tuve problemas con MP");
                            return new ResponseEntity<String>(resultadoNuevaPromocion, HttpStatus.BAD_REQUEST);
                        }else{
                             // ES DECIR EXCEPCION
                            log.info(" Salí por except: "+resultadoNuevaPromocion);
                            return new ResponseEntity<String>(resultadoNuevaPromocion, HttpStatus.BAD_REQUEST);
                        }
                    }
            	}else {
            		return new ResponseEntity<Mensaje>(new Mensaje("Acceso no autorizado"), HttpStatus.UNAUTHORIZED);
            	}
            }	
            return new ResponseEntity<String>("Token de autenticación no válido", HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            return new ResponseEntity<Object>(null, HttpStatus.BAD_REQUEST);
        }


    }

    @PostMapping("detalle/{id}")
    public ResponseEntity<?> detalle (@PathVariable Long id, @RequestBody LoginDatos ld){
        log.info(" POST -> /detalle/id: "+ld.getIdUsuario());

        try{
            if (promocionServicio.validarTokenUsuario(ld)){
	        	if(promocionServicio.permisos(ld)) {
	                log.info(" Vamos a buscar los detalles de la promocion ");
	                if (this.promocionServicio.existePromocion(id)) {
	                    log.info(" La promoción existe");
	                    Promocion p = this.promocionServicio.obtenerPromocion(id);
	                    log.info(" Obtuve los datos de la promoción, se la devuelvo a FE");
	                    return new ResponseEntity<Promocion>(p, HttpStatus.OK);
	                }else{
	                    return new ResponseEntity<String>(" Id de promoción inexistente ", HttpStatus.OK);
	                }
	            }else {
            		return new ResponseEntity<Mensaje>(new Mensaje("Acceso no autorizado"), HttpStatus.UNAUTHORIZED);
	            }
            }
            return new ResponseEntity<String>("Token de autenticación no válido", HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            log.info("Estamos saliendo por except "+e.getMessage());
            return new ResponseEntity<Object>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("editar")
    public ResponseEntity<?> editar (@RequestBody PromocionRequest request){
        LoginDatos ld = request.getLoginDatos();
        Promocion promocion = request.getPromocion();
        log.info(" POST -> /editar/ \n User Logged: "+ld.getNombreUsuario());
        try{
            if (promocionServicio.validarTokenUsuario(ld)){
	        	if(promocionServicio.permisos(ld)) {
	                log.info(" Vamos a editar la promocion con ID "+promocion.getId());
	                // los datos de promocion ya están editados, lo que hay que hacer es actualizar las propiedades de MPAGO
	                this.promocionServicio.editarPromocion(promocion, ld);
	                log.info(" TERMINE DE EDITAR y GUARDE");
	                return new ResponseEntity<Mensaje>(new Mensaje("Editado correctamente"), HttpStatus.OK);
	        	}else {
            		return new ResponseEntity<Mensaje>(new Mensaje("Acceso no autorizado"), HttpStatus.UNAUTHORIZED);
	        	}
            }
            return new ResponseEntity<String>("Token de autenticación no válido", HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            log.info("Estamos saliendo por except "+e.getMessage());
            return new ResponseEntity<Object>(null, HttpStatus.BAD_REQUEST);
        }


    }
}
