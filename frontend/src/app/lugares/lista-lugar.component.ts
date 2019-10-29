import { Component, OnInit } from '@angular/core';

import { Router } from '@angular/router';
import Swal from 'sweetalert2';
import { Lugar } from '../modelos/lugar';
import { LugarService } from '../servicios/lugar.service';
import { UsuarioService } from '../servicios/usuario.service';
import { LoginDatos } from '../modelos/logindatos';

@Component({
  selector: 'app-lista-lugar',
  templateUrl: './lista-lugar.component.html',
  styleUrls: ['./lista-lugar.component.css']
})
export class ListaLugarComponent implements OnInit {

  lugares: Lugar[] = [];
  userLogged: LoginDatos;
  constructor(private lugarService: LugarService, private router: Router, private usuarioService: UsuarioService) { }

  ngOnInit() {
    
    this.userLogged = this.usuarioService.getUserLoggedIn();
    this.cargarLugares();
  }

  cargarLugares(): void {
    this.lugarService.listarLosMios(this.userLogged).subscribe(data => {
    //this.lugarService.lista().subscribe(data => {
      this.lugares = data;
    },
      (err: any) => {
        console.log(err);
      });
  }

  onDelete(id: number): void {
    Swal.fire({
      title: '¿Estás seguro?',
      text: "La eliminacion es permanente !",
      type: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'eliminar!',
      background: 'url(./assets/img/guitar_music_strings_musical_instrument_111863_1920x1080.jpg)'
    }).then((confirmado) => {
      if (confirmado.value) {

        this.lugarService.borrar(id).subscribe(data => {
          this.router.navigate(['/lugar/'])
          this.cargarLugares();
        });

        Swal.fire(
          'Eliminado!',
          '.',
          'success'
        );
      }
    });
  }
}