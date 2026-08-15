# 💈 Sistema de Reservas — Barbería

Sistema de gestión de citas para una barbería con múltiples barberos, cada uno con su propio horario. Los clientes reservan un servicio con un barbero en un horario disponible, y el sistema valida en tiempo real que no haya conflictos de agenda.

Muchas barberías coordinan citas por WhatsApp o a mano — este proyecto automatiza ese proceso con reglas de negocio reales: control de roles, validación de horarios y prevención de doble reserva.

## 🧩 El problema técnico central

La parte más interesante de este proyecto es evitar que dos personas reserven el mismo horario con el mismo barbero. Antes de confirmar una cita, el backend valida:

1. Que el horario solicitado caiga dentro del horario laboral del barbero ese día.
2. Que no exista ya otra cita activa para ese barbero que se solape con el rango de tiempo solicitado (usando la fórmula clásica de solapamiento de intervalos: `A.inicio < B.fin AND B.inicio < A.fin`).

Ambas validaciones corren dentro de una transacción (`@Transactional`) para reducir el riesgo de condiciones de carrera si dos personas reservan casi al mismo tiempo, y están cubiertas por tests unitarios que no dependen de la base de datos.

## 🛠️ Stack técnico

**Backend**
- Java 21 + Spring Boot
- Spring Security + JWT (autenticación sin estado)
- Spring Data JPA + PostgreSQL
- RBAC por rol (`@PreAuthorize`) y por dueño del recurso
- JUnit 5 + Mockito (tests unitarios)
- Docker + Docker Compose

**Roles del sistema**
| Rol | Puede hacer |
|---|---|
| `ADMIN` | Gestionar barberos y servicios |
| `BARBERO` | Ver su propia agenda, marcar citas como completadas |
| `CLIENTE` | Reservar citas, ver su historial, cancelar sus propias citas |

## 📐 Modelo de datos

```
Usuario (1) ──< (N) Cita >── (1) Barbero
                    │
                    └──< (1) Servicio

Barbero (1) ──< (N) HorarioDisponible
```

**Entidades principales:** `Usuario`, `Servicio`, `Barbero`, `HorarioDisponible`, `Cita`.

## 🔌 Endpoints principales

| Método | Endpoint | Rol requerido | Descripción |
|--------|----------|----------------|-------------|
| POST | `/auth/register` | Público | Registro de usuario |
| POST | `/auth/login` | Público | Login, devuelve JWT |
| GET | `/servicios` | Público | Lista de servicios activos |
| POST | `/servicios` | ADMIN | Crear servicio |
| POST | `/barberos/{usuarioId}` | ADMIN | Convertir un usuario en barbero |
| POST | `/horarios/barbero/{barberoId}` | Autenticado | Cargar horario laboral de un barbero |
| POST | `/citas` | CLIENTE | Crear una reserva (con validación de solapamiento) |
| GET | `/citas/mias` | CLIENTE | Historial del cliente autenticado |
| GET | `/citas/agenda` | BARBERO | Agenda del barbero autenticado |
| PATCH | `/citas/{id}/cancelar` | CLIENTE (dueño) / ADMIN | Cancelar una cita |
| PATCH | `/citas/{id}/completar` | BARBERO (asignado) | Marcar cita como completada |

## 🚀 Cómo correrlo localmente

Requiere tener [Docker](https://www.docker.com/) instalado.

```bash
git clone https://github.com/Josephover/barberia-backend.git
cd barberia-backend
docker compose up --build
```

Esto levanta dos contenedores: PostgreSQL y el backend de Spring Boot. La API queda disponible en `http://localhost:8080`.

### Probar la API

1. Registra un usuario administrador:
```bash
POST http://localhost:8080/auth/register
{
  "nombre": "Admin",
  "email": "admin@barberia.com",
  "password": "admin123",
  "telefono": "0999999999",
  "rol": "ADMIN"
}
```

2. Usa el token devuelto para crear servicios, barberos y horarios.
3. Registra un cliente y reserva una cita en `/citas`.

## ✅ Tests

```bash
./mvnw test
```

Los tests cubren la lógica de `CitaService` de forma aislada (con mocks de los repositorios): creación exitosa, rechazo por solapamiento, rechazo por horario fuera de servicio, y manejo de barbero inexistente.

## 🗺️ Roadmap

- [ ] Frontend en React (calendario de reservas, dashboard por rol)
- [ ] Notificación por email al confirmar una cita
- [ ] Deploy en producción (Render/Railway + Vercel)

## 👤 Autor

Joseph André Sánchez Verdesoto
[GitHub](https://github.com/Josephover) · [LinkedIn](https://linkedin.com/in/joseph-sánchez-b83211188)