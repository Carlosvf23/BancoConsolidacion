import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {

        Persona persona1 = new Persona(
                "Luciano",
                "19.054.257-2",
                LocalDate.of(1995, 3, 7)
        );

        System.out.println("Edad: " + persona1.getEdad());
    }
}