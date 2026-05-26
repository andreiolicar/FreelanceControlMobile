package com.example.freelancecontrolmobile.data

import android.content.ContentValues
import android.database.Cursor
import com.example.freelancecontrolmobile.model.TimeEntry

/**
 * Camada de acesso a dados (DAO) para a tabela de registros de tempo (time_entries).
 * Realiza as operações de CRUD utilizando SQLite direto.
 */
class TimeEntryDao(private val dbHelper: DatabaseHelper) {

    /**
     * Insere um novo registro de tempo no banco de dados.
     * @return O ID da linha inserida ou -1 se houver erro.
     */
    fun insertTimeEntry(timeEntry: TimeEntry): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("project_id", timeEntry.projectId)
            put("work_date", timeEntry.workDate)
            put("start_time", timeEntry.startTime)
            put("end_time", timeEntry.endTime)
            put("duration_minutes", timeEntry.durationMinutes)
            put("notes", timeEntry.notes)
        }
        return db.insert(DatabaseHelper.TABLE_TIME_ENTRIES, null, values)
    }

    /**
     * Busca todos os registros de tempo cadastrados, ordenados por data decrescente.
     */
    fun getAllTimeEntries(): List<TimeEntry> {
        val entries = mutableListOf<TimeEntry>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_TIME_ENTRIES,
            null, null, null, null, null, "work_date DESC"
        )

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    entries.add(mapCursorToTimeEntry(it))
                } while (it.moveToNext())
            }
        }
        return entries
    }

    /**
     * Busca um registro de tempo específico pelo seu ID.
     */
    fun getTimeEntryById(id: Int): TimeEntry? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_TIME_ENTRIES,
            null,
            "id = ?",
            arrayOf(id.toString()),
            null, null, null
        )

        return cursor.use {
            if (it.moveToFirst()) mapCursorToTimeEntry(it) else null
        }
    }

    /**
     * Busca todos os registros de tempo vinculados a um projeto específico.
     */
    fun getTimeEntriesByProjectId(projectId: Int): List<TimeEntry> {
        val entries = mutableListOf<TimeEntry>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_TIME_ENTRIES,
            null,
            "project_id = ?",
            arrayOf(projectId.toString()),
            null, null, "work_date DESC"
        )

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    entries.add(mapCursorToTimeEntry(it))
                } while (it.moveToNext())
            }
        }
        return entries
    }

    /**
     * Atualiza os dados de um registro de tempo existente.
     * @return O número de linhas afetadas ou 0 se o ID for nulo.
     */
    fun updateTimeEntry(timeEntry: TimeEntry): Int {
        val entryId = timeEntry.id ?: return 0
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("project_id", timeEntry.projectId)
            put("work_date", timeEntry.workDate)
            put("start_time", timeEntry.startTime)
            put("end_time", timeEntry.endTime)
            put("duration_minutes", timeEntry.durationMinutes)
            put("notes", timeEntry.notes)
        }
        return db.update(
            DatabaseHelper.TABLE_TIME_ENTRIES,
            values,
            "id = ?",
            arrayOf(entryId.toString())
        )
    }

    /**
     * Remove um registro de tempo do banco de dados pelo seu ID.
     * @return O número de linhas afetadas.
     */
    fun deleteTimeEntry(id: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            DatabaseHelper.TABLE_TIME_ENTRIES,
            "id = ?",
            arrayOf(id.toString())
        )
    }

    /**
     * Mapeia o registro atual do Cursor para um objeto TimeEntry.
     */
    private fun mapCursorToTimeEntry(cursor: Cursor): TimeEntry {
        return TimeEntry(
            id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
            projectId = cursor.getInt(cursor.getColumnIndexOrThrow("project_id")),
            workDate = cursor.getString(cursor.getColumnIndexOrThrow("work_date")),
            startTime = cursor.getString(cursor.getColumnIndexOrThrow("start_time")),
            endTime = cursor.getString(cursor.getColumnIndexOrThrow("end_time")),
            durationMinutes = cursor.getInt(cursor.getColumnIndexOrThrow("duration_minutes")),
            notes = cursor.getString(cursor.getColumnIndexOrThrow("notes"))
        )
    }
}
