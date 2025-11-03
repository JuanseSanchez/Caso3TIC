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
            try {
                Thread.sleep(1000); 
            } catch (InterruptedException e) {}

            Mensaje[] mensajes = buzonCuarentena.obtenerMensajes();
                for (Mensaje mensaje : mensajes) {
                    System.out.println("ManejadorCuarentena revisa mensaje: " + mensaje);
                    if (mensaje.isFin()) {
                        buzonCuarentena.removerMensaje(mensaje);
                        ejecutando = false;
                        break;
                    }

                    mensaje.decrementarTiempoCuarentena();
                    
                    if (mensaje.getTiempoCuarentena() <= 0) {
                        int numeroAleatorio = random.nextInt(21) + 1;
                        
                        if (numeroAleatorio % 7 != 0) {
                            buzonEntrega.depositar(mensaje);
                        } else {
                            System.out.println("ManejadorCuarentena descarta mensaje: " + mensaje);
                        }
                        buzonCuarentena.removerMensaje(mensaje);
                    }
                }
            }
            System.out.println("Manejador de cuarentena terminó");
        }
        
    }
