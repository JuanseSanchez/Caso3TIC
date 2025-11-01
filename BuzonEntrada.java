import java.util.ArrayList;

public class BuzonEntrada {
    private final ArrayList<Mensaje> mensajes;
    private final int capacidadMaxima;

    public BuzonEntrada(int capacidadMaxima) {
        this.mensajes = new ArrayList<>();
        this.capacidadMaxima = capacidadMaxima;
    }

    public synchronized void depositar(Mensaje mensaje) throws InterruptedException {
        while (mensajes.size() >= capacidadMaxima) {
            wait(); // Espera pasiva si el buzón está lleno
        }
        mensajes.add(mensaje);
        System.out.println("BuzonEntrada recibe mensaje: " + mensaje);
        notify(); // Notifica a UN filtro que hay un nuevo mensaje
    }

    public synchronized Mensaje retirar() throws InterruptedException {
        while (mensajes.isEmpty()) {
            wait(); // Espera pasiva si el buzón está vacío
        }
        Mensaje mensaje = mensajes.remove(0);
        System.out.println("BuzonEntrada entrega mensaje a Filtro: " + mensaje);
        if (mensajes.size() < capacidadMaxima) {
            notify(); // Notifica a UN productor que hay espacio
        }
        return mensaje;
    }

    public synchronized boolean estaVacio() {
        return mensajes.isEmpty();
    }

    public synchronized int size() {
        return mensajes.size();
    }
}