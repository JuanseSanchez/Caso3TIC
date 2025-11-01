import java.util.Random;

public class ManejadorCuarentena extends Thread {
    private final BuzonCuarentena buzonCuarentena;
    private final BuzonEntrega buzonEntrega;
    private final Random random;
    private boolean ejecutando;

    public ManejadorCuarentena(BuzonCuarentena buzonCuarentena, BuzonEntrega buzonEntrega) {
        this.buzonCuarentena = buzonCuarentena;
        this.buzonEntrega = buzonEntrega;
        this.random = new Random();
        this.ejecutando = true;
    }

    @Override
    public void run() {
        long ultimaEjecucion = System.currentTimeMillis();
        while (ejecutando) {
            long tiempoActual = System.currentTimeMillis();
            if (tiempoActual - ultimaEjecucion >= 1000) { // Ejecutar cada segundo
                ultimaEjecucion = tiempoActual;
                
                Mensaje[] mensajes = buzonCuarentena.obtenerMensajes();
                for (Mensaje mensaje : mensajes) {
                    System.out.println("ManejadorCuarentena revisa mensaje: " + mensaje);
                    if (mensaje.isFin()) {
                        ejecutando = false;
                        break;
                    }

                    mensaje.decrementarTiempoCuarentena();
                    
                    if (mensaje.getTiempoCuarentena() <= 0) {
                        int numeroAleatorio = random.nextInt(21) + 1;
                        
                        if (numeroAleatorio % 7 != 0) {
                            while (true) { // Espera semi-activa para depositar en entrega
                                try {
                                    System.out.println("ManejadorCuarentena libera mensaje a Entrega: " + mensaje);
                                    buzonEntrega.depositar(mensaje);
                                    break;
                                } catch (Exception e) {
                                    Thread.yield();
                                }
                            }
                        } else {
                            System.out.println("ManejadorCuarentena descarta mensaje: " + mensaje);
                        }
                        
                        buzonCuarentena.removerMensaje(mensaje);
                    }
                }
            }
            Thread.yield(); // Espera semi-activa entre revisiones
        }
        System.out.println("Manejador de cuarentena terminó");
    }
}