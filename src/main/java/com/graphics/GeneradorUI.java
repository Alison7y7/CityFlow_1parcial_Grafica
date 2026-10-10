package com.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Generador de Interfaces 2D para CityFlow.
 * Utiliza Java AWT para dibujar menús elegantes, nítidos y modernos.
 */
public class GeneradorUI {

    private static final Color FONDO_OSCURO = new Color(15, 20, 25, 210);
    private static final Color CYAN_NEON = new Color(0, 255, 255);
    private static final Color AZUL_OSCURO = new Color(0, 100, 200, 150);
    private static final Color AMARILLO_VIAL = new Color(255, 204, 0);

    /** Genera la textura para el Menú Principal (1024x1024) */
    public static BufferedImage generarMenuPrincipal(int opcionSeleccionada) {
        BufferedImage img = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Panel central
        g2d.setColor(FONDO_OSCURO);
        g2d.fillRoundRect(212, 212, 600, 600, 40, 40);
        
        // Borde elegante
        g2d.setColor(CYAN_NEON);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(212, 212, 600, 600, 40, 40);

        // Título principal
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 72));
        centrarTexto(g2d, "CITYFLOW", 320, 1024);

        g2d.setColor(AMARILLO_VIAL);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 24));
        centrarTexto(g2d, "SIMULADOR DE CONDUCCIÓN URBANA", 370, 1024);

        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 20));
        centrarTexto(g2d, "Explora la ciudad. Conduce. Completa las entregas.", 420, 1024);

        // Opciones
        String[] opciones = {"INICIAR PARTIDA", "CONTROLES", "SALIR"};
        dibujarBotones(g2d, opciones, opcionSeleccionada, 500);

        g2d.dispose();
        return img;
    }

    /** Genera la textura para el Menú de Pausa (1024x1024) */
    public static BufferedImage generarMenuPausa(int opcionSeleccionada) {
        BufferedImage img = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fondo oscuro global para la pausa
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(0, 0, 1024, 1024);

        // Panel
        g2d.setColor(FONDO_OSCURO);
        g2d.fillRoundRect(262, 212, 500, 600, 40, 40);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 64));
        centrarTexto(g2d, "PAUSA", 320, 1024);

        String[] opciones = {"CONTINUAR", "CONTROLES", "REINICIAR PARTIDA", "MENÚ PRINCIPAL", "SALIR"};
        dibujarBotones(g2d, opciones, opcionSeleccionada, 420);

        g2d.dispose();
        return img;
    }

    /** Genera la pantalla de Victoria (1024x1024) */
    public static BufferedImage generarVictoria(int opcionSeleccionada, int tiempo) {
        BufferedImage img = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(new Color(0, 50, 20, 180));
        g2d.fillRect(0, 0, 1024, 1024);

        g2d.setColor(FONDO_OSCURO);
        g2d.fillRoundRect(212, 262, 600, 500, 40, 40);

        g2d.setColor(AMARILLO_VIAL);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 54));
        centrarTexto(g2d, "¡ENTREGAS COMPLETADAS!", 350, 1024);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 36));
        centrarTexto(g2d, "MISIÓN CUMPLIDA", 410, 1024);

        g2d.setColor(CYAN_NEON);
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 28));
        centrarTexto(g2d, "Tiempo total: " + tiempo + " segundos", 470, 1024);

        String[] opciones = {"JUGAR OTRA VEZ", "MENÚ PRINCIPAL", "SALIR"};
        dibujarBotones(g2d, opciones, opcionSeleccionada, 550);

        g2d.dispose();
        return img;
    }

    /** Genera la pantalla de Controles (1024x1024) */
    public static BufferedImage generarControles() {
        BufferedImage img = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(FONDO_OSCURO);
        g2d.fillRoundRect(112, 112, 800, 800, 40, 40);

        g2d.setColor(CYAN_NEON);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 48));
        centrarTexto(g2d, "CONTROLES", 200, 1024);

        g2d.setColor(Color.WHITE);
        int y = 280;
        
        g2d.setFont(new Font("SansSerif", Font.BOLD, 24));
        g2d.setColor(AMARILLO_VIAL);
        g2d.drawString("CONDUCCIÓN", 180, y);
        g2d.setColor(Color.WHITE); g2d.setFont(new Font("SansSerif", Font.PLAIN, 22));
        g2d.drawString("W / S : Acelerar y retroceder", 180, y += 40);
        g2d.drawString("A / D : Girar", 180, y += 40);
        g2d.drawString("ESPACIO : Frenar", 180, y += 40);
        g2d.drawString("Flechas : Conducción alternativa", 180, y += 40);

        y = 280;
        g2d.setFont(new Font("SansSerif", Font.BOLD, 24));
        g2d.setColor(AMARILLO_VIAL);
        g2d.drawString("CÁMARA E ILUMINACIÓN", 550, y);
        g2d.setColor(Color.WHITE); g2d.setFont(new Font("SansSerif", Font.PLAIN, 22));
        g2d.drawString("C : Cambiar cámara", 550, y += 40);
        g2d.drawString("N : Día / Noche", 550, y += 40);
        g2d.drawString("F : Faros ON/OFF", 550, y += 40);

        y = 520;
        g2d.setFont(new Font("SansSerif", Font.BOLD, 24));
        g2d.setColor(AMARILLO_VIAL);
        g2d.drawString("INTERFAZ Y PARTIDA", 180, y);
        g2d.setColor(Color.WHITE); g2d.setFont(new Font("SansSerif", Font.PLAIN, 22));
        g2d.drawString("M : Minimapa", 180, y += 40);
        g2d.drawString("P o ESC : Pausa", 180, y += 40);
        g2d.drawString("R : Reiniciar", 180, y += 40);
        g2d.drawString("ENTER : Seleccionar en menús", 180, y += 40);

        g2d.setColor(Color.GRAY);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 28));
        centrarTexto(g2d, "[ ENTER o ESC para volver ]", 820, 1024);

        g2d.dispose();
        return img;
    }

    /** Genera la textura del HUD (512x128) - Rediseño Definitivo sin superposiciones */
    public static BufferedImage generarHUD(float velocidad, boolean noche, boolean faros, int entregas, String sector) {
        BufferedImage img = new BufferedImage(512, 128, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Fondo elegante
        g2d.setColor(FONDO_OSCURO);
        g2d.fillRoundRect(0, 0, 512, 128, 20, 20);
        
        // Borde superior decorativo
        g2d.setColor(CYAN_NEON);
        g2d.fillRect(20, 0, 472, 4);

        // ============ FILA SUPERIOR ============
        // SECTOR (Izquierda, espacio amplio)
        g2d.setColor(AMARILLO_VIAL);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
        g2d.drawString("SECTOR ACTUAL", 20, 25);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2d.drawString(sector, 20, 45);

        // ENTREGAS (Derecha)
        g2d.setColor(AMARILLO_VIAL);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
        g2d.drawString("ENTREGAS", 400, 25);
        g2d.setColor(entregas == 3 ? Color.GREEN : Color.WHITE);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 22));
        g2d.drawString(entregas + "/3", 400, 50);

        // ============ FILA CENTRAL ============
        // VELOCIDAD
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2d.drawString("VELOCIDAD", 20, 70);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 22));
        g2d.drawString((int)Math.abs(velocidad*10) + " km/h", 100, 70);

        // Barra de Velocidad (Atraviesa todo el panel horizontalmente)
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRoundRect(20, 80, 472, 10, 5, 5);
        float ratio = Math.min(1.0f, Math.abs(velocidad) / 16.0f);
        g2d.setColor(new Color(1f - ratio, ratio, 0.2f)); 
        g2d.fillRoundRect(20, 80, (int)(472 * ratio), 10, 5, 5);

        // ============ FILA INFERIOR ============
        // ENTORNO
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2d.drawString("ENTORNO:", 20, 110);
        g2d.setColor(noche ? new Color(150, 150, 255) : Color.YELLOW);
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g2d.drawString(noche ? "NOCHE" : "DÍA", 90, 110);

        // FAROS
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2d.drawString("FAROS:", 200, 110);
        g2d.setColor(faros ? CYAN_NEON : Color.GRAY);
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g2d.drawString(faros ? "ON" : "OFF", 250, 110);

        g2d.dispose();
        return img;
    }

    private static void centrarTexto(Graphics2D g2d, String texto, int y, int anchoTotal) {
        int w = g2d.getFontMetrics().stringWidth(texto);
        g2d.drawString(texto, (anchoTotal - w) / 2, y);
    }

    private static void dibujarBotones(Graphics2D g2d, String[] opciones, int seleccionada, int startY) {
        for (int i = 0; i < opciones.length; i++) {
            int y = startY + i * 80;
            if (i == seleccionada) {
                g2d.setColor(AZUL_OSCURO);
                g2d.fillRoundRect(262, y - 45, 500, 60, 20, 20);
                g2d.setColor(CYAN_NEON);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(262, y - 45, 500, 60, 20, 20);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("SansSerif", Font.BOLD, 28));
                centrarTexto(g2d, "> " + opciones[i] + " <", y, 1024);
            } else {
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 28));
                centrarTexto(g2d, opciones[i], y, 1024);
            }
        }
    }
}
