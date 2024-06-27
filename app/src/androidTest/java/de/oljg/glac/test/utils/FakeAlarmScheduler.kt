package de.oljg.glac.test.utils

import de.oljg.glac.feature_alarm.domain.manager.AlarmScheduler
import de.oljg.glac.feature_alarm.domain.model.Alarm

/**
 * Since it's very likely, that Google has been tested AlarmManager exhaustively,
 * there is no need to test this here/again, so, assuming every alarm is going to
 * scheduleed/canceled successfully.
 */
class FakeAlarmScheduler: AlarmScheduler {
    override fun schedule(alarm: Alarm): Boolean {
        return true
    }

    override fun cancel(alarm: Alarm): Boolean {
        return true
    }
}
