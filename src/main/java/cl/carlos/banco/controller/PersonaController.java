package cl.carlos.banco.controller;

import cl.carlos.banco.model.Persona;
import cl.carlos.banco.service.PersonaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    private final PersonaService personaService;

    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @GetMapping
    public List<Persona> listarPersonas() {
        return personaService.listarPersonas();
    }
    @GetMapping("/{rut}")
    public ResponseEntity<Persona> buscarPorRut(@PathVariable String rut) {

        return personaService.buscarPorRut(rut)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PostMapping
    public Persona crearPersona(@RequestBody Persona persona) {
        return personaService.guardarPersona(persona);
    }
    @DeleteMapping("/{rut}")
    public ResponseEntity<Void> eliminarPersona(@PathVariable String rut) {

        if (personaService.buscarPorRut(rut).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        personaService.eliminarPersona(rut);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{rut}")
    public ResponseEntity<Persona> actualizarPersona(
            @PathVariable String rut,
            @RequestBody Persona persona) {

        return personaService.actualizarPersona(rut, persona)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}