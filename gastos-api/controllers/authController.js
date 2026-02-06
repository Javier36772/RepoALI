const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const { pool } = require('../config/database');
const { generateId } = require('../utils/generateId');
require('dotenv').config();

/**
 * Generar token JWT
 */
const generateToken = (user) => {
    return jwt.sign(
        {
            id: user.id,
            email: user.email,
            name: user.name
        },
        process.env.JWT_SECRET,
        {
            expiresIn: process.env.JWT_EXPIRES_IN || '24h'
        }
    );
};

/**
 * @route   POST /api/auth/register
 * @desc    Registrar nuevo usuario
 * @access  Public
 */
const register = async (req, res, next) => {
    try {
        const { email, password, name } = req.body;

        // Verificar si el usuario ya existe
        const [existingUsers] = await pool.query(
            'SELECT id FROM users WHERE email = ?',
            [email]
        );

        if (existingUsers.length > 0) {
            return res.status(409).json({
                success: false,
                message: 'El email ya está registrado.'
            });
        }

        // Hashear contraseña
        const saltRounds = 10;
        const password_hash = await bcrypt.hash(password, saltRounds);

        // Crear usuario
        const userId = generateId();
        await pool.query(
            'INSERT INTO users (id, email, name, password_hash, is_active) VALUES (?, ?, ?, ?, ?)',
            [userId, email, name, password_hash, true]
        );

        // Obtener usuario creado (sin password)
        const [users] = await pool.query(
            'SELECT id, email, name, created_at FROM users WHERE id = ?',
            [userId]
        );

        const user = users[0];

        // Generar token
        const token = generateToken(user);

        res.status(201).json({
            success: true,
            message: 'Usuario registrado exitosamente',
            data: {
                user,
                token
            }
        });
    } catch (error) {
        next(error);
    }
};

/**
 * @route   POST /api/auth/login
 * @desc    Autenticar usuario
 * @access  Public
 */
const login = async (req, res, next) => {
    try {
        const { email, password } = req.body;

        // Buscar usuario
        const [users] = await pool.query(
            'SELECT id, email, name, password_hash, is_active FROM users WHERE email = ?',
            [email]
        );

        if (users.length === 0) {
            return res.status(401).json({
                success: false,
                message: 'Credenciales inválidas.'
            });
        }

        const user = users[0];

        // Verificar si el usuario está activo
        if (!user.is_active) {
            return res.status(403).json({
                success: false,
                message: 'Usuario inactivo. Contacte al administrador.'
            });
        }

        // Verificar contraseña
        const isPasswordValid = await bcrypt.compare(password, user.password_hash);

        if (!isPasswordValid) {
            return res.status(401).json({
                success: false,
                message: 'Credenciales inválidas.'
            });
        }

        // Remover password_hash del objeto
        delete user.password_hash;
        delete user.is_active;

        // Generar token
        const token = generateToken(user);

        res.json({
            success: true,
            message: 'Login exitoso',
            data: {
                user,
                token
            }
        });
    } catch (error) {
        next(error);
    }
};

/**
 * @route   POST /api/auth/logout
 * @desc    Cerrar sesión (cliente debe eliminar token)
 * @access  Private
 */
const logout = async (req, res) => {
    // En implementación JWT stateless, el logout es del lado del cliente
    // El cliente debe eliminar el token
    res.json({
        success: true,
        message: 'Logout exitoso. El token debe ser eliminado del cliente.'
    });
};

/**
 * @route   GET /api/auth/me
 * @desc    Obtener usuario actual
 * @access  Private
 */
const getCurrentUser = async (req, res, next) => {
    try {
        // req.user viene del middleware auth
        const [users] = await pool.query(
            'SELECT id, email, name, created_at FROM users WHERE id = ?',
            [req.user.id]
        );

        if (users.length === 0) {
            return res.status(404).json({
                success: false,
                message: 'Usuario no encontrado.'
            });
        }

        res.json({
            success: true,
            data: {
                user: users[0]
            }
        });
    } catch (error) {
        next(error);
    }
};

module.exports = {
    register,
    login,
    logout,
    getCurrentUser
};
