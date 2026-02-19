# Flujo de Comunicación y Arquitectura

## Diagrama de flujo

```
Cliente HTTP (Frontend, Postman, Swagger)
    ↓
[EmpleadoController] ← punto de entrada REST
    ↓
[EmpleadoService] ← interfaz (contrato de negocio)
    ↓
[EmpleadoServiceImpl] ← implementación de la lógica de negocio
    ↓
[EmpleadoRepository] ← interfaz (contrato de acceso a datos)
    ↓
[EmpleadoRepositoryImpl] ← implementación real (acceso a BD)
    ↓
[Empleado] ← modelo de datos (entidad)
    ↓
Base de Datos (H2/SQL Server)
```

## Explicación de cada archivo

- **EmpleadoController.java**: Expone los endpoints REST. Recibe las peticiones del cliente y delega la lógica al Service.
- **EmpleadoService.java**: Interfaz que define los métodos de negocio (por ejemplo, `obtenerEmpleados`).
- **EmpleadoServiceImpl.java**: Implementa la lógica de negocio definida en la interfaz. Llama al Repository para obtener los datos.
- **EmpleadoRepository.java**: Interfaz que define los métodos de acceso a datos (por ejemplo, `obtenerEmpleados`).
- **EmpleadoRepositoryImpl.java**: Implementa el acceso a la base de datos. Aquí se ejecuta la función/VIEW/SP que retorna el JSON y se mapea a objetos Java.
- **Empleado.java**: Modelo de datos que representa la entidad en la base de datos.
- **EmpleadoDTO.java**: Objeto de transferencia de datos. Se usa para exponer solo los campos necesarios al frontend.

## ¿Por qué usar interfaces y "Impl"?
- **Flexibilidad**: Puedes cambiar la implementación sin afectar el resto del sistema.
- **Testabilidad**: Puedes hacer pruebas unitarias usando mocks de las interfaces.
- **Mantenimiento**: Si cambias la lógica de acceso a datos, solo modificas la clase `Impl`, no el resto del sistema.

## Ejemplo de flujo real
1. El cliente hace una petición GET a `/api/empleados`.
2. `EmpleadoController` recibe la petición y llama a `EmpleadoService.obtenerEmpleados()`.
3. `EmpleadoServiceImpl` implementa ese método y llama a `EmpleadoRepository.obtenerEmpleados()`.
4. `EmpleadoRepositoryImpl` ejecuta la función/VIEW/SP en la base de datos, recibe el JSON, lo mapea a objetos `Empleado` y retorna la lista.
5. `EmpleadoServiceImpl` transforma la lista de modelos a DTOs.
6. `EmpleadoController` retorna la lista de DTOs al cliente.

---
