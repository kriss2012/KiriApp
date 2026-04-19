package com.apex.asg.ui.viewmodels;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rJ\u0016\u0010\u000e\u001a\u00020\u000b2\u0006\u0010\u000f\u001a\u00020\r2\u0006\u0010\f\u001a\u00020\rR\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t\u00a8\u0006\u0010"}, d2 = {"Lcom/apex/asg/ui/viewmodels/NotificationViewModel;", "Landroidx/lifecycle/ViewModel;", "()V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/apex/asg/ui/viewmodels/NotificationState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "fetchNotifications", "", "userId", "", "markAsRead", "notificationId", "app_debug"})
public final class NotificationViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.apex.asg.ui.viewmodels.NotificationState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.apex.asg.ui.viewmodels.NotificationState> uiState = null;
    
    public NotificationViewModel() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.apex.asg.ui.viewmodels.NotificationState> getUiState() {
        return null;
    }
    
    public final void fetchNotifications(@org.jetbrains.annotations.NotNull()
    java.lang.String userId) {
    }
    
    public final void markAsRead(@org.jetbrains.annotations.NotNull()
    java.lang.String notificationId, @org.jetbrains.annotations.NotNull()
    java.lang.String userId) {
    }
}