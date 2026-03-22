package com.kevin.medidor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import com.kevin.medidor.model.Casa;
import com.kevin.medidor.model.LecturaMensual;
import com.kevin.medidor.repository.CasaRepository;
import com.kevin.medidor.repository.LecturaRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class MedidorController {

    @Autowired
    private CasaRepository repo;

    @Autowired
    private LecturaRepository lecturaRepo;

    @Value("${ADMIN_USER:admin}")
    private String adminUser;

    @Value("${ADMIN_PASS:1234}")
    private String adminPass;

    // --- SEGURIDAD: MÉTODO AUXILIAR ---
    // Verifica si el usuario pasó por el login
    private boolean estaLogueado(HttpSession session) {
        return session.getAttribute("usuarioLogueado") != null;
    }

    // LOGIN: Lo primero que verá el usuario
    @GetMapping("/")
    public String mostrarLogin() {
        return "login";
    }

    // VALIDACIÓN
    @PostMapping("/login")
    public String procesarLogin(@RequestParam String usuario,  // Antes decía username
                                @RequestParam String clave,    // Antes decía password
                                HttpSession session, 
                                Model model) {
        
        // Verificamos con los nuevos nombres de variable
        if (adminUser.equals(usuario) && adminPass.equals(clave)) {
            session.setAttribute("usuarioLogueado", usuario); 
            return "redirect:/menu";
        } else {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
            return "login";
        }
    }

    // CERRAR SESIÓN
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Borra todos los datos de la sesión
        return "redirect:/";
    }

    // --- RUTAS PROTEGIDAS ---

    @GetMapping("/menu")
    public String mostrarMenu(HttpSession session) {
        if (!estaLogueado(session)) return "redirect:/";
        return "menu";
    }

    @GetMapping("/anotar")
    public String mostrarIndice(HttpSession session, Model model) {
        if (!estaLogueado(session)) return "redirect:/";
        model.addAttribute("todasLasCasas", repo.findAll());
        return "indice";
    }

    @PostMapping("/calcular")
    public String procesarLectura(
            @RequestParam("idCasa") int id, 
            @RequestParam("lecturaActual") double lecturaActual, 
            HttpSession session, // Agregado por seguridad
            Model model) {
        
        if (!estaLogueado(session)) return "redirect:/";
        
        Casa casa = repo.findById(id).orElseThrow();
        double lecturaAnterior = casa.getUltimaLecturaKwh();

        if (lecturaActual < lecturaAnterior) {
            model.addAttribute("error", "⚠️ La lectura actual no puede ser menor a la anterior.");
            model.addAttribute("todasLasCasas", repo.findAll());
            return "indice"; 
        }

        double consumo = lecturaActual - lecturaAnterior;
        int totalAPagar = (int) Math.round(consumo * casa.getPrecioKwh());
        
        LecturaMensual historial = new LecturaMensual();
        historial.setCasa(casa);
        historial.setLecturaMedidor(lecturaActual);
        historial.setConsumoKwh(consumo);
        historial.setMontoTotal(totalAPagar);
        
        String mesActual = java.time.format.DateTimeFormatter.ofPattern("MM-yyyy")
                            .format(java.time.LocalDate.now());
        historial.setMesAnio(mesActual);
        
        lecturaRepo.save(historial); 

        casa.setUltimaLecturaKwh(lecturaActual);
        repo.save(casa);
        
        model.addAttribute("monto", totalAPagar);
        model.addAttribute("consumoKwh", consumo);
        model.addAttribute("nombre", casa.getNombreArrendatario());
        
        return "resultado";
    }

    @GetMapping("/historial")
    public String verHistorial(@RequestParam(name = "idCasa", required = false) Integer idCasa, 
                               HttpSession session, 
                               Model model) {
        if (!estaLogueado(session)) return "redirect:/";

        if (idCasa != null) {
            model.addAttribute("lecturas", lecturaRepo.findByCasaId(idCasa));
            model.addAttribute("casaSeleccionada", repo.findById(idCasa).orElse(null));
        }
        model.addAttribute("todasLasCasas", repo.findAll());
        return "historial";
    }

    @GetMapping("/mensual")
    public String verReporteMensual(@RequestParam(name = "mesSeleccionado", required = false) String mesSeleccionado, 
                                    HttpSession session, 
                                    Model model) {
        if (!estaLogueado(session)) return "redirect:/";

        String mesBusqueda;
        if (mesSeleccionado != null && !mesSeleccionado.isEmpty()) {
            String[] partes = mesSeleccionado.split("-");
            mesBusqueda = partes[1] + "-" + partes[0];
        } else {
            mesBusqueda = java.time.format.DateTimeFormatter.ofPattern("MM-yyyy")
                            .format(java.time.LocalDate.now());
        }

        model.addAttribute("lecturasMes", lecturaRepo.findByMesAnio(mesBusqueda));
        model.addAttribute("mesActual", mesBusqueda);
        return "mensual";
    }
}