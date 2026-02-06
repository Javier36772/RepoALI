/**
 * Middleware centralizado para manejo de errores
 */
const errorHandler = (err, req, res, next) => {
    console.error('Error:', err);

    // Error de validación
    if (err.name === 'ValidationError') {
        return res.status(400).json({
            success: false,
            message: 'Error de validación',
            errors: err.errors
        });
    }

    // Error de MySQL
    if (err.code && err.code.startsWith('ER_')) {
        // Duplicate entry
        if (err.code === 'ER_DUP_ENTRY') {
            return res.status(409).json({
                success: false,
                message: 'El registro ya existe en la base de datos.'
            });
        }

        // Foreign key constraint
        if (err.code === 'ER_NO_REFERENCED_ROW_2') {
            return res.status(400).json({
                success: false,
                message: 'Referencia inválida en la base de datos.'
            });
        }

        return res.status(500).json({
            success: false,
            message: 'Error de base de datos.',
            ...(process.env.NODE_ENV === 'development' && { error: err.message })
        });
    }

    // Error de JWT
    if (err.name === 'JsonWebTokenError' || err.name === 'TokenExpiredError') {
        return res.status(401).json({
            success: false,
            message: 'Token inválido o expirado.'
        });
    }

    // Error personalizado con statusCode
    if (err.statusCode) {
        return res.status(err.statusCode).json({
            success: false,
            message: err.message
        });
    }

    // Error genérico
    res.status(500).json({
        success: false,
        message: 'Error interno del servidor.',
        ...(process.env.NODE_ENV === 'development' && { error: err.message })
    });
};

/**
 * Middleware para rutas no encontradas
 */
const notFound = (req, res) => {
    res.status(404).json({
        success: false,
        message: `Ruta ${req.originalUrl} no encontrada.`
    });
};

module.exports = {
    errorHandler,
    notFound
};
