const express = require('express');
const router = express.Router();
const {
    register,
    login,
    logout,
    getCurrentUser
} = require('../controllers/authController');
const auth = require('../middleware/auth');
const {
    validateRegister,
    validateLogin
} = require('../middleware/validators');

// Rutas públicas
router.post('/register', validateRegister, register);
router.post('/login', validateLogin, login);

// Rutas protegidas
router.post('/logout', auth, logout);
router.get('/me', auth, getCurrentUser);

module.exports = router;
