package it.gadg.contagiapp.splash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;

import it.gadg.contagiapp.autenticazione.Login;
import it.gadg.contagiapp.MainActivity;
import it.gadg.contagiapp.R;

public class Splash extends AppCompatActivity {

    ImageView splash;
    LottieAnimationView lottieAnimationView;
    ViewPager viewPager;
    ScreenSlidePageAdapter pagerAdapter;

    private static final int numeroPagineIntro = 3;
    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        splash = findViewById(R.id.img);
        lottieAnimationView = findViewById(R.id.splash);
        splash.animate().translationY(-3000).setDuration(2500).setStartDelay(3000);
        lottieAnimationView.animate().translationY(1400).setDuration(2500).setStartDelay(3000);

        if(null ==  FirebaseAuth.getInstance().getCurrentUser()){
            viewPager = findViewById(R.id.pager);
            pagerAdapter = new ScreenSlidePageAdapter(getSupportFragmentManager());
            viewPager.setAdapter(pagerAdapter);
            animation = AnimationUtils.loadAnimation(this,R.anim.anim);
            viewPager.startAnimation(animation);
        }else{

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.anim,R.anim.no_animation);
                }
            }, 2000);


        }

    }

    public void splashAuth(View view) {
        Intent i = new Intent(getApplicationContext(), Login.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    private static class ScreenSlidePageAdapter extends FragmentStatePagerAdapter{

         public ScreenSlidePageAdapter(@NonNull FragmentManager fm) {
             super(fm);
         }

         @NonNull
         @Override
         public Fragment getItem(int position) {
             switch (position){
                 case 0:
                     OnBoardginFragment1 pag1 = new OnBoardginFragment1();
                     return pag1;
                 case 1:
                     OnBoardginFragment2 pag2 = new OnBoardginFragment2();
                     return pag2;
                 case 2:
                     OnBoardginFragment3 pag3 = new OnBoardginFragment3();
                     return pag3;
             }
             return null;
         }

         @Override
         public int getCount() {
             return numeroPagineIntro;
         }
     }
}