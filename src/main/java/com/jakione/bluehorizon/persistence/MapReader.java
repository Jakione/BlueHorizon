package com.jakione.bluehorizon.persistence;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe di utilità per il caricamento dei dati persistenti della mappa.
 * Isola la logica di I/O dal modello di dominio.
 */
public class MapReader {

    /**
     * Legge un file di testo dalle risorse e genera la matrice di interi corrispondente.
     *
     * @param resourcePath Il percorso del file all'interno della cartella resources (es. "/maps/level1.txt")
     * @return Una matrice 2D di interi che rappresenta la mappa [colonne][righe]
     * @throws RuntimeException se il file non viene trovato o è illeggibile
     */
    public static int[][] loadMapMatrix(String resourcePath) {
        List<int[]> rowList = new ArrayList<>();
        int columns = 0;

        try (InputStream is = MapReader.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("File della mappa non trovato: " + resourcePath);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;

            while ((line = reader.readLine()) != null) {
                // Rimuove spazi extra e divide per spazi o tabulazioni
                String[] tokens = line.trim().split("\\s+");
                int[] row = new int[tokens.length];

                for (int i = 0; i < tokens.length; i++) {
                    row[i] = Integer.parseInt(tokens[i]);
                }

                // Traccia il numero massimo di colonne trovate
                if (row.length > columns) {
                    columns = row.length;
                }
                rowList.add(row);
            }
        } catch (Exception e) {
            throw new RuntimeException("Errore durante il caricamento della mappa: " + e.getMessage(), e);
        }

        int rows = rowList.size();
        int[][] mapMatrix = new int[columns][rows];

        // Trasponiamo la lista di righe nella nostra struttura [col][row]
        for (int r = 0; r < rows; r++) {
            int[] currentRow = rowList.get(r);
            for (int c = 0; c < currentRow.length; c++) {
                mapMatrix[c][r] = currentRow[c];
            }
        }

        return mapMatrix;
    }
}