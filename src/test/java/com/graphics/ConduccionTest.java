package com.graphics;

import junit.framework.TestCase;

public class ConduccionTest extends TestCase {

    public void testCallesYDestinosTransitables() {
        clase2 juego = new clase2();
        for (float z = -50; z <= 50; z += 0.25f) { 
            assertTrue(juego.puedeCircular(-50, z));
        }
        for (float x = -50; x <= 50; x += 0.25f) { 
            assertTrue(juego.puedeCircular(x, -50));
        }
        assertTrue(juego.puedeCircular(-10, -10));
        assertFalse(juego.puedeCircular(0, 0));
    }

    public void testObstaculosYLimites() {
        clase2 juego = new clase2();
        assertFalse(juego.puedeCircular(-40, -40));
        assertFalse(juego.puedeCircular(0, -40));
        assertFalse(juego.puedeCircular(-45.5f, -40));
        assertFalse(juego.puedeCircular(54, 0));
        assertFalse(juego.puedeCircular(0, -56));
        assertTrue(juego.puedeCircular(-47, -40));
    }

    public void testReinicio() {
        clase4 juego = new clase4();
        juego.autoX = 12;
        juego.autoZ = 4;
        juego.velocidad = 10;
        juego.angulo = 2;
        juego.tiempo = 34.5f;
        juego.entregas = 2;
        
        juego.reiniciar();
        
        assertEquals(-50f, juego.autoX, 0f);
        assertEquals(50f, juego.autoZ, 0f);
        assertEquals(0f, juego.velocidad, 0f);
        assertEquals(0f, juego.angulo, 0f);
        assertEquals(0, juego.entregas);
        assertEquals(0f, juego.tiempo, 0f);
    }

    public void testLogicaSemaforoYEntregas() {
        clase4 juego = new clase4();
        juego.tiempo = 2.0f;
        int faseRoja = (int) (juego.tiempo % 12);
        assertTrue(faseRoja < 5);

        juego.tiempo = 7.0f;
        int faseVerde = (int) (juego.tiempo % 12);
        assertTrue(faseVerde >= 5 && faseVerde < 10);

        juego.tiempo = 11.0f;
        int faseAmarilla = (int) (juego.tiempo % 12);
        assertTrue(faseAmarilla >= 10);
    }

    public void testTraficoAutonomo() {
        float[] rutaX = {-10, 10};
        float[] rutaZ = {0, 0};
        VehiculoAutonomo v = new VehiculoAutonomo(rutaX, rutaZ, 1, 0, 0, 5.0f);
        
        assertEquals(-10f, v.x, 0f);
        assertEquals(0f, v.z, 0f);
        
        // El jugador está lejos (-50, 50), así que no debe frenar.
        v.actualizar(1.0f, -50f, 50f, new VehiculoAutonomo[]{v});
        assertEquals(-5f, v.x, 0.01f);
        assertEquals(0f, v.z, 0.01f);
        
        v.actualizar(3.0f, -50f, 50f, new VehiculoAutonomo[]{v});
        assertEquals(10f, v.x, 0.01f);
    }

    public void testColisionAutonomos() {
        // Un auto va hacia el Este (X positivo)
        VehiculoAutonomo v1 = new VehiculoAutonomo(new float[]{-10, 10}, new float[]{0, 0}, 1, 0, 0, 5.0f);
        // Colocamos al jugador exactamente enfrente, en X=-8, Z=0
        
        // 1 segundo de actualización. Normalmente avanzaría a X=-5, pero como el jugador está en -8, debería frenar.
        v1.actualizar(1.0f, -8f, 0f, new VehiculoAutonomo[]{v1});
        
        // La velocidad debería haber disminuido
        assertTrue("El auto debería frenar ante un obstáculo", v1.velocidadActual < 5.0f);
        
        // Si sigue pasando el tiempo, su velocidad debería llegar a 0
        v1.actualizar(1.0f, -8f, 0f, new VehiculoAutonomo[]{v1});
        assertTrue("El auto debería detenerse", v1.velocidadActual <= 0.0f);
    }

    public void testColisionJugadorContraAutonomo() {
        clase4 juego = new clase4();
        // Inicializar tráfico en clase4
        juego.iniciar(); // Inicia variables, no abre ventana gráfica por suerte? Wait, iniciar() llama glfwInit!
        // No llamamos iniciar(), asignamos tráfico a mano:
        juego.trafico = new VehiculoAutonomo[] {
            new VehiculoAutonomo(new float[]{0, 10}, new float[]{0, 0}, 1, 0, 0, 5.0f)
        };
        // Estado inicial de juego es MENU_PRINCIPAL, pasarlo a JUGANDO
        juego.estadoActual = clase4.EstadoJuego.JUGANDO;
        
        // El jugador choca directamente con el auto en (0,0)
        assertFalse("El jugador no debe atravesar al autónomo", juego.puedeCircular(0f, 0f));
        // Pero si está lejos, sí puede circular
        assertTrue("El jugador debe poder moverse si está lejos", juego.puedeCircular(-50f, 50f));
    }
}
