

class DeleteStudent_UseCase (private val repository: StudentRepository){

    suspend operator fun invoke(studentId): StudentResponse {
        //verificar que el estudiante exista

        val deleted = repository.deleteStudent(studentId)

        return if (deleted){
            StudentResponse(
                success = true,
                message = "Estudiante eliminado exitosamente"
            )
        } else {
            StudentResponse(
                success = false,
                message = "Error al eliminar estudiante"
            )
        }
    }
}