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
import cl.carlos.banco.dto.PersonaRequest;
import jakarta.validation.Valid;
import cl.carlos.banco.dto.PersonaResponse;
import cl.carlos.banco.dto.PersonaUpdateRequest;
@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    private final PersonaService personaService;

    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @GetMapping
    public List<PersonaResponse> listarPersonas() {
        return personaService.listarPersonas()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }
    @GetMapping("/{rut}")
    public ResponseEntity<PersonaResponse> buscarPorRut(@PathVariable String rut) {

        return personaService.buscarPorRut(rut)
                .map(this::convertirAResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PostMapping
    public PersonaResponse crearPersona(
            @Valid @RequestBody PersonaRequest request) {

        Persona persona = new Persona(
                request.getNombre(),
                request.getRut(),
                request.getFechaNacimiento()
        );

        Persona personaGuardada =
                personaService.guardarPersona(persona);

        return convertirAResponse(personaGuardada);
    }
    @PutMapping("/{rut}")
    public ResponseEntity<PersonaResponse> actualizarPersona(
            @PathVariable String rut,
            @Valid @RequestBody PersonaUpdateRequest request) {

        Persona datosNuevos = new Persona(
                request.getNombre(),
                rut,
                request.getFechaNacimiento()
        );

        return personaService.actualizarPersona(rut, datosNuevos)
                .map(this::convertirAResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    private PersonaResponse convertirAResponse(Persona persona) {
        return new PersonaResponse(
                persona.getNombre(),
                persona.getRut(),
                persona.getFechaNacimiento(),
                persona.getEdad()
        );
    }

}