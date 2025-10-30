package teachers.application

import teachers.domain.ITeacherRepository

class DeleteTeacherUseCase(private val db: ITeacherRepository) {

    suspend fun execute(teacherId: Int) {
        val existingTeacher =
                db.getById(teacherId) ?: throw IllegalArgumentException("El teacher no existe")

        if (existingTeacher.teacherId != null) {
            println("Eliminando teacher con ID: ${existingTeacher.teacherId}")
        }

        db.delete(teacherId)
    }
}
