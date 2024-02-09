import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

class Celda extends Thread {
    private int fila;
    private int capacidadBuzon;
    private Pendientes pendientes;
    private int columna;
    private BlockingQueue<Boolean> buzon;
    private Vecino[] vecinos;
    private boolean state;
    private int vecinosVivos;
    private int mensajesRecibidos;
    private int mensajesEnviados;
    private CountDownLatch countDownLatch;

    public Celda(int fila, int columna, boolean valor) {
        this.fila = fila;
        this.capacidadBuzon = fila + 1;
        this.pendientes = new Pendientes();
        this.columna = columna;
        this.state = valor;
        this.buzon = new ArrayBlockingQueue<>(capacidadBuzon);
        this.vecinosVivos = 0;
        this.mensajesRecibidos = 0;
        this.mensajesEnviados = 0;
    }

    public void getAllData() {
        System.out.println("Fila: " + fila);
        System.out.println("Columna: " + columna);
        System.out.println("Capacidad del buzon: " + capacidadBuzon);
        System.out.println("Pendientes: " + pendientes);
        System.out.println("Estado: " + state);
        System.out.println("Vecinos: " + vecinos.length);
        System.out.println("--------------------");
    }

    public Vecino[] getVecinos() {
        return vecinos;
    }

    public void notificar(String mensaje) {
        System.out.println("La celda en la fila " + fila + " y columna " + columna + " te quiere decir: " + mensaje);
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        // notificar("Estoy corriendo");
        mandarMensajesAVecinos();
        revisarMensajes();
        // notificar("Ya revise y mandé la primera tanda");
        // notificar("tengo " + vecinos.length + " vecinos");
        while (vecinos.length != mensajesRecibidos || vecinos.length != mensajesEnviados) {
            mandarMensajesAVecinos();
            revisarMensajes();
        }
        changeState(nextState());
        restartData();
    }

    public void restartData() {
        vecinosVivos = 0;
        mensajesRecibidos = 0;
        mensajesEnviados = 0;
        pendientes.resetPendientes();
        buzon.clear();
        for (Vecino vecino : vecinos) {
            vecino.reset();
        }
    }

    public void revisarMensajes() {
        while (!buzon.isEmpty()) {
            try {
                boolean mensaje = buzon.take();
                if (mensaje) {
                    vecinosVivos++;
                }
                mensajesRecibidos++;
                pendientes.decrementPendientes();
                ;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void mandarMensajesAVecinos() {
        for (Vecino vecino : vecinos) {
            if (!vecino.getRecibioNotificacion()) {
                boolean recibido = vecino.getCelda().recibirMensaje(state);
                if (recibido) {
                    vecino.setRecibioNotificacion(recibido);
                    mensajesEnviados++;
                }
            }
        }
    }

    // TODO preguntar porqué no sirve
    public boolean recibirMensaje(boolean mensaje) {
        try {
            synchronized (pendientes) {
                if (pendientes.getPendientes() < capacidadBuzon) {
                    pendientes.incrementPendientes();
                    buzon.add(mensaje);
                    return true;
                } else {
                    return false;
                }
            }
        } catch (IllegalStateException e) {
            return false;
        }
    }

    public void seekRealVecinos(Celda[][] matrix) {
        int contador = 0;
        for (int i = fila - 1; i <= fila + 1; i++) {
            for (int j = columna - 1; j <= columna + 1; j++) {
                if (i >= 0 && i < matrix.length && j >= 0 && j < matrix.length) {
                    if (i != fila || j != columna) {
                        contador++;
                    }
                }
            }
        }
        vecinos = new Vecino[contador];
        contador = 0;
        for (int i = fila - 1; i <= fila + 1; i++) {
            for (int j = columna - 1; j <= columna + 1; j++) {
                if (i >= 0 && i < matrix.length && j >= 0 && j < matrix.length) {
                    if (i != fila || j != columna) {
                        vecinos[contador] = new Vecino(i, j, matrix[i][j]);
                        contador++;
                    }
                }
            }
        }
    }

    public boolean nextState() {
        if (state) {
            if (vecinosVivos > 3 || vecinosVivos == 0) {
                return false;
            } else {
                return true;
            }
        } else {
            if (vecinosVivos == 3) {
                return true;
            } else {
                return false;
            }
        }
    }

    public void changeState(boolean valor) {
        this.state = valor;
        countDownLatch.countDown();
    }

    public boolean getStateCelda() {
        return state;
    }
}
