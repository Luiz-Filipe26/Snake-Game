package com.mycompany.snakegame;

import javafx.scene.input.KeyCode;

/**
 *
 * @author Luiz
 */
public interface ViewObserver {
    void keyPressed(KeyCode keyCode);
    void novoJogo(float dificuldade, boolean atrevessarBordas);
    void fecharJogo();
    void viewFechada();
}
