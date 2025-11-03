import java.util.ArrayList;
import java.util.Random;

public class BuzonCuarentena {
    private final ArrayList<Mensaje> mensajes;
    private boolean finRecibido = false;
    private final Random random = new Random();

    public BuzonCuarentena() {
        this.mensajes = new ArrayList<>();
    }

    public void depositar(Mensaje mensaje) {
        mensaje.setTiempoCuarentena(new Random().nextInt(11) + 10);
        synchronized (this) {
            mensajes.add(mensaje);
            System.out.println("Cuarentena recibe: " + mensaje);
            notifyAll();
        }
    }
    

    public synchronized Mensaje[] obtenerMensajes() {
        return mensajes.toArray(new Mensaje[0]);
    }

    public synchronized void removerMensaje(Mensaje mensaje) {
        mensajes.remove(mensaje);
        System.out.println("Cuarentena entrega mensaje para revisión: " + mensaje);
        notifyAll();
    }

    public synchronized void marcarFinRecibido() {
        this.finRecibido = true;
        System.out.println("[BuzonCuarentena] 🏁 Recibida señal de finalización");
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