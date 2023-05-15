import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class QuadTree {

    private Nodo raiz;
    private int anchoOriginal, altoOriginal, blanco, pot;
	
	public QuadTree(File imagejpg){
		this.raiz= new Nodo();
		this.blanco= Color.WHITE.getRGB();
		BufferedImage image = null;

		try {
			image= ImageIO.read(imagejpg);
			this.altoOriginal= image.getHeight();
			this.anchoOriginal= image.getWidth();
			this.pot= potenciaMayorCercana();
			image= ajustarImagen(image);
			compresion(image);
		} catch (IOException e) {
			System.out.println("No existe el archivo con dirección: " + imagejpg);
		} catch (EInfo e) {
			e.getMessage();
		}


	}
	
	public void compresion(BufferedImage image) throws EInfo {
		int size=pot;
		//Se consigue el alto de la imagen
		int[] alto = {0, size - 1};
		//Se consigue el ancho de la imagen
		int[] ancho = {0, size - 1};
		//Comienza la compresion en el metodo recursivo
		compresion(image, alto, ancho, raiz);
	}
	public void compresion(BufferedImage image, int[] alto, int[] ancho, Nodo actual) throws EInfo {
		//Se coge el pixel en las primeras cordenadas del alto y ancho
		int pixel = image.getRGB(ancho[0], alto[0]);

		int fila = alto[0];
		int columna = ancho[0];
		//Se busca en todo el area de pixeles si se tiene un color distinto del primer pixel
		boolean interrupcion = false;
		while(fila <= alto[1] && !interrupcion) {
			columna = ancho[0];
			while(columna <= ancho[1] && !interrupcion) {
				if(image.getRGB(columna, fila) != pixel) {
					interrupcion = true;
					fila--; columna--; //
				}
				columna++;
			}
			fila++;
		}

		//En caso de que se salga del area que tenemos, se define la hoja, si no, se crean los hijos.
		//Incluso cuando la subimagen que se analice sea de un solo pixel, se cumplirá en los whiles de arriba
		//que al sumarle 1 a fila y columna sera mayor que el punto en el que se ubicaba ese pixel
		if(fila > alto[1] && columna > ancho[1]) {
			//Se define la hoja con el color del pixel
			if(pixel == blanco) actual.setInfo(1);
			else actual.setInfo(-1);
		}else {
			//Se crean los hijos
			int mitadAlto = (alto[0] + alto[1])/2;
			int mitadAncho = (ancho[0] + ancho[1])/2;

			actual.crearHijos();
			//Arriba izquierda
			compresion(image, new int[] {alto[0], mitadAlto}, new int[] {ancho[0], mitadAncho}, actual.getHijo(0));
			//Arriba derecha
			compresion(image, new int[] {alto[0], mitadAlto}, new int[] {mitadAncho + 1, ancho[1]}, actual.getHijo(1));
			//Abajo derecha
			compresion(image, new int[] {mitadAlto + 1, alto[1]}, new int[] {mitadAncho + 1, ancho[1]}, actual.getHijo(2));
			//Abajo izquierda
			compresion(image, new int[] {mitadAlto + 1, alto[1]}, new int[] {ancho[0], mitadAncho}, actual.getHijo(3));

		}
	}
	
	public int potenciaMayorCercana(){ //Se encuentra la potencia de 2 más cercana y mayor o igual a la medida más grande (ancho o alto)
		int i=0, pot;
		while((pot= (int) Math.pow(2, i++))<anchoOriginal || pot<altoOriginal);
		return pot;
	}
	
	public BufferedImage ajustarImagen(BufferedImage image) throws IOException {
		BufferedImage newImage = image;

		if (anchoOriginal != pot || altoOriginal != pot) {
			GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
			// Se crea un nuevo lienzo con las dimensiones de la potencia encontrada
			newImage = config.createCompatibleImage(pot, pot);
			Graphics2D g2 = newImage.createGraphics();

			// Creamos bordes para la imagen tal que cumpla con las dimensiones deseadas y
			// requeridas para la conmpresion (2^n, 2^n)
			g2.setColor(Color.WHITE);
			g2.fillRect(0, 0, pot, pot);
			g2.drawImage(image, 0, 0, null);
			g2.dispose();
		}
		return newImage;
	}
	public BufferedImage limpiarImagen(BufferedImage image) throws IOException{
		return image.getSubimage(0, 0, anchoOriginal, altoOriginal);
	}

	public void reconstruccion() throws EInfo, IOException {
		//Se lee la imagen
		int size= pot;
		BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);

		//Se consigue el alto de la imagen
		int[] alto = {0, size - 1};
		//Se consigue el ancho de la imagen
		int[] ancho = {0, size - 1};

		//Comienza la reconstruccion en el metodo recursivo
		reconstruccion(image, alto, ancho, raiz);
		//Por ultimo se limpia la imagen en caso de ser necesario
		image= limpiarImagen(image);
        ImageIO.write(image, "png", new File("C:\\Users\\David\\eclipse-workspace\\QuadTree\\src\\Imagenes\\Reconstruccion.png"));
	}
	public void reconstruccion(BufferedImage image, int[] alto, int[] ancho, Nodo actual) throws EInfo {
		//Nos fijamos si el nodo actual tiene hijos
		if (actual.getHijos()!=null){
			int mitadAlto = (alto[0] + alto[1])/2;
			int mitadAncho = (ancho[0] + ancho[1])/2;
			//Arriba izquierda
			reconstruccion(image, new int[] {alto[0], mitadAlto}, new int[] {ancho[0], mitadAncho}, actual.getHijo(0));
			//Arriba derecha
			reconstruccion(image, new int[] {alto[0], mitadAlto}, new int[] {mitadAncho + 1, ancho[1]}, actual.getHijo(1));
			//Abajo derecha
			reconstruccion(image, new int[] {mitadAlto + 1, alto[1]}, new int[] {mitadAncho + 1, ancho[1]}, actual.getHijo(2));
			//Abajo izquierda
			reconstruccion(image, new int[] {mitadAlto + 1, alto[1]}, new int[] {ancho[0], mitadAncho}, actual.getHijo(3));
		}else{
			Color color= (actual.getInfo()==1)? Color.WHITE: Color.BLACK;
			int fila = alto[0];
			int columna = ancho[0];
			//Se busca en todo el area de pixeles si se tiene un color distinto del primer pixel
			while(fila <= alto[1]) {
				columna = ancho[0];
				while(columna <= ancho[1]) {
					image.setRGB(columna, fila, color.getRGB());
					columna++;
				}
				fila++;
			}
		}
	}

	public static void main(String[] args) {
		File archivo_imagen = new File("C:\\Users\\David\\eclipse-workspace\\QuadTree\\src\\Imagenes\\Ejemplo2.png");
		QuadTree a = new QuadTree(archivo_imagen);
		try {
			a.reconstruccion();
		} catch (EInfo e) {
			e.getMessage();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
