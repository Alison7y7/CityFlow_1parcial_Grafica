package com.graphics;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.glfw.GLFW.*; 
import static org.lwjgl.opengl.GL33.*; 

/**
 * CLASE 4: CIUDAD TERMINADA, ENTREGAS Y MINIMAPA.
 * Modificada en Fase 5 (Rediseño UI) para incluir máquina de estados completa y superposición gráfica de Videojuego.
 */
public class clase4 extends clase3 {

    // ==================== 1. ESTADO DEL JUEGO ====================
    public enum EstadoJuego { MENU_PRINCIPAL, CONTROLES, JUGANDO, PAUSA, VICTORIA }
    protected EstadoJuego estadoActual = EstadoJuego.MENU_PRINCIPAL;
    protected EstadoJuego estadoAnterior = EstadoJuego.MENU_PRINCIPAL;
    protected int opcionSeleccionada = 0;
    
    // UI HUD y Menús
    private int shaderUI;
    private int vaoUI;
    private int vboUI;
    private int texOverlay; // Textura central de alta resolución para menús (1024x1024)
    private int texHUD;     // Textura para el HUD inferior (512x128)
    private boolean menuSucio = true; // Flag para recrear la textura del menú sólo cuando cambia la selección
    
    protected VehiculoAutonomo[] trafico;
    protected boolean mostrarMapa = true; 
    protected int entregas = 0; 
    protected float tiempo = 0;
    
    private static final float[][] DESTINOS = {
        {50, -50}, {-50, -50}, {-50, 50} 
    };

    // ==================== 2. MÁQUINA DE ESTADOS Y CONTROLES ====================
    @Override
    protected void tecla(int key) {
        if (estadoActual == EstadoJuego.MENU_PRINCIPAL) {
            manejarMenuPrincipal(key);
        } else if (estadoActual == EstadoJuego.PAUSA) {
            manejarMenuPausa(key);
        } else if (estadoActual == EstadoJuego.VICTORIA) {
            manejarMenuVictoria(key);
        } else if (estadoActual == EstadoJuego.CONTROLES) {
            if (key == GLFW_KEY_ENTER || key == GLFW_KEY_ESCAPE) {
                cambiarEstado(estadoAnterior);
            }
        } else if (estadoActual == EstadoJuego.JUGANDO) {
            // Controles de juego
            super.tecla(key);
            if (key == GLFW_KEY_M) { mostrarMapa = !mostrarMapa; }
            if (key == GLFW_KEY_H) { cambiarEstado(EstadoJuego.CONTROLES); }
            if (key == GLFW_KEY_P || key == GLFW_KEY_ESCAPE) { cambiarEstado(EstadoJuego.PAUSA); }
        }
    }

    private void cambiarEstado(EstadoJuego nuevoEstado) {
        estadoAnterior = estadoActual;
        estadoActual = nuevoEstado;
        opcionSeleccionada = 0;
        menuSucio = true;
    }

    private void manejarMenuPrincipal(int key) {
        if (key == GLFW_KEY_W || key == GLFW_KEY_UP) { opcionSeleccionada = Math.max(0, opcionSeleccionada - 1); menuSucio = true; }
        if (key == GLFW_KEY_S || key == GLFW_KEY_DOWN) { opcionSeleccionada = Math.min(2, opcionSeleccionada + 1); menuSucio = true; }
        if (key == GLFW_KEY_ENTER || key == GLFW_KEY_SPACE) {
            if (opcionSeleccionada == 0) iniciarPartida();
            else if (opcionSeleccionada == 1) cambiarEstado(EstadoJuego.CONTROLES);
            else if (opcionSeleccionada == 2) glfwSetWindowShouldClose(ventana, true);
        }
    }

