package de.oljg.glac.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.oljg.glac.core.settings.data.ClockSettingsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideClockSettingsRepository(
        @ApplicationContext context: Context
    ): ClockSettingsRepository {
        return ClockSettingsRepository(context)
    }
}