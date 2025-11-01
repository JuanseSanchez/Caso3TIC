public class Mensaje {
    private int id;
    private boolean isSpam;
    private boolean isInicio;
    private boolean isFin;
    private int tiempoCuarentena;
    private int clienteId;

    public Mensaje(int id, boolean isSpam, int clienteId) {
        this.id = id;
        this.isSpam = isSpam;
        this.isInicio = false;
        this.isFin = false;
        this.tiempoCuarentena = 0;
        this.clienteId = clienteId;
    }

    public static Mensaje crearMensajeInicio(int clienteId) {
        Mensaje m = new Mensaje(-1, false, clienteId);
        m.isInicio = true;
        return m;
    }

    public static Mensaje crearMensajeFin(int clienteId) {
        Mensaje m = new Mensaje(-1, false, clienteId);
        m.isFin = true;
        return m;
    }

    public int getId() {
        return id;
    }

    public boolean isSpam() {
        return isSpam;
    }

    public boolean isInicio() {
        return isInicio;
    }

    public boolean isFin() {
        return isFin;
    }

    public int getTiempoCuarentena() {
        return tiempoCuarentena;
    }

    public void setTiempoCuarentena(int tiempo) {
        this.tiempoCuarentena = tiempo;
    }

    public void decrementarTiempoCuarentena() {
        if (tiempoCuarentena > 0) {
            tiempoCuarentena--;
        }
    }

    public int getClienteId() {
        return clienteId;
    }

    @Override
    public String toString() {
        if (isInicio) return "INICIO-Cliente" + clienteId;
        if (isFin) return "FIN-Cliente" + clienteId;
        return "Mensaje[id=" + id + ", cliente=" + clienteId + ", spam=" + isSpam + "]";
    }
}