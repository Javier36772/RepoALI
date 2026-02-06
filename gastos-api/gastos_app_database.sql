-- =====================================================
-- Base de Datos: GastosApp
-- Sistema de Control de Gastos Personales
-- Basado en APP_mobil_Ali_Gastos
-- =====================================================

-- Crear base de datos
CREATE DATABASE IF NOT EXISTS gastos_app
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE gastos_app;

-- =====================================================
-- TABLA: users
-- Descripción: Almacena información de usuarios registrados
-- =====================================================

CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY COMMENT 'UUID único del usuario',
    email VARCHAR(255) NOT NULL UNIQUE COMMENT 'Correo electrónico del usuario',
    name VARCHAR(255) NOT NULL COMMENT 'Nombre completo del usuario',
    password_hash VARCHAR(255) NOT NULL COMMENT 'Hash de la contraseña (bcrypt recomendado)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha de registro',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Fecha de última actualización',
    is_active BOOLEAN DEFAULT TRUE COMMENT 'Estado del usuario (activo/inactivo)',
    INDEX idx_email (email),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Usuarios del sistema de gastos';

-- =====================================================
-- TABLA: categories
-- Descripción: Catálogo de categorías de gastos
-- =====================================================

CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE COMMENT 'Nombre de la categoría',
    description TEXT COMMENT 'Descripción de la categoría',
    icon VARCHAR(50) COMMENT 'Nombre del ícono asociado',
    color VARCHAR(7) COMMENT 'Color en formato hexadecimal (#RRGGBB)',
    is_active BOOLEAN DEFAULT TRUE COMMENT 'Estado de la categoría',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_name (name),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Categorías de gastos';

-- =====================================================
-- TABLA: expenses
-- Descripción: Registro de gastos de usuarios
-- =====================================================

