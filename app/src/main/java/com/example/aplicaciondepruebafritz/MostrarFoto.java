package com.example.aplicaciondepruebafritz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

public class MostrarFoto extends AppCompatActivity {
ImageView imgFoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_foto);

        imgFoto = this.findViewById(R.id.imgMostrarFoto);
        Bundle b = getIntent().getExtras();

        Bitmap bit = (Bitmap) b.get("Foto");

        imgFoto.setImageBitmap(bit);
    }
}
