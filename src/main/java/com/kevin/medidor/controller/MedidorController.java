package com.kevin.medidor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.kevin.medidor.model.Casa;
import com.kevin.medidor.model.LecturaMensual;
import com.kevin.medidor.repository.CasaRepository;
import com.kevin.medidor.repository.LecturaRepository;


@Controller
public class MedidorController {

    @Autowired
    private CasaRepository repo;

    @Autowired
    private LecturaRepository lecturaRepo;

    // LOGIN: Lo primero que verá el usuario
    @GetMapping("/")
    public String mostrarLogin() {
        return "login";
    }

    // VALIDACIÓN SIMPLE (Punto 1)
    @PostMapping("/login")
    public String validarLogin(@RequestParam String usuario, @RequestParam String clave, Model model) {
        if ("admin".equals(usuario) && "1234".equals(clave)) {
            return "redirect:/menu"; // Si es correcto, va al menú
        } else {
            model.addAttribute("error", "Usuario o clave incorrectos");
            return "login";
        }
    }

    // MENÚ PRINCIPAL (Punto 2)
    @GetMapping("/menu")
    public String mostrarMenu() {
        return "menu";
    }

    // OPCIÓN: ANOTAR LECTURA
    @GetMapping("/anotar")
    public String mostrarIndice(Model model) {
        model.addAttribute("todasLasCasas", repo.findAll());
        return "indice";
    }

    // PROCESAR CALCULO (Lo que ya teníamos, pero redirige a resultado)
    @PostMapping("/calcular")
public String procesarLectura(
        @RequestParam("idCasa") int id, 
        @RequestParam("lecturaActual") double lecturaActual, 
        Model model) {
    
    Casa casa = repo.findById(id).orElseThrow();
    double lecturaAnterior = casa.getUltimaLecturaKwh();

    // 1. VALIDACIÓN (Primero revisamos que el número sea lógico)
    if (lecturaActual < lecturaAnterior) {
        model.addAttribute("error", "⚠️ La lectura actual no puede ser menor a la anterior.");
        model.addAttribute("todasLasCasas", repo.findAll());
        return "indice"; 
    }

    // 2. CÁLCULO (Aquí creamos las variables que te daban error)
    double consumo = lecturaActual - lecturaAnterior;
    int totalAPagar = (int) Math.round(consumo * casa.getPrecioKwh());
    
    // 3. GUARDAR EN HISTORIAL (Ahora sí podemos usar consumo y totalAPagar)
    LecturaMensual historial = new LecturaMensual();
    historial.setCasa(casa);
    historial.setLecturaMedidor(lecturaActual);
    historial.setConsumoKwh(consumo);
    historial.setMontoTotal(totalAPagar);
    
    String mesActual = java.time.format.DateTimeFormatter.ofPattern("MM-yyyy")
                        .format(java.time.LocalDate.now());
    historial.setMesAnio(mesActual);
    
    lecturaRepo.save(historial); 

    // 4. ACTUALIZAR BASE DE LA CASA
    casa.setUltimaLecturaKwh(lecturaActual);
    repo.save(casa);
    
    // 5. ENVIAR DATOS A LA WEB
    model.addAttribute("monto", totalAPagar);
    model.addAttribute("consumoKwh", consumo);
    model.addAttribute("nombre", casa.getNombreArrendatario());
    
    return "resultado";
}

    @GetMapping("/historial")
    public String verHistorial(@RequestParam(name = "idCasa", required = false) Integer idCasa, Model model) {
        if (idCasa != null) {
            // Buscamos solo las lecturas de esa casa
            model.addAttribute("lecturas", lecturaRepo.findByCasaId(idCasa));
            model.addAttribute("casaSeleccionada", repo.findById(idCasa).orElse(null));
        }
        model.addAttribute("todasLasCasas", repo.findAll());
        return "historial";
    }

    @GetMapping("/mensual")
    public String verReporteMensual(@RequestParam(name = "mesSeleccionado", required = false) String mesSeleccionado, Model model) {
        String mesBusqueda;

        if (mesSeleccionado != null && !mesSeleccionado.isEmpty()) {
            // Transformamos "2026-03" a "03-2026" para que coincida con la DB
            String[] partes = mesSeleccionado.split("-");
            mesBusqueda = partes[1] + "-" + partes[0];
        } else {
            // Mes actual por defecto
            mesBusqueda = java.time.format.DateTimeFormatter.ofPattern("MM-yyyy")
                            .format(java.time.LocalDate.now());
        }

        model.addAttribute("lecturasMes", lecturaRepo.findByMesAnio(mesBusqueda));
        model.addAttribute("mesActual", mesBusqueda);
        return "mensual";
    }
}