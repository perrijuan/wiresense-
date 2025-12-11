# ⚡ WireSense: Segmentação de Cabos com IA

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![TensorFlow Lite](https://img.shields.io/badge/TensorFlow_Lite-FF6F00?style=for-the-badge&logo=tensorflow&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)

> **Segmentação inteligente de fiação elétrica e cabos estruturados em tempo real, rodando localmente no Android.**

---

## 📱 Visão Geral

O **WireSense** é uma aplicação nativa desenvolvida para auxiliar técnicos e engenheiros. Utilizando modelos de Deep Learning (**U-Net**) otimizados via **TensorFlow Lite**, o app processa imagens da câmera ou galeria para identificar e destacar cabos em ambientes complexos.

Este projeto demonstra a aplicação prática de **Visão Computacional Mobile** (Edge AI), onde todo o processamento ocorre no dispositivo, garantindo privacidade e funcionamento offline.

## 📸 Screenshots

| Tela Inicial | Detecção (Câmera) | Galeria |
|:---:|:---:|:---:|
| <img src="screens<img width="1080" height="2400" alt="Screenshot_20251211_010643" src="https://github.com/user-attachments/assets/686cfb96-f1e4-4653-b0e0-f3e57556f6e4" />
<img width="1080" height="2400" alt="Screenshot_20251211_004950" src="https://github.com/user-attachments/assets/65c16f25-8f41-4fe9-a0e0-02a0fe9216b8" />
<img width="1080" height="2400" alt="Screenshot_20251211_004450" src="https://github.com/user-attachments/assets/adfed33b-5842-4d2e-969c-296a91f7ea1a" />
<img width="1080" height="2400" alt="Screenshot_20251211_004443" src="https://github.com/user-attachments/assets/9af3e649-ea53-4590-8a16-b7fe6e4a33f3" />
<img width="1080" height="2400" alt="Screenshot_20251211_004437" src="https://github.com/user-attachments/assets/088225bf-870d-4f6c-ae87-fefe9721f056" />
<img width="1080" height="2400" alt="Screenshot_20251211_004434" src="https://github.com/user-attachments/assets/07917765-0077-4fd5-a07b-2f1d8ea3f404" />
<img width="1080" height="2400" alt="Screenshot_20251211_004428" src="https://github.com/user-attachments/assets/39617f28-a502-4ea6-b6f1-d8464c2e2745" />
hots/home.png" width="200" /> | <img src="screenshots/detect.gif" width="200" /> | <img src="screenshots/gallery.png" width="200" /> |



## Funcionalidades

* **🔍 Detecção Automática:** Uso do modelo `attention_unet.tflite` para segmentação pixel-a-pixel.
* **📂 Suporte Robusto à Galeria:** Algoritmo de redimensionamento inteligente para processar imagens de alta resolução (12MP+) sem travar a memória (OOM).
* **⚡ Inferência On-Device:** Zero dependência de internet.
* **🎨 UI "Dark Tech":** Interface moderna focada em usabilidade com feedback visual imediato.

## Tecnologias Utilizadas

* **Linguagem:** Java (Android Nativo)
* **IA/ML:** TensorFlow Lite (Interpreter API)
* **Câmera:** CameraX API
* **Layouts:** ConstraintLayout & Material Design 3
* **Processamento:** Manipulação avançada de Bitmaps e ByteBuffers

## Como Executar o Projeto

### Pré-requisitos
* Android Studio Iguana ou superior.
* Dispositivo Android com Android 8.0+ (Oreo).

### Passo a Passo

1.  **Clone o repositório:**
    ```bash
    git clone [https://github.com/SEU-USUARIO/WireSense.git](https://github.com/SEU-USUARIO/WireSense.git)
    ```

2.  **Configuração do Modelo:**
    * Este projeto requer o arquivo `attention_unet.tflite`.
    * Certifique-se de que ele esteja localizado em: `app/src/main/assets/`.
    * *(Nota: Se o modelo for proprietário, mencione aqui como o usuário pode baixá-lo ou treinar o seu próprio).*

3.  **Abra no Android Studio:**
    * Aguarde o Gradle sincronizar as dependências.

4.  **Execute:**
    * Conecte seu celular via USB e clique em `Run`.

## Estrutura do Modelo (TFLite)

O processador de IA (`TfliteProcessor.java`) espera os seguintes parâmetros:
* **Input:** Imagem RGB redimensionada para `256x256` (Normalizada 0-1).
* **Output:** Máscara de segmentação binária ou probabilística.


---
Desenvolvido por **[juan perri]**
[LinkedIn](www.linkedin.com/in/juan-perri-libc) | 
