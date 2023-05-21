import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class QuadTree {

    private Nodo raiz;
    private int anchoOriginal, altoOriginal, pixeles;
	
	public QuadTree(File imagejpg){
		this.raiz= new Nodo();
		// Se recorre de a 1 pixel
		this.pixeles = 1;
		crear(imagejpg);
	}
	
	public QuadTree(File imagejpg, int pixeles){
		this.raiz= new Nodo();
		this.pixeles = pixeles;
		crear(imagejpg);
	}
	
	private void crear(File imagejpg) {
		try {
			BufferedImage image = null;
			image= ImageIO.read(imagejpg);
			this.altoOriginal= image.getHeight();
			this.anchoOriginal= image.getWidth();
			compresion(image);
		} catch (IOException e) {
			System.out.println("No existe el archivo con dirección: " + imagejpg);
		} catch (EInfo e) {
			System.out.println(e.getMessage());
		}
	}
	
	public Nodo getRaiz() {
		return raiz;
	}
	public void setRaiz(Nodo raiz) {
		this.raiz = raiz;
	}

	public int getAnchoOriginal() {
		return anchoOriginal;
	}
	public void setAnchoOriginal(int anchoOriginal) {
		this.anchoOriginal = anchoOriginal;
	}

	public int getAltoOriginal() {
		return altoOriginal;
	}
	public void setAltoOriginal(int altoOriginal) {
		this.altoOriginal = altoOriginal;
	}

	public int getPixeles() {
		return pixeles;
	}
	public void setPixeles(int pixeles) {
		this.pixeles = pixeles;
	}

	public void compresion(BufferedImage image) throws EInfo {
		// Se crean arreglos del alto y el ancho de la imagen
		/*
		 * [0] = inicio de la imagen
		 * [1] = fin de la imagen
		 */
		int[] alto = {0, this.altoOriginal - 1};
		int[] ancho = {0, this.anchoOriginal - 1};
		compresion(image, alto, ancho, raiz);
	}
	public void compresion(BufferedImage image, int[] alto, int[] ancho, Nodo actual) throws EInfo {
		// Se obtiene el codigo rgb del primer pixel
		int pixel = image.getRGB(ancho[0], alto[0]);

		int fila = alto[0];
		int columna = ancho[0];
		boolean interrupcion = false;
		
		// Se recorre la imagen cada cantidad de pixeles (this.pixeles)
		// Para cuando se pasa del alto o ancho que se le pasa
		// O cuando algun pixel no coincide con el primero obtenido
		while(fila < alto[1] && !interrupcion) {
			columna = ancho[0];
			while(columna < ancho[1] && !interrupcion) {
				if(image.getRGB(columna, fila) != pixel) {
					interrupcion = true;
					fila-=this.pixeles; 
					columna-=this.pixeles;
				}
				columna+=this.pixeles;
			}
			fila+=this.pixeles;
		}

		// Si fila y columna salen del margen, todos los pixeles son del mismo color
		if(fila > alto[1] && columna > ancho[1]) {
			// Se guarda el color en la hoja
			actual.setInfo(new Color(pixel));
		}else {
			//Se crean hijos
			actual.crearHijos();
			
			int mitadAlto = (alto[0] + alto[1])/2;
			int mitadAncho = (ancho[0] + ancho[1])/2;

			// NO
			compresion(image, new int[] {alto[0], mitadAlto}, new int[] {ancho[0], mitadAncho}, actual.getHijo(0));
			// NE
			compresion(image, new int[] {alto[0], mitadAlto}, new int[] {mitadAncho + 1, ancho[1]}, actual.getHijo(1));
			// SE
			compresion(image, new int[] {mitadAlto + 1, alto[1]}, new int[] {mitadAncho + 1, ancho[1]}, actual.getHijo(2));
			// SO
			compresion(image, new int[] {mitadAlto + 1, alto[1]}, new int[] {ancho[0], mitadAncho}, actual.getHijo(3));
		}
	}

	public static void main(String[] args) {
		try {
			File archivo_imagen = new File("src\\Imagenes\\ejm7.png");
			QuadTree a = new QuadTree(archivo_imagen,5);
			System.out.println(a);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
