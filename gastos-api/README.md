# GastosApp - REST API

API REST para aplicación de control de gastos personales. Desarrollada con Node.js, Express y MySQL.

## 🚀 Características

- ✅ Autenticación JWT
- ✅ CRUD completo de gastos
- ✅ Gestión de categorías
- ✅ Filtros y búsqueda
- ✅ Resúmenes estadísticos
- ✅ Validación de datos
- ✅ Manejo centralizado de errores

## 📋 Prerequisitos

- Node.js 18+ instalado
- MySQL 5.7+ instalado y en ejecución
- Base de datos `gastos_app` creada (ejecutar el script SQL proporcionado)

## 🛠️ Instalación

### 1. Clonar o copiar el proyecto

```bash
cd c:\Users\Javi\ApiMovil\gastos-api
```

### 2. Instalar dependencias

```bash
npm install
```

### 3. Configurar variables de entorno

Copia el archivo `.env.example` a `.env`:

```bash
copy .env.example .env
```

Edita el archivo `.env` con tus credenciales:

```env
NODE_ENV=development
PORT=3000

# Configuración de MySQL
DB_HOST=localhost
DB_PORT=3306
DB_USER=root
DB_PASSWORD=tu_password
DB_NAME=gastos_app

# JWT Secret (cámbialo por algo seguro)
JWT_SECRET=mi_secreto_super_seguro_12345
JWT_EXPIRES_IN=24h
```

### 4. Crear la base de datos (Automático)

Ahora es mucho más fácil. Simplemente ejecuta:

```bash
npm run setup
```

Este comando te pedirá tu contraseña de MySQL (si no la pusiste en el .env) y creará la base de datos, tablas y datos de ejemplo automáticamente.

**Opción manual:**
Si prefieres, puedes ejecutar el script SQL manualmente en Workbench:
- Archivo ubicado en: `gastos_app_database.sql`

### 5. Ejecutar el servidor

**Modo desarrollo (con auto-reinicio):**
```bash
npm run dev
```

**Modo producción:**
```bash
npm start
```

Si todo está correcto, verás:
```
✅ Conectado a MySQL exitosamente
🚀 Servidor corriendo en puerto 3000
📍 http://localhost:3000
```

## 📚 API Endpoints

### Autenticación

#### Registrar usuario
```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "usuario@example.com",
  "name": "Juan Pérez",
  "password": "password123"
}
```

**Respuesta:**
```json
{
  "success": true,
  "message": "Usuario registrado exitosamente",
  "data": {
    "user": {
      "id": "uuid-here",
      "email": "usuario@example.com",
      "name": "Juan Pérez"
    },
    "token": "jwt-token-here"
  }
}
```

#### Iniciar sesión
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "usuario@example.com",
  "password": "password123"
}
```

#### Obtener usuario actual
```http
GET /api/auth/me
Authorization: Bearer jwt-token-here
```

#### Cerrar sesión
```http
POST /api/auth/logout
Authorization: Bearer jwt-token-here
```

---

### Gastos

**Nota:** Todos los endpoints de gastos requieren autenticación (header `Authorization: Bearer <token>`)

#### Listar gastos
```http
GET /api/expenses
Authorization: Bearer jwt-token-here

# Query params opcionales:
# ?limit=20&offset=0
# &start_date=2026-02-01&end_date=2026-02-28
# &category=Alimentos
# &search=comida
```

**Respuesta:**
```json
{
  "success": true,
  "data": {
    "expenses": [...],
    "pagination": {
      "total": 50,
      "limit": 20,
      "offset": 0,
      "hasMore": true
    }
  }
}
```

#### Obtener un gasto
```http
GET /api/expenses/:id
Authorization: Bearer jwt-token-here
```

#### Crear gasto
```http
POST /api/expenses
Authorization: Bearer jwt-token-here
Content-Type: application/json

{
  "description": "Comida en restaurante",
  "amount": 250.50,
  "category": "Alimentos",
  "expense_date": "2026-02-06",
  "notes": "Cena con cliente"
}
```

#### Actualizar gasto
```http
PUT /api/expenses/:id
Authorization: Bearer jwt-token-here
Content-Type: application/json

