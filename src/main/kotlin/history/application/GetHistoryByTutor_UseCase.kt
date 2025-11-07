package history.application

import history.domain.IHistoryRepository
import history.domain.dto.HistoryWithDetailsResponse

class GetHistoryByTutorUseCase(private val repository: IHistoryRepository) {
    suspend fun execute(tutorId: Int): List<HistoryWithDetailsResponse> {
        val data = repository.getByTutorIdWithDetails(tutorId)
        return data.map { HistoryWithDetailsResponse.fromMap(it) }
    }
}