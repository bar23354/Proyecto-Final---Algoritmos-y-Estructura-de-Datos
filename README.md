# Proyecto-Final---Algoritmos-y-Estructura-de-Datos
Algoritmo de recomendación: Date Site

Isabella Obando, 23074
Mia Fuentes, 23775
Roberto Barreda, 23354


Paso 1: Preparar el Entorno de Desarrollo
Instalar Java Development Kit (JDK):

Asegúrate de tener instalado JDK (al menos la versión 8 o superior). Puedes descargarlo desde Oracle o usar OpenJDK.
Instalar un Entorno de Desarrollo Integrado (IDE):
Utiliza un IDE como VsCode para facilitar el desarrollo.

Paso 2: Instalar Neo4j
Descarga Neo4j Community Edition desde Neo4j Download Center e instálalo en tu máquina.

Iniciar Neo4j:
Inicia el servicio de Neo4j y asegúrate de que esté corriendo en bolt://localhost:7687.

Configurar Credenciales:
Asegúrate de que las credenciales de acceso a la base de datos son neo4j y password.

Paso 3: Configurar el Proyecto Java
Crear una Estructura de Proyecto:
Crea un proyecto nuevo en tu IDE y organiza la estructura de archivos según el paquete src.

Agregar Dependencias de Neo4j:
Utiliza Maven o Gradle para gestionar las dependencias.
<dependencies>
    <dependency>
        <groupId>org.neo4j.driver</groupId>
        <artifactId>neo4j-java-driver</artifactId>
        <version>4.3.3</version>
    </dependency>
</dependencies>

Agregar las Clases al Proyecto:
Añade todas las clases proporcionadas (Aplicacion, Category, Interest, Neo4jConnection, y User) dentro del paquete src.

Paso 5: Ejecutar el Programa
Compila y ejecuta la clase Aplicacion. Asegúrate de que el servidor Neo4j esté corriendo y accesible.

Interacción con el Programa:
El programa te permitirá cargar usuarios desde el archivo CSV, crear usuarios, añadir gustos y disgustos, ver conexiones y desconexiones basadas en estos gustos y disgustos, y eliminar usuarios. 

Notas Adicionales
Manejo de Errores: Asegúrate de que el archivo CSV y las credenciales de Neo4j sean correctas. Si algo falla, el programa debería proporcionar mensajes de error útiles.
Los datos subidos por el CSV no son necesarios para interactuar con el database. Algo importante, los nodos creados con el CSV NO PUEDEN SER ELIMINADOS. 
