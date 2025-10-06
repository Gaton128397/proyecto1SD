import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.CountDownLatch; //para sincronización de hilos
import java.util.concurrent.ExecutorService;//para manejar el pool de hilos
import java.util.concurrent.Executors;//para crear el pool de hilos y iniciar tareas

public class ProcesarParalelo extends ProcesarSecuencial {

    public int numHilos;

    public ProcesarParalelo(String rutaImagen, int numHilos) throws IOException {
        super(rutaImagen);
        this.numHilos = numHilos;
    }
    //procesa la imagen en paralelo dividiendola en franjas horizontales
    /*
        Ejemplo del programa:
        ------------
        |          |
        |  Hilo 0  |  <- procesa filas 0 a N/numHilos
        |          |
        ------------
        |          |
        |  Hilo 1  |  <- procesa filas N/numHilos a
        |          |
        ------------
        etc...
    */
    public long procesarParalelo(Operacion operacion, ElementoEstructurante elemento) {
        System.out.println("\n=== Procesamiento Paralelo ===");
        System.out.println("Operación: " + operacion);
        System.out.println("Elemento: Caso " + elemento.getCaso() + " - " + elemento.getNombreCaso());
        System.out.println("Número de hilos: " + numHilos);

        long tiempoInicio = System.currentTimeMillis();

        BufferedImage imagenResultado = new BufferedImage(
            getAncho(), getAlto(), BufferedImage.TYPE_INT_RGB
        );

        // Crear pool de hilos
        ExecutorService executor = Executors.newFixedThreadPool(numHilos);
        CountDownLatch latch = new CountDownLatch(numHilos);

        // Dividir el trabajo en franjas horizontales
        int filasPorHilo = getAlto() / numHilos;

        for (int i = 0; i < numHilos; i++) {
            final int hiloNum = i;
            final int filaInicio = i * filasPorHilo;
            final int filaFin = (i == numHilos - 1) ? getAlto() : (i + 1) * filasPorHilo;

            executor.submit(() -> {
                try {
                    System.out.println("Hilo " + hiloNum + " procesando filas " +
                            filaInicio + " a " + (filaFin - 1));

                    // Procesar la franja asignada
                    for (int y = filaInicio; y < filaFin; y++) {
                        for (int x = 0; x < getAncho(); x++) {
                            int nuevoPixel;

                            if (operacion == Operacion.EROSION) {
                                nuevoPixel = aplicarErosionParalelo(x, y, elemento);
                            } else {
                                nuevoPixel = aplicarDilatacionParalelo(x, y, elemento);
                            }

                            imagenResultado.setRGB(x, y, nuevoPixel);
                        }
                    }

                    System.out.println("Hilo " + hiloNum + " completado");

                } finally {
                    latch.countDown(); // Indicar que este hilo ha terminado
                }
            });
        }

        try {
            // Esperar a que todos los hilos terminen
            latch.await(); // Espera activa para que todos los hilos terminen
        } catch (InterruptedException e) {
            System.err.println("Error esperando hilos: " + e.getMessage());
        } finally {
            executor.shutdown();
        }

        long tiempoFin = System.currentTimeMillis();
        long tiempoTotal = tiempoFin - tiempoInicio;

        System.out.println("Progreso: 100%");
        System.out.println("Tiempo de procesamiento paralelo: " + tiempoTotal + " ms");

        // Guardar el resultado en el objeto
        setImagenResultado(imagenResultado);

        return tiempoTotal;
    }
    //se usa el minimo de los pixeles cubiertos por el elemento estructurante
    public int aplicarErosionParalelo(int x, int y, ElementoEstructurante elemento) {
        int minR = 255, minG = 255, minB = 255;

        int alturaElem = elemento.getAlto();
        int anchoElem = elemento.getAncho();
        int centroX = elemento.getCentroX();
        int centroY = elemento.getCentroY();

        for (int ey = 0; ey < alturaElem; ey++) {
            for (int ex = 0; ex < anchoElem; ex++) {
                if (elemento.estaActivo(ey, ex)) {
                    int imgX = x + (ex - centroX);
                    int imgY = y + (ey - centroY);
                    int rgb = obtenerPixelSeguro(imgX, imgY);

                    minR = Math.min(minR, obtenerRojo(rgb));
                    minG = Math.min(minG, obtenerVerde(rgb));
                    minB = Math.min(minB, obtenerAzul(rgb));
                }
            }
        }

        return combinarRGB(minR, minG, minB);
    }

    //se usa el maximo de los pixeles cubiertos por el elemento estructurante
    public int aplicarDilatacionParalelo(int x, int y, ElementoEstructurante elemento) {
        int maxR = 0, maxG = 0, maxB = 0;

        int alturaElem = elemento.getAlto();
        int anchoElem = elemento.getAncho();
        int centroX = elemento.getCentroX();
        int centroY = elemento.getCentroY();

        for (int ey = 0; ey < alturaElem; ey++) {
            for (int ex = 0; ex < anchoElem; ex++) {
                if (elemento.estaActivo(ey, ex)) {
                    int imgX = x + (ex - centroX);
                    int imgY = y + (ey - centroY);
                    int rgb = obtenerPixelSeguro(imgX, imgY);

                    maxR = Math.max(maxR, obtenerRojo(rgb));
                    maxG = Math.max(maxG, obtenerVerde(rgb));
                    maxB = Math.max(maxB, obtenerAzul(rgb));
                }
            }
        }

        return combinarRGB(maxR, maxG, maxB);
    }
    //metodo para establecer la imagen resultado usando reflexion
    public void setImagenResultado(BufferedImage imagen) {
        try {
            java.lang.reflect.Field field = ProcesarSecuencial.class.getDeclaredField("imagenResultado");
            field.setAccessible(true);
            field.set(this, imagen);
        } catch (Exception e) {
            System.err.println("Error al establecer imagen resultado: " + e.getMessage());
        }
    }
}
