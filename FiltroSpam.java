public class FiltroSpam extends Thread {
    private final int id;
    private final BuzonEntrada buzonEntrada;
    private final BuzonEntrega buzonEntrega;
    private final BuzonCuarentena buzonCuarentena;
    private static volatile int clientesRegistrados = 0;
    private static volatile int mensajesFinRecibidos = 0;
    private static volatile boolean finEnviado = false;
    private static final Object lock = new Object();

    public FiltroSpam(int id, BuzonEntrada buzonEntrada, BuzonEntrega buzonEntrega, BuzonCuarentena buzonCuarentena) {
        this.id = id;
        this.buzonEntrada = buzonEntrada;
        this.buzonEntrega = buzonEntrega;
        this.buzonCuarentena = buzonCuarentena;
    }

    private boolean debeTerminar() {
        return mensajesFinRecibidos == clientesRegistrados &&
               buzonEntrada.estaVacio() &&
               buzonCuarentena.estaVacio();
    }

    private static synchronized void registrarCliente() {
        clientesRegistrados++;
    }

    private static synchronized boolean registrarMensajeFin() {
        mensajesFinRecibidos++;
        return mensajesFinRecibidos == clientesRegistrados && !finEnviado;
    }

    private static synchronized void marcarFinEnviado() {
        if (!finEnviado) {
            finEnviado = true;
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                Mensaje mensaje = buzonEntrada.retirar();
                System.out.println("Filtro recibe mensaje: " + mensaje);
                if (mensaje.isInicio()) {
                    System.out.println("Filtro detecta INICIO de cliente " + mensaje.getClienteId());
                    registrarCliente();
                    buzonEntrega.depositar(mensaje);
                    continue;
                }

                if (mensaje.isFin()) {
                    System.out.println("Filtro detecta FIN de cliente " + mensaje.getClienteId());
                    if (registrarMensajeFin() && buzonEntrada.estaVacio() && buzonCuarentena.estaVacio()) {
                        System.out.println("[Filtro " + id + "]  Enviando mensaje FIN al sistema");
                        marcarFinEnviado();
                        buzonEntrega.depositar(Mensaje.crearMensajeFin(-1));
                        buzonCuarentena.depositar(Mensaje.crearMensajeFin(-1));
                        System.out.println("[Filtro " + id + "]  Ha terminado su ejecución");
                        break;
                    }
                    System.out.println("Filtro " + id + " - Mensajes FIN recibidos: " + mensajesFinRecibidos + "/" + clientesRegistrados);
                    continue;
                }

                if (mensaje.isSpam()) {
                    System.out.println("Filtro envía mensaje a Cuarentena: " + mensaje);
                    buzonCuarentena.depositar(mensaje);
                } else {
                    System.out.println("Filtro envía mensaje a Entrega: " + mensaje);
                    buzonEntrega.depositar(mensaje);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}