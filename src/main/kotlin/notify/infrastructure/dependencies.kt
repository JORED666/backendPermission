package notify.infrastructure

import core.ConnMySQL
import notify.domain.INotifyRepository
import notify.infrastructure.adapters.MySQLNotifyRepository
import notify.application.*
import notify.infrastructure.controllers.*
import notify.infrastructure.websocket.WebSocketManager
import students.domain.IStudentRepository
import permitsTeacher.domain.IPermitTeacherRepository
import tutors.domain.ITutorRepository  // ← AGREGAR

data class DependenciesNotify(
    val getNotificationsController: GetNotificationsController,
    val markAsReadController: MarkAsReadController,
    val notificationService: NotificationService,
    val webSocketManager: WebSocketManager
)

fun initNotify(
    conn: ConnMySQL,
    studentRepository: IStudentRepository,
    permitTeacherRepository: IPermitTeacherRepository,
    tutorRepository: ITutorRepository  
): DependenciesNotify {
    val notifyRepository: INotifyRepository = MySQLNotifyRepository(conn)
    val webSocketManager = WebSocketManager()
    
    val createNotificationUseCase = CreateNotificationUseCase(notifyRepository)
    val getNotificationsByUserUseCase = GetNotificationsByUserUseCase(notifyRepository)
    val markAsReadUseCase = MarkAsReadUseCase(notifyRepository)
    
    val notificationService = NotificationService(
        createNotificationUseCase,
        webSocketManager,
        studentRepository,
        permitTeacherRepository,
        tutorRepository  
    )

    return DependenciesNotify(
        getNotificationsController = GetNotificationsController(getNotificationsByUserUseCase),
        markAsReadController = MarkAsReadController(markAsReadUseCase),
        notificationService = notificationService,
        webSocketManager = webSocketManager
    )
}