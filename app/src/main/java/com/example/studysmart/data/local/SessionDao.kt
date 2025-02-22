package com.example.studysmart.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.studysmart.domain.model.Session
import kotlinx.coroutines.flow.Flow


@Dao
interface SessionDao {


    @Insert
    suspend fun insertSession(session: Session)

    @Delete
    suspend fun deleteSession(session: Session)

    @Query("SELECT * FROM session")
     fun  getAllSessions(): Flow<List<Session>>

     @Query("SELECT * FROM session WHERE sessionSubjectId = :subjectId")
     fun getRecentSessionsForSubject(subjectId: Int): Flow<List<Session>>

     @Query("SELECT SUM(duration) FROM session")
     fun getTotalSessionsDuration(): Flow<Long>

    @Query("SELECT SUM(duration) FROM session WHERE sessionSubjectId = :subjectId")
     fun getTotalSessionDurationBySubjectId(subjectId: Int): Flow<Long>


     @Query("DELETE  FROM session WHERE sessionSubjectId = :subjectId")
     suspend fun deleteSessionsBySubjectId(subjectId: Int)



}