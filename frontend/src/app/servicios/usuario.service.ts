import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Usuario } from '../modelos/usuario';
import { LoginDatos } from '../modelos/logindatos';
import { Mensaje } from '../modelos/mensaje';

const cabecera = {headers: new HttpHeaders({'Content-Type': 'application/json'})};

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {
 

  private isUserLoggedIn = false;
  usuarioFrontEnd: Usuario = new Usuario();
  loginDatos: LoginDatos = new LoginDatos();
  valueByGet : String;
  // DESARROLLO URL
  baseURL = 'http://localhost:8081/api/usuario/';

  // PROD URL
  //baseURL = 'http://ec2-3-93-69-45.compute-1.amazonaws.com:9000/api/usuario/';

  constructor(private httpClient: HttpClient) { }


  setUserLoggedIn(loginDat: LoginDatos) {
    this.isUserLoggedIn = true;
    this.loginDatos = loginDat;
    localStorage.setItem('currentUser', JSON.stringify(this.loginDatos));
  }

  getUserLoggedIn() {
  	return JSON.parse(localStorage.getItem('currentUser'));
  }

  public chequearPermisosPorRol(ld : LoginDatos): Observable <Mensaje> {
    return this.httpClient.post<Mensaje>(this.baseURL + `chequear_permisos_por_rol`,ld, cabecera);
  }
  public chequearPermisosPorSubsite(ld : LoginDatos,sitio : String): Observable <Mensaje> {
    
    return this.httpClient.post<Mensaje>(this.baseURL + `chequear_permisos_por_subsite`,{ld,sitio}, cabecera);
  }

  public validar(loginDatos: LoginDatos): Observable<boolean> {
    //public ingresar(usuario: Usuario): Observable<any> {
      
      //alert (JSON.stringify(loginDatos));
      return this.httpClient.post<any>(this.baseURL + 'validar', loginDatos, cabecera);
    }

    
  public obtenerTodos (ld : LoginDatos) : Observable <any> {
      return this.httpClient.post<any>(this.baseURL + 'obtenerTodos', ld, cabecera);
  }
  
  public ingresar(usuarioForm: Usuario): Observable<any> {
  //public ingresar(usuario: Usuario): Observable<any> {
    
    this.usuarioFrontEnd.username = usuarioForm.username;
    this.usuarioFrontEnd.pwd = usuarioForm.pwd;
    return this.httpClient.post<any>(this.baseURL + 'ingresar', this.usuarioFrontEnd, cabecera);
  }

  public registrar(form: any, isArtista: boolean, instrumentosSeleccionados : string[]): Observable<any> {
    
    this.usuarioFrontEnd.username = form.username;
    this.usuarioFrontEnd.pwd = form.pwd;
    this.usuarioFrontEnd.email = form.email;

    return this.httpClient.post<any>(this.baseURL + `registrar`, {usuario : this.usuarioFrontEnd, formulario: form, instrumentos: instrumentosSeleccionados.toString()}, cabecera); 
    
  }

  public verificarComercioActivado(ld : LoginDatos): Observable<any>{
    
    return this.httpClient.post<any>(this.baseURL + `comercio_esta_activado`, ld, cabecera); 
  }

  public obtenerDatosUsuario (login : LoginDatos): Observable<any> {
    return this.httpClient.post<any>(this.baseURL + 'obtenerDatosUsuario',login, cabecera);
  }
  
}

