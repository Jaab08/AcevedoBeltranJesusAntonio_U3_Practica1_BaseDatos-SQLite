package mx.tecnm.tepic.ladm_u3_practica1

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDatos(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase?) {

        db?.execSQL("CREATE TABLE ACTIVIDADES(ID_ACT INTEGER PRIMARY KEY AUTOINCREMENT, DESCRIPCION VARCHAR(200), FECHACAPTURA DATE, FECHAENTREGA DATE)")

        db?.execSQL("CREATE TABLE EVIDENCIAS(ID_EV INTEGER PRIMARY KEY AUTOINCREMENT, ID_ACT INTEGER, FOTO BLOB, FOREIGN KEY(ID_ACT) REFERENCES ACTIVIDADES (ID_ACT))")

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

}