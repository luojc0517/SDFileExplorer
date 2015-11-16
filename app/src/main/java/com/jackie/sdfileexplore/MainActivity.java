package com.jackie.sdfileexplore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    TextView textView;
    File currParent;
    File[] currFiles;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.lvFiles);
        textView = (TextView) findViewById(R.id.tvPath);
        button = (Button) findViewById(R.id.btnParent);
        File root = new File("/mnt/sdcard/");
        if (root.exists()) {
            currParent = root;
            currFiles = root.listFiles();
            //如果这里报空指针错误，检查权限是否配置
            Log.d("jackie", "  " + currFiles);
            inflateFiles(currFiles);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currFiles[position].isFile()) return;
                File[] children = currFiles[position].listFiles();
                if (children == null||children.length==0){
                    Toast.makeText(MainActivity.this,"该目录下没有文件",Toast.LENGTH_SHORT).show();
                }
                currParent = currFiles[position];
                currFiles = children;
                inflateFiles(currFiles);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(!currParent.getCanonicalPath().equals("/mnt/sdcard/")){
                        currParent = currParent.getParentFile();
                        currFiles = currParent.listFiles();
                        inflateFiles(currFiles);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void inflateFiles(File[] files) {
        List<Map<String, Object>> listFiles = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < files.length; i++) {
            Map<String, Object> itemFile = new HashMap<String, Object>();
            if (files[i].isDirectory()) {
                itemFile.put("icon", R.drawable.folder);
            } else {
                itemFile.put("icon", R.drawable.file);
            }
            itemFile.put("fileName", files[i].getName());
            listFiles.add(itemFile);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(
                this,
                listFiles,
                R.layout.item_file,
                new String[]{"icon", "fileName"},
                new int[]{R.id.ivItemFile, R.id.tvItemFile});
        listView.setAdapter(simpleAdapter);
        try {
            textView.setText("当前路径是" + currParent.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
