package com.example.aplicaciondepruebafritz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class MostrarFoto extends Activity {
ImageView imgFoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_foto);

        imgFoto = this.findViewById(R.id.imgMostrarFoto);

/*
        Bundle b = this.getIntent().getExtras();

        byte[] byteArray = b.getByteArray("Foto");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
 */

        Bitmap bmp = MainActivity.getInstance().getBitmapFromMemCache("FotoMarcada");


        imgFoto.setImageBitmap(bmp);
    }
}
