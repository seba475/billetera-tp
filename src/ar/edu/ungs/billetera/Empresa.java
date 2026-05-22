package ar.edu.ungs.billetera;

import java.util.ArrayList;
import java.util.List;

public class Empresa {

    private String cuit;
    private String nombreFantasia;
    private String telefono;
    private String email;
    private String nombreContacto;
    private List<String> personasAutorizadas;

    public Empresa(String cuit, String nombreFantasia, String telefono, String email, String nombreContacto) {
        this.cuit = cuit;
        this.nombreFantasia = nombreFantasia;
        this.telefono = telefono;
        this.email = email;
        this.nombreContacto = nombreContacto;
        this.personasAutorizadas = new ArrayList<>();
    }

    public void agregarAutorizado(String dni) {
        if (personasAutorizadas.contains(dni)) {
            throw new IllegalArgumentException("La persona ya está autorizada");
        }
        personasAutorizadas.add(dni);
    }

    public boolean estaAutorizado(String dni) {
        return personasAutorizadas.contains(dni);
    }

    public String obtenerCuit() {
        return cuit;
    }

    @Override
    public String toString() {
        return "(Empresa: " + nombreFantasia + " - CUIT: " + cuit + ")";
    }
}