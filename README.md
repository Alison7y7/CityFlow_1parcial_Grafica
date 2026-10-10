# CITYFLOW — Simulador de Conducción Urbana 3D

## Descripción del proyecto

CityFlow es un simulador de conducción 3D interactivo desarrollado como proyecto de evaluación universitaria. El objetivo principal es explorar una ciudad generada proceduralmente y completar tres entregas (representadas por balizas doradas) estacionando el vehículo en los puntos de destino en el menor tiempo posible. 

El proyecto destaca por su estética low-poly, su ciclo de día/noche con iluminación en tiempo real, inteligencia artificial básica para el tráfico urbano, y un sistema de interfaz propio (HUD) completamente adaptado sin el uso de pesadas librerías gráficas de terceros.

## Tecnologías utilizadas

El desarrollo se apoya puramente en herramientas y bibliotecas de bajo nivel, demostrando un entendimiento sólido de los gráficos por computadora:

- **Java 17**: Lenguaje principal de programación.
- **Maven 3.13**: Gestor de dependencias y construcción del proyecto.
- **LWJGL 3.3.3** *(Lightweight Java Game Library)*: Binding utilizado para comunicarse con los periféricos y la tarjeta gráfica.
- **OpenGL 3.3**: API gráfica base para la renderización tridimensional.
- **GLSL** *(OpenGL Shading Language)*: Utilizado para programar de cero los Vertex Shaders y Fragment Shaders encargados de la geometría, iluminación difusa, emisión y texturizado 2D superpuesto.
- **Java AWT (`Graphics2D`)**: Empleado para la generación limpia de tipografías y paneles de interfaz, exportados como texturas dinámicas a OpenGL.

## Diseño de la ciudad

La ciudad es un entorno urbano ficticio, pero ambientado y sectorizado inspirándose en nombres reconocidos de **Santa Cruz de la Sierra** (Bolivia).

- **Dimensiones:** Mapa cuadriculado de 11 × 11 celdas (originalmente 7x7).
- **Escala:** Cada celda mide 10 unidades, resultando en un suelo transitable total de 110x110 unidades.
- **Geometría urbana:** El mapa cuenta con 19 rascacielos/edificios de alturas aleatorizadas con techos modelados y 6 parques arbolados.
- **Sectores:** Dividido geográficamente en 6 sectores: *Centro, Av. Cristo Redentor, Equipetrol, Av. Santos Dumont, Av. Virgen de Cotoca y Av. Grigotá*.
- **Colisiones:** Sistema de físicas basado en detección circular de intersección (teorema de Pitágoras adaptado al cuadrado de las distancias) contra las manzanas rígidas, garantizando rebotes sin cálculos costosos de raíz cuadrada.

## Funcionalidades

- **Conducción Arcade:** Aceleración suave con inercia, frenado, y giro angular dependiente de las leyes de movimiento uniformemente acelerado.
- **Cámaras Dinámicas:** Alternancia en tiempo real entre vista aérea y cámara de seguimiento en tercera persona.
- **Ciclo Día/Noche:** Cambio de iluminación ambiental y direccional. Activación automática de ventanas emisivas en la noche.
- **Sistema de Iluminación Físico:** Implementación de cálculo de Luz Difusa (dot product) en Shaders. Nueve farolas que decrecen por distancia y Faros delanteros dinámicos en el auto del jugador.
- **Decoración Urbana:** Árboles, bancos, letreros de avenidas en 3D (estilo placa vial verde) y pasos peatonales.
- **Semáforos:** Sistema de luces (rojo, amarillo, verde) animado cíclicamente basado en el `deltaTime`.
- **Tráfico Autónomo:** Tres vehículos de IA independientes circulando perpetuamente por rutas de coordenadas (waypoints).
- **Minimapa en Tiempo Real (PiP):** Visión cenital en la esquina superior derecha utilizando el estado *glScissor* y un segundo pase completo de renderizado.
- **Máquina de Estados:** Videojuego implementado con estado de Menú Principal (con cámara cinemática), Pausa (donde el tiempo se congela), Controles, Partida Activa y Pantalla de Victoria.
- **Interfaz (HUD):** Texturas de alta calidad que indican la velocidad (con barra progresiva), luces, entorno, contador de entregas y el sector geográfico actual en tiempo real sin entorpecer el escenario 3D.

## Controles

### Menús e Interfaz
| Tecla | Acción |
|---|---|
| **W / S** o **Flechas** | Navegar arriba y abajo por las opciones del menú |
| **ENTER / ESPACIO** | Confirmar selección |
| **ESC / P** | Pausar el juego o volver al estado anterior |
| **H** | Mostrar / Ocultar pantalla de Controles (Ayuda) |
| **M** | Mostrar / Ocultar el Minimapa de la esquina superior |

