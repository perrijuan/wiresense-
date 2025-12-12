package com.example.tensorflowlite;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;

public class TfliteProcessor {

    private Interpreter interpreter;
    // IMPORTANTE: Mude para 128, 224 ou 256 dependendo de como treinou seu modelo!
    // Vou usar 256 como padrão.
    private static final int INPUT_SIZE = 256;

    public TfliteProcessor(Context context, String modelName) {
        try {
            if (!modelName.endsWith(".tflite")) {
                modelName += ".tflite";
            }
            MappedByteBuffer tfliteModel = FileUtil.loadMappedFile(context, modelName);
            Interpreter.Options options = new Interpreter.Options();
            interpreter = new Interpreter(tfliteModel, options);
            Log.d("TFLITE", "Modelo carregado: " + modelName);
        } catch (IOException e) {
            Log.e("TFLITE", "ERRO CRÍTICO: Modelo não encontrado na pasta assets!", e);
        }
    }

    public Bitmap segmentImage(Bitmap bitmap) {
        if (interpreter == null) {
            Log.e("TFLITE", "Interpreter é nulo. Abortando.");
            return null;
        }

        // 1. Redimensionar (Evita erro de shape)
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, true);

        // 2. Preparar Input
        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(4 * INPUT_SIZE * INPUT_SIZE * 3);
        inputBuffer.order(ByteOrder.nativeOrder());

        int[] pixels = new int[INPUT_SIZE * INPUT_SIZE];
        resized.getPixels(pixels, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE);

        for (int pixel : pixels) {
            // Normaliza 0-255 para 0.0-1.0
            inputBuffer.putFloat(((pixel >> 16) & 0xFF) / 255.0f);
            inputBuffer.putFloat(((pixel >> 8) & 0xFF) / 255.0f);
            inputBuffer.putFloat((pixel & 0xFF) / 255.0f);
        }

        // 3. Output (Assumindo saída binária 1 canal ou 2 canais softmax)
        // Se seu modelo falhar aqui, troque o '1' final por '2'
        float[][][][] outputBuffer = new float[1][INPUT_SIZE][INPUT_SIZE][1];

        // 4. Rodar
        interpreter.run(inputBuffer, outputBuffer);

        // 5. Converter para Máscara
        return convertOutputToBitmap(outputBuffer);
    }

    private Bitmap convertOutputToBitmap(float[][][][] output) {
        int[] maskPixels = new int[INPUT_SIZE * INPUT_SIZE];

        for (int y = 0; y < INPUT_SIZE; y++) {
            for (int x = 0; x < INPUT_SIZE; x++) {
                // Pega a probabilidade do pixel ser um fio
                float confidence = output[0][y][x][0];

                if (confidence > 0.5f) {
                    maskPixels[y * INPUT_SIZE + x] = Color.argb(150, 0, 255, 0); // Verde Neon
                } else {
                    maskPixels[y * INPUT_SIZE + x] = Color.TRANSPARENT;
                }
            }
        }
        return Bitmap.createBitmap(maskPixels, INPUT_SIZE, INPUT_SIZE, Bitmap.Config.ARGB_8888);
    }

    public void close() {
        if (interpreter != null) interpreter.close();
    }
}