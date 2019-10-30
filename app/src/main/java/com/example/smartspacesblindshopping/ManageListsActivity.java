package com.example.smartspacesblindshopping;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ManageListsActivity extends MyActivity {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createLists(View view) {

        Intent intent = new Intent(this, ListActivity.class);
        startActivityForResult(intent, 20);
    }

    public void readList(View view) {
        Intent intent = new Intent(this, ReadActivity.class);
        intent.putExtra(MANAGE_OR_SHOP, "manage");
        startActivity(intent);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_lists);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 20)
        {
            if (resultCode == RESULT_OK && data != null) {

                ArrayList<String> result = data.getStringArrayListExtra(APPEND_TO_LIST);
                if(result == null || result.isEmpty()) return;

                ArrayList<String> tmp = ReadWriteCSV.readCSV(getApplicationContext(), PATH);

                LocalDateTime now = LocalDateTime.now();
                String fileName = now.toString()+ ".csv";

                ArrayList<String> list = new ArrayList<>();

                list.add(fileName);

                ReadWriteCSV.writeToCSV(this, list, PATH);

                ReadWriteCSV.writeToCSV(this, result, fileName);

                Toast.makeText(this, "List written", Toast.LENGTH_SHORT).show();

                Log.d("", result.toString());
            }
        }
    }
}
