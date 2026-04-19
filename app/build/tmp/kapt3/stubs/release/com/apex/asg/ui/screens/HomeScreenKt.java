package com.apex.asg.ui.screens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000B\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\b\u0010\u0000\u001a\u00020\u0001H\u0007\u001aJ\u0010\u0002\u001a\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\u00042\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\nH\u0007\u00f8\u0001\u0000\u00a2\u0006\u0004\b\f\u0010\r\u001a\u0010\u0010\u000e\u001a\u00020\u00012\u0006\u0010\u000f\u001a\u00020\u0004H\u0007\u001a,\u0010\u0010\u001a\u00020\u00012\u0006\u0010\u0011\u001a\u00020\u00122\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00150\u00142\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00010\u0017H\u0007\u001a\"\u0010\u0018\u001a\u00020\u00012\b\b\u0002\u0010\u0019\u001a\u00020\u001a2\u000e\b\u0002\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00010\u0017H\u0007\u001a\u0016\u0010\u001b\u001a\u00020\u00012\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00010\u0017H\u0007\u001a\b\u0010\u001c\u001a\u00020\u0001H\u0007\u001a\u0010\u0010\u001d\u001a\u00020\u00012\u0006\u0010\u001e\u001a\u00020\u001fH\u0007\u001a\u0016\u0010 \u001a\u00020\u00012\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00150\u0014H\u0007\u0082\u0002\u0007\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006!"}, d2 = {"DiscoverCommunityCard", "", "EventItemCard", "day", "", "month", "title", "location", "tag", "tagBg", "Landroidx/compose/ui/graphics/Color;", "tagText", "EventItemCard-IhcsPec", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JJ)V", "GreetingSection", "userName", "HomeContent", "user", "Lcom/apex/asg/data/remote/UserDto;", "events", "", "Lcom/apex/asg/data/remote/EventDto;", "onNavigateToNotifications", "Lkotlin/Function0;", "HomeScreen", "viewModel", "Lcom/apex/asg/ui/viewmodels/HomeViewModel;", "HomeTopBar", "RepositoriesSection", "RepositoryCard", "item", "Lcom/apex/asg/ui/screens/RepoItem;", "UpcomingEventsSection", "app_release"})
public final class HomeScreenKt {
    
    @androidx.compose.runtime.Composable()
    public static final void HomeScreen(@org.jetbrains.annotations.NotNull()
    com.apex.asg.ui.viewmodels.HomeViewModel viewModel, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToNotifications) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void HomeContent(@org.jetbrains.annotations.NotNull()
    com.apex.asg.data.remote.UserDto user, @org.jetbrains.annotations.NotNull()
    java.util.List<com.apex.asg.data.remote.EventDto> events, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToNotifications) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void HomeTopBar(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToNotifications) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void GreetingSection(@org.jetbrains.annotations.NotNull()
    java.lang.String userName) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void DiscoverCommunityCard() {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void RepositoriesSection() {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void RepositoryCard(@org.jetbrains.annotations.NotNull()
    com.apex.asg.ui.screens.RepoItem item) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void UpcomingEventsSection(@org.jetbrains.annotations.NotNull()
    java.util.List<com.apex.asg.data.remote.EventDto> events) {
    }
}