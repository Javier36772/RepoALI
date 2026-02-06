const { pool } = require('../config/database');
const { generateId } = require('../utils/generateId');

/**
 * @route   GET /api/expenses
 * @desc    Obtener todos los gastos del usuario
 * @access  Private
 */
const getAllExpenses = async (req, res, next) => {
    try {
        const userId = req.user.id;
        const {
            limit = 50,
            offset = 0,
            start_date,
            end_date,
            category,
            search
        } = req.query;

        // Construir query dinámicamente
        let query = 'SELECT * FROM expenses WHERE user_id = ?';
        const params = [userId];

        // Filtros opcionales
        if (start_date) {
            query += ' AND expense_date >= ?';
            params.push(start_date);
        }

        if (end_date) {
            query += ' AND expense_date <= ?';
            params.push(end_date);
        }

        if (category) {
            query += ' AND category = ?';
            params.push(category);
        }

        if (search) {
            query += ' AND (description LIKE ? OR category LIKE ?)';
            const searchPattern = `%${search}%`;
            params.push(searchPattern, searchPattern);
        }

        // Ordenar por fecha descendente
        query += ' ORDER BY expense_date DESC, created_at DESC';

        // Paginación
        query += ' LIMIT ? OFFSET ?';
        params.push(parseInt(limit), parseInt(offset));

        const [expenses] = await pool.query(query, params);

        // Contar total de resultados (sin paginación)
        let countQuery = 'SELECT COUNT(*) as total FROM expenses WHERE user_id = ?';
        const countParams = [userId];

        if (start_date) {
            countQuery += ' AND expense_date >= ?';
            countParams.push(start_date);
        }
        if (end_date) {
            countQuery += ' AND expense_date <= ?';
            countParams.push(end_date);
        }
        if (category) {
            countQuery += ' AND category = ?';
            countParams.push(category);
        }
        if (search) {
            countQuery += ' AND (description LIKE ? OR category LIKE ?)';
            const searchPattern = `%${search}%`;
            countParams.push(searchPattern, searchPattern);
        }

        const [countResult] = await pool.query(countQuery, countParams);
        const total = countResult[0].total;

        res.json({
            success: true,
            data: {
                expenses,
                pagination: {
                    total,
                    limit: parseInt(limit),
                    offset: parseInt(offset),
                    hasMore: parseInt(offset) + expenses.length < total
                }
            }
        });
    } catch (error) {
        next(error);
    }
};

/**
 * @route   GET /api/expenses/:id
 * @desc    Obtener un gasto específico
 * @access  Private
 */
const getExpenseById = async (req, res, next) => {
    try {
        const { id } = req.params;
        const userId = req.user.id;

        const [expenses] = await pool.query(
            'SELECT * FROM expenses WHERE id = ? AND user_id = ?',
            [id, userId]
        );

        if (expenses.length === 0) {
            return res.status(404).json({
                success: false,
                message: 'Gasto no encontrado.'
            });
        }

        res.json({
            success: true,
            data: {
                expense: expenses[0]
            }
        });
    } catch (error) {
        next(error);
    }
};

/**
 * @route   POST /api/expenses
 * @desc    Crear nuevo gasto
 * @access  Private
 */
const createExpense = async (req, res, next) => {
    try {
        const { description, amount, category, expense_date, notes } = req.body;
        const userId = req.user.id;

        const expenseId = generateId();

        await pool.query(
            'INSERT INTO expenses (id, user_id, description, amount, category, expense_date, notes) VALUES (?, ?, ?, ?, ?, ?, ?)',
            [expenseId, userId, description, amount, category, expense_date, notes || null]
        );

        // Obtener el gasto creado
        const [expenses] = await pool.query(
            'SELECT * FROM expenses WHERE id = ?',
            [expenseId]
        );

        res.status(201).json({
            success: true,
            message: 'Gasto creado exitosamente',
            data: {
                expense: expenses[0]
            }
        });
    } catch (error) {
        next(error);
    }
};

/**
 * @route   PUT /api/expenses/:id
 * @desc    Actualizar gasto existente
 * @access  Private
 */
