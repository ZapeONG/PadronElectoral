package presentacion.tcp;

import dto.FormatoSalida;
import dto.SolicitudPadron;

public class SolicitudTcpParser {

    public SolicitudPadron parsear(String linea) {
        if (linea == null || linea.isBlank()) {
            throw new IllegalArgumentException("Solicitud TCP vacía. Use: GET|cedula|JSON o GET|cedula|XML");
        }

        String texto = linea.trim();
        if (texto.equalsIgnoreCase("BYE")) {
            return null;
        }

        String[] partes = texto.split("\\|", -1);
        if (partes.length != 3) {
            throw new IllegalArgumentException("Formato TCP inválido. Use: GET|cedula|JSON o GET|cedula|XML");
        }

        if (!"GET".equalsIgnoreCase(partes[0].trim())) {
            throw new IllegalArgumentException("Comando TCP inválido. Solo se permite GET.");
        }

        return new SolicitudPadron(partes[1].trim(), FormatoSalida.desde(partes[2].trim()));
    }
}
