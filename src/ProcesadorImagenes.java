import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * Procesador de Imágenes con implementación Secuencial y Paralela
 * Proyecto 1 - Sistemas Distribuidos
 * Universidad de Talca
 */
public class ProcesadorImagenes {

    // Clase para almacenar métricas de rendimiento
    static class MetricasRendimiento {
        long tiempoEjecucion;
        long memoriaUsada;
        long memoriaMaxima;
        int nucleosUsados;

        public void imprimir(String tipo) {
            System.out.println("\n=== Métricas de Rendimiento - " + tipo + " ===");
            System.out.println("Tiempo de ejecución: " + tiempoEjecucion + " ms (" +
                    (tiempoEjecucion / 1000.0) + " segundos)");
            System.out.println("Memoria usada: " + (memoriaUsada / (1024 * 1024)) + " MB");
            System.out.println("Memoria máxima: " + (memoriaMaxima / (1024 * 1024)) + " MB");
            System.out.println("Núcleos utilizados: " + nucleosUsados);
            System.out.println("=".repeat(50));
        }
    }

    /**
     * Procesa una imagen de forma SECUENCIAL
     */
    public static BufferedImage procesarSecuencial(BufferedImage imagenOriginal,
                                                   MetricasRendimiento metricas) {
        System.out.println("\n>>> Iniciando procesamiento SECUENCIAL <<<");

        // Obtener información de memoria antes de procesar
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapBefore = memoryBean.getHeapMemoryUsage();

        long tiempoInicio = System.currentTimeMillis();

        int ancho = imagenOriginal.getWidth();
        int alto = imagenOriginal.getHeight();

        // Crear imagen de salida
        BufferedImage imagenResultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

        // Recorrer la imagen píxel por píxel (de izquierda a derecha, arriba a abajo)
        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {

                // Obtener el píxel RGB
                int rgb = imagenOriginal.getRGB(x, y);

                // Extraer componentes R, G, B
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                // AQUÍ IRÁ LA LÓGICA DE EROSIÓN O DILATACIÓN
                // Por ahora solo copiamos el píxel (placeholder)
                int nuevoRGB = (r << 16) | (g << 8) | b;

                imagenResultado.setRGB(x, y, nuevoRGB);
            }

            // Mostrar progreso cada 10%
            if (y % (alto / 10) == 0 && y > 0) {
                int porcentaje = (y * 100) / alto;
                System.out.println("Progreso secuencial: " + porcentaje + "%");
            }
        }

        long tiempoFin = System.currentTimeMillis();

        // Obtener información de memoria después de procesar
        MemoryUsage heapAfter = memoryBean.getHeapMemoryUsage();

        // Guardar métricas
        metricas.tiempoEjecucion = tiempoFin - tiempoInicio;
        metricas.memoriaUsada = heapAfter.getUsed() - heapBefore.getUsed();
        metricas.memoriaMaxima = heapAfter.getMax();
        metricas.nucleosUsados = 1; // Secuencial usa 1 núcleo

        System.out.println("Progreso secuencial: 100%");

