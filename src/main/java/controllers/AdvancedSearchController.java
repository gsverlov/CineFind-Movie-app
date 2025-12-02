package controllers;

import models.Movie;
import uipanels.AdvancedPanel;
import uipanels.ResultsPanel;

import javax.swing.*;
import java.awt.event.ActionListener;

public class AdvancedSearchController {

    private AdvancedPanel advancedPanel;
    private ResultsPanel resultsPanel;
    private AdvancedSearchInteractor interactor;
    private AppController appController;

    public AdvancedSearchController(AdvancedPanel advancedPanel, ResultsPanel resultsPanel, AppController appController) {
        this.advancedPanel = advancedPanel;
        this.resultsPanel = resultsPanel;
        this.appController = appController;
        this.interactor = new AdvancedSearchInteractor();

        // Pinecone host is now configured in apiservices.ApiConfig.java
        // No need to set it here anymore!

        // Listen for Search button
        ActionListener searchAction = e -> performSearch();
        advancedPanel.getSearchButton().addActionListener(searchAction);

        // Listen for Back button
        advancedPanel.getBackButton().addActionListener(e -> {
            // Clear fields when going back
            advancedPanel.moodText.setText("");
            advancedPanel.themesText.setText("");
            appController.show(AppController.SEARCH);
        });
    }

    private void performSearch() {
        // Get input from uipanels.AdvancedPanel
        String query = advancedPanel.getSearchInput();

        // Validate input
        if (query.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    advancedPanel,
                    "Please enter your mood and/or movie themes!",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Show loading indicator
        advancedPanel.searchButton.setEnabled(false);
        advancedPanel.searchButton.setText("Searching...");

        // Perform search in background thread to avoid freezing UI
        SwingWorker<Movie[], Void> worker = new SwingWorker<Movie[], Void>() {
            @Override
            protected Movie[] doInBackground() throws Exception {
                return interactor.search(query);
            }

            @Override
            protected void done() {
                try {
                    Movie[] results = get();

                    // Update uipanels.ResultsPanel with results
                    if (results.length == 0) {
                        JOptionPane.showMessageDialog(
                                advancedPanel,
                                "No matching movies found. Try different preferences!",
                                "No Results",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    } else {
                        resultsPanel.movieList.setListData(results);
                        appController.show(AppController.RESULTS);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(
                            advancedPanel,
                            "Error searching: " + e.getMessage() + "\n\nMake sure you:\n" +
                                    "1. Set API keys in apiservices.ApiConfig.java\n" +
                                    "2. Set PINECONE_HOST in apiservices.ApiConfig.java\n" +
                                    "3. Created Pinecone index (dimension: 768 for Nomic)\n" +
                                    "4. Ran searchlogic.MovieEmbeddingPipeline.java first to populate the index",
                            "Search Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                } finally {
                    // Re-enable search button
                    advancedPanel.searchButton.setEnabled(true);
                    advancedPanel.searchButton.setText("Search");
                }
            }
        };

        worker.execute();
    }
}