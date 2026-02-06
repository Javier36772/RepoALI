const { v4: uuidv4 } = require('uuid');

/**
 * Genera un UUID v4 para usar como ID único
 */
const generateId = () => {
    return uuidv4();
};

module.exports = { generateId };
