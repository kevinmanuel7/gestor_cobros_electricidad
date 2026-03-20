package com.kevin.medidor.model;

import jakarta.persistence.*;

@Entity
@Table(name = "casas")
public class Casa {
    @Id
    private int id; // Número de casa (1 al 9)
    private String nombreArrendatario;
    private double ultimaLecturaKwh;
    private int precioKwh;
 
   // IMPORTANTE: Haz clic derecho aquí adentro -> Insert Code -> Getter and Setter -> Selecciona todos -> Generate

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreArrendatario() {
        return nombreArrendatario;
    }

    public void setNombreArrendatario(String nombreArrendatario) {
        this.nombreArrendatario = nombreArrendatario;
    }

    public double getUltimaLecturaKwh() {
        return ultimaLecturaKwh;
    }

    public void setUltimaLecturaKwh(double ultimaLecturaKwh) {
        this.ultimaLecturaKwh = ultimaLecturaKwh;
    }

    public double getPrecioKwh() {
        return precioKwh;
    }

    public void setPrecioKwh(int precioKwh) {
        this.precioKwh = precioKwh;
    }

}
