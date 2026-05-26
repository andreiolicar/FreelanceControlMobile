package com.example.freelancecontrolmobile.data

import android.content.ContentValues
import android.database.Cursor
import com.example.freelancecontrolmobile.model.Project

/**
 * Camada de acesso a dados (DAO) para a tabela de projetos.
 * Realiza as operações de CRUD utilizando SQLite direto.
 */
class ProjectDao(private val dbHelper: DatabaseHelper) {

    /**
     * Insere um novo projeto no banco de dados.
     * @return O ID da linha inserida ou -1 se houver erro.
     */
    fun insertProject(project: Project): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("client_id", project.clientId)
            put("title", project.title)
            put("description", project.description)
            put("hourly_rate", project.hourlyRate)
            put("status", project.status)
        }
        return db.insert(DatabaseHelper.TABLE_PROJECTS, null, values)
    }

    /**
     * Busca todos os projetos cadastrados, ordenados por título.
     */
    fun getAllProjects(): List<Project> {
        val projects = mutableListOf<Project>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_PROJECTS,
            null, null, null, null, null, "title ASC"
        )

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    projects.add(mapCursorToProject(it))
                } while (it.moveToNext())
            }
        }
        return projects
    }

    /**
     * Busca um projeto específico pelo seu ID.
     */
    fun getProjectById(id: Int): Project? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_PROJECTS,
            null,
            "id = ?",
            arrayOf(id.toString()),
            null, null, null
        )

        return cursor.use {
            if (it.moveToFirst()) mapCursorToProject(it) else null
        }
    }

    /**
     * Busca todos os projetos vinculados a um cliente específico.
     */
    fun getProjectsByClientId(clientId: Int): List<Project> {
        val projects = mutableListOf<Project>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_PROJECTS,
            null,
            "client_id = ?",
            arrayOf(clientId.toString()),
            null, null, "title ASC"
        )

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    projects.add(mapCursorToProject(it))
                } while (it.moveToNext())
            }
        }
        return projects
    }

    /**
     * Atualiza os dados de um projeto existente.
     * @return O número de linhas afetadas ou 0 se o ID for nulo.
     */
    fun updateProject(project: Project): Int {
        val projectId = project.id ?: return 0
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("client_id", project.clientId)
            put("title", project.title)
            put("description", project.description)
            put("hourly_rate", project.hourlyRate)
            put("status", project.status)
        }
        return db.update(
            DatabaseHelper.TABLE_PROJECTS,
            values,
            "id = ?",
            arrayOf(projectId.toString())
        )
    }

    /**
     * Remove um projeto do banco de dados pelo seu ID.
     * @return O número de linhas afetadas.
     */
    fun deleteProject(id: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            DatabaseHelper.TABLE_PROJECTS,
            "id = ?",
            arrayOf(id.toString())
        )
    }

    /**
     * Mapeia o registro atual do Cursor para um objeto Project.
     */
    private fun mapCursorToProject(cursor: Cursor): Project {
        return Project(
            id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
            clientId = cursor.getInt(cursor.getColumnIndexOrThrow("client_id")),
            title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
            description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
            hourlyRate = cursor.getDouble(cursor.getColumnIndexOrThrow("hourly_rate")),
            status = cursor.getString(cursor.getColumnIndexOrThrow("status"))
        )
    }
}
