package com.apex.asg.data.local;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\'\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H&\u00a8\u0006\u0005"}, d2 = {"Lcom/apex/asg/data/local/ASGDatabase;", "Landroidx/room/RoomDatabase;", "()V", "eventDao", "Lcom/apex/asg/data/local/EventDao;", "app_release"})
@androidx.room.Database(entities = {com.apex.asg.data.local.EventEntity.class}, version = 1, exportSchema = false)
public abstract class ASGDatabase extends androidx.room.RoomDatabase {
    
    public ASGDatabase() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.apex.asg.data.local.EventDao eventDao();
}