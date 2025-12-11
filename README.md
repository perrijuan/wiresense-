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

Este projeto demonstra a aplicação prática de **Visão Computacional Mobile** (Edge AI), onde todo o processamento ocorre no dispositivo, garantindo privacidade e funcionamento offline.

## Screenshots

<p align="center">
  <img src="https://github.com/user-attachments/assets/39617f28-a502-4ea6-b6f1-d8464c2e2745" width="22%" alt="Seleção de Perfil" />
  <img src="https://github.com/user-attachments/assets/088225bf-870d-4f6c-ae87-fefe9721f056" width="22%" alt="Tela de Login" />
  <img src="https://github.com/user-attachments/assets/65c16f25-8f41-4fe9-a0e0-02a0fe9216b8" width="22%" alt="Segmentação de Fio" />
  <img src="https://github.com/user-attachments/assets/686cfb96-f1e4-4653-b0e0-f3e57556f6e4" width="22%" alt="Analytics" />
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
    git clone [https://github.com/SEU-USUARIO/WireSense.git](https://github.com/perrijuan/WireSense.git)
    ```

2.  **Configuração dos Modelos:**
    * Certifique-se de que os arquivos de modelo (`attention_unet.tflite` e modelos ONNX do SAM) estejam localizados em: `app/src/main/assets/`.

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
