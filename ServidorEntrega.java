import java.util.Random;

public class ServidorEntrega extends Thread {
    private final int id;
    private final BuzonEntrega buzonEntrega;
    private final Random random;
    private boolean esperandoInicio;

    public ServidorEntrega(int id, BuzonEntrega buzonEntrega) {
        this.id = id;
        this.buzonEntrega = buzonEntrega;
        this.random = new Random();
        this.esperandoInicio = true;
    }

    @Override
    public void run() {
        System.out.println("Servidor " + id + " iniciado");
        while (true) {
            // Espera activa para leer mensajes
            Mensaje mensaje = null;
            while (mensaje == null) { // Bucle de espera activa
                mensaje = buzonEntrega.retirar();
                System.out.println("Servidor procesa mensaje: " + mensaje);
            }

            if (mensaje.isInicio()) {
                System.out.println("Servidor " + id + " recibió INICIO");
                esperandoInicio = false;
                continue;
            }

            if (mensaje.isFin()) {
                System.out.println("Servidor " + id + " recibió FIN");
                System.out.println("Servidor " + id + " ha terminado su ejecución");
                break;
            }
        }
    }
}