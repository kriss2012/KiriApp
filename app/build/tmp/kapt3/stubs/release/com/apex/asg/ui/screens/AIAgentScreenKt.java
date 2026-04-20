package com.apex.asg.ui.screens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000,\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a\"\u0010\u0000\u001a\u00020\u00012\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u0007\u001a\b\u0010\u0006\u001a\u00020\u0001H\u0007\u001aX\u0010\u0007\u001a\u00020\u00012\u0006\u0010\b\u001a\u00020\t2\u0012\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00010\u000b2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\b\u0010\u000e\u001a\u0004\u0018\u00010\t2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003H\u0007\u001a\u0010\u0010\u0010\u001a\u00020\u00012\u0006\u0010\u0011\u001a\u00020\u0012H\u0007\u001a$\u0010\u0013\u001a\u00020\u00012\u0006\u0010\u0014\u001a\u00020\t2\u0012\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00010\u000bH\u0007\u00a8\u0006\u0016"}, d2 = {"AIAgentScreen", "", "onBack", "Lkotlin/Function0;", "vm", "Lcom/apex/asg/ui/viewmodels/KiriAIViewModel;", "KiriEmptyState", "KiriInputBar", "text", "", "onTextChange", "Lkotlin/Function1;", "onSend", "onAttachClick", "selectedFileName", "onCancelAttachment", "KiriMessageBubble", "msg", "Lcom/apex/asg/ui/viewmodels/KiriMessage;", "SpecializationSelector", "selected", "onSelected", "app_release"})
public final class AIAgentScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void AIAgentScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack, @org.jetbrains.annotations.NotNull()
    com.apex.asg.ui.viewmodels.KiriAIViewModel vm) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void KiriInputBar(@org.jetbrains.annotations.NotNull()
    java.lang.String text, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onTextChange, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onSend, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onAttachClick, @org.jetbrains.annotations.Nullable()
    java.lang.String selectedFileName, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onCancelAttachment) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void KiriEmptyState() {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void KiriMessageBubble(@org.jetbrains.annotations.NotNull()
    com.apex.asg.ui.viewmodels.KiriMessage msg) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void SpecializationSelector(@org.jetbrains.annotations.NotNull()
    java.lang.String selected, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onSelected) {
    }
}