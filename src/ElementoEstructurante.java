/**
 * Representa un elemento estructurante para operaciones morfológicas
 * Contiene las posiciones relativas (offsets) respecto al píxel central
 * 
 * Proyecto 1 - Sistemas Distribuidos
 * Universidad de Talca
 */
public class ElementoEstructurante {
    
    private int[][] offsets;  // Pares [dx, dy] de desplazamientos
    private String nombre;
    
    /**
     * Constructor privado para crear elementos estructurantes
     */
    private ElementoEstructurante(String nombre, int[][] offsets) {
        this.nombre = nombre;
        this.offsets = offsets;
    }
    /**
     * Elemento 0: Cruz completa (más común)
     *   □
     * □ ■ □
     *   □
     */
    public static ElementoEstructurante ELEMENTO_0() {
        return new ElementoEstructurante("Elemento 0 (Cruz Completa)", new int[][] {
            {0, -1},  // Arriba
            {-1, 0},  // Izquierda
            {0, 0},   // Centro (píxel de referencia)
            {1, 0},   // Derecha
            {0, 1}    // Abajo
        });
    }
    /**
     * Elemento 1: L invertida (esquina superior izquierda)
     * ■ □
     * ■ □
     */
    public static ElementoEstructurante ELEMENTO_1() {
        return new ElementoEstructurante("Elemento 1 (L invertida)", new int[][] {
            {-1, 0}, // Superior izquierda
            {0, 0},  // Superior centro (píxel de referencia)
            {0, 1}   // Centro izquierda
        });
    }
    
    /**
     * Elemento 2: L (esquina inferior izquierda)
     *   □
     * □ ■
     */
    public static ElementoEstructurante ELEMENTO_2() {
        return new ElementoEstructurante("Elemento 2 (L normal)", new int[][] {
            {0, -1},  // Superior centro
            {-1, 0},  // Centro izquierda
            {0, 0}    // Centro (píxel de referencia)
        });
    }
    
    /**
     * Elemento 3: Línea horizontal de 3 píxeles
     * □ ■ □
     */
    public static ElementoEstructurante ELEMENTO_3() {
        return new ElementoEstructurante("Elemento 3 (Línea Horizontal 3x1)", new int[][] {
            {-1, 0},  // Izquierda
            {0, 0},   // Centro (píxel de referencia)
            {1, 0}    // Derecha
        });
    }
    
    /**
     * Elemento 4: Línea vertical de 2 píxeles
     * □
     * ■
     */
    public static ElementoEstructurante ELEMENTO_4() {
        return new ElementoEstructurante("Elemento 4 (Línea Vertical 2x1)", new int[][] {
            {0, 1},  // Arriba
            {0, 0}    // Centro (píxel de referencia)
        });
    }
    
    /**
     * Elemento 5: Cruz completa (más común)
     * □   □ 
     *   ■ 
     * □   □
     */
    public static ElementoEstructurante ELEMENTO_5() {
        return new ElementoEstructurante("Elemento 5 (Cruz Completa)", new int[][] {
            {-1, -1},  // Arriba
            {-1, 1},  // Izquierda
            {0, 0},   // Centro (píxel de referencia)
            {1, 1},   // Derecha
            {1, -1}    // Abajo
        });
    }
    
    /**
     * Elemento estructurante 3x3 completo (para casos especiales)
     * □ □ □
     * □ ■ □
     * □ □ □
     */
    public static ElementoEstructurante CUADRADO_3x3() {
        return new ElementoEstructurante("Cuadrado 3x3", new int[][] {
            {-1, -1}, {0, -1}, {1, -1},  // Fila superior
            {-1, 0},  {0, 0},  {1, 0},   // Fila central
            {-1, 1},  {0, 1},  {1, 1}    // Fila inferior
        });
    }
    
    /**
     * Obtiene todos los elementos estructurantes del proyecto
     */
    public static ElementoEstructurante[] obtenerTodos() {
        return new ElementoEstructurante[] {
            ELEMENTO_1(),
            ELEMENTO_2(),
            ELEMENTO_3(),
            ELEMENTO_4(),
            ELEMENTO_5()
        };
    }
    
    // Getters
    public int[][] getOffsets() {
        return offsets;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public int getTamanio() {
        return offsets.length;
    }
    
    /**
     * Verifica si una posición está dentro de los límites de la imagen
     */
    public boolean dentroLimites(int x, int y, int ancho, int alto) {
        return x >= 0 && x < ancho && y >= 0 && y < alto;
    }
    
    /**
     * Visualiza el elemento estructurante (para debugging)
     */
    public void mostrar() {
        System.out.println("\n" + nombre + ":");
        
        // Encontrar dimensiones del elemento
        int minX = 0, maxX = 0, minY = 0, maxY = 0;
        for (int[] offset : offsets) {
            minX = Math.min(minX, offset[0]);
            maxX = Math.max(maxX, offset[0]);
            minY = Math.min(minY, offset[1]);
            maxY = Math.max(maxY, offset[1]);
        }
        
        // Crear una matriz para visualizar
        int altura = maxY - minY + 1;
        int anchura = maxX - minX + 1;
        char[][] grid = new char[altura][anchura];
        
        // Llenar con espacios
        for (int i = 0; i < altura; i++) {
            for (int j = 0; j < anchura; j++) {
                grid[i][j] = ' ';
            }
        }
        
        // Marcar los puntos del elemento estructurante
        for (int[] offset : offsets) {
            int gridY = offset[1] - minY;
            int gridX = offset[0] - minX;
            
            if (offset[0] == 0 && offset[1] == 0) {
                grid[gridY][gridX] = '■';  // Píxel central
            } else {
                grid[gridY][gridX] = '□';  // Píxeles vecinos
            }
        }
        
        // Imprimir
        for (int i = 0; i < altura; i++) {
            System.out.print("  ");
            for (int j = 0; j < anchura; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
    }
    
    @Override
    public String toString() {
        return nombre + " (" + offsets.length + " píxeles)";
    }
    
    /**
     * Método main para probar los elementos estructurantes
     */
    public static void main(String[] args) {
        System.out.println("=== Elementos Estructurantes - Proyecto 1 ===\n");
        
        ElementoEstructurante[] elementos = obtenerTodos();
        
        for (int i = 0; i < elementos.length; i++) {
            System.out.println((i + 1) + ". " + elementos[i]);
            elementos[i].mostrar();
        }
        
        System.out.println("\n=== Elemento Adicional (3x3 completo) ===");
        CUADRADO_3x3().mostrar();
    }
}