CREATE TABLE IF NOT EXISTS expenses (
    id VARCHAR(36) PRIMARY KEY COMMENT 'UUID único del gasto',
    user_id VARCHAR(36) NOT NULL COMMENT 'ID del usuario que registró el gasto',
    description VARCHAR(500) NOT NULL COMMENT 'Descripción del gasto',
    amount DECIMAL(10, 2) NOT NULL COMMENT 'Monto del gasto',
    category VARCHAR(100) NOT NULL COMMENT 'Categoría del gasto',
    expense_date DATE NOT NULL COMMENT 'Fecha en que se realizó el gasto',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha de registro en el sistema',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Fecha de última actualización',
    notes TEXT COMMENT 'Notas adicionales sobre el gasto',
    
    -- Claves foráneas
    CONSTRAINT fk_expenses_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    
    -- Índices para mejorar rendimiento
    INDEX idx_user_id (user_id),
    INDEX idx_expense_date (expense_date),
    INDEX idx_category (category),
    INDEX idx_user_date (user_id, expense_date),
    INDEX idx_amount (amount),
    
    -- Validaciones
    CONSTRAINT chk_amount_positive CHECK (amount > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Registro de gastos de usuarios';

-- =====================================================
-- TABLA: user_sessions
-- Descripción: Gestión de sesiones de usuario (opcional)
-- =====================================================

CREATE TABLE IF NOT EXISTS user_sessions (
    id VARCHAR(36) PRIMARY KEY COMMENT 'UUID de la sesión',
    user_id VARCHAR(36) NOT NULL COMMENT 'ID del usuario',
    token VARCHAR(512) NOT NULL COMMENT 'Token de sesión',
    device_info VARCHAR(255) COMMENT 'Información del dispositivo',
    ip_address VARCHAR(45) COMMENT 'Dirección IP',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL COMMENT 'Fecha de expiración de la sesión',
    is_active BOOLEAN DEFAULT TRUE,
    
    CONSTRAINT fk_sessions_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    
    INDEX idx_user_id (user_id),
    INDEX idx_token (token),
    INDEX idx_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Sesiones activas de usuarios';

-- =====================================================
-- DATOS INICIALES: Categorías predefinidas
-- =====================================================

INSERT IGNORE INTO categories (name, description, icon, color) VALUES
('Alimentos', 'Compras de supermercado, restaurantes y comida', 'restaurant', '#FF6B6B'),
('Transporte', 'Uber, taxis, gasolina, transporte público', 'directions_car', '#4ECDC4'),
('Entretenimiento', 'Streaming, cine, ocio y diversión', 'movie', '#95E1D3'),
('Salud', 'Consultas médicas, medicamentos, seguro', 'local_hospital', '#F38181'),
('Servicios', 'Luz, agua, internet, teléfono', 'flash_on', '#FFA07A'),
('Educación', 'Cursos, libros, material educativo', 'school', '#786FA6'),
('Hogar', 'Renta, muebles, mantenimiento', 'home', '#F8B500'),
('Ropa', 'Vestimenta y accesorios', 'checkroom', '#574B90'),
('Otros', 'Gastos diversos no categorizados', 'more_horiz', '#95A5A6');

-- =====================================================
-- DATOS DE PRUEBA: Usuario y gastos de ejemplo
-- =====================================================

-- Insertar usuario de prueba
INSERT IGNORE INTO users (id, email, name, password_hash, is_active) VALUES
('550e8400-e29b-41d4-a716-446655440000', 'demo@gastosapp.com', 'Usuario Demo', '$2a$10$DemoHashPasswordExample123456789', TRUE),
('550e8400-e29b-41d4-a716-446655440001', 'ali@example.com', 'Ali García', '$2a$10$AnotherHashPasswordExample123456', TRUE);

-- Insertar gastos de ejemplo (basados en MockData.java)
INSERT IGNORE INTO expenses (id, user_id, description, amount, category, expense_date) VALUES
('650e8400-e29b-41d4-a716-446655440010', '550e8400-e29b-41d4-a716-446655440000', 'Comida en restaurante', 250.00, 'Alimentos', '2026-02-06'),
('650e8400-e29b-41d4-a716-446655440011', '550e8400-e29b-41d4-a716-446655440000', 'Uber al trabajo', 85.50, 'Transporte', '2026-02-05'),
('650e8400-e29b-41d4-a716-446655440012', '550e8400-e29b-41d4-a716-446655440000', 'Netflix mensual', 199.00, 'Entretenimiento', '2026-02-04'),
('650e8400-e29b-41d4-a716-446655440013', '550e8400-e29b-41d4-a716-446655440000', 'Consulta médica', 500.00, 'Salud', '2026-02-03'),
('650e8400-e29b-41d4-a716-446655440014', '550e8400-e29b-41d4-a716-446655440000', 'Luz del mes', 450.00, 'Servicios', '2026-02-02'),
('650e8400-e29b-41d4-a716-446655440015', '550e8400-e29b-41d4-a716-446655440000', 'Compras supermercado', 1200.00, 'Alimentos', '2026-02-01'),
('650e8400-e29b-41d4-a716-446655440016', '550e8400-e29b-41d4-a716-446655440001', 'Gasolina', 600.00, 'Transporte', '2026-02-06'),
('650e8400-e29b-41d4-a716-446655440017', '550e8400-e29b-41d4-a716-446655440001', 'Pizza familiar', 320.00, 'Alimentos', '2026-02-05');

-- =====================================================
-- VISTAS ÚTILES
-- =====================================================

-- Vista: Resumen de gastos por usuario
CREATE OR REPLACE VIEW v_user_expense_summary AS
SELECT 
    u.id AS user_id,
    u.name AS user_name,
    u.email,
    COUNT(e.id) AS total_expenses,
    COALESCE(SUM(e.amount), 0) AS total_amount,
    MIN(e.expense_date) AS first_expense_date,
    MAX(e.expense_date) AS last_expense_date
FROM users u
LEFT JOIN expenses e ON u.id = e.user_id
GROUP BY u.id, u.name, u.email;

-- Vista: Gastos del mes actual por usuario
CREATE OR REPLACE VIEW v_current_month_expenses AS
SELECT 
    e.id,
    e.user_id,
    u.name AS user_name,
    e.description,
    e.amount,
    e.category,
    e.expense_date,
    e.created_at
FROM expenses e
INNER JOIN users u ON e.user_id = u.id
WHERE YEAR(e.expense_date) = YEAR(CURRENT_DATE)
  AND MONTH(e.expense_date) = MONTH(CURRENT_DATE)
ORDER BY e.expense_date DESC, e.created_at DESC;

-- Vista: Resumen por categoría
CREATE OR REPLACE VIEW v_category_summary AS
SELECT 
    category,
    COUNT(*) AS expense_count,
    SUM(amount) AS total_amount,
    AVG(amount) AS avg_amount,
    MIN(amount) AS min_amount,
    MAX(amount) AS max_amount
FROM expenses
GROUP BY category
ORDER BY total_amount DESC;

-- =====================================================
-- PROCEDIMIENTOS ALMACENADOS
-- =====================================================

DELIMITER //

-- Procedimiento: Obtener gastos de un usuario en un rango de fechas
CREATE PROCEDURE sp_get_user_expenses_by_date_range(
    IN p_user_id VARCHAR(36),
    IN p_start_date DATE,
    IN p_end_date DATE
)
BEGIN
    SELECT 
        id,
        description,
        amount,
        category,
        expense_date,
        notes,
        created_at
    FROM expenses
    WHERE user_id = p_user_id
      AND expense_date BETWEEN p_start_date AND p_end_date
    ORDER BY expense_date DESC, created_at DESC;
END //

-- Procedimiento: Obtener resumen mensual de gastos por categoría
CREATE PROCEDURE sp_monthly_category_summary(
    IN p_user_id VARCHAR(36),
    IN p_year INT,
    IN p_month INT
)
BEGIN
    SELECT 
        category,
        COUNT(*) AS expense_count,
        SUM(amount) AS total_amount,
        AVG(amount) AS avg_amount
    FROM expenses
    WHERE user_id = p_user_id
      AND YEAR(expense_date) = p_year
      AND MONTH(expense_date) = p_month
    GROUP BY category
    ORDER BY total_amount DESC;
END //

-- Procedimiento: Insertar nuevo gasto
CREATE PROCEDURE sp_insert_expense(
    IN p_id VARCHAR(36),
    IN p_user_id VARCHAR(36),
    IN p_description VARCHAR(500),
    IN p_amount DECIMAL(10, 2),
    IN p_category VARCHAR(100),
    IN p_expense_date DATE,
    IN p_notes TEXT
)
BEGIN
    INSERT INTO expenses (id, user_id, description, amount, category, expense_date, notes)
    VALUES (p_id, p_user_id, p_description, p_amount, p_category, p_expense_date, p_notes);
    
    SELECT * FROM expenses WHERE id = p_id;
END //

DELIMITER ;

-- =====================================================
-- FUNCIONES ÚTILES
-- =====================================================

DELIMITER //

-- Función: Calcular total de gastos del mes actual
CREATE FUNCTION fn_get_current_month_total(p_user_id VARCHAR(36))
RETURNS DECIMAL(10, 2)
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_total DECIMAL(10, 2);
    
    SELECT COALESCE(SUM(amount), 0) INTO v_total
    FROM expenses
    WHERE user_id = p_user_id
      AND YEAR(expense_date) = YEAR(CURRENT_DATE)
      AND MONTH(expense_date) = MONTH(CURRENT_DATE);
    
    RETURN v_total;
END //

-- Función: Obtener categoría con mayor gasto
CREATE FUNCTION fn_get_top_category(p_user_id VARCHAR(36))
RETURNS VARCHAR(100)
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_category VARCHAR(100);
    
    SELECT category INTO v_category
    FROM expenses
    WHERE user_id = p_user_id
    GROUP BY category
    ORDER BY SUM(amount) DESC
    LIMIT 1;
    
    RETURN v_category;
END //

DELIMITER ;

-- =====================================================
-- TRIGGERS
-- =====================================================

DELIMITER //

-- Trigger: Validar email antes de insertar usuario
CREATE TRIGGER trg_validate_user_email_before_insert
BEFORE INSERT ON users
FOR EACH ROW
BEGIN
    IF NEW.email NOT REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Email inválido';
    END IF;
END //

-- Trigger: Validar email antes de actualizar usuario
CREATE TRIGGER trg_validate_user_email_before_update
BEFORE UPDATE ON users
FOR EACH ROW
BEGIN
    IF NEW.email NOT REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Email inválido';
    END IF;
END //

DELIMITER ;

-- =====================================================
-- CONSULTAS DE VERIFICACIÓN
-- =====================================================

-- Verificar estructura de tablas
SELECT 
    TABLE_NAME,
    TABLE_ROWS,
    CREATE_TIME
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'gastos_app'
ORDER BY TABLE_NAME;

-- Verificar datos iniciales
SELECT 'Usuarios' AS tipo, COUNT(*) AS cantidad FROM users
UNION ALL
SELECT 'Categorías', COUNT(*) FROM categories
UNION ALL
SELECT 'Gastos', COUNT(*) FROM expenses;

-- Resumen de gastos por categoría
SELECT * FROM v_category_summary;

-- Total de gastos por usuario
SELECT * FROM v_user_expense_summary;

-- =====================================================
-- NOTAS DE IMPLEMENTACIÓN
-- =====================================================
/*
1. Las contraseñas en los datos de ejemplo son SOLO para demostración.
   En producción, usar bcrypt o Argon2 para hashear contraseñas.

2. Los UUIDs se generan en la aplicación. Para generar UUIDs en MySQL:
   SELECT UUID();

3. Índices creados para optimizar consultas frecuentes:
   - Búsqueda por usuario
   - Filtrado por fecha
   - Agrupación por categoría

4. Constraints de integridad referencial configurados con CASCADE
   para mantener consistencia de datos.

5. Para integración con la app Android, implementar API REST que:
   - Autentique usuarios (JWT recomendado)
   - CRUD de gastos
   - Consultas de resumen y estadísticas

6. Consideraciones de seguridad:
   - Usar conexiones SSL/TLS
   - Implementar rate limiting en API
   - Sanitizar inputs para prevenir SQL injection
   - Encriptar datos sensibles en tránsito y reposo
*/

-- =====================================================
-- FIN DEL SCRIPT
-- =====================================================
