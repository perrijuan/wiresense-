package com.example.tensorflowlite;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SegmentationFragment extends Fragment {

    // Views
    private PreviewView viewFinder;
    private View loadingIndicator;
    private FloatingActionButton btnCapture, btnGallery, btnClose;
    private android.widget.ImageView capturedImageView;
    private OverlayView overlayView;
    private android.widget.TextView txtInstructions;

    // IA
    private TfliteProcessor tfliteProcessor;

    // Câmera
    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;
    private Bitmap currentPhoto;

    // Launchers
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) startCamera();
            });

    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> { if(uri != null) processGalleryImage(uri); });

    public SegmentationFragment() {
        super(R.layout.fragment_segmentation);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bindings (Confirme se os IDs batem com seu XML)
        viewFinder = view.findViewById(R.id.viewFinder);
        loadingIndicator = view.findViewById(R.id.loadingIndicator);
        btnCapture = view.findViewById(R.id.btnCapture);
        btnGallery = view.findViewById(R.id.btnGallery);
        btnClose = view.findViewById(R.id.btnClose);
        capturedImageView = view.findViewById(R.id.capturedImageView);
        overlayView = view.findViewById(R.id.overlayView); // Certifique-se que OverlayView existe no XML
        txtInstructions = view.findViewById(R.id.txtInstructions);

        cameraExecutor = Executors.newSingleThreadExecutor();

        // Inicializar Câmera
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }

        // Inicializar Modelo IA
        try {
            // NOME EXATO DO ARQUIVO AQUI:
            tfliteProcessor = new TfliteProcessor(requireContext(), "attention_unet");
        } catch (Exception e) {
            showError("Erro fatal ao iniciar IA: " + e.getMessage());
        }

        // Listeners
        btnCapture.setOnClickListener(v -> takePhoto());
        btnGallery.setOnClickListener(v -> galleryLauncher.launch("image/*"));
        btnClose.setOnClickListener(v -> resetUI());
    }

    // --- PROCESSAMENTO DA GALERIA (CORREÇÃO DE MEMÓRIA) ---
    private void processGalleryImage(Uri uri) {
        loadingIndicator.setVisibility(View.VISIBLE);
        txtInstructions.setText("Carregando imagem...");

        new Thread(() -> {
            // Método seguro para carregar imagens grandes
            Bitmap bitmap = loadSafeBitmap(uri);

            if (bitmap != null) {
                prepareImage(bitmap);
            } else {
                requireActivity().runOnUiThread(() -> {
                    loadingIndicator.setVisibility(View.GONE);
                    showError("Não foi possível ler a imagem.");
                });
            }
        }).start();
    }

    // Carrega Bitmap redimensionado para evitar OutOfMemoryError
    private Bitmap loadSafeBitmap(Uri uri) {
        try {
            InputStream in = requireContext().getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; // Apenas ler tamanho
            BitmapFactory.decodeStream(in, null, options);
            in.close();

            // Calcular redução
            int scale = 1;
            while(options.outWidth / scale > 1024 || options.outHeight / scale > 1024) {
                scale *= 2;
            }

            // Carregar de verdade
            BitmapFactory.Options options2 = new BitmapFactory.Options();
            options2.inSampleSize = scale;
            in = requireContext().getContentResolver().openInputStream(uri);
            Bitmap b = BitmapFactory.decodeStream(in, null, options2);
            in.close();
            return b;

        } catch (Exception e) {
            Log.e("SEG_APP", "Erro ao abrir imagem", e);
            return null;
        }
    }

    // --- CÂMERA ---
    private void takePhoto() {
        if (imageCapture == null) return;
        loadingIndicator.setVisibility(View.VISIBLE);

        imageCapture.takePicture(ContextCompat.getMainExecutor(requireContext()), new ImageCapture.OnImageCapturedCallback() {
            @Override public void onCaptureSuccess(@NonNull ImageProxy image) {
                Bitmap bitmap = imageProxyToBitmap(image);
                image.close();
                // Processar em thread separada
                new Thread(() -> prepareImage(bitmap)).start();
            }
            @Override public void onError(@NonNull ImageCaptureException exception) {
                loadingIndicator.setVisibility(View.GONE);
                showError("Erro na câmera");
            }
        });
    }

    // --- LÓGICA CENTRAL ---
    private void prepareImage(Bitmap bitmap) {
        currentPhoto = bitmap;

        // Atualizar UI na Thread principal
        requireActivity().runOnUiThread(() -> {
            capturedImageView.setImageBitmap(bitmap);
            capturedImageView.setVisibility(View.VISIBLE);
            viewFinder.setVisibility(View.INVISIBLE);
            btnCapture.setVisibility(View.GONE);
            btnGallery.setVisibility(View.GONE);
            overlayView.setVisibility(View.VISIBLE);
            overlayView.clear(); // Limpa máscaras antigas se houver método clear()
            txtInstructions.setText("Segmentando Automático (TFLite)...");
        });

        runTflite();
    }

    private void runTflite() {
        if (tfliteProcessor == null) {
            requireActivity().runOnUiThread(() -> {
                loadingIndicator.setVisibility(View.GONE);
                showError("Modelo IA não carregado!");
            });
            return;
        }

        try {
            Log.d("SEG_APP", "Iniciando inferência...");
            long start = System.currentTimeMillis();

            // SEGMENTAÇÃO AQUI
            Bitmap mask = tfliteProcessor.segmentImage(currentPhoto);

            long time = System.currentTimeMillis() - start;
            Log.d("SEG_APP", "Inferência concluída em " + time + "ms");

            // Sucesso
            requireActivity().runOnUiThread(() -> {
                if (mask != null) {
                    overlayView.setMask(mask); // Certifique-se que sua OverlayView tem esse método
                    txtInstructions.setText("Fio detectado! (" + time + "ms)");
                } else {
                    txtInstructions.setText("Nenhum resultado.");
                }
                loadingIndicator.setVisibility(View.GONE);
                btnClose.setVisibility(View.VISIBLE);
            });

        } catch (Exception e) {
            Log.e("SEG_APP", "Erro na inferência", e);
            // GARANTIR QUE O LOADING SAIA MESMO COM ERRO
            requireActivity().runOnUiThread(() -> {
                loadingIndicator.setVisibility(View.GONE);
                btnClose.setVisibility(View.VISIBLE);
                showError("Erro no processamento: " + e.getMessage());
            });
        }
    }

    // --- UTILS ---
    private void resetUI() {
        currentPhoto = null;
        capturedImageView.setVisibility(View.GONE);
        overlayView.setVisibility(View.GONE);
        viewFinder.setVisibility(View.VISIBLE);
        btnCapture.setVisibility(View.VISIBLE);
        btnGallery.setVisibility(View.VISIBLE);
        btnClose.setVisibility(View.GONE);
        txtInstructions.setText("");
        loadingIndicator.setVisibility(View.GONE);
    }

    private void showError(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private Bitmap imageProxyToBitmap(ImageProxy image) {
        java.nio.ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        Matrix matrix = new Matrix();
        matrix.postRotate(image.getImageInfo().getRotationDegrees());
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private void startCamera() {
        com.google.common.util.concurrent.ListenableFuture<ProcessCameraProvider> future = ProcessCameraProvider.getInstance(requireContext());
        future.addListener(() -> {
            try {
                ProcessCameraProvider provider = future.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());
                imageCapture = new ImageCapture.Builder().build();
                provider.unbindAll();
                provider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture);
            } catch (Exception e) { e.printStackTrace(); }
        }, ContextCompat.getMainExecutor(requireContext()));
    }
}