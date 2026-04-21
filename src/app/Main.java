package app;

import datos.RepositorioDistelec;
import datos.RepositorioDistelecArchivo;
import datos.RepositorioPadron;
import datos.RepositorioPadronArchivo;
import java.nio.file.Path;
import logica.ServicioPadron;
import presentacion.gui.VentanaPrincipal;
import presentacion.http.ServidorHttp;
import presentacion.tcp.ServidorTcp;

public class Main {

    private static final int PUERTO_TCP = 5555;
    private static final int PUERTO_HTTP = 9090;

    public static void main(String[] args) {
        try {
            Path base = Path.of(System.getProperty("user.dir"));
            Path rutaPadron = base.resolve("PADRON_COMPLETO.txt");
            Path rutaDistelec = base.resolve("distelec.txt");

            RepositorioPadron repositorioPadron = new RepositorioPadronArchivo(rutaPadron);
            RepositorioDistelec repositorioDistelec = new RepositorioDistelecArchivo(rutaDistelec);
            ServicioPadron servicioPadron = new ServicioPadron(repositorioPadron, repositorioDistelec);

            ServidorHttp servidorHttp = new ServidorHttp(PUERTO_HTTP, servicioPadron);
            servidorHttp.iniciar();

            ServidorTcp servidorTcp = new ServidorTcp(PUERTO_TCP, servicioPadron, 10);
            Thread hiloTcp = new Thread(() -> {
                try {
                    servidorTcp.iniciar();
                } catch (Exception e) {
                    System.err.println("No fue posible iniciar el servidor TCP: " + e.getMessage());
                    e.printStackTrace();
                }
            }, "servidor-tcp");
            hiloTcp.setDaemon(true);
            hiloTcp.start();

            System.out.println("Servidor HTTP escuchando en puerto " + PUERTO_HTTP);
            System.out.println("Servidor TCP escuchando en puerto " + PUERTO_TCP);

            VentanaPrincipal.mostrar(servicioPadron, PUERTO_HTTP, PUERTO_TCP);
        } catch (Exception e) {
            System.err.println("No fue posible iniciar el sistema: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
