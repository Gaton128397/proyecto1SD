public class ElementoEstructurante {
    private int[][] matriz;
    private int centroX;
    private int centroY;
    private int caso;

    public ElementoEstructurante(int caso) {
        this.caso = caso;
        inicializarElemento(caso);
    }

    private void inicializarElemento(int caso) {
        switch (caso) {
            case 1: // Cruz
                // □ x □
                // x X x
                // □ x □
                matriz = new int[][] {
                        {0, 1, 0},
                        {1, 1, 1},
                        {0, 1, 0}
                };
                centroX = 1;
                centroY = 1;
                break;

            case 2: // L invertida hacia abajo
                // x X □
                // □ x □ 
                matriz = new int[][] {
                        {1, 1, 0},
                        {0, 1, 0}
                };
                centroX = 1;
                centroY = 0;
                break;

            case 3: // L invertida
                // □ x □
                // x X □
                matriz = new int[][] {
                        {0, 1, 0},
                        {1, 1, 0}
                };
                centroX = 1;
                centroY = 1;
                break;

            case 4: // Horizontal
                // x X x
                matriz = new int[][] {
                        {1, 1, 1}
                };
                centroX = 1;
                centroY = 0;
                break;

            case 5: // Pixel
                // X
                // x
                matriz = new int[][] {
                        {1},
                        {1}
                };
                centroX = 0;
                centroY = 0;
                break;

            case 6: // Diagonal
                // x □ x
                // □ x □
                // x □ x
                matriz = new int[][] {
                        {1, 0, 1},
                        {0, 1, 0},
                        {1, 0, 1}
                };
                centroX = 1;
                centroY = 1;
                break;

            default:
                // Por defecto, caso 5
                matriz = new int[][] {{1}};
                centroX = 0;
                centroY = 0;
                this.caso = 1;
                System.err.println("Caso no válido (debe ser 1-6). Usando caso 1 (cruz) por defecto.");
        }
    }

    public int getCaso() {
        return caso;
    }

    public int[][] getMatriz() {
        return matriz;
    }

    public int getAlto() {
        return matriz.length;
    }

    public int getAncho() {
        return matriz[0].length;
    }

    public int getCentroX() {
        return centroX;
    }

    public int getCentroY() {
        return centroY;
    }

    public boolean estaActivo(int y, int x) {
        if (y >= 0 && y < matriz.length && x >= 0 && x < matriz[0].length) {
            return matriz[y][x] == 1;
        }
        return false;
    }

    public String getNombreCaso() {
        switch (caso) {
            case 1: return "Cruz";
            case 2: return "L invertida hacia abajo";
            case 3: return "L invertida";
            case 4: return "Horizontal";
            case 5: return "Pixel";
            case 6: return "Diagonal";
            default: return "Desconocido";
        }
    }

    public void imprimir() {
        System.out.println("Elemento Estructurante - Caso " + caso + ": " + getNombreCaso());
        System.out.println("Centro: (" + centroX + ", " + centroY + ")");
        for (int y = 0; y < matriz.length; y++) {
            for (int x = 0; x < matriz[0].length; x++) {
                if (y == centroY && x == centroX) {
                    System.out.print("[" + matriz[y][x] + "] ");
                } else {
                    System.out.print(" " + matriz[y][x] + "  ");
                }
            }
            System.out.println();
        }
    }

    public static void mostrarCasosDisponibles() {
        System.out.println("Casos de Elementos Estructurantes disponibles:");
        System.out.println("1 - Cruz");
        System.out.println("2 - L invertida hacia abajo");
        System.out.println("3 - L invertida");
        System.out.println("4 - Horizontal");
        System.out.println("5 - Pixel");
        System.out.println("6 - Diagonal");
    }
}