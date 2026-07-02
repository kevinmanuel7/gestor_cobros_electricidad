# Usamos Java 17, que es la versión de tu proyecto
FROM eclipse-temurin:17-jdk

# Le decimos que trabaje en esta carpeta dentro de Docker
WORKDIR /app

# Copiamos el archivo .jar que fabrica NetBeans hacia adentro de Docker
COPY target/*.jar app.jar

# El comando que ejecutará tu programa
ENTRYPOINT ["java", "-jar", "app.jar"]