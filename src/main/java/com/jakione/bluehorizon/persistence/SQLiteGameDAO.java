package com.jakione.bluehorizon.persistence;

import com.jakione.bluehorizon.model.GameModel;
import com.jakione.bluehorizon.model.fish.CatchRecord;
import com.jakione.bluehorizon.model.fish.FishSpecies;
import com.jakione.bluehorizon.model.inventory.RodType;
import com.jakione.bluehorizon.model.inventory.UsableItem;

import java.sql.*;
import java.util.Map;

public class SQLiteGameDAO implements GameDAO {

    private static final String DB_URL = "jdbc:sqlite:bluehorizon_save.db";

    public SQLiteGameDAO() {
        initDatabase();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void initDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // 1. Tabella Posizione Giocatore
            stmt.execute("CREATE TABLE IF NOT EXISTS player (id INTEGER PRIMARY KEY, col INTEGER, row INTEGER);");
            // 2. Tabella Inventario (Canne e Oggetti)
            stmt.execute("CREATE TABLE IF NOT EXISTS inventory (item_id TEXT PRIMARY KEY, quantity INTEGER, is_rod INTEGER);");
            // 3. Tabella Registro Catture
            stmt.execute("CREATE TABLE IF NOT EXISTS catch_registry (species_id TEXT PRIMARY KEY, is_caught INTEGER, max_weight REAL, max_length REAL, total_caught INTEGER);");
        } catch (SQLException e) {
            System.err.println("Errore inizializzazione DB SQLite: " + e.getMessage());
        }
    }

    @Override
    public void saveGame(GameModel model) {
        String savePlayerSql = "INSERT OR REPLACE INTO player (id, col, row) VALUES (1, ?, ?);";
        String saveInventorySql = "INSERT OR REPLACE INTO inventory (item_id, quantity, is_rod) VALUES (?, ?, ?);";
        String saveRegistrySql = "INSERT OR REPLACE INTO catch_registry (species_id, is_caught, max_weight, max_length, total_caught) VALUES (?, ?, ?, ?, ?);";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false); // Transazione per garantire l'atomicità del salvataggio

            // Salva Giocatore
            try (PreparedStatement pstmt = conn.prepareStatement(savePlayerSql)) {
                pstmt.setInt(1, model.getPlayer().getCol());
                pstmt.setInt(2, model.getPlayer().getRow());
                pstmt.executeUpdate();
            }

            // Salva Inventario (Canne)
            try (PreparedStatement pstmt = conn.prepareStatement(saveInventorySql)) {
                for (Map.Entry<RodType, Integer> entry : model.getPlayer().getInventory().getRods().entrySet()) {
                    pstmt.setString(1, entry.getKey().name());
                    pstmt.setInt(2, entry.getValue());
                    pstmt.setInt(3, 1); // 1 = true
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            // Salva Inventario (Consumabili)
            try (PreparedStatement pstmt = conn.prepareStatement(saveInventorySql)) {
                for (Map.Entry<UsableItem, Integer> entry : model.getPlayer().getInventory().getUsables().entrySet()) {
                    pstmt.setString(1, entry.getKey().name());
                    pstmt.setInt(2, entry.getValue());
                    pstmt.setInt(3, 0); // 0 = false
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            // Salva Registro Catture
            try (PreparedStatement pstmt = conn.prepareStatement(saveRegistrySql)) {
                for (Map.Entry<FishSpecies, CatchRecord> entry : model.getPlayer().getCatchRegistry().getRecords().entrySet()) {
                    CatchRecord record = entry.getValue();
                    pstmt.setString(1, entry.getKey().name());
                    pstmt.setInt(2, record.isCaught() ? 1 : 0);
                    pstmt.setDouble(3, record.getMaxWeight());
                    pstmt.setDouble(4, record.getRecordLength());
                    pstmt.setInt(5, record.getTotalCaught());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            conn.commit(); // Conferma le modifiche solo se tutte le query vanno a buon fine
            System.out.println("Salvataggio su SQLite completato con successo.");
        } catch (SQLException e) {
            System.err.println("Errore durante il salvataggio dei dati: " + e.getMessage());
        }
    }

    @Override
    public boolean loadGame(GameModel model) {
        String loadPlayerSql = "SELECT col, row FROM player WHERE id = 1;";
        String loadInventorySql = "SELECT item_id, quantity, is_rod FROM inventory;";
        String loadRegistrySql = "SELECT species_id, is_caught, max_weight, max_length, total_caught FROM catch_registry;";

        try (Connection conn = getConnection()) {
            // 1. Carica Giocatore
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(loadPlayerSql)) {
                if (rs.next()) {
                    model.getPlayer().setCol(rs.getInt("col"));
                    model.getPlayer().setRow(rs.getInt("row"));
                } else {
                    return false; // Nessun salvataggio precedente trovato
                }
            }

            // 2. Carica Inventario
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(loadInventorySql)) {

                // Puliamo l'inventario dai valori di default iniziali (es. la canna base)
                model.getPlayer().getInventory().clear();

                while (rs.next()) {
                    String itemId = rs.getString("item_id");
                    int qty = rs.getInt("quantity");
                    int isRod = rs.getInt("is_rod");

                    if (isRod == 1) {
                        RodType type = RodType.valueOf(itemId);
                        // Usiamo il nuovo setter dedicato invece di forzare la mappa
                        model.getPlayer().getInventory().setRod(type, qty);
                    } else {
                        UsableItem item = UsableItem.valueOf(itemId);
                        // Usiamo il nuovo setter dedicato invece di forzare la mappa
                        model.getPlayer().getInventory().setUsable(item, qty);
                    }
                }
            }

            // 3. Carica Registro Catture
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(loadRegistrySql)) {
                while (rs.next()) {
                    FishSpecies species = FishSpecies.valueOf(rs.getString("species_id"));

                    // La variabile is_caught dal DB non serve più, lo deduciamo da total_caught
                    double maxWeight = rs.getDouble("max_weight");
                    double maxLength = rs.getDouble("max_length");
                    int totalCaught = rs.getInt("total_caught");

                    CatchRecord record = model.getPlayer().getCatchRegistry().getRecords().get(species);
                    if (record != null) {
                        record.setMaxWeight(maxWeight);
                        record.setRecordLength(maxLength);
                        record.setTotalCaught(totalCaught);
                    }
                }
            }

            return true;
        } catch (IllegalArgumentException | SQLException e) {
            System.err.println("Errore durante il caricamento dei dati: " + e.getMessage());
            return false;
        }
    }
}