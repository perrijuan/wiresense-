package com.example.tensorflowlite;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OnnxValue;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;

public class Sam2Processor {
    private static final String TAG = "SAM2";

    // Mantemos 512 para garantir velocidade.
    // Se seu celular for muito potente, pode tentar voltar para 1024 depois.
    private static final int TARGET_SIZE = 1024;

    private OrtEnvironment env;
    private OrtSession sessionEncoder;
    private OrtSession sessionDecoder;
    private Map<String, OnnxTensor> cachedEncoderOutputs;

    public Sam2Processor(Context context) throws Exception {
        Log.d(TAG, "Inicializando SAM 2 com Otimizações...");
        this.env = OrtEnvironment.getEnvironment();

        // --- OTIMIZAÇÃO BASEADA NO ARTIGO ---
        OrtSession.SessionOptions options = new OrtSession.SessionOptions();

        // 1. Otimização de Grafo (Funde operações matemáticas para ser mais rápido)
        options.setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT);

        try {
            // 2. Tenta usar NNAPI (Aceleração de Hardware / NPU / GPU)
            // Isso é o que faz a diferença real em celulares Android
            options.addNnapi();
            Log.d(TAG, "Aceleração NNAPI ativada com sucesso!");
        } catch (Exception e) {
            // Se o celular não suportar ou der erro, faz fallback para CPU Multithread
            Log.w(TAG, "NNAPI falhou, usando CPU com 4 threads. Erro: " + e.getMessage());
            options.setIntraOpNumThreads(4);
        }
        // ------------------------------------

        // Nomes exatos conforme seu projeto
        byte[] encBytes = readAsset(context, "sam2_hiera_tiny_encoder.onnx");
        byte[] decBytes = readAsset(context, "sam2_hiera_tiny_decoder.onnx");

        // Cria as sessões usando as opções otimizadas
        this.sessionEncoder = env.createSession(encBytes, options);
        this.sessionDecoder = env.createSession(decBytes, options);

        Log.d(TAG, "Modelos carregados e sessões criadas.");
    }

    public void encodeImage(Bitmap bitmap) throws Exception {
        Log.d(TAG, "Iniciando Encoder...");
        long startTime = System.currentTimeMillis();

        Bitmap resized = Bitmap.createScaledBitmap(bitmap, TARGET_SIZE, TARGET_SIZE, true);
        FloatBuffer buffer = preprocess(resized);

        long[] shape = {1, 3, TARGET_SIZE, TARGET_SIZE};
        OnnxTensor inputTensor = OnnxTensor.createTensor(env, buffer, shape);

        OrtSession.Result result = sessionEncoder.run(Collections.singletonMap("image", inputTensor));

        cachedEncoderOutputs = new HashMap<>();
        for (Map.Entry<String, OnnxValue> entry : result) {
            if (entry.getValue() instanceof OnnxTensor) {
                cachedEncoderOutputs.put(entry.getKey(), (OnnxTensor) entry.getValue());
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        Log.d(TAG, "Encoder finalizado em: " + duration + "ms");
    }

    public Bitmap decodeMask(List<Float> listX, List<Float> listY, int viewW, int viewH) throws Exception {
        if (cachedEncoderOutputs == null || listX.isEmpty()) return null;

        int numPoints = listX.size();
        float scaleX = (float) TARGET_SIZE / viewW;
        float scaleY = (float) TARGET_SIZE / viewH;

        FloatBuffer pointsBuffer = FloatBuffer.allocate(numPoints * 2);
        FloatBuffer labelsBuffer = FloatBuffer.allocate(numPoints);

        for (int i = 0; i < numPoints; i++) {
            pointsBuffer.put(listX.get(i) * scaleX);
            pointsBuffer.put(listY.get(i) * scaleY);
            labelsBuffer.put(1.0f); // 1.0 = Clique positivo
        }
        pointsBuffer.rewind();
        labelsBuffer.rewind();

        OnnxTensor pointsTensor = OnnxTensor.createTensor(env, pointsBuffer, new long[]{1, numPoints, 2});
        OnnxTensor labelsTensor = OnnxTensor.createTensor(env, labelsBuffer, new long[]{1, numPoints});

        Map<String, OnnxTensor> inputs = new HashMap<>(cachedEncoderOutputs);
        inputs.put("point_coords", pointsTensor);
        inputs.put("point_labels", labelsTensor);

        // Inputs dummy para evitar erro de compatibilidade
        float[] maskInput = new float[1 * 1 * 256 * 256];
        inputs.put("mask_input", OnnxTensor.createTensor(env, FloatBuffer.wrap(maskInput), new long[]{1, 1, 256, 256}));
        inputs.put("has_mask_input", OnnxTensor.createTensor(env, FloatBuffer.wrap(new float[]{0f}), new long[]{1}));

        OnnxTensor maskTensor = (OnnxTensor) sessionDecoder.run(inputs).get(0);
        return maskToBitmap(maskTensor);
    }

    private FloatBuffer preprocess(Bitmap bitmap) {
        FloatBuffer buffer = FloatBuffer.allocate(3 * TARGET_SIZE * TARGET_SIZE);
        int[] pixels = new int[TARGET_SIZE * TARGET_SIZE];
        bitmap.getPixels(pixels, 0, TARGET_SIZE, 0, 0, TARGET_SIZE, TARGET_SIZE);

        for (int p : pixels) {
            float r = ((p >> 16) & 0xFF) / 255.0f;
            float g = ((p >> 8) & 0xFF) / 255.0f;
            float b = (p & 0xFF) / 255.0f;
            // Normalização padrão ImageNet
            buffer.put((r - 0.485f) / 0.229f);
            buffer.put((g - 0.456f) / 0.224f);
            buffer.put((b - 0.406f) / 0.225f);
        }
        buffer.rewind();
        return buffer;
    }

    private Bitmap maskToBitmap(OnnxTensor tensor) {
        float[] data = tensor.getFloatBuffer().array();
        int dim = (int) Math.sqrt(data.length);
        int[] pixels = new int[dim * dim];
        for (int i = 0; i < data.length; i++) {
            // Ciano Neon (#00FFFF) com transparência
            pixels[i] = (data[i] > 0.0f) ? Color.argb(160, 0, 255, 255) : Color.TRANSPARENT;
        }
        return Bitmap.createBitmap(pixels, dim, dim, Bitmap.Config.ARGB_8888);
    }

    private byte[] readAsset(Context context, String name) throws IOException {
        try (InputStream is = context.getAssets().open(name);
             ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) os.write(buffer, 0, len);
            return os.toByteArray();
        }
    }

    public void close() throws Exception {
        if (sessionEncoder != null) sessionEncoder.close();
        if (sessionDecoder != null) sessionDecoder.close();
        if (env != null) env.close();
    }
}