const updateExpense = async (req, res, next) => {
    try {
        const { id } = req.params;
        const userId = req.user.id;
        const { description, amount, category, expense_date, notes } = req.body;

        // Verificar que el gasto existe y pertenece al usuario
        const [existingExpenses] = await pool.query(
            'SELECT id FROM expenses WHERE id = ? AND user_id = ?',
            [id, userId]
        );

        if (existingExpenses.length === 0) {
            return res.status(404).json({
                success: false,
                message: 'Gasto no encontrado.'
            });
        }

        // Construir query de actualización dinámicamente
        const updates = [];
        const params = [];

        if (description !== undefined) {
            updates.push('description = ?');
            params.push(description);
        }
        if (amount !== undefined) {
            updates.push('amount = ?');
            params.push(amount);
        }
        if (category !== undefined) {
            updates.push('category = ?');
            params.push(category);
        }
        if (expense_date !== undefined) {
            updates.push('expense_date = ?');
            params.push(expense_date);
        }
        if (notes !== undefined) {
            updates.push('notes = ?');
            params.push(notes);
        }

        if (updates.length === 0) {
            return res.status(400).json({
                success: false,
                message: 'No se proporcionaron campos para actualizar.'
            });
        }

        params.push(id, userId);

        await pool.query(
            `UPDATE expenses SET ${updates.join(', ')} WHERE id = ? AND user_id = ?`,
            params
        );

        // Obtener el gasto actualizado
        const [updatedExpenses] = await pool.query(
            'SELECT * FROM expenses WHERE id = ?',
            [id]
        );

        res.json({
            success: true,
            message: 'Gasto actualizado exitosamente',
            data: {
                expense: updatedExpenses[0]
            }
        });
    } catch (error) {
        next(error);
    }
};

/**
 * @route   DELETE /api/expenses/:id
 * @desc    Eliminar gasto
 * @access  Private
 */
const deleteExpense = async (req, res, next) => {
    try {
        const { id } = req.params;
        const userId = req.user.id;

        // Verificar que el gasto existe y pertenece al usuario
        const [existingExpenses] = await pool.query(
            'SELECT id FROM expenses WHERE id = ? AND user_id = ?',
            [id, userId]
        );

        if (existingExpenses.length === 0) {
            return res.status(404).json({
                success: false,
                message: 'Gasto no encontrado.'
            });
        }

        await pool.query(
            'DELETE FROM expenses WHERE id = ? AND user_id = ?',
            [id, userId]
        );

        res.json({
            success: true,
            message: 'Gasto eliminado exitosamente'
        });
    } catch (error) {
        next(error);
    }
};

/**
 * @route   GET /api/expenses/summary
 * @desc    Obtener resumen estadístico de gastos
 * @access  Private
 */
const getExpenseSummary = async (req, res, next) => {
    try {
        const userId = req.user.id;

        // Total del mes actual
        const [monthlyTotal] = await pool.query(
            `SELECT COALESCE(SUM(amount), 0) as total
             FROM expenses
             WHERE user_id = ?
             AND YEAR(expense_date) = YEAR(CURRENT_DATE)
             AND MONTH(expense_date) = MONTH(CURRENT_DATE)`,
            [userId]
        );

        // Total por categoría (mes actual)
        const [categoryBreakdown] = await pool.query(
            `SELECT category, COUNT(*) as count, SUM(amount) as total
             FROM expenses
             WHERE user_id = ?
             AND YEAR(expense_date) = YEAR(CURRENT_DATE)
             AND MONTH(expense_date) = MONTH(CURRENT_DATE)
             GROUP BY category
             ORDER BY total DESC`,
            [userId]
        );

        // Gastos recientes (últimos 5)
        const [recentExpenses] = await pool.query(
            `SELECT * FROM expenses
             WHERE user_id = ?
             ORDER BY expense_date DESC, created_at DESC
             LIMIT 5`,
            [userId]
        );

        // Total general de todos los tiempos
        const [totalAll] = await pool.query(
            `SELECT COALESCE(SUM(amount), 0) as total, COUNT(*) as count
             FROM expenses
             WHERE user_id = ?`,
            [userId]
        );

        res.json({
            success: true,
            data: {
                currentMonth: {
                    total: parseFloat(monthlyTotal[0].total),
                    byCategory: categoryBreakdown.map(cat => ({
                        category: cat.category,
                        count: cat.count,
                        total: parseFloat(cat.total)
                    }))
                },
                allTime: {
                    total: parseFloat(totalAll[0].total),
                    count: totalAll[0].count
                },
                recentExpenses
            }
        });
    } catch (error) {
        next(error);
    }
};

module.exports = {
    getAllExpenses,
    getExpenseById,
    createExpense,
    updateExpense,
    deleteExpense,
    getExpenseSummary
};
