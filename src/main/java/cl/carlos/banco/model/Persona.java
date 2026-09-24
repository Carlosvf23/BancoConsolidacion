package cl.carlos.banco.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "personas")
public class Persona {

    @Column(name = "nombre")
    private String nombre;
    @Id
    @Column(name = "rut")
    private String rut;
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;
    protected Persona() {
    }
    public Persona(
            String nombre,
            String rut,
            LocalDate fechaNacimiento
    ) {
        this.nombre = nombre;
        this.rut = rut;
        this.fechaNacimiento = fechaNacimiento;
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
        return Period.between(
                fechaNacimiento,
                LocalDate.now()
        ).getYears();
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof Persona persona)) {
            return false;
        }

        return Objects.equals(rut, persona.rut);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rut);
    }

    @Override
    public String toString() {
        return "cl.carlos.banco.model.Persona{" +
                "nombre='" + nombre + '\'' +
                ", rut='" + rut + '\'' +
                ", fechaNacimiento=" + fechaNacimiento +
                '}';
    }
}