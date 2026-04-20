package com.apex.asg.ui.screens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000*\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u001a&\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u0007H\u0007\u001a \u0010\b\u001a\u00020\u00012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\b\b\u0002\u0010\n\u001a\u00020\u000bH\u0007\u001a\u001a\u0010\f\u001a\u00020\u00012\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\r\u001a\u00020\u000eH\u0007\u00a8\u0006\u000f"}, d2 = {"ConnectionItem", "", "user", "Lcom/apex/asg/data/remote/UserDto;", "isRequest", "", "onAccept", "Lkotlin/Function0;", "ConnectionsScreen", "onBack", "viewModel", "Lcom/apex/asg/ui/viewmodels/ConnectionsViewModel;", "EmptyConnections", "modifier", "Landroidx/compose/ui/Modifier;", "app_release"})
public final class ConnectionsScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void ConnectionsScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack, @org.jetbrains.annotations.NotNull()
    com.apex.asg.ui.viewmodels.ConnectionsViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void ConnectionItem(@org.jetbrains.annotations.NotNull()
    com.apex.asg.data.remote.UserDto user, boolean isRequest, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onAccept) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void EmptyConnections(boolean isRequest, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
}