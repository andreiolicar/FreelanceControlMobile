package com.example.freelancecontrolmobile.data

import android.content.ContentValues
import android.database.Cursor
import com.example.freelancecontrolmobile.model.Client

/**
 * Camada de acesso a dados (DAO) para a tabela de clientes.
 * Realiza as operações de CRUD utilizando SQLite direto.
 */
class ClientDao(private val dbHelper: DatabaseHelper) {

    /**
     * Insere um novo cliente no banco de dados.
     * @return O ID da linha inserida ou -1 se houver erro.
     */
    fun insertClient(client: Client): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", client.name)
            put("email", client.email)
            put("phone", client.phone)
        }
        return db.insert(DatabaseHelper.TABLE_CLIENTS, null, values)
    }

    /**
     * Busca todos os clientes cadastrados, ordenados por nome.
     */
    fun getAllClients(): List<Client> {
        val clients = mutableListOf<Client>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_CLIENTS,
            null, null, null, null, null, "name ASC"
        )

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    clients.add(mapCursorToClient(it))
                } while (it.moveToNext())
            }
        }
        return clients
    }

    /**
     * Busca um cliente específico pelo seu ID.
     */
    fun getClientById(id: Int): Client? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_CLIENTS,
            null,
            "id = ?",
            arrayOf(id.toString()),
            null, null, null
        )

        return cursor.use {
            if (it.moveToFirst()) mapCursorToClient(it) else null
        }
    }

    /**
     * Atualiza os dados de um cliente existente.
     * @return O número de linhas afetadas ou 0 se o ID for nulo.
     */
    fun updateClient(client: Client): Int {
        val id = client.id ?: return 0
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", client.name)
            put("email", client.email)
            put("phone", client.phone)
        }
        return db.update(
            DatabaseHelper.TABLE_CLIENTS,
            values,
            "id = ?",
            arrayOf(id.toString())
        )
    }

    /**
     * Remove um cliente do banco de dados pelo seu ID.
     * @return O número de linhas afetadas.
     */
    fun deleteClient(id: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            DatabaseHelper.TABLE_CLIENTS,
            "id = ?",
            arrayOf(id.toString())
        )
    }

    /**
     * Mapeia o registro atual do Cursor para um objeto Client.
     */
    private fun mapCursorToClient(cursor: Cursor): Client {
        return Client(
            id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
            name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
            email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
            phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"))
        )
    }
}
