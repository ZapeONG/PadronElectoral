package logica;

import datos.RepositorioDistelec;
import datos.RepositorioPadron;
import dto.RespuestaPadron;
import entidades.Direccion;
import entidades.Persona;
import java.io.IOException;
import java.util.Optional;
import util.ValidadorCedula;

public class ServicioPadron {

    private final RepositorioPadron repositorioPadron;
    private final RepositorioDistelec repositorioDistelec;

    public ServicioPadron(RepositorioPadron repositorioPadron, RepositorioDistelec repositorioDistelec) {
        this.repositorioPadron = repositorioPadron;
        this.repositorioDistelec = repositorioDistelec;
    }

    public RespuestaPadron atender(String cedulaCruda) {
        try {
            String cedulaNormalizada = ValidadorCedula.normalizar(cedulaCruda);
            Optional<Persona> personaOpt = repositorioPadron.buscarPorCedula(cedulaNormalizada);

            if (personaOpt.isEmpty()) {
                return RespuestaPadron.error(404, "La cédula consultada no fue encontrada.");
            }

            Persona persona = personaOpt.get();
            Direccion direccion = repositorioDistelec.buscarPorCodigo(persona.getCodigoElectoral()).orElse(null);
            return RespuestaPadron.exito(persona, direccion);
        } catch (IllegalArgumentException e) {
            return RespuestaPadron.error(400, e.getMessage());
        } catch (IOException e) {
            return RespuestaPadron.error(500, "Ocurrió un error al leer los archivos de datos.");
        } catch (Exception e) {
            return RespuestaPadron.error(500, "Ocurrió un error interno controlado.");
        }
    }
}
