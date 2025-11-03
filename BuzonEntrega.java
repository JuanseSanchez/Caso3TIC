import java.util.ArrayList;

public class BuzonEntrega {
    private final ArrayList<Mensaje> mensajes;
    private final int capacidad;
    private int numServidores;

    public BuzonEntrega(int capacidad) {
        this.mensajes = new ArrayList<>();
        this.capacidad = capacidad;
    }

    public void depositar(Mensaje mensaje) {
        boolean puesto = false;
        while (!puesto) {
            synchronized (this) {
                if (mensajes.size() < capacidad) {
                    mensajes.add(mensaje);
                    System.out.println("BuzonEntrega recibe: " + mensaje);
                    notifyAll();
                    puesto = true;
                }
            }
            if (!puesto) Thread.yield(); // esperar un poquito y reintentar
        }
    }

    public synchronized Mensaje retirar() {
        if (!mensajes.isEmpty()) {
            Mensaje m = mensajes.remove(0);
            System.out.println("Entrega da a Servidor: " + m);
            notifyAll();
            return m;
        }
        return null; // vacío, el servidor hace la espera activa
    }
    

    public synchronized void setNumServidores(int n) { 
        this.numServidores = n; 
    }
    
    public void broadcastFin() {
        for (int i = 0; i < numServidores; i++) {
            depositar(Mensaje.crearMensajeFin(-1));
        }
    }

    public synchronized boolean estaVacio() {
        return mensajes.isEmpty();
    }

    public synchronized int size() {
        return mensajes.size();
    }
}