{
  "description": "Comida actualizada",
  "amount": 300.00
}
```

#### Eliminar gasto
```http
DELETE /api/expenses/:id
Authorization: Bearer jwt-token-here
```

#### Resumen de gastos
```http
GET /api/expenses/summary
Authorization: Bearer jwt-token-here
```

**Respuesta:**
```json
{
  "success": true,
  "data": {
    "currentMonth": {
      "total": 2684.50,
      "byCategory": [
        {
          "category": "Alimentos",
          "count": 3,
          "total": 1450.00
        }
      ]
    },
    "allTime": {
      "total": 5000.00,
      "count": 15
    },
    "recentExpenses": [...]
  }
}
```

---

### Categorías

#### Listar categorías
```http
GET /api/categories
```

**Respuesta:**
```json
{
  "success": true,
  "data": {
    "categories": [
      {
        "id": 1,
        "name": "Alimentos",
        "description": "Compras de supermercado, restaurantes y comida",
        "icon": "restaurant",
        "color": "#FF6B6B"
      }
    ]
  }
}
```

---

## 🧪 Probar la API

Puedes usar cualquiera de estas herramientas:

### 1. Thunder Client (VS Code Extension)
- Instala Thunder Client en VS Code
- Importa las peticiones manualmente

### 2. Postman
- Descarga Postman
- Crea una colección con los endpoints

### 3. cURL (Línea de comandos)

**Registrar usuario:**
```bash
curl -X POST http://localhost:3000/api/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"test@example.com\",\"name\":\"Test User\",\"password\":\"password123\"}"
```

**Login:**
```bash
curl -X POST http://localhost:3000/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"test@example.com\",\"password\":\"password123\"}"
```

**Crear gasto (con token):**
```bash
curl -X POST http://localhost:3000/api/expenses ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer TU_TOKEN_AQUI" ^
  -d "{\"description\":\"Test expense\",\"amount\":100,\"category\":\"Alimentos\",\"expense_date\":\"2026-02-06\"}"
```

---

## 📁 Estructura del Proyecto

```
gastos-api/
├── server.js              # Punto de entrada
├── package.json           # Dependencias
├── .env.example          # Template de variables de entorno
├── .gitignore            # Archivos ignorados por git
│
├── config/
│   └── database.js       # Configuración de MySQL
│
├── controllers/
│   ├── authController.js      # Lógica de autenticación
│   ├── expenseController.js   # Lógica de gastos
│   └── categoryController.js  # Lógica de categorías
│
├── middleware/
│   ├── auth.js           # Verificación JWT
│   ├── errorHandler.js   # Manejo de errores
│   └── validators.js     # Validación de requests
│
├── routes/
│   ├── auth.js           # Rutas de autenticación
│   ├── expenses.js       # Rutas de gastos
│   └── categories.js     # Rutas de categorías
│
└── utils/
    └── generateId.js     # Generador de UUIDs
```

---

## 🔒 Seguridad

- ✅ Contraseñas hasheadas con bcrypt (10 salt rounds)
- ✅ Tokens JWT con expiración configurable
- ✅ Validación de datos en todas las entradas
- ✅ Protección contra SQL injection (prepared statements)
- ✅ CORS habilitado
- ✅ Variables sensibles en archivo .env (no versionado)

**⚠️ IMPORTANTE para producción:**
- Cambia `JWT_SECRET` por algo más seguro
- Configura CORS solo para dominios permitidos
- Usa HTTPS
- Implementa rate limiting
- Habilita logs de producción

---

## 🐛 Troubleshooting

### Error: "Cannot connect to MySQL"
- Verifica que MySQL esté corriendo
- Revisa las credenciales en `.env`
- Confirma que la base de datos `gastos_app` existe

### Error: "Table doesn't exist"
- Ejecuta el script SQL `gastos_app_database.sql` en MySQL Workbench

### Error: "Token inválido"
- El token ha expirado (re-login)
- El `JWT_SECRET` cambió (re-login)
- El formato del header es incorrecto (debe ser: `Authorization: Bearer <token>`)

### Puerto 3000 ya en uso
- Cambia el `PORT` en `.env`
- O detén el proceso que usa el puerto 3000

---

## 🔄 Integración con Android App

Para conectar la app Android con esta API:

1. **Actualizar MockData.java:**
   - Reemplaza los métodos mock con llamadas HTTP a la API
   - Usa Retrofit o similar para las peticiones

2. **Configurar base URL:**
   ```java
   private static final String BASE_URL = "http://TU_IP:3000/api/";
   // Ejemplo: "http://192.168.1.100:3000/api/"
   ```

3. **Almacenar token JWT:**
   - Guarda el token en SharedPreferences
   - Inclúyelo en cada petición autenticada

4. **Manejo de errores:**
   - Implementa manejo de errores 401 (token expirado)
   - Redirige a login cuando sea necesario

---

## 📝 Notas Adicionales

- La API usa paginación por defecto (limit: 50)
- Las fechas deben estar en formato ISO 8601: `YYYY-MM-DD`
- Los montos son decimales con 2 decimales de precisión
- Los UUIDs se generan automáticamente en el servidor

---

## 👨‍💻 Desarrollo

**Dependencias principales:**
- express: Framework web
- mysql2: Driver MySQL con promises
- jsonwebtoken: Autenticación JWT
- bcrypt: Hash de contraseñas
- express-validator: Validación de datos
- cors: Habilitar CORS
- dotenv: Variables de entorno

**Dev Dependencies:**
- nodemon: Auto-reinicio en desarrollo

---

## 📄 Licencia

ISC

---

## 🤝 Soporte

Para problemas o preguntas, revisa la documentación de los endpoints o contacta al equipo de desarrollo.
