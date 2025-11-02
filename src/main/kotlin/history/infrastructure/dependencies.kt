package history.infrastructure

import core.ConnMySQL
import history.domain.IHistoryRepository
import history.infrastructure.adapters.MySQLHistoryRepository
import history.application.*
import history.infrastructure.controllers.*

data class DependenciesHistory(
    val createHistoryController: CreateHistoryController,
    val getAllHistoryController: GetAllHistoryController,
    val getHistoryByIdController: GetHistoryByIdController,
    val getHistoryByStudentController: GetHistoryByStudentController,
    val updateHistoryStatusController: UpdateHistoryStatusController
)

fun initHistory(conn: ConnMySQL): DependenciesHistory {
    val historyRepository: IHistoryRepository = MySQLHistoryRepository(conn)
    
    val createHistoryUseCase = CreateHistoryUseCase(historyRepository)
    val getAllHistoryUseCase = GetAllHistoryUseCase(historyRepository)
    val getHistoryByIdUseCase = GetHistoryByIdUseCase(historyRepository)
    val getHistoryByStudentUseCase = GetHistoryByStudentUseCase(historyRepository)
    val updateHistoryStatusUseCase = UpdateHistoryStatusUseCase(historyRepository)

    return DependenciesHistory(
        createHistoryController = CreateHistoryController(createHistoryUseCase),
        getAllHistoryController = GetAllHistoryController(getAllHistoryUseCase),
        getHistoryByIdController = GetHistoryByIdController(getHistoryByIdUseCase),
        getHistoryByStudentController = GetHistoryByStudentController(getHistoryByStudentUseCase),
        updateHistoryStatusController = UpdateHistoryStatusController(updateHistoryStatusUseCase)
    )
}