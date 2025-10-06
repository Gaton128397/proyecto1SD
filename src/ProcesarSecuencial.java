import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class ProcesarSecuencial {

    private BufferedImage imagenOriginal;
    private BufferedImage imagenResultado;
    private int ancho;
    private int alto;


    public ProcesarSecuencial(String rutaImagen) throws IOException {
        cargarImagen(rutaImagen);
    }

    private void cargarImagen(String rutaImagen) throws IOException {
        File archivoImagen = new File(rutaImagen);
        if (!archivoImagen.exists()) {
            throw new IOException("La imagen " + rutaImagen + " no existe."); //si la imagen no existe lanza error
        }

        imagenOriginal = ImageIO.read(archivoImagen);
        ancho = imagenOriginal.getWidth();
        alto = imagenOriginal.getHeight();

        System.out.println("Imagen cargada: " + ancho + "x" + alto + " píxeles");
    }
    //procesa la imagen de forma secuencial aplicando erosion o dilatacion
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
    //aplica la erosion a un pixel segun el elemento estructurante, se comienza con el valor maximo y se busca el minimo
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
    //aplica la dilatacion a un pixel segun el elemento estructurante, se comienza con el valor minimo y se busca el maximo
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
    //obtiene el pixel de la imagen, si esta fuera de los limites devuelve un color negro para evitar cualquer error
    protected int obtenerPixelSeguro(int x, int y) {
        if (x >= 0 && x < ancho && y >= 0 && y < alto) {
            return imagenOriginal.getRGB(x, y);
        }
        return 0; // Negro para píxeles fuera de la imagen
    }

    //obtener los colores dentro del espectro correspodiente

    protected int obtenerRojo(int rgb) {
        return (rgb >> 16) & 0xFF;
    }


    protected int obtenerVerde(int rgb) {
        return (rgb >> 8) & 0xFF;
    }


    protected int obtenerAzul(int rgb) {
        return rgb & 0xFF;
    }


    protected int combinarRGB(int r, int g, int b) {
        return (r << 16) | (g << 8) | b;
    }

    //donde se guardara la imagen
    public void guardarImagen(String nombreArchivo) throws IOException {
        if (imagenResultado == null) {
            throw new IOException("No hay imagen procesada para guardar.");
        }

        File archivoSalida = new File(nombreArchivo);
        ImageIO.write(imagenResultado, "PNG", archivoSalida);

        System.out.println("Imagen guardada: " + nombreArchivo);
        System.out.println("Tamaño del archivo: " + (archivoSalida.length() / 1024) + " KB");
    }

    public BufferedImage getImagenOriginal() {
        return imagenOriginal;
    }


    public BufferedImage getImagenResultado() {
        return imagenResultado;
    }


    public int getAncho() {
        return ancho;
    }

    public int getAlto() {
        return alto;
    }

    protected void setImagenResultado(BufferedImage imagen) {
        this.imagenResultado = imagen;
    }
}