package datos;

import entidades.Direccion;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RepositorioDistelecArchivo implements RepositorioDistelec {

    private static final Charset CHARSET_FUENTE = Charset.forName("ISO-8859-1");
    private final Map<String, Direccion> direccionesPorCodigo;

    public RepositorioDistelecArchivo(Path rutaDistelec) throws IOException {
        this.direccionesPorCodigo = cargarArchivo(rutaDistelec);
    }

    @Override
    public Optional<Direccion> buscarPorCodigo(String codigoElectoral) {
        return Optional.ofNullable(direccionesPorCodigo.get(codigoElectoral));
    }

    private Map<String, Direccion> cargarArchivo(Path rutaDistelec) throws IOException {
        Map<String, Direccion> mapa = new HashMap<>();

        try (BufferedReader reader = Files.newBufferedReader(rutaDistelec, CHARSET_FUENTE)) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                Direccion direccion = parsearLinea(linea);
                if (direccion != null) {
                    mapa.put(direccion.getCodigoElectoral(), direccion);
                }
            }
        }

        return Collections.unmodifiableMap(mapa);
    }

    private Direccion parsearLinea(String linea) {
        if (linea == null || linea.isBlank()) {
            return null;
        }

        String[] columnas = linea.split(",", -1);
        if (columnas.length < 4) {
            return null;
        }

        String codigoElectoral = columnas[0].trim();
        String provincia = columnas[1].trim();
        String canton = columnas[2].trim();
        String distrito = columnas[3].trim();

        if (codigoElectoral.isBlank()) {
            return null;
        }

        return new Direccion(codigoElectoral, provincia, canton, distrito);
    }
}
