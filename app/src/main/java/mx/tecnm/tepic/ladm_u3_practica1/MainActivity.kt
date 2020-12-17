package mx.tecnm.tepic.ladm_u3_practica1

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteException
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    val nombreBaseDatos = "Tareas"
    var dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy/M/d", Locale.getDefault())
    var date = Date()
    var idImagen =""


    var fechaActual: String = dateFormat.format(date)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edit_fechaCaptura.setText(fechaActual)
        edit_fechaEntrega.setText(fechaActual)

        btn_imagen.setOnClickListener {

            var edit_id = EditText(this)

            AlertDialog.Builder(this)
                .setView(edit_id)
                .setTitle("ID ACTIVIDAD")
                .setMessage("INSERTE EL ID DE LA ACTIVIDAD")
                .setPositiveButton("OK"){d,i->
                    idImagen = edit_id.text.toString()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED){
                            val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                            requestPermissions(permissions, PERMISSION_CODE);
                        }
                        else{
                            subirImagen()
                        }
                    }
                    else{
                        subirImagen()
                    }
                }
                .setNegativeButton("CANCELAR"){d,i->}
                .show()
        }

        btn_capturar.setOnClickListener {
            if (edit_desc.text.isEmpty()) {
                mensaje("DEBE PONER UNA DESCRIPCION")
                return@setOnClickListener
            }
            else{
                insertarActividad()
            }
        }

        btn_irBuscar.setOnClickListener {
            var otroActivity = Intent(this,MainActivity2::class.java)
            startActivity(otroActivity)
        }

    }

    private fun insertarActividad(){

        try{
            var baseDatos = BaseDatos(this,nombreBaseDatos,null,1)
            var insertar = baseDatos.writableDatabase

            /////////////////////INSERTAR ACTIVIDAD//////////////////
            var act = Actividad(edit_desc.text.toString(),edit_fechaCaptura.text.toString(),edit_fechaEntrega.text.toString())
            act.asignarPuntero(this)
            var resultado = act.insertar()

            if(resultado == true) {

                mensaje("SE CAPTURO ACTIVIDAD")

            }
            else{

                when(act.error){
                    1 -> {dialogo("error en tabla, no se creó o no se conectó base datos")}
                    2 -> {dialogo("error no se pudo insertar")}
                }

            }
            /////////////////////INSERTAR ACTIVIDAD//////////////////
        }catch (error: SQLiteException){
            mensaje(error.message.toString())
        }
    }

    private fun subirImagen() {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
        //Permission code
        private val PERMISSION_CODE = 1001;
    }

    //handle requested permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size >0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup granted
                    subirImagen()
                }
                else{
                    //permission from popup denied
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            imageView.setImageURI(data?.data)
            val bitmap = (imageView.drawable as BitmapDrawable).bitmap
            var imagenInsertar = getBytes(bitmap)
            var resultado = insertarImagen(idImagen,imagenInsertar)
            if (resultado == true) {
                dialogo("SE INSERTO LA IMAGEN")
            } else {
                dialogo("ERROR")
            }
        }
    }



    fun obtenerImagen(imageView: ImageView): ByteArray? {
        val bitmapDrawable = imageView.getDrawable() as BitmapDrawable
        val bitmap: Bitmap
        if (bitmapDrawable == null) {
            imageView.buildDrawingCache()
            bitmap = imageView.getDrawingCache()
            imageView.buildDrawingCache(false)
        } else {
            bitmap = bitmapDrawable.bitmap
        }
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        var imageString = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
        return stream.toByteArray()
    }



    fun mensaje(mensaje:String){
        AlertDialog.Builder(this)
            .setMessage(mensaje)
            .show()
    }

    fun dialogo(s:String){
        AlertDialog.Builder(this)
            .setTitle("ATENCION").setMessage(s)
            .setPositiveButton("OK"){d,i->}
            .show()
    }
    fun getBytes(bitmap: Bitmap):ByteArray{
        var stream= ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
        return stream.toByteArray()
    }//getBytes


    fun insertarImagen(idAct:String,foto:ByteArray): Boolean{
        try {
            var base =BaseDatos(this,nombreBaseDatos,null,1)
            var insertar = base.writableDatabase
            var datos = ContentValues()
            datos.put("Foto",foto)
            datos.put("Id_actividad",idAct)
            var respuesta = insertar.insert("EVIDENCIAS","Id_evidencia",datos)
            if(respuesta.toInt() == -1){
                return false
            }
        }catch (e: SQLiteException){
            return false
        }

        return true
    }
}