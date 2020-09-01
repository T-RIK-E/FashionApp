package com.example.rikit.fashionapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.core.view.GestureDetectorCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawer;
    private BottomNavigationView bottomNav;
    private DatabaseReference db;
    private int num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i= new Intent(MainActivity.this, ScrapeToBase.class);
        MainActivity.this.startService(i);

        final ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(1);
        db = FirebaseDatabase.getInstance().getReference();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {

                // Check if this is the page you want.
                if (viewPager.getCurrentItem() == 1)
                {
                    viewPager.setCurrentItem(1);
                    num++;
                    db.child("Nike").child(Integer.toString(num)).child("like").setValue("true");
                    ValueEventListener eventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                            TextView productTitle = (TextView) findViewById(R.id.productTitle);
                            ImageView image = (ImageView) findViewById(R.id.itemImage);
                            TextView price = (TextView) findViewById(R.id.productPrice);

                            productTitle.setText(dataSnapshot.child("Nike").child(Integer.toString(num)).child("name").getValue(String.class));
                            String url = dataSnapshot.child("Nike").child(Integer.toString(num)).child("image").getValue(String.class);
                            new DownloadImageFromInternet(image).execute(url);
                            image.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent implicit = new Intent(Intent.ACTION_VIEW, Uri.parse(dataSnapshot.child("Nike").child(Integer.toString(num)).child("url").getValue(String.class)));
                                    startActivity(implicit);
                                }
                            });
                            price.setText(dataSnapshot.child(Integer.toString(num)).child("org_price").getValue(String.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    };
                    db.addListenerForSingleValueEvent(eventListener);
                }
                else{
                    viewPager.setCurrentItem(1);
                    num++;
                    db.child("Nike").child(Integer.toString(num)).child("like").setValue("false");
                }
            }
        });

        bottomNav = findViewById(R.id.bottom_navigation_bar);
        bottomNav.setOnNavigationItemSelectedListener(navBarListener);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(navDrawerListener);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }

    //Navigation bar

    private BottomNavigationView.OnNavigationItemSelectedListener navBarListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()){
                        case R.id.bar_browse:
                            selectedFragment = new browseFragment();
                            break;
                        case R.id.bar_developerFavorites:
                            selectedFragment = new developerFragment();
                            break;
                    }

                    if (selectedFragment == null)
                    {

                        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.bar_developerFavorites);
                        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                        return true;
                    }

                    else
                    {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedFragment).commit();
                        return true;
                    }


                }
            };


    //Navigation drawer
    private NavigationView.OnNavigationItemSelectedListener navDrawerListener =
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    BottomNavigationView bnv = (BottomNavigationView) findViewById(R.id.bottom_navigation_bar);
                    switch (item.getItemId()) {
                        case R.id.nav_message:
                            findViewById(R.id.fragment_container).setVisibility(View.GONE);
                            bnv.setVisibility(View.VISIBLE);
                            break;
                        case R.id.nav_chat:
                            findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new ChatFragment()).commit();
                            bnv.setVisibility(View.GONE);
                            break;
                        case R.id.nav_profile:
                            findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new AccountFragment()).commit();
                            bnv.setVisibility(View.GONE);
                            break;
                        case R.id.nav_settings:
                            findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new MessageFragment()).commit();
                            bnv.setVisibility(View.GONE);
                            break;
                    }

                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                }
            };

    //Navigation drawer

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    public void animateHeart(ImageView view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        prepareAnimation(scaleAnimation);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        prepareAnimation(alphaAnimation);

        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(alphaAnimation);
        animation.addAnimation(scaleAnimation);
        animation.setDuration(700);
        animation.setFillAfter(true);

        view.startAnimation(animation);

    }

    private Animation prepareAnimation(Animation animation){
        animation.setRepeatCount(1);
        animation.setRepeatMode(Animation.REVERSE);
        return animation;
    }
}
