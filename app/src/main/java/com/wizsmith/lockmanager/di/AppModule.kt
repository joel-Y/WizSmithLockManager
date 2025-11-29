package com.wizsmith.lockmanager.di

import android.content.Context
import androidx.room.Room
import com.wizsmith.lockmanager.data.local.AppDatabase
import com.wizsmith.lockmanager.data.local.dao.LockDao
import com.wizsmith.lockmanager.data.local.dao.LockEventDao
import com.wizsmith.lockmanager.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "wizsmith_database"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideLockDao(database: AppDatabase): LockDao {
        return database.lockDao()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideLockEventDao(database: AppDatabase): LockEventDao {
        return database.lockEventDao()
    }
}