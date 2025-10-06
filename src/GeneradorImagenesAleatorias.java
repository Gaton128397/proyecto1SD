import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class GeneradorImagenesAleatorias {

    // NOMBRE FIJO DE LA IMAGEN A GENERAR
    private static final String NOMBRE_IMAGEN = "IMGPR.png";
    //genera una imagen de ruido aleatorio
    public static void generarImagen(int ancho, int alto, String tipoNoise) {
        long tiempoInicio = System.currentTimeMillis();
        System.out.println("Generando imagen de " + ancho + "x" + alto + " píxeles...");
        System.out.println("Tipo de ruido: " + tipoNoise);
        System.out.println("Nombre del archivo: " + NOMBRE_IMAGEN);

        BufferedImage imagen = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);// Crear imagen en RGB
        Random random = new Random();

        //se recorre cada pixel de la imagen
        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int rgb = generarPixel(x, y, ancho, alto, tipoNoise, random);
                imagen.setRGB(x, y, rgb);
            }
            if (y % (alto / 10) == 0 && y > 0) {
                int porcentaje = (y * 100) / alto;
                System.out.println("Progreso: " + porcentaje + "%");
            }
        }

        try {
            File archivoSalida = new File(NOMBRE_IMAGEN);
            ImageIO.write(imagen, "PNG", archivoSalida);

            long tiempoFin = System.currentTimeMillis();
            long tiempoTotal = tiempoFin - tiempoInicio;

            System.out.println("Progreso: 100%");
            System.out.println("Imagen generada exitosamente: " + NOMBRE_IMAGEN);
            System.out.println("Tiempo de generación: " + tiempoTotal + " ms (" +
                    (tiempoTotal / 1000.0) + " segundos)");
            System.out.println("Tamaño del archivo: " + (archivoSalida.length() / 1024) + " KB");

        } catch (IOException e) {
            System.err.println("Error al guardar la imagen: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // genera el color de cada pixel segun el tipo de ruido
    //hay 5 tipos de ruido: random, gradiente, patrones, mixto y grises
    private static int generarPixel(int x, int y, int ancho, int alto, String tipo, Random random) {
        int r, g, b;

        switch (tipo.toLowerCase()) {
            case "random":
                r = random.nextInt(256);
                g = random.nextInt(256);
                b = random.nextInt(256);
                break;

            case "gradiente":
                r = (x * 255) / ancho;
                g = (y * 255) / alto;
                b = ((x + y) * 255) / (ancho + alto);
                break;

            case "patrones":
                r = ((x / 10) % 2 == 0) ? 200 + random.nextInt(56) : random.nextInt(100);
                g = ((y / 10) % 2 == 0) ? 200 + random.nextInt(56) : random.nextInt(100);
                b = (((x + y) / 10) % 2 == 0) ? 200 + random.nextInt(56) : random.nextInt(100);
                break;

            case "mixto":
                int baseR = (x * 255) / ancho;
                int baseG = (y * 255) / alto;
                int baseB = ((x + y) * 255) / (ancho + alto);

                r = Math.min(255, Math.max(0, baseR + random.nextInt(100) - 50));
                g = Math.min(255, Math.max(0, baseG + random.nextInt(100) - 50));
                b = Math.min(255, Math.max(0, baseB + random.nextInt(100) - 50));
                break;

            case "grises":
                int gris = random.nextInt(256);
                r = g = b = gris;
                break;

            default:
                r = random.nextInt(256);
                g = random.nextInt(256);
                b = random.nextInt(256);
        }

        return (r << 16) | (g << 8) | b;
    }

    public static void main(String[] args) {
        System.out.println("=== Generador de Imágenes Aleatorias ===");
        System.out.println("Proyecto 1 - Sistemas Distribuidos");
        System.out.println("Universidad de Talca\n");

        // Configuración por defecto
        int ancho = 1000;
        int alto = 1000;
        String tipoNoise = "mixto";

        // Permitir configuración por argumentos
        if (args.length >= 2) {
            try {
                ancho = Integer.parseInt(args[0]);
                alto = Integer.parseInt(args[1]);
                if (args.length >= 3) {
                    tipoNoise = args[2];
                }
            } catch (NumberFormatException e) {
                System.err.println("Error en los argumentos. Uso: java GeneradorImagenesAleatorias <ancho> <alto> [tipo]");
                System.err.println("Usando valores por defecto: 1000x1000, tipo mixto");
            }
        }

        System.out.println("Configuración:");
        System.out.println("  Dimensiones: " + ancho + "x" + alto);
        System.out.println("  Tipo de ruido: " + tipoNoise);
        System.out.println("\n" + "=".repeat(50) + "\n");

        generarImagen(ancho, alto, tipoNoise);

        System.out.println("\n" + "=".repeat(50));
        System.out.println("\nTipos de ruido disponibles:");
        System.out.println("  - random: Ruido completamente aleatorio");
        System.out.println("  - gradiente: Gradiente de colores suave");
        System.out.println("  - patrones: Patrones con ruido");
        System.out.println("  - mixto: Gradiente con ruido aleatorio");
        System.out.println("  - grises: Escala de grises");

        System.out.println("\nEjemplos de uso:");
        System.out.println("  java GeneradorImagenesAleatorias");
        System.out.println("  java GeneradorImagenesAleatorias 5000 5000");
        System.out.println("  java GeneradorImagenesAleatorias 5000 5000 random");
    }
}