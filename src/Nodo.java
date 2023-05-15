class EInfo extends Exception{
	private static final long serialVersionUID = 1L;

	public EInfo(String m){
        super(m);
    }
}

public class Nodo {
	private int info; //1 = blanco, -1 = negro y 0 = null (para cuando no tenga valor sino que tenga hijos)
	private Nodo padre;
	private Nodo[] hijos;

    public Nodo() {
		this.info = 0;
        this.hijos= null;
	}
	
	public int getInfo() {
		return this.info;
	}
	public void setInfo(int info) throws EInfo {
        if (info!=-1 && info!=1) throw new EInfo("Dato invalido");
		this.info = info;
	}
	
	public Nodo getPadre() {
		return this.padre;
	}
	public void setPadre(Nodo padre) {
		this.padre = padre;
	}

    public Nodo[] getHijos() {
        return this.hijos;
    }
    public Nodo getHijo(int index) throws EInfo {
        if (0>index || index>4) throw new EInfo("Dato invalido");
        return this.hijos[index];
    }

    public void crearHijos() {
        this.hijos= new Nodo[4];
        for (int i=0;i<4;i++){
            hijos[i]= new Nodo();
            hijos[i].setPadre(this);
        }
    }
}
