package com.example.freelancecontrolmobile.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// classe responsável por criar e gerenciar o banco de dados sqlite
class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "freelance_control.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_CLIENTS = "clients"
        const val TABLE_PROJECTS = "projects"
        const val TABLE_TIME_ENTRIES = "time_entries"
    }

    // ativa o suporte a foreign keys no sqlite
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    // cria as tabelas quando o banco é iniciado pela primeira vez
    override fun onCreate(db: SQLiteDatabase) {
        createClientsTable(db)
        createProjectsTable(db)
        createTimeEntriesTable(db)
    }

    // recria as tabelas caso a versão do banco seja alterada
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TIME_ENTRIES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PROJECTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CLIENTS")
        onCreate(db)
    }

    // tabela de clientes
    private fun createClientsTable(db: SQLiteDatabase) {
        val sql = """
            CREATE TABLE $TABLE_CLIENTS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                email TEXT,
                phone TEXT
            )
        """.trimIndent()

        db.execSQL(sql)
    }

    // tabela de projetos com foreign key para clientes
    private fun createProjectsTable(db: SQLiteDatabase) {
        val sql = """
            CREATE TABLE $TABLE_PROJECTS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                client_id INTEGER NOT NULL,
                title TEXT NOT NULL,
                description TEXT,
                hourly_rate REAL NOT NULL,
                status TEXT NOT NULL DEFAULT 'ativo',
                FOREIGN KEY (client_id) REFERENCES $TABLE_CLIENTS(id) ON DELETE CASCADE
            )
        """.trimIndent()

        db.execSQL(sql)
    }

    // tabela de registros de horas com foreign key para projetos
    private fun createTimeEntriesTable(db: SQLiteDatabase) {
        val sql = """
            CREATE TABLE $TABLE_TIME_ENTRIES (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                project_id INTEGER NOT NULL,
                work_date TEXT NOT NULL,
                start_time TEXT NOT NULL,
                end_time TEXT NOT NULL,
                duration_minutes INTEGER NOT NULL,
                notes TEXT,
                FOREIGN KEY (project_id) REFERENCES $TABLE_PROJECTS(id) ON DELETE CASCADE
            )
        """.trimIndent()

        db.execSQL(sql)
    }
}