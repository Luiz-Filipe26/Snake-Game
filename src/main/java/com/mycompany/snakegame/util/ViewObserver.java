package com.mycompany.snakegame.util;

import javafx.scene.input.KeyCode;

public interface ViewObserver {
    void keyPressed(String keyCode);
    void keyReleased(String keyCode);
    void novoJogo(float dificuldade, boolean atrevessarBordas);
    void fecharJogo();
    void viewFechada();
}
