const mysql = require('mysql2/promise');
const fs = require('fs');
const path = require('path');
const readline = require('readline');
require('dotenv').config();

const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

const askQuestion = (question) => {
    return new Promise((resolve) => {
        rl.question(question, (answer) => {
            resolve(answer);
        });
    });
};

const runSetup = async () => {
    console.log('\n📦 Configuración Automática de Base de Datos - GastosApp\n');

    let password = process.env.DB_PASSWORD;

    // Si no hay password en .env, preguntar al usuario
    if (password === undefined || password === '') {
        console.log('⚠️ No se encontró DB_PASSWORD en el archivo .env');
        password = await askQuestion('🔑 Por favor ingresa tu contraseña de MySQL (root): ');
    } else {
        console.log('✅ Usando contraseña definida en .env');
    }

    const config = {
        host: process.env.DB_HOST || 'localhost',
        port: process.env.DB_PORT || 3306,
        user: process.env.DB_USER || 'root',
        password: password,
        multipleStatements: true // Importante para ejecutar script SQL
    };

    let connection;

    try {
        console.log('🔌 Conectando a MySQL...');
        connection = await mysql.createConnection(config);
        console.log('✅ Conexión exitosa.');

        console.log('📂 Leyendo script SQL...');
        const sqlPath = path.join(__dirname, '..', 'gastos_app_database.sql');
        let sql = fs.readFileSync(sqlPath, 'utf8');

        // Limpieza de SQL para compatibilidad con el driver de Node.js
        // 1. Quitar líneas de DELIMITER (solo son para CLI/Workbench)
        sql = sql.replace(/DELIMITER\s+[\S]+/ig, '');
        // 2. Reemplazar los delimitadores personalizados (//) por el estándar (;)
        // Solo si están al final de una línea o seguidos de espacio/salto de línea
        sql = sql.replace(/\/\/\s*$/gm, ';');
        sql = sql.replace(/\/\/\s+/g, '; ');

        console.log('⚙️ Ejecutando script de base de datos...');
        await connection.query(sql);

        console.log('\n✅ ¡Base de datos instalada correctamente!');
        console.log('✅ Tablas creadas, datos de ejemplo insertados.');

    } catch (error) {
        console.error('\n❌ Ocurrió un error:', error.message);
        if (error.code === 'ECONNREFUSED') {
            console.error('👉 Asegúrate de que MySQL esté ejecutándose.');
        } else if (error.code === 'ER_ACCESS_DENIED_ERROR') {
            console.error('👉 Contraseña incorrecta. Verifica tu configuración.');
        }
    } finally {
        if (connection) await connection.end();
        rl.close();
        process.exit();
    }
};

runSetup();
