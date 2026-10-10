package com.graphics; // Mantiene esta lección junto a las clases anteriores.

import static org.lwjgl.glfw.GLFW.*; // Incluye las constantes de las teclas N y F.

/**
 * CLASE 3: ILUMINACIÓN DE LA CIUDAD.
 * Conserva ciudad y conducción; añade sol, farolas y focos del vehículo.
 * El shader calcula iluminación local: este ejemplo todavía no proyecta sombras.
 */
public class clase3 extends clase2 {

    // ==================== 1. ESTADO Y POSICIONES DE LAS LUCES ====================
    // Esta variable indica si estamos en modo noche.
    // Cuando presionamos N, cambiamos su valor.
    // Así podemos usar diferentes intensidades de luz para el día y para la noche.
    protected boolean noche = true; // Inicia la escena con iluminación nocturna.

    // Esta variable indica si los faros delanteros del auto están encendidos.
    // Se alterna presionando la tecla F.
    protected boolean faros = true; // Inicia los focos del auto encendidos.

    // Guardamos las posiciones de las farolas de la ciudad.
    // Cada farola tiene una coordenada X, Y (altura) y Z.
    // Estas posiciones corresponden al mapa ampliado de 11x11 y caen sobre aceras.
    protected static final float[][] LUCES = { 
        {-44, 4.5f, -44}, // Farola en la zona Noroeste.
        {-4, 4.5f, -44},  // Farola en la zona Norte central.
        {36, 4.5f, -44},  // Farola en la zona Noreste.
        {-44, 4.5f, -4},  // Farola en la zona Oeste central.
        {-4, 4.5f, -4},   // Farola en el Centro de la ciudad.
        {36, 4.5f, -4},   // Farola en la zona Este central.
        {-44, 4.5f, 36},  // Farola en la zona Suroeste.
        {-4, 4.5f, 36},   // Farola en la zona Sur central.
        {36, 4.5f, 36}    // Farola en la zona Sureste.
    };

    // ==================== 2. CONTROLES E INDICADORES ====================

    /** Añade los interruptores de iluminación a los controles existentes. */
    @Override // Amplía el método de teclado de clase2.
    protected void tecla(int key) {
        super.tecla(key); // Conserva ESC, C y R.
        if (key == GLFW_KEY_N) { // Comprueba la tecla de cambio de ambiente.
            noche = !noche; // Alterna entre noche y día.
        }
        if (key == GLFW_KEY_F) { // Comprueba el interruptor de los focos del auto.
            faros = !faros; // Enciende los faros si estaban apagados y viceversa.
        }
    }

    /** Prepara el texto que clase2 incorpora al título de la ventana. */
    @Override // Añade información al espacio reservado para indicadores.
    protected String estadoExtra() {
        String ambiente = "dia"; // Usa día como texto inicial.
        if (noche) { // Comprueba si está seleccionado el ambiente nocturno.
            ambiente = "noche"; // Reemplaza el texto por el estado real.
        }
        String estadoFaros = "OFF"; // Usa apagado como texto inicial de los focos.
        if (faros) { // Comprueba si las luces del auto están activas.
            estadoFaros = "ON"; // Indica que los faros están encendidos.
        }
        return " | N: " + ambiente + " | F: faros " + estadoFaros; // Devuelve ambos indicadores y sus teclas.
    }

    // ==================== 3. DATOS QUE RECIBE EL SHADER ====================

    /** Actualiza los uniforms de iluminación antes de dibujar la ciudad. */
    @Override // Sustituye el método vacío de clase1.
    protected void prepararLuces() {
        entero("uNoche", 0); // Inicialmente configura iluminación diurna.
        if (noche) { // Revisa el modo elegido por el usuario.
            entero("uNoche", 1); // Comunica al shader que debe usar iluminación nocturna.
        }
        entero("uFaros", 0); // Inicialmente desactiva los conos de luz del auto.
        if (faros) { // Revisa el interruptor de los faros.
            entero("uFaros", 1); // Activa el cálculo de los dos focos en el shader.
        }
        entero("uEmision", 0); // Hace que los objetos normales reciban iluminación.

        for (int indice = 0; indice < LUCES.length; indice++) { // Recorre las nueve bombillas.
            float x = LUCES[indice][0]; // Lee la coordenada horizontal de la bombilla.
            float y = LUCES[indice][1]; // Lee su altura sobre el suelo.
            float z = LUCES[indice][2]; // Lee su coordenada en profundidad.
            vector("uLuces[" + indice + "]", x, y, z); // Envía esa posición al arreglo GLSL.
        }

        float frenteX = -(float) Math.sin(angulo); // Calcula hacia dónde apunta el auto en X.
        float frenteZ = -(float) Math.cos(angulo); // Calcula hacia dónde apunta el auto en Z.
        vector("uAuto", autoX, 0.7f, autoZ); // Envía el centro del vehículo a la altura de sus focos.
        vector("uFrente", frenteX, 0, frenteZ); // Envía la dirección frontal del vehículo.
    }

