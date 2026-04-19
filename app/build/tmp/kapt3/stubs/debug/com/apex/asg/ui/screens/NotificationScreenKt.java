package com.apex.asg.ui.screens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u00006\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\u001a\u0012\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u0007\u001a\u001e\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00010\bH\u0007\u001a*\u0010\t\u001a\u00020\u00012\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00060\u000b2\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u00010\rH\u0007\u001a \u0010\u000f\u001a\u00020\u00012\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00010\b2\b\b\u0002\u0010\u0011\u001a\u00020\u0012H\u0007\u00a8\u0006\u0013"}, d2 = {"EmptyNotifications", "", "modifier", "Landroidx/compose/ui/Modifier;", "NotificationItem", "notification", "Lcom/apex/asg/data/remote/NotificationDto;", "onClick", "Lkotlin/Function0;", "NotificationList", "notifications", "", "onMarkRead", "Lkotlin/Function1;", "", "NotificationScreen", "onBack", "viewModel", "Lcom/apex/asg/ui/viewmodels/NotificationViewModel;", "app_debug"})
public final class NotificationScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void NotificationScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack, @org.jetbrains.annotations.NotNull()
    com.apex.asg.ui.viewmodels.NotificationViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void NotificationList(@org.jetbrains.annotations.NotNull()
    java.util.List<com.apex.asg.data.remote.NotificationDto> notifications, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onMarkRead) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void NotificationItem(@org.jetbrains.annotations.NotNull()
    com.apex.asg.data.remote.NotificationDto notification, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void EmptyNotifications(@org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
}