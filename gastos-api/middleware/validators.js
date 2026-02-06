const { body, param, query, validationResult } = require('express-validator');

/**
 * Middleware para manejar resultados de validación
 */
const handleValidationErrors = (req, res, next) => {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
        return res.status(400).json({
            success: false,
            message: 'Error de validación',
            errors: errors.array()
        });
    }
    next();
};

/**
 * Validadores para registro de usuario
 */
const validateRegister = [
    body('email')
        .isEmail()
        .withMessage('Debe proporcionar un email válido')
        .normalizeEmail(),
    body('password')
        .isLength({ min: 6 })
        .withMessage('La contraseña debe tener al menos 6 caracteres'),
    body('name')
        .trim()
        .notEmpty()
        .withMessage('El nombre es requerido')
        .isLength({ min: 2, max: 255 })
        .withMessage('El nombre debe tener entre 2 y 255 caracteres'),
    handleValidationErrors
];

/**
 * Validadores para login
 */
const validateLogin = [
    body('email')
        .isEmail()
        .withMessage('Debe proporcionar un email válido')
        .normalizeEmail(),
    body('password')
        .notEmpty()
        .withMessage('La contraseña es requerida'),
    handleValidationErrors
];

/**
 * Validadores para crear gasto
 */
const validateCreateExpense = [
    body('description')
        .trim()
        .notEmpty()
        .withMessage('La descripción es requerida')
        .isLength({ min: 3, max: 500 })
        .withMessage('La descripción debe tener entre 3 y 500 caracteres'),
    body('amount')
        .isFloat({ min: 0.01 })
        .withMessage('El monto debe ser un número positivo mayor a 0'),
    body('category')
        .trim()
        .notEmpty()
        .withMessage('La categoría es requerida')
        .isLength({ max: 100 })
        .withMessage('La categoría no puede exceder 100 caracteres'),
    body('expense_date')
        .isISO8601()
        .withMessage('La fecha debe estar en formato ISO 8601 (YYYY-MM-DD)')
        .toDate(),
    body('notes')
        .optional()
        .isLength({ max: 1000 })
        .withMessage('Las notas no pueden exceder 1000 caracteres'),
    handleValidationErrors
];

/**
 * Validadores para actualizar gasto
 */
const validateUpdateExpense = [
    body('description')
        .optional()
        .trim()
        .isLength({ min: 3, max: 500 })
        .withMessage('La descripción debe tener entre 3 y 500 caracteres'),
    body('amount')
        .optional()
        .isFloat({ min: 0.01 })
        .withMessage('El monto debe ser un número positivo mayor a 0'),
    body('category')
        .optional()
        .trim()
        .isLength({ max: 100 })
        .withMessage('La categoría no puede exceder 100 caracteres'),
    body('expense_date')
        .optional()
        .isISO8601()
        .withMessage('La fecha debe estar en formato ISO 8601 (YYYY-MM-DD)')
        .toDate(),
    body('notes')
        .optional()
        .isLength({ max: 1000 })
        .withMessage('Las notas no pueden exceder 1000 caracteres'),
    handleValidationErrors
];

/**
 * Validador para ID de parámetro UUID
 */
const validateUUID = [
    param('id')
        .isUUID()
        .withMessage('ID inválido'),
    handleValidationErrors
];

/**
 * Validadores para query params de filtros
 */
const validateExpenseFilters = [
    query('limit')
        .optional()
        .isInt({ min: 1, max: 100 })
        .withMessage('Limit debe ser un entero entre 1 y 100')
        .toInt(),
    query('offset')
        .optional()
        .isInt({ min: 0 })
        .withMessage('Offset debe ser un entero positivo')
        .toInt(),
    query('start_date')
        .optional()
        .isISO8601()
        .withMessage('start_date debe estar en formato ISO 8601')
        .toDate(),
    query('end_date')
        .optional()
        .isISO8601()
        .withMessage('end_date debe estar en formato ISO 8601')
        .toDate(),
    query('category')
        .optional()
        .trim()
        .isLength({ max: 100 })
        .withMessage('Categoría no puede exceder 100 caracteres'),
    query('search')
        .optional()
        .trim()
        .isLength({ max: 255 })
        .withMessage('Búsqueda no puede exceder 255 caracteres'),
    handleValidationErrors
];

module.exports = {
    validateRegister,
    validateLogin,
    validateCreateExpense,
    validateUpdateExpense,
    validateUUID,
    validateExpenseFilters
};
