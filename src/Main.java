import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        // Configuración por defecto
        String rutaImagen = "IMGPR.png";
        Operacion operacion = Operacion.EROSION;
        int caso = 1;
        int numHilos = Runtime.getRuntime().availableProcessors();
        String modo = "ambos";
        // Verificar que la imagen existe
        File archivoImagen = new File(rutaImagen);
        if (!archivoImagen.exists()) {
            System.err.println("ERROR: No se encontró la imagen " + rutaImagen);
            System.err.println("\nDebe ejecutar primero GeneradorImagenesAleatorias para crear la imagen.");
            System.err.println("\nEjemplos:");
            System.err.println("  java GeneradorImagenesAleatorias");
            System.err.println("  java GeneradorImagenesAleatorias 5000 5000 mixto");
            return; // ← SE DETIENE AQUÍ
        }
        // Permitir configuración por argumentos
        if (args.length >= 1) operacion = Operacion.valueOf(args[0].toUpperCase());
        if (args.length >= 2) caso = Integer.parseInt(args[1]);
        if (args.length >= 3) numHilos = Integer.parseInt(args[2]);
        if (args.length >= 4) modo = args[3].toLowerCase();

        // Validar modo
        if (!modo.equals("secuencial") && !modo.equals("paralelo") && !modo.equals("ambos")) {
            System.err.println("Modo no válido. Use: secuencial, paralelo, o ambos");
            mostrarAyuda();
            return;
        }

        System.out.println("Configuración:");
        System.out.println("  Imagen: " + rutaImagen);
        System.out.println("  Operación: " + operacion);
        System.out.println("  Caso elemento: " + caso);
        System.out.println("  Modo: " + modo);
        if (modo.equals("paralelo") || modo.equals("ambos")) {
            System.out.println("  Hilos: " + numHilos);
        }
        System.out.println("\n" + "=".repeat(50) + "\n");

        try {
            ElementoEstructurante elemento = new ElementoEstructurante(caso);
            elemento.imprimir();
            System.out.println();
            long tiempoSecuencial = 0;
            long tiempoParalelo = 0;

            if (modo.equals("secuencial") || modo.equals("ambos")) {
                ProcesarSecuencial procSecuencial = new ProcesarSecuencial(rutaImagen);
                tiempoSecuencial = procSecuencial.procesar(operacion, elemento);
                procSecuencial.guardarImagen("resultado_secuencial_" + operacion.toString().toLowerCase() + "_caso" + caso + ".png");
            }

            if (modo.equals("paralelo") || modo.equals("ambos")) {
                ProcesarParalelo procParalelo = new ProcesarParalelo(rutaImagen, numHilos);
                tiempoParalelo = procParalelo.procesarParalelo(operacion, elemento);
                procParalelo.guardarImagen("resultado_paralelo_" + operacion.toString().toLowerCase() + "_caso" + caso + ".png");
            }

            if (modo.equals("ambos")) {
                System.out.println("\n" + "=".repeat(50));
                System.out.println("Resultados Comparativos:");
                System.out.println("Tiempo Secuencial: " + tiempoSecuencial + " ms");
                System.out.println("Tiempo Paralelo:   " + tiempoParalelo + " ms");
                double speedup = (double) tiempoSecuencial / tiempoParalelo;
                System.out.println("Speedup:           " + String.format("%.2f", speedup) + "x");
                System.out.println("Eficiencia:        " + String.format("%.2f", (speedup / numHilos) * 100) + "%");
            } else if (modo.equals("secuencial")) {
                System.out.println("\n" + "=".repeat(50));
                System.out.println("Tiempo de ejecución: " + tiempoSecuencial + " ms");
            } else if (modo.equals("paralelo")) {
                System.out.println("\n" + "=".repeat(50));
                System.out.println("Tiempo de ejecución: " + tiempoParalelo + " ms");
            }

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void mostrarAyuda() {
        System.out.println("\nUso: java Main <operacion> <caso> <numHilos> <modo>");
        System.out.println("\nParámetros:");
        System.out.println("  <operacion>  : EROSION o DILATACION (por defecto: EROSION)");
        System.out.println("  <caso>       : 1-6 (por defecto: 1)");
        System.out.println("  <numHilos>   : Número de hilos (por defecto: procesadores disponibles)");
        System.out.println("  <modo>       : secuencial, paralelo, o ambos (por defecto: ambos)");
        System.out.println("\nEjemplos:");
        System.out.println("  java Main EROSION 1 4 secuencial");
        System.out.println("  java Main DILATACION 3 8 paralelo");
        System.out.println("  java Main EROSION 2 4 ambos");
        System.out.println("\nCasos de Elementos Estructurantes:");
        ElementoEstructurante.mostrarCasosDisponibles();
    }
}