### Durante la Partida (Conducción)
| Tecla | Acción |
|---|---|
| **W / Flecha Arriba** | Acelerar el vehículo hacia adelante |
| **S / Flecha Abajo** | Marcha atrás (retroceder) |
| **A / D / Izq-Der** | Girar el vehículo a izquierda y derecha |
| **ESPACIO** | Frenar de golpe |
| **C** | Alternar vista general / seguimiento trasero |
| **N** | Alternar entorno (Día / Noche) |
| **F** | Encender / Apagar faros delanteros del auto |
| **R** | Reiniciar la partida y el contador de tiempo a cero |

## Requisitos

- **Java JDK 17** o superior correctamente instalado y configurado en el `PATH`.
- **Apache Maven 3.6+**.
- **Hardware:** Computadora con tarjeta gráfica compatible con OpenGL 3.3 (Cualquier GPU moderna integrada o dedicada soportada).
- No se requiere instalar librerías C++ nativas adicionales, ya que Maven y LWJGL se encargarán de descargar automáticamente las librerías correctas (Windows, macOS o Linux) al compilar.

## Instalación y ejecución

1. Descomprime o clona el proyecto en tu máquina.
2. Abre una terminal (o consola de comandos) y navega hasta el directorio raíz donde se ubica el archivo `pom.xml`.
3. Para compilar y ejecutar el simulador directamente, introduce:
   ```bash
   mvn compile exec:exec "-DmainClass=com.graphics.clase4"
   ```
4. Para ejecutar la batería de pruebas de la arquitectura sin abrir la ventana gráfica, utiliza:
   ```bash
   mvn clean test
   ```

## Estructura del proyecto

El proyecto ha sido construido por etapas (clases), manteniendo el historial educativo, pero `clase4.java` es el motor definitivo:

- `pom.xml` - Dependencias y configuración de construcción.
- `clase1.java` - Arquitectura base de ventana GLFW, ciclo de vida (game loop), inputs básicos de teclado, compilación de shaders OpenGL.
- `clase2.java` - Geometría, creación de colisiones (`puedeCircular()`), movimiento de entidades en `deltaTime`, matriz de transformación y diseño primario del vehículo.
- `clase3.java` - Implementación puramente gráfica de Iluminación. Noche/Día, envío de uniformes, manejo de normales en el Buffer de Vértices.
- `clase4.java` - (MAIN ENTRY POINT) La clase definitiva. Herencia de todo lo anterior, incluye Máquina de Estados, HUD, tráfico de IA, renderizado del minimapa y el ciclo final del juego.
- `VehiculoAutonomo.java` - Lógica de ruteo e interpolación lineal (waypoints) para los 3 coches manejados por la computadora.
- `GeneradorUI.java` - Proveedor estático de texturas. Renderiza paneles, textos y botones en `Graphics2D` para subirlos a la Tarjeta de Video.
- `ConduccionTest.java` - Conjunto de pruebas unitarias basadas en JUnit3 para corroborar las colisiones y la lógica del contador de tiempo/entregas.

## Limitaciones conocidas

- **Ciudad ficticia:** A pesar del uso de los nombres inspirados en avenidas reales de Santa Cruz, las calles, el trazado y las ubicaciones no representan el plano urbano fidedigno del lugar.
- **Tráfico guiado:** Los vehículos autónomos siguen rutas estrictas mediante puntos estáticos (waypoints). No poseen inteligencia artificial para esquivar colisiones con el jugador (el coche del jugador sí colisionará con las aceras, pero atravesará los modelos de la IA y viceversa).
- **Semáforos decorativos:** El ciclo de luces de semáforos corre mediante la acumulación de segundos del reloj del juego, pero sirve estrictamente como decoración urbana; los coches de la inteligencia artificial no respetarán las luces rojas.

## Recursos externos y créditos

CityFlow se ha desarrollado sobre la arquitectura base inicial del proyecto **CiudadConduccion**, proporcionado de manera educativa por el docente universitario del curso. 

**Las contribuciones originales abarcan (Fase 1 a 5):**
1. Ampliación del motor geométrico para un mapa 11x11.
2. Expansión lógica del cálculo de luz para integrar iluminación direccional y focal estricta (Faros).
3. Motor independiente HUD sin dependencias externas mediante shaders 2D.
4. Generación y rediseño de coches Low Poly en código.
5. Inclusión total de la Máquina de Estados.

No se utilizaron modelos `.obj`, fuentes ajenas de pago ni bibliotecas externas fuera de las proporcionadas nativamente por LWJGL 3 (Core, GLFW, OpenGL) y JUnit.
