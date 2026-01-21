package com.mycompany.recuperacion;

import controlador.FacturaControlador;
import modelo.FacturaD;
import vista.FacturaVista;

public class Recuperacion {

    public static void main(String[] args) {
        // 1. Instanciar (crear) la Vista
        FacturaVista vista = new FacturaVista();
        
        // 2. Instanciar el DAO (Modelo de Base de Datos)
        FacturaD dao = new FacturaD();
        
        // 3. Instanciar el Controlador y pasarle la vista y el dao
        FacturaControlador ctrl = new FacturaControlador(vista, dao);
        
        // 4. Iniciar la aplicación
        ctrl.iniciar();
    }
}