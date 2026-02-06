const { pool } = require('../config/database');

/**
 * @route   GET /api/categories
 * @desc    Obtener todas las categorías activas
 * @access  Public
 */
const getAllCategories = async (req, res, next) => {
    try {
        const [categories] = await pool.query(
            'SELECT id, name, description, icon, color FROM categories WHERE is_active = TRUE ORDER BY name ASC'
        );

        res.json({
            success: true,
            data: {
                categories
            }
        });
    } catch (error) {
        next(error);
    }
};

module.exports = {
    getAllCategories
};
