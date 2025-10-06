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
     * La erosión toma el MÍNIMO valor de cada canal (R, G, B) en la vecindad
     * definida por el elemento estructurante
     *
     * @param x Coordenada X del píxel central
     * @param y Coordenada Y del píxel central
     * @param elemento Elemento estructurante
     * @return Valor RGB del píxel resultante (mínimo de los valores)
     */
    private int aplicarErosion(int x, int y, ElementoEstructurante elemento) {
        // Inicializar con valores máximos (255) para encontrar el mínimo
        int minR = 255;
        int minG = 255;
        int minB = 255;

        // Obtener dimensiones y centro del elemento estructurante
        int alturaElem = elemento.getAlto();
        int anchoElem = elemento.getAncho();
        int centroX = elemento.getCentroX();
        int centroY = elemento.getCentroY();

        // Recorrer el elemento estructurante
        for (int ey = 0; ey < alturaElem; ey++) {
            for (int ex = 0; ex < anchoElem; ex++) {
                // Solo procesar si el elemento está activo (valor = 1)
                if (elemento.estaActivo(ey, ex)) {
                    // Calcular la posición correspondiente en la imagen
                    int imgX = x + (ex - centroX);
                    int imgY = y + (ey - centroY);

                    // Obtener el píxel de forma segura (maneja bordes)
                    int rgb = obtenerPixelSeguro(imgX, imgY);

                    // Extraer componentes RGB
                    int r = obtenerRojo(rgb);
                    int g = obtenerVerde(rgb);
                    int b = obtenerAzul(rgb);

                    // Encontrar el MÍNIMO de cada canal
                    minR = Math.min(minR, r);
                    minG = Math.min(minG, g);
                    minB = Math.min(minB, b);
                }
            }
        }

        // Combinar los valores mínimos en un color RGB
        return combinarRGB(minR, minG, minB);
    }

    /**
     * Aplica la operación de DILATACIÓN en un píxel específico
     * La dilatación toma el MÁXIMO valor de cada canal (R, G, B) en la vecindad
     * definida por el elemento estructurante
     *
     * @param x Coordenada X del píxel central
     * @param y Coordenada Y del píxel central
     * @param elemento Elemento estructurante
     * @return Valor RGB del píxel resultante (máximo de los valores)
     */
    private int aplicarDilatacion(int x, int y, ElementoEstructurante elemento) {
        // Inicializar con valores mínimos (0) para encontrar el máximo
        int maxR = 0;
        int maxG = 0;
        int maxB = 0;

        // Obtener dimensiones y centro del elemento estructurante
        int alturaElem = elemento.getAlto();
        int anchoElem = elemento.getAncho();
        int centroX = elemento.getCentroX();
        int centroY = elemento.getCentroY();

        // Recorrer el elemento estructurante
        for (int ey = 0; ey < alturaElem; ey++) {
            for (int ex = 0; ex < anchoElem; ex++) {
                // Solo procesar si el elemento está activo (valor = 1)
                if (elemento.estaActivo(ey, ex)) {
                    // Calcular la posición correspondiente en la imagen
                    int imgX = x + (ex - centroX);
                    int imgY = y + (ey - centroY);

                    // Obtener el píxel de forma segura (maneja bordes)
                    int rgb = obtenerPixelSeguro(imgX, imgY);

                    // Extraer componentes RGB
                    int r = obtenerRojo(rgb);
                    int g = obtenerVerde(rgb);
                    int b = obtenerAzul(rgb);

                    // Encontrar el MÁXIMO de cada canal
                    maxR = Math.max(maxR, r);
                    maxG = Math.max(maxG, g);
                    maxB = Math.max(maxB, b);
                }
            }
        }

        // Combinar los valores máximos en un color RGB
        return combinarRGB(maxR, maxG, maxB);
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

    /**
     * Establece la imagen resultante (usado por ProcesarParalelo)
     */
    protected void setImagenResultado(BufferedImage imagen) {
        this.imagenResultado = imagen;
    }
}