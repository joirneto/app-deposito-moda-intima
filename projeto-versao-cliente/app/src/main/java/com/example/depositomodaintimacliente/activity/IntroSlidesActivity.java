package com.example.depositomodaintimacliente.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.depositomodaintimacliente.R;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;
import com.heinrichreimersoftware.materialintro.slide.Slide;

public class IntroSlidesActivity extends IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_intro_slides);

        setButtonBackVisible(false);
        setButtonNextVisible(false);

       addSlide(new FragmentSlide.Builder()
               .background(R.color.colorPrimary)
               .fragment(R.layout.intro_01)
               .build()
        );

    }

    public void iniciarAtividade(View view){
        startActivity(new Intent(getApplicationContext(),VerificacaoActivity.class));
        finish();
    }
}

