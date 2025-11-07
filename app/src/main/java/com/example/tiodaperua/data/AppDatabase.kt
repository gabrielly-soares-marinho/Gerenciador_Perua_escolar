package com.example.tiodaperua.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Adicionando os imports que faltavam
import com.example.tiodaperua.data.Condutor
import com.example.tiodaperua.data.Escola
import com.example.tiodaperua.data.Turma

@Database(entities = [Aluno::class, Responsavel::class, Turma::class, Escola::class, Condutor::class, User::class], version = 9, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun alunoDao(): AlunoDao
    abstract fun responsavelDao(): ResponsavelDao
    abstract fun turmaDao(): TurmaDao
    abstract fun escolaDao(): EscolaDao
    abstract fun condutorDao(): CondutorDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}