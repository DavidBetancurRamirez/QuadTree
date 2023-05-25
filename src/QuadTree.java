import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.imageio.ImageIO;

public class QuadTree implements Serializable {
	private static final long serialVersionUID = 1L;

	private Nodo raiz;
	private int anchoOriginal, altoOriginal, pixeles;
	private Color color;

	public QuadTree(File image, int pixeles, Color color) throws IOException, EInfo {
		this.raiz = new Nodo();
		this.pixeles = pixeles;
		this.color = color;
		crear(image);
	}

	public QuadTree(File image, int pixeles) throws IOException, EInfo {
		this.raiz = new Nodo();
		this.pixeles = pixeles;
		crear(image);
	}

	private void crear(File image) throws IOException, EInfo {
		// Cargar la imagen
		BufferedImage imagen = null;
		imagen = ImageIO.read(image);
		// Obtener propiedades importantes
		this.altoOriginal = imagen.getHeight();
		this.anchoOriginal = imagen.getWidth();
		// Pasando imagen a arbol
		compresion(imagen);
	}

	public void compresion(BufferedImage image) throws EInfo {
		/*
		 * Se crean arreglos del alto y el ancho de la imagen [0] = inicio de la imagen
		 * [1] = fin de la imagen
		 */
		int[] alto = { 0, this.altoOriginal - 1 };
		int[] ancho = { 0, this.anchoOriginal - 1 };
		compresion(image, alto, ancho, raiz);
	}

	public void compresion(BufferedImage image, int[] alto, int[] ancho, Nodo actual) throws EInfo {
		// Se obtiene el codigo rgb del primer pixel
		int pixel = image.getRGB(ancho[0], alto[0]);

		int fila = alto[0];
		int columna = ancho[0];
		boolean interrupcion = false;

		// Se recorre la imagen cada (this.pixeles) de pixeles
		// Se detiene cuando se excede el alto o ancho
		// O cuando algun pixel no coincide con el primero obtenido
		while (fila < alto[1] && !interrupcion) {
			columna = ancho[0];
			while (columna < ancho[1] && !interrupcion) {
				if (image.getRGB(columna, fila) != pixel) {
					interrupcion = true;
					fila -= this.pixeles;
					columna -= this.pixeles;
				}
				columna += this.pixeles;
			}
			fila += this.pixeles;
		}

		// Si fila y columna salen del margen, todos los pixeles son del mismo color
		if (fila >= alto[1] && columna >= ancho[1]) {
			// Se guarda el color en la hoja
			actual.setInfo(new Color(pixel));
		} else {
			// Se crean hijos
			actual.crearHijos();

			int mitadAlto = (alto[0] + alto[1]) / 2;
			int mitadAncho = (ancho[0] + ancho[1]) / 2;

			// NO
			compresion(image, new int[] { alto[0], mitadAlto }, new int[] { ancho[0], mitadAncho }, actual.getHijo(0));
			// NE
			compresion(image, new int[] { alto[0], mitadAlto }, new int[] { mitadAncho + 1, ancho[1] },
					actual.getHijo(1));
			// SE
			compresion(image, new int[] { mitadAlto + 1, alto[1] }, new int[] { mitadAncho + 1, ancho[1] },
					actual.getHijo(2));
			// SO
			compresion(image, new int[] { mitadAlto + 1, alto[1] }, new int[] { ancho[0], mitadAncho },
					actual.getHijo(3));
		}
	}

	public BufferedImage reconstruir() throws EInfo {
		// Crear base de imagen
		BufferedImage image = new BufferedImage(this.anchoOriginal, this.altoOriginal, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, this.anchoOriginal, this.altoOriginal);

		// Se llama reconstruir para pintar la imagen
		reconstruir(this.raiz, graphics, 0, 0, this.anchoOriginal, this.altoOriginal);

		graphics.dispose();
		return image;
	}

	private void reconstruir(Nodo nodo, Graphics2D graphics, int x, int y, int width, int height) throws EInfo {
		if (nodo != null) {
			if (nodo.getHijos() == null) {
				graphics.setColor(nodo.getInfo());
				graphics.fillRect(x, y, width, height);
			} else {
				int halfWidth = width / 2;
				int halfHeight = height / 2;

				// NO
				reconstruir(nodo.getHijo(0), graphics, x, y, halfWidth, halfHeight);
				// NE
				reconstruir(nodo.getHijo(1), graphics, x + halfWidth, y, width - halfWidth, halfHeight);
				// SE
				reconstruir(nodo.getHijo(2), graphics, x + halfWidth, y + halfHeight, width - halfWidth,
						height - halfHeight);
				// SO
				reconstruir(nodo.getHijo(3), graphics, x, y + halfHeight, halfWidth, height - halfHeight);
			}
		}
	}

	public void cuadricula(BufferedImage image) throws EInfo {
		Graphics2D graphics = image.createGraphics();
		graphics.setColor(color);
		graphics.setStroke(new BasicStroke(1f));
		cuadricula(this.raiz, graphics, 0, 0, this.anchoOriginal, this.altoOriginal);
	}

	public void cuadricula(Nodo nodo, Graphics2D g, int x, int y, int width, int height) throws EInfo {
		// Verificar si el nodo actual no es una hoja
		if (nodo.getHijos() != null) {
			// Dibujar líneas verticales y horizontales en las divisiones del Quadtree
			int midX = x + (width / 2);
			int midY = y + (height / 2);

			g.drawLine(midX, y, midX, y + height);
			g.drawLine(x, midY, x + width, midY);

			// Llamar recursivamente al método cuadricula para los hijos
			int halfWidth = width / 2;
			int halfHeight = height / 2;

			// NO
			cuadricula(nodo.getHijo(0), g, x, y, halfWidth, halfHeight);
			// NE
			cuadricula(nodo.getHijo(1), g, x + halfWidth, y, width - halfWidth, halfHeight);
			// SE
			cuadricula(nodo.getHijo(2), g, x + halfWidth, y + halfHeight, width - halfWidth, height - halfHeight);
			// SO
			cuadricula(nodo.getHijo(3), g, x, y + halfHeight, halfWidth, height - halfHeight);
		}
	}

	public void subirQuadTree(String path) throws IOException {
		FileOutputStream fos = new FileOutputStream(path);
		ObjectOutputStream oos = new ObjectOutputStream(fos);

		oos.writeObject(this);

		oos.close();
		fos.close();
	}

	public static QuadTree cargarQuadTree(String path) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(path);
		ObjectInputStream ois = new ObjectInputStream(fis);

		QuadTree qt = (QuadTree) ois.readObject();

		ois.close();
		fis.close();

		return qt;
	}
}