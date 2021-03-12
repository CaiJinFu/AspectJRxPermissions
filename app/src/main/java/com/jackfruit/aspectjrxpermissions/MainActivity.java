package com.jackfruit.aspectjrxpermissions;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.jackfruit.aspectjrxpermissions.annotation.AndroidPermission;
import com.jackfruit.aspectjrxpermissions.annotation.ExecuteTime;
import com.jackfruit.aspectjrxpermissions.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
    // setContentView(R.layout.activity_main);
    setContentView(binding.getRoot());
    binding.tv.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        getPermission();
      }
    });
    getTime();
  }

  @AndroidPermission(permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
      Manifest.permission.WRITE_EXTERNAL_STORAGE})
  public void getPermission() {
    Log.i("TAG", "获取权限成功了: ");
  }

  @ExecuteTime
  public void getTime() {
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    Log.i("TAG", "执行了getTime方法: ");
  }
}