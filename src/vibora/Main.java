package vibora;

import com.golden.gamedev.*;
import com.golden.gamedev.object.*;
import com.golden.gamedev.object.background.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import com.golden.gamedev.object.Timer;

/* Datos importantes:

Las direcciones en el juego se manejan como ints.
1 = izquierda.
2 = derecha.
3 = arriba.
4 = abajo.
*/
/*public class IntegerComp implements Comparator<Integer>{
    @Override
    public int compare(int x, int y)
    {
        // Assume neither string is null. Real code should
        // probably be more robust
        // You could also just return x.length() - y.length(),
        // which would be more efficient.
        if (x < y)
        {
            return -1;
        }
        if (x > y)
        {
            return 1;
        }
        return 0;
    }
}*/
public class Main extends Game {
	/* Clase Spt es el objeto de cada elemento del juego,
	simplemente maneja las coordenadas correspondientemente*/
	static class Spt{
		public int x,y;
		Spt(int x, int y){
			this.x=x;
			this.y=y;
		}
	}
	Background fondo, f1, f2;
	BufferedImage serpiente, comidita, obstaculo, serpienteEnemiga;
	static Random r;
	/* Determinamos el tamanio de la matriz */
	static final int VERTICAL_SIZE=22;
	static final int HORIZONTAL_SIZE=28;
	static final int POTENCIA = 30;
	static int[][] mat=new int[VERTICAL_SIZE][HORIZONTAL_SIZE];
	static int[][] dec=new int[VERTICAL_SIZE][HORIZONTAL_SIZE];
	static int profundidad=4;
	static int inf = 10000;
	static int food = 99;
	static ArrayList<Spt> jug, comidas, ai;
	/* Inicializamos a mi serpiente hacia la derecha siempre*/
	static int dirJug = 2;
	/* Inicializamos la direccion de la otra serpiente */
	static int dirAi = 1;
	static int nivel=-1;
	//static Comparator<Integer> comparator = new IntegerComp();
	static PriorityQueue<Integer> queue;

	/* Iniciamos los elementos, imagenes,  alistamos los Frames Per Second */
	public void initResources(){
		fondo = new ImageBackground(getImage("images/background.jpg"),768,576);
		f1 = new ImageBackground(getImage("images/titulo.jpg"),768,576);
		f2 = new ImageBackground(getImage("images/instrucciones.jpg"),768,576);
		serpiente = getImage("images/cuadroblanco.jpeg");
		comidita = getImage("images/egg.png");
		obstaculo = getImage("images/bardita.png");
		serpienteEnemiga = getImage("images/cuadrorojo.jpg");
		r = new Random();
		/*
		for(int i=0;i<28;i++){
			for(int j=0;j<22;j++){
				mat[i][j]=0;
				if(i==0 || i==27 || j==0 || j==21)
					mat[i][j]=-2;
			}
		}

		mat[r.nextInt(26)+1][r.nextInt(20)+1] = 99;
		mat[r.nextInt(26)+1][r.nextInt(20)+1] = 99;
		mat[r.nextInt(26)+1][r.nextInt(20)+1] = 99;
		*/
		setFPS(2);
	}

	//INICIO DE SECCION: CODIGOS DE A ESTRELLA
	static void llenaDec(){ //nos llena la matriz dec con los valores de manhattan
		for(int i=1;i<VERTICAL_SIZE-1;i++){
			for(int j=1;j<HORIZONTAL_SIZE-1;j++){
				int h = manhattan(new Spt(j,i),jug.get(0)); //saco manhattan entre nodo actual y cabeza de jugador
				if(h<=POTENCIA){
					dec[i][j]=(int)Math.pow(2,POTENCIA-h);
				}
				else{
					dec[i][j]=0;
				}
			}
		}
		for(int i=0;i<jug.size();i++){
			dec[jug.get(i).y][jug.get(i).x]=-1;
		}
		for(int i=0;i<ai.size();i++){
			dec[ai.get(i).y][ai.get(i).x]=-3;
		}
	}
	static int ataca(){ //regresa la direccion que mas conviene a ai moverse si quiere atacar
		//por ahora la implementacion es voraz pero pues le podemos echar la priority queue
		int opciones[]= new int[5];
		Spt head = ai.get(0);
		Arrays.fill(opciones,-inf);
		if(dec[head.y][head.x-1]>=0&&dirAi!=2){//checamos que no tenga obstaculo y no implique un giro de 180 grados
			
			opciones[1]=dec[head.y][head.x-1];
		}
		if(dec[head.y][head.x+1]>=0&&dirAi!=1){//checamos que no tenga obstaculo y no implique un giro de 180 grados
			opciones[2]=dec[head.y][head.x+1];
		}
		if(dec[head.y-1][head.x]>=0&&dirAi!=4){//checamos que no tenga obstaculo y no implique un giro de 180 grados
			opciones[3]=dec[head.y-1][head.x];
		}
		if(dec[head.y+1][head.x]>=0&&dirAi!=3){//checamos que no tenga obstaculo y no implique un giro de 180 grados
			opciones[4]=dec[head.y+1][head.x];
		}
		int max=-inf+1;
		int indice=1;
		for(int i=1;i<5;i++){
			if(opciones[i]>=max){
				max=opciones[i];
				indice=i;
			}
		}

		return indice;
	}
	//FIN DE SECCION: CODIGOS DE A ESTRELLA

