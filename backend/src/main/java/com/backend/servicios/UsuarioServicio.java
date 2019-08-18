package com.backend.servicios;

import com.backend.entidades.Artista;
import com.backend.entidades.Comercio;
import com.backend.entidades.Rol;
import com.backend.entidades.TokenUsuario;
import com.backend.entidades.Usuario;
import com.backend.recursos.LoginDatos;
import com.backend.repositorios.ArtistaRepositorio;
import com.backend.repositorios.ComercioRepositorio;
import com.backend.repositorios.RolRepositorio;
import com.backend.repositorios.TokenUsuarioRepositorio;
import com.backend.repositorios.UsuarioRepositorio;

import com.backend.singleton.ConfiguradorSingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Set;


@Service
@Transactional
public class UsuarioServicio {
	
    private static final Logger log = LoggerFactory.getLogger(UsuarioServicio.class);
    @Autowired UsuarioRepositorio usuarioRepositorio;
    @Autowired ArtistaRepositorio artistaRepositorio;
    @Autowired ComercioRepositorio comercioRepositorio;
    @Autowired RolRepositorio rolRepositorio;
    @Autowired TokenUsuarioServicio tokenUsuarioServicio;
    @Autowired ComercioServicio comercioServicio;
    @Autowired ComercioServicio artistaServicio;
    @Autowired ConfiguradorSingleton configuradorSingleton;
    public boolean validarCredenciales(Usuario usuarioFrontend){
        try{
            Usuario usuarioBackend = usuarioRepositorio.findByUsername(usuarioFrontend.getUsername());
            if (usuarioBackend.validatePassword(usuarioFrontend.getpwd())){
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            return false;
        }
    }

    public boolean guardar(Usuario usuarioFrontEnd, Artista artista) {
        if (this.usuarioRepositorio.existsByUsername(usuarioFrontEnd.getUsername())) return false; else {
            // Obtengo el rol que me dice
            Rol rol = rolRepositorio.findByNombre("Artista");
            usuarioFrontEnd.addRol(rol);
            
            // Luego encodeo la pass
            usuarioFrontEnd.encodePassword();
            log.info("Persistiendo usuario:"+usuarioFrontEnd.getUsername()+" rol: "+usuarioFrontEnd.rolesToString()+ " Artista: "+artista.getUsuario().getUsername());
            this.usuarioRepositorio.save(usuarioFrontEnd);
            this.artistaRepositorio.save(artista);
    		return true;
        }
    }
    
    public boolean guardar(Usuario usuarioFrontEnd, Comercio comercio) {
    	 if (this.usuarioRepositorio.existsByUsername(usuarioFrontEnd.getUsername())) return false; else {
			 // Obtengo el rol que me dice
			 Rol rol = rolRepositorio.findByNombre("Comercio");
			 usuarioFrontEnd.addRol(rol);
			 
			 // Luego encodeo la pass
			 usuarioFrontEnd.encodePassword();
			 this.usuarioRepositorio.save(usuarioFrontEnd);
			 this.comercioRepositorio.save(comercio);
     		 return true;
         }
    }

    public TokenUsuario generarTokenUsuario(Usuario usuario) {
        log.info( "Generar Token Usuario  -> "+usuario.getUsername());
        TokenUsuario tu = tokenUsuarioServicio.crearTokenUsuario(usuario);
        this.tokenUsuarioServicio.guardarTokenUsuario(tu);
        return tu;
    }

    public boolean validarTokenUsuario (LoginDatos loginDatos){
        Long idUsuarioFE = loginDatos.getIdUsuario();
        String tokenFE = loginDatos.getTokenUsuario();
        log.info("validarTokenUsuario (LoginDatos loginDatos) ");
        TokenUsuario tuBE = tokenUsuarioServicio.obtenerTokenUsuario(idUsuarioFE);
        log.info(" OBTUVIMOS TOKEN USUARIO CON ID USUARIO FE ");
        if ((tuBE != null) && (tokenFE.equals(tuBE.getToken()))){

            return tokenUsuarioServicio.validarToken(tuBE);
            // Si devuelve true -> Válido y renovado
            // Si devuelve false -> Se expiró
        }else{
            log.info(" DATOS INCONSISTENTES; DESLOGUEAR");
            return false;
        }
    }

    public Usuario obtener (Long idUsuario){
       return this.usuarioRepositorio.findById(idUsuario).get();
    }

    public Usuario obtenerPorNombre (String nombre){
        return this.usuarioRepositorio.findByUsername(nombre);
    }
    public boolean existe(Usuario usuarioFrontEnd) {
    	return this.usuarioRepositorio.existsByUsername((usuarioFrontEnd.getUsername()));
    }

	public boolean disponePermisos(LoginDatos ld, String site) {
        Usuario tipoUsuario = this.usuarioRepositorio.findById(ld.getIdUsuario()).get();
        log.info("encontre el usuario completo, valide su token, ahora");
        log.info(" me encargo de ver si sus permisos están en la lista");
        try{
            if (this.comercioServicio.existe(tipoUsuario)){
                log.info(" EL USUARIO ES COMERCIO, entonces..");

                if (this.configuradorSingleton.permisosDelComercio.contains(site)){
                    log.info(" OK -> Singleton: "+this.configuradorSingleton.permisosDelComercio.toString()+ " Subsite: "+site);
                    return true;
                }else{
                    log.info(" No OK -> CHE NO ESTABAN");
                    return false;
                }
            }else{
                // ES un ARTISTa hay que completar
                return false;
            }
        }catch (Exception e){
            log.error("salida por excepcion" +e.getMessage());
            return false;
        }




	}
}
