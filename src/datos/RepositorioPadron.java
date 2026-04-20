package datos;

import entidades.Persona;
import java.io.IOException;
import java.util.Optional;

public interface RepositorioPadron {
    Optional<Persona> buscarPorCedula(String cedulaNormalizada) throws IOException;
}
