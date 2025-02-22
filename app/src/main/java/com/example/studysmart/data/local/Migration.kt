package com.example.studysmart.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS Task_new (
                taskId INTEGER PRIMARY KEY AUTOINCREMENT,
                taskSubjectId INTEGER NOT NULL,
                title TEXT NOT NULL,
                description TEXT NOT NULL,
                dueDate INTEGER NOT NULL,
                priority INTEGER NOT NULL,
                relatedToSubject TEXT NOT NULL,
                isComplete INTEGER NOT NULL CHECK (isComplete IN (0,1))
            )
            """
        )

        database.execSQL(
            """
            INSERT INTO Task_new (taskId, taskSubjectId, title, description, dueDate, priority, relatedToSubject, isComplete)
            SELECT taskId, taskSubjectId, title, description, 
                   CASE 
                       WHEN dueDate GLOB '[0-9]*' THEN dueDate
                       ELSE CAST(strftime('%s', dueDate) AS INTEGER) * 1000
                   END,
                   priority, relatedToSubject, isComplete 
            FROM Task
            """
        )

        database.execSQL("DROP TABLE Task")
        database.execSQL("ALTER TABLE Task_new RENAME TO Task")
    }
}

val MIGRATION = MIGRATION_1_2