import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.Window.Type;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.awt.ScrollPane;
import java.awt.Point;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Interfaz extends JFrame {

	private JPanel contentPane;
	private JTextField cantidadPxBox;
	private QuadTree qt;
	private String ruta = "";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Interfaz frame = new Interfaz();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Interfaz() {
		setUndecorated(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(180, 8, 1200, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setBackground(new Color(254, 208, 173));
		setContentPane(contentPane);
		setLocationRelativeTo(null);
		contentPane.setLayout(null);

		JButton exitButton = new JButton();
		ImageIcon icon = new ImageIcon("src\\Imagenes\\closeIcon.png");
		Image image = icon.getImage();
		Image scaledImage = image.getScaledInstance(50, 50, Image.SCALE_SMOOTH); // Ajusta el tamaño deseado
		ImageIcon scaledIcon = new ImageIcon(scaledImage);
		exitButton.setIcon(scaledIcon);
		exitButton.setAlignmentY(Component.TOP_ALIGNMENT);
		exitButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		exitButton.setBounds(1150, 0, 50, 50);
		exitButton.setBackground(new Color(255, 180, 126));
		contentPane.add(exitButton);

		JLabel cantidadPx = new JLabel("Cantidad de píxeles");
		cantidadPx.setFont(new Font("Times New Roman", Font.BOLD, 13));
		cantidadPx.setBounds(640, 10, 120, 15);
		contentPane.add(cantidadPx);

		cantidadPxBox = new JTextField();
		cantidadPxBox.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				int key = e.getKeyChar();
				boolean numero = key >= 48 && key <= 57;

				if (!numero || cantidadPxBox.getText().length() >= 2)
					e.consume();
			}
		});
		cantidadPxBox.setBackground(new Color(255, 222, 173));
		cantidadPxBox.setBounds(640, 35, 120, 25);
		contentPane.add(cantidadPxBox);
		cantidadPxBox.setColumns(10);

		final JLabel uploadImage = new JLabel("");
		uploadImage.setSize(0, 0);
		contentPane.add(uploadImage);

		JButton uploadButton = new JButton("Cargar imagen");
		uploadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				File defaultDirectory = new File ("src\\Imagenes");
				fileChooser.setCurrentDirectory(defaultDirectory);
				FileNameExtensionFilter filtro = new FileNameExtensionFilter("PNG & JPG", "png", "jpg");
				fileChooser.setFileFilter(filtro);

				int result = fileChooser.showOpenDialog(Interfaz.this);
				if (result == JFileChooser.APPROVE_OPTION) {
					ruta = fileChooser.getSelectedFile().getPath();
					
					Image img = new ImageIcon(ruta).getImage();
					
					
					int width = img.getWidth(uploadImage); 
					int height = img.getHeight(uploadImage);
					
					height *= validate(width);
					width *= validate(width);
					uploadImage.setSize(width, height);
					uploadImage.setLocation(contentPane.getWidth() / 4 - width / 2, contentPane.getHeight() / 2 - height / 2);
					ImageIcon icon = new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
					uploadImage.setIcon(icon);
				}
			}
		});
		uploadButton.setBounds(60, 35, 120, 30);
		uploadButton.setBackground(new Color(255, 180, 126));
		contentPane.add(uploadButton);

		final JLabel convertedImage = new JLabel("");
		convertedImage.setBounds(521, 534, 45, 13);
		contentPane.add(convertedImage);

		JButton convertButton = new JButton("Convertir");
		convertButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File archivo = new File (ruta);
				
				try {
					if (cantidadPxBox.getText().length() != 0) {
						int px = Integer.parseInt(cantidadPxBox.getText());
						qt = new QuadTree (archivo, px);
					} else {
						qt = new QuadTree (archivo);
					}
				}catch (Exception m) {
					uploadImage.setIcon(null);
					JOptionPane.showMessageDialog(null, "Cantidad de px inválidas, por favor ingrese otra cantidad");
					return;
				}
			
				if (qt == null) {
					JOptionPane.showMessageDialog(null, "Se debe de seleccionar una imagen primero");
					return;
				}
				
				try {
					BufferedImage i = qt.reconstruir();
					ImageIO.write(i, "png", new File("src\\Imagenes\\Reconstruccion.png"));
				}catch (EInfo m) {
					JOptionPane.showMessageDialog(null, "Problemas al reconstruir");
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "Problemas con la dirección de guardado");
				}
				
				JFileChooser fileChooser = new JFileChooser();
				File defaultDirectory = new File ("src\\Imagenes");
				fileChooser.setCurrentDirectory(defaultDirectory);
				int result = fileChooser.showOpenDialog(Interfaz.this);

				File n = new File("src\\Imagenes\\Reconstruccion.png");
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					String filePath = selectedFile.getAbsolutePath();
					String originalFileExtension = getFileExtension(n.getAbsolutePath());
					filePath = filePath + "." + originalFileExtension;
					String ruta = "";
					try {
						Path sourcePath = n.toPath();
						Path destinationPath = new File(filePath).toPath();
						ruta = destinationPath.toString();
						Files.copy(sourcePath, destinationPath);
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null, "Problemas con la dirección de guardado");
					}
					
					Image img = new ImageIcon(ruta).getImage();
					
					int width = img.getWidth(convertedImage); 
					int height = img.getHeight(convertedImage);
					height *= validate(width);
					width *= validate(width);
					
					convertedImage.setSize(width, height);
					convertedImage.setLocation(contentPane.getWidth()*3/ 4 - width / 2, contentPane.getHeight() / 2 - height / 2);
					ImageIcon icon = new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
					convertedImage.setIcon(icon);
				}

			}
		});
		convertButton.setBounds(800, 35, 120, 30);
		convertButton.setBackground(new Color(255, 180, 126));
		contentPane.add(convertButton);

	}

	public int validate(int a) {
		if (a >= 300)
			return 1;
		else if (a < 300 && a >= 100)
			return 3;
		else
			return 5;
	}

	private String getFileExtension(String filePath) {
		String extension = "";
		int dotIndex = filePath.lastIndexOf('.');
		if (dotIndex > 0 && dotIndex < filePath.length() - 1) {
			extension = filePath.substring(dotIndex + 1).toLowerCase();
		}
		return extension;
	}
}
