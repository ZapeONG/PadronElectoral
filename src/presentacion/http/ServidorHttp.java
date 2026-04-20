package presentacion.http;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import logica.ServicioPadron;

public class ServidorHttp {

    private final int puerto;
    private final ServicioPadron servicioPadron;
    private HttpServer server;

    public ServidorHttp(int puerto, ServicioPadron servicioPadron) {
        this.puerto = puerto;
        this.servicioPadron = servicioPadron;
    }

    public void iniciar() throws IOException {
        server = HttpServer.create(new InetSocketAddress(puerto), 0);
        PadronHttpHandler handler = new PadronHttpHandler(servicioPadron);
        server.createContext("/padron", handler);
        server.createContext("/padron/", handler);
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
        System.out.println("Servidor HTTP escuchando en puerto " + puerto);
    }
}
