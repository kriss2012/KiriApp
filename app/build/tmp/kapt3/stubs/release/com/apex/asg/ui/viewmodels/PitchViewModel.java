package com.apex.asg.ui.viewmodels;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J$\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0011J\u0006\u0010\u0012\u001a\u00020\u000bJ\u001c\u0010\u0013\u001a\u00020\u000b2\u0006\u0010\u0014\u001a\u00020\u00152\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0011R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u00078F\u00a2\u0006\u0006\u001a\u0004\b\b\u0010\t\u00a8\u0006\u0016"}, d2 = {"Lcom/apex/asg/ui/viewmodels/PitchViewModel;", "Landroidx/lifecycle/ViewModel;", "()V", "_pitches", "Landroidx/compose/runtime/snapshots/SnapshotStateList;", "Lcom/apex/asg/data/remote/PitchDto;", "pitches", "", "getPitches", "()Ljava/util/List;", "backPitch", "", "pitchId", "", "points", "", "onSuccess", "Lkotlin/Function0;", "loadPitches", "submitPitch", "request", "Lcom/apex/asg/data/remote/CreatePitchRequest;", "app_release"})
public final class PitchViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final androidx.compose.runtime.snapshots.SnapshotStateList<com.apex.asg.data.remote.PitchDto> _pitches = null;
    
    public PitchViewModel() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.apex.asg.data.remote.PitchDto> getPitches() {
        return null;
    }
    
    public final void loadPitches() {
    }
    
    public final void submitPitch(@org.jetbrains.annotations.NotNull()
    com.apex.asg.data.remote.CreatePitchRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onSuccess) {
    }
    
    public final void backPitch(@org.jetbrains.annotations.NotNull()
    java.lang.String pitchId, int points, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onSuccess) {
    }
}