	static void llenaTablero(){
		for(int i=0;i<VERTICAL_SIZE;i++){
			for(int j=0;j<HORIZONTAL_SIZE;j++){
				if(i==0||j==0||i==VERTICAL_SIZE-1||j==HORIZONTAL_SIZE-1){
					mat[i][j]=-2;
					dec[i][j]=-2;
				}
				else{
					mat[i][j]=0;
					dec[i][j]=0;
				}
			}
		}
		for(int i=0;i<jug.size();i++){
			mat[jug.get(i).y][jug.get(i).x]=-1;
		}for(int i=0;i<comidas.size();i++){
			mat[comidas.get(i).y][comidas.get(i).x]=food;
		}
		for(int i=0;i<ai.size();i++){
			mat[ai.get(i).y][ai.get(i).x]=-3;
		}
	//mat[4][24]=-2;
	//mat[3][24]=-2;
	//mat[3][23]=-2;
	//mat[4][22]=-2;
	//mat[5][22]=-2;
	//mat[7][9]=-2;
	//mat[6][24]=-2;
    //mat[5][5]=-2;
	//mat[4][15]=-2;
	}
	static void llenaComidas(){
		for(int i=0;i<comidas.size();i++){
			mat[comidas.get(i).y][comidas.get(i).x]=food;
		}
	}
	/* Regresa true si detecta que, moviendose en la direccion actual, chocara la serpiente en el siguiente movimiento */
	public static boolean aiPeligra(ArrayList<Spt> ai){
		Spt head = ai.get(0);
		if(dirAi==1){
			if(mat[head.y][head.x-1]<0)
				return true;
		}
		else if(dirAi==2){
			if(mat[head.y][head.x+1]<0)
				return true;
		}
		else if(dirAi==3){
			if(mat[head.y-1][head.x]<0)
				return true;
		}
		else if(dirAi==4){
			if(mat[head.y+1][head.x]<0)
				return true;
		}
		return false;
	}
	/* Simple metodo de movimiento evasivo para la serpiente */
	public static int determinaDir(ArrayList<Spt> ai){
		Spt head = ai.get(0);
		int nuevaDir = dirAi;
		if(nuevaDir==1){//si para la izq choco
			if(mat[head.y-1][head.x]>=0)//si puedo ir para arriba
			return 3;
			else return 4;//si no, para abajo
		}
		if(nuevaDir==2){
			if(mat[head.y-1][head.x]>=0)//si puedo ir para arriba
			return 3;
			else return 4;//si no, para abajo
		}
		if(nuevaDir==3){
			if(mat[head.y][head.x-1]>=0)//si puedo ir para izquierda
			return 1;
			else return 2;//si no, para derecha
		}
		if(nuevaDir==4){
			if(mat[head.y][head.x-1]>=0)//si puedo ir para izquierda
			return 1;
			else return 2;//si no, para derecha
		}
		System.out.println("ERROR: en determinaDir");
		return 1;
	}

	/* Detecta si la serpiente choco consigo mismo o con la pared */
	public static boolean hasCrashed(ArrayList<Spt> s, ArrayList<Spt> enemigoS){
		Spt head= s.get(0);
		if(mat[head.y][head.x]<0){
			return true;
		}
		if(head.x<=0||head.x>=HORIZONTAL_SIZE-1||head.y<=0||head.y>=VERTICAL_SIZE-1){
			return true;
		}
		if(igualesCoor(head,enemigoS.get(0))) return true; // EMPATE
		for(int i=1;i<s.size();i++){
			if(igualesCoor(head,s.get(i))){
				return true;
			}
		}
		for(int i=1;i<enemigoS.size();i++){
			if(igualesCoor(head,enemigoS.get(i))){
				return true;
			}
		}
		return false;
	}
	public static boolean igualesCoor(Spt a, Spt b){
		if(a.x==b.x&&a.y==b.y) return true;
		return false;
	}
	static void generaComida(ArrayList<Spt> jug, ArrayList<Spt> ai, ArrayList<Spt> comidas){
		boolean loop =true;
		Spt nuevaComida = new Spt(r.nextInt(HORIZONTAL_SIZE-2)+1,r.nextInt(VERTICAL_SIZE-2)+1);
		while(loop){
			nuevaComida = new Spt(r.nextInt(HORIZONTAL_SIZE-2)+1,r.nextInt(VERTICAL_SIZE-2)+1);
			loop=false;
			for(int i=0;i<jug.size();i++){
				if(igualesCoor(nuevaComida, jug.get(i))){
					loop=true;
					break;
				}
			}
			if(loop) continue;
			for(int i=0;i<ai.size();i++){
				if(igualesCoor(nuevaComida, ai.get(i))){
					loop=true;
					break;
				}
			}
			
			break;
		}
		comidas.add(nuevaComida);
		llenaComidas();
	}
	static int manhattan(Spt s, Spt com){
		// si estan en la misma ubicacion es mejor solucion posible
		if(s.x==com.x&&s.y==com.y)
			return Integer.MIN_VALUE;
		return (Math.abs(s.x-com.x)+Math.abs(s.y-com.y));
	}

