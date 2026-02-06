const express = require('express');
const router = express.Router();
const { getAllCategories } = require('../controllers/categoryController');

// Ruta pública para obtener categorías
router.get('/', getAllCategories);

module.exports = router;
