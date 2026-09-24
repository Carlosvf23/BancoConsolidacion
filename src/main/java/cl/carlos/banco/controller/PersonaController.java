package cl.carlos.banco.controller;

import cl.carlos.banco.model.Persona;
import cl.carlos.banco.service.PersonaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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
}