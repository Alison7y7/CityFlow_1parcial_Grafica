package com.mycompany.app;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 * Este programa hace 3 cosas: 
 * 1. abrir una ventana usando la biblioteca LWJGL,
 * 2. vamos a definir un triangulo y uso de Shaders,
 * 3. vamos a definir en un bucle, dibujar el triangulo en la ventana una y otra vez.
 * 
 *   CONCEPTOS CLAVE:
 * - OpenGL: es un conjunto de funciones para dibujar en la pantalla usando la GPU.
 * - Glfw: es una biblioteca que crea la ventana y maneja el teclado y el raton.
 * - OpenGL: solo dice como dibujar, pero no crea ventanas, por eso necesitamos Glfw.
 * - GPU: Es la tarjeta grafica 
 * - Shader: es un codigo que dice como transformar vertice y que color darle a un pixel.
 *           Se escribe en lenguaje GLSL y se compila en la GPU.
 * - VAO/VBO: son formas de guardar en la GPU los datos de los vertices
 * 
 *
 */
public class App 
{
    // Referencia a nuestra ventana
    private long window;

    // Nuestro programa de shaders: es el vertex shader + fragment shader unidos.
    // La GPU Usa este programa para dibujar en cada frame.
    private int program;

    // VAO: Vertex Array Object: es como un dosificador que queda todas las configuraciones
    private int vao;

    // VBO: Vertex Buffer Object: es un bloque memoria en la GPU donde guardamos 
    // las posiciones de los vertices
    private int vbo;

    public static final int ANCHO = 800;
    public static final int ALTO = 600;

    /*  
    
     * Punto de entrada: inicializamos todo, 
                         ejecutamos el bucle del dibujo y al salir limpiamos la memoria.
    */
    public void run() {
        init();
        Loop(); 
        cleanup();   
    }

    /* 
      PASO 1: Inicializar GLFW y crear la ventana.
      GLFW(Graphics Library Framework) es una biblioteca que:
      - crea y gestiona la ventana del sistema 
      - Recibe igual eventos, teclado, raton, ventana.
    */

    public void init() {
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("No se pudo inicializar GLFW");
        }

        // Restablece todas las opciones por defecto 
        GLFW.glfwDefaultWindowHints();


        // Ocultar la ventan al principio, la vamos a mostrar recien cuando OPENGL este listo.
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);

        // Permitir que el usuario pueda redimensionar la pantalla
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

        // Queremos que use la version 3.3 de OpenGL
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);

        // Solo funciones modernas de OpenGL, para evitar compatibilidad con codigos muy antiguos.
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);

        // Para la MacOs hay que reactivar para usar core profile
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
        

        // Cremos la ventana: ancho, alto, titulo, monitor (0 para ventana), ventana compartida (0 para no compartir)
        window = GLFW.glfwCreateWindow(ANCHO, ALTO,"OpenGL-Triangulo", 0, 0);
        
        // Decir que todas las ordenes de OpenGL que ejecutamos a apartir de ahora.
        // Se aplique a esta ventana.
        GLFW.glfwMakeContextCurrent(window);

        // Sincronizar el dibujo con el monitor (1 = esperar el siguiente refresco)
        // Evitamos el tearing, (imagen partida en dos)
        GLFW.glfwSwapInterval(1); 

        // Hacer visible la ventana
        GLFW.glfwShowWindow(window);
        
        // Carga en Java las funciones de OpenGl
        // LWJGL necesita este paso para enlazar las llamadas de Java en la GPU.
        GL.createCapabilities(); 
        
         crearShader();
         crearTriangulo();

        }  
        
        private void crearShader() {
        }
        
        private void crearTriangulo() {
        }

        // BUCLE PRINCIPAL: en cada iteracion dibujamos un frame.
        // Mientras la ventana no se cierre:
        // 1. Limpiamos la ventana.
        // 2. Decimos usar este programa de shaders y este VAO para dibujar
        // 3. Ordenamos dibujar los 3 vertices.
        // 4. Intercambiamos los buffers.
        // 5. Procesamos los eventos.
        private void Loop() {
            while (!GLFW.glfwWindowShouldClose(window)) {
                // Color con el que vamos a limpiar la pantalla(Gris oscuro: RGBA)
               GL11.glClearColor(0.08f, 0.08f, 0.12f, 1.0f);
               // Para limpiar el buffer de color(rellenar con el color anterior)
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
                
                // Activar nuestro programa de shaders
                GL20.glUseProgram(program);

                // Activar el VAO que contiene el triangulo
                GL30.glBindVertexArray(vao);
                // Orden del dibujo: modo, empezar con el vertice 0, y dibujar 3 vertices.
                GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);

                // Doble buffer: hemos dibujado en un buffer oculto y ahora lo mostramos en pantalla e intercambiamos los buffers.
                GLFW.glfwSwapBuffers(window);
                
                // Procesamos eventos del sistema(si el usuario cierra ventana, si hizo click, etc)
                GLFW.glfwPollEvents();
            }

        }

        private void cleanup() {
            GL30.glDeleteVertexArrays(vao);
            GL15.glDeleteBuffers(vbo);
            GL20.glDeleteProgram(program);
            GLFW.glfwDestroyWindow(window);
            GLFW.glfwTerminate();
           
        }


    public static void main( String[] args )
    {
        new App().run();
    }
}
