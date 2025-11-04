import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        // Configuración del sistema
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream("config.properties")) {
            props.load(in);
        } catch (IOException e) {
            System.out.println("No se pudo cargar config.properties, usando valores por defecto");
        }

        // Leer valores del archivo
        int numClientes = Integer.parseInt(props.getProperty("numClientes", "2"));
        int numMensajesPorCliente = Integer.parseInt(props.getProperty("numMensajesPorCliente", "3"));
        int numFiltros = Integer.parseInt(props.getProperty("numFiltros", "1"));
        int numServidores = Integer.parseInt(props.getProperty("numServidores", "2"));
        int capacidadBuzonEntrada = Integer.parseInt(props.getProperty("capacidadBuzonEntrada", "10"));
        int capacidadBuzonEntrega = Integer.parseInt(props.getProperty("capacidadBuzonEntrega", "20"));

            // Crear buzones
            BuzonEntrada buzonEntrada = new BuzonEntrada(capacidadBuzonEntrada);
            BuzonEntrega buzonEntrega = new BuzonEntrega(capacidadBuzonEntrega);
            buzonEntrega.setNumServidores(numServidores);
            BuzonCuarentena buzonCuarentena = new BuzonCuarentena();

            // Crear y comenzar manejador de cuarentena
            ManejadorCuarentena manejadorCuarentena = new ManejadorCuarentena(buzonCuarentena, buzonEntrega);
            manejadorCuarentena.start();

            // Crear y comenzar servidores
            ArrayList<ServidorEntrega> servidores = new ArrayList<>();
            for (int i = 0; i < numServidores; i++) {
                ServidorEntrega servidor = new ServidorEntrega(i, buzonEntrega);
                servidores.add(servidor);
                servidor.start();
            }

            // Crear y comenzar filtros
            ArrayList<FiltroSpam> filtros = new ArrayList<>();
            for (int i = 0; i < numFiltros; i++) {
                FiltroSpam filtro = new FiltroSpam(i, buzonEntrada, buzonEntrega, buzonCuarentena);
                filtros.add(filtro);
                filtro.start();
            }

            // Crear y comenzar clientes
            ArrayList<ClienteEmisor> clientes = new ArrayList<>();
            for (int i = 0; i < numClientes; i++) {
                ClienteEmisor cliente = new ClienteEmisor(i, numMensajesPorCliente, buzonEntrada);
                clientes.add(cliente);
                cliente.start();
            }

            try {
                // Esperar a que terminen todos los clientes primero
                for (ClienteEmisor cliente : clientes) {
                    cliente.join();
                }
                System.out.println("Todos los clientes terminaron");

                // Esperar a que terminen los filtros
                for (FiltroSpam filtro : filtros) {
                    filtro.join();
                }
                System.out.println("Todos los filtros terminaron");

                // Esperar al manejador de cuarentena
                manejadorCuarentena.join();
                System.out.println("Manejador de cuarentena terminó");

                // Finalmente esperar a los servidores
                for (ServidorEntrega servidor : servidores) {
                    servidor.join();
                }
                System.out.println("Todos los servidores terminaron");

                // Verificar que los buzones estén vacíos
                if (buzonEntrada.estaVacio() && 
                    buzonEntrega.estaVacio() && 
                    buzonCuarentena.estaVacio()) {
                    System.out.println("Sistema terminado correctamente: todos los buzones vacíos");
                } else {
                    System.out.println("ERROR: Algunos buzones aún tienen mensajes");
                }
            } catch (InterruptedException e) {
                System.out.println("Error en la ejecución de los hilos: " + e.getMessage());
            }
    }
}