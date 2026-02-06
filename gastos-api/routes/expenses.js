const express = require('express');
const router = express.Router();
const {
    getAllExpenses,
    getExpenseById,
    createExpense,
    updateExpense,
    deleteExpense,
    getExpenseSummary
} = require('../controllers/expenseController');
const auth = require('../middleware/auth');
const {
    validateCreateExpense,
    validateUpdateExpense,
    validateUUID,
    validateExpenseFilters
} = require('../middleware/validators');

// Todas las rutas de expenses requieren autenticación
router.use(auth);

// Rutas de gastos
router.get('/', validateExpenseFilters, getAllExpenses);
router.get('/summary', getExpenseSummary);
router.get('/:id', validateUUID, getExpenseById);
router.post('/', validateCreateExpense, createExpense);
router.put('/:id', validateUUID, validateUpdateExpense, updateExpense);
router.delete('/:id', validateUUID, deleteExpense);

module.exports = router;
