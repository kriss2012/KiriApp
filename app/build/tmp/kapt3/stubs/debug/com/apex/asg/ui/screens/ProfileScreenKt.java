package com.apex.asg.ui.screens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u00004\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\u001a$\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003H\u0007\u001a\u001e\u0010\u0005\u001a\u00020\u00012\u0006\u0010\u0006\u001a\u00020\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003H\u0007\u001a:\u0010\t\u001a\u00020\u00012\u0006\u0010\n\u001a\u00020\u000b2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003H\u0007\u001a\u001e\u0010\r\u001a\u00020\u00012\u0006\u0010\n\u001a\u00020\u000b2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003H\u0007\u001aB\u0010\u000e\u001a\u00020\u00012\b\b\u0002\u0010\u000f\u001a\u00020\u00102\u000e\b\u0002\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003H\u0007\u001a\u001c\u0010\u0011\u001a\u00020\u0001*\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0014H\u0007\u00a8\u0006\u0016"}, d2 = {"DashboardMenu", "", "onNavigateToConnections", "Lkotlin/Function0;", "onNavigateToActivity", "DashboardMenuCard", "item", "Lcom/apex/asg/ui/screens/DashboardMenuItem;", "onClick", "ProfileContent", "user", "Lcom/apex/asg/data/remote/UserDto;", "onNavigateToEdit", "ProfileHeroSection", "ProfileScreen", "viewModel", "Lcom/apex/asg/ui/viewmodels/ProfileViewModel;", "StatBox", "Landroidx/compose/foundation/layout/RowScope;", "value", "", "label", "app_debug"})
public final class ProfileScreenKt {
    
    @androidx.compose.runtime.Composable()
    public static final void ProfileScreen(@org.jetbrains.annotations.NotNull()
    com.apex.asg.ui.viewmodels.ProfileViewModel viewModel, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToEdit, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToConnections, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToActivity) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void ProfileContent(@org.jetbrains.annotations.NotNull()
    com.apex.asg.data.remote.UserDto user, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToEdit, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToConnections, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToActivity) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void ProfileHeroSection(@org.jetbrains.annotations.NotNull()
    com.apex.asg.data.remote.UserDto user, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToEdit) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void StatBox(@org.jetbrains.annotations.NotNull()
    androidx.compose.foundation.layout.RowScope $this$StatBox, @org.jetbrains.annotations.NotNull()
    java.lang.String value, @org.jetbrains.annotations.NotNull()
    java.lang.String label) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void DashboardMenu(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToConnections, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToActivity) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void DashboardMenuCard(@org.jetbrains.annotations.NotNull()
    com.apex.asg.ui.screens.DashboardMenuItem item, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
}