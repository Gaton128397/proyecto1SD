import java.io.IOException;

public class ProcesadorImagenes {
    
    public static void main(String[] args) {
        System.out.println("=== Procesador de Imágenes Morfológicas ===");
        System.out.println("Proyecto 1 - Sistemas Distribuidos");
        System.out.println("Universidad de Talca\n");

        // Configuración
        String rutaImagen = "IMGPR.png";
        Operacion operacion = Operacion.EROSION; // Cambiar a DILATACION si se desea
        int caso = 1; // Caso del elemento estructurante (1-6)
        int numHilos = Runtime.getRuntime().availableProcessors();

        // Permitir configuración por argumentos
        if (args.length >= 1) operacion = Operacion.valueOf(args[0].toUpperCase());
        if (args.length >= 2) caso = Integer.parseInt(args[1]);
        if (args.length >= 3) numHilos = Integer.parseInt(args[2]);

        System.out.println("Configuración:");
        System.out.println("  Imagen: " + rutaImagen);
        System.out.println("  Operación: " + operacion);
        System.out.println("  Caso elemento: " + caso);
        System.out.println("  Hilos disponibles: " + numHilos);
        System.out.println("\n" + "=".repeat(50) + "\n");

        try {
            // Crear elemento estructurante
            ElementoEstructurante elemento = new ElementoEstructurante(caso);
            elemento.imprimir();
            System.out.println();

            // PROCESAMIENTO SECUENCIAL
            ProcesarSecuencial procSecuencial = new ProcesarSecuencial(rutaImagen);
            long tiempoSecuencial = procSecuencial.procesar(operacion, elemento);
            procSecuencial.guardarImagen("resultado_secuencial_" + operacion.toString().toLowerCase() + "_caso" + caso + ".png");

            // PROCESAMIENTO PARALELO
            ProcesarParalelo procParalelo = new ProcesarParalelo(rutaImagen, numHilos);
            long tiempoParalelo = procParalelo.procesarParalelo(operacion, elemento);
            procParalelo.guardarImagen("resultado_paralelo_" + operacion.toString().toLowerCase() + "_caso" + caso + ".png");

            // COMPARACIÓN
            System.out.println("\n" + "=".repeat(50));
            System.out.println("COMPARACIÓN DE RENDIMIENTO");
            System.out.println("=".repeat(50));
            System.out.println("Tiempo Secuencial: " + tiempoSecuencial + " ms");
            System.out.println("Tiempo Paralelo:   " + tiempoParalelo + " ms");
            double speedup = (double) tiempoSecuencial / tiempoParalelo;
            System.out.println("Speedup:           " + String.format("%.2f", speedup) + "x");
            System.out.println("Eficiencia:        " + String.format("%.2f", (speedup / numHilos) * 100) + "%");

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
