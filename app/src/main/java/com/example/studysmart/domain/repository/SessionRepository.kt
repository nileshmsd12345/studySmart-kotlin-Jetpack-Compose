package com.example.studysmart.domain.repository

import com.example.studysmart.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {

    suspend fun insertSession(session: Session)

     fun getAllSessions() : Flow<List<Session>>


    suspend fun deleteSession(session: Session)

    fun getRecentFiveSessions(): Flow<List<Session>>


    fun getRecentSessionsForSubject(subjectId: Int): Flow<List<Session>>


    fun getTotalSessionsDuration(): Flow<Long>


    fun getTotalSessionDurationBySubject(subjectId: Int): Flow<Long>


    suspend fun deleteSessionsBySubjectId(subjectId: Int)

    fun getRecentTenSessionForSubject(subjectId: Int): Flow<List<Session>>


}