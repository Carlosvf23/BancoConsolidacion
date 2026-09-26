package cl.carlos.banco.dto;

import java.time.LocalDate;

public class PersonaResponse {

    private String nombre;
    private String rut;
    private LocalDate fechaNacimiento;
    private int edad;

    public PersonaResponse(
            String nombre,
            String rut,
            LocalDate fechaNacimiento,
            int edad
    ) {
        this.nombre = nombre;
        this.rut = rut;
        this.fechaNacimiento = fechaNacimiento;
        this.edad = edad;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRut() {
        return rut;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public int getEdad() {
        return edad;
    }
}