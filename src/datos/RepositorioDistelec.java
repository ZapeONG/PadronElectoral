package datos;

import entidades.Direccion;
import java.util.Optional;

public interface RepositorioDistelec {
    Optional<Direccion> buscarPorCodigo(String codigoElectoral);
}
