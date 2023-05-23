import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class Interfaz extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField cantidadPxBox;
	private QuadTree qt;
	private String ruta = "";
	private final String convertedDirectory = "src\\Imagenes\\Reconstruccion.png";
	private ImageIcon uploadedImageIcon;
	private Color selectedColor = Color.BLACK;

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
		setBounds(180, 8, 1200, 650);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setBackground(new Color(254, 208, 173));
		setContentPane(contentPane);
		setLocationRelativeTo(null);
		contentPane.setLayout(null);

		JButton exitButton = new JButton();
		ImageIcon icon = new ImageIcon("src\\closeIcon.png");
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

		cantidadPxBox = new JTextField();
		cantidadPxBox.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if (cantidadPxBox.getText().isEmpty()) {
		            cantidadPxBox.setText("Cantidad px");
		            cantidadPxBox.setForeground(Color.GRAY);
		            cantidadPxBox.setFont(cantidadPxBox.getFont().deriveFont(Font.ITALIC));
		        }
			}
		});
		cantidadPxBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				 cantidadPxBox.setText("");
		         cantidadPxBox.setForeground(Color.BLACK);
		         cantidadPxBox.setFont(cantidadPxBox.getFont().deriveFont(Font.PLAIN));
			}
		});
		cantidadPxBox.setForeground(Color.GRAY);
        cantidadPxBox.setFont(cantidadPxBox.getFont().deriveFont(Font.ITALIC));
        cantidadPxBox.setText("Cantidad px");
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
		cantidadPxBox.setBounds(480, 15, 120, 30);
		contentPane.add(cantidadPxBox);
		cantidadPxBox.setColumns(10);

		final JLabel uploadImage = new JLabel("");
		uploadImage.setSize(0, 0);
		contentPane.add(uploadImage);

		final JLabel convertedImage = new JLabel("");
		convertedImage.setSize(0, 0);
		contentPane.add(convertedImage);
		
		JButton colorButton = new JButton("Color");
		colorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedColor = JColorChooser.showDialog(Interfaz.this, "Selecciona un color", Color.BLACK);
			}
		});
		colorButton.setBounds(640, 15, 120, 30);
		colorButton.setBackground(new Color(255, 180, 126));
		contentPane.add(colorButton);

		JButton uploadButton = new JButton("Cargar imagen");
		uploadButton.setBackground(new Color(255, 180, 126));
		uploadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				convertedImage.setIcon(null);
				JFileChooser fileChooser = new JFileChooser();
				File defaultDirectory = new File("src\\Imagenes");
				fileChooser.setCurrentDirectory(defaultDirectory);
				FileNameExtensionFilter filtro = new FileNameExtensionFilter("PNG", "png");
				fileChooser.setFileFilter(filtro);

				int result = fileChooser.showOpenDialog(Interfaz.this);
				if (result == JFileChooser.APPROVE_OPTION) {
					ruta = fileChooser.getSelectedFile().getPath();

					Image img = new ImageIcon(ruta).getImage();

					double width = img.getWidth(uploadImage);
					double height = img.getHeight(uploadImage);
					
					if (width >= height) {
						height *= validate(width);
						width *= validate(width);
					}else if (height > width){
						width *= validate(height);
						height *= validate(height);
					}
					
					uploadImage.setSize((int)width, (int)height);
					uploadImage.setLocation(contentPane.getWidth() / 4 - (int)width / 2,
							contentPane.getHeight() / 2 - (int)height / 2);
					ImageIcon icon = new ImageIcon(img.getScaledInstance((int)width, (int)height, Image.SCALE_SMOOTH));
					uploadImage.setIcon(icon);
				}
			}
		});
		uploadButton.setBounds(60, 15, 120, 30);
		contentPane.add(uploadButton);

		JButton convertButton = new JButton("Convertir");
		convertButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File archivo = new File(ruta);

				try {
					if(cantidadPxBox.getText().compareTo("Cantidad px") == 0)
						cantidadPxBox.setText("");
					if (!cantidadPxBox.getText().isEmpty()) {
						int px = Integer.parseInt(cantidadPxBox.getText());
						qt = new QuadTree(archivo, px, selectedColor);
					} else {
						qt = new QuadTree(archivo, selectedColor);
					}
				} catch (Exception m) {
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
					ImageIO.write(i, "png", new File(convertedDirectory));
				} catch (EInfo m) {
					JOptionPane.showMessageDialog(null, "Problemas al reconstruir");
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "Problemas con la dirección de guardado");
				}

				boolean archivoValido = false;
				File selectedFile = null;
				String filePath = "";
				
				while (!archivoValido) {
					JFileChooser fileChooser = new JFileChooser();
					File defaultDirectory = new File("src\\Imagenes");
					fileChooser.setCurrentDirectory(defaultDirectory);
					int result = fileChooser.showOpenDialog(Interfaz.this);
					File n = new File(convertedDirectory);
					if (result == JFileChooser.APPROVE_OPTION) {
						selectedFile = fileChooser.getSelectedFile();
						filePath = selectedFile.getAbsolutePath();
					
						if(!filePath.endsWith(".png"))
							filePath += ".png";
						 
						if (selectedFile.exists()) {
							int existe = JOptionPane.showConfirmDialog(null, "El archivo ya existe. ¿Desea sobrescribirlo?", "Confirmación", JOptionPane.YES_NO_OPTION);
							if (existe == JOptionPane.NO_OPTION) {
								JOptionPane.showMessageDialog(null, "Entonces debe de cambiar el nombre del proyecto");
								continue;
							}
						}							
						archivoValido = true;
		
					try {
						Path sourcePath = n.toPath();
						Path destinationPath = new File(filePath).toPath();
						Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
						ruta = destinationPath.toString();
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null, "Problemas con la dirección de guardado");
					}

					BufferedImage convertedImg;
					try {
					    convertedImg = ImageIO.read(new File(convertedDirectory));
					} catch (IOException e1) {
					    JOptionPane.showMessageDialog(null, "Error al leer la imagen convertida");
					    return;
					}

					double width = convertedImg.getWidth();
					double height = convertedImg.getHeight();
					
					if (width >= height) {
						height *= validate(width);
						width *= validate(width);
					}else if (height > width){
						width *= validate(height);
						height *= validate(height);
					}

					convertedImage.setSize((int)width, (int)height);
					convertedImage.setLocation(contentPane.getWidth() * 3 / 4 - (int)width / 2,
					        contentPane.getHeight() / 2 - (int)height / 2);
					ImageIcon convertedIcon = new ImageIcon(convertedImg.getScaledInstance((int)width, (int)height, Image.SCALE_SMOOTH));
					convertedImage.setIcon(convertedIcon);


				} else {
					convertedImage.setIcon(uploadedImageIcon);
					break;
				}

			}
			}
		});
		convertButton.setBounds(800, 15, 120, 30);
		convertButton.setBackground(new Color(255, 180, 126));
		contentPane.add(convertButton);
		
		

	}

	public double validate(double a) {
		if (a < 400)
			return 400/a;
		else if (a > 500)
			return 500/a;
		else
			return 1;
	}
}
