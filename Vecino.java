public class Vecino {
    private boolean recibioNotificacion;
    private Celda celda;

    public Vecino(int fila, int columna, Celda celda) {
        this.recibioNotificacion = false;
        this.celda = celda;
    }

    public boolean getRecibioNotificacion() {
        return recibioNotificacion;
    }

    public void setRecibioNotificacion(boolean recibioNotificacion) {
        this.recibioNotificacion = recibioNotificacion;
    }

    public Celda getCelda() {
        return celda;
    }

    public void setCelda(Celda celda) {
        this.celda = celda;
    }

    public void reset() {
        this.recibioNotificacion = false;
    }
}
