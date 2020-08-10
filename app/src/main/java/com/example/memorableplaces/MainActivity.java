package com.example.memorableplaces;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView list;
    ArrayList<String> displayList;
    ArrayAdapter<String> adapter;
    static ArrayList<LatLng> latndLng =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = (ListView)findViewById(R.id.memorablePlaces);
        displayList=new ArrayList<String>();
        displayList.add("Add a Memorable Place");
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,displayList);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent map = new Intent(getApplicationContext(),MapsActivity.class);
                map.putExtra("place",i-1);
                startActivityForResult(map,1);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == 1) {
            if (data.hasExtra("Latitude")) {
                Log.i ("Latitude",data.getExtras().getString("Latitude"));
            }
            if (data.hasExtra("Longitude")) {
                Log.i ("Longitude",data.getExtras().getString("Longitude"));
            }
            if (data.hasExtra("address")){
                displayList.add(data.getExtras().getString("address"));
                Log.i ("address",data.getExtras().getString("address"));
                adapter.notifyDataSetChanged();
            }
            //updateList();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
   /* public  void updateList(){
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,displayList);
        list.setAdapter(adapter);
    }*/
}
