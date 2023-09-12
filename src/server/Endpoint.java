package server;

public enum Endpoint {
    GetTasks, GetTaskById, PostTask, DeleteTasks, DeleteTaskById,
    GetSubTasks, GetSubTaskById, PostSubTask, DeleteSubTasks, DeleteSubTaskById,
    GetEpics, GetEpicById, PostEpic, DeleteEpics, DeleteEpicById,
    GetHistory, getPrioritizedTasks, GetSubByEpic,
    Unknown
}