    private void manejarMenuPausa(int key) {
        if (key == GLFW_KEY_W || key == GLFW_KEY_UP) { opcionSeleccionada = Math.max(0, opcionSeleccionada - 1); menuSucio = true; }
        if (key == GLFW_KEY_S || key == GLFW_KEY_DOWN) { opcionSeleccionada = Math.min(4, opcionSeleccionada + 1); menuSucio = true; }
        if (key == GLFW_KEY_ESCAPE) { cambiarEstado(EstadoJuego.JUGANDO); }
        if (key == GLFW_KEY_ENTER || key == GLFW_KEY_SPACE) {
            if (opcionSeleccionada == 0) cambiarEstado(EstadoJuego.JUGANDO);
            else if (opcionSeleccionada == 1) cambiarEstado(EstadoJuego.CONTROLES);
            else if (opcionSeleccionada == 2) iniciarPartida();
            else if (opcionSeleccionada == 3) cambiarEstado(EstadoJuego.MENU_PRINCIPAL);
            else if (opcionSeleccionada == 4) glfwSetWindowShouldClose(ventana, true);
        }
    }

    private void manejarMenuVictoria(int key) {
        if (key == GLFW_KEY_W || key == GLFW_KEY_UP) { opcionSeleccionada = Math.max(0, opcionSeleccionada - 1); menuSucio = true; }
        if (key == GLFW_KEY_S || key == GLFW_KEY_DOWN) { opcionSeleccionada = Math.min(2, opcionSeleccionada + 1); menuSucio = true; }
        if (key == GLFW_KEY_ENTER || key == GLFW_KEY_SPACE) {
            if (opcionSeleccionada == 0) iniciarPartida();
            else if (opcionSeleccionada == 1) cambiarEstado(EstadoJuego.MENU_PRINCIPAL);
            else if (opcionSeleccionada == 2) glfwSetWindowShouldClose(ventana, true);
        }
    }

    @Override
    protected void reiniciar() {
        super.reiniciar();
        entregas = 0;
        tiempo = 0;
    }

    private void iniciarPartida() {
        reiniciar(); 
        cambiarEstado(EstadoJuego.JUGANDO);
    }

    // ==================== 3. CÁMARA CINEMÁTICA Y LÓGICA ====================
    @Override
    protected boolean puedeCircular(float x, float z) {
        if (!super.puedeCircular(x, z)) return false; 
        if (trafico != null && estadoActual == EstadoJuego.JUGANDO) {
            for (VehiculoAutonomo v : trafico) {
                float dx = x - v.x;
                float dz = z - v.z;
                if (dx * dx + dz * dz < 16.0f) return false; 
            }
        }
        return true;
    }

    @Override
    protected void configurarCamara() {
        if (estadoActual == EstadoJuego.MENU_PRINCIPAL || estadoActual == EstadoJuego.CONTROLES && estadoAnterior == EstadoJuego.MENU_PRINCIPAL) {
            // Cámara aérea cinematográfica girando suavemente en el menú inicial
            float t = (float)glfwGetTime() * 0.15f;
            float camaraX = (float) Math.sin(t) * 100;
            float camaraZ = (float) Math.cos(t) * 100;
            vector("uOjo", camaraX, 80, camaraZ);
            vector("uObjetivo", 0, 0, 0);
            decimal("uAspecto", (float) ancho / alto);
        } else {
            super.configurarCamara();
        }
    }

    @Override
    protected void actualizar(float deltaTime) {
        // En pausa o menús, NO se acumula el tiempo de la física.
        if (estadoActual != EstadoJuego.JUGANDO) return;
        
        super.actualizar(deltaTime);
        if (trafico != null) {
            for (VehiculoAutonomo v : trafico) v.actualizar(deltaTime, autoX, autoZ, trafico);
        }
        
        if (entregas >= DESTINOS.length) return;
        
        tiempo += deltaTime;
        float distanciaX = autoX - DESTINOS[entregas][0];
        float distanciaZ = autoZ - DESTINOS[entregas][1];
        if (distanciaX * distanciaX + distanciaZ * distanciaZ < 9 && Math.abs(velocidad) < 1) {
            entregas++;
            if (entregas == DESTINOS.length) {
                cambiarEstado(EstadoJuego.VICTORIA);
            }
        }
    }

