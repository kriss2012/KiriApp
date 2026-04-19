package com.apex.asg.ui.screens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000>\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a\b\u0010\u0000\u001a\u00020\u0001H\u0007\u001a\b\u0010\u0002\u001a\u00020\u0001H\u0007\u001a\u0010\u0010\u0003\u001a\u00020\u00012\u0006\u0010\u0004\u001a\u00020\u0005H\u0007\u001a@\u0010\u0006\u001a\u00020\u00012\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\f2\u0018\u0010\r\u001a\u0014\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00010\u000eH\u0007\u001aJ\u0010\u0010\u001a\u00020\u00012\u0006\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\u0011\u001a\u00020\n2\u0006\u0010\t\u001a\u00020\n2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\f2\u0018\u0010\r\u001a\u0014\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00010\u000eH\u0007\u001a\u0012\u0010\u0012\u001a\u00020\u00012\b\b\u0002\u0010\u0013\u001a\u00020\u0014H\u0007\u001a\u001c\u0010\u0015\u001a\u00020\u0001*\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u000f2\u0006\u0010\u0018\u001a\u00020\u000fH\u0007\u00a8\u0006\u0019"}, d2 = {"AchievementChipsRow", "", "DashboardMenu", "DashboardMenuCard", "item", "Lcom/apex/asg/ui/screens/DashboardMenuItem;", "ProfileContent", "user", "Lcom/apex/asg/data/remote/UserDto;", "isEditing", "", "onEditToggle", "Lkotlin/Function0;", "onSave", "Lkotlin/Function2;", "", "ProfileHeroSection", "isVerified", "ProfileScreen", "viewModel", "Lcom/apex/asg/ui/viewmodels/ProfileViewModel;", "StatBox", "Landroidx/compose/foundation/layout/RowScope;", "value", "label", "app_release"})
public final class ProfileScreenKt {
    
    @androidx.compose.runtime.Composable()
    public static final void ProfileScreen(@org.jetbrains.annotations.NotNull()
    com.apex.asg.ui.viewmodels.ProfileViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void ProfileContent(@org.jetbrains.annotations.NotNull()
    com.apex.asg.data.remote.UserDto user, boolean isEditing, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onEditToggle, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super java.lang.String, ? super java.lang.String, kotlin.Unit> onSave) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void ProfileHeroSection(@org.jetbrains.annotations.NotNull()
    com.apex.asg.data.remote.UserDto user, boolean isVerified, boolean isEditing, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onEditToggle, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super java.lang.String, ? super java.lang.String, kotlin.Unit> onSave) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void StatBox(@org.jetbrains.annotations.NotNull()
    androidx.compose.foundation.layout.RowScope $this$StatBox, @org.jetbrains.annotations.NotNull()
    java.lang.String value, @org.jetbrains.annotations.NotNull()
    java.lang.String label) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void AchievementChipsRow() {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void DashboardMenu() {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void DashboardMenuCard(@org.jetbrains.annotations.NotNull()
    com.apex.asg.ui.screens.DashboardMenuItem item) {
    }
}