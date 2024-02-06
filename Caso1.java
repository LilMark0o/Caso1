import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

class Data {
    private int generaciones;
    private String nombreArchivo;

    public Data(int generaciones, String nombreArchivo) {
        this.generaciones = generaciones;
        this.nombreArchivo = nombreArchivo;
    }

    public int getGeneraciones() {
        return generaciones;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }
}

public class Caso1 {
    private static Celda[][] matrix;

    public static void main(String[] args) {
        Data data = pedirData();
        matrix = crearMatriz(data.getNombreArchivo());
        int generaciones = data.getGeneraciones();
        simular(generaciones);
    }

    public static void simular(int generaciones) {
        System.out.println("La generación 0 es:");
        imprimirMatriz(matrix);
        for (int i = 0; i < generaciones; i++) {
            CountDownLatch countDownLatch = new CountDownLatch(matrix.length * matrix[0].length);
            for (int j = 0; j < matrix.length; j++) {
                for (int k = 0; k < matrix.length; k++) {
                    matrix[j][k].setCountDownLatch(countDownLatch);
                    matrix[j][k].start();
                }
            }
            try {
                countDownLatch.await();
                System.out.println("La generación " + (i + 1) + " es:");
                imprimirMatriz(matrix);
                matrix = reDoMatrix(matrix);
            } catch (InterruptedException e) {
            }

        }
    }

    public static Celda[][] reDoMatrix(Celda[][] matrix) {
        Celda[][] newMatrix = new Celda[matrix.length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                newMatrix[i][j] = new Celda(i, j, matrix[i][j].getStateCelda());
            }
        }
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                newMatrix[i][j].seekRealVecinos(newMatrix);
            }
        }
        return newMatrix;
    }

    public static void imprimirMatriz(Celda[][] matriz) {
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                if (matriz[i][j].getStateCelda()) {
                    System.out.print("| X ");
                } else {
                    System.out.print("| 0 ");
                }
            }
            System.out.println("|");
        }
    }

    public static Data pedirData() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el nombre del archivo: ");
        String nombreArchivo = scanner.nextLine();
        System.out.print("Ingrese el número de generaciones a simular: ");
        int generaciones = scanner.nextInt();
        scanner.close();
        return new Data(generaciones, nombreArchivo);

    }

    private static Celda[][] crearMatriz(String nombreArchivo) {
        try {
            // Lee el archivo
            BufferedReader br = new BufferedReader(new FileReader(nombreArchivo));
            String n_str = br.readLine();
            int n = Integer.parseInt(n_str);

            // Crea la matriz booleana con el tamaño especificado
            Celda[][] matriz = new Celda[n][n];
            // Lee las líneas y llena la matriz
            for (int i = 0; i < n; i++) {
                String[] valores = br.readLine().split(",");
                for (int j = 0; j < n; j++) {
                    boolean valor = Boolean.parseBoolean(valores[j]);
                    matriz[i][j] = new Celda(i, j, valor);
                }
            }
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    matriz[i][j].seekRealVecinos(matriz);
                }
            }
            // Cierra el BufferedReader
            br.close();
            return matriz;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return matrix;
    }
}
