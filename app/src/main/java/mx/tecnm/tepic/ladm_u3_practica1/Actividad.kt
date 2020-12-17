package mx.tecnm.tepic.ladm_u3_practica1

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteException

class Actividad(desc:String,fechaC:String,fechaE:String) {
    var descripcion=desc
    var fechaCaptura = fechaC
    var fechaEntrega = fechaE
    var id=0
    var error = -1

    val nombreBaseDatos = "Tareas"
    var puntero : Context?= null


    fun asignarPuntero(p: Context){
        puntero = p
    }


    fun insertar():Boolean{
        error = -1
        try{
            var base = BaseDatos(puntero,nombreBaseDatos,null,1)
            var insertar = base.writableDatabase
            var datos = ContentValues()

            //OBTENER LOS DATOS DE LA TABLA
            datos.put("Descripcion",descripcion)
            datos.put("FechaCaptura",fechaCaptura)
            datos.put("FechaEntrega",fechaEntrega)

            //EL insert(TABLA A INSERTAR, VALORES QUE ESTARÁN EN NULL, DATOS A INSERTAR
            var respuesta = insertar.insert("ACTIVIDADES","Id_actividad",datos)

            if(respuesta.toInt()==-1){
                error = 2
                return false
            }

        }catch (e: SQLiteException){
            error = 1
            return false
        }

        return true
    }

    fun buscar(id:String):Actividad{
        var actividadEncontrada = Actividad("-1","-1","-1")

        error = -1
        try{

            var base = BaseDatos(puntero!!,nombreBaseDatos,null,1)
            var select = base.readableDatabase
            var columnas = arrayOf("*")
            var idBuscar = arrayOf(id)

            var cursor = select.query("ACTIVIDADES",columnas,"Id_actividad = ?",idBuscar,null,null,null)

            if (cursor.moveToFirst()){

                actividadEncontrada.id = id.toInt()
                actividadEncontrada.descripcion = cursor.getString(1)
                actividadEncontrada.fechaCaptura = cursor.getString(2)
                actividadEncontrada.fechaEntrega = cursor.getString(3)

            }
            else{
                error = 4
            }

        }catch (e:SQLiteException){
            error = 1
        }

        return actividadEncontrada
    }
}