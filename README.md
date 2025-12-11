# ⚡ WireSense: Segmentação de Cabos com IA

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![TensorFlow Lite](https://img.shields.io/badge/TensorFlow_Lite-FF6F00?style=for-the-badge&logo=tensorflow&logoColor=white)
![ONNX](https://img.shields.io/badge/ONNX_Runtime-005CED?style=for-the-badge&logo=onnx&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)

> **Segmentação inteligente de fiação elétrica e cabos estruturados em tempo real, rodando localmente no Android.**

---

## Visão Geral

O **WireSense** é uma aplicação nativa desenvolvida para auxiliar técnicos e engenheiros. O projeto utiliza uma abordagem híbrida de Inteligência Artificial:

1.  **Modo Automático:** Utiliza modelos **U-Net** otimizados via **TensorFlow Lite** para detecção instantânea.
2.  **Modo Interativo:** Integra o **SAM 2 (Segment Anything Model)** via **ONNX Runtime**, permitindo segmentação precisa baseada em pontos de clique.
<img width="1762" height="444" alt="image" src="https://github.com/user-attachments/assets/e1341cd4-077f-441d-af82-a07a03b6d842" />


Este projeto demonstra a aplicação prática de **Visão Computacional Mobile** (Edge AI), onde todo o processamento ocorre no dispositivo, garantindo privacidade e funcionamento offline.

<p align="center">
  <img width="30%" alt="Screenshot 1" src="https://github.com/user-attachments/assets/d91f8703-7e37-49ac-8560-9edb6e435b33" />
  <img width="30%" alt="Screenshot 2" src="https://github.com/user-attachments/assets/fb6ecbb1-21ca-41b1-a8f4-219ac2e2d696" />
  <img width="30%" alt="Screenshot 3" src="https://github.com/user-attachments/assets/9509efa9-bd47-429e-8dcd-f09d21002f6d" />
  
  <br /> <img width="30%" alt="Screenshot 4" src="https://github.com/user-attachments/assets/d94e2f07-c006-4015-b19f-c84df40c4c3e" />
  <img width="30%" alt="Screenshot 5" src="https://github.com/user-attachments/assets/9fc268e8-623b-4f11-8f17-acb71f710d9b" />
  <img width="30%" alt="Screenshot 6" src="https://github.com/user-attachments/assets/9ac724f6-efa3-4d00-b736-67e940d14be2" />
</p>

## Funcionalidades

* **🔍 Detecção Híbrida:**
    * **TFLite (Attention U-Net):** Para segmentação automática pixel-a-pixel.
    * **ONNX (SAM 2):** Para segmentação interativa de alta precisão baseada em prompts do usuário.
* **📂 Suporte Robusto à Galeria:** Algoritmo de redimensionamento inteligente para processar imagens de alta resolução (12MP+) sem travar a memória (OOM).
* **⚡ Inferência On-Device:** Zero dependência de internet.
* **🎨 UI "Dark Tech":** Interface moderna focada em usabilidade com feedback visual imediato.

## Tecnologias Utilizadas

* **Linguagem:** Java (Android Nativo)
* **IA/ML:**
    * TensorFlow Lite (Interpreter API)
    * ONNX Runtime (para execução do SAM 2)
* **Câmera:** CameraX API
* **Layouts:** ConstraintLayout & Material Design 3
* **Processamento:** Manipulação avançada de Bitmaps e ByteBuffers

## 🚀 Como Executar o Projeto

### Pré-requisitos
* Android Studio Iguana ou superior.
* Dispositivo Android com Android 8.0+ (Oreo).

### Passo a Passo

1.  **Clone o repositório:**
    ```bash
    git clone [https://github.com/perrijuan/WireSense.git](https://github.com/perrijuan/WireSense.git)
    ```

2.  **Configuração dos Modelos:**
    * Certifique-se de que os seguintes arquivos estejam na pasta `app/src/main/assets/`:
        * `attention_unet.tflite` (Modelo U-Net)
        * `sam2_encoder_tiny.onnx` (Encoder do SAM 2)
        * `sam2_decoder_tiny.onnx` (Decoder do SAM 2)

3.  **Abra no Android Studio:**
    * Aguarde o Gradle sincronizar as dependências.

4.  **Execute:**
    * Conecte seu celular via USB e clique em `Run`.

## Estrutura dos Modelos

O projeto abstrai a complexidade da inferência em duas classes principais:
* **`TfliteProcessor.java`:** Gerencia a inferência leve e rápida do U-Net.
* **`Sam2Processor.java`:** Gerencia o encoder/decoder do SAM 2 utilizando a runtime do ONNX para segmentação interativa.

---
Desenvolvido por **[Juan Perri]**
[LinkedIn](https://www.linkedin.com/in/juan-perri-libc)