    // ==================== 4. INICIALIZACIÓN ====================
    @Override
    protected void iniciar() {
        super.iniciar();
        
        trafico = new VehiculoAutonomo[3];
        trafico[0] = new VehiculoAutonomo(new float[]{-50, -50, -10, -10}, new float[]{-10, -50, -50, -10}, 0.1f, 0.4f, 0.8f, 5.0f);
        trafico[1] = new VehiculoAutonomo(new float[]{10, 10, 50, 50}, new float[]{10, 50, 50, 10}, 0.8f, 0.2f, 0.2f, 4.5f);
        trafico[2] = new VehiculoAutonomo(new float[]{-30, 30, 30, -30}, new float[]{50, 50, -50, -50}, 0.9f, 0.7f, 0.1f, 7.0f);
        
        inicializarUI();
    }

    private void inicializarUI() {
        String vs = "#version 330 core\nlayout (location = 0) in vec2 aPos;\nlayout (location = 1) in vec2 aTex;\nout vec2 vTex;\nvoid main() { gl_Position = vec4(aPos, 0.0, 1.0); vTex = aTex; }";
        String fs = "#version 330 core\nin vec2 vTex;\nout vec4 color;\nuniform sampler2D tex;\nvoid main() { color = texture(tex, vec2(vTex.x, 1.0 - vTex.y)); }";
        
        int vsId = glCreateShader(GL_VERTEX_SHADER); glShaderSource(vsId, vs); glCompileShader(vsId);
        int fsId = glCreateShader(GL_FRAGMENT_SHADER); glShaderSource(fsId, fs); glCompileShader(fsId);
        shaderUI = glCreateProgram(); glAttachShader(shaderUI, vsId); glAttachShader(shaderUI, fsId); glLinkProgram(shaderUI);

        float[] vertices = {-1f,1f,0f,1f, -1f,-1f,0f,0f, 1f,-1f,1f,0f, -1f,1f,0f,1f, 1f,-1f,1f,0f, 1f,1f,1f,1f};
        vaoUI = glGenVertexArrays(); vboUI = glGenBuffers();
        glBindVertexArray(vaoUI); glBindBuffer(GL_ARRAY_BUFFER, vboUI);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 16, 0); glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 16, 8); glEnableVertexAttribArray(1);

        texOverlay = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texOverlay);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 1024, 1024, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);

        texHUD = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texHUD);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 512, 128, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
    }

    private void subirTextura(BufferedImage img, int tex) {
        int w = img.getWidth(), h = img.getHeight();
        int[] pixels = img.getRGB(0, 0, w, h, null, 0, w);
        ByteBuffer buffer = BufferUtils.createByteBuffer(w * h * 4);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int p = pixels[y * w + x];
                buffer.put((byte) ((p >> 16) & 0xFF)); buffer.put((byte) ((p >> 8) & 0xFF));
                buffer.put((byte) (p & 0xFF)); buffer.put((byte) ((p >> 24) & 0xFF));
            }
        }
        buffer.flip();
        glBindTexture(GL_TEXTURE_2D, tex);
        glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, w, h, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
    }

    // ==================== 5. ESCENA 3D Y DECORACIONES ====================
    @Override
    protected void escena() {
        super.escena();
        if (trafico != null) {
            for (VehiculoAutonomo v : trafico) {
                dibujarCoche3D(v.x, v.z, v.angulo, v.r, v.g, v.b);
            }
        }
        if (!vistaMapa) decorarCiudad();
        if (entregas < DESTINOS.length) dibujarDestino();
    }

    @Override
    protected void dibujarAuto() {
        // Rediseño visual del automóvil del jugador (rojo)
        dibujarCoche3D(autoX, autoZ, angulo, 0.9f, 0.2f, 0.2f);
    }

    /** Permite construir un vehículo low-poly utilizando coordenadas relativas a su centro y rotación. */
    protected void piezaLocal(float cX, float cZ, float angBase, float x, float y, float z, float sx, float sy, float sz, float r, float g, float b) {
        float coseno = (float) Math.cos(angBase);
        float seno = (float) Math.sin(angBase);
        float mundoX = cX + coseno * x + seno * z;
        float mundoZ = cZ - seno * x + coseno * z;
        cajaGirada(mundoX, y, mundoZ, sx, sy, sz, r, g, b, angBase);
    }

    /** Dibuja un coche 3D con volumen, capó, ventanas, 4 ruedas, luces y matrícula. (Z-negativo es el frente) */
    private void dibujarCoche3D(float cX, float cZ, float ang, float r, float g, float b) {
        // Carrocería principal (chasis)
        piezaLocal(cX, cZ, ang, 0, 0.45f, 0, 1.8f, 0.4f, 4.4f, r, g, b);
        
        // Cabina central superior (Oscura/Ventanas)
        piezaLocal(cX, cZ, ang, 0, 0.95f, 0.2f, 1.6f, 0.5f, 2.2f, 0.15f, 0.15f, 0.18f);
        
        // Techo (Color de la carrocería)
        piezaLocal(cX, cZ, ang, 0, 1.25f, 0.3f, 1.4f, 0.1f, 1.8f, r, g, b);

        // Parabrisas simulado inclinado en escalera
        piezaLocal(cX, cZ, ang, 0, 0.85f, -1.0f, 1.5f, 0.3f, 0.4f, 0.12f, 0.12f, 0.15f);
        
        // 4 Ruedas Negras
        float rx = 0.95f, rz = 1.4f;
        float rw = 0.3f, rh = 0.6f, rd = 0.6f;
        piezaLocal(cX, cZ, ang,  rx, 0.3f, -rz, rw, rh, rd, 0.05f, 0.05f, 0.05f); // Del-Der
        piezaLocal(cX, cZ, ang, -rx, 0.3f, -rz, rw, rh, rd, 0.05f, 0.05f, 0.05f); // Del-Izq
        piezaLocal(cX, cZ, ang,  rx, 0.3f,  rz, rw, rh, rd, 0.05f, 0.05f, 0.05f); // Tras-Der
        piezaLocal(cX, cZ, ang, -rx, 0.3f,  rz, rw, rh, rd, 0.05f, 0.05f, 0.05f); // Tras-Izq
        
        // Faros delanteros (Amarillo brillante)
        piezaLocal(cX, cZ, ang,  0.6f, 0.55f, -2.25f, 0.4f, 0.2f, 0.1f, 1.0f, 1.0f, 0.8f);
        piezaLocal(cX, cZ, ang, -0.6f, 0.55f, -2.25f, 0.4f, 0.2f, 0.1f, 1.0f, 1.0f, 0.8f);
        
        // Luces traseras rojas
        piezaLocal(cX, cZ, ang,  0.7f, 0.6f, 2.25f, 0.3f, 0.15f, 0.1f, 0.9f, 0.1f, 0.1f);
        piezaLocal(cX, cZ, ang, -0.7f, 0.6f, 2.25f, 0.3f, 0.15f, 0.1f, 0.9f, 0.1f, 0.1f);
        
        // Placas / Matrículas
        piezaLocal(cX, cZ, ang, 0, 0.4f, 2.25f, 0.6f, 0.15f, 0.1f, 0.9f, 0.9f, 0.9f);
        piezaLocal(cX, cZ, ang, 0, 0.35f, -2.25f, 0.6f, 0.15f, 0.1f, 0.9f, 0.9f, 0.9f);
    }

    private void dibujarDestino() {
        float x = DESTINOS[entregas][0], z = DESTINOS[entregas][1];
        entero("uEmision", 1);
        caja(x, 0.06f, z, 5, 0.08f, 5, 1, 0.72f, 0.12f);
        if (!vistaMapa) cajaGirada(x, 3.5f + (float) Math.sin(tiempo * 2) * 0.3f, z, 0.8f, 0.8f, 0.8f, 1, 0.8f, 0.15f, tiempo);
        entero("uEmision", 0);
    }

    private void decorarCiudad() {
        for (int f = 0; f < MAPA.length; f++) {
            for (int c = 0; c < MAPA[f].length; c++) {
                float x = centro(c), z = centro(f);
                int tipo = MAPA[f][c];
                if (tipo == 2) dibujarParque(x, z);
                if (tipo == 1) dibujarVentanas(x, z, 5 + (f * 3 + c * 7) % 9);
                if (tipo != 0) {
                    dibujarPasoPeatonal(x, z);
                    dibujarSemaforo(x + 4, z - 4);
                    dibujarLetreroAvenida(x - 4.5f, z + 4.5f);
                }
            }
        }
    }

    private void dibujarParque(float x, float z) {
        for (float dx : new float[]{-2.5f, 2.5f}) {
            for (float dz : new float[]{-2.5f, 2.5f}) {
                caja(x + dx, 1.2f, z + dz, 0.35f, 2, 0.35f, 0.38f, 0.22f, 0.12f);
                caja(x + dx, 2.7f, z + dz, 2, 2.3f, 2, 0.12f, 0.42f, 0.23f);
            }
        }
        caja(x, 0.65f, z, 3, 0.25f, 0.8f, 0.55f, 0.30f, 0.13f);
        caja(x, 0.4f, z, 2, 0.6f, 0.35f, 0.22f, 0.24f, 0.24f);
        caja(x, 1, z + 0.35f, 3, 0.7f, 0.15f, 0.55f, 0.30f, 0.13f);
    }

    private void dibujarVentanas(float x, float z, float altura) {
        if (noche) entero("uEmision", 1);
        for (float y = 1.7f; y < altura; y += 2) {
            for (float d = -2; d <= 2; d += 2) {
                caja(x + d, y, z - 3.51f, 0.8f, 0.9f, 0.04f, 0.95f, 0.75f, 0.38f);
                caja(x + d, y, z + 3.51f, 0.8f, 0.9f, 0.04f, 0.95f, 0.75f, 0.38f);
                caja(x - 3.51f, y, z + d, 0.04f, 0.9f, 0.8f, 0.95f, 0.75f, 0.38f);
                caja(x + 3.51f, y, z + d, 0.04f, 0.9f, 0.8f, 0.95f, 0.75f, 0.38f);
            }
        }
        entero("uEmision", 0);
        // Mejora visual 3D: Añadir un pequeño borde sobresaliente en el techo de cada edificio
        caja(x, altura + 0.1f, z, 4.2f, 0.2f, 4.2f, 0.15f, 0.15f, 0.18f);
    }

    private void dibujarPasoPeatonal(float x, float z) {
        for (int d = -3; d <= 3; d++) caja(x + d, 0.045f, z - 8, 0.45f, 0.04f, 2, 0.85f, 0.87f, 0.83f); 
    }

    private void dibujarSemaforo(float x, float z) {
        caja(x, 1.7f, z, 0.18f, 2.8f, 0.18f, 0.18f, 0.20f, 0.22f);
        caja(x, 3.1f, z, 0.55f, 1.2f, 0.45f, 0.08f, 0.10f, 0.12f);
        int fase = (int) (tiempo % 12); 
        for (int indice = 0; indice < 3; indice++) { 
            boolean enc = (indice == 0) ? fase < 5 : (indice == 1) ? fase >= 10 : fase >= 5 && fase < 10;
            float brillo = 0.15f; entero("uEmision", 0);
            if (enc) { brillo = 1; entero("uEmision", 1); }
            caja(x, 3.45f - indice * 0.35f, z - 0.24f, 0.28f, 0.25f, 0.06f, indice < 2 ? brillo : 0, indice > 0 ? brillo : 0, 0.02f);
        }
        entero("uEmision", 0);
    }

    private void dibujarLetreroAvenida(float x, float z) {
        caja(x, 1.2f, z, 0.08f, 2.4f, 0.08f, 0.3f, 0.3f, 0.3f); // Poste gris cilíndrico
        caja(x, 2.2f, z, 1.2f, 0.4f, 0.04f, 0.1f, 0.4f, 0.2f); // Placa verde (Avenida Santa Cruz)
        
        // Simulación de texto (Líneas blancas low-poly dentro del letrero)
        entero("uEmision", 1);
        caja(x, 2.26f, z + 0.03f, 0.9f, 0.05f, 0.02f, 1.0f, 1.0f, 1.0f);
        caja(x, 2.14f, z + 0.03f, 0.6f, 0.04f, 0.02f, 1.0f, 1.0f, 1.0f);
        entero("uEmision", 0);
    }

    // ==================== 6. ORDEN DE RENDERIZADO ====================
    @Override
    protected void dibujarFrame() {
        super.dibujarFrame(); // 1. Pase principal 3D
        
        // 2. Pase del Minimapa (sólo si estamos jugando)
        if (mostrarMapa && (estadoActual == EstadoJuego.JUGANDO || estadoActual == EstadoJuego.PAUSA)) {
            int dim = Math.min(ancho, alto), lado = Math.min(260, dim / 3), margen = Math.min(18, dim / 20);
            int x = ancho - lado - margen, y = alto - lado - margen; 
            
            glEnable(GL_SCISSOR_TEST); 
            glScissor(x - 3, y - 3, lado + 6, lado + 6); glClearColor(0.8f, 0.87f, 0.94f, 1); glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); 
            glScissor(x, y, lado, lado); glClearColor(0.06f, 0.10f, 0.15f, 1); glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); 
            glViewport(x, y, lado, lado); entero("uMapa", 1); vistaMapa = true; 
            
            try { escena(); dibujarIndicadorAuto(); } 
            finally { 
                vistaMapa = false; entero("uMapa", 0); glDisable(GL_SCISSOR_TEST); glViewport(0, 0, ancho, alto); 
            }
        }
        
        // 3. Pase UI Superpuesta
        renderizarUI();
        
        if (glGetError() != GL_NO_ERROR) throw new IllegalStateException("Error de OpenGL"); 
    }

    private void dibujarIndicadorAuto() {
        cajaGirada(autoX, 25, autoZ, 2.2f, 0.1f, 3.2f, 0.1f, 1, 1, angulo);
        float fX = -(float) Math.sin(angulo), fZ = -(float) Math.cos(angulo); 
        caja(autoX + fX * 2, 26, autoZ + fZ * 2, 0.9f, 0.1f, 0.9f, 1, 1, 1);
    }

    // ==================== 7. RENDERIZADO DE INTERFAZ 2D ====================
    private void renderizarUI() {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        glUseProgram(shaderUI);
        glBindVertexArray(vaoUI);

        // A. HUD inferior (sólo durante el juego o pausa)
        if (estadoActual == EstadoJuego.JUGANDO || estadoActual == EstadoJuego.PAUSA) {
            String sector = calcularSector(autoX, autoZ);
            BufferedImage imgHUD = GeneradorUI.generarHUD(velocidad, noche, faros, entregas, sector);
            subirTextura(imgHUD, texHUD);
            glBindTexture(GL_TEXTURE_2D, texHUD);
            glViewport(20, 20, 512, 128); 
            glDrawArrays(GL_TRIANGLES, 0, 6);
        }

        // B. Pantallas de Menú (Superposición central completa)
        if (estadoActual != EstadoJuego.JUGANDO) {
            if (menuSucio) {
                BufferedImage imgOverlay = null;
                switch (estadoActual) {
                    case MENU_PRINCIPAL: imgOverlay = GeneradorUI.generarMenuPrincipal(opcionSeleccionada); break;
                    case PAUSA: imgOverlay = GeneradorUI.generarMenuPausa(opcionSeleccionada); break;
                    case VICTORIA: imgOverlay = GeneradorUI.generarVictoria(opcionSeleccionada, (int)tiempo); break;
                    case CONTROLES: imgOverlay = GeneradorUI.generarControles(); break;
                }
                subirTextura(imgOverlay, texOverlay);
                menuSucio = false;
            }
            
            glBindTexture(GL_TEXTURE_2D, texOverlay);
            int size = Math.min(ancho, alto);
            glViewport(ancho / 2 - size / 2, alto / 2 - size / 2, size, size);
            glDrawArrays(GL_TRIANGLES, 0, 6);
        }

        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
        glUseProgram(programa);
    }

    /** Calcula el sector geográfico basado en la cuadrícula de 11x11 (-55 a 55) con nombres de Santa Cruz */
    private String calcularSector(float x, float z) {
        if (Math.abs(x) <= 20 && Math.abs(z) <= 20) return "Centro";
        if (z < -20 && x > 20) return "Equipetrol";
        if (z < -20 && x <= 20) return "Av. Cristo Redentor";
        if (z > 20 && x > 20) return "Av. Virgen de Cotoca";
        if (z > 20 && x <= 20) return "Av. Santos Dumont";
        if (x < -20) return "Av. Grigotá";
        return "Distrito Urbano";
    }

    public static void main(String[] args) {
        new clase4().run();
    }
}
