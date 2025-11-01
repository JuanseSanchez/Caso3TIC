import java.util.Random;

public class ClienteEmisor extends Thread {
    private final int id;
    private final int numMensajes;
    private final BuzonEntrada buzonEntrada;
    private final Random random;
    private int secuencial;

    public ClienteEmisor(int id, int numMensajes, BuzonEntrada buzonEntrada) {
        this.id = id;
        this.numMensajes = numMensajes;
        this.buzonEntrada = buzonEntrada;
        this.random = new Random();
        this.secuencial = 0;
    }

    @Override
    public void run() {
        try {
            // Enviar mensaje de inicio
            Mensaje inicio = Mensaje.crearMensajeInicio(id);
            System.out.println("Cliente " + id + " crea mensaje: " + inicio);
            buzonEntrada.depositar(inicio);

            // Generar mensajes
            for (int i = 0; i < numMensajes; i++) {
                boolean isSpam = random.nextBoolean();
                Mensaje mensaje = new Mensaje(++secuencial, isSpam, id);
                System.out.println("Cliente " + id + " crea mensaje: " + mensaje);
                System.out.println("Cliente " + id + " deposita mensaje en BuzonEntrada");
                buzonEntrada.depositar(mensaje);
                Thread.sleep(random.nextInt(100)); // Pequeña pausa aleatoria
            }

            // Enviar mensaje de fin
            Mensaje fin = Mensaje.crearMensajeFin(id);
            System.out.println("Cliente " + id + " deposita mensaje FIN en BuzonEntrada");  
            buzonEntrada.depositar(fin);
            System.out.println("Cliente " + id + " ha terminado su ejecución");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}