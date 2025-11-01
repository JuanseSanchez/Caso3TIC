import java.util.ArrayList;

public class BuzonEntrega {
    private final ArrayList<Mensaje> mensajes;
    private final int capacidad;
    private boolean finRecibido = false;

    public BuzonEntrega(int capacidad) {
        this.mensajes = new ArrayList<>();
        this.capacidad = capacidad;
    }

    public synchronized void depositar(Mensaje mensaje) {
        if (mensaje.isInicio()) { 
        System.out.println("BuzonEntrega descarta mensaje INICIO: " + mensaje);
        return; // No se encola, no se notifica a servidor
    }
        while (mensajes.size() >= capacidad) {
            System.out.println("[BuzonEntrega] ⏳ Buzón lleno, esperando espacio...");
            Thread.yield(); // Espera semi-activa
        }
        mensajes.add(mensaje);
        System.out.println("BuzonEntrega recibe mensaje: " + mensaje);
        notifyAll();
    }

    public synchronized Mensaje retirar() {
        while (true) {
            if (!mensajes.isEmpty()) {
                Mensaje mensaje = mensajes.remove(0);
                System.out.println("BuzonEntrega entrega mensaje a Servidor: " + mensaje);
                notifyAll();
                return mensaje;
            }
            if (finRecibido && mensajes.isEmpty()) {
                System.out.println("BuzonEntrega recibió señal de fin");
                return null;
            }
            Thread.yield(); // Espera semi-activa
        }
    }

    public synchronized void marcarFinRecibido() {
        this.finRecibido = true;
        System.out.println("BuzonEntrega sin mensajes y con fin recibido");
        notifyAll();
    }

    public synchronized boolean isFinRecibido() {
        return finRecibido;
    }

    public synchronized boolean estaVacio() {
        return mensajes.isEmpty();
    }

    public synchronized int size() {
        return mensajes.size();
    }
}