    // ==================== 4. MODELOS DE LAS FAROLAS ====================

    /** Dibuja postes y bombillas en las mismas posiciones usadas para calcular la luz. */
    @Override // Añade geometría a la ciudad y al auto heredados.
    protected void escena() {
        super.escena(); // Dibuja la ciudad y el vehículo de las primeras dos lecciones.
        for (float[] luz : LUCES) { // Selecciona una farola a la vez.
            float x = luz[0]; // Lee la posición horizontal del poste.
            float y = luz[1]; // Lee la altura de la bombilla.
            float z = luz[2]; // Lee la posición del poste en profundidad.
            caja(x, 2.2f, z, 0.18f, 4.4f, 0.18f, 0.20f, 0.24f, 0.28f); // Dibuja el poste delgado y oscuro.
            if (noche) { // Las bombillas aparentan estar encendidas únicamente de noche.
                entero("uEmision", 1); // Evita que la bombilla sea oscurecida por la iluminación.
            }
            caja(x, y, z, 0.7f, 0.35f, 0.7f, 1, 0.83f, 0.42f); // Dibuja la bombilla de color cálido.
            entero("uEmision", 0); // Restablece el material normal para el siguiente objeto.
        }
    }

    // ==================== 5. CÁLCULO DE LUZ EN LA GPU ====================

