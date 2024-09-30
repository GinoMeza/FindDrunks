package com.gin.screenbeody;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.gin.screenbeody.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.ml.modeldownloader.CustomModel;
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions;
import com.google.firebase.ml.modeldownloader.DownloadType;
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private Interpreter interpreter;  // TFLite model interpreter
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean cameraPermissionGranted = result.getOrDefault(Manifest.permission.CAMERA, false);

                if (cameraPermissionGranted != null && cameraPermissionGranted) {
                    openCamera();
                } else {
                    Snackbar.make(binding.getRoot(), "Permiso de cámara no otorgado", Snackbar.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndRequestPermissions();
            }
        });

        // Descargar el modelo de Firebase
        downloadModel();
    }

    private void downloadModel() {
        CustomModelDownloadConditions conditions = new CustomModelDownloadConditions.Builder()
                .build();

        FirebaseModelDownloader.getInstance()
                .getModel("modelo_final", DownloadType.LOCAL_MODEL, conditions)
                .addOnSuccessListener(model -> {
                    // El modelo se ha descargado correctamente
//                    try (FileInputStream modelFile = new FileInputStream(model.getFile())) {
//                        interpreter = new Interpreter(modelFile);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    File modelFile = model.getFile();  // Obtener el archivo del modelo
                    if (modelFile != null) {
                        interpreter = new Interpreter(modelFile);  // Inicializar el intérprete con el archivo
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Error al descargar el modelo", Toast.LENGTH_SHORT).show();
                });
    }

    private void checkAndRequestPermissions() {
        boolean cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

        if (cameraPermission) {
            openCamera();
        } else {
            requestPermissionLauncher.launch(new String[]{Manifest.permission.CAMERA});
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "No se puede abrir la cámara", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            // Procesar la imagen usando el modelo de machine learning
            if (imageBitmap != null) {
                processImageWithMLModel(imageBitmap);
            }
        }
    }

    private Bitmap resizeImage(Bitmap imageBitmap) {
        return Bitmap.createScaledBitmap(imageBitmap, 224, 224, true);
    }

    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3);
        byteBuffer.order(ByteOrder.nativeOrder());

        int[] intValues = new int[224 * 224];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int pixel : intValues) {
            int r = (pixel >> 16) & 0xFF;
            int g = (pixel >> 8) & 0xFF;
            int b = pixel & 0xFF;

            byteBuffer.putFloat(r / 255.0f);
            byteBuffer.putFloat(g / 255.0f);
            byteBuffer.putFloat(b / 255.0f);
        }
        return byteBuffer;
    }

    private void processImageWithMLModel(Bitmap imageBitmap) {
        if (interpreter == null) {
            Toast.makeText(this, "El modelo no está cargado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verifica el tamaño del Bitmap
        if (imageBitmap == null || imageBitmap.getWidth() == 0 || imageBitmap.getHeight() == 0) {
            Toast.makeText(this, "La imagen es inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap resizedBitmap = resizeImage(imageBitmap);
        ByteBuffer input = convertBitmapToByteBuffer(resizedBitmap);

//        // Definir la salida del modelo (2 clases: borracho y sano)
//        float[][] output = new float[1][2];
//
//        // Hacer la inferencia
//        interpreter.run(input, output);
//
//        // Interpretar el resultado
//        float borrachoScore = output[0][0];
//        float sanoScore = output[0][1];
//        String result;

        // Definir la salida del modelo (1 valor de probabilidad)
        float[][] output = new float[1][1];

            // Hacer la inferencia
        interpreter.run(input, output);

        // Interpretar el resultado
        float borrachoProbability = output[0][0];  // Probabilidad de estar borracho
        String result;

        if (borrachoProbability > 0.5) {
            // Persona borracha detectada
            Toast.makeText(this, "Persona borracha detectada", Toast.LENGTH_LONG).show();
            result = "Persona borracha detectada";
        } else {
            // Persona sana detectada
            Toast.makeText(this, "Persona sana detectada", Toast.LENGTH_LONG).show();
            result = "Persona sana detectada";
        }

        // Mostrar el resultado usando el fragmento
        showResultInFragment(imageBitmap, result);


//        if (borrachoScore > sanoScore) {
//            Toast.makeText(this, "Persona borracha detectada", Toast.LENGTH_LONG).show();
//            result = "Persona borracha detectada";
//        } else {
//            Toast.makeText(this, "Persona sana detectada", Toast.LENGTH_LONG).show();
//            result = "Persona sana detectada";
//        }
//
//        // Mostrar el resultado usando el fragmento
//        showResultInFragment(imageBitmap, result);
    }

    private void showResultInFragment(Bitmap bitmap, String result) {
//        FirstFragment firstFragment = (FirstFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
//        if (firstFragment != null) {
//            firstFragment.setImageAndResult(bitmap, result);
//        }
        Bundle bundle = new Bundle();
        bundle.putParcelable("image", bitmap);
        bundle.putString("result", result);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        navController.navigate(R.id.FirstFragment, bundle);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }


}
