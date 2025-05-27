# 📡 Proyecto Final - Cómputo Distribuido

## 🔍 Nombre del Proyecto
**Red Peer-to-Peer con Compartición de Archivos y Elección de Coordinador mediante el Algoritmo Bully**

## 🎓 Universidad
Benemérita Universidad Autónoma de Puebla  
Facultad de Ciencias de la Computación

---

## 👥 Autores
- Evelyn Dolores Flores Lechuga  
- Itzel Daniela Martínez Carrera
- Fernado Garza de la Luz

---

## 🎬 Video de Presentación

▶️ Mira la demostración del proyecto en YouTube:  
[https://youtu.be/mngCBLUSk2Q](https://youtu.be/mngCBLUSk2Q)

---

## 🧠 Descripción General

Este proyecto implementa una **red Peer-to-Peer (P2P)** en Java, donde los nodos pueden:

- Compartir archivos entre sí (como PDF).
- Comunicarse a través de un chat distribuido.
- Participar en un proceso de **elección de coordinador** usando el **algoritmo Bully**.

Cada nodo puede actuar como cliente y servidor a la vez. El sistema se comunica a través de **sockets multicast** y utiliza **multihilos** para las operaciones concurrentes.

---

## 🧩 Componentes del Proyecto

### 📁 `Peer.java`
- Interfaz gráfica (Swing) para interactuar con la red P2P.
- Permite enviar mensajes, cargar y descargar archivos.
- Se identifica al usuario con nombre y hora como ID.

### 🌐 `RedP2P.java`
- Lógica de red: envío de mensajes, carga/descarga de archivos.
- Gestión local de carpetas `Upload` y `Download`.
- Comunicación multicast entre peers.

### 👑 `Bully.java`
- Implementación del **algoritmo Bully**.
- Permite elegir dinámicamente un coordinador basado en prioridad (hora de conexión).
- Mecanismos de detección de fallos y nueva elección automática.

---

## 🛠️ Tecnologías Utilizadas

- **Lenguaje:** Java
- **IDE:** NetBeans 17
- **Redes:** Sockets multicast
- **Interfaz gráfica:** Java Swing
- **Topología de red:** LAN (red local con módem)

---

## 🧪 Funcionalidades Clave

- 🔗 Conexión de múltiples nodos en red local.
- 📨 Envío y recepción de mensajes entre peers.
- 📂 Compartición y descarga de archivos.
- 🔄 Actualización automática de lista de peers conectados.
- 👑 Elección dinámica de coordinador con algoritmo Bully.

---

## 📸 Capturas del Proyecto
Se encuentran en la documentación adjunta en este repositorio.

