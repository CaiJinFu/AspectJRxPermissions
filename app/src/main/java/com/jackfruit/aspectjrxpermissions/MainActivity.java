package com.jackfruit.aspectjrxpermissions;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.jackfruit.aspectjrxpermissions.annotation.AndroidPermission;
import com.jackfruit.aspectjrxpermissions.annotation.ExecuteTime;
import com.jackfruit.aspectjrxpermissions.databinding.ActivityMainBinding;
import com.jackfruit.aspectjrxpermissions.rxpermissions2.Permission;

import io.reactivex.functions.Consumer;

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
        // getPermission();
        getPermissionResult(new Consumer< Permission >() {
          @Override
          public void accept(Permission permission) throws Exception {
            if (permission.granted) {
              // 用户已经同意该权限
              Log.i("TAG", "获取权限1: ");
            } else if (permission.shouldShowRequestPermissionRationale) {
              Log.i("TAG", "拒绝权限1: ");
            } else {
              Log.i("TAG", "点击不再询问权限1: ");
            }
          }
        });
      }
    });
    getTime();
  }

  @AndroidPermission(permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
      Manifest.permission.WRITE_EXTERNAL_STORAGE})
  public void getPermission() {
    Log.i("TAG", "获取权限成功了: ");
  }

  @AndroidPermission(permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
      Manifest.permission.WRITE_EXTERNAL_STORAGE})
  public void getPermissionResult(Consumer< Permission > onNext) {
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