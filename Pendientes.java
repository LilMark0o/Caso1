class Pendientes {
    private int pendientes;

    public Pendientes() {
        this.pendientes = 0;
    }

    public int getPendientes() {
        return pendientes;
    }

    public void incrementPendientes() {
        this.pendientes++;
    }

    public void decrementPendientes() {
        this.pendientes--;
    }

    public void resetPendientes() {
        this.pendientes = 0;
    }
}