        return imagenResultado;
    }

    /**
     * Procesa una imagen de forma PARALELA usando múltiples hilos
     */
    public static BufferedImage procesarParalelo(BufferedImage imagenOriginal,
                                                 MetricasRendimiento metricas) {
        System.out.println("\n>>> Iniciando procesamiento PARALELO <<<");

        // Obtener información de memoria antes de procesar
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapBefore = memoryBean.getHeapMemoryUsage();

        long tiempoInicio = System.currentTimeMillis();

        int ancho = imagenOriginal.getWidth();
        int alto = imagenOriginal.getHeight();

        // Crear imagen de salida
        BufferedImage imagenResultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

        // Obtener número de núcleos disponibles
        int numNucleos = Runtime.getRuntime().availableProcessors();
        System.out.println("Núcleos disponibles: " + numNucleos);

        // Crear un pool de hilos
        ExecutorService executor = Executors.newFixedThreadPool(numNucleos);

        // Dividir la imagen en franjas horizontales (una por núcleo)
        int filasPorHilo = alto / numNucleos;
        CountDownLatch latch = new CountDownLatch(numNucleos);

        // Crear tareas para cada franja
        for (int i = 0; i < numNucleos; i++) {
            final int filaInicio = i * filasPorHilo;
            final int filaFin = (i == numNucleos - 1) ? alto : (i + 1) * filasPorHilo;
            final int hiloNum = i + 1;

            executor.submit(() -> {
                try {
                    System.out.println("Hilo " + hiloNum + " procesando filas " +
                            filaInicio + " a " + (filaFin - 1));

                    // Procesar la franja asignada a este hilo
                    for (int y = filaInicio; y < filaFin; y++) {
                        for (int x = 0; x < ancho; x++) {

                            // Obtener el píxel RGB
                            int rgb = imagenOriginal.getRGB(x, y);

                            // Extraer componentes R, G, B
                            int r = (rgb >> 16) & 0xFF;
                            int g = (rgb >> 8) & 0xFF;
                            int b = rgb & 0xFF;

                            // AQUÍ IRÁ LA LÓGICA DE EROSIÓN O DILATACIÓN
                            // Por ahora solo copiamos el píxel (placeholder)
                            int nuevoRGB = (r << 16) | (g << 8) | b;

                            imagenResultado.setRGB(x, y, nuevoRGB);
                        }
                    }

                    System.out.println("Hilo " + hiloNum + " completado");

                } finally {
                    latch.countDown();
                }
            });
        }

        // Esperar a que todos los hilos terminen
        try {
            latch.await();
        } catch (InterruptedException e) {
            System.err.println("Error esperando hilos: " + e.getMessage());
            Thread.currentThread().interrupt();
        }

        // Cerrar el executor
        executor.shutdown();

        long tiempoFin = System.currentTimeMillis();

        // Obtener información de memoria después de procesar
        MemoryUsage heapAfter = memoryBean.getHeapMemoryUsage();

        // Guardar métricas
        metricas.tiempoEjecucion = tiempoFin - tiempoInicio;
        metricas.memoriaUsada = heapAfter.getUsed() - heapBefore.getUsed();
        metricas.memoriaMaxima = heapAfter.getMax();
        metricas.nucleosUsados = numNucleos;

        return imagenResultado;
    }

    /**
     * Carga una imagen desde un archivo
     */
    public static BufferedImage cargarImagen(String rutaArchivo) {
        try {
            File archivo = new File(rutaArchivo);
            BufferedImage imagen = ImageIO.read(archivo);

            if (imagen == null) {
                throw new IOException("No se pudo leer la imagen");
            }

            System.out.println("Imagen cargada: " + rutaArchivo);
            System.out.println("Dimensiones: " + imagen.getWidth() + "x" + imagen.getHeight());

            return imagen;

        } catch (IOException e) {
            System.err.println("Error al cargar imagen: " + e.getMessage());
            return null;
        }
    }

    /**
     * Guarda una imagen en un archivo
     */
    public static void guardarImagen(BufferedImage imagen, String rutaArchivo) {
        try {
            File archivo = new File(rutaArchivo);
            ImageIO.write(imagen, "PNG", archivo);
            System.out.println("Imagen guardada: " + rutaArchivo);

        } catch (IOException e) {
            System.err.println("Error al guardar imagen: " + e.getMessage());
        }
    }

    /**
     * Compara dos imágenes para verificar que sean idénticas
     */
    public static boolean compararImagenes(BufferedImage img1, BufferedImage img2) {
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            return false;
        }

        int diferencias = 0;
        for (int y = 0; y < img1.getHeight(); y++) {
            for (int x = 0; x < img1.getWidth(); x++) {
                if (img1.getRGB(x, y) != img2.getRGB(x, y)) {
                    diferencias++;
                }
            }
        }

        if (diferencias > 0) {
            System.out.println("Las imágenes difieren en " + diferencias + " píxeles");
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
        System.out.println("=== Procesador de Imágenes Secuencial vs Paralelo ===");
        System.out.println("Proyecto 1 - Sistemas Distribuidos");
        System.out.println("Universidad de Talca\n");

        // Ruta de la imagen a procesar (puedes cambiarla según tu estructura)
        String rutaImagenEntrada = "imagen_prueba_1000x1000.png";

        // Si se pasa una ruta como argumento, usarla
        if (args.length > 0) {
            rutaImagenEntrada = args[0];
            System.out.println("Usando imagen especificada: " + rutaImagenEntrada);
        }

        // Verificar que el archivo exista
        File archivo = new File(rutaImagenEntrada);
        if (!archivo.exists()) {
            System.err.println("ERROR: No se encontró el archivo " + rutaImagenEntrada);
            System.out.println("\nOpciones:");
            System.out.println("1. Copia la imagen a la carpeta actual");
            System.out.println("2. Especifica la ruta completa: java ProcesadorImagenes <ruta>");
            System.out.println("3. Genera la imagen primero con GeneradorImagenesAleatorias.java");
            return;
        }

        // Cargar imagen
        BufferedImage imagenOriginal = cargarImagen(rutaImagenEntrada);
        if (imagenOriginal == null) {
            return;
        }

        // Procesar de forma SECUENCIAL
        MetricasRendimiento metricasSecuencial = new MetricasRendimiento();
        BufferedImage resultadoSecuencial = procesarSecuencial(imagenOriginal, metricasSecuencial);
        guardarImagen(resultadoSecuencial, "resultado_secuencial.png");
        metricasSecuencial.imprimir("SECUENCIAL");

        // Esperar un momento para que la memoria se estabilice
        System.out.println("\nEsperando para limpiar memoria...");
        System.gc();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Procesar de forma PARALELA
        MetricasRendimiento metricasParalela = new MetricasRendimiento();
        BufferedImage resultadoParalelo = procesarParalelo(imagenOriginal, metricasParalela);
        guardarImagen(resultadoParalelo, "resultado_paralelo.png");
        metricasParalela.imprimir("PARALELO");

        // Comparar resultados
        System.out.println("\n=== Comparación de Resultados ===");
        boolean sonIguales = compararImagenes(resultadoSecuencial, resultadoParalelo);
        if (sonIguales) {
            System.out.println("✓ Las imágenes procesadas son IDÉNTICAS");
        } else {
            System.out.println("✗ Las imágenes procesadas son DIFERENTES");
        }

        // Calcular speedup
        System.out.println("\n=== Análisis de Rendimiento ===");
        double speedup = (double) metricasSecuencial.tiempoEjecucion / metricasParalela.tiempoEjecucion;
        System.out.printf("Speedup: %.2fx\n", speedup);
        System.out.printf("Eficiencia: %.2f%%\n", (speedup / metricasParalela.nucleosUsados) * 100);

        System.out.println("\n¡Procesamiento completado!");
    }
}