package presentacion.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dto.FormatoSalida;
import dto.RespuestaPadron;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import logica.ServicioPadron;
import util.Serializador;

public class PadronHttpHandler implements HttpHandler {

    private final ServicioPadron servicioPadron;

    public PadronHttpHandler(ServicioPadron servicioPadron) {
        this.servicioPadron = servicioPadron;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        FormatoSalida formato = FormatoSalida.JSON;
        try {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                responder(exchange, FormatoSalida.JSON,
                        RespuestaPadron.error(405, "Método no permitido. Solo se admite GET."));
                return;
            }

            Map<String, String> queryParams = parsearQuery(exchange.getRequestURI().getRawQuery());
            String ruta = exchange.getRequestURI().getPath();
            String cedula = extraerCedula(ruta, queryParams);
            formato = FormatoSalida.desde(queryParams.getOrDefault("format", "json"));

            RespuestaPadron respuesta = servicioPadron.atender(cedula);
            responder(exchange, formato, respuesta);
        } catch (IllegalArgumentException e) {
            responder(exchange, formato, RespuestaPadron.error(400, e.getMessage()));
        } catch (Exception e) {
            responder(exchange, formato, RespuestaPadron.error(500, "Error interno controlado en el servidor HTTP."));
        }
    }

    private String extraerCedula(String ruta, Map<String, String> queryParams) {
        if (queryParams.containsKey("cedula")) {
            return queryParams.get("cedula");
        }

        String prefijo = "/padron/";
        if (ruta != null && ruta.startsWith(prefijo) && ruta.length() > prefijo.length()) {
            return ruta.substring(prefijo.length());
        }

        throw new IllegalArgumentException("Debe indicar la cédula en /padron?cedula=... o en /padron/{cedula}.");
    }

    private Map<String, String> parsearQuery(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isBlank()) {
            return params;
        }

        String[] pares = query.split("&");
        for (String par : pares) {
            String[] partes = par.split("=", 2);
            String llave = URLDecoder.decode(partes[0], StandardCharsets.UTF_8);
            String valor = partes.length > 1 ? URLDecoder.decode(partes[1], StandardCharsets.UTF_8) : "";
            params.put(llave, valor);
        }
        return params;
    }

    private void responder(HttpExchange exchange, FormatoSalida formato, RespuestaPadron respuesta) throws IOException {
        String cuerpo = Serializador.serializar(respuesta, formato);
        byte[] bytes = cuerpo.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", formato.getContentType());
        exchange.sendResponseHeaders(respuesta.getCodigoHttp(), bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
