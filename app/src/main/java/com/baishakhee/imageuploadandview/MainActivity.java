package com.baishakhee.imageuploadandview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import com.baishakhee.imageuploadandview.databinding.ActivityMainBinding;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
ActivityMainBinding binding;
    private static final int PICK_IMAGES_REQUEST = 1;
    private List<String> selectedImages = new ArrayList<>();
    private List<String> listOfImages = new ArrayList<>();

    ImageAdapter imageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding= DataBindingUtil.setContentView(this,R.layout.activity_main);
        imageAdapter=new ImageAdapter(this,selectedImages);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(imageAdapter);
       binding.imageCapture.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               pickImages();
           }
       });
    }



    private void pickImages() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, PICK_IMAGES_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == RESULT_OK) {
            ClipData clipData = data.getClipData();
            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri imageUri = clipData.getItemAt(i).getUri();
                    String base64 = convertToBase64(imageUri);
                    System.out.println("base64...................."+base64);
                    listOfImages.add(base64);

                    selectedImages.add(imageUri.toString());
                }
            } else {
                Uri imageUri = data.getData();
                String base64 = convertToBase64(imageUri);
                listOfImages.add(base64);
                selectedImages.add(imageUri.toString());

            }
            String s=String.join(",",listOfImages);
            System.out.println("listOfImages..............................."+s);
            imageAdapter=new ImageAdapter(this,selectedImages);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            binding.recyclerView.setAdapter(imageAdapter);
            imageAdapter.notifyDataSetChanged();
        }

/*
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == RESULT_OK && data != null) {

            if (data.getClipData() != null) {
                // User selected multiple images
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    String base64 = convertToBase64(imageUri);
                    // Upload the base64 string to the API
                //    uploadToApi(base64);
                }
            } else if (data.getData() != null) {
                // User selected a single image
                Uri imageUri = data.getData();
                String base64 = convertToBase64(imageUri);
                // Upload the base64 string to the API
              //  uploadToApi(base64);
            }
        }*/
    }

    private String convertToBase64(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

