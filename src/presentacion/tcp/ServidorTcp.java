package presentacion.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import logica.ServicioPadron;

public class ServidorTcp {

    private final int puerto;
    private final ServicioPadron servicioPadron;
    private final ExecutorService pool;
    private volatile boolean activo;

    public ServidorTcp(int puerto, ServicioPadron servicioPadron, int cantidadHilos) {
        this.puerto = puerto;
        this.servicioPadron = servicioPadron;
        this.pool = Executors.newFixedThreadPool(cantidadHilos);
    }

    public void iniciar() throws IOException {
        activo = true;
        SolicitudTcpParser parser = new SolicitudTcpParser();

        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor TCP escuchando en puerto " + puerto);
            while (activo) {
                Socket cliente = serverSocket.accept();
                pool.submit(new ClienteTcpHandler(cliente, servicioPadron, parser));
            }
        } finally {
            apagarPool();
        }
    }

    private void apagarPool() {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
