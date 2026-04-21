# Proyecto 1: Sistema de Consulta de Padrón Electoral

Este sistema permite la consulta distribuida de datos del Padrón Electoral de Costa Rica. Utiliza una arquitectura de capas para procesar archivos planos y ofrecer respuestas a través de una interfaz gráfica (GUI) y dos servidores de red (TCP y HTTP).


##  Integrantes del Grupo
* **Oscar Molina** 
* **Travis Rivera**
* **Rhoswen Mora**
* **Sherry Avalos**
* **Britany Pineda**



## Cómo Compilar y Ejecutar

### Requisitos Previos
1. **Java JDK 17** o superior instalado.
2. **IDE NetBeans** (recomendado).
3. Los archivos de datos deben estar en la carpeta resources con los nombres:
   * PADRON_COMPLETO.txt
   * distelec.txt

### Pasos para Ejecutar
1. Abre el proyecto en NetBeans.
2. Asegúrate de que las rutas de los archivos en la clase `Main` coincidan con tu estructura de carpetas.
3. Haz clic derecho sobre el proyecto y selecciona **"Clean and Build"**.
4. Ejecuta la clase `Main.java`. 
5. Se abrirá automáticamente la **Interfaz Gráfica** y los servidores se activarán en la consola.

---

## Protocolo TCP (Puerto 5555)

El servidor TCP permite consultas de bajo nivel mediante una conexión de flujo de datos. Es ideal para sistemas que requieren alta velocidad y confirmación de entrega.

**Formato del Comando:** ACCION|CEDULA|FORMATO

* **Ejemplo de comando:** GET|101240037|JSON
* **Funcionamiento:** El servidor recibe la cadena, la divide usando el separador |, busca en el padrón y devuelve el resultado en el formato solicitado antes de cerrar la conexión.

---

## Endpoints HTTP (Puerto 9090)

El servidor HTTP permite realizar consultas desde cualquier navegador web o cliente REST. 

| Endpoint | Parámetros | Descripción |
| :--- | :--- | :--- |
| `/padron` | `cedula` (String), `format` (json/xml) | Consulta los datos de un ciudadano por su número de cédula. |

---

## Ejemplos de Requests y Responses

### 1. Consulta en formato JSON
**Request (Navegador):** http://localhost:9090/padron?cedula=101240037&format=json

**Response:**
json
{
  "exito": true,
  "persona": {
    "cedula": "101240037",
    "nombre": "ANA MARIA",
    "apellido1": "PEREZ",
    "apellido2": "PEREZ"
  },
  "direccion": "PAVAS"
}
