package com.kevin.medidor.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lecturas_mensuales")
public class LecturaMensual {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "casa_id")
    private Casa casa;

    private String mesAnio;
    private double lecturaMedidor;
    private double consumoKwh;
    private int montoTotal;
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    // Getters y Setters (Muy importantes para que Thymeleaf lea los datos)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Casa getCasa() { return casa; }
    public void setCasa(Casa casa) { this.casa = casa; }
    public String getMesAnio() { return mesAnio; }
    public void setMesAnio(String mesAnio) { this.mesAnio = mesAnio; }
    public double getLecturaMedidor() { return lecturaMedidor; }
    public void setLecturaMedidor(double lecturaMedidor) { this.lecturaMedidor = lecturaMedidor; }
    public double getConsumoKwh() { return consumoKwh; }
    public void setConsumoKwh(double consumoKwh) { this.consumoKwh = consumoKwh; }
    public int getMontoTotal() { return montoTotal; }
    public void setMontoTotal(int montoTotal) { this.montoTotal = montoTotal; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}