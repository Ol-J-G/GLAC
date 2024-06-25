package de.oljg.glac.feature_alarm.domain.use_case

import de.oljg.glac.feature_alarm.domain.manager.AlarmScheduler
import de.oljg.glac.feature_alarm.domain.model.Alarm
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class ReScheduleAllAlarms(
    private val alarmScheduler: AlarmScheduler
) {
    suspend fun execute(alarms: List<Alarm>) {
        /**
         * Cancel and (re-)schedule all alarms. Will be done in two different jobs independently.
         *
         * Note:
         * To cancel and (re-)schedule immediately each after another alarm, seems to be too
         * fast for Android's AlarmManager to handle correctly (some tests resulted in duplicated
         * scheduled alarms^^ (I guess, cancel was aborted and only schedule finished)
         * => Divide and conquer is the solution here! => Two different coroutines ...
         */
        coroutineScope {
            val cancelJob = launch(start = CoroutineStart.LAZY) {
                alarms.forEach { alarm ->
                    alarmScheduler.cancel(alarm)
                }
            }

            // 1st => Cancel all scheduled alarms
            cancelJob.start()

            // 2nd => Wait for cancelJob to be finished
            cancelJob.join()

            val scheduleJob = launch(start = CoroutineStart.LAZY) {
                alarms.forEach { alarm ->
                    alarmScheduler.schedule(alarm)
                }
            }

            // 3rd => Re-schedule all alarms
            scheduleJob.start()
        }
    }
}
