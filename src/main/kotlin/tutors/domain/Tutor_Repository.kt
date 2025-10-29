package tutors.domain

import tutors.domain.entities.Tutor
import tutors.domain.entities.TutorWithDetails

interface ITutorRepository {
    suspend fun save(tutor: Tutor): Tutor
    suspend fun getAll(): List<Tutor>
    suspend fun getAllWithDetails(): List<TutorWithDetails>
    suspend fun getById(id: Int): Tutor?
    suspend fun getByIdWithDetails(id: Int): TutorWithDetails? 
    suspend fun getByUserId(userId: Int): Tutor?
    suspend fun update(tutor: Tutor)
    suspend fun delete(id: Int)
}