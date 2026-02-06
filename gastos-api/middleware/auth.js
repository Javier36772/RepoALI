const jwt = require('jsonwebtoken');
require('dotenv').config();

/**
 * Middleware para verificar token JWT y proteger rutas
 */
const auth = async (req, res, next) => {
    try {
        // Obtener token del header Authorization
        const authHeader = req.header('Authorization');

        if (!authHeader || !authHeader.startsWith('Bearer ')) {
            return res.status(401).json({
                success: false,
                message: 'Acceso denegado. Token no proporcionado.'
            });
        }

        // Extraer token (formato: "Bearer TOKEN")
        const token = authHeader.substring(7);

        // Verificar y decodificar token
        const decoded = jwt.verify(token, process.env.JWT_SECRET);

        // Agregar información del usuario al request
        req.user = {
            id: decoded.id,
            email: decoded.email,
            name: decoded.name
        };

        next();
    } catch (error) {
        if (error.name === 'TokenExpiredError') {
            return res.status(401).json({
                success: false,
                message: 'Token expirado. Por favor inicia sesión nuevamente.'
            });
        }

        if (error.name === 'JsonWebTokenError') {
            return res.status(401).json({
                success: false,
                message: 'Token inválido.'
            });
        }

        res.status(500).json({
            success: false,
            message: 'Error al verificar autenticación.'
        });
    }
};

module.exports = auth;
