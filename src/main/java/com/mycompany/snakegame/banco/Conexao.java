
package com.mycompany.snakegame.banco;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Conexao {
    private static Connection con;
    
    private Conexao(){
        
    }
    
    public static Connection obterConexao(){
        
        try {
            if(con==null || con.isClosed()){
                String URL="jdbc:sqlite:snake_database.db";
                con = DriverManager.getConnection(URL);
            }
            return con;
        } catch (SQLException ex) {
            System.out.println("Erro na conexão");
            Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static void fecharConexao() {
        try {
            if(con != null && !con.isClosed()) {
                con.close();
            }
        } catch (SQLException ex) {
            System.out.println("Erro ao fechar a conexão");
            Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}