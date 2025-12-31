# 🏃‍♂️ Fit Application – Tastefull

**Fit Application – Tastefull** es una aplicación móvil Android enfocada en el bienestar físico y nutricional del usuario. Permite llevar un control integral de la actividad física, alimentación, progreso corporal y motivación diaria, además de ofrecer recomendaciones personalizadas de dieta y planes premium.

El proyecto fue desarrollado en **Android Studio**, utilizando **Material Design**, base de datos local y consumo de APIs externas para información nutricional confiable.

## 🚀 Características principales

- Autenticación de usuarios (login y registro)
- Registro de actividad física y horas de sueño
- Control de alimentación y macronutrientes
- Recomendaciones personalizadas de dieta
- Sistema motivacional de rachas
- Planes premium de suscripción
- Diseño moderno y consistente con Material Design

## 🎬 Splash Screen

La aplicación inicia con una pantalla **Splash Screen** que muestra una animación de bienvenida mientras el sistema se prepara para su ejecución.

### Funcionalidades
- Animación tipo *fade* entre dos imágenes centrales
- Transición visual fluida y profesional
- Modo inmersivo (sin barra de estado ni navegación)
- Redirección automática a la pantalla de inicio de sesión

Esta pantalla mejora la experiencia inicial y refuerza la identidad visual de la aplicación.

## 🔐 Inicio de Sesión

Permite al usuario acceder a su cuenta dentro de la aplicación.

### Funciones
- Campo de usuario y contraseña
- Opción para mostrar u ocultar contraseña
- Validación de campos vacíos
- Verificación de credenciales en la base de datos
- Mensajes de error o confirmación
- Enlace a la pantalla de registro

## 📝 Registro de Usuario

Permite crear una nueva cuenta y recopilar información esencial para personalizar la experiencia del usuario.

### Datos solicitados
- Nombre completo  
- Correo electrónico  
- Nombre de usuario  
- Peso inicial  
- Edad  
- Sexo  
- Estatura  
- Contraseña  
- Confirmación de contraseña  

### Validaciones
- Campos obligatorios
- Coincidencia de contraseñas
- Verificación de usuario duplicado

Al finalizar el registro, el usuario es redirigido al inicio de sesión.

## 🏠 Menú Principal

Funciona como el centro de control del usuario.

### Contenido
- Mensaje de bienvenida
- Acceso destacado a Planes Premium
- Tarjetas informativas:
  - Pasos diarios
  - Peso inicial
  - Horas de sueño
- Botón principal **“¡Iniciar Actividad!”**
- Acceso rápido a contacto profesional

### Barra de navegación inferior
- Inicio
- Alimentación
- Dieta
- Progreso
- Rachas
- Perfil
- Cerrar sesión

## 👤 Perfil

Permite visualizar y gestionar la información personal del usuario.

### Características
- Imagen de perfil circular
- Nombre de usuario destacado
- Tarjeta con información personal organizada
- Diseño claro y coherente con la aplicación
  
## 🏋️ Registro de Actividad Física

Permite registrar ejercicios, horas de sueño y ubicación.

### Funciones
- Selección de tipo de actividad e intensidad
- Ingreso de duración y horas de sueño
- Cálculo automático de calorías quemadas
- Registro mediante GPS
- Almacenamiento en base de datos:
  - Actividad
  - Intensidad
  - Duración
  - Calorías
  - Ubicación
  - Sueño

## 🍎 Registro de Alimentos

Permite llevar un control detallado de la ingesta diaria.

### Funcionalidades
- Búsqueda con autocompletado
- Consumo de la API oficial USDA
- Información nutricional por cada 100g:
  - Calorías
  - Proteínas
  - Grasas
  - Carbohidratos
- Registro por gramos consumidos
- Lista diaria editable de alimentos
- Edición y eliminación de registros

## 🥗 Recomendador de Dieta

Ayuda al usuario a definir su objetivo corporal.

### Opciones
- Subir de peso
- Bajar de peso
- Quemar grasa
- Mejorar energía
- Tonificar músculos

El sistema utiliza el peso actual y el peso deseado para generar recomendaciones personalizadas.

## 📋 Dieta Recomendada

Presenta una guía alimenticia personalizada basada en el objetivo seleccionado.

### Incluye
- Tipo de dieta sugerida
- Alimentos recomendados
- Frecuencia y horarios de comida
- Porciones aproximadas
- Consejos complementarios

## 📊 Resumen del Día

Panel nutricional que muestra el progreso diario del usuario en tiempo real.

### Funciones
- Cálculo automático de metas diarias
- Total de calorías y macronutrientes consumidos
- Barras de progreso dinámicas
- Valores claros y comprensibles
- Integración con la navegación principal

## 🔥 Rachas

Sistema motivacional enfocado en la constancia del usuario.

### Funcionamiento
- Botón **“Cumplí mi meta”**
- Registro diario automático
- Incremento o reinicio de la racha
- Indicadores visuales semanales
- Estadísticas acumuladas:
  - Semanas
  - Meses
  - Años

## 💳 Pagos y Planes

Permite acceder a funciones premium mediante suscripción.

### Planes disponibles
- Premium
- Business

### Flujo
1. Selección del plan
2. Visualización del precio
3. Confirmación
4. Redirección al proceso de pago

## 🛠 Tecnologías utilizadas

- Android Studio
- Java / Kotlin
- Material Design
- SQLite
- API USDA
- GPS
- Gradle (Kotlin DSL)

## 📌 Estado del proyecto
 
Aplicación funcional con múltiples módulos  
Enfocada en experiencia de usuario y diseño moderno
