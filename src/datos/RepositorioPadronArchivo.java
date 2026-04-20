package datos;

import entidades.Persona;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class RepositorioPadronArchivo implements RepositorioPadron {

    private static final Charset CHARSET_FUENTE = Charset.forName("ISO-8859-1");
    private final Path rutaPadron;

    public RepositorioPadronArchivo(Path rutaPadron) {
        this.rutaPadron = rutaPadron;
    }

    @Override
    public Optional<Persona> buscarPorCedula(String cedulaNormalizada) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(rutaPadron, CHARSET_FUENTE)) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                Persona persona = parsearLinea(linea);
                if (persona != null && persona.getCedula().equals(cedulaNormalizada)) {
                    return Optional.of(persona);
                }
            }
        }
        return Optional.empty();
    }

    private Persona parsearLinea(String linea) {
        if (linea == null || linea.isBlank()) {
            return null;
        }

        String[] columnas = linea.split(",", -1);
        if (columnas.length < 8) {
            return null;
        }

        String cedula = columnas[0].trim();
        String codigoElectoral = columnas[1].trim();
        String nombre = columnas[5].trim();
        String primerApellido = columnas[6].trim();
        String segundoApellido = columnas[7].trim();

        if (cedula.isBlank()) {
            return null;
        }

        return new Persona(cedula, codigoElectoral, nombre, primerApellido, segundoApellido);
    }
}
