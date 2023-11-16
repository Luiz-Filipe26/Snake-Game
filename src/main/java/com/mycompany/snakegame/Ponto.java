/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.snakegame;

import java.util.Objects;

/**
 *
 * @author Luiz
 */
public class Ponto {
    public int x;
    public int y;
    
    Ponto(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Ponto ponto = (Ponto) obj;
        return ponto.x == x && ponto.y == y;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
