package com.apex.asg.ui.viewmodels;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0018\u001a\u00020\u0019J\u0006\u0010\u001a\u001a\u00020\u0019J\u0016\u0010\u001b\u001a\u00020\u00192\u0006\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u000bJ\u001a\u0010\u001f\u001a\u00020\u00192\u0006\u0010 \u001a\u00020\u00052\n\b\u0002\u0010\u001c\u001a\u0004\u0018\u00010\u001dJ\u000e\u0010!\u001a\u00020\u00192\u0006\u0010\"\u001a\u00020\u0005J\u001a\u0010#\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u000bH\u0002R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\t\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\n\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000b0\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00050\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0017\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\b0\u00118F\u00a2\u0006\u0006\u001a\u0004\b\u0012\u0010\u0013R\u0019\u0010\u0014\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00050\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u000fR\u0019\u0010\u0016\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000b0\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u000f\u00a8\u0006$"}, d2 = {"Lcom/apex/asg/ui/viewmodels/KiriAIViewModel;", "Landroidx/lifecycle/ViewModel;", "()V", "_currentSpecialization", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "_messages", "Landroidx/compose/runtime/snapshots/SnapshotStateList;", "Lcom/apex/asg/ui/viewmodels/KiriMessage;", "_selectedFileName", "_selectedFileUri", "Landroid/net/Uri;", "currentSpecialization", "Lkotlinx/coroutines/flow/StateFlow;", "getCurrentSpecialization", "()Lkotlinx/coroutines/flow/StateFlow;", "messages", "", "getMessages", "()Ljava/util/List;", "selectedFileName", "getSelectedFileName", "selectedFileUri", "getSelectedFileUri", "clearFileSelection", "", "loadHistory", "onFileSelected", "context", "Landroid/content/Context;", "uri", "sendMessage", "content", "setSpecialization", "specialization", "uriToBase64", "app_debug"})
public final class KiriAIViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final androidx.compose.runtime.snapshots.SnapshotStateList<com.apex.asg.ui.viewmodels.KiriMessage> _messages = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<android.net.Uri> _selectedFileUri = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<android.net.Uri> selectedFileUri = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _selectedFileName = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> selectedFileName = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _currentSpecialization = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> currentSpecialization = null;
    
    public KiriAIViewModel() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.apex.asg.ui.viewmodels.KiriMessage> getMessages() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<android.net.Uri> getSelectedFileUri() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getSelectedFileName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getCurrentSpecialization() {
        return null;
    }
    
    public final void setSpecialization(@org.jetbrains.annotations.NotNull()
    java.lang.String specialization) {
    }
    
    public final void onFileSelected(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.net.Uri uri) {
    }
    
    public final void clearFileSelection() {
    }
    
    public final void loadHistory() {
    }
    
    private final java.lang.String uriToBase64(android.content.Context context, android.net.Uri uri) {
        return null;
    }
    
    public final void sendMessage(@org.jetbrains.annotations.NotNull()
    java.lang.String content, @org.jetbrains.annotations.Nullable()
    android.content.Context context) {
    }
}