    /** Devuelve el programa GLSL que calcula el color iluminado de cada fragmento. */
    @Override // Reemplaza el shader de color plano de clase1.
    protected String fragmentShader() {
        return """
            #version 330 core // Selecciona la versión GLSL correspondiente a OpenGL 3.3.
            in vec3 vMundo; // Recibe la posición del fragmento en la ciudad.
            in vec3 vNormal; // Recibe la dirección perpendicular a la superficie.
            uniform vec3 uColor; // Recibe el color base de la caja.
            uniform vec3 uLuces[9]; // Recibe las posiciones de las nueve farolas.
            uniform vec3 uAuto; // Recibe la posición del auto a la altura de los faros.
            uniform vec3 uFrente; // Recibe la dirección hacia la que apunta el vehículo.
            uniform int uNoche; // Vale 1 de noche y 0 de día.
            uniform int uFaros; // Vale 1 cuando los focos están encendidos.
            uniform int uEmision; // Vale 1 si el objeto debe conservar su color sin oscurecerse.
            uniform int uMapa; // Vale 1 durante el dibujo del minimapa de clase4.
            out vec4 color; // Entrega el color RGBA final al framebuffer.

            void main() { // Se ejecuta para cada fragmento visible de una caja.
                // ==================== EMISIÓN VISUAL VS ENTORNO ====================
                // Una cosa es que un objeto (como una bombilla o el minimapa) se pinte con color brillante (emisión visual).
                // Otra muy distinta es calcular cómo esa luz viaja y afecta a los objetos cercanos (iluminación del entorno).
                // Si uEmision es 1, solo pintamos el objeto y evitamos oscurecerlo. Esto no ilumina el suelo.
                if (uEmision == 1 || uMapa == 1) { 
                    color = vec4(uColor, 1.0); // Conserva el color base con opacidad completa.
                    return; // Termina el shader sin calcular luces adicionales.
                }

                // ==================== NORMALES E ILUMINACIÓN DIFUSA ====================
                // La normal es un vector matemático perpendicular a la superficie. 
                // Indica hacia dónde "mira" la pared o el suelo.
                vec3 normal = normalize(vNormal); // Nos aseguramos de que el vector normal mida exactamente 1.
                vec3 luz = vec3(0.48); // Define la luz ambiental diurna básica que reciben todas las caras por igual.
                float intensidadSol = 0.65; // Define la fuerza de la luz del sol (luz direccional).
                
                if (uNoche == 1) { // Ajusta el ambiente si es de noche.
                    luz = vec3(0.12, 0.16, 0.24); // Usa una luz ambiental más tenue y azulada.
                    intensidadSol = 0.10; // La luna u otra fuente lejana también aporta algo de luz direccional.
                }
                
                vec3 direccionSol = normalize(vec3(0.4, 1.0, 0.3)); // Dirección fija hacia la fuente de luz lejana.
                // Producto punto (dot): Nos dice qué tan alineadas están la normal y la luz. 
                // Si la cara mira directo al sol, recibe más luz. Si mira al lado contrario, da negativo.
                // max(..., 0.0) evita colores negativos cuando la cara está de espaldas a la luz.
                float incidenciaSol = max(dot(normal, direccionSol), 0.0); // Esto es el cálculo de luz difusa (Lambert).
                luz += vec3(intensidadSol) * incidenciaSol; // Sumamos la luz del sol a la ambiental general.

                // ==================== FAROLAS Y ATENUACIÓN ====================
                if (uNoche == 1) { // Las farolas solo iluminan el entorno de noche.
                    for (int indice = 0; indice < 9; indice++) { // Revisamos el aporte de luz de cada farola al fragmento.
                        vec3 haciaLuz = uLuces[indice] - vMundo; // Vector que va desde este punto del suelo/pared hacia la bombilla.
                        
                        // Calculamos la distancia real entre la luz y el objeto usando la longitud del vector.
                        float distancia = length(haciaLuz); 
                        
                        // Difusa: si la cara mira hacia la farola, recibe más luz.
                        float difusa = max(dot(normal, normalize(haciaLuz)), 0.0); 
                        
                        // Atenuación: La luz pierde fuerza al alejarse de la fuente.
                        // La fórmula divide por (constante + lineal*dist + cuadratica*dist^2)
                        float atenuacion = 1.0 + 0.12 * distancia + 0.045 * distancia * distancia; 
                        
                        vec3 colorFarola = vec3(1.0, 0.73, 0.34); // Tono cálido/amarillento de la farola.
                        // El aporte final depende del color, el ángulo (difusa) y se divide por la atenuación.
                        luz += colorFarola * difusa * 3.0 / atenuacion; 
                    }
                }

                // ==================== FAROS DEL AUTOMÓVIL ====================
                // Los faros son "luces spot" o conos direccionales anclados al coche.
                if (uFaros == 1) { // Solo si la tecla F los tiene encendidos.
                    for (int indice = 0; indice < 2; indice++) { // Calculamos el foco izquierdo y derecho.
                        // El auto se mueve y gira, calculamos el vector "lateral" para separar los faros del centro.
                        vec3 lateral = vec3(-uFrente.z, 0.0, uFrente.x); 
                        float separacion = -0.55; // Faro izquierdo.
                        if (indice == 1) { 
                            separacion = 0.55; // Faro derecho.
                        }
                        
                        // Origen del foco: posición del coche + avance del capó + separación lateral.
                        vec3 origen = uAuto + uFrente * 1.36 + lateral * separacion; 
                        vec3 haciaSuperficie = vMundo - origen; // Vector desde el foco hacia este fragmento del mundo.
                        float distancia = length(haciaSuperficie); 
                        
                        // El cono de luz apunta hacia adelante y levemente hacia abajo (-0.10 en Y).
                        vec3 eje = normalize(uFrente + vec3(0.0, -0.10, 0.0)); 
                        
                        // El "producto punto" entre el rayo y el eje nos dice si estamos dentro del cono principal.
                        float alineacion = dot(normalize(haciaSuperficie), eje); 
                        float cono = smoothstep(0.85, 0.97, alineacion); // Suaviza los bordes del cono de luz.
                        
                        float difusa = max(dot(normal, -normalize(haciaSuperficie)), 0.0); // Iluminación difusa de la superficie.
                        float atenuacion = 1.0 + 0.04 * distancia * distancia; // La luz del faro también se atenúa con la distancia.
                        
                        vec3 colorFaro = vec3(1.0, 0.94, 0.72); // Blanco cálido.
                        luz += colorFaro * cono * difusa * 8.0 / atenuacion; // Sumamos la luz de los faros al total.
                    }
                }

                color = vec4(uColor * luz, 1.0); // El color final es el color del material multiplicado por toda la luz calculada.
            }
            """; // Finaliza el texto GLSL que Java enviará a OpenGL.
    }

    /** Punto de entrada de la tercera lección. */
    public static void main(String[] args) {
        clase3 aplicacion = new clase3(); // Crea la etapa con iluminación.
        aplicacion.run(); // Inicia el ciclo de vida heredado de clase1.
    }
}
