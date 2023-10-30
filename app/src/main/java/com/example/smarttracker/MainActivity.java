package com.example.smarttracker;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttracker.Adapters.CategoryAdapter;
import com.example.smarttracker.Adapters.TaskAdapter;
import com.example.smarttracker.Model.CategoryModel;
import com.example.smarttracker.Model.TaskModel;
import com.example.smarttracker.Utils.DatabaseHandler;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity implements DialogCloseListener{

    Toolbar Toolbar;
    TextView Username;
    TextView Email;
    ImageView profile;
    private RecyclerView tasksRecyclerView;
    private RecyclerView categoryRecyclerView;
    private TaskAdapter taskAdapter;
    private FloatingActionButton fab;
    private DatabaseHandler dbHandler;
    private List<TaskModel> tasklist;
    private CategoryAdapter categoryAdapter;
    private List<CategoryModel> categorylist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WindowCompat.setDecorFitsSystemWindows(getWindow(),false);
        Toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(Toolbar);
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

        dbHandler = new DatabaseHandler(this);
        dbHandler.open();

        tasklist = new ArrayList<>();
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        categoryRecyclerView=findViewById(R.id.categoryRecyclerView);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        categoryAdapter = new CategoryAdapter(dbHandler,this);
        tasksRecyclerView.setLayoutManager((new LinearLayoutManager(this)));
        taskAdapter = new TaskAdapter(dbHandler,this,categoryAdapter);
        categoryRecyclerView.setAdapter(categoryAdapter);
        tasksRecyclerView.setAdapter(taskAdapter);
        fab =findViewById(R.id.fab);
        categorylist = dbHandler.getAllCategories(null);
        categoryAdapter.setcategory(categorylist);
        tasklist = dbHandler.getAllTasks(null);
        taskAdapter.setTasks(tasklist);
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new TaskRecyclerItemTouchHelper(taskAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });

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
        TextView profilename = bottomSheetDialog.findViewById(R.id.btm_profilename);
        if (emailTextView != null) {
            emailTextView.setText(getInstance().getCurrentUser().getEmail());
        }
        if (profilename != null) {
            profilename.setText(getInstance().getCurrentUser().getDisplayName());
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
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (dbHandler != null) {
            dbHandler.close(); // Close the database when the activity is destroyed
        }
    }
    private void logout() {
        getInstance().signOut();
        startActivity(new Intent(MainActivity.this,Login.class));
        finish();
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        tasklist = dbHandler.getAllTasks(null);
        categorylist = dbHandler.getAllCategories(null);
        Collections.reverse(tasklist);
        categoryAdapter.setcategory(categorylist);
        taskAdapter.setTasks(tasklist);
        categoryAdapter.notifyDataSetChanged();
        taskAdapter.notifyDataSetChanged();
        Log.d("HomeFragment", "handleDialogClose: Dialog closed, data updated");
    }


}