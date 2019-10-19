# Spring Batch Exercise
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=rafaelbarbiero_spring-batch-exercise&metric=coverage)](https://sonarcloud.io/dashboard?id=rafaelbarbiero_spring-batch-exercise) 
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=rafaelbarbiero_spring-batch-exercise&metric=alert_status)](https://sonarcloud.io/dashboard?id=rafaelbarbiero_spring-batch-exercise)
### Tecnologias
* Java 8
* Maven
* Spring Boot
* Spring Batch
* JUnit5

### Execução local
* **Requisitos**
1. Java 8 
2. Maven 

### **Instruções**
Para construir a aplicação com **maven**:

1. Navegar até o diretório raiz do projeto
2. Executar o comando Maven para a construção da aplicação **mvnw clean install**
3. Navegar até o diretório **target**
4. Executar o comando de inicialização **java -jar spring-batch-exercise-0.0.1-SNAPSHOT.jar**

Para construir a aplicação com **docker**:

1. Navegar até o diretório raiz do projeto
    1. *docker build -f Dockerfile -t rbarbiero/spring-batch-exercise .*
    2. *docker run -d -p 8080:8080 rbarbiero/spring-batch-exercise java -jar app.jar*

**Utilizando a aplicação**
1. Navegue até a url http://localhost:8080/swagger-ui.html
2. Envie o arquivo para processamento no respectivo endpoint descrito na documentação. 
O retorno terá a url do recurso com seu identificador no *header* ***location***.
3. Para recuperar o arquivo processado:
    1. **Método 1** - **GET** - url contida no *header* ***location***
    2. **Método 2** - Use apenas o identificador no respectivo endpoint descrito na documentação

**Utilizando o serviço na AWS**
1. Esta aplicação está hospedado no serviço de computação em nuvem **Amazon AWS** disponibilizado na seguinte URL: **http://18.228.14.78:8080/swagger-ui.html**