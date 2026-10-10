package com.graphics;

/**
 * Representa un vehículo independiente que recorre una ruta de puntos, ahora con
 * detección de obstáculos (vehículo del jugador y otros AIs) para frenar y evitar traspasarlos.
 */
public class VehiculoAutonomo {
    public float x, z; // Posición actual en el mundo.
    public float angulo; // Orientación del capó.
    public float velocidadActual; // Velocidad con frenado dinámico.
    public float velocidadMaxima; // Velocidad límite de crucero.
    public float r, g, b; // Color de la carrocería.
    
    private float[] rutaX; // Lista de coordenadas X que debe visitar.
    private float[] rutaZ; // Lista de coordenadas Z correspondientes.
    private int objetivoActual = 1; // Índice del punto hacia donde se dirige.

    public VehiculoAutonomo(float[] rutaX, float[] rutaZ, float r, float g, float b, float velMaxima) {
        this.rutaX = rutaX;
        this.rutaZ = rutaZ;
        this.r = r;
        this.g = g;
        this.b = b;
        this.velocidadMaxima = velMaxima;
        this.velocidadActual = velMaxima;
        this.x = rutaX[0];
        this.z = rutaZ[0];
        orientarHaciaObjetivo();
    }

    /** Mueve el vehículo hacia su objetivo usando deltaTime y frenando ante obstáculos. */
    public void actualizar(float deltaTime, float jugadorX, float jugadorZ, VehiculoAutonomo[] trafico) {
        // --- DETECCIÓN DE OBSTÁCULOS Y FRENADO ---
        boolean obstaculo = false;
        
        // 1. Revisar si el jugador está delante
        if (esObstaculo(jugadorX, jugadorZ)) {
            obstaculo = true;
        }
        
        // 2. Revisar si otro vehículo autónomo está delante
        if (!obstaculo && trafico != null) {
            for (VehiculoAutonomo otro : trafico) {
                if (otro != this && esObstaculo(otro.x, otro.z)) {
                    obstaculo = true;
                    break;
                }
            }
        }
        
        // --- ACELERACIÓN / DESACELERACIÓN ---
        if (obstaculo) {
            // Frenado progresivo pero rápido
            velocidadActual = Math.max(0, velocidadActual - 15.0f * deltaTime); 
        } else {
            // Recuperar velocidad de crucero
            velocidadActual = Math.min(velocidadMaxima, velocidadActual + 5.0f * deltaTime); 
        }
        
        // --- MOVIMIENTO ---
        if (velocidadActual <= 0) return; // Detenido por completo, no avanzar.

        float destinoX = rutaX[objetivoActual];
        float destinoZ = rutaZ[objetivoActual];
        
        float distanciaX = destinoX - x;
        float distanciaZ = destinoZ - z;
        float distanciaFaltante = (float) Math.sqrt(distanciaX * distanciaX + distanciaZ * distanciaZ);
        
        float distanciaAvance = velocidadActual * deltaTime; // Espacio = v * t
        
        if (distanciaAvance >= distanciaFaltante) {
            x = destinoX;
            z = destinoZ;
            objetivoActual++;
            if (objetivoActual >= rutaX.length) {
                objetivoActual = 0; // Repetir la ruta
            }
            orientarHaciaObjetivo();
        } else {
            float frenteX = -(float) Math.sin(angulo);
            float frenteZ = -(float) Math.cos(angulo);
            x += frenteX * distanciaAvance;
            z += frenteZ * distanciaAvance;
        }
    }

    /** Comprueba si una coordenada representa un vehículo peligroso justo enfrente. */
    private boolean esObstaculo(float ox, float oz) {
        float dx = ox - this.x;
        float dz = oz - this.z;
        float distSq = dx * dx + dz * dz;
        
        // Si está a más de 16 unidades, ignorarlo.
        if (distSq > 256.0f) return false;
        
        // Si está MUY cerca (contacto inminente, radio cuadrado = 16), frenar sí o sí por seguridad.
        if (distSq < 16.0f) return true;
        
        // Determinar si el obstáculo está "delante" mediante el producto escalar (dot product).
        float frenteX = -(float) Math.sin(this.angulo);
        float frenteZ = -(float) Math.cos(this.angulo);
        
        float dist = (float) Math.sqrt(distSq);
        float dirHaciaObstaculoX = dx / dist;
        float dirHaciaObstaculoZ = dz / dist;
        
        float dotProduct = frenteX * dirHaciaObstaculoX + frenteZ * dirHaciaObstaculoZ;
        
        // Si dotProduct es > 0.85, significa que está en un cono cerrado delante de nuestro capó.
        return dotProduct > 0.85f;
    }

    /** Calcula el ángulo necesario para mirar hacia el siguiente punto. */
    private void orientarHaciaObjetivo() {
        float destinoX = rutaX[objetivoActual];
        float destinoZ = rutaZ[objetivoActual];
        float dx = destinoX - x;
        float dz = destinoZ - z;
        angulo = (float) Math.atan2(-dx, -dz);
    }
}
