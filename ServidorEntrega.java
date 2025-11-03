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
        Mensaje mensaje = buzonEntrega.retirar(); //puede ser null

        if (mensaje == null) {        
            try { Thread.sleep(10); } catch (InterruptedException e) {}
            continue;
        }

        if (mensaje.isInicio()) {      //marca que ya empezó
            System.out.println("Servidor " + id + " vio INICIO");
            continue;
        }

        if (mensaje.isFin()) {        
            System.out.println("Servidor " + id + " vio FIN");
            break;
        }

        
        try { 
            Thread.sleep(50 + new Random().nextInt(151)); 
        } catch (InterruptedException e) {}
    }
    System.out.println("Servidor " + id + " terminó");
}

}