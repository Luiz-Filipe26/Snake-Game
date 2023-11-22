
package com.mycompany.snakegame.util;

import java.util.Objects;

public class Ponto {
    public int x;
    public int y;
    
    public Ponto() {
        x = 0;
        y = 0;
    }
    
    public Ponto(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
    Object o;
    
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
