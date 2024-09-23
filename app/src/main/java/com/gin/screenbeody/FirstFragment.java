package com.gin.screenbeody;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.gin.screenbeody.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private Bitmap imageBitmap; // Guardar la imagen capturada
    private String resultText = "Resultado no disponible"; // Guardar el resultado del modelo

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Cargar la imagen y el resultado si existen
//        if (imageBitmap != null) {
//            binding.imageView.setImageBitmap(imageBitmap);
//        }
//
//        binding.textviewResult.setText(resultText);
//
//        // Navegar a la siguiente pantalla
//        binding.buttonFirst.setOnClickListener(v ->
//                NavHostFragment.findNavController(FirstFragment.this)
//                        .navigate(R.id.action_FirstFragment_to_SecondFragment)
//        );
        // Recibir datos del Bundle
        if (getArguments() != null) {
            Bitmap bitmap = getArguments().getParcelable("image");
            String result = getArguments().getString("result");

            setImageAndResult(bitmap, result); // Llama al método para establecer la imagen y el resultado
        }

        binding.buttonFirst.setOnClickListener(v ->
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment)
        );
    }

    // Método para recibir la imagen y el resultado
    public void setImageAndResult(Bitmap bitmap, String result) {
        if (bitmap != null) {
            this.imageBitmap = bitmap; // Guardar la imagen en la variable
            binding.imageView.setImageBitmap(bitmap); // Asegúrate de tener un ImageView en tu layout
        }
        this.resultText = result; // Guardar el resultado en la variable
        binding.textviewResult.setText(result); // Asegúrate de que este TextView esté bien referenciado
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
