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
        while (true) {
            try {
                mensaje.setTiempoCuarentena(random.nextInt(10001) + 10000); // Entre 10000 y 20000
                synchronized(this) {
                    mensajes.add(mensaje);
                    System.out.println("Cuarentena recibe mensaje: " + mensaje);
                    return;
                }
            } catch (Exception e) {
                System.out.println("[BuzonCuarentena] ⚠️ Error al depositar mensaje, reintentando...");
                Thread.yield(); // Espera semi-activa si hay algún problema
            }
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