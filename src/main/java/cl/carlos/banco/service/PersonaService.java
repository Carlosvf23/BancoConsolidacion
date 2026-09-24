package cl.carlos.banco.service;

import cl.carlos.banco.model.Persona;
import cl.carlos.banco.repository.PersonaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonaService {

    private final PersonaRepository personaRepository;

    public PersonaService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    public List<Persona> listarPersonas() {
        return personaRepository.findAll();
    }

    public Optional<Persona> buscarPorRut(String rut) {
        return personaRepository.findById(rut);
    }
}