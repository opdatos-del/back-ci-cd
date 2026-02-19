# backend-jovyweb

## Descripción

---

## Inicio rápido

### 1. Clonar el repositorio
```bash
git clone https://github.com/opdatos-del/backend-jovyweb.git
cd backend-jovyweb
```

### 2. Configurar variables de entorno

Copia el archivo de ejemplo:
```bash
cp .env.example .env
```

Luego edita `.env` con tus credenciales de base de datos:
```env
(Pedir credenciales al PM o a la persona encargada)
```

### 3. Instalar dependencias
```bash
mvnw clean install
```

### 4. Ejecutar la aplicación
```bash
mvnw spring-boot:run
```

La aplicación estará disponible en: **http://localhost:8080**

---


## Documentación

- [Flujo de trabajo Git](docs/git-workflow.md) - Estrategia de ramas y commits
- [Arquitectura](docs/arquitectura/flujo-comunicacion.md) - Descripción de la arquitectura del sistema

### **Para mas documentación, visita la carpeta "docs/"**
---


## Notas importantes

- Nunca subar el `.env` real - Solo usar `.env.example` en el repo
- Seguir el Git Flow - Leer [docs/git-workflow.md](docs/git-workflow.md)
- Documentación de API - Disponible en `/swagger-ui/index.html` cuando la app está corriendo
