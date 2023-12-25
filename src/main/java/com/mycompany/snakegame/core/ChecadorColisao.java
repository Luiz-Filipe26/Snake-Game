package com.mycompany.snakegame.core;

import java.util.List;

import javafx.geometry.Point2D;

public class ChecadorColisao {
	

    private static double unidadeLargura;
    private static double unidadeAltura;
    private static double larguraJogo;
    private static double alturaJogo;
    
    public static void inicializarValores() {
    	CarregaValores cv = CarregaValores.getInstancia();
    	unidadeLargura = cv.getUnidadeLargura();
    	unidadeAltura = cv.getUnidadeAltura();
    	larguraJogo = cv.getCanvasLargura();
    	alturaJogo = cv.getCanvasAltura();
    	larguraJogo -= cv.getXMargem();
    	alturaJogo -= cv.getYMargem();
    }
    
    
	
	public static boolean checarMargem(Point2D ponto) {
		return ponto.getX() < 0 || ponto.getX() > larguraJogo - unidadeLargura || ponto.getY() < 0 || ponto.getY() > alturaJogo - unidadeAltura;
	}
	
	public static boolean checar(Point2D ponto1, Point2D ponto2) {
		return ponto2.equals(ponto1);
	}
	
	public static boolean checar(List<Point2D> pontos, Point2D ponto) {
		return pontos.contains(ponto);
	}
	
	public static boolean checar(List<Point2D> pontos1, List<Point2D> pontos2) {
		return pontos2.stream().anyMatch(pontos1::contains);
	}
}
