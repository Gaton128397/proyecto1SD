import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * Generador de imágenes PNG aleatorias en formato RGB
 * Para el Proyecto 1 - Sistemas Distribuidos
 * Universidad de Talca
 */
public class GeneradorImagenesAleatorias {

    /**
     * Genera una imagen RGB aleatoria con las dimensiones especificadas
     *
     * @param ancho Ancho de la imagen en píxeles
     * @param alto Alto de la imagen en píxeles
     * @param nombreArchivo Nombre del archivo PNG de salida
     * @param tipoNoise Tipo de ruido: "random", "gradiente", "patrones", "mixto"
     */
    public static void generarImagen(int ancho, int alto, String nombreArchivo, String tipoNoise) {
        long tiempoInicio = System.currentTimeMillis();

        System.out.println("Generando imagen de " + ancho + "x" + alto + " píxeles...");
        System.out.println("Tipo de ruido: " + tipoNoise);

        // Crear imagen en formato RGB
        BufferedImage imagen = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        Random random = new Random();

        // Generar píxeles según el tipo de ruido
        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int rgb = generarPixel(x, y, ancho, alto, tipoNoise, random);
                imagen.setRGB(x, y, rgb);
            }

            // Mostrar progreso cada 10%
            if (y % (alto / 10) == 0 && y > 0) {
                int porcentaje = (y * 100) / alto;
                System.out.println("Progreso: " + porcentaje + "%");
            }
        }

        // Guardar imagen en formato PNG
        try {
            File archivoSalida = new File(nombreArchivo);
            ImageIO.write(imagen, "PNG", archivoSalida);

            long tiempoFin = System.currentTimeMillis();
            long tiempoTotal = tiempoFin - tiempoInicio;

            System.out.println("Imagen generada exitosamente: " + nombreArchivo);
            System.out.println("Tiempo de generación: " + tiempoTotal + " ms (" +
                    (tiempoTotal / 1000.0) + " segundos)");
            System.out.println("Tamaño del archivo: " + (archivoSalida.length() / 1024) + " KB");

        } catch (IOException e) {
            System.err.println("Error al guardar la imagen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Genera un píxel RGB según el tipo de ruido especificado
     */
    private static int generarPixel(int x, int y, int ancho, int alto, String tipo, Random random) {
        int r, g, b;

        switch (tipo.toLowerCase()) {
            case "random":
                // Ruido completamente aleatorio
                r = random.nextInt(256);
                g = random.nextInt(256);
                b = random.nextInt(256);
                break;

            case "gradiente":
                // Gradiente de colores
                r = (x * 255) / ancho;
                g = (y * 255) / alto;
                b = ((x + y) * 255) / (ancho + alto);
                break;

            case "patrones":
                // Patrones con ruido
                r = ((x / 10) % 2 == 0) ? 200 + random.nextInt(56) : random.nextInt(100);
                g = ((y / 10) % 2 == 0) ? 200 + random.nextInt(56) : random.nextInt(100);
                b = (((x + y) / 10) % 2 == 0) ? 200 + random.nextInt(56) : random.nextInt(100);
                break;

            case "mixto":
                // Mezcla de gradiente con ruido aleatorio
                int baseR = (x * 255) / ancho;
                int baseG = (y * 255) / alto;
                int baseB = ((x + y) * 255) / (ancho + alto);

                r = Math.min(255, Math.max(0, baseR + random.nextInt(100) - 50));
                g = Math.min(255, Math.max(0, baseG + random.nextInt(100) - 50));
                b = Math.min(255, Math.max(0, baseB + random.nextInt(100) - 50));
                break;

            case "grises":
                // Escala de grises con ruido
                int gris = random.nextInt(256);
                r = g = b = gris;
                break;

            default:
                // Por defecto: random
                r = random.nextInt(256);
                g = random.nextInt(256);
                b = random.nextInt(256);
        }

        // Combinar los componentes RGB en un solo entero
        return (r << 16) | (g << 8) | b;
    }

    public static void main(String[] args) {
        System.out.println("=== Generador de Imágenes Aleatorias ===");
        System.out.println("Proyecto 1 - Sistemas Distribuidos");
        System.out.println("Universidad de Talca\n");

        // Ejemplo 1: Imagen pequeña para pruebas rápidas (1000x1000)
        generarImagen(1000, 1000, "imagen_prueba_1000x1000.png", "mixto");
        System.out.println("\n" + "=".repeat(50) + "\n");

        // Ejemplo 2: Imagen mediana (5000x5000)
        generarImagen(5000, 5000, "imagen_mediana_5000x5000.png", "random");
        System.out.println("\n" + "=".repeat(50) + "\n");

        // Ejemplo 3: Imagen grande (10000x10000)
        // ADVERTENCIA: Esto puede consumir mucha memoria y tiempo
        // Descomentar solo si tu sistema tiene suficiente RAM
        // generarImagen(10000, 10000, "imagen_grande_10000x10000.png", "patrones");

        // Ejemplo 4: Imagen muy grande (15000x15000)
        // Requiere configurar JVM con más memoria: -Xmx8g
        // generarImagen(15000, 15000, "imagen_xlarge_15000x15000.png", "gradiente");

        System.out.println("\nGeneración completada!");
        System.out.println("\nTipos de ruido disponibles:");
        System.out.println("  - random: Ruido completamente aleatorio");
        System.out.println("  - gradiente: Gradiente de colores suave");
        System.out.println("  - patrones: Patrones con ruido");
        System.out.println("  - mixto: Gradiente con ruido aleatorio");
        System.out.println("  - grises: Escala de grises");
    }
}