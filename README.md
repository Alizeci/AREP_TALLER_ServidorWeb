## Construcción de un Servidor Web [![Heroku](https://img.icons8.com/color/25/000000/heroku.png)](https://servidor-web-ioc.herokuapp.com/)

Se construye un **servidor web** tipo apache en java, que soporte múlltiples solicitudes seguidas no concurrentes. El servidor cuanta con la capacidad de entregar páginas **html e imágenes PNG**. Así mismo el servidor provee un **framework IoC** base para la construcción de aplicaciones web a partir de **POJOS**. Se construyen dos uno **Game** y otro **Square**.

A continuación de describe el prototipo mínimo entregado, en el que se demuestra **capacidades reflexivas de JAVA** y permite por lo menos cargar un bean **(POJO)** y derivar una aplicación Web a partir de él.

## Entendimiento 🎯
Se puede evidenciar la capacidad del servidor web para gestionar **recursos estáticos** de subtipo: *.html*, *.css*, *.js*, *.jpg*, *.png* para ser leidos por el Usuario. Los *subtipos de los archivos* recibidos de tipo **image** y **text** pueden ser *extendidos*, agregandolos al Map en **MimeTypes.java**.

Ejemplos de recursos accesibles desde el navegador: [.html](https://servidor-web-ioc.herokuapp.com/index.html), [.png](https://servidor-web-ioc.herokuapp.com/check.png), [testImage.html](https://servidor-web-ioc.herokuapp.com/testImage.html) *(Imágen incrustada en html)*

Ejemplos de acceso a componentes del Framework y funcionalidad especificada en la uri: 
+ Usando el **POJO Game**, se muestra en pantalla el nombre de un juego diferente en cada llamado. [/appuser/randomsentence](https://servidor-web-ioc.herokuapp.com/appuser/randomsentence)
+ Usando el **POJO Square**, se obtiene un número quemado en código. [/appuser/square](https://servidor-web-ioc.herokuapp.com/appuser/square)

## Descripción Arquitectura ![Descripción detallada](https://img.icons8.com/windows/32/000000/product-architecture.png)

El **Framework propuesto** está compuesto de:
+ **Meta-Protocolos de Objetos**, Interfaces que brindan acceso al lenguaje.
+ **Reflexión**, API de *java* y de *google* para el acceso a las abstracciones como *Method, Class, Constructor, invocación de métodos*. Así como el cargue de las las clases, con anotación *@Component* al iniciar.
+ **Anotaciones**, Mecanismo de metadata para los programas Java y facilitar la creación del framework basado en *meta-información*. También se hace uso de **Meta-Anotaciones** como *@Retention* y *@Target*.
+ **POJOS**, Game y Square como *componentes*, con métodos de acceso anotados como *servicios* asociados a una uri.

Se desarrolla la arquitectura del servidor de aplicaciones con el **Patrón IoC**, evidenciandose en el uso de las anotaciones.

Finalmente, dada la estructura, se hace extensible la utilización de más de los dos POJOS implementados y/o cualquier otro Objeto bajo la anotación de *@Component*, es decir, el servidor lo cargará al iniciar y prepara para su uso. Así como la generación de más métodos en estos componentes bajo la anotación *@Service* y la uri de identificación.

## Herramientas utilizadas

| Nombre | Uso |
| ------ | ------ |
| **Maven** ![Maven](https://img.icons8.com/ios/25/000000/maven-ios.png) | Gestión y construcción del proyecto |
| **Eclipse IDE** ![Eclipse](https://img.icons8.com/office/25/000000/java-eclipse.png) | Plataforma de desarrollo |
| **JUnit** ![JUnit](https://img.icons8.com/fluency/25/000000/test-partial-passed.png) | Automatización de pruebas unitarias |
| **Git** ![Git](https://img.icons8.com/color/25/000000/git.png) | Sistema de control de versiones |
| **Github** ![Github](https://img.icons8.com/windows/25/000000/github.png) | Respositorio del código fuente |
| **Heroku** [![Heroku](https://img.icons8.com/color/25/000000/heroku.png)](https://servidor-web-ioc.herokuapp.com/) | Plataforma de producción |

## Autor ![Autor](https://img.icons8.com/fluency/30/000000/person-female.png)
Laura Alejandra Izquierdo Castro