	static int funcionEvaluacion(ArrayList<Spt> ai, ArrayList<Spt> comidas){
		/* Regresa la direccion que debe moverse la serpiente */
		/* NB: Hasta ahora solo funciona con una comida */
		int opciones[]= new int[5];
		Spt head = ai.get(0);
		Arrays.fill(opciones,inf);
		if(mat[head.y][head.x-1]>=0&&dirAi!=2){//checamos que no tenga obstaculo y no implique un giro de 180 grados
			if(mat[head.y][head.x-1]==food){
				opciones[1]=-1;
			}
			else{
				opciones[1]=manhattan(new Spt(head.x-1,head.y),comidas.get(0));
			}
		}
		if(mat[head.y][head.x+1]>=0&&dirAi!=1){//checamos que no tenga obstaculo y no implique un giro de 180 grados
			if(mat[head.y][head.x+1]==food){
				opciones[2]=-1;
			}
			else{
				opciones[2]=manhattan(new Spt(head.x+1,head.y),comidas.get(0));
			}
		}
		if(mat[head.y-1][head.x]>=0&&dirAi!=4){//checamos que no tenga obstaculo y no implique un giro de 180 grados
			if(mat[head.y-1][head.x]==food){
				opciones[3]=-1;
			}
			else{
				opciones[3]=manhattan(new Spt(head.x,head.y-1),comidas.get(0));
			}
		}
		if(mat[head.y+1][head.x]>=0&&dirAi!=3){//checamos que no tenga obstaculo y no implique un giro de 180 grados
			if(mat[head.y+1][head.x]==food){
				opciones[4]=-1;
			}
			else{
				opciones[4]=manhattan(new Spt(head.x,head.y+1),comidas.get(0));
			}
		}
		int min=inf-1;
		int indice=1;
		for(int i=1;i<5;i++){
			if(opciones[i]<=min){
				min=opciones[i];
				indice=i;
			}
		}

		return indice;
	}
	/* Ejecuta movimiento de serpientes y checa que el juego continua */
	static int pasaTurno(int dir, ArrayList<Spt> jug, ArrayList<Spt> ai, ArrayList<Spt> comidas){
		/* determina estado actual del juego. 0 si activo, -1 si perdiste, 1 si ganaste. */
		int estado=0;
		
		//1. El control del jugador
		if(dir!=0){
			dirJug=dir;
		}
		Spt oldhead=jug.get(0);
		//izquierda
		Spt newhead;
		if(dirJug==1)
			newhead = new Spt(oldhead.x-1,oldhead.y);
		else if(dirJug==2)
			newhead = new Spt(oldhead.x+1,oldhead.y);
		else if(dirJug==3)
			newhead = new Spt(oldhead.x,oldhead.y-1);
		else
			newhead = new Spt(oldhead.x,oldhead.y+1);

		//Checamos si hay comida en lugar que acabamos de entrar, si es asi, entonces no eliminamos cola
		if(mat[newhead.y][newhead.x]!=food)
			jug.remove(jug.size()-1);
		else{
			for(int i=0;i<comidas.size();i++){
				if(newhead.y==comidas.get(i).y&&newhead.x==comidas.get(i).x){
					comidas.remove(i);
					generaComida(jug,ai,comidas);
					break;
				}
			}
		}
		//2. El control de la AI. Solo cambia de direccion si ve peligro en el cuadro justo enfrente.
		llenaDec();
		imprime();
		if(aiPeligra(ai)){
			dirAi = determinaDir(ai);
		}
		else{ //Direccion hacia comida
			dirAi = ataca();
		}
		Spt aiNewhead;
		Spt aiOldhead = ai.get(0);
		if(dirAi==1)
			aiNewhead = new Spt(aiOldhead.x-1,aiOldhead.y);
		else if(dirAi==2)
			aiNewhead = new Spt(aiOldhead.x+1,aiOldhead.y);
		else if(dirAi==3)
			aiNewhead = new Spt(aiOldhead.x,aiOldhead.y-1);
		else
			aiNewhead = new Spt(aiOldhead.x,aiOldhead.y+1);
		//Checamos si hay comida en lugar que acabamos de entrar, si es asi, entonces no eliminamos cola
		if(mat[aiNewhead.y][aiNewhead.x]!=food)
			ai.remove(ai.size()-1);
		else{
			for(int i=0;i<comidas.size();i++){
				if(aiNewhead.y==comidas.get(i).y&&aiNewhead.x==comidas.get(i).x){
					comidas.remove(i);
					generaComida(jug,ai,comidas);

					break;
				}
			}
		}
		//checamos si murio ya la serpientilla
		ai.add(0,aiNewhead);
		jug.add(0,newhead);
		//System.out.println("La coordenada de la cabeza jugadora es "+newhead.y+","+newhead.x);
		if(hasCrashed(jug, ai)){
			System.out.println("Perdiste!!! La serpiente murio");
			estado=-1;
		}
		else if(hasCrashed(ai, jug)){
			System.out.println("Ganaste!!! La serpiente murio");
			estado =1;
		}
		llenaTablero();
		return estado;
	}
	/* Ciclo de juego, maneja los niveles mediante variable nivel */
	static void imprime(){
		for(int i=0;i<VERTICAL_SIZE;i++){
			for(int j=0;j<HORIZONTAL_SIZE;j++){
				System.out.print(dec[i][j]+" ");
			}
			System.out.println();
		}
	}
	public void update(long elapsedTime) {
		
		if(nivel==-1){
			f1.update(elapsedTime);
			if (keyDown(KeyEvent.VK_SPACE)) nivel=0;
		}else if(nivel==0){
			if (keyDown(KeyEvent.VK_SPACE)) nivel=1;
			f2.update(elapsedTime);
		}
		else fondo.update(elapsedTime);
		if(nivel==1){
			jug= new ArrayList<Spt>();
			comidas = new ArrayList<Spt>();
			ai= new ArrayList<Spt>();
			/* Sección: creación de elementos en pantalla predeterminado */
			comidas.add(new Spt(10,10));
			jug.add(new Spt(4,5));
			jug.add(new Spt(4,6));
			jug.add(new Spt(4,7));
			jug.add(new Spt(4,8));
			ai.add(new Spt(20,19));
			ai.add(new Spt(20,18));
			ai.add(new Spt(20,17));
			/* Fin creación de elementos en pantalla. */
			llenaTablero();
			nivel++;
		}
		else if(nivel==2){
			int queHago= 0;
			/* Sección: Control de movimiento */
			if (keyDown(KeyEvent.VK_A)) queHago=1;
			else if (keyDown(KeyEvent.VK_D)) queHago=2;
			else if (keyDown(KeyEvent.VK_W)) queHago=3;
			else if (keyDown(KeyEvent.VK_S)) queHago=4;
			/* Fin Control de movimiento */
			//checamos si el movimiento es valido
			int estado=0;
			if((queHago==1&&dirJug==2)||(queHago==2&&dirJug==1)||(queHago==3&&dirJug==4)||(queHago==4&&dirJug==3)||(queHago==dirJug))
				estado = pasaTurno(0,jug,ai,comidas);
			else estado = pasaTurno(queHago,jug,ai,comidas);
			if(estado==-1){//pierdes
				System.out.println("Debug: has chocado");
			}
			else if(estado==1){
				System.out.println("Debug: has ganado");
			}
			if(estado!=0){
				nivel++;
			}
		}
		else if(nivel==3){
			System.out.println("fin");
		}
	}
	public void render(Graphics2D g) {
		g.fillRect(0, 0, getWidth(), getHeight());
		if(nivel==-1){
			f1.render(g);
		}else if(nivel==0){
			f2.render(g);
		}else{
			fondo.render(g);
			for(int i=0;i<VERTICAL_SIZE;i++){
				for(int j=0;j<HORIZONTAL_SIZE;j++){
					if(mat[i][j]==-2)
						g.drawImage(obstaculo, j*20+137, i*20+66,null);
					if(mat[i][j]==99)
						g.drawImage(comidita, j*20+137, i*20+66,null);
					else if(mat[i][j]==-1)
						g.drawImage(serpiente, j*20+137, i*20+66,null);
					else if(mat[i][j]==-3)
						g.drawImage(serpienteEnemiga, j*20+137, i*20+66, null);
				}
			}
		}



	}
	public static void main(String[] args) {
		GameLoader game = new GameLoader();
		int width = 768;
		int height = 576;
		game.setup(new Main(), new Dimension(width,height), false);
		game.start();
	}
}