package com.mycompany.snakegame.banco;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GerenciadorRecordes {
    private static GerenciadorRecordes gr;
    private final int MAX_RECORDS = 5;

    public static GerenciadorRecordes getInstancia() {
        if (gr == null) {
            gr = new GerenciadorRecordes();
        }
        return gr;
    }

    private GerenciadorRecordes() {
        //Criar tabela de recordes se não existir
        try (Connection con = Conexao.obterConexao();
            Statement stmt = con.createStatement()) {

            // Verifica se a tabela já existe
            DatabaseMetaData metaData = con.getMetaData();
            ResultSet rs = metaData.getTables(null, null, "recordes", null);

            if (!rs.next()) {
                // Se a tabela não existe, cria-a
                stmt.executeUpdate("CREATE TABLE recordes (id INTEGER PRIMARY KEY AUTOINCREMENT, nome VARCHAR(50), pontos INTEGER)");
            }

        } catch (SQLException e) {
            Logger.getLogger(GerenciadorRecordes.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            Conexao.fecharConexao();
        }
    }

    public boolean isNovoRecorde(int pontos) {
        try (Connection con = Conexao.obterConexao();
             PreparedStatement stmt = con.prepareStatement("SELECT MIN(pontos) FROM recordes")) {

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int menorRecordeAtual = rs.getInt(1);
                    boolean novoRecorde = pontos > menorRecordeAtual;
                    return novoRecorde;
                }
            }

        } catch (SQLException e) {
            Logger.getLogger(GerenciadorRecordes.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            Conexao.fecharConexao();
        }
        return false;
    }

    public List<String> lerRecordes() {
        List<String> recordes = new ArrayList<>();

        try (Connection con = Conexao.obterConexao();
             PreparedStatement stmt = con.prepareStatement("SELECT nome, pontos FROM recordes ORDER BY pontos DESC LIMIT ?")) {

            stmt.setInt(1, MAX_RECORDS);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String nome = rs.getString("nome");
                    String pontosStr = rs.getString("pontos");
                    recordes.add(nome + ": " + pontosStr);
                }
            }

        } catch (SQLException e) {
            Logger.getLogger(GerenciadorRecordes.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            Conexao.fecharConexao();
        }

        return recordes;
    }

    public void inserirRecorde(String nome, int pontos) {
        try (Connection con = Conexao.obterConexao()) {
            // Inserir novo recorde
            try (PreparedStatement insertStmt = con.prepareStatement("INSERT INTO recordes (nome, pontos) VALUES (?, ?)")) {
                insertStmt.setString(1, nome);
                insertStmt.setInt(2, pontos);
                insertStmt.executeUpdate();
            }

            // Excluir registros além do limite
            try (PreparedStatement deleteStmt = con.prepareStatement(
                    "DELETE FROM recordes WHERE id NOT IN (SELECT id FROM recordes ORDER BY pontos DESC LIMIT ?)")) {
                deleteStmt.setInt(1, MAX_RECORDS);
                deleteStmt.executeUpdate();
            }

        } catch (SQLException e) {
            Logger.getLogger(GerenciadorRecordes.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            Conexao.fecharConexao();
        }
    }
}
