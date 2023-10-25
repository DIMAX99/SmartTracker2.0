package com.example.smarttracker;

import static com.google.firebase.auth.FirebaseAuth.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smarttracker.fragments.DailyTaskFragment;
import com.example.smarttracker.fragments.HomeFragment;
import com.example.smarttracker.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarView;


public class MainActivity extends AppCompatActivity {

    Toolbar Toolbar;
    TextView Username;
    TextView Email;
    ImageView profile;
    FrameLayout frameLayout;
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WindowCompat.setDecorFitsSystemWindows(getWindow(),false);
        Toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(Toolbar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        Username = findViewById(R.id.username);
        Email = findViewById(R.id.email_toolbar);
        profile = findViewById(R.id.default_icon);

        Username.setText(getInstance().getCurrentUser().getDisplayName());
        Email.setText(getInstance().getCurrentUser().getEmail());

        Toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet();
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int NavId = item.getItemId();
                if(NavId==R.id.btmbar_Task){
                    LoadFrag(new HomeFragment(), true);
                } else if (NavId==R.id.btmbar_dailytask) {
                    LoadFrag(new DailyTaskFragment(), false);
                } else if (NavId==R.id.btmbar_profile) {
                    LoadFrag(new ProfileFragment(), false);
                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.btmbar_Task);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.toolbar_optmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
    private void showBottomSheet(){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_layout);
        // Find views within the BottomSheetLayout
        TextView emailTextView = bottomSheetDialog.findViewById(R.id.btm_dialog_email);
        Button logoutButton = bottomSheetDialog.findViewById(R.id.btm_Logout_btn);
        if (emailTextView != null) {
            emailTextView.setText(getInstance().getCurrentUser().getEmail());
        }
        logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    logout();
                    bottomSheetDialog.dismiss();
                }
        });
        bottomSheetDialog.show();
    }
    private void logout() {
        getInstance().signOut();
        startActivity(new Intent(MainActivity.this,Login.class));
        finish();
    }
    public void LoadFrag(Fragment fragment, boolean btmFlag){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if(btmFlag==true)
            ft.add(R.id.FrameContainer , fragment);
        else
            ft.replace(R.id.FrameContainer , fragment);
        ft.commit();
    }
}