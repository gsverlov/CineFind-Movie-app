import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MovieCellRenderer extends JPanel implements ListCellRenderer<Object> {

    private JLabel titleLabel;
    private JLabel yearLabel;
    private JLabel typeLabel;
    private JLabel idLabel;
    private JLabel posterLabel;
    private JPanel textPanel;

    private static Map<String, ImageIcon> imageCache = new HashMap<>();
    private static Set<String> pendingDownloads = Collections.synchronizedSet(new HashSet<>());
    private final ImageIcon placeholderIcon;

    public MovieCellRenderer() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setOpaque(true);

        placeholderIcon = createPlaceholder();

        textPanel = new JPanel(new GridLayout(4, 1));
        textPanel.setOpaque(false);

        titleLabel = new JLabel();
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        yearLabel = new JLabel();
        typeLabel = new JLabel();
        idLabel = new JLabel();

        yearLabel.setForeground(Color.DARK_GRAY);
        typeLabel.setForeground(Color.GRAY);
        idLabel.setForeground(Color.GRAY);

        textPanel.add(titleLabel);
        textPanel.add(yearLabel);
        textPanel.add(typeLabel);
        textPanel.add(idLabel);

        add(textPanel, BorderLayout.CENTER);

        posterLabel = new JLabel();
        posterLabel.setHorizontalAlignment(SwingConstants.CENTER);
        posterLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        add(posterLabel, BorderLayout.EAST);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        if (value instanceof Movie) {
            Movie movie = (Movie) value;

            titleLabel.setText(movie.title);
            yearLabel.setText("Year: " + movie.year);
            typeLabel.setText("Type: " + movie.type);
            idLabel.setText("ID: " + movie.imdbID);

            String url = movie.poster;

            if (url != null && !url.equals("N/A")) {
                if (imageCache.containsKey(url)) {
                    ImageIcon icon = imageCache.get(url);
                    if (icon != null) {
                        posterLabel.setIcon(icon);
                        posterLabel.setText("");
                    } else {
                        posterLabel.setIcon(placeholderIcon);
                        posterLabel.setText("Error");
                    }
                } else {
                    posterLabel.setIcon(placeholderIcon);
                    posterLabel.setText("Loading...");

                    if (!pendingDownloads.contains(url)) {
                        loadImage(url, list);
                    }
                }
            } else {
                posterLabel.setIcon(placeholderIcon);
                posterLabel.setText("No Image");
            }
        } else if (value != null) {
            titleLabel.setText(value.toString());
            yearLabel.setText("");
            typeLabel.setText("");
            idLabel.setText("");
            posterLabel.setIcon(null);
            posterLabel.setText("");
        }

        return this;
    }

    private void loadImage(String urlString, JList<?> list) {
        pendingDownloads.add(urlString);
        new Thread(() -> {
            try {
                URL url = new URL(urlString);
                ImageIcon originalIcon = new ImageIcon(url);

                if (originalIcon.getIconWidth() > 0) {
                    Image scaledImage = originalIcon.getImage().getScaledInstance(60, 90, Image.SCALE_SMOOTH);
                    ImageIcon icon = new ImageIcon(scaledImage);
                    imageCache.put(urlString, icon);
                } else {
                    imageCache.put(urlString, null);
                }
                SwingUtilities.invokeLater(list::repaint);

            } catch (Exception e) {
                imageCache.put(urlString, null);
            } finally {
                pendingDownloads.remove(urlString);
            }
        }).start();
    }

    private ImageIcon createPlaceholder() {
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(60, 90, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(230, 230, 230));
        g.fillRect(0, 0, 60, 90);
        g.setColor(Color.GRAY);
        g.drawRect(0, 0, 59, 89);
        g.dispose();
        return new ImageIcon(img);
    }
}