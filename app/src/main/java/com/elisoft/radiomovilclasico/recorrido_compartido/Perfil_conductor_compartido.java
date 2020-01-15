package com.elisoft.radiomovilclasico.recorrido_compartido;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.elisoft.radiomovilclasico.Empresa_radio_taxi;
import com.elisoft.radiomovilclasico.R;

import java.io.InputStream;

public class Perfil_conductor_compartido extends AppCompatActivity implements View.OnClickListener {



    TextView tv_nombre,tv_placa,tv_marca,tv_color,tv_empresa,tv_celular;
    ImageView im_perfil;

    LinearLayout ll_celular,ll_empresa;

    String id_empresa="",id_conductor="",s_nombre="",s_marca="",s_placa="",s_color="",s_celular="",s_empresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_conductor_compartido);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tv_nombre=(TextView)findViewById(R.id.tv_nombre);
        tv_marca=(TextView)findViewById(R.id.tv_marca);
        tv_placa=(TextView)findViewById(R.id.tv_placa);
        tv_color=(TextView)findViewById(R.id.tv_color);
        im_perfil=(ImageView) findViewById(R.id.im_perfil);

        ll_empresa=(LinearLayout)findViewById(R.id.ll_empresa);
        ll_celular=(LinearLayout) findViewById(R.id.ll_celular);
        tv_celular=(TextView)findViewById(R.id.tv_celular);
        tv_empresa=(TextView)findViewById(R.id.tv_empresa);

        try{
            Bundle bundle=getIntent().getExtras();
            id_conductor= String.valueOf( bundle.getInt("id_conductor"));
            s_nombre=bundle.getString("nombre");
            s_marca=bundle.getString("marca");
            s_placa=bundle.getString("placa");
            s_color=bundle.getString("color");
            s_celular=bundle.getString("celular");
            s_empresa=bundle.getString("empresa");
            id_empresa=bundle.getString("id_empresa");


            cargar_datos();
            getImage(id_conductor);
        }catch (Exception e)
        {
            finish();
        }



        ll_empresa.setOnClickListener(this);
        ll_celular.setOnClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.ll_empresa){
            Intent empresa=new Intent(this,Empresa_radio_taxi.class);
            empresa.putExtra("id_empresa",id_empresa);
            startActivity(empresa);
        }
        if(R.id.ll_celular==v.getId()){

            Intent intentll = new Intent(Intent.ACTION_CALL);
            intentll.setPackage("com.android.phone");
            intentll.setData(Uri.parse("tel: +591"+s_celular));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                startActivity(intentll);
            }else
                startActivity(intentll);
        }
    }

    public void cargar_datos()
    {
        tv_nombre.setText(s_nombre);
        tv_marca.setText(s_marca);
        tv_placa.setText(s_placa);
        tv_color.setText(s_color);
        tv_celular.setText(s_celular);
        tv_empresa.setText(s_empresa);
    }


    private void getImage(String id)//
    {
        class GetImage extends AsyncTask<String,Void,Bitmap> {


            ImageView bmImage;


            public GetImage(ImageView bmImage) {
                this.bmImage = bmImage;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);

                Drawable dw = new BitmapDrawable(getResources(), bitmap);
                //se edita la imagen para ponerlo en circulo.

                if( bitmap==null)
                { dw = getResources().getDrawable(R.mipmap.ic_perfil);}

                imagen_circulo(dw,bmImage);

            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected Bitmap doInBackground(String... strings) {
                String url = getString(R.string.servidor)+"frmTaxi.php?opcion=get_imagen&id_conductor="+strings[0];//hace consulta ala Bd para recurar la imagen

                Bitmap mIcon =null;
                try {
                    InputStream in = new java.net.URL(url).openStream();
                    mIcon = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                }
                return mIcon;
            }
        }

        GetImage gi = new GetImage(im_perfil);
        gi.execute(id);
    }

    public void imagen_circulo(Drawable id_imagen, ImageView imagen) {
        Bitmap originalBitmap = ((BitmapDrawable) id_imagen).getBitmap();
        if (originalBitmap.getWidth() > originalBitmap.getHeight()) {
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getHeight(), originalBitmap.getHeight());
        } else if (originalBitmap.getWidth() < originalBitmap.getHeight()) {
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getWidth());
        }

//creamos el drawable redondeado
        RoundedBitmapDrawable roundedDrawable =
                RoundedBitmapDrawableFactory.create(getResources(), originalBitmap);

//asignamos el CornerRadius
        roundedDrawable.setCornerRadius(originalBitmap.getWidth());
        try {
            imagen.setImageDrawable(roundedDrawable);
        }catch (Exception e)
        {

        }
    }
}
