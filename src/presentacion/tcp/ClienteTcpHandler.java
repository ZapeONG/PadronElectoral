package presentacion.tcp;

import dto.FormatoSalida;
import dto.RespuestaPadron;
import dto.SolicitudPadron;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import logica.ServicioPadron;
import util.Serializador;

public class ClienteTcpHandler implements Runnable {

    private final Socket socket;
    private final ServicioPadron servicioPadron;
    private final SolicitudTcpParser parser;

    public ClienteTcpHandler(Socket socket, ServicioPadron servicioPadron, SolicitudTcpParser parser) {
        this.socket = socket;
        this.servicioPadron = servicioPadron;
        this.parser = parser;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            writer.println("Servidor TCP de padrón listo. Use GET|cedula|JSON o GET|cedula|XML. Escriba BYE para salir.");

            String linea;
            while ((linea = reader.readLine()) != null) {
                try {
                    SolicitudPadron solicitud = parser.parsear(linea);
                    if (solicitud == null) {
                        writer.println("BYE");
                        break;
                    }

                    RespuestaPadron respuesta = servicioPadron.atender(solicitud.getCedula());
                    writer.println(Serializador.serializar(respuesta, solicitud.getFormatoSalida()));
                } catch (IllegalArgumentException e) {
                    RespuestaPadron error = RespuestaPadron.error(400, e.getMessage());
                    writer.println(Serializador.serializar(error, FormatoSalida.JSON));
                } catch (Exception e) {
                    RespuestaPadron error = RespuestaPadron.error(500, "Error interno controlado en el servidor TCP.");
                    writer.println(Serializador.serializar(error, FormatoSalida.JSON));
                }
            }
        } catch (IOException e) {
            // Se ignora para no botar el servidor completo por una conexión individual.
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                // Sin acción adicional.
            }
        }
    }
}
