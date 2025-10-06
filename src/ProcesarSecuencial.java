import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Clase para procesar imágenes de forma secuencial
 * Aplica operaciones de erosión y dilatación usando elementos estructurantes
 */
public class ProcesarSecuencial {

    private BufferedImage imagenOriginal;
    private BufferedImage imagenResultado;
    private int ancho;
    private int alto;

    /**
     * Constructor que carga la imagen a procesar
     * @param rutaImagen Ruta de la imagen a procesar
     */
    public ProcesarSecuencial(String rutaImagen) throws IOException {
        cargarImagen(rutaImagen);
    }

    /**
     * Carga la imagen desde el archivo
     */
    private void cargarImagen(String rutaImagen) throws IOException {
        File archivoImagen = new File(rutaImagen);
        if (!archivoImagen.exists()) {
            throw new IOException("La imagen " + rutaImagen + " no existe.");
        }

        imagenOriginal = ImageIO.read(archivoImagen);
        ancho = imagenOriginal.getWidth();
        alto = imagenOriginal.getHeight();

        System.out.println("Imagen cargada: " + ancho + "x" + alto + " píxeles");
    }

    /**
     * Procesa la imagen aplicando la operación especificada
     * @param operacion Tipo de operación (EROSION o DILATACION)
     * @param elemento Elemento estructurante a aplicar
     * @return Tiempo de ejecución en milisegundos
     */
    public long procesar(Operacion operacion, ElementoEstructurante elemento) {
        System.out.println("\n=== Procesamiento Secuencial ===");
        System.out.println("Operación: " + operacion);
        System.out.println("Elemento: Caso " + elemento.getCaso() + " - " + elemento.getNombreCaso());

        long tiempoInicio = System.currentTimeMillis();

        // Crear imagen resultado con las mismas dimensiones
        imagenResultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

        // Recorrer cada píxel de la imagen
        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int nuevoPixel;

                if (operacion == Operacion.EROSION) {
                    nuevoPixel = aplicarErosion(x, y, elemento);
                } else {
                    nuevoPixel = aplicarDilatacion(x, y, elemento);
                }

                imagenResultado.setRGB(x, y, nuevoPixel);
            }

            // Mostrar progreso cada 10%
            if (y % (alto / 10) == 0 && y > 0) {
                int porcentaje = (y * 100) / alto;
                System.out.println("Progreso: " + porcentaje + "%");
            }
        }

        long tiempoFin = System.currentTimeMillis();
        long tiempoTotal = tiempoFin - tiempoInicio;

        System.out.println("Progreso: 100%");
        System.out.println("Tiempo de procesamiento: " + tiempoTotal + " ms");

        return tiempoTotal;
    }

    /**
     * Aplica la operación de EROSIÓN en un píxel específico
     * TODO: Tu compañero debe implementar este método
     *
     * @param x Coordenada X del píxel central
     * @param y Coordenada Y del píxel central
     * @param elemento Elemento estructurante
     * @return Valor RGB del píxel resultante (debe ser el MÍNIMO de los valores del elemento estructurante)
     */
    private int aplicarErosion(int x, int y, ElementoEstructurante elemento) {
        // TODO: Implementar erosión
        // 1. Obtener los valores R, G, B mínimos de los píxeles cubiertos por el elemento estructurante
        // 2. Retornar el color RGB con esos valores mínimos

        // PISTA: Debes recorrer la matriz del elemento estructurante
        // y obtener el mínimo valor de R, G, B por separado

        return imagenOriginal.getRGB(x, y); // Placeholder - CAMBIAR
    }

    /**
     * Aplica la operación de DILATACIÓN en un píxel específico
     * TODO: Tu compañero debe implementar este método
     *
     * @param x Coordenada X del píxel central
     * @param y Coordenada Y del píxel central
     * @param elemento Elemento estructurante
     * @return Valor RGB del píxel resultante (debe ser el MÁXIMO de los valores del elemento estructurante)
     */
    private int aplicarDilatacion(int x, int y, ElementoEstructurante elemento) {
        // TODO: Implementar dilatación
        // 1. Obtener los valores R, G, B máximos de los píxeles cubiertos por el elemento estructurante
        // 2. Retornar el color RGB con esos valores máximos

        // PISTA: Similar a erosión pero buscando el MÁXIMO

        return imagenOriginal.getRGB(x, y); // Placeholder - CAMBIAR
    }

    /**
     * Método auxiliar para obtener un píxel de forma segura (maneja bordes)
     * @param x Coordenada X
     * @param y Coordenada Y
     * @return Valor RGB del píxel, o negro (0) si está fuera de límites
     */
    protected int obtenerPixelSeguro(int x, int y) {
        if (x >= 0 && x < ancho && y >= 0 && y < alto) {
            return imagenOriginal.getRGB(x, y);
        }
        return 0; // Negro para píxeles fuera de la imagen
    }

    /**
     * Extrae el componente Rojo de un color RGB
     */
    protected int obtenerRojo(int rgb) {
        return (rgb >> 16) & 0xFF;
    }

    /**
     * Extrae el componente Verde de un color RGB
     */
    protected int obtenerVerde(int rgb) {
        return (rgb >> 8) & 0xFF;
    }

    /**
     * Extrae el componente Azul de un color RGB
     */
    protected int obtenerAzul(int rgb) {
        return rgb & 0xFF;
    }

    /**
     * Combina componentes R, G, B en un valor RGB
     */
    protected int combinarRGB(int r, int g, int b) {
        return (r << 16) | (g << 8) | b;
    }

    /**
     * Guarda la imagen resultante en un archivo
     * @param nombreArchivo Nombre del archivo de salida
     */
    public void guardarImagen(String nombreArchivo) throws IOException {
        if (imagenResultado == null) {
            throw new IOException("No hay imagen procesada para guardar.");
        }

        File archivoSalida = new File(nombreArchivo);
        ImageIO.write(imagenResultado, "PNG", archivoSalida);

        System.out.println("Imagen guardada: " + nombreArchivo);
        System.out.println("Tamaño del archivo: " + (archivoSalida.length() / 1024) + " KB");
    }

    /**
     * Obtiene la imagen original
     */
    public BufferedImage getImagenOriginal() {
        return imagenOriginal;
    }

    /**
     * Obtiene la imagen resultante
     */
    public BufferedImage getImagenResultado() {
        return imagenResultado;
    }

    /**
     * Obtiene el ancho de la imagen
     */
    public int getAncho() {
        return ancho;
    }

    /**
     * Obtiene el alto de la imagen
     */
    public int getAlto() {
        return alto;
    }
}