package com.saintrepublic.locationtracker.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.lifecycle.MutableLiveData
import com.saintrepublic.locationtracker.common.Event
import com.saintrepublic.locationtracker.common.TrackerData

class DatabaseService(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    val isDatabaseChangedEvent = MutableLiveData<Event<Unit>>()

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE_HISTORY ("
                    + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "$COLUMN_TIME TEXT, $COLUMN_LATITUDE TEXT,"
                    + "$COLUMN_LONGITUDE TEXT, $COLUMN_SPEED TEXT,"
                    + "$COLUMN_ACC_X TEXT, $COLUMN_ACC_Y TEXT, $COLUMN_ACC_Z TEXT,"
                    + "$COLUMN_ANG_X TEXT, $COLUMN_ANG_Y TEXT, $COLUMN_ANG_Z TEXT"
                    + ");"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_HISTORY")
        onCreate(db);
    }

    suspend fun save(data: TrackerData) {
        val values = ContentValues().apply {
            put(COLUMN_TIME, data.time)
            put(COLUMN_LATITUDE, data.latitude)
            put(COLUMN_LONGITUDE, data.longitude)
            put(COLUMN_SPEED, data.speed)
            put(COLUMN_ACC_X, data.accX)
            put(COLUMN_ACC_Y, data.accY)
            put(COLUMN_ACC_Z, data.accZ)
            put(COLUMN_ANG_X, data.angX)
            put(COLUMN_ANG_Y, data.angY)
            put(COLUMN_ANG_Z, data.angZ)
        }
        if (data.id < 0)
            data.id = writableDatabase.insert(TABLE_HISTORY, null, values)
        else
            writableDatabase.update(
                TABLE_HISTORY,
                values,
                "$COLUMN_ID=?",
                arrayOf("${data.id}")
            )

        isDatabaseChangedEvent.postValue(Event(Unit))
    }

    suspend fun selectAll(): List<TrackerData> {
        val list = mutableListOf<TrackerData>()
        readableDatabase.rawQuery("SELECT * FROM $TABLE_HISTORY ORDER BY $COLUMN_ID DESC", null)
            .use { cursor ->
                while (cursor.moveToNext()) {
                    list.add(
                        TrackerData(
                            cursor.getLong(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5),
                            cursor.getString(6),
                            cursor.getString(7),
                            cursor.getString(8),
                            cursor.getString(9),
                            cursor.getString(10)
                        )
                    )
                }
            }
        return list
    }

    companion object {
        private const val DATABASE_NAME = "tracker.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_HISTORY = "history"

        private const val COLUMN_ID = "_id"
        private const val COLUMN_TIME = "time"
        private const val COLUMN_LATITUDE = "latitude"
        private const val COLUMN_LONGITUDE = "longitude"
        private const val COLUMN_SPEED = "speed"
        private const val COLUMN_ACC_X = "acc_x"
        private const val COLUMN_ACC_Y = "acc_y"
        private const val COLUMN_ACC_Z = "acc_z"
        private const val COLUMN_ANG_X = "agl_x"
        private const val COLUMN_ANG_Y = "agl_y"
        private const val COLUMN_ANG_Z = "agl_z"
    }

}